#include "validate.h"
#include <vector>
using namespace std;

#define MAX_CASES 1

struct Point {
  double x, y;
  int cost;
  Point(double x = 0.0, double y = 0.0) : x(x), y(y), cost(0) {}
  Point operator-(const Point& p) const {return Point(x-p.x, y-p.y);}
  Point operator+(const Point& p) const {return Point(x+p.x, y+p.y);}
  Point operator*(double c) const {return Point(x*c, y*c);}
  Point operator/(double c) const {return Point(x/c, y/c);}
  double len() {return hypot(x, y);}
};

double dotp(const Point& a, const Point& b) {
  return a.x*b.x + a.y*b.y;
}
double crossp(const Point& a, const Point& b) {
  return a.x*b.y - a.y*b.x;
}

// Precise when the endpoints are lattice points <= 1e7.
Point intersect(const Point& l1a, const Point& l1b,
                const Point& l2a, const Point& l2b) {
  double cp1 = crossp(l2b-l2a, l1a-l2a);
  double cp2 = crossp(l2b-l2a, l1b-l2a);
  if (cp1 < 0 && cp2 < 0 || cp1 > 0 && cp2 > 0) return Point(1e10, 1e10);
  if (cp1 == 0 && cp2 == 0) {
    double dp1 = dotp(l2b-l2a, l1a-l2a);
    double dp2 = dotp(l2b-l2a, l1b-l2a);
    if (dp1 < 0 && dp2 < 0) return Point(1e10, 1e10);
    if (dp1 <= 0 && dp2 <= 0) return l2a;
    dp1 = dotp(l2a-l2b, l1a-l2b);
    dp2 = dotp(l2a-l2b, l1b-l2b);
    if (dp1 < 0 && dp2 < 0) return Point(1e10, 1e10);
    if (dp1 <= 0 && dp2 <= 0) return l2b;
    return Point(1e11, 1e11);
  }
  cp1 = crossp(l1b-l1a, l2a-l1a);
  cp2 = crossp(l1b-l1a, l2b-l1a);
  if (cp1 < 0 && cp2 < 0 || cp1 > 0 && cp2 > 0) return Point(1e10, 1e10);
  return (l2a * cp2 - l2b * cp1) / (cp2 - cp1);
}

bool point_in_poly(const vector<Point>& poly, const Point& p) {
  Point p2 = p + Point(1e7, 1);
  int n = 0;
  for (int i = 0; i < poly.size(); i++) {
    n += (intersect(p, p2, poly[i], poly[(i+1)%poly.size()]).x != 1e10);
  }
  return n%2 == 1;
}

main() {
  for (int prob = 1; ; prob++) {
    ReadChar();
    bool done = in->eof();
    UnReadChar();
    if (done) break;
    if (prob > MAX_CASES) ShowError("Too many cases.");

    vector<Point> A[2];
    for (int a = 0; a < 2; a++) {
      int N = ExpectInteger(3, 10);
      ExpectChar(' ');

      vector<Point>& P = A[a];
      P.resize(N);
      for (int i = 0; i < N; i++) {
        P[i].x = ExpectInteger(-10000, 10000);
        ExpectChar(' ');
        P[i].y = ExpectInteger(-10000, 10000);
        ExpectChar(' ');
      }

      P.push_back(P[0]);
      P.push_back(P[1]);
      for (int i = 0; i < N; i++) {
        if ((P[i+1]-P[i]).len() > 500) {
          ShowError("Line " + ToString(i) + " longer than 500.");
        }
      }
      for (int i = 0; i < N; i++)
      for (int j = i+2; j < N; j++) {
        if (i == 0 && j == N-1) continue;
        if (intersect(P[i], P[i+1], P[j], P[j+1]).x != 1e10) {
          ShowError("Lines " + ToString(i) + " and " + ToString(j)
              + " intersect.");
        }
      }
      for (int i = 1; i <= N; i++) {
        if (crossp(P[i]-P[i-1], P[i+1]-P[i]) >= 0) {
          ShowError("Lines " + ToString(i-1) + " and " + ToString(i)
              + " not convex.");
        }
      }
      P.pop_back();
      P.pop_back();

      ExpectInteger(-100, 100);
      ExpectChar(' ');
      ExpectInteger(-100, 100);
      ExpectEndOfLine();
    }
    
    for (int i = 0; i < A[0].size(); i++)
    for (int j = 0; j < A[1].size(); j++)
      if (intersect(A[0][i], A[0][(i+1)%A[0].size()],
                    A[1][j], A[1][(j+1)%A[1].size()]).x != 1e10) {
        ShowError("Lines " + ToString(i) + " (A0) and " + ToString(j)
            + " (A1) intersect.");
      }
    if (point_in_poly(A[0], A[1][0])) {
      ShowError("A1 inside A0.");
    }
    if (point_in_poly(A[1], A[0][0])) {
      ShowError("A0 inside A1.");
    }
  }
  ExpectEndOfFile();
  PrintSuccess();
}
