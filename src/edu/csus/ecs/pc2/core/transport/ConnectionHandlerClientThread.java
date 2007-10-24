package edu.csus.ecs.pc2.core.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import javax.crypto.SealedObject;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.transport.TransportManager.tmTypes;

/**
 * A Client ConnectionHandler.
 * 
 * This manages a client connection, it established a connection to
 * a server and allows the pc2 module to send and receive information
 * from a server.  Note that a server that logs into another server
 * uses this connection handler.
 * 
 * @see edu.csus.ecs.pc2.core.transport.ConnectionHandler
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ConnectionHandlerClientThread extends ConnectionHandlerThread {
    public static final String SVN_ID = "$Id$";

    private tmTypes tmType = null;

    public ConnectionHandlerClientThread(Socket socket, TransportManager tmCallBack, ConnectionHandler chCallBack,
            tmTypes incomingTmType, Log log) {
        super(socket, tmCallBack, chCallBack);
        chCallBack.setConnectionHandlerClientThread(this);
        setTmType(incomingTmType);
        setLog(log);
    }

    public void run() {
        try {
            setToOtherModule(new ObjectOutputStream(getMySocket().getOutputStream()));

            InputStream iss = getMySocket().getInputStream();
            setFromOtherModule(new ObjectInputStream(iss));

            if (getTmType() == tmTypes.SERVER) {
                getTmCallBack().registerIncomingConnectionRequest(getMyConnectionID(), this);
            }

            // Code should insure that the receive worked!
            getTmCallBack().receiveUnencrypted(receiveUnencrypted(), getMyConnectionID());

            // Code should insure that the send worked!
            sendUnencrypted(getTmCallBack().getPublicKeyPacket());

            setStillListening(true);

            while (isStillListening()) {
                SealedObject sealedObject = null;
                try {
                    sealedObject = receive();
                    getTmCallBack().receive(sealedObject, getMyConnectionID());
                } catch (TransportException e) {
                    getLog().throwing(getClass().getName(), "run", e);
                    if (e.getMessage().equalsIgnoreCase(TransportException.CONNECTION_RESET)) {
                        setStillListening(false);
                    }
                }
            }

        } catch (SocketException e) {
            getLog().info("Lost connection to this client!");
        } catch (TransportException e) {
            e.printStackTrace();
            getLog().throwing(getClass().getName(), "run", e);
        } catch (IOException e) {
            e.printStackTrace();
            getLog().throwing(getClass().getName(), "run", e);
        }
        getTmCallBack().connectionDropped(getMyConnectionID());
    }

    private tmTypes getTmType() {
        return tmType;
    }

    private void setTmType(tmTypes tmType) {
        this.tmType = tmType;
    }
}
