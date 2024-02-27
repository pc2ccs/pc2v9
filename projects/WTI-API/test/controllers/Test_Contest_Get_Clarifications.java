package controllers;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import edu.csus.ecs.pc2.api.IClarification;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;
import models.ClarificationModel;
import utils.ContestControllerInjection;

/**
 * Contains various tests to insure proper operation of {@link ContestController} methods which
 * fetch contest clarifications from the PC2 Contest.
 *
 * The setup() prior to running the JUnit tests injects into the ContestController a "mock" PC2 server
 * connection, so that the test can be run without actually having a PC2 server/contest running.
 *
 * Each test method is responsible for configuring the "mock contest" values located as protected fields in
 * in the {@link ContestControllerInjection} superclass (and therefore accessible by this class).
 * (This is a somewhat poor use of inheritance, but it does provide a convenient way give all JUnit tests
 * access to the mock PC2 server connection.)
 *
 * @author EWU WebTeamClient project team, with updates by John Clevenger, PC2 Development Team (pc2.ecs.csus.edu)
 *
 */

public class Test_Contest_Get_Clarifications extends ContestControllerInjection{

	private Response response;
	private ContestController controller;

	//a (mock) clarification
	private String teamName = "Team1";
	private String problem = "A. Dogzilla";
	private String question = "Is the third line of the sample input supposed to be 7?";
	private String answer = "No, it is supposed to be 9.";

	@Before
	public void setUp() throws Exception {

		//inject a Mock PC2 server connection into the ContestController
		this.contestControllerInjection();
		this.controller = new ContestController();
	}


	/**
	 * Test that failure to provide a valid (logged-in) team id when requesting clarifications returns
	 * "401" (Unauthorized).
	 */
	@Test
	public void Test_Get_Clarifications_401_Unauthorized_User(){

		this.response = this.controller.clarifications("inValidId");
		assertEquals(401, this.response.getStatus());
	}

	/**
	 * Test that under normal circumstances (logged in, contest properly configured and running), requesting clarifications
	 * returns "200" (OK).
	 *
	 * @throws Exception
	 */
	@Test
	public void Test_Get_Clarifications_200() throws Exception{

		Mockito.when(connection.getContest().getClarifications()).thenReturn(new IClarification[] {mockedClarification});
		Mockito.when(mockedClarification.getTeam()).thenReturn(mockedTeam);
		Mockito.when(mockedClarification.getTeam().getPrimaryGroup()).thenReturn(mockedGroup);
		Mockito.when(mockedClarification.getTeam().getPrimaryGroup().getName()).thenReturn(teamName);
		Mockito.when(mockedClarification.getTeam().getDisplayName()).thenReturn(teamName.toLowerCase());
		Mockito.when(mockedClarification.getTeam().getLoginName()).thenReturn(teamName.toLowerCase());
		Mockito.when(mockedClarification.getProblem()).thenReturn(mockedProblem);
		Mockito.when(mockedClarification.getProblem().getName()).thenReturn(problem);
		Mockito.when(mockedClarification.getQuestion()).thenReturn(question);
		Mockito.when(mockedClarification.getAnswer()).thenReturn(answer);
		Mockito.when(mockedClarification.getSubmissionTime()).thenReturn(12345678L);
		Mockito.when(mockedClarification.isAnswered()).thenReturn(true);
		Mockito.when(connection.getMyClient().getLoginName()).thenReturn("Team1");

		//make sure the mock says that the contest is running for this test
		Mockito.when(connection.getContest().isContestClockRunning()).thenReturn(true);

		this.response = this.controller.clarifications(testKey);
		assertEquals(200, this.response.getStatus());
	}

	/**
	 * Test that, while properly logged in and the contest is running, when requesting a list of the clarifications submitted by a team
	 * but there is a clarification that has no problem associated with it,
	 * "500" (INTERNAL_SERVER_ERROR) is returned -- because the server should never have accepted a
	 * clarification that doesn't have a valid problem associated with it.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void Test_Get_Clarifications_500_Clar_Has_No_Associated_Problem() throws Exception {

		Mockito.when(connection.getContest().getClarifications()).thenReturn(new IClarification[] {mockedClarification});

		Mockito.when(mockedClarification.getTeam()).thenReturn(mockedTeam);
		Mockito.when(mockedClarification.getTeam().getPrimaryGroup()).thenReturn(mockedGroup);
		Mockito.when(mockedClarification.getTeam().getPrimaryGroup().getName()).thenReturn(teamName.toLowerCase());
		Mockito.when(mockedClarification.getTeam().getDisplayName()).thenReturn(teamName.toLowerCase());
		Mockito.when(mockedClarification.getTeam().getLoginName()).thenReturn(teamName.toLowerCase());
		Mockito.when(mockedClarification.getProblem()).thenThrow(NullPointerException.class);

		Mockito.when(connection.getMyClient().getLoginName()).thenReturn(teamName);
		Mockito.when(connection.getContest().getProblems()).thenThrow(NullPointerException.class);

		//make sure the mock says that the contest is running for this test
		Mockito.when(connection.getContest().isContestClockRunning()).thenReturn(true);

		this.response = this.controller.clarifications(testKey);
		assertEquals(500, this.response.getStatus());
	}

	/**
	 * Test that an attempt to fetch clarifications when properly logged in but the contest is not running returns
	 * "401" (Unauthorized).
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void Test_Get_Clarifications_401_Contest_Not_Running() throws Exception{

		//define some (mock) clarifications, even though we shouldn't be able to access them
		Mockito.when(connection.getContest().getClarifications()).thenReturn(new IClarification[] {mockedClarification});

		Mockito.when(mockedClarification.getTeam()).thenReturn(mockedTeam);
		Mockito.when(mockedClarification.getTeam().getPrimaryGroup()).thenReturn(mockedGroup);
		Mockito.when(mockedClarification.getTeam().getPrimaryGroup().getName()).thenReturn("Team1");
		Mockito.when(mockedClarification.getTeam().getDisplayName()).thenReturn(teamName.toLowerCase());
		Mockito.when(mockedClarification.getTeam().getLoginName()).thenReturn(teamName.toLowerCase());

		Mockito.when(connection.getMyClient().getLoginName()).thenReturn(teamName);
		Mockito.when(connection.getContest().getProblems()).thenThrow(NotLoggedInException.class);

		//make sure the mock says that the contest is not running for this test
		Mockito.when(connection.getContest().isContestClockRunning()).thenReturn(false);

		this.response = this.controller.clarifications(testKey);
		assertEquals(401, this.response.getStatus());
	}

	/**
	 * Test that requesting the first clarification from Site 1 returns a clarification with is "1-1"
	 * and a response code of "200" (OK).
	 * @throws Exception
	 */
	@Test public void Test_Get_Clarifications_200_Id_Received() throws Exception{

		//return Mocked Clarification info when the server (via the userconnection) asks the contest for clarifications
		Mockito.when(connection.getContest().getClarifications()).thenReturn(new IClarification[] {mockedClarification});
		Mockito.when(mockedClarification.getQuestion()).thenReturn(question);
		Mockito.when(mockedClarification.getAnswer()).thenReturn(answer);
		Mockito.when(mockedClarification.getSubmissionTime()).thenReturn(12345678L);
		Mockito.when(mockedClarification.isAnswered()).thenReturn(true);
		Mockito.when(mockedClarification.getSiteNumber()).thenReturn(1);
		Mockito.when(mockedClarification.getNumber()).thenReturn(1);

		//return Mocked Team info when the server (via the userconnection) asks the Clarification for Team info
		Mockito.when(mockedClarification.getTeam()).thenReturn(mockedTeam);
		Mockito.when(mockedClarification.getTeam().getPrimaryGroup()).thenReturn(mockedGroup);
		Mockito.when(mockedClarification.getTeam().getPrimaryGroup().getName()).thenReturn(teamName);
		Mockito.when(mockedClarification.getTeam().getDisplayName()).thenReturn(teamName.toLowerCase());
		Mockito.when(mockedClarification.getTeam().getLoginName()).thenReturn(teamName.toLowerCase());

		//return Mocked Problem info when the server (via the userconnection) asks the Clarification for Problem info
		Mockito.when(mockedClarification.getProblem()).thenReturn(mockedProblem);
		Mockito.when(mockedClarification.getProblem().getName()).thenReturn(problem);

		Mockito.when(connection.getMyClient().getLoginName()).thenReturn(teamName);

		//make sure the mock says that the contest is running for this test
		Mockito.when(connection.getContest().isContestClockRunning()).thenReturn(true);

		this.response =  this.controller.clarifications(testKey);
		assertEquals(200, this.response.getStatus());

		@SuppressWarnings("unchecked")
		List<ClarificationModel> clars = (List<ClarificationModel>) this.response.getEntity();
		assertEquals("1-1", clars.get(0).id);

	}

	@After
	public void tearDown() throws Exception {
		if(response != null)
			this.response.close();
	}

}
