package edu.csus.ecs.pc2.core.scoring;

import edu.csus.ecs.pc2.core.model.IModel;

/**
 * Interface that every Scoring Algorithm must implement.
 *
 * @author pc2@ecs.csus.edu
 *
 *
 */
public interface IScoringAlgorithm {


// $HeadURL: http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu/csus/ecs/pc2/core/scoring/ScoringAlgorithm.java $

    String SVN_ID = "$Id: ScoringAlgorithm.java 881 2006-12-09 11:06:17Z boudreat $";



    /**
     * Returns an XML description of the current contest standings.
     *
     * @param theContest A proxy object referencing the underlying
     *                  model describing the contest
     * @return An XML descriptor giving standings properties for each team
     */
    public String getStandings(IModel theContest);

}
