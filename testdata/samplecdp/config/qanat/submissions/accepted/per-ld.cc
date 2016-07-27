#include <cstdio>
#include <vector>

using namespace std;
typedef vector<long double> vd;

long double H, opt[2000], pos[2000];
long double sqr(long double x) { return x*x; }

int main(void) {
	int w, h, n;
	scanf("%d%d%d", &w, &h, &n);
	H = (long double)h/w;
	opt[0] = sqr(1+H)/4  - sqr(H)/2;
	for (int i = 1; i <= n; ++i) {
		long double x = pos[i] = (1+H)*(1-H)/2/(2*opt[i-1] + sqr(1-H)/2);
		opt[i] = opt[i-1]*sqr(x) + sqr(1 - x + H*(1+x))/4 - sqr(H)/2;
	}
	printf("%.9Lf\n", (opt[n]+sqr(H)/2)*w*w);
	vd res;
	res.push_back(pos[n]);
	for (int i = n-1; i > 0; --i)
		res.push_back(res.back()*pos[i]);
	for (int i = 0; i < min(n, 10); ++i)
		printf("%.9Lf\n", res[n-i-1]*w);
}
