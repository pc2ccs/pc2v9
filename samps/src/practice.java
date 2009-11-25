import java.io.File;
import java.io.RandomAccessFile;

/**
 * Practice program, sigma positive integers.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class practice {

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
        String filename = "practice.dat";
        try {

            RandomAccessFile file = new RandomAccessFile(filename, "r");
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
                System.out.println("N = " + inval + "  Sum = " + sum);
            }

            System.out.println("Did not find trailing zero");
        } catch (Exception e) {
            System.out.println("Possible trouble reading " + filename + " in " + getCurrentDirectory());
        }
        System.exit(4);
    }
}

// eof practice.java $Id$
