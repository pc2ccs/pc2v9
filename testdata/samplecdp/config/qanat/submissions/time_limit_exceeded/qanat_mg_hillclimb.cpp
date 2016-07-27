// This approach attempts to do a generic hill-climbing approach to
// place the N shafts, starting with them uniformly distributed.
#include <iostream>
#include <iomanip>
#include <cmath>
using namespace std;

#define MAX_W 10000
#define MAX_N 1000
#define EPS   0.00000000001

bool VERBOSE(false);

double x[2+MAX_N];  // x[k] will be final coordinate for k-th shaft from 1 to N

double W,H;
int N;

double cost() {
    double total = 0;
    for (int k=1; k <= N+1; k++) {
        double h1 = x[k-1]*H/W;      // height of left shaft
        double h2 = x[k]*H/W;        // height of right shaft
        double a = (h2-h1+x[k]+x[k-1])/2.0;
        double L = h1+a-x[k-1];
        total += L*L - h1*h1/2.0;    // don't charge for mining shaft k-1
    }
    return total;
}

void output(int j, ostream& out) {  // print up to the first j entries
    out << x[1];
    int end = min(10,N);
    for (int k=2; k <= end; k++)
        out << " " << x[k];
    out << endl;
}

int main(int argv, char** argc) {
    if (argv > 1)
        VERBOSE = true;

    cerr << fixed << setprecision(15);
    cin >> W >> H >> N;

    // initial guess for x
    for (int j=0; j<2+N; j++)
        x[j] = j*W/(N+1);

    // hill climb
    double opt, prev;
    opt = cost();
    long reps(0);
    do {
        cerr << "Current opt: " << opt << endl;
        output(N, cerr);
        prev = opt;
        reps++;
        // does it matter if we do round left-to-right or right-to-left
        for (int j=N; j > 0; j--) {
            // consider moving x[j]
            if (VERBOSE) cerr << "Considering moving x[" << j << "] from " << x[j] << endl;
            for (int sign = -1; sign <= 1; sign+=2) {
                double d = (x[j-sign] - x[j])/2;
                while (abs(d) > EPS) {
                    double save = x[j];
                    x[j] += d;
                    if (VERBOSE) cerr << "Considering x[" << j << "] of " << x[j] << "...";
                    d /= 2;
                    double temp = cost();
                    if (temp < opt) {
                        opt = temp;
                        if (VERBOSE) cerr << "YES" << endl;
                    } else {
                        x[j] = save; // roll back most recent change but try again with next delta
                        if (VERBOSE) cerr << "NO" << endl;
                    }
                }
            }
        }
    } while (prev - opt > EPS);

    cout << fixed << setprecision(6);
    cout << cost() << endl;
    output(10,cout);
    cerr << "Used " << reps << " rounds of climbing" << endl;
}
