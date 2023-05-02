// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
/**
 * IExecutableNotify interface
 * This interface provides call-back method(s) that a an implementor uses to control
 * the execution of a process.
 * 
 * Currently, the only call-back, executeFrameTerminated(), tells the execution class that
 * it should terminate the run.  This interface works in conjunction with the IExecutableMonitor.
 * An implementation will register itself with an IExecutableMonitor using IExecutableMonitor.setTerminateButtonNotify(this).
 * When the IExecutableMonitor wants to terminate the execution (a button press on the
 * Terminate button, for example), it can then call executeFrameTerminated() to notify the
 * IExecutableNotify implementation (such as Executable). 
 *  
 */
package edu.csus.ecs.pc2.core.execute;

/**
 * @author John Buck
 *
 */
public interface IExecutableNotify {

    /**
     * called when the user presses the terminate button
     */
    public void executeFrameTerminated();
}
