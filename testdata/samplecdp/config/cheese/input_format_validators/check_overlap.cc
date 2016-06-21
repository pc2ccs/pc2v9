#include <cassert>
#include <cstdio>
typedef long long ll;

struct hole {
	int r, x, y, z;
};

ll sqr(ll x) { return x*x; }

int main(void) {
	int n, s;
	assert(scanf("%d%d", &n, &s) == 2);
	hole h[n];
	for (int i = 0; i < n; ++i) {
		assert(scanf("%d%d%d%d", &h[i].r, &h[i].x, &h[i].y, &h[i].z) == 4);
		for (int j = 0; j < i; ++j)
			assert(sqr(h[i].x-h[j].x)+sqr(h[i].y-h[j].y)+sqr(h[i].z-h[j].z) >= sqr(h[i].r+h[j].r));
	}
	return 42;
}
