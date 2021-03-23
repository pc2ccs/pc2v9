package edu.csus.ecs.pc2.core.report;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.Group;

/**
 * Compare by group id ascending.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class GroupComparatorById implements Comparator<Group>, Serializable {

    private static final long serialVersionUID = -8186853692614143780L;

    public int compare(Group group1, Group group2) {
        return group1.getGroupId() - group2.getGroupId();
    }

}
