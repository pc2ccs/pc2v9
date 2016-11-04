#!/bin/bash

if [ $# -lt 4 ]
then
	echo Invalid Arguments
	echo Please Enter in the following format:
	echo 1. Filename '('Executable binary or classname w/o .class')'
	echo 2. Indicator '(' c for C/C++ and java for Java')'
	echo 3. Policy file to be used
	echo 4. Output log file name
	echo Optional:
	echo 5. timeout in seconds
	
else
	filename=$1
	indicator=$2
	policy=$3
	outlogfile=$4
	timeout=30
	if [ $# -ge 5 ]; then
		timeout=$5
	fi
	timeout2=`expr $timeout + 1`
	
	if [ -x /usr/local/pc2v9/bin/execute ]
	then 
		if [ -f $outlogfile ]; then
			rm $outlogfile
		fi
		if [ -f timelimit.txt ]; then
			rm timelimit.txt
		fi
		starttime=`date "+%s"`
		(
		ulimit -m 2048 -t $timeout2 -c 100
		/usr/local/pc2v9/bin/execute $filename $indicator $policy $outlogfile 2> /dev/null
		)
		# shell exits with 0 regardless
		endtime=`date "+%s"`
		ptime=$(($endtime - $starttime))
		if [ $ptime -gt $timeout ]; then
		    echo "No Security Violation" > $outlogfile
		    echo "$ptime" > timelimit.txt
		fi
	else
		if [ $indicator ==  "c" ]
		then
			./$filename
		fi
		
		if [ $indicator == "java" ]
		then
			java -client -Xss8m -Xmx2048m $filename
		fi
	fi
fi

