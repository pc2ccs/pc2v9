# Meant to be "source'd" into bash scripts.

# The judge's home directory
JUDGE_HOME=/home/icpc

# Modify constants as necessary for your installation
PC2_RUN_DIR=${JUDGE_HOME}/pc2
PC2_CDP=${PC2_RUN_DIR}/current/config

EXE_LINK_PREFIX=../exedir
PROB_LINK_PREFIX=../probdir

# How many exedir and probdir links to keep around
NUM_LINK_KEEP=4

# Where can we find the contest banner png file for webpage headers
BANNER_FILE=banner.png
BANNER_IMAGE=${PC2_RUN_DIR}/current/contest/${BANNER_FILE}

# Where to the the result of script failure before execution
RESULT_FAILURE_FILE=failure.txt

# Where judgments are
REJECT_INI=${JUDGE_HOME}/pc2/reject.ini

# Where PC2 puts CLICS validator results
EXECUTE_DATA_PREFIX=executedata
# Where PC2 puts run output/error
TEST_OUT_PREFIX="teamoutput"
TEST_ERR_PREFIX="teamstderr"
TEST_VALOUT_PREFIX="valout"
TEST_VALERR_PREFIX="valerr"
COMP_OUT_PREFIX="cstdout"
COMP_ERR_PREFIX="cstderr"

# Detailed log of entire judging process
SANDBOX_LOG=sandbox.log

# Testcase file prefix
TESTCASE_REPORT_FILE_PREFIX="testcase"
# Briefcase file prefix
BRIEFCASE_FILE_PREFIX="briefcase"
REPORTS_DIR=reports

declare -A Judgments

InitJudgments()
{
	# Defauls, may be overriden by reject.ini
	Judgments["accepted"]="AC"
	Judgments["Accepted"]="AC"
	Judgments["timelimit"]="TLE"
	Judgments["run error"]="RTE"
	Judgments["compiler error"]="CE"
	if test -s "${REJECT_INI}"
	then
		while read j
		do
			if [[ $j = \#* ]]
			then
				continue
			fi
			savIFS="$IFS"
			IFS='|'
			set $j
			IFS="$savIFS"
			key="$1"
			shortcode="$2"
			case ${shortcode} in
			AC|CE|RTE|WA|TLE)	;;
			MLE)	shortcode="RTE (MLE)" ;;
			*)	shortcode="WA (${shortcode})" ;;
			esac
#			echo Mapping $key to $shortcode
			Judgments[$key]="$shortcode"
		done < $REJECT_INI
	else echo NO REJECT FILE
	fi
}

# Takes the judgement string, eg. "Wrong answer" as arg 1 and the validation Result (42 or 42) as arg 2
MapJudgment()
{
	jm="$1"
	vr="$2"
	result=${Judgments[$jm]}
	if test -z "${result}"
	then
		if test "${validationReturnCode}" -eq 0
		then
			if test "${vr}" = "43"
			then
				resul="WA"
			elif test "${vr}" = "42"
			then
				result="AC"
			else
				result="WA (Default)"
			fi
		else
			result="JE (Validator EC=${validationReturnCode})"
		fi

	fi
}

GetLastJudgmentFile()
{
	lj_exdir="$1"
	result=`ls $lj_exdir/${EXECUTE_DATA_PREFIX}.[0-9]*.txt 2>/dev/null | sed -e 's;^.*/;;' | sort -t. +1rn | head -1`
}

GetTestCaseNumber()
{
	if test -z "$1"
	then
		result=0
	else
		saveIFS="$IFS"
		IFS=.
		set ${1}
		result="$2"
		IFS="$saveIFS"
		if test -z "${result}"
		then
			result=0
		fi
	fi
}


GetLastJudgmentFile()
{
	lj_exdir="$1"
	result=`ls $lj_exdir/${EXECUTE_DATA_PREFIX}.[0-9]*.txt 2>/dev/null | sed -e 's;^.*/;;' | sort -t. +1rn | head -1`
}


GetJudgmentFromFile()
{
	exdata="$1"
	if test -z "${exdata}"
	then
		result="No results"
	else
		# Source the file
		. ${exdata}
		if test "${compileSuccess}" = "false"
		then
			result="CE"
		elif test "${executeSuccess}" = "true"
		then
			if test "${validationSuccess}" = "true"
			then
				MapJudgment "${validationResults}" "${validationReturnCode}"
			else
				result="JE (Validator error)"
			fi
		else
			result="RTE (Execute error)"
		fi
	fi
}

GetJudgment()
{
	dir=$1
	exdata=""
	if ! cd ${dir}
	then
		result="Not found"
	elif test -s "${RESULT_FAILURE_FILE}"
	then
		jerr=`cat ${RESULT_FAILURE_FILE}`
		result="JE ($jerr)"
	else
		# We got a real live run
		# Have to check all judgements, in order
		jresult="AC"
		for jfile in `ls ${EXECUTE_DATA_PREFIX}.[0-9]*.txt 2>/dev/null | sed -e 's;^.*/;;' | sort -t. +1n`
		do
			GetJudgmentFromFile ./${jfile}
			if test -n "$result" -a "$result" != "AC"
			then
				jresult="$result"
				break
			fi
		done
		result="$jresult"
	fi
}

GetLastJudgment()
{
	dir=$1
	exdata=""
	if ! cd ${dir}
	then
		result="Not found"
	elif test -s "${RESULT_FAILURE_FILE}"
	then
		jerr=`cat ${RESULT_FAILURE_FILE}`
		result="JE ($jerr)"
	else
		# We got a real live run
		# Check out the biggest executedata file
		GetLastJudgmentFile $dir
		GetJudgmentFromFile ./${result}
	fi
}


MakeTestcaseFile()
{
	d="$1"
	t="$2"
	result=`printf '%s/%s/%s_%03d.log' "$d" "$REPORTS_DIR" "$TESTCASE_REPORT_FILE_PREFIX" "$t"`
}

MakeBriefcaseFile()
{
	d="$1"
	t="$2"
	result=`printf '%s/%s/%s_%03d.log' "$d" "$REPORTS_DIR" "$BRIEFCASE_FILE_PREFIX" "$t"`
}

# Must redirect from briefcase file
ReadBriefcase()
{
	read judgein
	read judgeans
	read cpunum
	read exepid
	read exetime
	read execpums cpulimms exewallms mempeakbytes memlimbytes
	mempeak=$mempeakbytes
	memlim=$memlimbytes
	# Calculate Mib from bytes, round upto next Mib
	if [[ $mempeak = [0-9]* ]]
	then
		mempeak=$(((mempeak+(1024*1024)-1)/(1024*1024)))
	fi
	if [[ $memlim = [0-9]* ]]
	then
		memlim=$(((memlim+(1024*1024)-1)/(1024*1024)))
	fi
}

DeleteOldestLinks()
{
	for linkpref in ${EXE_LINK_PREFIX} ${PROB_LINK_PREFIX}
	do
		dellist=`ls -1td ${linkpref}* | sed 1,${NUM_LINK_KEEP}d`
		if test -n "${dellist}"
		then
			rm -f ${dellist}
		fi
	done
}

GetSourceList()
{
        sllang="$1"
        sldir="$2"
        ext=""
        case "$sllang" in
        c)      ext="c" ;;
        cpp)    ext="cc cpp cxx c++" ;;
        java)   ext="java" ;;
        python3)        ext="py" ;;
        kotlin) ext="kt" ;;
        esac
        result=""
        if test -n "$ext"
        then
                for e in $ext
                do
                        result="$result "`ls -d $sldir/*.$e 2>/dev/null`
                done
        fi

}

InitJudgments

