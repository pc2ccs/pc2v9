package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.Group;

/**
 * Maintain a list of {@link edu.csus.ecs.pc2.core.model.Group}s.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class GroupList extends ElementList {

    /**
     *
     */
    private static final long serialVersionUID = -4214802174057330733L;

    public static final String SVN_ID = "$Id$";

    /**
     *
     * @param group
     *            {@link Group} to be added.
     */
    public void add(Group group) {
        super.add(group);

    }

    /**
     * Return list of Groups.
     *
     * @return list of {@link Group}.
     */
    public Group[] getList() {
        Group[] theList = new Group[size()];

        if (theList.length == 0) {
            return theList;
        }
        return (Group[]) values().toArray(new Group[size()]);
    }

}
