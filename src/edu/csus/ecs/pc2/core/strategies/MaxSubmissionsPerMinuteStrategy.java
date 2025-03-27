// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.strategies;

import edu.csus.ecs.pc2.core.IThrottleStrategy;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * This class implements an {@link IThrottleStrategy} which rejects runs when the submitting team
 * has submitted more than "N" runs in the last minute.  The value of N is set when the class is instantiated.
 * 
 * @author John Clevenger
 *
 */
public class MaxSubmissionsPerMinuteStrategy implements IThrottleStrategy {
    
    public static int DEFAULT_MAX_SUBMISSIONS_PER_MINUTE = 10 ;

    private  IInternalContest contest;
    private int maxPerMinute;
    
    /**
     * Construct a strategy with a default limit on submissions per minute.
     * 
     * @param inContest the {@link IInternalContest} with which this strategy is associated.
     */
    public MaxSubmissionsPerMinuteStrategy (IInternalContest inContest) {
        this.contest = inContest ;
        this.maxPerMinute = DEFAULT_MAX_SUBMISSIONS_PER_MINUTE ;
    }

    /**
     * Construct a strategy with a caller-specified limit on submissions per minute.
     * 
     * @param inContest the {@link IInternalContest} with which this strategy is associated.
     */
    public MaxSubmissionsPerMinuteStrategy (IInternalContest inContest, int maxAllowed) {
        this.contest = inContest ;
        this.maxPerMinute = maxAllowed ;
    }

    @Override
    /**
     * Rejects the specified run if the submitting team has already submitted the maximum allowed number of runs in the past minute. 
     */
    public boolean accept(Run run) {
        int numSoFar = getNumberOfRunsSubmittedInTheLastMinute(contest, run);
        if (numSoFar < maxPerMinute) 
          return true;
        else 
          return false ;
    }

    /**
     * Queries the specified contest to find the runs already submitted by the submitter of the specified run,
     * then determines how many of those previously-submitted runs occurred in the last minute.
     * 
     * @param contest the contest containing runs submitted by teams.
     * @param newRun the (new) {@link Run} submitted by a team about which a strategy decision (i.e. whether or not
     *                  to forward the run to the PC2 Server) is to be made.  
     * 
     * @return the number of runs which the team has already submitted in the last minute.
     */
    private int getNumberOfRunsSubmittedInTheLastMinute(IInternalContest contest, Run newRun) {
        
        //get the clientId for the new Run
        ClientId newSubmitter = newRun.getSubmitter();

        //get the current contest time so we can check run submission times against current time
        ContestTime contestTime = contest.getContestTime();
        long contestElapsedSecs = contestTime.getElapsedSecs();
        
        //get all the runs in the contest
        Run[] allRuns = contest.getRuns();
        
        //count the number of runs previously submitted in the last minute by the same client
        int numSubmittedInLastMinute = 0;
        
        //check each contest run 
        for (Run curRun : allRuns ) {
            
            //see if the current run came from the same submitter
            if (curRun.getSubmitter().equals(newSubmitter)) {

                //yes, same submitter; get the submission time (in seconds) for the current run
                long runSubmisionTimeSecs = curRun.getOriginalElapsedMS()/1000;
                
                //check if the run submission time was within the last minute (60 seconds)
                if (runSubmisionTimeSecs >= contestElapsedSecs-60) {
                    
                    //the run was submitted in the last minute; count it
                    numSubmittedInLastMinute++ ;
                }
            }
        }
        
        return numSubmittedInLastMinute ;
    }

}
