import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
import edu.csus.ecs.pc2.api.implementation.Contest;
import edu.csus.ecs.pc2.api.listener.IRunEventListener;

/**
 * This class is designed to allow PHP to easily manage a collection of team
 * ServerConnection objects. ServerConnections are stored in a
 * ServerConnectionManager object, and are identified by a "teamKey" A teamKey
 * can be an IP address, teamID, or any other property unique to each team.
 * 
 * ServerInterface is a Singleton, and therefore should be referenced by first
 * calling getInstance()
 * 
 * @version $Id$
 */

// $HeadURL$
public class ServerInterface {
	public ServerConnectionManager server = new ServerConnectionManager();
	public static ServerInterface serverInterface = new ServerInterface();
	private static int connectionId = 0;
	private ArrayList<IClarification> clarBuffer = new ArrayList<IClarification>();
	private ArrayList<TeamData> teams = new ArrayList<TeamData>();
	private ServerConnection scoreBoard = new ServerConnection();
	private String scoreboardPassword;

	private int prev_clar_num = 0;
	private int cur_clar_num = 0;

	// explicitly private constructor for Singleton behavior
	private ServerInterface() {
		try {
			// Load scoreboard2 password from .ini file.
			Properties pc2Properties = new Properties();
			pc2Properties.load(new FileInputStream("pc2v9.ini"));
			scoreboardPassword = (String) pc2Properties
					.getProperty("scoreboard2password");
			pc2Properties = null;
		} catch (Exception e) {
			throw new RuntimeException("Error loading pc2v9.ini "
					+ e.getMessage(), e);
		}
	}

	// get instance
	public static ServerInterface getInstance() {
		return serverInterface;
	}

	// Check if user is logged in
	public boolean isLoggedIn(String teamkey) {
		try {
			// if(server.getTeam(teamkey).isLoggedIn())
			// System.out.println(teamkey + " is logged in.");
			// else
			// System.out.println(teamkey + " is not logged in.");
			return server.getTeam(teamkey).isLoggedIn();
		} catch (NotLoggedInException e) {
			// System.out.println(teamkey + " is not logged in (exception).");
			return false;
		}
	}

	// ret:
	// true - Contest is stopped, deny submission.
	// false - Contest is running, allow submission.
	public boolean isContestStopped(String connectionID) {
		boolean ret = false;
		try {
			ret = (!server.getTeam(connectionID).getContest()
					.isContestClockRunning());
		} catch (NotLoggedInException e) {
			ret = true;
		}
		return ret;
	}

	// log a team in
	public String login(String username, String password, String sessionId)
			throws LoginFailureException, NotLoggedInException {

		if (!username.toUpperCase().startsWith("TEAM")) {
			throw new LoginFailureException("Can only login as a team");
		}

		String conId;
		synchronized (this) {
			conId = sessionId + Integer.toString(connectionId++);
		}
		server.addTeam(conId, username, password);
		IContest contest = server.getTeam(conId).getContest();

		// only add to list if not already there
		boolean inlist = false;

		for (TeamData t : teams) {
			if (t.getTeamHash() == server.getTeam(conId).getContest()
					.getMyClient().hashCode())
				inlist = true;
		}
		if (!inlist)
			teams.add(new TeamData(server.getTeam(conId).getContest()
					.getMyClient()));

		contest.addRunListener(new IRunEventListener() {
			public void runJudged(IRun run, boolean isFinal) {
				for (TeamData t : teams) {
					if (run.getTeam().hashCode() == t.getTeamHash()) {
						t.setNewRun(run);
						return;
					}

				}
			}

			public void runCheckedOut(IRun run, boolean isFinal) {
			}

			public void runCompiling(IRun run, boolean isFinal) {
			}

			public void runDeleted(IRun run) {
			}

			public void runExecuting(IRun run, boolean isFinal) {
			}

			public void runJudgingCanceled(IRun run, boolean isFinal) {
			}

			public void runSubmitted(IRun run) {
				for (TeamData t : teams) {
					if (run.getTeam().hashCode() == t.getTeamHash()) {
						t.setNewRunSubmission(run);
						return;
					}
				}
			}

			public void runUpdated(IRun run, boolean isFinal) {
			}

			public void runValidating(IRun run, boolean isFinal) {
			}

		}// end contest IRunEventListener() {...}
		);// end contest.add(...)

		contest.addClarificationListener(new IClarificationEventListener() {
			public void clarificationAdded(IClarification clar) {
				for (TeamData t : teams) {
					if (clar.getTeam().hashCode() == t.getTeamHash()) {
						t.setNewClarificationSubmission(clar);
						return;
					}
				}
			}

			public void clarificationAnswered(IClarification clar) {

				for (TeamData t : teams) {
					// System.out.println("clar hash: " + clar.hashCode());
					// System.out.println("team hash: " + t.getTeamHash());
					if (clar.getTeam().hashCode() == t.getTeamHash()) {
						t.setNewClar(clar);
						return;
					}

				}

				// addToClarBuffer(clar);
				// cur_clar_num++;
				// System.out.println("it worked");
			}

			public void clarificationRemoved(IClarification clar) {
			}

			public void clarificationUpdated(IClarification clar) {
				// clarification edited
			}
		});
		return conId;
	}

	// log a team out
	public void logout(String teamKey) throws NotLoggedInException {
		server.removeTeam(teamKey);
	}

	// get a team connection
	public ServerConnection getTeam(String teamKey) throws NotLoggedInException {
		return server.getTeam(teamKey);
	}

	// get list of problems for specific team's contest
	public IProblem[] getProblems(String teamKey) throws NotLoggedInException {
		return getTeam(teamKey).getContest().getProblems();
	}

	/**
 	 * @return get list of clar categories and contest problems.
 	 * @throws NotLoggedInException
 	 */
	public IProblem[] getClarificationProblems(String teamKey)
			throws NotLoggedInException

	{
		Contest contest = getTeam(teamKey).getContest();
		ArrayList<IProblem> list = new ArrayList<IProblem>();

		IProblem[] problems = contest.getProblems();
		list.addAll(Arrays.asList(problems));

		problems = contest.getClarificationCategories();
		list.addAll(Arrays.asList(problems));

		return (IProblem[]) list.toArray(new IProblem[list.size()]);
	}

	// get list of languages for specific team's contest
	public ILanguage[] getLanguages(String teamKey) throws NotLoggedInException {
		return getTeam(teamKey).getContest().getLanguages();
	}

	// get clarifications for specific team's contest
	// NOTE: this includes clarifications for all teams in the specified team's
	// contest
	/*
	 * public IClarification[] getClarifications(String teamKey) throws
	 * NotLoggedInException { //Make a call to Jeremy's program. return
	 * getTeam(teamKey).getContest().getClarifications(); }
	 */

	public IClarification[] getClarificationsById(String teamKey)
			throws NotLoggedInException {
		return getTeam(teamKey).getContest().getClarifications();
	}

	// subit a problem to the contest
	// the temporary filename passed in will be split between the first '.' in
	// the file name.
	// NOTE: be sure to add the correct directory to where file will be sotred
	// (to new file name)
	public synchronized void submitProblem(String teamKey, String problemName,
			String language, String mainFileName, String[] otherFiles)
			throws NotLoggedInException, ProblemNotFoundException,
			LanguageNotFoundException, Exception {
		// String directory = "file_uploads/";
		String directory = "../uploads/";

		// Convert primitive parameters to needed objects

		File file = null;
		File newFile = null;

		try {

			ServerConnection currentTeam = getTeam(teamKey);
			IProblem prob = getProblemByName(teamKey, problemName);
			ILanguage lang = getLanguageByName(teamKey, language);

			// rename temporary file
			String oldFileName = mainFileName;
			mainFileName = getFileName(mainFileName);
			int indexOfBreak = mainFileName.indexOf('.');
			mainFileName = directory + mainFileName.substring(indexOfBreak + 1);

			file = new File(oldFileName);
			newFile = new File(mainFileName);

			file.renameTo(newFile);

			// submit file for judgment
			currentTeam
					.submitRun(prob, lang, mainFileName, new String[0], 0, 0);

			// delete old file
			newFile.delete();

		} catch (Exception e) {

			if (file != null)
				file.delete();
			if (newFile != null)
				newFile.delete();
			throw e;
		}
	}

	private String getFileName(String mainFileName) {
		int index = -1;
		if (mainFileName.contains("/")) {
			index = mainFileName.lastIndexOf('/');
		}

		if (mainFileName.contains("\\")) {
			index = mainFileName.lastIndexOf('\\');
		}
		return mainFileName.substring(index + 1);
	}

	// submit a clarification
	public void submitClarification(String teamKey, String problemName,
			String question) throws NotLoggedInException {
		try {
			IProblem problem = getProblemByName(teamKey, problemName);

			getTeam(teamKey).submitClarification(problem, question);
		} catch (Exception e) {
			// System.out.println("failed to submit clarification");
			return;
		}

	}

	// get problem by name
	public IProblem getProblemByName(String teamKey, String problemName)
			throws ProblemNotFoundException {
		try {
			IProblem[] problems = getProblems(teamKey);
			for (IProblem p : problems)
				if (p.getName().equals(problemName))
					return p;
		} catch (NotLoggedInException e) {
		}
		throw new ProblemNotFoundException();
	}

	// get a language by its name
	public ILanguage getLanguageByName(String teamKey, String languageName)
			throws LanguageNotFoundException {
		try {
			ILanguage[] languages = getLanguages(teamKey);
			for (ILanguage l : languages)
				if (l.getName().equals(languageName))
					return l;
		} catch (NotLoggedInException e) {
		}
		throw new LanguageNotFoundException();
	}

	public IRun[] getRuns(String teamKey) {
		try {
			// TODO: why is currentClient not used ??
			IClient currentClient = getTeam(teamKey).getMyClient();
			IRun[] allRuns = getTeam(teamKey).getContest().getRuns();
			return allRuns;

			/*
			 * ArrayList<IRun> runs = new ArrayList<IRun>(); for(IRun r :
			 * allRuns){ //System.out.println("Team: " +
			 * r.getTeam().getLoginName());
			 * if(r.getTeam().equals(currentClient)){
			 * 
			 * runs.add(r); //DEBUG //System.out.println("Team: " +
			 * r.getTeam().getLoginName() + " Problem: " +
			 * r.getProblem().getName()); } } IRun[] myRunArray = new
			 * IRun[runs.size()]; return runs.toArray(myRunArray);
			 */
		} catch (Exception e) {
			// couldn't get runs
			System.out.println(e);
			return null;
		}
	}// end getRuns

	public IContestClock getClock(String teamKey) throws Exception {
		return getTeam(teamKey).getContest().getContestClock();
	}// end method:getClock

	public IStanding[] getStandings(String teamKey) {
		try {
			synchronized (this) {
				if (!scoreBoard.isLoggedIn()) {
					scoreBoard.login("scoreboard2", getScoreboardPassword());
					// scoreBoard.login(getScoreboardLogin(),getScoreboardPassword());
					// scoreBoard.login("scoreboard1","scoreboard1");
				}
			}
			IStanding[] allStandings = scoreBoard.getContest().getStandings();

			ArrayList<IStanding> standings = new ArrayList<IStanding>();
			for (IStanding s : allStandings) {
				standings.add(s);
			}
			IStanding[] myStandingsArray = new IStanding[standings.size()];

			return standings.toArray(myStandingsArray);
		} catch (Exception e) {
			// couldn't get runs
			System.out.println(e);
			return null;
		}
	}// end getRuns

	private String getScoreboardLogin() {
		return "scoreboard2";
	}

	private String getScoreboardPassword() {
		return scoreboardPassword;
	}

	private void addToClarBuffer(IClarification clar) {
		clarBuffer.add(clar);
	}

	public IClarification[] getClarBuffer() {
		if (clarBuffer.size() > 0) {
			IClarification[] temp = clarBuffer
					.toArray(new IClarification[clarBuffer.size()]);
			clarBuffer.clear();
			return temp;
		}
		return null;
	}

	public IRun JudgmentOccurred(String username) {
		if (username == null) {
			return null;
		}
		for (TeamData t : teams) {
			if (username.equals(t.getTeamName())) {
				return t.getNewRun();
			}

		}
		return null;
	}

	public IClarification clarificationOccurred(String username) {

		if (username == null) {
			return null;
		}
		for (TeamData t : teams) {
			if (username.equals(t.getTeamName())) {
				return t.getNewClar();
			}
		}
		return null;
	}

	public IRun runSubmitOccurred(String username) {
		if (username == null) {
			return null;
		}
		for (TeamData t : teams) {
			if (username.equals(t.getTeamName())) {
				return t.getRunSubmission();
			}
		}
		return null;
	}

	public IClarification clarificationSubmitOccurred(String username) {
		if (username == null) {
			return null;
		}
		for (TeamData t : teams) {
			if (username.equals(t.getTeamName())) {
				return t.getClarificationSubmission();
			}
		}
		return null;
	}

}// end class:ServerInterface

