#include <cmath>
#include <cstdlib>
#include <iostream>
#include <sstream>
#include <cstdio>
using namespace std;

const string SPACES = " \t\n\r\x0D\x0A";
const string DIGITS = "0123456789";
const string UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
const string LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
const string LETTERS = UPPERCASE_LETTERS + LOWERCASE_LETTERS;

int lineno = 1, charno = 0, lastcharno = 0;

// Start processing an input stream.  We can only handle one at a time.
istream* in = &cin;
void SetInput(istream* new_in) {
  in = new_in;
  lineno = 1; charno = 0; lastcharno = 0;
}

// Fatal function that displays an error then quits.
void ShowError(string s) {
  if (charno == 0)
    cerr << "Error on line " << lineno-1 << ", char " << lastcharno;
  else
    cerr << "Error on line " << lineno << ", char " << charno;
  cerr << ": " << s << endl;
  exit(1);
}

// Fatal function that declares all input valid.
void PrintSuccess() {
  cerr << "No problems found." << endl;
  exit(42);
}

bool read_char = false;
char ReadChar() {
  char c;
  in->get(c);
  if (in->eof()) return 0;
  if (c == '\n') {
    lineno++;
    lastcharno = charno;
    charno = 0;
  } else {
    charno++;
  }
  read_char = true;
  return c;
}

void UnReadChar() {
  if (!read_char) {
    cerr << "ERROR: attempt to unread twice in a row!" << endl;
    exit(1);
  }
  if (in->eof()) return;
  read_char = false;
  if (charno == 0) {
    charno = lastcharno;
    lineno--;
  } else {
    charno--;
  }
  in->unget();
}

template<typename T> string ToString(T x) {
  ostringstream sout;
  sout << x;
  return sout.str();
}

// Expects a single character, which should be ch.
void ExpectChar(char ch) {
  char c = ReadChar();
  if (c != ch) ShowError("Expected '" + ToString(ch) + "'.");
}

char ExpectOneOfChars(string s) {
  char c = ReadChar();
  if (s.find(c) == -1) ShowError("Expected one of \"" + s + "\".");
  return c;
}

void ExpectEndOfLine() {
  /*
  // handle both DOS and Unix-style newlines.
  char c = ReadChar();
  if (c != '\x0A' && c != '\x0D') ShowError("Expected end of line.");
  char c2 = ReadChar();
  if (c == c2 || c2 != '\x0A' && c2 != '\x0D') UnReadChar();
  */
  // Allow Unix-style newlines only.
  char c = ReadChar();
  if (c != '\n') ShowError("Expected end of line.");
}

void ExpectEndOfFile() {
	char c = ReadChar();
	if (c != 0) ShowError("Expected end of file, got " + ToString(c));
}

// Expects an integer between min and max.  Returns the number read.
// (Note: Cannot handle -2^63.)
long long ExpectInteger(long long min, long long max) {
  long long n = 0;
  bool neg = false;
  string s;
  char c = ReadChar();
  if (c == '-') {neg = true; s += c; c = ReadChar();}
  while (isdigit(c)) {
    s += c;
    n = 10*n + (c-'0');
    if (n < 0 || s.size() > 19) ShowError("Integer overflows 64 bits.");
    c = ReadChar();
  }
  if (s.size() == 0) ShowError("Expected integer.");
  UnReadChar();
  if (s != "0" && (s[0] == '0' || s[0] == '-' && s[1] == '0'))
    ShowError("Integer has leading zero.");
  if (neg) n = -n;
  if (s != ToString(n)) ShowError("Oddly formatted integer: " + s);
  if (n < min) ShowError("Integer below " + ToString(min));
  if (n > max) ShowError("Integer above " + ToString(max));
  return n;
}

// Expects a floating-point number in a specific simple format.  At most
// dec_digits digits may appear after the decimal point.  No exponential
// notation is allowed.
double ExpectFloat(int dec_digits, double min, double max) {
  double n = 0, dec = 1;
  bool neg = false, in_dec = false;
  string s;
  char c = ReadChar();
  if (c == '-') {neg = true; s += c; c = ReadChar();}
  while (isdigit(c) || c == '.' && !in_dec) {
    s += c;
    if (c == '.') {
      in_dec = true;
    } else {
      if (in_dec) {
        dec *= 10;
        if (--dec_digits < 0) ShowError("Too many digits after decimal.");
      }
      n = 10*n + (c-'0');
    }
    c = ReadChar();
  }
  if (c == 'e' || c == 'E')
    ShowError("Exponential notation for floats not allowed.");
  if (s.size() == 0) ShowError("Expected float.");
  UnReadChar();
  if (s[s.size()-1] == '.') ShowError("No digits after decimal.");
  if (s[0] == '0' && s.size() > 1 && isdigit(s[1]) ||
      s[0] == '-' && s[1] == '0' && s.size() > 2 && isdigit(s[2]))
    ShowError("Float has leading zero.");
  n /= dec;
  if (neg) n = -n;
  if (n < min-1e-10) ShowError("Float below " + ToString(min));
  if (n > max+1e-10) ShowError("Float above " + ToString(max));
  return n;
}

// Expects a string terminated by one of the given characters.
// Returns that string.
string ExpectString(string terms, bool nonempty = true) {
  string s;
  for (char c = ReadChar(); terms.find(c) == -1; c = ReadChar()) s += c;
  UnReadChar();
  if (nonempty && s.size() == 0) ShowError("Expected non-empty string.");
  return s;
}

// Expects a non-empty string composed of the given characters.
// Returns that string.
string ExpectStringOf(string allowed, bool nonempty = true) {
  string s;
  for (char c = ReadChar(); allowed.find(c) != -1; c = ReadChar()) s += c;
  UnReadChar();
  if (nonempty && s.size() == 0) ShowError("Expected non-empty string.");
  return s;
}

// Tests whether the given floating-point answer is too close to rounding after
// the given # of digits of precision.  With the default precision (0.1), this
// test will fail if the next digit is 4 or 5.
void TestForRounding(double v, int digits, double prec = 0.1) {
  double iv = v * pow(10.0, digits);
  iv -= trunc(iv) + 0.5;
  if (fabs(iv - round(iv)) < prec) {
    char buf[100];
    string digs = "%0." + ToString(digits+3) + "lf";
    sprintf(buf, digs.c_str(), v);
    ShowError("Value " + string(buf) + " is too close to rounding.");
  }
}
