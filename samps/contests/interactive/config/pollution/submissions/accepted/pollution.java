import java.util.Scanner;
import java.text.DecimalFormat;

public class pollution
{
	public static final int MAX = 100;
	public static Point[] poly = new Point[MAX];
	public static Point[] isect = new Point[3*MAX];

	public static void printPoint(Point p)
	{
		System.out.print("(" + p.x + "," + p.y + ")");
	}

	public static boolean between(double a, double b, double c)
	{
		if (a > b && a < c)
			return true;
		return (a > c && a < b);
	}

	public static int intersect(Point p1, Point p2, double r, Point ints[])
	{
		double dx = p1.x-p2.x;
		double dy = p1.y-p2.y;
		if (dx == 0) {
			if (p1.inCircle && !p2.inCircle || !p1.inCircle && p2.inCircle) {
				ints[0] = new Point();
				ints[0].x = p1.x;
				ints[0].y = Math.sqrt(r*r-p1.x*p1.x);
				ints[0].onCircle = true;
				return 1;
			}
			else
				return 0;
		}
		else if (p1.inCircle && p2.inCircle)
			return 0;
		else {
			double m = dy/dx;
			double b = p1.y - m*p1.x;
			double disc = 4*m*m*b*b - 4*(1+m*m)*(b*b-r*r);
			if (Math.abs(disc) <= 0.01) {
				System.out.print("WARNING: ");
				printPoint(p1);
				printPoint(p2);
				System.out.println(" close to tangent line");
				return 0;
			}
			if (disc < 0)
				return 0;
			disc = Math.sqrt(disc);
			double x1 = (-2*m*b + disc)/(2*(1+m*m));
			double x2 = (-2*m*b - disc)/(2*(1+m*m));
			if ((x2-x1)*(p2.x-p1.x) < 0) {
				double tmp = x1;
				x1 = x2;
				x2 = tmp;
			}
			int ni = 0;
			if (between(x1, p1.x, p2.x)) {
				ints[ni] = new Point();
				ints[ni].x = x1;
				ints[ni].y = Math.sqrt(r*r-x1*x1);
				ints[ni].onCircle = true;
				ni++;
			}
			if (between(x2, p1.x, p2.x)) {
				ints[ni] = new Point();
				ints[ni].x = x2;
				ints[ni].y = Math.sqrt(r*r-x2*x2);
				ints[ni].onCircle = true;
				ni++;
			}
			if (ni == 2) {
				ints[2] = new Point();
				ints[2].x = ints[1].x;
				ints[2].y = ints[1].y;
				ints[2].onCircle = ints[1].onCircle;
				ints[1].x = (ints[0].x + ints[1].x)/2.0;
				ints[1].y = (ints[0].y + ints[1].y)/2.0;
				ints[1].onCircle = false;
				ni = 3;
			}
			return ni;
		}
	}

	public static double trapArea(Point p1, Point p2, double r)
	{
		double area = (p2.x - p1.x)*(p2.y+p1.y)/2.0;
		if (p1.onCircle && p2.onCircle) {
			double cosa = (p1.x*p2.x + p1.y*p2.y)/r/r;
			double sina = Math.sqrt(1.0 - cosa*cosa);
			double a = Math.atan(sina/cosa);
			if (a == 0)
				a = Math.PI;
			else if (a < 0)
				a += Math.PI;
			double extra = (a - sina)*r*r/2.0;
			if (area*extra > 0)
				area += extra;
			else
				area -= extra;
		}
		return area;
	}

	public static void main(String [] args)
	{
		int np;
		double r;
		Scanner in = new Scanner(System.in);

		np = in.nextInt();
		r = in.nextDouble();
		for(int i=0; i<np; i++) {
			double x, y;
			x = in.nextDouble();
			y = in.nextDouble();
			poly[i] = new Point();
			poly[i].x = x;
			poly[i].y = y;
			poly[i].inCircle = (r*r - x*x - y*y > 0);
			poly[i].onCircle = false;
		}
		Point plast = poly[np-1];
		Point[] ints = new Point[3];
		int ni = 0;
		for(int i=0; i<np; i++) {
			Point p = poly[i];
			if (plast.inCircle && p.inCircle)
				isect[ni++] = p;
			else {
				int nint = intersect(plast, poly[i], r, ints);
				if (plast.inCircle)
					isect[ni++] = ints[0];
				else if (p.inCircle) {
					isect[ni++] = ints[0];
					isect[ni++] = p;
				}
				else {
					for(int j = 0; j<nint; j++)  {
						isect[ni++] = ints[j];
					}
				}
			}
			plast = poly[i];
		}
		double area = 0.0;
		if (ni > 0) {
			if (ni < 3)
				System.out.println("ERROR: less than 3 intersection points");
			area = trapArea(isect[ni-1], isect[0], r);
			for(int i=0; i<ni-1; i++) {
				area += trapArea(isect[i], isect[i+1], r);
			}
			if (area < 0.0)
				area *= -1;
		}
		DecimalFormat form = new DecimalFormat("0.000");
		System.out.println(form.format(area));
	}
}

class Point {
	double x, y;
	boolean onCircle;
	boolean inCircle;
}
