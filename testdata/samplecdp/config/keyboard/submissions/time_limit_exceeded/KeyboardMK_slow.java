
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * World Finals sample solution: KEYBOARD.
 * Slower solution.
 * 
 * It is basically a shortest-path problem, but we may choose between several
 * nodes to be visited.
 * <p>
 * Algorithm description:
 * 1. Compute shortest paths from all grid position to all other grid positions.
 * 2. Take character by character and move to the corresponding key.
 * 3. Always consider ALL unit squares that form the key, and try to make further steps from all of them.
 * <p>
 * Time complexity: O(grid_squares^2 * text_length) = O(grid_size^4 * text_length)
 */
public class KeyboardMK_slow {
	
	static final int MAX_SIZE = 30;
	static final int INF = 1000000;

	StringTokenizer st = new StringTokenizer("");
	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	public static void main(String[] args) throws Exception {
		KeyboardMK_slow instance = new KeyboardMK_slow();
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
	Square[][] keys; // one record per square, indexed by position
	Square[] byindex; // linear array of all
	int[][] dist; // all-to-all shortest paths
	int[] queue; // used in one method only (but we do not want to reallocate each time)
	List<Square>[] bychar; // all squares corresponding to a character
	
	List<Square> curkeys; // list of all squares 
	int[] curdist; // number of strokes; indexed by square index
	char curchar; // current character of the text
	
	@SuppressWarnings("unchecked")
	static List<Square>[] allocList(int size) {
		return new ArrayList[size];
	}

	/** Move the current state to the next character.
	 * Try to move to all possible unit squares and remember all of them.
	 */
	void advanceTo(char ch) {
		if (ch == curchar) return;
		for (Square s : bychar[ch]) {
			int sd = INF;
			for (Square s0 : curkeys)
				sd = Math.min(sd, curdist[s0.idx] + dist[s0.idx][s.idx]);
			curdist[s.idx] = sd;
		}
		curkeys = bychar[curchar = ch];
	}

	/** Solve one instance. */
	boolean run() throws Exception {
		// read input
		rsize = nextInt(); if (rsize < 0) return false;
		csize = nextInt(); idxsize = rsize * csize;
		keys = new Square[rsize][csize];
		byindex = new Square[idxsize];
		bychar = allocList('Z'+1);
		int idx = 0;
		for (int i = 0; i < rsize; ++i) {
			String row = nextToken();
			for (int j = 0; j < csize; ++j) {
				keys[i][j] = new Square(idx, i, j, row.charAt(j));
				byindex[idx++] = keys[i][j];
				if (bychar[keys[i][j].k] == null) bychar[keys[i][j].k] = new ArrayList<Square>();
				bychar[keys[i][j].k].add(keys[i][j]);
			}
		}
		// make graph
		for (int i = 0; i < rsize; ++i)
			for (int j = 0; j < csize; ++j)
				connectSquare(keys[i][j]);
		// calculate all-to-all distances
		dist = new int[idxsize][idxsize];
		queue = new int[idxsize];
		for (int i = 0; i < idxsize; ++i)
			calcDistance(i);

		// process the text
		String txt = nextToken();
		curkeys = Collections.singletonList(keys[0][0]);
		curdist = new int[idxsize];
		curchar = 0;
		for (char ch : txt.toCharArray()) {
			advanceTo(ch);
		}
		advanceTo('*');
		
		// find the best solution of all unit squares forming the "Enter" key
		int best = INF;
		for (Square s : curkeys)
			if (curdist[s.idx] < best) best = curdist[s.idx];
		System.out.println(best + txt.length() + 1);
		return true;
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

	/** Calculate the distance from one square to all remaining ones. */
	void calcDistance(int idx) {
		for (int i = 0; i < idxsize; ++i) dist[idx][i] = INF;
		dist[idx][idx] = 0;
		int qb = 0, qe = 0;
		queue[qe++] = idx;
		while (qb < qe) {
			Square s = byindex[queue[qb++]];
			for (int d = 0; d < 4; ++d) {
				Square s2 = s.dir[d];
				if (s2 == null) continue;
				if (dist[idx][s2.idx] == INF) {
					dist[idx][s2.idx] = dist[idx][s.idx] + 1;
					queue[qe++] = s2.idx;
				}
			}
		}
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
