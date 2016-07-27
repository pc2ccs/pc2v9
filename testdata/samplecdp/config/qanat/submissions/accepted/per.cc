#include <cstdio>
#include <vector>

using namespace std;
typedef vector<double> vd;

double H, opt[2000], pos[2000];
double sqr(double x) { return x*x; }

int main(void) {
	int w, h, n;
	scanf("%d%d%d", &w, &h, &n);
	H = 1.0*h/w;
	opt[0] = sqr(1+H)/4  - sqr(H)/2;
	for (int i = 1; i <= n; ++i) {
		double x = (1+H)*(1-H)/2/(2*opt[i-1] + sqr(1-H)/2);
		opt[i] = opt[i-1]*sqr(x) + sqr(1 - x + H*(1+x))/4 - sqr(H)/2;
		pos[i] = x;
	}
	printf("%.9lf\n", (opt[n]+sqr(H)/2)*w*w);
	vd res;
	res.push_back(pos[n]);
	for (int i = n-1; i > 0; --i)
		res.push_back(res.back()*pos[i]);
	for (int i = 0; i < min(n, 10); ++i)
		printf("%.9lf\n", res[n-i-1]*w);
}
