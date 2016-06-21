#include <cstring>
#include <algorithm>
#include <iostream>
#include <string>
#include <vector>

using namespace std;

typedef vector<string> vs;
typedef vector<int> vi;

bool sub(const string &x, const string &y) {
	int i = 0;
	for (auto c: y) {
		while (i < x.length() && x[i] != c) ++i;
		if (i == x.length()) return false;
		++i;
	}
	return true;
}

int n;
int adj[4010][4010], path[4010][4010];

bool Path(int i, int j) {
	if (i == n) return true;
	int &r = path[i][j];
	if (r == -1) {
		r = false;
		if (adj[j][i+1]) r |= Path(i+1, i);
		if (adj[i][i+1]) r |= Path(i+1, j);
	}
	return path[i][j];
}

void MakePath(int i, int j, vi &A, vi &B) {
	if (i == n) return;
	if (adj[i][i+1] && Path(i+1, j)) {
		MakePath(i+1, j, A, B);
		A.push_back(i+1);
	} else {
		MakePath(i+1, i, B, A);
		B.push_back(i+1);
	}
}

bool solve() {
	if (!(cin >> n)) return false;
	vs dna(n+1);
	for (auto &x: dna) cin >> x;
	sort(dna.begin()+1, dna.end(), [](string x, string y) { return x.length() > y.length(); });
	int at = 0;
	memset(adj, 0, sizeof(adj));
	memset(path, -1, sizeof(path));
	int pi = 0;
	for (int i = 1; i <= n; ) {
		int j = i+1;
		while (j <= n && sub(dna[j-1], dna[j])) ++j;
		int pj = pi;
		for (int a = i; a < j; ++a) {
			while (pj < i && sub(dna[pj], dna[a])) ++pj;
			for (int b = pi; b < pj; ++b) adj[b][a] = true;
			for (int b = a+1; b < j; ++b) adj[a][b] = true;
		}
		if (j <= n && sub(dna[i-1], dna[j])) adj[i-1][j] = true;
		pi = i; 
		i = j;
	}
	if (Path(0, 0)) {
		vi A, B;
		MakePath(0, 0, A, B);
		printf("%d %d\n", A.size(), B.size());
		for (auto i: A) printf("%s\n", dna[i].c_str());
		for (auto i: B) printf("%s\n", dna[i].c_str());
	} else
		printf("Impossible\n");
	return true;
}

int main(void) {
	while (solve());
	return 0;
}
