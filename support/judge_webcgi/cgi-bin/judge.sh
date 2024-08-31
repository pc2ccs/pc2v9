#!/bin/bash
. ./pc2common.sh
. ./webcommon.sh
. ./cdpcommon.sh


TableHeader()
{
	cat << EOFTH
<tr>
<th>Run ID <span>&#x25b2;</span></th>
<th class="cent">Disp</th>
<th class="cent">Judgment <span>&#x25b2;</span></th>
<th>Problem <span>&#x25b2;</span></th>
<th>Team <span>&#x25b2;</span></th>
<th class="cent">Test Cases <span>&#x25b2;</span></th>
<th class="cent">Language <span>&#x25b2;</span></th>
<th class="cent">Judge <span>&#x25b2;</span></th>
<th>Time Judged <span>&#x25b2;</span></th>
</tr>
EOFTH
}

MyTableStyles()
{
	cat << EOFMYSTYLES
th {
    cursor: pointer;
}
EOFMYSTYLES
}

TableRow()
{
	dir="$1"
	runid=$2
	problet=$3
	shortname=$4
	langid=$5
	teamnum=$6
	judgment="$7"
	runtime="$8"
	testinfo="$9"
	judge="${10}"
	probname=""
	probdir=""
	if test -n "${shortname}"
	then
		probdir=${PC2_CDP}/${shortname}
		probstatement=${probdir}/problem_statement/problem.en.tex
		if test ! -s "{probstatement}"
		then
			probstatement=${probdir}/problem_statement/problem.tex
		fi
		if test -s "${probstatement}"
		then
			probname=`head -1 ${probstatement}`
			probname=${probname##*\{}
			probname=${probname%\}}
		fi
	fi
	problem="$problet - $probname (${shortname})"
	if test "${judgment}" = "AC"
	then
		jstyle="green"
		jicon='<img class="judgeicon" src="../Correct.png">'
	elif test "${judgment}" = "CE"
	then
		jicon='<img class="judgeicon" src="../Warning.png">'
	else
		jstyle="red"
		jicon='<img class="judgeicon" src="../Wrong.png">'
	fi
	echo '  <tr>'
	echo '   <td><a href="showrun.sh?run='$runid'&dir='$dir'&probdir='$probdir'&probletter='$problet'&probshort='$shortname'&lang='$langid'&submitter='$teamnum'&judgment='$judgment'">'"Run $runid</a></td>"
#	echo '   <td class="'$jstyle'">'$judgment"</td>"
#	echo '   <td><div class="rspecial">'$judgment"</div></td>"
	echo '   <td class="cent">'$jicon'</td>'
	echo '   <td class="cent">'$judgment'</td>'
	echo "   <td>$problem</td>"
	echo "   <td>team$teamnum</td>"
	echo "   <td class=\"cent\">$testinfo</td>"
	echo "   <td class=\"cent\">$langid</td>"
	echo "   <td class=\"cent\">$judge</td>"
	echo "   <td>$runtime</td>"
	echo "  </tr>"
}

###ParseProblemYaml
Preamble
Styles
MyTableStyles
EndStyles
StartHTMLDoc
Header
StartTable
TableHeader

# Format of the execute folders must be: ex_runid_probletter_probshort_teamnum_langid_judge
for exdir in `ls ${PC2_RUN_DIR} | grep -P '^ex_\d+_[A-Z]_[a-z\d]+_\d+_[a-z\d]+' | sort --field-separator=_ +1rn`
do
	# exdir looks like: ex_188_Y_compression_46103_cpp
	#                    RId P ProbShort   team# Lang
	# RId = Run ID
	# P = problem letter
	# Lang = CLICS Language id
	saveIFS="$IFS"
	IFS="_"
	set ${exdir}
	IFS="$saveIFS"
	if test $# -ge 6
	then
		exedir=${PC2_RUN_DIR}/$exdir
		runid=$2
		problet=$3
		probshort=$4
		teamnum=$5
		langid=$6
		judge=$7
		if test -z "${judge}"
		then
			judge="N/A"
		fi
		GetJudgment "${exedir}"
		judgment="${result}"
		runtime="${executeDateTime}"
		# Get how many total test cases there are
		probdir=${PC2_CDP}/${probshort}
		if test -n "${probdir}"
		then
			GetNumberOfTestCases "${probdir}"
			numcases=${result}
		else
			numcases="??"
		fi
		# Note that GetJudgment also filled in exdata with the last execute data
		GetTestCaseNumber "${exdata##./}"
		testcaseinfo=$((result+1))/${numcases}

		TableRow "$exedir" $runid $problet $probshort $langid $teamnum "$judgment" "$runtime" "$testcaseinfo" "$judge"
	fi
done
EndTable
TableSortScripts
Trailer
exit 0
