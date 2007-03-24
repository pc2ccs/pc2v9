package edu.csus.ecs.pc2.team;

import java.util.Vector;

import edu.csus.ecs.pc2.core.AccountListener;
import edu.csus.ecs.pc2.core.IModel;
import edu.csus.ecs.pc2.core.RunEvent;
import edu.csus.ecs.pc2.core.RunListener;
import edu.csus.ecs.pc2.core.SubmittedRun;
import edu.csus.ecs.pc2.core.RunEvent.Action;

/**
 * Represents the collection of contest data.
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class TeamModel implements  IModel {

    public static final String SVN_ID = "$Id$";

    private Vector<RunListener> runListenterList = new Vector<RunListener>();

    private Vector<SubmittedRun> runList = new Vector<SubmittedRun>();

    public TeamModel() {
    }

    public void addRunListener(RunListener runListener) {
        runListenterList.addElement(runListener);

    }

    public void removeRunListener(RunListener runListener) {
        runListenterList.removeElement(runListener);
    }

    private void fireRunListener(RunEvent runEvent) {
        for (int i = 0; i < runListenterList.size(); i++) {

            if (runEvent.getAction() == Action.ADDED) {
                runListenterList.elementAt(i).runAdded(runEvent);
            } else if (runEvent.getAction() == Action.DELETED) {
                runListenterList.elementAt(i).runRemoved(runEvent);
            } else {
                runListenterList.elementAt(i).runChanged(runEvent);
            }
        }
    }

    public void addRun(SubmittedRun submittedRun) {
        runList.addElement(submittedRun);
        RunEvent runEvent = new RunEvent (Action.ADDED, submittedRun);
        fireRunListener(runEvent);
    }

    public SubmittedRun acceptRun(SubmittedRun submittedRun) throws Exception {
        throw new Exception("who cares");
    }

    public void generateNewAccounts(String clientTypeName, int count, boolean active) {
        // TODO Is there a need for this setting on the team ??
        
    }

    public void addAccountListener(AccountListener accountListener) {
        // TODO Auto-generated method stub
        
    }

    public void removeAccountListener(AccountListener accountListener) {
        // TODO Auto-generated method stub
        
    }
}
