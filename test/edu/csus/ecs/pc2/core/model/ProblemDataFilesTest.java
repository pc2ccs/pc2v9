package edu.csus.ecs.pc2.core.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.core.util.JUnitUtilities;

/**
 * Tests for ProblemDataFiles.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 * 
 */
public class ProblemDataFilesTest extends AbstractTestCase {

    private String loadDir = "testdata" + File.separator;

    private SampleContest sample = new SampleContest();

    protected void setUp() throws Exception {
        String projectPath = JUnitUtilities.locate(loadDir);
        if (projectPath == null) {
            throw new Exception("Unable to locate " + loadDir);
        }
        File dir = new File(projectPath + File.separator + loadDir);
        if (dir.exists()) {
            loadDir = dir.toString() + File.separator;
        } else {
            System.err.println("could not find " + loadDir);
            throw new Exception("Unable to locate " + loadDir);
        }
        super.setUp();
    }

    public void testEmpty() {
        Problem p1 = new Problem("A");
        ProblemDataFiles pdf1 = new ProblemDataFiles(p1);
        ProblemDataFiles pdf2 = null;
        try {
            pdf2 = pdf1.copy(p1);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            assertTrue("copy failure", false);
            // if this was in clone, we would either fail the clone
            // or blank out the validator/data/answer file names.
        }
        assertTrue("copy failed", pdf1.isSameAs(pdf2));
    }

    public void testOne() {
        Problem p1 = new Problem("A");
        ProblemDataFiles pdf1 = new ProblemDataFiles(p1);
        SerializedFile dataFile;
        dataFile = new SerializedFile(loadDir + "pdftest" + File.separator + "sumit.dat");
        pdf1.setJudgesDataFile(dataFile);
        SerializedFile[] fileArray = new SerializedFile[1];
        fileArray[0] = dataFile;
        pdf1.setJudgesAnswerFiles(fileArray);
        ProblemDataFiles pdf2 = null;

        try {
            pdf2 = pdf1.copy(p1);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            assertTrue("copy failure", false);
            // if this was in clone, we would either fail the clone
            // or blank out the validator/data/answer file names.
        }
        assertTrue("copy failed with 1 answer/1 data file", pdf1.isSameAs(pdf2));

    }

    public void testTwo() {
        Problem p1 = new Problem("A");
        ProblemDataFiles pdf1 = new ProblemDataFiles(p1);
        SerializedFile dataFile;
        dataFile = new SerializedFile(loadDir + "pdftest" + File.separator + "sumit.dat");
        pdf1.setJudgesDataFile(dataFile);
        SerializedFile[] fileArray = new SerializedFile[2];
        fileArray[0] = dataFile;
        fileArray[1] = new SerializedFile(loadDir + "pdftest" + File.separator + "sumit.ans");
        pdf1.setJudgesAnswerFiles(fileArray);
        ProblemDataFiles pdf2 = null;

        try {
            pdf2 = pdf1.copy(p1);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            assertTrue("copy failure", false);
            // if this was in clone, we would either fail the clone
            // or blank out the validator/data/answer file names.
        }
        assertTrue("copy failed with 2 answer/1 data file", pdf1.isSameAs(pdf2));

    }

    /**
     * Test createProblemDataFiles.
     * 
     * @throws Exception
     */
    public void testcreateProblemDataFiles() throws Exception {

        IInternalContest contest = sample.createStandardContest();

        Problem[] problems = contest.getProblems();

        Problem firstProblem = problems[0];

        /**
         * Number of test cases/files to create.
         */
        int numCases = 321; 
        
        ProblemDataFiles dataFiles = sample.createProblemDataFiles(firstProblem, numCases);

        assertEquals("Expecting data files ", numCases, dataFiles.getJudgesDataFiles().length);
        assertEquals("Expecting answer files ", numCases, dataFiles.getJudgesAnswerFiles().length);

        SerializedFile[] files = dataFiles.getJudgesAnswerFiles();
        String firstFile = files[0].getName();
        for (SerializedFile serializedFile : files) {
            assertEquals("Expecting all filenames to be same ", firstFile, serializedFile.getName());
            assertEquals("Expecting all checksums to be same ", files[0].getSHA1sum(), serializedFile.getSHA1sum());
        }

    }

    public void testfullJudgesDataFilenames() throws Exception {

        sample = new SampleContest();

        IInternalContest contest = sample.createStandardContest();

        contest.setClientId(contest.getAccounts(Type.JUDGE).firstElement().getClientId());

        Problem[] problems = contest.getProblems();
        Problem firstProblem = problems[0];

        int numCases = 321;

        ProblemDataFiles dataFiles = sample.createProblemDataFiles(firstProblem, numCases);

        String[] list = dataFiles.getFullJudgesAnswerFilenames(contest, "executable22");

        assertEquals("Expecting judge filenames count ", numCases, list.length);
        
     // TODO 164 - write code to test filenames

        // for (String string : list) {
        // debugPrint("debug 22 judgeFullNames "+string);
        // }

    }

    public void testfullJudgesAnswerFilenames() throws Exception {

        // TODO 164 - write coe to test for judge answer filenames, see testfullJudgesDataFilenames 
    }

    /**
     * Test check and create for internal files for Judge.
     * 
     * Tests {@link ProblemDataFiles#checkAndCreateFiles(IInternalContest, String).
     * 
     * @throws Exception
     */
    public void testcheckAndCreateFilesInternal() throws Exception {

        String testDir = getOutputDataDirectory();

        removeDirectory(testDir);
        ensureDirectory(testDir);

        sample = new SampleContest();

        IInternalContest contest = sample.createStandardContest();

        contest.setClientId(contest.getAccounts(Type.JUDGE).firstElement().getClientId());
        
         Problem[] problems = contest.getProblems();
        
         Problem firstProblem = problems[0];
        
         int numCases = 12;
        
         ProblemDataFiles dataFiles = sample.createProblemDataFiles(firstProblem, numCases);
        
         String executeDirectory = testDir + File.separator + "executable22";
         ensureDirectory(executeDirectory);
         
         checkFiles(contest, dataFiles, executeDirectory);
    }

    /**
     * Tests whether external files present for Judge.
     * 
     * Tests {@link ProblemDataFiles#checkAndCreateFiles(IInternalContest, String).
     * 
     * @throws Exception
     */
    public void testcheckAndCreateFilesExternalJudge() throws Exception {

        String inputTestDirectory = getDataDirectory(this.getName());
        String testDir = getOutputDataDirectory();

        removeDirectory(testDir);
        ensureDirectory(testDir);

        sample = new SampleContest();

        IInternalContest contest = sample.createStandardContest();

        // Set the external data location for CDP files for a judge.
        setCDPLocation(contest, inputTestDirectory);

        ClientId clientId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();
        contest.setClientId(clientId);
        
        Problem firstProblem = contest.getProblems()[0];
        ProblemDataFiles dataFiles = loadExternalFiles(contest, firstProblem, inputTestDirectory);

        checkFiles(contest, dataFiles, testDir);
    }


    /**
     * Tests whether external files present for Administrator.
     * 
     * Tests {@link ProblemDataFiles#checkAndCreateFiles(IInternalContest, String).
     * 
     * @throws Exception
     */
    public void testcheckAndCreateFilesExternalAdministrator() throws Exception {

        String inputTestDirectory = getDataDirectory(this.getName());
        String testDir = getOutputDataDirectory();

        removeDirectory(testDir);
        ensureDirectory(testDir);

        sample = new SampleContest();

        IInternalContest contest = sample.createStandardContest();

        ClientId clientId = contest.getAccounts(Type.ADMINISTRATOR).firstElement().getClientId();
        contest.setClientId(clientId);
        
        Problem[] problems = contest.getProblems();
        Problem firstProblem = problems[0];

        ProblemDataFiles dataFiles = loadExternalFiles(contest, firstProblem, inputTestDirectory);
        
        dumpProblemDataFiles("Admin", contest, dataFiles, null);

        checkFiles(contest, dataFiles, testDir);
    }
    
    public void testLoadDataFiles() throws Exception {
        
        // TODO 164 write test code 
//         ProblemDataFiles dataFiles = sample.loadDataFiles(problem, null, problemFilesDirectory, ".in", ".ans");
    }
    

    private void dumpProblemDataFiles(String string, IInternalContest contest, ProblemDataFiles dataFiles, String executableDir) {
        
        debugPrint("dumpProblemDataFiles " + string);

        String[] files = dataFiles.getFullJudgesAnswerFilenames(contest, executableDir);
        for (int i = 0; i < files.length; i++) {
            debugPrint("dumpProblemDataFiles data[" + i + "] = " + files[i]);
        }
        files = dataFiles.getFullJudgesDataFilenames(contest, executableDir);
        for (int i = 0; i < files.length; i++) {
            debugPrint("dumpProblemDataFiles answer[" + i + "] = " + files[i]);
        }

    }

    /**
     * Set the base directory (in the contest) where the CDP is on the the judge machine.
     * 
     * @param contest
     * @param judgeCDPDirectory
     */
    private void setCDPLocation(IInternalContest contest, String judgeCDPDirectory) {

        ContestInformation info = contest.getContestInformation();
        
        if (info == null){
            info = new ContestInformation();
        }
        
        info.setJudgeCDPBasePath(judgeCDPDirectory);
    }

    /**
     * Create datafiles with external CDP files.
     * 
     * 
     * 
     * @param contest
     * @param inputTestDirectory base CDP data files location 
     * @return
     * @throws IOException
     */
    private ProblemDataFiles loadExternalFiles(IInternalContest contest, Problem problem, String baseDirectory) throws IOException {

        problem.setUsingExternalDataFiles(true);

        String problemFilesDirectory = Utilities.getSecretDataPath(baseDirectory, problem);
        ensureDirectory(problemFilesDirectory);
//        startExplorer(problemFilesDirectory);
        ProblemDataFiles dataFiles = sample.loadDataFiles(problem, null, problemFilesDirectory, ".in", ".ans");

        return dataFiles;

    }
 
    /**
     * Test whether all expected files exist in ProblemDataFiles.
     * 
     * Uses {@link ProblemDataFiles#checkAndCreateFiles(IInternalContest, String).
     * Tests {@link ProblemDataFiles#checkAndCreateFiles(IInternalContest, String).
     * 
     * @param contest
     * @param dataFiles
     * @param executeDirectory
     * @throws FileNotFoundException
     */
    private void checkFiles(IInternalContest contest, ProblemDataFiles dataFiles, String executeDirectory) throws FileNotFoundException {

        // make sure all internal files are created.
        // note with external files on judges this will fail, because if the judgeCDPBasePath
        // is set, then the executedirectory becomes where it looks for files...
        dataFiles.checkAndCreateFiles(contest, executeDirectory);

        // data files
        String[] list = dataFiles.getFullJudgesDataFilenames(contest, executeDirectory);
        for (String filename : list) {
            assertFileExists(filename);
        }

        // answer files
        list = dataFiles.getFullJudgesAnswerFilenames(contest, executeDirectory);
        for (String filename : list) {
            assertFileExists(filename);
        }

    }
    
    /**
     * Test whether exception thrown when executeDirectory does not exist.
     * 
     * Tests {@link ProblemDataFiles#checkAndCreateFiles(IInternalContest, String).
     * 
     * @throws Exception
     */
    public void testcheckAndCreateFilesInternalNoExecutableDirectory() throws Exception {

        String testDir = getOutputDataDirectory();

        removeDirectory(testDir);
        ensureDirectory(testDir);

        sample = new SampleContest();

        IInternalContest contest = sample.createStandardContest();

        contest.setClientId(contest.getAccounts(Type.JUDGE).firstElement().getClientId());
        
         Problem[] problems = contest.getProblems();
        
         Problem firstProblem = problems[0];
        
         int numCases = 12;
        
         ProblemDataFiles dataFiles = sample.createProblemDataFiles(firstProblem, numCases);
         
        String executeDirectory = testDir + File.separator + "nodirectorycreated";
        try {
            checkFiles(contest, dataFiles, executeDirectory);
        } catch (RuntimeException e) {
            
            // Expecting to not be able to write sumit.dat becuase executeDirectory does not exist.
            
            String fullMessage = "Unable to write " + executeDirectory + File.separator + "sumit.dat";
            assertEquals("Expecting exception message ", fullMessage, e.getMessage());
        }
    }
    
    public void testremoveDataSetItem3() throws Exception {
        
        String testDir = getOutputDataDirectory();

        removeDirectory(testDir);
        ensureDirectory(testDir);

        sample = new SampleContest();

        IInternalContest contest = sample.createStandardContest();

        contest.setClientId(contest.getAccounts(Type.JUDGE).firstElement().getClientId());
        
         Problem[] problems = contest.getProblems();
        
         Problem firstProblem = problems[0];
        
         int numCases = 12;
        
         ProblemDataFiles dataFiles = sample.createProblemDataFiles(firstProblem, numCases);
         
         dataFiles.removeDataSet(3);
        
    }
    
    public void testremoveDataSetItem0() throws Exception {

        String testDir = getOutputDataDirectory();

        removeDirectory(testDir);
        ensureDirectory(testDir);

        sample = new SampleContest();

        IInternalContest contest = sample.createStandardContest();

        contest.setClientId(contest.getAccounts(Type.JUDGE).firstElement().getClientId());

        Problem[] problems = contest.getProblems();

        Problem firstProblem = problems[0];

        int numCases = 12;

        ProblemDataFiles dataFiles = sample.createProblemDataFiles(firstProblem, numCases);

        dataFiles.removeDataSet(0);
    }

    public void testremoveDataSetItemRightEdge() throws Exception {

        String testDir = getOutputDataDirectory();

        removeDirectory(testDir);
        ensureDirectory(testDir);

        sample = new SampleContest();

        IInternalContest contest = sample.createStandardContest();

        contest.setClientId(contest.getAccounts(Type.JUDGE).firstElement().getClientId());

        Problem[] problems = contest.getProblems();

        Problem firstProblem = problems[0];

        int numCases = 12;

        ProblemDataFiles dataFiles = sample.createProblemDataFiles(firstProblem, numCases);

        dataFiles.removeDataSet(numCases - 1);

    }

    /**
     * Test remove data set N.
     * 
     * Should error with out of index
     * 
     * @throws Exception
     */
    public void testremoveDataSetN() throws Exception {

        String testDir = getOutputDataDirectory();

        removeDirectory(testDir);
        ensureDirectory(testDir);

        sample = new SampleContest();

        IInternalContest contest = sample.createStandardContest();

        contest.setClientId(contest.getAccounts(Type.JUDGE).firstElement().getClientId());

        Problem[] problems = contest.getProblems();

        Problem firstProblem = problems[0];

        int numCases = 22;

        ProblemDataFiles dataFiles = sample.createProblemDataFiles(firstProblem, numCases);

        try {
            dataFiles.removeDataSet(numCases);
            fail("Should have failed, with ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
            assertEquals("" + (numCases - 1), e.getMessage());
        }
    }

    public void testtestremoveDataSet() throws Exception {

        Problem problem = new Problem("removeOne");
        ProblemDataFiles dataFiles = new ProblemDataFiles(problem);
        dataFiles.removeDataSet(1);
    }
    
    public void testCopy() throws Exception {
        
//        String testDir = getOutputDataDirectory();
//
//        removeDirectory(testDir);
//        ensureDirectory(testDir);
//
//        SampleContest sample = new SampleContest();
//
//        IInternalContest contest = sample.createStandardContest();
//
//        contest.setClientId(contest.getAccounts(Type.JUDGE).firstElement().getClientId());
//        
//         Problem[] problems = contest.getProblems();
//        
//         Problem firstProblem = problems[0];
//        
//         int numCases = 12;
//        
//         ProblemDataFiles dataFiles = sample.createProblemDataFiles(firstProblem, numCases);
         
//         ProblemDataFiles data = dataFiles.copy(firstProblem);
         
        // TODO 917 add test with data and dataFiles
         

    }
        
}
