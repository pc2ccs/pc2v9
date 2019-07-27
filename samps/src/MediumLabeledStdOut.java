/**
 * Produces a medium-sized collection of output to stdout with each line having a different begining label.
 * "Medium-sized" is define relative to the program "LargeLabeledStdout"...
 * 
 * @author John
 */
public class MediumLabeledStdOut {

    public static void main(String[] args) {
        
        int outputCharCount = 0;
        int outputLineCount = 0;
        
        while (outputCharCount < 100000  && outputLineCount<5000) {
            
            //output a unique line label
            String lineLabel = "Line " + (++outputLineCount) + ": " ;
            System.out.print (lineLabel);
            
            //count the characters output
            outputCharCount += lineLabel.length();
            
            //output 80 additional chars on the line
            for (int i=0; i<4; i++) {
                System.out.print("1234567890");
                outputCharCount += 10;
            }

            System.out.println();
        }

    }

}
