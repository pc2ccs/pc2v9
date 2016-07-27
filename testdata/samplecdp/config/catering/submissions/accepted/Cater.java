import java.util.*;

public class Cater
{
	public final static int MAXN = 100;
	public final static int MAXK = 100;
	public final static int MAXV = 10000000;
	public final static long INF = MAXN*MAXV+1;

	public static void bellmanFord(Graph g, int src)
	{
		int n = g.numV;

		// Step 1: Initialize distances from src to all other vertices as INFINITE
		for(int i=0; i<n; i++) {
			g.v[i].dist = INF;
			g.v[i].prev = -1;
		}
		g.v[src].dist = 0;

		// Step 2: Relax all edges |V| - 1 int dest. A simple shortest path from src
		// to any other vertex can have at-most |V| - 1 edges
		for (int i = 1; i <= n-1; i++)
		{
			for (int j = 0; j < n; j++)
			{
				Edge p = g.v[j].alist.next;
				while (p != null) {
					int w = p.dest;
					long weight = p.weight;
					if (g.v[j].dist + weight < g.v[w].dist) {
						g.v[w].dist = g.v[j].dist + weight;
						g.v[w].prev = j;
					}
					p = p.next;
				}
			}
		}

		// Step 3: check for negative-weight cycles.  The above step guarantees
		// shortest distances if graph doesn't contain negative weight cycle.
		// If we get a shorter path, then there is a cycle.
		//	  can't happen in this problem

		return;
	}

	public static void main(String [] args)
	{
		int n, nSets;
		Scanner in = new Scanner(System.in);

		n = in.nextInt();
		nSets = in.nextInt();
		if (nSets > n)
			nSets = n;
		Graph g = new Graph(2*n+2);
		for(int j=1; j<=n; j++) {
			long cost;
			cost = in.nextLong();
			g.addEdge(0, 2*j-1, cost-(MAXV+1));
		}
		for(int i=1; i<=n; i++) {
			g.addEdge(2*i-1, 2*i, 0);
			for(int j=i+1; j<=n; j++) {
				long cost;
				cost = in.nextLong();
				g.addEdge(2*i, 2*j-1, cost-(MAXV+1));
			}
		}
		for(int i=0; i<=n; i++)
			g.addEdge(2*i, 2*n+1, 0);

		long ans = 0;
		for(int i=0; i<nSets; i++) {		 // find next augmenting path
			bellmanFord(g, 0);
			int j=2*n+1;
			long dist = g.v[j].dist;
			int k = 2*n+1;
			int count = 0;
			int tmp = g.v[k].prev;
									// count net # of neg weight edges on path
			while(tmp != -1) {
				Edge p = g.v[tmp].alist.next;
				while (p.dest != k) {
					p = p.next;
				}
				if (p.weight < 0)
					count++;
				else if (p.weight > 0)
					count--;
				k = tmp;
				tmp = g.v[k].prev;
   		 	}
									// use this count to get true cost of path
			dist += count*(MAXV+1);
			if (dist == 0)
				break;
			ans += dist;
			while(j != 0) {
				int prevj = g.v[j].prev;
				if (j != 2*n+1 || prevj != 0) {
					g.removeEdge(prevj, j);
				}
				j = prevj;
			}
		}
		System.out.println(ans);
	}
}

class Edge
{
	public int dest;
	public long weight;
	public Edge next;

	public Edge()
	{
		dest = -1;
		weight = 0;
		next = null;
	}

	public Edge(int d, long w, Edge n)
	{
		dest = d;
		weight = w;
		next = n;
	}
}

class Vertex
{
	public long dist;
	public int prev;
	Edge alist;

	public Vertex()
	{
		dist = Cater.INF;
		prev = -1;
		alist = new Edge();
	}
}

class Graph
{
	public Vertex [] v;
	public int numV;

	public Graph(int n)
	{
		numV = n;
		v = new Vertex[n];
		for(int i=0; i<n; i++)
			v[i] = new Vertex();
	}

	public void addEdge(int src, int dest, long w)
	{
		v[src].alist.next = new Edge(dest, w, v[src].alist.next);
	}

	void removeEdge(int src, int dest)
	{
		Edge p = v[src].alist;
		while (p.next != null && p.next.dest != dest)
			p = p.next;
		Edge tmp = p.next;
		p.next = tmp.next;
		addEdge(dest, src, -tmp.weight);
	}
}
