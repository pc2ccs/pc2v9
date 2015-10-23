package edu.csus.ecs.pc2.core.transport;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.crypto.SealedObject;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.transport.TransportManager.TMTypes;

/**
 * A sing connection handler, serves as a base class for client and server connection handlers.
 * 
 * This class will:
 * <ol>
 * <li> Startup as a server (listener initially) or client (sender intitally)
 * <li> Keep the connection alive (by keeping the socket open)
 * <li> For a server, create a listener and handle incoming transmissions/connections
 * <li> Provide a way to send information to a server.
 * <li> Provide the data feed to and from the pc2 modules.
 * </ol>
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */
// $HeadURL$
public class ConnectionHandler implements Runnable {
    public static final String SVN_ID = "$Id$";

    private Socket mySocket = null;

    private ServerSocket serverSocket = null;

    private boolean serverListening = false;

    private ObjectOutputStream toOtherModule = null;

    private ObjectInputStream fromOtherModule = null;

    private Log log = null;

    private TransportManager tmCallBack = null;

    private int listeningPort = 50002;

    private ConnectionHandlerClientThread connectionHandlerClientThread = null;

    private ConnectionHandlerServerThread connectionHandlerServerThread = null;

    private ConnectionHandlerID connectionHandlerID = null;

    /**
     * Client Connection Handler.
     * @param log
     * @param host
     * @param port
     * @param tmCallBack
     * @param tmType
     * @throws TransportException
     */
    protected ConnectionHandler(Log log, String host, int port, TransportManager tmCallBack, TMTypes tmType)
            throws TransportException {
        super();

        setLog(log);
        setTmCallBack(tmCallBack);
        try {
            setMySocket(new Socket(host, port));
        } catch (UnknownHostException e) {
            getLog().info("could not resolve host:" + host);
            throw new TransportException(e.getMessage());
        } catch (IOException e) {
            getLog().info("IO Exception:" + host);
            throw new TransportException(e.getMessage());
        } catch (Exception e) {
            getLog().info("Exception:" + host);
            throw new TransportException(e.getMessage());
        }

        if (getMySocket() != null) {
            try {

                setConnectionHandlerID(new ConnectionHandlerID(getMySocket().toString()));

                new ConnectionHandlerClientThread(getMySocket(), getTmCallBack(), this, tmType, getLog()).start();
            } catch (Exception e) {
                getLog().info("Could spawn new client thread");
                throw new TransportException(e.getMessage());
            }
        }
    }

    /**
     * Server Connection Handler
     * @param log
     * @param port
     * @param tmCallBack
     * @throws TransportException
     */
    protected ConnectionHandler(Log log, int port, TransportManager tmCallBack) throws TransportException {
        super();

        setLog(log);
        setTmCallBack(tmCallBack);
        setListeningPort(port);

        try {
            setServerSocket(new ServerSocket(getListeningPort()));

            setConnectionHandlerID(new ConnectionHandlerID(getServerSocket().toString()));

        } catch (IOException e) {
            getLog().info("Could not listen on port:" + getListeningPort());
            throw new TransportException(e.getMessage());
        }
    }

    public void send(SealedObject msgToSend) throws TransportException {
        try {
            if (getTmCallBack().getTmType() == TMTypes.SERVER) {
                getConnectionHandlerServerThread().send(msgToSend);
            } else {
                getConnectionHandlerClientThread().send(msgToSend);
            }
        } catch (Exception e) {
            throw new TransportException(e.getMessage());
        }
    }

    public void send(TransportWrapper msgToSend) throws TransportException {
        try {
            if (getTmCallBack().getTmType() == TMTypes.SERVER) {
                getConnectionHandlerServerThread().send(msgToSend);
            } else {
                getConnectionHandlerClientThread().send(msgToSend);
            }
        } catch (Exception e) {
            throw new TransportException(e.getMessage());
        }
    }

    private void setLog(Log log) {
        this.log = log;
    }

    private TransportManager getTmCallBack() {
        return tmCallBack;
    }

    private void setTmCallBack(TransportManager tmCallBack) {
        this.tmCallBack = tmCallBack;
    }

    private ServerSocket getServerSocket() {
        return serverSocket;
    }

    private void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    private boolean isServerListening() {
        return serverListening;
    }

    private void setServerListening(boolean serverListening) {
        this.serverListening = serverListening;
    }

    protected int getListeningPort() {
        return listeningPort;
    }

    protected void setListeningPort(int listeningPort) {
        this.listeningPort = listeningPort;
    }

    private Socket getMySocket() {
        return mySocket;
    }

    private void setMySocket(Socket mySocket) {
        this.mySocket = mySocket;
        try {
            this.mySocket.setKeepAlive(true);
        } catch (SocketException e) {
            getLog().info("Could not set KeepAlive on socket");
            getLog().throwing(getClass().getName(), "setMySocket", e);
        }
    }

    protected ConnectionHandlerClientThread getConnectionHandlerClientThread() {
        return connectionHandlerClientThread;
    }

    protected void setConnectionHandlerClientThread(ConnectionHandlerClientThread connectionHandlerClientThread) {
        this.connectionHandlerClientThread = connectionHandlerClientThread;
    }

    protected ConnectionHandlerServerThread getConnectionHandlerServerThread() {
        return connectionHandlerServerThread;
    }

    protected void setConnectionHandlerServerThread(ConnectionHandlerServerThread connectionHandlerServerThread) {
        this.connectionHandlerServerThread = connectionHandlerServerThread;
    }

    protected ObjectInputStream getFromOtherModule() {
        return fromOtherModule;
    }

    protected void setFromOtherModule(ObjectInputStream fromOtherModule) {
        this.fromOtherModule = fromOtherModule;
    }

    protected ObjectOutputStream getToOtherModule() {
        return toOtherModule;
    }

    protected void setToOtherModule(ObjectOutputStream toOtherModule) {
        this.toOtherModule = toOtherModule;
    }

    private Log getLog() {
        return log;
    }

    /**
     * Runnable thread for handling Incoming connections on a server
     */
    public void run() {
        if (getServerSocket() != null) {
            setServerListening(true);
            while (isServerListening()) {
                try {
                    new ConnectionHandlerServerThread(getServerSocket().accept(), getTmCallBack(), this, getLog()).start();
                } catch (Exception e) {
                    getLog().info("Could not spawn new thread:");
                    // throw new TransportException (e.getMessage());
                    // TODO: This should pass the exception back up to the
                    // TransportManager
                }
            }
        }

    }

    public ConnectionHandlerID getConnectionHandlerID() {
        return connectionHandlerID;
    }

    private void setConnectionHandlerID(ConnectionHandlerID connectionHandlerID) {
        this.connectionHandlerID = connectionHandlerID;
    }
}
