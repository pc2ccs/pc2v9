/**
 * 
 */
package edu.csus.ecs.pc2.core.model;

import java.util.Comparator;

/**
 * Sorts Profile by (1) name then (2) description then (3) createDate.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfileComparatorByName implements Comparator<Profile> {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Profile o1, Profile o2) {
        int l1 = o1.getName().compareTo(o2.getName());
        if (l1 == 0) {
            int l2 = o1.getDescription().compareTo(o2.getDescription());
            if (l2 == 0) {
                return o1.getCreateDate().compareTo(o2.getCreateDate());
            } else {
                return l2;
            }
        } else {
            return l1;
        }
    }
}
