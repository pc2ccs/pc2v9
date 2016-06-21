#include <cassert>
#include <cstdio>
#include <vector>
#include <set>
#include <cstring>

using namespace std;
typedef vector<int> vi;

int divs[600000];
int _ans[600000];

int ans(int a) {
	if (_ans[a] != -1) return _ans[a];
	int res = 0;
	for (int z = 1; z < a; ++z)
		res += divs[z]*divs[a-z];
	return _ans[a] = res;
}

int main(void) {
	memset(divs, 0, sizeof(divs));
	divs[1] = 1;
	for (int i = 2; i <= 500000; ++i) {
		if (divs[i]) {
			int x = i, e = 0;
			while (x % divs[i] == 0) ++e, x /= divs[i];
			divs[i] = divs[x]*(e+1);
		} else {
			divs[i] = 2;
			for (int j = 2*i; j < 500000; j += i)
				divs[j] = i;
		}
	}
	int n;
	scanf("%d", &n);
	memset(_ans, -1, sizeof(_ans));
	for (int i = 0; i < n; ++i) {
		int lo, hi, r = 0, a= 0;
		scanf("%d%d", &lo, &hi);
		for (int x = hi; x >= lo && x >= hi-100; --x) {
			int w = ans(x);
			if (w > r)
				r = w, a = x;
		}
		printf("%d %d\n", a, r);
	}
	return 0;
}
