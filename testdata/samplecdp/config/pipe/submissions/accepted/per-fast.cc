#include<cassert>
#include<cstdio>
#include<cstring>
#include<vector>

using namespace std;
typedef vector<int> vi;

long long l, v1, v2, t, s;
int N;

int dl(int i) { return l/(s*(v2-t*i)); }

int MaxR(int l, int d, int goal) {
	if (l > N || d >= dl(l)) return l;
	if (dl(l) >= goal) return (l + (1LL << goal-d) - 1);
	int jmp = (1LL << dl(l)-d-1)-1;
	int mid = dl(l+jmp) == dl(l) ? l+jmp : MaxR(l, d+1, goal);
	return MaxR(mid+1, d+1, goal);
}

void solve() {
	scanf("%lld%lld%lld%lld%lld", &l, &v1, &v2, &t, &s);
	N = (v2-v1-1)/t;
	int q = 0;
	while ((q == 0 || MaxR(1, 0, q) > MaxR(1, 0, q-1)) && MaxR(1, 0, q) <= N) ++q;
	if (MaxR(1, 0, q) <= N)
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

