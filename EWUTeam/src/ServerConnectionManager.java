import edu.csus.ecs.pc2.api.*;
import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;
import java.security.Permission;

import java.util.*;

// This class stores a collection of PC^2 ServerConnections in a Java HashMap.
//	
//	Standard HashMap errors are translated to PC^2 exceptions.
public class ServerConnectionManager
{
	private HashMap<String, ServerConnection> connections;
	
	public ServerConnectionManager()
	{
		connections = new HashMap<String, ServerConnection>();
		
		//cause System.exit() calls to throw a security exception
		System.setSecurityManager(new SecurityManager()
		{
			public void checkPermission(Permission perm){}
			public void checkExit(int status){throw new SecurityException();}
		});
	}
	
//-------------------------------------Add Team
	public void addTeam(String teamKey, String username, String password) throws LoginFailureException
	{
		
		//If the server is not running, the program will attempt to exit, and throw a SecurityException
		try
		{
			ServerConnection newTeam = new ServerConnection();
			newTeam.login(username, password);
			connections.put(teamKey, newTeam);
		}
		catch(SecurityException e)
		{
			throw new LoginFailureException("Failed to connect to server");
		}
		
		
	}//end method:addTeam(...);
	
//-------------------------------------Get Team
	//Get specific pc2 team connection.
	// If team is not currently logged in, throws NotLoggedInException
	public ServerConnection getTeam(String teamKey) throws NotLoggedInException
	{
		ServerConnection team = connections.get(teamKey);
		if(team == null)
			throw new NotLoggedInException();
		return team;
	}
	
//-------------------------------------Remove Team
	public void removeTeam(String teamKey) throws NotLoggedInException
	{
		ServerConnection connectionToRemove = connections.get(teamKey);
		if(connectionToRemove == null)
			throw new NotLoggedInException();
		connections.remove(teamKey);
		connectionToRemove.logoff();

	
	}//end method:removeTeam(...);
	
	public void clean()
	{
		for(ServerConnection sc : connections.values())
		{
			try
			{
				sc.logoff();
			}
			catch(NotLoggedInException e)
			{
				
			}
		}
	}
}//end class:ServerconnectionManager
