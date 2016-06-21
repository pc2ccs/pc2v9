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

    int W = ExpectInteger(2, 10000);
    ExpectChar(' ');
    ExpectInteger(1, W-1);
    ExpectChar(' ');
    ExpectInteger(1, 1000);
    ExpectEndOfLine();
  }
  ExpectEndOfFile();
  PrintSuccess();
}
