package edu.csus.ecs.pc2.core.scoring;

import java.util.Properties;

import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IContest;

/**
 * Interface that every Scoring Algorithm must implement.
 * 
 * @author pc2@ecs.csus.edu
 */
// $HeadURL$
public interface IScoringAlgorithm {

    String SVN_ID = "$Id$";

    /**
     * Returns an XML description of the current contest standings.
     * 
     * @param theContest
     *            A proxy object referencing the underlying model describing the contest
     * @param properties
     *            general and implementation specific settings.
     * @param log
     *            a logger, used to add info to the log file and window.
     * @return An XML descriptor giving standings properties for each team
     */
    String getStandings(IContest theContest, Properties properties, Log log) throws IllegalContestState;
}
