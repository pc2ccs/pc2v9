package edu.csus.ecs.pc2.core.strategies;

import java.util.ArrayList;
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

    private  IInternalContest contest;
    private int maxPerMinute;
    
    public MaxSubmissionsPerMinuteStrategy (IInternalContest inContest, int maxAllowed) {
        this.contest = inContest ;
        this.maxPerMinute = maxAllowed ;
    }

    @Override
    /**
     * Rejects the specified run if the submitting team has submitted the maximum allowed number of runs in the past minute. 
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

        //a list of runs submitted by the same submitter as who submitted "newRun", initially empty
        ArrayList<Run> teamRuns = new ArrayList<Run>();

        //get all the runs in the contest
        Run[] allRuns = contest.getRuns();
        
        //check each contest run to see if it came from the same submitter
        for (Run curRun : allRuns ) {
            if (curRun.getSubmitter().equals(newSubmitter)) {
                //add the current run to the list of runs submitted by the newRun submitter
                teamRuns.add(curRun);
            }
        }
        
        //check if the team has actually submitted anything
        if (teamRuns.size()==0) {
            return 0;
        }
        
        //get the current contest time
        ContestTime contestTime = contest.getContestTime();
        long contestElapsedSecs = contestTime.getElapsedSecs();
        
        //count the number of runs previously submitted in the last minute
        int numSubmittedInLastMinute = 0;
        for (Run run : teamRuns) {
            //get the submission time (in seconds) for the run
            long runSubmisionTimeSecs = run.getOriginalElapsedMS()/1000;
            
            //check if the run submission time was within the last minute (60 seconds)
            if (runSubmisionTimeSecs >= contestElapsedSecs-60) {
                //the run was submitted in the last minute; count it
                numSubmittedInLastMinute++ ;
            }
        }
        
        return numSubmittedInLastMinute ;

    }

}
