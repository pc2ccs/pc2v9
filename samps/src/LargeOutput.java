// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
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
