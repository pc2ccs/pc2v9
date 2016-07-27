#include <cstdio>
#include <cstring>
#include <set>

using namespace std;
typedef pair<int,int> pii;
const int oo = 0x1f1f1f1f;

int cap[250][250], flow[250][250], cst[250][250];

int main(void) {
	int n, k, d[300][300], mate[300];
	scanf("%d%d", &n, &k);
	memset(cst, 0, sizeof(cst));
	memset(flow, 0, sizeof(flow));
	memset(cap, 0, sizeof(cap));
	for (int i = 0; i < n; ++i)
		for (int j = 0; j < n-i; ++j)
			scanf("%d", d[i]+i+j+1);
	int s = 2*n+1, t = 2*n+2;
	for (int u = 0; u <= n; ++u) {
		cap[s][u] = u ? 1 : k;
		for (int v = u+1; v <= n; ++v) {
			cap[u][n+v] = 1;
			cst[u][n+v] = d[u][v];
			cst[n+v][u] = -d[u][v];
		}
	}
	for (int v = n+1; v <= 2*n; ++v)
		cap[v][t] = 1;
	int res = 0;
	for (int x = 0; x < n; ++x) {
		int back[250], dst[250];
		memset(back, -1, sizeof(back));
		memset(dst, 0x1f, sizeof(dst));
		dst[s] = 0;
		bool changing = true;
		while (changing) {
			changing = false;
			for (int u = 0; u <= t; ++u)
				if (dst[u] < oo)
					for (int v = 0; v <= t; ++v)
						if (cap[u][v] - flow[u][v] > 0 && dst[u] + cst[u][v] < dst[v]) {
							dst[v] = dst[u] + cst[u][v];
							back[v] = u;
							changing = true;
						}
		}
		res += dst[t];
		int u = t;
		while (u != s) {
			flow[back[u]][u] += 1;
			flow[u][back[u]] -= 1;
			u = back[u];
		}
	}
	printf("%d\n", res);
	return 0;
}
