import java.io.*;
import java.util.Scanner;

class Complex {
	double re, im;
	Complex(double r, double i) { re = r; im = i; }
	Complex(Complex c) { re = c.re; im = c.im; }
	void add(Complex c) { re += c.re; im += c.im; }
	void sub(Complex c) { re -= c.re; im -= c.im; }
	void mul(Complex c) { 
		double r = re*c.re - im*c.im;
		im = re*c.im + c.re*im;
		re = r;
	}
}

public class per_fft {
	static final double tau = 4.0*Math.acos(0.0);

	static void fft(Complex x[], Complex y[], int d, int sgn) {
		int N = 1<<d;
		if (N == 1) {
			y[0] = x[0];
			return;
		}
		Complex sx[] = new Complex[1<<d-1];
		Complex sy[] = new Complex[1<<d-1];
		for (int j = 0; j < N/2; ++j) sx[j] = x[2*j];
		fft(sx, sy, d-1, sgn);
		for (int i = 0; i < N/2; ++i)  {
			y[i] = new Complex(sy[i]); 
			y[i+N/2] = sy[i];
		}
		for (int j = 0; j < N/2; ++j) sx[j] = x[2*j+1];
		fft(sx, sy, d-1, sgn);
		for (int i = 0; i < N/2; ++i) {
			double v = tau*sgn*i/N;
			sy[i].mul(new Complex(Math.cos(v), Math.sin(v)));
			y[i].add(sy[i]);
			y[i+N/2].sub(sy[i]);
		}
	}
	
	public static void main(String args[]) throws IOException {
		int d = 20, N = 1<<d, n = (1<<d-1)-1;
		Complex div[] = new Complex[N];
		Complex fdiv[] = new Complex[N];
		for (int i = 0; i < N; ++i) div[i] = new Complex(0, 0);
		for (int i = 1; i <= n; ++i) {
			++div[i].re;
			for (int j = i+i; j <= n; j += i)
				++div[j].re;
		}
		fft(div, fdiv, d, 1);
		for (int i = 0; i < N; ++i) fdiv[i].mul(fdiv[i]);
		fft(fdiv, div, d, -1);
		Scanner sc = new Scanner(System.in);
		n = sc.nextInt();
		for (int i = 0; i < n; ++i) {
			int lo = sc.nextInt(), hi = sc.nextInt(), r =  0, a = 0;
			for (int x = lo; x <= hi; ++x) {
				int w = (int)(div[x].re/N+0.5);
				if (w > r) {
					r = w;
					a = x;
				}
			}
			System.out.println(a + " " + r);
		}
	}
}