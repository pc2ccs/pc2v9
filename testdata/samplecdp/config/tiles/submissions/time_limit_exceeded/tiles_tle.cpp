#include <cstdio>
#include <vector>

using namespace std;

typedef long long ll;

ll D[600000];
ll V[600000];

int main() {
  int cases;
  scanf("%d", &cases);
  vector<int> As;
  vector<int> Bs;
  int max_b = 0;
  int min_a = 10000000;
  for (int i = 0; i < cases; ++i) {
    int a, b;
    scanf("%d %d", &a, &b);
    As.push_back(a);
    Bs.push_back(b);
    if (b > max_b) max_b = b;
    if (a < min_a) min_a = a;
  }

  for (int N = 1; N <= max_b; ++N) D[N] = 0LL;
  for (int N = 1; N <= max_b; ++N) {
    for (int k = N; k <= max_b; k += N) {
      D[k] += 1;
    }
  }
//  for (int i = 0; i <= max_b; ++i) printf("D[%d] = %lld\n", i, D[i]);
  for (int i = min_a; i <= max_b; ++i) V[i] = 0LL;
  for (int i = min_a; i <= max_b; ++i) if (i < 100 || ((i & 4) == 0)) {
    for (int j = 1; j < i; ++j) {
      V[i] += D[j] * D[i-j];
    }
  }
  for (int i = 0; i < As.size(); ++i) {
    ll best = 0LL;
    int best_pos = 0;
//    printf("VERIFYING [%d, %d]\n", As[i], Bs[i]);
    for (int k = As[i]; k <= Bs[i]; ++k) {
//      printf("  trying V[%d] = %lld\n", k, V[k]);
      if (V[k] > best) {
        best = V[k];
        best_pos = k;
      }
    }
    printf("%d %lld\n", best_pos, best);
  }
  return 0;
}
