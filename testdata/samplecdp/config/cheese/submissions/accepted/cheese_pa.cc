#include <cassert>
#include <cmath>
#include <cstdio>
#include <vector>

using namespace std;

const double XM = 100000;
const double YM = 100000;
const double ZM = 100000;
const double pi = 2.0*acos(0.0);

struct hole {
	int x, y, z, r;
};

vector<hole> holes;

double cube(double x) { return x*x*x; }
double prim(double x) { return x*(1-x*x/3); }
double vol(double z) {
	double res = XM*YM*z;
	for (auto h: holes)
		if (z > h.z-h.r)
			res -= pi*cube(h.r)*(prim(min(1.0*(z-h.z)/h.r, 1.0))-prim(-1));
	return res;
}

bool solve() {
	int n, s;
	if (scanf("%d%d", &n, &s) != 2) return false;
	holes.resize(n);
	double vv = 0;
	for (auto &h: holes) {
		scanf("%d%d%d%d", &h.r, &h.x, &h.y, &h.z);
		vv += cube(h.r);
		assert(h.x >= h.r && h.x <= XM-h.r);
		assert(h.y >= h.r && h.y <= YM-h.r);
		assert(h.z >= h.r && h.z <= ZM-h.r);
	}
	double V = vol(ZM);
	
	fprintf(stderr, "tot vol %lf, spherevol %lf\n", V, pi*vv*4.0/3.0);
	for (int i = 0; i < 10; ++i)
		fprintf(stderr, "  vol %lf: %lf\n", i*10000.0, vol(i*10000.0));
	double prev = 0;
	for (int i = 1; i <= s; ++i) {
		double lo = 0, hi = ZM;
		for (int j = 0; j < 50; ++j) {
			double m = (lo+hi)/2, v = vol(m);
			(v > (i*V/s) ? hi : lo) = m;
		}
		printf("%.9lf\n", lo/1000.0-prev);
		prev = lo/1000.0;
	}
}

int main(void) {
	while (solve());
	return 0;
}
