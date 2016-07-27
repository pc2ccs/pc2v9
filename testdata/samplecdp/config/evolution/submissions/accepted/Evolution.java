
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * World Finals 2015 problem solution: PARALLEL EVOLUTION
 * 
 * @author Martin Kacer
 */
public class Evolution {
	StringTokenizer st = new StringTokenizer("");
	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	public static void main(String[] args) throws Exception {
		Evolution instance = new Evolution();
		while (instance.run()) { /*repeat*/ }
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
	
	static final char[] EMPTY_SEQ = new char[0];
	
	char[][] samples;
	char[] current;
	int[] parent;

	/** Solve one instance. */
	boolean run() throws Exception {
		int n = nextInt(); if (n < 0) return false;
		samples = new char[n][];
		parent = new int[n];
		current = nextToken().toCharArray();
		for (int i = 0; i < n; ++i)
			samples[i] = nextToken().toCharArray();
		if (!solve())
			System.out.println("Impossible");
		return true;
	}
	
	boolean solve() {
		for (int i = 0; i < samples.length; ++i)
			if (!isSubString(samples[i], current))
				return false;
		Arrays.sort(samples, new LengthComparator());
		char[] s1 = EMPTY_SEQ, s2 = EMPTY_SEQ, common = null;
		int i1 = -1, i2 = -1, ic = -1, ic0 = -1;
		for (int i = 0; i < samples.length; ++i) {
			char[] s = samples[i];
			if (common == null) {
				if (isSubString(s1, s)) {
					if (isSubString(s2, s)) {
						common = s; ic = ic0 = i; // parent[ic0] will be set later
					} else {
						s1 = s; parent[i] = i1; i1 = i;
					}
				} else if (isSubString(s2, s)) {
					s2 = s; parent[i] = i2; i2 = i;
				} else
					return false;
			} else {
				if (isSubString(common, s)) {
					common = s; parent[i] = ic; ic = i;
				} else {
					if (isSubString(s1, s)) {
						s1 = s; parent[i] = i1; i1 = i;
						s2 = common; parent[ic0] = i2; i2 = ic;
					} else if (isSubString(s2, s)) {
						s2 = s; parent[i] = i2; i2 = i;
						s1 = common; parent[ic0] = i1; i1 = ic;
					} else
						return false;
					common = null;
				}
			}
		}
		if (common != null) {
			parent[ic0] = i1; i1 = ic;
		}
		int cnt1 = 0;
		for (int i = i1; i >= 0; i = parent[i]) ++cnt1;
		System.out.println(cnt1 + " " + (samples.length - cnt1));
		List<String> lst = new ArrayList<String>(samples.length);
		for (int i = i1; i >= 0; i = parent[i])
			lst.add(new String(samples[i]));
		Collections.reverse(lst);
		for (String s : lst) System.out.println(s);
		lst.clear();
		for (int i = i2; i >= 0; i = parent[i])
			lst.add(new String(samples[i]));
		Collections.reverse(lst);
		for (String s : lst) System.out.println(s);
		return true;
	}
	
	static boolean isSubString(char[] sub, char[] big) {
		if (sub.length > big.length) return false;
		int i = 0;
		for (int j = 0; i < sub.length && j < big.length; ++j) {
			if (sub[i] == big[j]) ++i;
		}
		return (i == sub.length);
	}

	static class LengthComparator implements Comparator<char[]> {
		public int compare(char[] s1, char[] s2) {
			return s1.length - s2.length;
		}
	}
}
