package edu.csus.ecs.pc2.core;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.archive.PacketArchiver;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ConfigurationIO;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;
import edu.csus.ecs.pc2.core.packet.PacketType;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.core.transport.IBtoA;
import edu.csus.ecs.pc2.core.transport.ITransportManager;
import edu.csus.ecs.pc2.core.transport.ITwoToOne;
import edu.csus.ecs.pc2.core.transport.TransportException;
import edu.csus.ecs.pc2.core.transport.TransportManager;
import edu.csus.ecs.pc2.ui.CountDownMessage;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.LoadUIClass;
import edu.csus.ecs.pc2.ui.LoginFrame;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Implementation of Contest Controller.
 * 
 * Run Flow, submit run.
 * <ol>
 * <li> Team: {@link #submitRun(Problem, Language, String)}
 * <li> Server: {@link edu.csus.ecs.pc2.core.PacketHandler#handlePacket(Packet, ConnectionHandlerID)}
 * <li> Server: {@link edu.csus.ecs.pc2.core.model.Contest#acceptRun(Run, RunFiles)}
 * <li> Team: {@link edu.csus.ecs.pc2.core.model.IRunListener#runAdded(edu.csus.ecs.pc2.core.model.RunEvent)} RunEvent action is:
 * {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#ADDED}
 * <li> Client: {@link edu.csus.ecs.pc2.core.model.IRunListener#runAdded(edu.csus.ecs.pc2.core.model.RunEvent)} RunEvent action is:
 * {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#ADDED}
 * </ol>
 * Check out run
 * <ol>
 * <li> Judge: {@link #checkOutRun(Run)}
 * <li> Server: {@link edu.csus.ecs.pc2.core.PacketHandler#requestRun(Run, IContest, IController, ClientId)}
 * <li> Judge and clients: {@link edu.csus.ecs.pc2.core.model.IRunListener#runChanged(edu.csus.ecs.pc2.core.model.RunEvent)}, check
 * {@link edu.csus.ecs.pc2.core.model.RunEvent#getSentToClientId()} to learn if you are the judge/client to get the run. RunEvent
 * action is: {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#CHECKEDOUT_RUN}
 * </ol>
 * Submit Judgement
 * <ol>
 * <li> Judge: {@link #submitRunJudgement(Run, JudgementRecord, RunResultFiles)}
 * <li> Server:
 * {@link edu.csus.ecs.pc2.core.PacketHandler#judgeRun(Run, IContest, IController, JudgementRecord, RunResultFiles, ClientId)}
 * <li> Team: {@link edu.csus.ecs.pc2.core.model.IRunListener#runChanged(edu.csus.ecs.pc2.core.model.RunEvent)} if {@link Run#isSendToTeams()} set true.
 * RunEvent action is: {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#CHANGED}
 * <li> Clients: {@link edu.csus.ecs.pc2.core.model.IRunListener#runChanged(edu.csus.ecs.pc2.core.model.RunEvent)} RunEvent action is:
 * {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#CHANGED}
 * </ol>
 * Cancel Run
 * <ol>
 * <li> Judge: {@link #cancelRun(Run)}
 * <li> Server: {@link edu.csus.ecs.pc2.core.PacketHandler#cancelRun(Run, IContest, IController, ClientId)}
 * <li> Team: n/a
 * <li> Judge/Clients: {@link edu.csus.ecs.pc2.core.model.IRunListener#runChanged(edu.csus.ecs.pc2.core.model.RunEvent)}. RunEvent action is:
 * {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#RUN_AVIALABLE}
 * </ol>
 * 
 * @author pc2@ecs.csus.edu *
 */
// $HeadURL$
public class Controller implements IController, ITwoToOne, IBtoA {

    public static final String SVN_ID = "$Id$";

    /**
     * Contest data.
     */
    private IContest contest;

    /**
     * Transport.
     */
    private ITransportManager transportManager;

    /**
     * Controller.
     */

    /**
     * The main UI, started by the controller.
     */
    private UIPlugin uiPlugin = null;

    private Log log;
    
    private static final String LOGIN_OPTION_STRING = "--login";
    private static final String PASSWORD_OPTION_STRING = "--password";
    // TODO code implement --loginUI
    @SuppressWarnings("unused")
    private static final String LOGIN_UI_OPTION_STRING = "--loginUI";

    /**
     * The port to contact and for the server to listen on.
     */
    private static int port;

    /**
     * The host/IP for a server to login to/contact.
     */
    private static String remoteHostName = "127.0.0.1";

    /**
     * The port for a server to login to/contact.
     */
    private static int remoteHostPort;

    /**
     * Key in .ini for the server port.
     */
    private static final String SERVER_PORT_KEY = "server.port";
    
    /**
     * Key in the .ini for the remote server host name.
     */
    private static final String REMOTE_SERVER_KEY = "server.remoteServer";

    /**
     * Key in the .ini for the client server name.
     */
    private static final String CLIENT_SERVER_KEY = "client.server";

    /**
     * Key in the .ini for the client port.
     */
    private static final String CLIENT_PORT_KEY = "client.port";

    private static ConnectionHandlerID remoteServerConnectionHandlerID = null;

    @SuppressWarnings("unused")
    private ParseArguments parseArguments = new ParseArguments();
    
    private boolean contactingRemoteServer = true;
    
    private boolean usingMainUI = true;
    
    private PacketArchiver packetArchiver = new PacketArchiver();
    
    // TODO change this to UIPlugin
    /*
     * Difficulty with changing LoginFrame to UIPlugin, there
     * is no way to setVisible(false) a UIPlugin or make
     * the GUI cursor change for a UIPlugin. dal.
     * 
     */
    private LoginFrame loginUI;
    
    /*
     * Set to true when start() is called,
     * checked by login().
     */
    private boolean isStarted = false;

    private PacketHandler packetHandler = null;
    
    /**
     * Is this a server module.
     */
    private boolean serverModule = false;
    
    private ConfigurationIO configurationIO = new ConfigurationIO(1);

    /**
     * Load and Save configuration to disk
     */
    private boolean saveCofigurationToDisk = true;
    
    public Controller(IContest contest) {
        super();
        this.contest = contest;
        packetHandler  = new PacketHandler(this, contest);
    }
    
    /**
     * Client send packet to server.
     * @param packet
     */
    private void sendToLocalServer(Packet packet) {
        try {
            log.info("Sending packet to server "+packet);
            transportManager.send(packet);
        } catch (TransportException e) {
            info("Unable to send to Server  " + packet);
            e.printStackTrace();
        }
        log.info("Sent    packet to server "+packet);
    }

    private void sendToClient(ConnectionHandlerID connectionHandlerID, Packet packet) {
        info("sendToClient (send) " +packet+" "+connectionHandlerID);
        try {
            transportManager.send(packet, connectionHandlerID);
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
    public void sendToRemoteServer (int siteNumber, Packet packet){
        ClientId clientId = new ClientId(siteNumber, Type.SERVER, 0);

        ConnectionHandlerID connectionHandlerID = contest.getConnectionHandleID(clientId);
        
        Type type = packet.getSourceId().getClientType();
        if ((!type.equals(Type.ADMINISTRATOR)) && (! type.equals(Type.SERVER))){
            log.log(Log.SEVERE, "Unexpected User sent packet to other ("+siteNumber+") site.  "+packet);
        }

        if (connectionHandlerID != null) {
            
            try {
                transportManager.send(packet, connectionHandlerID);
            } catch (TransportException e) {
                log.log(Log.SEVERE,"Exception sending packet to site "+siteNumber+" "+packet, e);
            }
            
        } else {
            log.log(Log.SEVERE,"Unable to send packet to site "+siteNumber+" ("+clientId+")" +packet);
        }
    }

    public void sendToClient(Packet packet) {
        info("sendToClient b4 to " + packet.getDestinationId() + " " + packet);

        ConnectionHandlerID connectionHandlerID = contest.getConnectionHandleID(packet.getDestinationId());
        info("sendToClient " + packet.getSourceId() + " " + connectionHandlerID);
        if (connectionHandlerID == null) {
            int destinationSiteNumber = packet.getDestinationId().getSiteNumber();
            if (isThisSite(destinationSiteNumber)) {
                sendToLocalServer(packet);
            } else {
                sendToRemoteServer(destinationSiteNumber, packet);
            }
        } else {
            sendToClient(connectionHandlerID, packet);
        }

        info("sendToClient af to " + packet.getDestinationId() + " " + packet);
    }

    public void submitRun(Problem problem, Language language, String filename) throws Exception {
        SerializedFile serializedFile = new SerializedFile(filename);

        ClientId serverClientId = new ClientId(contest.getSiteNumber(), Type.SERVER, 0);
        Run run = new Run(contest.getClientId(), language, problem);
        RunFiles runFiles = new RunFiles(run, serializedFile, null);

        Packet packet = PacketFactory.createSubmittedRun(contest.getClientId(), serverClientId, run, runFiles);

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

    protected static ClientId loginShortcutExpansion(int defaultSiteNumber, String loginName) {
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
        } else if (loginName.startsWith("s") && loginName.length() > 1) {
            if (Character.isDigit(loginName.charAt(1))) {
                int number = getIntegerValue(loginName.substring(1));
                return new ClientId(number, Type.SERVER, 0);
            } else {
                throw new SecurityException("No such account " + loginName);
            }
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
        } else {
            throw new SecurityException("No such account " + loginName);
        }

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
            // TODO review this message
            throw new SecurityException("Invalid sequence, must call start(String[]) method before login(String, String).");
        }
        ClientId clientId = loginShortcutExpansion(0, id);

        log = new Log(clientId.toString());
        StaticLog.setLog(log);

        info(new VersionInfo().getSystemVersionInfo());
        info("Login: " + id + " (aka " + clientId.getName() + ")");

        if (password.length() < 1) {
            password = clientId.getName(); // Joe password.
            if (clientId.getClientType().equals(Type.SERVER)) {
                password = "site" + clientId.getSiteNumber();
            }
        }

        port = Integer.parseInt(TransportManager.DEFAULT_PC2_PORT);

        if (clientId.getClientType().equals(Type.SERVER)) {

            if (containsINIKey(SERVER_PORT_KEY)) {
                port = Integer.parseInt(getINIValue(SERVER_PORT_KEY));
            }

            if (isContactingRemoteServer()) {

                // Contacting another server. "join"
                remoteHostName = getINIValue(REMOTE_SERVER_KEY);

                // Set port to default
                remoteHostPort = Integer.parseInt(TransportManager.DEFAULT_PC2_PORT);

                int idx = remoteHostName.indexOf(":");
                if (idx > 2) {
                    remoteHostPort = Integer.parseInt(remoteHostName.substring(idx + 1));
                    remoteHostName = remoteHostName.substring(0, idx);
                }

                info("Contacting " + remoteHostName + ":" + remoteHostPort);
                try {
                    remoteServerConnectionHandlerID = transportManager.connectToServer(remoteHostName, remoteHostPort);
                } catch (TransportException e) {
                    info("** ERROR ** Unable to contact server at " + remoteHostName + ":" + remoteHostPort);
                    info("Server at " + remoteHostName + ":" + remoteHostPort + " not started or contacting wrong host or port ?");
                    info("Transport Exception ", e);
                    throw new SecurityException("Unable to contact server, check logs");
                }

                info("Contacted using connection id " + remoteServerConnectionHandlerID);

                info("Sending login request to " + remoteHostName + " as " + clientId + " " + password); // TODO remove this
                sendLoginRequest(transportManager, remoteServerConnectionHandlerID, clientId, password);

            } else {

                if (!serverModule) {
                    SecurityException securityException = new SecurityException("Can not login as server, check logs");
                    getLog().log(Log.WARNING, "Can not login as server, must start this module with --server command line option");
                    securityException.printStackTrace(System.err);
                    throw securityException;
                }

                clientId = authenticateFirstServer(password);
                try {
                    transportManager.accecptConnections(port);
                    info("Started Server Transport listening on " + port);
                } catch (Exception e) {
                    info("Exception logged ", e);
                    SecurityException securityException = new SecurityException("Port " + port + " in use, server already running?");
                    securityException.printStackTrace(System.err);
                    throw securityException;
                }
                info("Primary Server has started.");
                startMainUI(clientId);
            }

        } else {

            // Client login
            info("Sending login request to Server as " + clientId); // TODO debug remove this
            sendLoginRequest(transportManager, clientId, password);
        }
    }

    private ClientId authenticateFirstServer(String password) {
        
        if (contest.getSites().length == 0){
            
            boolean loadedConfiguration = false;
            
            if (saveCofigurationToDisk){
                loadedConfiguration = configurationIO.loadFromDisk(1, contest, getLog());
                contest.initializeSubmissions(); // load submissions from disk too.
            }
            
            if (! loadedConfiguration){
                contest.initializeWithFakeData();
                log.info("initializing controller with default settings");
                writeConfigToDisk();
            } else {
                log.info("Loaded configuration from disk");
            }
            
        }
        int newSiteNumber = getServerSiteNumber(password);

        ClientId newId = new ClientId(newSiteNumber, ClientType.Type.SERVER, 0);
        if (contest.isLocalLoggedIn(newId)) {
            info("Note site " + newId + " site " + newSiteNumber + " already logged in, ignoring ");
        }
        return newId;
    }
    
    /**
     * Reads .ini file and sets server and port.
     * 
     * Sets the server and port for client.
     *
     */
    private void setClientServerAndPort (){
        
        remoteHostName = "localhost";

        port = Integer.parseInt(TransportManager.DEFAULT_PC2_PORT);
        
        if (containsINIKey(CLIENT_SERVER_KEY)) {
            remoteHostName = getINIValue(CLIENT_SERVER_KEY);
            int idx = remoteHostName.indexOf(":");
            if (idx > 2) {
                port = Integer.parseInt(remoteHostName.substring(idx + 1));
                remoteHostName = remoteHostName.substring(0, idx);
            }
        }
        
        if (containsINIKey(CLIENT_PORT_KEY)) {
            port = Integer.parseInt(getINIValue(CLIENT_PORT_KEY));
        }
    }

    private void sendLoginRequest(ITransportManager manager, ConnectionHandlerID connectionHandlerID, ClientId clientId,
            String password) {
        try {
            info("sendLoginRequest ConId start - sending from " + clientId);
            ClientId serverClientId = new ClientId(0, Type.SERVER, 0);
            Packet loginPacket = PacketFactory.createLoginRequest(clientId, password, serverClientId);
            manager.send(loginPacket, connectionHandlerID);
            info("sendLoginRequest ConId end - packet sent.");
        } catch (TransportException e) {
            // TODO log exception
            e.printStackTrace();
        }
    }

    /**
     * Send login request to server as a login.
     * 
     * @param manager
     * @param clientId
     * @param password
     */
    private void sendLoginRequest(ITransportManager manager, ClientId clientId, String password) {
        try {
            info("sendLoginRequest start - sending from "+clientId);
            ClientId serverClientId = new ClientId(0, Type.SERVER, 0);
            Packet loginPacket = PacketFactory.createLoginRequest(clientId, password, serverClientId);
            manager.send(loginPacket);
            info("sendLoginRequest end - packet sent.");
        } catch (TransportException e) {
            // TODO log exception
            e.printStackTrace();
        }
    }

    /**
     * Server receive object.
     * 
     * @see edu.csus.ecs.pc2.core.transport.ITwoToOne#receiveObject(java.io.Serializable,
     *      edu.csus.ecs.pc2.core.transport.ConnectionHandlerID)
     */
    public void receiveObject(Serializable object, ConnectionHandlerID connectionHandlerID) {

        // TODO code check the input connection to insure they are valid connection
        info("receiveObject (S,C) debug start (by "+contest.getClientId()+") got " + object);

        try {

            if (object instanceof Packet) {

                Packet packet = (Packet) object;
                ClientId clientId = packet.getSourceId();

                info("receiveObject " + packet);
                if (contest.isLocalLoggedIn(packet.getSourceId())) {

                    /**
                     * This user is in the login list and we process their request.
                     */

                    // TODO code security double check by checking their clientId and
                    // their connection id against what we have in the contest.
                    processPacket(packet, connectionHandlerID);

                } else if (packet.getType().equals(PacketType.Type.LOGIN_REQUEST)) {
                    String password = PacketFactory.getStringValue(packet, PacketFactory.PASSWORD);
                    try {

                        /**
                         * Login request from client or other server. When this block is done, they are logged in and a login
                         * success is sent to them.
                         */
                        
                        packetArchiver.writeNextPacket(packet);

                        if (clientId.getSiteNumber() == ClientId.UNSET) {
                            clientId = new ClientId(contest.getSiteNumber(), clientId.getClientType(), clientId.getClientNumber());
                        }
                        attemptToLogin(clientId, password, connectionHandlerID);
                        removeConnection(connectionHandlerID);
                        sendLoginSuccess(clientId, connectionHandlerID);

                        // Send login notification to users.
                        
                        Packet loginConfirmedPacket  = PacketFactory.createLogin(contest.getClientId(), PacketFactory.ALL_SERVERS, connectionHandlerID, clientId);
                        sendToAdministrators(loginConfirmedPacket);
                        sendToJudges(loginConfirmedPacket);
                        sendToServers(loginConfirmedPacket);
                        packetArchiver.writeNextPacket(loginConfirmedPacket);

                    } catch (SecurityException securityException) {
                        String message = securityException.getMessage();
                        sendLoginFailure(packet.getSourceId(), connectionHandlerID, message);
                    }
                } else {
                    // Security Failure??

                    if (clientId.getClientType().equals(Type.SERVER)) {
                        // Packet from a server.

                        if (packet.getType() == PacketType.Type.LOGIN_FAILED) {
                            handleServerLoginFailure(packet);
                        } else if (!contest.isLoggedIn() && packet.getType().equals(PacketType.Type.LOGIN_SUCCESS)) {

                            /**
                             * Since this module is not logged in, this packet should only be a LOGIN_SUCCESS from a server we just
                             * tried to login to. At this point we are not connected to the contest and need information from the
                             * server we logged into.
                             */

                            // TODO add a security check that this connection id matches the one we
                            // sent the login request packet to. If we don't add this, then some other
                            // server could send us a LOGIN_SUCCESS packet, which would be bad. Highly
                            // unlikely but potentially bad.
                            // Add data from packet into contest.
                            processPacket(packet, connectionHandlerID);

                            // Add the other (server we logged into) into our logged in list.
                            contest.addLocalLogin(clientId, connectionHandlerID);

                        } else {
                            
                            
                            // TODO find out why this happens
                            log.log(Log.INFO, "Packet from non-logged in server, processed anyways " + packet);

//                            // 
//                            String message = "Security violation user " + clientId + " got a " + packet;
//                            info(message + " on " + connectionHandlerID);
//                            PacketFactory.dumpPacket(System.err, packet);
                            
//                            try {
//                                
//                                packetArchiver.writeNextPacket(packet);
//                                log.info("Security violation possible spoof packet from "+clientId+" connection "+connectionHandlerID);
//                                log.info("Security violation wrote packet to "+packetArchiver+" packet "+packet);
//                            } catch (Exception e) {
//                                log.log(Log.WARNING, "Exception logged writing packet ", e);
//                            }
                            
                            
                            processPacket(packet, connectionHandlerID);
                            
                        }
                        return;
                    } else if ( clientId.getClientType().equals(Type.ADMINISTRATOR)) {
                        
                        // TODO code security kluge admin 
                        // TODO KLUDGE HUGE KLUDGE - this block allows any admin to update stuff.
                        
                        // TODO code security double check by checking their clientId and
                        // their connection id against what we have in the contest.
                        processPacket(packet, connectionHandlerID);
                        
                    } else {
                        // else

                        // TODO warning got packet but client is not logged in

                        log.log(Log.INFO, "Packet from non-logged in user, processed anyways " + packet);

//                        if (packet.getSourceId().getClientType().equals(ClientType.Type.TEAM)) {
//                            String message = "Security violation user " + clientId + " got a " + packet;
//                            info(message + " on " + connectionHandlerID);
//                            PacketFactory.dumpPacket(System.err, packet);
//                            sendSecurityVioation(clientId, connectionHandlerID, message);
//
//                        } else {
                            processPacket(packet, connectionHandlerID);

                    } 
                }
            } else {
                info("receiveObject(S,C): Unsupported class received: " + object);
            }

        } catch (Exception e) {

            // TODO Archive the packet that had an exception for future review - soon.

            info("Exception in receiveObject(S,C): " + e.getMessage(),e);
            info("Exception in receiveObject ", e);
        }
        info("receiveObject (S,C) debug end   (by "+contest.getClientId()+") got " + object.getClass().getName());
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
        PacketFactory.dumpPacket(System.err, packet);

        if (loginUI != null ){
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
    private int getServerSiteNumber(String password) {
        for (Site site : contest.getSites()) {
            if (site.getPassword().equals(password)) {
                return site.getSiteNumber();
            }
        }
        
        if (contest.getSites().length > 1 || contest.isLoggedIn()){
            throw new SecurityException("No such site or invalid site password");
        } else {
            throw new SecurityException("Does not match first site password");
        }

    }

    /**
     * Attempt to login, if login success add to login list.
     * 
     * @param clientId
     * @param password
     * @param connectionHandlerID
     */
    private void attemptToLogin(ClientId clientId, String password, ConnectionHandlerID connectionHandlerID) {

        info("attemptToLogin debug " + clientId + " pass:" + password + " " + connectionHandlerID);

        if (clientId.getClientType().equals(Type.SERVER)) {

            int newSiteNumber = getServerSiteNumber(password);

            ClientId newId = new ClientId(newSiteNumber, ClientType.Type.SERVER, 0);
            if (contest.isLocalLoggedIn(newId)) {
                info("Note site " + clientId + " site " + newSiteNumber + " already logged in, ignoring ");
            }
            contest.addLogin(newId, connectionHandlerID);

        } else if (contest.isValidLoginAndPassword(clientId, password)) {
            info("Added " + clientId);
            contest.addLogin(clientId, connectionHandlerID);
            info("attemptToLogin debug logged on: " + clientId);

        } else {

            info("attemptToLogin debug FAILED logged on: " + clientId);
            // this code will never be executed, if invalid login
            // isValidLogin will throw a SecurityException.
            throw new SecurityException("Failed attempt to login");
        }
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
            packetHandler.handlePacket(packet, connectionHandlerID);
            
        } catch (Exception e) {
            System.err.println("Exception in processPacket, check logs "); // TODO debug
            log.log(Log.WARNING, "Exception processPacket ", e);
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
     * Return an array of all logged in users.
     * 
     * @return array of clientId's.
     */
    private ClientId [] allLoggedInUsers() {
        Vector<ClientId> clientList = new Vector<ClientId>();

        for (ClientType.Type ctype : ClientType.Type.values()) {

            Enumeration<ClientId> enumeration = contest.getLoggedInClients(ctype);
            if (contest.getLoggedInClients(ctype).hasMoreElements()) {
                while (enumeration.hasMoreElements()) {
                    ClientId aClientId = (ClientId) enumeration.nextElement();
                    clientList.addElement(aClientId);
                }
            }
        }
        if (clientList.size() == 0) {
            return new ClientId[0];
        } else {
            ClientId [] clients = (ClientId[]) clientList.toArray(new ClientId[clientList.size()]);
            return clients;
        }
    }

    private void sendLoginSuccess(ClientId clientId, ConnectionHandlerID connectionHandlerID) {

        Run[] runs = null;
        Clarification[] clarifications = null;
        ProblemDataFiles [] problemDataFiles = new ProblemDataFiles[0];

        if (clientId.getClientType().equals(ClientType.Type.TEAM)) {
            runs = contest.getRuns(clientId);
            clarifications = contest.getClarifications(clientId);
        } else {
            runs = contest.getRuns();
            clarifications = contest.getClarifications();
            problemDataFiles = contest.getProblemDataFiles();
        }

        Packet packetToSend = PacketFactory.createLoginSuccess(contest.getClientId(), clientId, contest.getContestTime(), contest.getContestTimes(), contest.getSiteNumber(), 
                contest.getLanguages(), contest.getProblems(), contest.getJudgements(), contest.getSites(), runs, clarifications, 
                allLoggedInUsers(), contest.getConnectionHandleIDs(), getAllAccounts(), problemDataFiles);
        
        sendToClient(packetToSend);
    }

    /**
     * Return all accounts for all sites.
     * 
     * @return Array of all accounts in contest.
     */
    private Account[] getAllAccounts() {

        Vector<Account> allAccounts = new Vector<Account>();

        for (ClientType.Type ctype : ClientType.Type.values()) {
            if (contest.getAccounts(ctype).size() > 0) {
                Vector<Account> accounts = contest.getAccounts(ctype);
                allAccounts.addAll(accounts);
            }
        }

        Account[] accountList = (Account[]) allAccounts.toArray(new Account[allAccounts.size()]);
        return accountList;
    }

    public void connectionEstablished(ConnectionHandlerID connectionHandlerID) {
        info("connectionEstablished: "+ connectionHandlerID);
        contest.connectionEstablished(connectionHandlerID);

        Packet connectionPacket = PacketFactory.createEstablishedConnection(contest.getClientId(), PacketFactory.ALL_SERVERS, connectionHandlerID);
        sendToAdministrators(connectionPacket);
        sendToServers(connectionPacket);
    }

    /**
     * Connection to client lost.
     */
    public void connectionDropped(ConnectionHandlerID connectionHandlerID) {
        // TODO code connectionDropped reconnection logic

        ClientId clientId = contest.getLoginClientId(connectionHandlerID);
        if (clientId != null) {
            info("connectionDropped: removed user " + clientId);
            contest.removeLogin(clientId);
            try {
                Packet logoffPacket = PacketFactory.createLogoff(contest.getClientId(), PacketFactory.ALL_SERVERS, clientId);
                sendToAdministrators(logoffPacket);
                sendToJudges(logoffPacket);
                sendToServers(logoffPacket);
            } catch (Exception e) {
                log.log(Log.SEVERE, "Exception logged ", e);
            }
        } else {
            info("connectionDropped: connection " + connectionHandlerID);
        }
    }

    public void connectionError(Serializable object, ConnectionHandlerID connectionHandlerID, String causeDescription) {

        // TODO code create a packet and send it to servers and admins

        // TODO code connectionError
        info("connectionError: " + contest.getTitle() + " " + connectionHandlerID + " " + causeDescription + " "
                + object.getClass().getName());

    }

    /**
     * Client receive object.
     * 
     * @see edu.csus.ecs.pc2.core.transport.IBtoA#receiveObject(java.io.Serializable)
     */
    public void receiveObject(Serializable object) {

        info(" receiveObject(S) debug start (by "+contest.getClientId()+") "+ object);

        try {
            if (object instanceof Packet) {
                Packet packet = (Packet) object;
                PacketFactory.dumpPacket(System.err, packet);

                // TODO code put the server's connection handler id as 4th parameter
                packetHandler.handlePacket(packet, null);
            } else {
                info("receiveObject(S) Unsupported class received: " + object.getClass().getName());
            }
        } catch (Exception e) {
            // TODO: log handle exception
            String message = "Unable to start main UI, contact staff";
            
            if (loginUI != null ){
                FrameUtilities.regularCursor(loginUI);
            }
            contest.loginDenied(null, null, message);
            info ("Exception ", e);
        }
        info(" receiveObject(S) debug end   (by "+contest.getClientId()+") "+ object);
    }

    /**
     * Connection Dropped on client.
     */
    public void connectionDropped() {
        // TODO code handle client dropped
        
        // Connection dropped, countdown and die.
        CountDownMessage countDownMessage = new CountDownMessage("Shutting down PC^2 in ", 10);
        if (contest.getClientId() != null) {
            info("connectionDropped: " + contest.getClientId());
            countDownMessage.setTitle("Shutting down PC^2 " + contest.getClientId().getClientType() + " " + contest.getTitle());
        } else {
            info("connectionDropped: <non-logged in client>");
            countDownMessage.setTitle("Shutting down PC^2 Client");
        }
        countDownMessage.setExitOnClose(true);
        if (isUsingMainUI()){
            countDownMessage.setVisible(true);
        }

    }

    public void info(String s) {
        log.warning(s);
        System.err.println(Thread.currentThread().getName() + " " + s);
        System.err.flush();
    }

    public void info(String s, Exception exception) {
        log.log (Log.WARNING, s, exception);
        System.err.println(Thread.currentThread().getName() + " " + s);
        System.err.flush();
        exception.printStackTrace(System.err);
    }

    public void setSiteNumber(int number) {
        contest.setSiteNumber(number);
    }

    public void setContestTime(ContestTime contestTime) {
        contest.updateContestTime(contestTime);
    }

    public void sendToServers(Packet packet) {
        Enumeration<ClientId> clientIds = contest.getLocalLoggedInClients(ClientType.Type.SERVER);
        while (clientIds.hasMoreElements()) {
            ClientId clientId = clientIds.nextElement();
            ConnectionHandlerID connectionHandlerID = contest.getConnectionHandleID(clientId);
            boolean isThisServer = isThisSite(clientId.getSiteNumber());
            if (!isThisServer) {
                // only send to other servers
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
        Enumeration<ClientId> clientIds = contest.getLocalLoggedInClients(type);
        while (clientIds.hasMoreElements()) {
            ClientId clientId = clientIds.nextElement();
            if (isThisSite(clientId.getSiteNumber())) {
                ConnectionHandlerID connectionHandlerID = contest.getConnectionHandleID(clientId);
                sendToClient(connectionHandlerID, packet);
            }
        }
    }

    private boolean isThisSite(int siteNumber) {
        return siteNumber == contest.getSiteNumber();
    }

    public void sendToJudges(Packet packet) {
        sendPacketToClients(packet, ClientType.Type.JUDGE);
    }

    public void sendToAdministrators(Packet packet) {
        sendPacketToClients(packet, ClientType.Type.ADMINISTRATOR);
    }

    public void sendToScoreboards(Packet packet) {
        sendPacketToClients(packet, ClientType.Type.SCOREBOARD);
    }

    public void sendToTeams(Packet packet) {
        sendPacketToClients(packet, ClientType.Type.TEAM);
    }
    
    private int getPortForSite(int inSiteNumber) {

        try {
            Site []  sites = contest.getSites();
            Arrays.sort(sites, new SiteComparatorBySiteNumber());
            Site thisSite = sites[contest.getSiteNumber() - 1];
            String portStr = thisSite.getConnectionInfo().getProperty(Site.PORT_KEY);
            return Integer.parseInt(portStr);

        } catch (Exception e) {
            // TODO: log handle exception
            info("Exception logged ", e);
            throw new SecurityException("Unable to determine port for site " + inSiteNumber);
        }
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
                info("Started Server Transport listening on " + port);
                transportManager.accecptConnections(port);

                info("Secondary Server has started " + contest.getTitle());
            }

            try {
                if (isUsingMainUI()) {
                    if (uiPlugin == null) {
                        // NO UI to display, so let's find one to display
                        
                        String uiClassName = LoadUIClass.getUIClassName(clientId);
                        if (uiClassName == null) {
                            info("Unable to find UI class for " + clientId.getClientType().toString().toLowerCase());
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
                // TODO: log handle exception
                System.err.println("Error loading UI, check log, (class not found?)  " + e.getMessage());
                info("Exception loading UI for (class not found?) " + clientId.getName(), e);
                throw new Exception("Unable to start main UI, contact staff");
            }

        } catch (Exception e) {
            // TODO log this
            info("Trouble showing frame ", e);
            if (loginUI != null ){
                FrameUtilities.regularCursor(loginUI);
            }
            contest.loginDenied(clientId, null, e.getMessage());
        }
    }

     /**
     * Start the UI.
     */
    public void start(String[] stringArray) {

        log = new Log("pc2.startup");
        StaticLog.setLog (log);
        
        /**
         * Saved exception.
         * 
         * If TransportException thrown before UI has been created,
         * save the exception and present it on the UI later.
         */
        TransportException savedTransportException = null;
        
        String[] arguments = { "--login", "--id", "--password", "--loginUI", "--remoteServer", "--server", "--port", "--nosave" };
        parseArguments = new ParseArguments(stringArray, arguments);
        
        if (parseArguments.isOptPresent("--help")){
            System.out.println("Usage: Starter [--login <login>] [--password <pass>] [--help]");
            System.out.println("where <login> is a user name");
            // TODO un-comment this when --server works. 
//            System.out.println("Usage: Starter [--login <login>] [--help] [--server] ");
            System.exit(0);
        }
        
        for (String arg : stringArray){
            if (arg.equals("--first")){
                setContactingRemoteServer(false);
            }
        }
        
        // TODO parse arguments logic 
        
        /**
         * if (args DOES NOT contains login/pwd) {
         *   String s;
         *   if (args contains LoginUI )
         *   {
         *      s = args login UI
         *    }
         *    else
         *    {
         *        s = pc2 LoginFrame
         *    }
         *    UIPlugin l = classloader (s);
         *    l.setModelAndListener (contest, this);
         * }
         * else {
         *   this.login (login,password)
         * 
         */
        
        log.info("Starting TransportManager...");
        transportManager = new TransportManager(log);
        log.info("Started TransportManager");
        
        if (IniFile.isFilePresent()) {
            // Only read and load .ini file if it is present.
            new IniFile();
        }
        
        if (parseArguments.isOptPresent("--nosave")){
            saveCofigurationToDisk = false;
        }


        if ( parseArguments.isOptPresent("--server")) {
            info("Starting Server Transport...");
            transportManager.startServerTransport(this);
            serverModule = true;
        } else {
            // Client contact server 
            setClientServerAndPort();
            info("Contacting server at " + remoteHostName + ":" + port);
            transportManager.startClientTransport(remoteHostName, port, this);
            try {
                transportManager.connectToMyServer();
            } catch (TransportException transportException) {
                savedTransportException = transportException;
                log.log(Log.INFO, "Exception logged ", transportException);
                
            }
        }
        
        isStarted = true;
        
        if (  ! parseArguments.isOptPresent(LOGIN_OPTION_STRING)){

            // TODO: code handle alternate Login UI.
            
//            if ( parseArguments.isOptPresent(LOGIN_UI_OPTION_STRING)) {
//                String loginUIName = parseArguments.getOptValue(LOGIN_UI_OPTION_STRING);
//                // TODO: load Login UI
////                loginUI = LoadUIClass.loadUIClass(loginUIName);
//            } else {
//              
//            }
            if (isUsingMainUI()){
                loginUI = new LoginFrame();
                loginUI.setContestAndController(contest, this);
            }
            
        } else {
            // has a login, go for it.
            
            
            // Get loginId
            String loginName = "";
            if ( parseArguments.isOptPresent(LOGIN_OPTION_STRING)) {
                loginName = parseArguments.getOptValue(LOGIN_OPTION_STRING);
            }
            
            // get password (optional if joe password)
            String password = "";
            if ( parseArguments.isOptPresent(PASSWORD_OPTION_STRING)) {
                password = parseArguments.getOptValue(PASSWORD_OPTION_STRING);
            }
            
            if (isUsingMainUI()){
                loginUI = new LoginFrame();
                loginUI.setContestAndController(contest, this); // this displays the login
            } 
            
            try {

                if (savedTransportException == null){
                    login (loginName, password);  // starts login attempt, will show failure to LoginFrame
                }

            } catch (Exception e) {
                log.log(Log.INFO, "Exception logged ", e);
                if (loginUI != null){
                    loginUI.setStatusMessage(e.getMessage());
                }
            }
            
            if (savedTransportException != null && loginUI != null){
                loginUI.setStatusMessage("Unable to contact server, contact staff");
            }
        
        }
    }

    private ClientId getServerClientId(){
//      TODO s/new ClientId(contest.getSiteNumber(), Type.SERVER, 0);/getServerClientId()/
        return new ClientId (contest.getSiteNumber(), Type.SERVER, 0);
    }

    public void checkOutRun(Run run, boolean readOnly) {
        ClientId clientId = contest.getClientId();
        Packet packet = PacketFactory.createRunRequest(clientId,getServerClientId(),run, clientId, readOnly);
        sendToLocalServer(packet);
    }

    /**
     * Send run judgement to server.
     */
    public void submitRunJudgement(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles) {
        ClientId clientId = contest.getClientId();
        Packet packet = PacketFactory.createRunJudgement(clientId,getServerClientId(), run, judgementRecord, runResultFiles);
        sendToLocalServer(packet);
    }

    /**
     * Send cancel run to server.
     */
    public void cancelRun(Run run) {
        ClientId clientId = contest.getClientId();
        Packet packet = PacketFactory.createUnCheckoutRun(clientId, getServerClientId(), run);
        sendToLocalServer(packet);
    }

    /**
     * Add a new site into contest, send update to other servers.
     */
    public void addNewSite(Site site) {
        if (isServer ()){
            contest.addSite(site);
            writeConfigToDisk();
            Packet packet = PacketFactory.createAddSetting(contest.getClientId(), PacketFactory.ALL_SERVERS, site);
            sendToServers(packet);
            
            sendToJudges(packet);
            sendToAdministrators(packet);
            sendToScoreboards(packet);
        } else {
            Packet packet = PacketFactory.createAddSetting(contest.getClientId(), getServerClientId(), site);
            sendToLocalServer(packet);
        }
    }
    
    /**
     * Modify an existing site, send update to other servers.
     */
    public void modifySite (Site site) {
        contest.changeSite(site);
        Packet packet = PacketFactory.createUpdateSetting(getServerClientId(), PacketFactory.ALL_SERVERS, site);
        sendToServers(packet);
    }

    /**
     * Server login to other sites.
     * 
     * Contacts the other site and sends a login request.
     */
    public void sendServerLoginRequest(int inSiteNumber) {
        
        if (isThisSite(inSiteNumber)) {
            /**
             * We should not send a LOGIN_REQUEST from this site... to this site.
             */
            System.err.println(" Tried to send login request to ourselves, login to "+inSiteNumber+", ignored");
            log.log(Log.DEBUG, " Tried to send login request to ourselves, login to "+inSiteNumber+", ignored");
            return;
        }

        try {
            Site remoteSite = contest.getSite(inSiteNumber);
            Site localSite = contest.getSite(contest.getSiteNumber());
            String localPassword = localSite.getPassword();

            String hostName = remoteSite.getConnectionInfo().getProperty(Site.IP_KEY);
            String portStr = remoteSite.getConnectionInfo().getProperty(Site.PORT_KEY);
            int portNumber = Integer.parseInt(portStr);

            info("Send login request to Site " + remoteSite.getSiteNumber() + " " + hostName + ":" + portStr);
            ConnectionHandlerID connectionHandlerID = transportManager.connectToServer(hostName, portNumber);

            info("Contacted Site "+remoteSite.getSiteNumber()+ " using connection id " + connectionHandlerID);
            info("Sending login request to Site "+remoteSite.getSiteNumber()+" " + hostName + " as " + getServerClientId() + " " + localPassword); // TODO remove this
            sendLoginRequest(transportManager, connectionHandlerID, getServerClientId(), localPassword);
            
        } catch (Exception e) {
            // TODO: log handle exception
            info("Unable to login to site "+inSiteNumber, e);
        }

    }

    /**
     * Contacting remote server (joining contest).
     * @return true if joining contest, false if first server
     */
    public boolean isContactingRemoteServer() {
        return contactingRemoteServer && containsINIKey(REMOTE_SERVER_KEY);
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

        // TODO seems to be circular, make finite.
        if (isServer ()){
            contest.changeSite(site);
            Packet packet = PacketFactory.createUpdateSetting(contest.getClientId(), PacketFactory.ALL_SERVERS, site);
            sendToServers(packet);
            
            sendToJudges(packet);
            sendToAdministrators(packet);
            sendToScoreboards(packet);
        } else {
            Packet packet = PacketFactory.createUpdateSetting(contest.getClientId(), getServerClientId(), site);
            sendToLocalServer(packet);
        }
    }

    /**
     * Returns true if this client is a server.
     * @return true if logged in client is a server.
     */
    private boolean isServer() {
        return contest.getClientId().getClientType().equals(ClientType.Type.SERVER);
    }

    public final Log getLog() {
        return log;
    }

    public void generateNewAccounts(String clientTypeName, int siteNumber, int count, int startNumber, boolean active) {
        ClientType.Type type = ClientType.Type.valueOf(clientTypeName);
        Packet packet = PacketFactory.createGenerateAccounts(contest.getClientId(), getServerClientId(), siteNumber, type, count, startNumber,  active);
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
        // TODO Auto-generated method stub
        
    }

    public void cancelClarification(Clarification clarification) {
        // TODO Auto-generated method stub
        
    }

    public void submitClarificationAnswer(Clarification clarification) {
        ClientId serverClientId = new ClientId(contest.getSiteNumber(), Type.SERVER, 0);
        Packet packet = PacketFactory.createClarificationSubmission(contest.getClientId(), serverClientId, clarification);

        sendToLocalServer(packet);
    }


    public void forceConnectionDrop(ConnectionHandlerID connectionHandlerID) {
        transportManager.unregisterConnection(connectionHandlerID);
        contest.connectionDropped(connectionHandlerID);
        Packet disconnectionPacket = PacketFactory.createDroppedConnection(contest.getClientId(), PacketFactory.ALL_SERVERS, connectionHandlerID);
        sendToAdministrators(disconnectionPacket);
        sendToServers(disconnectionPacket);
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
        transportManager.shutdownTransport();
    }

    public void removeConnection(ConnectionHandlerID connectionHandlerID) {
        contest.connectionDropped(connectionHandlerID);
    }

    public void removeLogin(ClientId clientId) {
        contest.removeLogin(clientId);
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
        Packet packet = PacketFactory.createStartAllClocks(contest.getClientId(), getServerClientId(),  contest.getClientId());
        sendToLocalServer(packet);
    }

    public void stopAllContestTimes() {
        Packet packet = PacketFactory.createStopAllClocks(contest.getClientId(), getServerClientId(),  contest.getClientId());
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
    
    public void updateJudgement (Judgement judgement) {
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
    
    public void writeConfigToDisk (){
        
        if (saveCofigurationToDisk) {
            try {
                configurationIO.saveToDisk(1, contest, getLog());
            } catch (IOException e) {
                System.err.println("Unable to write configuration to disk " + e.getMessage());
                getLog().log(Log.SEVERE, "Error logging to disk ", e);
            }
        }
    }
    
}
