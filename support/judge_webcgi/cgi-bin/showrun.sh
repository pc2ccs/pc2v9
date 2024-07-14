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
	echo '<center><p style="font-size:30px">'
	if test -n "${result}" -a -n "${compileTimeMS}"
	then
		cat << LBEOF0
The program took <b>${compileTimeMS}ms</b> to compile.
<br>
LBEOF0
	fi

	# Read the first briefcase file (if any) for limits
	MakeBriefcaseFile ${EXE_DIR_LINK} 1
	if test -s "${result}"
	then
		ReadBriefcase < $result
	else
		cpulimms=""
		memlim=""
	fi
	if test -n "${cpulimms}"
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
The CPU Limit for this problem is <b>${cpusecs} (${cpulimms}ms)</b>.
<br>
LBEOF1
	else
		cat << LBEOF1AA
The CPU Limit for this problem is <b>N/A</b>.
<br>
LBEOF1AA
	fi
	if test -n "${memlim}"
	then
		if test ${memlim} = "0"
		then
			memlim="Unlimited"
		else
			memlim=${memlim}MiB
		fi
		cat << LBEOF1A
The Memory limit for this problem is <b>${memlim}</b>.
<br>
LBEOF1A
	else
		cat << LBEOF1AAA
The Memory limit for this problem is <b>N/A</b>.
<br>
LBEOF1AAA
	fi

	GetSourceList $lang ${EXE_DIR_LINK}
	srclist="$result"
	echo "Submission Source Code: "
	# Get source files
	sep=""
	for sfile in $srclist
	do
		if test -z "$sep"
		then
			sep=" "
		else
			echo -n "$sep"
		fi
		GenGenericFileLink "$sfile"
	done
	echo "<br>"
	echo "</center>"

	sandlog=${EXE_DIR_LINK}/${SANDBOX_LOG}
	if test -s "${sandlog}"
	then
		cat << LBEOF2
<a href="$sandlog" target="_blank">Click here for the full sandbox log for this run</a>
<p>
<p>
LBEOF2
	fi
}

TableHeader()
{
	cat << EOFTH
<tr>
 <th class="cent">Test</th>
 <th class="cent">Disp</th>
 <th class="cent">Judgment</th>
 <th class="cent">Exit</th>
 <th class="right">Execute Time</th>
 <th class="right">MiB Used</th>
 <th class="right">Val Time</th>
 <th class="cent">Val Success</th>
 <th>Run stdout</th>
 <th>Run stderr</th>
 <th>Judge In</th>
 <th>Judge Ans</th>
 <th>Val Out</th>
 <th>Val Err</th>
</tr>
EOFTH
}

# Usage is:
# GenGenericFileLink full-path-to-file [link-text]
# If no [link-text] supplied, then use basename of file
# This just makes up the href.
#
GenGenericFileLink()
{
	tstfile=$1
	gentxt="$2"
	if test -z "$gentxt"
	then
		gentxt=${tstfile##*/}
	fi
	tstpath=$tstfile
	if test -s "${tstpath}"
	then
		echo -n '<a href="'$EXE_DIR_LINK/$tstfile'" target="_blank">'$gentxt'</a>'
	elif test -e "${tstpath}"
	then
		echo -n "($gentxt Empty)"
	else
		echo -n "$gentxt Not found"
	fi
}

# Usage is:
# GenGenericFileLinkTd full-path-to-file [link-text]
# If no [link-text] supplied, then use basename of file
# This includes the bracketing <td>...</td>
#
GenGenericFileLinkTd()
{
	tstfile=$1
	gentxt="$2"
	if test -z "$gentxt"
	then
		gentxt=${tstfile##*/}
	fi
	tstpath=$dir/$tstfile
	if test -s "${tstpath}"
	then
		echo '   <td><a href="'$EXE_DIR_LINK/$tstfile'" target="_blank">'$gentxt'</a></td>'
	elif test -e "${tstpath}"
	then
		echo '   <td>(Empty)</td>'
	else
		echo '   <td>Not found</td>'
	fi
}

GenFileLink()
{
	tstcase="$2"
	tstcase=$((tstcase-1))
	tstfile=$1.$tstcase.txt
	tstpath=$dir/$1.$tstcase.txt
	if test -s "${tstpath}"
	then
		bytes=`stat -c %s ${tstpath}`
		echo '   <td><a href="'$EXE_DIR_LINK/$tstfile'" target="_blank">View ('$bytes' bytes)</td>'
	elif test -e "${tstpath}"
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
	if test -z "${linktext}"
	then
		echo '   <td>N/A</td>'
	else
		bytes=`stat -c %s ${linkaddr}`
		echo '   <td><a href="'$linkaddr'" style="color:'$linkcolor'" target="_blank">'$linktext' ('$bytes' bytes)</td>'
	fi
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
	memused="$9"
	memusedbytes="${10}"
	exesandms="${11}"
	srclist="${12}"
	# Create link to report/testcase file for testcase number
	MakeTestcaseFile ${EXE_DIR_LINK} ${tc}
	tcreport="${result}"
	if test ! -s "${tcreport}"
	then
		tcreport=""
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

	if test -n "${tcreport}"
	then
		echo '   <td class="cent"><a href="'${tcreport}'" target="_blank">'$tc'</a></td>'
	else
		echo '   <td class="cent">'$tc'</td>'
	fi
	echo '   <td class="cent">'$jicon'</td>'
	echo '   <td class="cent">'$judgment'</td>'
	echo '   <td class="cent">'$ec'</td>'
	if [[ ${exesandms} = [0-9]* ]]
	then
		echo '   <td class="right"><div class="tooltip">'$exetm'ms<span class="tooltiptext">'$exesandms'ms in the Sandbox</span></div></td>'
	else
		echo '   <td class="right">'$exetm'ms</td>'
	fi
	if [[ ${memusedbytes} = [0-9]* ]]
	then
		echo '   <td class="right"><div class="tooltip">'$memused'MiB<span class="tooltiptext">'$memusedbytes' bytes</span></div></td>'
	else
		echo '   <td class="right">'$memused'</td>'
	fi
	echo '   <td class="right">'$valtm'ms</td>'
	echo '   <td class="cent">'$valsucc'</td>'
	if test "$judgment" != "CE"
	then
		GenFileLink $TEST_OUT_PREFIX $tc
		GenFileLink $TEST_ERR_PREFIX $tc
	else
		GenGenericFileLinkTd $COMP_OUT_PREFIX.pc2 "View Compiler"
		GenGenericFileLinkTd $COMP_ERR_PREFIX.pc2 "View Compiler"
	fi
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
Styles
EndStyles
StartHTMLDoc
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
	if test -s "${result}"
	then
		ReadBriefcase < ${result}
	else
		judgein=""
		judgeans=""
		mempeak=""
		mempeakbytes=""
	fi
	GetSourceList $lang ${EXE_DIR_LINK}
	TableRow "$tc" "$judgment" "$ec" "$exetm" "$valtm" "$judgein" "$judgeans" "$valsuc" "$mempeak" "$mempeakbytes" "$execpums" "$result"
done

Trailer

#rm -f ${EXE_DIR_LINK} ${PROB_DIR_LINK}
exit 0
