package edu.csus.ecs.pc2.team;

import edu.csus.ecs.pc2.core.IController;

/**
 * Represents functions provided by modules comprising the contest engine.
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @author pc2@ecs.csus.edu
 */

// $HeadURL:
// http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu.csus.ecs.pc2/IController.java $
public interface ITeamController extends IController {

    void submitRun(int teamNumber, String problemName, String languageName, String filename) throws Exception;

}
