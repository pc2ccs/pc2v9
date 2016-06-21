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
    ExpectEndOfLine();
    for (int i = 0; i < N; i++) {
      ExpectInteger(1, 1000000000);
      ExpectChar(' ');
      int V1 = ExpectInteger(1, 999999999);
      ExpectChar(' ');
      ExpectInteger(V1+1, 1000000000);
      ExpectChar(' ');
      ExpectInteger(1, 1000000000);
      ExpectChar(' ');
      ExpectInteger(1, 1000000000);
      ExpectEndOfLine();
    }
  }
  ExpectEndOfFile();
  PrintSuccess();
}
