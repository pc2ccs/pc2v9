package edu.csus.ecs.pc2.ui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.report.IReport;

/**
 * Base class for UIPlugin panes.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public abstract class JPanePlugin extends JPanel implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 3600350449535614012L;

    private IInternalController controller;

    private IInternalContest contest;
    
    private JFrame parentFrame = null;

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.controller = inController;
        this.contest = inContest;
    }
    

    public abstract String getPluginTitle();

    public IInternalController getController() {
        return controller;
    }

    public void setController(IInternalController controller) {
        this.controller = controller;
    }

    public IInternalContest getContest() {
        return contest;
    }

    public void setContest(IInternalContest contest) {
        this.contest = contest;
    }


    public JFrame getParentFrame() {
        return parentFrame;
    }


    public void setParentFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }
    
    /**
     * Get filename based on the report title.
     *  
     * @param selectedReport
     * @return
     */
    private String getFileName(IReport selectedReport) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.SSS");
        // "yyMMdd HHmmss.SSS");
        String reportName = selectedReport.getReportTitle();

        while (reportName.indexOf(' ') > -1) {
            reportName = reportName.replace(" ", "_");
        }
        return "report." + reportName + "." + simpleDateFormat.format(new Date()) + ".txt";

    }

    /**
     * Create and view via GUI the report file.
     * 
     * @param report
     * @param log
     * @throws IOException
     */
    public void createAndViewReportFile(IReport report, Log log) throws IOException {
        createAndViewReportFile(report, null, null, log);
    }

    /**
     * Create and view via GUI the report file.
     * 
     * @param report
     * @param filename
     * @param inFilter
     * @param log
     * @throws IOException
     */
    public void createAndViewReportFile(IReport report, String filename, Filter inFilter, Log log) throws IOException {

        ReportPane reportPane = new ReportPane();
        reportPane.setContestAndController(contest, controller);

        reportPane.createReportFile(report, filename, inFilter);

        if (filename == null) {
            filename = contest.getStorage().getDirectoryName() + File.separator + getFileName(report);
        }

        String title = report.getReportTitle();

        MultipleFileViewer multipleFileViewer = new MultipleFileViewer(log);
        multipleFileViewer.addFilePane(title, filename);
        multipleFileViewer.setTitle("PC^2 Report (Build " + new VersionInfo().getBuildNumber() + ")");
        FrameUtilities.centerFrameFullScreenHeight(multipleFileViewer);
        multipleFileViewer.setVisible(true);
    }
}
