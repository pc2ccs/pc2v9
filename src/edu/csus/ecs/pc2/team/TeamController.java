package edu.csus.ecs.pc2.team;

import edu.csus.ecs.pc2.core.Controller;
import edu.csus.ecs.pc2.core.IModel;
import edu.csus.ecs.pc2.core.SerializedFile;
import edu.csus.ecs.pc2.core.SubmittedRun;
import edu.csus.ecs.pc2.transport.StaticTransport;

/**
 * Represents the collection of modules comprising a contest engine.
 * 
 * @see edu.csus.ecs.pc2.Starter
 */

// $HeadURL$
public class TeamController extends Controller implements ITeamController {

    public static final String SVN_ID = "$Id$";

    private IModel model = null;

    public TeamController(IModel model) {
        super();
        this.model = model;
    }


    /**
     * Submit a run to the server.
     */
    public void submitRun(int teamNumber, String problemName, String languageName, String filename) throws Exception {

        SerializedFile serializedFile = new SerializedFile(filename);

        SubmittedRun submittedRun = new SubmittedRun(teamNumber, problemName, languageName, serializedFile);

        
        // If we want to immediately populate the run on the GUI without
        // the run number we can invoke:  model.addRun(submittedRun);
        
        StaticTransport.sendToServer(submittedRun);
        
        System.out.println("TeamController.submitRun - submitted - "+submittedRun);
    }

    /**
     * Transport call back.
     */
    public void receiveSubmittedRun(SubmittedRun submittedRun) {
        model.addRun(submittedRun);
    }

}
