/**
 * 
 */
package edu.csus.ecs.pc2.core;

import java.util.GregorianCalendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * This class holds a task for "automatically starting" a contest.
 * Constructing an instance requires providing a contest model and a controller,
 * although the model is currently unused (the controller is used for logging).
 * Calling {@link #scheduleFutureStartContestTask(GregorianCalendar)} creates
 * a task which will start the contest at the specified time (provided the time 
 * is in the future).
 * 
 * 
 * @author John
 *
 */
public class AutoStarter {
    
    @SuppressWarnings("unused")
    private IInternalContest model;
    private IInternalController controller;
    private Log log;

    /**
     * Constructor requires the controller for logging purposes.
     * @param aModel the current contest {@link IInternalContest} (model)
     * @param aController the current {@link IInternalController} (controller)
     */
    public AutoStarter(IInternalContest aModel, IInternalController aController) {
        this.model = aModel;
        this.controller = aController;
    }
    
    //the currently scheduled start task, if any
    private ScheduledFuture<?> startTimeTask = null;

    /**
     * Creates a task (thread) to wake up and auto-start the contest at the specified time.  
     * If the specified start time is not in the future then the method logs a warning but otherwise
     * silently does nothing.
     */
    protected void scheduleFutureStartContestTask(GregorianCalendar startTime) {
        
        GregorianCalendar now = new GregorianCalendar();
        
        //verify we can get a log
        log = null ;
        if (controller!=null) {
            log = controller.getLog();
        }
        
        if (startTime.after(now)) {
            
            cancelAnyScheduledStartContestTask();
            
            //get a thread to handle the execution of the scheduled task
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            
            //create a runnable that will actually start the contest (when it is executed)
            final Runnable contestStarter = new Runnable() {
                public void run() { 
                    
                    //TODO: should "auto-start" start THIS site instead of ALL sites?
                    // (i.e., which of the following should be used?)
//                  controller.startContest(getSiteNumber()); 
                    controller.startAllContestTimes();
                    
                    //TODO: previously, the following code appeared (when the auto-starting was being handled in the model
                    // instead of in the controller).  Now, with autostart being invoked from the Controller, there's
                    // currently no way to generate the "CLOCK_AUTO_STARTED" event -- which is a potential problem.  
                    // On the other hand, perhaps the "CLOCK_AUTO_STARTED" event is superfluous; the event is logged (below)
                    //  and maybe that's enough?
//                    ContestTimeEvent contestTimeEvent = new ContestTimeEvent(ContestTimeEvent.Action.CLOCK_AUTO_STARTED, 
//                            contest.getContestTime(), getSiteNumber());
//                    contest.fireContestTimeListener(contestTimeEvent);
                    
                    //log the automatic start
                    if (controller!=null) {
                        Log log = controller.getLog();
                        if (log!=null) {
                            log.info("Contest automatically started by AutoStarter scheduled task.");
                        } else {
                            System.out.println ("Contest automatically started by AutoStarter scheduled task.");
                        }
                    } else {
                        System.out.println ("Contest automatically started by AutoStarter scheduled task.");
                    }
                }
            };
            
            //schedule the runnable to execute at the specified future time
            long delay = startTime.getTimeInMillis() - now.getTimeInMillis();
            final ScheduledFuture<?> starterHandle = scheduler.schedule(contestStarter, delay, TimeUnit.MILLISECONDS);
            
            //save the handle so the task can be killed (cancelled) later (before it executes) if necessary
            startTimeTask = starterHandle;
            
        } else {
            
            //starttime is not in the future; log and ignore
            if (log!=null) {
                log.warning("AutoStarter received a start time that was not in the future; ignored: " + startTime.getTimeInMillis());
            } else {
                System.err.println ("Warning: AutoStarter: unable to log warning about ignoring start time which is not in the future: " + startTime.getTimeInMillis());
            }
        }
    }
    
    
    /**
     * Removes any currently scheduled task (that is, invokes cancel()
     * on the task and then disposes of the task holder).
     */
    private void cancelAnyScheduledStartContestTask() {
        
        //check if there's even any scheduled task
        if (startTimeTask != null) {
            
            //yes; cancel the scheduled task
            boolean success = startTimeTask.cancel(true);
            
            //log whether the cancel succeeded or failed
            if (success) {
                if (log!=null) {
                    log.info("AutoStarter: cancelled scheduled Start task");
                } else {
                    System.err.println ("Warning: AutoStarter: unable to log informational notice of cancellation of scheduled Start task.");
                }
            } else {
                //cancel failed
                if (log!=null) {
                    log.warning("AutoStarter: unable to cancel scheduled Start task");
                } else {
                    System.err.println ("Warning: AutoStarter: unable log failure to cancel scheduled Start task.");
                }
            } 
        }
        
        //dispose of the task, if any
        startTimeTask = null;
    }


}
