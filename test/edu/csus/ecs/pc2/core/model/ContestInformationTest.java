// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

import java.util.Properties;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;

/**
 * Unit Tests.
 * 
 * @author pc2@ecs.csus.edu
 */

public class ContestInformationTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIsSameAs() {

        ContestInformation contestInformation1 = new ContestInformation();
        ContestInformation contestInformation2 = new ContestInformation();

        assertTrue("Same properties", contestInformation1.isSameAs(contestInformation2));

        String title = "Contest Title";
        contestInformation1.setContestTitle(title);

        assertFalse("Diff title", contestInformation1.isSameAs(contestInformation2));

        contestInformation2.setContestTitle(title);

        assertTrue("Same title", contestInformation1.isSameAs(contestInformation2));

        contestInformation1.setMaxOutputSizeInBytes(4000);

        assertFalse("Diff max file size", contestInformation1.isSameAs(contestInformation2));

        contestInformation2.setMaxOutputSizeInBytes(4000);

        assertTrue("Same max file size", contestInformation1.isSameAs(contestInformation2));
        
        contestInformation2.setExternalYamlPath("/home/datalogin/contest");

        assertFalse("Diff YAML Path", contestInformation1.isSameAs(contestInformation2));

        contestInformation1.setExternalYamlPath("/home/datalogin/contest");

        assertTrue("Same YAML Path", contestInformation1.isSameAs(contestInformation2));
        
        contestInformation2.setRsiCommand("/usr/local/bin/sccsrs");
        
        assertFalse("Diff RSI command", contestInformation1.isSameAs(contestInformation2));
        
        contestInformation1.setRsiCommand("/usr/local/bin/sccsrs");
        
        assertTrue("Same RSI command", contestInformation1.isSameAs(contestInformation2));

    }

    public void testPropertiesIsSameAs() {

        ContestInformation contestInformation1 = new ContestInformation();
        ContestInformation contestInformation2 = new ContestInformation();

        Properties properties = DefaultScoringAlgorithm.getDefaultProperties();
        contestInformation1.setScoringProperties(properties);
        properties = DefaultScoringAlgorithm.getDefaultProperties();
        contestInformation2.setScoringProperties(properties);

        assertTrue("Same properties", contestInformation1.isSameAs(contestInformation1));

        assertTrue("Same properties", contestInformation1.isSameAs(contestInformation2));

        properties = new Properties();
        contestInformation2.setScoringProperties(properties);
        assertFalse("Not Same properties", contestInformation1.isSameAs(contestInformation2));

    }
    
    /**
     * Test for Judges CDP Path.
     * 
     * Bug 904.
     */
    public void testPropertiesIsSameAsWithJudgeCDPPath() {

        ContestInformation contestInformation1 = new ContestInformation();
        ContestInformation contestInformation2 = new ContestInformation();

        Properties properties = DefaultScoringAlgorithm.getDefaultProperties();
        contestInformation1.setScoringProperties(properties);
        properties = DefaultScoringAlgorithm.getDefaultProperties();
        contestInformation2.setScoringProperties(properties);

        assertTrue("Same properties", contestInformation1.isSameAs(contestInformation1));

        assertTrue("Same properties", contestInformation1.isSameAs(contestInformation2));

        properties = new Properties();
        contestInformation2.setJudgeCDPBasePath("/home/pc2/cdp");
        assertFalse("Expecting not same properties", contestInformation1.isSameAs(contestInformation2));

    }
    
    
    /**
     * Test isSameAs for freeze time 
     */
    public void testFreezeTimeisSameAs() throws Exception {

        ContestInformation info = new ContestInformation();
        ContestInformation info2 = new ContestInformation();

        assertTrue("Contest info identical ", info.isSameAs(info2));

        info.setFreezeTime("2:00:00");
        assertFalse("Expeced Freeze time changed ", info.isSameAs(info2));

        info2.setFreezeTime("2:00:00");
        assertTrue("Expeced Freeze time changed ", info.isSameAs(info2));

        info2.setFreezeTime("1:23:00");
        assertFalse("Expeced Freeze time changed ", info.isSameAs(info2));
    }
    
    public void testDefaultMaxOutputSize() throws Exception {
        ContestInformation info = new ContestInformation();
        assertEquals(info.getMaxOutputSizeInBytes(), Constants.DEFAULT_MAX_OUTPUT_SIZE_K * 1024);
    }
    
}
