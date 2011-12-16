package edu.csus.ecs.pc2.ui;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Non-GUI message saver.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AutoJudgeNotifyMesssageImpl implements AutoJudgeNotifyMessages, UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -631243055810352484L;

    private Log log;

    public void updateStatusLabel(String string) {
        updateMessage(string);
    }

    public void updateMessage(String string) {
        System.out.println(string);
        log.info(string);
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        log = inController.getLog();
    }

    public String getPluginTitle() {
        return "Non-GUI Auto Judge message handler";
    }

}
