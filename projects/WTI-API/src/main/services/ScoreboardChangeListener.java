package services;

import controllers.ContestController;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.listener.ContestEvent;
import edu.csus.ecs.pc2.api.listener.IConfigurationUpdateListener;
import edu.csus.ecs.pc2.api.listener.IRunEventListener;

public class ScoreboardChangeListener implements IRunEventListener, IConfigurationUpdateListener {

	private ContestController contestController ;
	/**
	 * Constructs a new ScoreboardChangeListener which listens for changes in standings-related items 
	 * in the specified contest.  When a change which MAY cause the contest standings to alter is detected,
	 * the corresponding listener routine sets the 'WtiServerStandingsAreCurrent flag in the specified
	 * {@link ContestController} false.
	 *  
	 * @param contestController the ContestController which this ScoreboardChangeListener will update on detecting potential-scoreboard-changing 
	 * events.  
	 */
	public ScoreboardChangeListener(ContestController contestController) {
		this.contestController = contestController;
	}
	
	/**
	 * Sets the contest controller's "wtiServerStandingsAreCurrent" flag false, since a
	 * submitted run potentially changes the standings (in terms of "pending runs" count,
	 * which some standings displays may utilize).
	 */
	@Override
	public void runSubmitted(IRun run) {
		contestController.setWtiServerStandingsAreCurrent(false) ;
	}

	/**
	 * Sets the contest controller's "wtiServerStandingsAreCurrent" flag false, since a
	 * deleted run potentially changes the standings.
	 */
	@Override
	public void runDeleted(IRun run) {
		contestController.setWtiServerStandingsAreCurrent(false) ;
	}

	/**
	 * This method does nothing, since checking out a run (to judge it) has no effect on standings.
	 */
	@Override
	public void runCheckedOut(IRun run, boolean isFinal) {
		// Nothing; this can't change the standings
	}

	/**
	 * Sets the contest controller's "wtiServerStandingsAreCurrent" flag false, since a
	 * judged run potentially changes the standings.
	 */
	@Override
	public void runJudged(IRun run, boolean isFinal) {
		contestController.setWtiServerStandingsAreCurrent(false) ;	
	}

	/**
	 * Sets the contest controller's "wtiServerStandingsAreCurrent" flag false, since an
	 * updated run potentially changes the standings.
	 */
	@Override
	public void runUpdated(IRun run, boolean isFinal) {
		contestController.setWtiServerStandingsAreCurrent(false) ;
	}

	/**
	 * This method does nothing, since compiling a run has no effect on standings.
	 */
	@Override
	public void runCompiling(IRun run, boolean isFinal) {
		// Nothing; this can't change the standings
	}

	/**
	 * This method does nothing, since executing a run has no effect on standings.
	 */
	@Override
	public void runExecuting(IRun run, boolean isFinal) {
		// Nothing; this can't change the standings
	}

	/**
	 * This method does nothing, since validating a run has no effect on standings.
	 */
	@Override
	public void runValidating(IRun run, boolean isFinal) {
		// Nothing; this can't change the standings
	}

	/**
	 * This method does nothing, since cancelling the judging of a run has no effect on standings.
	 */
	@Override
	public void runJudgingCanceled(IRun run, boolean isFinal) {
		// Nothing; this can't change the standings
	}

	/**
	 * Sets the contest controller's "wtiServerStandingsAreCurrent" flag false, since
	 * adding a configuration item (for example, a new team account) potentially changes the standings.
	 */
	@Override
	public void configurationItemAdded(ContestEvent contestEvent) {
		contestController.setWtiServerStandingsAreCurrent(false) ;
	}

	/**
	 * Sets the contest controller's "wtiServerStandingsAreCurrent" flag false, since
	 * updating a configuration item (for example, changing the "display on scoreboard" property of
	 * a team acount) potentially changes the standings.
	 */
	@Override
	public void configurationItemUpdated(ContestEvent contestEvent) {
		contestController.setWtiServerStandingsAreCurrent(false) ;
	}

	/**
	 * Sets the contest controller's "wtiServerStandingsAreCurrent" flag false, since
	 * removing a configuration item (for example, a team account) potentially changes the standings.
	 */
	@Override
	public void configurationItemRemoved(ContestEvent contestEvent) {
		contestController.setWtiServerStandingsAreCurrent(false) ;
	}

}
