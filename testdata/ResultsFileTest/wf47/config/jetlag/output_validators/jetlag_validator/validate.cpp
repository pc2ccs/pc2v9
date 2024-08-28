#include <bits/stdc++.h>
#include "validate.h"
using namespace std;
typedef long long int LL;

int main(int argc, char **argv) {
    init_io(argc, argv);
    
    // Parsing the first line of the output.
    string judge_line, contestant_line;
    judge_ans >> judge_line;
    author_out >> contestant_line;

    transform(judge_line.begin(), judge_line.end(), judge_line.begin(),
              [](char c){ return tolower(c); });
    transform(contestant_line.begin(), contestant_line.end(), contestant_line.begin(),
              [](char c){ return tolower(c); });

    if (judge_line == "impossible" and contestant_line != "impossible")
        wrong_answer("It is impossible, but the contestant finds a solution.\n");
    
    if (contestant_line == "impossible" and judge_line != "impossible")
        wrong_answer("It is possible, but the contestant says it is impossible.\n");
        
    string trash;
    if (contestant_line == "impossible") {
      if (author_out >> trash) {
        wrong_answer("Trailing output after 'impossible'\n");
      }
      accept();
    }
    
    stringstream first_line_stream(contestant_line);
    int p;
    if (!(first_line_stream >> p)) {
      wrong_answer("Failed to read a number from the first line\n");
    }
    if (p <= 0 or p > 1'000'000)
        wrong_answer("The number of sleep periods " + to_string(p) + " does not belong to the range [1, 1'000'000].\n");
    
    vector<pair<LL,LL>> intervals; // sleep periods and activities
    
    // Reading activities from the input file. 
    int n;
    judge_in >> n;
    for (int i = 0; i < n; i++) {
        LL b, e;
        judge_in >> b >> e;
        intervals.push_back({b, e});
    }
    
    
    // Checking that the sleep schedule is well-formed and satisfies 
    // the desired constraints.
    LL awake_until = 0;
    LL functioning_until = 0;
    for (int i = 1; i <= p; i++) {
        LL s, t;
        if (!(author_out >> s >> t)) {
          wrong_answer("Failed to read two elements of the sleep schedule from the solution\n");
        }
        if (s < awake_until)
            wrong_answer("The " + to_string(i) + "th sleeping period intersects the no-sleep phase of the previous one.\n");
        if (s > functioning_until)
            wrong_answer("The " + to_string(i) + "th sleeping period starts after the end of the functioning phase of the previous one.\n");
        if (t <= s)
            wrong_answer("The " + to_string(i) + "th sleeping period does not have a positive duration.\n");
        if (t >= intervals[n-1].second)
            wrong_answer("The " + to_string(i) + "th sleeping period starts after the last activity.\n");
        intervals.push_back({s, t});
        awake_until = s + 2 * (t-s);
        functioning_until = s + 3 * (t-s);
    }
    
    if (functioning_until < intervals[n-1].second)
        wrong_answer("The sleep schedule does not reach the last activity of the finals.\n");
    if (author_out >> trash) {
      wrong_answer("Trailing output after a sleep schedule.\n");
    }
    
    // Checking that sleep periods and activities are disjoint.
    sort(intervals.begin(), intervals.end());
    for (int i = 1; i < n + p; i++) {
        if (intervals[i].first < intervals[i-1].second)
            wrong_answer("One of the sleep periods intersects one of the activities: [" + to_string(intervals[i-1].first) + ", " + to_string(intervals[i-1].second) + "] and [" + to_string(intervals[i].first) + ", " + to_string(intervals[i].second) + "]");
    }
    accept();
    return 0;
}
