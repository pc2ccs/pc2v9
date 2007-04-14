package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;
import java.util.Properties;
import java.util.Vector;

import edu.csus.ecs.pc2.core.list.AccountList;

/**
 * Interface that every Scoring Algorithm must implement.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface IScoringAlgorithm extends Serializable {

    String SVN_ID = "$Id$";

    /**
     * Name for number of teams property key.
     * 
     * Passed into {@link #getStandings(Run[], AccountList, Problem[], Properties)}, the value is a integer.
     * 
     */
    String NUMBER_OF_TEAMS = "NUMBER_OF_TEAMS";

    /**
     * 
     */
    String NUMBER_OF_PROBLEMS = "NUMBER_OF_PROBLEMS";

    /**
     * Get Algorithm specific name and values.
     * 
     * @return Properties
     */
    Properties getProperties();

    /**
     * Return a in rankings order list of standings.
     * 
     * @param runs
     *            a list of runs.
     * @param properties
     *            a list of custom and system properties.
     * @return list of rankings.
     */
    Vector<StandingsRecord> getStandings(Run[] runs, AccountList accountList, Problem[] problems, Properties properties) throws Exception;

}
