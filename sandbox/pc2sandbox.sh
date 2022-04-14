#!/bin/bash
#  
# File:    pc2_sandbox.sh 
# Purpose: a Unix sandbox for pc2
#  

# TODO handle signals for when ulimits are exceeded

FAIL_EXIT_CODE=43

# ------------------------------------------------------------

usage()
{
  cat <<SAGE
Usage: pc2_sandbox.sh memlimit timelimit command command_args

memlimit, in Meg
timelimit, in seconds

SAGE
}
# ------------------------------------------------------------

if [ -z "$1" ] ; then
   echo $0: No command line arguments
   exit $FAIL_EXIT_CODE
fi 

if [ -z "$3" ] ; then
   echo $0: expected 3 or more arguments, found: $*
   exit $FAIL_EXIT_CODE
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

# ulimit memory limit in K
# set limit in Meg (add 000)
if [ "$MEMLIMIT" -gt "0" ] ; then
  ulimit -m ${MEMLIMIT}000
fi 

# time limit in seconds
ulimit -t $TIMELIMIT

# run the command
time $COMMAND $*

# exit with return code from time
exit $?

# eof pc2_sandbox.sh
