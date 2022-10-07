// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.report.ContestCompareModel;
import edu.csus.ecs.pc2.core.report.ContestCompareReport;
import edu.csus.ecs.pc2.core.report.IReport;

/**
 * Contest EF Compare Pane.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class ContestEFComparePane extends JPanePlugin {
    
    
    // TODO REFACTOR move REPORTS_DIRECTORY_NAME  into AppConstants
    // TODO SOMEDAY update ReportsPane using REPORTS_DIRECTORY_NAME
    private static final String REPORTS_DIRECTORY_NAME = "reports";

    private JLabel refreshLabel = null;

    private JLabel topLable = null;

    private ContestCompareModel model = null;
    
    public ContestEFComparePane() {
        setLayout(new BorderLayout(0, 0));
        
        JPanel centerPanel = new JPanel();
        add(centerPanel);
        centerPanel.setLayout(new BorderLayout(0, 0));
        
        JPanel buttonPanel = new JPanel();
        FlowLayout fl_buttonPanel = (FlowLayout) buttonPanel.getLayout();
        fl_buttonPanel.setHgap(45);
        add(buttonPanel, BorderLayout.SOUTH);
        
        JButton reportButton = new JButton("Report");
        reportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showReport();
            }
        });
        reportButton.setToolTipText("Show Contest EF Compare Report");
        buttonPanel.add(reportButton);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                populate();
            }
        });
        refreshButton.setToolTipText("Refresh Compare Info");
        buttonPanel.add(refreshButton);
         
        refreshLabel = new JLabel("Click Refresh ");
        buttonPanel.add(refreshLabel);
        
        JPanel topPanel = new JPanel();
        topPanel.setSize(new Dimension(35, 35));
        topPanel.setPreferredSize(new Dimension(35, 35));
        add(topPanel, BorderLayout.NORTH);
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        topLable = new JLabel("TOP LABEL");
        topPanel.add(topLable);
    }

    protected void populate() {

        if (!isPrimaryCCSAPIDefined()) {
            showMessage(this, "Unable to show comparison", "Remote CCS Settings not defined");
        } else {

            FrameUtilities.waitCursor(this);

            try {

                String url = getContest().getContestInformation().getPrimaryCCS_URL();
                topLable.setText("Loading from " + url);
                refreshLabel.setText("Loading...");

                model = new ContestCompareModel(getContest());

                FrameUtilities.regularCursor(this);

                if (model.isMatch()) {
                    topLable.setText("Contest model matches " + url);
                } else {
                    int numDifferences = model.getNonMatchingComparisonRecords().size();
                    topLable.setText(numDifferences + " differences between contest model and " + url);
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E h:m:s a");
                String timeString = simpleDateFormat.format(new Date());

                refreshLabel.setText("Last refresh at " + timeString);

            } catch (Exception e) {
                FrameUtilities.regularCursor(this);
                e.printStackTrace(); // SOMEDAY remove this output to stdout
                getLog().log(Log.INFO.WARNING, "Unable to populate pane ContestEFComparePane", e);
                showMessage(this, "Unable to populate pane ContestEFComparePane", e.getMessage());
            }
        }
    }
    
    public String getFileName(IReport selectedReport, String extension) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.SSS");
        String reportName = selectedReport.getReportTitle();

        while (reportName.indexOf(' ') > -1) {
            reportName = reportName.replace(" ", "_");
        }
        return "report." + reportName + "." + simpleDateFormat.format(new Date()) + "." + extension;

    }

    protected void showReport() {
        
        if (!isPrimaryCCSAPIDefined()) {
            showMessage(this, "Unable to show report", "Remote CCS Settings not defined");
        } else {
            
            FrameUtilities.waitCursor(this);
            
            try {
                ContestCompareReport report = new ContestCompareReport();
                report.setContestAndController(getContest(), getController());
                
                String extension = "txt";
                String filename = getFileName(report, extension);
                
                File reportDirectoryFile = new File(REPORTS_DIRECTORY_NAME);
                if (reportDirectoryFile.exists()) {
                    if (reportDirectoryFile.isDirectory()) {
                        filename = reportDirectoryFile.getCanonicalPath() + File.separator + filename;
                    }
                } else {
                    if (reportDirectoryFile.mkdirs()) {
                        filename = reportDirectoryFile.getCanonicalPath() + File.separator + filename;
                    }
                }
                
                PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);
                report.writeReport(printWriter);
                printWriter.close();
                
                FrameUtilities.regularCursor(this);
                
                viewFile(filename, report.getReportTitle());
                
                
            } catch (Exception e) {
                FrameUtilities.regularCursor(this);
                e.printStackTrace(); // SOMEDAY remove this output to stdout
                getLog().log(Log.INFO.WARNING,"Unable to show ContestCompareReport", e);
                showMessage(this, "Unable to show report",  e.getMessage());
            }
        }
        
    }
    
    private void viewFile(String filename, String title) {
        // TODO REFACTOR move viewFile to FrameUtilities
        MultipleFileViewer multipleFileViewer = new MultipleFileViewer(getLog());
        multipleFileViewer.addFilePane(title, filename);
        multipleFileViewer.setTitle("PC^2 Report (Build " + new VersionInfo().getBuildNumber() + ")");
        FrameUtilities.centerFrameFullScreenHeight(multipleFileViewer);
        multipleFileViewer.setVisible(true);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 3069598004536364273L;

    @Override
    public String getPluginTitle() {
        return "Event Feed config compare";
    }
    
    /**
     * are priamry CCS connection settings defined?
     * @return true if Primary settings defined
     */
    private boolean isPrimaryCCSAPIDefined()
    {
        ContestInformation information = getContest().getContestInformation();
        return ( !StringUtilities.isEmpty(information.getPrimaryCCS_URL())) && //
                (!StringUtilities.isEmpty(information.getPrimaryCCS_user_login())) && //
                (!StringUtilities.isEmpty(information.getPrimaryCCS_user_pw()));
    }
}
