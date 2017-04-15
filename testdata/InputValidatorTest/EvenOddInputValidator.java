import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * This class is an Input Validator for a (hypothetical) contest problem which requires that the judge's input data consist of 
 * alternating positive even and odd integers separated by whitespace. Note that the (hypothetical) problem requirement does NOT include 
 * knowing in advance whether the first number is even or odd; only that, once the first one is read then all the rest must alternate 
 * (even/odd or odd/even, depending on the first value).
 * 
 * The class was developed as a basic test for Input Validator handling in PC2. It follows the "CLICS Problem Package" specification 
 * for Input Validators, which states that Input Validators should receive the file (data) to be validated on their standard input 
 * and should return Exit Code 42 if the input data satisfies the validation rules of the contest problem. Any other Exit Code
 * represents failure of the input data to comply with the validation rules. 
 * 
 * Note that this code does NOT check whether the input data follows the "alternating even-odd" requirement; only that the input consists
 * solely of positive integers.  Checking for alternating even/odd numbers is a function of a problem solution, not an input validator
 * (that is, a valid input file could fail to have alternating integers -- it would be a data file for which the problem solution should
 * return "No, the input doesn't meet the requirement"; but it's still valid input data file).
 * 
 * This implementation returns Exit Code -39 if there is an internal error in the Input Validator.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
public class EvenOddInputValidator {

    public static final int INPUT_VALIDATOR_SUCCESS_EXIT_CODE = 42;

    public static final int INPUT_VALIDATOR_FAILED_EXIT_CODE = 43;

    public static final int INPUT_VALIDATOR_INTERNAL_ERROR_EXIT_CODE = -39;

    /**
     * @param args
     */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        boolean done = false;
        int nextInt;
        int exitCode = INPUT_VALIDATOR_INTERNAL_ERROR_EXIT_CODE;

        // continue as long as the stdin scanner has data
        while (!done) {

            // if there's no more input we're done (and we haven't had an error-exit)
            if (!scanner.hasNext()) {
                
                done = true;
                exitCode = INPUT_VALIDATOR_SUCCESS_EXIT_CODE;
                
            } else {

                // we know the input has SOMETHING; verify it's an INTEGER (the problem spec requires the input consist solely of integers)
                if (!scanner.hasNextInt()) {
                    
                    done = true;
                    exitCode = INPUT_VALIDATOR_FAILED_EXIT_CODE;
                    
                } else {
                    
                    // we know there's an integer next; make sure it is positive (required by the problem spec)
                    nextInt = scanner.nextInt();
                    if (nextInt <= 0) {
                        done = true;
                        exitCode = INPUT_VALIDATOR_FAILED_EXIT_CODE;
                    }
                } //end scanner.hasNextInt()
            } //end scanner.hasNext()
        } //end while

        scanner.close();
        
        String msg = "EvenOddInputValidator returning exit code " + exitCode;
        switch (exitCode) {
            case INPUT_VALIDATOR_SUCCESS_EXIT_CODE:
                msg += " (success; input passes validation)"; 
                break;
            case INPUT_VALIDATOR_FAILED_EXIT_CODE:
                msg += " (input fails validation)";
                break;
            default:
                msg += " (error during input validator execution)";
        }
        
        System.out.println(msg);
        System.err.println(msg);
        System.exit(exitCode);

    }

}
