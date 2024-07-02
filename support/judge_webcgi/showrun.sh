#!/bin/bash
. ./pc2common.sh
. ./webcommon.sh
. ./cdpcommon.sh

EXE_DIR_LINK=${EXE_LINK_PREFIX}$$
PROB_DIR_LINK=${PROB_LINK_PREFIX}$$

# Provide a way to look at the sandbox log
LogButton()
{
	# Read first judgment file to get compile time - it's compiled once for all
	GetJudgmentFromFile "$dir/${EXECUTE_DATA_PREFIX}.0.txt"
	if test -n ${result} -a -n ${compileTimeMS}
	then
		cat << LBEOF0
<center>
<p style="font-size:30px">The program took <b>${compileTimeMS}ms</b> to compile.
</center>
LBEOF0
	fi

	# Read the first briefcase file (if any) for limits
	MakeBriefcaseFile ${EXE_DIR_LINK} 1
	ReadBriefcase < $result
	if test -n ${cpulimms}
	then
		cpulimms=${cpulimms%%.*}
		cpusecs="$((cpulimms/1000))"
		if test ${cpusecs} != "1"
		then
			cpusecs="${cpusecs} seconds"
		else
			cpusecs="1 second"
		fi
		cat << LBEOF1
<center>
<p style="font-size:30px">The CPU Limit for this problem is <b>${cpusecs} (${cpulimms}ms)</b>.
</center>
LBEOF1
	fi
	if test -n ${memlim}
	then
		if test ${memlim} = "0"
		then
			memlim="Unlimited"
		else
			memlim=${memlim}MB
		fi
		cat << LBEOF1A
<center>
<p style="font-size:30px">The Memory limit for this problem is <b>${memlim}</b>.
</center>
LBEOF1A
	fi
	sandlog=${EXE_DIR_LINK}/${SANDBOX_LOG}
	if test -s ${sandlog}
	then
		cat << LBEOF2
<a href="$sandlog">Click here for the full sandbox log for this run</a>
<p>
<p>
LBEOF2
	fi
}

TableHeader()
{
	cat << EOFTH
<tr>
<th class="cent">Test</th><th class="cent">Disp</th><th class="cent">Judgment</th><th class="cent">Exit</th><th class="right">Execute Time</th><th class="right">Val Time</th><th class="cent">Val Success</th><th>Run Out</th><th>Run Err</th><th>Judge In</th><th>Judge Ans</th><th>Val Out</th><th>Val Err</th>
</tr>
EOFTH
}

GenFileLink()
{
	tstcase="$2"
	tstcase=$((tstcase-1))
	tstfile=$1.$tstcase.txt
	tstpath=$dir/$1.$tstcase.txt
	if test -s ${tstpath}
	then
		bytes=`stat -c %s ${tstpath}`
		echo '   <td><a href="'$EXE_DIR_LINK/$tstfile'">View ('$bytes' bytes)</td>'
	elif test -e ${tstpath}
	then
		echo '   <td>(Empty)</td>'
	else
		echo '   <td>Not found</td>'
	fi
}

GenFileLinkWithText()
{
	linkaddr="$1"
	linktext="$2"
	linkcolor="$3"
	bytes=`stat -c %s ${linkaddr}`
	echo '   <td><a href="'$linkaddr'" style="color:'$linkcolor'">'$linktext' ('$bytes' bytes)</td>'
}

TableRow()
{
	tc=$1
	judgment="$2"
	ec=$3
	exetm="$4"
	valtm="$5"
	jin="$6"
	jans="$7"
	if test "$8" = "true"
	then
		valsucc=Yes
	elif test "$8" = "false"
	then
		valsucc=No
	else
		valsucc="N/A"
	fi
	# Strip the stuff of before sample (or secret)
	jin=${jin##*/data/}
	jans=${jans##*/data/}
	# Just the basenames for link text
	jinbase=${jin##*/}
	jansbase=${jans##*/}
	if [[ $jin = sample/* ]]
	then
		lcolor=#00a0a0
	else
		lcolor=#00a000
	fi
	if test "${judgment}" = "AC"
	then
		jicon='<img class="judgeicon" src="../Correct.png">'
	elif test "${judgment}" = "CE"
	then
		jicon='<img class="judgeicon" src="../Warning.png">'
	else
		jicon='<img class="judgeicon" src="../Wrong.png">'
	fi

	echo '  <tr>'

	echo '   <td class="cent">'$tc'</td>'
	echo '   <td class="cent">'$jicon'</td>'
	echo '   <td class="cent">'$judgment'</td>'
	echo '   <td class="cent">'$ec'</td>'
	echo '   <td class="right">'$exetm'ms</td>'
	echo '   <td class="right">'$valtm'ms</td>'
	echo '   <td class="cent">'$valsucc'</td>'
	GenFileLink $TEST_OUT_PREFIX $tc
	GenFileLink $TEST_ERR_PREFIX $tc
	GenFileLinkWithText $PROB_DIR_LINK/data/"$jin" "$jinbase" $lcolor
	GenFileLinkWithText $PROB_DIR_LINK/data/"$jans" "$jansbase" $lcolor
	GenFileLink $TEST_VALOUT_PREFIX $tc
	GenFileLink $TEST_VALERR_PREFIX $tc

	echo '  </tr>'
}

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

DeleteOldestLinks
Preamble - 'Run '$run
HeaderNoBanner Details for Run Id $run - $probletter "("$probshort") - team$submitter - Judged: $judgment"

# Create links apache can access in our html folder
#rm ${EXE_DIR_LINK} ${PROB_DIR_LINK}
ln -s ${dir} ${EXE_DIR_LINK}
ln -s ${probdir} ${PROB_DIR_LINK}

LogButton

StartTable
TableHeader
for testcase in `ls $dir/${EXECUTE_DATA_PREFIX}.[0-9]*.txt 2>/dev/null | sed -e 's;^.*/;;' | sort -t. +1n`
do
	GetTestCaseNumber $testcase
	tc=$((result+1))
	# This will also source the execute data
	GetJudgmentFromFile $dir/$testcase
	judgment=$result
	ec=$executeExitValue
#	comptm=$compileTimeMS
	exetm=$executeTimeMS
	valtm=$validateTimeMS
	valsuc=$validationSuccess
	MakeBriefcaseFile "$dir" "$tc"
	ReadBriefcase < ${result}
	TableRow "$tc" "$judgment" "$ec" "$exetm" "$valtm" "$judgein" "$judgeans" "$valsuc"
done

Trailer

#rm -f ${EXE_DIR_LINK} ${PROB_DIR_LINK}
exit 0
