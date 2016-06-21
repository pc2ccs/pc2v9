#include<cassert>
#include<cstdio>
#include<cstring>
#include<vector>

using namespace std;
typedef vector<int> vi;

vi T;
vector<vector<vi>> maxr;

int MaxR(int l, int used, int rem) {
	if (used >= T[l]) return l;
	if (used+rem <= T[l]) return min((int)T.size(), l+(1<<rem)-1);
	if (maxr[l].size() <= used) maxr[l].resize(used+1);
	if (maxr[l][used].size() <= rem) maxr[l][used].resize(rem+1, -1);
	if (maxr[l][used][rem] == -1) {
		int mid = MaxR(l, used+1, rem-1);
		maxr[l][used][rem] = MaxR(mid+1, used+1, rem-1);
	}
	return maxr[l][used][rem];
}

void solve() {
	long long l, v1, v2, t, s;
	scanf("%lld%lld%lld%lld%lld", &l, &v1, &v2, &t, &s);
	int pts = (v2-v1-1)/t;
	T.resize(pts+1);
	for (int i = 1; i <= pts; ++i)
		T[i] = l/(s*(v2-t*i));
	maxr.clear();
	maxr.resize(pts+1);
	int q = 0;
	while ((q == 0 || MaxR(1, 0, q) > MaxR(1, 0, q-1)) && MaxR(1, 0, q) <= pts) ++q;
	if (MaxR(1, 0, q) <= pts)
		printf("impossible\n");
	else
		printf("%d\n", q);
}


int main(void) {
	int cases;
	scanf("%d", &cases);
	for (int i = 0; i < cases; ++i) solve();
	return 0;
}

