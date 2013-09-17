import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

import edu.csus.ecs.pc2.api.IClarification;
import edu.csus.ecs.pc2.api.IClarificationEventListener;
import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IContest;
import edu.csus.ecs.pc2.api.IContestClock;
import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.IStanding;
import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;

/**
 * Manager for a collection of PC^2 ServerConnections.
 * 
 * This class is designed to allow PHP to easily manage a collection of team ServerConnection objects.
 *
 * ServerConnections are stored in a ServerConnectionManager object, and are identified by a "teamKey" 
 * A teamKey can be an IP address, teamID, or any other property unique to each team.
 *
 * ServerInterface is a Singleton, and therefore should be referenced by first calling getInstance()
 * 
 * @author EWU Senior Project Team 2013
 * @version $Id$
 */

// $HeadURL$
public class ServerInterface
{
	public ServerConnectionManager server = new ServerConnectionManager();
	public static ServerInterface serverInterface = new ServerInterface();
	private static int connectionId = 0;
	private ServerConnection scoreBoard;
	private ArrayList <IClarification> clarBuffer = new ArrayList<IClarification>();
    private String scoreboardPassword;

	//explicitly private constructor for Singleton behavior
    private ServerInterface()
    {
        try {
            Properties pc2Properties = new Properties();
            pc2Properties.load(new FileInputStream("pc2v9.ini"));
            scoreboardPassword = (String) pc2Properties.getProperty("client.scoreboard2password");
            pc2Properties = null;
        } catch (Exception e) {
            throw new RuntimeException("Error loading pc2v9.ini "+e.getMessage(), e);
        }
    }

	//get instance
	public static ServerInterface getInstance() { return serverInterface; }
	
	//Check if user is logged in
	public boolean isLoggedIn(String teamkey)
	{
		try{
			return server.getTeam(teamkey).isLoggedIn();
		}
		catch(NotLoggedInException e)
		{
			return false;
		}
	}

	
	//log a team in
	public String login(String username, String password) throws LoginFailureException, NotLoggedInException
	{
		String conId;
		synchronized(this)
		{
			conId ="" + connectionId++;
		}
		server.addTeam(conId, username, password);
		IContest contest = server.getTeam(conId).getContest();
		contest.addClarificationListener(
			new IClarificationEventListener()
			{
				public void clarificationAdded(IClarification clar)
				{
					//when a clarification is submitted
//DEBUG
//System.out.println("Clarification sent: " + clar.getQuestion());
				}
				
				public void clarificationAnswered(IClarification clar)
				{
					addToClarBuffer(clar);
					
				}

				public void clarificationRemoved(IClarification clar)
				{
				}
				
				public void clarificationUpdated(IClarification clar)
				{
					//clarification edited
				}
			}
		);
		return conId;
	}
	
	//log a team out
	public void logout(String teamKey) throws NotLoggedInException
	{
		server.removeTeam(teamKey);
	}
	
	//get a team connection
	public ServerConnection getTeam(String teamKey) throws NotLoggedInException
	{
		return server.getTeam(teamKey);
	}
	
	//get list of problems for specific team's contest
	public IProblem[] getProblems(String teamKey) throws NotLoggedInException
	{
		return getTeam(teamKey).getContest().getProblems();
	}
	
	//get list of languages for specific team's contest
	public ILanguage[] getLanguages(String teamKey) throws NotLoggedInException
	{
		return getTeam(teamKey).getContest().getLanguages();
	}
	
	//get clarifications for specific team's contest
	//	NOTE: this includes clarifications for all teams in the specified team's contest
	public IClarification[] getClarifications(String teamKey) throws NotLoggedInException
	{
		//Make a call to Jeremy's program.
		return getTeam(teamKey).getContest().getClarifications();
	}
	

	public IClarification[] getClarificationsById(String teamKey) {

		IClarification[] myClarArray = null;

		try {
			IClient currentClient = getTeam(teamKey).getMyClient();
			IClarification[] allClars = getTeam(teamKey).getContest().getClarifications();

			ArrayList<IClarification> clars  = new ArrayList<IClarification>();

			for(IClarification c : allClars) {
				if(c.getTeam().equals(currentClient)) {
					clars.add(c);
				}
			}

			myClarArray = new IClarification[clars.size()];
			return clars.toArray(myClarArray);

		}catch(Exception e){
			e.printStackTrace();
		}
		return myClarArray;
	}

	//subit a problem to the contest
	//the temporary filename passed in will be split between the first '.' in the file name.
	//NOTE: be sure to add the correct directory to where file will be sotred (to new file name)
	public synchronized void submitProblem(String teamKey, String problemName,
		String language, String mainFileName, String[] otherFiles) throws NotLoggedInException
	{
		String directory = "../uploads/";

		//Convert primitive parameters to needed objects
		try
		{
			ServerConnection currentTeam = getTeam(teamKey);
			IProblem prob = getProblemByName(teamKey, problemName);
			ILanguage lang = getLanguageByName(teamKey, language);

			//rename temporary file
			String oldFileName = mainFileName;
			int indexOfBreak = mainFileName.indexOf('.');
			mainFileName = directory + mainFileName.substring(indexOfBreak + 1);

			File file = new File(oldFileName);
			File newFile = new File(mainFileName);

			file.renameTo(newFile);

			//submit file for judgement
			currentTeam.submitRun(prob, lang, mainFileName, new String[0], 0, 0);

			//delete old file
			newFile.delete();

		}
		catch(NotLoggedInException e) //only throw this exception, easily allows us to redirect user to login page
		{
			throw e;
		}
		catch(Exception e)
		{
			System.out.println("Something failed... " + e);
		}
	}

	//submit a clarification
	public void submitClarification(String teamKey, String problemName, String question) throws NotLoggedInException
	{
		try{
			IProblem problem = getProblemByName(teamKey, problemName);

			getTeam(teamKey).submitClarification(problem, question);
		}
		catch(Exception e)
		{
//System.out.println("failed to submit clarification");
			return;
		}
	
	}

	//get problem by name
	public IProblem getProblemByName(String teamKey, String problemName) throws Exception{
		try{
			IProblem[] problems = getProblems(teamKey);
			for(IProblem p : problems)
				if(p.getName().equals(problemName))
					return p;
		}catch(NotLoggedInException e){
		}
		throw new Exception("Problem not found");
	}


	//get a language by its name
	public ILanguage getLanguageByName(String teamKey, String languageName) throws Exception{
		try{
			ILanguage[] languages = getLanguages(teamKey);
			for(ILanguage l : languages)
				if(l.getName().equals(languageName))
					return l;
		}catch(NotLoggedInException e){	
		}
		throw new Exception("Language not found");
	}
	
	
	public IRun[] getRuns(String teamKey){
		try{
			IClient currentClient = getTeam(teamKey).getMyClient();
			IRun[] allRuns = getTeam(teamKey).getContest().getRuns();
		
			ArrayList<IRun> runs = new ArrayList<IRun>();
			for(IRun r : allRuns){
//System.out.println("Team: " + r.getTeam().getLoginName());
				if(r.getTeam().equals(currentClient)){

					runs.add(r);
//DEBUG
//System.out.println("Team: " + r.getTeam().getLoginName() + " Problem: " + r.getProblem().getName());
				}
			}
			IRun[] myRunArray = new IRun[runs.size()];
			return runs.toArray(myRunArray);
		}catch(Exception e){
			//couldn't get runs
			System.out.println(e);
			return null;
		}
	}//end getRuns

	public IContestClock getClock(String teamKey) throws Exception {
		return getTeam(teamKey).getContest().getContestClock();
	}//end method:getClock

	public IStanding[] getStandings(String teamKey){
		try{
			IStanding[] allStandings = null;
			synchronized(this) {
				scoreBoard = new ServerConnection();
				scoreBoard.login(getScoreboardLogin(),getScoreboardPassword());
				allStandings = scoreBoard.getContest().getStandings();
				scoreBoard.logoff();
			}
		
			ArrayList<IStanding> standings = new ArrayList<IStanding>();
			for(IStanding s : allStandings ){
				standings.add(s);
			}
			IStanding[] myStandingsArray = new IStanding[standings.size()];
			//scoreBoard.logoff();
			return standings.toArray(myStandingsArray);
		}catch(Exception e){
			//couldn't get runs
			System.out.println(e);
			return null;
		}
	}//end getRuns

	private String getScoreboardLogin() {
        return "scoreboard2";
    }

    private String getScoreboardPassword() {
        return scoreboardPassword;
    }

    private void addToClarBuffer(IClarification clar)
	{
		clarBuffer.add(clar);
	}

	public IClarification[] getClarBuffer()
	{
		IClarification[] temp = clarBuffer.toArray(new IClarification[clarBuffer.size()]);
		clarBuffer.clear();
		return temp;
	}

}//end class:ServerInterface

