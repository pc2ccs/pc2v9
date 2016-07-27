#include <cassert>
#include <cstdio>
#include <vector>
#include <algorithm>

using namespace std;

bool overlap1(int x1, int x2, int y1, int y2) {
	return (x1 <= y1 && y1 < x2) || (x1 < y2 && y2 <= x2) ||
		(y1 <= x1 && x1 < y2) || (y1 < x2 && x2 <= y2);
}

struct win {
	int l, t, r, b, id;
	win(int l, int t, int r, int b, int id): l(l), t(t), r(r), b(b), id(id) {}
	bool overlap(const win &w) const {
		return overlap1(l, r, w.l, w.r) && overlap1(t, b, w.t, w.b);
	}
	bool contains(int x, int y) {
		return l <= x && x < r && t <= y && y < b; 
	}
	bool operator<(const win &w)  const { return l < w.l; }
};

bool idless(const win &w, const win &v) { return w.id < v.id; }

typedef vector<win> LW;

LW wins;
int W, H;

LW::iterator findw(int x, int y) {
	for (LW::iterator it = wins.begin(); it != wins.end(); ++it)
		if (it->contains(x, y)) return it;
	return wins.end();
}

int move(int id, int dx) {
	int room[300], move[300] = {0}, i;
	for (i = wins.size()-1; i >= 0; --i) {
		room[i] = W-wins[i].r;
		for (int j = i+1; j < wins.size(); ++j)
			if (overlap1(wins[i].t, wins[i].b, wins[j].t, wins[j].b))
				room[i] = min(room[i], wins[j].l+room[j]-wins[i].r);
		if (wins[i].id == id) break;
	}
	int ret = move[i] = min(dx, room[i]);
	for (; i < wins.size(); ++i) {
		wins[i].l += move[i];
		wins[i].r += move[i];
		for (int j = i+1; j < wins.size(); ++j)
			if (overlap1(wins[i].t, wins[i].b, wins[j].t, wins[j].b))
				move[j] = max(move[j], wins[i].r-wins[j].l);
	}
	return ret;
}

void flip() {
	for (win &w: wins) {
		w.l = W-w.l;
		w.r = W-w.r;
		w.t = H-w.t;
		w.b = H-w.b;
		swap(w.l, w.r);
		swap(w.t, w.b);
	}
}

void rot() {
	for (win &w: wins) {
		swap(w.l, w.t);
		swap(w.r, w.b);
	}
	swap(W, H);
}

bool winok(const win &w) {
	for (LW::iterator it = wins.begin(); it != wins.end(); ++it)
		if (&*it != &w && w.overlap(*it))
			return false;
	return (w.r <= W && w.b <= H);
}


char cmd[100];
int cnt = 0;

void nowin() {
	printf("Command %d: %s - no window at given position\n", cnt, cmd);
}

void unfit() {
	printf("Command %d: %s - window does not fit\n", cnt, cmd);
}

void close(int x, int y) {
	LW::iterator it = findw(x, y);
	if (it != wins.end()) wins.erase(it);
	else nowin();
}

void open(int x, int y, int w, int h) {
	win n(x, y, x+w, y+h, cnt);
	if (winok(n)) wins.push_back(n);
	else unfit();
}

void resize(int x, int y, int w, int h) {
	LW::iterator it = findw(x,y);
	if (it != wins.end()) {
		win o = *it;
		it->r = it->l + w;
		it->b = it->t + h;
		if (!winok(*it)) {
			*it = o;
			unfit();
		}
	} else nowin();
}

void move(int x, int y, int dx, int dy) {
	LW::iterator it = findw(x, y);
	if (it != wins.end()) {
		int id = it->id;
		if (dy) { rot(); swap(x, y); }
		if (dx + dy < 0) { flip(); x = W-x; y = H-y; }
		sort(wins.begin(), wins.end());
		int d = abs(dx) + abs(dy);
		int td = move(id, d);
		if (td < d)
			printf("Command %d: %s - moved %d instead of %d\n", cnt, cmd, td, d);
		if (dx + dy < 0) flip();
		if (dy) rot();
	} else nowin();
}

int main(void) {
	scanf("%d%d", &W, &H);
	int x, y, w, h, dx, dy;
	while (scanf("%s%d%d", cmd, &x, &y) == 3) {
		++cnt;
		if (*cmd != 'C') scanf("%d%d", &w, &h);
		switch (*cmd) {
		case 'O': open(x, y, w, h);	break;
		case 'C': close(x, y); break;
		case 'R': resize(x, y, w, h); break;
		case 'M': move(x, y, w, h); break;
		}
	}
	sort(wins.begin(), wins.end(), idless);
	printf("%d window(s):\n", wins.size());
	for (win &w: wins)
		printf("%d %d %d %d\n", w.l, w.t, w.r-w.l, w.b-w.t);
}
