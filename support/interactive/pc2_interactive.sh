#!/bin/bash
# Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
#  
# File:    pc2_interacive.sh 
# Purpose: run interactive submission with interative validator
# Input arguments:
#  $1: interactive-validator
#  $2: judge.in
#  $3: judge.out
#  $4: testcase number
#  $5: command to be executed
#  $6... : command arguments
# 
# Author: John Buck, based on earlier versions by John Clevenger and Doug Lane

# FAIL_RETCODE_BASE is 128 + 64 + xx
# 128 = system error, like signal
# 64 = biggest used signal
FAIL_RETCODE_BASE=192
FAIL_EXIT_CODE=$((FAIL_RETCODE_BASE+43))
FAIL_NO_ARGS_EXIT_CODE=$((FAIL_RETCODE_BASE+44))
FAIL_INSUFFICIENT_ARGS_EXIT_CODE=$((FAIL_RETCODE_BASE+45))
FAIL_WALL_TIME_LIMIT_EXCEEDED=$((FAIL_RETCODE_BASE+53))
FAIL_INTERACTIVE_ERROR=$((FAIL_RETCODE_BASE+55))

# Maximum number of sub-processes before we will kill it due to fork bomb
# This gets added to the current number of executing processes for this user.
MAXPROCS=32

# taskset cpu mask for running submission on single processor
CPUMASK=0x08

# Process ID of submission
submissionpid=""

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
DEBUG_FILE=interactive.log
function DEBUG()
{
  [ "$_DEBUG" == "on" ] && $@ >> $DEBUG_FILE
}

# For per-testcase reporting/logging
function REPORT()
{
	if test -n "$REPORTFILE"
	then
		echo $* >> $REPORTFILE
	fi
}

# For per-testcase report and debugging both
function REPORT_DEBUG()
{
	if test -n "$REPORTFILE"
	then
		echo $* >> $REPORTFILE
	fi
	[ "$_DEBUG" == "on" ] && echo $@ >> $DEBUG_FILE
}

# ------------------------------------------------------------

usage()
{
  cat <<SAGE
Usage: pc2_interactive.sh interactive-validator judgein judgeans testcase command command_args

interactive-validator, judge's program to execute to provide interactive responses to the submission
judgein, judges input file
judgeans, judges answer file
testcase, test case number (for logging)

SAGE
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
# is wall-time exceeded which is execute time limit limit
HandleTerminateFromPC2()
{
	DEBUG echo "Received TERMINATE signal from PC2"
	KillValidator
	if test -n "$submissionpid" -a -d /proc/$submissionpid
	then
		DEBUG echo "Killing off submission process $submissionpid"
		kill -9 "$submissionpid"
	fi
	DEBUG echo $0: Wall time exceeded - exiting with code $FAIL_WALL_TIME_LIMIT_EXCEEDED
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
   echo $0: No command line arguments
   exit $FAIL_NO_ARGS_EXIT_CODE
fi 

if [ "$1" = "-h" -o "$1" = "--help" ] ; then
   usage
   exit $FAIL_EXIT_CODE
fi 

if [ "$#" -lt 5 ] ; then
   echo $0: expected 5 or more arguments, found: $*
   exit $FAIL_INSUFFICIENT_ARGS_EXIT_CODE
fi 

VALIDATOR="$1"
JUDGEIN="$2"
JUDGEANS="$3"
TESTCASE="$4"
COMMAND="$5"
shift 5

# the rest of the commmand line arguments  are the command args for the submission


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

# Remember wall time when we started
starttime=`GetTimeInMicros`

# run the command
REPORT_DEBUG Executing "taskset $CPUMASK $COMMAND $* < $INFIFO > $OUTFIFO"

taskset $CPUMASK $COMMAND $* < $INFIFO > $OUTFIFO  &
# Remember child's PID for possible killing off later
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
		echo No more children found while waiting
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
				kill -9 "$submissionpid"
			fi
			# This is just determines if the program ran, not if it's correct.
			# The result file has the correctness in it.
			# We only do this if the contestant program has not finished yet.
			COMMAND_EXIT_CODE=0
		fi
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
		# Get wall time
		endtime=`GetTimeInMicros`

		REPORT Contestant PID $submissionpid finished status $wstat
		contestant_done=1
		COMMAND_EXIT_CODE=$wstat

		if test "$COMMAND_EXIT_CODE" -ge 128
		then
			REPORT_DEBUG The command terminated abnormally due to a signal with exit code $COMMAND_EXIT_CODE
		else
			walltime=$((endtime-starttime))
			REPORT_DEBUG The command terminated normally and took ${walltime}us wall time
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

# eof pc2_interactive.sh 

