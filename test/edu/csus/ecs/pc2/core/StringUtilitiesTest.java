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
    
    public void testTrunc() throws Exception {
        String[] data = { //
                "a;5;a", //
                "abc;5;abc", //
                "abcdefg;5;ab...", //
                "abcdefghijklmopqrstuv;5;ab...", //
                "abcdefghijklm;13;abcdefghijklm", //
        };

        for (String line : data) {
            
            String[] f = line.split(";");

            String source = f[0];
            int maxlen = Integer.parseInt(f[1]);
            String expected = f[2];

            String actual = StringUtilities.trunc(source, maxlen);
            assertEquals("trunc method ", expected, actual);
        }
        
        
    }
}
