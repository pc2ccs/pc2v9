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
     * @return
     */
    boolean addFilePane(String string, String outputFile);

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

}
