package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.Group;

/**
 * Sort group by name then (external) id.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class GroupComparator implements Comparator<Group>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8186853692614143780L;

    public int compare(Group group1, Group group2) {

        if (group1.getDisplayName().equals(group2.getDisplayName())) {
            return group1.getGroupId() - group1.getGroupId();
        } else {
            return group1.getDisplayName().compareTo(group2.getDisplayName());
        }
    }
}
