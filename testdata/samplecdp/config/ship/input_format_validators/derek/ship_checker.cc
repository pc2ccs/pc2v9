#include "validate.h"
using namespace std;

#define MAX_CASES 1

main() {
  for (int prob = 1; ; prob++) {
    ReadChar();
    bool done = in->eof();
    UnReadChar();
    if (done) break;
    if (prob > MAX_CASES) ShowError("Too many cases.");

    int N = ExpectInteger(1, 100000);
    ExpectChar(' ');
    ExpectInteger(1, 1000);
    ExpectChar(' ');
    ExpectInteger(1, 100);
    ExpectChar(' ');
    ExpectInteger(1, 100);
    ExpectChar(' ');
    int T1 = ExpectInteger(0, 999999);
    ExpectChar(' ');
    int T2 = ExpectInteger(T1+1, 1000000);
    ExpectEndOfLine();
    int tot = 0;
    for (int i = 0; i < N; i++) {
      char ch = ExpectOneOfChars("EW");
      ExpectChar(' ');
      int M = ExpectInteger(0, 100000);
      tot += M;
      int last = -1000000000;
      for (int j = 0; j < M; j++) {
        ExpectChar(' ');
        int L = ExpectInteger(1, 1000);
        ExpectChar(' ');
        int P = ExpectInteger(-1000000, 1000000);
        if (ch == 'E') {
          if (P-L <= last) ShowError("Overlapping ship.");
          last = P;
        } else {
          if (P <= last) ShowError("Overlapping ship.");
          last = P+L;
        }
      }
      ExpectEndOfLine();
    }
    if (tot < 1) ShowError("No ships.");
    if (tot > 100000) ShowError("Too many ships.");
  }
  ExpectEndOfFile();
  PrintSuccess();
}
