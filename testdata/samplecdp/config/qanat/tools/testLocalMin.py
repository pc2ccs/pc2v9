params = raw_input().split()
W,H,N = float(params[0]),float(params[1]),float(params[2])

EPS = 0.000001

x = [0.0] + [float(k) for k in raw_input().split()] + [W]

def cost(x):
  total = 0.0
  for k in range(1,len(x)):
    # consider all dirt from shaft k-1 to shaft k, and shaft k cost
    h1 = x[k-1]*H/W      # height at x[k-1]
    h2 = x[k]*H/W        # height at x[k]

    a = (h2-h1+x[k]+x[k-1])/2.0
    L = h1+a-x[k-1]
    total += L*L
    total -= h1*h1/2.0   # don't charge here for shaft k-1
  return total

opt = cost(x)

print 'opt: ',opt

for k in range(1,len(x)-1):
  for s in (-1,1):
    for m in range(1,5):
      g = list(x)
      g[k] += s * m * EPS
      other = cost(g)
      if other < opt:
        print other
        print g
        opt = other
