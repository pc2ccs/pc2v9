#include <cassert>
#include <climits>
#include <cstdio>
#include <iostream>
#include <sstream>
#include <vector>

using namespace std;

typedef long long ll;
typedef vector<ll> vll;

// minimum and maximum number of test cases
const ll CMIN = 1;
const ll CMAX = 100;

// minimum and maximum number
const ll NMIN = 1;
const ll NMAX = 1000000000;

vll readline(ll lo, ll hi) {
  string line;
  assert(cin.good());
  getline(cin, line);
  assert(!cin.fail());
  stringstream lstream(line);
  assert(!lstream.fail());
  lstream >> noskipws;
  assert(!lstream.fail());
  vll result;
  while (lstream.peek() != EOF) {
    string word;
    assert(lstream.good());
    lstream >> word;
    assert(!lstream.fail());
    assert(word.size() <= 10);
    stringstream wstream(word);
    assert(wstream.good());
    ll x = LLONG_MIN;
    wstream >> x;
    assert(!wstream.fail());
    assert(wstream.eof());
    assert(lo <= x && x <= hi);
    result.push_back(x);
    if (lstream.eof()) break;
    char ch;
    assert(lstream.good());
    lstream >> ch;
    assert(lstream.good());
    assert(ch == ' ');
  }
  assert(!lstream.fail());
  assert(lstream.eof());
  return result;
}

int main() {
  vll xs = readline(CMIN, CMAX);
  assert(xs.size() == 1);
  for (ll i=0 ; i<xs[0] ; i++) {
    vll ys = readline(NMIN, NMAX);
    assert(ys.size() == 5);
    assert(ys[1] < ys[2]);
  }
  assert(cin.peek() == EOF);
  assert(!cin.fail());
  return 42;
}

