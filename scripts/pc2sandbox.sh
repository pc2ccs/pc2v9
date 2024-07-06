#!/bin/bash
# Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
#  
# File:    pc2sandbox.sh 
# Purpose: a sandbox for pc2 using Linux CGroups v2.
# Input arguments:
#  $1: memory limit in MB
#  $2: time limit in seconds
#  $3: judges_input_file
#  $4: judges_answer_file
#  $5: testcase number
#  $6: command to be executed
#  $7... : command arguments
# 
# Author: John Buck, based on earlier versions by John Clevenger and Doug Lane
DEFAULT_CPU_NUM=3
CPU_OVERRIDE_FILE=$HOME/pc2_cpu_override

# Where to the the result of the first failure.  If this file is not created, then the
# run was accepted. (correct)
RESULT_FAILURE_FILE=failure.txt

# FAIL_RETCODE_BASE is 128 + 64 + xx
# 128 = system error, like signal
# 64 = biggest used signal
FAIL_RETCODE_BASE=192
FAIL_EXIT_CODE=$((FAIL_RETCODE_BASE+43))
FAIL_NO_ARGS_EXIT_CODE=$((FAIL_RETCODE_BASE+44))
FAIL_INSUFFICIENT_ARGS_EXIT_CODE=$((FAIL_RETCODE_BASE+45))
FAIL_INVALID_CGROUP_INSTALLATION=$((FAIL_RETCODE_BASE+46))
FAIL_MISSING_CGROUP_CONTROLLERS_FILE=$((FAIL_RETCODE_BASE+47))
FAIL_MISSING_CGROUP_SUBTREE_CONTROL_FILE=$((FAIL_RETCODE_BASE+48))
FAIL_CPU_CONTROLLER_NOT_ENABLED=$((FAIL_RETCODE_BASE+49))
FAIL_MEMORY_CONTROLLER_NOT_ENABLED=$((FAIL_RETCODE_BASE+50))
FAIL_MEMORY_LIMIT_EXCEEDED=$((FAIL_RETCODE_BASE+51))
FAIL_TIME_LIMIT_EXCEEDED=$((FAIL_RETCODE_BASE+52))
FAIL_WALL_TIME_LIMIT_EXCEEDED=$((FAIL_RETCODE_BASE+53))
FAIL_SANDBOX_ERROR=$((FAIL_RETCODE_BASE+54))

# Maximum number of sub-processes before we will kill it due to fork bomb
# This gets added to the current number of executing processes for this user.
MAXPROCS=32

# Compute taskset cpu mask for running submission on single processor

# Get system's maximum CPU number
MAX_CPU_NUM=`lscpu -p=cpu | tail -1`

# See if the admin wants to override the CPU by reading the override file
if test -s ${CPU_OVERRIDE_FILE}
then
	# This will get the first line that consists of numbers only
	cpunum=`egrep '^[0-9]+$' ${CPU_OVERRIDE_FILE} | head -1`
	if test -z ${cpunum}
	then
		cpunum=""
	elif test ${cpunum} -gt ${MAX_CPU_NUM}
	then
		cpunum=""
	fi
fi

# If there was no override or the override was bad, let's try to figure out the cpunum
if test -z ${cpunum}
then
	# The login id must be "judge#" where # is the desired CPU and judge number.
	# If the login is not "judge#", then the system default is used.
	# This special case is for when you want to run multiple judges on one computer
	# that has lots of CPU's, but want to pin each judge to its own cpu.
	cpunum=${USER/judge/}
	if [[ "$cpunum" =~ ^[1-9][0-9]*$ ]]
	then
		# Restrict to number of CPU's.
		cpunum=$(((cpunum-1)%(MAX_CPU_NUM+1)))
	else
		cpunum=$(((DEFAULT_CPU_NUM+1)))
	fi
fi
cpunum=$((cpunum-1))
CPUMASK=$((1<<cpunum))

# Process ID of submission
submissionpid=""

CGROUP_PATH=/sys/fs/cgroup
PC2_CGROUP_PATH=$CGROUP_PATH/pc2

# Name of sandbox for this run
PC2_SANDBOX_CGROUP_PATH=$PC2_CGROUP_PATH/sandbox_$USER

# Create unique sandbox name, if possible
SESSIONID=`ps -ho sess $$`
# If session is a valid number, tack that on for the unique ID
if ((SESSIONID))
then
	PC2_SANDBOX_CGROUP_PATH="${PC2_SANDBOX_CGROUP_PATH}_$((SESSIONID))"
fi

# the kill control for the cgroup
PC2_SANDBOX_CGROUP_PATH_KILL=${PC2_SANDBOX_CGROUP_PATH}/cgroup.kill

# We putreports of each testcase in this folder
REPORTDIR=reports

# control whether the script outputs debug/tracing info
_DEBUG="on"   # change this to anything other than "on" to disable debug/trace output
DEBUG_FILE=sandbox.log
function DEBUG()
{
  [ "$_DEBUG" == "on" ] && "$@" >> $DEBUG_FILE
}

# For per-testcase reporting/logging
function REPORT()
{
	if test -n "$REPORTFILE"
	then
		echo "$@" >> $REPORTFILE
	fi
}
function REPORT_BRIEF()
{
	if test -n "$BRIEFREPORTFILE"
	then
		echo "$@" >> $BRIEFREPORTFILE
	fi
}

# For per-testcase report and debugging both
function REPORT_DEBUG()
{
	if test -n "$REPORTFILE"
	then
		echo "$@" >> $REPORTFILE
	fi
	[ "$_DEBUG" == "on" ] && echo "$@" >> $DEBUG_FILE
}

# ------------------------------------------------------------

usage()
{
  cat <<SAGE
Usage: pc2_sandbox.sh memlimit timelimit command command_args

memlimit, in MB
timelimit, in seconds

SAGE
}

# Function to kill all processes in the cgroupv2 
KillcgroupProcs()
{
	if test -n ${PC2_SANDBOX_CGROUP_PATH_KILL}
	then
		DEBUG echo "Purging cgroup ${PC2_SANDBOX_CGROUP_PATH_KILL} of processes"
		echo 1 > ${PC2_SANDBOX_CGROUP_PATH_KILL}
	fi
}

# Kill active children and stragglers
KillChildProcs()
{
	DEBUG echo "Killing off submission process group $submissionpid and all children"
	# Kill off process group
	if test -n "$submissionpid"
	then
		pkill -9 -s $submissionpid
	fi
	# and... extra stragglers
	pkill -9 -P $$
}

# Function to handle getting killed by PC2's execute timer (basically, this
# is wall-time exceeded which is execute time limit + 1 second
HandleTerminateFromPC2()
{
	REPORT_DEBUG "Received TERMINATE signal from PC2"
	REPORT_DEBUG "Kiling off submission process group $submissionpid and all children"
	KillChildProcs
	REPORT_DEBUG $0: Wall time exceeded - exiting with code $FAIL_WALL_TIME_LIMIT_EXCEEDED
	exit $FAIL_WALL_TIME_LIMIT_EXCEEDED 
}

GetTimeInMicros()
{
        set `date "+%s %6N"`
        sec=$1
        us=$2
        us=$((10#$us))
        ret=$((sec*1000000))
        ret=$((ret+$us))
        echo $ret
}

# Show run's resource summary in a nice format, eg.
#   CPU ms  Limit ms    Wall ms   Memory Used   Memory Limit
#    3.356  5000.000      4.698       1839104     2147483648
ShowStats()
{
	cpuused=$1
	cpulim=$2
	walltime=$3
	memused=$4
	memlim=$5
	REPORT_BRIEF "$(printf '%d.%03d' $((cpuused / 1000)) $((cpuused % 1000)))" \
		"$(printf '%d.%03d' $((cpulim / 1000)) $((cpulim % 1000)))" \
		"$(printf '%d.%03d' $((walltime / 1000)) $((walltime % 1000)))" \
		${memused} $((memlim))
	REPORT_DEBUG Resources used for this run:
	REPORT_DEBUG "$(printf '   CPU ms  Limit ms    Wall ms   Memory Used Memory Limit\n')"
	REPORT_DEBUG "$(printf '%5d.%03d %5d.%03d %6d.%03d  %12s %12d\n' $((cpuused / 1000)) $((cpuused % 1000)) \
		$((cpulim / 1000)) $((cpulim % 1000)) \
		$((walltime / 1000)) $((walltime % 1000)) \
		${memused} $((memlim)))"
}

# Generate a system failure result file.  This will be the FIRST failure as it will not overwrite the file
# if it exists.
SysFailure()
{
	if test ! -s ${RESULT_FAILURE_FILE}
	then
		(echo system; echo $* ) > ${RESULT_FAILURE_FILE}
	fi
}

# Generate a failure result file.  This will be the FIRST failure as it will not overwrite the file
# if it exists.
Failure()
{
	if test ! -s ${RESULT_FAILURE_FILE}
	then
		echo $* > ${RESULT_FAILURE_FILE}
	fi
}

# ------------------------------------------------------------

if [ "$#" -lt 1 ] ; then
   echo $0: No command line arguments
   SysFailure No command line arguments to $0
   exit $FAIL_NO_ARGS_EXIT_CODE
fi 

if [ "$#" -lt 3 ] ; then
   echo $0: expected 3 or more arguments, found: $*
   SysFailure Expected 3 or more arguments to $0, found: $*
   exit $FAIL_INSUFFICIENT_ARGS_EXIT_CODE
fi 

if [ "$1" = "-h" -o "$1" = "--help" ] ; then
   usage
   exit $FAIL_EXIT_CODE
fi 

MEMLIMIT=$1
TIMELIMIT=$2
JUDGEIN=$3
JUDGEANS=$4
TESTCASE=$5
COMMAND=$6
DEBUG echo "+---------------- Test Case ${TESTCASE} ----------------+"
DEBUG echo Command line: $0 $*
shift
shift
shift
shift
shift
shift
DEBUG echo -e "\nYou can run this by hand in the sandbox by using the following command:"
RUN_LOCAL_CMD="$0 ${MEMLIMIT} ${TIMELIMIT} xxx xxx ${COMMAND} $* < ${JUDGEIN} > $TESTCASE.ans"
DIFF_OUTPUT_CMD="diff -w ${JUDGEANS} $TESTCASE.ans | more"
DEBUG echo -e "\n${RUN_LOCAL_CMD}"
DEBUG echo -e "\nor, without the sandbox by using the following command:"
DEBUG echo -e "\n${COMMAND} $* < ${JUDGEIN} > $TESTCASE.ans"
DEBUG echo -e "\nAnd compare with the judge's answer:"
DEBUG echo -e "\n${DIFF_OUTPUT_CMD}\n"

#### Debugging - just set expected first args to: 8MB 2seconds
###MEMLIMIT=8
###TIMELIMIT=2
###JUDGEIN=none
###JUDGEANS=none
###TESTCASE=1
###COMMAND=$1
###shift

# the rest is the command args

# make sure we have CGroups V2 properly installed on this system, including a PC2 structure

DEBUG echo checking PC2 CGroup V2 installation...
if [ ! -d "$PC2_CGROUP_PATH" ]; then
   echo $0: expected pc2sandbox CGroups v2 installation in $PC2_CGROUP_PATH 
   SysFailure CGroups v2 not installed in $PC2_CGROUP_PATH
   exit $FAIL_INVALID_CGROUP_INSTALLATION
fi

if [ ! -f "$CGROUP_PATH/cgroup.controllers" ]; then
   echo $0: missing file cgroup.controllers in $CGROUP_PATH
   SysFailure Missing cgroup.controllers in $CGROUP_PATH
   exit $FAIL_MISSING_CGROUP_CONTROLLERS_FILE
fi

if [ ! -f "$CGROUP_PATH/cgroup.subtree_control" ]; then
   echo $0: missing file cgroup.subtree_control in $CGROUP_PATH
   SysFailure Missing cgroup.subtree_controll in $CGORUP_PATH
   exit $FAIL_MISSING_CGROUP_SUBTREE_CONTROL_FILE
fi

# make sure the cpu and memory controllers are enabled
if ! grep -q -F "cpu" "$CGROUP_PATH/cgroup.subtree_control"; then
   echo $0: cgroup.subtree_control in $CGROUP_PATH does not enable cpu controller
   SysFailure CPU controller not enabled in cgroup.subtree_control in $CGROUP_PATH
   exit $FAIL_CPU_CONTROLLER_NOT_ENABLED
fi

if ! grep -q -F "memory" "$CGROUP_PATH/cgroup.subtree_control"; then
   echo $0: cgroup.subtree_control in $CGROUP_PATH does not enable memory controller
   SysFailure Memory controller not enabled in cgroup.subtree_control in $CGROUP_PATH
   exit $FAIL_MEMORY_CONTROLLER_NOT_ENABLED
fi


# we seem to have a valid CGroup installation
DEBUG echo ...done.

if test -d $PC2_SANDBOX_CGROUP_PATH
then
	DEBUG echo Removing existing sandbox to start clean
	KillcgroupProcs
	if ! rmdir $PC2_SANDBOX_CGROUP_PATH
	then
		DEBUG echo Cannot remove previous sandbox: $PC2_SANDBOX_CGROUP_PATH
		SysFailure Can not remove previous sandbox $PC2_SANDBOX_CGROUP_PATH
		exit $FAIL_SANDBOX_ERROR
	fi
fi

DEBUG echo Creating sandbox $PC2_SANDBOX_CGROUP_PATH
if ! mkdir $PC2_SANDBOX_CGROUP_PATH
then
	DEBUG echo Cannot create $PC2_SANDBOX_CGROUP_PATH
	SysFailure Can not create $PC2_SANDBOX_CGROUP_PATH
	exit $FAIL_INVALID_CGROUP_INSTALLATION
fi

# Set up report directory for per-case logging
mkdir -p "$REPORTDIR"

# Set report file to be testcase specific one now
REPORTFILE=`printf "$REPORTDIR/testcase_%03d.log" $TESTCASE`
BRIEFREPORTFILE=`printf "$REPORTDIR/briefcase_%03d.log" $TESTCASE`
DEBUG echo Report file: ${REPORTFILE} Brief Report File: ${BRIEFREORTFILE}
REPORT Test case $TESTCASE:
REPORT Command: "${RUN_LOCAL_CMD}"
REPORT Diff: "   ${DIFF_OUTPUT_CMD}"

# set the specified memory limit - input is in MB, cgroup v2 requires bytes, so multiply by 1M
# but only if > 0.
# "max" means unlimited, which is the cgroup v2 default
DEBUG echo checking memory limit
if [ "$MEMLIMIT" -gt "0" ] ; then
  REPORT_DEBUG echo Setting memory limit to $MEMLIMIT MB
  echo $(( $MEMLIMIT * 1024 * 1024 ))  > $PC2_SANDBOX_CGROUP_PATH/memory.max
  echo 1  > $PC2_SANDBOX_CGROUP_PATH/memory.swap.max
else
  REPORT_DEBUG echo Setting memory limit to max, meaning no limit
  echo "max" > $PC2_SANDBOX_CGROUP_PATH/memory.max  
  echo "max" > $PC2_SANDBOX_CGROUP_PATH/memory.swap.max  
fi

# We use ulimit to limit CPU time, not cgroups.  Time is supplied in seconds.  This may have to
# be reworked if ms accuracy is needed.  The problem is, cgroups do not kill off a process that
# exceeds the time limit, ulimit does.
TIMELIMIT_US=$((TIMELIMIT * 1000000))
REPORT_DEBUG Setting cpu limit to $TIMELIMIT_US microseconds "("ulimit -t $TIMELIMIT ")"
ulimit -t $TIMELIMIT

MAXPROCS=$((MAXPROCS+`ps -T -u $USER | wc -l`))
REPORT_DEBUG Setting maximum user processes to $MAXPROCS
ulimit -u $MAXPROCS

# Keep track of details for reports
REPORT_BRIEF ${JUDGEIN}
REPORT_BRIEF ${JUDGEANS}
REPORT_BRIEF $cpunum
REPORT_BRIEF $$
REPORT_BRIEF $(date "+%F %T.%6N")

# Remember wall time when we started
starttime=`GetTimeInMicros`

#put the current process (and implicitly its children) into the pc2sandbox cgroup.
REPORT Putting $$ into $PC2_SANDBOX_CGROUP_PATH cgroup
if ! echo $$ > $PC2_SANDBOX_CGROUP_PATH/cgroup.procs
then
	echo $0: Could not add current process to $PC2_SANDBOX_CGROUP_PATH/cgroup.procs - not executing submission.
	SysFailure Could not add current process to $PC2_SANDBOX_CGROUP_PATH/cgroup.procs
	exit $FAIL_SANDBOX_ERROR
fi

# run the command
# execute it directly (it's a child so it should still fall under the cgroup limits).
REPORT_DEBUG Executing "setsid taskset $CPUMASK $COMMAND $*"

# Set up trap handler to catch wall-clock time exceeded and getting killed by PC2's execute timer
trap HandleTerminateFromPC2 15

# This will create a new process group
#bash -imc "taskset ${CPUMASK} $COMMAND $*" <&0 &
setsid taskset ${CPUMASK} $COMMAND $* <&0 &
# Remember child's PID/PGRP for possible killing off later
submissionpid=$!

# Wait for child
wait $submissionpid

COMMAND_EXIT_CODE=$?

# See if we were killed due to memory - this is a kill 9 if it happened

kills=`grep oom_kill $PC2_SANDBOX_CGROUP_PATH/memory.events | cut -d ' ' -f 2`

KillChildProcs

# Get cpu time used.
cputime=`grep usage_usec $PC2_SANDBOX_CGROUP_PATH/cpu.stat | cut -d ' ' -f 2`

# Get wall time - we want it as close as possible to when we fetch the cpu time so they stay close
# since the cpu.stat includes the time this script takes AFTER the submission finishes.
endtime=`GetTimeInMicros`
walltime=$((endtime-starttime))

# Newer kernels support memory.peak, so we have to check if it's there.
if test -e $PC2_SANDBOX_CGROUP_PATH/memory.peak
then
	peakmem=`cat $PC2_SANDBOX_CGROUP_PATH/memory.peak`
else
	peakmem="N/A"
fi
ShowStats ${cputime} ${TIMELIMIT_US} ${walltime} ${peakmem} $((MEMLIMIT*1024*1024))

REPORT_DEBUG The command exited with code: ${COMMAND_EXIT_CODE}

if test "$kills" != "0"
then
	REPORT_DEBUG The command was killed because it exceeded the memory limit
	REPORT_BRIEF MLE
	COMMAND_EXIT_CODE=${FAIL_MEMORY_LIMIT_EXCEEDED}
else
	# See why we terminated.  137 = 128 + 9 = SIGKILL, which is what ulimit -t sends.
	if test "$COMMAND_EXIT_CODE" -eq 137 -o "$cputime" -gt "$TIMELIMIT_US"
	then
		REPORT_DEBUG The command was killed because it exceeded the CPU Time limit
		REPORT_BRIEF TLE
		COMMAND_EXIT_CODE=${FAIL_TIME_LIMIT_EXCEEDED}
	elif test "$COMMAND_EXIT_CODE" -ge 128
	then
		REPORT_DEBUG The command terminated abnormally with exit code $COMMAND_EXIT_CODE
		REPORT_BRIEF RTE Exit:$COMMAND_EXIT_CODE
	else
		REPORT_DEBUG The command terminated normally.
	fi
fi
REPORT_DEBUG Finished executing $COMMAND $*
REPORT_DEBUG $COMMAND exited with exit code $COMMAND_EXIT_CODE
DEBUG echo


# TODO: determine how to pass more detailed pc2sandbox.sh results back to PC2... Perhaps in a file...

# return the exit code of the command as our exit code
exit $COMMAND_EXIT_CODE

# eof pc2sandbox.sh 

