import java.util.Scanner;

public class artJB
{
    public static void main(String [] args)
    {
        long p, a, b, c, d;
        int n;
        Scanner in = new Scanner(System.in);

        p = in.nextLong();
        a = in.nextLong();
        b = in.nextLong();
        c = in.nextLong();
        d = in.nextLong();
        n = in.nextInt();
        double smallest = Math.sin(a*n+b) + Math.cos(c*n+d);
        double maxdec = 0.0;
        int iend=0, istart=0, ismall = n;
        for(int i=n-1; i>=1; i--) {
            double val = Math.sin(a*i+b) + Math.cos(c*i+d);
            if (val - smallest >= maxdec) {
                maxdec = val - smallest;
                istart = i;
                iend = ismall;
            }
            else if (val < smallest) {
                smallest = val;
                ismall = i;
            }
        }
//        if (maxdec == 0.0)
//            System.out.println("0 0 0");
//        else
//            System.out.println(p*maxdec + " " + istart + " " + iend);
            System.out.println(p*maxdec);
    }
}

