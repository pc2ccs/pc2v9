package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compare the name of one team with another.
 * <P>
 * For strings that are identical, except the number at the end of a string,
 * sort the strings using that number.
 * <P>
 * This is not a lexical sort. With a lexical sort, team10 will be before team2.
 * With this comparator team2 will always be before team10.
 * <br>
 * The logic is: find identical strings except the trailing number(s). 
 * Find the last non-digit character and extract the number after
 * that character.  The compare the numbers extracted from each string.
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
// $Id$

public class AccountNameComparator implements Comparator<String>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5842577552526394242L;

    public int compare(String displayNameOne, String displayNameTwo) {

        if (displayNameOne.charAt(0) != displayNameTwo.charAt(0)) {
            // if first character different then just regular sort
            return displayNameOne.compareTo(displayNameTwo);
        }

        char lastCharOne = displayNameOne.charAt(displayNameOne.length() - 1);
        char lastCharTwo = displayNameTwo.charAt(displayNameTwo.length() - 1);

        if (Character.isDigit(lastCharOne) && Character.isDigit(lastCharTwo)) {

            int lastNumberIndexOne = findLastNumberIndex(displayNameOne);
            int lastNumberIndexTwo = findLastNumberIndex(displayNameTwo);
            
            if (lastNumberIndexOne == lastNumberIndexTwo && lastNumberIndexOne > 0) {

                // If the text preceeding the final number on the string are different, just compare
                if (!displayNameOne.substring(0, lastNumberIndexOne + 1).equals(displayNameTwo.substring(0, lastNumberIndexOne + 1))) {
                    return displayNameOne.compareTo(displayNameTwo);
                }
                
                // Else compare the number on the end of the strings.
                int numberOne = Integer.parseInt(displayNameOne.substring(lastNumberIndexOne+1));
                int numberTwo = Integer.parseInt(displayNameTwo.substring(lastNumberIndexOne+1));
                return numberOne - numberTwo;
            }

            return displayNameOne.compareTo(displayNameTwo);

        } else {
            // no last digit as a character do regular sort
            return displayNameOne.compareTo(displayNameTwo);
        }
    }

    private int findLastNumberIndex(String name) {

        for (int i = name.length() - 1; i > 0; i--) {

            if (!Character.isDigit(name.charAt(i))) {
                return i;
            }
        }
        return 0;
    }
}
