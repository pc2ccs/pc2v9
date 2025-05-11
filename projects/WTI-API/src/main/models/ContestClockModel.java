package models;

import edu.csus.ecs.pc2.core.model.ContestTime;

/**
 * This class encapsulates the features of the PC2 "contest clock" (class {@link ContestTime}) which need to be able to be passed
 * to the WTI-UI.
 * 
 * @author JohnC
 *
 */
public class ContestClockModel {

	private boolean isRunning;
	private long contestLengthSecs;
	private long elapsedSecs;  			//total time in seconds that the contest has been running -- does NOT include time during any "pauses" 
	private long wallClockStartTime;	//unix timestamp when the contest actually started (msec since the Epoch),
										// or zero if contest has not ever been started.  Does not change due to "pauses".
	
	public ContestClockModel(boolean isRunning, long contestLengthSecs, long elapsedSecs, long wallClockStartTime) {
			this.isRunning = isRunning;
			this.contestLengthSecs = contestLengthSecs;
			this.elapsedSecs = elapsedSecs;
			this.wallClockStartTime = wallClockStartTime;
	}
	
	public ContestClockModel() { }

	public boolean isRunning() {
		return isRunning;
	}

	public long getContestLengthSecs() {
		return contestLengthSecs;

	}

	public long getElapsedSecs() {
		return elapsedSecs;
	}

	public long getWallClockStartTime() {
		return wallClockStartTime;
	}
}