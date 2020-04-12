package controllers;

import java.net.URISyntaxException;
import java.util.*;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.csus.ecs.pc2.api.IClarification;
import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.IJudgement;
import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;
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

	public ContestController() throws URISyntaxException {
		super();
	}

	/***
	 *  Languages gets all the languages in the PC^2 contest.
	 *  
	 * @param languages[] is a ArrayList of type LanguageModel containing language options for the contest
	 * @return Response of 401 (unauthorized) if user's credentials are incorrect or 200 if languages were successfully returned
	 * 
	 * 
	 * @param key is a String that provides username
	 * @return Response of either a unauthorized if user is not found or a 200 if ok and languages list returned.
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
			logger.logError(e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ServerErrorResponseModel(Response.Status.INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR"))
					.type(MediaType.APPLICATION_JSON).build();
			
		}
		
		return Response.ok()
				.entity(languages)
				.type(MediaType.APPLICATION_JSON).build();
	}

	/**
	 * Judgements gets all the possible judgments in the PC^2 contest.
	 * 
	 * @param judgements[] is a ArrayList of type JudgementModel containing judgement options for the contest
	 * @return Response of either a unauthorized if users credentials are incorrect or a 200 if languages were successfully returned
	 * 
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
		catch(NullPointerException | NotLoggedInException e) {
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
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
	 * @param key is a String that identifies the user (team)
	 * 
	 * @return Response either 401 (unauthorized) if either the user (team) is not logged in or the contest is not running; 
	 * 				otherwise a 200 (OK) and a list of problems is returned.
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
		catch(NullPointerException | NotLoggedInException e) {
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
					.type(MediaType.APPLICATION_JSON).build();
		}
		return Response.ok()
				.entity(problems)
				.type(MediaType.APPLICATION_JSON).build();
	}
	
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
	 * @param key a String that identifies the user (team)
	 * 
	 * @return Response either 401 (unauthorized) if either the user (team) is not logged in or the contest is not running; 
	 * 				otherwise a 200 (OK) and a list of clarifications is returned.
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
		catch(NullPointerException | NotLoggedInException e) {

			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
					.type(MediaType.APPLICATION_JSON).build();
		}
		return Response.ok()
				.entity(clarifications)
				.type(MediaType.APPLICATION_JSON).build();
	}

}
