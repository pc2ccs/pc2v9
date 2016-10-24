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
    
    SampleContest sample = new SampleContest();
    
    public void testCVSFile() throws Exception {
        
        Formats format = Formats.CSV;
        
        ensureDirectory(getOutputDataDirectory());
//        startExplorer(getOutputDataDirectory());

//        ensureDirectory(getDataDirectory());
        
        String expectedOutputFileName = getTestFilename(this.getName() + "." + format.toString().toLowerCase());
        
        IInternalContest contest = sample.createStandardContest();
        sample.assignSampleGroups(contest, "Group Thing One", "Group Thing Two");
        
//        generateFile(contest, Formats.CSV, expectedOutputFileName);
//        editFile(expectedOutputFileName);
        
        assertFileExists(expectedOutputFileName);
        
        String actualExportFileName = getOutputTestFilename(this.getName() + "." + format.toString().toLowerCase());

        generateFile(contest, format, actualExportFileName);
        
        assertFileExists(actualExportFileName);
        assertFileContentsEquals(new File(expectedOutputFileName),  new File(actualExportFileName));
        
    }

    protected void generateFile(IInternalContest contest,  Formats format, String outputFile) throws Exception {
        
        Group[] groups = contest.getGroups();
        Account[] accounts = SampleContest.getTeamAccounts(contest);
        
        assertEquals("Team accounts ", 120, accounts.length);
        assertEquals("Groups ", 2, groups.length);
        
        ExportAccounts.saveAccounts(format, accounts, groups, new File(outputFile));
        
        if (ExportAccounts.getException() != null){
            throw ExportAccounts.getException();
        }
        
    }
    

    /**
     * ONly test whether it creates a file
     * 
     * @throws Exception
     */
    public void testXMLFile() throws Exception {

        ensureDirectory(getOutputDataDirectory(this.getName()));

        Formats format = Formats.XML;

        IInternalContest contest = new SampleContest().createStandardContest();
        sample.assignSampleGroups(contest, "Group Thing One", "Group Thing Two");
        
        String exportedFileName = getOutputTestFilename(this.getName() + "." + format.toString().toLowerCase());

        generateFile(contest, format, exportedFileName);

        assertFileExists(exportedFileName);
    }

    /**
     * ONly test whether it creates a file
     * 
     * @throws Exception
     */
    public void testTXTFile() throws Exception {

        Formats format = Formats.TXT;

        ensureDirectory(getOutputDataDirectory(this.getName()));

        IInternalContest contest = new SampleContest().createStandardContest();
        sample.assignSampleGroups(contest, "Group Thing One", "Group Thing Two");
        
        String exportedFileName = getOutputTestFilename(this.getName() + "." + format.toString().toLowerCase());

        generateFile(contest, format, exportedFileName);

        assertFileExists(exportedFileName);
//        editFile(exportedFileName);

        String expectedOutputFileName = getOutputTestFilename(this.getName() + "." + format.toString().toLowerCase());

        assertFileExists(expectedOutputFileName);

        String actualExportFileName = getOutputTestFilename(this.getName() + "." + format.toString().toLowerCase());
        generateFile(contest, format, actualExportFileName);

        assertFileExists(actualExportFileName);
        assertFileContentsEquals(new File(expectedOutputFileName),  new File(actualExportFileName));
      
    }


}
