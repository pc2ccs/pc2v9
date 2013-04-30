package edu.csus.ecs.pc2.core;

import junit.framework.TestCase;

/**
 * Unit test.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class XMLUtilitiesTest extends TestCase {
    
    public void testformatSeconds() throws Exception {

        String [] data  = {
                "1001;1.001",
                "2020;2.020",
                "0;0.000",
        };
        
        for (String line : data) {
            String [] fields = line.split(";");
            Long ms = Long.parseLong(fields[0]);
            String expected = fields[1];
            String actual = XMLUtilities.formatSeconds(ms);
            assertEquals("Expected to be equal", expected, actual);
            
        }
    }

}
