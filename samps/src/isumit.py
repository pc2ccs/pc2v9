#
# File:    isumit.py
# Purpose: to sum the integers from stdin
# Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
#
# $Id: sumit.pl 326 2006-10-25 02:53:57Z laned $
#

import sys

# softspace required to avoid extra space after print
# sys.stdout.softspace=0

hand = sys.stdin

sum = 0

for line in hand .readlines():
  num = int(line);
  if num > 0:
    sum += num

print("The sum of the integers is %s"% sum)

# eof sumit.py $Id: hello.py 2323 2011-09-11 02:02:34Z laned $

