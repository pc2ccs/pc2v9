package edu.csus.ecs.pc2.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;

import edu.csus.ecs.pc2.core.list.ClientIdComparator;
import edu.csus.ecs.pc2.core.model.BalloonDeliveryInfo;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.util.IMemento;
import edu.csus.ecs.pc2.core.util.XMLMemento;

/**
 * CCS Notification XML.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class NotificationXML {

    public static final String NOTIFICATIONS_TAG = "notifications";

    public static final String NOTIFICATION_TAG = "notification";

    private ClientIdComparator comparator = new ClientIdComparator();

    public XMLMemento createElement(IInternalContest contest, Run run) throws Exception {
        XMLMemento memento = XMLMemento.createWriteRoot(NOTIFICATIONS_TAG);
        IMemento noteMemento = memento.createChild(NOTIFICATION_TAG);
        addNotificationMemento(noteMemento, contest, run);
        return memento;
    }

    private int getProblemIndex(IInternalContest contest, Problem inProblem) {
        int idx = 0;
        for (Problem problem : contest.getProblems()) {
            if (problem.getElementId().equals(inProblem.getElementId())) {
                return idx;
            }
            idx++;
        }

        return -1;
    }

    /**
     * Returns key for 
     * 
     * <pre>
     * from BalloonHandler.getBalloonKey(who, problemId)
     * </pre>
     * TODO this should be in a utility class or a static method somewhere dal  
     * 
     * @param who
     * @param problemId
     * @return
     */
    public String getBalloonKey(ClientId who, ElementId problemId) {
        return who.getTripletKey() + " " + problemId.toString();
    }

    public IMemento addNotificationMemento(IMemento memento, IInternalContest contest, Run run) throws Exception {

        // <?xml version="1.0" encoding="UTF-8" ?>
        // <notifications>
        // <notification>
        // <team-id>34</team-id>
        // <team>U Waterloo</team>
        // <time>132.04213</time>
        // <timestamp>1298733213.102</timestamp>
        // <nr>214</nr>

        XMLUtilities.addChild(memento, "team-id", run.getSubmitter().getClientNumber());
        XMLUtilities.addChild(memento, "team", contest.getAccount(run.getSubmitter()).getDisplayName());

        XMLUtilities.addChild(memento, "time", XMLUtilities.formatSeconds(contest.getContestTime().getElapsedMS())); 
        XMLUtilities.addChild(memento, "contest-time", XMLUtilities.formatSeconds(run.getElapsedMS())); 

        XMLUtilities.addChild(memento, "timestamp", XMLUtilities.getTimeStamp());
        XMLUtilities.addChild(memento, "nr", "TODO: nr?");

        /**
         * Get balloon delivery information for this site.
         */

        // This site
        BalloonSettings balloonSettings = contest.getBalloonSettings(contest.getSiteNumber());

        if (balloonSettings == null) {
            throw new Exception("Balloon Settings not set for site " + contest.getSiteNumber());
        }

        // The client that serves the deliveries
        ClientId clientId = balloonSettings.getBalloonClient();

        ClientSettings clientSettings = contest.getClientSettings(clientId);

        if (clientSettings == null) {
            throw new Exception("No Balloon Client/Settings set/defined for site  " + contest.getSiteNumber());
        }

        // <balloon>
        // <problem-id>B</problem-id>
        // <problem>Bulls and bears</problem>
        // <rgb>ff0000</rgb>
        // <color>Red</color>
        // </balloon>

        Hashtable<String, BalloonDeliveryInfo> hashtable = clientSettings.getBalloonList();
        String balloonKey = getBalloonKey(run.getSubmitter(), run.getProblemId());
        BalloonDeliveryInfo balloonDeliveryInfo = hashtable.get(balloonKey);

        if (balloonDeliveryInfo == null) {
            throw new Exception("No notification/delivery for run "+run);
        }

        addMemento(memento, contest, balloonDeliveryInfo);

        // <first-by-team>true</first-by-team>

        XMLUtilities.addChild(memento, "first-by-team", "TODO:");

        // <balloons>
        // <balloon>
        // <problem-id>D</problem-id>
        // <problem>Down the hill</problem>
        // <rgb>33cc00</rgb>
        // <color>Green</color>
        // </balloon>
        // <balloon>
        // <problem-id>F</problem-id>
        // <problem>Failing to make the grade</problem>
        // <rgb>ffff00</rgb>
        // <color>Yellow</color>
        // </balloon>
        // </balloons>
        // </notification>
        // </notifications>

        IMemento balloonMemento = memento.createChild("balloons"); // A bunch of balloons

        String[] keyList = (String[]) hashtable.keySet().toArray(new String[hashtable.keySet().size()]);
        Arrays.sort(keyList, new BalloonKeyComparator());

        for (String key : keyList) {
            BalloonDeliveryInfo info = hashtable.get(key);

            if (run.getSubmitter().equals(info.getClientId())) {

                /**
                 * Here is logic in the case where the balloons summary does not include the current problem.
                 * 
                 * <pre>
                 * if (! run.getProblemId().equals(info.getProblemId())) {
                 * </pre>
                 */

                addMemento(balloonMemento, contest, info);
            }
        }

        return memento;
    }

    /**
     * Add balloon element.
     * 
     * @param mementoRoot
     * @param contest
     * @param balloonDeliveryInfo
     * @return
     */
    private IMemento addMemento(IMemento mementoRoot, IInternalContest contest, BalloonDeliveryInfo balloonDeliveryInfo) {

        // <balloon>
        // <problem-id>D</problem-id>
        // <problem>Down the hill</problem>
        // <rgb>33cc00</rgb>
        // <color>Green</color>
        // </balloon>

        IMemento memento = mementoRoot.createChild("balloon");

        Problem problem = contest.getProblem(balloonDeliveryInfo.getProblemId());
        int problemIndex = getProblemIndex(contest, problem);

        char let = 'A';
        let += (problemIndex - 1);

        XMLUtilities.addChild(memento, "problem-id", "" + let);
        XMLUtilities.addChild(memento, "problem", problem.getDisplayName());

        BalloonSettings settings = contest.getBalloonSettings(contest.getSiteNumber());
        String balloonColor = settings.getColor(problem);
        XMLUtilities.addChild(memento, "rgb", settings.getColorRGB(problem));
        XMLUtilities.addChild(memento, "color", balloonColor);

        return memento;
    }


    /**
     * Comparator to sort the balloonKeys.
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    protected class BalloonKeyComparator implements Comparator<String>, Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 3204840271423881062L;

        /**
         * 
         * @param clientIDString
         *            a string in form 1TEAM12 for Team 12 Site 1
         * @return
         */
        public ClientId getClientId(String clientIDString) {

            String clientTypeName = ClientType.Type.TEAM.toString();
            int teamIndex = clientIDString.indexOf(clientTypeName);

            if (teamIndex > 0) {
                int siteNumber = Integer.parseInt(clientIDString.substring(0, teamIndex));
                int teamNumber = Integer.parseInt(clientIDString.substring(teamIndex + clientTypeName.length()));
                return new ClientId(siteNumber, ClientType.Type.TEAM, teamNumber);

            } else {
                return new ClientId(0, ClientType.Type.TEAM, 0);
            }
        }

        public int compare(String key1, String key2) {

            String[] fields1 = key1.split(" ");
            String[] fields2 = key2.split(" ");

            ClientId clientId1 = getClientId(fields1[0]);
            ClientId clientId2 = getClientId(fields2[0]);

            return comparator.compare(clientId1, clientId2);
        }
    }

}
