import java.util.*;

public class Asteroids
{

	public static final int MAXS = 10;
	public static double[] hits = new double[4*MAXS];
	public static int nhits;
	public static asteroid a1, a2;
	public static Scanner in = new Scanner(System.in);
	public static double tint = 0.0;				// gross global variable, but makes things easier

	public static boolean equal(double a, double b)
	{
		return (Math.abs(a-b) <= 0.000000001);
	}

	public static void inputAsteroid(asteroid a)
	{
		a.nseg = in.nextInt();
		a.segs = new seg[a.nseg];
		a.segs[0] = new seg();
		a.segs[0].p1.x = in.nextInt();
		a.segs[0].p1.y = in.nextInt();
		for(int i=0; i<a.nseg-1; i++) {
			a.segs[i].p2.x = in.nextInt();
			a.segs[i].p2.y = in.nextInt();
			a.segs[i+1] = new seg();
			a.segs[i+1].p1.x = a.segs[i].p2.x;
			a.segs[i+1].p1.y = a.segs[i].p2.y;
		}
		a.segs[a.nseg-1].p2.x = a.segs[0].p1.x;
		a.segs[a.nseg-1].p2.y = a.segs[0].p1.y;
		a.vx = in.nextInt();
		a.vy = in.nextInt();
	}

	public static boolean between(double a, double b, double c)
	{
		if (a>=b && a<=c)
			return true;
		return (a<=b && a>=c);
	}

	public static boolean inside(point p, seg s)
	{
		return (((p.x-s.p1.x)*(s.p2.y-s.p1.y) - (s.p2.x-s.p1.x)*(p.y-s.p1.y)) >= 0);
	}

	public static boolean getInitialInt(point p1, point p2, point p3, int vx, int vy)
	{
		point p4 = new point();
		p4.x = p3.x+vx;
		p4.y = p3.y+vy;
		double a = p2.x - p1.x;
		double b = p3.x - p4.x;
		double c = p3.x - p1.x;
		double d = p2.y - p1.y;
		double e = p3.y - p4.y;
		double f = p3.y - p1.y;
		double den = a*e-b*d;
		double num = c*e-b*f;
		if (Math.abs(den) >= 0.000001) {
			double t = num/den;
			double u = (a*f-c*d)/den;
			if (u < 0.0 || t < 0.0 || t > 1.0)
				return false;
			if (vx != 0)
				tint = -u*b/vx;
			else
				tint = -u*e/vy;
			return true;
		}
		else if (Math.abs(num) >= 0.000001)
			return false;
		else if (vx != 0) {
			tint = (p1.x-p3.x)/vx;
			double t2 = (p2.x-p3.x)/vx;
			if (t2 < tint)
				tint = t2;
			return (tint > 0.0);
		}
		else if (vy != 0) {
			tint = (p1.y-p3.y)/vy;
			double t2 = (p2.y-p3.y)/vy;
			if (t2 < tint)
				tint = t2;
			return (tint > 0.0);
		}
		else
			return false;
	}

	public static point getSegInt(point p1, point p2, point p3, point p4)
	{
		point ans = new point();
		double a = p2.x - p1.x;
		double b = p3.x - p4.x;
		double c = p3.x - p1.x;
		double d = p2.y - p1.y;
		double e = p3.y - p4.y;
		double f = p3.y - p1.y;
		double den = a*e-b*d;
		double num = c*e-b*f;
		if (Math.abs(den) >= 0.000001) {
			double t = num/den;
			ans.x = p1.x + a*t;
			ans.y = p1.y + d*t;
		}
		else if (between(p1.x, p3.x, p4.x)) {
			ans.x = p1.x;
			ans.y = p1.y;
		}
		else {
			ans.x = p2.x;
			ans.y = p2.y;
		}
		return ans;
	}

	public static double calcArea(point p[], int np)
	{
		if (np == 0)
			return 0.0;
		double area = (p[0].x-p[np-1].x)*(p[0].y+p[np-1].y)/2.0;
		for(int i=0; i<np-1; i++) {
			area += (p[i+1].x-p[i].x)*(p[i+1].y+p[i].y)/2.0;
		}
		if (Math.abs(area) <= 0.0000001)
			area = 0.0;
		return area;
	}

	public static double calcPolyInt(asteroid a1, asteroid a2, double t)
	{
		point [] ip = new point[2*MAXS];
		point [] op = new point[2*MAXS];
		for(int i=0; i<2*MAXS; i++) {
			ip[i] = new point();
			op[i] = new point();
		}
		int nip, nop;
		for(int i=0; i<a2.nseg; i++) {
			op[i].x = a2.segs[i].p1.x + t*a2.vx;
			op[i].y = a2.segs[i].p1.y + t*a2.vy;
		}
		nop = a2.nseg;
		for(int i=0; i<a1.nseg; i++) {		// treat a1 as clipping polygon
			for(int j=0; j<nop; j++) {
				ip[j].x = op[j].x;
				ip[j].y = op[j].y;
			}
			nip = nop;
			nop = 0;
			boolean previn = false;
			point prev = new point();
			if (nip > 0) {
				prev.x = ip[nip-1].x;
				prev.y = ip[nip-1].y;
				previn = inside(prev, a1.segs[i]);
			}
			for(int j=0; j<nip; j++) {
				boolean in = inside(ip[j], a1.segs[i]);
				if (in) {
					if(!previn) {
						op[nop++] = getSegInt(prev, ip[j], a1.segs[i].p1, a1.segs[i].p2);
					}
					op[nop].x = ip[j].x;
					op[nop++].y = ip[j].y;
				}
				else if (previn) {
					op[nop++] = getSegInt(prev, ip[j], a1.segs[i].p1, a1.segs[i].p2);
				}
				prev.x = ip[j].x;
				prev.y = ip[j].y;
				previn = in;
			}
		}
		return calcArea(op, nop);
	}

	public static double findMaxOverlapTime(asteroid a1, asteroid a2, double tl, double th)
	{
		double vl = 0.0, vh = 0.0;
		double tm = (tl+th)/2.0;
		double vm = calcPolyInt(a1, a2, tm);
		while (th-tl > 0.0000005) {
			double t1 = (tl+tm)/2.0;
			double v1 = calcPolyInt(a1, a2, t1);
			if (v1 > vm || equal(v1, vm)) {
				th = tm;
				tm = t1;
				vh = vm;
				vm = v1;
			}
			else {
				double t2 = (tm+th)/2.0;
				double v2 = calcPolyInt(a1, a2, t2);
				if (v2 > vm && !equal(v2, vm)) {
					tl = tm;
					tm = t2;
					vl = vm;
					vm = v2;
				}
				else {
					tl = t1;
					th = t2;
					vl = v1;
					vh = v2;
				}
			}
		}
		return tm;
	}

	public static retval getBorderTimes(asteroid a1, asteroid a2)
	{
		double tl = -1, th = -1;
		boolean first = true;
		tint = -1.0;
		for(int i=0; i<a2.nseg; i++) {
			for(int j=0; j<a1.nseg; j++) {
				if (getInitialInt(a1.segs[j].p1, a1.segs[j].p2, a2.segs[i].p1, a2.vx, a2.vy)) {
					if (first) {
						tl = th = tint;
						first = false;
					}
					else if (tint < tl)
						tl = tint;
					else if (tint > th)
						th = tint;
				}
				if (getInitialInt(a2.segs[i].p1, a2.segs[i].p2, a1.segs[j].p1, -a2.vx, -a2.vy)) {
					if (first) {
						tl = th = tint;
						first = false;
					}
					else if (tint < tl)
						tl = tint;
					else if (tint > th)
						th = tint;
				}
			}
		}
		retval val = new retval();
		val.d1 = tl;
		val.d2 = th;
		val.b = !first;
		return val;
	}

	public static void main(String [] args)
	{
		int ncases = 1;
		for(int icase=1; icase<=ncases; icase++) {
			a1 = new asteroid();
			a2 = new asteroid();
			inputAsteroid(a1);
			inputAsteroid(a2);
			a2.vx -= a1.vx;
			a2.vy -= a1.vy;

			retval rv = new retval();
			rv = getBorderTimes(a1, a2);
			boolean b = rv.b;
			double tl = rv.d1, th = rv.d2;
			if (!b)
				System.out.println("never");
			else if (tl == th)
				System.out.printf("%.3f\n", tl);
			else {
				double t = findMaxOverlapTime(a1, a2, tl, th);
				System.out.printf("%.3f\n", t);
			}
		}
	}
}
	
class point
{
	public double x, y;
}

class seg
{
	public seg()
	{
		p1 = new point();
		p2 = new point();
	}

	public point p1, p2;
}

class asteroid
{
	public int nseg;
	public seg [] segs;
	public int vx, vy;
}

class retval
{
	public double d1, d2;
	public boolean b;
}
