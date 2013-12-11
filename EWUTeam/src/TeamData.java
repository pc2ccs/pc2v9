import java.util.LinkedList;

import edu.csus.ecs.pc2.api.IClarification;
import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IRun;

/**
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class TeamData {
	private IClient team;
	// no longer need newClar or newRun, instead pop of the queues
	// private boolean newClar;
	// private boolean newRun;
	private LinkedList<IClarification> clars;
	private LinkedList<IRun> runs;
	private LinkedList<IRun> runVerification;
	private LinkedList<IClarification> clarificationVerification;

	public TeamData(IClient t) {
		this.team = t;
		// newClar = false;
		// newRun = false;
		clars = new LinkedList<IClarification>();
		runs = new LinkedList<IRun>();
		runVerification = new LinkedList<IRun>();
		clarificationVerification = new LinkedList<IClarification>();
	}

	public int getTeamHash() {
		return team.hashCode();
	}

	public String getTeamName() {
		return team.getLoginName();
	}

	public IClarification getNewClar() {
		if (clars.isEmpty()) {
			return null;
			/*
			 * otherwise a pop of empty list throws a NoSuchElementException
			 */
		}
		return (IClarification) clars.pop(); // returns null if nothing in list
		// return newClar; // for old way return type would be boolean
	}

	public IRun getNewRun() {
		if (runs.isEmpty()) {
			return null;
			/*
			 * otherwise a pop of empty list throws a NoSuchElementException
			 */
		}
		return (IRun) runs.pop(); // returns null if nothing in list
		// return newRun;// for old way return type would be boolean
	}

	public IRun getRunSubmission() {
		if (runVerification.isEmpty())
			return null;
		return (IRun) runVerification.pop();
	}

	public void setNewRunSubmission(IRun run) {
		runVerification.addLast(run);
	}

	public IClarification getClarificationSubmission() {
		if (clarificationVerification.isEmpty())
			return null;
		return (IClarification) clarificationVerification.pop();
	}

	public void setNewClarificationSubmission(IClarification run) {
		clarificationVerification.addLast(run);
	}

	public void setNewClar(IClarification c)// boolean a) old way
	{
		clars.addLast(c);
		// newClar = a;
	}

	public void setNewRun(IRun r)// boolean a) old way
	{
		runs.addLast(r);
		// newRun = a;
	}

}
