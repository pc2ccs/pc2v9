package edu.csus.ecs.pc2.core.export;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

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

//        addShortNames(contest);

        Problem[] problems = contest.getProblems();
        
        if (debugMode){
            for (Problem problem : contest.getProblems()) {
                System.out.println(problem.getDisplayName() + " " +problem.getShortName());
            }
        }
        
        String testDirectory = getOutputDataDirectory("testOne");
        ensureDirectory(testDirectory);

        sampleContest.addDataFiles(contest, testDirectory, problems[0], "sumit.dat", "sumit.ans");
        sampleContest.addDataFiles(contest, testDirectory, problems[1], "quads.in", "quads.ans");
        sampleContest.addDataFiles(contest, testDirectory, problems[4], "london.dat", "london.ans");
        sampleContest.addInternalValidator(contest, problems[4], 3);

        ExportYAML exportYAML = new ExportYAML();

        exportYAML.exportFiles(testDirectory, contest);

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
        
        String testDirectory = getOutputDataDirectory("testCreateYaml");
        ensureDirectory(testDirectory);
        
        IInternalContest contest = sampleContest.createContest(3, 3, 12, 5, true);
        
        asertProblemShortAssigned(contest);
        
        // Add Manual Evalution on problem 2
        
        addManualReview(contest, 2, true);
        
//        addShortNames(contest);

        ExportYAML exportYAML = new ExportYAML();

        exportYAML.exportFiles(testDirectory, contest);
        
        String actualContestYamlFile = testDirectory+File.separator+ExportYAML.CONTEST_FILENAME;
        
        String expectedContestYamlFile = getTestFilename("expected.contest.yaml");
        if (File.separator.equals("/")) {
            expectedContestYamlFile = getTestFilename("expected.unix.contest.yaml");
        }
//        editFile(actualContestYamlFile);
//        editFile(expectedContestYamlFile);
        
        assertFileContentsEquals(new File(expectedContestYamlFile), new File(actualContestYamlFile), 4);
        
//        String filename = testDirectory + File.separator + ExportYAML.CONTEST_FILENAME;

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
