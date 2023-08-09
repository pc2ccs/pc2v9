#!/usr/bin/python

# arbitrary exploration, and if we get stuck, just wait

import sys

explored = set()

moves = {
        'up': (-1, 0),
        'down': (1, 0),
        'left': (0, -1),
        'right': (0, 1)
        }

def move_by(r, c, direction):
    dr, dc = moves[direction]
    return (r + dr, c + dc)


r = c = 0
while True:
    for direction in moves:
        if (direction, r, c) in explored:
            # we can get stuck looping here
            continue
        else:
            sys.stderr.write('TRYING {}\n'.format(direction))

            sys.stdout.write('{}\n'.format(direction))
            sys.stdout.flush()

            explored.add((direction, r, c))
            nr, nc = move_by(r, c, direction)

            result = sys.stdin.readline().strip()
            sys.stderr.write('GOT RESULT {}\n'.format(result))

            if result in ('', 'wrong', 'solved'):
                # nothing to do but sit here
                sys.stderr.write('I AM STUCK... JUST GOING TO SIT HERE...\n')
                while True:
                    pass

            if result == 'ok':
                r, c = nr, nc
            else:
                assert result == 'wall'


