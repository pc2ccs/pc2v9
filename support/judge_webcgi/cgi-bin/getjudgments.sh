#!/bin/bash

# Set to 1 to search through all test cases looking for first failure, otherwise
# it will use the last one.  In practice, it makes little difference in terms of
# how long it takes, so might as well set it to 1
USE_ALL_TESTCASES=1

. ./pc2common.sh
. ./webcommon.sh
. ./cdpcommon.sh

CACHEDIR=${JUDGE_HOME}/html/cache
LASTFILE=${CACHEDIR}/lastfile

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
	echo '  {'
	echo '   "runid":"'$runid'"',
	echo '   "href":"http://'$hostname'/cgi-bin/showrun.sh?run='$runid'&dir='$dir'&probdir='$probdir'&probletter='$problet'&probshort='$shortname'&lang='$langid'&submitter='$teamnum'&judgment='$judgment'"',
	echo '   "judgment":"'$judgment'"',
	echo '   "problem":"'$problem'"',
	echo '	 "problem_letter":"'$problet'"',
	echo '   "problem_short_name":"'$probshort'"',
	echo '   "directory":"'$dir'"',
	echo '   "problem_dir":"'$probdir'"',
	echo '   "team_number":"'$teamnum'"',
	echo '   "test_info":"'$testinfo'"',
	echo '   "language_id":"'$langid'"',
	echo '   "judge":"'$judge'"',
	echo '   "runtime":"'$runtime'"'
	echo '  }'
}

hostname=`hostname`

# Create cache dir, if it does not exist
mkdir -p ${CACHEDIR}
# Create last update time file if it does not exist
if test ! -s ${LASTFILE}
then
	lastdate="2024-01-01 00:00:01"
else
	lastdate=`cat ${LASTFILE}`
fi
newdate="${lastdate}"

#echo $LASTFILE contains date $lastdate 1>&2

for exedir in `find ${PC2_RUN_DIR} -name  'ex_[0-9]*_[A-Z]_[a-z][a-z0-9]*_[0-9]*_[a-z0-9]*_[a-z][a-z0-9]*' -a -newermt "${lastdate}" -a -type d`
do
	# Strip leading PC2_RUN_DIR
	exdir=${exedir##*/}
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
		# Update last update time if newer
		filetime=`stat --format %y $exedir`
		if [[ -z "$newdate" || "${filetime}" > "${newdate}" ]]
		then
			newdate="${filetime}"
		fi
		cachefile=${CACHEDIR}/$exdir
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
		if test ${USE_ALL_TESTCASES} -eq 1
		then
			GetJudgment "${exedir}"
		else
			GetLastJudgment "${exedir}"
		fi
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
		TableRow "$exedir" $runid $problet $probshort $langid $teamnum "$judgment" "$runtime" "$testcaseinfo" "$judge" > $cachefile
	fi
done
if [[ -z "$lastdate" || "${newdate}" > "${lastdate}" ]]
then
	echo "${newdate}" > ${LASTFILE}
#	echo Updated $LASTFILE with $newdate 1>&2
fi

echo "Content-type: application/json"
echo ""

echo '['
sep=""
# Format of the execute folders must be: ex_runid_probletter_probshort_teamnum_langid_judge
for exdir in `ls ${CACHEDIR} | grep -P '^ex_\d+_[A-Z]_[a-z\-\d]+_\d+_[a-z\d]+' | sort --field-separator=_ +1rn`
do
	if test -n "$sep"
	then
		echo "  $sep"
	else
		sep=","
	fi
	cat ${CACHEDIR}/$exdir
done
echo ']'
exit 0
