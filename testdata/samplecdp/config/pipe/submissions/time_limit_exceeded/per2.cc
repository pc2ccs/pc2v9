// Has the ideas for the fast solution but only uses them to speed up
// some parts of the slow solution.  Should make sure this times out
// if we go for requiring O(log n) solution.
#include<cassert>
#include<cstdio>
#include<cstring>
#include<vector>

using namespace std;
typedef vector<int> vi;

long long l, v1, v2, t, s;

vi T;
vector<vector<vi>> maxr;

int dl(int i) {
	return l/(s*(v2-t*i));
}

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

int perf(int n) {
	int r = 0;
	while ((1<<r)-1 < n) ++r;
	return r;
}

void solve() {
	scanf("%lld%lld%lld%lld%lld", &l, &v1, &v2, &t, &s);
	int pts = (v2-v1-1)/t;
	int at = 1, sub = 0;
	while (at <= pts) {
		int Ta = dl(at) - sub;
		if (Ta > perf(pts-at+1)) {
			printf("%d\n", sub+perf(pts-at+1));
			return;
		}
		if (Ta <= 0) {
			printf("impossible\n");
			return;
		}
		int jmp = (1<<Ta-1)-1;
		if (dl(at+jmp) == dl(at)) {
			at += jmp+1;
			++sub;
		} else
			break;
	}
	T.resize(pts-at+2);
	for (int i = at; i <= pts; ++i)
		T[i-at+1] = dl(i)-sub;
	pts = T.size()-1;
	maxr.clear();
	maxr.resize(pts+1);
	int q = 0;
	while ((q == 0 || MaxR(1, 0, q) > MaxR(1, 0, q-1)) && MaxR(1, 0, q) <= pts) ++q;
	if (MaxR(1, 0, q) <= pts)
		printf("impossible\n");
	else
		printf("%d\n", q+sub);
}


int main(void) {
	int cases;
	scanf("%d", &cases);
	for (int i = 0; i < cases; ++i) solve();
	return 0;
}

