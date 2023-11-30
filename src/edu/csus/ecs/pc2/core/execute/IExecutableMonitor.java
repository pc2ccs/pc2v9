// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
/**
 * IExecutableMonitor interface
 * This interface may be implemented by classes that use the Executable class and
 * desire ongoing status of Executable operations.
 * 
 * The current implementations of this interface, AutoJudgeStatusFrame and ExecuteTimerFrame
 * are JFrames and update GUI components with the Executable status.
 * 
 */
package edu.csus.ecs.pc2.core.execute;

import java.awt.Color;

/**
 * @author John Buck
 *
 */
public interface IExecutableMonitor {
   
    /**
     * sets the following frame components to their initial "reset" states
     * Timer label
     * Timer counter
     * Timer terminate button
     */
    public void resetFrame();
    
    /**
     * show or hide the timer frame
     * 
     * @param bVis
     */
    public void setTimerFrameVisible(boolean bVis);
    
    /**
     * change the color of the timer text (for the "Test" button if execute time gets too big)
     * @param fg
     */
    public void setTimerCountLabelColor(Color fg);
    
    /**
     * change the timer text label
     * @param msg
     */
    public void setTimerCountLabelText(String msg);
    
    /**
     * change the label indicating what is currently being done:
     * executing, compiling, validating, etc.
     * @param msg
     */
    public void setExecuteTimerLabel(String msg);
    
    /**
     * set who gets notified if the Terminate button is pressed (null to clear)
     * @param ntfy
     */
    public void setTerminateButtonNotify(IExecutableNotify ntfy);
}
