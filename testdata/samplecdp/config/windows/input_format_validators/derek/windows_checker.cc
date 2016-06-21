#include "validate.h"
using namespace std;

#define MAX_CASES 256

main() {
  int X = ExpectInteger(1, 1000000000);
  ExpectChar(' ');
  int Y = ExpectInteger(1, 1000000000);
  ExpectEndOfLine();

  for (int prob = 1; ; prob++) {
    ReadChar();
    bool done = in->eof();
    UnReadChar();
    if (done) break;
    if (prob > MAX_CASES) ShowError("Too many cases.");

    string cmd = ExpectString(" ");
    ExpectChar(' ');
    if (cmd == "OPEN") {
      ExpectInteger(0, X-1);
      ExpectChar(' ');
      ExpectInteger(0, Y-1);
      ExpectChar(' ');
      ExpectInteger(1, 1000000000);
      ExpectChar(' ');
      ExpectInteger(1, 1000000000);
      ExpectEndOfLine();
    } else if (cmd == "CLOSE") {
      ExpectInteger(0, X-1);
      ExpectChar(' ');
      ExpectInteger(0, Y-1);
      ExpectEndOfLine();
    } else if (cmd == "RESIZE") {
      ExpectInteger(0, X-1);
      ExpectChar(' ');
      ExpectInteger(0, Y-1);
      ExpectChar(' ');
      ExpectInteger(1, 1000000000);
      ExpectChar(' ');
      ExpectInteger(1, 1000000000);
      ExpectEndOfLine();
    } else if (cmd == "MOVE") {
      ExpectInteger(0, X-1);
      ExpectChar(' ');
      ExpectInteger(0, Y-1);
      ExpectChar(' ');
      int dx = ExpectInteger(-1000000000, 1000000000);
      ExpectChar(' ');
      int dy = ExpectInteger(-1000000000, 1000000000);
      if (dx != 0 && dy != 0) ShowError("dx and dy both non-zero.");
      ExpectEndOfLine();
    } else {
      ShowError("Invalid cmd: " + cmd + ".");
    }
  }
  ExpectEndOfFile();
  PrintSuccess();
}
