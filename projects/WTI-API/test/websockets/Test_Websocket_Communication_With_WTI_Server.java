package websockets;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import communication.WTIWebsocket;
import controllers.TeamsController;
import edu.csus.ecs.pc2.api.IRun;
import services.RunsService;
import services.TestRunService;
import utils.TeamsControllerInjection;

/**
 * Contains various tests to insure proper operation of websocket operations handled by the {@link TeamsController}.
 * 
 * The setup() prior to running the JUnit tests injects into the TeamsController a "mock" PC2 server
 * connection, so that the test can be run without actually having a PC2 server/contest running.
 * 
 * Each test method is responsible for configuring the "mock contest" values located as protected fields in 
 * in the {@link TeamsControllerInjection} superclass (and therefore accessible by this class).  
 * (This is a somewhat poor use of inheritance, but it does provide a convenient way give all JUnit tests 
 * access to the mock PC2 server connection.)
 * 
 * @author EWU WebTeamClient project team, with updates by John Clevenger, PC2 Development Team (pc2.ecs.csus.edu)
 *
 */

public class Test_Websocket_Communication_With_WTI_Server extends TeamsControllerInjection {
	
	String teamId = "xxx-xxxx-xxxxxx";
	String websocketUrl = "ws://localhost:8080/websocket/WTISocket";
	JsonObject builder;
	String serverTestId = "server";
	
	@Before
	public void setup() throws DeploymentException, IOException {
		
		//construct a JSON object holding a typical "run has been judged" websocket message
		this.builder = Json.createObjectBuilder()
				.add("type", "judged")
				.add("id", String.format("%s-%s", "5", "4"))
				.add("teamId", this.teamId)
				.build();
	}
	
	/**
	 * Test that we can open a websocket to the WTI server.  Note that the WTI server must be running in order
	 * for this test to succeed.
	 * 
	 * @throws URISyntaxException
	 * @throws DeploymentException
	 * @throws IOException
	 */
	@Test
	public void Test_Websocket_Connection() throws URISyntaxException, DeploymentException, IOException {
		
		String id = "xxx-xxxx-xxxxxx";
		WTIWebsocket sock = new WTIWebsocket();
		Session ses = ContainerProvider.getWebSocketContainer().connectToServer(sock,  URI.create(String.format("%s/%s", this.websocketUrl, id)));
		
		assertTrue(ses.isOpen());
	}
	
	/**
	 * Test that we can send a "run judged" message through the websocket.
	 * @throws InterruptedException
	 * @throws DeploymentException
	 * @throws IOException
	 */
	@Test
	public void Test_Sending_Judged_Through_Client_Websocket() throws InterruptedException, DeploymentException, IOException {
		WTIWebsocket sock = new WTIWebsocket();
		ContainerProvider.getWebSocketContainer().connectToServer(sock,  URI.create(String.format("%s/%s", this.websocketUrl, this.serverTestId)));
		
		sock.sendMessage(this.builder.toString());
	}
	
	@Test
	public void Test_Successful_Client_Websocket_Connection_With_URI() throws URISyntaxException {
		WTIWebsocket backEndClient = new WTIWebsocket(new URI(String.format("%s/%s", websocketUrl, this.serverTestId)));	
		backEndClient.sendMessage(this.builder.toString());
	}
	
	/**
	 * Test that we can construct a {@link RunsService} (a "listener" for Judged Run Completed notifications used by
	 * the WTI-UI browser client) and get that listener to accept a websocket message without throwing an exception.
	 * 
	 * @throws URISyntaxException
	 */
	@Test
	public void Test_Successful_PC2_Judged_Run_Submission() throws URISyntaxException {
		String teamId = "pc2subTeamId";
		
		WTIWebsocket client = new WTIWebsocket(new URI(String.format("%s/%s", this.websocketUrl, "server")));
		
		//build a "RunService" listener, just like a browswer client would do
		RunsService service = new RunsService(teamId, client);
		
		//create a mock run for the listener to accept
		IRun mockedRun = Mockito.mock(IRun.class);
		
		//tell the listener to send a "run judged" notification for the (mock) run to a client.
		//If this fails an exception will be thrown; if it succeeds then the test is considered to have passed.
		service.runJudged(mockedRun, true);
	}
	
	/**
	 * Test that we can construct a {@link TestRunsService} (a "listener" for Test Run Completed notifications used by
	 * the WTI-UI browser client) and get that listener to accept a websocket message without throwing an exception.
	 * 
	 * @throws URISyntaxException
	 */
	@Test
	public void Test_Successful_PC2_Test_Run_Submission() throws URISyntaxException {
		String teamId = "pc2subTeamId";
		
		WTIWebsocket client = new WTIWebsocket(new URI(String.format("%s/%s", this.websocketUrl, "server")));
		TestRunService service = new TestRunService(teamId, client);
		IRun mockedRun = Mockito.mock(IRun.class);
		
		//tell the listener to send a "test run judged" notification for the (mock) run to a client.
		//If this fails an exception will be thrown; if it succeeds then the test is considered to have passed.
		service.testRunTestingCompleted(mockedRun);
	}
	
	//it's not clear what this test is for or why it has "fail()" hardcoded at the end...
//	@Test
//	public void Test_Several_PC2_Judged_Run_Submissions() throws URISyntaxException, NotLoggedInException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
//		IRun mRun = Mockito.mock(IRun.class);
//		WTIWebsocket client = new WTIWebsocket(new URI(String.format("%s/%s", this.websocketUrl, "server2")));
//		RunListenerList mList = Mockito.mock(RunListenerList.class, Mockito.RETURNS_DEEP_STUBS);
//		ServerConnection connection = Mockito.mock(ServerConnection.class, Mockito.RETURNS_DEEP_STUBS);
//		
//		RunsService service = new RunsService(this.teamId, client);
//		
//		Mockito.doAnswer(new Answer<Void>() {
//
//			@Override
//			public Void answer(InvocationOnMock invocation) throws Throwable {
//				System.out.println("hello");
//				return null;
//
//			}
//			
//		}).when(mList).addRunListener(service);
//		
//		connection.getContest().addRunListener(service);
//			
//		fail();	//it's not clear why this is here... we WANT the test to fail??
//	}
	
	
	@After
	public void tearDown() throws IOException {

	}

}
