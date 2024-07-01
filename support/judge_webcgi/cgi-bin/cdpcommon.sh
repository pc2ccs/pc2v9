# This file is meant to be 'source'd' to give functionality to read the CDP

CountSamples()
{
	datadir="$1"
	if test -d ${datadir}
	then
		result=`ls $datadir/*.in | wc -l`
	else
		result=0
	fi
}

GetNumberOfTestCases()
{
	probdir="$1"
	CountSamples $probdir/data/sample
	tot=$result
	CountSamples $probdir/data/secret
	result=$((tot+result))
}
