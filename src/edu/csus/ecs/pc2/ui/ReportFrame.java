package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Report Frame.s
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ReportFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 4445138164153522572L;

    private ReportPane reportPane = null;

    /**
     * This method initializes
     * 
     */
    public ReportFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(496, 230));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle("InternalContest Reports");
        this.setContentPane(getReportPane());

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        reportPane.setContestAndController(inContest, inController);
    }

    public String getPluginTitle() {
        return "Report Frame";
    }

    /**
     * This method initializes reportPane
     * 
     * @return edu.csus.ecs.pc2.ui.ReportPane
     */
    private ReportPane getReportPane() {
        if (reportPane == null) {
            reportPane = new ReportPane();
        }
        return reportPane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
