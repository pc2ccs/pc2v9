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

    int N = ExpectInteger(1, 100);
    ExpectChar(' ');
    int K = ExpectInteger(1, 100);
    ExpectEndOfLine();
    for (int i = N; i >= 1; i--) {
      for (int j = 0; j < i; j++) {
        ExpectInteger(0, 1000000);
        if (j+1 < i) ExpectChar(' ');
      }
      ExpectEndOfLine();
    }
  }
  ExpectEndOfFile();
  PrintSuccess();
}
