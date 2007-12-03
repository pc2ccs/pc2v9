package edu.csus.ecs.pc2.ui;

/**
 * File Viewer. 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface IFileViewer extends UIPlugin {

    /**
     * Show message on viewer.
     * @param string
     */
    void showMessage(String string);

    /**
     * Add file pane to viewer.
     * @param string
     * @param outputFile
     */
    boolean addFilePane(String string, String outputFile);
    
    /**
     * Add Text in a frame.
     * 
     * @param title title of frame
     * @param inMessage text message
     */
    boolean addTextPane(String title, String inMessage);

    /**
     * Show information.
     * @param string
     */
    void setInformationLabelText(String string);

    /**
     * Set title for viewer window.
     * @param string
     */
    void setTitle(String string);
    
    /**
     * Shutdown window, remove all components, and listeners.
     */
    void dispose();

    /**
     * Show window to user.
     * @param b
     */
    void setVisible(boolean b);
    
    void setCompareFileNames(String incomingJudgeOutputFileName, String incomingTeamOutputFileName);

    void enableCompareButton(boolean value);

}
