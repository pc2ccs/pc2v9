import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Practice program, sigma positive integers.
 * 
 * @author pc2@ecs.csus.edu
 */
// Fri Oct 11 16:30:40 PDT 2019

public class IPractice {


    /**
     * Get Current Working Directory.
     * 
     * @return current working directory.
     */
    private static String getCurrentDirectory() {
        File curdir = new File(".");

        try {
            return curdir.getCanonicalPath();
        } catch (Exception e) {
            // ignore exception
            return ".";
        }
    }

    public static void main(String[] args) {
        try {

    		BufferedReader file = new BufferedReader (
    				new InputStreamReader (System.in), 1);
            String line;

            int inval = 0;
            int sum = 0;

            while ((line = file.readLine()) != null) {
                inval = new Integer(line.trim()).intValue();
                sum = 0;

                if (inval == 0)
                    System.exit(0);

                if (inval < 1) {
                    for (int i = 1; i >= inval; i--)
                        sum += i;
                } else {
                    for (int i = 1; i <= inval; i++)
                        sum += i;
                }

                String outString = String.format("N = %-3d    Sum = " + sum, inval);
                System.out.println(outString);

                // System.out.println("N = " + inval + "  Sum = " + sum);
            }

            System.out.println("Did not find trailing zero");
        } catch (Exception e) {
            System.out.println("Possible trouble reading stdin "+e.getMessage());
            System.exit(4);
        }
        System.exit(0);
    }
}

// eof practice.java 
