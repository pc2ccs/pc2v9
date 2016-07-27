import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

/**
 * World Finals 2015 sample solution: WINDOW MANAGER
 * @author Martin Kacer
 */
public class WindowsMK {

	StringTokenizer st = new StringTokenizer("");
	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	public static void main(String[] args) throws Exception {
		WindowsMK instance = new WindowsMK();
		while (instance.run()) {/*repeat*/}
	}
	String nextToken() throws Exception {
		while (!st.hasMoreTokens()) {
			String ln = input.readLine();
			if (ln == null) return null;
			st = new StringTokenizer(ln);
		}
		return st.nextToken();
	}
	int nextInt() throws Exception {
		String s = nextToken();
		return (s == null) ? -1 : Integer.parseInt(s);
	}
	
	static final int MAX = 256, INF = 1000000010;
	int[] wx1 = new int[MAX], wy1 = new int[MAX], wx2 = new int[MAX], wy2 = new int[MAX];
	int[] mx1 = new int[MAX], my1 = new int[MAX], mx2 = new int[MAX], my2 = new int[MAX];
	int[] dist = new int[MAX];
	int wincnt, cmdcnt, xmax, ymax;
	String cmd;
	
	final Comparator<Integer> distComparator = new Comparator<Integer>() {
		public int compare(Integer o1, Integer o2) {
			return dist[o1.intValue()] - dist[o2.intValue()];
		}
	};
	
	int findWindow(int x, int y) {
		for (int i = 0; i < wincnt; ++i)
			if (x >= wx1[i] && x < wx2[i] && y >= wy1[i] && y < wy2[i])
				return i;
		return -1;
	}
	int locateWindow() throws Exception {
		int x = nextInt();
		int i = findWindow(x, nextInt());
		if (i < 0)
			System.out.println("Command " + cmdcnt + ": " + cmd + " - no window at given position");
		return i;
	}
	
	boolean isFree(int x1, int y1, int x2, int y2, int ignore) {
		boolean ok = (x2 <= xmax && y2 <= ymax);
		for (int i = 0; ok && i < wincnt; ++i) if (i != ignore) {
			if (x2 > wx1[i] && wx2[i] > x1 && y2 > wy1[i] && wy2[i] > y1)
				ok = false;
		}
		if (!ok) {
			System.out.println("Command " + cmdcnt + ": " + cmd + " - window does not fit");
		}
		return ok;
	}
	
	void openWindow() throws Exception {
		wx1[wincnt] = nextInt(); wy1[wincnt] = nextInt();
		wx2[wincnt] = wx1[wincnt] + nextInt(); wy2[wincnt] = wy1[wincnt] + nextInt();
		if (isFree(wx1[wincnt], wy1[wincnt], wx2[wincnt], wy2[wincnt], -1))
			++wincnt;
	}
	
	void closeWindow() throws Exception {
		int win = locateWindow();
		if (win < 0) return;
		--wincnt;
		for (int i = win; i < wincnt; ++i) {
			wx1[i] = wx1[i+1]; wy1[i] = wy1[i+1]; wx2[i] = wx2[i+1]; wy2[i] = wy2[i+1];
		}
	}
	
	void resizeWindow() throws Exception {
		int win = locateWindow();
		int w = nextInt(), h = nextInt();
		if (win < 0) return;
		if (!isFree(wx1[win], wy1[win], wx1[win]+w, wy1[win]+h, win)) return;
		wx2[win] = wx1[win] + w; wy2[win] = wy1[win] + h;
	}
	
	void moveWindow() throws Exception {
		int win = locateWindow();
		int dx = nextInt(), dy = nextInt();
		if (win < 0) return;
		if (dx > 0) {
			moveRight(win, dx);
		} else if (dx < 0) {
			rotate(); rotate();
			moveRight(win, -dx);
			rotate(); rotate();
		} else if (dy > 0) {
			rotate(); rotate(); rotate();
			moveRight(win, dy);
			rotate();
		} else if (dy < 0) {
			rotate();
			moveRight(win, -dy);
			rotate(); rotate(); rotate();
		}
	}
	
	void moveRight(int win, int dx) {
		boolean[] moving = new boolean[wincnt];
		for (int i = 0; i < wincnt; ++i) dist[i] = INF;
		PriorityQueue<Integer> heap = new PriorityQueue<Integer>(wincnt, distComparator);
		dist[win] = 0;
		heap.add(Integer.valueOf(win));
		while (!heap.isEmpty()) {
			int cur = heap.poll().intValue();
			if (dist[cur] >= dx) break;
			moving[cur] = true;
			for (int i = 0; i < wincnt; ++i) {
				if (moving[i]) continue;
				if (wx1[i] < wx2[cur]) continue;
				if (wy1[i] >= wy2[cur]) continue;
				if (wy2[i] <= wy1[cur]) continue;
				int d = dist[cur] + wx1[i] - wx2[cur];
				if (d < dist[i]) {
					Integer ii = Integer.valueOf(i);
					heap.remove(ii);
					dist[i] = d;
					heap.add(ii);
				}
			}
		}
		int rx = dx;
		for (int i = 0; i < wincnt; ++i) if (moving[i]) {
			int d = xmax - wx2[i] + dist[i];
			if (d < rx) rx = d;
		}
		if (rx < dx) {
			System.out.println("Command " + cmdcnt + ": " + cmd + " - moved " + rx + " instead of " + dx);
		}
		for (int i = 0; i < wincnt; ++i) if (moving[i])
			if (rx > dist[i]) {
				wx1[i] += rx - dist[i]; wx2[i] += rx - dist[i];
			}
	}
	
	void rotate() {
		for (int i = 0; i < wincnt; ++i) {
			int ox1 = wx1[i], ox2 = wx2[i];
			wx2[i] = ymax - wy1[i];
			wx1[i] = ymax - wy2[i];
			wy1[i] = ox1; wy2[i] = ox2;
		}
		int t = xmax; xmax = ymax; ymax = t;
	}

	/** Solve one instance. */
	boolean run() throws Exception {
		xmax = nextInt(); ymax = nextInt();
		wincnt = 0;
		while ((cmd = nextToken()) != null) {
			++cmdcnt;
			if ("OPEN".equals(cmd)) openWindow();
			else if ("CLOSE".equals(cmd)) closeWindow();
			else if ("RESIZE".equals(cmd)) resizeWindow();
			else if ("MOVE".equals(cmd)) moveWindow();
		}
		System.out.println(wincnt + " window(s):");
		for (int i = 0; i < wincnt; ++i)
			System.out.println(wx1[i] + " " + wy1[i] + " " + (wx2[i]-wx1[i]) + " " + (wy2[i]-wy1[i]));
		return false;
	}
}
