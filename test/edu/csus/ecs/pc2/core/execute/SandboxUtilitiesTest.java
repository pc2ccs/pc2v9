// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.execute;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Properties;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

public class SandboxUtilitiesTest extends AbstractTestCase {
    
     private SampleContest sampleContest = new SampleContest();

    /**
     * Test writeSandboxInputProperties.
     * 
     * @throws Exception
     */
    public void testwriteSandboxInputProperties() throws Exception {

        ensureOutputDirectory();
        String storageDirectory = getOutputDataDirectory();
        
//        startExplorer(storageDirectory);
        
        String [] runsData = {

                "1,1,A,1,No",  //20
                "2,1,A,3,Yes",  //3 (first yes counts Minutes only)
                "3,1,A,5,No",  //20
        };
        
        IInternalContest contest = sampleContest.createContest(1, 3, 12, 12, true);

        IInternalController controller = sampleContest.createController(contest, storageDirectory, true, false);
//        String logFilename = storageDirectory + File.pathSeparator +"test2" + getName();
        

        for (String runInfoLine : runsData) {
            sampleContest.addARun(contest, runInfoLine);      
        }
        
        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());
        assertEquals("Number of runs", 3, runs.length);
        
        for (Run run : runs) {
            
            int timeLimit =  run.getNumber() * 100;
            int memLimit = 200 +run.getNumber();
            Problem problem = contest.getProblem(run.getProblemId());
            Language language = contest.getLanguage(run.getLanguageId());
            
            // write properties file
            SandboxUtilities.writeSandboxInputProperties(storageDirectory, run, "Sumit.java", problem, language, new File("Sumit.in"), timeLimit, memLimit);
            
            String sandboxFile = storageDirectory + File.separator + SandboxInput.SANDBOX_PROPERTIES_FILENAME;
            
            assertFileExists(sandboxFile, "Expecting properties written to "+sandboxFile);
            
            Properties props = new Properties();
            FileInputStream fileInputStream = new FileInputStream(sandboxFile);
            props.load(fileInputStream);
            
            String keyName = SandboxInput.MEMLIMIT_KEY;
            String str = props.getProperty(keyName);
            int value = Integer.parseInt(str);
            assertEquals("For run "+run.getNumber()+" expecting value for key="+keyName, timeLimit, value);

            keyName = SandboxInput.TIMELIMIT_KEY;
            str = props.getProperty(keyName);
            value = Integer.parseInt(str);
            assertEquals("For run "+run.getNumber()+" expecting value for key "+keyName, memLimit, value);

            keyName = SandboxInput.DATAFILENAME_KEY;
            str = props.getProperty(keyName);
            File dFile = new File(str);
            String fileName = dFile.getName();
            assertEquals("For run "+run.getNumber()+" expecting value for key "+keyName, "Sumit.in", fileName);

            keyName = SandboxInput.SOURCEFILENAME;
            str = props.getProperty(keyName);
            assertEquals("For run "+run.getNumber()+" expecting value for key "+keyName, "Sumit.java", str);
        }
        
    }

}
