package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.IGroup;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
/**
 * Implementation for IGroup.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class GroupImplementation implements IGroup {

    private String title;

    public GroupImplementation(String title) {
        super();
        this.title = title;
    }

    public GroupImplementation(ElementId groupId, IInternalContest internalContest) {
        this(internalContest.getGroup(groupId), internalContest);
    }

    public GroupImplementation(Group group, IInternalContest contest) {
        title = group.getDisplayName();
    }

    public String getTitle() {
        return title;
    }

}
