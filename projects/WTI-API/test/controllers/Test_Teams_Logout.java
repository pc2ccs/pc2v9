package controllers;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.TeamsControllerInjection;


public class Test_Teams_Logout extends TeamsControllerInjection{
	
	WebTarget teamsEndpoint;
	Response response;
	
	@Before
	public void setUp() throws Exception {
		this.teamsEndpoint = ClientBuilder.newClient().target("http://localhost:8080/api/teams/logout");
		this.teamsControllerInjection();
		this.controller = new TeamsController();
	}

	
	@Test
	public void test_TeamsLogout_200() throws IOException {
		
		response = controller.logout(this.testKey);
		
		assertEquals(200, response.getStatus());
	}
	
	@Test
	public void test_TeamsLogout_401() throws IOException {
		
		response = controller.logout("noTeam");
		
		assertEquals(401, response.getStatus());
	}
	
	@After
	public void tearDown() throws Exception {
		if(response != null)
			this.response.close();
	}
	
}
