#!/bin/bash
# Usage is: copy_xml_result.sh {:resfile} {:feedbackdir} {:testcase}
#
# This is called from PC2 as the output validator for an interative problem.
# Replaces the security field in the interactive validator's temporary result ($TMP_RESULTFILE) file with the :resfile
# Renames the temporary result xml file to :resfile
# Renames the temporary feedback directory to the real one (:feedbackdir)
# Adds the feedbackdir files and result xml file to the per-testcase log file (reports/testcase_###.log)
# Also see pc2sandbox_interactive.sh for the definitions of these variables.
#
TMP_FEEDBACKDIR=tmp_feedback
TMP_RESULTFILE=tmp_results.xml
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

if ! /usr/bin/sed -e "s/$TMP_RESULTFILE/$resxml/" < $TMP_RESULTFILE > $resxml
then
	echo $0: Can not copy $TMP_RESULTFILE to $resxml
	exit 1
fi

# Copy results file, and any feedback files to pc2 desired locations
(
	echo ""
	echo "-----------------"
	echo "Results XML File:"
	echo "-----------------"
	cat "$resxml"
	if test -d "$TMP_FEEDBACKDIR"
	then
		for file in `ls "$TMP_FEEDBACKDIR"`
		do
			f="$TMP_FEEDBACKDIR/$file"
			if test -f "$f"
			then
				echo ""
				echo "-----------------------"
				echo "Feedback file $f:"
				echo "-----------------------"
				cat "$f"
			fi
		done
	fi
) >> $REPORTFILE

# If the feedback directory exists, copy all the stuff from the interactive validator there
if test -d "$feedbackdir"
then
	mv "$TMP_FEEDBACKDIR"/* "$feedbackdir"
fi

rm -rf "$TMP_RESULTFILE" "$TMP_FEEDBACKDIR"
exit 0
