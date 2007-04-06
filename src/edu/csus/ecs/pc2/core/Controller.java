package edu.csus.ecs.pc2.core;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
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
import edu.csus.ecs.pc2.ui.LoadUIClass;
import edu.csus.ecs.pc2.ui.LoginFrame;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Implementation of Contest Controller.
 * 
 * Run Flow, submit run.
 * <ol>
 * <li> Team: {@link #submitRun(Problem, Language, String)}
 * <li> Server: {@link edu.csus.ecs.pc2.core.PacketHandler#handlePacket(IController, IModel, Packet, ConnectionHandlerID)}
 * <li> Server: {@link edu.csus.ecs.pc2.core.model.Model#acceptRun(Run, RunFiles)}
 * <li> Team: {@link edu.csus.ecs.pc2.core.model.IRunListener#runAdded(RunEvent)} RunEvent action is:
 * {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#ADDED}
 * <li> Client: {@link edu.csus.ecs.pc2.core.model.IRunListener#runAdded(RunEvent)} RunEvent action is:
 * {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#ADDED}
 * </ol>
 * Check out run
 * <ol>
 * <li> Judge: {@link #checkOutRun(Run)}
 * <li> Server: {@link edu.csus.ecs.pc2.core.PacketHandler#requestRun(Run, IModel, IController, ClientId)}
 * <li> Judge and clients: {@link edu.csus.ecs.pc2.core.model.IRunListener#runChanged(RunEvent)}, check
 * {@link edu.csus.ecs.pc2.core.model.RunEvent#getSentToClientId()} to learn if you are the judge/client to get the run. RunEvent
 * action is: {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#CHECKEDOUT_RUN}
 * </ol>
 * Submit Judgement
 * <ol>
 * <li> Judge: {@link #submitRunJudgement(Run, JudgementRecord, RunResultFiles)}
 * <li> Server:
 * {@link edu.csus.ecs.pc2.core.PacketHandler#judgeRun(Run, IModel, IController, JudgementRecord, RunResultFiles, ClientId)}
 * <li> Team: {@link edu.csus.ecs.pc2.core.model.IRunListener#runChanged(RunEvent)} if {@link Run#isSendToTeams()} set true.
 * RunEvent action is: {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#UPDATED}
 * <li> Clients: {@link edu.csus.ecs.pc2.core.model.IRunListener#runChanged(RunEvent)} RunEvent action is:
 * {@link edu.csus.ecs.pc2.core.model.RunEvent.Action#UPDATED}
 * </ol>
 * Cancel Run
 * <ol>
 * <li> Judge: {@link #cancelRun(Run)}
 * <li> Server: {@link edu.csus.ecs.pc2.core.PacketHandler#cancelRun(Run, IModel, IController, ClientId)}
 * <li> Team: n/a
 * <li> Judge/Clients: {@link edu.csus.ecs.pc2.core.model.IRunListener#runChanged(RunEvent)}. RunEvent action is:
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
    private IModel model;

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
    private UIPlugin mainUI;

    private Log log;

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

    // TODO change this to UIPlugin
    private LoginFrame loginUI;

    public Controller(IModel model) {
        super();
        this.model = model;
    }

    private void sendToServer(Packet packet) {
        try {
            transportManager.send(packet);
        } catch (TransportException e) {
            info("Unable to send to Server  " + packet);
            e.printStackTrace();
        }
    }

    private void sendToClient(ConnectionHandlerID connectionHandlerID, Packet packet) {
        try {
            transportManager.send(packet, connectionHandlerID);
        } catch (TransportException e) {
            info("Unable to send to " + connectionHandlerID + " packet " + packet);
            e.printStackTrace();
        }
    }

    public void sendToClient(Packet packet) {
        info(" sendToClient b4 " + packet);
        ConnectionHandlerID connectionHandlerID = model.getConnectionHandleID(packet.getDestinationId());
        info("sendToClient " + packet.getSourceId() + " " + connectionHandlerID);
        if (connectionHandlerID == null) {
            sendToServer(packet);
        } else {
            sendToClient(connectionHandlerID, packet);
        }

        info(" sendToClient af " + packet);
    }

    public void submitRun(Problem problem, Language language, String filename) throws Exception {
        SerializedFile serializedFile = new SerializedFile(filename);

        ClientId serverClientId = new ClientId(model.getSiteNumber(), Type.SERVER, 0);
        Run run = new Run(model.getClientId(), language, problem);
        RunFiles runFiles = new RunFiles(run, serializedFile, null);

        Packet packet = PacketFactory.createSubmittedRun(model.getClientId(), serverClientId, run, runFiles);

        sendToServer(packet);
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
        } else if (loginName.startsWith("j") && loginName.length() > 1) {
            int number = getIntegerValue(loginName.substring(1));
            return new ClientId(defaultSiteNumber, Type.JUDGE, number);
        } else if (loginName.startsWith("t") && loginName.length() > 4) {
            int number = getIntegerValue(loginName.substring(4));
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

        try {

            ClientId clientId = loginShortcutExpansion(0, id);

            log = new Log(clientId.toString());
            StaticLog.setLog(log);

            info("");
            info(new VersionInfo().getSystemVersionInfo());
            info(" login(" + id + "," + password + ")");

            if (password.length() < 1) {
                password = clientId.getName(); // Joe password.
                if (clientId.getClientType().equals(Type.SERVER)) {
                    password = "site" + clientId.getSiteNumber();
                }
            }

            if (IniFile.isFilePresent()) {
                // Only read and load .ini file if it is present.
                new IniFile();
            }

            port = Integer.parseInt(TransportManager.DEFAULT_PC2_PORT);

            if (clientId.getClientType().equals(Type.SERVER)) {

                if (containsINIKey(SERVER_PORT_KEY)) {
                    port = Integer.parseInt(getINIValue(SERVER_PORT_KEY));
                }

                info("Starting Server Transport...");
                transportManager = new TransportManager(log, this);

                if (containsINIKey(REMOTE_SERVER_KEY)) {

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
                    remoteServerConnectionHandlerID = transportManager.connectToServer(remoteHostName, remoteHostPort);

                    info("Contacted using connection id " + remoteServerConnectionHandlerID);

                    info("Sending login request to " + remoteHostName + " as " + clientId + " " + password); // TODO remove this
                    sendLoginRequest(transportManager, remoteServerConnectionHandlerID, clientId, password);

                } else {

                    clientId = authenticateFirstServer(password);
                    info("Started Server Transport listening on " + port);
                    transportManager.accecptConnections(port);
                    info("Primary Server has started.");
                    startMainUI(clientId);
                }

            } else {

                // Client login

                remoteHostName = "localhost";

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

                try {
                    info("Contacting server at " + remoteHostName + ":" + port);
                    transportManager = new TransportManager(log, remoteHostName, port, this);
                    transportManager.connectToMyServer();
                    info("Started Client Transport");

                    sendLoginRequest(transportManager, clientId, password);
                } catch (TransportException e) {
                    // TODO: log make this a very silent log entry.
                    StaticLog.log("Exception starting up ", e);
                    throw new SecurityException("Unable to contact server, contact staff");
                }
            }
        } catch (TransportException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            StaticLog.log("Exception on startup ", e);
            throw new SecurityException("Unable to start server, check logs");
        }
    }

    private ClientId authenticateFirstServer(String password) {
        
        if (model.getSites().length == 0){
            // TODO remove this when we can populate with real data.
            model.initializeWithFakeData();
        }
        int newSiteNumber = getServerSiteNumber(password);

        ClientId newId = new ClientId(newSiteNumber, ClientType.Type.SERVER, 0);
        if (model.isLoggedIn(newId)) {
            info("Note site " + newId + " site " + newSiteNumber + " already logged in, ignoring ");
        }
        ConnectionHandlerID connectionHandlerID = new ConnectionHandlerID("Site " + newSiteNumber);
        model.addLogin(newId, connectionHandlerID);
        return newId;
    }

    private static void sendLoginRequest(ITransportManager manager, ConnectionHandlerID connectionHandlerID, ClientId clientId,
            String password) {
        try {
            info("sendLoginRequest ConId start - sending from " + clientId);
            ClientId serverClientId = new ClientId(0, Type.SERVER, 0);
            Packet loginPacket = PacketFactory.createLogin(clientId, password, serverClientId);
            manager.send(loginPacket, connectionHandlerID);
            info("sendLoginRequest ConId end - packet sent.");
        } catch (TransportException e) {
            // TODO log exception
            e.printStackTrace();
        }
    }

    /**
     * Send login request to server.
     * 
     * @param manager
     * @param clientId
     * @param password
     */
    private static void sendLoginRequest(ITransportManager manager, ClientId clientId, String password) {
        try {
            info("sendLoginRequest start - sending from "+clientId);
            ClientId serverClientId = new ClientId(0, Type.SERVER, 0);
            Packet loginPacket = PacketFactory.createLogin(clientId, password, serverClientId);
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
        info("receiveObject (S,C) debug start : Processing " + object.getClass().getName());

        try {

            if (object instanceof Packet) {

                Packet packet = (Packet) object;
                ClientId clientId = packet.getSourceId();

                info("receiveObject " + packet);
                if (model.isLoggedIn(packet.getSourceId())) {

                    /**
                     * This user is in the login list and we process their request.
                     */

                    // TODO code security double check by checking their clientId and
                    // their connection id against what we have in the model.
                    processPacket(packet, connectionHandlerID);

                } else if (packet.getType().equals(PacketType.Type.LOGIN_REQUEST)) {
                    String password = PacketFactory.getStringValue(packet, PacketFactory.PASSWORD);
                    try {

                        /**
                         * Login request from client or other server. When this block is done, they are logged in and a login
                         * success is sent to them.
                         */

                        if (clientId.getSiteNumber() == ClientId.UNSET) {
                            clientId = new ClientId(model.getSiteNumber(), clientId.getClientType(), clientId.getClientNumber());
                        }
                        attemptToLogin(clientId, password, connectionHandlerID);
                        sendLoginSuccess(clientId, connectionHandlerID);

                    } catch (SecurityException securityException) {
                        String message = securityException.getMessage();
                        sendLoginFailure(packet.getSourceId(), connectionHandlerID, message);
                    }
                } else {
                    // Security Failure

                    if (clientId.getClientType().equals(Type.SERVER)) {
                        // Packet from a server.

                        if (packet.getType() == PacketType.Type.LOGIN_FAILED) {
                            handleServerLoginFailure(packet);
                        } else if (!model.isLoggedIn() && packet.getType().equals(PacketType.Type.LOGIN_SUCCESS)) {

                            /**
                             * Since this module is not logged in, this packet should only be a LOGIN_SUCCESS from a server we just
                             * tried to login to. At this point we are not connected to the contest and need information from the
                             * server we logged into.
                             */

                            // TODO add a security check that this connection id matches the one we
                            // sent the login request packet to. If we don't add this, then some other
                            // server could send us a LOGIN_SUCCESS packet, which would be bad. Highly
                            // unlikely but potentially bad.
                            // Add data from packet into model.
                            processPacket(packet, connectionHandlerID);

                            // Add the other (server we logged into) into our logged in list.
                            model.addLogin(clientId, connectionHandlerID);

                        } else if (model.isLoggedIn() && packet.getType().equals(PacketType.Type.LOGIN_SUCCESS)) {

                            /**
                             * This server is logged in, so this must be from a server we attempted to login to. We don't want their
                             * information because we are already connected to other servers in the contest.
                             */

                            // Add the other (server we logged into) into our logged in list.
                            model.addLogin(clientId, connectionHandlerID);

                        } else {
                            String message = "Security violation user " + clientId + " got a " + packet;
                            info(message + " on " + connectionHandlerID);
                            PacketFactory.dumpPacket(System.err, packet);
                        }
                        return;
                    }
                    // else
                    // We don't know why they send us these things...

                    String message = "Security violation user " + clientId + " got a " + packet;
                    info(message + " on " + connectionHandlerID);
                    PacketFactory.dumpPacket(System.err, packet);
                    sendSecurityVioation(clientId, connectionHandlerID, message);
                }
            } else {
                info("receiveObject(S,C): Unsupported class received: " + object.getClass().getName());
            }

        } catch (Exception e) {

            // TODO Archive the packet that had an exception for future review - soon.

            System.err.println("Exception in receiveObject(S,C): " + e.getMessage());
            StaticLog.unclassified("Exception in receiveObject ", e);
        }
        info("receiveObject (S,C) debug end   : Processing " + object.getClass().getName());
    }

    private void handleServerLoginFailure(Packet packet) {
        // TODO rewrite handle this failure better
        
        String message = PacketFactory.getStringValue(packet, PacketFactory.MESSAGE_STRING);

        // TODO Handle this better via new login code.
        info("Login Failed: " + message);
        info("Login Failure");
        PacketFactory.dumpPacket(System.err, packet);

        model.loginDenied(packet.getDestinationId(), null, message);

    }

    /**
     * Looks up site number based on password.
     * 
     * @param password
     * @return site number or throws SecurityException if nothing matches.
     */
    private int getServerSiteNumber(String password) {
        for (Site site : model.getSites()) {
            if (site.getPassword().equals(password)) {
                return site.getSiteNumber();
            }
        }

        throw new SecurityException("No such site or invalid password");
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
            if (model.isLoggedIn(newId)) {
                info("Note site " + clientId + " site " + newSiteNumber + " already logged in, ignoring ");
            }
            model.addLogin(newId, connectionHandlerID);

        } else if (model.isValidLoginAndPassword(clientId, password)) {
            info("Added " + clientId);
            model.addLogin(clientId, connectionHandlerID);
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
        PacketHandler.handlePacket(this, model, packet, connectionHandlerID);

    }

    /**
     * Send login failure packet back to non-logged in user, via ConnectionHandlerID.
     * 
     * @param destinationId
     * @param connectionHandlerID
     * @param message
     */
    private void sendLoginFailure(ClientId destinationId, ConnectionHandlerID connectionHandlerID, String message) {
        Packet packet = PacketFactory.createLoginDenied(model.getClientId(), destinationId, message);
        sendToClient(connectionHandlerID, packet);
    }

    /**
     * Send login failure packet back to non-logged in user, via ConnectionHandlerID.
     * 
     * @param destinationId
     * @param connectionHandlerID
     * @param message
     */
    private void sendSecurityVioation(ClientId destinationId, ConnectionHandlerID connectionHandlerID, String message) {
        Packet packet = PacketFactory.createMessage(model.getClientId(), destinationId, message);
        sendToClient(connectionHandlerID, packet);
    }

    private void sendLoginSuccess(ClientId clientId, ConnectionHandlerID connectionHandlerID) {
        Packet packetToSend = PacketFactory.createLoginSuccess(model.getClientId(), clientId, model.getContestTime(), model
                .getSiteNumber(), model.getLanguages(), model.getProblems(), model.getJudgements(), model.getSites(), model
                .getRuns());
        sendToClient(packetToSend);
    }

    public void connectionEstablished(ConnectionHandlerID connectionHandlerID) {
        // TODO code connectionEstablished
        info("connectionEstablished: "+ connectionHandlerID);
    }

    /**
     * Connection to client lost.
     */
    public void connectionDropped(ConnectionHandlerID connectionHandlerID) {
        // TODO code connectionDropped reconnection logic
        // TODO code create a packet and send it to servers and admins

        ClientId clientId = model.getLoginClientId(connectionHandlerID);
        if (clientId != null) {
            info("connectionDropped: removed user " + clientId);
            model.removeLogin(clientId);
        } else {
            info("connectionDropped: connection " + connectionHandlerID);
        }
    }

    public void connectionError(Serializable object, ConnectionHandlerID connectionHandlerID, String causeDescription) {

        // TODO code create a packet and send it to servers and admins

        // TODO code connectionError
        info("connectionError: " + model.getTitle() + " " + connectionHandlerID + " " + causeDescription + " "
                + object.getClass().getName());

    }

    /**
     * Client receive object.
     * 
     * @see edu.csus.ecs.pc2.core.transport.IBtoA#receiveObject(java.io.Serializable)
     */
    public void receiveObject(Serializable object) {

        info(" receiveObject(S) debug start " + object.getClass().getName());

        try {
            if (object instanceof Packet) {
                Packet packet = (Packet) object;
                PacketFactory.dumpPacket(System.err, packet);

                // TODO code put the server's connection handler id as 4th parameter
                PacketHandler.handlePacket(this, model, packet, null);
            } else {
                info("receiveObject(S) Unsupported class received: " + object.getClass().getName());
            }
        } catch (Exception e) {
            // TODO: log handle exception
            StaticLog.unclassified("Exception logged ", e);
        }
        info(" receiveObject(S) debug end   " + object.getClass().getName());

    }

    /**
     * Connection Dropped on client.
     */
    public void connectionDropped() {
        // TODO code handle client dropped
        
        // Connection dropped, countdown and die.
        CountDownMessage countDownMessage = new CountDownMessage("Shutting down PC^2 in ", 10);
        if (model.getClientId() != null) {
            info("connectionDropped: " + model.getClientId());
            countDownMessage.setTitle("Shutting down PC^2 " + model.getClientId().getClientType() + " " + model.getTitle());
        } else {
            info("connectionDropped: <non-logged in client>");
            countDownMessage.setTitle("Shutting down PC^2 Client");
        }
        countDownMessage.setExitOnClose(true);
        countDownMessage.setVisible(true);

    }

    public static void info(String s) {
        StaticLog.unclassified(s);
        System.err.println(Thread.currentThread().getName() + " " + s);
    }

    public static void info(String s, Exception exception) {
        StaticLog.unclassified (s, exception);
        System.err.println(Thread.currentThread().getName() + " " + s);
        exception.printStackTrace(System.err);
    }

    public void setSiteNumber(int number) {
        model.setSiteNumber(number);
    }

    public void setContestTime(ContestTime contestTime) {
        // TODO code
    }

    public void sendToServers(Packet packet) {
        sendPacketToClients(packet, ClientType.Type.SERVER);
    }

    /**
     * Send packet to all logged in server.
     * 
     * @param packet
     */
    private void sendPacketToClients(Packet packet, ClientType.Type type) {
        Enumeration<ClientId> clientIds = model.getLoggedInClients(type);
        while (clientIds.hasMoreElements()) {
            ClientId clientId = clientIds.nextElement();
            ConnectionHandlerID connectionHandlerID = model.getConnectionHandleID(clientId);
            if (isThisSite(clientId.getSiteNumber())) {
                sendToClient(connectionHandlerID, packet);
            }
        }

    }

    private boolean isThisSite(int siteNumber) {
        return siteNumber == model.getSiteNumber();
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
        sendPacketToClients(packet, ClientType.Type.SCOREBOARD);
    }

    /**
     * Client has successfully logged in, show them new UI.
     * 
     * @param clientId
     *            new client id
     */
    public void startMainUI(ClientId clientId) {

        try {

            model.setClientId(clientId);

            if (containsINIKey(REMOTE_SERVER_KEY)) {
                // secondary server logged in, start listening.
                info("Started Server Transport listening on " + port);
                transportManager.accecptConnections(port);

                info("Secondary Server has started " + model.getTitle());
            }

            try {
                String uiClassName = LoadUIClass.getUIClassName(clientId);
                mainUI = LoadUIClass.loadUIClass(uiClassName);
                mainUI.setModelAndController(model, this);
                loginUI.dispose();
            } catch (Exception e) {
                // TODO: log handle exception
                System.err.println("Error loading UI, check log, (class not found?)  " + e.getMessage());
                StaticLog.log("Exception loading UI for (class not found?) " + clientId.getName(), e);
                throw new Exception("Unable to start main UI, contact staff");
            }

        } catch (Exception e) {
            // TODO log this
            info("Trouble showing frame ", e);
            model.loginDenied(clientId, null, e.getMessage());
        }
    }

    /**
     * Start the UI.
     */
    public void start(String[] stringArray) {

        String[] arguments = { "--site", "--login", "--id", "--password", "--loginUI" };
        parseArguments = new ParseArguments(stringArray, arguments);

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
         *    l.setModelAndListener (model, this);
         * }
         * else {
         *   this.login (login,password)
         * 
         */

        log = new Log("pc2.startup");
        StaticLog.setLog(log);

        loginUI = new LoginFrame();
        loginUI.setModelAndController(model, this);

    }

    /**
     * Dump connection data
     */
    public void dumpContestData() {

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.SSS");
            // "yyMMdd HHmmss.SSS");
            String filename = "dump" + simpleDateFormat.format(new Date()) + ".log";
            PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false), true);

            writer.println(new VersionInfo().getSystemName());
            writer.println("Build " + new VersionInfo().getBuildNumber());
            writer.println(new VersionInfo().getSystemVersionInfo());
            writer.println("Date: " + new Date());

            writer.println();
            writer.println("-- Accounts --");
            for (ClientType.Type ctype : ClientType.Type.values()) {
                if (model.getAccounts(ctype).size() > 0) {
                    writer.println("Accounts " + ctype.toString() + " there are " + model.getAccounts(ctype).size());
                    Vector<Account> accounts = model.getAccounts(ctype);
                    for (int i = 0; i < accounts.size(); i++) {
                        writer.println("   " + accounts.elementAt(i));
                    }
                }

            }

            // Sites
            writer.println();
            writer.println("-- " + model.getSites().length + " sites --");
            for (Site site1 : model.getSites()) {
                writer.println("Site " + site1.getSiteNumber() + " " + site1.getDisplayName() + "/" + site1.getPassword());
            }

            // Problem
            writer.println();
            writer.println("-- " + model.getProblems().length + " problems --");
            for (Problem problem : model.getProblems()) {
                writer.println("  Problem " + problem);
            }

            // Language
            writer.println();
            writer.println("-- " + model.getLanguages().length + " languages --");
            for (Language language : model.getLanguages()) {
                writer.println("  Language " + language);
            }

            // Logins
            writer.println();
            writer.println("-- Logins -- ");
            for (ClientType.Type ctype : ClientType.Type.values()) {

                Enumeration<ClientId> enumeration = model.getLoggedInClients(ctype);
                if (model.getLoggedInClients(ctype).hasMoreElements()) {
                    writer.println("Logged in " + ctype.toString());
                    while (enumeration.hasMoreElements()) {
                        ClientId aClientId = (ClientId) enumeration.nextElement();
                        writer.println("   " + aClientId);
                    }
                }
            }

            writer.println();
            writer.println("Current clientId is " + model.getClientId());

            writer.println();
            writer.println("*end*");

            writer.close();
            writer = null;

            String command = "/windows/vi.bat " + filename;
            Runtime.getRuntime().exec(command);

        } catch (Exception e) {
            // TODO: writer handle exception
            info("Exception logged ", e);
        }

    }

    private ClientId getServerClientId(){
        return new ClientId (model.getSiteNumber(), Type.SERVER, 0);
    }

    /**
     * Send checkout run request to server.
     */
    public void checkOutRun(Run run) {
        ClientId clientId = model.getClientId();
        Packet packet = PacketFactory.createRunRequest(clientId,getServerClientId(),run.getElementId(), clientId);
        sendToServer(packet);
    }

    /**
     * Send run judgement to server.
     */
    public void submitRunJudgement(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles) {
        ClientId clientId = model.getClientId();
        Packet packet = PacketFactory.createRunJudgement(clientId,getServerClientId(), run, judgementRecord, runResultFiles);
        sendToServer(packet);
    }

    /**
     * Send cancel run to server.
     */
    public void cancelRun(Run run) {
        ClientId clientId = model.getClientId();
        Packet packet = PacketFactory.createUnCheckoutRun(clientId, getServerClientId(), run);
        sendToServer(packet);
    }

}
