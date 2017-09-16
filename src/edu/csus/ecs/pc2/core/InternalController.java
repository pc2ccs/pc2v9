package edu.csus.ecs.pc2.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.clics.RunSubmitterInterfaceManager;
import edu.csus.ecs.pc2.convert.EventFeedRun;
import edu.csus.ecs.pc2.convert.LoadRuns;
import edu.csus.ecs.pc2.core.archive.PacketArchiver;
import edu.csus.ecs.pc2.core.exception.ContestSecurityException;
import edu.csus.ecs.pc2.core.exception.ProfileException;
import edu.csus.ecs.pc2.core.exception.ServerProcessException;
import edu.csus.ecs.pc2.core.log.EvaluationLog;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Category;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.Clarification.ClarificationStates;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ILoginListener;
import edu.csus.ecs.pc2.core.model.IPacketListener;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LoginEvent;
import edu.csus.ecs.pc2.core.model.MessageEvent.Area;
import edu.csus.ecs.pc2.core.model.PacketEvent;
import edu.csus.ecs.pc2.core.model.PacketEvent.Action;
import edu.csus.ecs.pc2.core.model.PlaybackInfo;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.model.RunExecutionStatus;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;
import edu.csus.ecs.pc2.core.packet.PacketType;
import edu.csus.ecs.pc2.core.report.ContestSummaryReports;
import edu.csus.ecs.pc2.core.security.FileSecurity;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.core.transport.IBtoA;
import edu.csus.ecs.pc2.core.transport.ITransportManager;
import edu.csus.ecs.pc2.core.transport.ITwoToOne;
import edu.csus.ecs.pc2.core.transport.TransportException;
import edu.csus.ecs.pc2.core.transport.connection.ConnectionManager;
import edu.csus.ecs.pc2.imports.ccs.ContestSnakeYAMLLoader;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;
import edu.csus.ecs.pc2.profile.ProfileCloneSettings;
import edu.csus.ecs.pc2.profile.ProfileManager;
import edu.csus.ecs.pc2.ui.ILogWindow;
import edu.csus.ecs.pc2.ui.ILoginUI;
import edu.csus.ecs.pc2.ui.IStartupContestDialog;
import edu.csus.ecs.pc2.ui.LoadUIClass;
import edu.csus.ecs.pc2.ui.TextCountDownMessage;
import edu.csus.ecs.pc2.ui.UIPlugin;
import edu.csus.ecs.pc2.ui.UIPluginList;

/**
 * Implementation of InternalContest InternalController.
 * 
 * Run Flow, submit run.
 * <ol>
 * <li>Team: {@link #submitRun(Problem, Language, String)}
 * <li>Server: {@link edu.csus.ecs.pc2.core.PacketHandler#handlePacket(Packet, ConnectionHandlerID)}
 * <li>Server: {@link edu.csus.ecs.pc2.core.model.InternalContest#acceptRun(Run, RunFiles)}
 * <li>Team: {@link edu.csus.ecs.pc2.core.model.IRunListener#runAdded(edu.csus.ecs.pc2.core.model.RunEvent)} RunEvent action is: {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#ADDED}
 * <li>Client: {@link edu.csus.ecs.pc2.core.model.IRunListener#runAdded(edu.csus.ecs.pc2.core.model.RunEvent)} RunEvent action is: {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#ADDED}
 * </ol>
 * Check out run
 * <ol>
 * <li>Judge: {@link #checkOutRun(Run, boolean)}
 * <li>Server: {@link edu.csus.ecs.pc2.core.PacketHandler#requestRun(Packet, Run, ClientId)}
 * <li>Judge and clients: {@link edu.csus.ecs.pc2.core.model.IRunListener#runChanged(edu.csus.ecs.pc2.core.model.RunEvent)}, check {@link edu.csus.ecs.pc2.core.model.RunEvent#getSentToClientId()} to
 * learn if you are the judge/client to get the run. RunEvent action is: {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#CHECKEDOUT_RUN}
 * </ol>
 * Submit Judgement
 * <ol>
 * <li>Judge: {@link #submitRunJudgement(Run, JudgementRecord, RunResultFiles)}
 * <li>Server: {@link edu.csus.ecs.pc2.core.PacketHandler#judgeRun(Run, JudgementRecord, RunResultFiles, ClientId)}
 * <li>Team: {@link edu.csus.ecs.pc2.core.model.IRunListener#runChanged(edu.csus.ecs.pc2.core.model.RunEvent)} if {@link Run#isSendToTeams()} set true. RunEvent action is:
 * {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#CHANGED}
 * <li>Clients: {@link edu.csus.ecs.pc2.core.model.IRunListener#runChanged(edu.csus.ecs.pc2.core.model.RunEvent)} RunEvent action is: {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#CHANGED}
 * </ol>
 * Cancel Run
 * <ol>
 * <li>Judge: {@link #cancelRun(Run)}
 * <li>Server: {@link edu.csus.ecs.pc2.core.PacketHandler#cancelRun(Packet, Run, ClientId)}
 * <li>Team: n/a
 * <li>Judge/Clients: {@link edu.csus.ecs.pc2.core.model.IRunListener#runChanged(edu.csus.ecs.pc2.core.model.RunEvent)}. RunEvent action is:
 * {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#RUN_AVAILABLE}
 * </ol>
 * <P>
 * 
 * @author pc2@ecs.csus.edu
 */
public class InternalController implements IInternalController, ITwoToOne, IBtoA {


    private static final String INI_FILENAME_OPTION_STRING = "--ini";

    /**
     * Override profile option.
     * 
     */
    private static final String PROFILE_OPTION_STRING = "--profile";
    
    private static final String LOAD_OPTION_STRING = "--load";
    
    private static final String LOAD_EF_JUDGEMENTS = "--addefjs";

    private static final String FILE_OPTION_STRING = "-F";

    private static final String NO_GUI_OPTION_STRING = "--nogui";
    
    private boolean haltOnFatalError = true;

    /**
     * InternalContest data.
     */
    private IInternalContest contest;

    /**
     * Transport.
     */
    private ITransportManager connectionManager;

    private Vector<IPacketListener> packetListenerList = new Vector<IPacketListener>();

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

    private static final String DEBUG_OPTION_STRING = "--debug";

    private static final String LOGIN_OPTION_STRING = "--login";
    
//    private static final String LOAD_YAML_OPTION_STRING = "--loadyaml";

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
     * Key in the .ini that indicates whether this site wants to be proxied.
     * 
     * Example:  server.proxyme=true
     */
    private static final String PROXY_ME_SERVER_KEY = "server.proxyme";

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

    /**
     * The connection handle for the server this server logged into.
     */
    private static ConnectionHandlerID remoteServerConnectionHandlerID = null;

    private ParseArguments parseArguments = new ParseArguments();

    private boolean contactingRemoteServer = true;

    private boolean usingMainUI = true;

    private PacketArchiver packetArchiver = null;

    // SOMEDAY change this to UIPlugin
    /*
     * Note: Difficulty with changing LoginFrame to UIPlugin, there is no way to setVisible(false) a UIPlugin or make the GUI cursor change for a UIPlugin. dal.
     */
    private ILoginUI loginUI;

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

    private static final String LOG_WINDOW_GUI_CLASS = "edu.csus.ecs.pc2.ui.LogWindow";

    private String logWindowClassName = LOG_WINDOW_GUI_CLASS;

    private static final String STARTUP_DIALOG_GUI_CLASS = "edu.csus.ecs.pc2.ui.server.StartupContestDialog";

    private static final String LOGIN_UI_GUI_CLASSNAME = "edu.csus.ecs.pc2.ui.LoginFrame";

    private static final String COUNTDOWN_UI_CLASSNAME = "edu.csus.ecs.pc2.ui.CountDownMessage";

    private static final String PORT_OPTION_STRING = "--port";

    /**
     * Remote server to login to.
     */
    private static final String REMOTE_SERVER_OPTION_STRING = "--remoteServer";

    /**
     * Proxy this server, used with {{@link #REMOTE_SERVER_OPTION_STRING}}
     */
    private static final String PROXYME_OPTION_STRING = "--proxyme";

    private static final Object FIRST_SERVER_OPTION_STRING = "--first";

    private String loginClassName = LOGIN_UI_GUI_CLASSNAME;

    private String startupDialogClassName = STARTUP_DIALOG_GUI_CLASS;
    
    private String countdownClassName = COUNTDOWN_UI_CLASSNAME;

    private IStartupContestDialog startDialog;

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

    private UIPluginList pluginList = new UIPluginList();

    // SOMEDAY fix so theProfile is only populated from the ProfileManager
    private Profile theProfile = null;

    private ILogWindow logWindow = null;

    private RunSubmitterInterfaceManager runSubmitterInterfaceManager = new RunSubmitterInterfaceManager();
    
    /**
     * The object used to control contest auto-starting.
     */
    private AutoStarter autoStarter;

    public InternalController(IInternalContest contest) {
        super();
        setContest(contest);
    }

    public void sendToLocalServer(Packet packet) {

        if (isThisServer(packet.getSourceId()) && isServer()) {
            ConnectionHandlerID connectionHandlerID = contest.getConnectionHandleID(contest.getClientId());
            processPacket(packet, connectionHandlerID);
            log.info("Loopback send packet to server " + packet);
            return;
        } else {
            try {
                log.info("Sending packet to server " + packet);
                if (contest.getProfile() != null) {
                    packet.setContestIdentifier(contest.getContestIdentifier().toString());
                }
                connectionManager.send(packet);
                outgoingPacket(packet);

            } catch (TransportException e) {
                info("Unable to send to Server  " + packet);
                e.printStackTrace();
            }
            log.info("Sent    packet to server " + packet);
        }
    }

    private boolean isThisServer(ClientId sourceId) {
        if (isServer(sourceId)) {
            return sourceId.getSiteNumber() == contest.getSiteNumber();
        }
        return false;
    }

    private void sendToClient(ConnectionHandlerID connectionHandlerID, Packet packet) {
        info("sendToClient (send) " + packet.getDestinationId() + " " + packet + " " + connectionHandlerID);
        try {
            packet.setContestIdentifier(contest.getContestIdentifier().toString());
            connectionManager.send(packet, connectionHandlerID);
            outgoingPacket(packet);

        } catch (TransportException e) {
            info("Unable to send to " + connectionHandlerID + " packet " + packet);
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param inSiteNumber site number for client to send to
     * @param packet info to send
     */
    public void sendToRemoteServer(int inSiteNumber, Packet packet) {

        if (packet.getSourceId() != null && (packet.getOriginalSourceId().getSiteNumber() == inSiteNumber)) {
            info("sendToRemoteServer packet from same site as sending to, skipping");
            return;
        }
        Site remoteSite = lookupRemoteServer(inSiteNumber);
        int siteNumber = remoteSite.getSiteNumber();
        
        ClientId clientId = new ClientId(siteNumber, Type.SERVER, 0);
        
        ConnectionHandlerID connectionHandlerID = contest.getConnectionHandleID(clientId);
        info("sendToRemoteServer " + clientId + " at "+siteNumber+" " + packet + " " + connectionHandlerID);

        Type type = packet.getSourceId().getClientType();
        if ((!type.equals(Type.ADMINISTRATOR)) && (!type.equals(Type.SERVER))) {
            log.log(Log.WARNING, "Unexpected User sent packet to other (" + siteNumber + ") site.  " + packet);
        }

        if (connectionHandlerID != null) {

            try {
                packet.setContestIdentifier(contest.getContestIdentifier().toString());
                connectionManager.send(packet, connectionHandlerID);
                outgoingPacket(packet);

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
                    logWarning("Unable to send packet to " + toClientId + " not logged in.  Packet saved in: " + packetArchiver.getLastArchiveFilename());
                    sendMessage(Area.OTHER, "Unable to send packet to " + toClientId + " not logged in. " + packet);
                } catch (Exception e) {
                    logWarning("Unable to send packet to " + toClientId + " could not save packet", e);
                    sendMessage(Area.OTHER, "Unable to send packet to " + toClientId + " not logged in. " + packet, e);
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

    private void sendMessage(Area area, String message) {
        Packet messPacket = PacketFactory.createMessage(getServerClientId(), PacketFactory.ALL_SERVERS, area, message);
        sendToServers(messPacket);
    }

    private void sendMessage(Area area, String message, Exception ex) {
        Packet messPacket = PacketFactory.createMessage(getServerClientId(), PacketFactory.ALL_SERVERS, area, message, ex);
        sendToServers(messPacket);
    }

    protected void sendToServersAndAdmins(Packet packet) {
        if (isServer()) {
            sendToAdministrators(packet);
            sendToServers(packet);
        } else {
            Exception ex = new Exception("User " + packet.getSourceId() + " tried to send packet to judges and others");
            logWarning("Warning - tried to send packet to others (as non server) " + packet, ex);
        }
    }

    public void submitRun(Problem problem, Language language, String filename, SerializedFile[] otherFiles) throws Exception {
        submitRun(problem, language, filename, otherFiles, 0, 0);
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
        } else if (loginName.startsWith("feeder") && loginName.length() > 6) {
            int number = getIntegerValue(loginName.substring(6));
            return new ClientId(defaultSiteNumber, Type.FEEDER, number);
        } else if (loginName.startsWith("eventfeed") && loginName.length() > 8) {
            int number = getIntegerValue(loginName.substring(8));
            return new ClientId(defaultSiteNumber, Type.FEEDER, number);
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
        } else if (loginName.startsWith("ef") && loginName.length() > 2) {
            int number = getIntegerValue(loginName.substring(2));
            return new ClientId(defaultSiteNumber, Type.FEEDER, number);
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

        throw new SecurityException("No such account \"" + loginName + "\"");

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
     * @throws SecurityException 
     *              if a login is attempted when the contest is not running or if the login is from a server
     *              and this is a Client (servers can only log in to other servers)
     */
    public void login(String id, String password) {

        if (!isStarted) {
            // SOMEDAY get rid of this by not allowing this condition dal
            throw new SecurityException("Invalid sequence, must call start(String[]) method before login(String, String).");
        }
        ClientId clientId = loginShortcutExpansion(0, id);

        startLog(getBaseProfileDirectoryName(Log.LOG_DIRECTORY_NAME), stripChar(clientId.toString(), ' '), id, clientId.getName());
        connectionManager.setLog(log);

        if (password.length() < 1) {
            password = clientId.getName(); // Joe password.
            if (clientId.getClientType().equals(Type.SERVER)) {
                password = "site" + clientId.getSiteNumber();
            }
        }

        if (clientId.getClientType().equals(Type.SERVER)) {

            if (!serverModule) {
                SecurityException securityException = new SecurityException("Cannot login as server, check logs");
                getLog().log(Log.WARNING, "Cannot login as server, must start this module with --server command line option");
                throw securityException;
            }

            if (isContactingRemoteServer()) {

                // remoteHostName and remoteHostPort set using huh

                String contactInfo = remoteHostName + ":" + remoteHostPort;

                info("Contacting " + remoteHostName + ":" + remoteHostPort);
                try {
                    remoteServerConnectionHandlerID = connectionManager.connectToServer(remoteHostName, remoteHostPort);
                } catch (TransportException e) {
                    info("** ERROR ** Unable to contact server at " + contactInfo);
                    info("Server at " + contactInfo + " not started or contacting wrong host or port ?");
                    info("Transport Exception ", e);
                    throw new SecurityException("Unable to contact server at " + contactInfo + " (server not started?)");
                }

                info("Contacted using connection id " + remoteServerConnectionHandlerID);
                
                boolean loggingInSiteRequestsToBeProxy = getBooleanValue(getINIValue(PROXY_ME_SERVER_KEY), false);
                
                if (parseArguments.isOptPresent(PROXYME_OPTION_STRING)){
                    loggingInSiteRequestsToBeProxy = true;
                }
                
                sendLoginRequestFromServerToServer(connectionManager, remoteServerConnectionHandlerID, clientId, password, loggingInSiteRequestsToBeProxy);

            } else {

                contest.setSiteNumber(clientId.getSiteNumber());
                log.log(Log.DEBUG, "Site Number is set as " + contest.getSiteNumber() + " (0 means unset)");

                clientId = authenticateFirstServer(clientId.getSiteNumber(), password);

                try {
                    connectionManager.accecptConnections(port);
                    info("Started Server Transport listening on " + port);
                } catch (Exception e) {
                    fatalError("Port " + port + " in use, server already running?", e);
                }
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
     * For input string return boolean value.
     * 
     * Does a trim and case in-sensitive match on the list: yes, no, true, false
     * 
     * @param string true/false string/value
     * @param defaultBoolean if matches none in list returns this values
     */
    public boolean getBooleanValue(String string, boolean defaultBoolean) {

        boolean value = defaultBoolean;

        if (string != null && string.length() > 0) {
            string = string.trim();
            if ("yes".equalsIgnoreCase(string)) {
                value = true;
            } else if ("no".equalsIgnoreCase(string)) {
                value = false;
            } else if ("true".equalsIgnoreCase(string)) {
                value = true;
            } else if ("false".equalsIgnoreCase(string)) {
                value = false;
            }
        }

        return value;
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
            contest.setCommandLineArguments(parseArguments);
            this.contest = contest;
        }

        public IInternalController getController() {
            return controller;
        }

        public void setController(IInternalController controller) {
            this.controller = controller;
        }

        public void loginAdded(LoginEvent event) {
            // no action

        }

        public void loginRemoved(LoginEvent event) {
            // no action

        }

        public void loginDenied(LoginEvent event) {
            securityException = new SecurityException("Login denied " + event.getMessage());
        }

        public void loginRefreshAll(LoginEvent event) {
            // no action
        }
    }

    public IInternalContest clientLogin(IInternalContest internalContest, String loginName, String password) throws Exception {

        if (!isStarted) {
            // SOMEDAY review this message
            throw new SecurityException("Invalid sequence, must call start(String[]) method before login(String, String).");
        }

        if (connectionManager == null) {
            isStarted = false;
            throw new Exception("Unable to contact server at " + getHostContacted() + ":" + getPortContacted() + " (server not started?)");
        }

        ClientId clientId = loginShortcutExpansion(0, loginName);

        startLog(null, stripChar(clientId.toString(), ' '), loginName, clientId.getName());
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
                if (curTimeMS > endTimeMS) {
                    info("Login failed - timed out, server at " + remoteHostName + ":" + remoteHostPort + " as " + clientId);
                    throw new SecurityException("Login failed - timed out");
                }
            }

            return temporaryClientUI.getContest();
        }
    }

    public void initializeStorage(IStorage storage) {
        contest.setStorage(storage);
        packetArchiver = new PacketArchiver(storage, getBaseProfileDirectoryName("packets"));

    }

    public void initializeServer(IInternalContest inContest) throws IOException, ClassNotFoundException, FileSecurityException {

        if (inContest.getSites().length == 0) {

            if (inContest.getSiteNumber() == 0) {
                inContest.setSiteNumber(1);
            }
            info("initializeServer STARTED this site as Site " + inContest.getSiteNumber());

            if (inContest.getContestPassword() == null) {

                if (usingGUI) {
                    startDialog = (IStartupContestDialog) loadUIClass(startupDialogClassName);
                    startDialog.setVisible(true);
                    String password = startDialog.getContestPassword();
                    inContest.setContestPassword(password);
                    theProfile = startDialog.getProfile();
                } else {

                    if (!isContactingRemoteServer()) {
                        fatalError("The contest password must be specified on the command line");
                    }
                }
            }

            String baseDirectoryName = getBaseProfileDirectoryName("db." + inContest.getSiteNumber());
            FileSecurity fileSecurity = new FileSecurity(baseDirectoryName);
            initializeStorage(fileSecurity);

            try {
                fileSecurity.verifyPassword(inContest.getContestPassword().toCharArray());

            } catch (FileSecurityException fileSecurityException) {
                if (fileSecurityException.getMessage().equals(FileSecurity.KEY_FILE_NOT_FOUND)) {

                    try {
                        fileSecurity.saveSecretKey(inContest.getContestPassword().toCharArray());
                    } catch (Exception e) {
                        StaticLog.getLog().log(Log.SEVERE, "FATAL ERROR ", e);
                        fatalError("Unable to save contest password, " + e.getMessage() + " check logs");
                    }
                } else {
                    StaticLog.getLog().log(Log.SEVERE, "FATAL ERROR ", fileSecurityException);
                    fatalError("Invalid contest password");
                }
            } catch (Exception e) {
                StaticLog.getLog().log(Log.SEVERE, "FATAL ERROR 3 ", e);
                fatalError("Exception while verifying contest password " + e.getMessage() + " check logs", e);
            }
        }

        ProfileManager manager = new ProfileManager();

        boolean loadedConfiguration = readConfigFromDisk(inContest.getSiteNumber());

        if (!loadedConfiguration) {
            // No configuration on disk, initialize settings.

            log.info("initializing controller with default settings");

            if (inContest.getSite(1) == null) {
                Site site = createFirstSite(inContest.getSiteNumber(), "localhost", port);
                inContest.addSite(site);
            }

            inContest.initializeStartupData(inContest.getSiteNumber());

            inContest.initializeSubmissions(inContest.getSiteNumber());

            loadJudgements();

            if (inContest.getGeneralProblem() == null) {
                inContest.setGeneralProblem(new Problem("General"));
            }

            if (inContest.getProfile() == null) {
                inContest.setProfile(theProfile);
            }

            info("initialized controller Site " + inContest.getSiteNumber());
            
            handleCDPLoad(true);
            
            dumpAutoStartInformation(contest.getContestInformation());
            
            try {
                manager.mergeProfiles(inContest);
            } catch (Exception e) {
                logException(e);
            }

            inContest.storeConfiguration(getLog());
        } else {
            if (saveCofigurationToDisk) {
                inContest.initializeSubmissions(inContest.getSiteNumber());
            }
            info("Loaded configuration from disk");

            try {
                manager.mergeProfiles(inContest);
            } catch (Exception e) {
                logException(e);
            }

            if (saveCofigurationToDisk) {
                // save newly merged profiles
                inContest.storeConfiguration(getLog());
            }
            handleCDPLoad(false);
        }

        theProfile = inContest.getProfile();

        try {
            if (evaluationLog == null) {
                String logDirectory = getBaseProfileDirectoryName(Log.LOG_DIRECTORY_NAME);
                Utilities.insureDir(logDirectory);
                // this not only opens the log but registers this class to handle all run events.
                evaluationLog = new EvaluationLog(logDirectory + File.separator + "evals.log", inContest, this);
                evaluationLog.getEvalLog().println("# Log opened " + new Date());
                info("evals.log is opened at " + logDirectory);
            }
        } catch (Exception e) {
            getLog().log(Log.WARNING, "Exception creating evals.log ", e);
        }
        
        updateAutoStartInformation(inContest, this);

    }

    private void insureProfileDirectory(Profile profile) {

        String profileDirectory = profile.getProfilePath();

        if (!new File(profileDirectory).isDirectory()) {
            new File(profileDirectory).mkdirs();
        }
    }

    protected void loadJudgements() {

        if (!isContactingRemoteServer()) {

            if (contest.getJudgements().length == 0) {

                if (loadedJudgementsFromIni(Constants.JUDGEMENT_INIT_FILENAME)) {
                    info("Loaded judgements from " + Constants.JUDGEMENT_INIT_FILENAME);
                } else {
                    info(Constants.JUDGEMENT_INIT_FILENAME + " not found.  Loading default judgements");
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
    protected boolean loadedJudgementsFromIni(String filename) {

        if (new File(filename).exists()) {

            String[] lines = Utilities.loadINIFile(filename);

            if (lines == null || lines.length == 0) {
                return false;
            }

            Judgement judgement = new Judgement("Yes", Judgement.ACRONYM_ACCEPTED);
            contest.addJudgement(judgement);
            int offset = contest.getJudgements().length;

            for (String judgementName : lines) {
                String waNumber = String.format("%03d", offset++);
                judgement = new Judgement("No - " + judgementName, Judgement.ACRONYM_WRONG_ANSWER+waNumber);
                contest.addJudgement(judgement);
            }

            return true;
        }
        return false;
    }

    protected void loadDefaultJudgements() {

        String[] judgementNames = { //
                "Yes", // 
                "No - Compilation Error", // 
                "No - Run-time Error", // 
                "No - Time Limit Exceeded", // 
                "No - Wrong Answer", // 
                "No - Excessive Output", // 
                "No - Output Format Error", // 
                "No - Other - Contact Staff" //
        };
        String [] judgementAcronyms = {
                Judgement.ACRONYM_ACCEPTED, // 
                Judgement.ACRONYM_COMPILATION_ERROR, //
                Judgement.ACRONYM_RUN_TIME_ERROR, //
                Judgement.ACRONYM_TIME_LIMIT_EXCEEDED, //
                Judgement.ACRONYM_WRONG_ANSWER, //
                Judgement.ACRONYM_EXCESSIVE_OUTPUT, //
                Judgement.ACRONYM_OUTPUT_FORMAT_ERROR, //
                Judgement.ACRONYM_OTHER_CONTACT_STAFF, //
        };
        
        int i = 0;
        for (String judgementName : judgementNames) {
            Judgement judgement = new Judgement(judgementName, judgementAcronyms[i]);
            contest.addJudgement(judgement);
            i++;
        }
    }

    private ClientId authenticateFirstServer(int siteNum, String password) {

        try {
            initializeServer(contest);
            // only the 1st server needs these
            // XXX is there a better place to initialize them?
            contest.setupDefaultCategories();
        } catch (IOException e) {
            // SOMEDAY Handle exception better
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // SOMEDAY Handle exception better
            e.printStackTrace();
        } catch (FileSecurityException e) {
            // SOMEDAY Handle exception better
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

            getLog().log(Log.INFO, "INIFile File location: " + IniFile.getIniFileURL());
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
     * @param proxyMe if true, this site acts as a proxy for the site logging in.
     */
    private void sendLoginRequestFromServerToServer(ITransportManager manager, ConnectionHandlerID targetConnectionHandlerID, ClientId clientId, String password, boolean proxyMe) {
        
        
        try {
            info("sendLoginRequestFromServerToServer ConId start - sending from " + clientId);
            ClientId serverClientId = new ClientId(0, Type.SERVER, 0);
            String joeLoginName = password;
            Packet loginPacket = PacketFactory.createLoginRequest(clientId, joeLoginName, password, serverClientId, proxyMe);
            manager.send(loginPacket, targetConnectionHandlerID);
            info("sendLoginRequestFromServerToServer ConId end - packet sent.");
        } catch (TransportException e) {
            info("Exception sendLoginRequestFromServerToServer ", e);
        }
        
        
//        info("sendLoginRequestFromServerToServer ConId start - sending from " + clientId + " proxyMe = " + proxyMe);
//        ClientId serverClientId = new ClientId(0, Type.SERVER, 0);
//        String joeLoginName = password;
//        try {
//            manager.send(loginPacket, targetConnectionHandlerID);
//            info("sendLoginRequestFromServerToServer ConId end - packet sent.");
//        } catch (Exception e) {
//            info("sendLoginRequestFromServerToServer ConId end - packet not sent.", e);
//        }
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

        // SOMEDAY SECURITY code check the input connection to insure they are valid connection
        info("receiveObject start got " + object);

        try {

            if (object instanceof Packet) {

                Packet packet = (Packet) object;
                ClientId clientId = packet.getSourceId();

                info("receiveObject " + packet);

                incomingPacket(packet);

                if (PacketType.Type.AUTO_REGISTRATION_LOGIN_REQUEST.equals(packet.getType())) {

                    packetArchiver.writeNextPacket(packet);

                    String loginName = PacketFactory.getStringValue(packet, PacketFactory.LOGIN);

                    if (isEnableAutoRegistration()) {

                        handleAutoRegistration(packet, connectionHandlerID);

                    } else {
                        info("Client attempted to auto register, auto registration not enabled, tried to use '" + loginName + "' " + connectionHandlerID);
                        String message = "Auto Registration not allowed";
                        sendLoginFailure(packet.getSourceId(), connectionHandlerID, message);
                    }

                } else if (packet.getType().equals(PacketType.Type.LOGIN_REQUEST)) {
                    String password = PacketFactory.getStringValue(packet, PacketFactory.PASSWORD);
                    boolean requestProxy = false;
                    Boolean requestedProxy = PacketFactory.getBooleanValue(packet, PacketFactory.REQUEST_LOGIN_AS_PROXY);
                    if (requestedProxy != null) {
                        requestProxy = requestedProxy.booleanValue();
                    }
                    
                    try {

                        /**
                         * Login request from client or other server. When this block is done, they are logged in and a login success is sent to them.
                         */

                        packetArchiver.writeNextPacket(packet);

                        if (clientId.getSiteNumber() == ClientId.UNSET) {
                            clientId = new ClientId(contest.getSiteNumber(), clientId.getClientType(), clientId.getClientNumber());
                        }
                        attemptToLogin(clientId, password, connectionHandlerID);
                        
                        if (requestProxy){
                            if (isServer(packet.getSourceId())){
                                /**
                                 * Bug 1246 - requesting to be a proxy
                                 */
                                sendSiteUpdateSetProxy (packet.getSourceId(), contest.getSiteNumber());
                            }
                        }
                        
                        sendLoginSuccess(clientId, connectionHandlerID);

                        // Send login notification to users.

                        Packet loginConfirmedPacket = PacketFactory.createLogin(contest.getClientId(), PacketFactory.ALL_SERVERS, connectionHandlerID, clientId, getClientSettings(clientId));
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

                    String remoteContestId = packet.getContestIdentifier();
                    String localContestId = contest.getContestIdentifier();

                    if (remoteContestId.equals(localContestId)) {
                        processPacket(packet, connectionHandlerID);
                    } else {

                        switch (packet.getType()) {
                            case UPDATE_CLIENT_PROFILE:
                            case REQUEST_SERVER_STATUS:
                            case MESSAGE:
                            case REQUEST_REMOTE_DATA:
                            case SERVER_STATUS:
                                processPacket(packet, connectionHandlerID);
                                break;

                            default:
                                /**
                                 * Non-matching contest Ids - do not process packet
                                 */

                                contest.addMessage(Area.INCOMING_PACKET, getServerClientId(), getServerClientId(), "Packet contestId does not match for " + packet + " local:" + localContestId
                                        + " remote:" + remoteContestId);

                                logWarning("Packet contestId does not match for " + packet + " local:" + localContestId + " remote:" + remoteContestId);

                                processPacket(packet, connectionHandlerID);
                                break;
                        }
                    }

                } else if (contest.isRemoteLoggedIn(packet.getSourceId()) && clientId.getClientType().equals(Type.ADMINISTRATOR)) {
                    // this is needed for site1 admin to create accounts on site2
                    // securityCheck does not always have ConnectionHandlerID list of remotely logged in clients
//                    securityCheck(packet, connectionHandlerID);
                    processPacket(packet, connectionHandlerID);
                } else {
                    
                    if (clientId.getClientType().equals(Type.SERVER)) {
                        // Packet from a server.

                        if (packet.getType() == PacketType.Type.LOGIN_FAILED) {
                            
                            handleServerLoginFailure(packet);
                            
                        } else if (packet.getType().equals(PacketType.Type.LOGIN_SUCCESS)) {
                            
                            /**
                             * The current server has successfully logged into a remote server.
                             */

                            // SOMEDAY SECURITY add a security check that this connection id matches the one we
                            // sent the login request packet to. If we don't add this, then some other
                            // server could send us a LOGIN_SUCCESS packet, which would be bad. Highly
                            // unlikely but potentially bad.

                            // Add the other (server we logged into) into our local logged in list.
                            loginServer(clientId, connectionHandlerID);
                            
                            /**
                             * Since this module is not logged in, this packet should only be a LOGIN_SUCCESS from a server we initially logged into,
                             * aka the remoteServer.  
                             */
                            
                            if (connectionHandlerID.equals(remoteServerConnectionHandlerID)){
                                
                                /**
                                 * Only accept/change config data on this site if the login success is from the server that this site connected to initially.
                                 */
                                info("Loading contest settings from remoteServer " + clientId + " @ " + connectionHandlerID);

                                // Add data from packet into contest and sync all runs
                                processPacket(packet, connectionHandlerID);
                                
                            } else {
                                
                                /**
                                 * This should happen when a server logs into another server that is not its remoeteServer (initial server logged into).
                                 * 
                                 * This is where the remote site's data is synced on this server.
                                 */
                                
                                info("Updating this server's settings from remote server specific settings from " + clientId + " @ " + connectionHandlerID);
 
                                packetHandler.loadSettingsFromRemoteServer(new ContestLoader(), packet, connectionHandlerID);
                      
                            }

                        } else if (contest.isLocalLoggedIn(clientId) && packet.getType().equals(PacketType.Type.LOGIN)) {
                            /**
                             * A user has logged into another remote server 
                             */
                            // on site 2 login to site 1, site 2 reports LOGIN on login to site1
                            processPacket(packet, connectionHandlerID);
                        } else if (packet.getType().equals(PacketType.Type.LOGIN) && contest.getSite(contest.getSiteNumber()).hasProxy() && packet.getDestinationId().getSiteNumber() == 0) {
                            /**
                             * This is probably coming from our proxy 
                             */
                            processPacket(packet, connectionHandlerID);
                        } else if (contest.isLocalLoggedIn(packet.getDestinationId()) && contest.getSite(contest.getSiteNumber()).getMyProxy() == packet.getDestinationId().getSiteNumber()) {
                            /**
                             * This is coming from our proxy 
                             */
                            processPacket(packet, connectionHandlerID);
                        } else if (contest.isRemoteLoggedIn(packet.getSourceId()) && packet.getSourceId().getClientType() == ClientType.Type.SERVER) {
                            /*
                             * This is coming from a proxied server
                             */
                            processPacket(packet, connectionHandlerID);
                        } else {
                            System.err.println("Security Violation Packet from non-logged in server " + packet);
                            info("Note: Security violation in packet: Packet from non-logged in server "+ packet);
                            log.info("Security Violation for packet " + packet);
                       }
                        return;
                    } else {
                        System.err.println("Security violation Packet from non-logged in server " + packet);
                        info("Note: security violation in packet: Packet from non-logged in server "+packet);
                        log.info("Security Violation for packet " + packet);
                    }
                }
            } else {
                // SOMEDAY code archive packet, send security violation to notification system.

                info("receiveObject(S,C): Unsupported class received: " + object);
            }

        } catch (Exception e) {

            // SOMEDAY code archive packet, send security violation to notification system.

            info("Exception in receiveObject(S,C): " + e.getMessage(), e);
            info("Exception in receiveObject ", e);
        }
        info("receiveObject end   got " + object.getClass().getName());
    }

    /**
     * Assign proxy, send updated Site to all connected sites.
     * 
     * @param siteToBeProxied client/site to be proxied
     * @param proxySiteNumber proxy site for siteToBeProxied
     */
    private void sendSiteUpdateSetProxy(ClientId siteToBeProxied, int proxySiteNumber) {
        
        // assign proxy 
         Site site = contest.getSite(siteToBeProxied.getSiteNumber());
         site.setMyProxy(proxySiteNumber);
         
         updateSite(site);
        
    }

    private String[] removeFirstElement(String[] stringArray) {
        String[] newArray = new String[stringArray.length - 1];
        System.arraycopy(stringArray, 1, newArray, 0, newArray.length);
        return newArray;
    }

    /**
     * Handle client attempt to auto register.
     * 
     * @param packet
     * @param connectionHandlerID
     */
    private void handleAutoRegistration(Packet packet, ConnectionHandlerID connectionHandlerID) {

        String autoLoginInformation = PacketFactory.getStringValue(packet, PacketFactory.AUTO_REG_REQUEST_INFO);

        String delimit = PacketType.FIELD_DELIMIT;
        String[] fields = autoLoginInformation.split(delimit);

        String errorMessage = null;

        if (fields.length == 0) {
            errorMessage = "Missing team name, enter a team name";
        } else if (fields.length == 1) {
            errorMessage = "Missing team member name(s), enter team member name";
        } else {
            String teamName = fields[0];
            String[] teamMemberNames = removeFirstElement(fields);
            Account account = contest.autoRegisterTeam(teamName, teamMemberNames, null);

            try {
                Packet newAccountPacket = PacketFactory.createAutoRegReply(getServerClientId(), account.getClientId(), account);
                sendToClient(connectionHandlerID, newAccountPacket);

                contest.storeConfiguration(getLog());
                Packet newAccountsPacket = PacketFactory.createAddSetting(contest.getClientId(), PacketFactory.ALL_SERVERS, account);
                sendToJudgesAndOthers(newAccountsPacket, true);
            } catch (Exception e) {
                logException(e);
            }
        }

        if (errorMessage != null) {
            info("Client attempted to auto register, auto registration not enabled, tried to use '" + autoLoginInformation + "' " + connectionHandlerID);
            String message = "Auto Registration not allowed";
            sendLoginFailure(packet.getSourceId(), connectionHandlerID, message);
        }
    }

    public void sendToJudgesAndOthers(Packet packet, boolean sendToServers) {

        if (isServer()) {
            // If I am a server forward to clients on this site.

            sendToAdministrators(packet);
            sendToJudges(packet);
            sendToScoreboards(packet);
            sendToFeeders(packet);
            if (sendToServers) {
                sendToServers(packet);
            }
        } else {
            info("Warning - tried to send packet to others (as non server) " + packet);
            Exception ex = new Exception("User " + packet.getSourceId() + " tried to send packet to judges and others");
            getLog().log(Log.WARNING, "Warning - tried to send packet to others (as non server) " + packet, ex);
        }
    }

    private boolean isEnableAutoRegistration() {
        try {
            return contest.getContestInformation().isEnableAutoRegistration();
        } catch (Exception e) {
            getLog().log(Log.WARNING, "Unable to determine auto reg value", e);
            return false;
        }
    }

    /**
     * Creates and saves a ClientSettings.
     * 
     * @param clientId
     * @return
     */
    private ClientSettings getClientSettings(ClientId clientId) {

        ClientSettings clientSettings = contest.getClientSettings(clientId);

        if (clientSettings == null) {
            clientSettings = new ClientSettings(clientId);
            clientSettings.put(ClientSettings.LOGIN_DATE, new Date().toString());
            contest.addClientSettings(clientSettings);
            try {
                contest.storeConfiguration(log);
            } catch (IOException e) {
                info("Exception saving ClientSettings for " + clientId, e);
            } catch (ClassNotFoundException e) {
                info("Exception saving ClientSettings for " + clientId, e);
            } catch (FileSecurityException e) {
                info("Exception saving ClientSettings for " + clientId, e);
            }
        }

        return clientSettings;
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

    boolean isLoggedIn(ClientId clientId) {
        return contest.isRemoteLoggedIn(clientId) || contest.isLocalLoggedIn(clientId);
    }

    private void securityCheck(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException {

        if (!isLoggedIn(packet.getSourceId())) {
            log.info("Security Violation for packet " + packet);
            log.info("User " + packet.getSourceId() + " not logged in ");
            // SOMEDAY code throw an exception if the security fails.
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

            throw new ContestSecurityException(packet.getSourceId(), connectionHandlerID, connectionHandlerID.toString() + " should be " + connectionHandlerIDAuthen);

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

    /**
     * Handle incoming invalid login and message.
     * 
     * @param packet
     */
    private void handleServerLoginFailure(Packet packet) {
        // SOMEDAY rewrite handle this failure better

        try {
            packetArchiver.writeNextPacket(packet);
            log.info("Login failure packet written to " + packetArchiver.getLastArchiveFilename() + " " + packet);
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception trying to write packet ", e);
        }

        String message = PacketFactory.getStringValue(packet, PacketFactory.MESSAGE_STRING);

        if (!usingGUI) {
            fatalError("Login Failed: " + message);
        }

        // SOMEDAY Handle this better via new login code.
        info("Login Failed: " + message);
        info("Login Failure");
        PacketFactory.dumpPacket(System.err, packet, "Login Failed");
        // SOMEDAY output dumppacket to a log

        if (loginUI != null) {
            loginUI.regularCursor();
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

        if (siteNum > contest.getSites().length) {
            throw new SecurityException("No such site (Site " + siteNum + ")");
        } else if (contest.getSites().length > 1) {
            throw new SecurityException("Invalid password for site " + siteNum);
        } else {
            throw new SecurityException("Does not match first site password");
        }

    }

    /**
     * Returns true if the password matches the hash for the override password.
     */
    public static boolean matchOverride(String password) {
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

            if (newSiteNumber == contest.getSiteNumber()) {
                throw new SecurityException("Site " + newSiteNumber + " is already logged in (attempt from secondary site to login as same site a primary site)");
            }

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

                // FIXME throw an security exception when contest Id works/is present

                // throw new ContestSecurityException(packet.getSourceId(), connectionHandlerID, "Packet " + packet.getSourceId() + " does not match contest id " + packet.getContestIdentifier()
                // + " should be " + contest.getContestIdentifier());
            }

            packetHandler.handlePacket(packet, connectionHandlerID);

        } catch (ProfileException profileException) {

            log.log(Level.WARNING, "Error switching profile: " + profileException.getMessage(), profileException);

            if (profileException.getMessage().indexOf("FileSecurityException") != -1) {

                if (profileException.getMessage().indexOf(FileSecurity.FAILED_TO_DECRYPT) != -1) {

                    // Invalid contest password

                    Packet messagePacket = PacketFactory.createMessage(getServerClientId(), packet.getSourceId(), Area.PROFILES, "Invalid contest password");
                    sendToClient(messagePacket);

                    logException("Invalid contest password", profileException);
                } else {
                    Packet messagePacket = PacketFactory.createMessage(getServerClientId(), packet.getSourceId(), Area.PROFILES, "Unable to change profile " + profileException.getMessage());
                    sendToClient(messagePacket);

                    logException("Unable to change profile", profileException);
                }
            } else {
                Packet messagePacket = PacketFactory.createMessage(getServerClientId(), packet.getSourceId(), Area.PROFILES, "Unable to change profile " + profileException.getMessage());
                sendToClient(messagePacket);

                logException("Unable to change profile", profileException);
            }
        } catch (ServerProcessException serverProcessException) {
            Packet messagePacket = PacketFactory.createMessage(contest.getClientId(), PacketFactory.ALL_SERVERS, Area.SERVER_PROCESSING, serverProcessException.getProcessingMessage(),
                    serverProcessException);

            sendToAdministrators(messagePacket);
            sendToServers(messagePacket);

        } catch (ContestSecurityException contestSecurityException) {

            // Security Violation, someone tried to do something they weren't allowed to

            log.log(Log.SEVERE, "SECURITY Violation  " + contestSecurityException.getSecurityMessage() + packet);

            // SOMEDAY code fire trigger in Contest

            contest.newSecurityMessage(packet.getSourceId(), "Security violation", packet.getType().toString(), contestSecurityException);

            // SOMEDAY use sendSecurityMessage method in place of createSecurityMessagePacket and sendToAdministrators, sendToServers
            Packet violationPacket = PacketFactory.createSecurityMessagePacket(contest.getClientId(), PacketFactory.ALL_SERVERS, contestSecurityException.getSecurityMessage(), null,
                    connectionHandlerID, contestSecurityException, packet);

            sendToAdministrators(violationPacket);
            sendToServers(violationPacket);

        } catch (Exception e) {
            info("Exception in processPacket, check logs ", e);
            info("Exception in processPacket for " + packet);
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
     * 
     * @throws IOException
     */
    public void connectionDropped(ConnectionHandlerID connectionHandlerID) {

        // getLog().log(Log.INFO, "connection Dropped for " + connectionHandlerID, new Exception("connection Dropped for " + connectionHandlerID));
        info("connection Dropped for " + connectionHandlerID);
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

    protected void cancelAllClarsByThisJudge(ClientId judgeId) throws ContestSecurityException {
        Clarification[] clars = contest.getClarifications();
        for (int i = 0; i < clars.length; i++) {
            if ((clars[i].getState() == ClarificationStates.BEING_ANSWERED) && (clars[i].getWhoCheckedItOutId().equals(judgeId))) {
                Packet packet = PacketFactory.createUnCheckoutClarification(contest.getClientId(), getServerClientId(), clars[i]);
                try {
                    packetHandler.cancelClarificationCheckOut(packet, null);
                } catch (IOException e) {
                    // SOMEDAY dal Auto-generated catch block
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    // SOMEDAY Handle exception better
                    e.printStackTrace();
                } catch (FileSecurityException e) {
                    // SOMEDAY Handle exception better
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

    protected void cancelAllRunsByThisJudge(ClientId judgeId) {
        ElementId[] runIDs = contest.getRunIdsCheckedOutBy(judgeId);
        for (int i = 0; i < runIDs.length; i++) {
            Run run = contest.getRun(runIDs[i]);
            if (run.getStatus().equals(RunStates.BEING_JUDGED)) {
                ClientId destinationId = new ClientId(run.getSiteNumber(), Type.SERVER, 0);
                Packet packet = PacketFactory.createUnCheckoutRun(judgeId, destinationId, run, judgeId);
                try {
                    packetHandler.cancelRun(packet, run, judgeId, null);
                } catch (IOException e) {
                    // SOMEDAY Handle exception better
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    // SOMEDAY Handle exception better
                    e.printStackTrace();
                } catch (FileSecurityException e) {
                    // SOMEDAY Handle exception better
                    e.printStackTrace();
                }
            }
        }
    }

    public void logoffUser(ClientId clientId) {

        if (isServer() && contest.isLocalLoggedIn(clientId)) {
            // Logged into this server, so we log them off and send out packet.

            /**
             * This is a condition where the ServerView, for instance, logs off a user, there is no need to send a packet to the local server, just log them off locally and send out a logoff packet.
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

        // SOMEDAY code create a packet and send it to servers and admins

        // SOMEDAY code connectionError
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

                incomingPacket(packet);

                PacketFactory.dumpPacket(log, packet, "recieveObject");

                // SOMEDAY code put the server's connection handler id as 4th parameter
                packetHandler.handlePacket(packet, null);
                
            } else {
                info("receiveObject(S) Unsupported class received: " + object.getClass().getName());
            }
        } catch (Exception e) {
            
            error(e.toString(),e);
            
            if (loginUI != null) {
                loginUI.regularCursor();
            }
        }
        info(" receiveObject(S) end   got " + object);
    }

    /**
     * This client lost connection.
     */
    public void connectionDropped() {

        // Connection dropped, countdown and halt client

        if (clientAutoShutdown) {

            shutdown();

            if (contest.getClientId() != null) {
                fatalError("Shutting down PC^2 " + contest.getClientId().getClientType() + " " + contest.getTitle());
            } else {
                fatalError("connectionDropped: shutting down <non-logged in client>");
            }

        } else {

            // Tell API that connection was dropped
            contest.connectionDropped(null);
        }
    }
    
    public void setCountdownClassName(String countdownClassName) {
        this.countdownClassName = countdownClassName;
    }
    
    public String getCountdownClassName() {
        return countdownClassName;
    }
    
    public void setLoginClassName(String loginClassName) {
        this.loginClassName = loginClassName;
    }
    
    public String getLoginClassName() {
        return loginClassName;
    }

    public void setStartupDialogClassName(String startupDialogClassName) {
        this.startupDialogClassName = startupDialogClassName;
    }

    public String getStartupDialogClassName() {
        return startupDialogClassName;
    }   

    private void shutdown() {

        ICountDownMessage countDownMessage = null;
        if (usingGUI) {
            /**
             * GUI countdown
             */
            countDownMessage = (ICountDownMessage) loadUIClass(countdownClassName);
        }

        if (countDownMessage == null) {
            /**
             * Text count down
             */
            countDownMessage = new TextCountDownMessage();
        }
        
        if (contest.getClientId() != null) {
            info("connectionDropped: shutting down " + contest.getClientId());
            countDownMessage.setTitle("Shutting down PC^2 " + contest.getClientId().getClientType() + " " + contest.getTitle());
        } else {
            info("connectionDropped: shutting down <non-logged in client>");
            countDownMessage.setTitle("Shutting down PC^2 Client");
        }
        countDownMessage.setExitOnClose(true);
        countDownMessage.start("Shutting down PC^2 in ", 10);
        
        /**
         * This is needed to allow the countdown timer threads to 
         * actually run, otherwise the JVM ends before the timer starts. 
         */
        sleep (12);
        
    }

    /**
     * Sleep for a number of seconds.
     * @param secs
     */
    private void sleep(int secs) {
        
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }
    
    public void info(String message) {
        if (! isUsingGUI()){
            System.out.println(message);
        }
        log.log(Log.INFO, message);
    }
    
    public void error(String message, Exception ex){
        if (! isUsingGUI()){
            System.err.println(message+" exception "+ex.getMessage());
        }
        log.log(Log.SEVERE, message, ex);
    }

    public void info(String message, Exception exception) {
        
        if (! isUsingGUI()){
            System.out.println(message);
        }
        // HOWTO print thread name to output println
        // System.err.println(Thread.currentThread().getName() + " " + s);
        // System.err.flush();
        // exception.printStackTrace(System.err);
        log.log(Log.INFO, message, exception);
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

        ClientId[] clientIds1 = contest.getLocalLoggedInClients(ClientType.Type.SERVER);
        ClientId[] clientIds2 = contest.getRemoteLoggedInClients(ClientType.Type.SERVER);
        ClientId[] clientIds = Arrays.copyOf(clientIds1, clientIds1.length + clientIds2.length);
        System.arraycopy(clientIds2, 0, clientIds, clientIds1.length, clientIds2.length);

        if (clientIds.length > 0) {
            outgoingPacket(packet);
        }

        int fromSite = -1;
        if (packet != null) {
            if (packet.getOriginalSourceId() != null) {
                fromSite = packet.getOriginalSourceId().getSiteNumber();
            }
        }
        for (ClientId clientId : clientIds) {
            boolean isThisServer = isThisSite(clientId.getSiteNumber());
            // do not send to ourselves and
            // do not send to the site we got the packet
            if (!isThisServer && !(fromSite == clientId.getSiteNumber())) {
                // Send to other servers, after cloning and modifying the dest
                Packet packetClone = PacketFactory.clonePacket(packet.getSourceId(), clientId, packet);
                sendToRemoteServer(clientId.getSiteNumber(), packetClone);
            }
        }
    }

    /**
     * Send packet to all clients  logged into this site that are a particular clienttype
     * 
     * @param packet
     * @param type client type
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
    
    public void sendToFeeders(Packet packet) {
        sendPacketToClients(packet, ClientType.Type.FEEDER);
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
            info("Exception getting port for site "+inSiteNumber, e);
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

            startLog(getBaseProfileDirectoryName(Log.LOG_DIRECTORY_NAME), stripChar(clientId.toString(), ' '), clientId.getName(), clientId.getName());

            boolean isServer = clientId.getClientType().equals(ClientType.Type.SERVER);

            if (isServer && isContactingRemoteServer()) {
                // secondary server logged in, start listening.

                port = getPortForSite(contest.getSiteNumber());

                if (parseArguments.getOptValue(PORT_OPTION_STRING) != null) {
                    String portString = parseArguments.getOptValue(PORT_OPTION_STRING);
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
                            uiPlugin = loadUIClass(uiClassName);
                            if (uiPlugin == null){
                                throw new Exception("Class not loaded "+uiClassName);
                            }
                            info("Loaded UI class " + uiClassName);
                        }
                    }
                    
                    uiPlugin.setContestAndController(contest, this);

                    if (loginUI != null) {
                        loginUI.dispose();
                    }
                }
            } catch (Exception e) {
                fatalError("Error loading UI, check log, (class not found?)  " + e.getMessage(), e);
            }

        } catch (Exception e) {
            // SOMEDAY separate the showing main Frame and listening to port exception messages

            info("Error showing frame or listening to port ", e);
            if (loginUI != null) {
                loginUI.regularCursor();
            }
            contest.loginDenied(clientId, null, e.getMessage() + " (port " + port + ")");

            if (!usingGUI) {
                fatalError(e.getMessage() + " (port=" + port + ")");
            }
        }
    }

    /**
     * Handles --load option, loads CDP.
     * @param allowedToLoadConfig if true will overwrite config with CDP, else does nothing.
     */
    protected void handleCDPLoad(boolean allowedToLoadConfig) {
        
        /**
         * Name of CDP dir or file to be loaded.
         */
        String entryLocation = contest.getCommandLineOptionValue(LOAD_OPTION_STRING);
        
        if (contest.isCommandLineOptionPresent(LOAD_OPTION_STRING)){
            
            if (allowedToLoadConfig){
                
                IContestLoader loader = new ContestSnakeYAMLLoader();
                info("Loading CDP from " + entryLocation);
                
                try {
                    
                    // Load Configuration from CDP
                    
                    loader.initializeContest(contest, new File(entryLocation));
                    
                    info("Loaded CDP/config values from " + entryLocation);
                    
                    File cdpConfigDir = loader.findCDPConfigDirectory(new File(entryLocation));
                    
                    if (saveCofigurationToDisk) {
                        // save newly merged profiles
                        contest.storeConfiguration(getLog());
                    }
                    
                    // Load Submissions from CDP
                    
                    String submissionsDir = entryLocation + File.separator + IContestLoader.SUBMISSIONS_DIRNAME;
                    
                    String eventFeedDirectory = entryLocation + File.separator + IContestLoader.EVENT_FEED_DIRNAME; 
                    String eventFeedfilename = eventFeedDirectory + File.separator + IContestLoader.EVENT_FEED_XML_FILENAME;
                    
                    
                    if (new File(submissionsDir).isDirectory()){
                        
                        if (new File(eventFeedfilename).isFile()){
                            
                            info("Loading event feed runs... ");
                            info("Event feed file is "+eventFeedfilename);
                            
                            LoadRuns loadRuns = new LoadRuns();
                            
                            List<EventFeedRun> eventFeedRuns = loadRuns.loadRunsFromEventFeed(eventFeedfilename);
                            
                            info("Found "+eventFeedRuns.size()+" runs.");
                            
                            info("Validating event feed runs");
                            
                            loadRuns.validateEventFeedRuns(contest, eventFeedRuns);
                            
                            info("Validated "+eventFeedRuns.size()+" event feed runs.");
                            
                            info("Loading "+eventFeedRuns.size()+" event feed runs.");
                            
                            boolean addRuneJudgementsFromEventFeed = contest.isCommandLineOptionPresent(LOAD_EF_JUDGEMENTS);
                            
                            loadRuns.updateContestFromEFRuns(contest, eventFeedRuns, cdpConfigDir.getParent(), addRuneJudgementsFromEventFeed);
                            
                            info("Loaded "+eventFeedRuns.size()+" event feed runs.");
                            if (addRuneJudgementsFromEventFeed){
                                info("Loaded judgments for "+eventFeedRuns.size()+" event feed runs.");
                            } else {
                                info("No judgements were added for "+eventFeedRuns.size()+" event feed runs, all are 'new' runs.");
                            }
                            
                            eventFeedRuns = null;
                            loadRuns = null;
                            
                        } else {
                            info("No submissions loaded, no event feed at "+eventFeedfilename);
                        }
                        
                    } else {
                        info("No submissions loaded, no submissions at "+submissionsDir);
                    }
                    
                    
                    
                } catch (Exception e) {
                    fatalError("Error loading from " + entryLocation, e);
                } finally {
                    loader = null;
                }
            } else {
                
                info("** Not loading (--load) CDP from " + entryLocation+" contest config ALREADY exists. **");
                System.err.println ("Warning: contest configuration already exists; ignoring --load option"); 
                
            }
        }
    }

    /**
     * Dump Auto Start information to log.
     * 
     * @param contestInformation
     */
    private void dumpAutoStartInformation(ContestInformation contestInformation) {

        if (contestInformation == null) {
            info("NO auto start information set/found.");
        } else {
            if (contestInformation.getScheduledStartDate() != null && contestInformation.isAutoStartContest()) {
                info("Will auto start contest at: " + contestInformation.getScheduledStartDate());
            } else {
                info("No auto start.  Date = " + contestInformation.getScheduledStartDate() + " set to start " + contestInformation.isAutoStartContest());
            }
        }
    }

    /**
     * Start the UI.
     */
    public void start(String[] stringArray) {

        /**
         * Saved exception.
         * 
         * If TransportException thrown before UI has been created, save the exception and present it on the UI later.
         */
        TransportException savedTransportException = null;

        String[] requireArguementArgs = { // 
                "--login", //
                "--password", // 
                MAIN_UI_OPTION, 
                REMOTE_SERVER_OPTION_STRING, //
                PORT_OPTION_STRING, //
                LOAD_OPTION_STRING, //
                PROFILE_OPTION_STRING, //
                INI_FILENAME_OPTION_STRING, //
                CONTEST_PASSWORD_OPTION, //
                "--id", // 
                FILE_OPTION_STRING };
        
        parseArguments = new ParseArguments(stringArray, requireArguementArgs);

        if (parseArguments.isOptPresent(DEBUG_OPTION_STRING)){
            parseArguments.dumpArgs(System.out);
        }
        
        contest.setCommandLineArguments(parseArguments);
        
        if (parseArguments.isOptPresent("--server")) {
            if (!isContactingRemoteServer()) {
                theProfile = getCurrentProfile();
                String profilePath = theProfile.getProfilePath();
                insureProfileDirectory(theProfile);
                startLog(profilePath, "pc2.startup", null, null);
            } else {
                startLog(null, "pc2.startup", null, null);
            }
        } else {
            startLog(null, "pc2.startup."+System.currentTimeMillis(), null, null);
        }
        
        handleCommandLineOptions();

        for (String arg : stringArray) {
            if (arg.equals(FIRST_SERVER_OPTION_STRING)) {
                setContactingRemoteServer(false);
            }
        }

        /**
         * if (args DOES NOT contains login/pwd) { String s; if (args contains LoginUI ) { s = args login UI } else { s = pc2 LoginFrame } UIPlugin l = classloader (s); l.setModelAndListener (contest,
         * this); } else { this.login (login,password)
         * 
         */

        log.info("Starting ConnectionManager...");
        if (connectionManager == null){
            /**
             * If connection manager has not been set, create new one.
             */
            connectionManager = new ConnectionManager(log);
        }
        log.info("Started ConnectionManager");

        boolean useIniFile = !parseArguments.isOptPresent("--skipini");

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

            if (exception != null) {
                fatalError("Cannot start PC^2, " + iniName + " cannot be read (" + exception.getMessage() + ")", exception);
            }
        }

        contest.setSiteNumber(0);

        if (useIniFile && (!parseArguments.isOptPresent(INI_FILENAME_OPTION_STRING))) {
            if (IniFile.isFilePresent()) {
                // Only read and load .ini file if it is present.
                new IniFile();
            } else {
                String currentDirectory = Utilities.getCurrentDirectory();
                fatalError("Cannot start PC^2, " + IniFile.getINIFilename() + " file not found in " + currentDirectory);
            }
        }

        // SOMEDAY code add NO_SAVE_OPTION_STRING
        if (parseArguments.isOptPresent("--nosave")) {
            saveCofigurationToDisk = false;
        }

        if (parseArguments.isOptPresent("--server")) {

            info("Starting Server Transport...");
            connectionManager.startServerTransport(this);
            serverModule = true;

            contactingRemoteServer = false;
            setServerRemoteHostAndPort(parseArguments.getOptValue(REMOTE_SERVER_OPTION_STRING));

            if (!isContactingRemoteServer()) {
                theProfile = getCurrentProfile();
                String profilePath = theProfile.getProfilePath();
                insureProfileDirectory(theProfile);
                startLog(profilePath, "pc2.startup", null, null);
            }

            try {
                setServerPort(parseArguments.getOptValue(PORT_OPTION_STRING));
            } catch (NumberFormatException numException) {
                savedTransportException = new TransportException("Unable to parse value after --port '" + parseArguments.getOptValue(PORT_OPTION_STRING) + "'");
                log.log(Log.WARNING, "Exception parsing --port ", numException);
            }

        } else {
            // Client contact server

            try {

                setClientServerAndPort(parseArguments.getOptValue(PORT_OPTION_STRING));

                info("Contacting server at " + remoteHostName + ":" + remoteHostPort);
                connectionManager.startClientTransport(remoteHostName, remoteHostPort, this);
            } catch (NumberFormatException numException) {
                savedTransportException = new TransportException("Unable to parse value after --port '" + parseArguments.getOptValue(PORT_OPTION_STRING) + "'");
                log.log(Log.WARNING, "Exception setting remote host and port ", numException);
            }

            try {
                connectionManager.connectToMyServer();
            } catch (TransportException transportException) {
                savedTransportException = transportException;
                log.log(Log.INFO, "Exception contacting my server ", transportException);
                info("Unable to contact server at " + remoteHostName + ":" + port + " " + transportException.getMessage());
            }
        }

        isStarted = true;

        if (!parseArguments.isOptPresent(LOGIN_OPTION_STRING)) {

            if (usingGUI && isUsingMainUI()) {
                loginUI = createLoginFrame();
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

            if (usingGUI) {
                loginUI = createLoginFrame();
                loginUI.setContestAndController(contest, this); // this displays the login
            }

            try {

                if (savedTransportException == null) {
                    login(loginName, password); // starts login attempt, will show failure to LoginFrame
                }

            } catch (Exception e) {
                log.log(Log.INFO, "Exception while logging in ", e);
                if (usingGUI) {
                    loginUI.setStatusMessage(e.getMessage());
                } else {
                    fatalError(e.getMessage());
                }
            }
        }

        String contactInfo = getHostContacted() + ":" + getPortContacted();

        if (usingGUI && (savedTransportException != null && loginUI != null)) {
            loginUI.disableLoginButton();
            loginUI.setStatusMessage("Unable to contact server, contact staff");
            showErrorMessage("Unable to contact server at: " + contactInfo, "Error contacting server");
        } else if (savedTransportException != null) {
            connectionManager = null;
            fatalError("Unable to contact server at: " + contactInfo + ", contact staff", savedTransportException);
        }
    }

//    /**
//     * Is string null or trimmed length zero?.
//     * @param string
//     * @return
//     */
//    private boolean isEmpty(String string) {
//        return string == null || string.trim().length() == 0;
//    }

    private ILoginUI createLoginFrame() {
        ILoginUI ui = (ILoginUI) loadUIClass(loginClassName);
        ui.setContestAndController(contest, this);
        return ui;
    }

    private UIPlugin loadUIClass(String className) {

        try {
            UIPlugin ui = LoadUIClass.loadUIClass(className);
            return ui;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            log.log(Log.WARNING, "Unable to load UI, class = "+className);
            getLog();
        }

        return null;
    }

    /**
     * Get current profile, create one if needed.
     * 
     * @return
     */
    private Profile getCurrentProfile() {

        // SOMEDAY handle startup for when not starting with site 1 profile.setSiteNumber
        // SOMEDAY handle this exclusively in ProfileManager

        try {
            ProfileManager manager = new ProfileManager();
            if (manager.hasDefaultProfile()) {
                Profile defaultProfile = manager.getDefaultProfile();
                defaultProfile.setSiteNumber(1);
                return defaultProfile;
            } else {
                // Create new profile and save
                Profile newProfile = ProfileManager.createNewProfile();
                newProfile.setSiteNumber(1);
                manager.storeDefaultProfile(newProfile);
                return newProfile;
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
            fatalError("Unable to start pc2 unable to create initial Profile");
            return null; // known unreachable code, but compiler complains.
        }
    }

    private void handleCommandLineOptions() {

        if (parseArguments.isOptPresent("--help")) {
            // -F is the ParseArguements internal option to pre-load command line options from a file

            String[] usage = { //
                    "Usage: Starter [--help] [-F filename] [--server] [--first] [--login <login>] [--password <pass>] " + //
                    "[" + LOAD_OPTION_STRING + " <dir>|<file> ] " + //
                    "[--skipini] " + //
                    "[" + INI_FILENAME_OPTION_STRING + " filename] [" + //
                    CONTEST_PASSWORD_OPTION + " <pass>] [" + NO_GUI_OPTION_STRING + "] "+ //
                    "[" + REMOTE_SERVER_OPTION_STRING + " <remoteHostname> --proxyme]" + //
                    "[" + MAIN_UI_OPTION + " classname]", // 
                    "", //
                    "See http://pc2.ecs.csus.edu/wiki/Command_Line for more information", // 
            };
            for (String string : usage) {
                System.out.println(string);
            }
            System.exit(0);
        }

        if (parseArguments.isOptPresent(FILE_OPTION_STRING)) {
            String propertiesFileName = parseArguments.getOptValue(FILE_OPTION_STRING);

            if (!(new File(propertiesFileName).exists())) {
                fatalError(propertiesFileName + " does not exist (pwd: " + Utilities.getCurrentDirectory() + ")", null);
            }

            try {
                parseArguments.overRideOptions(propertiesFileName);
            } catch (IOException e) {
                fatalError("Unable to read file " + propertiesFileName, e);
            }
        }

        if (parseArguments.isOptPresent(NO_GUI_OPTION_STRING)) {

            usingGUI = false;

            // Insure that they have specified required
            // do not check contestPassword here

            if (!parseArguments.isOptPresent(LOGIN_OPTION_STRING)) {
                fatalError("Must specify " + LOGIN_OPTION_STRING + " option and login name when using " + NO_GUI_OPTION_STRING);
            }

            String loginName = parseArguments.getOptValue(LOGIN_OPTION_STRING);

            if (loginName == null) {
                fatalError("Missing login name after " + LOGIN_OPTION_STRING);
            }

            ClientId client = null;

            try {
                client = loginShortcutExpansion(0, loginName);
            } catch (SecurityException e) {
                fatalError(e.getLocalizedMessage());
            }

            if (overRideUIName == null) {
                if (isServer(client)) {
                    overRideUIName = "edu.csus.ecs.pc2.ui.server.ServerModule";
                } else if (isJudge(client)) {
                    overRideUIName = "edu.csus.ecs.pc2.ui.judge.AutoJudgeModule";
                } else if (isScoreboard(client)){
                    overRideUIName = "edu.csus.ecs.pc2.ui.board.ScoreboardModule";
                } else if (isEventFeeder(client)) {
                    overRideUIName = "edu.csus.ecs.pc2.services.eventFeed.EventFeederModule"; 
                } else {
                    fatalError(NO_GUI_OPTION_STRING + " can only be used with a judge or server login, login '" + loginName + "' is not a judge or server login.");
                }
            }

        }

        if (parseArguments.isOptPresent(DEBUG_OPTION_STRING)) {

            Utilities.setDebugMode(true);

            log.info("Debug mode ON");
            printDebug("Debug mode ON");

            printDebug(new VersionInfo().getSystemName() + " Build " + new VersionInfo().getBuildNumber());

            try {
                printDebug("Working directory is " + new File(".").getCanonicalPath());
            } catch (IOException e1) {
                System.err.println("debug: Could not determine working directory " + e1.getMessage());
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

    }

    private boolean isEventFeeder(ClientId clientId) {
        return clientId.getClientType().equals(ClientType.Type.FEEDER);
    }

    /**
     * Print debug message to stdout.
     * 
     * @param string
     */
    protected void printDebug(String message) {
        System.out.println("debug: "+message);
        
    }

    private boolean isJudge(ClientId clientId) {
        return clientId.getClientType().equals(ClientType.Type.JUDGE);
    }
    
    private boolean isScoreboard(ClientId clientId) {
        return clientId.getClientType().equals(ClientType.Type.SCOREBOARD);
    }

    private ClientId getServerClientId() {
        // SOMEDAY for all in this class, s/new ClientId(contest.getSiteNumber(), Type.SERVER, 0);/getServerClientId()/
        return new ClientId(contest.getSiteNumber(), Type.SERVER, 0);
    }

    /**
     * Return working directory.
     * 
     * File.separator has already been appended as needed.
     * 
     * @param dirname
     * @return profile directory if server, else return "";
     */
    private String getBaseProfileDirectoryName(String dirname) {

        if (theProfile != null) {
            return theProfile.getProfilePath() + File.separator + dirname;
        } else {
            return dirname;
        }
    }

    /**
     * Start new Log for client/server.
     * 
     * This new a new Log(logFileName), sets up the StaticLog, and prints basic info to the log. If loginName is not null a Login: line is printed.
     * 
     * @param directoryName
     *            if null will use the profile/* directory.
     * @param logFileName
     * @param loginName
     * @param clientName
     */
    private void startLog(String directoryName, String logFileName, String loginName, String clientName) {

        if (directoryName == null) {
            directoryName = getBaseProfileDirectoryName(Log.LOG_DIRECTORY_NAME);
            Utilities.insureDir(directoryName);
        }

        log = new Log(directoryName, logFileName);
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
            log.info("Process id is " + ManagementFactory.getRuntimeMXBean().getName());
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
     * 
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
                // SOMEDAY dal Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // SOMEDAY dal Auto-generated catch block
                e.printStackTrace();
            } catch (FileSecurityException e) {
                // SOMEDAY dal Auto-generated catch block
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

            Site remoteSite = lookupRemoteServer(inSiteNumber);
            
            Site localSite = contest.getSite(contest.getSiteNumber());
            String localPassword = localSite.getPassword();

            String hostName = remoteSite.getConnectionInfo().getProperty(Site.IP_KEY);
            String portStr = remoteSite.getConnectionInfo().getProperty(Site.PORT_KEY);
            int portNumber = Integer.parseInt(portStr);

            info("Send login request to Site " + remoteSite.getSiteNumber() + " " + hostName + ":" + portStr);
            ConnectionHandlerID connectionHandlerID = null;
            if (localSite.hasProxy()) {
                // if we have a proxy, it is our remote server, so use that.
                connectionHandlerID = remoteServerConnectionHandlerID;
            } else {
                // remoteSite may have changed from inSiteNumber, check to see if we
                // already know the connectionHandlerID for the server.
                ClientId clientId = new ClientId(remoteSite.getSiteNumber(), ClientType.Type.SERVER, 0);
                connectionHandlerID = contest.getConnectionHandleID(clientId);
                // do not exist, or is a fake handle
                if (connectionHandlerID == null || connectionHandlerID.toString().startsWith("FauxSite")) {
                    // connect to the server directly
                    connectionHandlerID = connectionManager.connectToServer(hostName, portNumber);
                } else {
                    info("Used cached connectionHandlerID for "+remoteSite+" of "+connectionHandlerID);
                }
            }

            info("Contacted Site " + inSiteNumber + " (via " + remoteSite.getSiteNumber() + ") using connection id " + connectionHandlerID);
            sendLoginRequestFromServerToServer(connectionManager, connectionHandlerID, getServerClientId(), localPassword, false);
            
        } else if (contest.isAllowed(Permission.Type.ALLOWED_TO_RECONNECT_SERVER)) {
            // Send the reconnection request to our server

            Packet reconnectPacket = PacketFactory.createReconnectPacket(contest.getClientId(), getServerClientId(), inSiteNumber);
            sendToLocalServer(reconnectPacket);
        } else {
            // SOMEDAY SECURITY security problem
            System.err.println(" Non-admin Tried to send reconnection request " + inSiteNumber + ", ignored");
            log.log(Log.DEBUG, " Non-admin Tried to send reconnection request " + inSiteNumber + ", ignored");
            return;
        }

    }

    /**
     * Determine which site to send this packet to.
     * 
     * This handles packets sent via proxy.
     * 
     * @param inSiteNumber destination site
     * @return site to send packet to.
     */
    protected Site lookupRemoteServer(int inSiteNumber) {

        /**
         * Site to send packet to.
         */
        Site site = contest.getSite(inSiteNumber);
        Site mySite = contest.getSite(contest.getSiteNumber());
        
        // if the destination has a proxy 
        if (site.hasProxy()) {

            /**
             * Send to proxy.
             */
            if (contest.getSiteNumber() != site.getMyProxy()) {
                // Current Site is NOT proxy for target site, send to proxy
                site = contest.getSite(site.getMyProxy());
            } // else this is the proxy site for the destination site - send to that site

        } else if (mySite.hasProxy()) {
            site = contest.getSite(mySite.getMyProxy());
        }// else no proxys, send to site
        
        System.out.println("debug 22 lookupRemoteServer for site "+inSiteNumber+" use site "+site);
        return site;
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

    public void updateSite(Site site) {

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
                error("Error updating site " + site, e);
            } catch (ClassNotFoundException e) {
                error("Error updating site " + site, e);
            } catch (FileSecurityException e) {
                error("Error updating site " + site, e);
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

        if (isServer()) {

            if (contest.isConnected(connectionHandlerID)) {
                log.log(Log.INFO, "forceConnectionDrop: " + connectionHandlerID);
                connectionManager.unregisterConnection(connectionHandlerID);
                contest.connectionDropped(connectionHandlerID);
            } else if (contest.isConnectedToRemoteSite(connectionHandlerID)) {
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

    public void addNewProblem(Problem[] problem, ProblemDataFiles[] problemDataFiles) {
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

    public void updateCategories(Category[] categories) {
        Packet updatePacket = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), categories);
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

    /**
     * Read Configuration.
     * 
     * Halt if configuration is corrupt.
     * 
     * @param siteNum
     * @return true if config file read
     */
    public boolean readConfigFromDisk(int siteNum) {

        boolean loadedConfiguration = false;
        if (saveCofigurationToDisk) {
            try {
                loadedConfiguration = contest.readConfiguration(siteNum, getLog());
                
            } catch (FileNotFoundException fnf){
                // This is expected
                loadedConfiguration = false;
            
            } catch (Exception e) {
                logException(e);
                
                // Bug 879 -If there is a problem reading the config then exit and show a message. 
                fatalError("Halting server - configuration file corrupt", e);
                return false;
            }
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
        Packet contestInfoPacket = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), contestInformation);
        sendToLocalServer(contestInfoPacket);
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
        Packet securityMessagePacket = PacketFactory.createSecurityMessagePacket(contest.getClientId(), getServerClientId(), event, contestSecurityException.getClientId(),
                contestSecurityException.getConnectionHandlerID(), contestSecurityException, null);
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
        if (runFiles != null) {
            contest.updateRun(run, runFiles, null, null);
        } else {
            Packet fetchRunPacket = PacketFactory.createFetchRun(contest.getClientId(), getServerClientId(), run, contest.getClientId());
            sendToLocalServer(fetchRunPacket);
        }
    }

    private void sendStatusMessge(Run run, RunExecutionStatus status) {

        if (contest.isSendAdditionalRunStatusMessages()) {
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
        Profile currentProfile = contest.getProfile();
        Packet sendPacket = PacketFactory.createResetAllSitesPacket(contest.getClientId(), getServerClientId(), clientResettingContest, currentProfile, eraseProblems, eraseLanguages);
        sendToLocalServer(sendPacket);
    }

    public void cloneProfile(Profile profile, ProfileCloneSettings settings, boolean switchNow) {
        Packet sendPacket = PacketFactory.createCloneProfilePacket(contest.getClientId(), getServerClientId(), profile, settings, switchNow);
        sendToLocalServer(sendPacket);
    }

    public void switchProfile(Profile currentProfile, Profile switchToProfile, String contestPassword) {
        Packet sendPacket = PacketFactory.createSwitchProfilePacket(contest.getClientId(), getServerClientId(), currentProfile, switchToProfile, contestPassword);
        sendToLocalServer(sendPacket);
    }

    public void updateProfile(Profile profile) {
        Packet updateProfilePackert = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), profile);
        sendToLocalServer(updateProfilePackert);
    }

    protected void warning(String message, Exception ex) {
        if (log != null) {
            if (ex != null) {
                log.log(Log.WARNING, message, ex);
            } else {
                log.log(Log.WARNING, message);
            }
        }

        System.err.println(message);
        if (Utilities.isDebugMode()) {
            if (ex != null) {
                ex.printStackTrace(System.err);
            }
        }
    }

    private void showErrorMessage(String message, String title) {
        // TODO 736 fix this to show GUI message
        System.err.println(title+": "+message); // TODO 736 remove this
    }

    /**
     * Fatal error - log error and show user message before exiting.
     * 
     * @param message
     * @param ex
     */
    protected void fatalError(String message, Exception ex) {
        
        if (log != null) {
            if (ex != null) {
                log.log(Log.SEVERE, message, ex);
            } else {
                log.log(Log.SEVERE, message);
            }

            if (haltOnFatalError) {
                log.log(Log.INFO, "PC^2 halted");
            }
        }

        if (usingGUI) {
            if (ex != null) {
                ex.printStackTrace(System.err);
            }
            showErrorMessage(message + " check logs", "PC^2 Halted");
        } else {
            if (ex != null) {
                ex.printStackTrace(System.err);
            }
            System.err.println(message);

            if (haltOnFatalError) {
                System.err.println("PC^2 Halted - check logs");
            }
        }

        if (haltOnFatalError) {
            System.exit(4);
        }
        
    }

    /**
     * 
     * @see #fatalError(String, Exception)
     * @param message
     */
    private void fatalError(String message) {
        fatalError(message, null);
    }

    public void setContest(IInternalContest newContest) {
        this.contest = newContest;
        contest.setCommandLineArguments(parseArguments);
        packetHandler = new PacketHandler(this, newContest);
        runSubmitterInterfaceManager.setContestAndController(newContest, this);
    }

    public void register(UIPlugin plugin) {
        pluginList.register(plugin);
    }

    public UIPlugin[] getPluginList() {
        return pluginList.getList();
    }

    private void firePacketListener(PacketEvent packetEvent) {
        for (int i = 0; i < packetListenerList.size(); i++) {

            if (packetEvent.getAction() == PacketEvent.Action.RECEIVED) {
                packetListenerList.elementAt(i).packetReceived(packetEvent);
            } else {
                // packetEvent.getAction() == PacketEvent.Action.SENT
                packetListenerList.elementAt(i).packetSent(packetEvent);
            }
        }
    }

    public void updateContestController(IInternalContest inContest, IInternalController inController) {

        setContest(inContest);

        ClientId clientId = contest.getClientId();
        String id = clientId.getName();
        try {
            if (log != null) {
                log.close();
            }
        } catch (Throwable e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        setTheProfile(inContest.getProfile());
        startLog(getBaseProfileDirectoryName(Log.LOG_DIRECTORY_NAME), stripChar(clientId.toString(), ' '), id, clientId.getName());
        if (evaluationLog != null) {
            evaluationLog.closeEvalLog();
            evaluationLog = null;
        }
        if (evaluationLog == null) {
            String logDirectory = getBaseProfileDirectoryName(Log.LOG_DIRECTORY_NAME);
            Utilities.insureDir(logDirectory);
            // this not only opens the log but registers this class to handle all run events.
            evaluationLog = new EvaluationLog(logDirectory + File.separator + "evals.log", inContest, this);
            evaluationLog.getEvalLog().println("# Log opened " + new Date());
            info("evals.log is opened at " + logDirectory);
        }

        try {
            new ProfileManager().mergeProfiles(contest);
        } catch (Exception e) {
            logException(e);
        }

        for (UIPlugin plugin : getPluginList()) {

            try {
                plugin.setContestAndController(contest, inController);

                inController.getLog().info("plugin.setContestAndController for " + plugin.getPluginTitle());

                if (Utilities.isDebugMode()) {
                    System.out.println("plugin.setContestAndController for " + plugin.getPluginTitle());
                }

            } catch (Exception e) {
                logException(e);
            }
        }

        packetHandler = new PacketHandler(this, contest);

    }

    private void logException(Exception e) {

        if (StaticLog.getLog() != null) {
            StaticLog.getLog().log(Log.WARNING, "Exception", e);
        } else {
            e.printStackTrace(System.err);
        }
    }

    private void logException(String message, Exception e) {

        if (StaticLog.getLog() != null) {
            StaticLog.getLog().log(Log.WARNING, "Exception - " + message, e);

            if (Utilities.isDebugMode()) {
                System.err.println("Exception - " + message);
                e.printStackTrace(System.err);
            }
        } else {
            System.err.println("Exception - " + message);
            e.printStackTrace(System.err);
        }
    }

    public void addPacketListener(IPacketListener packetListener) {
        packetListenerList.addElement(packetListener);
    }

    public void removePacketListener(IPacketListener packetListener) {
        packetListenerList.removeElement(packetListener);
    }

    public void incomingPacket(Packet packet) {
        PacketEvent event = new PacketEvent(Action.RECEIVED, packet);
        firePacketListener(event);
    }

    public void outgoingPacket(Packet packet) {
        PacketEvent event = new PacketEvent(Action.SENT, packet);
        firePacketListener(event);
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public boolean isUsingGUI() {
        return usingGUI;
    }

    public ILogWindow startLogWindow(IInternalContest inContest) {

        if (!isUsingGUI()) {
            /**
             * Do not show LogWindow if not in GUI mode.
             */
            return null;
        }

        if (logWindow != null) {
            logWindow.dispose();
        }

        logWindow = null;
        logWindow = (ILogWindow) loadUIClass(logWindowClassName);
        logWindow.setContestAndController(inContest, this);
        logWindow.setTitle("Log " + inContest.getClientId().toString());

        return logWindow;
    }

    public void showLogWindow(boolean showWindow) {

        if (isUsingGUI()) {
            logWindow.setVisible(showWindow);
        }
    }

    public boolean isLogWindowVisible() {
        if (isUsingGUI()) {
            logWindow.isVisible();
        }

        return false;
    }

    public void logWarning(String string) {
        log.log(Log.WARNING, string);
        System.err.println("Warning: " + string);
    }

    public void logWarning(String string, Exception e) {
        log.log(Log.WARNING, string, e);
        System.err.println("Warning: " + string);
        printStackTraceTop(e, System.err, 5);
    }

    public void logSevere(String string, Exception e) {
        log.log(Log.SEVERE, string, e);
        System.err.println("Severe Error: " + string);
        printStackTraceTop(e, System.err, 5);
    }

    private void printStackTraceTop(Throwable throwable, PrintStream printStream, int count) {

        printStream.println("Exception " + throwable.getClass().getName() + ": " + throwable.getMessage());

        StackTraceElement[] elements = throwable.getStackTrace();
        for (int i = 0; i < count; i++) {
            StackTraceElement stackTraceElement = elements[i];
            String sourceName = "(Unknown Source)";
            if (stackTraceElement.getFileName() != null) {
                sourceName = "(" + stackTraceElement.getFileName() + ":" + stackTraceElement.getLineNumber() + ")";
            }
            printStream.println("     at " + stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + " " + sourceName);
        }
    }

    public void syncProfileSubmissions(Profile profile) {
        Packet packet = PacketFactory.createSwitchSynchronizePacket(contest.getClientId(), getServerClientId(), profile);
        sendToLocalServer(packet);
    }

    public void sendShutdownAllSites() {
        Packet packet = PacketFactory.createShutdownAllServersPacket(contest.getClientId(), getServerClientId());
        sendToLocalServer(packet);
    }

    public void sendShutdownSite(int siteNumber) {
        Packet packet = PacketFactory.createShutdownPacket(contest.getClientId(), getServerClientId(), siteNumber);
        sendToLocalServer(packet);
    }

    /**
     * 
     */
    public void shutdownServer(ClientId requestor) {

        if (isServer()) {

            if (contest.isAllowed(requestor, Permission.Type.SHUTDOWN_ALL_SERVERS) || contest.isAllowed(requestor, Permission.Type.SHUTDOWN_SERVER)) {
                shutdownProxys(requestor); // shutdown my proxys

                try {
                    ContestSummaryReports contestReports = new ContestSummaryReports();
                    contestReports.setContestAndController(contest, this);

                    if (contestReports.isLateInContest()) {
                        contestReports.generateReports();
                        log.info("Reports Generated to " + contestReports.getReportDirectory());
                    }
                } catch (Exception e) {
                    log.log(Log.WARNING, "Unable to create reports ", e);
                }

                log.info("Server " + contest.getSiteNumber() + " halted by " + requestor);
                System.exit(0);

            } else {
                throw new SecurityException("User " + requestor + " not allowed to shutdown Server");
            }
        } else {
            /**
             * If this is reached then there is a bug elsewhere. This shutdownServer should only be called if this is running as a Server.
             */
            throw new SecurityException("Attempted to shutdown non-server client");
        }
    }

    public void shutdownRemoteServers(ClientId requestor) {

        if (contest.isAllowed(requestor, Permission.Type.SHUTDOWN_ALL_SERVERS)) {

            ClientId[] clientIds = contest.getLocalLoggedInClients(ClientType.Type.SERVER);

            for (ClientId clientId : clientIds) {
                Packet shutdownPacket = PacketFactory.createShutdownPacket(requestor, clientId, clientId.getSiteNumber());
                ConnectionHandlerID connectionHandlerID = contest.getConnectionHandleID(clientId);
                sendToClient(connectionHandlerID, shutdownPacket);
            }
        } else {
            throw new SecurityException("User " + requestor + " not allowed to shutdown remote servers");
        }
    }

    public void shutdownServer(ClientId requestor, int siteNumber) {

        if (contest.isAllowed(requestor, Permission.Type.SHUTDOWN_ALL_SERVERS) || contest.isAllowed(requestor, Permission.Type.SHUTDOWN_SERVER)) {

            if (siteNumber == contest.getSiteNumber()) {
                shutdownProxys(requestor); // shutdown my proxys
                shutdownServer(requestor); // This site shut it down
            } else {
                ClientId serverId = getServerClientId(siteNumber);
                Packet shutdownPacket = PacketFactory.createShutdownPacket(requestor, serverId, siteNumber);
                ConnectionHandlerID connectionHandlerID = contest.getConnectionHandleID(serverId);
                sendToClient(connectionHandlerID, shutdownPacket);
            }
        } else {
            throw new SecurityException("User " + requestor + " not allowed to shutdown remote servers");
        }
    }

    private void shutdownProxys(ClientId requestor) {
        // we need to know who the packet is from (do not send it back to them)
        int fromSite = requestor.getSiteNumber();
        Site[] sites = contest.getSites();
        for (Site site : sites) {
            // skip myself and who the packet came from
            if (site.getSiteNumber() == contest.getSiteNumber() || site.getSiteNumber() == fromSite) {
                continue;
            }
            // we are a proxy for this site
            if (site.getMyProxy() == contest.getSiteNumber()) {
                int siteNumber = site.getSiteNumber();
                ClientId serverId = getServerClientId(siteNumber);
                Packet shutdownPacket = PacketFactory.createShutdownPacket(requestor, serverId, siteNumber);
                ConnectionHandlerID connectionHandlerID = contest.getConnectionHandleID(serverId);
                sendToClient(connectionHandlerID, shutdownPacket);
            }
        }
    }

    protected ClientId getServerClientId(int siteNumber) {
        return new ClientId(siteNumber, Type.SERVER, 0);
    }

    public void updateFinalizeData(FinalizeData data) {
        Packet updateFinalizePacket = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), data);
        sendToLocalServer(updateFinalizePacket);
    }

    public void setUsingGUI(boolean usingGUI) {
        this.usingGUI = usingGUI;
    }

    public void updateCategory(Category newCategory) {
        Packet updatePacket = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), newCategory);
        sendToLocalServer(updatePacket);
    }

    public void addNewCategory(Category newCategory) {
        Packet addNewCategory = PacketFactory.createAddSetting(contest.getClientId(), getServerClientId(), newCategory);
        sendToLocalServer(addNewCategory);
    }

    public void startPlayback(PlaybackInfo playbackInfo) {
        Packet startPacket = PacketFactory.createStartPlayback(contest.getClientId(), getServerClientId(), playbackInfo);
        sendToLocalServer(startPacket);
    }

    public void submitRun(Problem problem, Language language, String filename, SerializedFile[] otherFiles, long overrideSubmissionTime, long overrideRunId) {

        SerializedFile serializedFile = new SerializedFile(filename);

        ClientId serverClientId = new ClientId(contest.getSiteNumber(), Type.SERVER, 0);
        Run run = new Run(contest.getClientId(), language, problem);
        RunFiles runFiles = new RunFiles(run, serializedFile, otherFiles);

        Packet packet = PacketFactory.createSubmittedRun(contest.getClientId(), serverClientId, run, runFiles, overrideSubmissionTime, overrideRunId);
        sendToLocalServer(packet);
    }

    public void sendRunToSubmissionInterface(Run run, RunFiles runFiles) {
        try {
            runSubmitterInterfaceManager.sendRun(run, runFiles);
        } catch (Exception e) {
            logException("Failure in RSI ", e);
            e.printStackTrace();
        }
    }

    public void addConsoleLogging() {

        if (log != null) {
            // HOWTO add a console logger (console handler) to the StaticLog

            ConsoleHandler consoleHandler = new ConsoleHandler();
            log.addHandler(consoleHandler);
        } else {
            System.err.println("Unable to add console logging, log is null");
        }
    }
    
    public Profile getTheProfile() {
        return theProfile;
    }
    
    public void setTheProfile(Profile theProfile) {
        this.theProfile = theProfile;
    }
    
    public void autoRegister(String loginName) {

        ClientId serverClientId = new ClientId(contest.getSiteNumber(), Type.SERVER, 0);
        ClientId fauxClientId = new ClientId(0, Type.OTHER, 0);
        Packet packet = PacketFactory.createAutoRegisterRequest(fauxClientId, serverClientId, loginName);
        sendToLocalServer(packet);
    }

    @Override
    public void setConnectionManager(ITransportManager connectionManager) {
        this.connectionManager = connectionManager;
    }
    
    public void setHaltOnFatalError(boolean haltOnFatalError) {
        this.haltOnFatalError = haltOnFatalError;
    }
    
    public boolean isHaltOnFatalError() {
        return haltOnFatalError;
    }

    @Override
    public void addNewLanguages(Language[] languages) {
        Packet addPacket = PacketFactory.createAddSetting(contest.getClientId(), getServerClientId(), languages);
        sendToLocalServer(addPacket);
    }

    @Override
    public void updateLanguages(Language[] languages) {
        Packet updatePacket = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), languages);
        sendToLocalServer(updatePacket);
    }

    @Override
    public void addNewGroups(Group[] groups) {
        Packet addPacket = PacketFactory.createAddSetting(contest.getClientId(), getServerClientId(), groups);
        sendToLocalServer(addPacket);
    }

    @Override
    public void updateGroups(Group[] groups) {
        Packet updatePacket = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), groups);
        sendToLocalServer(updatePacket);
    }
    
    /**
     * Creates an {@link AutoStarter} if none exists, and then instructs the AutoStarter to update its Scheduled Start Task to correspond to the Scheduled Start Time information in the
     * {@link ContestInformation} object in the received {@link IInternalContest}.
     * 
     * @param theContest
     *            - the Contest (Model) containing the Scheduled Start Time information
     * @param theController
     *            - the Controller to which this request applies
     */
    public void updateAutoStartInformation(IInternalContest theContest, IInternalController theController) {
        
        // if I'm a server then if there's no AutoStarter create one, and then tell the AutoStarter to
        // update its Scheduled Start task list based on the ContestInformation in theContest (Model).
//        if (isServer()) {

            if (log == null) {
                System.err.println("InternalController.updateAutoStartInformation(): no log available!");
            }

            // create an AutoStarter if there isn't already one
            if (autoStarter == null) {
                autoStarter = new AutoStarter(theContest, theController);
                if (log != null) {
                    log.info("Created AutoStarter...");
                } else {
                    System.err.println("Created AutoStarter... (but no log available)");
                }
            }

            // if we have a Model, get its ContestInformation and update the AutoStarter
            if (theContest != null) {
                ContestInformation contestInfo = theContest.getContestInformation();
                if (contestInfo != null) {

                    // we have the ContestInformation; update the AutoStarter from the CI
                    autoStarter.updateScheduleStartContestTask(contestInfo);

                    if (log != null) {
                        log.info("updated AutoStarter with date " + contestInfo.getScheduledStartDate() + " (note: null means no scheduled start is set)");
                    } else {
                        System.err.println("updated AutoStarter with date " + contestInfo.getScheduledStartDate()
                                + " (note: null means no scheduled start is set)"
                                + " but no log is available!");
                    }

                } else {
                    // we got a Model but it doesn't have any ContestInfo
                    if (log != null) {
                        log.warning("InternalController.updateAutoStartInformation(): contest Model has no ContestInformation; cannot update AutoStarter! ");
                    } else {
                        System.err.println("InternalController.updateAutoStartInformation(): contest model has no ContestInformation; cannot update AutoStarter"
                                + "...and no log is available!");
                    }
                }

            } else {
                // we got a null Model
                if (log != null) {
                    log.warning("InternalController.updateAutoStartInformation() received a null contest Model!");
                } else {
                    System.err.println("InternalController.updateAutoStartInformation() received a null contest Model ... and no log is available!");
                }
            }
//        } else {
//            System.err.println ("in updateAutoStartInformation() -- NOT A SERVER!!");
//        }
    }
}
