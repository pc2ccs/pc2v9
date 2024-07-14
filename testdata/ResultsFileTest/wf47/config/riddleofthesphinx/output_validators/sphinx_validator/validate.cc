#include <utility>
#include <string>
#include <cassert>
#include <cstring>
#include <cmath>
#include <array>
#include <vector>
#include "validate.h"

using namespace std;
typedef long long int64;
const int MAX_COEFF = 10;
const int MAX_ANSWER = 100000;
const int MAX_VAL = MAX_ANSWER / MAX_COEFF; // max possible #legs of one creature

ostringstream interaction;

bool valid_coeff(int c) {
    return 0 <= c && c <= MAX_COEFF;
}


int64 inner_product(array<int64, 3> u, array<int64, 3> v) {
    return u[0]*v[0] + u[1]*v[1] + u[2]*v[2];
}

template <class Z> Z euclid(Z a, Z b, Z &x, Z &y) {
  if (b) {  Z d = euclid(b, a % b, y, x);
            return y -= a/b * x, d;  }
  return x = 1, y = 0, a;
}


// Given an interaction transcript, check if accepted.
// Checks that:
// 1. Solution given is valid.
// 2. There are no other valid solutions.
// If these two conditions do not hold, then reports Wrong Answer.
void check_sol(const array<array<int64, 3>, 5> &questions,
               const array<int64, 5> &answers,
               const array<int64, 3> &team_sol) {

    // Step 1. Check that team solution is *a* valid solution for the given questions and answers
    int team_errs = 0;
    for (int i = 0; i < 5; ++i) {
        int64 team_ans = inner_product(questions[i], team_sol);
        team_errs += (team_ans != answers[i]);
    }
    if (team_errs > 1) {
        wrong_answer("Solution provided by team is invalid, uses %d > 1 lies.\nInteraction:\n%s",
                     team_errs, interaction.str().c_str());
    }

    // Step 2 (adaptive part).  Check that there are no other solutions.

    auto check_sol = [&team_sol](int64 x0, int64 x1, int64 x2, int bad_ans) {
        if (x0 != team_sol[0] || x1 != team_sol[1] || x2 != team_sol[2]) {
            wrong_answer("A different solution (%lld %lld %lld) exists than the one claimed by the solution (with reply %d a lie).\nInteraction:\n%s",
                         x0, x1, x2, bad_ans+1, interaction.str().c_str());
        }
    };
    
    auto check_x0 = [&](int64 x0, int bad_ans) {
        // Check for solutions with a candidate x0 value.
        vector<vector<int64>> Ab;
        for (int i = 0; i < 5; ++i) {
            if (i != bad_ans) {
                int64 rem = answers[i] - questions[i][0]*x0;
                if (rem < 0) return;
                vector<int64> nrow = {questions[i][1], questions[i][2], rem};
                assert(nrow[0] == questions[i][1]);
                if (nrow[0] == 0 && nrow[1] == 0) {
                    if (rem != 0) return;
                    continue;
                }
                bool indep = true;
                for (auto &row: Ab) {
                    if (row[0]*nrow[1] - row[1]*nrow[0] == 0) {
                        // linearly dependent with another row, check if consistent
                        if (row[2] * (nrow[0] + nrow[1])  != nrow[2] * (row[0] + row[1]))
                            return;
                        indep = false;
                    }
                }
                if (indep) Ab.push_back(nrow);
            }
        }

        if (Ab.size() >= 2) { // full rank, unique solution
            int64 det = Ab[0][0]*Ab[1][1] - Ab[0][1]*Ab[1][0];
            int64 x1 =  Ab[1][1]*Ab[0][2] - Ab[0][1]*Ab[1][2];
            int64 x2 = -Ab[1][0]*Ab[0][2] + Ab[0][0]*Ab[1][2];
            if (x1 % det || x2 % det) return;
            if (x1 < 0 || x2 < 0) return;
            x1 /= det;
            x2 /= det;
            for (auto &row: Ab)
                if (row[0] * x1 + row[1] * x2 != row[2])
                    return;
            check_sol(x0, x1, x2, bad_ans);
            return;
        } else if (Ab.size() == 1) { // rank 1
            // non-neg integer solutions to a*x1 + b*x2 = c
            int64 a = Ab[0][0], b = Ab[0][1], c = Ab[0][2];
            assert(a >= 0 && b >= 0 && c >= 0);
            assert(a > 0 || b > 0);
            
            int64 x1, x2;
            int64 d = euclid(a, b, x1, x2);
            if (c % d) return; // no integer solutions
            a /= d;
            b /= d;
            c /= d;
            x1 *= c;
            x2 *= c;
          
            if (a == 0) { // x1 unconstrained (this check not needed but gives more informative error message)
                wrong_answer("Infinitely many solutions of form (%lld, *, %lld) with reply %d a lie.\nInteraction:\n%s",
                             x0, x2, bad_ans+1, interaction.str().c_str());
            }
            if (b == 0) { // x2 unconstrained (this check *is* needed, we assume b > 0 below)
                wrong_answer("Infinitely many solutions of form (%lld, %lld, *) with reply %d a lie.\nInteraction:\n%s",
                             x0, x1, bad_ans+1, interaction.str().c_str());
            }
            // move to solution with smallest non-neg x1 value
            int64 steps = x1 < 0 ? (-x1 + b - 1) / b : -(x1 / b);
            x1 += b*steps;
            x2 -= a*steps;
            if (x2 < 0) return; // no non-neg solutions

            check_sol(x0, x1, x2, bad_ans);
            if (x2 - a >= 0) // enough to check one other solution
                check_sol(x0, x1 + b, x2 - a, bad_ans);
            return;
        } else { // rank 0, no non-trivial equations
            assert(Ab.size() == 0);
            wrong_answer("Infinitely many solutions of form (%lld, *, *) with reply %d a lie.\nInteraction:\n%s",
                         x0, bad_ans+1, interaction.str().c_str());
        }
    };
    
    for (int bad_ans = 0; bad_ans < 5; ++bad_ans)
        for (int64 x0 = 0; x0 <= MAX_VAL; ++x0)
            check_x0(x0, bad_ans);
}

void check_fixed() {
    array<int64, 3> ans, lie;
    int64 lie_q, lie_diff;
    judge_in >> ans[0] >> ans[1] >> ans[2];
    judge_in >> lie_q >> lie_diff;
    judge_in >> lie[0] >> lie[1] >> lie[2];

    array<array<int64, 3>, 5> questions;
    array<int64, 5> answers;
    array<int64, 3> team_sol;
    
    for (int i = 0; i < 5; ++i) {
        if (!(author_out >> questions[i][0] >> questions[i][1] >> questions[i][2])) {
            wrong_answer("Question %d: failed to read three integers.\nInteraction:\n%s",
                         i+1, interaction.str().c_str());
        }
        interaction << "> " << questions[i][0] << " " << questions[i][1] << " " << questions[i][2] << "\n";
        for (int j = 0; j < 3; ++j)
            if (!valid_coeff(questions[i][j]))
                wrong_answer("Question %d coeff %d invalid.\nInteraction:\n%s",
                             i+1, j+1, interaction.str().c_str());
        int64 reply = inner_product(ans, questions[i]);
        if (i == lie_q) {
            reply = inner_product(lie, questions[i]) + lie_diff;
            reply %= MAX_ANSWER + 1;
            if (reply < 0) reply += MAX_ANSWER+1;
            assert(0 <= reply && reply <= MAX_ANSWER);
        }
        cout << reply << endl;
        interaction << "< " << reply << "\n";
        answers[i] = reply;
    }
    
    if (!(author_out >> team_sol[0] >> team_sol[1] >> team_sol[2])) {
        wrong_answer("Failed to read three integers giving final answer.\nInteraction:\n%s",
                     interaction.str().c_str());
    }
    interaction << "> " << team_sol[0] << " " << team_sol[1] << " " << team_sol[2] << "\n";
    check_sol(questions, answers, team_sol);
}

void check_case() {
    string mode;
    assert(judge_in >> mode);
    if (mode == "fixed")
        check_fixed();
    else
        judge_error("mode '%s' not implemented", mode.c_str());
}

int main(int argc, char **argv) {
  init_io(argc, argv);

  check_case();

  /* Check for trailing output. */
  string trash;
  if (author_out >> trash) {
      wrong_answer("Trailing output\n");
  }

  /* Yay! */
  accept();
}
