package edu.csus.ecs.pc2.ui;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * A viewer that shows nothing.
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class NullViewer implements IFileViewer, UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -4793436161895153405L;

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        // nothing here, move on

    }

    public String getPluginTitle() {
        return "Null Viewer";
    }

    public void showMessage(String string) {
        // nothing here, move on

    }

    public boolean addFilePane(String string, String outputFile) {
        // nothing here, move on
        return false;
    }

    public boolean addTextPane(String title, String inMessage) {
        // nothing here, move on
        return false;
    }

    public void setInformationLabelText(String string) {
        // nothing here, move on

    }

    public void setSelectedIndex(int index) {
        // nothing here, move on

    }

    public void setTitle(String string) {
        // nothing here, move on

    }

    public void dispose() {
        // nothing here, move on

    }

    public void setVisible(boolean b) {
        // nothing here, move on

    }

    public void setCompareFileNames(String incomingJudgeOutputFileName, String incomingTeamOutputFileName) {
        // nothing here, move on

    }

    public void enableCompareButton(boolean value) {
        // nothing here, move on

    }

}
