package services;

import java.util.Calendar;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonObject;

import WebsocketEnums.WebsocketMsgType;
import communication.WTIWebsocket;
import controllers.ContestController;
import edu.csus.ecs.pc2.api.IContestClock;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.api.listener.ContestEvent;
import edu.csus.ecs.pc2.api.listener.IConfigurationUpdateListener;
import edu.csus.ecs.pc2.api.listener.IRunEventListener;

public class ScoreboardChangeListener implements IRunEventListener, IConfigurationUpdateListener {

	private ContestController contestController ;
	private WTIWebsocket socket ;
	private boolean contestHasStarted = false;
	
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
	 * Also checks to see whether the specified event indicates that the contest has just started, and if
	 * so POSTS a message to the webserver indicating the contest has started.
	 */
	@Override
	public void configurationItemUpdated(ContestEvent contestEvent) {
		markStandingsNotCurrent();
		checkContestStartedState(contestEvent);
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
	
	/**
	 * Checks to see whether we've previously seen a Contest Started event; if not, checks the specified ContestEvent to see 
	 * whether it indicates the contest has started and if so sets the global flag indicating the contest has started and makes
	 * an HTTP POST indicating the contest has started.
	 * 
	 * @param contestEvent the {@link ContestEvent} to check if we haven't already seen a contest started event.
	 */
	private void checkContestStartedState(ContestEvent contestEvent) {
		
		//check whether we've already seen a contest started event (this check avoids making repeated contest-has-started posts).
		if (!contestHasStarted) {

			//we haven't yet seen a Contest Start yet; check if the event is a ContestClock event
			if (contestEvent.getEventType().equals(ContestEvent.EventType.CONTEST_CLOCK)) {
				
				//get the clock out of the event and see if the contest has started
				IContestClock clock = contestEvent.getContestClock();
				Calendar startTime = clock.getContestStartTime();
				
				if (startTime != null) {
					
					//yes, the contest has started; mark it so
					contestHasStarted = true;
					
					//post a contest-started notification to the webserver (Note that this amounts to posting
					// to ourselves; it's necessary because we can't detect contest-started within the webserver
					// until after the PC2 API has been constructed, and the only way the webserver can get contest state
					// changes is via the PC2 API.
					
					postContestHasStarted();
				}
			}
		} 
	}
	
	/**
	 * Invokes the ContestController to issue an HTTP POST indicating that the contest has started.
	 */
	private void postContestHasStarted() {
		contestController.postContestHasStarted();
	}
}
