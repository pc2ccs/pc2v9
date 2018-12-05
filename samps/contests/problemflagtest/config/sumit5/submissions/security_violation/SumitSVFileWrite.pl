#
# File:    sumit.pl
# Purpose: to sum the integers - and write to a file  
# Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
#
# Thu Nov  3 17:04:35 PDT 2016
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

	open (FP, ">sumitplout.txt");
	print FP "The sum of the integers is $sum\n";
	close (FP);

# eof 
