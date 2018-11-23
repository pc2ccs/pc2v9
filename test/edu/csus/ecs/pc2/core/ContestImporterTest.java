package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import junit.framework.TestCase;

/**
 * Unit test.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class ContestImporterTest extends TestCase {
    
    
    /**
     * Test replaceBlankContestTitle method.
     * 
     * <pre>
     * https://pc2.ecs.csus.edu/bugzilla/show_bug.cgi?id=1381
     * </pre>
     * @throws Exception
     */
    public void testreplaceBlankContestTitle() throws Exception {
        
        ContestImporter importer = new ContestImporter();
        
        SampleContest sampleContest = new SampleContest();
        IInternalContest contest = sampleContest.createStandardContest();
        String curTitle = contest.getContestInformation().getContestTitle();
        
        ContestInformation contestInformation = new ContestInformation();
        
        // null input contest title
        contestInformation.setContestTitle(null);
        
        ContestInformation newContestInfo = importer.replaceBlankContestTitle(contest, contestInformation);
        String newTitle = newContestInfo.getContestTitle();
        
        assertEquals("Expecting no title change ", curTitle, newTitle);

        // blanke input contest titlte
        contestInformation.setContestTitle("");
        
        newContestInfo = importer.replaceBlankContestTitle(contest, contestInformation);
        newTitle = newContestInfo.getContestTitle();
        
        assertEquals("Expecting no title change ", curTitle, newTitle);

        
    }

}
