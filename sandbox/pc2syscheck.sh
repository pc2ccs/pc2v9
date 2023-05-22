#!/bin/bash
# Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
#  
# File:    pc2syscheck.sh
# Purpose: Check host system to make sure it's setup properly for sandbox usage
# 
# Author: John Buck

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

CGROUP_PATH=/sys/fs/cgroup
PC2_CGROUP_PATH=$CGROUP_PATH/pc2

# control whether the script outputs debug/tracing info
_DEBUG="on"   # change this to anything other than "on" to disable debug/trace output
DEBUG_FILE=sandbox.log
function DEBUG()
{
  [ "$_DEBUG" == "off" ] && $@ >> $DEBUG_FILE
}

# ------------------------------------------------------------

usage()
{
  echo Usage: pc2syscheck.sh [--help]
  echo "   Exit code of 0 means success, system supports pc2 sandbox"
  echo "   Exit code of $FAIL_INVALID_CGROUP_INSTALLATION  - cgroup not installed on system"
  echo "   Exit code of $FAIL_MISSING_CGROUP_CONTROLLERS_FILE - missing cgroup controller file"
  echo "   Exit code of $FAIL_MISSING_CGROUP_SUBTREE_CONTROL_FILE - missing cgroup subtree file"
  echo "   Exit code of $FAIL_CPU_CONTROLLER_NOT_ENABLED - CPU cgroup controller is not enabled"
  echo "   Exit code of $FAIL_MEMORY_CONTROLLER_NOT_ENABLED - MEMORY cgroup controller is not enabled"
  echo "   Exit code of 1 indicates the --help option was specified"
}

if test $# -eq 1 -a "$1" = "--help"
then
	usage
	exit 1
fi

# make sure we have cgroup V2 properly installed on this system, including a PC2 structure

DEBUG echo checking cgroup V2 installation...
if [ ! -d "$CGROUP_PATH" ]; then
   echo $0: Failed to find expected cgroup v2 installation in /sys/fs/cgroup
   exit $FAIL_INVALID_CGROUP_INSTALLATION
fi

DEBUG echo checking PC2 cgroup V2 installation...
if [ ! -d "$PC2_CGROUP_PATH" ]; then
   echo $0: Failed to find expected "'pc2'" sandbox cgroup v2 installation in $PC2_CGROUP_PATH 
   exit $FAIL_INVALID_CGROUP_INSTALLATION
fi

if [ ! -f "$CGROUP_PATH/cgroup.controllers" ]; then
   echo $0: Failure: The cgroup.controllers file is missing in $CGROUP_PATH
   exit $FAIL_MISSING_CGROUP_CONTROLLERS_FILE
fi

if [ ! -f "$CGROUP_PATH/cgroup.subtree_control" ]; then
   echo $0: Failure: The cgroup.subtree_control file is missing in $CGROUP_PATH
   exit $FAIL_MISSING_CGROUP_SUBTREE_CONTROL_FILE
fi

# make sure the cpu and memory controllers are enabled
if ! grep -q -F "cpu" "$CGROUP_PATH/cgroup.subtree_control"; then
   echo $0: Failure: The cgroup.subtree_control file in $CGROUP_PATH does not enable cpu controller
   exit $FAIL_CPU_CONTROLLER_NOT_ENABLED
fi

if ! grep -q -F "memory" "$CGROUP_PATH/cgroup.subtree_control"; then
   echo $0: Failure: The cgroup.subtree_control file in $CGROUP_PATH does not enable memory controller
   exit $FAIL_MEMORY_CONTROLLER_NOT_ENABLED
fi


# we seem to have a valid cgroup installation
DEBUG echo ...done.
exit 0
