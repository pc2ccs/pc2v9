import sys

# is there a binary tree of maximal depth maxd whose leaves have depths <= ds, in this order, where ds is an increasing sequence?
def check(maxd, ds):
  ix = [] # 0-1 sequence: path to current leaf, 0 = left, 1 = right
  for e in ds:
    while len(ix) < min(maxd, e):
      ix.append(0)
    while len(ix) > min(maxd, e):
      ix.pop()
    while len(ix) > 0 and ix[-1] > 0: # go to next node at that level: increment binary counter
      ix.pop()
    if len(ix) == 0: # tree is full
      return False
    ix[-1] = 1
  return True

t = int(sys.stdin.readline())
for _ in range(t):
  l, v1, v2, tol, s = map(int, sys.stdin.readline().split())
  ds = [] # maximal depths of the intervals, which are the leaves of a tree
  for v in range(v2-tol, v1, -tol): # backwards to make the boundaries as small as possible
    ds.append(l // (v * s))
  # v = velocity that decides between two intervals
  # v * s = location of tapping
  # l / (v * s) = maximal number of tappings until this tapping takes place
  maxtap = len(ds) # an upper bound on the depth of the tree
  # binary search for the minimal depth of the tree
  lo = 0
  hi = maxtap + 1 # add 1 to see if it cannot be done at all
  while lo < hi:
    med = (lo + hi) // 2
    if check(med, ds):
      hi = med
    else:
      lo = med + 1
  if lo > maxtap:
    print("impossible")
  else:
    print(lo)

