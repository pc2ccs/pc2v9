#include <algorithm>
#include <cstdio>
#include <vector>
#include <cmath>

using namespace std;
typedef long double real;

const real pi = 2.0*acos(0.0);
const real eps = 1e-13;

struct point {
	real x, y;
	point(real x=0, real y=0): x(x), y(y) {}
	real cross(const point &p) const { return x*p.y - y*p.x; }
	real dot(const point &p) const { return x*p.x + y*p.y; }
	real dist2() const { return dot(*this); }
	point operator-(const point &p) const { return point(x-p.x, y-p.y); }
	point operator+(const point &p) const { return point(x+p.x, y+p.y); }
	point operator*(const real &c) const { return point(c*x, c*y); }
	point operator/(const real &c) const { return point(x/c, y/c); }
	real angle() const { return atan2(y, x); }
	real angle(const point &p) const {
		real r = angle() - p.angle();
		if (r < -pi) r += pi;
		if (r > pi) r -= pi;
		return r;
	}
};

typedef pair<real, real> pdd;
typedef vector<point> vp;

// t >= 0 s.t. p+v*t is on segment (q1,q1+dq), or 1e99 if none
real line_isect(point p, point dp, point q1, point dq) {
	if (dq.cross(dp) == 0) {
		if ((p-q1).cross(dq) != 0) return 1e99;
		real t = min((q1+dq-p).dot(dp), (q1-p).dot(dp)) / dp.dist2();
		if (t < 0) return 1e99;
		return t;
	} 
	real t = dq.cross(q1-p)/dq.cross(dp);
	real s = dp.cross(q1-p)/dq.cross(dp);
	if (s < 0 || s > 1 || t < 0) return 1e99;
	return t;
}

bool on_segment(point p, point q1, point q2) {
	return fabs((p-q1).cross(q2-q1)) < eps &&
		-eps < (p-q1).dot(q2-q1) && (p-q1).dot(q2-q1) < 1+eps;
}


real area(vp poly) {
	real A = 0;
	for (int i = 0, j = poly.size()-1; i < poly.size(); j = i++)
		A += poly[j].cross(poly[i]);
	return A/2;
}

bool inside(point p, vp P) {
	real a = 0;
	for (int i = 0, j = P.size()-1; i < P.size(); j = i++) {
		if (on_segment(p, P[i], P[j])) return 1;
		a += (P[j]-p).angle(P[i]-p);
	}
	return a > pi/2;
}

point v1, v2;
vp p1, p2;

pdd isect_ival() {
	pdd res(1e99, -1e99);
	for (int i1 = 0, j1 = p1.size()-1; i1 < p1.size(); j1=i1++)
		for (int i2 = 0, j2 = p2.size()-1; i2 < p2.size(); j2=i2++) {
			real t1 = line_isect(p1[i1], v1-v2, p2[i2], p2[j2]-p2[i2]);
			real t2 = line_isect(p2[i2], v2-v1, p1[i1], p1[j1]-p1[i1]);
			if (t1 != 1e99)
				res = pdd(min(res.first, t1), max(res.second, t1));
			if (t2 != 1e99)
				res = pdd(min(res.first, t2), max(res.second, t2));
		}
	return res;
}

real area(real t) {
	vp P1 = p1, P2 = p2, R;
	for (auto &p: P2) p = p + (v2-v1)*t;
	for (auto &p: P1) if (inside(p, P2)) R.push_back(p);
	for (auto &p: P2) if (inside(p, P1)) R.push_back(p);
	for (int i1 = 0, j1 = P1.size()-1; i1 < P1.size(); j1 = i1++)
		for (int i2 = 0, j2 = P2.size()-1; i2 < P2.size(); j2 = i2++) {
			real t = line_isect(P1[i1], P1[j1]-P1[i1], P2[i2], P2[j2]-P2[i2]);
			if (t < 1) R.push_back(P1[i1]*(1-t) + P1[j1]*t);
		}
	point a;
	for (auto &p: R) a = a + p / R.size();
	sort(R.begin(), R.end(), [a](point p, point q) { return (p-a).angle() < (q-a).angle(); });
	return area(R);
}

void read_poly(vp &p, point &v) {
	int n;
	scanf("%d", &n);
	p.resize(n);
	for (int i = 0; i < n; ++i)	scanf("%Lf%Lf", &p[i].x, &p[i].y);
	scanf("%Lf%Lf", &v.x, &v.y);
}

int main(void) {
	read_poly(p1, v1);
	read_poly(p2, v2);
	real t = 1e99;
	pdd isect = isect_ival();
	if (isect.first == 1e99) printf("never\n"); 
	else {
		real lo = isect.first, hi = isect.second;
		for (int i = 0; i < 100; ++i) {
			real m1 = (2*lo+hi)/3, m2 = (lo+2*hi)/3;
			(area(m1) + eps < area(m2)) ? (lo = m1) : (hi = m2);
		}
		printf("%.6Lf\n", lo);
	}
	return 0;
}

