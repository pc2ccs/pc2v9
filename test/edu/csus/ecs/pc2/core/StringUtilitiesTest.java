package edu.csus.ecs.pc2.core;

import junit.framework.TestCase;

/**
 * Unit test.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class StringUtilitiesTest extends TestCase {

    public void testAppendString() throws Exception {

        String[] data = {
                //
                "a,b", //
                "a,b,c,d,e,f", //
        };

        for (String line : data) {

            System.out.println("line " + line);
            String[] f = line.split(",");

            String[] results = new String[0];

            for (int i = 0; i < f.length; i++) {
                results = StringUtilities.appendString(results, f[i]);
                compareArrayParts(f, i + 1, results);
            }
        }
    }

    private void compareArrayParts(String[] source, int count, String[] actual) {
        for (int i = 0; i < actual.length; i++) {
            assertEquals(source[i], actual[i]);
        }
    }
}
