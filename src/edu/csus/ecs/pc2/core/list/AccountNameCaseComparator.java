// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;
import java.text.Normalizer;

/**
 * Compare the name of one team with another, ignoring case.
 * <P>
 * For strings that are identical (ignoring case), except the number at the end
 * of a string, sort the strings using that number.
 * <P>
 * This is not a lexical sort. With a lexical sort, team10 will be before team2.
 * With this comparator team2 will always be before team10.
 * <br>
 * The logic is: find identical strings except the trailing number(s). 
 * Find the last non-digit character and extract the number after
 * that character.  Then compare the numbers extracted from each string.
 *
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
// $Id$

public class AccountNameCaseComparator implements Comparator<String>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5842577552526394242L;

    public int compare(String displayNameOne, String displayNameTwo) {

        if (displayNameOne.length() == 0 || displayNameTwo.length() == 0) {
            if (displayNameOne.length() == 0 && displayNameTwo.length() == 0) {
                return 0;
            } else {
                if (displayNameOne.length() == 0) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }

        /**
         * strip accents off string to make them accent insensitive
         */
        displayNameOne = Normalizer.normalize(displayNameOne, Normalizer.Form.NFD);
        displayNameTwo = Normalizer.normalize(displayNameTwo, Normalizer.Form.NFD);
        /*
         * Perform quick check to see if the 2 strings have a chance of being in the same ballpark
         */
        if (Character.toLowerCase(displayNameOne.charAt(0)) == Character.toLowerCase(displayNameTwo.charAt(0))) {
           
            char lastCharOne = displayNameOne.charAt(displayNameOne.length() - 1);
            char lastCharTwo = displayNameTwo.charAt(displayNameTwo.length() - 1);
            
            if (Character.isDigit(lastCharOne) && Character.isDigit(lastCharTwo)) {
    
                int lastNumberIndexOne = findLastNumberIndex(displayNameOne);
                int lastNumberIndexTwo = findLastNumberIndex(displayNameTwo);
                int nCmpResult;
               
                if (lastNumberIndexOne == lastNumberIndexTwo && lastNumberIndexOne > 0) {
    
                    nCmpResult = displayNameOne.substring(0, lastNumberIndexOne + 1).compareToIgnoreCase(displayNameTwo.substring(0, lastNumberIndexOne + 1));
                    // If the text preceeding the final number on the string are different, just compare
                    if (nCmpResult != 0) {
                        return nCmpResult;
                    }
                    
                    // Else compare the number on the end of the strings.
                    // Guaranteed for the most part, not to cause a parse integer exception
                    int numberOne = Integer.parseInt(displayNameOne.substring(lastNumberIndexOne+1));
                    int numberTwo = Integer.parseInt(displayNameTwo.substring(lastNumberIndexOne+1));
                    return numberOne - numberTwo;
                }
            }
        }
        /*
         * Default is to just compare ignoring case.
         */
        return displayNameOne.compareToIgnoreCase(displayNameTwo);
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
