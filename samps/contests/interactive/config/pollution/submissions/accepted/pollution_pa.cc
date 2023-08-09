#include <cstdio>
#include <cmath>
#include <vector>

using namespace std;

const double eps = 1e-9;
const double pi = 2.0*acos(0.0);

struct point {
	double x, y;
	point(double x=0, double y=0): x(x), y(y) {}
	point operator-(point p) { return point(x-p.x, y-p.y); }
	point operator+(point p) { return point(x+p.x, y+p.y); }
	point operator*(double c) { return point(c*x, c*y); }
	double cross(point p) { return (y*p.x-x*p.y); }
	double dot(point p) { return x*p.x+y*p.y; }
	double dist2() { return dot(*this); }
	double theta() { return atan2(y, x); }
};

typedef vector<point> vp;

vp isect(point p, point q, int r) {
	point d = q-p;
	double b = d.dot(p)/d.dist2(), c = (p.dist2() - r) / d.dist2(), disc = b*b-c;
	vp res;
	if (disc > eps) {
		disc = sqrt(disc);
		double t1 = -b - disc, t2 = -b + disc;
		if (t1 > 0 && t1 < 1) res.push_back(p + d*t1);
		if (t2 > 0 && t2 < 1) res.push_back(p + d*t2);
	}
	return res;
}

bool solve() {
	int n, r;
	if (scanf("%d%d", &n, &r) != 2) return false;
	point P[200];
	for (int i = 0; i < n; ++i) scanf("%lf%lf", &P[i].x, &P[i].y);
	r *= r;
	double A = 0;
	for (int i = 0, j = n-1; i < n; j = i++) {
		vp I = isect(P[j], P[i], r);
		//		A = 0;
		switch (I.size()) {
		case 0:
			if (P[j].dist2() < r) A += P[i].cross(P[j]);
			else A += (P[i].theta() - P[j].theta())*r;
			break;
		case 1:
			if (P[j].dist2() < r) A += I[0].cross(P[j]) + (P[i].theta() - I[0].theta())*r;
			else                  A += P[i].cross(I[0]) + (I[0].theta() - P[j].theta())*r;
			break;
		case 2:
			A += I[1].cross(I[0]) + (P[i].theta() - I[1].theta() + I[0].theta() - P[j].theta())*r;
		}
		//		printf("tri (0,0)-(%.0f,%.0f)-(%.0f,%.0f) isect %d, A %lf\n",  P[i].x, P[i].y, P[j].x, P[j].y, I.size(), A);
		//		if (I.size()) printf("    isect is %lf %lf thata %lf\n", I[0].x, I[0].y, I[0].theta());
	}
	printf("%.9lf\n", A/2);
	return true;
}

int main(void) {
	while (solve());
	return 0;
}
