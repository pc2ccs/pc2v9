package edu.csus.ecs.pc2.core.export;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;

/**
 * Test for YAML export.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: ExportYAMLTest.java 207 2011-07-08 18:32:38Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/test/edu/csus/ecs/pc2/export/ExportYAMLTest.java $
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
        assertFileContentsEquals(new File(actualFileName), new File(expectedFileName), 4);

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
    
    public void testCreateYaml() throws Exception {
        
        String dataDirectory = getDataDirectory("testCreateYaml");
        ensureDirectory(dataDirectory);
        
        String testDirectory = getOutputDataDirectory("testCreateYaml");
        ensureDirectory(testDirectory);
        
        startExplorer(dataDirectory);
        startExplorer(testDirectory);
        
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
            convertToLocalFileSeperators (expectedContestYamlFile);
        }
        
        stripStartTime(actualContestYamlFile);
//        editFile(expectedContestYamlFile);
        
        assertFileContentsEquals(new File(expectedContestYamlFile), new File(actualContestYamlFile), 4);
        
//        String filename = testDirectory + File.separator + IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;
        
        String actualFileName =testDirectory + File.separator + IContestLoader.DEFAULT_PROBLEM_SET_YAML_FILENAME;
        String expectedFileName = dataDirectory + File.separator + IContestLoader.DEFAULT_PROBLEM_SET_YAML_FILENAME;
        
        // Start compare on line 4 to skip header/version/time information
        assertFileContentsEquals(new File(actualFileName), new File(expectedFileName), 4);


        exportYAML = null;

    }
    
    /**
     * Will convert input file ./ to local path/file separators.
     * 
     * Only applies to ./ string.
     * 
     * @param filename
     * @throws Exception
     */
    private void convertToLocalFileSeperators(String filename) throws Exception {

        File actualFile = new File(filename);
        File newFile = new File(filename + ".tmp");
        PrintWriter printWriter = new PrintWriter(newFile);
        String[] actualContents = Utilities.loadFile(actualFile.getAbsolutePath());
        for (int i = 0; i < actualContents.length; i++) {
            String string = actualContents[i];
            string = string.replace("./", ".\\");
            printWriter.println(string);
        }
        printWriter.close();
        actualFile.delete();
        boolean result = newFile.renameTo(actualFile);
        assertTrue("rename Failed", result);
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

}
