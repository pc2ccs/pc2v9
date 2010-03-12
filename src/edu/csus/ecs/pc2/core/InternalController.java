package edu.csus.ecs.pc2.core;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JOptionPane;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.archive.PacketArchiver;
import edu.csus.ecs.pc2.core.exception.ContestSecurityException;
import edu.csus.ecs.pc2.core.log.EvaluationLog;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ILoginListener;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LoginEvent;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunExecutionStatus;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.Clarification.ClarificationStates;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;
import edu.csus.ecs.pc2.core.packet.PacketType;
import edu.csus.ecs.pc2.core.security.FileSecurity;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.core.transport.IBtoA;
import edu.csus.ecs.pc2.core.transport.ITransportManager;
import edu.csus.ecs.pc2.core.transport.ITwoToOne;
import edu.csus.ecs.pc2.core.transport.TransportException;
import edu.csus.ecs.pc2.core.transport.connection.ConnectionManager;
import edu.csus.ecs.pc2.profile.ProfileCloneSettings;
import edu.csus.ecs.pc2.ui.CountDownMessage;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.LoadUIClass;
import edu.csus.ecs.pc2.ui.LoginFrame;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Implementation of InternalContest InternalController.
 * 
 * Run Flow, submit run.
 * <ol>
 * <li> Team: {@link #submitRun(Problem, Language, String)}
 * <li> Server: {@link edu.csus.ecs.pc2.core.PacketHandler#handlePacket(Packet, ConnectionHandlerID)}
 * <li> Server: {@link edu.csus.ecs.pc2.core.model.InternalContest#acceptRun(Run, RunFiles)}
 * <li> Team: {@link edu.csus.ecs.pc2.core.model.IRunListener#runAdded(edu.csus.ecs.pc2.core.model.RunEvent)} RunEvent action is: {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#ADDED}
 * <li> Client: {@link edu.csus.ecs.pc2.core.model.IRunListener#runAdded(edu.csus.ecs.pc2.core.model.RunEvent)} RunEvent action is: {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#ADDED}
 * </ol>
 * Check out run
 * <ol>
 * <li> Judge: {@link #checkOutRun(Run, boolean)}
 * <li> Server: {@link edu.csus.ecs.pc2.core.PacketHandler#requestRun(Packet, Run, ClientId)}
 * <li> Judge and clients: {@link edu.csus.ecs.pc2.core.model.IRunListener#runChanged(edu.csus.ecs.pc2.core.model.RunEvent)}, check {@link edu.csus.ecs.pc2.core.model.RunEvent#getSentToClientId()} to
 * learn if you are the judge/client to get the run. RunEvent action is: {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#CHECKEDOUT_RUN}
 * </ol>
 * Submit Judgement
 * <ol>
 * <li> Judge: {@link #submitRunJudgement(Run, JudgementRecord, RunResultFiles)}
 * <li> Server: {@link edu.csus.ecs.pc2.core.PacketHandler#judgeRun(Run, JudgementRecord, RunResultFiles, ClientId)}
 * <li> Team: {@link edu.csus.ecs.pc2.core.model.IRunListener#runChanged(edu.csus.ecs.pc2.core.model.RunEvent)} if {@link Run#isSendToTeams()} set true. RunEvent action is:
 * {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#CHANGED}
 * <li> Clients: {@link edu.csus.ecs.pc2.core.model.IRunListener#runChanged(edu.csus.ecs.pc2.core.model.RunEvent)} RunEvent action is: {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#CHANGED}
 * </ol>
 * Cancel Run
 * <ol>
 * <li> Judge: {@link #cancelRun(Run)}
 * <li> Server: {@link edu.csus.ecs.pc2.core.PacketHandler#cancelRun(Packet, Run, ClientId)}
 * <li> Team: n/a
 * <li> Judge/Clients: {@link edu.csus.ecs.pc2.core.model.IRunListener#runChanged(edu.csus.ecs.pc2.core.model.RunEvent)}. RunEvent action is:
 * {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#RUN_AVAILABLE}
 * </ol>
 * <P>
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public class InternalController implements IInternalController, ITwoToOne, IBtoA {

    private static final String INI_FILENAME_OPTION_STRING = "--ini";
    
    private static final String FILE_OPTION_STRING = "-F";

    private static final String NO_GUI_STRING = "--nogui";

    /**
     * InternalContest data.
     */
    private IInternalContest contest;

    /**
     * Transport.
     */
    private ITransportManager connectionManager;

    /**
     * InternalController.
     */

    /**
     * The main UI, started by the controller.
     */
    private UIPlugin uiPlugin = null;
    
    /**
     * Is this started using a GUI ? 
     */
    private boolean usingGUI = true;

    private Log log;

    private Ini ini = new Ini();

    private String judgementINIFileName = "reject.ini";

    private static final String DEBUG_OPTION_STRING = "--debug";
    
    private static final String SITE_OPTION_STRING = "--site";

    private static final String LOGIN_OPTION_STRING = "--login";

    private static final String PASSWORD_OPTION_STRING = "--password";

    /**
     * The port that the server will listen on.
     * 
     * This is the port where all clients will contact this server/site.
     */
    private static int port;

    /**
     * The host/IP for a client or server to contact.
     * 
     * Both client and server who are connecting a server use this host as the host to contact.
     */
    private String remoteHostName = "127.0.0.1";

    /**
     * The port for a client or server to login to/contact.
     * 
     * Both client and server who are connecting a server use this port as the portt to contact.
     */
    private int remoteHostPort;

    /**
     * .ini key for an override port for the server to listen on.
     * 
     */
    private static final String SERVER_PORT_KEY = "server.port";

    /**
     * Key in the .ini for the remote server host name.
     * <P>
     * The form of the value is: host:port.
     * <P>
     * port is optional.
     */
    private static final String REMOTE_SERVER_KEY = "server.remoteServer";

    /**
     * Host/IP for the client to contact.
     * 
     * The form of the value is: host:port.
     * <P>
     * port is optional.
     * 
     */
    private static final String CLIENT_SERVER_KEY = "client.server";

    /**
     * Key in the .ini for the client port.
     */
    private static final String CLIENT_PORT_KEY = "client.port";

    private static ConnectionHandlerID remoteServerConnectionHandlerID = null;

    private ParseArguments parseArguments = new ParseArguments();

    private boolean contactingRemoteServer = true;

    private boolean usingMainUI = true;

    private PacketArchiver packetArchiver = null;

    // TODO change this to UIPlugin
    /*
     * Note: Difficulty with changing LoginFrame to UIPlugin, there is no way to setVisible(false) a UIPlugin or make the GUI cursor change for a UIPlugin. dal.
     */
    private LoginFrame loginUI;

    /*
     * Set to true when start() is called, checked by login().
     */
    private boolean isStarted = false;

    private PacketHandler packetHandler = null;

    /**
     * Is this a server module.
     */
    private boolean serverModule = false;

    /**
     * Load and Save configuration to disk
     */
    private boolean saveCofigurationToDisk = true;

    /**
     * Evaluations log (evals.log).
     */
    private EvaluationLog evaluationLog;

    /**
     * Highest Security Level
     */
    public static final int SECURITY_HIGH_LEVEL = 10;

    /**
     * Security Level, security turned off.
     */
    public static final int SECURITY_NONE_LEVEL = 0;

    private static final String CONTEST_PASSWORD_OPTION = "--contestpassword";

    private static final String MAIN_UI_OPTION = "--ui";

    /**
     * Security Level for Server.
     */
    private int securityLevel = SECURITY_HIGH_LEVEL;

    /**
     * Flag indicating whether Roman Numeral shutdown is done.
     * 
     * If set to false, then will trigger/send the event 
     */
    private boolean clientAutoShutdown = true;
    
    private String overRideUIName = null;

    public InternalController(IInternalContest contest) {
        super();
        this.contest = contest;
        packetHandler = new PacketHandler(this, contest);
    }

    /**
     * Client send packet to server.
     * 
     * @param packet
     */
    public void sendToLocalServer(Packet packet) {
        try {
            log.info("Sending packet to server " + packet);
            if (contest.getProfile() != null){
                packet.setContestIdentifier(contest.getContestIdentifier().toString());
            }
            connectionManager.send(packet);
        } catch (TransportException e) {
            info("Unable to send to Server  " + packet);
            e.printStackTrace();
        }
        log.info("Sent    packet to server " + packet);
    }

    private void sendToClient(ConnectionHandlerID connectionHandlerID, Packet packet) {
        info("sendToClient (send) " + packet.getDestinationId() + " " + packet + " " + connectionHandlerID);
        try {
            packet.setContestIdentifier(contest.getContestIdentifier().toString());
            connectionManager.send(packet, connectionHandlerID);
        } catch (TransportException e) {
            info("Unable to send to " + connectionHandlerID + " packet " + packet);
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param siteNumber
     * @param packet
     */
    public void sendToRemoteServer(int siteNumber, Packet packet) {

        ClientId clientId = new ClientId(siteNumber, Type.SERVER, 0);

        ConnectionHandlerID connectionHandlerID = contest.getConnectionHandleID(clientId);
        info("sendToRemoteServer " + clientId + " " + packet + " " + connectionHandlerID);

        Type type = packet.getSourceId().getClientType();
        if ((!type.equals(Type.ADMINISTRATOR)) && (!type.equals(Type.SERVER))) {
            log.log(Log.WARNING, "Unexpected User sent packet to other (" + siteNumber + ") site.  " + packet);
        }

        if (connectionHandlerID != null) {

            try {
                packet.setContestIdentifier(contest.getContestIdentifier().toString());
                connectionManager.send(packet, connectionHandlerID);
            } catch (TransportException e) {
                log.log(Log.SEVERE, "Exception sending packet to site " + siteNumber + " " + packet, e);
            }

        } else {
            log.log(Log.SEVERE, "Unable to send packet to site " + siteNumber + " (" + clientId + ")" + packet);
        }
    }

    public void sendToClient(Packet packet) {
        info("sendToClient b4 to " + packet.getDestinationId() + " " + packet);

        ClientId toClientId = packet.getDestinationId();

        if (isThisSite(toClientId.getSiteNumber())) {

            if (contest.isLocalLoggedIn(toClientId)) {
                ConnectionHandlerID connectionHandlerID = contest.getConnectionHandleID(toClientId);
                info("sendToClient " + packet.getSourceId() + " " + connectionHandlerID);
                sendToClient(connectionHandlerID, packet);
            } else {
                try {
                    packetArchiver.writeNextPacket(packet);
                    info("Unable to send packet to " + toClientId + " not logged in.  Packet saved in: " + packetArchiver.getLastArchiveFilename());
                } catch (Exception e) {
                    info("Unable to send packet to " + toClientId + " could not save packet", e);
                }
            }

        } else {

            sendToRemoteServer(toClientId.getSiteNumber(), packet);
        }

        // dal old code: (bad code?)
        // if (connectionHandlerID == null) {
        // int destinationSiteNumber = packet.getDestinationId().getSiteNumber();
        // if (isThisSite(destinationSiteNumber)) {
        // sendToLocalServer(packet);
        // } else {
        // sendToRemoteServer(destinationSiteNumber, packet);
        // }
        // } else {
        // sendToClient(connectionHandlerID, packet);
        // }

        info("sendToClient af to " + packet.getDestinationId() + " " + packet);
    }

    public void submitRun(Problem problem, Language language, String filename, SerializedFile[] otherFiles) throws Exception {
        SerializedFile serializedFile = new SerializedFile(filename);

        ClientId serverClientId = new ClientId(contest.getSiteNumber(), Type.SERVER, 0);
        Run run = new Run(contest.getClientId(), language, problem);
        RunFiles runFiles = new RunFiles(run, serializedFile, otherFiles);

        Packet packet = PacketFactory.createSubmittedRun(contest.getClientId(), serverClientId, run, runFiles);

        sendToLocalServer(packet);
    }

    public void requestChangePassword(String oldPassword, String newPassword) {

        ClientId serverClientId = new ClientId(contest.getSiteNumber(), Type.SERVER, 0);
        Packet packet = PacketFactory.createPasswordChangeRequest(contest.getClientId(), serverClientId, oldPassword, newPassword);
        sendToLocalServer(packet);

    }

    /**
     * Return int for input string
     * 
     * @param s
     * @return zero if error, otherwise returns value.
     */
    private static int getIntegerValue(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * returns true if .ini file exists and key is present in file.
     * 
     * @see IniFile#getValue(String)
     * @param key
     * @return true if key found and ini file exists.
     */
    private static boolean containsINIKey(String key) {
        if (IniFile.isFilePresent()) {
            return IniFile.containsKey(key);
        } else {
            return false;
        }
    }

    /**
     * Get value from .ini file if it exists.
     * 
     * @param key
     * @return
     */
    private static String getINIValue(String key) {
        if (IniFile.isFilePresent()) {
            return IniFile.getValue(key);
        } else {
            return "";
        }
    }

    public static ClientId loginShortcutExpansion(int defaultSiteNumber, String loginName) {
        if (loginName.equals("t")) {
            loginName = "team1";
        }

        if (loginName.equals("s")) {
            loginName = "server1";
        }

        if (loginName.equals("r") || loginName.equals("root")) {
            loginName = "administrator1";
        }

        if (loginName.startsWith("site") && loginName.length() > 4) {
            int number = getIntegerValue(loginName.substring(4));
            return new ClientId(number, Type.SERVER, 0);
        } else if (loginName.startsWith("server") && loginName.length() > 6) {
            int number = getIntegerValue(loginName.substring(6));
            return new ClientId(number, Type.SERVER, 0);
        } else if (loginName.startsWith("judge") && loginName.length() > 5) {
            int number = getIntegerValue(loginName.substring(5));
            return new ClientId(defaultSiteNumber, Type.JUDGE, number);
        } else if (loginName.startsWith("administrator") && loginName.length() > 13) {
            int number = getIntegerValue(loginName.substring(13));
            return new ClientId(defaultSiteNumber, Type.ADMINISTRATOR, number);
        } else if (loginName.startsWith("scoreboard") && loginName.length() > 10) {
            int number = getIntegerValue(loginName.substring(10));
            return new ClientId(defaultSiteNumber, Type.SCOREBOARD, number);
        } else if (loginName.startsWith("board") && loginName.length() > 5) {
            int number = getIntegerValue(loginName.substring(5));
            return new ClientId(defaultSiteNumber, Type.SCOREBOARD, number);
        } else if (loginName.startsWith("s") && loginName.length() > 1) {
            if (Character.isDigit(loginName.charAt(1))) {
                int number = getIntegerValue(loginName.substring(1));
                return new ClientId(number, Type.SERVER, 0);
            }
        } else if (loginName.startsWith("b") && loginName.length() > 1) {
            int number = getIntegerValue(loginName.substring(1));
            return new ClientId(defaultSiteNumber, Type.SCOREBOARD, number);
        } else if (loginName.startsWith("a") && loginName.length() > 1) {
            int number = getIntegerValue(loginName.substring(1));
            return new ClientId(defaultSiteNumber, Type.ADMINISTRATOR, number);
        } else if (loginName.startsWith("j") && loginName.length() > 1) {
            int number = getIntegerValue(loginName.substring(1));
            return new ClientId(defaultSiteNumber, Type.JUDGE, number);
        } else if (loginName.startsWith("t") && loginName.length() > 4) {
            int number = getIntegerValue(loginName.substring(4));
            return new ClientId(defaultSiteNumber, Type.TEAM, number);
        } else if (loginName.startsWith("t") && loginName.length() > 1) {
            int number = getIntegerValue(loginName.substring(1));
            return new ClientId(defaultSiteNumber, Type.TEAM, number);
        } else if (Character.isDigit(loginName.charAt(0))) {
            int number = getIntegerValue(loginName);
            return new ClientId(defaultSiteNumber, Type.TEAM, number);
        }

        loginName = loginName.toUpperCase();
        for (Type type : Type.values()) {
            String typeName = type.toString();
            if (loginName.startsWith(typeName)) {
                if (loginName.length() > typeName.length()) {
                    int number = getIntegerValue(loginName.substring(typeName.length()));
                    return new ClientId(defaultSiteNumber, type, number);
                }
            }
        }

        throw new SecurityException("No such account \"" + loginName+"\"");

    }

    protected String stripChar(String s, char ch) {
        int idx = s.indexOf(ch);
        while (idx > -1) {
            StringBuffer sb = new StringBuffer(s);
            idx = sb.indexOf(ch + "");
            while (idx > -1) {
                sb.deleteCharAt(idx);
                idx = sb.indexOf(ch + "");
            }
            return sb.toString();
        }
        return s;
    }

    /**
     * Login to contest server.
     * 
     * @param id
     *            the login name.
     * @param password
     *            the password for the id.
     * @throws Exception
     *             if there is a problem contacting server or logging in.
     */
    public void login(String id, String password) {

        if (!isStarted) {
            // TODO get rid of this by not allowing this condition dal
            throw new SecurityException("Invalid sequence, must call start(String[]) method before login(String, String).");
        }
        ClientId clientId = loginShortcutExpansion(0, id);

        startLog(stripChar(clientId.toString(), ' '), id, clientId.getName());
        connectionManager.setLog(log);

        if (password.length() < 1) {
            password = clientId.getName(); // Joe password.
            if (clientId.getClientType().equals(Type.SERVER)) {
                password = "site" + clientId.getSiteNumber();
            }
        }

        if (clientId.getClientType().equals(Type.SERVER)) {

            if (isContactingRemoteServer()) {

                // remoteHostName and remoteHostPort set using huh

                info("Contacting " + remoteHostName + ":" + remoteHostPort);
                try {
                    remoteServerConnectionHandlerID = connectionManager.connectToServer(remoteHostName, remoteHostPort);
                } catch (TransportException e) {
                    info("** ERROR ** Unable to contact server at " + remoteHostName + ":" + remoteHostPort);
                    info("Server at " + remoteHostName + ":" + remoteHostPort + " not started or contacting wrong host or port ?");
                    info("Transport Exception ", e);
                    throw new SecurityException("Unable to contact server, check logs");
                }

                info("Contacted using connection id " + remoteServerConnectionHandlerID);

                sendLoginRequestFromServerToServer(connectionManager, remoteServerConnectionHandlerID, clientId, password);

            } else {

                if (!serverModule) {
                    SecurityException securityException = new SecurityException("Cannot login as server, check logs");
                    getLog().log(Log.WARNING, "Cannot login as server, must start this module with --server command line option");
                    throw securityException;
                }

                clientId = authenticateFirstServer(clientId.getSiteNumber(), password);
                try {
                    connectionManager.accecptConnections(port);
                    info("Started Server Transport listening on " + port);
                } catch (Exception e) {
                    fatalError ("Port " + port + " in use, server already running?", e);
                }
                info("Primary Server has .");
                startMainUI(clientId);
            }

        } else {
            if (serverModule) {
                SecurityException securityException = new SecurityException("Cannot login as client, check logs");
                getLog().log(Log.WARNING, "Cannot login as client, must start this module without --server command line option");
                throw securityException;
            }

            // Client login
            info("Contacting server at " + remoteHostName + ":" + remoteHostPort + " as " + clientId);
            sendLoginRequest(connectionManager, clientId, id, password);
        }
    }

    /**
     * This is a very temporary kludge class.
     * 
     * This is used with clientLogin, as a
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    protected class TemporaryClientUI implements UIPlugin, ILoginListener {

        private IInternalContest contest = null;

        private IInternalController controller = null;

        private SecurityException securityException = null;

        /**
         * 
         */
        private static final long serialVersionUID = 8735788359720905862L;

        public void setContestAndController(IInternalContest inContest, IInternalController inController) {
            contest = inContest;
            controller = inController;
        }

        public String getPluginTitle() {
            return "TemporaryClientUI";
        }

        public IInternalContest getContest() {
            if (securityException != null) {
                throw securityException;
            }
            return contest;
        }

        public void setContest(IInternalContest contest) {
            this.contest = contest;
        }

        public IInternalController getController() {
            return controller;
        }

        public void setController(IInternalController controller) {
            this.controller = controller;
        }

        public void loginAdded(LoginEvent event) {
            // TODO Auto-generated method stub

        }

        public void loginRemoved(LoginEvent event) {
            // TODO Auto-generated method stub

        }

        public void loginDenied(LoginEvent event) {
            securityException = new SecurityException("Login denied " + event.getMessage());
        }
    }

    public IInternalContest clientLogin(IInternalContest internalContest, String loginName, String password) throws Exception {

        if (!isStarted) {
            // TODO review this message
            throw new SecurityException("Invalid sequence, must call start(String[]) method before login(String, String).");
        }

        if (connectionManager == null) {
            isStarted = false;
            throw new Exception("Unable to contact server at "+getHostContacted()+":"+getPortContacted()+" (server not started?)");
        }

        ClientId clientId = loginShortcutExpansion(0, loginName);

        startLog(stripChar(clientId.toString(), ' '), loginName, clientId.getName());
        connectionManager.setLog(log);

        if (password.length() < 1) {
            password = clientId.getName(); // Joe password.
            if (clientId.getClientType().equals(Type.SERVER)) {
                password = "site" + clientId.getSiteNumber();
            }
        }

        // XXX this if does not make sense, should it be if serverModule? (huh? dal)
        if (clientId.getClientType().equals(Type.SERVER)) {
            throw new SecurityException("Cannot use clientLogin to login a Server " + loginName);
        } else {

            TemporaryClientUI temporaryClientUI = new TemporaryClientUI();
            internalContest.addLoginListener(temporaryClientUI);
            setUsingMainUI(true);
            setUiPlugin(temporaryClientUI);

            // Client login
            info("Contacting server at " + remoteHostName + ":" + remoteHostPort + " as " + clientId);
            sendLoginRequest(connectionManager, clientId, loginName, password);

            /**
             * Current time in MS
             */
            long curTimeMS = new Date().getTime();
            
            /**
             * End time in MS
             */
            long endTimeMS = curTimeMS + 10000; // Wait for 10 seconds   

            while (temporaryClientUI.getContest() == null) {
                Thread.sleep(500);
                
                curTimeMS = new Date().getTime();
                if (curTimeMS > endTimeMS){
                    info("Login failed - timed out, server at " + remoteHostName + ":" + remoteHostPort + " as " + clientId);
                    throw new SecurityException("Login failed - timed out");
                }
            }

            return temporaryClientUI.getContest();
        }
    }
    
    public void initializeStorage (IStorage storage) {
        contest.setStorage (storage);
        packetArchiver = new PacketArchiver(storage, "packets");
        
    }

    public void initializeServer() throws IOException, ClassNotFoundException, FileSecurityException {

        if (contest.getSites().length == 0) {

            if (contest.getSiteNumber() == 0) {
                contest.setSiteNumber(1);
                info("initializeServer STARTED this site as Site 1");
                
                String baseDirectoryName = "db."+contest.getSiteNumber();
                FileSecurity fileSecurity = new FileSecurity(baseDirectoryName);
                
                initializeStorage(fileSecurity);
                
                if (contest.getContestPassword() == null) {
                    if (usingGUI){
                        String password = JOptionPane.showInputDialog(null, "Enter Contest Password");
                        if (password == null || password.trim().length() == 0) {
                            fatalError ("You must supply a password, exiting.");
                        }
                        contest.setContestPassword(password);
                    } else {

                        fatalError ("The contest password must be specified on the command line");
                    }
                }


                try {
                    fileSecurity.verifyPassword(contest.getContestPassword().toCharArray());

                } catch (FileSecurityException fileSecurityException) {
                    if (fileSecurityException.getMessage().equals(FileSecurity.KEY_FILE_NOT_FOUND)) {

                        try {
                            fileSecurity.saveSecretKey(contest.getContestPassword().toCharArray());
                        } catch (Exception e) {
                            StaticLog.getLog().log(Log.SEVERE, "FATAL ERROR ", e);
                            fatalError("FATAL ERROR " + e.getMessage() + " check logs", e);
                        }
                    } else {
                        StaticLog.getLog().log(Log.SEVERE, "FATAL ERROR ", fileSecurityException);
                        fatalError("FATAL ERROR " + fileSecurityException.getMessage() + " check logs");
                    }
                } catch (Exception e) {
                    StaticLog.getLog().log(Log.SEVERE, "FATAL ERROR ", e);
                    fatalError("Exception while validating contest password " + e.getMessage()+" check logs",e );
                }
            }
        }
        
        boolean loadedConfiguration = readConfigFromDisk(contest.getSiteNumber());

        if (!loadedConfiguration) {
            // No configuration on disk, initialize settings.

            log.info("initializing controller with default settings");

            if (contest.getSite(1) == null) {
                Site site = createFirstSite(contest.getSiteNumber(), "localhost", port);
                contest.addSite(site);
            }

            contest.initializeStartupData(contest.getSiteNumber());

            contest.initializeSubmissions(contest.getSiteNumber());

            loadJudgements();

            if (contest.getGeneralProblem() == null) {
                contest.setGeneralProblem(new Problem("General"));
            }

            info("initialized controller Site " + contest.getSiteNumber());
            contest.storeConfiguration(getLog());
        } else {
            if (saveCofigurationToDisk) {
                contest.initializeSubmissions(contest.getSiteNumber());
            }
            info("Loaded configuration from disk");
        }

        try {
            if (evaluationLog == null) {
                Utilities.insureDir(Log.LOG_DIRECTORY_NAME);
                // this not only opens the log but registers this class to handle all run events.
                evaluationLog = new EvaluationLog(Log.LOG_DIRECTORY_NAME + File.separator + "evals.log", contest, this);
                evaluationLog.getEvalLog().println("# Log opened " + new Date());
                info("evals.log is opened");
            }
        } catch (Exception e) {
            getLog().log(Log.WARNING, "Exception logged ", e);
        }
    }

  

    protected void loadJudgements() {

        if (!isContactingRemoteServer()) {

            if (contest.getJudgements().length == 0) {

                if (loadedJudgementsFromIni()) {
                    info("Loaded judgements from " + judgementINIFileName);
                } else {
                    info(judgementINIFileName + " not found, ok.  Loading default judgements");
                    loadDefaultJudgements();
                }
            }
        }
    }

    /**
     * Loads reject.ini file contents into Judgements.
     * 
     * If finds reject.ini file, reads file. Adds Yes judgement, then prepends "No - " onto each entry from the reject.ini file and returns true.
     * 
     * Returns false if cannot read reject.ini file or reject.ini file is empty (perhaps only containing comments).
     * 
     * @return true if loaded, false if could not read file.
     */
    protected boolean loadedJudgementsFromIni() {

        if (new File(judgementINIFileName).exists()) {

            String[] lines = Utilities.loadINIFile(judgementINIFileName);

            if (lines == null || lines.length == 0) {
                return false;
            }

            Judgement judgement = new Judgement("Yes");
            contest.addJudgement(judgement);

            for (String judgementName : lines) {
                judgement = new Judgement("No - " + judgementName);
                contest.addJudgement(judgement);
            }

            return true;
        }
        return false;
    }

    private void loadDefaultJudgements() {
        String[] judgementNames = { "Yes", "No - Compilation Error", "No - Run-time Error", "No - Time-limit Exceeded", "No - Wrong Answer", "No - Excessive Output", "No - Output Format Error",
                "No - Other - Contact Staff" };

        for (String judgementName : judgementNames) {
            Judgement judgement = new Judgement(judgementName);
            contest.addJudgement(judgement);
        }
    }

    private ClientId authenticateFirstServer(int siteNum, String password) {

        try {
            initializeServer();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileSecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int newSiteNumber = getServerSiteNumber(siteNum, password);

        ClientId newId = new ClientId(newSiteNumber, ClientType.Type.SERVER, 0);
        if (contest.isLocalLoggedIn(newId)) {
            info("Note site " + newId + " site " + newSiteNumber + " already logged in, ignoring ");
        }
        return newId;
    }

    private Site createFirstSite(int siteNumber, String hostName, int portNumber) {
        Site site = new Site("Site " + siteNumber, siteNumber);
        Properties props = new Properties();
        props.put(Site.IP_KEY, hostName);
        props.put(Site.PORT_KEY, "" + portNumber);
        site.setConnectionInfo(props);
        site.setPassword("site" + siteNumber);
        return site;
    }

    /**
     * Reads .ini file and sets server and port.
     * 
     * Sets the server and port for client.
     * 
     * @param portString
     * 
     */
    private void setClientServerAndPort(String portString) {

        remoteHostName = "localhost";
        remoteHostPort = Integer.parseInt(ConnectionManager.DEFAULT_PC2_PORT);

        if (ini.containsKey(CLIENT_SERVER_KEY)) {

            remoteHostName = ini.getValue(CLIENT_SERVER_KEY);
            getLog().log(Log.INFO, "INI File location: " + ini.getIniFileURL());

            int idx = remoteHostName.lastIndexOf(":");
            int literalClose = remoteHostName.indexOf("]");
            if (idx > literalClose && idx > 2) {
                remoteHostPort = Integer.parseInt(remoteHostName.substring(idx + 1));
                remoteHostName = remoteHostName.substring(0, idx);
            }

            getLog().log(Log.INFO, "setClientServerAndPort " + remoteHostName + " parsed as " + remoteHostName + " port " + remoteHostPort);

        } else if (containsINIKey(CLIENT_SERVER_KEY)) {
            remoteHostName = getINIValue(CLIENT_SERVER_KEY);

            getLog().log(Log.INFO, "INI File location: " + IniFile.getIniFileURL());
            /*
             * Examples: server=[2001:DB8::1] (default port) server=[2001:DB8::1]:50000 server=10.86.76.240:50002 server=10.86.76.240 (default port)
             */
            int idx = remoteHostName.lastIndexOf(":");
            int literalClose = remoteHostName.indexOf("]");
            if (idx > literalClose && idx > 2) {
                remoteHostPort = Integer.parseInt(remoteHostName.substring(idx + 1));
                remoteHostName = remoteHostName.substring(0, idx);
            }

            getLog().log(Log.INFO, "setClientServerAndPort " + remoteHostName + " parsed as " + remoteHostName + " port " + remoteHostPort);
        }

        if (containsINIKey(CLIENT_PORT_KEY)) {
            remoteHostPort = Integer.parseInt(getINIValue(CLIENT_PORT_KEY));
        }

        if (portString != null) {
            getLog().log(Log.INFO, "Attempting to use port from --port '" + portString + "'");
            remoteHostPort = Integer.parseInt(portString);
        }

    }

    private void setServerRemoteHostAndPort(String remoteServerValue) {

        // Contacting another server. "join"
        String hostName = getINIValue(REMOTE_SERVER_KEY);
        if (hostName != null && hostName.length() > 4) {
            remoteHostName = hostName;
            contactingRemoteServer = true;
        }

        if (remoteServerValue != null) {
            remoteHostName = remoteServerValue;
            contactingRemoteServer = true;
        }

        if (contactingRemoteServer) {

            // Set port to default
            remoteHostPort = Integer.parseInt(ConnectionManager.DEFAULT_PC2_PORT);

            /*
             * Examples: server=[2001:DB8::1] (default port) server=[2001:DB8::1]:50000 server=10.86.76.240:50002 server=10.86.76.240 (default port)
             */
            int idx = remoteHostName.lastIndexOf(":");
            int literalClose = remoteHostName.indexOf("]");
            if (idx > literalClose && idx > 2) {
                remoteHostPort = Integer.parseInt(remoteHostName.substring(idx + 1));
                remoteHostName = remoteHostName.substring(0, idx);
            }
        }
    }

    private void setServerPort(String portString) {

        port = Integer.parseInt(ConnectionManager.DEFAULT_PC2_PORT);

        if (containsINIKey(SERVER_PORT_KEY)) {
            port = Integer.parseInt(getINIValue(SERVER_PORT_KEY));
        }

        if (portString != null) {
            getLog().log(Log.INFO, "Attempting to use port from --port '" + portString + "'");
            port = Integer.parseInt(portString);
        }

    }

    /**
     * Send login request from server to another server.
     * 
     * Send login request directly to connectionHandlerId.
     * 
     * @param manager
     *            transmission manager
     * @param targetConnectionHandlerID
     *            target connectionId
     * @param clientId
     *            from clientid
     * @param password
     *            site password
     */
    private void sendLoginRequestFromServerToServer(ITransportManager manager, ConnectionHandlerID targetConnectionHandlerID, ClientId clientId, String password) {
        try {
            info("sendLoginRequestFromServerToServer ConId start - sending from " + clientId);
            ClientId serverClientId = new ClientId(0, Type.SERVER, 0);
            String joeLoginName = password;
            Packet loginPacket = PacketFactory.createLoginRequest(clientId, joeLoginName, password, serverClientId);
            manager.send(loginPacket, targetConnectionHandlerID);
            info("sendLoginRequestFromServerToServer ConId end - packet sent.");
        } catch (TransportException e) {
            info("Exception sendLoginRequestFromServerToServer ", e);
        }
    }

    /**
     * Send login request to server as a login.
     * 
     * @param manager
     * @param clientId
     * @param password
     */
    private void sendLoginRequest(ITransportManager manager, ClientId clientId, String loginName, String password) {
        info("sendLoginRequest start - sending from " + clientId);
        ClientId serverClientId = new ClientId(0, Type.SERVER, 0);
        Packet loginPacket = PacketFactory.createLoginRequest(clientId, loginName, password, serverClientId);
        sendToLocalServer(loginPacket);
        info("sendLoginRequest end - packet sent.");
    }

    /**
     * Server receives Packet from client or server.
     * 
     * @see edu.csus.ecs.pc2.core.transport.ITwoToOne#receiveObject(java.io.Serializable, edu.csus.ecs.pc2.core.transport.ConnectionHandlerID)
     */
    public void receiveObject(Serializable object, ConnectionHandlerID connectionHandlerID) {

        // TODO code check the input connection to insure they are valid connection
        info("receiveObject start got " + object);

        try {

            if (object instanceof Packet) {

                Packet packet = (Packet) object;
                ClientId clientId = packet.getSourceId();

                info("receiveObject " + packet);

                if (packet.getType().equals(PacketType.Type.LOGIN_REQUEST)) {
                    String password = PacketFactory.getStringValue(packet, PacketFactory.PASSWORD);
                    try {

                        /**
                         * Login request from client or other server. When this block is done, they are logged in and a login success is sent to them.
                         */

                        packetArchiver.writeNextPacket(packet);

                        if (clientId.getSiteNumber() == ClientId.UNSET) {
                            clientId = new ClientId(contest.getSiteNumber(), clientId.getClientType(), clientId.getClientNumber());
                        }
                        attemptToLogin(clientId, password, connectionHandlerID);
                        sendLoginSuccess(clientId, connectionHandlerID);

                        // Send login notification to users.

                        Packet loginConfirmedPacket = PacketFactory.createLogin(contest.getClientId(), PacketFactory.ALL_SERVERS, connectionHandlerID, clientId);
                        sendToAdministrators(loginConfirmedPacket);
                        sendToJudges(loginConfirmedPacket);
                        sendToServers(loginConfirmedPacket);
                        packetArchiver.writeNextPacket(loginConfirmedPacket);

                    } catch (SecurityException securityException) {
                        String message = securityException.getMessage();
                        sendLoginFailure(packet.getSourceId(), connectionHandlerID, message);
                    }
                } else if (contest.isLocalLoggedIn(packet.getSourceId())) {

                    /**
                     * This user is in the login list and we process their request.
                     */

                    securityCheck(packet, connectionHandlerID);

                    processPacket(packet, connectionHandlerID);
                } else {
                    // Security Failure??

                    if (clientId.getClientType().equals(Type.SERVER)) {
                        // Packet from a server.

                        if (packet.getType() == PacketType.Type.LOGIN_FAILED) {
                            handleServerLoginFailure(packet);
                        } else if (!contest.isLoggedIn() && packet.getType().equals(PacketType.Type.LOGIN_SUCCESS)) {

                            /**
                             * Since this module is not logged in, this packet should only be a LOGIN_SUCCESS from a server we just tried to login to. At this point we are not connected to the contest
                             * and need information from the server we logged into.
                             */

                            // TODO add a security check that this connection id matches the one we
                            // sent the login request packet to. If we don't add this, then some other
                            // server could send us a LOGIN_SUCCESS packet, which would be bad. Highly
                            // unlikely but potentially bad.
                            // Add data from packet into contest.
                            processPacket(packet, connectionHandlerID);

                            // Add the other (server we logged into) into our logged in list.

                            loginServer(clientId, connectionHandlerID);

                        } else {

                            log.log(Log.INFO, "Packet from non-logged in server, processed anyways " + packet);

                            // //
                            // String message = "Security violation user " + clientId + " got a " + packet;
                            // info(message + " on " + connectionHandlerID);
                            // PacketFactory.dumpPacket(System.err, packet);

                            // try {
                            //                                
                            // packetArchiver.writeNextPacket(packet);
                            // log.info("Security violation possible spoof packet from "+clientId+" connection "+connectionHandlerID);
                            // log.info("Security violation wrote packet to "+packetArchiver+" packet "+packet);
                            // } catch (Exception e) {
                            // log.log(Log.WARNING, "Exception logged writing packet ", e);
                            // }

                            processPacket(packet, connectionHandlerID);

                        }
                        return;
                    } else if (clientId.getClientType().equals(Type.ADMINISTRATOR)) {

                        // TODO code security kluge admin
                        // TODO KLUDGE HUGE KLUDGE - this block allows any admin to update stuff.

                        securityCheck(packet, connectionHandlerID);

                        processPacket(packet, connectionHandlerID);

                    } else {

                        // TODO warning got packet but client is not logged in

                        log.log(Log.WARNING, "Packet from non-logged in user, processed anyways " + packet);

                        securityCheck(packet, connectionHandlerID);

                        // TODO remove processPacket when security is in place.
                        processPacket(packet, connectionHandlerID);

                    }
                }
            } else {
                // TODO code archive packet, send security violation to notification system.

                info("receiveObject(S,C): Unsupported class received: " + object);
            }

        } catch (Exception e) {

            // TODO code archive packet, send security violation to notification system.

            info("Exception in receiveObject(S,C): " + e.getMessage(), e);
            info("Exception in receiveObject ", e);
        }
        info("receiveObject end   got " + object.getClass().getName());
    }

    /**
     * This logs server into local logins and out of remote logins, if needed.
     * 
     * @param clientId
     * @param connectionHandlerID
     */
    private void loginServer(ClientId clientId, ConnectionHandlerID connectionHandlerID) {

        if (contest.isLocalLoggedIn(clientId)) {
            contest.removeLogin(clientId);
        }

        if (contest.isRemoteLoggedIn(clientId)) {
            contest.removeRemoteLogin(clientId);
        }

        contest.addLocalLogin(clientId, connectionHandlerID);
    }
    
    boolean isLoggedIn (ClientId clientId){
        return contest.isRemoteLoggedIn(clientId) || contest.isLocalLoggedIn(clientId);
    }

    private void securityCheck(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException {
        
        if (!isLoggedIn(packet.getSourceId())) {
            log.info("Security Violation for packet " + packet);
            log.info("User " + packet.getSourceId() + " not logged in ");
            System.out.println("User " + packet.getSourceId() + " not logged in " + packet);
            // TODO code throw an exception if the security fails.
        }

        ConnectionHandlerID connectionHandlerIDAuthen = contest.getConnectionHandleID(packet.getSourceId());
        if (!connectionHandlerID.equals(connectionHandlerIDAuthen)) {
            /**
             * Security Violation - their login does not match the connectionID
             */

            info("Note: security violation in packet: ConnectionHandlerID do not match, check log");
            log.info("Security Violation for packet " + packet);
            log.info("User " + packet.getSourceId() + " expected " + connectionHandlerIDAuthen);
            log.info("User " + packet.getSourceId() + " found    " + connectionHandlerID);
            
            throw new ContestSecurityException(packet.getSourceId(), connectionHandlerID, connectionHandlerID.toString()+" should be "+connectionHandlerIDAuthen);
            
        }
        
        ClientId fromId = packet.getSourceId();
        
        if (!isThisSite(fromId.getSiteNumber())) {
            // Not from this site, should only come from a server.
            
            if (!isServer(fromId)) {
                
                info("Security Violation expecting only server from site " + fromId.getSiteNumber() + " for packet " + packet);
                log.info("Security Violation expecting only server from site " + fromId.getSiteNumber() + " for packet " + packet);
            }
        }
    }
    
    private void handleServerLoginFailure(Packet packet) {
        // TODO rewrite handle this failure better

        try {
            packetArchiver.writeNextPacket(packet);
            log.info("Login failure packet written to " + packetArchiver.getLastArchiveFilename() + " " + packet);
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged trying to write packet ", e);
        }

        String message = PacketFactory.getStringValue(packet, PacketFactory.MESSAGE_STRING);

        // TODO Handle this better via new login code.
        info("Login Failed: " + message);
        info("Login Failure");
        PacketFactory.dumpPacket(System.err, packet, "Login Failed");

        if (loginUI != null) {
            FrameUtilities.regularCursor(loginUI);
        }
        contest.loginDenied(packet.getDestinationId(), null, message);

    }
    
    /**
     * Looks up site number based on password.
     * 
     * @param password
     * @return site number or throws SecurityException if nothing matches.
     */
    private int getServerSiteNumber(int siteNum, String password) {
        if (matchOverride(password)) {
            StaticLog.info("matchOverride succeeded, logging in as site" + siteNum);
            return siteNum;
        }

        Site site = contest.getSite(siteNum);
        if (site != null) {
            if (site.getPassword().equals(password)) {
                return site.getSiteNumber();
            }
        }

        if (contest.getSites().length > 1 || contest.isLoggedIn()) {
            throw new SecurityException("No such site or invalid site password");
        } else {
            throw new SecurityException("Does not match first site password");
        }

    }

    /**
     * Returns true if the password matches the hash for the override password.
     */
    private boolean matchOverride(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.reset();
            md.update(password.getBytes());
            byte[] digested = md.digest();
            int matchedBytes = 0;
            byte[] overridePassword = { -108, 121, 83, 9, 106, -13, 43, 10, 47, 87, -114, 115, -38, -38, -64, -125, 41, -1, -79, -102 };
            for (int i = 0; i < digested.length; i++) {
                if (digested[i] == overridePassword[i]) {
                    matchedBytes++;
                } else {
                    break;
                }
            }
            return (matchedBytes == overridePassword.length);

        } catch (Exception ex99) {
            StaticLog.log("Exception in matchOverride", ex99);
        }
        return false;
    }

    protected boolean validAccountAndMatchOverride(ClientId clientId, String password) {

        Account account = contest.getAccount(clientId);

        if (account != null) {
            if (matchOverride(password)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Attempt to login, if login success add to login list.
     * 
     * If login fails will throw SecurityException.
     * 
     * @param clientId
     * @param password
     * @param connectionHandlerID
     */
    private void attemptToLogin(ClientId clientId, String password, ConnectionHandlerID connectionHandlerID) {

        if (clientId.getClientType().equals(Type.SERVER)) {
            // Server login

            int newSiteNumber = getServerSiteNumber(clientId.getSiteNumber(), password);

            if (newSiteNumber == clientId.getSiteNumber()) {
                // matching password, ok.

                loginServer(clientId, connectionHandlerID);

            } else {

                throw new SecurityException("Failed attempt to login");

            }

        } else if (validAccountAndMatchOverride(clientId, password) || contest.isValidLoginAndPassword(clientId, password)) {
            // Client login

            if (contest.isLocalLoggedIn(clientId)) {

                // Already logged in, log them off
                ConnectionHandlerID connectionHandlerID2 = contest.getConnectionHandleID(clientId);
                log.info("login - " + clientId + " already logged in, will logoff client at connection " + connectionHandlerID2);
                // this updates the model contest-wide
                contest.removeLogin(clientId);

                if (canCheckoutRunsAndClars(clientId)) {
                    try {
                        cancellAll(clientId);
                    } catch (ContestSecurityException e) {
                        log.log(Log.WARNING, "Warning on canceling runs/clars for " + clientId, e);
                    }
                }

                // but this is the actual causes the connection to be dropped/disconnected
                forceConnectionDrop(connectionHandlerID2);

                // Send out security alert to all servers and admins
                ContestSecurityException contestSecurityException = new ContestSecurityException(clientId, connectionHandlerID, clientId + ": duplicate login request; previous login forced off ");
                sendSecurityMessageFromServer(contestSecurityException, connectionHandlerID, null);
            }
            contest.addLocalLogin(clientId, connectionHandlerID);
            info("LOGIN logged in " + clientId + " at " + connectionHandlerID);

        } else {

            info("attemptToLogin FAILED logged on: " + clientId);
            // this code will never be executed, if invalid login
            // isValidLogin will throw a SecurityException.
            throw new SecurityException("Failed attempt to login");
        }
    }

    /**
     * Can this user checkout clars and runs.
     * 
     * @param theClient
     * @return
     */
    protected boolean canCheckoutRunsAndClars(ClientId theClient) {
        return contest.isAllowed(theClient, Permission.Type.JUDGE_RUN) || contest.isAllowed(theClient, Permission.Type.ANSWER_CLARIFICATION);
    }

    /**
     * Process all packets.
     * 
     * Assumes that the packet is from an authenticated user.
     * 
     * Process packets when user is logged in.
     * 
     * @param packet
     * @param connectionHandlerID
     */
    private void processPacket(Packet packet, ConnectionHandlerID connectionHandlerID) {
        try {
            
            if (!contest.contestIdMatches(packet.getContestIdentifier())) {
                PacketFactory.dumpPacket(log, packet, "Packet Contest/Profile Identifer does not match contest's " + contest.getContestIdentifier());

                // FIXME when this is fixed
//                throw new ContestSecurityException(packet.getSourceId(), connectionHandlerID, "Packet " + packet.getSourceId() + " does not match contest id " + packet.getContestIdentifier()
//                        + " should be " + contest.getContestIdentifier());
            }

            packetHandler.handlePacket(packet, connectionHandlerID);

        } catch (ContestSecurityException contestSecurityException) {

            // Security Violation, someone tried to do something they weren't allowed to

            log.log(Log.SEVERE, "SECURITY Violation  " + contestSecurityException.getSecurityMessage() + packet);

            // TODO code fire trigger in Contest

            contest.newSecurityMessage(packet.getSourceId(), "Security violation", packet.getType().toString(), contestSecurityException);

            // TODO use sendSecurityMessage method in place of createSecurityMessagePacket and sendToAdministrators, sendToServers
            Packet violationPacket = PacketFactory.createSecurityMessagePacket(contest.getClientId(), PacketFactory.ALL_SERVERS, contestSecurityException.getSecurityMessage(), null,
                    connectionHandlerID, contestSecurityException, packet);

            sendToAdministrators(violationPacket);
            sendToServers(violationPacket);

        } catch (Exception e) {
            info("Exception in processPacket, check logs ", e);
        }

    }

    /**
     * Send login failure packet back to non-logged in user, via ConnectionHandlerID.
     * 
     * @param destinationId
     * @param connectionHandlerID
     * @param message
     */
    private void sendLoginFailure(ClientId destinationId, ConnectionHandlerID connectionHandlerID, String message) {
        Packet packet = PacketFactory.createLoginDenied(contest.getClientId(), destinationId, message);
        sendToClient(connectionHandlerID, packet);
    }

    /**
     * Send Login Success packet to client.
     * 
     * @param clientId
     * @param connectionHandlerID
     */
    private void sendLoginSuccess(ClientId clientId, ConnectionHandlerID connectionHandlerID) {

        sendToClient(packetHandler.createLoginSuccessPacket(clientId, contest.getContestPassword()));
    }

    public void connectionEstablished(ConnectionHandlerID connectionHandlerID) {
        info("connectionEstablished: " + connectionHandlerID);
        contest.connectionEstablished(connectionHandlerID);

        Packet connectionPacket = PacketFactory.createEstablishedConnection(contest.getClientId(), PacketFactory.ALL_SERVERS, connectionHandlerID);
        sendToAdministrators(connectionPacket);
        sendToServers(connectionPacket);
    }

    /**
     * Connection to client lost.
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public void connectionDropped(ConnectionHandlerID connectionHandlerID)  {

        getLog().log(Log.INFO, "connection Dropped for " + connectionHandlerID, new Exception("connection Dropped for " + connectionHandlerID));
        ClientId clientId = contest.getLoginClientId(connectionHandlerID);
        if (clientId != null) {
            getLog().log(Log.INFO, "connection Dropped for " + connectionHandlerID + " which is " + clientId);
            // Logged in
            removeLogin(clientId);
            if (canCheckoutRunsAndClars(clientId)) {
                try {
                    cancellAll(clientId);
                } catch (ContestSecurityException e) {
                    log.log(Log.WARNING, "Warning on canceling runs/clars for " + clientId, e);
                }
            }
        }

        if (contest.isConnected(connectionHandlerID)) {
            removeConnection(connectionHandlerID);
        }
        // else nothing to do.
    }

    protected void cancelAllClarsByThisJudge(ClientId judgeId) throws ContestSecurityException{
        Clarification[] clars = contest.getClarifications();
        for (int i = 0; i < clars.length; i++) {
            if ((clars[i].getState() == ClarificationStates.BEING_ANSWERED) && (clars[i].getWhoCheckedItOutId().equals(judgeId))) {
                Packet packet = PacketFactory.createUnCheckoutClarification(contest.getClientId(), getServerClientId(), clars[i]);
                try {
                    packetHandler.cancelClarificationCheckOut(packet, null);
                } catch (IOException e) {
                    // TODO dal Auto-generated catch block
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (FileSecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Cancel all checked out runs and clarifications by this client.
     * 
     * @param judgeId
     * @throws ContestSecurityException
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    protected void cancellAll(ClientId judgeId) throws ContestSecurityException {
        cancelAllRunsByThisJudge(judgeId);
        cancelAllClarsByThisJudge(judgeId);
    }

    protected void cancelAllRunsByThisJudge(ClientId judgeId)  {
        ElementId[] runIDs = contest.getRunIdsCheckedOutBy(judgeId);
        for (int i = 0; i < runIDs.length; i++) {
            Run run = contest.getRun(runIDs[i]);
            if (run.getStatus().equals(RunStates.BEING_JUDGED)) {
                ClientId destinationId = new ClientId(run.getSiteNumber(), Type.SERVER, 0);
                Packet packet = PacketFactory.createUnCheckoutRun(judgeId, destinationId, run, judgeId);
                try {
                    packetHandler.cancelRun(packet, run, judgeId, null);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (FileSecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void logoffUser(ClientId clientId) {

        if (isServer() && contest.isLocalLoggedIn(clientId)) {
            // Logged into this server, so we log them off and send out packet.
            
            /**
             * This is a condition where the ServerView, for instance, logs off a user,
             * there is no need to send a packet to the local server, just log them off
             * locally and send out a logoff packet.
             */
            
            ConnectionHandlerID connectionHandlerID = contest.getConnectionHandleID(clientId);
            
            contest.removeLogin(clientId);
            
            forceConnectionDrop(connectionHandlerID);
            
            Packet packet = PacketFactory.createLogoff(contest.getClientId(), PacketFactory.ALL_SERVERS, clientId);

            sendToServers(packet);
            sendToAdministrators(packet);

        } else {
            // Send packet to my sever
            Packet packet = PacketFactory.createLogoff(contest.getClientId(), getServerClientId(), clientId);
            sendToLocalServer(packet);
        }

    }
    
    public void connectionError(Serializable object, ConnectionHandlerID connectionHandlerID, String causeDescription) {

        // TODO code create a packet and send it to servers and admins

        // TODO code connectionError
        info("connectionError: " + contest.getTitle() + " " + connectionHandlerID + " " + causeDescription + " " + object.getClass().getName());

    }

    /**
     * Client receive object.
     * 
     * @see edu.csus.ecs.pc2.core.transport.IBtoA#receiveObject(java.io.Serializable)
     */
    public void receiveObject(Serializable object) {

        info(" receiveObject(S) start got " + object);

        try {
            if (object instanceof Packet) {
                Packet packet = (Packet) object;
                PacketFactory.dumpPacket(log, packet, "recieveObject");

                // TODO code put the server's connection handler id as 4th parameter
                packetHandler.handlePacket(packet, null);
            } else {
                info("receiveObject(S) Unsupported class received: " + object.getClass().getName());
            }
        } catch (Exception e) {
            String message = "Unable to start main UI, contact staff";

            if (loginUI != null) {
                FrameUtilities.regularCursor(loginUI);
            }
            contest.loginDenied(null, null, message);
            info("Exception ", e);
        }
        info(" receiveObject(S) end   got " + object);
    }

    /**
     * This client lost connection.
     */
    public void connectionDropped() {

        // Connection dropped, countdown and halt client
        
        if (clientAutoShutdown){

            if (usingGUI){
                CountDownMessage countDownMessage = new CountDownMessage("Shutting down PC^2 in ", 10);
                if (contest.getClientId() != null) {
                    info("connectionDropped: shutting down " + contest.getClientId());
                    countDownMessage.setTitle("Shutting down PC^2 " + contest.getClientId().getClientType() + " " + contest.getTitle());
                } else {
                    info("connectionDropped: shutting down <non-logged in client>");
                    countDownMessage.setTitle("Shutting down PC^2 Client");
                }
                countDownMessage.setExitOnClose(true);
                if (isUsingMainUI()) {
                    countDownMessage.setVisible(true);
                }
                
            } else {
                if (contest.getClientId() != null) {
                    fatalError("Shutting down PC^2 " + contest.getClientId().getClientType() + " " + contest.getTitle());
                } else {
                    fatalError("connectionDropped: shutting down <non-logged in client>");
                }
            }
            
        } else {
            
            // Tell API that connection was dropped
            contest.connectionDropped(null);
        }
    }

    public void info(String s) {
        // log.warning(s);
        // System.err.println(Thread.currentThread().getName() + " " + s);
        // System.err.flush();
        log.log(Log.INFO, s);
    }

    public void info(String s, Exception exception) {
        // log.log(Log.WARNING, s, exception);
        // System.err.println(Thread.currentThread().getName() + " " + s);
        // System.err.flush();
        // exception.printStackTrace(System.err);
        log.log(Log.INFO, s, exception);
    }

    public void setSiteNumber(int number) {
        contest.setSiteNumber(number);
    }

    public void setContestTime(ContestTime contestTime) {
        if (contest.getContestTime() != null) {
            contest.updateContestTime(contestTime);
        } else {
            contest.addContestTime(contestTime);
        }
    }

    public void sendToServers(Packet packet) {
        ClientId[] clientIds = contest.getLocalLoggedInClients(ClientType.Type.SERVER);
        for (ClientId clientId : clientIds) {
            ConnectionHandlerID connectionHandlerID = contest.getConnectionHandleID(clientId);
            boolean isThisServer = isThisSite(clientId.getSiteNumber());
            if (!isThisServer) {
                // Send to other servers
                sendToClient(connectionHandlerID, packet);
            }
        }
    }

    /**
     * Send packet to all this sites logged in clients.
     * 
     * @param packet
     */
    private void sendPacketToClients(Packet packet, ClientType.Type type) {

        ClientId[] clientIds = contest.getLocalLoggedInClients(type);
        for (ClientId clientId : clientIds) {
            if (isThisSite(clientId.getSiteNumber())) {
                ConnectionHandlerID connectionHandlerID = contest.getConnectionHandleID(clientId);
                sendToClient(connectionHandlerID, packet);
            }
        }
    }

    private boolean isThisSite(int siteNumber) {
        return siteNumber == contest.getSiteNumber();
    }

    /**
     * Send to judges and spectators clients.
     * 
     */
    public void sendToJudges(Packet packet) {
        sendPacketToClients(packet, ClientType.Type.JUDGE);
        sendPacketToClients(packet, ClientType.Type.SPECTATOR);
    }

    public void sendToSpectators(Packet packet) {
        sendPacketToClients(packet, ClientType.Type.SPECTATOR);
    }
    public void sendToAdministrators(Packet packet) {
        sendPacketToClients(packet, ClientType.Type.ADMINISTRATOR);
    }

    public void sendToScoreboards(Packet packet) {
        sendPacketToClients(packet, ClientType.Type.SCOREBOARD);
    }

    public void sendToTeams(Packet packet) {
        Properties properties = (Properties) packet.getContent();
        // does the packet includes problemDataFiles
        boolean abort = true;
        if (properties.containsKey(PacketFactory.PROBLEM_DATA_FILES)) {
            // clone before start mucking with it, or do we need a deep clone?
            Properties cloneProperties = new Properties();
            for (Iterator<?> iter = properties.keySet().iterator(); iter.hasNext();) {
                String element = (String) iter.next();
                // skip PROBLEM_DATA_FILES, otherwise clone the element
                if (!element.equals(PacketFactory.PROBLEM_DATA_FILES)) {
                    cloneProperties.put(element, properties.get(element));
                    abort = false;
                }
            }
            packet = PacketFactory.clonePacket(packet.getSourceId(), packet.getDestinationId(), packet);
            // stick it back into the packet
            packet.setContent(cloneProperties);
        } else {
            abort = false;
        }
        if (!abort) {
            sendPacketToClients(packet, ClientType.Type.TEAM);
        }
    }

    private int getPortForSite(int inSiteNumber) {

        try {
            Site[] sites = contest.getSites();
            for (Site site : sites) {
                if (site.getSiteNumber() == inSiteNumber) {
                    String portStr = site.getConnectionInfo().getProperty(Site.PORT_KEY);
                    return Integer.parseInt(portStr);
                }
            }

        } catch (Exception e) {
            info("Exception logged ", e);
            throw new SecurityException("Unable to determine port for site " + inSiteNumber);
        }

        throw new SecurityException("Could not find site " + inSiteNumber + " in site list, there are " + contest.getSites().length + " sites.");
    }

    /**
     * Client has successfully logged in, show them UI.
     * 
     * @param clientId
     *            new client id
     */
    public void startMainUI(ClientId clientId) {

        try {

            contest.setClientId(clientId);

            boolean isServer = clientId.getClientType().equals(ClientType.Type.SERVER);

            if (isServer && isContactingRemoteServer()) {
                // secondary server logged in, start listening.

                port = getPortForSite(contest.getSiteNumber());

                if (parseArguments.getOptValue("--port") != null) {
                    String portString = parseArguments.getOptValue("--port");
                    getLog().log(Log.INFO, "Attempting to use port from --port '" + portString + "'");
                    port = Integer.parseInt(portString);
                }

                info("Started Server Transport listening on " + port);
                connectionManager.accecptConnections(port);

                info("Secondary Server has started " + contest.getTitle());
            }

            try {
                if (isUsingMainUI()) {
                    if (uiPlugin == null) {

                        String uiClassName = overRideUIName;

                        if (overRideUIName == null) {
                            uiClassName = LoadUIClass.getUIClassName(clientId);
                        }

                        if (uiClassName == null) {
                            String clientName = clientId.getClientType().toString().toLowerCase();
                            info("Unable to find UI for client " + clientName + " in properties file " + LoadUIClass.UI_PROPERTIES_FILENAME);
                            fatalError("Unable to determine UI class for " + clientName);
                        } else {
                            info("Attempting to load UI class " + uiClassName);
                            uiPlugin = LoadUIClass.loadUIClass(uiClassName);
                            info("Loaded UI class " + uiClassName);
                        }
                    }

                    uiPlugin.setContestAndController(contest, this);

                    if (loginUI != null) {
                        loginUI.dispose();
                    }
                }
            } catch (Exception e) {
                fatalError ("Error loading UI, check log, (class not found?)  " + e.getMessage(), e);
            }

        } catch (Exception e) {
            // TODO separate the showing main Frame and listening to port exception messages
            info("Error showing frame or listening to port ", e);
            if (loginUI != null) {
                FrameUtilities.regularCursor(loginUI);
            }
            contest.loginDenied(clientId, null, e.getMessage() + " (port " + port + ")");
        }
    }

    /**
     * Start the UI.
     */
    public void start(String[] stringArray) {

        startLog("pc2.startup", null, null);
        
        /**
         * Saved exception.
         * 
         * If TransportException thrown before UI has been created, save the exception and present it on the UI later.
         */
        TransportException savedTransportException = null;

        String[] requireArguementArgs = { "--login", "--id", "--password", MAIN_UI_OPTION, "--remoteServer", "--port", INI_FILENAME_OPTION_STRING, CONTEST_PASSWORD_OPTION, FILE_OPTION_STRING };
        parseArguments = new ParseArguments(stringArray, requireArguementArgs);
        
        if (parseArguments.isOptPresent("--help")) {
            // -F is the ParseArguements internal option to pre-load command line options from a file
            System.out.println("Usage: Starter [--help] [--server] [--first] [--login <login>] [--password <pass>] [--site ##] [--skipini] ["+INI_FILENAME_OPTION_STRING+" filename] [" 
                    +  CONTEST_PASSWORD_OPTION + " <pass>] [-F filename] ["+NO_GUI_STRING+"] ["+MAIN_UI_OPTION+" classname]");
            System.exit(0);
        }
        
        if (parseArguments.isOptPresent(FILE_OPTION_STRING)){
            String propertiesFileName = parseArguments.getOptValue(FILE_OPTION_STRING);
            
            if (! (new File(propertiesFileName).exists())){
                fatalError(propertiesFileName+" does not exist (pwd: "+Utilities.getCurrentDirectory()+")", null);
            }
            
            try {
                parseArguments.overRideOptions(propertiesFileName);
            } catch (IOException e) {
                fatalError("Unable to read file "+propertiesFileName, e);
            }
        }
        
        if (parseArguments.isOptPresent(NO_GUI_STRING)) {
            
            usingGUI = false;
            
            // Insure that they have specified required
            // do not check contestPassword here
            
            String loginName = parseArguments.getOptValue(LOGIN_OPTION_STRING);
            
            ClientId client = loginShortcutExpansion(0, loginName);
            if (! isServer(client)) {
                fatalError("Only PC^2 server can use --nogui (--login is "+loginName+")");
            }
            
            if (loginName == null){
                fatalError("Must specify "+LOGIN_OPTION_STRING+" when using "+NO_GUI_STRING);
            }
            
            if (overRideUIName == null){
                overRideUIName = "edu.csus.ecs.pc2.ui.server.ServerModule";
            }
        }

        if (parseArguments.isOptPresent(DEBUG_OPTION_STRING)) {
            
            Utilities.setDebugMode(true);

            log.info("Debug mode ON");
            System.out.println("Debug mode ON");
            
            System.out.println("debug: "+new VersionInfo().getSystemName()+" Build "+new VersionInfo().getBuildNumber());
            
            try {
                System.out.println("debug: Working directory is " + new File(".").getCanonicalPath());
            } catch (IOException e1) {
                System.out.println("debug: Could not determine working directory " + e1.getMessage());
                e1.printStackTrace(System.err);
            }
        }
        
        if (parseArguments.isOptPresent(CONTEST_PASSWORD_OPTION)) {

            String newContestPassword = parseArguments.getOptValue(CONTEST_PASSWORD_OPTION);
            if (newContestPassword == null) {
                fatalError("No contest password found after " + CONTEST_PASSWORD_OPTION);
            }
            contest.setContestPassword(newContestPassword);
        }
        
        if (parseArguments.isOptPresent(MAIN_UI_OPTION)) {
            String overrideClassName = parseArguments.getOptValue(MAIN_UI_OPTION);
            if (overrideClassName == null) {
                fatalError("No UI name after " + MAIN_UI_OPTION);
            }
            overRideUIName = overrideClassName;
        }
        

        for (String arg : stringArray) {
            if (arg.equals("--first")) {
                setContactingRemoteServer(false);
            }
        }

        /**
         * if (args DOES NOT contains login/pwd) { String s; if (args contains LoginUI ) { s = args login UI } else { s = pc2 LoginFrame } UIPlugin l = classloader (s); l.setModelAndListener (contest,
         * this); } else { this.login (login,password)
         * 
         */

        log.info("Starting ConnectionManager...");
        connectionManager = new ConnectionManager(log);
        log.info("Started ConnectionManager");
        
        boolean useIniFile = ! parseArguments.isOptPresent("--skipini"); 

        if (parseArguments.isOptPresent(INI_FILENAME_OPTION_STRING) && useIniFile) {
            String iniName = parseArguments.getOptValue(INI_FILENAME_OPTION_STRING);
            Exception exception = null;
            try {
                System.err.println("Loading INI from " + iniName);
                ini.setIniURLorFile(iniName);
                // _source is set if we can successfully open the stream
                if (!ini.containsKey("_source")) {
                    System.err.println("Unable to load INI from " + iniName);
                    getLog().log(Log.WARNING, "Unable to read ini URL " + iniName);
                    exception = new Exception("Unable to read ini file " + iniName);
                }
            } catch (Exception e) {
                System.err.println("Unable to load INI from " + iniName);
                getLog().log(Log.WARNING, "Unable to read ini URL " + iniName, e);
                exception = e;
            }

            if (exception != null){
                fatalError("Cannot start PC^2, "+iniName+" cannot be read ("+exception.getMessage()+")", exception);
            }
        }

        contest.setSiteNumber(0);

        if (parseArguments.isOptPresent(SITE_OPTION_STRING)) {

            String siteNumberParam = parseArguments.getOptValue(SITE_OPTION_STRING);

            if (siteNumberParam == null || siteNumberParam.trim().length() == 0) {
                savedTransportException = new TransportException("No site found after " + SITE_OPTION_STRING);
            }

            try {
                int siteNumber = Integer.parseInt(siteNumberParam);
                contest.setSiteNumber(siteNumber);

            } catch (Exception e) {
                getLog().log(Log.WARNING, "Expecting a number after " + SITE_OPTION_STRING + " found " + siteNumberParam, e);
                savedTransportException = new TransportException("Invalid site after " + SITE_OPTION_STRING);
            }
        }
        
        if (useIniFile && (!parseArguments.isOptPresent(INI_FILENAME_OPTION_STRING))) {
            if (IniFile.isFilePresent()) {
                // Only read and load .ini file if it is present.
                new IniFile();
            } else {
                String currentDirectory = Utilities.getCurrentDirectory();
                fatalError("Cannot start PC^2, " + IniFile.getINIFilename() + " file not found in " + currentDirectory);
            }
        }
        
        log.log(Log.DEBUG, "Site Number is set as " + contest.getSiteNumber() + " (0 means unset)");


        // TODO code add NO_SAVE_OPTION_STRING
        if (parseArguments.isOptPresent("--nosave")) {
            saveCofigurationToDisk = false;
        }

        if (parseArguments.isOptPresent("--server")) {
            info("Starting Server Transport...");
            connectionManager.startServerTransport(this);
            serverModule = true;

            contactingRemoteServer = false;
            setServerRemoteHostAndPort(parseArguments.getOptValue("--remoteServer"));

            try {
                setServerPort(parseArguments.getOptValue("--port"));
            } catch (NumberFormatException numException) {
                savedTransportException = new TransportException("Unable to parse value after --port '" + parseArguments.getOptValue("--port") + "'");
                log.log(Log.WARNING, "Exception logged ", numException);
            }

        } else {
            // Client contact server

            try {

                setClientServerAndPort(parseArguments.getOptValue("--port"));

                info("Contacting server at " + remoteHostName + ":" + remoteHostPort);
                connectionManager.startClientTransport(remoteHostName, remoteHostPort, this);
            } catch (NumberFormatException numException) {
                savedTransportException = new TransportException("Unable to parse value after --port '" + parseArguments.getOptValue("--port") + "'");
                log.log(Log.WARNING, "Exception logged ", numException);
            }

            try {
                connectionManager.connectToMyServer();
            } catch (TransportException transportException) {
                savedTransportException = transportException;
                log.log(Log.INFO, "Exception logged ", transportException);
                info("Unable to contact server at " + remoteHostName + ":" + port + " " + transportException.getMessage());
            }
        }

        isStarted = true;

        if (!parseArguments.isOptPresent(LOGIN_OPTION_STRING)) {

            if (usingGUI && isUsingMainUI()) {
                loginUI = new LoginFrame();
                loginUI.setContestAndController(contest, this);
            }

        } else {
            // has a login, go for it.

            // Get loginId
            String loginName = "";
            if (parseArguments.isOptPresent(LOGIN_OPTION_STRING)) {
                loginName = parseArguments.getOptValue(LOGIN_OPTION_STRING);
            }

            // get password (optional if joe password)
            String password = "";
            if (parseArguments.isOptPresent(PASSWORD_OPTION_STRING)) {
                password = parseArguments.getOptValue(PASSWORD_OPTION_STRING);
            }
            
            if (usingGUI){
                loginUI = new LoginFrame();
                loginUI.setContestAndController(contest, this); // this displays the login
            }

            try {

                if (savedTransportException == null) {
                    login(loginName, password); // starts login attempt, will show failure to LoginFrame
                }

            } catch (Exception e) {
                log.log(Log.INFO, "Exception logged ", e);
                if (loginUI != null) {
                    loginUI.setStatusMessage(e.getMessage());
                }
            }
        }
        
        
        String contactInfo = getHostContacted()+":"+getPortContacted();

        if (usingGUI && (savedTransportException != null && loginUI != null)) {
            loginUI.disableLoginButton();
            loginUI.setStatusMessage("Unable to contact server, contact staff");
            JOptionPane.showMessageDialog(null, "Unable to contact server at: "+contactInfo, "Error contacting server", JOptionPane.ERROR_MESSAGE);
        } else if (savedTransportException != null) {
            connectionManager = null;
            fatalError("Unable to contact server at: " + contactInfo + ", contact staff", savedTransportException);
        }
    }

    private ClientId getServerClientId() {
        // TODO s/new ClientId(contest.getSiteNumber(), Type.SERVER, 0);/getServerClientId()/
        return new ClientId(contest.getSiteNumber(), Type.SERVER, 0);
    }

    /*
     * This new a new Log(logFileName), sets up the StaticLog, and prints
     * basic info to the log.  If loginName is not null a Login: line is printed.
     */
    private void startLog(String logFileName, String loginName, String clientName) {
        log = new Log(logFileName);
        StaticLog.setLog(log);

        info("");
        info(new VersionInfo().getSystemVersionInfo());
        if (loginName != null) {
            info("Login: " + loginName + " (aka " + clientName + ")");
        }
        try {
            log.info("Working directory is " + new File(".").getCanonicalPath());
        } catch (IOException e1) {
            log.info("Could not determine working directory " + e1.getMessage());
        }
        try {
            log.info("Process id is "+ManagementFactory.getRuntimeMXBean().getName());
        } catch (Exception e) {
            log.info("Could not determine process id " + e.getMessage());
        }
    }

    public void checkOutRun(Run run, boolean readOnly, boolean computerJudge) {
        ClientId clientId = contest.getClientId();
        Packet packet = PacketFactory.createRunRequest(clientId, getServerClientId(), run, clientId, readOnly, computerJudge);
        sendToLocalServer(packet);
    }

    public void checkOutRejudgeRun(Run run) {
        ClientId clientId = contest.getClientId();
        Packet packet = PacketFactory.createRunRejudgeRequest(clientId, getServerClientId(), run, clientId);
        sendToLocalServer(packet);
    }

    /**
     * Send run judgement to server.
     */
    public void submitRunJudgement(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles) {
        ClientId clientId = contest.getClientId();
        Packet packet = PacketFactory.createRunJudgement(clientId, getServerClientId(), run, judgementRecord, runResultFiles);
        sendToLocalServer(packet);
    }

    /**
     * Send cancel run to server.
     */
    public void cancelRun(Run run) {
        ClientId clientId = contest.getClientId();
        Packet packet = PacketFactory.createUnCheckoutRun(clientId, getServerClientId(), run, clientId);
        sendToLocalServer(packet);
    }

    /**
     * Add a new site into contest, send update to other servers.
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public void addNewSite(Site site) {
        if (isServer()) {
            contest.addSite(site);
            try {
                contest.storeConfiguration(getLog());
                
                Packet packet = PacketFactory.createAddSetting(contest.getClientId(), PacketFactory.ALL_SERVERS, site);
                sendToServers(packet);

                sendToJudges(packet);
                sendToAdministrators(packet);
                sendToScoreboards(packet);
                
            } catch (IOException e) {
                // TODO dal Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO dal Auto-generated catch block
                e.printStackTrace();
            } catch (FileSecurityException e) {
                // TODO dal Auto-generated catch block
                e.printStackTrace();
            }
 
        } else {
            Packet packet = PacketFactory.createAddSetting(contest.getClientId(), getServerClientId(), site);
            sendToLocalServer(packet);
        }
    }

    /**
     * Modify an existing site, send update to other servers.
     */
    public void modifySite(Site site) {
        contest.changeSite(site);
        Packet packet = PacketFactory.createUpdateSetting(getServerClientId(), PacketFactory.ALL_SERVERS, site);
        sendToServers(packet);
    }

    public void sendServerLoginRequest(int inSiteNumber) throws Exception {

        if (isServer()) {

            if (isThisSite(inSiteNumber)) {
                /**
                 * We should not send a LOGIN_REQUEST from this site... to this site.
                 */
                System.err.println(" Tried to send login request to ourselves, login to " + inSiteNumber + ", ignored");
                log.log(Log.DEBUG, " Tried to send login request to ourselves, login to " + inSiteNumber + ", ignored");
                return;
            }

            Site remoteSite = contest.getSite(inSiteNumber);
            Site localSite = contest.getSite(contest.getSiteNumber());
            String localPassword = localSite.getPassword();

            String hostName = remoteSite.getConnectionInfo().getProperty(Site.IP_KEY);
            String portStr = remoteSite.getConnectionInfo().getProperty(Site.PORT_KEY);
            int portNumber = Integer.parseInt(portStr);

            info("Send login request to Site " + remoteSite.getSiteNumber() + " " + hostName + ":" + portStr);
            ConnectionHandlerID connectionHandlerID = connectionManager.connectToServer(hostName, portNumber);

            info("Contacted Site " + remoteSite.getSiteNumber() + " using connection id " + connectionHandlerID);
            info("Sending login request to Site " + remoteSite.getSiteNumber() + " " + hostName + " as " + getServerClientId() + " " + localPassword); // TODO remove this
            sendLoginRequestFromServerToServer(connectionManager, connectionHandlerID, getServerClientId(), localPassword);
        } else if (contest.isAllowed(Permission.Type.ALLOWED_TO_RECONNECT_SERVER)) {
            // Send the reconnection request to our server

            Packet reconnectPacket = PacketFactory.createReconnectPacket(contest.getClientId(), getServerClientId(), inSiteNumber);
            sendToLocalServer(reconnectPacket);
        } else {
            // TODO security problem
            System.err.println(" Non-admin Tried to send reconnection request " + inSiteNumber + ", ignored");
            log.log(Log.DEBUG, " Non-admin Tried to send reconnection request " + inSiteNumber + ", ignored");
            return;

        }

    }

    /**
     * Contacting remote server (joining contest).
     * 
     * @return true if joining contest, false if first server
     */
    public boolean isContactingRemoteServer() {
        return contactingRemoteServer;
    }

    public void setContactingRemoteServer(boolean contactingRemoteServer) {
        this.contactingRemoteServer = contactingRemoteServer;
    }

    /**
     * Will main UI be invoked/displayed ?
     * 
     * This includes Login UI as well as Main UI.
     * 
     * @return true - shows main UI, false - does not show main UI.
     */
    public boolean isUsingMainUI() {
        return usingMainUI;
    }

    public void setUsingMainUI(boolean usingMainUI) {
        this.usingMainUI = usingMainUI;
    }

    public UIPlugin getUiPlugin() {
        return uiPlugin;
    }

    public void setUiPlugin(UIPlugin uiPlugin) {
        this.uiPlugin = uiPlugin;
    }

    public void updateSite(Site site)  {

        if (isServer()) {
            contest.changeSite(site);
            try {
                contest.storeConfiguration(getLog());

                Packet packet = PacketFactory.createUpdateSetting(contest.getClientId(), PacketFactory.ALL_SERVERS, site);
                sendToServers(packet);

                sendToJudges(packet);
                sendToAdministrators(packet);
                sendToScoreboards(packet);
                
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FileSecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            Packet packet = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), site);
            sendToLocalServer(packet);
        }
    }

    /**
     * Returns true if this client is a server.
     * 
     * @return true if logged in client is a server.
     */
    private boolean isServer() {
        return contest.getClientId() != null && isServer(contest.getClientId());
    }

    private boolean isServer(ClientId clientId) {
        return clientId.getClientType().equals(ClientType.Type.SERVER);
    }

    public final Log getLog() {
        return log;
    }

    public void generateNewAccounts(String clientTypeName, int siteNumber, int count, int startNumber, boolean active) {
        ClientType.Type type = ClientType.Type.valueOf(clientTypeName);
        Packet packet = PacketFactory.createGenerateAccounts(contest.getClientId(), getServerClientId(), siteNumber, type, count, startNumber, active);
        sendToLocalServer(packet);
    }

    public void generateNewAccounts(String clientTypeName, int count, int startNumber, boolean active) {
        generateNewAccounts(clientTypeName, contest.getSiteNumber(), count, startNumber, active);

    }

    public void submitClarification(Problem problem, String question) {

        ClientId serverClientId = new ClientId(contest.getSiteNumber(), Type.SERVER, 0);
        Clarification clarification = new Clarification(contest.getClientId(), problem, question);

        Packet packet = PacketFactory.createClarificationSubmission(contest.getClientId(), serverClientId, clarification);

        sendToLocalServer(packet);
    }

    public void checkOutClarification(Clarification clarification, boolean readOnly) {
        ClientId serverClientId = new ClientId(contest.getSiteNumber(), Type.SERVER, 0);
        Packet packet = PacketFactory.createClarificationRequest(contest.getClientId(), serverClientId, clarification.getElementId(), contest.getClientId());
        sendToLocalServer(packet);
    }

    public void cancelClarification(Clarification clarification) {
        ClientId serverClientId = new ClientId(contest.getSiteNumber(), Type.SERVER, 0);
        Packet packet = PacketFactory.createUnCheckoutClarification(contest.getClientId(), serverClientId, clarification);
        sendToLocalServer(packet);
    }

    public void submitClarificationAnswer(Clarification clarification) {
        ClientId serverClientId = new ClientId(contest.getSiteNumber(), Type.SERVER, 0);
        Packet packet = PacketFactory.createAnsweredClarification(contest.getClientId(), serverClientId, clarification, clarification.getAnswer());
        sendToLocalServer(packet);
    }

    public void forceConnectionDrop(ConnectionHandlerID connectionHandlerID) {
        
        if (isServer()){
            
            if (contest.isConnected(connectionHandlerID)){
                log.log(Log.INFO, "forceConnectionDrop: " + connectionHandlerID);
                connectionManager.unregisterConnection(connectionHandlerID);
                contest.connectionDropped(connectionHandlerID);
            } else {
                // must be another server, send to all servers
                
                Packet forceDiscoPacket = PacketFactory.createForceLogoff(contest.getClientId(), PacketFactory.ALL_SERVERS, connectionHandlerID);
                sendToServers(forceDiscoPacket);
            }
        
        } else {
            // Local connection list, remove them
            contest.connectionDropped(connectionHandlerID);
        }
    }

    public void addNewProblem(Problem problem, ProblemDataFiles problemDataFiles) {
        Packet updateProblemPacket = PacketFactory.createAddSetting(contest.getClientId(), getServerClientId(), problem, problemDataFiles);
        sendToLocalServer(updateProblemPacket);
    }

    public void updateRun(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles) {
        Packet updateRunPacket = PacketFactory.createRunUpdated(contest.getClientId(), getServerClientId(), run, judgementRecord, runResultFiles, contest.getClientId());
        sendToLocalServer(updateRunPacket);
    }

    public void addProblem(Problem problem) {
        Packet updateProblemPacket = PacketFactory.createAddSetting(contest.getClientId(), getServerClientId(), problem, null);
        sendToLocalServer(updateProblemPacket);
    }

    public void updateProblem(Problem problem) {
        Packet updateProblemPacket = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), problem, null);
        sendToLocalServer(updateProblemPacket);
    }

    public void updateProblem(Problem problem, ProblemDataFiles problemDataFiles) {
        Packet updateProblemPacket = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), problem, problemDataFiles);
        sendToLocalServer(updateProblemPacket);
    }

    public ProblemDataFiles getProblemDataFiles(Problem problem) {
        return contest.getProblemDataFile(problem);
    }

    public void shutdownTransport() {
        connectionManager.shutdownTransport();
    }

    /**
     * Removes connection from list and sends packet.
     */
    public void removeConnection(ConnectionHandlerID connectionHandlerID) {

        contest.connectionDropped(connectionHandlerID);
        Packet disconnectionPacket = PacketFactory.createDroppedConnection(contest.getClientId(), PacketFactory.ALL_SERVERS, connectionHandlerID);
        PacketFactory.dumpPacket(log, disconnectionPacket, "removeConnection");
        sendToAdministrators(disconnectionPacket);
        sendToServers(disconnectionPacket);

    }

    /**
     * Removed login from system and sends packet.
     */
    public void removeLogin(ClientId clientId) {

        contest.removeLogin(clientId);

        try {
            Packet logoffPacket = PacketFactory.createLogoff(contest.getClientId(), PacketFactory.ALL_SERVERS, clientId);
            sendToAdministrators(logoffPacket);
            if (!isServer(clientId)) {
                // Each server tracks its own list of server logins.
                sendToServers(logoffPacket);
            }
        } catch (Exception e) {
            log.log(Log.SEVERE, "Exception removeLogin ", e);
        }
    }

    public void startContest(int inSiteNumber) {
        Packet packet = PacketFactory.createStartContestClock(contest.getClientId(), getServerClientId(), inSiteNumber, contest.getClientId());
        sendToLocalServer(packet);
    }

    public void stopContest(int inSiteNumber) {
        Packet packet = PacketFactory.createStopContestClock(contest.getClientId(), getServerClientId(), inSiteNumber, contest.getClientId());
        sendToLocalServer(packet);
    }

    public void startAllContestTimes() {
        Packet packet = PacketFactory.createStartAllClocks(contest.getClientId(), getServerClientId(), contest.getClientId());
        sendToLocalServer(packet);
    }

    public void stopAllContestTimes() {
        Packet packet = PacketFactory.createStopAllClocks(contest.getClientId(), getServerClientId(), contest.getClientId());
        sendToLocalServer(packet);
    }

    public void addNewLanguage(Language language) {
        Packet addLanguagePacket = PacketFactory.createAddSetting(contest.getClientId(), getServerClientId(), language);
        sendToLocalServer(addLanguagePacket);
    }

    public void addNewJudgement(Judgement judgement) {
        Packet addJudgementPacket = PacketFactory.createAddSetting(contest.getClientId(), getServerClientId(), judgement);
        sendToLocalServer(addJudgementPacket);
    }

    public void updateLanguage(Language language) {
        Packet updateLanguagePacket = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), language);
        sendToLocalServer(updateLanguagePacket);
    }

    public void updateJudgement(Judgement judgement) {
        Packet updatePacket = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), judgement);
        sendToLocalServer(updatePacket);
    }

    public void updateAccount(Account account) {
        Packet updatePacket = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), account);
        sendToLocalServer(updatePacket);
    }

    public void updateAccounts(Account[] accounts) {
        Packet updatePacket = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), accounts);
        sendToLocalServer(updatePacket);
    }

    public void addNewAccount(Account account) {
        Packet addAccountPacket = PacketFactory.createAddSetting(contest.getClientId(), getServerClientId(), account);
        sendToLocalServer(addAccountPacket);
    }

    public void addNewAccounts(Account[] accounts) {
        Packet addAccountPacket = PacketFactory.createAddSetting(contest.getClientId(), getServerClientId(), accounts);
        sendToLocalServer(addAccountPacket);
    }

    public boolean readConfigFromDisk(int siteNum) {

        boolean loadedConfiguration = false;
        if (saveCofigurationToDisk) {
            loadedConfiguration = contest.readConfiguration(siteNum, getLog());
        }
        return loadedConfiguration;
    }

   


    public void addNewClientSettings(ClientSettings clientSettings) {
        Packet addClientSettingsPacket = PacketFactory.createAddSetting(contest.getClientId(), getServerClientId(), clientSettings);
        sendToLocalServer(addClientSettingsPacket);
    }

    public void updateClientSettings(ClientSettings clientSettings) {
        Packet updateClientSettingsPacket = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), clientSettings);
        sendToLocalServer(updateClientSettingsPacket);
    }

    public void updateContestInformation(ContestInformation contestInformation) {
        Packet addAccountPacket = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), contestInformation);
        sendToLocalServer(addAccountPacket);
    }

    public void setJudgementList(Judgement[] judgementList) {
        Packet updatePacket = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), judgementList);
        sendToLocalServer(updatePacket);
    }

    public void removeJudgement(Judgement judgement) {
        Packet deleteJudgmentPacket = PacketFactory.createDeleteSetting(contest.getClientId(), getServerClientId(), judgement);
        sendToLocalServer(deleteJudgmentPacket);
    }

    public void addNewBalloonSettings(BalloonSettings newBalloonSettings) {
        Packet newBalloonSettingsPacket = PacketFactory.createAddSetting(contest.getClientId(), getServerClientId(), newBalloonSettings);
        sendToLocalServer(newBalloonSettingsPacket);
    }

    public void updateBalloonSettings(BalloonSettings balloonSettings) {
        Packet balloonSettingsPacket = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), balloonSettings);
        sendToLocalServer(balloonSettingsPacket);
    }

    public int getSiteNumber() {
        return contest.getSiteNumber();
    }

    public void updateContestTime(ContestTime newContestTime) {
        Packet newContestTimePacket = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), newContestTime);
        sendToLocalServer(newContestTimePacket);
    }

    public void addNewGroup(Group group) {
        Packet newGroupPacket = PacketFactory.createAddSetting(contest.getClientId(), getServerClientId(), group);
        sendToLocalServer(newGroupPacket);
    }

    public void updateGroup(Group group) {
        Packet groupPacket = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), group);
        sendToLocalServer(groupPacket);
    }

    public int getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(int securityLevel) {
        this.securityLevel = securityLevel;
    }

    /**
     * Server send out security packet.
     * 
     */
    public void sendSecurityMessageFromServer(ContestSecurityException contestSecurityException, ConnectionHandlerID connectionHandlerID, Packet packet) {
        Packet violationPacket = PacketFactory.createSecurityMessagePacket(contest.getClientId(), PacketFactory.ALL_SERVERS, contestSecurityException.getSecurityMessage(), null, connectionHandlerID,
                contestSecurityException, packet);

        sendToAdministrators(violationPacket);
        sendToServers(violationPacket);

        contest.newSecurityMessage(contestSecurityException.getClientId(), contestSecurityException.getSecurityMessage(), "", contestSecurityException);

    }

    public void sendSecurityMessage(String event, String message, ContestSecurityException contestSecurityException) {
        Packet securityMessagePacket = PacketFactory.createSecurityMessagePacket(contest.getClientId(), getServerClientId(), event, contestSecurityException.getClientId(), contestSecurityException
                .getConnectionHandlerID(), contestSecurityException, null);
        sendToLocalServer(securityMessagePacket);
    }

    public String getHostContacted() {
        return remoteHostName;
    }

    public int getPortContacted() {
        return remoteHostPort;
    }

    public void fetchRun(Run run) throws IOException, ClassNotFoundException, FileSecurityException {
        
        RunFiles runFiles = contest.getRunFiles(run);
        if (runFiles != null){
            contest.updateRun(run, runFiles, null, null);       
        } else {
            Packet fetchRunPacket = PacketFactory.createFetchRun(contest.getClientId(), getServerClientId(), run, contest.getClientId());
            sendToLocalServer(fetchRunPacket);
        }
    }
    
    private void sendStatusMessge(Run run, RunExecutionStatus status){
        
        if (contest.isSendAdditionalRunStatusMessages()){
                Packet sendPacket = PacketFactory.createRunStatusPacket(contest.getClientId(), getServerClientId(), run, contest.getClientId(), status);
                sendToLocalServer(sendPacket);
        }
    }

    public void sendCompilingMessage(Run run) {
        sendStatusMessge(run, RunExecutionStatus.COMPILING);
    }

    public void sendExecutingMessage(Run run) {
        sendStatusMessge(run, RunExecutionStatus.EXECUTING);
    }

    public void sendValidatingMessage(Run run) {
        sendStatusMessge(run, RunExecutionStatus.VALIDATING);
    }

    public boolean isClientAutoShutdown() {
        return clientAutoShutdown;
    }

    public void setClientAutoShutdown(boolean clientAutoShutdown) {
        this.clientAutoShutdown = clientAutoShutdown;
    }

    public void resetContest(ClientId clientResettingContest, boolean eraseProblems, boolean eraseLanguages) {
        Packet sendPacket = PacketFactory.createResetAllSitesPacket(contest.getClientId(), getServerClientId(), clientResettingContest, eraseProblems, eraseLanguages);
        sendToLocalServer(sendPacket);
    }

    public void cloneProfile(Profile profile, ProfileCloneSettings settings, boolean switchNow) {
        Packet sendPacket = PacketFactory.createCloneProfilePacket(contest.getClientId(), getServerClientId(), profile, settings, switchNow);
        sendToLocalServer(sendPacket);
    }

    public void switchProfile(Profile currentProfile, Profile switchToProfile) {
        Packet sendPacket = PacketFactory.createSwitchProfilePacket(contest.getClientId(), getServerClientId(), currentProfile, switchToProfile);
        sendToLocalServer(sendPacket);
    }

    public void updateProfile(Profile profile) {
        Packet updateProfilePackert = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), profile);
        sendToLocalServer(updateProfilePackert);
    }
    
    /**
     * Fatal error - log error and show user message before exiting.
     * 
     * @param message
     * @param ex
     */
    protected void fatalError (String message, Exception ex){
        if (log != null){
            if (ex != null){
                log.log(Log.SEVERE, message, ex);
            } else {
                log.log(Log.SEVERE, message);
            }
            log.log(Log.INFO, "PC^2 halted");
        }

        if (usingGUI){
            JOptionPane.showMessageDialog(null, message +" check logs" , "PC^2 Halted", JOptionPane.ERROR_MESSAGE);
        } else {
            System.err.println(message);
            if (ex != null){
                ex.printStackTrace(System.err);
            }
            System.err.println("PC^2 Halted");
        }
        
        System.exit(4);
    }
    
    /**
     * 
     * @see #fatalError(String, Exception)
     * @param message
     */
    private void fatalError(String message) {
        fatalError(message, null);
    }
}
