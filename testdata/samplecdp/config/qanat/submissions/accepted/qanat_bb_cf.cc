// Solution to Qanat
// 2015 ICPC World Finals
// Babak Behsaz
//
// This implementation uses closed form to optimize the choice of shaft
// each level of recursion
#include <iostream>
#include <cmath>
#include <vector>

using namespace std;

double W, H; 
int N;
vector<double> r;
vector<double> cost;

double cost_r(int n, double r)
{
  double l1 = (1 + r) * H + (1 - r) * W;
  double l2 = r * H;
  return r * r * cost[n - 1] + l1 * l1 / 4 - l2 * l2 / 2;
}

void solve_mathematical(int n)
{
  double num = W * W - H * H;
  double denom = 4 * cost[n - 1] + (W - H) * (W - H) - 2 * H * H;
  r[n - 1] = num / denom;
  cost[n] = cost_r(n, r[n - 1]);
}

int main()
{
  cin >> W >> H >> N;
  r = vector<double>(N, -1);
  cost = vector<double>(N + 1, 0);
  cost[0] = (W + H) * (W + H) / 4;
  for (int i = 1; i <= N; i++)
    solve_mathematical(i);
  vector<double> ans;
  double ratio = 1;
  for (int i = 0; i < N; i++)
  {
    ratio *= r[N - i - 1];
    ans.push_back(ratio * W);
  }
  
  cout.setf(ios::fixed | ios::showpoint);
  cout.precision(6);
  cout << cost[N] << endl;
  for (int i = ans.size() - 1; i >= 0 && i >= (int)ans.size() - 10; i--)
  {
    cout << ans[i];
    if (i > 0)
      cout << ' ';
  }
  cout << endl;
  
  return 0;
}
