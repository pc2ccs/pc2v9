// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.execute;

import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.swing.Timer;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;

/**
 * Count up timer window.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
// TODO recreate in VE.
// TODO rename to ExecutionTimer
public class ExecuteTimer extends Thread implements
    IExecuteFrameNotify, java.awt.event.ActionListener {

    private long maxTime = 120; // time in seconds when display turns Red

    private Timer timer; // a timer to generate 1-sec interrupts

    private IExecuteTimerFrame ivjExecuteTimerFrame = null;

    private GregorianCalendar startTime = now();

    private Process proc;

    private IOCollector firstIO;

    private IOCollector secondIO;

    private boolean doAutoStop = false;

    private boolean runTimeLimitExceeded = false;

    private boolean terminatedByOperator = false;

    private Log log = null;

    private ClientId clientId;

    private boolean usingGUI = true;
    
    public ExecuteTimer(Log log, int timeLimit, ClientId clientId, IExecuteTimerFrame eFrame) {
        super();
        this.log = log;
        this.clientId = clientId;
        maxTime = timeLimit;
        ivjExecuteTimerFrame = eFrame;
        if(eFrame != null) {
            usingGUI = true;
        } else {
            usingGUI = false;
        }
        initialize();
    }

    /**   
     * This method is entered when either the underlying Swing Timer fires an event (which happens once per second),
     * or when the "Terminate" button on the ExecuteTimer GUI is pressed.
     * It determines how much time has elapsed since the timer was started and updates the GUI display showing the
     * elapsed time.  If the elapsed time has exceeded the time limit (specified when the ExecuteTimer was created)
     * and the current client is not a team, the method changes the timer display value to red and checks to see if
     * the ExecuteTimer was configured for "autostop"; if so, the method sets the "run time limit exceeded" flag to true
     * and calls {@link #stopIOCollectors()} to shut down IO Collection for the process being controlled by this ExecutionTimer.
     * Finally, if the method was invoked due to a press of the Terminate button, the method invokes connEtoC1() 
     * (a linkage method from the original VisualAge for Java (VAJ) implementation) to perform user-termination cleanup.
     */    
    public void actionPerformed(java.awt.event.ActionEvent e) {

        long elapsedSecs = getElapsedSecs();
        
//        System.out.println("Tick: elapsed = " + elapsedSecs + ";   maxtime = " + maxTime);

        // compute the minutes and seconds elapsed (assumes execution will be < 1 hour)
        long seconds = elapsedSecs % 60;
        long minutes = elapsedSecs / 60;

        // build a string containing the time in MM:SS form
        String newTime = "";
        if (minutes < 10) {
            newTime = newTime + "0";
        }
        newTime = newTime + minutes + ":";

        if (seconds < 10) {
            newTime = newTime + "0";
        }
        newTime = newTime + seconds;

        // check if time is past upper limit; if so, change text display to RED
        if (elapsedSecs > maxTime && (! isTeam())) {
            if(usingGUI) {
                ivjExecuteTimerFrame.setTimerCountLabelColor(java.awt.Color.red);
            }
            if (doAutoStop) {
//                System.out.println("ExecuteTimer - halting run execution, time " + elapsedSecs + " over time limit of " + maxTime + " seconds.");
                log.info("ExecuteTimer - halting run execution, time " + elapsedSecs + " over time limit of " + maxTime + " seconds.");
                setRunTimeLimitExceeded(true);
                stopIOCollectors();
            }

        }
        // update the on-screen display time
        if(usingGUI) {
            ivjExecuteTimerFrame.setTimerCountLabelText(newTime);
        }
    }

    /**
     * This terminates the process that this timer is working
     */
    public void executeFrameTerminated() {
        try {
            if (doAutoStop) {
                log.config("ExecuteTimer - User hit terminate while AJ'ing.");
                setOtherContactStaff(true);
            }
    
            stopIOCollectors();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }


    /**
     * Returns the number of seconds which have passed from the moment the timer's "startTime" was most recently set
     * until now.  Note that the timer's "startTime" is set initially (to "now()") when the ExecuteTimer object is 
     * constructed; it is reset (also to the current value of "now()") when method {@link #setStartTime()} is called.
     * 
     * @return a long containing the number of elapsed seconds from timer start until now
     */
    private long getElapsedSecs() {
        long milliDiff = now().getTime().getTime() - startTime.getTime().getTime();
//        System.out.println("milliDiff = " + milliDiff);
        long secs = milliDiff / 1000;
        return secs;
    }

    /**
     * @return long
     */
    public long getMaxTime() {
        return maxTime;
    }

    /**
     * @return java.lang.Process
     */
    public java.lang.Process getProc() {
        return proc;
    }

    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            timer = new javax.swing.Timer(1000, this);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * @return boolean
     */
    public boolean isTerminatedByOperator() {
        return terminatedByOperator;
    }

    /**
     * @return boolean
     */
    public boolean isRunTimeLimitExceeded() {
        return runTimeLimitExceeded;
    }

    private GregorianCalendar now() {
        TimeZone tz = TimeZone.getTimeZone("GMT");
        return new GregorianCalendar(tz);
    }

    public void setDoAutoStop() {
        doAutoStop = true;
    }

    public void setDoAutoStop(boolean newDoAutoStop) {
        doAutoStop = newDoAutoStop;
    }

    public void setIOCollectors(IOCollector one, IOCollector two) {
        firstIO = one;
        secondIO = two;

    }

    public void setMaxTime(long newMaxTime) {
        maxTime = newMaxTime;
    }

    private void setOtherContactStaff(boolean newOtherContactStaff) {
        terminatedByOperator = newOtherContactStaff;
    }

    public void setProc(java.lang.Process newProc) {
        proc = newProc;
    }

    private void setRunTimeLimitExceeded(boolean newRunTimeLimitExceeded) {
        runTimeLimitExceeded = newRunTimeLimitExceeded;
    }

    /**
     * Resets the timer value text in the GUI to "00:00" and resets the timer's
     * "start time" to "now()".
     */
    public void setStartTime() {
        if(usingGUI) {
            ivjExecuteTimerFrame.setTimerCountLabelText("00:00");
        }
        startTime = now();

    }

    public void setTitle(String msg) {
        if(usingGUI) {
            ivjExecuteTimerFrame.resetFrame();
            ivjExecuteTimerFrame.setExecuteTimerLabel(msg);
        }
    }

    /**
     * Resets the ExecuteTimer object's value to zero and starts it counting.  
     * Calling this method automatically resets the ExecuteTimer's
     * "start time" to "now()" (by calling {@link ExecuteTimer#setStartTime()}) before actually starting the
     * timer counting.  If the timer was creating with the "usingGUI" flag set to true, this method also makes
     * the JFrame containing the timer GUI components (counter text value and "terminate" button) visible.
     */
    public void startTimer() {
        if (usingGUI) {
            ivjExecuteTimerFrame.setTerminateButtonNotify(this);
        }
        setStartTime();
        timer.start();
    }

    /**
     * Kills timer, iocollectors, and process
     */
    public void stopIOCollectors() {

        // Stop the underlying Swing timer and dispose the GUI (if any)
        stopTimer();

        // Stop IO Collectors
        log.info("ExecuteTimer: halting IOCollectors");
        firstIO.haltMe();
        secondIO.haltMe();

        // stop the process
        if (proc != null) {
            log.info("ExecuteTimer: calling Process.destroy() on process " + getProcessID(proc));
            proc.destroy();
//            proc.destroyForcibly();  //should use this, but it requires Java 8
        }

    }

    /**
     * Stops the underlying Swing timer used to update this ExecuteTimer object.
     * If the ExecuteTimer is being displayed in a GUI, also makes the GUI hidden
     * and then disposes it.
     */
    public void stopTimer() {
        if (usingGUI) {
            ivjExecuteTimerFrame.setTerminateButtonNotify(null);
        }
        timer.stop();
    }
    
    private boolean isTeam (){
        return clientId.getClientType().equals(Type.TEAM);
    }
    
    public void setUsingGUI(boolean usingGUI) {
        this.usingGUI = usingGUI;
    }
    
    public boolean isUsingGUI() {
        return usingGUI;
    }
    
    /**
     * This method receives a {@link Process} object and returns the id of that Process.
     * 
     * TODO: currently the implementation of this method simply returns the toString() of the received
     * Process object.  A future upgrade should use the Java 9 method Process.getProcessID() to obtain
     * the actual platform-specific id of the Process.
     *  
     * @param theProcess the Process object who's ID is to be returned
     * @return a String containing the id of the specified Process
     */
    private String getProcessID(Process theProcess) {
        if (theProcess == null) {
            return "null";
        } else {

            // TODO: return the actual process id instead of the toString()
            return theProcess.toString();
        }
    }

}
