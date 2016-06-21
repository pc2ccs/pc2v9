"""
Calculate and display the raw probability of each N-day weather pattern.
"""
try:
    raw_input           # Python2
except:
    raw_input = input   # Python3

LABELS = 'SRCF'

#SCALE = None        # to get float probs
SCALE = 1000000000   # to get int probs

def displayProb(p, scale=SCALE):
    """Display probability (allows ability to display as scaled integer)"""
    if scale is None:
        return '%f'%p
    else:
        return str(int(p * scale))

def fill(days):
    if len(days) == N:
        p = 1.0
        for k in range(len(days)):
            p *= prob[days[k]]
        print(''.join(LABELS[k] for k in days) + ' ' + displayProb(p))
    else:
        for k in range(4):
            days.append(k)
            fill(days)
            days.pop()


N = int(raw_input())
prob = [float(k) for k in raw_input().split()]
if len(prob) < 4:
    prob.append(1 - prob[0] - prob[1] - prob[2])
fill([])

    
for i in range(3):
    for j in range(3):
        for k in range(3):
            p = prob[i]*prob[j]*prob[k]
