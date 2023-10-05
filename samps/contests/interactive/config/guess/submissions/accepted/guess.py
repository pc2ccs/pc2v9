#!/usr/bin/python

import sys

n = int(sys.stdin.readline())

for _ in range(n):
    low, high = 1, 1000
    correct = False
    while not correct:
        guess = (low + high) / 2
        sys.stdout.write('{}\n'.format(int(guess)))
        sys.stdout.flush()

        response = sys.stdin.readline().strip()
        if response == 'correct':
            correct = True
        elif response == 'higher':
            low = guess + 1
        elif response == 'lower':
            high = guess - 1
        else:
            assert False
