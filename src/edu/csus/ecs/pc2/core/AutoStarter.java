package edu.csus.ecs.pc2.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Controls auto starting of contest clock.
 * 
 * This class holds a task for "automatically starting" a contest.
 * Constructing an instance requires providing a contest model and a controller,
 * although the model is currently unused (the controller is used for logging).
 * Calling {@link #scheduleFutureStartContestTask(GregorianCalendar)} creates
 * a task which will start the contest at the specified time (provided the time 
 * is in the future).
 * 
 * 
 * @author John
 */
public class AutoStarter {

    @SuppressWarnings("unused")
    private IInternalContest model;

    private IInternalController controller;
    private Log log;

    //the currently scheduled start task, if any
    private ScheduledFuture<?> startTimeTask = null;

    /**
     * Constructor requires the controller for logging purposes.
     * @param aModel the current contest {@link IInternalContest} (model)
     * @param aController the current {@link IInternalController} (controller)
     */
    public AutoStarter(IInternalContest aModel, IInternalController aController) {
        this.model = aModel;
        this.controller = aController;
        this.log = aController.getLog();
    }

    /**
     * Runnable that starts all contest clocks.
     */
    private final Runnable contestStarterRunnable = new Runnable() {
        public void run() { 

            //TODO: should "auto-start" start THIS site instead of ALL sites?
            // (i.e., which of the following should be used?)
            //                  controller.startContest(getSiteNumber()); 
            controller.startAllContestTimes();

            //TODO: Should an CLOCK_AUTO_STARTED event be created, or is the existing code enough?
            // previously, the following code appeared (when the auto-starting was being handled in the model
            // instead of in the controller).  Now, with autostart being invoked from the Controller, there's
            // currently no way to generate the "CLOCK_AUTO_STARTED" event -- which is a potential problem.  
            // On the other hand, perhaps the "CLOCK_AUTO_STARTED" event is superfluous; the event is logged (below)
            //  and maybe that's enough?
            //                    ContestTimeEvent contestTimeEvent = new ContestTimeEvent(ContestTimeEvent.Action.CLOCK_AUTO_STARTED, 
            //                            contest.getContestTime(), getSiteNumber());
            //                    contest.fireContestTimeListener(contestTimeEvent);
            // dal note: The auto start is logged on the server, there does not seem to be any report/need on a client
            // to know that a contets was auto started vs just started.

            info("Contest automatically started by AutoStarter scheduled task.");
        }
    };



    /**
     * The runnable which starts a contest.
     * 
     * This allows for an override of the Runnable.
     */
    private Runnable starter = contestStarterRunnable;

    /**
     * Creates a task (thread) to wake up and auto-start the contest at the specified time. 
     * 
     * <P>
     * If the specified start time is not in the future then the method logs a warning but otherwise
     * silently does nothing.
     */
    private void scheduleFutureStartContestTask(GregorianCalendar startTime) {

        GregorianCalendar now = new GregorianCalendar();

        if (startTime.after(now)) {

            cancelAnyScheduledStartContestTask();

            //get a thread to handle the execution of the scheduled task
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

            final Runnable contestStarter = starter;

            //schedule the runnable to execute at the specified future time
            long delay = startTime.getTimeInMillis() - now.getTimeInMillis();
            final ScheduledFuture<?> starterHandle = scheduler.schedule(contestStarter, delay, TimeUnit.MILLISECONDS);

            //save the handle so the task can be killed (cancelled) later (before it executes) if necessary
            startTimeTask = starterHandle;

        } else {

            //starttime is not in the future; log and ignore
            if (log!=null) {
                log.warning("AutoStarter received a start time that was not in the future; ignored: " + formatTime(startTime));
            } else {
                System.err.println ("Warning: AutoStarter: unable to log warning about ignoring start time which is not in the future: " + formatTime(startTime));
            }
        }
    }


    /**
     * Format input starttime.
     * 
     * ex. 2016-Oct-14 Oct 14:19:30
     * @param gregorianCalendar a GregorianCalendar object specifying a date/time
     * @return the specified date/time formatted as "YYY-MMM-dd MMM HH:mm:ss"
     */
    public static String formatTime(GregorianCalendar gregorianCalendar) {
        String pattern = "YYY-MMM-dd MMM HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(gregorianCalendar.getTime()) + " (" + gregorianCalendar.getTimeInMillis() + " ms)";
    }

    /**
     * Removes any currently scheduled task.
     * 
     *  <P>
     *  (that is, invokes cancel()
     * on the task and then disposes of the task holder).
     */
    protected void cancelAnyScheduledStartContestTask() {

        //check if there's even any scheduled task
        if (startTimeTask != null) {

            //yes; cancel the scheduled task
            boolean success = startTimeTask.cancel(true);


            //log whether the cancel succeeded or failed
            if (success) {
                info ("AutoStarter: cancelled scheduled Start task");
            } else {
                warn("AutoStarter: unable to cancel scheduled Start task");
            } 
        }

        //dispose of the task, if any
        startTimeTask = null;
    }

    /**
     * Returns a {@link GregorianCalendar} representing the date (time) at which the current AutoStart task will be executed.
     * 
     * @return a {@link GregorianCalendar}
     */
    protected GregorianCalendar getScheduledFutureStartTime() {
        if (startTimeTask != null) {
            long remainingDelay = startTimeTask.getDelay(TimeUnit.MILLISECONDS);
            GregorianCalendar date = new GregorianCalendar();
            date.add(GregorianCalendar.MILLISECOND, (int) remainingDelay);
            return date;
        } else {
            return null;
        }
    }
    
    public Long getRemainingTimeMs(){
        if (startTimeTask != null) {
            long remainingDelay = startTimeTask.getDelay(TimeUnit.MILLISECONDS);
            return remainingDelay;
        } else {
            return null;
        }
    }

    public boolean isAutoStartTaskRunning() {
        return getScheduledFutureStartTime() != null;
    }

    /**
     * Updates scheduled start task.
     * <P>
     * Based on fields isAutoStartContest and getScheduledStartTime in {@link ContestInformation}
     * either starts or stops the contest start countdown task.
     * 
     * @param contestInformation
     */
    public void updateScheduleStartContestTask(ContestInformation contestInformation) {

        // get the scheduled start time (if any) and the time now
        GregorianCalendar startTime = contestInformation.getScheduledStartTime();
        GregorianCalendar now = new GregorianCalendar();

        // if starttime is null it means there should not be any autostart; clear any scheduled start
        
        if (startTime == null) {

            GregorianCalendar scheduledStart = getScheduledFutureStartTime();

            if (scheduledStart != null) {
                info("Cancelling automatic contest start scheduled for time " + scheduledStart.getTimeInMillis());
            }

            cancelAnyScheduledStartContestTask();
        }

        // check if we should schedule a start task
        if (startTime != null && startTime.after(now) && contestInformation.isAutoStartContest()) {

            cancelAnyScheduledStartContestTask();

            scheduleFutureStartContestTask(startTime);

            info("Scheduled automatic contest start at time " + formatTime(startTime));

        }
    }

    /**
     * Override default runnable invoked when contest started.
     * 
     * @param starter
     */
    public void setStarter(Runnable starter) {
        this.starter = starter;
    }

    private void info(String message) {

        logMessage(Level.WARNING, message);
    }

    private void warn(String message) {
        logMessage(Level.WARNING, message);
    }

    private void logMessage(Level level, String message) {

        Date now = new Date();

        if (controller != null) {

            if (log != null) {
                log.log(level,message);
            } else {
                System.err.println (now + " Warning: AutoStarter: unable to log information.");
                System.err.println(now +" "+ message + " Note: couldn't get Log");
            }
        } else {
            System.err.println(now +" " + message + " but couldn't get Controller (so no log)");
        }
    }
}
