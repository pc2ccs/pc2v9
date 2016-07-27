// variant that rounds to exactly 3 digits of precision. This should be acceptable given tolerance range

#include <iostream>
#include <iomanip>
#include <sstream>
#include <queue>
#include <cmath>
using namespace std;

bool debug = false;

int n;            // number of measurements
double p[4];      // probabilities
long long factn;  // n! (calculated only once)

// bundle represents many identical trees
struct bundle {
    long long mult;   // how many of these trees are in the queue
    double prob;      // probability that weather sample lies in one such tree tree
    double bits;      // contribution to overall expected bitlength for one such tree
    bool operator<(const bundle& other) const {
        return prob > other.prob;   // small prob has higher priority
    }
};
// for debug only
ostream& operator<<(ostream& out, const bundle& b) {
    out << b.mult << "x{p:" << b.prob << ", b:" << b.bits << "}";
    return out;
}


long long fact(int k) {
    long long result=1;
    while (k)
        result *= k--;
    return result;
}

long long combinations(int a, int b, int c) {
    long long result = factn;
    result /= fact(a);
    result /= fact(b);
    result /= fact(c);
    result /= fact(n-a-b-c);
    return result;
}

// return new bundle created by combining all pairs from this bundle
// (and as side effect, leave given bundle with count 0 or 1)
bundle reduce(bundle& orig) {
    if (debug) cout << orig <<  "                             "
                    <<" --> ";
    bundle result(orig);  // start with copy
    orig.mult = orig.mult % 2;
    result.mult /= 2;
    result.prob *= 2;
    result.bits = 2*result.bits + result.prob;  // one extra bit for every entry of tree
    if (debug) {
        cout << result;
        if (orig.mult > 0) cout << " + " << orig;
        cout << endl;
    }
    return result;
}

// create bundle by joining one tree of first with one tree of second
// (and as side effect, reduces count of second bundle; we assume first is depleted)
bundle combine(bundle& first, bundle& second) {
    if (debug) cout << first << " + " << second << " --> ";
    bundle result;
    second.mult--;    // we're using up one
    result.mult = 1;
    result.prob = first.prob + second.prob;
    result.bits = first.bits + second.bits + result.prob;
    if (debug) {
        cout << result;
        if (second.mult > 0) cout << " + " << second;
        cout << endl;
    }
    return result;
}

double solve() {
    priority_queue<bundle> queue;
    // initialize
    for (int a=0; a <= n; a++)
        for (int b=0; b <= n-a; b++)
            for (int c=0; c <= n-a-b; c++) {
                int d = n-a-b-c;
                bundle temp;
                temp.prob = pow(p[0],a) * pow(p[1],b) * pow(p[2],c) * pow(p[3],d);
                temp.bits = 0;
                temp.mult = combinations(a,b,c);
                if (debug) {
                    cout << "created bundle for counts: " << a << " " << b << " " << c << " "
                         << (n-a-b-c) << " --> " << temp << endl;
                }
                queue.push(temp);
            }

    // reduce to single tree
    while (queue.size() > 1 || queue.top().mult > 1) {
        if (debug) cout << "Queue size currently " << queue.size() << endl;
        bundle first = queue.top();
        queue.pop();
        if (first.mult > 1) {
            queue.push(reduce(first));
            if (first.mult > 0)
                queue.push(first);
        } else {
            bundle second = queue.top();
            queue.pop();
            queue.push(combine(first,second));
            if (second.mult > 0)
                queue.push(second);
        }
    }
    return queue.top().bits;
}

int main(int argc, char** argv) {
    if (argc > 1) debug = true;
    cout << fixed << setprecision(4);
    cin >> n;
    factn = fact(n);
    for (int j=0; j<4; j++) cin >> p[j];
    double answer = solve();
    cout << answer << endl;
    return 0;
}
