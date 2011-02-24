package edu.csus.ecs.pc2.core.util;

import junit.framework.TestCase;

/**
 * Test for XMLMemento.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class XMLMementoTest extends TestCase {

    public void testGetValue() {
        
        String rootTag = "contest";
        String childTag = "key01";
        String theValue = "testValue";
        
        XMLMemento mementoRoot = XMLMemento.createWriteRoot(rootTag);

        IMemento parent = mementoRoot.createChildNode(childTag, theValue);
        parent.putString("foo", "foovalue");
        mementoRoot.createChild("booga");
        
        String testValue = parent.getValue();
        
        assertEquals(theValue, testValue);
    }

}
