package controllers;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import edu.csus.ecs.pc2.api.IJudgement;
import utils.ContestControllerInjection;

public class Test_Contest_Get_Judgements extends ContestControllerInjection{
	
	WebTarget contestEndpoint;
	Response response;
	ContestController controller;

	@Before
	public void setUp() throws Exception {
		this.contestEndpoint = ClientBuilder.newClient().target("http://localhost:8080/api/judgements");
		
		this.contestControllerInjection();
		this.controller = new ContestController();
	}
	
	@Test
	public void Test_Get_Judgements_401_Unauthorized_User(){
		this.response = this.controller.clarifications("inValidId");
		assertEquals(401, this.response.getStatus());
	}
		
	@Test
	public void Test_Get_Judgements_200() throws Exception{	
						
		Mockito.when(connection.getContest().getJudgements()).thenReturn(new IJudgement[] {mockedJudgement});
		Mockito.when(mockedJudgement.getName()).thenReturn("Accepted");
		
		this.response = this.controller.judgements(testKey);
		assertEquals(200, this.response.getStatus());
	}
	
	@After
	public void tearDown() throws Exception {
		if(response != null)
			this.response.close();
	}
	
	
}
