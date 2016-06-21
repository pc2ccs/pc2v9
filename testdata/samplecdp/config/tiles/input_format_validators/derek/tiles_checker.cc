#include "validate.h"
using namespace std;

#define MAX_CASES 1
#define MAX 500000

main() {
  for (int prob = 1; ; prob++) {
    ReadChar();
    bool done = in->eof();
    UnReadChar();
    if (done) break;
    if (prob > MAX_CASES) ShowError("Too many cases.");

	int n = ExpectInteger(1, 500);
    ExpectEndOfLine();
	for (int i = 0; i < n; ++i) {
		int lo = ExpectInteger(1, MAX);
		ExpectChar(' ');
		ExpectInteger(lo, MAX);
		ExpectEndOfLine();
	}
  }
  ExpectEndOfFile();
  PrintSuccess();
}
