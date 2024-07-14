#include "validate.h"
#include <vector>
#include <bitset>

using namespace std;

constexpr int MAXN = 10000;

//int n, iso, teamiso; 
int n, w, iso, teamiso;  // int reps of iso, teamiso
string isos, teamisos;   // string reps of iso, teamiso
vector<string> s;

void read_nw(istream &in) {
  in >> n >> w;
}

void read_iso(istream &ans) {
  ans >> isos; // read first line as a string since it could be "Infinity"
  for (auto& c:isos) c = tolower(c); // Just to play it safe!
}

void read_teamiso(istream &team, feedback_function feedback) {
  if (!(team >> teamisos)) {
    feedback("EOF");
  }
  for (auto& c:teamisos) c = tolower(c);

  // Should probably do the following more carefully than merely comparing as strings

  if (teamisos != isos) {
      wrong_answer("judge answer = %s but submission output = %s\n",
                   isos.c_str(), teamisos.c_str());
  }

  // isolations agree--either both Infinity or both identical integers
  //for (int i = 0; i < isos.length(); i++) isos[i] = tolower(isos[i]);
  if (isos == "infinity") { // we still need to check team answer for trailing output
    string trash;
    if (author_out >> trash)
      wrong_answer("Trailing output\n");
    accept();
  } else {
    iso = stoi(isos);
    teamiso = stoi(teamisos);
  } 
}

void read_teamschedule(istream &team, feedback_function feedback) {
  string t;
//  for (int i = 0; i < 52; i++) {
  for (int i = 0; i < w; i++) {
    if (!(team >> t)) {
      feedback("insufficient input");
    }
    if (t.length() != n) {
      feedback("wrong length");
    }
    s.push_back(t);
  }
}


template <int N>
void check_teamschedule_bitset_inner(feedback_function feedback) {
//  vector<bitset<N>> B(52);
  vector<bitset<N>> B(w);

//  for (int i = 0; i < 52; i++) {
  for (int i = 0; i < w; i++) {
    for (int j = 0; j < n; ++j) {
      B[i].set(j, s[i][j] == '1');
    }
  }

//  for (int i = 0; i + iso <= 52; ++i) {
  for (int i = 0; i + iso <= w; ++i) {
    for (int j = 0; j < n; ++j) {
      /* AndA is the bitwise and of all weeks where (j,1) comes.
       * If this and has any bit set at the end, it means that there is a subteam (k,2)
       * that never meets (j,1).
       *
       * OrA is the bitwise or of all weeks where (j,1) comes.
       * If at the end there is a zero bit somewhere, it means that there is a subteam (k,1)
       * that never meets (j,1).
       *
       * Similarly for AndB and OrB, which verify that team (j,2) meets everyone. */
      bitset<N> AndA, OrA, AndB, OrB;
      AndA.set(); AndB.set();
      for (int k = 0; k < iso; ++k) {
        if (B[i+k][j]) {
          AndA &= B[i+k];
          OrA |= B[i+k];
        } else {
          AndB &= B[i+k];
          OrB |= B[i+k];
        }
      }

      if (AndA.count() != 1 || AndB.count() != 0 || OrA.count() != n || OrB.count() != n-1) {
        feedback("missing some pairs");
      }
    } 
  }
}

void check_teamschedule_bitset(feedback_function feedback) {
  /* bitsets need to have size known at compile time. In order to avoid testing
   * small test cases on a full sized bitset of size 10000, we hard code a couple 
   * of options for the bitset size. */
  if (n <= 64) check_teamschedule_bitset_inner<64>(feedback);
  else if (n <= 640) check_teamschedule_bitset_inner<640>(feedback);
  else check_teamschedule_bitset_inner<MAXN>(feedback);  
}

int main(int argc, char **argv) {
  init_io(argc, argv);

//  read_n(judge_in);
  read_nw(judge_in);
  read_iso(judge_ans);
  read_teamiso(author_out, wrong_answer);
  read_teamschedule(author_out,wrong_answer);
  check_teamschedule_bitset(wrong_answer);

  /* Check for trailing output. */
  string trash;
  if (author_out >> trash)
    wrong_answer("Trailing output\n");

  accept();
}

