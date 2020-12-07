import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class is an "Input Validator" for data files for the "Sumit" problem, which requires reading input
 * containing integers and printing the sum of the non-negative integers in the input.
 * Input is terminated either by a zero or by EOF.
 * 
 * A VIVA-like pattern (accurate except that VIVA doesn't accept "#" comments) specifying the input file format requirements is:
 * <pre>
 * {                                #repeat block
 * [= n (n=0)]                      #0=sentinel (EOF)
 * n:integer                        #each line contains one integer
 * }                                #until EOF
 * </pre>
 * 
 * This input validator follows the <A href=https://clics.ecs.baylor.edu/index.php?title=Problem_format#Input_Validators>CLICS Input Validator specification</a>,
 * meaning that it:
 * <ul>
 *   <li> accepts via stdin a single input data file to be validated;
 *   <li> writes result information to stdout
 *   <li> reports any errors (with the valdator, not with the input data file) to stderr
 *   <li> exits with exit code "42" if the input file passes validation
 * </ul>
 * 
 * If the input file fails validation, the following exit codes are returned:
 * <ul>
 *   <li> 43: input contains data beyond "0" sentinel
 *   <li> 44: input contains a line with other than a single integer
 *   <li> 45: an IOException occurred while reading the input from stdin
 * </ul>
 * 
 * @author john clevenger, PC2 Development Team (pc2@ecs.csus.edu).
 *
 */
public class SumitInputValidator {

    public static void main(String[] args) {
        //read input lines from stdin
        BufferedReader br = new BufferedReader (new InputStreamReader (System.in), 1);
        int lineNum = 1;
        try {
            
            String line = br.readLine();
            int inVal ;
            while(line != null) {           
                inVal = new Integer(line.trim()).intValue();
                if (inVal == 0) {
                    break;
                }
                line = br.readLine();
                lineNum++;
            }
            //try to read another line (to see if there's data past a "0")
            line = br.readLine();
            lineNum++;
            if (line!=null) {
                //input contains data past "0" sentinel
                System.out.println("Input file contains data past zero sentinel, at line " + lineNum);
                System.exit(43);
            }
        
        } catch (NumberFormatException e) {
            System.out.println("Input file contains other than a single integer, at line " + lineNum);
            System.exit(44);
        } catch (IOException e) {
            System.err.println("IOException reading input: " + e);
            System.exit(45);
        }
        
        //if we get here, every line of the file, up to either a "0" sentinel or EOF, contains a single integer
        System.out.println("Input file passes verification");
        System.exit(42);
        
    }

}
