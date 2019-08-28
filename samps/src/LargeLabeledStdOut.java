// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
/**
 * Produces a large collection of output to stdout with each line having a different beginning label.
 * 
 * @author John
 */
public class LargeLabeledStdOut {

    public static void main(String[] args) {
        
        int outputCharCount = 0;
        int outputLineCount = 0;
        
        while (outputCharCount < 500000  && outputLineCount<10000) {
            
            //output a unique line label
            String lineLabel = "Line " + (++outputLineCount) + ": " ;
            System.out.print (lineLabel);
 
            
            //count the characters output
            outputCharCount += lineLabel.length();
            
            //output 80 additional chars on the line
            for (int i=0; i<8; i++) {
                System.out.print("1234567890");
                outputCharCount += 10;
            }

            System.out.println();

        }

    }

}
