// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core;

import java.util.Date;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Contest Clock Monitor Thread.
 * (Formerly: AutoStopContestThread)
 *
 * The run() method will start a loop to detect both freeze start and end of contest and if contest running stop contest clock for this site.
 * Note that the contest clock will only be done if the current {@link ContestTime#setHaltContestAtTimeZero(boolean)}
 * is set to true.
 *
 * We need to monitor when the freeze starts so we can send a state update on the event feed using ContestTimeListener.
 * We do not need to monitor if the contest is thawed, since that will be automatically triggered when the unfreeze
 * button is pressed on the Settings tab.
 *
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class ContestClockMonitorThread extends Thread {

    /**
     * Default sleep duration.
     */
    public static final long DEFAULT_SLEEP_DURATION_MS = 1000;

    private boolean running = false;

    private IInternalController controller = null;

    private Log log;

    private long delayMs = DEFAULT_SLEEP_DURATION_MS;

    private IInternalContest contest;

    private ContestTime contestTime;

    /**
     * Constructor.
     *
     * Use .start method to start thread.
     *
     * @param controller
     * @param contest
     */
    public ContestClockMonitorThread(IInternalController controller, IInternalContest contest) {
        super();
        log = controller.getLog();
        this.contest = contest;
        this.controller = controller;
        contestTime = contest.getContestTime();

        contest.addContestTimeListener(new ContestTimeListener());
    }

    /**
     * @return true if thread running.
     */
    public boolean isRunning() {
        return running;
    }


    @Override
    public void run() {
        running = true;

        // remember initial state of freeze so we don't send it more than once
        boolean isFrozen = isContestFrozen();

        while (running) {
            try {

                // stop only if both halt is set true and past end of contest.

                if (isHaltContestAtTimeZero() && contestTime.isContestRunning() && contestTime.isPastEndOfContest()) {
                    try {

                        int siteNumber = contestTime.getSiteNumber();
                        info("ContestClockMonitorThread - stopping contest at site " + siteNumber + ", remaining = " + contestTime.getRemainingSecs());

                        controller.stopContest(contestTime.getSiteNumber());

                    } catch (Exception ex2) {
                        warning("Exception in ContestClockMonitorThread stopContest ", ex2);
                    }
                }
                // See if we transitioned to frozen state
                if(!isFrozen && isContestFrozen()) {
                    info("ContestClockMonitorThread - entering freeze period - time remaining = " + contestTime.getRemainingSecs());
                    isFrozen = true;
                    // signal a state change - we are not really changing anything, just causing the notification
                    ContestTime ct = contest.getContestTime();
                    controller.updateContestTime(ct);
                }

                try {
                    Thread.sleep(delayMs);
                } catch (Exception ex2) {
                    ex2.printStackTrace(); // ignore
                }

            } catch (Exception ex3) {
                warning("Exception in ContestClockMonitorThread ", ex3);
            }
        }
        info("ContestClockMonitorThread - thread has been stopped ");
    }

    private boolean isHaltContestAtTimeZero() {

//        return contestTime.isHaltContestAtTimeZero(); // local only

        ContestInformation contestInfo = contest.getContestInformation();
        return contestInfo.isAutoStopContest();
    }

    private void warning(String message, Exception exception) {

        if (!controller.isUsingGUI()) {
            System.err.println(new Date() + " Warning - " + message);
            exception.printStackTrace(System.err);
        }

        if (log != null) {
            log.log(Log.WARNING, message, exception);
        } else {
            System.err.println(" Warning -  " + this.getClass().getName() + " log undefined ");
            System.err.println(new Date() + " Warning - " + message);
            exception.printStackTrace(System.err);
        }

    }

    private void info(String string) {

        if (!controller.isUsingGUI()) {
            System.out.println(new Date() + " " + string);
        }

        if (log != null) {
            log.info(string);
        } else {
            System.err.println(" Warning -  " + this.getClass().getName() + " log undefined ");
            System.err.println(new Date() + " " + string);
        }

    }

    /**
     * Set period to check for end of contest.
     *
     * @param millis
     */
    public void setSleepMs(long millis) {
        this.delayMs = millis;
    }

    /**
     * MS between checks for end of contest.
     *
     * @return
     */
    public long getSleepMs() {
        return delayMs;
    }

    /**
     * Stop the thread
     */
    public void halt() {
        info("halting thread");
        running = false;
    }

    private boolean isThisSite(int siteNumber) {
        return siteNumber == contest.getSiteNumber();
    }

    /**
     * Check if the contest was frozen.  We don't care if it was unfrozen.  We only want to detect
     * a transition between never froze and frozen.
     *
     * @return if the contest was frozen at some point
     */
    private boolean isContestFrozen() {
        if(Utilities.isDebugMode()) {
            System.out.println("ContestClockMonitorThread: Freeze info: FT: " + Utilities.getFreezeTime(contest) + " ET:" + contest.getContestTime().getElapsedSecs());
        }
        return(Utilities.getFreezeTime(contest) <= contest.getContestTime().getElapsedSecs());
    }
    /**
     * A listener that updates contestTime.
     *
     */
    class ContestTimeListener implements IContestTimeListener {

        @Override
        public void contestTimeAdded(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        @Override
        public void contestTimeRemoved(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        @Override
        public void contestTimeChanged(ContestTimeEvent event) {
            ContestTime time = event.getContestTime();
            if (isThisSite(time.getSiteNumber())){
                contestTime = time;
            }
        }

        @Override
        public void contestStarted(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        @Override
        public void contestStopped(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        @Override
        public void contestAutoStarted(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        @Override
        public void refreshAll(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

    }
}
