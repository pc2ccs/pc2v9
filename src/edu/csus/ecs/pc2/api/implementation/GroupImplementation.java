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

    private String name;
    
    private ElementId elementId;

    public GroupImplementation(String name) {
        super();
        this.name = name;
    }

    public GroupImplementation(ElementId groupId, IInternalContest internalContest) {
        this(internalContest.getGroup(groupId), internalContest);
    }

    public GroupImplementation(Group group, IInternalContest contest) {
        name = group.getDisplayName();
        elementId = group.getElementId();
    }

    public String getName() {
        return name;
    }
    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof GroupImplementation) {
            GroupImplementation groupImplementation = (GroupImplementation) obj;
            return (groupImplementation.elementId.equals(elementId));
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return elementId.toString().hashCode();
    }
  

}
