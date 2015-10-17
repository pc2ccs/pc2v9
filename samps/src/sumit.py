#
# File:    sumit.py
# Purpose: to sum the integers in the file sumit.dat
# Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
#
# Sat Oct 30 13:20:59 PDT 1999
#
# $Id$
#

import sys

# softspace required to avoid extra space after print
# sys.stdout.softspace=0

datafile = "sumit.dat"

hand = open (datafile, "r")

sum = 0

for line in hand .readlines():
  num = int(line);
  if num > 0:
    sum += num

print("The sum of the integers is %d"% sum)

# eof sumit.py $Id$

