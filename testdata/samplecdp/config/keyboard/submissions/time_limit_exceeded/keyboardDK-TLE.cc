#include <cstring>
#include <iostream>
#include <queue>
#include <string>
#include <vector>
using namespace std;

int dx[4] = {1, 0, -1, 0}, dy[4] = {0, -1, 0, 1};
int mv[50][50][4];
int best[50][50];

main() {
  int Y, X;
  while (cin >> Y >> X) {
    vector<string> g(Y);
    for (int i = 0; i < Y; i++) cin >> g[i];
    string message;
    cin >> message;
    message += '*';

    memset(mv, -1, sizeof(mv));
    for (int y = 0; y < Y; y++)
    for (int x = 0; x < X; x++)
    for (int d = 0; d < 4; d++) {
      int x2 = x, y2 = y;
      for (int n = 0; ; n++) {
        if (x2 < 0 || x2 >= X || y2 < 0 || y2 >= Y) break;
        if (g[y2][x2] != g[y][x]) {
          mv[y][x][d] = n;
          break;
        }
        x2 += dx[d]; y2 += dy[d];
      }
    }

    priority_queue<pair<int, pair<short, short> > > q;
    q.push(make_pair(0, make_pair(0, 0)));
    for (int i = 0; i < message.size(); i++) {
      memset(best, 63, sizeof(best));
      while (!q.empty()) {
        int dist = -q.top().first;
        short x = q.top().second.first, y = q.top().second.second;
        q.pop();
        if (best[y][x] <= dist) continue;
        best[y][x] = dist;
        if (g[y][x] == message[i]) continue;
        for (int d = 0; d < 4; d++) if (mv[y][x][d] != -1) {
          int x2 = x + dx[d]*mv[y][x][d];
          int y2 = y + dy[d]*mv[y][x][d];
          if (best[y2][x2] > dist+1) {
            q.push(make_pair(-dist-1, make_pair(x + dx[d]*mv[y][x][d],
                                                y + dy[d]*mv[y][x][d])));
          }
        }
      }
      for (int y = 0; y < Y; y++)
      for (int x = 0; x < X; x++) if (g[y][x] == message[i]) {
        q.push(make_pair(-best[y][x]-1, make_pair(x, y)));
      }
    }

    cout << -q.top().first << endl;
  }
}
