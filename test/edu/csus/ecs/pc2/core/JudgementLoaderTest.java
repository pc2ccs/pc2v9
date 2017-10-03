package edu.csus.ecs.pc2.core;

import java.io.File;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tsst.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class JudgementLoaderTest extends AbstractTestCase {
    
    /**
     * Test Load from list of lines. 
     * @throws Exception
     */
    public void testLoadFromLLines() throws Exception {
        
        String [] lines = {
                //
                "#", //
                "# reject.ini - Override no judgement text.", //
                "#", //
                "# This sample contains the default judgements coded into the system.", //
                "#", //
                "# To use, this file should be renamed to reject.ini and placed in", //
                "# the directory from which the 1st server is started.  To verify these", //
                "# judgements are active view the Judgements report from the Server UI.", //
                "# If no reject.ini is provided, the system will default to these", //
                "# judgements.", //
                "#", //
                "# Blank lines and lines beginning with a # are ignored.", //
                "#", //
                "# $HeadURL$", //
                "#", //
                "Compilation Error", //
                "Run-time Error", //
                "Time Limit Exceeded", //
                "Wrong Answer", //
                "Excessive Output", //
                "Output Format Error", //
                "Other - Contact Staff", //
                "# eof $Id$", //
        };
        
        IInternalContest contest = new InternalContest();

        boolean loaded = new JudgementLoader().loadedJudgements(contest, lines);

        assertTrue("Expected judgements to be loaded ", loaded);

        Judgement[] judgements = contest.getJudgements();
        assertEquals("Expecting number of judgements ", 8, judgements.length);

        Judgement judgement = judgements[1];
        assertEquals("Expecting acronym for " + judgement, "WA001", judgement.getAcronym());

    }
    
    /**
     * Test load acronynms.
     * @throws Exception
     */
    public void testLoadWithAcronyms() throws Exception {
        

        String [] lines = {
                //
                "Compilation Error|CE", //
                "Run-time Error|RTE", //
                "Time Limit Exceeded|TLE", //
                "Wrong Answer|WA", //
                "Excessive Output|EE", //
                "Output Format Error|EFE", //
                "Various Differences|VD", //
                "Other - Contact Staff|CS", //
                "# eof $Id$", //
        };

        IInternalContest contest = new InternalContest();

        boolean loaded = new JudgementLoader().loadedJudgements(contest, lines);
        assertTrue("Expected judgements to be loaded ", loaded);

        Judgement[] judgements = contest.getJudgements();
        assertEquals("Expecting number of judgements ", 9, judgements.length);

        Judgement judgement = judgements[1];
        assertEquals("Expecting acronym for " + judgement, "CE", judgement.getAcronym());
        
        judgement = judgements[0];
        assertEquals("Expecting acronym for " + judgement, "AC", judgement.getAcronym());

        
    }
    
    /**
     * TEst for AC in file.
     * @throws Exception
     */
    public void testLoadWithAltAC() throws Exception {

        String [] lines = {
                //
                "Accepted|AC", //
                "Compilation Error|CE", //
                "Run-time Error|RTE", //
                "Time Limit Exceeded|TLE", //
                "Wrong Answer|WA", //
                "Excessive Output|EE", //
                "Output Format Error|EFE", //
                "Various Differences|VD", //
                "Other - Contact Staff|CS", //
                "# eof $Id$", //
        };
        
//        for (String string : lines) {
//            System.out.println(string);
//        }

        IInternalContest contest = new InternalContest();

        boolean loaded = new JudgementLoader().loadedJudgements(contest, lines);
        assertTrue("Expected judgements to be loaded ", loaded);

        Judgement[] judgements = contest.getJudgements();
        assertEquals("Expecting number of judgements ", 9, judgements.length);

        Judgement judgement = judgements[1];
        assertEquals("Expecting acronym for " + judgement, "CE", judgement.getAcronym());
        
        judgement = judgements[0];
        assertEquals("Expecting acronym for " + judgement, "AC", judgement.getAcronym());
        assertEquals("Expecting judgement for " + judgement, "Accepted", judgement.toString());
    }
    
    /**
     * Test load AC line at alternate location in file.
     * 
     * @throws Exception
     */
    public void testLoadWithAltACAtEndOfList() throws Exception {

        String [] lines = {
                //
                "Compilation Error|CE", //
                "Run-time Error|RTE", //
                "Time Limit Exceeded|TLE", //
                "Wrong Answer|WA", //
                "Excessive Output|EE", //
                "Output Format Error|EFE", //
                "Various Differences|VD", //
                "Other - Contact Staff|CS", //
                "Accepted|AC", //
                "# eof $Id$", //
        };

        IInternalContest contest = new InternalContest();

        boolean loaded = new JudgementLoader().loadedJudgements(contest, lines);
        assertTrue("Expected judgements to be loaded ", loaded);

        Judgement[] judgements = contest.getJudgements();
        assertEquals("Expecting number of judgements ", 9, judgements.length);

        Judgement judgement = judgements[1];
        assertEquals("Expecting acronym for " + judgement, "CE", judgement.getAcronym());
        
        judgement = judgements[0];
        assertEquals("Expecting acronym for " + judgement, "AC", judgement.getAcronym());
        assertEquals("Expecting judgement for " + judgement, "Accepted", judgement.toString());
    }
    
    public String getTestSamplesDirectory() {
        return getProjectRootDirectory() + File.separator + "samps" ;
    }
    
    public String getSampleseFilename(String filename) {
        String name = getTestSamplesDirectory() + File.separator + filename;
        assertFileExists(name);
        return name;
    }
    
    
    /**
     * Load from reject.ini
     * 
     * Load judgements from samples {@link Constants#JUDGEMENT_INIT_FILENAME}. 
     * 
     * @throws Exception
     */
    public void testLoadFromRejectIni() throws Exception {
        
        String rejectIniFilename = getSampleseFilename(Constants.JUDGEMENT_INIT_FILENAME);
        
        IInternalContest contest = new InternalContest();

        boolean loaded = new JudgementLoader().loadedJudgementsFromIni(contest, rejectIniFilename);

        assertTrue("Expected judgements to be loaded ", loaded);

        Judgement[] judgements = contest.getJudgements();
        assertEquals("Expecting number of judgements ", 8, judgements.length);

        Judgement judgement = judgements[1];
        assertEquals("Expecting acronym for " + judgement, "WA001", judgement.getAcronym());
        
        judgement = judgements[0];
        assertEquals("Expecting acronym for " + judgement, "AC", judgement.getAcronym());
        assertEquals("Expecting judgement for " + judgement, "Yes", judgement.toString());
    }
}
