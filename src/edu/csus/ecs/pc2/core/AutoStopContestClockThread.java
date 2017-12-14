package edu.csus.ecs.pc2.core;

import java.util.Date;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Auto Clock Stop Thread.
 * 
 * The run() method will start loop to detect end of contest and if contest running stop contest clock for this site.
 * Note that the contest clock will only be done if the current {@link ContestTime#setHaltContestAtTimeZero(boolean)}
 * is set to true.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class AutoStopContestClockThread extends Thread {

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
    public AutoStopContestClockThread(IInternalController controller, IInternalContest contest) {
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

        while (running) {
            try {

                // stop only if both halt is set true and past end of contest.
                
                if (isHaltContestAtTimeZero() && contestTime.isContestRunning() && contestTime.isPastEndOfContest()) {
                    try {

                        int siteNumber = contestTime.getSiteNumber();
                        info("AutoStopContestClockThread - stopping contest at site " + siteNumber + ", remaining = " + contestTime.getRemainingSecs());

                        controller.stopContest(contestTime.getSiteNumber());

                    } catch (Exception ex2) {
                        warning("Exception in AutoStopContestClockThread stopContest ", ex2);
                    }
                }

                try {
                    Thread.sleep(delayMs);
                } catch (Exception ex2) {
                    ex2.printStackTrace(); // ignore
                }

            } catch (Exception ex3) {
                warning("Exception in AutoStopContestClockThread ", ex3);
            }
        }
        info("AutoStopContestClockThread - thread has been stopped ");
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
