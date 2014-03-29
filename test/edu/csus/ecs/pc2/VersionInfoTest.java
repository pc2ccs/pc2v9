package edu.csus.ecs.pc2;

import junit.framework.TestCase;

/**
 * Unit tests.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class VersionInfoTest extends TestCase {

    
    /**
     * Bug 537 testing
     */
    public void testVersionPrint() throws Exception {
    
        printSystemInfo();
        
        VersionInfo info = new VersionInfo();
        String [] lines = info.getSystemVersionInfoMultiLine();
        
        assertEquals("Expecting line count ",4,lines.length);
    }

    private void printSystemInfo() {
        
        VersionInfo info = new VersionInfo();
        String [] lines = info.getSystemVersionInfoMultiLine();
        
        for (String line : lines) {
            System.out.println(line);
        }
        
    }

}
