/**
 * 
 */

/**
 * @author Troy
 * $Id$
 */
public class LargeOutput {

    /**
     * @param args
     */
    public static void main(String[] args) {
        int outputCount = 0;
        while (outputCount < 500000) {
            System.out.print("1234567890");
            System.err.print("1234567890");
            outputCount += 10;
            if (outputCount % 80 == 0) {
                System.out.println();
                System.err.println();
            }
        }

    }

}
