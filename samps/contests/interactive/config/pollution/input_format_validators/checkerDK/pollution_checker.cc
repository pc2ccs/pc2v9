#include "validate.h"
#include <vector>
using namespace std;

#define MAX_CASES 1

struct Point {
  int x, y;
  Point(int x = 0, int y = 0) : x(x), y(y) {}
  Point operator-(const Point& p) const {return Point(x - p.x, y - p.y);}
};

int dotp(const Point& a, const Point& b) {return a.x*b.x + a.y*b.y;}
int crossp(const Point& a, const Point& b) {return a.x*b.y - a.y*b.x;}

bool Intersect(const Point& a1, const Point& a2, const Point& b1, const Point& b2) {
  int cp1 = crossp(a2-a1, b1-a1);
  int cp2 = crossp(a2-a1, b2-a1);
  if (cp1 <= 0 && cp2 <= 0 || cp1 >= 0 && cp2 >= 0) return false;
  cp1 = crossp(b2-b1, a1-b1);
  cp2 = crossp(b2-b1, a2-b1);
  if (cp1 <= 0 && cp2 <= 0 || cp1 >= 0 && cp2 >= 0) return false;
  return true;
}

bool PointOnLine(const Point& p, const Point& a1, const Point& a2) {
  if (crossp(p-a1, a2-a1) != 0) return false;
  if (dotp(p-a1, a2-a1) < 0) return false;
  if (dotp(p-a2, a1-a2) < 0) return false;
  return true;
}

main() {
  for (int prob = 1; ; prob++) {
    ReadChar();
    bool done = in->eof();
    UnReadChar();
    if (done) break;
    if (prob > MAX_CASES) ShowError("Too many cases.");

    int N = ExpectInteger(3, 100);
    ExpectChar(' ');
    int R = ExpectInteger(1, 1000);
    ExpectEndOfLine();

    vector<Point> P(N);
    for (int i = 0; i < N; i++) {
      P[i].x = ExpectInteger(-1500, 1500);
      ExpectChar(' ');
      P[i].y = ExpectInteger(0, 1500);
      if (P[i].x*P[i].x + P[i].y*P[i].y == R*R) {
        ShowError("Vertex lies on circle boundary.");
      }
      ExpectEndOfLine();
    }
    P.push_back(P[0]);

    for (int i = 0; i < N; i++)
    for (int j = i+2; j < N; j++) {
      if (i == 0 && j == i-1) continue;
      if (Intersect(P[i], P[i+1], P[j], P[j+1])) {
        ShowError("Lines " + ToString(i) + " and " + ToString(j)
            + " intersect.");
      }
    }
    for (int i = 0; i < N; i++)
    for (int j = 0; j < N; j++) {
      if (j == i || j == (i+1)%N) continue;
      if (PointOnLine(P[j], P[i], P[i+1])) {
        ShowError("Point " + ToString(j) + " lies on line " + ToString(i)
            + ".");
      }
    }

    long long area = 0;
    for (int i = 0; i < N; i++)
      area += P[i].x * P[i+1].y - P[i+1].x * P[i].y;
    if (area < 0) ShowError("Vertices in clockwise order.");
  }
  ExpectEndOfFile();
  PrintSuccess();
}
