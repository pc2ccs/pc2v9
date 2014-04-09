/**
 * @author Godmar
 */
import java.util.*;

public class Casting {
    public static void main(String []av) {
        Scanner _s = new Scanner(System.in);
        int P = _s.nextInt();
        for (int i = 0; i < P; i++) {
            int N = _s.nextInt();
            int B = _s.nextInt();
            String D = _s.next();
            int r = 0;
            for (int j = D.length()-1; j >= 0; j--) {
                int d = (D.charAt(j) - '0');
                r = (r + d) % (B - 1);
            }
            System.out.println(N + " " + r);
        }
    }
}