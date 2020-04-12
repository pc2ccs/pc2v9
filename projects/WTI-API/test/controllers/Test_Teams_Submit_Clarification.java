package controllers;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import edu.csus.ecs.pc2.api.IProblem;
import models.SubmitClarificationRequestModel;
import utils.TeamsControllerInjection;

public class Test_Teams_Submit_Clarification extends TeamsControllerInjection {
	
	WebTarget teamsEndpoint;
	Response response;
	TeamsController controller;

	@Before
	public void setUp() throws Exception {
		this.teamsEndpoint = ClientBuilder.newClient().target("http://localhost:8080/api/teams/clarification");
		this.teamsControllerInjection();
		this.controller = new TeamsController();
		
		Mockito.when(connection.getContest().getProblems()).thenReturn(new IProblem[] {mockedProblem});
		Mockito.when(mockedProblem.getName()).thenReturn("Dogzilla");
	}
	
	@Test
	public void Test_Submit_Clarification_401_Unauthorized_User(){
		this.response = this.teamsEndpoint.request()
				.header("team_id", "inValidId")
				.post(Entity.json(new SubmitClarificationRequestModel()));
		assertEquals(401, this.response.getStatus());
	}

	@Test
	public void Test_Submit_Clarification_200() throws Exception{	
		
		String message = "How can I do this??";
		SubmitClarificationRequestModel clar = new SubmitClarificationRequestModel("Dogzilla", message);
		
		this.response = this.controller.submitClarification(this.testKey, clar);
		assertEquals(200, this.response.getStatus());
	}

	@Test
	public void Test_Submit_Clarification_400_Problem_Does_Not_Exist() throws Exception {	
		
		String message = "How can I do this??";
		SubmitClarificationRequestModel clar = new SubmitClarificationRequestModel("Dogzilla", message);

		Mockito.doThrow(Exception.class).when(connection).submitClarification(
				Mockito.any(IProblem.class), Mockito.any(String.class));
		this.response = this.controller.submitClarification(this.testKey, clar);

		assertEquals(400, this.response.getStatus());
	}
	
	@After
	public void tearDown() throws Exception {
		this.response.close();
	}
	
}
