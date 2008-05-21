package edu.csus.ecs.pc2.core;

import junit.framework.TestCase;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 *
 */
public class UtilitiesTest extends TestCase {

    public void testOne() {
        char[] array1 = null;
        char[] array2 = null;
        assertTrue("arrays are null", Utilities.isEquals(array1, array2));
        array2 = new char[1];
        array2[0] = 'C';
        assertFalse("arrays are not null", Utilities.isEquals(array1, array2));
        array1 = new char[1];
        array1[0] = 'C';
        assertTrue("arrays are equal", Utilities.isEquals(array1, array2));
        array1[0] = 'D';
        assertFalse("arrays are not equal", Utilities.isEquals(array1, array2));
        array2 = null;
        assertFalse("arrays are not null", Utilities.isEquals(array1, array2));
    }
}
