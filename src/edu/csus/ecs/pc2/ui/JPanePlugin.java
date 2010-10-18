package edu.csus.ecs.pc2.ui;

import java.io.File;
import java.io.IOException;

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

    /**
     * Create and view via GUI the report file.
     * 
     * @param report
     * @param filename
     * @param inFilter
     * @param log
     * @throws IOException
     */
    public void createAndViewReportFile(IReport report, Filter filter, Log log) throws IOException {

        ReportPane reportPane = new ReportPane();
        reportPane.setContestAndController(contest, controller);

        String filename = reportPane.getFileName(report, "txt");
        
        File reportDirectoryFile = new File(reportPane.getReportDirectory());
        if (reportDirectoryFile.exists()) {
            if (reportDirectoryFile.isDirectory()) {
                filename = reportDirectoryFile.getCanonicalPath() + File.separator + filename;
            }
        } else {
            if (reportDirectoryFile.mkdirs()) {
                filename = reportDirectoryFile.getCanonicalPath() + File.separator + filename;
            }
        }

        reportPane.createReportFile(report, filename, filter);

        String title = report.getReportTitle();

        MultipleFileViewer multipleFileViewer = new MultipleFileViewer(log);
        multipleFileViewer.addFilePane(title, filename);
        multipleFileViewer.setTitle("PC^2 Report (Build " + new VersionInfo().getBuildNumber() + ")");
        FrameUtilities.centerFrameFullScreenHeight(multipleFileViewer);
        multipleFileViewer.setVisible(true);
    }
    
    public void logException(String message, Exception ex){
        controller.getLog().log(Log.WARNING, message,ex);
    }
}
