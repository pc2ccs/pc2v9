
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * World Finals 2015 sample solution: KEYBOARD.
 * 
 * It is basically a shortest-path problem, but we may choose between several
 * nodes to be visited.
 * <p>
 * Algorithm description:
 * 1. Pre-compute neighboring squares in 4 directions for any possible grid square.
 * 2. Do a BFS on all possible moves (state = square + position in the text).
 * 3. Stop when a solution is found.
 * <p>
 * Time complexity: O(grid_squares * text_length) = O(grid_size^2 * text_length)
 */
public class KeyboardMK {
	
	static final int MAX_SIZE = 30, MAX_TEXT = 100000;
	static final int INF = MAX_TEXT * 1000;

	StringTokenizer st = new StringTokenizer("");
	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	public static void main(String[] args) throws Exception {
		KeyboardMK instance = new KeyboardMK();
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
	
	int rsize, csize, idxsize;
	String text;
	Square[][] keys; // one record per square, indexed by position
	Square[] qsq; // BFS queue (square)
	int[] qtxt; // BFS queue (text position)
	int qb, qe;
	int[][] dist; // distance (number of keystrokes) to a state

	/** Solve one instance. */
	boolean run() throws Exception {
		// read input
		rsize = nextInt(); if (rsize < 0) return false;
		csize = nextInt(); idxsize = rsize * csize;
		keys = new Square[rsize][csize];
		int idx = 0;
		for (int i = 0; i < rsize; ++i) {
			String row = nextToken();
			for (int j = 0; j < csize; ++j) {
				keys[i][j] = new Square(idx++, i, j, row.charAt(j));
			}
		}
		// make graph
		for (int i = 0; i < rsize; ++i)
			for (int j = 0; j < csize; ++j)
				connectSquare(keys[i][j]);
		
		// allocate data structures
		text = nextToken() + '*';
		dist = new int[idxsize][text.length()];
		qsq = new Square[idxsize * text.length()];
		qtxt = new int[idxsize * text.length()];
		
		// initial square
		enqueue(keys[0][0], 0, 1);

		// BFS
		while (qb < qe) {
			Square cur = qsq[qb];
			int pos = qtxt[qb++];
			int d = dist[cur.idx][pos] + 1;
			if (cur.k == text.charAt(pos)) {
				if (pos+1 == text.length()) {
					System.out.println(d - 1);
					return true;
				}
				enqueue(cur, pos+1, d);
			}
			for (int dir = 0; dir < 4; ++dir)
				enqueue(cur.dir[dir], pos, d);
		}
		System.out.println("impossible"); // should not happen
		return true;
	}
	
	void enqueue(Square sq, int txt, int d) {
		if (sq == null) return;
		if (dist[sq.idx][txt] > 0) return;
		dist[sq.idx][txt] = d;
		qsq[qe] = sq;
		qtxt[qe++] = txt;
	}

	/** Connect one square to its (up to four) neighbours. */
	void connectSquare(Square s) {
		for (int i = s.r; --i >= 0; )
			if (keys[i][s.c].k != s.k) { s.dir[0] = keys[i][s.c]; break; }
		for (int i = s.r; ++i < rsize; )
			if (keys[i][s.c].k != s.k) { s.dir[1] = keys[i][s.c]; break; }
		for (int j = s.c; --j >= 0; )
			if (keys[s.r][j].k != s.k) { s.dir[2] = keys[s.r][j]; break; }
		for (int j = s.c; ++j < csize; )
			if (keys[s.r][j].k != s.k) { s.dir[3] = keys[s.r][j]; break; }
	}

	/** Simple data structure representing one unit square. */
	static class Square {
		final int idx,r,c;
		final char k;
		Square[] dir = new Square[4];
		Square(int idx, int r, int c, char k) {
			this.idx = idx; this.r = r; this.c = c; this.k = k;
		}
	}
}
