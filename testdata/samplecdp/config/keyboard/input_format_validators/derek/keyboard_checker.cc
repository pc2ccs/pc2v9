#include "validate.h"
#include <vector>
using namespace std;

#define MAX_CASES 1

int X, Y;
char g[51][51];

void doit(int x, int y, char ch) {
  if (x < 0 || x >= X || y < 0 || y >= Y || g[y][x] != ch) return;
  g[y][x] = '.';
  doit(x+1, y, ch);
  doit(x-1, y, ch);
  doit(x, y+1, ch);
  doit(x, y-1, ch);
}

main() {
  for (int prob = 1; ; prob++) {
    ReadChar();
    bool done = in->eof();
    UnReadChar();
    if (done) break;
    if (prob > MAX_CASES) ShowError("Too many cases.");

    Y = ExpectInteger(1, 50);
    ExpectChar(' ');
    X = ExpectInteger(1, 50);
    ExpectEndOfLine();
    for (int y = 0; y < Y; y++) {
      for (int x = 0; x < X; x++) {
        g[y][x] = ExpectOneOfChars("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-*");
      }
      ExpectEndOfLine();
    }
    vector<bool> seen(256);
    for (int y = 0; y < Y; y++)
    for (int x = 0; x < X; x++) if (g[y][x] != '.') {
      if (seen[g[y][x]]) ShowError("Duplicate key " + ToString(g[y][x]) + ".");
      seen[g[y][x]] = true;
      doit(x, y, g[y][x]);
    }
    string s = ExpectStringOf("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-");
    if (s.size() > 10000) ShowError("String too long.");
    ExpectEndOfLine();
  }
  ExpectEndOfFile();
  PrintSuccess();
}
