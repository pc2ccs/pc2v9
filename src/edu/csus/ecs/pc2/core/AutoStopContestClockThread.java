package edu.csus.ecs.pc2.core;

import java.util.Date;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestTime;

/**
 * Auto Shutdown Thread.
 * 
 * This thread can be used to stop a contest 
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class AutoStopContestClockThread extends Thread {

    /**
     * Default sleep duration.
     */
    public static final long DEFAULT_SLEEP_DURATION_MS = 1000;

    private boolean running = false;

    private ContestTime contestTime = null;

    private IInternalController controller = null;

    private Log log;

    private long delayMs = DEFAULT_SLEEP_DURATION_MS;

    public AutoStopContestClockThread(IInternalController controller, ContestTime newTime) {
        super();
        contestTime = newTime;
        this.controller = controller;
        log = controller.getLog();
    }

    /**
     * @return true if thread running.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Starts loop to detect end of contest and if contest running stop contest clock.
     * 
     * <P>
     * Runs a loop that waits until the end of the contest and
     * then issues a stop contest, until that stop contest works.
     * <P>
     * This assumes that the contest will halt at the end, if the
     * contest does not halt at the end, then do not use this.
     */

    @Override
    public void run()
    {

        running = true;

        while (running)
        {
            try {
                try {
                    Thread.sleep(delayMs);
                } catch (Exception ex2) {
                    ex2.printStackTrace(); // ignore
                }

                if (contestTime.isPastEndOfContest())
                {
                    try {

                        int siteNumber = contestTime.getSiteNumber();
                        info("AutoShutdown - stopping contest at site " + siteNumber + ", remaining = " + contestTime.getRemainingSecs());

                        controller.stopContest(contestTime.getSiteNumber());
                        running = false;

                    } catch (Exception ex2) {
                        warning("Exception in AutoShutdown stopContest ", ex2);
                    }
                }

                if (!contestTime.isContestRunning())
                {
                    info("AutoShutdown - thread stopped, contest stopped");
                    running = false;
                }
            } catch (Exception ex3) {
                warning("Exception in AutoShutdown ", ex3);
            }
        }
        info("AutoShutdown - thread has been stopped ");
    }

    private void warning(String message, Exception exception) {

        if (!controller.isUsingGUI())
        {
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

        if (!controller.isUsingGUI())
        {
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
     * @param delayMs
     */
    public void setSleepMs(long delayMs) {
        this.delayMs = delayMs;
    }

    /**
     * MS between checks for end of contest.
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
}
