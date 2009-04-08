package edu.csus.ecs.pc2.core.util;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Balloon;
import edu.csus.ecs.pc2.core.model.BalloonDeliveryInfo;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Balloon Utility routines.
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class BalloonHandler implements UIPlugin {

    private IInternalContest contest;

    /**
     * 
     */
    private Hashtable<String,BalloonDeliveryInfo> sentBalloons = new Hashtable<String,BalloonDeliveryInfo>();
    
    /**
     * Quick access to the BalloonSettings by SiteId.
     */
    private Hashtable<Integer, BalloonSettings> balloonSettingsHash = new Hashtable<Integer, BalloonSettings>();

    private boolean sendNotificationsForPreliminary = false;
    
    /**
     * 
     */
    private static final long serialVersionUID = -4763667166259158323L;

    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    class ContestInformationListenerImplementation implements IContestInformationListener {

        public void contestInformationAdded(ContestInformationEvent event) {
            ContestInformation ci = event.getContestInformation();
            if (ci != null) {
                boolean oldValue = sendNotificationsForPreliminary;
                sendNotificationsForPreliminary = ci.isPreliminaryJudgementsTriggerNotifications();
                // it changed from off to on
                if (oldValue != sendNotificationsForPreliminary && sendNotificationsForPreliminary) {
                    reloadBalloonSettings();
                }
            }
        }

        public void contestInformationChanged(ContestInformationEvent event) {
            ContestInformation ci = event.getContestInformation();
            if (ci != null) {
                boolean oldValue = sendNotificationsForPreliminary;
                sendNotificationsForPreliminary = ci.isPreliminaryJudgementsTriggerNotifications();
                // it changed from off to on
                if (oldValue != sendNotificationsForPreliminary && sendNotificationsForPreliminary) {
                    reloadBalloonSettings();
                }
            }
        }

        public void contestInformationRemoved(ContestInformationEvent event) {
            // TODO Auto-generated method stub

        }
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        sendNotificationsForPreliminary = inContest.getContestInformation().isPreliminaryJudgementsTriggerNotifications();
        getContest().addContestInformationListener(new ContestInformationListenerImplementation());
    }

    public String getPluginTitle() {
        return "Balloon Handler methods";
    }
    

    /**
     * Construct a balloon. 
     * @param answer
     * @param submitter
     * @param problemId
     * @param aRun
     * @return
     */
    public Balloon buildBalloon(String answer, ClientId submitter, ElementId problemId, Run aRun) {
        BalloonSettings bSettings = balloonSettingsHash.get(Integer.valueOf(submitter.getSiteNumber()));
        Balloon balloon = new Balloon(bSettings, submitter, getContest().getAccount(submitter).getDisplayName(), problemId, getContest().getProblem(problemId).getDisplayName(), answer, aRun);
        balloon.setContestTitle(getContest().getContestInformation().getContestTitle());
        balloon.setSiteTitle(getContest().getSite(submitter.getSiteNumber()).getDisplayName());
        Run[] runs = getContest().getRuns(submitter);
        Vector<Problem> v = new Vector<Problem>();
        for (int i = 0; i < runs.length; i++) {
            Run run = runs[i];
            Problem problem = getContest().getProblem(run.getProblemId());
            if (!run.isDeleted() && run.isJudged() && run.isSolved() && !v.contains(problem)) {
                if (!run.getJudgementRecord().isPreliminaryJudgement() 
                        || (run.getJudgementRecord().isPreliminaryJudgement() && sendNotificationsForPreliminary)) { 
                                v.add(problem);
                }
            }
        }
        // now put them in the right order
        Problem[] result = new Problem[v.size()];
        int j = 0;
        for (Problem problem : getContest().getProblems()) {
            if (v.contains(problem)) {
                result[j] = problem;
                j++;
            }
        }
        balloon.setProblems(result);
        return balloon;
    }

    public IInternalContest getContest() {
        return contest;
    }

    public Hashtable<Integer, BalloonSettings> getBalloonSettingsHash() {
        return balloonSettingsHash;
    }

    public void setBalloonSettingsHash(Hashtable<Integer, BalloonSettings> balloonSettingsHash) {
        this.balloonSettingsHash = balloonSettingsHash;
    }
    
    /**
     * Has a balloon already been sent for this key
     * 
     * @param balloonKey
     * @return true if sent
     */
    public boolean hasBalloonBeenSent(String balloonKey) {
        boolean result = false;
        if (sentBalloons.containsKey(balloonKey)) {
            result = true;
        }
        return result;
    }

    /**
     * reload/recalculate/initialize balloon settings.
     *
     */
    public void reloadBalloonSettings() {
        BalloonSettings[] balloonSettings = getContest().getBalloonSettings();
        synchronized (balloonSettingsHash) {
            balloonSettingsHash.clear();
            for (int i = 0; i < balloonSettings.length; i++) {
                BalloonSettings settings = balloonSettings[i];
                if (settings.isMatchesBalloonClient(getContest().getClientId())) {
                    Integer siteNum = settings.getSiteNumber();
                    balloonSettingsHash.put(siteNum, settings);
                }
            }
        }
    }

    /**
     * Update delivery info, adding balloon delivery into list.
     * 
     * This is the method to indicate that a balloon has been
     * delivered.
     * 
     * @param balloonKey
     * @param info
     */
    public void updateDeliveryInfo(String balloonKey, BalloonDeliveryInfo info) {
        sentBalloons.put(balloonKey, info);
    }

    /**
     * Set/initialize/overwrite balloon list.
     * 
     * @param balloonList
     */
    public void setBalloonDeliveryList(Hashtable<String, BalloonDeliveryInfo> balloonList) {
        sentBalloons = balloonList;
    }

    public void removeBalloonDelivery(String balloonKey) {
        sentBalloons.remove(balloonKey);
    }

    public Hashtable<String, BalloonDeliveryInfo> getBalloonDeliveryList() {
        return sentBalloons;
    }

    public BalloonDeliveryInfo getBalloonDeliveryInfo(String balloonKey) {
        return sentBalloons.get(balloonKey);
    }

    public Enumeration<String> getBalloonDeliveryInfoKeys() {
        return sentBalloons.keys();
    }
    
    public boolean isRunSolved (ClientId who, ElementId problemId) {
        boolean isSolved = false;
        Run[] runs = getContest().getRuns(who);
        for (int i = 0; i < runs.length; i++) {
            Run run = runs[i];
            if (run.getProblemId().equals(problemId)) {
                if (!run.isDeleted() && run.isJudged() && run.isSolved() && isValidJudgement(run)) {
                    isSolved = true;
                    break;
                }
            }
        }
        return isSolved;
    }

    /**
     * This routine checks and obeys the preliminary judgement rules.
     * 
     * @param run
     * @return true if run is judged and the state is valid
     */
    public boolean isValidJudgement(Run run) {
        boolean result=false;
        if (run.getStatus().equals(RunStates.JUDGED)) {
            // done it's good & simple
            result = true;
        } else {
            // now the ugly stuff, handle being rejudged/preliminary judgements/...
            if (run.isJudged()) {
                // good... but why is the state not JUDGED
                if (run.getStatus().equals(RunStates.MANUAL_REVIEW)) {
                    if (sendNotificationsForPreliminary) {
                        result = true;
                    }
                } else {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Should send a balloon for this run? .
     * 
     * Both checks whether balloon has been send and whether
     * run merits a balloon being sent.
     * 
     * @param run
     * @return
     */
    public boolean shouldSendBalloon(Run run) {
        String balloonKey = getBalloonKey(run.getSubmitter(), run.getProblemId());
        if (!hasBalloonBeenSent(balloonKey)) {

            if ((run.isJudged() && run.isSolved()) && (!run.isDeleted() && isValidJudgement(run))) {

                if (!run.isSendToTeams()) {
                    if (getContest().isAllowed(Permission.Type.RESPECT_NOTIFY_TEAM_SETTING)) {
                        return false;
                    }
                }
                return true;
            }

        }
        return false;
    }
    
    public boolean shouldRemoveBalloon (Run run){
        String balloonKey = getBalloonKey(run.getSubmitter(), run.getProblemId());
        BalloonDeliveryInfo balloonDeliveryInfo = getBalloonDeliveryInfo(balloonKey);
        
        if (balloonDeliveryInfo != null){
            // If they have a balloon check whether they have NOT solved the problem
            if (run.isDeleted() || (! run.isSolved())){
                return true;
            }
        }
        return false;
    }

    /**
     * Return a unique key for the input client and problemId.
     * 
     * This key is used to lookup entries in the balloon delivery
     * info.
     * 
     * @param who
     * @param problemId
     * @return
     */
    public String getBalloonKey(ClientId who, ElementId problemId) {
        return who.getTripletKey() + " " + problemId.toString();
    }
    
}
