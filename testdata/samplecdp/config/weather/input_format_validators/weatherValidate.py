import sys

# limits defined in problem statement
MAX_N = 20
MAX_PRECISION = 6
MIN_P = 0.000001

success = 42

def fail(msg):
  global success
  success = -1
  print(msg)

lines = sys.stdin.readlines()
if lines[-1] == '\n':
  lines.pop()
if len(lines) != 2:
  fail('input should have 2 lines')

firstline = lines[0].strip('\n')
if not firstline.isdigit(): fail('first line not properly formatted')
n = int(firstline)
if n < 1 or n > MAX_N:  fail('illegal N value')
  
secondline = lines[1].strip('\n')
if ' '.join(secondline.split()) != secondline:
  fail('second line has invalid spacing')
total = 0.0  
for token in secondline.split():
  if not token.startswith('0.'):
    fail('probability not of form 0.xxxxxx')
  if len(token) > 2 + MAX_PRECISION:
    fail('token too long')
  p = float(token)
  total += p
  if p < MIN_P:
    fail('probability too small')
  if p >= 1.0:
    fail('probability too big')
if abs(total - 1.0) > 0.000000001:
  fail('probabilities do not add to 1.0')

if (success == 42):
  print('success')

sys.exit(success)
