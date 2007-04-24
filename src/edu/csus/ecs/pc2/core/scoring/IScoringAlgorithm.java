package edu.csus.ecs.pc2.core.scoring;

import edu.csus.ecs.pc2.core.model.IModel;

/**
 * Interface that every Scoring Algorithm must implement.
 *
 * @author pc2@ecs.csus.edu
 *
 *
 */
//$HeadURL$
public interface IScoringAlgorithm {

    String SVN_ID = "$Id$";

    /**
     * Returns an XML description of the current contest standings.
     *
     * @param theContest A proxy object referencing the underlying
     *                  model describing the contest
     * @return An XML descriptor giving standings properties for each team
     */
    String getStandings(IModel theContest);
}
