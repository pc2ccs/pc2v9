
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * World Finals 2015 sample solution: CUTTING CHEESE
 * @author Martin Kacer
 */
public class CheeseMK {
	
	static final double PRECISION = 1E-7, EPS = 1E-9;
	static final int MAX_HOLES = 10000, MAX_SLICE = 100;

	StringTokenizer st = new StringTokenizer("");
	BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	public static void main(String[] args) throws Exception {
		CheeseMK instance = new CheeseMK();
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
	
	double[] hz = new double[MAX_HOLES], hr = new double[MAX_HOLES];
	double[] result = new double[MAX_SLICE];
	int hcnt, rescnt;
	double slice;
	
	double holepart(double r, double v) {
		return Math.PI/3 * v*v * (3*r - v);
	}
	
	double volume(double zmax) {
		double holesvol = 0;
		for (int i = 0; i < hcnt; ++i) {
			if (hz[i] <= zmax) {
				holesvol += Math.PI*4/3*hr[i]*hr[i]*hr[i];
				if (hz[i] + hr[i] > zmax) holesvol -= holepart(hr[i], hz[i] + hr[i] - zmax);
			} else {
				if (hz[i] - hr[i] < zmax) holesvol += holepart(hr[i], zmax - hz[i] + hr[i]);
			}
		}
		return 100.0 * 100.0 * zmax - holesvol;
	}
	
	void find(double zfrom, double volfrom, double zto, double volto) {
		double ifrom = Math.floor(volfrom/slice), ito = Math.floor(volto/slice);
		if (ifrom == ito) return;
		if (zto - zfrom < PRECISION) { result[rescnt++] = zto; return; }
		double mid = (zfrom + zto) / 2, volmid = volume(mid);
		find(zfrom, volfrom, mid, volmid);
		find(mid, volmid, zto, volto);
	}

	/** Solve one instance. */
	boolean run() throws Exception {
		hcnt = nextInt();
		if (hcnt < 0) return false;
		int s = nextInt();
		for (int i = 0; i < hcnt; ++i) {
			hr[i] = nextInt()/1000.0; nextInt(); nextInt(); hz[i] = nextInt()/1000.0;
		}
		double totvol = volume(100.0);
		slice = totvol / s;
		rescnt = 0;
		find(0.0, 0.0, 100.0, totvol-EPS);
		result[rescnt] = 100.0;
		System.out.println(result[0]);
		for (int i = 0; i < rescnt; ++i) System.out.println(result[i+1] - result[i]);
		return true;
	}
}
