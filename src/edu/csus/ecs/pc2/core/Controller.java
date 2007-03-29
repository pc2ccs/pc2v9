package edu.csus.ecs.pc2.core;

import java.io.Serializable;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.Model;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.SubmittedRun;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.core.transport.IBtoA;
import edu.csus.ecs.pc2.core.transport.ITransportManager;
import edu.csus.ecs.pc2.core.transport.ITwoToOne;
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

    /**
     * Very temporary connection id for single client transport testing. dal.
     */
    // TODO remove this soon, replace with actual login list.
    private ConnectionHandlerID lastConnectionHandlerID;

    public Controller(IModel model) {
        super();
        this.model = model;
    }

    /**
     * Server Receive a run and add it to the run list.
     */
    public void receiveSubmittedRun(SubmittedRun submittedRun) {

        try {
            System.out.println("Controller.receiveSubmittedRun - got - " + submittedRun);
            SubmittedRun nextSubmittedRun = model.acceptRun(submittedRun);
            send(nextSubmittedRun);
        } catch (Exception e) {
            // TODO: handle exception maybe someday !! :)
            e.printStackTrace();
        }

    }

    public void receiveNewRun(SubmittedRun submittedRun) {
        model.addRun(submittedRun);
    }

    /**
     * Submit a run to the server.
     */
    public void submitRun(int teamNumber, String problemName, String languageName, String filename) throws Exception {

        SerializedFile serializedFile = new SerializedFile(filename);

        SubmittedRun submittedRun = new SubmittedRun(teamNumber, problemName, languageName, serializedFile);

        // If we want to immediately populate the run on the GUI without
        // the run number we can invoke: model.addRun(submittedRun);

        try {
            transportManager.send(submittedRun);
            System.out.println("Controller.submitRun - submitted - " + submittedRun);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace(System.err);
        }
    }

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

        }

        // TODO if first server must authenticate against "loaded" data.
        // TODO if joining server must login to other server and authenticate

        model.setClientId(clientId);
        model.initializeWithFakeData();

        return model;
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
            receiveSubmittedRun(submittedRun);
            lastConnectionHandlerID = connectionHandlerID;
        } else {
            System.err.println("receiveObject: Unsupported class received: " + object.getClass().getName());
        }
    }

    public void connectionEstablished(ConnectionHandlerID connectionHandlerID) {
        // TODO code
        System.err.println("connectionEstablished: " + model.getTitle() + " " + connectionHandlerID);
        
        lastConnectionHandlerID = connectionHandlerID;

    }

    public void connectionDropped(ConnectionHandlerID connectionHandlerID) {
        // TODO code
        System.err.println("connectionDropped: " + model.getTitle() + " " + connectionHandlerID);
        
        lastConnectionHandlerID = null;

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

    public void connectionDropped() {
        // TODO code handle client dropped
        System.err.println("connectionDropped: " + model.getTitle());

    }

    /**
     * Send object to client.
     * 
     * @param serializableObject
     */
    public void send(Serializable serializableObject) {
        try {
            transportManager.send(serializableObject, lastConnectionHandlerID);

        } catch (Exception e) {
            System.err.println("send: could not sent");
            e.printStackTrace(System.err);
        }
    }

}
