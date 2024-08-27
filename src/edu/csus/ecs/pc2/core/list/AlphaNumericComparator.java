// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compare the two strings as numbers
 * <P>
 * Compare things that may be words or numbers
 *
 * @author John Buck
 */

// $HeadURL$
// $Id$

public class AlphaNumericComparator implements Comparator<String>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(String ValOne, String ValTwo) {
        boolean b1Alpha = false;
        boolean b2Alpha = false;
        int cmpVal1 = 0;
        int cmpVal2 = 0;

        try {
            cmpVal1 = Integer.parseInt(ValOne);
        } catch(Exception exception) {
            b1Alpha = true;
        }
        try {
            cmpVal2 = Integer.parseInt(ValTwo);
        } catch(Exception exception) {
            b2Alpha = true;
        }
        if(b1Alpha) {
            // If both are alpha, then just compare the strings.
            if(b2Alpha) {
                return(ValOne.compareToIgnoreCase(ValTwo));
            }
            // ValTwo is a number, and it comes before the alpha
            return(1);
        }
        if(b2Alpha) {
            // ValOne is a number, it comes before the alpha string
            return(-1);
        }
        return(cmpVal1 - cmpVal2);
    }
}
