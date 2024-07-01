# Meant to be "source'd" into bash scripts.

# The judge's home directory
JUDGE_HOME=/home/icpc

# Modify constants as necessary for your installation
PC2_RUN_DIR=${JUDGE_HOME}/pc2
PC2_CDP=${PC2_RUN_DIR}/current/config

# Where can we find the contest banner png file for webpage headers
BANNER_FILE=banner.png
BANNER_IMAGE=${PC2_RUN_DIR}/current/contest/${BANNER_FILE}

# Where to the the result of script failure before execution
RESULT_FAILURE_FILE=failure.txt

# Where judgments are
REJECT_INI=${JUDGE_HOME}/pc2/reject.ini

# Where PC2 puts CLICS validator results
EXECUTE_DATA_PREFIX=executedata

declare -A Judgments

InitJudgments()
{
	# Defauls, may be overriden by reject.ini
	Judgments["accepted"]="AC"
	Judgments["Accepted"]="AC"
	Judgments["timelimit"]="TLE"
	Judgments["run error"]="RTE"
	Judgments["compiler error"]="CE"
	if test -s ${REJECT_INI}
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
	if test -z ${result}
	then
		if test ${validationReturnCode} -eq 0
		then
			if test ${vr} = "43"
			then
				resul="WA"
			elif test ${vr} = "42"
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

GetLastTestCaseNumber()
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

GetJudgment()
{
	dir=$1
	exdata=""
	if ! cd ${dir}
	then
		result="Not found"
	elif test -s ${RESULT_FAILURE_FILE}
	then
		jerr=`cat ${RESULT_FAILURE_FILE}`
		result="JE ($jerr)"
	else
		# We got a real live run
		# Check out the biggest executedata file
#		exdata=`ls ${EXECUTE_DATA_PREFIX}.[0-9]*.txt 2>/dev/null | sort -t. +1rn | head -1`
		GetLastJudgmentFile $dir
		exdata=${result}
		if test -z "${exdata}"
		then
			result="No results"
		else
			# Source the file
			. ./${exdata}
			if test ${compileSuccess} = "false"
			then
				result="CE"
			elif test ${executeSuccess} = "true"
			then
				if test ${validationSuccess} = "true"
				then
					MapJudgment "${validationResults}" "${validationReturnCode}"
				else
					result="JE (Validator error)"
				fi
			else
				result="RTE (Execute error)"
			fi
		fi
	fi
}

InitJudgments

