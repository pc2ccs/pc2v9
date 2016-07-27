#include <cstdio>

typedef long long ll;

ll IntervalsNeeded(ll t, ll ctime, ll v2, ll l) {
  ll lo = 0;
  ll hi = (2 * v2) / t + 1000;
  while (hi - lo > 1) {
    ll med = (hi + lo) / 2;
    if ((med + 1) * t * ctime >= v2 * ctime - l) {
      hi = med;
    } else {
      lo = med;
    }
  }
  return hi;
}

int main() {
  int cases; scanf("%d", &cases);
  for (int c = 0; c < cases; ++c) {
    ll l, v1, v2, t, s; scanf("%lld %lld %lld %lld %lld", &l, &v1, &v2, &t, &s);
    ll cintervals = 1;
    ll ctime = 0;
    while (true) {
//      printf("At knocks %lld (time %lld) I search [%lld, %lld], accur %lld, holding %lld intervals\n", ctime, ctime * s, v1, v2, t, cintervals);
      if (cintervals * t >= v2 - v1) {
        printf("%lld\n", ctime);
        break;
      }
      if ((v2 - t) * (ctime + 1) * s > l) {
        ll intervals_needed = IntervalsNeeded(t, (ctime + 1) * s, v2, l);
//        printf("We need %lld intervals - v2 is %lld, acc is %lld, we're at time %lld, pipe is %lld!\n", intervals_needed, v2, t, (ctime + 1) * s, l);
        if (intervals_needed >= cintervals) {
          printf("impossible\n");
          break;
        }
        cintervals -= intervals_needed;
        v2 -= t * intervals_needed;
      }
      cintervals *= 2;
      ctime += 1;
    }
  }
  return 0;
}
