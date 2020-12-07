// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.list;

import java.util.Collections;
import java.util.List;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Tests for verifying LoginList properly handles adding and removing multiple team logins.
 * 
 * @author John Clevenger (pc2@ecs.csus.edu)
 * 
 */

public class LoginListMultipleLoginsTest extends AbstractTestCase {
    
    private SampleContest sampleContest ;   
    private IInternalContest contest;
    
    private LoginList loginList = new LoginList();

    public LoginListMultipleLoginsTest() {
        super();
        
        // create a sample contest and controller
        sampleContest = new SampleContest();
        contest = sampleContest.createStandardContest();

        // get a judge id so we can create a log file
        ClientId judgeClientId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();
        contest.setClientId(judgeClientId);
//        System.out.println("LoginListMultipleLoginsTest: Logging to log file for " + judgeClientId);

//        log = controller.getLog();
//        String logName = log.getLogfilename();
//        System.out.println ("LoginListMultipleLoginsTest: Log file name = '" + logName + "'");
    }
  
    /**
     * Tests that a LoginList properly supports adding and removing duplicate Team logins.
     */
    public void testListSupportsMultipleTeamLogins() {
        
        //verify that we can switch from single-login-only mode (the default) to allow-multiple-logins
        assertFalse("Contest is not initialized in single-login mode", contest.getContestInformation().isAllowMultipleLoginsPerTeam());
        contest.getContestInformation().setAllowMultipleLoginsPerTeam(true);
        assertTrue("Unable to switch contest to allow-multiple-logins mode", contest.getContestInformation().isAllowMultipleLoginsPerTeam());
        
        //create a clientId for "Site 3 Team 12" and verify that client exists in the contest
        ClientId clientId1 = new ClientId(3, Type.TEAM, 12);
        assertTrue ("Invalid login: team12/team12", contest.isValidLoginAndPassword(clientId1, "team12"));
        
        //add Site 3 Team 12 (with its own ConnectionHandlerID) to the login list
        ConnectionHandlerID connectionHandlerID1 = new ConnectionHandlerID("id1");
        clientId1.setConnectionHandlerID(connectionHandlerID1);
        loginList.add(clientId1, connectionHandlerID1);
        
        //verify the list shows exactly one client logged in
        List<ConnectionHandlerID> connectionList = Collections.list(loginList.getConnectionHandlerIDs(clientId1));
        assertEquals("Improper connection list size after adding one logged in team: ", 1, connectionList.size());
        
        //add a second login, from a different connection, for the same team
        ClientId clientId2 = new ClientId(3, Type.TEAM, 12);
        ConnectionHandlerID connectionHandlerID2 = new ConnectionHandlerID("id2");
        clientId2.setConnectionHandlerID(connectionHandlerID2);
        loginList.add(clientId2, connectionHandlerID2);
        
        //verify the list shows exactly two logged in clients
        List<ConnectionHandlerID> connectionList1 = Collections.list(loginList.getConnectionHandlerIDs(clientId1));
        List<ConnectionHandlerID> connectionList2 = Collections.list(loginList.getConnectionHandlerIDs(clientId2));
        assertEquals("Improper connection list size after two logins for the same team: ", 2, connectionList1.size());
        assertEquals("Improper connection list size after two logins for the same team: ", 2, connectionList2.size());
        assertEquals("Connection lists do not match after two logins for the same team: ", connectionList1, connectionList2);
        
        //verify that the list has the correct (same) clientIds for each of the two login connectionHandlerIDs
        ClientId conn1Client = loginList.getClientId(connectionHandlerID1);
        ClientId conn2Client = loginList.getClientId(connectionHandlerID2);
        assertEquals("LoginList failed to return matching clientIds for duplicate logins:", conn1Client, conn2Client );
        assertNotEquals("LoginList matched clientIds which should not have matched:", conn1Client.toString(), new ClientId(1, Type.TEAM, 12).toString() );
        
        //verify the list does NOT return a valid clientId for some non-logged-in client connection (but rather it returns null)
        ConnectionHandlerID unknownConnID = new ConnectionHandlerID("unknownID");
        ClientId unknownClient = loginList.getClientId(unknownConnID);
        assertNull("LoginList failed to return null for non-logged-in connection " + unknownConnID, unknownClient);

        //verify that the list contains all (and only) the expected clients
        ClientId [] allClients = loginList.getClientIdList();
        assertEquals("Number of client logins does not match: ", 2, allClients.length);
        boolean match = allClients[0].equals(clientId1) && allClients[1].equals(clientId2);
        assertTrue("Clients in Login List do not match expected", match);
        
        //check all client types; verify that only the two teams are logged in and no others are logged in
        boolean ok = true;
        for (ClientType.Type clientType : ClientType.Type.values()) {
            
            // get all the clients of the current type
            Iterable<ClientId> clients = Collections.list(loginList.getClients(clientType));
            // check each client
            for (ClientId client : clients) {

                switch (clientType) {
                    case TEAM:
                        // it's a Team; make sure it is one of the logged in teams (both logins have the same clientId, just different connIDs)
                        if (!client.equals(clientId1) && !client.equals(clientId2)) {
                            ok = false;
                            break;
                        }
                        break;

                    default:
                        //there is some non-Team logged in; that's an error
                        ok = false;
                        break;
                }
            }
        }
        assertTrue("Found a client logged in other than the expected two teams", ok);
        
        //verify that there are exactly two ConnectionHandlerIDs for the logged in clients, and that they match the expected ones
        ConnectionHandlerID [] expected = {connectionHandlerID1, connectionHandlerID2} ;
        boolean ok1 = true;
        Boolean [] found = {false, false};
        Iterable<ConnectionHandlerID> connections = Collections.list(loginList.getConnectionHandlerIDs(clientId1));
        int foundCount = 0;
        for (ConnectionHandlerID connID : connections) {
            foundCount++;
            if (connID.equals(expected[0])) {
                found[0] = true;
            } else if (connID.equals(expected[1])) {
                found[1] = true;
            } else {
                //we found an unexpected connection
                ok1 = false;
            }
        }
        ok1 = ok1 && foundCount==2 && found[0] && found[1];
        assertTrue("Failed to find expected ConnectionHandlerIDs, or found unexpected one", ok1);
        
        //verify that we current have exactly two logins for Team12 Site 3
        int count = 0;
        for (ConnectionHandlerID connID : connections) {
            if (loginList.getClientId(connID).equals(clientId1) || loginList.getClientId(connID).equals(clientId2)) {
                count++;
            }
        }
        assertEquals("Incorrect number of login connections for Team12 Site 3: ", 2, count);

        //verify that we can remove one of the two duplicate logins and the other remains
        loginList.remove(clientId1);
        List<ConnectionHandlerID> remainingConnections = Collections.list(loginList.getConnectionHandlerIDs(clientId2));
        assertEquals("Incorrect number of connections in LoginList after logging out one duplicate team: ", 1, remainingConnections.size());
        ConnectionHandlerID remainingConnection = remainingConnections.get(0);
        assertTrue("Incorrect remaining connection for Client2 after logging out Client1.", remainingConnection.equals(connectionHandlerID2));
        
        //verify we can add "FauxSite" logins without getting exceptions due to mismatch between ClientId ConnectionHandler and FauxConnectionHandler
        ClientId clientId3 = new ClientId(3, Type.TEAM, 14);
        ConnectionHandlerID connectionHandlerID3 = new ConnectionHandlerID("id3");
        clientId3.setConnectionHandlerID(connectionHandlerID3);
        ConnectionHandlerID fauxConnID = new ConnectionHandlerID("FauxSite" + clientId3.getSiteNumber() + clientId3);
        try {
            loginList.add(clientId3, fauxConnID);
        } catch (IllegalArgumentException e) {
            failTest("Failed to add 'FauxSite' ConnectionID to LoginList", e);
        }

    }

}
