// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
import java.security.Permission;
import java.util.HashMap;
import java.util.logging.Level;

import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;
import edu.csus.ecs.pc2.core.log.StaticLog;

/**
 * Manages a collection of PC^2 ServerConnections (API logins).
 * 
 * Standard HashMap errors are translated to PC^2 exceptions.
 * 
 * @author pc2@ecs.csus.edu
 */
public class ServerConnectionManager {
	
	/**
	 * List of teamKey to ServerConnection connections
	 */
	private HashMap<String, ServerConnection> connections;

	public ServerConnectionManager() {
		
		// REFACTOR: should connections be initialized when the field is declared ?
		connections = new HashMap<String, ServerConnection>();

		// cause System.exit() calls to throw a security exception
		System.setSecurityManager(new SecurityManager() {
			public void checkPermission(Permission perm) {
			}

			public void checkExit(int status) {
				StaticLog.info("checkExit invoked throwing SecurityException");
				throw new SecurityException();
			}
		});
	}

	// -------------------------------------Add Team
	public void addTeam(String teamKey, String username, String password)
			throws LoginFailureException {

		// If the server is not running, the program will attempt to exit, and
		// throw a SecurityException
		try {
			ServerConnection newTeam = new ServerConnection();
			newTeam.login(username, password);
			connections.put(teamKey, newTeam);
			StaticLog.info("Team logged in user="+username+" key="+teamKey);
		} catch (SecurityException e) {
			StaticLog.info("Failed to login in addTeam,  teamKey=" + teamKey + " username=" + username + " " + e.getMessage());
			throw new LoginFailureException("Failed to connect to server");
		}

	}// end method:addTeam(...);

	// -------------------------------------Get Team
	// Get specific pc2 team connection.
	// If team is not currently logged in, throws NotLoggedInException
	public ServerConnection getTeam(String teamKey) throws NotLoggedInException {
		ServerConnection team = connections.get(teamKey);
		if (team == null) {
			throw new NotLoggedInException("Team not found in ");
		}
		return team;
	}

	// -------------------------------------Remove Team
	public void removeTeam(String teamKey) throws NotLoggedInException {
		ServerConnection connectionToRemove = connections.get(teamKey);
		if (connectionToRemove == null) {
			throw new NotLoggedInException("team key = "+teamKey);
		}
		
		try {
			connections.remove(teamKey);
			connectionToRemove.logoff();
		} catch (Exception e) {
			StaticLog.getLog().log(Level.WARNING, "Trouble logging out " + teamKey, e);
		}

	}// end method:removeTeam(...);

	/**
	 * Logoff/remove all server connections
	 */
	public void clean() {
		for (ServerConnection sc : connections.values()) {
			try {
				sc.logoff();
				// REFACTOR: FIX in addition to logoff should this server connection be removed from the field connections ?
			} catch (NotLoggedInException e) {
				StaticLog.info("Logoff failed in clean "+e.getMessage());
			}
		}
	}
}// end class:ServerconnectionManager
