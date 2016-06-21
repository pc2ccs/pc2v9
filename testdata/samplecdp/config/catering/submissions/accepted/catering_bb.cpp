#include <iostream>
#include <vector>

using namespace std;

const int INF = 1e9; // This selection of infinity is enough for edge costs at most 1e6 and n <= 1000.
const int LN = -2000001; // A large enough negative number to force min cost flow solution saturate all y_iy'_i edges.

// Returns true if there is path from the first vertex to the last vertex in the residual graph.
// p will contain the parent of each vertex in the shortest path tree of the first vertex.
bool dijkstra(vector<vector<int> > &cost, vector<vector<int> > &capacity, vector<vector<int> > &f, vector<int> &p, vector<int> &pi)
{
    int n = cost.size();
    vector<int> d(n + 1, INF); // d[0] to d[n - 1] will keep the shortest path cost and d[n] will be used as INF. 
    vector<bool> mark(n, false); // Marked vertices indicate the vertices added to the shortest path tree up to now.
    
    for (int i = 0; i < n; i++)
        p[i] = -1;
    
    // Intializing the d value of the source
    d[0] = 0;
    while (true)
    {
        int u = n;
        for (int i = 0; i < n; i++)
            if(!mark[i] && d[i] < d[u])
                u = i;
        if (u == n) // no unmarked vertex is reachable from s 
            break;
                    
        mark[u] = true;
        for (int v = 0; v < n; v++) // updating the distance of other vertices, cost[u][v] + pi[u] - pi[v] is the weight after reweighting. 
        if (!mark[v])
            {
                if (f[v][u] && d[v] > d[u] - cost[v][u] + pi[u] - pi[v]) // we have cost[u][v] = - cost[v][u]
                {
                    d[v] = d[u] - cost[v][u] + pi[u] - pi[v];
                    p[v] = u;
                }
                if (f[u][v] < capacity[u][v] && d[v] > d[u] + cost[u][v] + pi[u] - pi[v])
                {
                    d[v] = d[u] + cost[u][v] + pi[u] - pi[v];
                    p[v] = u;
                }
            }
    }

    // We update the pi values for the next run of dijkstra function.
    for (int i = 0; i < n; i++)
        if (pi[i] < INF)
            pi[i] += d[i];
            
    return mark[n - 1];
}

// Returns the minimum cost of a maximum flow from the first vertex to the last vertex
int minCostFlow(vector<vector<int> > &cost, vector<vector<int> > &capacity)
{
    int n = cost.size();
    vector<vector<int> > f(n, vector<int>(n, 0));
    vector<int> p(n);
    
    // We use pi values for the reweighting process in which we keep all edge weights non-negative, so we can use dijkstra
    vector<int> pi(n, INF);
    // Initializing pi values with Bellman-Ford algorithm
    bool relaxed = true;
    pi[0] = 0;
    for (int i = 1; i < n && relaxed; i++)
    {
        relaxed = false;
        for (int j = 0; j < n; j++)
            for (int k = 0; k < n; k++)
                if (pi[k] > pi[j] + cost[j][k])
                {
                    pi[k] = pi[j] + cost[j][k];
                    relaxed = true;
                }
    }            

    int minCost = 0;
    while (dijkstra(cost, capacity, f, p, pi))
    {
        int minAugmentation = INF;
        for (int x = n - 1; x != 0; x = p[x])
            if (f[x][p[x]])
                minAugmentation = min(f[x][p[x]], minAugmentation);
            else
                minAugmentation = min(capacity[p[x]][x] - f[p[x]][x], minAugmentation);
        for (int x = n - 1; x != 0; x = p[x])
            if (f[x][p[x]])
            {
                f[x][p[x]] -= minAugmentation;
                minCost -= minAugmentation * cost[x][p[x]];
            }
            else
            {
                f[p[x]][x] += minAugmentation;
                minCost += minAugmentation * cost[p[x]][x];
            }
    }
    
    return minCost;
}

int main()
{
    int n, k;
    while (cin >> n >> k)
    {
        vector<vector<int> > transportationCost(n + 1, vector<int>(n + 1, INF));
        for (int i = 0; i < n; i++)
            for (int j = i + 1; j <= n; j++)
                cin >> transportationCost[i][j];

        // Building a (2 + k + 2n)-vertices flow network with vertices {s, x_1, ..., x_k, y_1, ..., y_n, y'_1, ..., y'_n, t} 
        vector<vector<int> > cost(2 + k + 2 * n, vector<int>(2 + k + 2 * n, INF));
        vector<vector<int> > capacity(2 + k + 2 * n, vector<int>(2 + k + 2 * n, 0));
        
        // Edges from s to x_i vertices
        for (int i = 1; i <= k; i++)
        {
            cost[0][i] = 0;
            capacity[0][i] = 1;
        }
        
        // Edges from x_i vertices to y_j vertices
        for (int i = 1; i <= k; i++)
            for (int j = 1; j <= n; j++)
            {
                cost[i][k + j] = transportationCost[0][j];
                capacity[i][k + j] = 1;
            }
            
        // Edges from x_i vertices to t
        for (int i = 1; i <= k; i++)
        {
            cost[i][k + 2 * n + 1] = 0;
            capacity[i][k + 2 * n + 1] = 1;
        }
        
        // Edges from y_i vertices to y'_i vertices 
        for (int i = 1; i <= n; i++)
        {
            cost[k + i][k + n + i] = LN;
            capacity[k + i][k + n + i] = 1;
        }
        
        // Edges from y'_i vertices to y_j vertices for i < j
        for (int i = 1; i <= n; i++)
            for (int j = i + 1; j <= n; j++)
            {
                cost[k + n + i][k + j] = transportationCost[i][j];
                capacity[k + n + i][k + j] = 1;
            }
        
        // Edges from y'_i vertices to t
        for (int i = 1; i <= n; i++)
        {
            cost[k + n + i][k + 2 * n + 1] = 0;
            capacity[k + n + i][k + 2 * n + 1] = 1;
        }
        
        cout << minCostFlow(cost, capacity) - n * LN << endl;
    }
    
    return 0;
}
