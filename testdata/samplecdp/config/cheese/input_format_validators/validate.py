import sys

MAXC = 100000
MAXN = 10000
MAXS = 100

def getline():
    line = sys.stdin.readline()
    assert line[-1] == '\n'
    return line[:-1]

(n,s) = map(int,getline().split(' '))
assert 0 <= n <= MAXN
assert 1 <= s <= MAXS

for i in range(n):
    (r,x,y,z) = map(int, getline().split(' '))
    assert 0 <= x-r and x+r <= MAXC
    assert 0 <= y-r and y+r <= MAXC
    assert 0 <= z-r and z+r <= MAXC

assert sys.stdin.readline() == ''

sys.exit(42)
