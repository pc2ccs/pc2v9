package edu.csus.ecs.pc2.ui;


/**
 * A File Diff Viewer.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IDiffPanel extends UIPlugin {

    /**
     * Display files and their differences.
     * 
     * @param firstFileName
     * @param firstFileTitle
     * @param secondFileName
     * @param secondFileTitle
     */
    void showFiles(String firstFileName, String firstFileTitle, String secondFileName, String secondFileTitle);

    /**
     * Show previous files set in {@link #showFiles(String, String, String, String) showFiles}.
     *
     */
    void show();

    /**
     * Dispose of all components and window.
     */
    void dispose();

}
