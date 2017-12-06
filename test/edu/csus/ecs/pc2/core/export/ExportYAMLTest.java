package edu.csus.ecs.pc2.core.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.imports.ccs.ContestSnakeYAMLLoader;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;

/**
 * Test for YAML export.
 * 
 * @author pc2@ecs.csus.edu
 */
public class ExportYAMLTest extends AbstractTestCase {

    private boolean debugMode = false;

    private SampleContest sampleContest = new SampleContest();

    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }

    public void testOne() throws Exception {

        IInternalContest contest = sampleContest.createContest(3, 3, 12, 5, true);
        
        String dataDirectory = getDataDirectory();
        ensureDirectory(dataDirectory);
        
        String testDirectory = getOutputDataDirectory("testOne");
        ensureDirectory(testDirectory);
//        startExplorer(testDirectory);
//        startExplorer(dataDirectory);
        
//        addShortNames(contest);

        Problem[] problems = contest.getProblems();
        
        if (debugMode){
            for (Problem problem : contest.getProblems()) {
                System.out.println(problem.getDisplayName() + " " +problem.getShortName());
            }
        }
        
        
        sampleContest.addDataFiles(contest, testDirectory, problems[0], "sumit.dat", "sumit.ans");
        sampleContest.addDataFiles(contest, testDirectory, problems[1], "quads.in", "quads.ans");
        sampleContest.addDataFiles(contest, testDirectory, problems[4], "london.dat", "london.ans");
        sampleContest.addInternalValidator(contest, problems[4], 3);

        ExportYAML exportYAML = new ExportYAML();

        exportYAML.exportFiles(testDirectory, contest);

        String actualFileName =testDirectory + File.separator + IContestLoader.DEFAULT_PROBLEM_SET_YAML_FILENAME;
        String expectedFileName = dataDirectory + File.separator + IContestLoader.DEFAULT_PROBLEM_SET_YAML_FILENAME;
        
        // Start compare on line 4 to skip header/version/time information
        assertFileContentsEquals(new File(expectedFileName), new File(actualFileName), 4, getOverrideStringCompare());


        exportYAML = null;
    }
    
    public void testStringJoin() {

        String [] expected = {
            "a, b, c",
            "EJB, JPA, Glassfish, NetBeans",
            "A",
            "A, b",
            "a,b",
            ""
        };
        
        for (int i = 0; i < expected.length; i++) {
            List<String> list = Arrays.asList(expected[i].split(", "));
            StringBuffer actual = ExportYAML.join(", ", list);
            assertEquals("Expected join results ", expected[i], actual.toString());
        }

    }
    
//    public void testCreateYaml() throws Exception {
    public void UNUSED() throws Exception {
        
        // TODO TODAY - fix the comparison, fails unit test on athena.
        
        String dataDirectory = getDataDirectory("testCreateYaml");
        ensureDirectory(dataDirectory);
        
        String testDirectory = getOutputDataDirectory("testCreateYaml");
        ensureDirectory(testDirectory);
        
//        startExplorer(dataDirectory);
//        startExplorer(testDirectory);
        
        IInternalContest contest = sampleContest.createContest(3, 3, 12, 5, true);
        
        asertProblemShortAssigned(contest);
        
        // Add Manual Evalution on problem 2
        
        addManualReview(contest, 2, true);
        
//        addShortNames(contest);

        ExportYAML exportYAML = new ExportYAML();

        exportYAML.exportFiles(testDirectory, contest);
        
        String actualContestYamlFile = testDirectory+File.separator+IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;
        
//        String expectedContestYamlFile = getTestFilename("expected.contest.yaml");
        String expectedContestYamlFile = getTestFilename("expected.unix.contest.yaml");
        if (! "/".equals(File.separator)){
            convertToNativeFileSeperators (expectedContestYamlFile);
        }
        
        // Have to strip out start time because it changes, it is always different
        stripStartTime(actualContestYamlFile);
//        editFile(expectedContestYamlFile);
//        editFile(actualContestYamlFile);
        
        assertFileContentsEquals(new File(expectedContestYamlFile), new File(actualContestYamlFile), 4);
        
//        String filename = testDirectory + File.separator + IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;
        
        String actualFileName =testDirectory + File.separator + IContestLoader.DEFAULT_PROBLEM_SET_YAML_FILENAME;
        String expectedFileName = dataDirectory + File.separator + IContestLoader.DEFAULT_PROBLEM_SET_YAML_FILENAME;
        
        // Start compare on line 4 to skip header/version/time information
        assertFileContentsEquals(new File(actualFileName), new File(expectedFileName), 4, getOverrideStringCompare());

        exportYAML = null;

    }
    
    private OverrideStringCompare getOverrideStringCompare(){
        OverrideStringCompare op = new OverrideStringCompare(){
            @Override
            public boolean stringEquals(String one, String two) {
            	return one.replace('\\', '/').equals(two.replace('\\', '/'));
            }
        };
        return op;
    }
    
    public void testStringEqulas() throws Exception {
        
        String [] data  = {//
                "\\tmp\\file;/tmp/file", // 
                "work\\longfilename;work\\longfilename", //
                "\\\\\\\\;////", //
                "this is the way;this is the way", // 
                "c:\\tmp\\file2;c:/tmp/file2", //
        };
        
        OverrideStringCompare comp = getOverrideStringCompare();

        for (String string : data) {
            String[] fields = string.split(";");

            String input = fields[0];
            String expected = fields[1];

            boolean passed = comp.stringEquals(input, expected);
            assertTrue("Compare '"+input+"' to '"+expected+"'", passed);
        }

    }
    
    /**
     * Test freeze time.
     * 
     * @throws Exception
     */
    public void testFreezeTime() throws Exception {

        String dataDirectory = getDataDirectory(getName());
        ensureDirectory(dataDirectory);

        String outDir = getOutputDataDirectory(getName());
        ensureDirectory(outDir);

        //        startExplorer(dataDirectory);
        //        startExplorer(testDirectory);

        IInternalContest contest = sampleContest.createContest(3, 3, 12, 5, true);

        ContestInformation contestInformation = contest.getContestInformation();
        
        String expectedFreezeTime = "02:00:00";
        contestInformation.setFreezeTime(expectedFreezeTime);
        contest.updateContestInformation(contestInformation);
        
        Problem[] problems = contest.getProblems();
        
        sampleContest.addDataFiles(contest, outDir, problems[0], "sumit.in", "sumit.ans");
        sampleContest.addDataFiles(contest, outDir, problems[1], "quads.in", "quads.ans");
        sampleContest.addDataFiles(contest, outDir, problems[2], "quads.in", "quads.ans");
        sampleContest.addDataFiles(contest, outDir, problems[3], "quads.in", "quads.ans");
        sampleContest.addDataFiles(contest, outDir, problems[4], "london.in", "london.ans");
        sampleContest.addDataFiles(contest, outDir, problems[5], "finn.in", "finn.ans");

        // Add Manual Evaluation on problem 2
        addManualReview(contest, 2, true);

        ExportYAML exportYAML = new ExportYAML();
        exportYAML.exportFiles(outDir, contest);

        String actualContestYamlFile = outDir + File.separator + IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;
        
        // Have to strip out start time because it changes, it is always different
        stripStartTime(actualContestYamlFile);

        String expectedContestYamlFile = dataDirectory  + File.separator + "expected.frz.unix.contest.yaml";
        if (!"/".equals(File.separator)) {
            convertToNativeFileSeperators(expectedContestYamlFile);
        }
        
//        editFile(actualContestYamlFile);
//        editFile(expectedContestYamlFile);
        
        // Test Load
        
        ContestSnakeYAMLLoader loader = new ContestSnakeYAMLLoader();
        IInternalContest contest2 = loader.fromYaml(null, outDir);
        ContestInformation info = contest2.getContestInformation();
        assertEquals(expectedFreezeTime, info.getFreezeTime());
        
        // REFACTOR move this file length check into its own unit test method.
        
        /**
         * Instructions to create a new expected file for this unit test.
         * 
         * 1 - uncomment the copyFileOverwrite call
         * 2 - run the unit test method
         * 3 - comment the copyFileOverwrite call
         * 4 - be sure to git commit new expected file 
         */
//        copyFileOverwrite(actualContestYamlFile, expectedContestYamlFile);          

        /**
         * "Expecting expected file length to match new/actual file length" 
         */
        // REFACTOR - add String message as first paraamter to assertFileContentsEquals
        assertFileContentsEquals(
                // If this assert fails and Export YAML is outputting different contest,
                // follow the instructions above.
                new File(expectedContestYamlFile), new File(actualContestYamlFile), 4, getOverrideStringCompare());

        exportYAML = null;



    }


//  private void addShortNames(IInternalContest contest) throws Exception {
//
//      int idx = 1;
//      for (Problem problem : contest.getProblems()) {
//          if (problem.getShortName() == null){
//              problem.setShortName("short" + idx);
//          }
//          idx++;
//      }
//  }
    
    private void stripStartTime(String filename) {
        File actualFile = new File(filename);
        try {
            File newFile = new File(filename+".tmp");
            PrintWriter printWriter = new PrintWriter(newFile);
            String [] actualContents = Utilities.loadFile(actualFile.getAbsolutePath());
            String [] newContents = new String[actualContents.length];
            for (int i = 0; i < actualContents.length; i++) {
                String string = actualContents[i];
                if(string.startsWith("start-time:")) {
                    string = "start-time: ";
                }
                newContents[i] = string;
                printWriter.println(string);
            }
            printWriter.close();
            actualFile.delete();
            boolean result = newFile.renameTo(actualFile);
            assertTrue("rename Failed", result);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

      private void asertProblemShortAssigned(IInternalContest contest) throws Exception {
    
          for (Problem problem : contest.getProblems()) {
              assertNotNull("Problem short name not assigned "+problem,problem.getShortName());
          }
      }

      /**
       * Add manual review to all problems.
       * @param contest
       * @param problemNumber
       * @param flag
       */
      private void addManualReview(IInternalContest contest, int problemNumber, boolean flag) {

          Problem[] problems = contest.getProblems();
          Problem problem = problems[problemNumber+1];
          problem.setManualReview(flag);
      }
      
      /**
       * Test whether auto-stop-clock-at-end is written.
       * 
       * @throws Exception
       */
      public void testExportHalt() throws Exception {
          
          String outDir = getOutputDataDirectory(getName());
          ensureDirectory(outDir);

//          startExplorer(outDir);
          
          String cdpConfigDir = outDir;
//          startExplorer(outDir);

          IInternalContest contest = sampleContest.createContest(3, 3, 12, 5, true);
          addProblemDataFiles (cdpConfigDir, contest);

          ContestInformation contestInformation = contest.getContestInformation();
          contestInformation.setAutoStopContest(true);
          contest.updateContestInformation(contestInformation);

          ExportYAML exportYAML = new ExportYAML();
          exportYAML.exportFiles(outDir, contest);

          String contestYamlFilename = outDir + File.separator+ IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;
          assertFileExists(contestYamlFilename);

          ContestSnakeYAMLLoader loader = new ContestSnakeYAMLLoader();
          IInternalContest contest2 = loader.fromYaml(null, outDir, false);
          ContestInformation info = contest2.getContestInformation();
          assertTrue("Expecting auto stop to be set ", info.isAutoStopContest());
      }


      /**
       * Test whether auto-stop-clock-at-end is not written.
       * 
       * @throws Exception
       */
      public void testNoExportHaltAtEnd() throws Exception {
          
          String outDir = getOutputDataDirectory(getName());
          ensureDirectory(outDir);
          
          String cdpConfigDir = outDir;
//          startExplorer(outDir);

          IInternalContest contest = sampleContest.createContest(3, 3, 12, 5, true);
          addProblemDataFiles (cdpConfigDir, contest);

          ExportYAML exportYAML = new ExportYAML();
          exportYAML.exportFiles(outDir, contest);
          
          String contestYamlFilename = outDir + File.separator+ IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;
          assertFileExists(contestYamlFilename);
          
              ContestSnakeYAMLLoader loader = new ContestSnakeYAMLLoader();
              IInternalContest contest2 = loader.fromYaml(null, outDir, false);
              ContestInformation info = contest2.getContestInformation();
              assertFalse("Expecting auto stop to NOT be set ", info.isAutoStopContest());
    }

    private void addProblemDataFiles(String cdpConfigDir, IInternalContest contest) throws FileNotFoundException {
        
        String testDirectory = getOutputDataDirectory(cdpConfigDir);
        
        Problem[] problems = contest.getProblems();

        for (Problem problem : problems) {
            sampleContest.addDataFiles(contest, testDirectory, problem, "sumit.in", "sumit.ans");
        }

    }

}
