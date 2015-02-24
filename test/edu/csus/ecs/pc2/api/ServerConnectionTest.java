package edu.csus.ecs.pc2.api;

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.JOptionPane;

import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;
import edu.csus.ecs.pc2.api.implementation.LanguageImplementation;
import edu.csus.ecs.pc2.api.implementation.ProblemImplementation;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.InternalControllerSpecial;
import edu.csus.ecs.pc2.core.log.NullController;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageAutoFill;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;
import edu.csus.ecs.pc2.core.report.ProblemsReport;
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

    public ServerConnectionTest(String name) {
        super(name);
    }

    public ServerConnectionTest() {
        super();
    }

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

        assertNotNull("Expecting getElementId for language ", languageImplementation.getElementId());

        Problem problem = contest.getProblems()[0];
        ProblemImplementation problemImplementation = new ProblemImplementation(problem.getElementId(), contest);

        assertNotNull("Expecting getElementId for problem ", problemImplementation.getElementId());

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

        initContest(contest, "team2");
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

        Problem problemFetched = fetchProblemFromController(testController, 3000);

        assertNotNull("Expecting problem from controller", problemFetched);

        ProblemImplementation probImplementation = (ProblemImplementation) firstProblem;

        assertEquals("Expecting same element id for problem ", probImplementation.getElementId(), problemFetched.getElementId());

    }

    private void initContest(IInternalContest contest, String login) {

        // set contest login
        ClientId clientId = InternalController.loginShortcutExpansion(contest.getSiteNumber(), login);
        contest.setClientId(clientId);

        contest.startContest(contest.getSiteNumber());
    }

    // private IProblem getLastProblem(IContest iContest) {
    // IProblem[] problems = iContest.getProblems();
    // return problems[problems.length-1];
    // }

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
            System.out.println("debug 22" + new Date() + " problem " + contoller.getSaveProblem());
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
    class ServerConnectionTester extends ServerConnection {

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
        
        
        public IInternalContest getInternalContest(){
            return this.internalContest;
        }
        
        
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        
        try {
            if (args.length == 0) {
                
                // Run test Login prompt for fields 

                String login = JOptionPane.showInputDialog("Enter login name", "team4");
                if (login == null || login.trim().length() < 1) {
                    System.out.println("No login specified, exiting");
                    System.exit(4);
                }

                String password = JOptionPane.showInputDialog("Enter password", login);

                
                new ServerConnectionTest().testLogin(login, password);
               
            } else {
                
                String firstArg = args[0];
                
                if ("addA".equalsIgnoreCase(firstArg)) {
                    
                    new ServerConnectionTest().addAccountTest();
                    
                } else if ("addP".equalsIgnoreCase(firstArg)) {
                    
                    new ServerConnectionTest().addProblemTest();
                    
                } else 
                    
                
                if (args[0].equalsIgnoreCase("--help")) {
                    System.out.println("ServerConnectionTest [--help] [login|addA|addP]");
                    System.out.println();
                    System.out.println("If no parameters passed will prompt for login and password");
                    System.out.println();
                    System.out.println("login - login and password for test login ");
                    System.out.println();
                    System.out.println("adda - test add account method");
                    System.out.println("addp - test add problem method");
                    System.out.println();
                    System.out.println("When passes test prints: PASSED Test ");
                } else {
                    
                    // run test login 
                    
                    new ServerConnectionTest().testLogin(firstArg, firstArg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        
            }

  

    public void testTextValidClientType() throws Exception {

        ServerConnection connection = new ServerConnection();

        String name = "TEAM";
        boolean valid = connection.isValidAccountTypeName(name);
        assertTrue("Expecting " + name + " to be valid", valid);

        name = "BANNANA";
        valid = connection.isValidAccountTypeName(name);
        assertFalse("Expecting " + name + " to NOT be valid", valid);
    }

    /**
     * Add Account Unit test.
     * 
     * <P>
     * To test Bug 884:
     * <li> Create admin 2 account
     * <li> Run: <code>ServerConnectionTest adda</code>
     * 
     * @throws Exception
     */
    public void addAccountTest() throws Exception {

        ServerConnection connection = new ServerConnection();

        String user = "administrator2";
        
        try {
            
            IContest contest = connection.login(user, user);
            
            String nextTeamId = Integer.toString(contest.getTeams().length + 1);
            
//            connection.addAccount("TEAM", "Team " + nextTeamId, "team"+nextTeamId);
            connection.addAccount("TEAM", "Team + " + nextTeamId, "team"+nextTeamId+"Plus");
            
            Thread.sleep(1000); // sleep so packet can be sent/processed
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            connection.logoff();
        }
    }
    
    /**
     * Add Problem Unit test.
     * 
     * <P>
     * To test Bug 886:
     * <li> Create admin 2 account
     * <li> Run: <code>ServerConnectionTest addP</code>
     * 
     * @throws Exception
     */

    private void addProblemTest() throws NotLoggedInException {
        ServerConnection connection = new ServerConnection();

        String user = "administrator2";
        
        try {
            
            connection.login(user, user);
            
            String data = getSamplesSourceFilename("sumit.dat");
            String answer = getSamplesSourceFilename("sumit.ans");
            
            File dataFile = new File(data);
            File answerFile = new File(answer);
            
            connection.addProblem("Sumit Add Problem", "sumit2", dataFile, answerFile, false, null);
            
            Thread.sleep(1000); // sleep so packet can be sent/processed
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            connection.logoff();
        }
    }
    
    public void testAddProblem() throws Exception {
        
        SampleContest sample = new SampleContest();
        
        IInternalContest contest = sample.createContest(1, 1, 0,0,false);
        InternalControllerSpecial special = new InternalControllerSpecial(contest);
        
        ServerConnectionTester tester = createServerConnectionTester();
        tester.setController(special);
     
        String data = getSamplesSourceFilename("sumit.dat");
        String answer = getSamplesSourceFilename("sumit.ans");
        
        File dataFile = new File(data);
        File answerFile = new File(answer);
        tester.addProblem("Sumit Add Problem", "sumit2", dataFile, answerFile, false, null);
        
        Packet[] list = special.getPacketList();
        assertEquals("Expecting packets sent", 1, list.length);
        
        Packet packetOne = list[0];
        
        assertEquals("Expecting ", "ADD_SETTING", packetOne.getType().toString());
        
//        dumpPackets(special, contest);
        
        Problem problem = (Problem) PacketFactory.getObjectValue(packetOne, PacketFactory.PROBLEM);
        assertNotNull("Expecting a PROBLEM in packet ", problem);
    }

    private ServerConnectionTester createServerConnectionTester() {
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createStandardContest();
        
        ensureOutputDirectory();
        String storageDirectory = getOutputDataDirectory();

        IInternalController controller = sample.createController(contest, storageDirectory, true, false);

        ServerConnectionTester tester = new ServerConnectionTester();
        tester.setContest(contest);
        tester.setController(controller);
        return tester;
    }
    
    
    
    public void dumpPacket(Packet packet, String message, IInternalContest contest, IInternalController controller) {

        System.out.println ("Packet " + packet.getType() + " (Seq #" + packet.getPacketNumber() + " ) " + message);
        System.out.println ("  From: " + packet.getSourceId() + " (" + packet.getHostName() + " @ " + packet.getHostAddress() + ")" + " (Contest Id: " + packet.getContestIdentifier() + ")");
        System.out.println ("    To: " + packet.getDestinationId());
        Object obj = packet.getContent();
        if (obj instanceof Properties) {
            Properties prop = (Properties) obj;
            Enumeration<?> enumeration = prop.keys();

            while (enumeration.hasMoreElements()) {
                String element = (String) enumeration.nextElement();
                System.out.println ("   key: " + element + " is: " + prop.get(element).getClass().getName() + " " );
                dumpElement("      :   ",prop.get(element), contest, controller);
            }
        } else {

            System.out.println ("  Contains: " + obj.toString() + " " + obj);
        }
    }

    private String dumpElement(String pad, Object object, IInternalContest inContest, IInternalController controller) {

        if (object instanceof Problem) {
            
            Problem problem = (Problem) object;
            ProblemsReport report = new ProblemsReport();
            report.setContestAndController(inContest, controller);
            report.writeRow(new PrintWriter(System.out), problem, null);
            return "";

        } else {
            return object.toString();
        }
    }

    public void dumpPackets(InternalControllerSpecial controller, IInternalContest contest) {
        Packet[] list = controller.getPacketList();
        System.out.println("There are "+list.length+" packets.");
        for (Packet packet : list) {
            dumpPacket(packet, "", contest, controller);
        }
    }

    
    public void testAddProblemValidated() throws Exception {

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createStandardContest();
        
        /**
         * Set to admin so will "send" packet.
         */
        ClientId admin = getAccounts(contest, Type.ADMINISTRATOR)[0].getClientId();
        contest.setClientId(admin);
        
        ensureOutputDirectory();
        String storageDirectory = getOutputDataDirectory();

        InternalControllerSpecial special = sample.createPacketController(contest, storageDirectory, true, false);

        ServerConnectionTester tester = new ServerConnectionTester();
        tester.setContest(contest);
        tester.setController(special);

        String data = getSamplesSourceFilename("sumit.dat");
        String answer = getSamplesSourceFilename("sumit.ans");

        File dataFile = new File(data);
        File answerFile = new File(answer);

        Properties properties = new Properties();
        
        properties.put(APIConstants.JUDGING_TYPE, APIConstants.COMPUTER_JUDGING_ONLY);
        properties.put(APIConstants.VALIDATOR_PROGRAM, "/home/pc2/validdiff");
        
        tester.addProblem("Sumit Add Problem", "sumit2", dataFile, answerFile, true, properties);
        
        Packet[] list = special.getPacketList();
        assertEquals("Expecting packets sent", 1, list.length);

        Packet packetOne = list[0];
        
        assertEquals("Expecting packet type", "ADD_SETTING", packetOne.getType().toString());
        
//        dumpPackets(special, contest);
        
        Problem problem = (Problem) PacketFactory.getObjectValue(packetOne, PacketFactory.PROBLEM);
        assertNotNull("Expecting a PROBLEM in packet ", problem);
        
        assertTrue("Computer Judged only", problem.isComputerJudged());
        assertFalse("Manual review", problem.isManualReview());

        assertEquals("Data file name", "sumit.dat", problem.getDataFileName());
        assertEquals("Answer file name", "sumit.ans", problem.getAnswerFileName());

        assertEquals("Validator cmd line ", "{:validator} {:infile} {:outfile} {:ansfile} {:resfile} ", problem.getValidatorCommandLine());

// TODO         assertEquals("Validator prog name ", "/home/pc2/validdiff", problem.getValidatorProgramName());
    }
    
    /**
     * Test add problem, manual review only.
     * 
     * @throws Exception
     */
    public void testAddProblemDefault() throws Exception {
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createStandardContest();
        
        /**
         * Set to admin so will "send" packet.
         */
        ClientId admin = getAccounts(contest, Type.ADMINISTRATOR)[0].getClientId();
        contest.setClientId(admin);
        
        ensureOutputDirectory();
        String storageDirectory = getOutputDataDirectory();

        InternalControllerSpecial special = sample.createPacketController(contest, storageDirectory, true, false);

        ServerConnectionTester tester = new ServerConnectionTester();
        tester.setContest(contest);
        tester.setController(special);

        String data = getSamplesSourceFilename("sumit.dat");
        String answer = getSamplesSourceFilename("sumit.ans");

        File dataFile = new File(data);
        File answerFile = new File(answer);

        Properties properties = new Properties();
        
        tester.addProblem("Sumit Add Problem", "sumit2", dataFile, answerFile, true, properties);
        
        Packet[] list = special.getPacketList();
        assertEquals("Expecting packets sent", 1, list.length);

        Packet packetOne = list[0];
        
        assertEquals("Expecting ", "ADD_SETTING", packetOne.getType().toString());
        
//        dumpPackets(special, contest);
        
        Problem problem = (Problem) PacketFactory.getObjectValue(packetOne, PacketFactory.PROBLEM);
        assertNotNull("Expecting a PROBLEM in packet ", problem);
        
        assertFalse("Computer Judged only", problem.isComputerJudged());
        assertTrue("Manual review", problem.isManualReview()); 
        
    }

    public void testAddAccount() throws Exception {
        
        SampleContest sample = new SampleContest();
        
        IInternalContest contest = sample.createContest(1, 1, 0, 0, false);
        InternalControllerSpecial special = new InternalControllerSpecial(contest);

        ServerConnectionTester tester = createServerConnectionTester();
        tester.setController(special);

        String name = "Judge One";
        String pass = "judgepassword";
        tester.addAccount(Type.JUDGE.toString(), name, pass);

        Packet[] list = special.getPacketList();
        assertEquals("Expecting packets sent", 1, list.length);

        Packet packetOne = list[0];
        
        assertEquals("Expecting ", "ADD_SETTING", packetOne.getType().toString());
        
//        dumpPackets(special, contest);
        
        Account account = (Account) PacketFactory.getObjectValue(packetOne, PacketFactory.ACCOUNT);
        assertNotNull("Expecting a ACCOUNT in packet ", account);
        
        assertEquals("Expect account name", name, account.getDisplayName());
        assertEquals("Expect account password", pass, account.getPassword());
        
    }
    
    public void testAddLanguage() throws Exception {
        
        SampleContest sample = new SampleContest();
        
        IInternalContest contest = sample.createContest(1, 1, 0, 0, false);
        InternalControllerSpecial special = new InternalControllerSpecial(contest);

        ServerConnectionTester tester = createServerConnectionTester();
        tester.setController(special);

        tester.addLanguage(LanguageAutoFill.GNUCPPTITLE);

        Packet[] list = special.getPacketList();
        assertEquals("Expecting packets sent", 1, list.length);

        Packet packetOne = list[0];
        
        assertEquals("Expecting ", "ADD_SETTING", packetOne.getType().toString());
        
//        dumpPackets(special, contest);
        
        Language language = (Language) PacketFactory.getObjectValue(packetOne, PacketFactory.LANGUAGE);
        
        Language expected = LanguageAutoFill.createAutoFilledLanguage(LanguageAutoFill.GNUCPPTITLE);
        
        assertTrue("Expecting same", expected.isSameAs(language));
    }

    /**
     * Test add interpreted language.
     * @throws Exception
     */
    public void testAddLanguagePerl() throws Exception {
        
        SampleContest sample = new SampleContest();
        
        IInternalContest contest = sample.createContest(1, 1, 0, 0, false);
        InternalControllerSpecial special = new InternalControllerSpecial(contest);

        ServerConnectionTester tester = createServerConnectionTester();
        tester.setController(special);

        tester.addLanguage(LanguageAutoFill.PERLTITLE);

        Packet[] list = special.getPacketList();
        assertEquals("Expecting packets sent", 1, list.length);

        Packet packetOne = list[0];
        
        assertEquals("Expecting ", "ADD_SETTING", packetOne.getType().toString());
        
//        dumpPackets(special, contest);
        
        Language language = (Language) PacketFactory.getObjectValue(packetOne, PacketFactory.LANGUAGE);
        
        Language expected = LanguageAutoFill.createAutoFilledLanguage(LanguageAutoFill.PERLTITLE);
        
        assertTrue("Expecting same", expected.isSameAs(language));
    }


//    // public static TestSuite NotUsedSuite() {
//    public static TestSuite suite() {
//
//        TestSuite suite = new TestSuite(ServerConnectionTest.class);
//
//        String singletonTestName = "";
//        singletonTestName = "addAccountTest";
//        suite.addTest(new ServerConnectionTest(singletonTestName));
//
//        return suite;
//    }

}
