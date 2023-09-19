/* Version of corr.h using streams for input (much nicer handling of
 * strings, no need to specify some max-length for them).
 *
 * Still uses cstdio for output (because varargs are very convenient),
 * the mixing is a bit ugly.
 */
#ifndef H_STREAMCORR_H
#define H_STREAMCORR_H

#include <cstdarg>
#include <cstdio>
#include <cstdlib>
#include <iostream>
#include <fstream>

const int EXIT_AC = 42;
const int EXIT_WA = 43;

#define USAGE "%s: judge_in judge_ans feedback_dir < author_out\n"

std::ifstream judge_in, judge_ans;
std::istream author_out(std::cin.rdbuf());
char *feedbackdir;
FILE *feedback;

const char *judgemessage = "judgemessage.txt";
const char *score = "score.txt";
const char *authormessage = "authormessage.txt";
const char *judgeerror = "judgeerror.txt";

void vreport_feedback(const char *category, const char *msg, va_list pvar) {
  char file[1000];
  sprintf(file, "%s/%s", feedbackdir, category);
  FILE *f = fopen(file, "a");
  assert(f);
  vfprintf(f, msg, pvar);
  fclose(f);
}

void report_feedback(const char *category, const char *msg, ...) {
  va_list pvar;
  va_start(pvar, msg);
  vreport_feedback(category, msg, pvar);
}

void report_error(const char *err, ...) {
   va_list pvar;
   va_start(pvar, err);
   vreport_feedback(judgemessage, err, pvar);
   exit(EXIT_WA);
}

void judge_error(const char *err, ...) {
  va_list pvar;
  va_start(pvar, err);
  vreport_feedback(judgeerror, err, pvar);
  assert(0);
}

void accept() {
  exit(EXIT_AC);
}

void accept_with_score(double scorevalue) {
  report_feedback(score, "%.9le", scorevalue);
  exit(EXIT_AC);
}

void init_io(int argc, char **argv) {

   if(argc < 4) {
     fprintf(stderr, USAGE, argv[0]);
     judge_error("Usage: %s judgein judgeans feedbackdir [opts] < userout", argv[0]);
   }

   judge_in.open(argv[1], std::ios_base::in);
   if (judge_in.fail()) {
     judge_error("%s: failed to open %s\n", argv[0], argv[1]);
   }

   judge_ans.open(argv[2], std::ios_base::in);
   if (judge_ans.fail()) {
     judge_error("%s: failed to open %s\n", argv[0], argv[2]);
   }

   feedbackdir = argv[3];
   author_out.rdbuf(std::cin.rdbuf());
}

#endif /* H_STREAMCORR_H */
