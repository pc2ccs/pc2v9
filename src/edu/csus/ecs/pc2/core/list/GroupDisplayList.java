package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.Group;

/**
 * Maintain a list of {@link edu.csus.ecs.pc2.core.model.Group}s that are displayed.
 * 
 * Contains a list of Group classes, in order, to display for the users.
 *
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class GroupDisplayList extends ElementDisplayList {

    /**
     *
     */
    private static final long serialVersionUID = -289689878049427570L;

    public static final String SVN_ID = "$Id$";

    public void addElement(Group group) {
        super.addElement(group);
    }

    public void insertElementAt(Group group, int idx) {
        super.insertElementAt(group, idx);
    }

    public void update(Group group) {
        for (int i = 0; i < size(); i++) {
            Group listGroup = (Group) elementAt(i);

            if (listGroup.getElementId().equals(group.getElementId())) {
                setElementAt(group, i);
            }
        }
    }

    /**
     * Get a sorted list of Groups.
     * 
     * @return the array of Groups
     */
    public Group[] getList() {
        if (size() == 0) {
            return new Group[0];
        } else {
            return (Group[]) this.toArray(new Group[this.size()]);
        }
    }

}
