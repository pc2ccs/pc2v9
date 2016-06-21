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

    ExpectInteger(1, 1000);
    ExpectChar(' ');
    ExpectInteger(0, 1000);
    ExpectChar(' ');
    ExpectInteger(0, 1000);
    ExpectChar(' ');
    ExpectInteger(0, 1000);
    ExpectChar(' ');
    ExpectInteger(0, 1000);
    ExpectChar(' ');
    ExpectInteger(1, 1000000);
    ExpectEndOfLine();
  }
  ExpectEndOfFile();
  PrintSuccess();
}
