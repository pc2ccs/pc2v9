#!/bin/bash
EXE_DIR_LINK=../exedir$$
PROB_DIR_LINK=../probdir$$

. ./pc2common.sh
. ./webcommon.sh
. ./cdpcommon.sh

TableHeader()
{
	cat << EOFTH
<tr>
<th class="cent">Test</th><th class="cent">Disp</th><th class="cent">Judgment</th><th class="cent">Exit</th><th class="right">Compile Time</th><th class="right">Execute Time</th><th class="right">Val Time</th><th class="cent">Val Success</th><th>Run Out</th><th>Run Err</th><th>Judge In</th><th>Judge Ans</th><th>Val Out</th><th>Val Err</th>
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

TableRow()
{
	tc=$1
	judgment="$2"
	ec=$3
	comptm="$4"
	exetm="$5"
	valtm="$6"
	if test "$7" = "true"
	then
		valsucc=Yes
	elif test "$7" = "false"
	then
		valsucc=No
	else
		valsucc="N/A"
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
	echo '   <td class="right">'$comptm'ms</td>'
	echo '   <td class="right">'$exetm'ms</td>'
	echo '   <td class="right">'$valtm'ms</td>'
	echo '   <td class="cent">'$valsucc'</td>'
	GenFileLink $TEST_OUT_PREFIX $tc
	GenFileLink $TEST_ERR_PREFIX $tc
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

Preamble - 'Run '$run
HeaderNoBanner Details for RunId $run - $probletter "("$probshort") - team$submitter - Judged: $judgment"

StartTable
TableHeader
rm ${EXE_DIR_LINK} ${PROB_DIR_LINK}
ln -s ${dir} ${EXE_DIR_LINK}
ln -s ${probdir} ${PROB_DIR_LINK}
for testcase in `ls $dir/${EXECUTE_DATA_PREFIX}.[0-9]*.txt 2>/dev/null | sed -e 's;^.*/;;' | sort -t. +1n`
do
	GetTestCaseNumber $testcase
	tc=$((result+1))
	# This will also source the execute data
	GetJudgmentFromFile $dir/$testcase
	judgment=$result
	ec=$executeExitValue
	comptm=$compileTimeMS
	exetm=$executeTimeMS
	valtm=$validateTimeMS
	valsuc=$validationSuccess
	TableRow "$tc" "$judgment" "$ec" "$comptm" "$exetm" "$valtm" "$valsuc"
done

Trailer
exit 0
