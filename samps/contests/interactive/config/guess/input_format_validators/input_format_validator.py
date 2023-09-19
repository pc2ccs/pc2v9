#!/usr/bin/python
from sys import stdin
import sys
import re

integer = "(0|-?[1-9]\d*)"
pat = "(fixed|random|adaptive) " + integer + "\n"

line = stdin.readline()
assert re.match('^[1-9][0-9]*\n$', line)
num_cases = int(line)
assert 1 <= num_cases <= 100

for _ in range(num_cases): 
    line = stdin.readline()
    assert re.match(pat, line)

line = stdin.readline()
assert len(line) == 0

# Nothing to report
sys.exit(42)
