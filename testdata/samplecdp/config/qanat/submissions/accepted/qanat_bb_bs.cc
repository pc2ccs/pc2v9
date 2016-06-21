// Solution to Qanat
// 2015 ICPC World Finals
// Babak Behsaz
//
// This implementation uses binary search to optimize the choice of
// shaft at each level of recursion
#include <iostream>
#include <cmath>
#include <vector>

using namespace std;

const double EPS = 1e-15;

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

void solve_binary_search(int n)
{
  double left = 0, right = 1;
  double cost_left = cost_r(n, left);
  double cost_right = cost_r(n, right);
  while (right - left > EPS)
    if (cost_left < cost_right)
    {
      right = (left + right) / 2;
      cost_right = cost_r(n, right);
    }
    else
    {
      left = (left + right) / 2;
      cost_left = cost_r(n, left);
    }
  r[n - 1] = left;
  cost[n] = cost_r(n, left);
}

int main()
{
  cin >> W >> H >> N;
  r = vector<double>(N, -1);
  cost = vector<double>(N + 1, 0);
  cost[0] = (W + H) * (W + H) / 4;
  for (int i = 1; i <= N; i++)
    solve_binary_search(i);
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
