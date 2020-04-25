package controllers;

import static org.junit.Assert.*;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.core.model.IFile;
import models.File;
import models.SubmitRunRequestModel;
import utils.TeamsControllerInjection;

public class Test_Teams_Submit_Runs extends TeamsControllerInjection {


	WebTarget teamsEndpoint;
	Response response;
	TeamsController controller;

	@Before
	public void setUp() throws Exception {
		this.teamsEndpoint = ClientBuilder.newClient().target("http://localhost:8080/api/teams/run");
		this.teamsControllerInjection();
		this.controller = new TeamsController();
		
		Mockito.when(connection.getContest().getLanguages()).thenReturn(new ILanguage[] {mockedLanguage});
		Mockito.when(mockedLanguage.getName()).thenReturn("Java");
		Mockito.when(connection.getContest().getProblems()).thenReturn(new IProblem[] {mockedProblem});
		Mockito.when(mockedProblem.getName()).thenReturn("G");
	}


	@Test
	public void Test_Submit_Run_401_Unauthorized_User(){
		this.response = this.teamsEndpoint.request()
				.header("team_id", "inValidId")
				.post(Entity.json(new SubmitRunRequestModel()));
		assertEquals(401, this.response.getStatus());
	}

	@Test
	public void Test_Submit_Run_200_One_File() throws Exception{	
		
		File mtFile = new File("test.java", "3456CVGGH");
		SubmitRunRequestModel run = new SubmitRunRequestModel("G", "Java", mtFile, null, null, false);
		
		this.response = this.controller.submitRun(this.testKey, run);
		assertEquals(200, this.response.getStatus());
	}

	@Test
	public void Test_Submit_Run_400_Problem_Does_Not_Exist() throws Exception {	
		
		File mtFile = new File("test.java", "3456CVGGH");
		SubmitRunRequestModel run = new SubmitRunRequestModel("DoesNotExist", "Java", mtFile, null, null, false);

		Mockito.doThrow(Exception.class).when(connection).submitJudgeRun(Mockito.any(IProblem.class), Mockito.any(ILanguage.class), Mockito.any(IFile.class));
		this.response = this.controller.submitRun(this.testKey, run);

		assertEquals(400, this.response.getStatus());
	}

	@Test
	public void Test_Submit_Run_400_Language_Does_Not_Exist()throws Exception{	
		
		File mtFile = new File("test.java", "3456CVGGH");
		SubmitRunRequestModel run = new SubmitRunRequestModel("G", "DoesNotExist", mtFile, null, null, false);

		Mockito.doThrow(Exception.class).when(connection).submitJudgeRun(Mockito.any(IProblem.class), Mockito.any(ILanguage.class), Mockito.any(IFile.class));
		
		this.response = this.controller.submitRun(this.testKey, run);

		assertEquals(400, this.response.getStatus());
	}

	@Test
	public void Test_Submit_Run_200_Multiple_Files_With_Main() throws Exception{
		File[] extraFiles = {
			new File("FileOne", "5678FTCV"),
			new File("FileTwo", "67834GVC")
		};
		File mtFile = new File("test.java", "3456CVGGH");
		SubmitRunRequestModel run = new SubmitRunRequestModel("G", "Java", mtFile, extraFiles, null, false);
		
		this.response = this.controller.submitRun(this.testKey, run);

		assertEquals(200, this.response.getStatus());
	}
	
	@Test
	public void Test_Submit_Test_Run_200_One_File() throws Exception{
		File main = new File("mainFile", "5678GTHY");
		File testFile = new File("testFile", "674839GTY9");
		
		SubmitRunRequestModel run = new SubmitRunRequestModel("G", "Java", main, null, testFile, null, true);
		
		this.response = this.controller.submitRun(this.testKey, run);
		
		assertEquals(200, this.response.getStatus());
	}
	
	@Test
	public void Test_Submit_Test_Run_200_Multiple_Files_Test_With_Multiple_Files() {
		File[] extraFiles = {
				new File("FileOne", "5678FTCV"),
				new File("FileTwo", "67834GVC")
			};
		File main = new File("test.java", "3456CVGGH");
		
		File[] additionalTest = {
				new File("file3", "98483728"),
				new File("fiel4", "994ThY")
		};
		File testFile = new File("testFile", "674839GTY9");
		
		SubmitRunRequestModel run = new SubmitRunRequestModel("G", "Java", main, extraFiles, testFile, additionalTest, true);
		
		this.response = this.controller.submitRun(this.testKey, run);
		
		assertEquals(200, this.response.getStatus());
		
	}
	
	@Test
	public void Test_Submit_Test_Run_200_Multiple_Test_One_Main() throws Exception{
		File main = new File("mainFile", "5678GTHY");
		File[] additionalFiles = {
				new File("file3", "98483728"),
				new File("fiel4", "994ThY")
		};
		
		File testFile = new File("testFile", "674839GTY9");
		
		SubmitRunRequestModel run = new SubmitRunRequestModel("G", "Java", main, null, testFile, additionalFiles, true);
		
		this.response = this.controller.submitRun(this.testKey, run);
		
		assertEquals(200, this.response.getStatus());
	}
	
	

	@After
	public void tearDown() throws Exception {
		this.response.close();
	}

}
