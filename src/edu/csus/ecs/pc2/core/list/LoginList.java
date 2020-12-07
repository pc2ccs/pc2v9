// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
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
     * A mapping from ClientId to a list of (zero or more) ConnectionHandlerIDs for that Client.
     * This map was added to support multiple simultaneous teams logins; it replaces the earlier hashtable
     * which only allowed associating a single connectionHandlerID with each client.
     * (Note that currently, for clients OTHER THAN TEAMs, the contents of this map will be identical
     * to what previously existed in the old hashtable except that the "value" returned by the "get()" 
     * method will be a LIST (containing one ConnectionHandlerID) rather than a ConnectionID object.
     */
//    private Hashtable<ClientId, ConnectionHandlerID> clientHandlerHash = new Hashtable<ClientId, ConnectionHandlerID>();
    private HashMap<ClientId, List<ConnectionHandlerID>> clientToConnectionHandlerList = new HashMap<ClientId, List<ConnectionHandlerID>>();

    /**
     * A mapping from ConnectionHandlerIDs to the client being handled by that connection.
     */
    private Hashtable<ConnectionHandlerID, ClientId> handlerClientHash = new Hashtable<ConnectionHandlerID, ClientId>();

    /**
     * ClientId to Date.
     * 
     * Logged in date/time for ClientId.
     * 
     * TODO: Currently a new (TEAM) login will overwrite the existing Date in this table if multiple
     *      team logins are being allowed; thus, the entry in this table will be the MOST RECENT team
     *      login date.  Need to decide whether this makes an importance difference anywhere...
     */
    private Hashtable<ClientId, Date> clientDateHash = new Hashtable<ClientId, Date>();

    /**
     * Add or update a clientId in the list.
     * Specifically, what this method does is add the specified ConnectionHandlerID to the
     * list of ConnectionHandlerIDs associated with the specified ClientId.  
     * 
     * Note that a given ClientId may have multiple ConnectionHandlerIDs associated with it
     * if multiple simultaneous logins are allowed for the ClientType of the specified client.
     * 
     * Note also that this method was originally created before the advent of support for "multiple 
     * simultaneous logins".  At that time, ClientIds did not contain a ConnectionHandlerID, so this
     * method required two parameters -- the ClientId to be added and the ConnectionHandlerID associated with
     * that ClientId. Now, the ClientId should already contain the proper ConnectionHandlerID (and this method
     * checks for that condition and throws an exception if not); the two-parameter version was retained for
     * backwards-compatibility with other code.
     * 
     * @param clientId
     * @param connectionHandlerID
     * 
     * @throws IllegalArgumentException if the specified ClientId or ConnectionHandlerID is null,
     *              if the ClientId has a null ConnectionHandlerID, or
     *              if the specified ConnectionHandlerID does not match the ConnectionHandlerID in the ClientId.
     */
    public void add(ClientId clientId, ConnectionHandlerID connectionHandlerID) {
        if (clientId == null) {
            throw new IllegalArgumentException("clientId is null, connectionHandlerID="+connectionHandlerID);
        }
        if (connectionHandlerID == null) {
            throw new IllegalArgumentException("connection Id is null, clientId="+clientId);
        }
        
        //this method should never be called with a ClientId containing a null ConnectionHandlerID; however, the addition of 
        // "multiple login" support may have left some place where this is inadvertently true.
        //The following is an effort to catch/identify such situations.
        ConnectionHandlerID clientConnection = clientId.getConnectionHandlerID();
        if (clientConnection==null) {
            IllegalArgumentException e = new IllegalArgumentException("LoginList.add() called with null ConnectionHandlerID in ClientId " + clientId);
            e.printStackTrace();
            throw e;
        }
        
        //this method should never be called with a ClientId containing a ConnectionHandlerID which doesn't match the ConnectionID specified by the 
        // second method parameter; however, the addition of  "multiple login" support may have left some place where this is inadvertently true.
        //The following is an effort to catch/identify such situations.
        //Note: one exception to the rule about ConnectionHandlerIDs being required to match is if one of the ConnectionHandlerIDs is a "Faux" id...
        if (!clientId.getConnectionHandlerID().equals(connectionHandlerID)) {
            //check for a match that discounts "FauxSite" connections
            boolean fauxMatch = connectionHandlerID.toString().startsWith("FauxSite") || clientId.getConnectionHandlerID().toString().startsWith("FauxSite") ;
            if (!fauxMatch) {
                IllegalArgumentException e = new IllegalArgumentException("LoginList.add() called with ConnectionHandlerID not matching that in the ClientId: " + "ClientId.ConnectionHandler = "
                        + clientId.getConnectionHandlerID() + "; specified ConnectionHandlerID = " + connectionHandlerID);
                e.printStackTrace();
                throw e;
            }            
        }

        synchronized (clientToConnectionHandlerList) {
            
            //see if this client already has an entry in the list of clientToConnectionHandlers
            if (clientToConnectionHandlerList.get(clientId)==null) {
                //no entry exists; add an (empty) ArrayList for this client
                clientToConnectionHandlerList.put(clientId, new ArrayList<ConnectionHandlerID>());
            }
            //add the received ConnectionHandlerID to the list for this client
            clientToConnectionHandlerList.get(clientId).add(connectionHandlerID);

            handlerClientHash.put(connectionHandlerID, clientId);
            clientDateHash.put(clientId, new Date());
        }
    }

    /**
     * Remove client from loginlist.  
     * This method searches the list of currently logged-in clients for a client matching the
     * specified {@link ClientId} in client type, client number, client site number, and
     * client {@link ConnectionHandlerID}, and removes that client from the list if found.
     * 
     * Note that if multiple client logins are being allowed, there may be <I>more than one</i>
     * logged in client with the same client type, client number, and client site -- but there
     * should be at most one logged in client matching all those fields AND matching the 
     * ConnectionHandlerID of the specified client.  If more than one logged-in client with the
     * same client type, number, and site exists, only the one with the matching ConnectionHandlerID is removed.
     * 
     * @param clientId the ClientId whose login is to be removed.
     * 
     * @return true if the specified client was found and successfully removed, false otherwise.
     * 
     * @throws IllegalArgumentException if the specified clientId is null or if the specified clientId contains a null ConnectionHandlerID.
     */
    public boolean remove(ClientId clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("clientId is null");
        }
        
        //this method should never be called with a ClientId containing a null ConnectionHandlerID; however, the addition of 
        // "multiple login" support may have left some place where this is inadvertently true.
        //The following is an effort to catch/identify such situations.
        ConnectionHandlerID clientConnection = clientId.getConnectionHandlerID();
        if (clientConnection==null) {
            IllegalArgumentException e = new IllegalArgumentException("InternalContest.removeLogin() called with null ConnectionHandlerID in ClientId " + clientId);
            e.printStackTrace();
            throw e;
        }

        boolean returnFlag ;
        
        synchronized (clientToConnectionHandlerList) {

            // get a list of all the connectionHandlerIDs associated with this client
            // (for all clients except TEAMs this list will have at most one element; for Teams it
            // could have more if multiple team logins are being allowed)
            List<ConnectionHandlerID> connectionHandlerList = clientToConnectionHandlerList.get(clientId);

            // if we didn't even get a valid list, we know we've failed to find the specified client
            returnFlag = connectionHandlerList != null;

            if (connectionHandlerList != null) {

                boolean foundConnection = false;
                // search the list of connectionHandlers
                for (ConnectionHandlerID connHID : connectionHandlerList) {
                    if (connHID != null) {
                        // check if the current connHID matches the client
                        if (clientConnection.equals(connHID)) {
                            // match; remove it from the mapping of connectionHandlers to clientIds
                            foundConnection = true;
                            ClientId removedClient = handlerClientHash.remove(connHID);
                            // update the "found" flag based on whether removal was successful (i.e. connHID was found in the table and removed)
                            returnFlag = returnFlag && (removedClient != null);
                        }
                    }
                }
                
                //we're only going to be successful if we found the client's connection
                returnFlag = returnFlag && foundConnection;

                // try to remove the connection from the list of connections for the specified client
                boolean connectionRemovalWasSuccessful = connectionHandlerList.remove(clientConnection);
                // update the return flag to include whether we were successful in removing the connection from the client's list of connections
                returnFlag = returnFlag && connectionRemovalWasSuccessful;

                // if the client has no more connections, remove the client from the map of clients to connections and update the return flag
                if (connectionHandlerList.size() == 0) {
                    // remove the client from the list, but only if it was mapped to the same ConnectionHandlerList we've been using
                    boolean clientRemovalWasSuccessful = clientToConnectionHandlerList.remove(clientId, connectionHandlerList);
                    returnFlag = returnFlag && clientRemovalWasSuccessful;
                }

                // remove the client from the map of clientIds to connection dates and update the return flag
                returnFlag = returnFlag && clientDateHash.remove(clientId) != null;
            }
        }

        //at this point "found" will only be true if there was a non-null list of connectionHandlerIDs for the client
        // AND the client's connectionHandlerID was found and successfully removed from the map of connectionHandlers to clientIds
        // AND the client's connection was successfully removed from the list of client connections
        // AND if the client's connection was the last connection for this client then the clientId was successfully removed from the map of clients to connections
        // AND the clientId was successfully removed from the client-to-date map.
        return returnFlag;
    }

    /**
     * Returns an enumeration of logged in clients of the specified {@link ClientType.Type}.
     * The returned enumeration may be empty but will never be null.
     * 
     * Note that if multiple simultaneous logins are allowed for the specified
     * ClientType then the returned enumeration may contain multiple ClientIds
     * for the same client (for example, if the specified ClientType is TEAM
     * and there are currently two logins for "team1", the returned enumeration
     * will contain two ClientIds matching "TEAM1"). 
     * 
     * @param type the {@link edu.csus.ecs.pc2.core.model.ClientType.Type} to be searched for.
     * 
     * @return enumeration of logged-in clients of the specified ClientType.
     */
    public Enumeration<ClientId> getClients(ClientType.Type type) {
        
        //TODO: is there a potential synchronization problem with this being synchronized on a different object
        //   than other methods in this class?
        synchronized (handlerClientHash) {
            Vector<ClientId> v = new Vector<ClientId>();
            
            for (ClientId client : handlerClientHash.values()) {
                if (client.getClientType() == type) {
                    v.addElement(client);
                }
            }
            
            return v.elements();
        }
    }

    /**
     * Returns an enumeration of ConnectionHandlerIDs associated with the specified ClientType.
     * Note that if multiple simultaneous logins are allowed for the specified ClientType, the returned 
     * enumeration will contain ALL ConnectionHandlerIDs for clients of that ClientType,
     * including multiple ConnectionHandlerIDs for a given client if there is currently
     * more than one login for that client.
     * 
     * If the specified ClientType is null, null is returned; otherwise, a (possibly empty)
     * Enumeration of ConnectionHandlerIDs for the specified ClientType is returned.
     * 
     * @param type the {@link ClientType} whose ConnectionHandlerIDs are being requested.
     * 
     * @return a (possibly empty) Enumeration of all current ConnectionHandlerIDs for the specified ClientType, or null.
     */
    public Enumeration<ConnectionHandlerID> getHandles(ClientType.Type type) {
        
        if (type==null) {
            return null;
        }
        
        synchronized (clientToConnectionHandlerList) {
            Vector<ConnectionHandlerID> v = new Vector<ConnectionHandlerID>();
            
            for (ClientId client : clientToConnectionHandlerList.keySet()) {
                if (client.getClientType() == type) {
                    List<ConnectionHandlerID> connList = clientToConnectionHandlerList.get(client);
                    for (ConnectionHandlerID connHID : connList) {
                        v.addElement(connHID);
                    }
                }
            }
            
            return v.elements();
        }
    }

    /**
     * Fetch client id associated with the specified ConnectionHandlerID.
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
     * Fetch {@link ConnectionHandlerID}(s) for the specified client.
     * 
     * If the specified ClientId is null, an empty Enumeration is returned.  Otherwise, an Enumeration of the ConnectionHandlerIDs
     * associated with the specified Client is returned; the returned Enumeration will be empty if the specified
     * client has never logged in or has no associated ConnectionHanderIDs.
     * 
     * Note that if multiple simultaneous logins are allowed for the ClientType of the specified ClientId,
     * the returned Enumeration may contain multiple ConnectionHandlerIDs for that client (depending on 
     * whether or not there are currently multiple active logins for that client).
     * 
     * @param clientId the client whose ConnectionHandlerIDs are sought.
     * 
     * @return a (possibly empty) Enumeration of the ConnectionHandlerIDs associated with the specified ClientId.
     */
    public Enumeration<ConnectionHandlerID> getConnectionHandlerIDs(ClientId clientId) {
        if (clientId == null) {
            return Collections.emptyEnumeration();
        }

        synchronized (clientToConnectionHandlerList) {
            Vector<ConnectionHandlerID> v = new Vector<ConnectionHandlerID>();
            
            List<ConnectionHandlerID> clientConnectionList = clientToConnectionHandlerList.get(clientId);
            
            //make sure the client was actually present in the map
            if (clientConnectionList != null) {
                //we got a (possibly empty) list from the map; add each non-null entry to the vector
                for (ConnectionHandlerID connHID : clientConnectionList) {
                    if (connHID != null) {
                        v.addElement(connHID);
                    }
                } 
            }
            
            //return whatever ConnectionHandlerIDs were found (could be empty, if either the client wasn't found
            //  in the map or the client was found but there were no ConnectionHandlerIDs in its list)
            if (v.size()==0) {
                return Collections.emptyEnumeration();
            } else {
                return v.elements();
            }
        }
    }

    /**
     * Returns the logged-in date for the most recent login of the specified ClientId.
     * Note that if multiple logins are being allowed (e.g. for teams), then the
     * returned Date will be the most recent login date even if there are multiple
     * team logins currently active.
     * 
     * @param clientId
     * @return Date or null (if client not found).
     */
    public Date getLoggedInDate(ClientId clientId) {
        return clientDateHash.get(clientId);
    }

    /**
     * Returns true if the specified client is logged in.
     * 
     * Note that if the specified ClientId has a ClientType for which multiple simultaneous logins
     * are allowed, this method returns true if there is ANY (that is, one OR MORE) logins active
     * for the specified Client.
     * 
     * To check security of client use {@link #isValidConnectionID(ClientId, ConnectionHandlerID)}.
     * 
     * @param clientId
     * @return true if the specified ClientId is currently logged in (defined as "has a ConnectionHandlerID associated with it").
     */
    public boolean isLoggedIn(ClientId clientId) {
        Enumeration<ConnectionHandlerID> connHIDs = getConnectionHandlerIDs(clientId);
        return (connHIDs!=null && connHIDs.hasMoreElements());
    }

    /**
     * Compares the specified clientId and input ConnectionHandlerID with the current saved
     * mapping from clientId to ConnectionHandlerIDs; returns true if the specified
     * ConnectionHandlerID is a valid connection to the specified client.
     * 
     * Note that if multiple logins are allowed, it is possible that the specified ClientId
     * might map to more than one ConnectionHandlerID.  In that case, this method checks
     * ALL of the current ConnectionHandlerIDs associated with the specified ClientId
     * and returns true if ANY ONE of them matches.
     * 
     * @param clientId the ClientID whose ConnectionHandlerID is to be validated.
     * @param connectionHandlerID the purported ConnectionHandlerID for the specified ClientId.
     * 
     * @return true if the specified ConnectionHandlerID is equal to any of the ConnectionHandlerIDs
     *          for the specified ClientId; false if not.
     */
    public boolean isValidConnectionID(ClientId clientId, ConnectionHandlerID connectionHandlerID) {
        
//        ConnectionHandlerID savedConnectionHandlerID = getConnectionHandleID(clientId);
//        return connectionHandlerID.equals(savedConnectionHandlerID);
        
        List<ConnectionHandlerID> connectionList = clientToConnectionHandlerList.get(clientId);
        
        if (connectionList == null) {
            return false;
        } else {
            for (ConnectionHandlerID connHID : connectionList) {
                if (connHID.equals(connectionHandlerID)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get list of all connectionHandlers.  Note that if multiple simultaneous client logins are
     * allowed (e.g. for Teams), there could be multiple ConnectionHandlers for a given client;
     * in that case the returned array contains ALL of the ConnectionHandlers, including the 
     * multiple ConnectionHandlers for each client.  This in turn means that the size of the 
     * returned array is NOT a valid indication of the total number of different clients currently logged in.
     * 
     * @return an array of ConnectionHandlerIDs
     */
    public ConnectionHandlerID[] getHandlesList() {
        ConnectionHandlerID[] theList = new ConnectionHandlerID[handlerClientHash.keySet().size()];

        if (theList.length > 0) {
            
//            theList = (ConnectionHandlerID[]) clientHandlerHash.values().toArray(new ConnectionHandlerID[clientHandlerHash.size()]);
//            return theList;
            
            //copy each connectionHandlerID into theList array
            int i=0; 
            for (ConnectionHandlerID connHID : handlerClientHash.keySet()) {
                theList[i++] = connHID;
            }
        }
        
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
