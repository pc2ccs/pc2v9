#
# File:    ipractice.py
# Purpose: to sum the integers from stdin
# Author:  pc2@ecs.csus.edu or http://www.ecs.csus.edu/pc2
#
# Fri Oct 11 22:59:23 PDT 2019
#

import sys

# softspace required to avoid extra space after print
# sys.stdout.softspace=0

hand = sys.stdin

for line in hand .readlines():
  num = int(line);
  if num == 0:
    break

  sum = 0

  if num < 0:
    for i in range(1, num-1, -1):
      sum += i

  else:
    for i in range(1, num+1, 1):
      sum += i

  # print("The sum of the integers is %s"% sum)
  print("N = %-3d    Sum = %d"% (num, sum))

# eof ipractice.py 
