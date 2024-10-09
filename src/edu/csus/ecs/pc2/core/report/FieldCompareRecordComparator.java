// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.util.Comparator;

import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.imports.clics.FieldCompareRecord;

/**
 * Compare by State (type of error), then key then by fieldName.
 *
 * @author Douglas A. Lane, PC^2 team pc2@ecs.csus.edu
 */
public class FieldCompareRecordComparator implements Comparator<FieldCompareRecord> {

    @Override
    public int compare(FieldCompareRecord o1, FieldCompareRecord o2) {

        // Reverse sort by state number, since we want the non-matches first, then the missings.
        int cmpResult = o2.getState().ordinal() - o1.getState().ordinal();
        if(cmpResult == 0) {
            String k1 = o1.getKey();
            String k2 = o2.getKey();

            // Paranoia with the keys
            if(k1 == null){
                if(k2 == null) {
                    return(0);
                }
                return(-1);
            } else if(k2 == null) {
                return(1);
            }
            if (o1.getKey().equals(o2.getKey())) {
                cmpResult = StringUtilities.nullSafeNaturalCompareTo(o1.getFieldName(), o2.getFieldName(), false);
            } else {
                cmpResult = StringUtilities.nullSafeNaturalCompareTo(o1.getKey(), o2.getKey(), false);
            }
        }
        return(cmpResult);
    }

}
