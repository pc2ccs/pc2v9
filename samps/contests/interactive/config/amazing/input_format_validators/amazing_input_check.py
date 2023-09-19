
import sys
import re

def nextline():
    return sys.stdin.readline().replace("\n", "").replace("\r", "")

rows, cols, r, c = [int(x) for x in nextline().split()]

assert 1 <= r <= rows
assert 1 <= c <= cols

line = nextline()
assert len(line) == cols*2+1
assert re.match('^[._]*$', line)

for _ in range(rows):
    line = nextline()
    assert len(line) == cols*2+1
    assert re.match('^[.|_]([._][.|_])*$', line)

assert sys.stdin.readline() == ''

sys.exit(42)

# we do not have to validate the yes/no in *.ans files
# if there is "no" instead of "yes", all AC solutions would trigger a JE
# if there is "yes" instead of "no", all AC solutions will get WA
