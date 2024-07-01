#!/bin/bash
. ./pc2common.sh
. ./webcommon.sh
. ./cdpcommon.sh


# Parse query string into dictionary args
# This is not terribly secure.
declare -a parm
sIFS="$IFS"
IFS='=&'
parm=($QUERY_STRING)
IFS=$sIFS
for ((i=0; i<${#parm[@]}; i+=2))
do
    a=${parm[i]}
    eval $a=${parm[i+1]}
done
# Done parsing

Preamble - 'Run '$run
HeaderNoBanner Details for RunId $run - $probletter "("$probshort") - team$submitter - Judged: $judgment"

Trailer
exit 0
