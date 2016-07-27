/* Solution to "Tiles", variant "precalc or FFT", FFT-based solutions.
   The assumption is we're looking for the area that gives the maximum possible
   number of tiles, with the area being in [a, b), where a and b are the two
   numbers given on the input.

   The two versions of the solution are toggled by adding or removing the line
   "#define OPTI" below. The one with "define OPTI" is more precise but a bit
   slower.

   I could optimize it even further by precomputing all the interesting roots
   of 1 up front, sparing myself one complex multiplication in the deep loop.
*/

#include <cstdio>
#include <vector>
#include <complex>
#include <cmath>

#define REP(i,n) for (int i = 0; i < n; ++i)

using namespace std;

typedef long long ll;
typedef long double LD;
typedef complex<LD> C;

// The maximum number (inclusive) you're going to ask about.
#define MAXN 500000
// A power of two that is greater or equal to 2 * MAXN
#define POW2N (1<<20)
// Just a bit more not to think about array sizes.
const int MAXN2=(POW2N + 10);

C tmp[MAXN2];
C D2[MAXN2];
LD dwapi=4.0*acosl(0.0);

void DFT(C* a, int n, int rev) {
  if (n == 1) return;
  REP(i,n) tmp[i]=a[i];
  REP(i,n) if (i&1) a[i/2+n/2]=tmp[i];
  else a[i/2]=tmp[i];
  C *y0=a,*y1=a+n/2;
  DFT(y0,n/2,rev); DFT(y1,n/2,rev);
  LD alfa=dwapi/(LD)n;
#ifdef OPTI
  REP(k,n/2){
    C mnoznik=C(cosl(k*alfa),(rev?-1:1)*sinl(k*alfa))*y1[k];
    tmp[k]=y0[k]+mnoznik;
    tmp[k+n/2]=y0[k]-mnoznik;
  }
#endif
#ifndef OPTI
  C mnoznik(1., 0.);
  C pom = C(cos(alfa), (rev?-1:1)*sin(alfa));
  REP(k,n/2){
    tmp[k]=y0[k]+mnoznik*y1[k];
    tmp[k+n/2]=y0[k]-mnoznik*y1[k];
    mnoznik *= pom;
  }
#endif
  REP(i,n) a[i]=tmp[i];
}

inline ll val(const C &c) {
  return c.real() + 0.5;
}

int main() {
  for (int N = 0; N < MAXN2; ++N) D2[N] = C(0, 0);
  for (int N = 1; N <= MAXN; ++N) {
    for (int k = N; k <= MAXN; k += N) {
      D2[k] += C(1, 0);
    }
  }
  DFT(&D2[0], POW2N, 0); 
  for (int N = 0; N < MAXN2; ++N) D2[N] *= D2[N];
  DFT(&D2[0], POW2N, 1);
  for (int N = 0; N < MAXN2; ++N) D2[N] /= C(POW2N, 0);
  int cases;
  scanf("%d", &cases);
  for (int i = 0; i < cases; ++i) {
    int a, b;
    scanf("%d %d", &a, &b);
    ll best = 0LL;
    int best_pos = 0;
    for (int x = a; x <= b; ++x) {
      ll c = val(D2[x]);
      if (c > best) {
        best = c;
        best_pos = x;
      }
    }
    printf("%d %lld\n", best_pos, best);
  }
/*
// This lists all the maxima, for the "precompute requiring FFT variant"

  best = 0LL;
  for (int i = 0; i <= MAXN; ++i) {
    if (val(D2[i]) > best) {
      printf("%d: %lld\n", i, val(D2[i]));
      best = val(D2[i]);
    }
  }
*/
  return 0;
}
