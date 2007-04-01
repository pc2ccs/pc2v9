package edu.csus.ecs.pc2.core;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.ElementId;

/**
 * 
 * @author pc2@ecs.csus.edu
 * 
 */
// $HeadURL$
public class ElementIdTest extends TestCase {

    /**
     * svn id.
     */
    public static final String SVN_ID = "$Id$";

    /*
     * Test method for 'edu.csus.ecs.pc2.core.ElementId.hashCode()'
     */
    public void testHashCode() {
        ElementId element1 = new ElementId("test Case");
        ElementId element2 = new ElementId("test Case");
        assertFalse("hashCode match", element1.hashCode() == element2
                .hashCode());
    }

    /*
     * Test method for 'edu.csus.ecs.pc2.core.ElementId.toString()'
     */
    public void testToString() {
        ElementId element1 = new ElementId("test Case");
        ElementId element2 = new ElementId("test Case");
        assertFalse("toStrings match", element1.toString().equals(
                element2.toString()));
    }

    /*
     * Test method for 'edu.csus.ecs.pc2.core.ElementId.equals(Object)'
     */
    public void testEqualsObject() {
        ElementId element1 = new ElementId("test Case");
        ElementId element2 = new ElementId("test Case");
        assertFalse("equals match", element1.equals(element2));
    }

}
