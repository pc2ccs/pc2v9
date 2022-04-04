import sys

# limits defined in problem statement
MAX_W = 10000
MAX_N = 1000

success = 42

def fail(msg):
  global success
  success = -1
  print(msg)

lines = sys.stdin.readlines()
if lines[-1] == '\n':
  lines.pop()
if len(lines) != 1:
  fail('input should have 1 line')

line = lines[0].strip('\n')
pieces = line.split()
if ' '.join(pieces) != line:
  fail('line has invalid spacing')

if len(pieces) != 3:
  fail('there should be 3 tokens')

integers = []
for token in pieces:
  if token.startswith('0'):
    fail('redundant leading 0')
  try:
    integers.append(int(token))
  except ValueError:
    fail('invalid integer')

W,H,N = integers

if not 1 <= W <= MAX_W:
  fail('invalid W')

if not 1 <= H < W:
  fail('invalid H')

if not 1 <= N <= MAX_N:
  fail('invalid N')

if (success == 42):
  print('success')

sys.exit(success)
