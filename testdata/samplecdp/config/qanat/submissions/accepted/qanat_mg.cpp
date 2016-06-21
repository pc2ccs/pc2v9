// Solution to Qanat
// 2015 ICPC World Finals
// Michael Goldwasser
//
// Explanation of the mathematics of this approach are provided in a separate document.
#include <iostream>
#include <iomanip>
using namespace std;

#define MAX_W 10000
#define MAX_N 1000
#define MIN_SEPARATION 0.001

double C[1+MAX_N];  // C[k] opt construction cost if using k shafts
double t[1+MAX_N];  // rightmost shaft of k shafts should be at t[k]*W
double x[1+MAX_N];  // x[k] will be final coordinate for k-th shaft for 1 to N

int origW, origH, N;

double W;   // will be origW/origH so that we consider triangle that is W x 1

void computeT(int k) {
    if (k == 0)
        t[k] = 0;
    else {
        double numerator = (W*W-1);
        double denominator = 4*C[k-1] + (W-1)*(W-1);
        t[k] = numerator/denominator;
    }
}

void computeC(int k) {
    double temp = (1+W+t[k]*(1-W));
    double ans = temp*temp/4 - 0.5;
    if (k > 0)
        ans += t[k]*t[k]*C[k-1];
    C[k] = ans;
}

// used only for verifying input parameters
void validateInput() {
    if (origW < 1 || origW > MAX_W)
        cerr << "Illegal W value" << endl;
    if (origH < 1 || origH >= origW)
        cerr << "Illegal H value" << endl;
    if (N < 1 || N > MAX_N)
        cerr << "Illegal N value" << endl;

    double minsep = origW;
    x[0] = 0.0;                         // was never needed in main program
    for (int j=0; j < N; j++) {         // min must be between x[0] and x[1], but for good measure we'll check all
        if (x[j+1]-x[j] < minsep)
            minsep = x[j+1]-x[j];
    }

    if (minsep < MIN_SEPARATION)
        cerr << "WARNING: minimum shaft separation: " << setprecision(10) << x[1] << endl;
}

int main() {
    cout << fixed << setprecision(6);
    cin >> origW >> origH >> N;

    W = ((double) origW)/origH;

    for (int k=0; k <= N; k++) {
        computeT(k);
        computeC(k);
    }

    double scale = 1.0;
    for (int k=N; k>0; k--) {
        scale *= t[k];
        x[k] = scale * origW;
    }

    // output total cost (back to original scale)
    cout << (C[N] + 0.5) * origH * origH << endl;

    // output leftmost 10 shafts
    int stop = min(N,10);
    cout << x[1];
    for (int k=2; k <= stop; k++)
        cout << " " << x[k];
    cout << endl;

    validateInput();
}
