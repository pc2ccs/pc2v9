// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import javax.swing.JLabel;

/**
 * Compare the two strings as numbers
 * <P>
 * Simply convert to integers and compare
 *
 * @version $Id$
 * @author John Buck
 */

// $HeadURL$
// $Id$

public class LabelToDoubleComparator implements Comparator<JLabel>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(JLabel LabOne, JLabel LabTwo) {
        String valOne = LabOne.getText();
        String valTwo = LabTwo.getText();
        boolean b1Alpha = false;
        boolean b2Alpha = false;
        double cmpVal1 = 0;
        double cmpVal2 = 0;

        try {
            cmpVal1 = Double.parseDouble(valOne);
        } catch(Exception exception) {
            b1Alpha = true;
        }
        try {
            cmpVal2 = Double.parseDouble(valTwo);
        } catch(Exception exception) {
            b2Alpha = true;
        }
        if(b1Alpha) {
            // If both are alpha, then just compare the strings.
            if(b2Alpha) {
                return(valOne.compareToIgnoreCase(valTwo));
            }
            // ValTwo is a number, and it comes before the alpha
            return(1);
        }
        if(b2Alpha) {
            // ValOne is a number, it comes before the alpha string
            return(-1);
        }
        double dResult = cmpVal1 - cmpVal2;
        if(dResult < 0) {
            return(-1);
        } else if(dResult > 0) {
            return(1);
        }
        return(0);
    }
}
