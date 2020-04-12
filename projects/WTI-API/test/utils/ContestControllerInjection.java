package utils;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.mockito.Mockito;

import controllers.ContestController;
import controllers.MainController;
import edu.csus.ecs.pc2.api.IClarification;
import edu.csus.ecs.pc2.api.IGroup;
import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.IJudgement;
import edu.csus.ecs.pc2.api.ITeam;
import edu.csus.ecs.pc2.api.ServerConnection;

/**
 * This class constructs a "PC2 server connections" HashMap mapping a predetermined team "key" to a "mock" 
 * PC2 {@link ServerConnection} object and injects the HashMap into the static "connections" field of the WTI-API {@link ContestController}
 * class (or more correctly, into the {@link MainController} class which is the superclass of ContestController).
 * 
 * The effect is that the ContestController thinks that it has received an earlier login request from a team 
 * with a specified "team key", and that this login must have also successfully connected and logged in to a PC2 
 * server and obtained the PC2 {@link ServerConnection} in the map.  
 * (In other words, whenever the ContestController looks into its table of known PC2 server connections,
 * it will find a "valid" connection and will use this connection to obtain information about the PC2 contest to
 * which the team has "connected".
 * 
 * This allows JUnits to test various ContestController functions without requiring a running PC2 server;
 * any references to the PC2 server connection by the ContestController are instead intercepted and handled by
 * a "Mock" PC2 server connection.
 * 
 * In order to use this class, JUnits must use Mockito to set up appropriate return values from the various
 * PC2 API server connection modules being accessed by the ContestController as part of the JUnit test.
 * (See examples in the various existing WTI controllers test package.)
 * 
 * @author EWU WebTeamClient project team, with updates by John Clevenger, PC2 Development Team (pc2.ecs.csus.edu)
 *
 */
public class ContestControllerInjection {
	
	//the dummy "contest team login key" to be used for Mock accesses
	protected final String testKey = "xxx-xxx-xxxxxxxxx";
	
	//the mock PC2 ServerConnection object to be injected into the ContestController
	protected ServerConnection connection = Mockito.mock(ServerConnection.class, Mockito.RETURNS_DEEP_STUBS);
	
	//mock classes comprising the contents of the mock PC2 ServerConnection's contest (these are configured
	// as appropriate by the "JUnit Test" classes which extend this class; this isn't a very good programming style...)
	protected ILanguage mockedLanguage = Mockito.mock(ILanguage.class);
	protected IProblem mockedProblem = Mockito.mock(IProblem.class);
	protected IClarification mockedClarification = Mockito.mock(IClarification.class);
	protected IJudgement mockedJudgement = Mockito.mock(IJudgement.class);
	protected ITeam mockedTeam = Mockito.mock(ITeam.class);
	protected IGroup mockedGroup = Mockito.mock(IGroup.class);
	
	
	/**
	 * Injects a "PC2 server connections" HashMap into the ContestController class.
	 * The injected HashMap maps the team key "xxx-xxx-xxxxxxxxx" to a Mock PC2 {@link ServerConnection}.
	 * 
	 * @throws Exception
	 */
	public void contestControllerInjection() throws Exception {
		newConnectionsValue();
	}
	
	/**
	 * Constructs a HashTable containing a mapping from the above-defined "testKey" to the
	 * above-defined "connection" object, and injects the HashMap into the WTI ContestController.
	 * @throws Exception
	 */
	 private void newConnectionsValue() throws Exception {
		HashMap<String, ServerConnection> connections = new HashMap<String, ServerConnection>(); 
		
		connections.put(this.testKey, this.connection);

		setStaticField(ContestController.class.getSuperclass().getDeclaredField("connections"), connections);
	}

	 /**
	  * Does the actual injection of the specified Object into the specified static {@link Field}. 
	  * @param field a static field into which an Object is to be injected
	  * @param obj the Object to be injected
	  * @throws Exception
	  */
	private static void setStaticField(Field field, Object obj) throws Exception {
		field.setAccessible(true);
		field.set(null, obj);
	}
}
