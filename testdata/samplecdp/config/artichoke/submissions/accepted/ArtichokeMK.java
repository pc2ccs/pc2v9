
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * World Finals 2015 sample solution: ARTICHOKES
 * @author Martin Kacer
 */
public class ArtichokeMK {

	StringTokenizer st = new StringTokenizer("");
	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	public static void main(String[] args) throws Exception {
		ArtichokeMK instance = new ArtichokeMK();
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

	/** Solve one instance. */
	boolean run() throws Exception {
		double p = nextInt(), a = nextInt(), b = nextInt(), c = nextInt(), d = nextInt();
		int n = nextInt();
		if (n < 0) return false;
		double maxVal = -1E50;
		double maxDec = 0;
		for (int i = 1; i <= n; ++i) {
			double x = p * (Math.sin(a * i + b) + Math.cos(c * i + d) + 2);
			if (x > maxVal) maxVal = x;
			if (maxVal-x > maxDec) maxDec = maxVal-x;
		}
		System.out.println(maxDec);
		return true;
	}
}
