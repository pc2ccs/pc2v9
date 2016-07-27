// Note: this is the solution written for DNA (as the problem was
// called then) for ICPC 2013, slightly modified for the new format. /Per

#include<cstdio>
#include<algorithm>
#include<vector>
#include<string>
#include<utility>
#include<cstring>
#include<map>

#define REP(i, n) for (int i = 0; i < (n); ++i)
#define PB push_back

using namespace std;

int bot;
vector<int> now;

map<int, int> mnext;

string base;
vector<pair<int, string> > inputs;

const string &val(int p) {
  if (p < 0) return base;
  return inputs[p].second;
}

char tab[5000];

bool Fits(int i, int j) {
  const string &A = val(i);
  const string &B = val(j);
  int pa = 0;
  int pb = 0;
  while (pa < (int)A.size() && pb < (int)B.size()) {
    if (A[pa] == B[pb]) pb++;
    pa++;
  }
  return (pb == (int)B.size());
}

bool solve() {
  int N;
  if (scanf("%d\n", &N) != 1) return false;
  scanf("%s\n", tab);
  inputs.clear();
  mnext.clear();
  now.clear();
  base = tab;
  REP(i, N) {
	  scanf("%s\n", tab);
	  inputs.PB(make_pair(strlen(tab), string(tab)));
  }
  sort(inputs.begin(), inputs.end(), greater<pair<int, string> >());
  now.PB(-1);
  now.PB(-2);
  REP(i, inputs.size()) {
	  if (now.size() == 3) {
		  // We're in the optional stage.
		  if (Fits(now[2], i)) {
			  // Extend optional.
			  mnext[now[2]] = i;
			  now[2] = i;
		  } else {
			  // Fit into first and second.
			  bool first = Fits(now[0], i);
			  bool second = Fits(now[1], i);
			  if (!first && !second) {
				  printf("Impossible\n"); return true;
			  }
			  if (first) {
				  mnext[now[0]] = i;
				  mnext[now[1]] = bot;
				  int rem = now[2];
				  now.clear();
				  now.PB(i);
				  now.PB(rem);
			  } else {  // second
				  mnext[now[0]] = bot;
				  mnext[now[1]] = i;
				  int rem = now[2];
				  now.clear();
				  now.PB(i);
				  now.PB(rem);
			  }
		  }
	  } else {  // We're in the forced stage.
		  bool first = Fits(now[0], i);
		  bool second = Fits(now[1], i);
		  if (!first && !second) {
			  printf("Impossible\n"); return true;
		  }
		  if (first && second) {
			  now.PB(i);
			  bot = i;
		  } else {
			  if (first) {
				  mnext[now[0]] = i;
				  now[0] = i;
			  } else {  // second
				  mnext[now[1]] = i;
				  now[1] = i;
			  }
		  }
	  }
  }
  if (now.size() == 3) {
	  mnext[now[0]] = bot;  // Let's slap it anywhere.
  } 
  vector<int> f;
  vector<int> s;
  int sf = -2;
  while (mnext.find(sf) != mnext.end()) {
	  f.PB(mnext[sf]);
	  sf = mnext[sf];
  }
  sf = -1;
  while (mnext.find(sf) != mnext.end()) {
	  s.PB(mnext[sf]);
	  sf = mnext[sf];
  }
  printf("%u %u\n", f.size(), s.size());
  reverse(f.begin(), f.end());
  reverse(s.begin(), s.end());
  REP(i, f.size()) printf("%s\n", val(f[i]).c_str());
  REP(i, s.size()) printf("%s\n", val(s[i]).c_str());
  return true;
}

int main() { while (solve()); }

