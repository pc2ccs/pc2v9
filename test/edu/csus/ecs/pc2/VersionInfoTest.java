package edu.csus.ecs.pc2;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests.
 * 
 * @author pc2@ecs.csus.edu
 */

public class VersionInfoTest extends AbstractTestCase {
    
    /**
     * Bug 537 testing
     */
    public void testVersionPrint() throws Exception {
    
//        printSystemInfo();

        VersionInfo info = new VersionInfo();
        String[] lines = info.getSystemVersionInfoMultiLine();

        assertEquals("Expecting line count ", 4, lines.length);
    }

    protected void printSystemInfo() {
        
        VersionInfo info = new VersionInfo();
        String [] lines = info.getSystemVersionInfoMultiLine();
        
        for (String line : lines) {
            System.out.println(line);
        }
        
    }

}
