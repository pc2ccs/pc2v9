import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;


public class Qanat {
	static public double W, H; 
	static public int N;
	static public ArrayList<Double> r;
	static public ArrayList<Double> cost;

	static double cost_r(int n, double r)
	{
	  double l1 = (1 + r) * H + (1 - r) * W;
	  double l2 = r * H;
	  return r * r * cost.get(n - 1) + l1 * l1 / 4 - l2 * l2 / 2;
	}

	static void solve_mathematical(int n)
	{
	  double num = W * W - H * H;
	  double denom = 4 * cost.get(n - 1) + (W - H) * (W - H) - 2 * H * H;
	  r.set(n - 1, num / denom);
	  cost.set(n, cost_r(n, r.get(n - 1)));
	}
			
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		W = in.nextDouble();
		H = in.nextDouble();
	  N = in.nextInt();
	  r = new ArrayList<Double>(Collections.nCopies(N, -1.0));
	  cost = new ArrayList<Double>(Collections.nCopies(N + 1, 0.0));
	  cost.set(0, (W + H) * (W + H) / 4);
	  for (int i = 1; i <= N; i++)
	    solve_mathematical(i);
	  ArrayList<Double> ans = new ArrayList<Double>();
	  double ratio = 1;
	  for (int i = 0; i < N; i++)
	  {
	    ratio *= r.get(N - i - 1);
	    ans.add(ratio * W);
	  }
	  
    System.out.format("%.06f\n", cost.get(N));
    for (int i = ans.size() - 1; i >= 0 && i >= (int)ans.size() - 10; i--)
    {
      System.out.format("%.06f", ans.get(i));
      if (i > 0)
      System.out.print(" ");
    }
    System.out.println();
	  
    in.close();
	}
}
