#include <cstdio>
#include <cmath>

int main(void) {
	double maxv = -1, maxgap = 0;
	int p, a, b, c, d, n;
	scanf("%d%d%d%d%d%d", &p, &a, &b, &c, &d, &n);
	for (int i = 1; i <= n; ++i) {
		double v = p*(sin(a*i+b) + cos(c*i+d) + 2);
		if (v > maxv) maxv = v;
		if (maxv-v > maxgap) maxgap = maxv-v;
	}
	printf("%.9lf\n", maxgap);
}
