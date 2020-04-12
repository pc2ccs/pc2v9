package controllers;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import edu.csus.ecs.pc2.api.IProblem;
import utils.ContestControllerInjection;

/**
 * Contains various tests to insure proper operation of {@link ContestController} methods which
 * fetch contest problems from the PC2 Contest.
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
public class Test_Contest_Get_Problems extends ContestControllerInjection{

	Response response;
	ContestController controller;

	@Before
	public void setUp() throws Exception {
		
		//inject a Mock PC2 server connection into the ContestController
		this.contestControllerInjection();
		this.controller = new ContestController();
	}


	/**
	 * Test that an attempt to get problems using an unknown user id fails.
	 */
	@Test
	public void Test_Get_Problems_401_Unauthorized_User(){
		this.response = this.controller.problems("inValidId");
		assertEquals(401, this.response.getStatus());
	}
	
	/**
	 * Test that an attempt to get the contest problems under normal circumstances (logged in, 
	 * problems exist, contest is running) succeeds.
	 * @throws Exception
	 */
	@Test
	public void Test_Get_Problems_200() throws Exception{	
		
		//configure the mock PC2 server connection to return mock problem info when getProblems() is invoked
		Mockito.when(connection.getContest().getProblems()).thenReturn(new IProblem[] {mockedProblem});
		Mockito.when(mockedProblem.getName()).thenReturn("A. Dogzilla");
		Mockito.when(mockedProblem.getShortName()).thenReturn("A");
		
		//make sure the mock says that the contest is running for this test
		Mockito.when(connection.getContest().isContestClockRunning()).thenReturn(true);
		
		this.response = this.controller.problems(testKey);
		assertEquals(200, this.response.getStatus());
	}
	
	/**
	 * Test that an attempt to get the contest problems when the contest is not running fails.
	 * @throws Exception
	 */
	@Test
	public void Test_Get_Problems_401_Contest_Not_Running() throws Exception{
		
		//provide some mock problems, even though they shouldn't be accessible
		Mockito.when(connection.getContest().getProblems()).thenReturn(new IProblem[] {mockedProblem});
		//indicate the contest is not running
		Mockito.when(connection.getContest().isContestClockRunning()).thenReturn(false);
		
		this.response = this.controller.problems(testKey);
		assertEquals(401, this.response.getStatus());
	}
	
	@After
	public void tearDown() throws Exception {
		if(response != null)
			this.response.close();
	}
}
