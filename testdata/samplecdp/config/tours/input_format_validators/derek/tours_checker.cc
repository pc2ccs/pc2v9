#include "validate.h"
using namespace std;

#define MAX_CASES 1

int comp[2001];
int seen[2001][2001];

main() {
  for (int prob = 1; ; prob++) {
    ReadChar();
    bool done = in->eof();
    UnReadChar();
    if (done) break;
    if (prob > MAX_CASES) ShowError("Too many cases.");

    int n = ExpectInteger(1, 2000);
    ExpectChar(' ');
    int m = ExpectInteger(1, 2000);
    ExpectEndOfLine();

    bool loop = false;
    for (int i = 1; i <= n; i++) comp[i] = i;
    for (int i = 0; i < m; i++) {
      int a = ExpectInteger(1, n);
      ExpectChar(' ');
      int b = ExpectInteger(a+1, n);
      ExpectEndOfLine();
      if (seen[a][b]) ShowError("Duplicate edge.");
      seen[a][b] = seen[b][a] = true;
      int ac = comp[a], bc = comp[b];
      if (ac == bc) loop = true;
      for (int j = 1; j <= n; j++) if (comp[j] == bc) comp[j] = ac;
    }
    if (!loop) ShowError("No round tours.");
  }
  ExpectEndOfFile();
  PrintSuccess();
}
