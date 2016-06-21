
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * World Finals 2015 sample solution: SHIP TRAFFIC
 * @author Martin Kacer
 */
public class ShipMK {

	StringTokenizer st = new StringTokenizer("");
	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	public static void main(String[] args) throws Exception {
		ShipMK instance = new ShipMK();
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
	
	static class Interval implements Comparable<Interval> {
		double x,y;
		Interval(double x, double y) { this.x = x; this.y = y; }
		public int compareTo(Interval other) { return Double.compare(this.y, other.y); }
		public String toString() { return "("+x+"-"+y+")"; }
	}

	/** Solve one instance. */
	boolean run() throws Exception {
		int n = nextInt(); if (n < 0) return false;
		double w = nextInt(), u = nextInt(), v = nextInt(), t1 = nextInt(), t2 = nextInt();
		SortedSet<Interval> pos = new TreeSet<Interval>();
		pos.add(new Interval(t1, t2));
		for (int lane = 0; lane < n; ++lane) {
			String dir = nextToken();
			int m = nextInt();
			while (m-->0) {
				double s = nextInt(), p = nextInt();
				if ("E".equals(dir)) { p = -p; }
				shipInterval(pos, p/u - (lane+1)*w/v, (p+s)/u - lane*w/v);
			}
		}
		double max = 0;
		for (Interval pi : pos)
			if (pi.y-pi.x > max) max = pi.y-pi.x;
		System.out.println(max);
		return true;
	}
	
	void shipInterval(SortedSet<Interval> pos, double s1, double s2) {
		SortedSet<Interval> tail = pos.tailSet(new Interval(0,s1));
		if (tail.isEmpty()) return;
		double fx = tail.first().x;
		Interval add = (fx < s1) ? new Interval(fx, s1) : null;
		Iterator<Interval> it = tail.iterator();
		while (it.hasNext()) {
			Interval p = it.next();
			if (p.x >= s2) break;
			it.remove();
			if (p.y >= s2) {
				pos.add(new Interval(s2, p.y));
				break;
			}
		}
		if (add != null) pos.add(add);
	}
}
