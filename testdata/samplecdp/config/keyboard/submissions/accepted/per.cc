#include <cstdio>
#include <cstring>
#include <vector>
#include <queue>
#include <bitset>

using namespace std;
typedef pair<int, bitset<128> > state;

const int dx[] = {-1,0,1,0}, dy[] = {0,-1,0,1};
const int oo = 0x1f1f1f1f;

char keyb[60][60], goal[15000];
int mv[2500][4], dst[2500][2500];
vector<int> jmp[2500][128];
int W, H;

bool valid(int r, int c) { return 0 <= r && r < H && 0 <= c && c < W; }

int main(void) {
	scanf("%d%d", &H, &W);
	for (int i = 0; i < H; ++i)
		scanf("%s", keyb[i]);

	memset(mv, -1, sizeof(mv));
	for (int i = 0; i < H; ++i)
		for (int j = 0; j < W; ++j) {
			for (int d = 0; d < 4; ++d) {
				int ni = i, nj = j;
				while (valid(ni, nj) && keyb[ni][nj] == keyb[i][j])
					ni += dx[d], nj += dy[d];
				if (valid(ni, nj))
					mv[i*W+j][d] = ni*W+nj;
			}
		}
	memset(dst, 0x1f, sizeof(dst));
	for (int u = 0; u < W*H; ++u) {
		dst[u][u] = 0;
		queue<state> q;
		q.push(make_pair(u, bitset<128>()));
		while (!q.empty()) {
			int v = q.front().first;
			bitset<128> seen = q.front().second;
			q.pop();
			int c = keyb[v/W][v%W];
			if (!seen[c]) {
				jmp[u][c].push_back(v);
				seen[c] = 1;
			}
			for (int d = 0; d < 4; ++d)
				if (mv[v][d] != -1 && dst[u][mv[v][d]] == oo) {
					dst[u][mv[v][d]] = dst[u][v] + 1;
					q.push(make_pair(mv[v][d], seen));
				}
		}
	}

	scanf("%s", goal);
	int n = strlen(goal);
	goal[n] = '*';
	int cost[2][2500];
	memset(cost, 0x1f, sizeof(cost));
	cost[0][0] = 0;
	for (int i = 0; i <= n; ++i) {
		memset(cost[(i+1)%2], 0x1f, sizeof(cost[0]));
		for (int p = 0; p < W*H; ++p)
			if (cost[i%2][p] < oo)
				for (int np: jmp[p][(int)goal[i]])
					cost[(i+1)%2][np] = min(cost[(i+1)%2][np], cost[i%2][p] + dst[p][np] + 1);
		}
	int r = oo;
	for (int p = 0; p < W*H; ++p)
		r = min(r, cost[(n+1)%2][p]);
	printf("%d\n", r);
}
