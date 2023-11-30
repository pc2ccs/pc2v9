#!/usr/bin/env python2

import sys
import collections

# Solve the maze iteratively, rather than recursively, using a stack to keep
# track of where we have been and the next place to explore.
#
# The one slightly confusing thing is that in a "normal" maze solver, when we
# want to pop the stack, we just do it. Here, we have to inform the interactive
# judge that we are moving back; hence the special code for "backup()".

def DEBUG(msg):
    sys.stderr.write("DEBUG: " + msg + '\n')
    sys.stderr.flush()
    pass

moves = {
        'down':  (-1,  0),
        'up':    ( 1,  0),
        'left':  ( 0, -1),
        'right': ( 0,  1),
        }

directions = ['right', 'down', 'left', 'up']

opposite = {
        'right': 'left',
        'left': 'right',
        'down': 'up',
        'up': 'down',
        }

def make_move(r, c, direction):
    global moves
    nr, nc = moves[direction]
    return (r + nr, c + nc)

def no_way_out():
    sys.stdout.write('no way out\n')
    sys.stdout.flush()
    sys.exit(0)

def query_oracle(direction):
    sys.stdout.write('{}\n'.format(direction))
    sys.stdout.flush()

    response = sys.stdin.readline().strip()

    if not response:
        sys.exit(-1)

    if response == 'solved':
        sys.exit(0)

    if response == 'wrong':
        sys.exit(-1)

    return response == 'ok'

def print_path(stream, path):
    """Print the maze as we have explored it so far (only where we've been, not
    the walls)."""

    visited = {}

    for i, (r, c, _) in enumerate(path):
        visited[(r, c)] = "{:02d}".format(i % 100)

    rows = [r for r, _, _ in path]
    cols = [c for _, c, _ in path]
    min_r, max_r = min(rows), max(rows)
    min_c, max_c = min(cols), max(cols)

    for r in range(min_r, max_r + 1):
        s = []
        for c in range(min_c, max_c + 1):
            s.append(visited.get((r, c), '  '))
        stream.write(' '.join(s) + '\n')

def try_move(r, c, direction, path, seen, d_out):
    if direction in d_out[(r, c)]:
        return

    nr, nc = make_move(r, c, direction)
    if (nr, nc) in seen:
        return

    d_out[(r, c)].add(direction)

    valid_move = query_oracle(direction)

    if valid_move:
        path.append((nr, nc, 0))
        seen.add((nr, nc))

def backup(path, d_out):
    r, c, _ = path[-1] 
    path.pop()

    if path:
        # the forward_direction was the direction we went from the previous
        # location to get to (r, c)
        forward_direction = directions[path[-1][2] - 1]
        back_direction = opposite[forward_direction]
        d_out[(r, c)].add(back_direction)
        # since we came from there, we *must* be able to return
        assert query_oracle(back_direction)

if __name__ == '__main__':
    path = [(0, 0, 0)]
    seen = {(0, 0)}
    directions_out = collections.defaultdict(set) # which directions have been used going *out* of each location

    while path:
        r, c, d_ndx = path[-1]

        if len(directions) <= d_ndx:
            backup(path, directions_out)
            continue

        # advance to the next potential direction
        path.pop()
        path.append((r, c, d_ndx + 1))

        try_move(r, c, directions[d_ndx], path, seen, directions_out)

    no_way_out()

