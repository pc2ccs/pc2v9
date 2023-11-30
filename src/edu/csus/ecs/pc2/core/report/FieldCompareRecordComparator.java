// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.util.Comparator;

import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.imports.clics.FieldCompareRecord;

/**
 * Compare by key then by fieldName.
 * 
 * @author Douglas A. Lane, PC^2 team pc2@ecs.csus.edu
 */
public class FieldCompareRecordComparator implements Comparator<FieldCompareRecord> {

    @Override
    public int compare(FieldCompareRecord o1, FieldCompareRecord o2) {

        if (o1.getKey().equals(o2.getKey())) {
            return StringUtilities.nullSafeCompareTo(o1.getFieldName(), o2.getFieldName());
        } else {
            return StringUtilities.nullSafeCompareTo(o1.getKey(), o2.getKey());
        }
    }

}
