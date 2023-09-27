#!/bin/bash
# Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
#  
# File:    pc2sandbox_interacive.sh 
# Purpose: a sandbox for pc2 using Linux CGroups v2 and supporting interactive problems
# Input arguments:
#  $1: memory limit in MB
#  $2: time limit in seconds
#  $3: interactive-validator
#  $4: judge.in
#  $5: judge.out
#  $6: testcase number
#  $7: command to be executed
#  $8... : command arguments
# 
# Author: John Buck, based on earlier versions by John Clevenger and Doug Lane

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
FAIL_INTERACTIVE_ERROR=$((FAIL_RETCODE_BASE+55))

# Maximum number of sub-processes before we will kill it due to fork bomb
# This gets added to the current number of executing processes for this user.
MAXPROCS=32

# taskset cpu mask for running submission on single processor
cpunum=${USER/judge/}
if [[ "$cpunum" =~ ^[1-5]$ ]]
then
	CPUMASK=$((1<<(cpunum-1)))
else
	CPUMASK=0x08
fi

# Process ID of submission
submissionpid=""

CGROUP_PATH=/sys/fs/cgroup
PC2_CGROUP_PATH=$CGROUP_PATH/pc2

# Name of sandbox for this run
PC2_SANDBOX_CGROUP_PATH=$PC2_CGROUP_PATH/sandbox_${USER}

# Create unique sandbox name, if possible
SESSIONID=`ps -ho sess $$`
# If session is a valid number, tack that on for the unique ID
if ((SESSIONID))
then
	PC2_SANDBOX_CGROUP_PATH="${PC2_SANDBOX_CGROUP_PATH}_$((SESSIONID))"
fi

# the kill control for the cgroup
PC2_SANDBOX_CGROUP_PATH_KILL=${PC2_SANDBOX_CGROUP_PATH}/cgroup.kill

# For interactive validation
INFIFO=fin
OUTFIFO=fout
FIFOMODE=600
# The only 2 possible responses from the interactive validator
EXITCODE_AC=42
EXITCODE_WA=43

# Where the interim results are saved
INT_FEEDBACKDIR=interactive_feedback
INT_RESULTFILE=interactive_results.xml
feedbackfile=${INT_FEEDBACKDIR}/judgemessage.txt

# We put reports of each testcase in this folder
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
Usage: pc2sandbox_interactive.sh memlimit timelimit interactive-validator judgein judgeans testcase command command_args

memlimit, in MB
timelimit, in seconds
interactive-validator, judge's program to execute to provide interactive responses to the submission
judgein, judges input file
judgeans, judges answer file
testcase, test case number (for logging)
command, the submission program being judged (eg. ./a.out, /usr/bin/pypy3 a.py, /usr/bin/java Prob, etc.)
command_args, arguments, if any to be passed to the submission command

SAGE
}

# Function to kill all processes in the cgroupv2 after process has exited
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
        # and... extra stragglers with us as a parent
        pkill -9 -P $$
}

# Kill off the validator if it is still running
KillValidator()
{
	if test -n "$intv_pid" -a -d "/proc/$intv_pid"
	then
		REPORT_DEBUG "Killing off interactive validator process $intv_pid"
		kill -9 "$intv_pid"
	fi
}

# Function to handle getting killed by PC2's execute timer (basically, this
# is wall-time exceeded which is execute time limit + 1 second
HandleTerminateFromPC2()
{
	REPORT_DEBUG "Received TERMINATE signal from PC2"
	REPORT_DEBUG "Killing off submission process group $submissionpid and all children"
	KillValidator
	KillChildProcs
	REPORT_DEBUG Wall time exceeded - exiting with code $FAIL_WALL_TIME_LIMIT_EXCEEDED
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
	REPORT_DEBUG Resources used for this run:
	REPORT_DEBUG "$(printf '   CPU ms  Limit ms    Wall ms   Memory Used Memory Limit\n')"
	REPORT_DEBUG "$(printf '%5d.%03d %5d.%03d %6d.%03d  %12s %12d\n' $((cpuused / 1000)) $((cpuused % 1000)) \
		$((cpulim / 1000)) $((cpulim % 1000)) \
		$((walltime / 1000)) $((walltime % 1000)) \
		$((memused)) $((memlim)))"
}

sent_xml=0

GenXML()
{
	rm -rf "$INT_RESULTFILE"
	msg1="$1"
	msg2="$2"
	cat << EOF > $INT_RESULTFILE
<?xml version="1.0"?>
<result outcome="$msg1" security="$INT_RESULTFILE">$msg2</result>
EOF
	sent_xml=1
}

# ------------------------------------------------------------

if [ "$#" -lt 1 ] ; then
   echo $0: Missing command line arguments. Try '"'"$0 --help"'"' for help.
   exit $FAIL_NO_ARGS_EXIT_CODE
fi 

if [ "$1" = "-h" -o "$1" = "--help" ] ; then
   usage
   exit $FAIL_EXIT_CODE
fi 

if [ "$#" -lt 7 ] ; then
   echo $0: expected 7 or more arguments, found: $*
   exit $FAIL_INSUFFICIENT_ARGS_EXIT_CODE
fi 

MEMLIMIT=$1
TIMELIMIT=$2
VALIDATOR=./"$3"
JUDGEIN="$4"
JUDGEANS="$5"
TESTCASE="$6"
COMMAND="$7"
shift 7

# the rest of the commmand line arguments  are the command args for the submission


# make sure we have CGroups V2 properly installed on this system, including a PC2 structure

DEBUG echo checking PC2 CGroup V2 installation...
if [ ! -d "$PC2_CGROUP_PATH" ]; then
   echo $0: expected pc2sandbox CGroups v2 installation in $PC2_CGROUP_PATH 
   exit $FAIL_INVALID_CGROUP_INSTALLATION
fi

if [ ! -f "$CGROUP_PATH/cgroup.controllers" ]; then
   echo $0: missing file cgroup.controllers in $CGROUP_PATH
   exit $FAIL_MISSING_CGROUP_CONTROLLERS_FILE
fi

if [ ! -f "$CGROUP_PATH/cgroup.subtree_control" ]; then
   echo $0: missing file cgroup.subtree_control in $CGROUP_PATH
   exit $FAIL_MISSING_CGROUP_SUBTREE_CONTROL_FILE
fi

# make sure the cpu and memory controllers are enabled
if ! grep -q -F "cpu" "$CGROUP_PATH/cgroup.subtree_control"; then
   echo $0: cgroup.subtree_control in $CGROUP_PATH does not enable cpu controller
   exit $FAIL_CPU_CONTROLLER_NOT_ENABLED
fi

if ! grep -q -F "memory" "$CGROUP_PATH/cgroup.subtree_control"; then
   echo $0: cgroup.subtree_control in $CGROUP_PATH does not enable memory controller
   exit $FAIL_MEMORY_CONTROLLER_NOT_ENABLED
fi

if [ ! -e "$VALIDATOR" ]; then
   echo $0: The interactive validator \'"$VALIDATOR"\' was not found
   exit $FAIL_INTERACTIVE_ERROR
fi

if [ ! -x "$VALIDATOR" ]; then
   echo $0: The interactive validator \'"$VALIDATOR"\' is not an executable file
   exit $FAIL_INTERACTIVE_ERROR
fi

# we seem to have a valid CGroup installation
DEBUG echo ...done.

if test -d $PC2_SANDBOX_CGROUP_PATH
then
	DEBUG echo Removing existing sandbox to start clean
	KillcgroupProcs
	if ! rmdir $PC2_SANDBOX_CGROUP_PATH
	then
		DEBUG echo Cannot purge old sandbox: $PC2_SANDBOX_CGROUP_PATH
		exit $FAIL_SANDBOX_ERROR
	fi
fi

DEBUG echo Creating sandbox $PC2_SANDBOX_CGROUP_PATH
if ! mkdir $PC2_SANDBOX_CGROUP_PATH
then
	DEBUG echo Cannot create $PC2_SANDBOX_CGROUP_PATH
	exit $FAIL_INVALID_CGROUP_INSTALLATION
fi

# First, make the fifos for the pipework between the submission and interactive validator
rm -f "$INFIFO" "$OUTFIFO"
if ! mkfifo --mode=$FIFOMODE $INFIFO
then
	DEBUG Can not create FIFO $INFIFO 1>&2
	exit $FAIL_INTERACTIVE_ERROR
fi
if ! mkfifo --mode=$FIFOMODE $OUTFIFO
then
	DEBUG Can not create FIFO $INFIFO 1>&2
	rm $INFIFO
	exit $FAIL_INTERACTIVE_ERROR
fi

# Set up report directory for per-case logging
mkdir -p "$REPORTDIR"

# Set report file to be testcase specific one now
REPORTFILE=`printf "$REPORTDIR/testcase_%03d.log" $TESTCASE`

# set the specified memory limit - input is in MB, cgroup v2 requires bytes, so multiply by 1M
# but only if > 0.
# "max" means unlimited, which is the cgroup v2 default
DEBUG echo checking memory limit
if [ "$MEMLIMIT" -gt "0" ] ; then
  REPORT Setting memory limit to $MEMLIMIT MB
  echo $(( $MEMLIMIT * 1024 * 1024 ))  > $PC2_SANDBOX_CGROUP_PATH/memory.max
  echo 1  > $PC2_SANDBOX_CGROUP_PATH/memory.swap.max
else
  REPORT Setting memory limit to max, meaning no limit
  echo "max" > $PC2_SANDBOX_CGROUP_PATH/memory.max  
  echo "max" > $PC2_SANDBOX_CGROUP_PATH/memory.swap.max  
fi

# Set up trap handler to catch wall-clock time exceeded and getting killed by PC2's execute timer
trap HandleTerminateFromPC2 2 15

# We have to set up for the interactive validator now, and start it BEFORE placing ourselves in
# the cgroup to limit the submission, or, the validator will be part of the cgroup - no bueno.
# Create feedback directory for interactive validator
mkdir -p "$INT_FEEDBACKDIR"

# Note that starting of the validator will block until the submission is started since
# it will block on the $INFIFO (no one else connected yet)
REPORT Starting: "$VALIDATOR $JUDGEIN $JUDGEANS $INT_FEEDBACKDIR > $INFIFO < $OUTFIFO &"
$VALIDATOR $JUDGEIN $JUDGEANS $INT_FEEDBACKDIR > $INFIFO < $OUTFIFO &
intv_pid=$!
REPORT Started interactive validator PID $intv_pid

# We use ulimit to limit CPU time, not cgroups.  Time is supplied in seconds.  This may have to
# be reworked if ms accuracy is needed.  The problem is, cgroups do not kill off a process that
# exceeds the time limit, ulimit does.
TIMELIMIT_US=$((TIMELIMIT * 1000000))
REPORT Setting cpu limit to $TIMELIMIT_US microseconds "("ulimit -t $TIMELIMIT ")"
ulimit -t $TIMELIMIT

MAXPROCS=$((MAXPROCS+`ps -T -u $USER | wc -l`))
REPORT Setting maximum user processes to $MAXPROCS 
ulimit -u $MAXPROCS

# Remember wall time when we started
starttime=`GetTimeInMicros`

#put the current process (and implicitly its children) into the pc2sandbox cgroup.
REPORT Putting $$ into $PC2_SANDBOX_CGROUP_PATH cgroup
if ! echo $$ > $PC2_SANDBOX_CGROUP_PATH/cgroup.procs
then
	echo $0: Could not add current process to $PC2_SANDBOX_CGROUP_PATH/cgroup.procs - not executing submission.
	rm -f "$INFIFO" "$OUTFIFO"
	exit $FAIL_SANDBOX_ERROR
fi

# run the command
REPORT_DEBUG Executing "setsid taskset $CPUMASK $COMMAND $* < $INFIFO > $OUTFIFO"

setsid taskset $CPUMASK $COMMAND $* < $INFIFO > $OUTFIFO  &
# Remember child's PID/PGRP for possible killing off later
submissionpid=$!

# Flag to indicate if contestant submission has terminated
contestant_done=0

# Now we wait.  We wait for either the child or interactive validator to finish.
# If the validator finishes, no matter what, we're done.  If the submission is still
# running, we kill it.
# If the submission finishes first, we still wait for the validator unless the submission returns a
# non-zero exit status, in which case it's RTE.
while true
do
	# Wait for the next process and put its PID in child_pid
	wait -n -p child_pid
	wstat=$?
	# A return code 127 indicates there are no more children.  How did that happen?
	if test $wstat -eq 127
	then
		REPORT No more children found while waiting: Submission PID was $submissionpid and Interactive Validator PID was $intv_pid
		break
	fi
	# If interactive validator finishes
	if test "$child_pid" -eq "$intv_pid"
	then
		REPORT Validator finishes with status $wstat
		if test "$contestant_done" -eq 0
		then
			# Only kill it if it still exists
			if test -d /proc/$contestantpid
			then
				REPORT Contestant PID $submissionpid has not finished - killing it
				# TODO: We should kill and wait for it here and print out the stats
			fi
			# This just determines if the program ran, not if it's correct.
			# The result file has the correctness in it.
			# We only do this if the contestant program has not finished yet.
			COMMAND_EXIT_CODE=0
		fi

		KillChildProcs

		if test "$wstat" -eq $EXITCODE_AC
		then
			GenXML Accepted ""
		elif test "$wstat" -eq $EXITCODE_WA
		then
			# If validator created a feedback file, put the last line in the judgement
			if test -s "$feedbackfile"
			then
				GenXML "No - Wrong answer" `tail -1 $feedbackfile`
			else
				GenXML "No - Wrong answer" "No feedback file"
			fi
		else
			GenXML "Other - contact staff" "bad return code $wstat"
		fi
		break;
	fi
	# If this is the contestant submission
	if test "$child_pid" -eq "$submissionpid"
	then
		# Get cpu time immediately to minimize any usage by this shell
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


		REPORT Contestant PID $submissionpid finished with status $wstat
		contestant_done=1
		COMMAND_EXIT_CODE=$wstat

		ShowStats ${cputime} ${TIMELIMIT_US} ${walltime} ${peakmem} $((MEMLIMIT*1024*1024))

		# See if we were killed due to memory - this is a kill 9 if it happened
		kills=`grep oom_kill $PC2_SANDBOX_CGROUP_PATH/memory.events | cut -d ' ' -f 2`
		
		if test "$kills" != "0"
		then
			REPORT_DEBUG The command was killed because it exceeded the memory limit
			COMMAND_EXIT_CODE=${FAIL_MEMORY_LIMIT_EXCEEDED}
			GenXML "No - Memory limit exceeded" ""
			KillValidator
			break
		else
			# See why we terminated.  137 = 128 + 9 = SIGKILL, which is what ulimit -t sends
			if test "$COMMAND_EXIT_CODE" -eq 137 -o "$cputime" -gt "$TIMELIMIT_US"
			then
				REPORT_DEBUG The command was killed because it exceeded the CPU Time limit
				COMMAND_EXIT_CODE=${FAIL_TIME_LIMIT_EXCEEDED}
				GenXML "No - Time limit exceeded" "${cputime}us > ${TIMELIMIT_US}us"
				KillValidator
				break
			elif test "$COMMAND_EXIT_CODE" -ge 128
			then
				REPORT_DEBUG The command terminated abnormally due to a signal with exit code $COMMAND_EXIT_CODE signal $((COMMAND_EXIT_CODE - 128))
			else
				REPORT_DEBUG The command terminated normally.
			fi
		fi
		REPORT_DEBUG Finished executing "$COMMAND $*"
		REPORT_DEBUG "$COMMAND" exited with exit code $COMMAND_EXIT_CODE

		if test "$wstat" -ne 0
		then
			REPORT_DEBUG Contestant finished abnormally - killing validator
			KillValidator
			GenXML "No - Run-time Error" "Exit status $wstat"
			break
		fi
		REPORT_DEBUG Waiting for interactive validator to finish...
	fi
done

DEBUG echo
REPORT "________________________________________"

rm -f "$INFIFO" "$OUTFIFO"

# TODO: determine how to pass more detailed pc2sandbox.sh results back to PC2... Perhaps in a file...

# return the exit code of the command as our exit code
exit $COMMAND_EXIT_CODE

# eof pc2sandbox_interactive.sh 

