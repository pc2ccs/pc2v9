package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.list.UnavailableRunsList;

/**
 * This class encapsulates a {@link Run} which an AutoJudge has detected as being "Unavailable" -- that is, a Run
 * for which the AJ received back a "RUN_NOTAVAILABLE" response (packet) when it issued a "RUN_CHECKOUT" request.
 * 
 * The encapsulation contains two things:  the {@link Run} which an AJ has determined is "unavailable", and an
 * "expiration time", which is the contest elapsed time (in seconds) when the run should no longer be considered "unavailable"
 * (that is, a "timeout" value for removing the run from the list of Unavailable Runs).
 * 
 * Note that UnavailableRuns do not automatically get removed from the UnavailableRuns list when their expiration time
 * arrives; see {@link UnavailableRunsList#addRun(Run)} and {@link UnavailableRunsList#removeExpiredRuns()}.
 * 
 * The class is intended as temporary support -- a workaround for https://github.com/pc2ccs/pc2v9/issues/480.
 * 
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 *
 */
public class UnavailableRun {

    //the Run which has been determined to be unavailable
    private Run run;
    
    //the contest elapsed time, in seconds, when the Run's time on the UnavailableRunsList expires
    private long expirationTimeInSecs;
    
    /**
     * Constructs an UnavailableRun object containing a {@link Run} which an AutoJudge has determined is "unavailable",
     * together with an "expiration time" -- a contest elapsed time (in seconds) at which the contained Run should no longer be 
     * treated as unavailable.
     * 
     * @param run the Run which has been determined by an AJ to be "unavailable".
     * @param expirationTimeInSecs the contest elapsed time (in seconds) when the specified Run should no longer be considered "unavailable".
     */
    public UnavailableRun (Run run, long expirationTimeInSecs) {
        
        this.run = run;
        this.expirationTimeInSecs = expirationTimeInSecs;
    }

    /**
     * Returns the {@link Run} contained in this UnavailableRun object -- that is, the Run which at some point was
     * determined by an AutoJudge to be "unavailable".
     * 
     * @return the Run which is (or was at some point) "unavailable".
     */
    public Run getRun() {
        return run;
    }

    /**
     * Returns the expiration time (contest elapsed time in seconds) for the {@link Run} contained in this UnavailableRun object.
     * 
     * @return the expiration time of the unavailability of the contained Run.
     */
    public long getExpirationTimeInSecs() {
        return expirationTimeInSecs;
    }
    
    
}
