package edu.csus.ecs.pc2.core.scoring;

import java.util.Properties;

import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Interface that every Scoring Algorithm must implement.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public interface INewScoringAlgorithm extends IScoringAlgorithm {

    /**
     * Calculate scoring record per problem.
     * @param runs
     * @param problem
     * @param properties
     * @return
     */
    ProblemScoreRecord createProblemScoreRecord(Run[] runs, Problem problem, Properties properties);
    
    /**
     * Returns sorted and ranked StandingsRecord.
     * 
     * @param contest
     * @param properties
     * @return ranked StandingsRecords.
     * @throws IllegalContestState
     */
    StandingsRecord[] getStandingsRecords(IInternalContest contest, Properties properties) throws IllegalContestState;

}
