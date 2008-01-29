package edu.csus.ecs.pc2.api;

/**
 * Contest data/information.
 * 
 * Contains access to contest data and suppport for events that listen for changes in contest data.
 * <P>
 * Code Sample - this sample will login to the contest, print all runs (info) in the system, then wait for runs to
 * arrive and print those runs (info).
 * 
 * <pre>
 * import edu.csus.ecs.pc2.api.Contest;
 * import edu.csus.ecs.pc2.api.Controller;
 * import edu.csus.ecs.pc2.api.IRun;
 * import edu.csus.ecs.pc2.api.IRunEventListener;
 * import edu.csus.ecs.pc2.api.Run;
 * import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
 * 
 * public class APIExample {
 * 
 * 	protected class RunListener implements IRunEventListener {
 * 
 * 		public void runAdded(Run run) {
 * 			System.out.println(&quot;Site &quot; + run.getSiteNumber() + &quot; Run &quot; + run.getNumber() + &quot; added &quot;);
 * 		}
 * 
 * 		public void runRemoved(Run run) {
 * 			System.out.println(&quot;Site &quot; + run.getSiteNumber() + &quot; Run &quot; + run.getNumber() + &quot; removed &quot;);
 * 		}
 * 
 * 		public void runJudged(Run run) {
 * 			System.out.println(&quot;Site &quot; + run.getSiteNumber() + &quot; Run &quot; + run.getNumber() + &quot; judged &quot;);
 * 		}
 * 
 * 		public void runUpdated(Run run) {
 * 			System.out.println(&quot;Site &quot; + run.getSiteNumber() + &quot; Run &quot; + run.getNumber() + &quot; updated &quot;);
 * 		}
 * 
 * 	}
 * 
 * public void loginAndShowRuns(String loginName, String password) {
 *  
 *  		try {
 *  			Contest contest = Controller.login(loginName, password);
 *  
 *  			contest.addRunListener(new RunListener()); // Add listener for new runs and changed runs
 *  
 *  			System.out.println(&quot;Logged in as &quot; + contest.getClient().getTitle());
 *  
 *  			// Note: These runs are not sorted
 *  			for (IRun run : contest.getRuns()) {
 *  				System.out.println(&quot;Site &quot; + run.getSiteNumber() + &quot; Run &quot; + run.getNumber());
 *  			}
 *  
 *  			// this program will not stop because the listener above will keep it alive.
 *  
 *  		} catch (LoginFailureException e) {
 *  			System.err.println(&quot;Could not login &quot; + loginName + &quot; reason: &quot; + e.getMessage());
 *  		}
 *  	}	public static void main(String[] args) {
 * 		if (args.length != 2) {
 * 			System.out.println(&quot;API Sample, usage: APIExample loginName password&quot;);
 * 		} else {
 * 			System.out.println(&quot;login: &quot; + args[0] + &quot; password: &quot; + args[1]);
 * 			new APIExample().loginAndShowRuns(args[0], args[1]);
 * 		}
 * 	}
 * }
 * </pre>
 * 
 * @see Controller
 * @see edu.csus.ecs.pc2.api.ConfigurationUpdateEvent
 * @see edu.csus.ecs.pc2.api.RunUpdateEvent
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class Contest implements IContest {

	public boolean isLoggedIn() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	public ITeam[] getTeams() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSiteTitle(int siteNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContestTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSiteTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	public ILanguage[] getLanguages() {
		// TODO Auto-generated method stub
		return null;
	}

	public IProblem[] getProblems() {
		// TODO Auto-generated method stub
		return null;
	}

	public IJudgement[] getJudgements() {
		// TODO Auto-generated method stub
		return null;
	}

	public IRun[] getRuns() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addRunListener(IRunEventListener runEventListener) {
		// TODO Auto-generated method stub

	}

	public void removeRunListener(IRunEventListener runEventListener) {
		// TODO Auto-generated method stub

	}

	public void addContestUpdateConfigurationListener(IConfigurationUpdateListener contestUpdateConfigurationListener) {
		// TODO Auto-generated method stub

	}

	public void removeContestUpdateConfigurationListener(IConfigurationUpdateListener contestUpdateConfigurationListener) {
		// TODO Auto-generated method stub

	}

	public IClient getClient() {
		// TODO Auto-generated method stub
		return null;
	}

	public IContestTime getContestTime() {
		// TODO Auto-generated method stub
		return null;
	}

	public IGroup getGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isContestClockRunning() {
		// TODO Auto-generated method stub
		return false;
	}

}
