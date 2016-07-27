#include <cstdio>
#include <algorithm>
#include <cassert>

using namespace std;

typedef long double ld;

struct Event {
  Event() {}
  Event(ld t, bool beg) : t(t), beg(beg) {}
  ld t;
  bool beg;

  bool operator<(const Event &other) const {
    return (t < other.t);
  }
};

Event E[300000];
int etop;

ld W, U, V, T1, T2;

void Ship(char dir, int lane, ld pos, ld len) {
  if (dir == 'W') {
    pos = -pos;
  } else {
    assert(dir == 'E');
  }
  ld tx = pos;
  ld ty = (lane + 1) * W;
  ld Tb = -ty / V - tx / U;
  tx = pos - len;
  ty = lane * W;
  ld Te = -ty / V - tx / U;
  if (Tb > T2 || Te < T1) return;
  if (Tb < T1) Tb = T1;
  if (Te > T2) Te = T2;
  E[etop++] = Event(Tb, true);
  E[etop++] = Event(Te, false);
}

int main() {
  etop = 0;
  int N;
  scanf("%d %Lf %Lf %Lf %Lf %Lf", &N, &W, &U, &V, &T1, &T2);
  for (int i = 0; i < N; ++i) {
    char C;
    int M;
    scanf("\n%c %d", &C, &M);
    for (int j = 0; j < M; ++j) {
      ld L, P;
      scanf("%Lf %Lf", &L, &P);
      Ship(C, i, P, L);
    }
  }
  E[etop++] = Event(T1, false);
  E[etop++] = Event(T2, true);
  sort(&E[0], &E[etop]);
  int count = 1;
  ld beg = T1;
  ld best = 0;
  for (int i = 0; i < etop; ++i) {
    if (E[i].beg) {
      if (count == 0 && E[i].t - beg > best) best = E[i].t - beg;
      count += 1;
    } else {
      count -= 1;
      if (count == 0) beg = E[i].t;
    }
  }
  printf("%Lf\n", best);
  return 0;
}
