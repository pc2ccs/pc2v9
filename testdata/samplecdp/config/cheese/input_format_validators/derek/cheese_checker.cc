#include "validate.h"
#include <vector>
using namespace std;

#define MAX_CASES 1

main() {
  for (int prob = 1; ; prob++) {
    ReadChar();
    bool done = in->eof();
    UnReadChar();
    if (done) break;
    if (prob > MAX_CASES) ShowError("Too many cases.");

    int N = ExpectInteger(0, 10000);
    ExpectChar(' ');
    ExpectInteger(1, 100);
    ExpectEndOfLine();
    vector<int> vr(N), vx(N), vy(N), vz(N);
    for (int i = 0; i < N; i++) {
      vr[i] = ExpectInteger(0, 100000);
      ExpectChar(' ');
      vx[i] = ExpectInteger(0, 100000);
      ExpectChar(' ');
      vy[i] = ExpectInteger(0, 100000);
      ExpectChar(' ');
      vz[i] = ExpectInteger(0, 100000);
      ExpectEndOfLine();
      if (vx[i]-vr[i] < 0 || vx[i]+vr[i] > 100000 ||
          vy[i]-vr[i] < 0 || vy[i]+vr[i] > 100000 ||
          vz[i]-vr[i] < 0 || vz[i]+vr[i] > 100000) {
        ShowError("Hole " + ToString(i) + " extends out of cube.");
      }
    }
    for (int i = 0; i < N; i++)
    for (int j = i+1; j < N; j++) {
      if ((long long)(vx[i]-vx[j])*(vx[i]-vx[j]) +
          (long long)(vy[i]-vy[j])*(vy[i]-vy[j]) +
          (long long)(vz[i]-vz[j])*(vz[i]-vz[j]) <
          (long long)(vr[i]+vr[j])*(vr[i]+vr[j])) {
        ShowError("Holes " + ToString(i) + " and " + ToString(j) +
                  " intersect.");
      }
    }
  }
  ExpectEndOfFile();
  PrintSuccess();
}
