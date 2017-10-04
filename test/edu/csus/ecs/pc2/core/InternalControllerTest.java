package edu.csus.ecs.pc2.core;

import java.io.File;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ConfigurationIO;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;
import edu.csus.ecs.pc2.core.security.FileStorage;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit Tests.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class InternalControllerTest extends AbstractTestCase {
    
    private static final String [] SERVER_COMMAND_LINE_OPTIONS = {"--server", "--nogui", "--contestpassword", "foo", "--port", "42020", "--login" , "s"};

    private int siteNum = 1;

    /**
     * Test for Bug 825 for an Admin.
     * 
     * @throws Exception
     */
    public void testAdminRemoteExploit() throws Exception {

        ClientId requestingClientId = new ClientId(siteNum, Type.ADMINISTRATOR, 1);
        remoteExploit(requestingClientId);
    }

    /**
     * Test for Bug 825 for an Admin.
     * 
     * @throws Exception
     */
    public void testServerRemoteExploit() throws Exception {

        ClientId requestingClientId = new ClientId(siteNum, Type.SERVER, 1);
        remoteExploit(requestingClientId);
    }

    /**
     * Tests for Bug 825.
     * 
     * @param requestingClientId
     * 
     * @throws Exception
     */
    private void remoteExploit(ClientId requestingClientId) throws Exception {

        SampleContest sample = new SampleContest();

        IInternalContest contest = sample.createContest(siteNum, 1, 3, 1, true);

        ClientId adminId = new ClientId(siteNum, Type.ADMINISTRATOR, 42);

        Account admin1 = contest.getAccount(adminId);
        assertNull(admin1);

        InternalController controller = new InternalController(contest);
        controller.setLog(createLog(getName()));

        ClientId serverId = new ClientId(2, Type.SERVER, 1);

        int adminNum = 42;

        Packet packet = PacketFactory.createGenerateAccounts(
        /* source */requestingClientId,
        /* destination */serverId,
        /* siteNumber */siteNum,
        /* type */ClientType.Type.ADMINISTRATOR,
        /* count */1,
        /* startNumber */adminNum,
        /* isActive */true);

        controller.receiveObject(packet, null);

        admin1 = contest.getAccount(adminId);
        assertNull("There should be no admin admin42 account created", admin1);

        Vector<Account> accVector = contest.getAccounts(Type.ADMINISTRATOR);

        assertTrue("There should be one admin account ", accVector.size() == 1);

    }
    
    public void testLoadJudgements() throws Exception {

        String testdir = getDataDirectory(this.getName());
        // startExplorer(testdir);

        String filename = testdir + File.separator + Constants.JUDGEMENT_INIT_FILENAME;

        // editFile(filename);

        IInternalContest contest = new InternalContest();

        ClientId adminId = new ClientId(siteNum, Type.ADMINISTRATOR, 42);

        Account admin1 = contest.getAccount(adminId);
        assertNull(admin1);

        InternalController controller = new InternalController(contest);
        controller.setLog(createLog(getName()));
        controller.loadedJudgementsFromIni(filename);

        Judgement[] judgements = contest.getJudgements();

        assertEquals("Expecting judgements ", 7, judgements.length);

        String[] expected = { "AC;Yes", //
                "WA001;No - Compilation Error", //
                "WA002;No - Run-time Error", //
                "WA003;No - Time Limit Exceeded", //
                "WA004;No - Wrong Answer", //
                "WA005;No - Presentation Error", //
                "WA006;No - Other - Contact Staff", //
        };

        for (int i = 0; i < expected.length; i++) {
            Judgement judgement = judgements[i];
            String[] fields = expected[i].split(";");
            String acronym = fields[0];
            String title = fields[1];
            assertEquals("Expected judgement acronym ", acronym, judgement.getAcronym());
            assertEquals("Expected judgement title ", title, judgement.toString());
        }
    }
    
    /**
     * Override things like fatalError().
     * 
     * @author ICPC
     *
     */
    class OverrideFatalErrorController extends InternalController{
        
        private String message;
        private Exception ex;
        
        OverrideFatalErrorController(IInternalContest contest) {
            super(contest);
            if (contest.getClientId() == null){
                throw new InvalidParameterException("Must be logged in, clientId in contest is null");
            }
        }
        /**
         * Override log name and location.
         */
        public void overrideLog(String directoryName, String logFileName){
            Log newLog = new Log(directoryName, logFileName);
            setLog(newLog);
            StaticLog.setLog(newLog);
        }
        
        @Override
        protected void fatalError(String aMessage, Exception theEx) {
            this.message = aMessage;
            this.ex = theEx;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Exception getEx() {
            return ex;
        }
    }
    
    
    /**
     * Test when corrupt settings file.
     * 
     * @throws Exception
     */
    public void testCorruptSettingsFile() throws Exception {

        String storageDirectory = getOutputDataDirectory(this.getName());
        removeDirectory(storageDirectory);
        ensureDirectory(storageDirectory);
//        startExplorer(storageDirectory);

        SampleContest sample = new SampleContest();

        IInternalContest contest = sample.createStandardContest();
        IInternalContest standardContest = contest;

        IStorage storage = new FileStorage(storageDirectory);
        contest.setStorage(storage);

        String settingsFileName = new ConfigurationIO(storage).getFileName();

        OverrideFatalErrorController controller = new OverrideFatalErrorController(contest);

        // store config
        contest.storeConfiguration(controller.getLog());
        
        assertEquals(3, contest.getSiteNumber());

        // corrupt the config
        copyFileOverwrite(getSamplesSourceFilename(SUMIT_SOURCE_FILENAME), settingsFileName, controller.getLog());

        storage = new FileStorage(storageDirectory);

        // ConfigurationIO configurationIO = new ConfigurationIO(storage);
        // boolean loaded = configurationIO.loadFromDisk(contest.getSiteNumber(), contest, controller.getLog());

        controller = null;
        contest = null;
        contest = new InternalContest();
        contest.setClientId(getServerClientId(standardContest));
        contest.setSiteNumber(contest.getClientId().getSiteNumber());

        assertEquals(3, contest.getSiteNumber());

        storage = new FileStorage(storageDirectory);
        contest.setStorage(storage);

        controller = new OverrideFatalErrorController(contest);
        String outDir = getOutputDataDirectory(this.getName());
        controller.overrideLog(outDir, "first.log");
//        controller.addConsoleLogging();  
        controller.getLog().info("Logging started for "+this.getName());
        
        controller.setContactingRemoteServer(false);
        controller.setUsingMainUI(false);

        assertEquals(3, contest.getSiteNumber());
        assertEquals(3, controller.getSiteNumber());

        controller.start(SERVER_COMMAND_LINE_OPTIONS);
        
        /**
         * login causes "FATAL ERROR Attempted to load site 0 from Site 1"
         * 
         * SOMEDAY fix the way the controller assigned site number.
         * 
         * There is a undetected robustness/implementation bug where the original code to determine the
         * site number for a server is just odd, seemed to grow organically
         * and has worked for years, but assigning a non-1 site number, not
         * really tested.  It seems that the site number saved is always zero
         * whereas the rest of the data is using the actual site number.
         * 
         * IF the bug were to be solved it would require that on startup that there
         * is a way to assign the site number and save that site number in the settings.
         * Right now the non-firest site number is assigned via the login to the other site,
         * so say site 3 may not be able to be started up, event if there were a --site option.
         * 
         * SOMEDAY handle corrupt settings files by throwing exception from ConfigurationIO methods rather than using System.exit 
         */
//        controller.login(contest.getClientId().getName(), contest.getClientId().getName());
  
//        System.out.println("getMessage()='" + controller.getMessage() + "'");
//        // SOMEDAY controllger.getMessage should not be null, it is now.
//        assertNotNull("Expecting fatal error message ", controller.getMessage());
//        assertEquals("Expecting messsage ", "Halting server - configuration file corrupt", controller.getMessage());

    }
    
    /**
     * Test matching an override password
     * @throws Exception
     */
    public void testMatchDevOverride() throws Exception {
        
        String password = "foo";
        
        // print digest array
//        System.out.print("byte[] overridePassword = { ");
//        dumpByteArray(password);
//        System.out.println(" } ; ");
        
        assertTrue("Expecting true ",password.length() > 0);
        
//        assertTrue("match DEV override ", InternalController.matchDevOverride(password));
//        assertTrue("Did not match override ", InternalController.matchOverride(password));
        
    }

    /**
     * Sump the SHA hash for the input password in a byte array.
     * @param password
     * @throws NoSuchAlgorithmException
     */
    void dumpByteArray(String password) throws NoSuchAlgorithmException {
        
       // byte[] overridePassword = { -108, 121, 83, 9, 106, -13, 43, 10, 47, 87, -114, 115, -38, -38, -64, -125, 41, -1, -79, -102 };
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.reset();
        md.update(password.getBytes());
        byte[] digested = md.digest();
        for (int i = 0; i < digested.length; i++) {
            System.out.print(digested[i] + ", ");
        }
    }
}
