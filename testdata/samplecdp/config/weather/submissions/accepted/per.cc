#include <cstdio>
#include <algorithm>
#include <map>
#include <vector>
using namespace std;

typedef vector<int> vi;
typedef long long ll;

ll ways(const vi &F) {
	ll r = 1, n = 1;
	for (auto f: F)
		for (int i = 1; i <= f; ++i)
			r = r * n++ / i;
	return r;
}

struct state {
	double p, C;
	state(double p, double C=0): p(p), C(C) {}
	bool operator<(const state &st)  const {
		if (p != st.p) return p < st.p;
		return C < st.C;
	}
};

int main(void) {
	int n;
	double p1, p2, p3, p4;
	scanf("%d%lf%lf%lf%lf", &n, &p1, &p2, &p3, &p4);
	vi F(4);
	map<state,ll> S;
	for (F[0] = 0; F[0] <= n; ++F[0])
		for (F[1] = 0; F[0]+F[1] <= n; ++F[1])
			for (F[2] = 0; F[0]+F[1]+F[2] <= n; ++F[2]) {
				F[3] = n-F[0]-F[1]-F[2];
				S[state(pow(p1, F[0])*pow(p2, F[1])*pow(p3, F[2])*pow(p4, F[3]))] += ways(F);
			}
	while (true) {
		const state &s = S.begin()->first;
		ll cnt = S.begin()->second;
		if (cnt > 1)
			S[state(2*s.p, 1+s.C)] += cnt/2;
		S.erase(S.begin());
		if (S.empty()) {
			printf("%.9lf\n", s.C);
			break;
		}
		if (cnt % 2) {
			const state &t = S.begin()->first;
			if (!--S.begin()->second)
				S.erase(S.begin());
			++S[state(s.p+t.p, 1+(s.p*s.C+t.p*t.C)/(s.p+t.p))];
		}
	}
	return 0;
}
