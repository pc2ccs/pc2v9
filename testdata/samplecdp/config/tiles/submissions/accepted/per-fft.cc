#include <cstdio>
#include <complex>
#include <iostream>

using namespace std;
typedef complex<double> C;
const double tau = 4.0*acos(0.0);

void fft(C *x, C *y, int d, int sgn) {
	int N = 1<<d;
	if (N == 1) {
		y[0] = x[0];
		return;
	}
	C sx[1<<d-1], sy[1<<d-1];
	for (int j = 0; j < N/2; ++j) sx[j] = x[2*j];
	fft(sx, sy, d-1, sgn);
	for (int i = 0; i < N/2; ++i) y[i] = y[i+N/2] = sy[i];
	for (int j = 0; j < N/2; ++j) sx[j] = x[2*j+1];
	fft(sx, sy, d-1, sgn);
	for (int i = 0; i < N/2; ++i) {
		C f = polar(1.0, tau*sgn*i/N)*sy[i];
		y[i] += f;
		y[i+N/2] -= f;
	}
}

int main(void) {
	int d = 20, N = 1<<d, n = (1<<d-1)-1;
	C div[N], fdiv[N], one(1,0);
	for (int i = 1; i <= n; ++i) {
		div[i] += one;
		for (int j = i+i; j <= n; j += i)
			div[j] += one;
	}
	fft(div, fdiv, d, 1);
	for (int i = 0; i < N; ++i) fdiv[i] *= fdiv[i];
	fft(fdiv, div, d, -1);
	scanf("%d", &n);
	for (int i = 0; i < n; ++i) {
		int lo, hi, r = 0, a = 0;
		scanf("%d%d", &lo, &hi);
		for (int j = lo; j <= hi; ++j)
			if (int(div[j].real()/N+0.5) > r)
				r = int(div[j].real()/N+0.5), a = j;
		printf("%d %d\n", a, r);
	}
	//	for (int i = 1; i <= n; ++i)
	//		printf("%d\n", (int)(div[i].real()/N+0.5));
	return 0;
}
