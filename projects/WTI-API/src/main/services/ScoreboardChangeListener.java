package services;

import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonObject;

import WebsocketEnums.WebsocketMsgType;
import communication.WTIWebsocket;
import controllers.ContestController;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.api.listener.ContestEvent;
import edu.csus.ecs.pc2.api.listener.IConfigurationUpdateListener;
import edu.csus.ecs.pc2.api.listener.IRunEventListener;

public class ScoreboardChangeListener implements IRunEventListener, IConfigurationUpdateListener {

	private ContestController contestController ;
	private WTIWebsocket socket ;
	
	/**
	 * Constructs a new ScoreboardChangeListener which listens for changes in standings-related items 
	 * in the specified contest.  When a change which MAY cause the contest standings to alter is detected,
	 * the corresponding listener routine sets the 'WtiServerStandingsAreCurrent flag in the specified
	 * {@link ContestController} false.
	 *  
	 * @param contestController the ContestController which this ScoreboardChangeListener will update on detecting potential-scoreboard-changing 
	 * events.  
	 */
	public ScoreboardChangeListener(ContestController contestController, WTIWebsocket socket) {
		this.contestController = contestController;
		this.socket = socket;
	}
	
	/**
	 * Sets the contest controller's "wtiServerStandingsAreCurrent" flag false, since a
	 * submitted run potentially changes the standings (in terms of "pending runs" count,
	 * which some standings displays may utilize).
	 */
	@Override
	public void runSubmitted(IRun run) {
		markStandingsNotCurrent();
	}

	/**
	 * Sets the contest controller's "wtiServerStandingsAreCurrent" flag false, since a
	 * deleted run potentially changes the standings.
	 */
	@Override
	public void runDeleted(IRun run) {
		markStandingsNotCurrent();
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
		markStandingsNotCurrent();
	}

	/**
	 * Sets the contest controller's "wtiServerStandingsAreCurrent" flag false, since an
	 * updated run potentially changes the standings.
	 */
	@Override
	public void runUpdated(IRun run, boolean isFinal) {
		markStandingsNotCurrent();
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
		markStandingsNotCurrent();
	}

	/**
	 * Sets the contest controller's "wtiServerStandingsAreCurrent" flag false, since
	 * updating a configuration item (for example, changing the "display on scoreboard" property of
	 * a team acount) potentially changes the standings.
	 */
	@Override
	public void configurationItemUpdated(ContestEvent contestEvent) {
		markStandingsNotCurrent();
	}

	/**
	 * Sets the contest controller's "wtiServerStandingsAreCurrent" flag false, since
	 * removing a configuration item (for example, a team account) potentially changes the standings.
	 */
	@Override
	public void configurationItemRemoved(ContestEvent contestEvent) {
		markStandingsNotCurrent();
	}
	
	/**
	 * Sets "wtiServerStandingsAreCurrent" in ContestController to false; 
	 * sends a websocket message to each team client indicating standings are not current.
	 */
	private void markStandingsNotCurrent() {
		
		//tell the ContestController that its cached standings should no longer be considered current
		contestController.setWtiServerStandingsAreCurrent(false) ;

		//get the set of current team connections
		HashMap<String, ServerConnection> teamConnections = contestController.getTeamConnections();
		
		//get each team out of the set
		for (String teamkey : teamConnections.keySet()) {
	
			// build a message for the current team
			JsonObject builder = Json.createObjectBuilder()
				.add("type", WebsocketMsgType.STANDINGS.name().toLowerCase())
				.add("id", "1-1") //id value was: String.format("%s-%s", arg0.getSiteNumber(), arg0.getNumber())), but id is basically useless here...
				.add("teamId", teamkey)
				.build();

			//Send a websocket message telling the current team (browser client) that its standings should no longer be considered current
			//Do this on a separate thread for each send so that timeouts on one send don't delay the other sends
			Thread msgThread = new Thread (new Runnable() {

				@Override
				public void run() {
					socket.sendMessage(builder.toString());
				}
				
			});
			msgThread.start();
		}

	}

}
