package edu.csus.ecs.pc2.ui;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class ContestClockDisplayTest extends AbstractTestCase {

    SampleContest sample = new SampleContest();
    
    /**
     * Test remaining time clock text for team user
     * @throws Exception
     */
    public void testClockTextTeamUser() throws Exception {
        
        IInternalContest contest = sample.createStandardContest();
        
        Log log = new Log(this.getName()+".log");
        
        if (isDebugMode()){
            addConsoleHandler(log);
        }
        
        ContestTime time = contest.getContestTime();
        int localSiteNumber = contest.getSiteNumber();
        boolean isTeam = true;
        JFrame frame = null; // causes no update of title for frame.
        
        ContestClockDisplay display = new ContestClockDisplay(log, time, localSiteNumber, isTeam, frame);
        
        String actual = display.getRemainingTimeClockText(time);
        
        assertEquals("STOPPED", actual);
        time.setElapsedSecs(120);
        time.startContestClock();
        actual = display.getRemainingTimeClockText(time);
        assertEquals("4:58", actual);
        
        time.stopContestClock();
        time.setElapsedSecs(time.getContestLengthSecs());
        time.startContestClock();
        
        actual = display.getRemainingTimeClockText(time);
        assertEquals("< 2 mins", actual);
    }
    
    /**
     * Test remaining time clock text for admin user
     * @throws Exception
     */
    public void testClockTextAdminUser() throws Exception {
        
        IInternalContest contest = sample.createStandardContest();
        
        Account account = getAdminAccount(contest);
        contest.setClientId(account.getClientId()); 
        
        Log log = new Log(this.getName()+".log");
        
        if (isDebugMode()){
            addConsoleHandler(log);
        }
        
        ContestTime time = contest.getContestTime();
        int localSiteNumber = contest.getSiteNumber();
        boolean isTeam = false;
        JFrame frame = null; // causes no update of title for frame.
        
        ContestClockDisplay display = new ContestClockDisplay(log, time, localSiteNumber, isTeam, frame);
        
        String actual = display.getRemainingTimeClockText(time);
        
        assertEquals("5:00:00", actual);
        time.setElapsedSecs(120);
        time.startContestClock();
        actual = display.getRemainingTimeClockText(time);
        assertEquals("4:58:00", actual);
        
        time.stopContestClock();
        time.setElapsedSecs(time.getContestLengthSecs());
        time.startContestClock();
        
        actual = display.getRemainingTimeClockText(time);
        assertEquals("0 seconds ", actual);
    }

    private Account getAdminAccount(IInternalContest contest) {
        Account[] accounts = SampleContest.getAccounts(contest, Type.ADMINISTRATOR);
        assertTrue(accounts.length > 0);
        return accounts[0];
    }
    
    /**
     * Text 
     * @throws Exception
     */
    public void testGetScheduledStartTiemClockText() throws Exception {
        
        IInternalContest contest = sample.createStandardContest();
        
        Account account = getAdminAccount(contest);
        contest.setClientId(account.getClientId()); 
        
        Log log = new Log(this.getName()+".log");
        
        if (isDebugMode()){
            addConsoleHandler(log);
        }
        ContestTime time = contest.getContestTime();
        int localSiteNumber = contest.getSiteNumber();
        boolean isTeam = false;
        JFrame frame = null; // causes no update of title for frame.

        ContestClockDisplay display = new ContestClockDisplay(log, time, localSiteNumber, isTeam, frame);

        Calendar cal = GregorianCalendar.getInstance();
//        System.out.println("date before  " + cal.getTime());
        ContestInformation info = contest.getContestInformation();

        info.setScheduledStartDate(cal.getTime());
        display.setScheduledStartTime(info);
        
        // Start Time is now.

        String actual = display.getScheduledTimeClockText();
        assertEquals("0:00", actual);
        
        // Three minutes beyond scheduled time, aka -3:00

        int seconds = 180;
        cal.add(GregorianCalendar.MILLISECOND, seconds * 1000);
//        System.out.println("date after   " + cal.getTime());
        info.setScheduledStartDate(cal.getTime());
        display.setScheduledStartTime(info);

        actual = display.getScheduledTimeClockText();
        // add to use 2:59 because it might 
        assertTrue ("Display not correct "+actual, "2:59".equals(actual) || "3:00".equals(actual));
        
        // Three minutes before aka 3:00
        
        cal = GregorianCalendar.getInstance();
        seconds = 180;
        cal.add(GregorianCalendar.MILLISECOND, - seconds * 1000);
//        System.out.println("date after   " + cal.getTime());
        info.setScheduledStartDate(cal.getTime());
        display.setScheduledStartTime(info);

        actual = display.getScheduledTimeClockText();
        assertEquals("-3:00", actual);
 
    }
    
    public void testgetScheduleOrRemainingTime() throws Exception {
        
        if (isFastJUnitTesting()){
            return;
        }
        
        IInternalContest contest = sample.createStandardContest();
        
        Account account = getAdminAccount(contest);
        contest.setClientId(account.getClientId()); 
        
        Log log = new Log(this.getName()+".log");
        
        if (isDebugMode()){
            addConsoleHandler(log);
        }
        ContestTime time = contest.getContestTime();
        int localSiteNumber = contest.getSiteNumber();
        boolean isTeam = false;
        JFrame frame = null; // causes no update of title for frame.

        ContestClockDisplay display = new ContestClockDisplay(log, time, localSiteNumber, isTeam, frame);

        Calendar cal = GregorianCalendar.getInstance();
        System.out.println("date before  " + cal.getTime());
        ContestInformation info = contest.getContestInformation();

        info.setScheduledStartDate(cal.getTime());
        display.setScheduledStartTime(info);
        
        // Start Time is now.

        String actual = display.getScheduledTimeClockText();
        assertEquals("0:00", actual);
        
        // Three minutes beyond scheduled time, aka -3:00

        int seconds = 180;
        cal.add(GregorianCalendar.MILLISECOND, seconds * 1000);
        System.out.println("date after   " + cal.getTime());
        info.setScheduledStartDate(cal.getTime());
        display.setScheduledStartTime(info);

        actual = display.getScheduleOrRemainingTime(time);
        // add to use 2:59 because it might 
        assertTrue ("Display not correct "+actual, "2:59".equals(actual) || "3:00".equals(actual));
        
        // Three minutes before aka 3:00
        
        cal = GregorianCalendar.getInstance();
        seconds = 180;
        cal.add(GregorianCalendar.MILLISECOND, - seconds * 1000);
        System.out.println("date after   " + cal.getTime());
        info.setScheduledStartDate(cal.getTime());
        display.setScheduledStartTime(info);

        actual = display.getScheduleOrRemainingTime(time);
        assertEquals("-3:00", actual);
        
        time.startContestClock();
        
        actual = display.getScheduleOrRemainingTime(time);
        assertEquals("5:00:00", actual);
        
        Thread.sleep(5 * 1000);
        actual = display.getScheduleOrRemainingTime(time);
        assertEquals("4:59:55", actual);
        
        
        
        
        
    }
}
