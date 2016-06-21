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

    ExpectInteger(1, 20);
    ExpectEndOfLine();
    double a = ExpectFloat(6, 0.0000009, 1.0);
    ExpectChar(' ');
    double b = ExpectFloat(6, 0.0000009, 1.0);
    ExpectChar(' ');
    double c = ExpectFloat(6, 0.0000009, 1.0);
    ExpectChar(' ');
    double d = ExpectFloat(6, 0.0000009, 1.0);
    if (fabs(1.0-a-b-c-d) > 1e-9) ShowError("Probabilities don't sum to 1.");
    ExpectEndOfLine();
  }
  ExpectEndOfFile();
  PrintSuccess();
}
