
#
# File:    sumit.pl
# Purpose: to sum the integers from input from stdin
# Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
#
# Tue Nov  1 17:46:37 PDT 2016
# * TODO cause tle by sleeping/running for a long time
#

	$sum = 0;

	while (<>)
	{
		chomp;
		$num = 0 + $_;
		$sum += $num if $num > 0;

	}

	$sum += 1000;

	print "The sum of the integers is $sum\n";

# eof 
