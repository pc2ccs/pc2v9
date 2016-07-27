#include <cstdio>
#include <algorithm>

using namespace std;

typedef pair<double,double> pdd;
typedef vector<pdd> vpd;

int main(void) {
	int n;
	int w, u, v, t1, t2;
	scanf("%d%d%d%d%d%d", &n, &w, &u, &v, &t1, &t2);
	vpd ivals;
	for (int i = 0; i < n; ++i) {
		char s[10];
		scanf("%s", s);
		int d = *s == 'E' ? 1 : -1, m;
		scanf("%d", &m);
		for (int j = 0; j < m; ++j) {
			int l, p;
			scanf("%d%d", &l, &p);
			double t1 = -p*1.0*d/u, t2 = -(p-d*l)*1.0*d/u;
			if (t1 > t2) swap(t1, t2);
			ivals.push_back(pdd(t1-(i+1)*1.0*w/v, t2-i*1.0*w/v));
		}
	}
	//	ivals.push_back(pdd(t2, 1e90));
	sort(ivals.begin(), ivals.end());
	double at = t1, res = 0;
	for (pdd p: ivals) {
		res = max(res, p.first-at);
		at = max(at, p.second);
	}
	res = max(res, t2-at);
	printf("%.8lf\n", res);
	return 0;
}
