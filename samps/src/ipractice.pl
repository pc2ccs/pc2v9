
# Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau. 

#
# File:    ipractice.pl
# Purpose: sum of positive integers practice problem
# Author:  pc2@ecs.csus.edu
#
# Returns 4 if there is an error, 0 on successful solution.
#
# Sat Oct 12 12:35:55 PDT 2019 
#

my $inval = int (<STDIN>);

while ( $inval != 0 )
{
  my $sum = 0;

  if ($inval < 1)
  {
    for ($i = 1; $i >= $inval; $i --)
    {
       $sum += $i;	
    }
  }
  else
  {
    for ($i = 1; $i <= $inval; $i ++)
    {
       $sum += $i;	
    }
    
  }

  printf("N = %-3d    Sum = %d\n", $inval, $sum);

  $inval = int (<STDIN>);
}

exit 0;

# eof ipractice.pl
