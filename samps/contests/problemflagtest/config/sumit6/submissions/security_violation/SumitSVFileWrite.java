import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

// Thu Oct  2 20:25:28 PDT 2003

/**
 * Sample sumit that also writes to a file.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class SumitSVFileIO {
    
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in), 1);

            String line;
            int sum = 0;
            int rv = 0;
            while ((line = br.readLine()) != null) {
                rv = new Integer(line.trim()).intValue();
                if (rv > 0)
                    sum = sum + rv;
                // System.out.println(line);
            }

            sum += 1000;

            System.out.print("The sum of the integers is ");
            System.out.println(sum);

            // Write to file

            String outfilename = "SumitSVFileIOFile.txt";

            PrintWriter printWriter = new PrintWriter(new FileOutputStream(outfilename, false), true);

            printWriter.print("The sum of the integers is ");
            printWriter.println(sum);
            printWriter.close();

        } catch (Exception e) {
            System.err.println("Message: " + e.getMessage());
        }
    }
}

// eof isumit.java $Id: isumit.java 1962 2009-11-25 03:42:12Z boudreat $

