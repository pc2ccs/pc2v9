package edu.csus.ecs.pc2.core.list;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.Group;

/**
 *  Unit test.
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class GroupListTest extends TestCase {

    public void testUpdateGroup() {

        GroupList list = new GroupList();
        Group group = new Group("Foo");
        list.update(group);
        assertEquals("Expecting update to add to list ",1, list.getList().length);
    }
}
