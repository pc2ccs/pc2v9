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
# Author: John Clevenger, based on an earlier version by Doug Lane

FAIL_EXIT_CODE=43
FAIL_NO_ARGS_EXIT_CODE=44
FAIL_INSUFFICIENT_ARGS_EXIT_CODE=45
FAIL_INVALID_CGROUP_INSTALLATION=46
FAIL_MISSING_CGROUP_CONTROLLERS_FILE=47
FAIL_MISSING_CGROUP_SUBTREE_CONTROL_FILE=48
FAIL_CPU_CONTROLLER_NOT_ENABLED=49
FAIL_MEMORY_CONTROLLER_NOT_ENABLED=50

CGROUP_PATH=/sys/fs/cgroup
PC2_CGROUP_PATH=$CGROUP_PATH/pc2
PC2_SANDBOX_CGROUP_PATH=$PC2_CGROUP_PATH/pc2sandbox

# control whether the script outputs debug/tracing info
_DEBUG="off"   # change this to anything other than "on" to disable debug/trace output
function DEBUG()
{
  [ "$_DEBUG" == "on" ] && $@
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
# the rest is the command args

# make sure we have CGroups V2 properly installed on this system, including a PC2 structure

DEBUG echo checking PC2 CGroup V2 installation...
if [ ! -d "$PC2_SANDBOX_CGROUP_PATH" ]; then
   echo $0: expected pc2sandbox CGroups v2 installation in $PC2_SANDBOX_CGROUP_PATH 
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

# set the specified memory limit - input is in MB, cgroup v2 requires bytes, so multiply by 1M
# but only if > 0.
# "max" means unlimited, which is the cgroup v2 default
DEBUG echo checking memory limit
if [ "$MEMLIMIT" -gt "0" ] ; then
  DEBUG echo setting memory limit to $MEMLIMIT MB
  echo $(( $MEMLIMIT * 1000000 ))  > $PC2_SANDBOX_CGROUP_PATH/memory.max
else
  DEBUG echo setting memory limit to max, meaning no limit
  echo "max" > $PC_SANDBOX_CGROUP_PATH/memory.max  
fi

# set the specified CPU time limit - input is in secs, cgroup v2 requires usec, so multiply by 1M.
# cgroup v2 expects two parameters:  absolute time and "period", but if only one is provided it is "absolute time"
DEBUG echo setting cpu limit to $TIMELIMIT seconds
echo $(( $TIMELIMIT * 1000000 ))  > $PC2_SANDBOX_CGROUP_PATH/cpu.max

#put the current process (and implicitly its children) into the pc2sandbox cgroup.
#  Note that CGroups V2 defines that writing "0" to the cgroups.proc file means "current process".
DEBUG echo putting $$ into $PC2_SANDBOX_CGROUP_PATH cgroup
# TODO: need to remove the requirement for sudo in the following command, which is needed here because
# this shell is in the root cgroup by default and does not have write access to the root cgroup.procs file --
# which it never should.
# Need to have a way to put this shell into the pc2/pc2sandbox/cgroup.procs file as root, once.
# See https://man7.org/conf/ndctechtown2021/cgroups-v2-part-2-diving-deeper-NDC-TechTown-2021-Kerrisk.pdf,
# slides 25-28.
sudo echo 0 > $PC2_SANDBOX_CGROUP_PATH/cgroup.procs

# run the command
# the following are the cgroup-tools V1 commands; need to find cgroup-tools v2 commands
# echo Using cgexec to run $COMMAND $*
# cgexec -g cpu,memory:/pc2 $COMMAND $*

# since we don't know how to use cgroup-tools to execute, just execute it directly (it's a child so it
#  should still fall under the cgroup limits).
DEBUG echo Executing $COMMAND $* 
$COMMAND $*

COMMAND_EXIT_CODE=$?

DEBUG echo Finished executing $COMMAND $*
DEBUG echo $COMMAND exited with exit code $COMMAND_EXIT_CODE
DEBUG echo

# TODO: determine how to pass pc2sandbox.sh results back to PC2...

# return the exit code of the command as our exit code
exit $COMMAND_EXIT_CODE

# eof pc2sandbox.sh 
