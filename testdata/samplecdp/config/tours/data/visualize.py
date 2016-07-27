from igraph import Graph, plot
import sys
import os

base = os.path.splitext(sys.argv[1])[0]
input = open(base+'.in', 'r')
(n, m) = map(int, input.readline().split())
G = Graph(n)
E = []
for line in input:
    E.append(map(lambda x: int(x)-1, line.split()))
G.add_edges(E)

sz = max(5, min(25, 2000/n))
if sz == 25:
    labels = map(str, range(1, n+1))
else:
    labels = None
color = ['lightblue']*n

if n < 400:
    # Good results for small n
    layout = G.layout_fruchterman_reingold()
else:
    # Reasonable results for all n
    layout = G.layout_mds()#('rt')

plot(G, base+'.png', layout=layout, vertex_color=color, vertex_label=labels, vertex_size=sz)

