package controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import communication.WTIWebsocket;
import config.ServerInit;
import edu.csus.ecs.pc2.api.IClarification;
import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IJudgement;
import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import models.ClarificationModel;
import models.LanguageModel;
import models.ProblemModel;
import models.ServerErrorResponseModel;
import services.ScoreboardChangeListener;

/**
 * This class acts as the MVC "Controller" for all accesses to the WTI endpoints related to obtaining information about
 * a specific contest -- that is, it is the managing Resource for all REST accesses to the "/contest" WTI API endpoint.  
 * 
 * The {@link MainController} parent class maintains a HashTable mapping currently logged-in teams
 * to corresponding PC2 {@link ServerConnection} objects; when any reference to a sub-resource (such as "/contest/languages",
 * "/contest/problems", etc.) is made, the sub-resource checks the connection hashtable for an entry for the requesting team;
 * if found, the corresponding PC2 ServerConnection is invoked to obtain the requesting information.
 * 
 * The class is marked as @Singleton to insure that the Jetty webserver creates just one instance of the class to service
 * all /contest requests (rather than having a new instance created for each incoming request).
 * 
 * @author EWU WebTeamClient project team, with updates by John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 *
 */
@Path("/contest")
@Singleton
@Api(value = "contest", authorizations = {
		@Authorization(value="sampleoauth", scopes = {})
})
public class ContestController extends MainController {
	
	private final static String DEFAULT_PC2_SCOREBOARD_ACCOUNT = "scoreboard2";
	private final static String DEFAULT_PC2_SCOREBOARD_PASSWORD = "scoreboard2";
	
	//this boolean is reset false whenever a standings-changing event is received by the WTI scoreboard ServerConnection;
	// it is updated to true whenever an access to the /scoreboard REST API endpoint causes the current standings to be updated
	private boolean wtiServerStandingsAreCurrent = false;
			
	//the current ("cached") copy of the scoreboard standings, converted to JSON from XML returned by {@link DefaultScoringAlgorithm#getStandings()}
	private String currentJSONStandings ;
	
	// Static vars -- must be initialized as soon as the class is loaded so that Jetty startup fails if
	// PC2 Scoreboard login fails
	
	//the ServerConnection used by the ContestController to connect to the PC2 server with a scoreboard account
	private static ServerConnection scoreboardServerConn ;
	
	//the DSA to be used by the /scoreboard endpoint for updating standings when requested
	private static DefaultScoringAlgorithm dsa;

	//static init block, used to force PC2 scoreboard login before Jetty startup finishes
	static {
		
		logger.fine("Initializing Contest Controller static block");
		
		//create a scoreboard account connection to the PC2 server
		scoreboardServerConn = new ServerConnection();
	
		//get the credentials to be used to login to the PC2 server, either those given in the WTI pc2v9.ini file or the defaults defined above
		String sbAccount = ini.getScoreboardAccount();
		if (sbAccount==null || sbAccount.equals("")) {
			sbAccount = DEFAULT_PC2_SCOREBOARD_ACCOUNT;
		}
		String sbPassword = ini.getScoreboardPassword();
		if (sbPassword==null || sbPassword.equals("")) {
			sbPassword = DEFAULT_PC2_SCOREBOARD_PASSWORD;
		}
		
		//login to the PC2 server
		try {
			scoreboardServerConn.login(sbAccount, sbPassword);
		} catch (LoginFailureException e) {
			logger.info("WTI Login failed for scoreboard account " + sbAccount + ": " + e.getMessage());
			throw new RuntimeException("WTI login failed for PC2 scoreboard account '" + sbAccount + "'" + e.getMessage()) ;
		} 
		
		//create a DefaultScoringAlgorithm to be used for computing standings
		logger.fine("Constructing DSA for Contest Controller");
		dsa = new DefaultScoringAlgorithm() ;
		
		//insure that team clients only see FROZEN results when in a scoreboard freeze period
		dsa.setObeyFreeze(true);

	}
	
	/**
	 * Constructs a ContestController for the WTI server. Construction includes
	 * invoking the super-class {@link MainController}, constructor, which has the
	 * following effects:
	 * 
	 * <pre>
	 * <ol>
	 *   <li>create an (initially empty) {@link HashMap} mapping team keys to {@link ServerConnection}s.
	 *   <li>save the {@link ServerInit} object containing the WTI server initialization values loaded from the WTI pc2v9.ini file.
	 *   <li>create a new {@link WTIWebsocket} to be used for communicating with team clients.
	 * </ol>
	 * <p> The objects created by the superclass constructor are all "protected", making them accessible to this ContestController
	 * subclass.
	 * </pre>
	 * 
	 * In addition, this constructor creates a {@link ServerConnection} to the PC2 server (which must be running), and it logs into
	 * the PC2 server using the credentials specified in the WTI 
	 * 
	 * @throws URISyntaxException    if a valid websocket could not be constructed in the {@link MainController} super-class from
	 *                               the initialization values specified in the WTI pc2v9.ini file.
	 * @throws LoginFailureException if the ContestController could not login to the PC2 server using the credentials specified in
	 *                               the WTI pc2v9.ini file.
	 * @throws NotLoggedInException if the ContestController initialization block somehow successfully logged in but a subsequent 
	 * 								check returns "not logged in".
	 */
	public ContestController() throws URISyntaxException, LoginFailureException, NotLoggedInException {
		super();
		
		//add to the Scoreboard ServerConnection's contest a listener for each type of event which can change standings
		try {
			ScoreboardChangeListener sbListener = new ScoreboardChangeListener(this);
			scoreboardServerConn.getContest().addRunListener(sbListener);
			scoreboardServerConn.getContest().addContestConfigurationUpdateListener(sbListener);
		} catch (NotLoggedInException e) {
			//Cannot get here (theoretically) since a login failure should already have thrown a RuntimeException in the above static block.
			//Theoretically.
			logger.severe("Scoreboard not logged in to PC2 server in ContestController constructor -- should not be possible! " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	/***
	 *  Languages gets all the languages in the PC^2 contest.
	 *  
	 * @param key a String containing a key which uniquely identifies the team making the request.  
	 * 				The value of "key" is obtained from the HTTP header parameter "team_id".
	 * 
	 * @return Response of:
	 *				401 (unauthorized) if team's credentials are incorrect (i.e. the team is not logged in);
	 * 				500 (INTERNAL_SERVER_ERROR) if an error occurs in fetching the requested data from the PC2 server;
	 * 				otherwise 200 (OK) and a JSON string containing an array of {@link ILanguage}s is returned.
	 */	
	@Path("/languages")
	@GET
	@ApiOperation(value = "languages",
	notes = "Gets languages used within PC^2 contest.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Returns list of languages.", response = LanguageModel.class), 
		@ApiResponse(code = 401, message = "Returned if invalid credentials are supplied", response = ServerErrorResponseModel.class),
		@ApiResponse(code = 500, message = "Return if server is having trouble handling request", response = ServerErrorResponseModel.class)
	})
	public Response languages(
			@ApiParam(value="token used by logged in users to access teams information", required = true) @HeaderParam("team_id")String key) {

		ServerConnection userInformation = connections.get(key);

		// make sure we have connection information for this user (i.e. that the user is logged in)
		if (userInformation == null) {
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
					.type(MediaType.APPLICATION_JSON).build();
		}

		//the list of languages to be returned
		List<LanguageModel> languages = new ArrayList<LanguageModel>();
		
		try {
			ILanguage[] langs = userInformation.getContest().getLanguages();
			for(ILanguage lang : langs)
				languages.add(new LanguageModel(lang.getName(),lang.getTitle()));

		}
		catch(NotLoggedInException e) {
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
					.type(MediaType.APPLICATION_JSON).build();
		}
		catch(NullPointerException e) {
			logger.severe(e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ServerErrorResponseModel(Response.Status.INTERNAL_SERVER_ERROR, "NullPointerException in ContestController.languages()"))
					.type(MediaType.APPLICATION_JSON).build();
		}
		
		return Response.ok()
				.entity(languages)
				.type(MediaType.APPLICATION_JSON).build();
	}

	/**
	 * Judgements gets all the possible judgments in the PC^2 contest -- that is, a list containing every judgement which could be assigned to a submission.
	 * 
	 * @param key a String containing a key which uniquely identifies the team making the request.  
	 * 				The value of "key" is obtained from the HTTP header parameter "team_id".
	 * 
	 * @return Response of:
	 *				401 (unauthorized) if team's credentials are incorrect (i.e. the team is not logged in);
	 * 				500 (INTERNAL_SERVER_ERROR) if an error occurs in fetching the requested data from the PC2 server;
	 * 				otherwise 200 (OK) and a JSON string containing an array of {@link IJudgement}s is returned.
	 */		
	@Path("/judgements")
	@GET
	@ApiOperation(value = "judgements",
	notes = "Gets judgements used within PC^2 contest.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Returns list of judgements.", response = String.class), 
		@ApiResponse(code = 401, message = "Returned if invalid credentials are supplied", response = ServerErrorResponseModel.class)
	})
	public Response judgements(
			@ApiParam(value="token used by logged in users to access teams information", required = true) @HeaderParam("team_id")String key) {

		ServerConnection userInformation = connections.get(key);

		// make sure we have connection information for this user (i.e. that the user is logged in)
		if (userInformation == null) {
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
					.type(MediaType.APPLICATION_JSON).build();
		}

		//list of judgement types to be returned
		List<String> judgements = new ArrayList<String>();

		try {
			IJudgement[] judgs = userInformation.getContest().getJudgements();
			for(IJudgement judgement : judgs)
				judgements.add(judgement.getName());
		}
		catch(NotLoggedInException e) {
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
					.type(MediaType.APPLICATION_JSON).build();
		}
		catch(NullPointerException e) {
			logger.severe(e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ServerErrorResponseModel(Response.Status.INTERNAL_SERVER_ERROR, "NullPointerException in ContestController.judgements()"))
					.type(MediaType.APPLICATION_JSON).build();
		}
		
		return Response.ok()
				.entity(judgements)
				.type(MediaType.APPLICATION_JSON).build();
	}

	/***
	 *  This method returns a list of all the problems in the PC^2 contest.  It first checks to see that the 
	 *  user (team) specified by the received "key" is currently logged in and that the contest is running
	 *  (contest problems are only allowed to be viewed when the contest is running). If so, the method
	 *  returns the current contest problems, obtained from the PC2 {@link ServerConnection} for the team.
	 *  
	 *  Note that only NON-HIDDEN problems are returned.
	 * 
	 * @param key a String containing a key which uniquely identifies the team making the request.  
	 * 				The value of "key" is obtained from the HTTP header parameter "team_id".
	 * 
	 * @return Response of:
	 *				401 (unauthorized) if team's credentials are incorrect (i.e. the team is not logged in or is not allowed to make such a request);
	 * 				500 (INTERNAL_SERVER_ERROR) if an error occurs in fetching the requested data from the PC2 server;
	 * 				otherwise 200 (OK) and a JSON string containing an array of {@link IProblem}s is returned.
	 */
	@Path("/problems")
	@GET
	@ApiOperation(value = "problems",
	notes = "Gets problems used within PC^2 contest.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Returns list of problems.", response = ProblemModel.class), 
		@ApiResponse(code = 401, message = "Returned if invalid credentials are supplied or the contest is not running", response = ServerErrorResponseModel.class)
	})
	public Response problems(
			@ApiParam(value="token used by logged in users to access teams information", required = true) @HeaderParam("team_id")String key) {

		//get the PC2 server connection information for the team
		ServerConnection userInformation = connections.get(key);
		
		//verify the user is logged in and the contest is running
		try {
			// make sure we have connection information for this user (i.e. that the user is logged in)
			if (userInformation == null) {
				throw new NotLoggedInException();
			} else {
				// make sure that the contest is running (problems are not allowed to be seen when the contest is not running)
				if (!userInformation.getContest().isContestClockRunning()) {
					return Response.status(Response.Status.UNAUTHORIZED).entity(
							new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
							.type(MediaType.APPLICATION_JSON).build();
				}
			}
		} catch (NotLoggedInException e1) {
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
					.type(MediaType.APPLICATION_JSON).build();
		}

		//a list of the problems in the contest
		List<ProblemModel> problems = new ArrayList<ProblemModel>();

		try {
			IProblem[] probs = userInformation.getContest().getProblems(); //gets only the NON-HIDDEN problems
			for(IProblem prob : probs)
				problems.add(new ProblemModel(prob.getName(),prob.getShortName()));
		}
		catch(NotLoggedInException e) {
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
					.type(MediaType.APPLICATION_JSON).build();
		}
		catch(NullPointerException e) {
			logger.severe(e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ServerErrorResponseModel(Response.Status.INTERNAL_SERVER_ERROR, "NullPointerException in ContestController.problems()"))
					.type(MediaType.APPLICATION_JSON).build();
		}		
		
		return Response.ok()
				.entity(problems)
				.type(MediaType.APPLICATION_JSON).build();
	}
	
	/***
	 *  This method returns an indication of whether the contest clock is currently running.
	 * 
	 * @param key a String containing a key which uniquely identifies the team making the request.  
	 * 				The value of "key" is obtained from the HTTP header parameter "team_id".
	 * 
	 * @return Response of:
	 *				401 (unauthorized) if team's credentials are incorrect (i.e. the team is not logged in or is not allowed to make such a request);
	 * 				500 (INTERNAL_SERVER_ERROR) if an error occurs in fetching the requested data from the PC2 server;
	 * 				otherwise 200 (OK) and a JSON string containing a boolean value indicating whether (true) or not (false) the contest clock is running is returned.
	 */
	@Path("/isRunning")
	@GET
	@ApiOperation(value = "isRunning",
	notes = "Get the state of the contest if it is running of not")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Returns a boolean true or false", response = Boolean.class), 
		@ApiResponse(code = 401, message = "Returned if invalid credentials are supplied", response = ServerErrorResponseModel.class)
	})
	public Response isRunning(
			@ApiParam(value="token used by logged in users to access teams information", required = true) @HeaderParam("team_id")String key) {
		ServerConnection userInformation = connections.get(key);
		boolean isRunning = false;

		if(userInformation == null)
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
					.type(MediaType.APPLICATION_JSON).build();
		
		try {
			
			isRunning = userInformation.getContest().getContestClock().isContestClockRunning();
		}
		catch(NotLoggedInException e) {
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
					.type(MediaType.APPLICATION_JSON).build();
		}catch(Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ServerErrorResponseModel(Response.Status.INTERNAL_SERVER_ERROR, "Unauthorized user request"))
					.type(MediaType.APPLICATION_JSON).build();
		}
		
		return Response.ok()
				.entity(isRunning)
				.type(MediaType.APPLICATION_JSON).build();
	}

	/***
	 *  This method returns a list of all the clarifications in the PC^2 contest submitted by the specified team.  
	 *  It first checks to see that the user (team) specified by the received "key" is currently logged in and that the 
	 *  contest is running (contest clarifications are only allowed to be viewed when the contest is running). If so, the method
	 *  returns the current team's clarifications, obtained from the PC2 {@link ServerConnection} for the team.
	 * 
	 * @param key a String containing a key which uniquely identifies the team making the request.  
	 * 				The value of "key" is obtained from the HTTP header parameter "team_id".
	 * 
	 * @return Response of:
	 *				401 (unauthorized) if team's credentials are incorrect (i.e. the team is not logged in or is not allowed to make such a request);
	 * 				500 (INTERNAL_SERVER_ERROR) if an error occurs in fetching the requested data from the PC2 server;
	 * 				otherwise 200 (OK) and a JSON string containing a list of {@link IClarification}s is returned.
	 */
	@Path("/clarifications")
	@GET
	@ApiOperation(value = "clarifications",
	notes = "Gets all clarifications submitted in PC^2 contest.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Returns list of clarifications.", response = ClarificationModel.class), 
		@ApiResponse(code = 401, message = "Returned if invalid credentials are supplied", response = ServerErrorResponseModel.class)
	})

	public Response clarifications(@ApiParam(value="token used by logged in users to access teams information", 
	required = true) @HeaderParam("team_id")String key) {

		ServerConnection userInformation = connections.get(key);

		//verify the user is logged in and the contest is running
		try {
			// make sure we have connection information for this user (i.e. that the user is logged in)
			if (userInformation == null) {
				throw new NotLoggedInException();
			} else {
				// make sure that the contest is running (problems are not allowed to be seen when the contest is not running)
				if (!userInformation.getContest().isContestClockRunning()) {
					return Response.status(Response.Status.UNAUTHORIZED).entity(
							new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
							.type(MediaType.APPLICATION_JSON).build();
				}
			}
		} catch (NotLoggedInException e1) {
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
					.type(MediaType.APPLICATION_JSON).build();
		}

		//a list of the contest clarifications to be returned
		List<ClarificationModel> clarifications = new ArrayList<ClarificationModel>();
		
		try {
			//get ALL clarifications in the contest
			IClarification[] clars = userInformation.getContest().getClarifications();
			
			//check all contest clars to see which should be returned to the team
			for(IClarification clar : clars) {
				
				boolean isJudge =  clar.getTeam().getType() == IClient.ClientType.JUDGE_CLIENT;
								
				//return to the team only those clars that came from the team, or from the judges
				if(clar.getTeam().getLoginName().equalsIgnoreCase(userInformation.getMyClient().getLoginName()) || isJudge || clar.isSendToAll()) {
					
					String displayName = (isJudge || clar.isSendToAll()) ? "All" : clar.getTeam().getDisplayName();
					clarifications.add(new ClarificationModel(
						displayName, 
						clar.getProblem().getName(), 
						clar.getQuestion(), 
						clar.getAnswer(),
						String.format("%s-%s", clar.getSiteNumber(), clar.getNumber()),
						clar.getSubmissionTime(), 
						clar.isAnswered()));
				}
			}

		}
		catch(NotLoggedInException e) {
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
					.type(MediaType.APPLICATION_JSON).build();
		}
		catch(NullPointerException e) {
			logger.severe(e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ServerErrorResponseModel(Response.Status.INTERNAL_SERVER_ERROR, "NullPointerException in ContestController.clarifications()"))
					.type(MediaType.APPLICATION_JSON).build();
		}		
		
		return Response.ok()
				.entity(clarifications)
				.type(MediaType.APPLICATION_JSON).build();
	}
	
	/***
	 *  This method returns the current contest scoreboard standings as a JSON string.
	 *  
	 *  The returned JSON string is constructed by first making a call to 
	 *  {@link DefaultScoringAlgorithm#getStandings(edu.csus.ecs.pc2.core.model.IInternalContest, Properties, edu.csus.ecs.pc2.core.log.Log)},
	 *  which returns an XML document whose format is documented at 
	 *  <a href="https://github.com/pc2ccs/pc2v9/wiki/Scoreboard-HTML-Configuration#xml-standings-format">this URL</a>.
	 *  This XML string is then converted to JSON before being returned to the caller.
	 *  
	 *  The ContestController <I>caches</i> the scoreboard standings JSON each time they are updated; if standings have not changed
	 *  since the last call to this endpoint then the cached copy is returned rather than making a new call to {@link DefaultScoringAlgorithm}.
	 * 
	 * @param key a String containing a key which uniquely identifies the team making the request.  
	 * 				The value of "key" is obtained from the HTTP header parameter "team_id".
	 *
	 * @return Response of:
	 *				401 (unauthorized) if team's credentials are incorrect (i.e. the team is not logged in or is not allowed to make such a request);
	 * 				500 (INTERNAL_SERVER_ERROR) if an error occurs either within this ContestController or in fetching the requested data from the PC2 server;
	 * 				otherwise 200 (OK) and a JSON string containing {@link edu.csus.ecs.pc2.core.scoring.Standings}s is returned.
	 */	
	@Path("/scoreboard")
	@GET
	@ApiOperation(value = "scoreboard",
	notes = "Gets current scoreboard standings in the contest.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Returns XML standings.", response = String.class), 
		@ApiResponse(code = 401, message = "Returned if invalid credentials are supplied", response = ServerErrorResponseModel.class),
		@ApiResponse(code = 500, message = "Return if server is having trouble handling request", response = ServerErrorResponseModel.class)
	})
	public Response getStandings(
			@ApiParam(value="token used by logged in users to access team information", required = true) @HeaderParam("team_id")String key) {

		logger.fine("ContestController.getStandings(): looking up team login connection");
		
		ServerConnection userInformation = connections.get(key);

		// make sure we have connection information for this user (i.e. that the user is logged in)
		if (userInformation == null) {
			logger.fine("ContestController failed to find team login connection");
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
					.type(MediaType.APPLICATION_JSON).build();
		}

		//user is logged in; make sure we have a DSA to use
		if (dsa==null) {
			logger.severe("No DefaultScoringAlgorithm instance available");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ServerErrorResponseModel(Response.Status.INTERNAL_SERVER_ERROR, "DefaultScoringAlgorithm in null in ContestController"))
					.type(MediaType.APPLICATION_JSON).build();
		}
		
		//check if some event has occurred which could have changed the standings
		if (!wtiServerStandingsAreCurrent) {
			
			logger.info("Standings are not current; invoking DSA to update");
			
			//yes, standings could have changed;  try to get the actual InternalContest so we can use it to get updated standings
			IInternalContest internalContest = null;
			try {
				internalContest = scoreboardServerConn.getContest().getInternalContest();
			} catch (NotLoggedInException e) {
				return Response.status(Response.Status.UNAUTHORIZED)
						.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
						.type(MediaType.APPLICATION_JSON).build();
			}
			
			if (internalContest==null) {
				logger.severe("No InternalContest instance available for use with DefaultScoringAlgorithm");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(new ServerErrorResponseModel(Response.Status.INTERNAL_SERVER_ERROR, "InternalContest is null in ScoreboardServerConnection"))
						.type(MediaType.APPLICATION_JSON).build();
			}
			
			//we got the internal contest; pass it to the DefaultScoringAlgorithm and get back updated standings
			try {
				String xmlStandings = dsa.getStandings(internalContest, null, logger);
//					logger.fine("Got the following XML from DSA:");
//					logger.fine(xmlStandings);
					logger.info("Converting DSA XML to JSON");
				currentJSONStandings = this.getJSONStandings(xmlStandings);
					logger.fine("Got the following JSON standings:");
					logger.fine(currentJSONStandings);
			} catch (IllegalContestState e) {
				logger.throwing(dsa.getClass().getName(), "getStandings()", e);
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(new ServerErrorResponseModel(Response.Status.INTERNAL_SERVER_ERROR, "IllegalContestStateException in DefaultScoringAlgorithm"))
						.type(MediaType.APPLICATION_JSON).build();
			} 
			catch (JSONException e) {
				logger.throwing(this.getClass().getName(), "getJSONStandings()", e);
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(new ServerErrorResponseModel(Response.Status.INTERNAL_SERVER_ERROR, "IOException (JsonProcessingException?) in ContestController.getJSONStandings()"))
						.type(MediaType.APPLICATION_JSON).build();
			}
			catch (IOException e) {
				logger.throwing(this.getClass().getName(), "getJSONStandings()", e);
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(new ServerErrorResponseModel(Response.Status.INTERNAL_SERVER_ERROR, "IOException (JsonProcessingException?) in ContestController.getJSONStandings()"))
						.type(MediaType.APPLICATION_JSON).build();
			}
			wtiServerStandingsAreCurrent = true;
		}
			
		logger.info("Returning JSON standings in HTTP Response");
		logger.fine(currentJSONStandings);
		
		//standings are (now) current; return them to requestor
		return Response.ok()
				.entity(currentJSONStandings)
				.type(MediaType.APPLICATION_JSON).build();
	}

	
	/**
	 * Returns a JSON String containing the standings represented by the given XML Document String.
	 * 
	 * The JSON representation is created by first converting the given XML Document to a 
	 * {@link Standings} object, then invoking {@link Standings#toJSON()} on that object.
	 * 
	 * @param xmlStandings a String containing an XML Document with contest standings in the XML format produced 
	 * 					by {@link DefaultScoringAlgorithm#getStandings()}
	 * 
	 * @throws IOException if an error occurs in converting the Standings object to JSON form
	 * @throws JSONException 
	 * 
	 */
	private String getJSONStandings(String xmlStandings) throws IOException, JSONException {
		
		JSONObject jsonStandingsObject = XML.toJSONObject(xmlStandings);;
		
		return jsonStandingsObject.toString();

	}

	/**
	 * Returns the flag indicating whether the cached copy of the contest standings are current (true),
	 * or instead that some event has occured which potentially makes the standings out of date (false).
	 * 
	 * @return true if the cached contest standings are current; false if some event has occurred which means they MAY not be current.
	 */
	public boolean getWtiServerStandingsAreCurrent() {
		return wtiServerStandingsAreCurrent;
	}

	/**
	 * Sets the flag indicating that the cached copy of the contest standings should no longer be considered current.
	 * This method is intended to be called by listeners which listen for contest state changes which could cause standings
	 * to be invalid.
	 * 
	 * @param wtiServerStandingsAreCurrent the value to which the standings flag should be set.
	 */
	public void setWtiServerStandingsAreCurrent(boolean wtiServerStandingsAreCurrent) {
		this.wtiServerStandingsAreCurrent = wtiServerStandingsAreCurrent;
	}
	
}
