#include "validate.h"
#include <algorithm>
#include <string>
#include <vector>
using namespace std;

#define MAX_CASES 1

main() {
  for (int prob = 1; ; prob++) {
    ReadChar();
    bool done = in->eof();
    UnReadChar();
    if (done) break;
    if (prob > MAX_CASES) ShowError("Too many cases.");

    int N = ExpectInteger(1, 4000);
    ExpectEndOfLine();
    vector<string> v;
    for (int i = 0; i <= N; i++) {
      v.push_back(ExpectStringOf("ACM"));
      if (v.back().size() > 4000) ShowError("String too long.");
      ExpectEndOfLine();
    }
    sort(v.begin(), v.end());
    for (int i = 1; i < v.size(); i++) {
      if (v[i-1] == v[i]) ShowError("Equivalent strings: " + v[i]);
    }
  }
  ExpectEndOfFile();
  PrintSuccess();
}
