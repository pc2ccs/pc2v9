package edu.csus.ecs.pc2.api;

import java.util.Date;

import javax.swing.JOptionPane;

import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.api.implementation.LanguageImplementation;
import edu.csus.ecs.pc2.api.implementation.ProblemImplementation;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.log.NullController;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * A test of the Server Connection class, prompts for use and password to login.
 * 
 * main method will run a program to test ServerConnection, see usage (--help option).
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ServerConnectionTest extends AbstractTestCase {

    public void testLogin(String login, String password) {

        ServerConnection serverConnection = new ServerConnection();
        try {
            IContest contest = serverConnection.login(login, password);
            System.out.println("PASSED Test - Logged in as " + contest.getMyClient().getLoginName());
        } catch (LoginFailureException e) {
            System.out.println("Could not login because " + e.getMessage());
        }
        try {
            serverConnection.logoff();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Test Bug 767 - submitRun may submit for the wrong IProblem
     * @throws Exception 
     */
    public void testSubmittedRun() throws Exception {
        
        SampleContest sampleContest = new SampleContest();
        IInternalContest contest = sampleContest.createStandardContest();
        
        // Test that classes expose getElementId
        
        Language language = contest.getLanguages()[0];
        LanguageImplementation languageImplementation = new LanguageImplementation(language);
        
        assertNotNull ("Expecting getElementId for language ", languageImplementation.getElementId());
        
        Problem problem = contest.getProblems()[0];
        ProblemImplementation problemImplementation = new ProblemImplementation(problem.getElementId(), contest);

        assertNotNull ("Expecting getElementId for problem ", problemImplementation.getElementId());
        
        // Add dup languages
        
        Language newLang = new Language(language.getDisplayName());
        contest.addLanguage(newLang);
        newLang = new Language(language.getDisplayName());
        contest.addLanguage(newLang);
        newLang = new Language(language.getDisplayName());
        contest.addLanguage(newLang);
        
        
        // Add dup problems
        
        Problem newProblem = new Problem(problem.getDisplayName());
        contest.addProblem(newProblem);
        newProblem = new Problem(problem.getDisplayName());
        contest.addProblem(newProblem);
        newProblem = new Problem(problem.getDisplayName());
        contest.addProblem(newProblem);
        
        InternalControllerTester testController = new InternalControllerTester();
        
        ServerConnectionTester serverConnection = new ServerConnectionTester();
        serverConnection.setController(testController);
        serverConnection.setContest(contest);
        
        initContest (contest, "team2");
        String testId = "team2";
        IContest iContest = serverConnection.login(testId, testId);
        
        // get last problem, which is a dup name
        
        IProblem firstProblem = iContest.getProblems()[0];
        
        /**
         * Submit run and check problem
         */
         String mainFileName = getSamplesSourceFilename(HELLO_SOURCE_FILENAME);
        ILanguage apilanguage = iContest.getLanguages()[0];
        serverConnection.submitRun(firstProblem, apilanguage, mainFileName, new String[0], 0, 0);
        
        Problem problemFetched = fetchProblemFromController (testController, 3000);
        
        assertNotNull ("Expecting problem from controller", problemFetched);
        
        ProblemImplementation probImplementation = (ProblemImplementation) firstProblem;
        
        assertEquals("Expecting same element id for problem ", probImplementation.getElementId(), problemFetched.getElementId());
        
    }
    
    private void initContest(IInternalContest contest, String login) {
        
        // set contest login
        ClientId clientId = InternalController.loginShortcutExpansion(contest.getSiteNumber(), login);
        contest.setClientId(clientId);
        
        contest.startContest(contest.getSiteNumber());
    }

//    private IProblem getLastProblem(IContest iContest) {
//        IProblem[] problems = iContest.getProblems();
//        return problems[problems.length-1];
//    }

    /**
     * Fetches saved problem from the contoller
     * @param contoller
     * @param timeoutMS timeout for operation
     * @return
     * @throws InterruptedException 
     */
    private Problem fetchProblemFromController(InternalControllerTester contoller, int timeoutMS) throws InterruptedException {
        long maxtime = new Date().getTime() + timeoutMS;
        long curTime = new Date().getTime();
        
        // while not timeout and saved problem has not been populated loop
        while (curTime < maxtime && contoller.getSaveProblem() == null) {
            Thread.sleep(500);
            curTime = new Date().getTime();
            System.out.println("debug 22"+new Date()+" problem "+contoller.getSaveProblem());
        }
          
        return contoller.getSaveProblem();
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    class InternalControllerTester extends NullController {

        private Problem saveProblem = null;

        @Override
        public void submitRun(Problem problem, Language language, String filename, SerializedFile[] otherFiles) throws Exception {
            // super.submitRun(problem, language, filename, otherFiles);
            saveProblem = problem;
        }
        
        @Override
        public void submitRun(Problem problem, Language language, String mainFileName, SerializedFile[] auxFileList, long overrideSubmissionTimeMS) throws Exception {
            saveProblem = problem;
        }
        
        @Override
        public void submitRun(Problem problem, Language language, String mainFileName, SerializedFile[] auxFileList, long overrideSubmissionTimeMS, long overrideRunId) throws Exception {
            saveProblem = problem;
        }
        

        public Problem getSaveProblem() {
            return saveProblem;
        }

        public void setSaveProblem(Problem saveProblem) {
            this.saveProblem = saveProblem;
        }
    }
    
    /**
     * ServerConnection that provides overrides.
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    class ServerConnectionTester extends ServerConnection{
        
        /**
         * Override internalController
         * @param internalContest
         */
        public void setController(IInternalController controller) {
            this.controller = controller;
        }
        
        /**
         * Override internalContest
         * @param internalContest
         */
        public void setContest(IInternalContest contest) {
            this.internalContest = contest;
        }
    }
    

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {

        if (args.length == 0) {

            // Get login and password from user

            String login = JOptionPane.showInputDialog("Enter login name", "team4");
            if (login == null || login.trim().length() < 1) {
                System.out.println("No login specified, exiting");
                System.exit(4);
            }

            String password = JOptionPane.showInputDialog("Enter password", login);

            new ServerConnectionTest().testLogin(login, password);

        } else {
            if (args[0].equalsIgnoreCase("--help")) {
                System.out.println("ServerConnectionTest [--help] [login]");
                System.out.println();
                System.out.println("If no parameters passed will prompt for login and password");
                System.out.println();
                System.out.println("login - login and password for test login ");
                System.out.println();
                System.out.println("When passes test prints: PASSED Test ");
            } else {
                new ServerConnectionTest().testLogin(args[0], args[0]);
            }
        }

    }
    
    public void testTextValidClientType() throws Exception {
        
        ServerConnection connection = new ServerConnection();
        
        String name = "TEAM";
        boolean valid = connection.isValidAccountTypeName(name);
        assertTrue("Expecting "+name+" to be valid", valid);
        
        name = "BANNANA";
        valid = connection.isValidAccountTypeName(name);
        assertFalse("Expecting "+name+" to NOT be valid", valid);
        
        
    }

}
