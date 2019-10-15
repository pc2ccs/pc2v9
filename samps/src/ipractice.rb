#!/bin/env ruby
#
# File:    ipractice.rb
# Purpose: sum of positive integers practice problem
# Author:  pc2@ecs.csus.edu
#


line = gets
inval = line.to_i

until 0 == inval
  line = gets

  sum = 0

  if inval < 1
     1.downto(inval) do |i|
       sum = sum + i
     end
  else
     1.upto(inval).each do |i|
       # puts("#{inval} and #{i} sum #{sum}")
       sum = sum + i
     end
  end

  printf "N = %-3d    Sum = %d\n", inval, sum
  inval = line.to_i

end 

# eof ipractice.rb
