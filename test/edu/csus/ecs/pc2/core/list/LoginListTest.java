package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import junit.framework.TestCase;

/**
 * Test for loginlist 
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class LoginListTest extends TestCase {

    private LoginList loginList = new LoginList();

    private int siteNumber = 120;

    public void testSimple() {
        ConnectionHandlerID connectionHandlerID = new ConnectionHandlerID("id1");
        ClientId clientId = new ClientId(siteNumber, Type.TEAM, 12);

        loginList.add(clientId, connectionHandlerID);

        ConnectionHandlerID connectionHandlerID2 = loginList.getConnectionHandleID(clientId);

        assertTrue("Failed getConnectionHandleID lookup ", connectionHandlerID.equals(connectionHandlerID2));
        assertTrue("Failed getConnectionHandleID lookup 2 ", connectionHandlerID.toString().equals(connectionHandlerID2.toString()));

    }

    public void testFifth() {
        ConnectionHandlerID connectionHandlerID = new ConnectionHandlerID("id1");
        ClientId clientId = new ClientId(siteNumber, Type.TEAM, 12);
        ClientId clientId55 = new ClientId(siteNumber, Type.TEAM, 22);

        loginList.add(clientId, connectionHandlerID);

        ConnectionHandlerID connectionHandlerID2 = loginList.getConnectionHandleID(clientId);

        System.err.println("debug " + connectionHandlerID);
        System.err.println("debug " + connectionHandlerID2);

        assertTrue("Failed getConnectionHandleID lookup ", connectionHandlerID.equals(connectionHandlerID2));

        ClientId clientId2 = loginList.getClientId(connectionHandlerID);
        assertTrue("Failed getClientId lookup ", connectionHandlerID.equals(connectionHandlerID2));

        clientId2 = loginList.getClientId(connectionHandlerID2);

        System.err.println("debug " + clientId);
        System.err.println("debug " + clientId2);

        assertTrue("Failed getClientId lookup ", clientId.equals(clientId2));

        connectionHandlerID2 = loginList.getConnectionHandleID(clientId55);
        assertTrue("Failed getClientId lookup ", connectionHandlerID2 == null);

        ConnectionHandlerID nextConnectionHandlerID = new ConnectionHandlerID("id1");
        loginList.add(clientId55, nextConnectionHandlerID);
        ClientId clientId3 = loginList.getClientId(nextConnectionHandlerID);

        assertFalse("Failed getClientId 2 lookup ", clientId55.equals(clientId2));

        System.err.println();
        System.err.println("debug " + clientId2);
        System.err.println("debug " + clientId3);
        System.err.println("debug " + clientId55);
        System.err.println();

        assertTrue("Failed getClientId 2A lookup ", clientId55.equals(clientId3));

        connectionHandlerID2 = new ConnectionHandlerID("id3");

        loginList.add(clientId, connectionHandlerID2);
        ClientId clientId4 = loginList.getClientId(connectionHandlerID2);
        System.err.println();
        System.err.println("debug " + clientId);
        System.err.println("debug " + clientId4);
        System.err.println();
        assertTrue("Failed getClientId 2B lookup ", clientId.equals(clientId4));

    }

}
