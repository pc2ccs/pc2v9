/**
 * 
 */
package edu.csus.ecs.pc2.ui;

import java.util.Calendar;
import java.util.Hashtable;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.BalloonSettingsEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IBalloonSettingsListener;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.RunEvent.Action;

/**
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

//$HeadURL$
public class BalloonHandler extends JPanePlugin {
    /**
     * 
     */
    private static final long serialVersionUID = 3041019296688422631L;

    /**
     * @author pc2@ecs.csus.edu
     *
     */
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
        }

        public void accountModified(AccountEvent event) {
        }
        
    }
    /**
     * @author pc2@ecs.csus.edu
     *
     */
    public class BalloonSettingsListenerImplementation implements IBalloonSettingsListener {

        public void balloonSettingsAdded(BalloonSettingsEvent event) {
            // TODO Auto-generated method stub
            
        }

        public void balloonSettingsChanged(BalloonSettingsEvent event) {
            // TODO Auto-generated method stub
            
        }

        public void balloonSettingsRemoved(BalloonSettingsEvent event) {
            // TODO Auto-generated method stub
            
        }

    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    class ContestInformationListenerImplementation implements IContestInformationListener {

        public void contestInformationAdded(ContestInformationEvent event) {
            // TODO Auto-generated method stub
        }

        public void contestInformationChanged(ContestInformationEvent event) {
            // TODO Auto-generated method stub
        }

        public void contestInformationRemoved(ContestInformationEvent event) {
            // TODO Auto-generated method stub
        }
    }

    /**
     * @author pc2@ecs.csus.edu
     *
     */
    public class ProblemListenerImplementation implements IProblemListener {

        public void problemAdded(ProblemEvent event) {
        }

        public void problemChanged(ProblemEvent event) {
        }

        public void problemRemoved(ProblemEvent event) {
        }
        
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     *
     */
    public class RunListenerImplementation implements IRunListener{

        public void runAdded(RunEvent event) {
            // ignore
        }

        public void runChanged(RunEvent event) {
            if (event.getAction().equals(Action.CHANGED)){
                Run run = event.getRun();
                ClientId who = run.getSubmitter();
                ElementId what = run.getProblemId();
                String key = getBalloonKey(who, what);
                if (isBalloonSentFor(key)) {
                    // yes -> recomputeBalloonStatus(who, what);
                    recomputeBalloonStatus(who, what);
                } else { // have not processed any balloons for this key
                    if (!run.isDeleted() && run.isJudged() && run.isSolved()) {
                        if (sendBalloon(who, what)) {
                            sentBalloonFor(key);
                        }
                    } // else we do not send balloons for deleted/new/no 
                }
            }
        }


        public void runRemoved(RunEvent event) {
            Run run = event.getRun();
            ClientId who = run.getSubmitter();
            ElementId what = run.getProblemId();
            String key = getBalloonKey(who, what);
            if (isBalloonSentFor(key)) {
                // revoke if this was the only yes
                recomputeBalloonStatus(who, what);
            }
        }
        
    }

    private Log log;
    
    private Hashtable<String,Long> sentBalloons = new Hashtable<String,Long>();

    /**
     * 
     */
    public BalloonHandler() {
        super();
        // TODO Auto-generated constructor stub
    }
    private String getBalloonKey(ClientId who, ElementId problemId) {
        return who.getTripletKey() + " " + problemId.toString();
    }
    
    /* (non-Javadoc)
     * @see edu.csus.ecs.pc2.ui.JPanePlugin#getPluginTitle()
     */
    @Override
    public String getPluginTitle() {
        return "Balloon Handler";
    }
    private boolean isBalloonSentFor(String key) {
        boolean result = false;
        if (sentBalloons.containsKey(key)) {
            result = true;
        }
        return result;
    }
    /**
     * Should only be called if a balloon has been sent out for this pair.
     * Will issue a revoke if required.
     * @param who
     * @param problemId
     */
    void recomputeBalloonStatus(ClientId who, ElementId problemId) {
        boolean isSolved = false;
        Run[] runs = getContest().getRuns(who);
        for (int i = 0; i < runs.length; i++) {
            Run run = runs[i];
            if (run.getProblemId().equals(problemId)) {
                if (!run.isDeleted() && run.isJudged() && run.isSolved()) {
                    isSolved = true;
                    break;
                }
            }
        }
        if (!isSolved) {
            takeBalloonFrom(getBalloonKey(who, problemId));
            takeBalloon(who, problemId);
        }
    }

    boolean sendBalloon(ClientId who, ElementId problemId) {
        // TODO probably need to have the generic balloon here, eg missing arg
        log.finest("TODO giva a balloon away to "+who.getTripletKey()+ " for "+problemId.toString());
        // TODO fire some event to notify others
        return false;
    }

    private void sentBalloonFor(String key) {
        // record the time the balloon was sent, may be useful later
        sentBalloons.put(key, new Long(Calendar.getInstance().getTime().getTime()));
    }

    /* (non-Javadoc)
     * @see edu.csus.ecs.pc2.ui.UIPlugin#setContestAndController(edu.csus.ecs.pc2.core.model.IContest, edu.csus.ecs.pc2.core.IController)
     */
    @Override
    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);
        
        log = getController().getLog();
        
        getContest().addAccountListener(new AccountListenerImplementation());
        getContest().addProblemListener(new ProblemListenerImplementation());
        getContest().addRunListener(new RunListenerImplementation());
        getContest().addContestInformationListener(new ContestInformationListenerImplementation());
        getContest().addBalloonSettingsListener(new BalloonSettingsListenerImplementation());

    }

    void takeBalloon(ClientId who, ElementId problemId) {
        // TODO probably need to have the generic balloon here, eg missing arg
        log.finest("TODO take a balloon away from "+who.getTripletKey()+ " for "+problemId.toString());
        // TODO fire some event to notify others
    }

    private void takeBalloonFrom(String balloonKey) {
        sentBalloons.remove(balloonKey);
    }
}
