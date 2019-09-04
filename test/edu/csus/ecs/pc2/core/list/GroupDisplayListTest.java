// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.list;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.Group;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class GroupDisplayListTest extends TestCase {
    
    /**
     * Test update, ensure that update will add if group not present.
     */
    public void testUpdate() {
        
        GroupDisplayList displayList = new GroupDisplayList();
        Group group = new Group("Group 1");
        
        displayList.update(group);
        assertEquals("Should have added to list ", 1, displayList.getList().length);

    }

}
