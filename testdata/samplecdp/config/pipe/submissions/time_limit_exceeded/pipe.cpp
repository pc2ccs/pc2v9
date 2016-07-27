#include <iostream>
#include <vector>

using namespace std;

typedef vector<int> vi;

// is there a binary tree of maximal depth maxd whose leaves have depths <= ds, in this order, where ds is an increasing sequence?
bool check(int maxd, const vi ds) {
  vi ix; // 0-1 sequence: path to current leaf, 0 = left, 1 = right
  for (vi::const_iterator it = ds.begin() ; it != ds.end() ; ++it) {
    ix.resize(min(maxd, *it)); // go up to level *it
    while (!ix.empty() && ix.back()) // go to next node at that level: increment binary counter
      ix.pop_back();
    if (ix.empty()) // tree is full
      return false;
    ix.back() = 1;
  }
  return true;
}

int main() {
  int t;
  cin >> t;
  while (t--) {
    int l, v1, v2, tol, s;
    cin >> l >> v1 >> v2 >> tol >> s;
    vi ds; // maximal depths of the intervals, which are the leaves of a tree
    for (int v=v2-tol ; v>v1 ; v-=tol) // backwards to make the boundaries as small as possible
      ds.push_back((l / s) / v);
    // v = velocity that decides between two intervals
    // v * s = location of tapping
    // l / (v * s) = maximal number of tappings until this tapping takes place
    int maxtap = ds.size(); // an upper bound on the depth of the tree
    // binary search for the minimal depth of the tree
    int lo = 0;
    int hi = maxtap + 1; // add 1 to see if it cannot be done at all
    while (lo < hi) {
      int med = (lo + hi) / 2;
      if (check(med, ds))
        hi = med;
      else
        lo = med + 1;
    }
    if (lo > maxtap)
      cout << "impossible" << endl;
    else
      cout << lo << endl;
  }
  return 0;
}

