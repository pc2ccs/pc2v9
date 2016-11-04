
#
# File:    sumit.pl
# Purpose: to sum the integers from input from stdin
# Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
#
# Thu Nov  3 17:00:26 PDT 2016
#
# * TODO create a RTE
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

	exit 66;

# eof 
