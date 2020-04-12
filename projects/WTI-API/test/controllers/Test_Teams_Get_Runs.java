package controllers;

import static org.junit.Assert.*;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.core.model.IFile;
import utils.TeamsControllerInjection;

public class Test_Teams_Get_Runs extends TeamsControllerInjection {
	
	Response response;
	TeamsController controller;
	IRun mockedRun;
	
	@Before
	public void setup() throws Exception {
		this.mockedRun = Mockito.mock(IRun.class, Mockito.RETURNS_DEEP_STUBS);
		Mockito.when(this.connection.getContest().getRuns()).thenReturn(new IRun[] {mockedRun});
		Mockito.when(this.connection.getContest().getTestRuns()).thenReturn(new IRun[] {});
		
		this.teamsControllerInjection();
		this.controller = new TeamsController();
	}

	@Test
	public void Test_Get_Runs_200() throws Exception {
		String teamName = "team1";
		
		Mockito.when(this.mockedRun.getTeam().getLoginName()).thenReturn(teamName);
		Mockito.when(this.connection.getMyClient().getLoginName()).thenReturn(teamName);
		Mockito.when(this.mockedRun.getLanguage().getName()).thenReturn("Java");
		Mockito.when(this.mockedRun.getProblem().getName()).thenReturn("G");
		Mockito.when(this.mockedRun.getJudgementName()).thenReturn("Accepted");
		Mockito.when(this.mockedRun.getSubmissionTime()).thenReturn((long)9);
		Mockito.when(this.mockedRun.isTestRun()).thenReturn(false);
		Mockito.when(this.mockedRun.isPreliminaryJudged()).thenReturn(false);
		Mockito.when(this.mockedRun.isFinalJudged()).thenReturn(true);
		Mockito.when(this.mockedRun.getSiteNumber()).thenReturn(9);
		Mockito.when(this.mockedRun.getNumber()).thenReturn(1);
		
		this.response = this.controller.getTeamRuns(this.testKey);
		
		assertEquals(200, response.getStatus());
	}
	
	@Test
	public void Test_Get_Runs_200_Test_Run_With_Standard_Error() throws Exception {
		String teamName = "team1";
		
		Mockito.when(this.mockedRun.getTeam().getLoginName()).thenReturn(teamName);
		Mockito.when(this.connection.getMyClient().getLoginName()).thenReturn(teamName);
		Mockito.when(this.mockedRun.getLanguage().getName()).thenReturn("Java");
		Mockito.when(this.mockedRun.getProblem().getName()).thenReturn("G");
		Mockito.when(this.mockedRun.getJudgementName()).thenReturn("Accepted");
		Mockito.when(this.mockedRun.getSubmissionTime()).thenReturn((long)9);
		Mockito.when(this.mockedRun.isTestRun()).thenReturn(true);
		Mockito.when(mockedRun.getTestRunResults().getStderrFiles()).thenReturn(new IFile[] {});
		Mockito.when(mockedRun.getTestRunResults().getStdoutFiles()).thenReturn(new IFile[] {});
		Mockito.when(this.mockedRun.isPreliminaryJudged()).thenReturn(false);
		Mockito.when(this.mockedRun.isFinalJudged()).thenReturn(true);
		Mockito.when(this.mockedRun.getSiteNumber()).thenReturn(9);
		Mockito.when(this.mockedRun.getNumber()).thenReturn(1);
		
		this.response = this.controller.getTeamRuns(this.testKey);
		
		assertEquals(200, response.getStatus());
	}
	
	@Test
	public void Test_Get_Runs_200_Test_Run_With_Out_Standard_Error() throws Exception {
		String teamName = "team1";
		
		Mockito.when(this.mockedRun.getTeam().getLoginName()).thenReturn(teamName);
		Mockito.when(this.connection.getMyClient().getLoginName()).thenReturn(teamName);
		Mockito.when(this.mockedRun.getLanguage().getName()).thenReturn("Java");
		Mockito.when(this.mockedRun.getProblem().getName()).thenReturn("G");
		Mockito.when(this.mockedRun.getJudgementName()).thenReturn("Accepted");
		Mockito.when(this.mockedRun.getSubmissionTime()).thenReturn((long)9);
		Mockito.when(this.mockedRun.isTestRun()).thenReturn(true);
		Mockito.when(mockedRun.getTestRunResults().getStderrFiles()).thenReturn(null);
		Mockito.when(mockedRun.getTestRunResults().getStdoutFiles()).thenReturn(new IFile[] {});
		Mockito.when(this.mockedRun.isPreliminaryJudged()).thenReturn(false);
		Mockito.when(this.mockedRun.isFinalJudged()).thenReturn(true);
		Mockito.when(this.mockedRun.getSiteNumber()).thenReturn(9);
		Mockito.when(this.mockedRun.getNumber()).thenReturn(1);
		
		this.response = this.controller.getTeamRuns(this.testKey);
		
		assertEquals(200, response.getStatus());
	}

}
