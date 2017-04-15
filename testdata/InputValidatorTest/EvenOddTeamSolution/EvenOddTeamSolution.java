import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * This class is solution for a (hypothetical) contest problem which requires that the judge's input data consist of 
 * alternating even and odd integers separated by whitespace. Note that the (hypothetical) problem requirement does NOT include 
 * knowing in advance whether the first number is even or odd; only that, once the first one is read then all the rest must alternate 
 * (even/odd or odd/even, depending on the first value).
 * 
 * @author pc2@ecs.csus.edu
 *
 */
public class EvenOddTeamSolution {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        boolean done = false;
        boolean first = true;
        boolean lookingForOdd = true; // default initialization; required but not used
        int nextInt;

        // continue as long as the stdin scanner has data
        while (!done) {

            // if there's no more input we're done (and we've seen nothing but good input, so print "yes")
            if (!scanner.hasNext()) {
                done = true;
                System.out.println("Yes");
            } else {

                // we know the input has SOMETHING; verify it's an INTEGER (the problem spec requires the input consist solely of integers)
                if (!scanner.hasNextInt()) {

                    done = true;
                    System.out.println("Illegal (non-integer) input!");

                } else {

                    // we know there's an integer next; check whether it matches the even-odd sequence
                    nextInt = scanner.nextInt();

                    // make sure the integer is positive (required by the problem spec)
                    if (nextInt <= 0) {
                        done = true;
                        System.out.println("Illegal input -- integer < 0!");
                    } else {

                        // if this is the first time, indicate whether we're starting with odd or even
                        if (first) {
                            if (nextInt % 2 != 0) {
                                lookingForOdd = true;
                            } else {
                                lookingForOdd = false;
                            }
                            first = false;
                        }

                        // see if the current token meets the even/odd requirement
                        if (lookingForOdd) {
                            if (nextInt % 2 == 0) {
                                // we needed an odd but got an even
                                System.out.println("No");
                                done = true;
                            }
                        } else {
                            if (nextInt % 2 != 0) {
                                // we needed an even but got an odd
                                System.out.println("No");
                                done = true;
                            }
                        }

                        // if we get here then all input so far is valid and matches the even/odd alternation requirement

                        // toggle requirement for next loop
                        lookingForOdd = !lookingForOdd;
                    }

                } // end scanner.hasNextInt()
            } // end scanner.hasNext()
        } // end while

        scanner.close();
        System.exit(0);

    }

}
