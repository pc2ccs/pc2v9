package edu.csus.ecs.pc2.core;

import java.io.Serializable;
import java.util.Enumeration;

import edu.csus.ecs.pc2.core.log.Log;
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

    private static Controller controller;

    private static Log log;

    private static IBtoA btoA;

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
        RunFiles runFiles = new RunFiles(run, serializedFile,null);
        
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

    public static IModel login(String id, String password) throws Exception {

        // TODO Start Transport or something here

        ClientId clientId;

        if (id.equals("t")) {
            id = "team1";
        }

        if (id.startsWith("s")) {
            clientId = new ClientId(0, Type.SERVER, 0);
        } else if (id.startsWith("judge") && id.length() > 5) {
            int number = getIntegerValue(id.substring(5));
            clientId = new ClientId(0, Type.JUDGE, number);
        } else if (id.startsWith("j") && id.length() > 1) {
            int number = getIntegerValue(id.substring(1));
            clientId = new ClientId(0, Type.JUDGE, number);
        } else if (id.startsWith("t") && id.length() > 4) {
            int number = getIntegerValue(id.substring(4));
            clientId = new ClientId(0, Type.TEAM, number);
        } else if (Character.isDigit(id.charAt(0))) {
            int number = getIntegerValue(id);
            clientId = new ClientId(0, Type.TEAM, number);
        } else {
            throw new SecurityException("No such account " + id);
        }

        log = new Log(clientId.toString());

        if (password.length() < 1) {
            password = clientId.getName(); // Joe password.
        }

        Model model = new Model();
        controller = new Controller(model);
        btoA = controller;

        int port = Integer.parseInt(TransportManager.DEFAULT_PC2_PORT);

        if (clientId.getClientType().equals(Type.SERVER)) {

            // TODO handle server which is contacting another server.

            transportManager = new TransportManager(log, controller);
            info("Started Server Transport on " + port);

            // TODO if first server must authenticate against "loaded" data.
            // TODO if joining server must login to other server and authenticate

            transportManager.accecptConnections(port);

            model.setClientId(clientId);
            model.initializeWithFakeData();

        } else {

            String serverIP = "localhost";
            info("Contacting server at " + serverIP + ":" + port);
            transportManager = new TransportManager(log, serverIP, port, btoA);
            transportManager.connectToMyServer();
            info("Started Client Transport");

            model.setClientId(clientId);

            sendLoginRequest(transportManager, clientId, password);
        }

        return model;
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
            info("sendLoginRequest start");
            ClientId serverClientId = new ClientId(0, Type.SERVER, 0);
            Packet loginPacket = PacketFactory.createLogin(clientId, password, serverClientId);
            manager.send(loginPacket);
            info("sendLoginRequest end - sent packet");
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

                    String message = "Security violation user " + clientId+ " got a "+packet;
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

    /**
     * Attempt to login, if login success add to login list.
     * 
     * @param clientId
     * @param password
     * @param connectionHandlerID
     */
    private void attemptToLogin(ClientId clientId, String password, ConnectionHandlerID connectionHandlerID) {

        if (model.isValidLoginAndPassword(clientId, password)) {
            info("Added " + clientId);
            model.addLogin(clientId, connectionHandlerID);

            ConnectionHandlerID connectionHandlerID2 = model.getConnectionHandleID(clientId);

            info("attemptToLogin debug " + clientId);
            info("attemptToLogin debug " + connectionHandlerID);
            info("attemptToLogin debug " + connectionHandlerID2);

        } else {
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
                .getSiteNumber(), model.getLanguages(), model.getProblems(), model.getJudgements());
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
        CountDownMessage countDownMessage = new CountDownMessage(
                "Shutting down PC^2 in ", 10);
        if (model.getClientId() != null) {
            countDownMessage.setTitle("Shutting down PC^2 "
                    + model.getClientId().getClientType() + " "
                    + model.getTitle());
        } else {
            countDownMessage.setTitle("Shutting down PC^2 Client");
        }
        countDownMessage.setExitOnClose(true);
        countDownMessage.setVisible(true);
        
    }

    public static void info(String s) {
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
