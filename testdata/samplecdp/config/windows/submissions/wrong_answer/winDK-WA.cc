#include <algorithm>
#include <cassert>
#include <cmath>
#include <cstring>
#include <iostream>
#include <string>
#include <vector>
using namespace std;

int used[256];
int trymove(vector<int>& vx, const vector<int>& vy,
             vector<int>& vx2, const vector<int>& vy2,
             int X, int Y, int i, int dx, bool doit) {
  used[i] = true;
  int d;
  if (dx > 0) {
    d = min(dx, X-1-vx2[i]);
    vector<pair<int, int> > v;
    for (int j = 0; j < vx.size(); j++) if (j != i) {
      if (max(vy[i], vy[j]) <= min(vy2[i], vy2[j]) &&
          vx[j] > vx2[i] && vx[j] <= vx2[i]+d) {
        v.push_back(make_pair(vx[j], j));
      }
    }
    sort(v.begin(), v.end());
    for (int ji = 0; ji < v.size(); ji++) {
      int j = v[ji].second;
      if (used[j]) continue;
      int gap = vx[j]-vx2[i]-1;
      d = trymove(vx, vy, vx2, vy2, X, Y, j, d-gap, doit) + gap;
    }
  } else {
    d = max(dx, -vx[i]);
    vector<pair<int, int> > v;
    for (int j = 0; j < vx.size(); j++) if (j != i) {
      if (max(vy[i], vy[j]) <= min(vy2[i], vy2[j]) &&
          vx2[j] < vx[i] && vx2[j] >= vx[i]+d) {
        v.push_back(make_pair(-vx2[j], j));
      }
    }
    sort(v.begin(), v.end());
    for (int ji = 0; ji < v.size(); ji++) {
      int j = v[ji].second;
      if (used[j]) continue;
      int gap = vx[i]-vx2[j]-1;
      d = trymove(vx, vy, vx2, vy2, X, Y, j, d+gap, doit) - gap;
    }
  }
  if (doit) {
    vx[i] += d; vx2[i] += d;
  }
  return d;
}

main() {
  int X, Y, i, j, x, y, w, h, dx, dy;
  vector<int> vx, vy, vx2, vy2;
  cin >> X >> Y;

  string cmd;
  for (int ncmd = 1; cin >> cmd; ncmd++) {
    if (cmd == "OPEN") {
      cin >> x >> y >> w >> h;
      bool fail = (x+w > X || y+h > Y);
      for (i = 0; i < vx.size(); i++) {
        if (max(vx[i], x) <= min(vx2[i], x+w-1) &&
            max(vy[i], y) <= min(vy2[i], y+h-1)) {
          fail = true;
        }
      }
      if (fail) {
        cout << "Command " << ncmd << ": OPEN - " <<
                "window does not fit" << endl;
        continue;
      }
      vx.push_back(x);
      vy.push_back(y);
      vx2.push_back(x+w-1);
      vy2.push_back(y+h-1);
      assert(vx.size() <= 256);
    } else if (cmd == "RESIZE") {
      cin >> x >> y >> w >> h;
      for (i = 0; i < vx.size(); i++) {
        if (x >= vx[i] && x <= vx2[i] && y >= vy[i] && y <= vy2[i]) break;
      }
      if (i == vx.size()) {
        cout << "Command " << ncmd << ": RESIZE - " <<
                "no window at given position" << endl;
        continue;
      }
      x = vx[i]; y = vy[i];
      bool fail = (x+w > X || y+h > Y);
      for (j = 0; j < vx.size(); j++) if (j != i) {
        if (max(vx[j], x) <= min(vx2[j], x+w-1) &&
            max(vy[j], y) <= min(vy2[j], y+h-1)) {
          fail = true;
        }
      }
      if (fail) {
        cout << "Command " << ncmd << ": RESIZE - " <<
                "window does not fit" << endl;
        continue;
      }
      vx2[i] = x+w-1;
      vy2[i] = y+h-1;
    } else if (cmd == "CLOSE") {
      cin >> x >> y;
      for (i = 0; i < vx.size(); i++) {
        if (x >= vx[i] && x <= vx2[i] && y >= vy[i] && y <= vy2[i]) break;
      }
      if (i == vx.size()) {
        cout << "Command " << ncmd << ": CLOSE - " <<
                "no window at given position" << endl;
        continue;
      }
      vx.erase(vx.begin()+i);
      vy.erase(vy.begin()+i);
      vx2.erase(vx2.begin()+i);
      vy2.erase(vy2.begin()+i);
    } else if (cmd == "MOVE") {
      cin >> x >> y >> dx >> dy;
      for (i = 0; i < vx.size(); i++) {
        if (x >= vx[i] && x <= vx2[i] && y >= vy[i] && y <= vy2[i]) break;
      }
      if (i == vx.size()) {
        cout << "Command " << ncmd << ": MOVE - " <<
                "no window at given position" << endl;
        continue;
      }
      int d, d2;
      if (dx != 0) {
        d2 = dx;
        memset(used, 0, sizeof(used));
        d = trymove(vx, vy, vx2, vy2, X, Y, i, dx, false);
        memset(used, 0, sizeof(used));
        trymove(vx, vy, vx2, vy2, X, Y, i, d, true);
      } else {
        d2 = dy;
        memset(used, 0, sizeof(used));
        d = trymove(vy, vx, vy2, vx2, Y, X, i, dy, false);
        memset(used, 0, sizeof(used));
        trymove(vy, vx, vy2, vx2, Y, X, i, d, true);
      }
      if (d != d2) {
        cout << "Command " << ncmd << ": MOVE - " <<
                "moved " << abs(d) << " instead of " << abs(d2) << endl;
      }
    }
/*cout << vx.size() << " window(s):" << endl;
for (int i = 0; i < vx.size(); i++) {
  cout << vx[i] << ' ' << vy[i] << ' ' << vx2[i]-vx[i]+1 << ' ' << vy2[i]-vy[i]+1 << endl;
}*/
  }

  cout << vx.size() << " window(s):" << endl;
  for (int i = 0; i < vx.size(); i++) {
    cout << vx[i] << ' ' << vy[i] << ' ' << vx2[i]-vx[i]+1 << ' ' << vy2[i]-vy[i]+1 << endl;
  }
}
