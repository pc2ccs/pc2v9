import java.util.InputMismatchException;
import java.util.Scanner;


/**
 * This class is an Input Validator for a (hypothetical) contest problem which requires that the judge's input 
 * data consist of alternating even and odd integers separated by whitespace. Note that the (hypothetical)
 * problem requirement does NOT include knowing in advance whether the first number is even or odd;
 * only that, once the first one is read then all the rest must alternate (even/odd or odd/even, 
 * depending on the first value).
 * 
 * The class was developed as a basic test for Input Validator handling in PC2.
 * It follows the "CLICS Problem Package" specification for Input Validators, which states
 * that Input Validators should receive the file (data) to be validated on their standard input
 * and should return Exit Code 42 if the input data satisfies the validation rules of the contest problem.  
 * Any other Exit Code represents failure of the input data to comply with the validation rules.
 * This implementation returns Exit Code -39 if there is an internal error in the Input Validator.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
public class OddEvenInputValidator {

    public static final int INPUT_VALIDATOR_SUCCESS_EXIT_CODE = 42;
    
    public static final int INPUT_VALIDATOR_FAILED_EXIT_CODE = 43;
    
    public static final int INPUT_VALIDATOR_INTERNAL_ERROR_EXIT_CODE = -39;

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);
        
        boolean done = false;
        boolean first = true;
        boolean lookingForOdd = true;   //default initialization; required but not used
        int nextInt ;
        int exitCode = INPUT_VALIDATOR_INTERNAL_ERROR_EXIT_CODE;
        
        //continue as long as the stdin scanner has data
        while (!done) {
            
            //if there's no more input we're done (and we haven't had an error-exit)
            if (!scanner.hasNext()) {
                done = true;
                exitCode = INPUT_VALIDATOR_SUCCESS_EXIT_CODE ;
            } else {
            
                try {
                    // get the next integer
                    nextInt = scanner.nextInt();

                    // if this is the first time, indicate whether we're starting with odd or even
                    if (first) {
                        if (nextInt % 2 != 0) {
                            lookingForOdd = true;
                        } else {
                            lookingForOdd = false;
                        }
                        first = false;
                    }
                    
                    //see if the current token meets the even/odd requirement
                    if (lookingForOdd) {
                        if (nextInt % 2 == 0) {
                            //we needed an odd but got an even
                            exitCode = INPUT_VALIDATOR_FAILED_EXIT_CODE;
                            done = true;
                        }
                    } else {
                        if (nextInt % 2 != 0) {
                            //we needed an even but got an odd
                            exitCode = INPUT_VALIDATOR_FAILED_EXIT_CODE;
                            done = true;
                        }
                    }
                    
                    //if we get here then all input so far matches the even/odd alternation requirement
                    
                    //toggle requirement for next loop
                    lookingForOdd = !lookingForOdd;

                } catch (InputMismatchException e) {
                    
                    exitCode = INPUT_VALIDATOR_FAILED_EXIT_CODE;
                    done = true;

                } catch (IllegalStateException e) {
                    
                    exitCode = INPUT_VALIDATOR_INTERNAL_ERROR_EXIT_CODE;
                    done = true;
                }
            }
        }
        
        scanner.close();
        System.out.println ("OddEvenInputValidator returning Exit Code " + exitCode);
        System.err.println ("OddEvenInputValidator returning Exit Code " + exitCode);
        System.exit(exitCode) ;

    }

}
