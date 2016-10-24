package edu.csus.ecs.pc2.core.imports;

import java.io.File;

import edu.csus.ecs.pc2.core.imports.ExportAccounts.Formats;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */

// SOMEDAY test XML and txt file formats

public class ExportAccountsTest extends AbstractTestCase{
    
    
    public void testCVSFile() throws Exception {
        
//        ensureDirectory(getDataDirectory());
        
        String expectedOutputFileName = getTestFilename(this.getName() + ".csv");
        
        IInternalContest contest = new SampleContest().createStandardContest();
        
//        generateFile(contest, Formats.CSV, expectedOutputFileName);
        
        assertFileExists(expectedOutputFileName);
        
        String inputFileName = getOutputTestFilename(this.getName()+".csv");

        generateFile(contest, Formats.CSV, inputFileName);
        
        assertFileContentsEquals(new File(expectedOutputFileName),  new File(inputFileName));
        
    }

    protected void generateFile(IInternalContest contest,  Formats format, String outputFile) {
        
        Group[] groups = contest.getGroups();
        Account[] accounts = SampleContest.getTeamAccounts(contest);
        ExportAccounts.saveAccounts(format, accounts, groups, new File(outputFile));
        
    }

}
