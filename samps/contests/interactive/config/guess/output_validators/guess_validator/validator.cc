#include <utility>
#include <string>
#include <cassert>
#include <cstring>
#include <cmath>
#include <signal.h>
#include "streamcorr.h"

using namespace std;

bool author_messages = false;

void check_case() {
	string line;
	/* Get test mode description from judge input file */
    assert(getline(judge_in, line));

    int value = -1;
    if (sscanf(line.c_str(), "fixed %d", &value) != 1) {
        if (sscanf(line.c_str(), "random %d", &value) == 1) {
            srandom(value);
            value = 1 + random() % 1000;
        } else if (sscanf(line.c_str(), "adaptive %d", &value) == 1) {
            srandom(value);
            value = -1;
        } else {
            assert(!"unknown input instructions");
        }
    }
    if (value == -1) {
        report_feedback(judgemessage, "I'm not committing to a value, will adaptively choose worst one\n");
    } else {
        report_feedback(judgemessage, "I'm thinking of %d\n", value);
    }

    int sol_lo = 1, sol_hi = 1000;
    int guesses = 0;
    for (int guesses = 0; guesses < 10; ++guesses) {
        int guess;
        if (scanf("%d", &guess) != 1) {
            report_error("Guess %d: couldn't read an integer\n", guesses+1);
        }
        if (guess < 1 || guess > 1000) {
            report_error("Guess %d is out of range: %d\n", guesses+1, guess);
        }
        report_feedback(judgemessage, "Guess %d is %d\n", guesses+1, guess);
        int diff;
        if (value == -1) {
            if (guess == sol_lo && sol_lo == sol_hi) {
                diff = 0;
            } else if (guess-1 - sol_lo > sol_hi - (guess+1)) {
                diff = -1;
            } else if (guess-1 - sol_lo < sol_hi - (guess+1)) {
                diff = 1;
            } else {
                diff = 2*(random() %2) - 1;
            }
        } else {
            diff = value - guess;
        }
        if (!diff) {
            printf("correct\n");
            fflush(stdout);
            return;
        } else if (diff < 0) {
            printf("lower\n");
            sol_hi = guess-1;
            fflush(stdout);
        } else {
            printf("higher\n");
            sol_lo = guess+1;
            fflush(stdout);
        }
    }
    report_error("Didn't get to correct answer in 10 guesses\n");

	return;
}

int main(int argc, char **argv) {
  signal(SIGPIPE, SIG_IGN);

  init_io(argc, argv);

  string line;
  assert(getline(judge_in, line));
  int num_cases = stoi(line);
  printf("%d\n", num_cases);
  fflush(stdout);

  while (num_cases--) {
      check_case();
  }

  /* Check for trailing output. */
  char trash[200];
  if (scanf("%100s", trash) == 1) {
      report_error("Trailing output\n");
  }

  /* Yay! */
  accept();
}
