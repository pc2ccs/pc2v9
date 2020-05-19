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
import emptyObjs.EmptyLanguage;
import emptyObjs.EmptyProblem;
import io.swagger.annotations.Api;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import services.ClarificationService;
import services.ConfigurationService;
import services.RunsService;
import services.TestRunService;

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
	
	private ServerInit ini = ServerInit.createServerInit();
	protected static Logging logger;
	
	private final String websocketUrl = String.format("ws://localhost:%s%s/WTISocket", ini.getPortNum(), ini.getWsName());
	private static WTIWebsocket client;

	public MainController() throws URISyntaxException {

		client = new WTIWebsocket(new URI(String.format("%s/%s", this.websocketUrl, "server")));
		logger = Logging.getLogger();
	}

	protected IProblem findProblem(IProblem[] problems, String nameOfProblem) {
		for(IProblem prob : problems)
			if(prob.getName().equalsIgnoreCase(nameOfProblem))
				return prob;
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
		teamCon.addTestRunListener(new TestRunService(teamId, client));
		teamCon.addClarificationListener(new ClarificationService(teamId, client));
		teamCon.addContestConfigurationUpdateListener(new ConfigurationService(teamId, client));
	}
	
	protected ServerConnection createNewServerConnection() {
		return new ServerConnection();
	}

}

