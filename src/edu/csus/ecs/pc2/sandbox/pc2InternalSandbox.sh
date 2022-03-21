#!/bin/bash

# A rudimentary draft sandbox for testing; not meant for production
echo Setting Sandbox memlimit = $2 and timelimit = $3

# Q: the "memlimit" parameter is in MB, while the ulimit command expects K.  Will the shell do the following multiplication?
ulimit -H -v $2*1024 -t $3

echo Running "time" on team program $1
time $1

# Get the exit status of "time", which will be the exit status of the command executed by "time".
status = $?

# Return the command's exit status as the sandbox exit status
exit status
