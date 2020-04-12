package utils;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.mockito.Mockito;

import controllers.MainController;
import controllers.TeamsController;
import edu.csus.ecs.pc2.api.IContest;
import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.ServerConnection;
import models.LoginResponseModel;

/**
 * This class constructs a "PC2 server connections" HashMap mapping a predetermined team "key" to a "mock" 
 * PC2 {@link ServerConnection} object and injects the HashMap into the static "connections" field of the WTI-API {@link TeamsController}
 * class (or more correctly, into the {@link MainController} class which is the superclass of TeamsController).
 * 
 * The effect is that the TeamsController thinks that it has received an earlier login request from a team 
 * with a specified "team key", and that this login must have also successfully connected and logged in to a PC2 
 * server and obtained the PC2 {@link ServerConnection} in the map.  
 * (In other words, whenever the TeamsController looks into its table of known PC2 server connections,
 * it will find a "valid" connection and will use this connection to obtain information about the PC2 contest to
 * which the team has "connected".
 * 
 * This allows JUnits to test various TeamsController functions without requiring a running PC2 server;
 * any references to the PC2 server connection by the TeamsController are instead intercepted and handled by
 * a "Mock" PC2 server connection.
 * 
 * In order to use this class, JUnits must use Mockito to set up appropriate return values from the various
 * PC2 API server connection modules being accessed by the TeamsController as part of the JUnit test.
 * (See examples in the various existing WTI controllers test package.)
 * 
 * @author EWU WebTeamClient project team, with updates by John Clevenger, PC2 Development Team (pc2.ecs.csus.edu)
 *
 */

public class TeamsControllerInjection {
	
	protected final String testKey = "xxx-xxx-xxxxxxxxx";
	protected TeamsController controller;
	
	protected ServerConnection connection = Mockito.mock(ServerConnection.class, Mockito.RETURNS_DEEP_STUBS);
	protected ILanguage mockedLanguage = Mockito.mock(ILanguage.class);
	protected IProblem mockedProblem = Mockito.mock(IProblem.class);
	protected IContest mockedContest = Mockito.mock(IContest.class);
	protected LoginResponseModel mockedResponse = Mockito.mock(LoginResponseModel.class);
	
	public void teamsControllerInjection() throws Exception {
		newConnectionsValue();
	}
	
	 private void newConnectionsValue() throws Exception {
		HashMap<String, ServerConnection> connections = new HashMap<String, ServerConnection>(); 
		
		connections.put(this.testKey, this.connection);

		setStaticField(TeamsController.class.getSuperclass().getDeclaredField("connections"), connections);
	}

	private static void setStaticField(Field field, Object newValues) throws Exception {
		field.setAccessible(true);
		field.set(null, newValues);
	}
	
	

}
