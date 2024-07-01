#!/bin/bash
# Where to the the result of the first failure.  If this file is not created, then the
# run was accepted. (correct)
RESULT_FAILURE_FILE=failure.txt

# Where judgments are
REJECT_INI=$HOME/pc2/reject.ini

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
	if test -s ${REJECT_INIT}
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
	fi
}

# Takes the judgement string, eg. "Wrong answer" as arg 1 and the validation Result (42 or 42) as arg 2
MapJudgment()
{
	jm="$1"
	vr="$2"
	jv=${Judgments[$jm]}
	if test -z ${jv}
	then
		if test ${validationReturnCode} -eq 0
		then
			if test ${vr} = "43"
			then
				jv="WA"
			elif test ${vr} = "42"
			then
				jv="AC"
			else
				jv="WA (Default)"
			fi
		else
			jv="JE (Validator EC=${validationReturnCode})"
		fi

	fi
	echo $jv
}

GetJudgment()
{
	dir=$1
	if ! cd ${dir}
	then
		echo "Not found"
	else
		# We got a real live run
		# Check out the biggest executedata file
		exdata=`ls ${EXECUTE_DATA_PREFIX}.[0-9]*.txt 2>/dev/null | sort -t. +1rn | head -1`
		if test -z "${exdata}"
		then
			echo "No results"
		else
			# Source the file
			. ./${exdata}
			if test ${compileSuccess} = "false"
			then
				echo "CE"
			elif test ${executeSuccess} = "true"
			then
				if test ${validationSuccess} = "true"
				then
					MapJudgment "${validationResults}" "${validationReturnCode}"
				else
					echo "JE (Validator error)"
				fi
			else
				echo "RTE (Execute error)"
			fi
		fi
	fi
}

InitJudgments

for file in $*
do
	j=`GetJudgment $file`
	echo $file: $j
done
