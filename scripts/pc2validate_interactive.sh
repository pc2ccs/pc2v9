#!/bin/bash
# Usage is: pc2validate_interactive.sh {:resfile} {:feedbackdir} {:testcase}
#
# This is called from PC2 as the output validator for an interative problem.
# Replaces the security field in the interactive validator's temporary result ($TMP_RESULTFILE) file with the :resfile
# Renames the temporary result xml file to :resfile
# Renames the temporary feedback directory to the real one (:feedbackdir)
# Adds the feedbackdir files and result xml file to the per-testcase log file (reports/testcase_###.log)
# Also see pc2sandbox_interactive.sh for the definitions of these variables.
#
# Where the interim results are saved
INT_FEEDBACKDIR=interactive_feedback
INT_RESULTFILE=interactive_results.xml
REPORTDIR=reports

resxml="$1"
feedbackdir="$2"
testcase="$3"

# Should exist, but can't hurt
mkdir -p "$REPORTDIR"

if test -z "$resxml" -o -z "$testcase"
then
	echo $0: Bad arguments supplied
	exit 2
fi

# Where to log per-testcase results
REPORTFILE=`printf "$REPORTDIR/testcase_%03d.log" $testcase`

if ! /usr/bin/sed -e "s/$INT_RESULTFILE/$resxml/" < $INT_RESULTFILE > $resxml
then
	echo $0: Can not copy $INT_RESULTFILE to $resxml
	exit 1
fi

# Copy results file, and any feedback files to pc2 desired locations
(
	echo ""
	echo "----------------------------------"
	echo "Results XML File for testcase #$testcase:"
	echo "----------------------------------"
	cat "$resxml"
	if test -d "$INT_FEEDBACKDIR"
	then
		for file in `ls "$INT_FEEDBACKDIR"`
		do
			f="$INT_FEEDBACKDIR/$file"
			if test -f "$f"
			then
				echo ""
				echo "-----------------------------------------"
				echo "Feedback file for testcase #$testcase $f:"
				echo "-----------------------------------------"
				cat "$f"
			fi
		done
	fi
) >> $REPORTFILE

# If the feedback directory exists, copy all the stuff from the interactive validator there
if test -d "$feedbackdir"
then
	mv "$INT_FEEDBACKDIR"/* "$feedbackdir"
fi

rm -rf "$INT_RESULTFILE" "$INT_FEEDBACKDIR"
exit 0
