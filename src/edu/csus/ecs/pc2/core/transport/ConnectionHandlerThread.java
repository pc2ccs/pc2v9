package edu.csus.ecs.pc2.core.transport;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import javax.crypto.SealedObject;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.transport.TransportException.Type;

/**
 * Connection Handler Thread.
 * 
 * Provides methods to:
 * <ol>
 * <li>send information via a ConnectionHandler
 * <li>close down a ConnectionHandler and sockets.
 * </ol>
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public abstract class ConnectionHandlerThread extends Thread {
    public static final String SVN_ID = "$Id$";

    private Socket mySocket = null;

    private TransportManager tmCallBack = null;

    private ObjectOutputStream toOtherModule = null;

    private ObjectInputStream fromOtherModule = null;

    private boolean stillListening = false;

    private ConnectionHandlerID myConnectionID = null;

    private Log log = null;

    public ConnectionHandlerThread(Socket socket, TransportManager tmCallBack, ConnectionHandler chCallBack) {
        super();
        setMySocket(socket);
        setTmCallBack(tmCallBack);
        setMyConnectionID(new ConnectionHandlerID(socket.toString()));
    }

    protected TransportManager getTmCallBack() {
        return tmCallBack;
    }

    private void setTmCallBack(TransportManager tmCallBack) {
        this.tmCallBack = tmCallBack;
    }

    protected Socket getMySocket() {
        return mySocket;
    }

    private void setMySocket(Socket mySocket) {
        this.mySocket = mySocket;
    }

    protected ConnectionHandlerID getMyConnectionID() {
        return myConnectionID;
    }

    private void setMyConnectionID(ConnectionHandlerID myConnectionID) {
        this.myConnectionID = myConnectionID;
    }

    public abstract void run();

    protected boolean isStillListening() {
        return stillListening;
    }

    public void setStillListening(boolean stillListening) {
        this.stillListening = stillListening;
    }

    public void send(SealedObject msgObj) throws TransportException {

        int busywait = 0;
        while (!getMyConnectionID().isReadyToCommunicate()) {
            // TODO: Change this code to be a monitor
            busywait++;
        }

        try {
            getToOtherModule().writeObject(msgObj);
        } catch (Exception e) {
            getLog().log(Log.INFO, "Exception near writeObject", e);
            throw new TransportException(e.getMessage());
        }
    }

    public void send(TransportWrapper msgToSend) throws TransportException {
        try {
            getToOtherModule().writeObject(msgToSend);
        } catch (Exception e) {
            getLog().throwing(getClass().getName(),"send",e);
            throw new TransportException(e.getMessage());
        }
    }

    protected void sendUnencrypted(TransportWrapper msgObj) throws TransportException {
        try {
            getToOtherModule().writeObject(msgObj);
        } catch (Exception e) {
            getLog().throwing(getClass().getName(),"sendUnencrypted",e);
            throw new TransportException(e.getMessage());
        }
    }

    protected TransportWrapper receiveUnencrypted() throws TransportException {
        TransportWrapper msgObj = null;

        try {
            msgObj = (TransportWrapper) getFromOtherModule().readObject();
        } catch (Exception e) {
            getLog().throwing(getClass().getName(),"receiveUnencrypted",e);
            throw new TransportException(e.getMessage());
        }
        return msgObj;
    }

    protected SealedObject receive() throws TransportException {
        SealedObject msgObj = null;

        while (!getMyConnectionID().isReadyToCommunicate()) {
            msgObj = null;
            // TODO: Change this code to be a monitor
        }

        try {
            msgObj = (SealedObject) getFromOtherModule().readObject();
        } catch (SocketException e) {
            getLog().log(Log.INFO, "SocketException in receive for "+getMyConnectionID(), e);
            throw new TransportException(e.getMessage(), Type.CONNECTION_RESET);
        } catch (EOFException e) {
            log.log(Log.INFO, "Exception in receive for "+getMyConnectionID(), e);
//            throw new TransportException(e.getMessage(), Type.RECEIVE);
        } catch (Exception e) {
            log.log(Log.INFO, "Exception in receive for "+getMyConnectionID(), e);
            throw new TransportException(e.getMessage(), Type.RECEIVE);
        }
        return msgObj;
    }

    public ObjectInputStream getFromOtherModule() {
        return fromOtherModule;
    }

    public void setFromOtherModule(ObjectInputStream fromOtherModule) {
        this.fromOtherModule = fromOtherModule;
    }

    public ObjectOutputStream getToOtherModule() {
        return toOtherModule;
    }

    public void setToOtherModule(ObjectOutputStream toOtherModule) {
        this.toOtherModule = toOtherModule;
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public void shutdownConnection() {
        try {
            if (toOtherModule != null) {
                toOtherModule.close();
                toOtherModule = null;
            }

            if (fromOtherModule != null) {
                fromOtherModule.close();
                fromOtherModule = null;
            }

            if (mySocket != null) {
                mySocket.close();
                mySocket = null;
            }
        } catch (Exception exception) {
            getLog().log(Log.WARNING, "Shutdown connection - threw exception closing socket", exception);
        }
    }
}
