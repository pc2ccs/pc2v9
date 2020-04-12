package controllers;

import static org.junit.Assert.*;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import models.LoginRequestModel;
import models.LoginResponseModel;

public class Test_Teams_Login{

	WebTarget teamsEndpoint;
	Response response;
	TeamsController controller;
	ServerConnection connection;

	@Before
	public void setUp() throws Exception {
		this.controller = Mockito.spy(new TeamsController());
		connection = Mockito.mock(ServerConnection.class, Mockito.RETURNS_DEEP_STUBS);
		Mockito.when(this.controller.createNewServerConnection()).thenReturn(this.connection);
	}

	@Test
	public void test_TeamsLogin_200() throws IOException, LoginFailureException {
		LoginRequestModel login = new LoginRequestModel("team1", "team1");

		response = this.controller.login(login);
		assertEquals(200, response.getStatus());

	}


	@Test
	public void test_TeamsLogin_401() throws LoginFailureException {
		LoginRequestModel login = new LoginRequestModel("team1", "teamFaultyPass");
		Mockito.doThrow(LoginFailureException.class).when(connection).login(Mockito.anyString(), Mockito.anyString());
		this.response = this.controller.login(login);
		assertEquals(401, response.getStatus());
	}

	@Test
	public void test_TeamsLogin_TeamIdentificationReturned() throws LoginFailureException {

		LoginRequestModel login = new LoginRequestModel("team1", "team1");

		this.response = this.controller.login(login);
		LoginResponseModel responseModel = (LoginResponseModel) this.response.getEntity();

		assertNotNull(responseModel.teamId);
	}





	/* *///What is this test trying to test for?
//	@Test
//	public void test_TeamsLogin_TwoTeamLogin() {
//
//		LoginRequestModel teamOneLogin = new LoginRequestModel("team1", "team1");
//		LoginRequestModel teamTwoLogin = new LoginRequestModel("team2", "team2");
//
//		
//		
//		Mockito.when(this.controller.createNewServerConnection()).thenReturn(this.connection);
//		
//
//		//assertTrue(!teamOneResponse.teamId.equals(teamTwoResponse.teamId));
//	}
	

	@After
	public void tearDown() throws Exception {
		if(response != null)
			this.response.close();
	}

}
