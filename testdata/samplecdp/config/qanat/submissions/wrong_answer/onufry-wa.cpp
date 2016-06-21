#include <cstdio>

typedef long double ld;

ld P[600];  // P[i] = lambda ** i
ld S[600];  // S[i] = sum(0 : i-1) P[k]

ld UpCost(ld W, ld H, int K, int N) {
  return S[K + 1] * S[K + 1] * H * H / (2 * S[N + 1] * S[N + 1]);
}

ld LeftCost(ld W, ld H, int K, int N) {
  return P[K] * (W - H) * S[K + 1] * H / (2 * S[N + 1] * S[N + 1]) +
        (P[K] * P[K] * (W - H) * (W - H)) / (8 * S[N + 1] * S[N + 1]);
}

ld RightCost(ld W, ld H, int K, int N) {
  return P[K+1] * (W + H) * S[K + 1] * H / (2 * S[N + 1] * S[N + 1]) +
        (P[K+1] * P[K+1] * (W + H) * (W + H)) / (8 * S[N + 1] * S[N + 1]);
}

ld Cost(ld W, ld H, int N) {
  ld cost = RightCost(W, H, -1, N) + UpCost(W, H, N, N) + LeftCost(W, H, N, N);
  for (int K = 0; K < N; ++K) {
    cost += LeftCost(W, H, K, N) + UpCost(W, H, K, N) + LeftCost(W, H, K, N);
  }
  return cost;
}

int main() {
  ld W, H;
  int NN;
  scanf("%Lf %Lf %d", &W, &H, &NN);
  ld lambda = (W * W + H * H) / (W * W - H * H);
  P[0] = 1;
  for (int i = 1; i < 600; ++i) P[i] = P[i-1] * lambda;
  S[0] = 0;
  for (int i = 1; i < 600; ++i) S[i] = S[i-1] + P[i-1];
  int bestN = 0;
  ld bestC = W * W + H * H;
  for (int N = 0; N <= NN; ++N) {
    ld C = Cost(W, H, N);
    if (C < bestC) {
      bestN = N;
      bestC = C;
    }
  }
  bool skipped = false;
  for (int p = 0; p < bestN && p < 10; ++p) {
    if (skipped) printf(" ");
    skipped = true;
    printf("%.6Lf", W * S[p + 1] / S[bestN + 1]);
  }
  printf("\n");
  return 0;
}
