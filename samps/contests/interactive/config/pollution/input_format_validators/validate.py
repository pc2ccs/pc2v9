#!/usr/bin/python
from sys import stdin
import sys
import re

NMAX = 100
RMAX = 1000
CMAX = 1500
MAXCASES = 1

integer = "(0|-?[1-9]\d*)"

cases = 0

def diff(P1, P2):
    return (P2[0]-P1[0], P2[1]-P1[1])

def cross(P, Q):
    return Q[1]*P[0] - Q[0]*P[1]

def dot(P, Q):
    return P[0]*Q[0] + P[1]*Q[1]

def isect(P1, P2, Q1, Q2):
    dP = diff(P1, P2)
    dQ = diff(Q1, Q2)
    A = cross(dQ, dP)
    B = cross(dQ, diff(P1, Q1))
    C = cross(dP, diff(P1, Q1))
    if A < 0:
        A = -A
        B = -B
        C = -C
    if A == B == C == 0:
        PQ1 = dot(dP, diff(P1, Q1))
        PQ2 = dot(dP, diff(P1, Q2))
        return PQ1*PQ2 <= 0 or 0 <= PQ1 <= dot(dP, dP) or 0 <= PQ2 <= dot(dP, dP)
    return 0 <= B <= A and 0 <= C <= A

assert isect((4, 0), (10, 0), (8, -4), (8,4))
assert not isect((4, 0), (10, 0), (8, 2), (8,4))
assert not isect((4, 0), (7, 0), (8, -4), (8,4))
assert isect((5, 0), (10, 0), (15, 0), (7, 0))
assert not isect((5, 0), (10, 0), (15, 0), (27, 0))

while True:
    line = stdin.readline()
    if not line: break
    assert re.match(integer + " " + integer + "\n", line)
    N, R = [int(x) for x in line.split()]
    assert 3 <= N <= NMAX
    assert 1 <= R <= RMAX
    P = []
    for i in range(N):
        line = stdin.readline()
        assert re.match(integer + " " + integer + "\n", line)
        (x,y) = [int(x) for x in line.split()]
        assert -CMAX <= x <= CMAX
        assert 0 <= y <= CMAX
        assert x*x + y*y != R*R # not on boundary
        P.append((x,y))

    # check no touching
    for i in range(N):
        for j in range(2,N-1):
            assert not isect(P[i], P[(i+1)%N], P[(i+j)%N], P[(i+j+1)%N])

    cases += 1

assert cases <= MAXCASES

assert len(stdin.readline()) == 0
sys.exit(42)
