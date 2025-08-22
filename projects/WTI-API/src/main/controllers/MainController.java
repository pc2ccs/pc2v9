package controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import communication.WTIWebsocket;
import config.Logging;
import config.ServerInit;
import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.api.implementation.Contest;
import edu.csus.ecs.pc2.core.log.Log;
import emptyObjs.EmptyLanguage;
import emptyObjs.EmptyProblem;
import io.swagger.annotations.Api;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import services.ClarificationService;
import services.ConfigurationService;
import services.DroppedConnectionListener;
import services.RunsService;

@SwaggerDefinition(
		info = @Info(
				title = "Web Team Interface",
				description = "Web Team Interface REST Api is used to help developers with interacting with PC2 system. Use this API to help developers utilize interfacing with the PC^2 interface. This page will document and describe each endpoint including http responses, calls, and return types.",
				version = "V1.0.0"
				),
		schemes = {SwaggerDefinition.Scheme.HTTP},
		tags = {@Tag(name = "teams", description = "Teams information for pc^2 contest. All information to teams and for teams will be found here."),
				@Tag(name = "contest", description = "All information for pc^2 contest. All information to contest and for contest will be found here.")}
		)
@Api
@Consumes("application/json")
@Produces("application/json")
public abstract class MainController {

	//a map from unique team ids (UUID's generated in TeamsController.login()) to PC2 Server connections for the team.
	//PC2 Server connections are inserted into this map by TeamsController.login(), and removed by TeamsController.logout().
	// *** There may be a memory leak issue here: what if a team drops without invoking logout?  This might be a problem if
	// this class were to be used in a contest running, say, over the Internet for days or weeks...
	protected static HashMap<String, ServerConnection> connections = new HashMap<String, ServerConnection>();
	
	//the following two fields are "static" because they need to be referenced by the static initialization block in ContestController
	protected static ServerInit ini = ServerInit.createServerInit();
	protected static Log logger = Logging.getLogger();
	
	private final String websocketUrl = String.format("ws://localhost:%s%s/WTISocket", ini.getPortNum(), ini.getWsName());
	protected static WTIWebsocket client;

	public MainController() throws URISyntaxException {

		client = new WTIWebsocket(new URI(String.format("%s/%s", this.websocketUrl, "server")));
	}

	/**
	 * Searches the given array of IProblems and returns the first problem whose name matches the specified 
	 * nameOfProblem, or returns an {@link EmptyProblem} object if there was no match.
	 * <P>
	 * Note that this method is only invoked by the {@link TeamsController} class, and only from TeamsController 
	 * methods submitClarification(), submitRun(), and submitTestRun().  This means that the received array
	 * contains only actual contest problems (in the case of being invoked by submitting a Run), or may
	 * also contain "Categories" (which are, somewhat illogically, defined as "class Category extends Problem") in
	 * the case of being invoked by submitting a Clarification request.
	 * <P>
	 * Note also that all three invocations of this method construct the IProblems[] array which they send
	 * to this method by invoking api.ServerConnection.getContest().getProblems() and/or
	 * api.ServerConnection.getContest().getClarificationCategories, both of which return an array of
	 * ProblemImplementation (NOT an array of Problems).  This works because both ProblemImplementation and
	 * Problem implement IProblem.  Thus, while the declared ("Apparent") type of the elements in the received
	 * array is "IProblem", the ACTUAL TYPE of the elements will in all cases be "ProblemImplementation".
	 * 
	 * @param problems an array of IProblems, which are actually ProblemImplementations.
	 * @param nameOfProblem a String giving the name of the desired problem.
	 * 
	 * @return the named problem if it is found in the input array; otherwise, an {@link EmptyProblem} object.
	 */
	protected IProblem findProblem(IProblem[] problems, String nameOfProblem) {
		//check if the named problem is one of the problems in the received array.
		for(IProblem prob : problems) {
			if(prob.getName().equalsIgnoreCase(nameOfProblem))
				return prob;
		}
		//we didn't find a problem (or category) whose name matches; return a dummy problem.
		return new EmptyProblem();
	}

	protected ILanguage findLanguage(ILanguage[] languages, String nameOfLang) {
		for(ILanguage lang : languages)
			if(lang.getName().equalsIgnoreCase(nameOfLang))
				return lang;
		return new EmptyLanguage();
	}
	
	protected void subscription(Contest teamCon, String teamId) {
		teamCon.addRunListener(new RunsService(teamId, client));
//		teamCon.addTestRunListener(new TestRunService(teamId, client));  //this was commented-out because it requires implementing Test Run support in PC2 API
		teamCon.addClarificationListener(new ClarificationService(teamId, client));
		teamCon.addContestConfigurationUpdateListener(new ConfigurationService(teamId, client));
		
		//listen for a connectionDropped so we can notify the UI that a forced logout has happened
        teamCon.addConnectionListener(new DroppedConnectionListener(teamId, client));
	}
	
	protected ServerConnection createNewServerConnection() {
		return new ServerConnection();
	}
}

