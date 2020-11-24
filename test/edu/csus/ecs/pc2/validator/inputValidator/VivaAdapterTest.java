// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.validator.inputValidator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.Test;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests for the Viva Input Validator Adapter.
 * 
 * @author john@pc2.ecs.csus.edu
 */

public class VivaAdapterTest extends AbstractTestCase {
    
    private VivaAdapter vivaAdapter ;
    private SampleContest sampleContest ;   
    private IInternalContest contest;
    private IInternalController controller;
    private Log log;
    private String dataFilesDir;

    //TODO: currently the constructor is being executed once for each @Test method, creating and recreating the (same) contest 
    //  over and over (that is, a new instance of the class is created prior to running each @Test method).  
    //  It would be preferable to annotate the class with "@TestInstance(Lifecycle.PER_CLASS)", which would then
    //  allow moving the constructor initialization of instance variables into a setup() method annotated with @BeforeAll so that
    //  setup() only runs once, before all the @Test methods are run.  However, using the @TestInstance annotation requires JUnit 5...
    public VivaAdapterTest(String name) {
        super(name);
        
        // create a sample contest and controller
        sampleContest = new SampleContest();
        contest = sampleContest.createStandardContest();
        controller = sampleContest.createController(contest, true, false);

        // get a judge id so we can create a log file
        ClientId judgeClientId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();
        contest.setClientId(judgeClientId);
//        System.out.println("VivaAdapterTest: Logging to log file for " + judgeClientId);

        log = controller.getLog();
        String logName = log.getLogfilename();
//        System.out.println ("VivaAdapterTest: Log file name = '" + logName + "'");

        // get the first defined problem in the contest
        Problem problem = contest.getProblems()[0];

        // add some data files (judge's input data and corresponding answer files) to the problem
        dataFilesDir = getDataDirectory();  //this performs an "ensureDirectory()"
//        System.out.println("VivaAdapterTest: loading test data files from '" + dataFilesDir + "'");

        ProblemDataFiles problemDataFiles = sampleContest.loadDataFiles(problem, null, dataFilesDir, "in", "ans");

        contest.updateProblem(problem, problemDataFiles);

//        System.out.println("VivaAdapterTest: sample contest created with problem " + problem + " containing " + problemDataFiles.getJudgesDataFiles().length + " test cases.");

        //create a VivaAdapter for use by the test methods
        vivaAdapter = new VivaAdapter(controller);

    }
    
    @Test
    /**
     * Verify that Viva returns "valid" for a simple valid pattern.
     */
    public void testVivaSimpleValidPattern() {
        
        String pattern = "{x;}";
        VivaPatternTestResult result = vivaAdapter.checkPattern(pattern);
        assertTrue("Viva checkPattern() failed to return true for valid pattern " + pattern, result.isValidPattern());
        
    }
    
    @Test
    /**
     * Verify that Viva returns "invalid" for a simple invalid pattern.
     */
    public void testVivaSimpleInvalidPattern() {
        
        String pattern = "{x}";  //invalid because the pattern statement doesn't end with a semi-colon
        VivaPatternTestResult result = vivaAdapter.checkPattern(pattern);
        assertFalse("Viva checkPattern() failed to return false for invalid pattern " + pattern, result.isValidPattern());
        
    }
    
    @Test
    /**
     * Verify that Viva returns "invalid" for a more complex invalid pattern read from a file.
     */
    public void testVivaComplexInvalidPattern() {

        // make sure the pattern file is available
        String pattern = getPatternFromFile("Viva.InvalidPattern.viva");
        
        VivaPatternTestResult result = vivaAdapter.checkPattern(pattern);
        assertFalse("Viva checkPattern() failed to return false for invalid pattern " + pattern, result.isValidPattern());
        assertTrue("Viva checkPattern() response fails to contain error message 'Unknown identifier: minint'", 
                        result.getVivaResponseMessage().contains("Unknown identifier: minint"));
//        System.out.println("Viva response: " + result.getVivaResponseMessage());

    }
    
   @Test
   /**
    * Verify that Viva correctly fails data files with multiple data on a line.
    */
   public void testFailOnMultipleDataPerLine() {
       
       //get the Viva pattern from file
       String pattern = getPatternFromFile("Viva.ValidPattern.FailOnMultipleDataPerLine.viva");
       
       //make sure the pattern is valid (it should be)
       VivaPatternTestResult result = vivaAdapter.checkPattern(pattern);
       assertTrue("Viva checkPattern() failed to return true for valid pattern '" + pattern + "'", result.isValidPattern());
//       System.out.println("testFailOnMultipleDataPerLine: pattern = " + pattern);
       
       //test each of the (three) test data files against the pattern.
       // Note that method testDataFile will throw a failed assertion if the specified file does not produce the
       // expected (specified) boolean test result
       testDataFile("sumit.in", pattern, true);
       testDataFile("sumit2.extraDataOnOneLine.in", pattern, false);
       testDataFile("sumit3.extraWhitespacePastEOD.in", pattern, false);
       testDataFile("sumit4.extraDataPastSentinel.in", pattern, true);
   }
    
   @Test
   /**
    * Verify that Viva correctly fails data files with negative data on a line.
    * Note that the pattern being used explicitly allows for multiple data values per line (as long as none is negative).
    */
   public void testFailOnNegativeData() {
       
       //get the Viva pattern from file
       String pattern = getPatternFromFile("Viva.ValidPattern.FailOnNegative.viva");
       
       //make sure the pattern is valid (it should be)
       VivaPatternTestResult result = vivaAdapter.checkPattern(pattern);
       assertTrue("Viva checkPattern() failed to return true for valid pattern '" + pattern + "'", result.isValidPattern());
//       System.out.println("testFailOnNegativeData: pattern = " + pattern);
       
       //test each of the (three) test data files against the pattern.
       // Note that method testDataFile will throw a failed assertion if the specified file does not produce the
       // expected (specified) boolean test result
       testDataFile("sumit.in", pattern, false);
       testDataFile("sumit2.extraDataOnOneLine.in", pattern, true);
       testDataFile("sumit3.extraWhitespacePastEOD.in", pattern, false);
       testDataFile("sumit4.extraDataPastSentinel.in", pattern, true);
   }
    
   @Test
   /**
    * Verify that Viva correctly fails data files with data beyond a pattern-declared zero sentinel.
    * Note that the pattern being used explicitly allows for multiple data values per line (as long as no data appears after
    * a line containing a zero sentinel).
    */
   public void testFailOnExtraDataPastSentinel() {
       
       //get the Viva pattern from file
       String pattern = getPatternFromFile("Viva.ValidPattern.FailOnDataPastZeroSentinel.viva");
//       System.out.println("testFailOnExtraDataPastSentinel: pattern = " + pattern);
       
       //make sure the pattern is valid (it should be)
       VivaPatternTestResult result = vivaAdapter.checkPattern(pattern);
       assertTrue("Viva checkPattern() failed to return true for valid pattern '" + pattern + "'", result.isValidPattern());
       
       //test each of the (three) test data files against the pattern.
       // Note that method testDataFile will throw a failed assertion if the specified file does not produce the
       // expected (specified) boolean test result
       testDataFile("sumit.in", pattern, true);
       testDataFile("sumit2.extraDataOnOneLine.in", pattern, true);
       testDataFile("sumit3.extraWhitespacePastEOD.in", pattern, false);
       testDataFile("sumit4.extraDataPastSentinel.in", pattern, false);
   }
    
   /**
    * Invokes Viva to test the specified data file against the specified pattern, using the
    * specified "expectedResult" value to determine whether the data file is supposed to match the
    * pattern or not.  Generates a failed assertion if the result from Viva does not match "expectedResult".
    * 
    * @param datafile the file which is to be tested against the Viva pattern.
    * @param pattern the Viva pattern to be used to test the specified data file.
    * @param expectedResult a boolean indicating whether the caller expects the data file to match the pattern or not.
    */
    private void testDataFile(String dataFileName, String pattern, boolean expectedResult) {

        //insure the data file is available
        String dataDir = getDataDirectory() + File.separator;
        assertDirectoryExists(dataDir, "Missing data directory");
        String fullFileName = dataDir + dataFileName;
        assertFileExists(fullFileName, "Missing test data file '" + dataFileName + "'");

        SerializedFile datafile = new SerializedFile(fullFileName);
        try {
            assertFalse("Error converting file '" + dataFileName + "' to SerializedFile: " + datafile.getErrorMessage(), Utilities.serializedFileError(datafile));
        } catch (Exception e) {
            System.err.println("Exception converting file '" + dataFileName + "' to SerializedFile: " + e.getMessage());
            fail("Exception converting file '" + dataFileName + "' to SerializedFile: " + e.getMessage());
        }

        VivaDataFileTestResult result = vivaAdapter.testFile(pattern, datafile);
//        System.out.println("testDataFile: Viva result for " + dataFileName + ": " + result);
        
        if (result==null) {
            fail("Viva returned null result for data file '" + dataFileName + "'");
        } else {
            assertEquals("Viva test result for file '" + dataFileName + "' doesn't match: ", expectedResult, result.passed());
        }

    }
   
   /**
    * Reads a Viva pattern from the specified file, generating failed assertions if either the data directory
    * or the specified file does not exist.
    * 
    * @param filename the name of the file from which a Viva pattern should be read.
    * @return a String containing the Viva pattern.
    */
   private String getPatternFromFile(String filename) {
       
       // make sure the pattern file is available
       String dataDir = getDataDirectory() + File.separator;
       assertDirectoryExists(dataDir, "Missing data directory");
       String patternFileName = dataDir + filename;
       assertFileExists(patternFileName, "Missing Viva Pattern file '" + patternFileName + "'");

       // read the pattern out of the data file
       String pattern = "";
       Scanner sc = null;
       try {
           sc = new Scanner(new File(patternFileName));
           while (sc.hasNextLine()) {
               pattern += sc.nextLine();
           }
       } catch (FileNotFoundException e) {
           // we should never get here, because of the assertFileExists() above
           e.printStackTrace();
       }

       return pattern;
   }

}
