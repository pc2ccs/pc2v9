#include <iostream>
using namespace std;

main() {
  long long T;
  for (cin >> T; T--;) {
    long long L, V1, V2, Tol, S;
    cin >> L >> V1 >> V2 >> Tol >> S;

    long long covered = 0;
    long long ret = 0, num = (V2-V1+Tol-1)/Tol, Vmid = V2-Tol;
    if (Vmid <= V1) goto done;

    while (Vmid > V1 && covered < (1LL<<60)) {
      long long maxdepth = min(60LL, L / (S*Vmid));
      if (num <= (((1LL<<60)-covered) >> (60-maxdepth))) {
        while (num <= (((1LL<<60)-covered) >> (60-maxdepth+1))) {
          maxdepth--;
        }
        ret = maxdepth;
        goto done;
      }
      covered += (1LL<<(60-maxdepth));
      Vmid -= Tol;
      num--;
    }
    cout << "impossible" << endl;
    continue;
done:
    cout << ret << endl;
  }
}
