package edu.csus.ecs.pc2.core;

import java.io.Serializable;
import java.util.Enumeration;

import javax.swing.JOptionPane;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Model;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
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

/**
 * 
 * @author pc2@ecs.csus.edu *
 */
// $HeadURL$
public class Controller implements IController, ITwoToOne, IBtoA {

    public static final String SVN_ID = "$Id$";

    private IModel model;

    private static ITransportManager transportManager;

    public static Controller controller;

    private static Log log;

    private static IBtoA btoA;

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
        sendToClient(connectionHandlerID, packet);
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

    /**
     * Login to contest server.
     * 
     * @param id the login name.
     * @param password the password for the id.
     * @return model (contest data) if login successful
     * @throws Exception if there is a problem contacting server or logging in.
     */
    public static IModel login(String id, String password, int tmpSiteNum) throws Exception {

        ClientId clientId;

        if (id.equals("t")) {
            id = "team1";
        }

        if (id.startsWith("s")) {
            clientId = new ClientId(tmpSiteNum, Type.SERVER, 0);
        } else if (id.startsWith("judge") && id.length() > 5) {
            int number = getIntegerValue(id.substring(5));
            clientId = new ClientId(tmpSiteNum, Type.JUDGE, number);
        } else if (id.startsWith("j") && id.length() > 1) {
            int number = getIntegerValue(id.substring(1));
            clientId = new ClientId(tmpSiteNum, Type.JUDGE, number);
        } else if (id.startsWith("t") && id.length() > 4) {
            int number = getIntegerValue(id.substring(4));
            clientId = new ClientId(tmpSiteNum, Type.TEAM, number);
        } else if (Character.isDigit(id.charAt(0))) {
            int number = getIntegerValue(id);
            clientId = new ClientId(tmpSiteNum, Type.TEAM, number);
        } else {
            throw new SecurityException("No such account " + id);
        }

        log = new Log(clientId.toString());
        StaticLog.setLog(log);  // From this point forward any class can use StaticLog.
        info("");
        info(new VersionInfo().getSystemVersionInfo());
        info(" login(" + id + "," + password + ")");

        if (password.length() < 1) {
            password = clientId.getName(); // Joe password.
        }

        Model model = new Model();
        controller = new Controller(model);
        btoA = controller;

        if (IniFile.isFilePresent()){
            // Only read and load .ini file if it is present.
            new IniFile();
        }

        port = Integer.parseInt(TransportManager.DEFAULT_PC2_PORT);
        
        model.setClientId(clientId);  

        if (clientId.getClientType().equals(Type.SERVER)) {

            model.initializeWithFakeData();
            
            if (containsINIKey(SERVER_PORT_KEY)) {
                port = Integer.parseInt(getINIValue(SERVER_PORT_KEY));
            }

            info("Starting Server Transport...");
            transportManager = new TransportManager(log, controller);

            if (containsINIKey(REMOTE_SERVER_KEY)) {

                // Contacting another server. "join"
                if (containsINIKey(REMOTE_SERVER_KEY)) {
                    remoteHostName = getINIValue(REMOTE_SERVER_KEY);
                }

                // Set port to default
                remoteHostPort = Integer.parseInt(TransportManager.DEFAULT_PC2_PORT);

                int idx = remoteHostName.indexOf(":");
                if (idx > 2) {
                    remoteHostPort = Integer.parseInt(remoteHostName.substring(idx + 1));
                    remoteHostName = remoteHostName.substring(0, idx);
                }

                info("Contacting " + remoteHostName + ":" + remoteHostPort);
                remoteServerConnectionHandlerID = transportManager.connectToServer(remoteHostName, remoteHostPort);
                
                info ("Contacted using connection id "+remoteServerConnectionHandlerID);

                 info("Sending login request to " + remoteHostName + " as " + clientId);
                 sendLoginRequest(transportManager, remoteServerConnectionHandlerID, clientId, password);
                 
                 info("Started Server Transport listening on " + port);
                 transportManager.accecptConnections(port);
                 
                 info("Secondary Server has started "+model.getTitle());
            } else {
                info("Started Server Transport listening on " + port);
                transportManager.accecptConnections(port);
                info("Primary Server has started "+model.getTitle());
            }

        } else {

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

            info("Contacting server at " + remoteHostName + ":" + port);
            transportManager = new TransportManager(log, remoteHostName, port, btoA);
            transportManager.connectToMyServer();
            info("Started Client Transport");

            sendLoginRequest(transportManager, clientId, password);
        }

        return model;
    }

    private static void sendLoginRequest(ITransportManager manager, ConnectionHandlerID connectionHandlerID, ClientId clientId, String password) {
        try {
            info("sendLoginRequest ConId start - sending from "+clientId);
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
        info("receiveObject " + object.getClass().getName());

        try {

            if (object instanceof Packet) {

                Packet packet = (Packet) object;
                ClientId clientId = packet.getSourceId();

                info("receiveObject " + packet);
                if (model.isLoggedIn(packet.getSourceId())) {
                    // LOGGED IN - process the packet

                    // TODO security authenticate the login vs connection

                    processPacket(packet);

                } else if (packet.getType().equals(PacketType.Type.LOGIN_REQUEST)) {
                    String password = PacketFactory.getStringValue(packet, PacketFactory.PASSWORD);
                    try {

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
                        if (packet.getType() == PacketType.Type.LOGIN_FAILED) {
                            handleServerLoginFailure(packet);
                        } else {
                            String message = "Security violation user " + clientId + " got a " + packet;
                            info(message + " on " + connectionHandlerID);
                            PacketFactory.dumpPacket(System.err, packet);
                        }
                        return;
                    }
                    
                    String message = "Security violation user " + clientId + " got a " + packet;
                    info(message + " on " + connectionHandlerID);
                    PacketFactory.dumpPacket(System.err, packet);
                    sendSecurityVioation(clientId, connectionHandlerID, message);
                }
            } else {
                info("receiveObject(S,C): Unsupported class received: " + object.getClass().getName());
            }

        } catch (Exception e) {
            info("Exception in receiveObject(S,C): " + e.getMessage());
            e.printStackTrace(System.err);
        }
        info("receiveObject (S,C) debug end : Processing " + object.getClass().getName());

    }

    private void handleServerLoginFailure(Packet packet) {
        // TODO rewrite handle this failure better
        
        String message = PacketFactory.getStringValue(packet, PacketFactory.MESSAGE_STRING);
        
        // TODO Handle this better via new login code.
        info("Login Failed: " + message);
        if (message.equals("No such account")) {
            message = "(Accounts Generated ??) ERROR " +message ;
        }
        
        info("Login Failure");
        PacketFactory.dumpPacket(System.err, packet);
        JOptionPane.showMessageDialog(null,message+" "+model.getClientId(),"Login Denied", JOptionPane.ERROR_MESSAGE);
        System.exit(0); // TODO remove this code on valid login
    }

    /**
     * Attempt to login, if login success add to login list.
     * 
     * @param clientId
     * @param password
     * @param connectionHandlerID
     */
    private void attemptToLogin(ClientId clientId, String password, ConnectionHandlerID connectionHandlerID) {

        info("attemptToLogin debug " + clientId + " pass:"+password+" "+connectionHandlerID);

        if (model.isValidLoginAndPassword(clientId, password)) {
            info("Added " + clientId);
            model.addLogin(clientId, connectionHandlerID);
            info("attemptToLogin debug logged on: " + clientId );

        } else {
            info("attemptToLogin debug FAILED logged on: " + clientId );
            // this code will never be executed, if invalid login
            // isValidLogin will throw a SecurityException.
            throw new SecurityException("Failed attempt to login");
        }
    }

    /**
     * Process all packets.
     * 
     * Process packets when user is logged in.
     * 
     * @param packet
     */
    private void processPacket(Packet packet) {
        PacketHandler.handlePacket(this, model, packet);
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
                .getSiteNumber(), model.getLanguages(), model.getProblems(), model.getJudgements(), model.getSites());
        sendToClient(packetToSend);
    }

    public void connectionEstablished(ConnectionHandlerID connectionHandlerID) {
        // TODO code connectionEstablished
        info("connectionEstablished: " + model.getTitle() + " " + connectionHandlerID);
    }

    /**
     * Connection to client lost.
     */
    public void connectionDropped(ConnectionHandlerID connectionHandlerID) {
        // TODO code connectionDropped
        info("connectionDropped: " + model.getTitle() + " " + connectionHandlerID);

        ClientId clientId = model.getLoginClientId(connectionHandlerID);
        if (clientId != null) {
            info("connectionDropped: removed user " + clientId);
            model.removeLogin(clientId);
        }

    }

    public void connectionError(Serializable object, ConnectionHandlerID connectionHandlerID, String causeDescription) {

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

        info(" receiveObject(S) debug Processing " + object.getClass().getName());

        if (object instanceof Packet) {
            Packet packet = (Packet) object;
            PacketFactory.dumpPacket(System.err, packet);
            PacketHandler.handlePacket(controller, model, packet);
        } else {
            info("receiveObject(S) Unsupported class received: " + object.getClass().getName());
        }
        info(" receiveObject(S) debug end Processing " + object.getClass().getName());

    }

    /**
     * Connection Dropped on client.
     */
    public void connectionDropped() {
        // TODO code handle client dropped
        info("connectionDropped: " + model.getTitle());

        // Connection dropped, countdown and die.
        CountDownMessage countDownMessage = new CountDownMessage("Shutting down PC^2 in ", 10);
        if (model.getClientId() != null) {
            countDownMessage.setTitle("Shutting down PC^2 " + model.getClientId().getClientType() + " " + model.getTitle());
        } else {
            countDownMessage.setTitle("Shutting down PC^2 Client");
        }
        countDownMessage.setExitOnClose(true);
        countDownMessage.setVisible(true);

    }

    public static void info(String s) {
        StaticLog.info(s);
        System.err.println(Thread.currentThread().getName() + " " + s);
    }

    public void setSiteNumber(int number) {
        model.setSiteNumber(number);
    }

    public void setContestTime(ContestTime contestTime) {
        // TODO code
    }

    public void setClientId(ClientId clientId) {
        model.setClientId(clientId);
    }

    public void sendToServers(Packet packet) {
        // TODO Auto-generated method stub

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

}
