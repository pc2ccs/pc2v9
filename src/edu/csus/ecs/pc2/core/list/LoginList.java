package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Maintain a list of logged in client.
 * 
 * Maintain a list of logged in users, when the logged in and the
 * {@link edu.csus.ecs.pc2.core.transport.ConnectionHandlerID ConnectionHandlerID} for that login.
 * 
 * <P>
 * Use: {@link #isValidConnectionID(ClientId, ConnectionHandlerID)} for security purposes, that method insures that the ClientId
 * matches the ConnectionId that the clientId was intialially
 * <P>
 * 
 * Maintains 3 maps: {@link edu.csus.ecs.pc2.core.model.ClientId} to {@link edu.csus.ecs.pc2.core.transport.ConnectionHandlerID},
 * and {@link edu.csus.ecs.pc2.core.transport.ConnectionHandlerID} to {@link edu.csus.ecs.pc2.core.model.ClientId} and
 * {@link edu.csus.ecs.pc2.core.model.ClientId} to {@link java.util.Date}
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu/csus/ecs/pc2/core/Problem.java$
public class LoginList implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6212994169038018512L;

    public static final String SVN_ID = "$Id$";

    /**
     * ClientId for a ConnectionHandlerID.
     */
    private Hashtable<ClientId, ConnectionHandlerID> clientHandlerHash = new Hashtable<ClientId, ConnectionHandlerID>();

    /**
     * ConnectionHandlerID for a ClientId.
     */
    private Hashtable<ConnectionHandlerID, ClientId> handlerClientHash = new Hashtable<ConnectionHandlerID, ClientId>();

    /**
     * ClientId to Date.
     * 
     * Logged in date/time for ClientId.
     */
    private Hashtable<ClientId, Date> clientDateHash = new Hashtable<ClientId, Date>();

    /**
     * Add or update a clientId in the list.
     * 
     * @param clientId
     * @param connectionHandlerID
     */
    public void add(ClientId clientId, ConnectionHandlerID connectionHandlerID) {
        if (connectionHandlerID == null) {
            throw new IllegalArgumentException("connection Id is null");
        }
        if (clientId == null) {
            throw new IllegalArgumentException("clientId is null");
        }
        synchronized (clientHandlerHash) {

            clientHandlerHash.put(clientId, connectionHandlerID);
            handlerClientHash.put(connectionHandlerID, clientId);
            clientDateHash.put(clientId, new Date());
        }
    }

    /**
     * remove client from loginlist.
     * 
     * @param clientId
     * @return true if deleted, false if not deleted or not found.
     */
    public boolean remove(ClientId clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("clientId is null");
        }
        synchronized (clientHandlerHash) {

            ConnectionHandlerID connectionHandlerID = clientHandlerHash.get(clientId);

            if (connectionHandlerID != null) {
                handlerClientHash.remove(connectionHandlerID);
                clientHandlerHash.remove(clientId);
                clientDateHash.remove(clientId);

                return true;
            }
            return false;
        }
    }

    /**
     * Returns an enumeration of logged in clients.
     * 
     * @param type
     *            {@link edu.csus.ecs.pc2.core.model.ClientType.Type}
     * @return enumeration of clients.
     */
    public Enumeration<ClientId> getClients(ClientType.Type type) {
        synchronized (clientHandlerHash) {
            Vector<ClientId> v = new Vector<ClientId>();
            Enumeration<ClientId> enumeration = clientHandlerHash.keys();
            while (enumeration.hasMoreElements()) {
                ClientId clientId = (ClientId) enumeration.nextElement();
                if (clientId.getClientType() == type) {
                    v.addElement(clientId);
                }
            }
            return v.elements();
        }
    }

    public Enumeration<ConnectionHandlerID> getHandles(ClientType.Type type) {
        synchronized (clientHandlerHash) {
            Vector<ConnectionHandlerID> v = new Vector<ConnectionHandlerID>();
            Enumeration<ClientId> enumeration = clientHandlerHash.keys();
            while (enumeration.hasMoreElements()) {
                ClientId clientId = (ClientId) enumeration.nextElement();
                if (clientId.getClientType() == type) {
                    ConnectionHandlerID connectionHandlerID = clientHandlerHash.get(clientId);
                    v.addElement(connectionHandlerID);
                }
            }

            return v.elements();
        }

    }

    /**
     * Fetch client id.
     * 
     * @param connectionHandlerID
     * @return null if not found, otherwise ClientId
     */
    public ClientId getClientId(ConnectionHandlerID connectionHandlerID) {
        if (connectionHandlerID == null) {
            return null;
        }
        return handlerClientHash.get(connectionHandlerID);
    }

    /**
     * Fetch ConnectionHandlerID for client.
     * 
     * @param clientId
     * @return ConnectionHandlerID or null if not found.
     */
    public ConnectionHandlerID getConnectionHandleID(ClientId clientId) {
        if (clientId == null) {
            return null;
        }
        return clientHandlerHash.get(clientId);
    }

    /**
     * 
     * @param clientId
     * @return Date or null (if client not found).
     */
    public Date getLoggedInDate(ClientId clientId) {
        return clientDateHash.get(clientId);
    }

    /**
     * is client logged in ?.
     * 
     * To check security of client use {@link #isValidConnectionID(ClientId, ConnectionHandlerID)}.
     * 
     * @param clientId
     * @return true if already logged in.
     */
    public boolean isLoggedIn(ClientId clientId) {
        ConnectionHandlerID connectionHandlerID = getConnectionHandleID(clientId);
        return connectionHandlerID != null;
    }

    /**
     * Compares clientId and input ConnectionHandlerID with saved ConnectionHandlerID.
     * 
     * @param clientId
     * @param connectionHandlerID
     * @return true if client has logged into input connection.
     */
    public boolean isValidConnectionID(ClientId clientId, ConnectionHandlerID connectionHandlerID) {
        ConnectionHandlerID savedConnectionHandlerID = getConnectionHandleID(clientId);
        return connectionHandlerID.equals(savedConnectionHandlerID);
    }

    /**
     * Get list of all connectionHandlers
     * 
     * @return an array of handler IDs
     */
    public ConnectionHandlerID[] getHandlesList() {
        ConnectionHandlerID[] theList = new ConnectionHandlerID[clientHandlerHash.size()];

        if (theList.length == 0) {
            return theList;
        }

        theList = (ConnectionHandlerID[]) clientHandlerHash.values().toArray(new ConnectionHandlerID[clientHandlerHash.size()]);
        return theList;
    }

    /**
     * Get a list of all logged in clients.
     */
    public ClientId[] getClientIdList() {
        ClientId[] theList = new ClientId[handlerClientHash.size()];

        if (theList.length == 0) {
            return theList;
        }

        theList = (ClientId[]) handlerClientHash.values().toArray(new ClientId[handlerClientHash.size()]);
        return theList;
    }
}
