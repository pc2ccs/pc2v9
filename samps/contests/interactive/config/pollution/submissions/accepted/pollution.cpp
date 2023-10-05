#include <iostream>
#include <cmath>
#include <cstdio>
using namespace std;

const int MAX = 100;
const double PI = atan(1.0)*4.0;

struct point {
	double x, y;
	bool onCircle;
	bool inCircle;
} poly[MAX], isect[3*MAX];

void printPoint(point p)
{
	cout << '(' << p.x << ',' << p.y << ')';
}

bool between(double a, double b, double c)
{
	if (a > b && a < c)
		return true;
	return (a > c && a < b);
}

int intersect(point p1, point p2, double r, point ints[])
{
	double dx = p1.x-p2.x;
	double dy = p1.y-p2.y;
	if (dx == 0) {
		if (p1.inCircle && !p2.inCircle || !p1.inCircle && p2.inCircle) {
			ints[0].x = p1.x;
			ints[0].y = sqrt(r*r-p1.x*p1.x);
			ints[0].onCircle = true;
			return 1;
		}
		else
			return 0;
	}
	else if (p1.inCircle && p2.inCircle)
		return 0;
	else {
		double m = dy/dx;
		double b = p1.y - m*p1.x;
		double disc = 4*m*m*b*b - 4*(1+m*m)*(b*b-r*r);
		if (abs(disc) <= 0.01) {
			cout << "WARNING: ";
			printPoint(p1);
			printPoint(p2);
			cout << " close to tangent line" << endl;
			return 0;
		}
		if (disc < 0)
			return 0;
		disc = sqrt(disc);
		double x1 = (-2*m*b + disc)/(2*(1+m*m));
		double x2 = (-2*m*b - disc)/(2*(1+m*m));
		if ((x2-x1)*(p2.x-p1.x) < 0) {
			double tmp = x1;
			x1 = x2;
			x2 = tmp;
		}
		int ni = 0;
		if (between(x1, p1.x, p2.x)) {
			ints[ni].x = x1;
			ints[ni].y = sqrt(r*r-x1*x1);
			ints[ni].onCircle = true;
			ni++;
		}
		if (between(x2, p1.x, p2.x)) {
			ints[ni].x = x2;
			ints[ni].y = sqrt(r*r-x2*x2);
			ints[ni].onCircle = true;
			ni++;
		}
		if (ni == 2) {
			ints[2] = ints[1];
			ints[1].x = (ints[0].x + ints[1].x)/2.0;
			ints[1].y = (ints[0].y + ints[1].y)/2.0;
			ints[1].onCircle = false;
			ni = 3;
		}
		return ni;
	}
}

double trapArea(point p1, point p2, double r)
{
	double area = (p2.x - p1.x)*(p2.y+p1.y)/2.0;
	if (p1.onCircle && p2.onCircle) {
		long double cosa = (p1.x*p2.x + p1.y*p2.y)/r/r;
		long double sina = sqrt(1.0 - cosa*cosa);
		long double a = atan(sina/cosa);
		if (a == 0)
			a = PI;
		else if (a < 0)
			a += PI;
		double extra = (a - sina)*r*r/2.0;
		if (area*extra > 0)
			area += extra;
		else
			area -= extra;
	}
	return area;
}

int main()
{
	int np;
	double r;

	cin >> np >> r;
	for(int i=0; i<np; i++) {
		double x, y;
		cin >> x >> y;
		poly[i].x = x;
		poly[i].y = y;
		poly[i].inCircle = (r*r - x*x - y*y > 0);
		poly[i].onCircle = false;
	}
	point plast = poly[np-1];
	point ints[3];
	int ni = 0;
	for(int i=0; i<np; i++) {
		point p = poly[i];
		if (plast.inCircle && p.inCircle)
			isect[ni++] = p;
		else {
			int nint = intersect(plast, poly[i], r, ints);
			if (plast.inCircle)
				isect[ni++] = ints[0];
			else if (p.inCircle) {
				isect[ni++] = ints[0];
				isect[ni++] = p;
			}
			else {
				for(int j = 0; j<nint; j++)  {
					isect[ni++] = ints[j];
				}
			}
		}
		plast = poly[i];
	}
	double area = 0.0;
	if (ni > 0) {
		if (ni < 3)
			cout << "ERROR: less than 3 intersection points" << endl;
		area = trapArea(isect[ni-1], isect[0], r);
		for(int i=0; i<ni-1; i++) {
			area += trapArea(isect[i], isect[i+1], r);
		}
		if (area < 0.0)
			area *= -1;
	}
	printf("%0.3f\n", area);
}
