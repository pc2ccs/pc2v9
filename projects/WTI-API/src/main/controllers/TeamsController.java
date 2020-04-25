package controllers;

import java.net.URISyntaxException;
import java.util.*;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;

import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;
import edu.csus.ecs.pc2.core.model.IFile;
import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.PC2APIFile;
import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import models.File;
import models.LoginRequestModel;
import models.LoginResponseModel;
import models.ServerErrorResponseModel;
import models.SubmitClarificationRequestModel;
import models.LogoutResponseModel;
import models.RunModel;
import models.SubmitRunRequestModel;
import services.FileService;

@Path("/teams")
@Singleton
@Api(value = "teams", authorizations = {
		@Authorization(value="sampleoauth", scopes = {})
})
public class TeamsController extends MainController {

	public TeamsController() throws URISyntaxException {
		super();
	}

	/**
	 * Login logs users into pc2 system using the credentials provided by pc2 admin. 
	 * 	*	System takes request passing it off to pc2 system then stores the connection made into a cache (hashmap temp), 
	 * 	*	creates a UUID (as a key), 
	 *  *	sends information off as a response to client.
	 * 
	 * Client will receive a value that will need to be passed back to server for requests made.
	 * @param login is a LoginRequestModel that provides username and password
	 * @return Response of either a unauthorized if users credentials are incorrect or a 200 if users were successfully logged in
	 */
	@POST
	@Path("/login")
	@ApiOperation(value = "User login",
	notes = "Creates a new session to WTI API. Once successfully created, users have access to all of WTI featues available within system.",
	response = LoginRequestModel.class,  //should this be LoginResponseModel ??
	responseContainer = "List")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Returned if the login is valid", response = LoginResponseModel.class), 
		@ApiResponse(code = 401, message = "Returned if invalid credentials are supplied", response = ServerErrorResponseModel.class)
	})
	public Response login(LoginRequestModel login) {

		ServerConnection teamsConnection = this.createNewServerConnection();
		UUID key = UUID.randomUUID();

		try {
			teamsConnection.login(login.teamName,login.password);
			subscription(teamsConnection.getContest(), key.toString());
		} catch (LoginFailureException | NotLoggedInException e) {
			logger.logInfo("Login failed for team " + login.teamName + ": "+  e.getMessage());
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
					.type(MediaType.APPLICATION_JSON).build();
		} catch(Exception e) {
			logger.logError(String.format("TeamsController Login: ", e.getMessage()));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ServerErrorResponseModel(Response.Status.INTERNAL_SERVER_ERROR, "Server error"))
					.type(MediaType.APPLICATION_JSON)
					.build();
		}


		//put the team's connection to the PC2 server into the (static, class-wide) connections map under the team's "id"
		connections.put(key.toString(), teamsConnection);
		LoginResponseModel response = new LoginResponseModel(login.teamName, key.toString());
		
		logger.logInfo("Logged in: " + login.teamName);
		return Response.ok()
				.entity(response)
				.type(MediaType.APPLICATION_JSON).build();
	}

	/***
	 * Logout logs users out of the pc2 system.
	 * 
	 * @param key is a String that provides username
	 * @return Response of either a unauthorized if user is not found or a 200 if user successfully logged out
	 */
	@DELETE
	@Path("/logout")
	@ApiOperation(value = "User logout",
	notes = "Logouts a user that is currently logged into the system.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Returned if the logout is valid", response = LogoutResponseModel.class), 
		@ApiResponse(code = 401, message = "Returned if no team found to logout", response = ServerErrorResponseModel.class),
		@ApiResponse(code = 500, message = "Return if server is having trouble handling request", response = ServerErrorResponseModel.class)
	})
	public Response logout(@HeaderParam("team_id")String key) {

		ServerConnection teamsConnection = MainController.connections.get(key);

		String teamAccount = "";
		try {
			//pull team account from connection since we're not given it in the API request
			teamAccount = teamsConnection.getMyClient().getLoginName();
			
			//log the team out of the PC2 server
			teamsConnection.logoff();

		}catch(NullPointerException e) {
			logger.logInfo("Null pointer exception trying to logoff team " + teamAccount + " from PC2 Server");
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Null pointer exception"))
					.type(MediaType.APPLICATION_JSON).build();
		}catch(NotLoggedInException e) {
			logger.logWarning("NotLoggedIn exception trying to logoff team " + teamAccount + " from PC2 Server");
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request.."))
					.type(MediaType.APPLICATION_JSON).build();
		}catch(Exception e) {
			logger.logError(String.format(e.getMessage()));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ServerErrorResponseModel(Response.Status.INTERNAL_SERVER_ERROR, "Server error"))
					.type(MediaType.APPLICATION_JSON)
					.build();
		}

		//remove the team's PC2 server connection from the global hashmap
		MainController.connections.remove(key, teamsConnection);
		
		LogoutResponseModel response = new LogoutResponseModel(key.toString());
		
		logger.logInfo("Logged out team " + teamAccount);
		return Response.ok()
				.entity(response)
				.type(MediaType.APPLICATION_JSON).build();
	}

	/**
	 * The /run endpoint allows logged in users to submit programming problems to PC^2. 
	 * The endpoint only works if users have first logged into PC^2. 
	 * The received {@link SubmitRunRequestModel} is examined to determine whether the submission is a judged run or a test run,
	 * and the submission is passed to the appropriate PC^2 API method.  
	 * Note: support for Test Runs is currently not implemented.
	 * 
	 * @param team_id a unique key for the submitting team, obtained from the header SubmitRunRequestModel parameter
	 * @param run a SubmitRunRequestModel encapsulating the run to be submitted
	 * 
	 * @return Response of either an ok value, not authorized, or bad request
	 */
	@POST
	@Path("/run")
	@ApiOperation(value = "Run",
	notes = "Endpoint to allow logged in users to submit programming runs. Submissions can be passed via http",
	response = SubmitRunRequestModel.class,
	responseContainer = "List")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Returned if submission was successfully submitted to pc^2 api"), 
		@ApiResponse(code = 401, message = "Returned if invalid id was provided", response = ServerErrorResponseModel.class),
		@ApiResponse(code = 400, message = "Returned if information regarding languages, missing files, or problems were not found", response = ServerErrorResponseModel.class)
	})
	public Response submitRun(
		@ApiParam(value="token used by logged in users to access teams information", required = true) 
		@HeaderParam("team_id") String key, SubmitRunRequestModel run) {
		
		ServerConnection teamsConn = connections.get(key);

		if(teamsConn == null) {
			logger.logWarning("Cannot find team connection to PC2 Server");
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(Entity.json(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Bad Request")))
					.type(MediaType.APPLICATION_JSON)
					.build();
		}
		
		//get the team id from the PC2 connection, since we only have the UUID key in the API request
		String teamAcct = "";
		try {
			teamAcct = teamsConn.getMyClient().getLoginName();
		} catch (NotLoggedInException e) {
			logger.logInfo("NotLoggedIn exception trying to submit run for team " + teamAcct);
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
					.type(MediaType.APPLICATION_JSON).build();
		}

		if(!run.isTest()) {
			Response submitRunResponse = this.submitRun(teamsConn, run);
			logger.logInfo("Submitted judged run to PC2 server for team " +  teamAcct + ", response = " + submitRunResponse.getStatus());
			return submitRunResponse ;
		}
		else {
			Response submitTestRunResponse = this.submitTestRun(teamsConn, run);
			logger.logInfo("Submitted test run to PC2 server for team " +  teamAcct + ", response = " + submitTestRunResponse.getStatus());
			return submitTestRunResponse;
		}
	}

	/**
	 * Submit a Judged Run to the PC2 server.
	 * 
	 * @param teamsConn the team connection to the PC2 server
	 * @param run the run to be submitted
	 * @return a {@link javax.ws.rs.core.Response} containing the response to be returned to the submitting client
	 */
	private Response submitRun(ServerConnection teamsConn, SubmitRunRequestModel run) {

		try {
			ILanguage lang = this.findLanguage(teamsConn.getContest().getLanguages(), run.getLanguage());
			IProblem prob = this.findProblem(teamsConn.getContest().getProblems(), run.getProbName());

			IFile main = new PC2APIFile(run.getMainFile().getFileName(), run.getMainFile().getByteData());

			if(run.getExtraFiles() != null) {			
				IFile[] extraFiles = FileService.createFileArray(run.getExtraFiles());
				teamsConn.submitJudgeRun(prob, lang, main, extraFiles);
			}
			else{
				teamsConn.submitJudgeRun(prob, lang, main);
			}
		} 
		catch(NullPointerException e) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Entity.json(new ServerErrorResponseModel(Response.Status.BAD_REQUEST, "Server Not Running")))
					.type(MediaType.APPLICATION_JSON)
					.build();
		}
		catch(NotLoggedInException e) {
			return Response.status(Response.Status.UNAUTHORIZED)
					.type(MediaType.APPLICATION_JSON)
					.build();
		}
		catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Entity.json(new ServerErrorResponseModel(Response.Status.BAD_REQUEST, "Bad Request")))
					.type(MediaType.APPLICATION_JSON)
					.build();
		}

		return Response.ok().type(MediaType.APPLICATION_JSON).build();
	}

	/**
	 * Submit a Test Run to the PC2 server.  Note: this feature is not yet implemented on the PC2 server side.
	 * 
	 * @param teamsConn the team connection to the PC2 server
	 * @param run the test run to be submitted
	 * @return a {@link javax.ws.rs.core.Response} containing the response to be returned to the submitting client
	 */
	private Response submitTestRun(ServerConnection teamsConn, SubmitRunRequestModel run) {
		try {
			ILanguage lang = this.findLanguage(teamsConn.getContest().getLanguages(), run.getLanguage());
			IProblem prob = this.findProblem(teamsConn.getContest().getProblems(), run.getProbName());

			IFile main = new PC2APIFile(run.getMainFile().getFileName(), run.getMainFile().getByteData());
			IFile testFile = new PC2APIFile(run.getTestFile().getFileName(), run.getTestFile().getByteData());

			IFile[] extraFiles = (run.getExtraFiles() != null ) ? FileService.createFileArray(run.getExtraFiles()) : null;
			IFile[] atFiles = (run.getAdditionalTestFiles() != null ) ? FileService.createFileArray(run.getAdditionalTestFiles()) : null;

			if(extraFiles != null || atFiles != null) {
				teamsConn.submitTestRun(prob, lang, main, testFile, extraFiles, atFiles);		
			}
			else {
				teamsConn.submitTestRun(prob, lang, main, testFile);
			}
		} catch (NotLoggedInException e) {
			return Response.status(Response.Status.UNAUTHORIZED)
					.type(MediaType.APPLICATION_JSON)
					.build();

		} 
		catch(NullPointerException e) {

			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Entity.json(new ServerErrorResponseModel(Response.Status.BAD_REQUEST, "Bad Request")))
					.type(MediaType.APPLICATION_JSON)
					.build();
		}
		catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(Entity.json(new ServerErrorResponseModel(Response.Status.INTERNAL_SERVER_ERROR, "Internal Server Error")))
					.type(MediaType.APPLICATION_JSON)
					.build();
		}

		return Response.ok().type(MediaType.APPLICATION_JSON).build();
	}

	/***
	 *  Run gets all the judged and test runs submitted in the PC^2 contest.
	 * 
	 * @param key is a String that provides username
	 * @return Response of either a unauthorized if user is not found or a 200 if ok and clarifications list returned.
	 */
	@Path("/run")
	@GET
	@ApiOperation(value = "Run",
	notes = "Gets all runs submitted in PC^2 contest.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Returns list of runs.", response = RunModel.class), 
		@ApiResponse(code = 401, message = "Returned if invalid credentials are supplied", response = ServerErrorResponseModel.class),
		@ApiResponse(code = 400, message = "Returned if information regarding runs is missing", response = ServerErrorResponseModel.class),
		@ApiResponse(code = 500, message = "Returned if server is having problems handling request", response = ServerErrorResponseModel.class)
	})

	public Response getTeamRuns(
			@ApiParam(value="token used by logged in users to access teams information", required = true) 
			@HeaderParam("team_id")String key) {

		List<RunModel> runs = new ArrayList<RunModel>();
		List<File> result = new ArrayList<File>();

		ServerConnection teamsConn = connections.get(key);

		if(teamsConn == null) {
			logger.logWarning("Cannot find team connection to PC2 Server");
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
					.type(MediaType.APPLICATION_JSON).build();
		}

		String teamAccount = "";
		try {
			//pull team account from connection since we're not given it in the API request
			teamAccount = teamsConn.getMyClient().getLoginName();

			List<IRun> allRuns = Arrays.asList(teamsConn.getContest().getRuns());
			allRuns.addAll(Arrays.asList(teamsConn.getContest().getTestRuns()));

			for(IRun run : allRuns){	

				if(run.getTeam().getLoginName().equals(teamsConn.getMyClient().getLoginName())) {

					if(run.isTestRun()) {
						result = FileService.convertFilesToModel(run.getTestRunResults().getStderrFiles(), run.getTestRunResults().getStdoutFiles());
					}
					
					runs.add(new RunModel(
							run.getTeam().getLoginName(),
							run.getLanguage().getName(), 
							run.getProblem().getName(), 
							run.getJudgementName(),
							result,
							run.getSubmissionTime(), 
							run.isTestRun(),
							run.isPreliminaryJudged(),
							run.isFinalJudged(),
							String.format("%s-%s", run.getSiteNumber(), run.getNumber())));
				}
				
			}
		}
		catch(NotLoggedInException e) {
			logger.logInfo("NotLoggedIn exception trying to get runs from PC2 Server for team " + teamAccount );
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
		catch(Exception e) {
			logger.logError(e.getMessage() + e.getStackTrace());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ServerErrorResponseModel(Response.Status.INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR"))
					.type(MediaType.APPLICATION_JSON).build();
		}
		Response okResponse = Response.ok()
				.entity(runs)
				.type(MediaType.APPLICATION_JSON).build();
		logger.logInfo("Returning 'OK' for team " + teamAccount );
		return okResponse;
	}

	/**
	 * Submit clarification allows logged in users to submit clarifications to pc^2. Endpoint only works if users have first logged into pc^2. 
	 * 
	 * @param team_id that is accessed from the header parameter. SubmitClarificationRequestModel
	 * @return Response of either an ok value, not authorized, or bad request
	 */
	@POST
	@Path("/clarification")
	@ApiOperation(value = "Clarification",
	notes = "Endpoint to allow logged in users to submit clarifications. Submissions can be passed via http",
	response = SubmitClarificationRequestModel.class,
	responseContainer = "List")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Returned if clarification was successfully submitted to pc^2 api"), 
		@ApiResponse(code = 401, message = "Returned if invalid id was provided", response = ServerErrorResponseModel.class),
		@ApiResponse(code = 400, message = "Returned if information regarding problems was not found", response = ServerErrorResponseModel.class)
	})
	public Response submitClarification(
			@ApiParam(value="token used by logged in users to access teams information", required = true) @HeaderParam("team_id") String key, SubmitClarificationRequestModel clar) {
		ServerConnection teamsConn = connections.get(key);

		if(teamsConn == null) {
			logger.logWarning("Cannot find team connection to PC2 Server" );
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(Entity.json(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Bad Request")))
					.type(MediaType.APPLICATION_JSON)
					.build();
		}

		//get the team id from the PC2 connection, since we only have the UUID key in the API request
		String teamAcct = "";
		try {
			teamAcct = teamsConn.getMyClient().getLoginName();
		} catch (NotLoggedInException e) {
			logger.logInfo("NotLoggedIn exception trying to submit clarification for team " + teamAcct);
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
					.type(MediaType.APPLICATION_JSON).build();
		}
		Response submitClarResponse = this.submitClarification(teamsConn, clar);
		logger.logInfo("Submitted clarification to PC2 server for team " +  teamAcct + ", response = " + submitClarResponse.getStatus());
		return submitClarResponse;

	}

	private Response submitClarification(ServerConnection teamsConn, SubmitClarificationRequestModel clar) {

		try {
			IProblem prob = this.findProblem(teamsConn.getContest().getProblems(), clar.getProbName());
			String message = clar.getMessage();

			teamsConn.submitClarification(prob, message);;

		} 
		catch(NullPointerException e) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Entity.json(new ServerErrorResponseModel(Response.Status.BAD_REQUEST, "Server Not Running")))
					.type(MediaType.APPLICATION_JSON)
					.build();
		}
		catch(NotLoggedInException e) {
			return Response.status(Response.Status.UNAUTHORIZED)
					.type(MediaType.APPLICATION_JSON)
					.build();
		}
		catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Entity.json(new ServerErrorResponseModel(Response.Status.BAD_REQUEST, "Bad Request")))
					.type(MediaType.APPLICATION_JSON)
					.build();
		}

		return Response.ok().type(MediaType.APPLICATION_JSON).build();
	}
	
	
	/**
	 * Options will allow users to change settings in contest. Endpoint only works if admin has allowed for changes. 
	 * 
	 * Not currently implemented
	 * 
	 * @param team_id that is accessed from the header parameter. 
	 * @return Response of either an ok value, not authorized
	 */
	
	@Path("/options")
	@GET
	@ApiOperation(value = "options",
	notes = "Get whether users can change their passwords")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Returns a boolean true or false", response = Boolean.class), 
		@ApiResponse(code = 401, message = "Returned if invalid credentials are supplied", response = ServerErrorResponseModel.class)
	})
	public Response options(
			@ApiParam(value="token used by logged in users to access teams information", required = true) @HeaderParam("team_id")String key) {
		
		ServerConnection userInformation = connections.get(key);
		boolean canChangePW = false;
		
		if(userInformation == null)
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Unauthorized user request"))
					.type(MediaType.APPLICATION_JSON).build();
		
		
		return Response.ok()
				.entity(canChangePW)
				.type(MediaType.APPLICATION_JSON).build();
	}
	
	/**
	 * Options will allow users to change settings in contest. Endpoint only works if admin has allowed for changes. 
	 * 
	 * Not currently implemented
	 * 
	 * @param team_id that is accessed from the header parameter. 
	 * @return Response of either an ok value, not authorized, or bad request, currently should return not implemented
	 */
	
	@POST
	@Path("/options")
	@ApiOperation(value = "options",
			notes = "Allow Users to submit password change if allowed by admins")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Returns if password change was successfully sent"), 
		@ApiResponse(code = 401, message = "Returned if invalid credentials are supplied", response = ServerErrorResponseModel.class),
		@ApiResponse(code = 501, message = "Returned if unimplemented", response = ServerErrorResponseModel.class)
	})
	public Response changePassword(
			@ApiParam(value="token used by logged in users to access teams information", required = true) @HeaderParam("team_id") String key) {
		ServerConnection teamsConn = connections.get(key);

		if(teamsConn == null) {
			logger.logWarning("Cannot find team connection to PC2 Server" );
			return Response.status(Response.Status.UNAUTHORIZED)
					.entity(Entity.json(new ServerErrorResponseModel(Response.Status.UNAUTHORIZED, "Bad Request")))
					.type(MediaType.APPLICATION_JSON)
					.build();
		}

		logger.logInfo("Returning 'Not Implemented' to client");
		return Response.status(Response.Status.NOT_IMPLEMENTED)
				.entity(Entity.json(new ServerErrorResponseModel(Response.Status.NOT_IMPLEMENTED, "Not yet implemented")))
				.type(MediaType.APPLICATION_JSON)
				.build();
	}


}
