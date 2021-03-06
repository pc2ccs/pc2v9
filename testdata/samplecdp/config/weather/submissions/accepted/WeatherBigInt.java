// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
// Java Solution to Weather Report (using BigInteger for larger N)
// 2015 ICPC World Finals
// Michael Goldwasser

import java.util.Scanner;
import java.math.BigInteger;
import java.util.PriorityQueue;
import static java.lang.Math.pow;

public class WeatherBigInt {

    public static boolean debug = false;

    static int n;                        // number of measurements
    static double[] p = new double[4];   // probabilities
    static BigInteger factn;             // n! (calculated only once)

    public static void main(String[] args) {
        if (args.length > 0) debug = true;
        Scanner in = new Scanner(System.in);
        n = in.nextInt();
        factn = fact(n);
        for (int j=0; j<4; j++)
            p[j] = in.nextDouble();
        System.out.printf("%.6f\n", solve());
    }

    public static BigInteger fact(int k) {
        BigInteger result = BigInteger.ONE;
        while (k>0)
            result = result.multiply(BigInteger.valueOf(k--));
        return result;
    }

    public static BigInteger combinations(int a, int b, int c) {
        BigInteger result = factn;
        result = result.divide(fact(a));
        result = result.divide(fact(b));
        result = result.divide(fact(c));
        result = result.divide(fact(n-a-b-c));
        return result;
    }

    static double solve() {
        PriorityQueue<Bundle> queue = new PriorityQueue<Bundle>();
        // initialize
        for (int a=0; a <= n; a++)
            for (int b=0; b <= n-a; b++)
                for (int c=0; c <= n-a-b; c++) {
                    int d = n-a-b-c;
                    Bundle temp = new Bundle();
                    temp.prob = pow(p[0],a) * pow(p[1],b) * pow(p[2],c) * pow(p[3],d);
                    temp.bits = 0;
                    temp.mult = combinations(a,b,c);
                    if (debug) {
                        System.out.println("created bundle for counts: " + a + " " + b + " " + c
                                           + " " + (n-a-b-c) + " --> " + temp);
                    }
                    queue.add(temp);
                }

        while (queue.size() > 1 || queue.peek().mult.compareTo(BigInteger.ONE) > 0) {
            if (debug) System.out.println("Queue size currently " + queue.size());
            Bundle first = queue.peek();
            queue.poll();
            if (first.mult.compareTo(BigInteger.ONE) > 0) {
                queue.add(first.reduce());
                if (first.mult.compareTo(BigInteger.ZERO) > 0)
                    queue.add(first);
            } else {
                Bundle second = queue.peek();
                queue.poll();
                queue.add(first.combine(second));
                if (second.mult.compareTo(BigInteger.ZERO) > 0)
                    queue.add(second);
            }
        }
        return queue.peek().bits;

    }

}

// a single Bundle represents many identical trees
class Bundle implements Comparable<Bundle> {
    BigInteger mult;   // how many of these trees are in the queue
    double prob;   // probability that weather sample lies in one such tree tree
    double bits;   // contribution to overall expected bitlength for one such tree

    // return new bundle created by combining all pairs from this bundle
    // (and as side effect, leave this bundle with count 0 or 1)
    Bundle reduce() {
        if (WeatherBigInt.debug)
            System.out.print(this + "                              --> ");
        Bundle result = new Bundle();
        result.prob = 2 * prob;
        result.mult = mult.divide(BigInteger.valueOf(2));
        mult = mult.remainder(BigInteger.valueOf(2));
        result.bits = 2*bits + result.prob;  // one extra bit for every entry of tree
        if (WeatherBigInt.debug) {
            System.out.print(result);
            if (mult.compareTo(BigInteger.ZERO) > 0)
                System.out.print(" + " + this);
            System.out.println();
        }
        return result;
    }
  
    // create bundle by joining one tree of this with one tree of other
    // (and as side effect, reduces count of other bundle; we assume this is depleted)
    Bundle combine(Bundle other) {
        if (WeatherBigInt.debug) System.out.print(this + " + " + other + " --> ");
        Bundle result = new Bundle();
        other.mult = other.mult.subtract(BigInteger.ONE);    // we're using up one
        result.mult = BigInteger.ONE;
        result.prob = prob + other.prob;
        result.bits = bits + other.bits + result.prob;
        if (WeatherBigInt.debug) {
            System.out.print(result);
            if (other.mult.compareTo(BigInteger.ZERO) > 0)
                System.out.print(" + " + other);
            System.out.println();
        }
        return result;
    }

    // Bundle's are ordered by probability
    public int compareTo(Bundle other) {
        if (prob < other.prob)
            return -1;
        else if (prob == other.prob)
            return 0;
        else
            return 1;
    }

    // for debug only
    public String toString() {
        return mult + "x{p:" + prob + ", b:" + bits + "}";
    }
}
