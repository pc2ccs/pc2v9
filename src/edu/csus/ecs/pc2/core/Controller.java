package edu.csus.ecs.pc2.core;

import java.io.Serializable;

import edu.csus.ecs.pc2.core.list.LoginList;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.LoginEvent;
import edu.csus.ecs.pc2.core.model.LoginRequest;
import edu.csus.ecs.pc2.core.model.Model;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.SubmittedRun;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.core.transport.IBtoA;
import edu.csus.ecs.pc2.core.transport.ITransportManager;
import edu.csus.ecs.pc2.core.transport.ITwoToOne;
import edu.csus.ecs.pc2.core.transport.TransportException;
import edu.csus.ecs.pc2.core.transport.TransportManager;

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

    private LoginList loginList = new LoginList();
    
    public Controller(IModel model) {
        super();
        this.model = model;
    }

    /**
     * Server Receive a run and add it to the run list.
     */
    public void receiveSubmittedRun(SubmittedRun submittedRun) {

        try {
            ClientId clientId = submittedRun.getClientId();
            System.out.println("Controller.receiveSubmittedRun - received - " + submittedRun);
            System.out.println("Controller.receiveSubmittedRun - from - " + clientId);
            SubmittedRun nextSubmittedRun = model.acceptRun(submittedRun);
            System.out.println("Controller.receiveSubmittedRun - added - " + nextSubmittedRun);
            
            ConnectionHandlerID connectionHandlerID = loginList.getConnectionHandleID(clientId);
            sendToClient (connectionHandlerID,nextSubmittedRun);
        } catch (Exception e) {
            // TODO: handle exception maybe someday !! :)
            e.printStackTrace();
        }

    }

    /**
     * Send a object to a client.
     * @param connectionHandlerID
     * @param submittedRun
     */
    private void sendToClient(ConnectionHandlerID connectionHandlerID, SubmittedRun submittedRun) {
        
        try {
            transportManager.send(submittedRun, connectionHandlerID);
        } catch (TransportException e) {
            // TODO log
            e.printStackTrace();
        }
    }

    public void receiveNewRun(SubmittedRun submittedRun) {
        System.out.println("Controller.receiveNewRun - added - " + submittedRun);
        model.addRun(submittedRun);
    }

    /**
     * Client submit a run to the server.
     */
    public void submitRun(int teamNumber, String problemName, String languageName, String filename) throws Exception {

        SerializedFile serializedFile = new SerializedFile(filename);

        SubmittedRun submittedRun = new SubmittedRun(model.getClientId(), problemName, languageName, serializedFile);

        // If we want to immediately populate the run on the GUI without
        // the run number we can invoke: model.addRun(submittedRun);

        try {
            transportManager.send (submittedRun);
            System.out.println("Controller.submitRun - submitted - " + submittedRun);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace(System.err);
        }
    }

    /**
     * Return int for input string
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

        // TODO Start Transport

        ClientId clientId;

        if (id.equals("t")) {
            id = "team1";
        }

        if (id.startsWith("s")) {
            clientId = new ClientId(0, Type.SERVER, 0);
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

        Model model = new Model();
        controller = new Controller(model);
        btoA = controller;

        int port = Integer.parseInt(TransportManager.DEFAULT_PC2_PORT);

        if (clientId.getClientType().equals(Type.SERVER)) {

            // TODO handle server which is contacting another server.

            transportManager = new TransportManager(log, controller);
            transportManager.accecptConnections(port);
            System.err.println("Started Server Transport on " + port);
        } else {

            String serverIP = "localhost";
            transportManager = new TransportManager(log, serverIP, port, btoA);
            transportManager.connectToMyServer();
            System.err.println("Started Client Transport");
            System.err.println("on " + serverIP + " " + port);
            
            sendLoginRequest (transportManager, clientId, password);

        }

        // TODO if first server must authenticate against "loaded" data.
        // TODO if joining server must login to other server and authenticate

        model.setClientId(clientId);
        model.initializeWithFakeData();

        return model;
    }

    /**
     * Send login request to server.
     * @param manager
     * @param clientId
     * @param password
     */
    private static void sendLoginRequest(ITransportManager manager, ClientId clientId, String password) {

        LoginRequest loginRequest = new LoginRequest(clientId, password, clientId.getClientType().toString());

        try {
            manager.send(loginRequest);
        } catch (TransportException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Server recieve object.
     * 
     * @see edu.csus.ecs.pc2.core.transport.ITwoToOne#receiveObject(java.io.Serializable, edu.csus.ecs.pc2.core.transport.ConnectionHandlerID)
     */
    public void receiveObject(Serializable object, ConnectionHandlerID connectionHandlerID) {

        // TODO code check the input connection to insure they are valid connection

        if (object instanceof SubmittedRun) {
            SubmittedRun submittedRun = (SubmittedRun) object;
//            ClientId clientId = submittedRun.getClientId();
//            
//            if (! loginList.isValidConnectionID(clientId, connectionHandlerID)) {
//                throw new SecurityException("attempted to spoof "+clientId+" @ "+connectionHandlerID);
//            }
            
            receiveSubmittedRun(submittedRun);
        } else if (object instanceof LoginRequest) {
            LoginRequest loginRequest = (LoginRequest) object;
            // TODO validate login requests
            
            ClientId clientId = loginRequest.getClientId();
            loginList.add(clientId, connectionHandlerID);
            
            LoginEvent loginEvent = new LoginEvent (clientId, connectionHandlerID);
            fireLoginListener (loginEvent);
            
        } else {
            System.err.println("receiveObject: Unsupported class received: " + object.getClass().getName());
        }
    }

    private void fireLoginListener(LoginEvent loginEvent) {
        // TODO Auto-generated method stub
        
        System.err.println("fireLoginListener: "+loginEvent.getClientId());
        
        // TODO send login response to team
        
        // TODO send login event to other clients and sites
        
    }

    public void connectionEstablished(ConnectionHandlerID connectionHandlerID) {
        // TODO code
        System.err.println("connectionEstablished: " + model.getTitle() + " " + connectionHandlerID);
    }

    /**
     * Connection to client lost.
     */
    public void connectionDropped(ConnectionHandlerID connectionHandlerID) {
        // TODO code
        System.err.println("connectionDropped: " + model.getTitle() + " " + connectionHandlerID);

        ClientId clientId = loginList.getClientId(connectionHandlerID);
        if (clientId != null){
            System.err.println("connectionDropped: removed user "+clientId);
            loginList.remove(clientId);            
        }

    }

    public void connectionError(Serializable object, ConnectionHandlerID connectionHandlerID, String causeDescription) {

        // TODO code
        System.err.println("connectionError: " + model.getTitle() + " " + connectionHandlerID + " " + causeDescription + " " + object.getClass().getName());

    }

    /**
     * Client recieve object.
     * 
     * @see edu.csus.ecs.pc2.core.transport.IBtoA#receiveObject(java.io.Serializable)
     */
    public void receiveObject(Serializable object) {

        if (object instanceof SubmittedRun) {
            SubmittedRun submittedRun = (SubmittedRun) object;
            receiveNewRun(submittedRun);
        } else {
            System.err.println("receiveObject: Unsupported class received: " + object.getClass().getName());
        }

    }

    /**
     * Connection Dropped on client.
     */
    public void connectionDropped() {
        // TODO code handle client dropped
        System.err.println("connectionDropped: " + model.getTitle());

    }

}
