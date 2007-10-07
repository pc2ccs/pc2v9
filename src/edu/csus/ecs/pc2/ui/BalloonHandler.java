/**
 * 
 */
package edu.csus.ecs.pc2.ui;

import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.list.RunComparatorByTeam;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.BalloonSettingsEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IBalloonSettingsListener;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.Site;
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
    public class BalloonSettingsListenerImplementation implements IBalloonSettingsListener {

        public void balloonSettingsAdded(BalloonSettingsEvent event) {
            loadBalloonSettings();
            BalloonSettings balloonSettings = event.getBalloonSettings();
            if (balloonSettings != null && balloonSettings.isBalloonsEnabled()) {
                recomputeBalloons(balloonSettings.getSiteNumber());
            }
            
        }

        public void balloonSettingsChanged(BalloonSettingsEvent event) {
            // we do not know exactly what changed, so just make sure we
            // are uptodate
            loadBalloonSettings();
            BalloonSettings balloonSettings = event.getBalloonSettings();
            if (balloonSettings != null && balloonSettings.isBalloonsEnabled()) {
                recomputeBalloons(balloonSettings.getSiteNumber());
            }
        }

        public void balloonSettingsRemoved(BalloonSettingsEvent event) {
            loadBalloonSettings();
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
                BalloonSettings balloonSettings = getContest().getBalloonSettings(Integer.valueOf(who.getSiteNumber()));
                if (balloonSettings == null) {
                    return;
                }
                if (!balloonSettings.isBalloonsEnabled()) {
                    return;
                }
                // TODO are we allowed to send this balloon (based on time?)
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
                // will revoke if this was the only yes
                recomputeBalloonStatus(who, what);
            }
        }
        
    }

    private Log log;
    
    private Hashtable<String,Long> sentBalloons = new Hashtable<String,Long>();
    
    /**
     * Quick access to the BalloonSettings by SiteId.
     */
    private Hashtable<Integer, BalloonSettings> balloonSettingsHash = new Hashtable<Integer, BalloonSettings>();
    
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
    
    void loadBalloonSettings() {
        BalloonSettings[] balloonSettings = getContest().getBalloonSettings();
        synchronized (balloonSettingsHash) {
            balloonSettingsHash.clear();
            for (int i = 0; i < balloonSettings.length; i++) {
                BalloonSettings settings = balloonSettings[i];
                Integer siteNum = settings.getSiteNumber();
                balloonSettingsHash.put(siteNum, settings);
            }
        }
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

    void recomputeBalloons(int siteNumber) {
        BalloonSettings balloonSettings = balloonSettingsHash.get(Integer.valueOf(siteNumber));
        if (balloonSettings == null) {
            return;
        }
        if (!balloonSettings.isBalloonsEnabled()) {
            return;
        }
       RunComparatorByTeam runComparatorByTeam = new RunComparatorByTeam();
        TreeMap<Run, Run> runTreeMap = new TreeMap<Run, Run>(runComparatorByTeam);
        Run[] runs = getContest().getRuns();
        for (int i = 0; i < runs.length; i++) {
            Run run = runs[i];
            if (run.getSiteNumber() != siteNumber) {
                continue;
            }
            runTreeMap.put(run, run);
        }
        Collection runColl = runTreeMap.values();
        Iterator runIterator = runColl.iterator();

        while (runIterator.hasNext()) {
            Object o = runIterator.next();
            Run run = (Run) o;
            if (!run.isDeleted() && run.isJudged() && run.isSolved()) {
                // there should be a yes for this run
                String key = getBalloonKey(run.getSubmitter(), run.getProblemId()); 
                if (!isBalloonSentFor(key)) { // no balloon has been sent
                    if (sendBalloon(run.getSubmitter(), run.getProblemId())) {
                        sentBalloonFor(key);
                    } else {
                        // TODO error sending balloon
                        log.info("Problem sending balloon to " + run.getSubmitter().getTripletKey() + " for " + run.getProblemId());
                    }
                }
            }
            // TODO handle revokes
        }

    }
    boolean sendBalloon(ClientId who, ElementId problemId) {
        // TODO probably need to have the generic balloon here, eg missing arg
        log.finest("TODO give a balloon to "+who.getTripletKey()+ " for "+problemId.toString());
        // TODO fire some event to notify others
        return true;
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
        
        loadBalloonSettings();
        // TODO return our clientSettings and populate balloons
        Site[] sites = inContest.getSites();
        for (int i = 0; i < sites.length; i++) {
            recomputeBalloons(sites[i].getSiteNumber());
        }

        getContest().addRunListener(new RunListenerImplementation());
        getContest().addBalloonSettingsListener(new BalloonSettingsListenerImplementation());

    }

    boolean takeBalloon(ClientId who, ElementId problemId) {
        // TODO probably need to have the generic balloon here, eg missing arg
        log.finest("TODO take a balloon away from "+who.getTripletKey()+ " for "+problemId.toString());
        // TODO fire some event to notify others
        return true;
    }

    private void takeBalloonFrom(String balloonKey) {
        sentBalloons.remove(balloonKey);
    }
}
