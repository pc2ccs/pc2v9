#include <iostream>
#include <cmath>
using namespace std;

const int MAX = 1000000;
int main()
{
	double p, a, b, c, d;
	int n;
	double vals[MAX+1];

	cin >> p;
	cin >> a >> b >> c >> d;
	cin >> n;
	for(int i=1; i<=n; i++)
		vals[i] = sin(a*i+b) + cos(c*i+d);
	double maxdec = 0.0;
	int iend, istart;
	for(int i=1; i<=n; i++) {
		for(int j=i+1; j<=n; j++) {
			if (vals[i] - vals[j] > maxdec) {
				maxdec = vals[i] - vals[j];
				istart = i;
				iend = j;
			}
		}
	}
//	cout << p*maxdec << ' ' << istart << ' ' << iend << endl;
	cout.precision(10);
	cout << p*maxdec << endl;
}

