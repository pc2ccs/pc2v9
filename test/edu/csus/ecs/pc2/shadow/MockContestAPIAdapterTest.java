package edu.csus.ecs.pc2.shadow;

import java.io.File;
import java.net.URL;

import edu.csus.ecs.pc2.core.FileUtilities;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit testing.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class MockContestAPIAdapterTest extends AbstractTestCase {
    
    public void testTenProblemCDP() throws Exception {
        
        String sampdirname = "tenprobs";
        URL sampleContestURL = FileUtilities.findCDPConfigDirectoryURL(new File(sampdirname));
        
        assertEquals("file", sampleContestURL.getProtocol());
        assertTrue(sampleContestURL.getFile().indexOf(sampdirname) != -1);
        
        MockContestAPIAdapter mock = new MockContestAPIAdapter(sampleContestURL, "", "");
        assertNotNull(mock);
        
        assertNotNull(mock.getCdpConfigDirectory());
        
        
        
    }
    
    

}
