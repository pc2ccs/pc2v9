package controllers;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import edu.csus.ecs.pc2.api.ILanguage;
import utils.ContestControllerInjection;

/**
 * Contains various tests to insure proper operation of {@link ContestController} methods which
 * fetch contest languages from the PC2 Contest.
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

public class Test_Contest_Get_Languages extends ContestControllerInjection{

	private Response response;
	private ContestController controller;

	@Before
	public void setUp() throws Exception {
		
		//inject a Mock PC2 server connection into the ContestController
		this.contestControllerInjection();
		this.controller = new ContestController();
	}


	/**
	 * Test that requesting the contest languages using an invalid (not logged-in) team Id fails.
	 */
	@Test
	public void Test_Get_Languages_401_Unauthorized_User(){
		this.response = this.controller.languages("inValidId");
		assertEquals(401, this.response.getStatus());
	}
	
	/**
	 * Test that requesting the contest languages when logged in succeeds.
	 * @throws Exception
	 */
	@Test
	public void Test_Get_Languages_200() throws Exception{	
		
		Mockito.when(connection.getContest().getLanguages()).thenReturn(new ILanguage[] {mockedLanguage});
		Mockito.when(mockedLanguage.getName()).thenReturn("JAVA");
		Mockito.when(mockedLanguage.getTitle()).thenReturn("Java");
		
		this.response = this.controller.languages(testKey);
		assertEquals(200, this.response.getStatus());
	}
	
	@After
	public void tearDown() throws Exception {
		if(response != null)
			this.response.close();
	}
}
