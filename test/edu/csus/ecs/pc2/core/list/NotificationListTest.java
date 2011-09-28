package edu.csus.ecs.pc2.core.list;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Notification;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.security.FileStorage;
import edu.csus.ecs.pc2.core.util.JUnitUtilities;
import edu.csus.ecs.pc2.core.util.XMLMemento;
import edu.csus.ecs.pc2.exports.ccs.EventFeedXML;

/**
 * Notification List testing.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class NotificationListTest extends TestCase {

    private String sampleFileName;

    private String projectPath;

    @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();

        // Directory where test data is
        String testDir = "testdata";
        projectPath = JUnitUtilities.locate(testDir);
        if (projectPath == null) {
            throw new Exception("Unable to locate " + testDir);
        }

        String loadFile = projectPath + File.separator + testDir + File.separator + "Sumit.java";
        File dir = new File(loadFile);
        if (!dir.exists()) {
            System.err.println("could not find " + loadFile);
            throw new Exception("Unable to locate " + loadFile);
        }

        sampleFileName = loadFile;

    }

    public void testSaveToDisk() throws Exception {

        String dirname = projectPath + File.separator + "testing" + File.separator + "NotifListTest";

        new File(dirname).mkdirs();

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(2, 12, 22, 12, true);

        FileStorage storage = new FileStorage(dirname);

        NotificationList list = new NotificationList(storage);

        Problem problem = contest.getProblems()[2];

        Account judge = contest.getAccounts(Type.JUDGE).firstElement();

        // Create runs

        Account[] teamAccounts = sample.getTeamAccounts(contest);
        Arrays.sort(teamAccounts, new AccountComparator());

        for (Account account : teamAccounts) {
            createYesJudgedRun(contest, account, judge, problem);
        }

        Notification[] notifications = createNotifications(contest, judge.getClientId());
        Arrays.sort(notifications, new NotificationComparator());

        assertEquals("Number of gen'd notifs ", notifications.length, contest.getRuns().length);

        for (Notification notification : notifications) {
            list.addNewNotification(notification);
            contest.acceptNotification(notification);
        }

        notifications = contest.getNotifications();
        Arrays.sort(notifications, new NotificationComparator());

        // dumpNotifications(notifications);

        // dumpNotificationsXML(contest, notifications);

        int numRuns = contest.getRuns().length;

        assertEquals("Number of notifs in list ", numRuns, list.getList().length);
        assertEquals("Number of notifs in list ", numRuns, list.getNextNotificationNumber() - 1);
        assertEquals("Number of notifs in list ", numRuns, contest.getNotifications().length);

        list = null;

        // Load and re-test

        list = new NotificationList(storage);
        list.loadFromDisk(0);

        assertEquals("Number of notifs in list ", numRuns, list.getNextNotificationNumber() - 1);
        assertEquals("Number of notifs in list ", numRuns, list.getList().length);

        list.clear();
        list = null;

        NotificationList list2 = new NotificationList(storage);
        list2.loadFromDisk(0);
        assertEquals("Cleared number of  notifs ", 1, list2.getNextNotificationNumber());

    }

    /**
     * Create run with Yes judgement for team and problem, and add to contest.
     * 
     * @param contest
     * @param team
     * @param judge
     * @param problem
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FileSecurityException
     */
    private Run createYesJudgedRun(IInternalContest contest, Account team, Account judge, Problem problem) throws IOException, ClassNotFoundException, FileSecurityException {

        SampleContest sample = new SampleContest();

        Run run = sample.createRun(contest, team.getClientId(), problem);

        RunFiles runFiles = new RunFiles(run, getSampleFileName());
        Run newRun = contest.acceptRun(run, runFiles);

        Run run2 = sample.addJudgement(contest, newRun, sample.getYesJudgement(contest), judge.getClientId());
        contest.updateRun(run2, judge.getClientId());
        return run2;
    }

    public String getSampleFileName() {
        return sampleFileName;
    }

    private Notification[] createNotifications(IInternalContest contest, ClientId judgeClient) {

        Vector<Notification> list = new Vector<Notification>();

        for (Run run : contest.getRuns()) {
            if (run.isSolved()) {
                Notification notif = contest.getNotification(run.getSubmitter(), run.getProblemId());
                if (notif == null) {
                    Notification notification = new Notification(run, judgeClient, new Date().getTime(), contest.getContestTime().getElapsedMS());
                    list.addElement(notification);
                }
            }
        }

        return (Notification[]) list.toArray(new Notification[list.size()]);
    }

    void dumpNotifications(Notification[] notifications) {

        for (Notification notification : notifications) {

            System.out.println(" Notification " + notification.getNumber() + " Site " + notification.getSiteNumber() + " elapsed " + (notification.getElapsedMS() / 1000));
            System.out.println("              by " + notification.getSubmitter() + " for " + notification.getProblemId());

        }
    }

    void dumpNotificationsXML(IInternalContest contest, Notification[] notifications) {

        EventFeedXML eventFeedXML = new EventFeedXML();

        for (Notification notification : notifications) {

            try {
                XMLMemento xmlMemento = eventFeedXML.createElement(contest, notification);
                System.out.println(xmlMemento.saveToString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

}
