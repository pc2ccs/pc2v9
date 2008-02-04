package edu.csus.ecs.pc2.core;

import junit.framework.TestCase;

/**
 * Test for Parse Arguments.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ParseArgumentsTest extends TestCase {

    public void testNull() {

        try {
            @SuppressWarnings("unused") ParseArguments parseArguments = new ParseArguments(null);
        } catch (IllegalArgumentException e) {
            nullFakeRoutine("Passed test threw IllegalArgumentException");
        }

    }

    public void testEmpty() {
        @SuppressWarnings("unused") ParseArguments parseArguments = new ParseArguments(new String[0]);
    }

    /**
     * A fake routine because of a need to
     * 
     */
    private void nullFakeRoutine(String reason) {
        // TODO Auto-generated method stub

    }

}
