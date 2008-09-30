package edu.csus.ecs.pc2.core.transport;

import java.io.Serializable;
import java.security.PublicKey;

import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.security.Crypto;
import edu.csus.ecs.pc2.core.security.CryptoException;

/**
 * Transport Manager Class used to control all communication between modules.
 * <P>
 * Each TransportManager is established as either a {@link TransportManager.tmTypes#CLIENT CLIENT} or a
 * {@link TransportManager.tmTypes#SERVER  SERVER} type connector.
 * <P>
 * A {@link TransportManager.tmTypes#SERVER SERVER} will start a listener on the input port and wait for connections from
 * {@link TransportManager.tmTypes#CLIENT CLIENT} TransportManagers.
 * <P>
 * A {@link TransportManager.tmTypes#CLIENT CLIENT} will contact a {@link TransportManager.tmTypes#SERVER SERVER} and establish a
 * connection. Note that a PC<sup>2</sup> server module can also be a {@link TransportManager.tmTypes#CLIENT CLIENT} if it joins a
 * contest.
 * <P>
 * To instantiate a Server:
 * <ol>
 * <li> Use the {@link #TransportManager(Log)} constructor.
 * <li> Start the server transport using {@link #startServerTransport(ITwoToOne)}
 * <li> Start the transport listening using {@link #accecptConnections(int)}
 * </ol>
 * <P>
 * To instantiate a Client:
 * <ol>
 * <li> Use the {@link #TransportManager(Log)} constructor.
 * <li> Start the client transport using {@link #startClientTransport(String, int, IBtoA)}
 * <li> Contact the server using {@link #connectToMyServer()}
 * </ol>
 * Needless to say the port numbers should be identical. 
 * 
 * @author pc2@ecs.csus.edu
 * 
 */
// $HeadURL: http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu/csus/ecs/pc2/core/transport/TransportManager.java$
public class TransportManager implements ITransportManager {
    public static final String SVN_ID = "$Id$";

    /**
     * The type/kind of transport manager.
     * 
     * This identifies the type of an instance of TransportManager.
     * 
     * @author pc2@ecs.csus.edu
     */

    public enum tmTypes {
        /**
         * Always contacts a server.
         * 
         * This is a client, it may be a client (like Team or Admin) or another server which is contacting a remoteServer.
         */
        CLIENT,
        /**
         * Creates a server (listener).
         * 
         * This is a Server, it will create a listener thread for incoming communication packets/requests.
         */
        SERVER
    };
    
    /**
     * Default connection/host port.
     */
    public static final String DEFAULT_PC2_PORT = "50002";

    private static final String PUBLIC_KEY = "PUBLIC_KEY";

    private String myServerIP = "";

    private int myServerPort = 0;

    private Log log = null;

    private ConnectionHandler myConnection = null;

    private Crypto encryptionKeys = null;

    private ConnectionHandlerThreadList connectionHandlerThreadList = null;

    private ConnectionHandlerList serversConnectionHandlerList = null;

    private tmTypes tmType;

    private ITwoToOne appServerCallBack = null;

    private IBtoA appClientCallBack = null;

    /**
     * TransportManager constructor.
     * 
     * Start log and create encryption keys
     * 
     * @param log
     */
    public TransportManager(Log log) {
        super();
        setLog(log);
        setEncrytionKeys(new Crypto());
    }

    /**
     * Instantiate a Server Transport.
     * 
     * @param appCallBack
     */
    public void startServerTransport(ITwoToOne appCallBack) {
        setConnectionHandlerThreadList(new ConnectionHandlerThreadList());
        setTmType(tmTypes.SERVER);
        setAppServerCallBack(appCallBack);
        setServersConnectionHandlerList(new ConnectionHandlerList());
    }

    /**
     * Instantiate a Client Transport.
     * 
     * @param serverIP
     * @param port
     * @param appCallBack
     */
    public void startClientTransport(String serverIP, int port, IBtoA appCallBack) {
        setMyServerPort(port);
        setMyServerIP(serverIP);
        setTmType(tmTypes.CLIENT);
        setAppClientCallBack(appCallBack);
    }

    /**
     * Issues shutdown command to Transport. Closes all Connections and disposes everything. 
     *
     */
    public void shutdownTransport() {
        
        info("******************* SHUTTING down transport on command ************************");
        
        if (getTmType() == tmTypes.CLIENT) { // we are a client transport
            info("******************* SHUTTING down client ************************");
            getMyConnection().getConnectionHandlerClientThread().setStillListening(false);
        } else { // we are a server transport

            info("******************* SHUTTING server connection down ************************");
            
            try {
                for(ConnectionHandlerID connectionHandlerID: getServersConnectionHandlerList().getKeys()) {
                    info("***** SHUTTING connections on server !! **** handle id = " + connectionHandlerID);
                    try {
                        unregisterConnection(connectionHandlerID);                
                    } catch (Exception e) {
                        info("Exception shutting down a server connection ("+connectionHandlerID+") ", e);
                    }
                }

            } catch (Exception e) {
                info("Exception shutting down a server connection ", e);
            }

            try {
                for(ConnectionHandlerID connectionHandlerID: getConnectionHandlerThreadList().getKeys()) {
                    info("***** SHUTTING client connections on server !! **** handle id = " + connectionHandlerID);
                    try {
                        unregisterConnection(connectionHandlerID);
                    } catch (Exception e) {
                        info("Exception shutting down a client connection ("+connectionHandlerID+") ", e);
                    }
                }
            } catch (Exception ex) {
                info("Exception shutting down a client connection ", ex);
            }
        }
    }
    
    /**
     * Used by Client to connect to it's server
     * 
     * @throws TransportException
     */
    public void connectToMyServer() throws TransportException {
        getLog().info("Connecting to " + getMyServerIP() + ":" + getMyServerPort());

        ConnectionHandler connectionHandler;
        try {
            connectionHandler = new ConnectionHandler(getLog(), getMyServerIP(), getMyServerPort(), this, getTmType());
            setMyConnection(connectionHandler);

        } catch (TransportException e) {
            getLog().info("Could not ConnectToMyServer()");
            throw new TransportException(e.getMessage());
        }
    }

    /**
     * Used by Servers to connect to other Servers
     * 
     * @param serverIP
     * @param port
     * @return the handler ID
     * @throws TransportException
     */
    public ConnectionHandlerID connectToServer(String serverIP, int port) throws TransportException {
        getLog().info("Connecting to " + serverIP + ":" + port);

        ConnectionHandler connectionHandler;
        try {
            connectionHandler = new ConnectionHandler(getLog(), serverIP, port, this, getTmType());
            return connectionHandler.getConnectionHandlerClientThread().getMyConnectionID();
        } catch (TransportException e) {
            getLog().info("Could not connect to server @ " + serverIP + ":" + port);
            throw new TransportException(e.getMessage());
        }
    }

    /**
     * Servers use this class to initiate listening on a specific port
     * 
     * @param listeningPort
     */
    public void accecptConnections(int listeningPort) throws TransportException {
        getLog().info("accecptConnections on port:" + listeningPort);

        try {
            ConnectionHandler connectionHandler = new ConnectionHandler(getLog(), listeningPort, this);
            getServersConnectionHandlerList().add(connectionHandler.getConnectionHandlerID(), connectionHandler);

            new Thread(connectionHandler).start();

        } catch (Exception e) {
            getLog().info("Could not Accept connections on Port:" + listeningPort);
            throw new TransportException(e.getMessage());
        }
    }

    /**
     * Returns the internal MyServerIP (used by a client)
     */
    private String getMyServerIP() {
        return myServerIP;
    }

    /**
     * Sets MyServerIP to the passed in parameter (used by a client)
     * 
     * @param myServerIP
     */
    private void setMyServerIP(String myServerIP) {
        this.myServerIP = myServerIP;
    }

    /**
     * Returns MyServerPort (used be a client)
     */
    private int getMyServerPort() {
        return myServerPort;
    }

    /**
     * Sets MyServerPort to the passed in parameter (used by client)
     * 
     * @param myServerPort
     */
    private void setMyServerPort(int myServerPort) {
        this.myServerPort = myServerPort;
    }

    /**
     * Returns the Log
     */
    private Log getLog() {
        return log;
    }

    /**
     * Returns encryptionKeys
     */
    private Crypto getEncrytionKeys() {
        return encryptionKeys;
    }

    /**
     * Sets encryptionsKeys to the passed in parameter
     * 
     * @param encrytionKeys
     */
    private void setEncrytionKeys(Crypto encrytionKeys) {
        this.encryptionKeys = encrytionKeys;
    }

    /**
     * Method used to generate a TransportWrapper packet for transmitting the initial Public key exchange
     * 
     * @return the packet
     */
    public TransportWrapper getPublicKeyPacket() {
        getLog().info("Generating Unencrypted Public Key Packet");
        getLog().info("checking key:= " + getEncrytionKeys().getPublicKey());
        TransportWrapper packet = new TransportWrapper(PUBLIC_KEY, getEncrytionKeys().getPublicKey());
        getLog().info("packet:= " + packet);
        
        return packet;
    }

    /**
     * Method called by the lower level ConnectionHandler to pass up the received SealedObject and ConnectionHandlerID to identify
     * the sender. This method decrypts the packet using the local copy of the SecretKey and then makes the appropriate Application
     * callback.
     * 
     * @param transportPacket
     * @param connectionHandlerID
     */
    public void receive(SealedObject transportPacket, ConnectionHandlerID connectionHandlerID) {
        // TODO change to fine
        getLog().info("public void receive(SealedObject TransportPacket, ConnectionHandlerID connectionHandlerID)");
        Serializable incomingMsg = null;

        try {
            incomingMsg = getEncrytionKeys().decrypt(transportPacket, connectionHandlerID.getSecretKey());
        } catch (CryptoException e) {
            getLog().log(Log.INFO,"Could not decrypt Packet!", e);
        }
        if (incomingMsg != null) {
            if (getTmType() == tmTypes.SERVER) {
                final Serializable fIncomingMsg = incomingMsg;
                final ConnectionHandlerID fConnectionHandlerID = connectionHandlerID;
                new Thread(new Runnable() {
                    public void run() {
                        getAppServerCallBack().receiveObject(fIncomingMsg, fConnectionHandlerID);
                    }
                }).start();
            } else {
                final Serializable fIncomingMsg = incomingMsg;
                new Thread(new Runnable() {
                    public void run() {
                        getAppClientCallBack().receiveObject(fIncomingMsg);
                    }
                }).start();
            }
        } else {
            // incoming message was not decrypted successfully
            getLog().info("Failed to Decrypt incoming message from: " + connectionHandlerID);
        }
    }

    /**
     * Method called by the lower level ConnectionHandler to notify the TransportManager that a connection was dropped. This method
     * in turn notifies the Application
     * 
     */
    public void connectionDropped(ConnectionHandlerID myConnectionID) {
        getLog().info("connectionDropped(ConnectionHandlerID myConnectionID) ");

        // UnRegister incoming connection with the Application
        if (getTmType() == tmTypes.SERVER) {
            ConnectionHandlerThread connectionHandlerThread = getConnectionHandlerThreadList().get(myConnectionID);
            if (connectionHandlerThread != null) {
                connectionHandlerThread.shutdownConnection();
                connectionHandlerThread = null;
            }
            getConnectionHandlerThreadList().remove(myConnectionID);
            getAppServerCallBack().connectionDropped(myConnectionID);
        } else {
            ConnectionHandlerClientThread connectionHandlerClientThread = getMyConnection().getConnectionHandlerClientThread();
            if (connectionHandlerClientThread != null) {
                connectionHandlerClientThread.shutdownConnection();
                connectionHandlerClientThread = null;
            }
            getAppClientCallBack().connectionDropped();
        }
    }

    /**
     * Method called by the lower level ConnectionHandler to notify the TransportManager that a connection was dropped. This method
     * in turn notifies the Application
     * 
     */
    public void unregisterConnection(ConnectionHandlerID myConnectionID) {
        getLog().info("unregisterConnection(ConnectionHandlerID myConnectionID) ");

        // UnRegister incoming connection with the Application
        if (getTmType() == tmTypes.SERVER) {
            ConnectionHandlerThread connectionHandlerThread = getConnectionHandlerThreadList().get(myConnectionID);
            connectionHandlerThread.setStillListening(false);
            connectionHandlerThread.shutdownConnection();
            connectionHandlerThread = null;
            getConnectionHandlerThreadList().remove(myConnectionID);
            getAppServerCallBack().connectionDropped(myConnectionID);
        } else {
            getMyConnection().getConnectionHandlerClientThread().setStillListening(false);
            getMyConnection().getConnectionHandlerClientThread().shutdownConnection();
            getMyConnection().setConnectionHandlerClientThread(null);
            getAppClientCallBack().connectionDropped();
        }
    }

    /**
     * Method called by the lower ConnectionHandler to notify the application of an incoming connection.
     * 
     * @param myConnectionID
     * @param thread
     */
    public void registerIncomingConnectionRequest(ConnectionHandlerID myConnectionID, ConnectionHandlerThread thread) {
        getLog().info("registerIncomingConnectionRequest(myConnectionID, thread) ");
        getConnectionHandlerThreadList().add(myConnectionID, thread);
        // Register incoming connection with Server
        getAppServerCallBack().connectionEstablished(myConnectionID);
    }

    /**
     * Returns the connectionHandlerThreadList
     */
    private ConnectionHandlerThreadList getConnectionHandlerThreadList() {
        return connectionHandlerThreadList;
    }

    /**
     * Sets the connectionHandlerThreadList to the passed in parameter
     * 
     * @param connectionHandlerList
     */
    private void setConnectionHandlerThreadList(ConnectionHandlerThreadList connectionHandlerList) {
        this.connectionHandlerThreadList = connectionHandlerList;
    }

    /**
     * Returns the myConnection
     */
    private ConnectionHandler getMyConnection() {
        return myConnection;
    }

    /**
     * Sets myConnection to the passed in parameter
     * 
     * @param myConnection
     */
    private void setMyConnection(ConnectionHandler myConnection) {
        this.myConnection = myConnection;
    }

    /**
     * Public Interface method for Client to send Object to their Server. This method is invoked by the application which passes in
     * a Serilizable Object to be sent to the Server.
     * 
     */
    public void send(Serializable msgObj) throws TransportException {

        final Serializable fMsgObj = msgObj;

        new Thread(new Runnable() {
            public void run() {
                getLog().info("send(Serializable)");

                // Wait for connectionHandler to be ready
                while (!getMyConnection().getConnectionHandlerClientThread().getMyConnectionID().isReadyToCommunicate()) {

                    // TODO: Change to Monitor rather than busy wait.
                    
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        getLog().throwing("TransportManager", "Busy wait failed", e);
                    }
                    
                }

                SecretKey secretKey = getMyConnection().getConnectionHandlerClientThread().getMyConnectionID().getSecretKey();
                SealedObject sealedObject = null;

                try {
                    sealedObject = getEncrytionKeys().encrypt(fMsgObj, secretKey);
                } catch (CryptoException e) {

                    // could not do this because we are not on a thread.
                    // throw new TransportException(e.getMessage());
                    //
                    // Application will have to handle the connectionError
                    // correctly

                    getAppClientCallBack().connectionError(fMsgObj, null, e.getMessage());
                }

                try {
                    getMyConnection().send(sealedObject);
                } catch (Exception e) {
                    // could not do this because we are not on a thread.
                    // throw new TransportException(e.getMessage());
                    //
                    // Application will have to handle the connectionError
                    // correctly

                    getAppClientCallBack().connectionError(fMsgObj, null, e.getMessage());
                }

            }
        }).start();
    }

    /**
     * Public Internet method for Server to send to clients. This method is invoked with two parameters, a Serilizable Object and a
     * connectionHandlerID. The TransportManager encrypts the Object using the specific connectionHandler SecretKey. Then it finds
     * the appropriate thread and invokes the Send method on that Thread.
     * 
     * @throws TransportException
     * 
     */
    public void send(Serializable msgObj, ConnectionHandlerID connectionHandlerID) throws TransportException {
        final Serializable fMsgObj = msgObj;
        final ConnectionHandlerID fConnectionHandlerID = connectionHandlerID;

        new Thread(new Runnable() {
            public void run() {

                getLog().info("send(Serializable, ConnectionHandlerID) to " + fConnectionHandlerID);

                // Wait for connectionHandler to be ready
                while (!fConnectionHandlerID.isReadyToCommunicate()) {
                    // TODO: Change to Monitor rather than Busywait
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        getLog().throwing("TransportManager", "Busy wait failed", e);
                    }
                }

                SecretKey secretKey = fConnectionHandlerID.getSecretKey();
                SealedObject sealedObject = null;
                try {
                    sealedObject = getEncrytionKeys().encrypt(fMsgObj, secretKey);
                } catch (CryptoException e) {
                    // could not do this because we are not on a thread.
                    // throw new TransportException(e.getMessage());
                    //
                    // Application will have to handle the connectionError
                    // correctly
                    if ( getAppClientCallBack() != null){
                        getAppClientCallBack().connectionError(fMsgObj, fConnectionHandlerID, e.getMessage());
                    } else {
                        log.log(Log.INFO, "Exception in send "+fConnectionHandlerID, e);
                    }
                }
                try {
                    getConnectionHandlerThreadList().get(fConnectionHandlerID).send(sealedObject);
                } catch (Exception e) {
                    // could not do this because we are not on a thread.
                    // throw new TransportException(e.getMessage());
                    //
                    // Application will have to handle the connectionError
                    // correctly
                    
                    if ( getAppClientCallBack() != null){
                        getAppClientCallBack().connectionError(fMsgObj, fConnectionHandlerID, e.getMessage());
                    } else {
                        log.log(Log.INFO, "Exception in send "+fConnectionHandlerID, e);
                    }

                }
            }
        }).start();
    }

    /**
     * Receive Unencrypted packet from otherModule & Generate SecretKey for this connection.
     * 
     * @param wrapper
     * @param myConnectionID
     */
    protected void receiveUnencrypted(TransportWrapper wrapper, ConnectionHandlerID myConnectionID) {
        getLog().info("receiveUnencrypted(TransportWrapper, ConnectionHandlerID="+myConnectionID+")");

        PublicKey pk = (PublicKey) wrapper.get(PUBLIC_KEY);
        
        SecretKey tmpKey = getEncrytionKeys().generateSecretKey(pk, getEncrytionKeys().getPrivateKey());
        getEncrytionKeys().setSecretKey(tmpKey);

        myConnectionID.setSecretKey(tmpKey);

        getLog().info("receiveUnencrypted Made a secret key for " + myConnectionID + " key is " + tmpKey.toString());
    }

    /**
     * Returns the tmType
     * 
     * @return the tmType
     */
    protected tmTypes getTmType() {
        return tmType;
    }

    /**
     * Sets the tmType to the passed in parameter. The Server Constructor sets this variable to tmTypes.SERVER and the Client
     * Constructor sets this variable to tmTypes.CLIENT.
     * 
     * @param tmType
     */
    protected void setTmType(tmTypes tmType) {
        this.tmType = tmType;
    }

    /**
     * Returns the Server Application Callback
     */
    private ITwoToOne getAppServerCallBack() {
        return appServerCallBack;
    }

    /**
     * Sets the Server Application callback to the passed in parameter
     * 
     * @param appCallBack
     */
    private void setAppServerCallBack(ITwoToOne appCallBack) {
        this.appServerCallBack = appCallBack;
    }

    /**
     * Returns the Client Application Callback
     */
    private IBtoA getAppClientCallBack() {
        return appClientCallBack;
    }

    /**
     * Sets the Client Applcation callback tot he passed in parameter
     * 
     * @param appClientCallBack
     */
    private void setAppClientCallBack(IBtoA appClientCallBack) {
        this.appClientCallBack = appClientCallBack;
    }

    /**
     * Sets the log to the passed in parameter
     * 
     * @param log
     */
    public void setLog(Log log) {
        this.log = log;
    }

    /**
     * Returns the serversConnectionHandlerList
     */
    private ConnectionHandlerList getServersConnectionHandlerList() {
        return serversConnectionHandlerList;
    }

    /**
     * Sets the serversConnectionHandlerList to the passed in parameter
     * 
     * @param serversConnectionHandlerList
     */
    private void setServersConnectionHandlerList(ConnectionHandlerList serversConnectionHandlerList) {
        this.serversConnectionHandlerList = serversConnectionHandlerList;
    }
    
    private void info(String s) {
        getLog().info(s);
        System.out.println(Thread.currentThread().getName() + " " + s);
        System.out.flush();
    }
    
    public void info(String s, Exception exception) {
        getLog().log (Log.INFO, s, exception);
        System.out.println(Thread.currentThread().getName() + " " + s);
        exception.printStackTrace(System.out);
        System.out.flush();
    }

}
