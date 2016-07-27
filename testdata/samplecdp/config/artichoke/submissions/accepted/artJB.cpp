#include <iostream>
#include <cmath>
#include <cstdio>
using namespace std;

int main()
{
	long long p, a, b, c, d;
	int n;

	cin >> p;
	cin >> a >> b >> c >> d;
	cin >> n;
	double smallest = sin(a*n+b) + cos(c*n+d);
	double maxdec = 0.0;
	int iend, istart, ismall = n;
	for(int i=n-1; i>=1; i--) {
		double val = sin(a*i+b) + cos(c*i+d);
		if (val - smallest >= maxdec) {
			maxdec = val - smallest;
			istart = i;
			iend = ismall;
		}
		else if (val < smallest) {
			smallest = val;
			ismall = i;
		}
	}
//	if (maxdec == 0.0)
//		cout << 0 << endl;
//	else
//		cout << p*maxdec << ' ' << istart << ' ' << iend << endl;
//		cout << p*maxdec << endl;
		printf("%.6f\n", p*maxdec);
}

