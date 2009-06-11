package edu.csus.ecs.pc2.core;

import java.util.HashMap;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.ElementId;

/**
 * Unit tests for ElementId.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ElementIdTest extends TestCase {

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
        
        assertEquals("elements should be equal", element2, element2);
        
        ElementId element3 = element1;
        assertEquals("elements should be equal", element1, element3);
    }
    
    /**
     * Test that each successive ElementId is relatively random
     */
    public void testConstructor(){
        
        int number = 5000;
        
        HashMap<ElementId, String> saveList = new HashMap<ElementId, String>(number);
        
        for (int i = 0; i < number; i++){
            ElementId elementId = new ElementId("name");
            assertNull ("ElementId generated duplicate id "+elementId, saveList.get(elementId));
            saveList.put(elementId, "fungo");
        }
        
//        System.err.println("testConstructor tested : "+number);
    }

}
