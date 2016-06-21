// Note: this is the solution written for DNA (as the problem was
// called then) for ICPC 2013, slightly modified for the new format. /Per

// Standardowy szablon
#include <cstdio>
#include <algorithm>
#include <string>
#include <vector>
using namespace std;
#define REP(i,n) for(int i=0;i<(n);++i)
#define PB push_back
#define SIZE(x) (int)x.size()


int N;
#define MAXN 4010
string T[MAXN]; // Strings sorted by length
string TR[MAXN]; // Strings as input

typedef struct perm { // A wrapper for ints for easy sorting
	int p;
} perm;

perm ps[MAXN+1];
int sons[MAXN+1];

bool operator< (const perm &A, const perm &B) {
	return TR[A.p].size() > TR[B.p].size();
}

int canfit (const string &A, const string &B) { // Is B a subsequence of A? (an easy greedy check)
	int pos = 0;
	int posB = 0;
	while (1) {
		while (pos < (int) A.size() && A[pos] != B[posB]) pos++;
		if (pos == (int) A.size()) return 0;
		posB++;
		pos++;
		if (posB == (int) B.size()) return 1;
	}
}

int ctops[3]; // Current candidates for the "last on stack"
int otop; // 
int ntops; // Number of candidates

int go(int ctop) { // Fit the next candidate (see solution description)
	if (ntops == 2) {
		if (canfit(T[ctops[0]], T[ctop])) {
			if (canfit(T[ctops[1]], T[ctop])) {
				ctops[2] = ctop;
				otop = ctop;
				ntops = 3;
			} else {
				sons[ctops[0]] = ctop;
				ctops[0] = ctop;
			}
		} else {
			if (canfit(T[ctops[1]], T[ctop])) {
				sons[ctops[1]] = ctop;
				ctops[1] = ctop;
			} else return 0;
		}
	} else {
		if (canfit(T[ctops[2]], T[ctop])) {
			sons[ctops[2]] = ctop;
			ctops[2] = ctop;
		} else {
			if (canfit(T[ctops[0]], T[ctop])) {
				sons[ctops[1]] = otop;
				sons[ctops[0]] = ctop;
				ctops[0] = ctop;
				ctops[1] = ctops[2];
				ntops = 2;
			} else {
				if (canfit(T[ctops[1]], T[ctop])) {
					sons[ctops[0]] = otop;
					sons[ctops[1]] = ctop;
					ctops[1] = ctop;
					ctops[0] = ctops[2];
					ntops = 2;
				} else return 0;
			}
		}
	}
	return 1;
}

int main() {
	int testnum = 0;
	char buf[2 * MAXN];
	while (scanf("%d\n", &N) != EOF)
	{
//		printf("Case %d: ", ++testnum);
		scanf("%s\n", buf);
		TR[0] = buf; 
		TR[1] = buf; // Begin the sequence by two instances of the start string
		int ok = 1;
		REP (i, N) {
			scanf("%s\n", buf);
			TR[i+2] = string(buf);
			if (!canfit(TR[0],TR[i+2])) ok = 0; // Check whether all strings are substrings of the input
		}
		if (ok == 0) {printf("Impossible\n"); continue;}
		N += 2;
		REP (i, N) ps[i].p = i;
		sort(&ps[0], &ps[N]); // Sort strings by length
		REP (i, N) T[i] = TR[ps[i].p];
		REP(i, N) sons[i] = -1;
		ctops[0] = 0;
		ctops[1] = 1;
		otop = -1;
		ntops = 2;
		int ctop = 2;
		while (ctop < N && go(ctop)) ctop++; // The main loop
		if (ctop < N) printf("Impossible\n"); else {
			if (ntops == 3) {
				sons[ctops[0]] = otop;
			}
			vector<string> res[2];
			REP (k, 2) { // Rewrite so as to have two sequences of strings
				int c = k;
				while (sons[c] != -1) {
					res[k].PB(T[sons[c]]); 
					c = sons[c];
				}
			}
			printf("%d %d\n", SIZE(res[0]), SIZE(res[1]));
			reverse(res[0].begin(), res[0].end());
			reverse(res[1].begin(), res[1].end());
			REP (i, SIZE(res[0])) printf("%s\n", res[0][i].c_str());
			REP (i, SIZE(res[1])) printf("%s\n", res[1][i].c_str());
		}
	}
	return 0;
}

