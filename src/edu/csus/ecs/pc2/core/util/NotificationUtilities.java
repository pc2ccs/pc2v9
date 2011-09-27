package edu.csus.ecs.pc2.core.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonDeliveryInfo;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Notification utilities.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// TODO CCS Remove this fill gap class.

// $HeadURL$
public class NotificationUtilities {

    /**
     * Key for balloon in BalloonList.
     * 
     * @see ClientSettings#getBalloonList();
     * 
     * @param who
     * @param problemId
     * @return
     */
    public String getBalloonKey(ClientId who, ElementId problemId) {
        return who.getTripletKey() + " " + problemId.toString();
    }

    /**
     * Insures at least one client and returns a client.
     * 
     * @param contest
     * @param type
     * @return account for type
     */
    private Account insureAndGetClient(IInternalContest contest, Type type) {
        Vector<Account> accounts = contest.getAccounts(type, contest.getSiteNumber());
        Account account = null;
        if (accounts.size() == 0) {
            contest.generateNewAccounts(type.toString(), 1, true);
            account = contest.getAccounts(type, contest.getSiteNumber()).firstElement();
        } else {
            account = accounts.firstElement();
        }

        return account;
    }

    /**
     * Return or create as needed ClientSettings.
     * 
     * Insures that scoreboard client is created and assigned.
     * 
     * @param contest
     * @return
     */
    public ClientSettings getScoreboardClientSettings(IInternalContest contest) {

        BalloonSettings balloonSettings = contest.getBalloonSettings(contest.getSiteNumber());
        if (balloonSettings == null) {
            balloonSettings = new BalloonSettings("ballonSet1", contest.getSiteNumber());
            Account account = insureAndGetClient(contest, Type.SCOREBOARD);
            balloonSettings.setBalloonClient(account.getClientId());
            contest.addBalloonSettings(balloonSettings);
        }

        ClientId id = balloonSettings.getBalloonClient();

        ClientSettings clientSettings = contest.getClientSettings(id);
        if (clientSettings == null) {
            clientSettings = new ClientSettings(id);
            contest.updateClientSettings(clientSettings);
        }

        return clientSettings;
    }

    /**
     * Add a notification for a run.
     * 
     * @param contest2
     * @param run
     */
    public BalloonDeliveryInfo addNotification(IInternalContest contest2, Run run) {

        ClientSettings settings = getScoreboardClientSettings(contest2);
        Hashtable<String, BalloonDeliveryInfo> balloonList = settings.getBalloonList();
        String key = getBalloonKey(run.getSubmitter(), run.getProblemId());

        BalloonDeliveryInfo info = new BalloonDeliveryInfo(run.getSubmitter(), run.getProblemId(), Calendar.getInstance().getTime().getTime());
        balloonList.put(key, info);
        settings.setBalloonList(balloonList);
        contest2.updateClientSettings(settings);
        return info;
    }

    /**
     * Get all balloon deliveries.
     * 
     * @param contest2
     * @return
     */
    public BalloonDeliveryInfo[] getBalloonDeliveries(IInternalContest contest2) {
        ClientSettings settings = getScoreboardClientSettings(contest2);
        Hashtable<String, BalloonDeliveryInfo> balloonList = settings.getBalloonList();
        ArrayList<BalloonDeliveryInfo> balloonDeliveryArray = Collections.list(balloonList.elements());
        BalloonDeliveryInfo[] balloonDeliveryInfos = (BalloonDeliveryInfo[]) balloonDeliveryArray.toArray(new BalloonDeliveryInfo[balloonDeliveryArray.size()]);
        return balloonDeliveryInfos;
    }

    /**
     * Get a notification for a run, if it exists.
     * 
     * @param contest2
     * @param run
     * @return null if not found, otherwise BalloonDeliveryInfo
     */
    public BalloonDeliveryInfo getNotification(IInternalContest contest2, Run run) {
        ClientSettings settings = getScoreboardClientSettings(contest2);
        Hashtable<String, BalloonDeliveryInfo> balloonList = settings.getBalloonList();
        String key = getBalloonKey(run.getSubmitter(), run.getProblemId());

        return balloonList.get(key);
    }

    /**
     * Does this run already have a notification?.
     * 
     * @param contest2
     * @param run
     * @return true if run has a notification, false otherwise.
     */
    public boolean alreadyHasNotification(IInternalContest contest2, Run run) {
        return getNotification(contest2, run) != null;
    }
}
