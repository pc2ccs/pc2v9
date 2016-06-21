#include <algorithm>
#include <cmath>
#include <cstdio>
#include <iostream>
using namespace std;

#define MAX 500000
#define INTERVAL 40

int nf[MAX+1];
int doit(int n) {
  int ret = 0;
  for (int i = 1; i < n; i++) ret += nf[i]*nf[n-i];
  return ret;
}

main() {
  for (int x = 1; x <= MAX; x++)
  for (int y = x; y <= MAX; y += x)
    nf[y]++;
  int mx = 0;
  for (int n = 1; n <= MAX; n++) {
    mx = max(mx, doit(n));
    if (n%INTERVAL == 0) {
      if ((n/INTERVAL)%8 == 1) printf("\"");
      printf("%08x", mx);
      if ((n/INTERVAL)%8 == 0) printf("\"\n");
      mx = 0;
    }
  }
}
