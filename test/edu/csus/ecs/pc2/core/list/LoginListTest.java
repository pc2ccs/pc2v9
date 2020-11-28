// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.list;

import junit.framework.TestCase;

import java.util.Collections;
import java.util.List;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Test for loginlist 
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

public class LoginListTest extends TestCase {

    private LoginList loginList = new LoginList();

    private int siteNumber = 120;

    private boolean debugMode = false;

    /**
     * Tests that when a client and connectionHandler is added to a LoginList, method getConnectionHandlerIDS 
     * returns a list containing the client and connectionHandler (and nothing else).
     */
    public void testSimple() {
        ConnectionHandlerID connectionHandlerID = new ConnectionHandlerID("id1");
        ClientId clientId = new ClientId(siteNumber, Type.TEAM, 12);

        clientId.setConnectionHandlerID(connectionHandlerID);
        loginList.add(clientId, connectionHandlerID);

        List<ConnectionHandlerID> connectionList = Collections.list(loginList.getConnectionHandlerIDs(clientId));
        
        assertTrue("Failed getConnectionHandlerID lookup ", connectionList.contains(connectionHandlerID));
        assertTrue("Failed getConnectionHandlerID lookup 2 ", connectionHandlerID.toString().equals(connectionList.get(0).toString()));
        
        assertTrue("Incorect number of connectionHandlerIDs in LoginList ", connectionList.size()==1);

    }

    public void testFifth() {
        ConnectionHandlerID connectionHandlerID = new ConnectionHandlerID("id1");
        ClientId clientId = new ClientId(siteNumber, Type.TEAM, 12);
        ClientId clientId55 = new ClientId(siteNumber, Type.TEAM, 22);

        clientId.setConnectionHandlerID(connectionHandlerID);
        loginList.add(clientId, connectionHandlerID);

        List<ConnectionHandlerID> connectionList = Collections.list(loginList.getConnectionHandlerIDs(clientId));
        ConnectionHandlerID connectionHandlerID2 = connectionList.get(0);

        if (debugMode){
            System.err.println("debug " + connectionHandlerID);
            System.err.println("debug " + connectionHandlerID2);
        }

        assertTrue("Failed getConnectionHandleID lookup ", connectionHandlerID.equals(connectionHandlerID2));

        ClientId clientId2 = loginList.getClientId(connectionHandlerID);
        assertTrue("Failed getClientId lookup ", connectionHandlerID.equals(connectionHandlerID2));

        clientId2 = loginList.getClientId(connectionHandlerID2);

        if (debugMode){
        System.err.println("debug " + clientId);
        System.err.println("debug " + clientId2);
        }
        assertTrue("Failed getClientId lookup ", clientId.equals(clientId2));

        //test attempting to fetch connectionHandlerIDs for a client that has never been added to the LoginList
        List<ConnectionHandlerID> connectionList2 = Collections.list(loginList.getConnectionHandlerIDs(clientId55));
        assertFalse ("LoginList.getConnectionHandlerIDs() returned null ", connectionList2==null);
        assertTrue("Failed getClientId lookup for non-logged in client", connectionList2.isEmpty());

        
        ConnectionHandlerID nextConnectionHandlerID = new ConnectionHandlerID("id1");
        clientId55.setConnectionHandlerID(nextConnectionHandlerID);
        loginList.add(clientId55, nextConnectionHandlerID);
        ClientId clientId3 = loginList.getClientId(nextConnectionHandlerID);

        assertFalse("Failed getClientId 2 lookup ", clientId55.equals(clientId2));
        if (debugMode){

        System.err.println();
        System.err.println("debug " + clientId2);
        System.err.println("debug " + clientId3);
        System.err.println("debug " + clientId55);
        System.err.println();
        }
        assertTrue("Failed getClientId 2A lookup ", clientId55.equals(clientId3));

        connectionHandlerID2 = new ConnectionHandlerID("id3");

        clientId.setConnectionHandlerID(connectionHandlerID2);
        loginList.add(clientId, connectionHandlerID2);
        ClientId clientId4 = loginList.getClientId(connectionHandlerID2);
        
        if (debugMode){
        System.err.println();
        System.err.println("debug " + clientId);
        System.err.println("debug " + clientId4);
        System.err.println();
        }
        
        assertTrue("Failed getClientId 2B lookup ", clientId.equals(clientId4));

    }

}
