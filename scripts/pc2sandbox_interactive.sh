#!/bin/bash
# Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
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

# KILL_WA_VALIDATOR may be set to 1 to kill off a submission if the validator finishes first with
# a WA judgment.  In this case, we don't care what the submission does.  This matches how DOMjudge
# handles this case.  Setting this to 0 will always make it wait for the contestant submission to
# finish, and use whatever judgement is appropriate.  If contestant finishes with 0, then we use the
# vaildator result, else we use the exit code of the submission.
KILL_WA_VALIDATOR=1
# If WAIT_FOR_SUB_ON_AC is 1, we wait for the submission if the validator says "AC".  We then
# use the exit code of the submission if it is non-zero to determine disposition, otherise,
# we return AC
# If WAIT_FOR_SUB_ON_AC is 0, we will kill off the submission as soon as we get an AC from the
# validator if the submission is not complete yet.
WAIT_FOR_SUB_ON_AC=1

# CPU to run submission on.  This is 0 based, so 3 means the 4th CPU
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
FAIL_INTERACTIVE_ERROR=$((FAIL_RETCODE_BASE+55))

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
CPUMASK=$((1<<cpunum-1))

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


sent_xml=0

GenXML()
{
	rm -rf "$INT_RESULTFILE"
	msg1="$1"
	shift
	msg2="$*"
	cat << EOF > $INT_RESULTFILE
<?xml version="1.0"?>
<result outcome="$msg1" security="$INT_RESULTFILE"><![CDATA[$msg2]]></result>
EOF
	sent_xml=1
}

# Dont complain to me - this is the order they appear in ./x86_64-linux-gnu/bits/signum-generic.h
declare -A signal_names=(
	["2"]="SIGINT"
	["4"]="SIGILL"
	["6"]="SIGABRT"
	["8"]="SIGFPE"
	["11"]="SIGSEGV"
	["15"]="SIGTERM"
	["1"]="SIGHUP"
	["3"]="SIGQUIT"
	["5"]="SIGTRAP"
	["9"]="SIGKILL"
	["13"]="SIGPIPE"
	["14"]="SIGALRM"
)

declare -A fail_codes=(
	["$FAIL_EXIT_CODE"]="FAIL_EXIT_CODE"
	["$FAIL_NO_ARGS_EXIT_CODE"]="FAIL_NO_ARGS_EXIT_CODE"
	["$FAIL_INSUFFICIENT_ARGS_EXIT_CODE"]="FAIL_INSUFFICIENT_ARGS_EXIT_CODE"
	["$FAIL_INVALID_CGROUP_INSTALLATION"]="FAIL_INVALID_CGROUP_INSTALLATION"
	["$FAIL_MISSING_CGROUP_CONTROLLERS_FILE"]="FAIL_MISSING_CGROUP_CONTROLLERS_FILE"
	["$FAIL_MISSING_CGROUP_SUBTREE_CONTROL_FILE"]="FAIL_MISSING_CGROUP_SUBTREE_CONTROL_FILE"
	["$FAIL_CPU_CONTROLLER_NOT_ENABLED"]="FAIL_CPU_CONTROLLER_NOT_ENABLED"
	["$FAIL_MEMORY_CONTROLLER_NOT_ENABLED"]="FAIL_MEMORY_CONTROLLER_NOT_ENABLED"
	["$FAIL_MEMORY_LIMIT_EXCEEDED"]="FAIL_MEMORY_LIMIT_EXCEEDED"
	["$FAIL_TIME_LIMIT_EXCEEDED"]="FAIL_TIME_LIMIT_EXCEEDED"
	["$FAIL_WALL_TIME_LIMIT_EXCEEDED"]="FAIL_WALL_TIME_LIMIT_EXCEEDED"
	["$FAIL_SANDBOX_ERROR"]="FAIL_SANDBOX_ERROR"
	["$FAIL_INTERACTIVE_ERROR"]="FAIL_INTERACTIVE_ERROR"
)
FormatExitCode()
{
	result="$1"
	if [[ "$result" = [0-9]* ]]
	then
		failerr=${fail_codes[$result]}
		if test -n "$failerr"
		then
			result="$result ($failerr)"
		elif test "$result" -gt 128 -a "$result" -lt 192
		then
			sig=$((result-128))
			signame=${signal_names[$sig]}
			if test -n "$signame"
			then
				signame="$signame "
			fi
			result="$result (Signal $signame$((result-128)))"
		fi
	fi
}

# ------------------------------------------------------------

if [ "$#" -lt 1 ] ; then
   echo $0: Missing command line arguments. Try '"'"$0 --help"'"' for help.
   SysFailure No command line arguments to $0
   exit $FAIL_NO_ARGS_EXIT_CODE
fi 

if [ "$1" = "-h" -o "$1" = "--help" ] ; then
   usage
   exit $FAIL_EXIT_CODE
fi 

if [ "$#" -lt 7 ] ; then
   echo $0: expected 7 or more arguments, found: $*
   SysFailure Expected 7 or more arguments to $0, found: $*
   exit $FAIL_INSUFFICIENT_ARGS_EXIT_CODE
fi 

MEMLIMIT=$1
TIMELIMIT=$2
VALIDATOR=./"$3"
JUDGEIN="$4"
JUDGEANS="$5"
TESTCASE="$6"
COMMAND="$7"
DEBUG echo "+---------------- Test Case ${TESTCASE} ----------------+"
DEBUG echo Command line: $0 $*
shift 7

DEBUG echo -e "\nYou can run this by hand in the sandbox by using the following command:"
RUN_LOCAL_CMD="$0 ${MEMLIMIT} ${TIMELIMIT} ${VALIDATOR} ${JUDGEIN} ${JUDGEANS} ${TESTCASE} ${COMMAND} $*"
tcfile=`printf "$REPORTDIR/testcase_%03d.log" $TESTCASE`
REPORT_OUTPUT_CMD="more ${tcfile}"
DEBUG echo -e "\n${RUN_LOCAL_CMD}"
#DEBUG echo -e "\nor, without the sandbox by using the following command:"
#DEBUG echo -e "\n${COMMAND} $* < ${JUDGEIN} > $TESTCASE.ans"
DEBUG echo -e "\nAnd see the run report using the following command:"
DEBUG echo -e "\n${REPORT_OUTPUT_CMD}\n"

# Skip used arguments
shift 7

# the rest of the commmand line arguments  are the command args for the submission
DEBUG echo -e "\nYou can run this by hand in the sandbox by using the following command:"
RUN_LOCAL_CMD="$0 ${MEMLIMIT} ${TIMELIMIT} ${VALIDATOR} ${JUDGEIN} ${JUDGEANS} ${TESTCASE} ${COMMAND} $*"
tcfile=`printf "$REPORTDIR/testcase_%03d.log" $TESTCASE`
REPORT_OUTPUT_CMD="more ${tcfile}"
DEBUG echo -e "\n${RUN_LOCAL_CMD}"
#DEBUG echo -e "\nor, without the sandbox by using the following command:"
#DEBUG echo -e "\n${COMMAND} $* < ${JUDGEIN} > $TESTCASE.ans"
DEBUG echo -e "\nAnd see the run report using the following command:"
DEBUG echo -e "\n${REPORT_OUTPUT_CMD}\n"

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

if [ ! -e "$VALIDATOR" ]; then
   echo $0: The interactive validator \'"$VALIDATOR"\' was not found
   SysFailure The interactive validator \'"$VALIDATOR"\' was not found
   exit $FAIL_INTERACTIVE_ERROR
fi

if [ ! -x "$VALIDATOR" ]; then
   echo $0: The interactive validator \'"$VALIDATOR"\' is not an executable file
   SysFailure  The interactive validator \'"$VALIDATOR"\' is not an executable file
   exit $FAIL_INTERACTIVE_ERROR
fi

# we seem to have a valid CGroup and validator setup
DEBUG echo ...done.

if test -d $PC2_SANDBOX_CGROUP_PATH
then
	DEBUG echo Removing existing sandbox to start clean
	KillcgroupProcs
	if ! rmdir $PC2_SANDBOX_CGROUP_PATH
	then
		DEBUG echo Cannot purge old sandbox: $PC2_SANDBOX_CGROUP_PATH
   		SysFailure  Cannot purge old sandbox: $PC2_SANDBOX_CGROUP_PATH
		exit $FAIL_SANDBOX_ERROR
	fi
fi

DEBUG echo Creating sandbox $PC2_SANDBOX_CGROUP_PATH
if ! mkdir $PC2_SANDBOX_CGROUP_PATH
then
	DEBUG echo Cannot create $PC2_SANDBOX_CGROUP_PATH
	SysFailure  Cannot create sandbox: $PC2_SANDBOX_CGROUP_PATH
	exit $FAIL_INVALID_CGROUP_INSTALLATION
fi

# First, make the fifos for the pipework between the submission and interactive validator
rm -f "$INFIFO" "$OUTFIFO"
if ! mkfifo --mode=$FIFOMODE $INFIFO
then
	DEBUG Can not create input FIFO $INFIFO 1>&2
	SysFailure  Cannot create input FIFO: $INFIFO
	exit $FAIL_INTERACTIVE_ERROR
fi
if ! mkfifo --mode=$FIFOMODE $OUTFIFO
then
	DEBUG Can not create output FIFO $OUTFIFO 1>&2
	SysFailure  Cannot create output FIFO: $OUTFIFO
	rm $INFIFO
	exit $FAIL_INTERACTIVE_ERROR
fi

# Set up report directory for per-case logging
mkdir -p "$REPORTDIR"

# Set report file to be testcase specific one now
REPORTFILE=`printf "$REPORTDIR/testcase_%03d.log" $TESTCASE`
BRIEFREPORTFILE=`printf "$REPORTDIR/briefcase_%03d.log" $TESTCASE`
DEBUG echo Report file: ${REPORTFILE} Brief Report File: ${BRIEFREORTFILE}
REPORT Test case $TESTCASE:
REPORT Command: "${RUN_LOCAL_CMD}"
REPORT Report: " ${REPORT_OUTPUT_CMD}"

# set the specified memory limit - input is in MB, cgroup v2 requires bytes, so multiply by 1M
# but only if > 0.
# "max" means unlimited, which is the cgroup v2 default
DEBUG echo checking memory limit
if [ "$MEMLIMIT" -gt "0" ] ; then
  REPORT_DEBUG Setting memory limit to $MEMLIMIT MiB
  echo $(( $MEMLIMIT * 1024 * 1024 ))  > $PC2_SANDBOX_CGROUP_PATH/memory.max
  echo 1  > $PC2_SANDBOX_CGROUP_PATH/memory.swap.max
else
  REPORT_DEBUG Setting memory limit to max, meaning no limit
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
REPORT_DEBUG Starting: "$VALIDATOR $JUDGEIN $JUDGEANS $INT_FEEDBACKDIR > $INFIFO < $OUTFIFO &"
$VALIDATOR $JUDGEIN $JUDGEANS $INT_FEEDBACKDIR > $INFIFO < $OUTFIFO &
intv_pid=$!
REPORT_DEBUG Started interactive validator PID $intv_pid

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
# Validator exit status, if it finished before submission
val_result=""
# Indicates if we still have to wait for the submission
wait_sub=0

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
		REPORT_DEBUG No more children found while waiting: Submission PID was $submissionpid and Interactive Validator PID was $intv_pid
		break
	fi
	# If interactive validator finishes
	if test "$child_pid" -eq "$intv_pid"
	then
		REPORT_DEBUG Validator finishes with exit code $wstat
		if test "$contestant_done" -eq 0
		then
			# If we always kill off the child when the validator finishes with WA, or the validator finishes with "AC" and we dont wait for submission on AC,
			# then kill off the child, otherwise we wait for it.
			if test "(" ${KILL_WA_VALIDATOR} -eq 1 -a "$wstat" -ne "${EXITCODE_AC}" ")" -o "(" ${WAIT_FOR_SUB_ON_AC} -eq 0 -a "$wstat" -eq ${EXITCODE_AC} ")"
			then
				# Only kill it if it still exists
				if test -d /proc/$contestantpid
				then
					REPORT_DEBUG Contestant PID $submissionpid has not finished - killing it "(KILL_WA_VALIDATOR=1)"
					# TODO: We should kill and wait for it here and print out the stats
				fi
				# This just determines if the program ran, not if it's correct.
				# The result file has the correctness in it.
				# We only do this if the contestant program has not finished yet.
				REPORT_DEBUG Indicating that the submission exited with code 0 because we killed it.
				COMMAND_EXIT_CODE=0
			else
				REPORT_DEBUG Contestant PID $submissionpid has not finished - waiting for it "(KILL_WA_VALIDATOR=1)"
				# Need to wait for child.  Remember validator result.
				val_result="$wstat"
				wait_sub=1
			fi
		fi

		# Kill everything off if we don't have to wait for submission
		if test "$wait_sub" -eq 0
		then
			KillChildProcs
		fi

		if test "$wstat" -eq $EXITCODE_AC
		then
			# COMMAND_EXIT_CODE will be set to 0 above, or 0 if the submission finished already with EC 0.
			# If COMMAND_EXIT_CODE is anything else, we will either be waiting for the submission, or, use
			# it's exit code as the result.  The GenXML will have been filled in in this case with any errors.
			if test -z "${COMMAND_EXIT_CODE}" -o "${COMMAND_EXIT_CODE}" = "0"
			then
				REPORT_DEBUG Both the validator and the submission indicate AC
				# Set the result to AC.  If we have to wait for the submission, this will get overwritten with
				# the submissions disposition.
				GenXML Accepted ""
			fi
		elif test "$wstat" -eq $EXITCODE_WA
		then
			# COMMAND_EXIT_CODE may be set to something here, either 0 from above, or
			# an exit code from when the submission finished below.  In the latter case, the
			# XML result file will have already been created with the disposition from below.
			# We will only generate an XML here if the validator says it failed (WA).
			# COMMAND_EXIT_CODE can be empty if we are waiting for the submission to finish.
			if test -z "${COMMAND_EXIT_CODE}" -o "${COMMAND_EXIT_CODE}" = "0"
			then
				REPORT_DEBUG Submission has an exit code of 0 and validator says WA.
				# If validator created a feedback file, put the last line in the judgement
				if test -s "$feedbackfile"
				then
					GenXML "No - Wrong answer" `head -n 1 $feedbackfile`
				else
					GenXML "No - Wrong answer" "No feedback file"
				fi
			fi
		else
			REPORT_DEBUG Validator returned code $wstat which is not 42 or 43 - validator error.
			GenXML "Other - contact staff" "bad validator return code $wstat"
			if test "$wait_sub" -ne 0
			then
				KillChildProcs
			fi
			COMMAND_EXIT_CODE=${FAIL_INTERACTIVE_ERROR}
			break
		fi
		if test "$wait_sub" -eq 0
		then
			REPORT_DEBUG No need to wait for submission to finish.
			break
		fi
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


		FormatExitCode $wstat
		REPORT_DEBUG Contestant PID $submissionpid finished with exit code $result
		contestant_done=1
		COMMAND_EXIT_CODE=$wstat

		ShowStats ${cputime} ${TIMELIMIT_US} ${walltime} ${peakmem} $((MEMLIMIT*1024*1024))

		# See if we were killed due to memory - this is a kill 9 if it happened
		kills=`grep oom_kill $PC2_SANDBOX_CGROUP_PATH/memory.events | cut -d ' ' -f 2`
		
		if test "$kills" != "0"
		then
			REPORT_DEBUG The command was killed because it exceeded the memory limit - setting exit code to ${FAIL_MEMORY_LIMIT_EXCEEDED}
			REPORT_BRIEF MLE
			COMMAND_EXIT_CODE=${FAIL_MEMORY_LIMIT_EXCEEDED}
			GenXML "No - Memory limit exceeded" ""
			if test "$val_result" = "${EXITCODE_AC}" -o "${KILL_WA_VALIDATOR}" -eq 0
			then
				KillValidator
				break
			fi
		else
			# See why we terminated.  137 = 128 + 9 = SIGKILL, which is what ulimit -t sends
			if test "$COMMAND_EXIT_CODE" -eq 137 -o "$cputime" -gt "$TIMELIMIT_US"
			then
				COMMAND_EXIT_CODE=${FAIL_TIME_LIMIT_EXCEEDED}
				FormatExitCode "$COMMAND_EXIT_CODE"
				REPORT_DEBUG The command was killed because it exceeded the CPU Time limit - setting exit code to $result
				REPORT_BRIEF TLE
				GenXML "No - Time limit exceeded" "${cputime}us > ${TIMELIMIT_US}us"
				if test "$val_result" = "${EXITCODE_AC}" -o "${KILL_WA_VALIDATOR}" -eq 0
				then
					KillValidator
					break
				fi
			elif test "$COMMAND_EXIT_CODE" -ge 128
			then
				FormatExitCode "$COMMAND_EXIT_CODE"
				REPORT_DEBUG The command terminated due to a signal with exit code $result
			else
				REPORT_DEBUG The command terminated normally with exit code $result
			fi
		fi
		REPORT_DEBUG Finished executing "$COMMAND $*"
		FormatExitCode "$COMMAND_EXIT_CODE"
		REPORT_DEBUG "$COMMAND" exited with exit code $result

		if test "$COMMAND_EXIT_CODE" -ne 0
		then
			REPORT_DEBUG Contestant finished with a non-zero exit code $result
			REPORT_BRIEF RTE
			GenXML "No - Run-time Error" "Exit status $wstat"
			# If validator finished with AC, but submission has a bad exit code, we use that.
			# Or, if we didn't kill off the submission, and want to use it's exit code
			if test "$val_result" = "${EXITCODE_AC}" -o "${KILL_WA_VALIDATOR}" -eq 0
			then
				KillValidator
				break
			fi
		else
			# COMMAND_EXIT_CODE is 0, let's see if the validator finished.  If
			# so, we're done, since the validator already created it's disposition.
			if test -n "$val_result"
			then
				break
			fi
		fi
		REPORT_DEBUG Waiting for interactive validator to finish...
	fi
done

DEBUG echo
REPORT "________________________________________"

rm -f "$INFIFO" "$OUTFIFO"

# TODO: determine how to pass more detailed pc2sandbox.sh results back to PC2... Perhaps in a file...

# return the exit code of the command as our exit code
FormatExitCode "$COMMAND_EXIT_CODE"
REPORT_DEBUG Returning exit code $result to PC2
exit $COMMAND_EXIT_CODE

# eof pc2sandbox_interactive.sh 

