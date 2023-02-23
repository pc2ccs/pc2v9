#!/bin/bash
#  
# File:    pc2sandbox.sh 
# Purpose: a sandbox for pc2 using Linux CGroups v2.
# Input arguments:
#  $1: memory limit in MB
#  $2: time limit in seconds
#  $3: command to be executed
#  $4... : command arguments
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

# Process ID of submission
submissionpid=""

CGROUP_PATH=/sys/fs/cgroup
PC2_CGROUP_PATH=$CGROUP_PATH/pc2
# TODO: should have unique runbox in case multiple instances are executing in separate judges
# For now, just use userid
PC2_SANDBOX_CGROUP_PATH=$PC2_CGROUP_PATH/sandbox_$USER

# control whether the script outputs debug/tracing info
_DEBUG="on"   # change this to anything other than "on" to disable debug/trace output
DEBUG_FILE=sandbox.log
function DEBUG()
{
  [ "$_DEBUG" == "on" ] && $@ >> $DEBUG_FILE
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

# Function to handle getting killed by PC2's execute timer (basically, this
# is wall-time exceeded which is execute time limit + 1 second
HandleTerminateFromPC2()
{
	DEBUG echo "Received TERMINATE signal from PC2"
	if test -n "$submissionpid"
	then
		DEBUG echo "Killing off submission process $submissionpid"
		kill -9 "$submissionpid"
	fi
	DEBUG echo $0: Wall time exceeded - exiting with code $FAIL_WALL_TIME_LIMIT_EXCEEDED
	exit $FAIL_WALL_TIME_LIMIT_EXCEEDED 
}

# ------------------------------------------------------------

if [ "$#" -lt 1 ] ; then
   echo $0: No command line arguments
   exit $FAIL_NO_ARGS_EXIT_CODE
fi 

if [ "$#" -lt 3 ] ; then
   echo $0: expected 3 or more arguments, found: $*
   exit $FAIL_INSUFFICIENT_ARGS_EXIT_CODE
fi 

if [ "$1" = "-h" -o "$1" = "--help" ] ; then
   usage
   exit $FAIL_EXIT_CODE
fi 

MEMLIMIT=$1
TIMELIMIT=$2
COMMAND=$3
shift
shift
shift

#### Debugging - just set expected first 3 args to: 16MB 5seconds
###MEMLIMIT=8
###TIMELIMIT=2
###COMMAND=$1
###shift

# the rest is the command args

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


# we seem to have a valid CGroup installation
DEBUG echo ...done.

if test -d $PC2_SANDBOX_CGROUP_PATH
then
	DEBUG echo Removing existing sandbox to start clean
	rmdir $PC2_SANDBOX_CGROUP_PATH
fi

DEBUG echo Creating sandbox $PC2_SANDBOX_CGROUP_PATH
if ! mkdir $PC2_SANDBOX_CGROUP_PATH
then
	DEBUG echo Can not create $PC2_SANDBOX_CGROUP_PATH
	exit $FAIL_INVALID_CGROUP_INSTALLATION
fi

# set the specified memory limit - input is in MB, cgroup v2 requires bytes, so multiply by 1M
# but only if > 0.
# "max" means unlimited, which is the cgroup v2 default
DEBUG echo checking memory limit
if [ "$MEMLIMIT" -gt "0" ] ; then
  DEBUG echo setting memory limit to $MEMLIMIT MB
  echo $(( $MEMLIMIT * 1024 * 1024 ))  > $PC2_SANDBOX_CGROUP_PATH/memory.max
  echo 1  > $PC2_SANDBOX_CGROUP_PATH/memory.swap.max
else
  DEBUG echo setting memory limit to max, meaning no limit
  echo "max" > $PC_SANDBOX_CGROUP_PATH/memory.max  
  echo "max" > $PC_SANDBOX_CGROUP_PATH/memory.swap.max  
fi

# set the specified CPU time limit - input is in secs, cgroup v2 requires usec, so multiply by 1M.
# cgroup v2 expects two parameters:  absolute time and "period", but if only one is provided it is "absolute time"
DEBUG echo setting cpu limit to $TIMELIMIT seconds
ulimit -t $TIMELIMIT
TIMELIMIT_US=$((TIMELIMIT * 1000000))
#echo $TIMELIMIT_US  > $PC2_SANDBOX_RUN_CGROUP_PATH/cpu.max

DEBUG echo setting maximum user processes to 32 + whatever the user is currently using
ulimit -u $((32+`ps -T -u $USER | wc -l`))

#put the current process (and implicitly its children) into the pc2sandbox cgroup.
DEBUG echo putting $$ into $PC2_SANDBOX_CGROUP_PATH cgroup
if ! echo $$ > $PC2_SANDBOX_CGROUP_PATH/cgroup.procs
then
	echo Failed. quitting.
	exit 1
fi

# run the command
# the following are the cgroup-tools V1 commands; need to find cgroup-tools v2 commands
# echo Using cgexec to run $COMMAND $*
# cgexec -g cpu,memory:/pc2 $COMMAND $*

# since we don't know how to use cgroup-tools to execute, just execute it directly (it's a child so it
#  should still fall under the cgroup limits).
DEBUG echo Executing $COMMAND $* 

# Set up trap handler to catch wall-clock time exceeded and getting killed by PC2's execute timer
trap HandleTerminateFromPC2 15

$COMMAND $* <&0 &
# Remember child's PID for possible killing off later
submissionpid=$!
# Wait for child
wait $submissionpid

COMMAND_EXIT_CODE=$?

# See if we were killed due to memory - this is a kill 9 if it happened

kills=`grep oom_kill $PC2_SANDBOX_CGROUP_PATH/memory.events | cut -d ' ' -f 2`

if test "$kills" != "0"
then
	DEBUG echo The command was killed due to out of memory
	COMMAND_EXIT_CODE=${FAIL_MEMORY_LIMIT_EXCEEDED}
else
	# Get cpu time
	cputime=`grep usage_usec $PC2_SANDBOX_CGROUP_PATH/cpu.stat | cut -d ' ' -f 2`
	if test "$cputime" -gt "$TIMELIMIT_US"
	then
		DEBUG echo The command was killed due to out of CPU Time "($cputime > $TIMELIMIT_US)"
		COMMAND_EXIT_CODE=${FAIL_TIME_LIMIT_EXCEEDED}
	elif test "$COMMAND_EXIT_CODE" -ge 128
	then
		DEBUG echo The command terminated abnormally with exit code $COMMAND_EXIT_CODE
	fi
fi
DEBUG echo Finished executing $COMMAND $*
DEBUG echo $COMMAND exited with exit code $COMMAND_EXIT_CODE
DEBUG echo

# TODO: determine how to pass pc2sandbox.sh results back to PC2...

# return the exit code of the command as our exit code
exit $COMMAND_EXIT_CODE

# eof pc2sandbox.sh 

