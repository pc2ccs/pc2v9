package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.imports.ContestXML;
import edu.csus.ecs.pc2.core.list.ReportNameByComparator;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.report.AccountPermissionReport;
import edu.csus.ecs.pc2.core.report.AccountsReport;
import edu.csus.ecs.pc2.core.report.AccountsTSVReport;
import edu.csus.ecs.pc2.core.report.AccountsTSVReportTeamAndJudges;
import edu.csus.ecs.pc2.core.report.AllReports;
import edu.csus.ecs.pc2.core.report.BalloonDeliveryReport;
import edu.csus.ecs.pc2.core.report.BalloonSettingsReport;
import edu.csus.ecs.pc2.core.report.BalloonSummaryReport;
import edu.csus.ecs.pc2.core.report.CategoryReport;
import edu.csus.ecs.pc2.core.report.ClarificationsReport;
import edu.csus.ecs.pc2.core.report.ClientSettingsReport;
import edu.csus.ecs.pc2.core.report.ContestAnalysisReport;
import edu.csus.ecs.pc2.core.report.ContestReport;
import edu.csus.ecs.pc2.core.report.ContestSettingsReport;
import edu.csus.ecs.pc2.core.report.ContestSummaryReports;
import edu.csus.ecs.pc2.core.report.EvaluationReport;
import edu.csus.ecs.pc2.core.report.EventFeed2013Report;
import edu.csus.ecs.pc2.core.report.EventFeedReport;
import edu.csus.ecs.pc2.core.report.ExportYamlReport;
import edu.csus.ecs.pc2.core.report.ExtractPlaybackLoadFilesReport;
import edu.csus.ecs.pc2.core.report.FastestSolvedReport;
import edu.csus.ecs.pc2.core.report.FastestSolvedSummaryReport;
import edu.csus.ecs.pc2.core.report.FinalizeReport;
import edu.csus.ecs.pc2.core.report.GroupsReport;
import edu.csus.ecs.pc2.core.report.GroupsTSVReport;
import edu.csus.ecs.pc2.core.report.HTMLReport;
import edu.csus.ecs.pc2.core.report.IReport;
import edu.csus.ecs.pc2.core.report.IReportFile;
import edu.csus.ecs.pc2.core.report.InternalDumpReport;
import edu.csus.ecs.pc2.core.report.JSONReport;
import edu.csus.ecs.pc2.core.report.JudgementNotificationsReport;
import edu.csus.ecs.pc2.core.report.JudgementReport;
import edu.csus.ecs.pc2.core.report.LanguagesReport;
import edu.csus.ecs.pc2.core.report.ListRunLanguages;
import edu.csus.ecs.pc2.core.report.LoginReport;
import edu.csus.ecs.pc2.core.report.NotificationsReport;
import edu.csus.ecs.pc2.core.report.OldRunsReport;
import edu.csus.ecs.pc2.core.report.PasswordsReport;
import edu.csus.ecs.pc2.core.report.PlaybackDumpReport;
import edu.csus.ecs.pc2.core.report.PluginsReport;
import edu.csus.ecs.pc2.core.report.ProblemsReport;
import edu.csus.ecs.pc2.core.report.ProfileCloneSettingsReport;
import edu.csus.ecs.pc2.core.report.ProfilesReport;
import edu.csus.ecs.pc2.core.report.ResolverEventFeedReport;
import edu.csus.ecs.pc2.core.report.RunJudgementNotificationsReport;
import edu.csus.ecs.pc2.core.report.RunStatisticsReport;
import edu.csus.ecs.pc2.core.report.RunsByTeamReport;
import edu.csus.ecs.pc2.core.report.RunsReport;
import edu.csus.ecs.pc2.core.report.RunsReport5;
import edu.csus.ecs.pc2.core.report.SubmissionsTSVReport;
import edu.csus.ecs.pc2.core.report.ScoreboardTSVReport;
import edu.csus.ecs.pc2.core.report.SitesReport;
import edu.csus.ecs.pc2.core.report.SolutionsByProblemReport;
import edu.csus.ecs.pc2.core.report.StandingsReport;
import edu.csus.ecs.pc2.core.report.TeamsTSVReport;
import edu.csus.ecs.pc2.core.report.UserdataTSVReport;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.Permission.Type;
import edu.csus.ecs.pc2.core.util.IMemento;
import edu.csus.ecs.pc2.core.util.XMLMemento;
import edu.csus.ecs.pc2.ui.EditFilterPane.ListNames;

/**
 * Report Pane, allows picking and viewing reports.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ReportPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -5165297328068331675L;

    private JPanel topPane = null;

    private JPanel buttonPane = null;

    private JPanel mainPane = null;

    private JButton viewReportButton = null;

    private JCheckBox breakdownBySiteCheckbox = null;

    private JPanel reportChoicePane = null;

    private JComboBox<String> reportsComboBox = null;

    private JLabel messageLabel = null;

    /**
     * List of reports.
     */
    private IReport[] listOfReports;

    private Log log;

    private String reportDirectory = "reports";  //  @jve:decl-index=0:

    private JCheckBox thisClientFilterButton = null;

    private JPanel filterPane = null;

    private JPanel filterButtonPane = null;

    private JButton editReportFilter = null;

    private JLabel filterLabel = null;

    private Filter filter = new Filter();
    
    private EditFilterFrame editFilterFrame = null;

    private JCheckBox xmlOutputCheckbox = null;

    private JButton generateSummaryButton = null;
    
    public String getReportDirectory() {
        return reportDirectory;
    }

    /**
     * This method can change the directory that the reports will be written to. The default is "reports".
     * 
     * @param reportDirectory
     *            what directory to write the reports to
     */
    public void setReportDirectory(String reportDirectory) {
        this.reportDirectory = reportDirectory;
    }

    /**
     * This method initializes
     * 
     */
    public ReportPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(525, 291));
        this.add(getTopPane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getMainPane(), java.awt.BorderLayout.CENTER);

        // populate list of reports
        Vector <IReport> reports = new Vector <IReport> ();
        
        reports.add(new AccountsReport());
        reports.add(new BalloonSummaryReport());
        
        reports.add(new EventFeedReport());
        reports.add(new EventFeed2013Report());
        reports.add(new NotificationsReport());

        reports.add(new AllReports());
        reports.add(new ContestSettingsReport());
        reports.add(new ContestReport());

        reports.add(new ContestAnalysisReport());
        reports.add(new SolutionsByProblemReport());
        reports.add(new ListRunLanguages());
        
        reports.add(new FastestSolvedSummaryReport());
        reports.add(new FastestSolvedReport());

        reports.add(new StandingsReport());
        reports.add(new LoginReport());
        reports.add(new ProfilesReport());
        reports.add(new PluginsReport());
        
        reports.add(new RunsReport());
        reports.add(new ClarificationsReport());
        reports.add(new ProblemsReport());
        reports.add(new LanguagesReport());

        reports.add(new JudgementReport());
        reports.add(new RunsByTeamReport());
        reports.add(new BalloonSettingsReport());
        reports.add(new ClientSettingsReport());
        reports.add(new GroupsReport());

        reports.add(new EvaluationReport());

        reports.add(new OldRunsReport());
        reports.add(new RunsReport5());

        reports.add(new AccountPermissionReport());
        reports.add(new BalloonDeliveryReport());
        reports.add(new ExtractPlaybackLoadFilesReport());
        
        reports.add(new RunJudgementNotificationsReport());
        reports.add(new JudgementNotificationsReport());
        
        reports.add(new ProfileCloneSettingsReport());
        reports.add(new SitesReport());
        
        reports.add(new FinalizeReport());
        
        reports.add(new InternalDumpReport());
        
        reports.add(new HTMLReport());
        
        reports.add(new ExportYamlReport());
        
        reports.add(new CategoryReport());
        
        reports.add(new RunStatisticsReport());

        reports.add(new PlaybackDumpReport());
        
        reports.add(new PasswordsReport());
        
        reports.add(new AccountsTSVReportTeamAndJudges());
        
        reports.add(new AccountsTSVReport());
        
        reports.add(new SubmissionsTSVReport());

        reports.add(new JSONReport());
        
        reports.add(new UserdataTSVReport());
        
        reports.add(new GroupsTSVReport());
        
        reports.add(new TeamsTSVReport());
        
        reports.add(new ScoreboardTSVReport());
        
        reports.add(new ResolverEventFeedReport());
        
        listOfReports = (IReport[]) reports.toArray(new IReport[reports.size()]);
        Arrays.sort(listOfReports, new ReportNameByComparator());
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        this.log = getController().getLog();
        
        if (isServer()) {
            String reportDir = getReportDirectory();
            setReportDirectory(inContest.getProfile().getProfilePath() + File.separator + reportDir);
        }
        
        getContest().addAccountListener(new AccountListenerImplementation());
        
        initializePermissions();
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getEditFilterFrame().setContestAndController(getContest(), getController());
                refreshGUI();
                updateGUIperPermissions();
            }
        });
        
    }
    
    private void updateGUIperPermissions() {
        generateSummaryButton.setEnabled(isAllowed(Permission.Type.EDIT_ACCOUNT));
        viewReportButton.setEnabled(isAllowed(Permission.Type.EDIT_ACCOUNT));
    }
    
    private boolean isServer() {
        return getContest().getClientId() != null && isServer(getContest().getClientId());
    }
    
    private boolean isServer(ClientId clientId) {
        return clientId.getClientType().equals(ClientType.Type.SERVER);
    }

    protected void refreshGUI() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                refreshReportComboBox();
                showXMLCheckbox();
            }
        });
    }

    private void refreshReportComboBox() {

        getReportsComboBox().removeAllItems();

        for (IReport report : listOfReports) {
            getReportsComboBox().addItem(report.getReportTitle());
        }

        getReportsComboBox().setSelectedIndex(0);

    }

    @Override
    public String getPluginTitle() {
        return "Reports Pane";
    }

    /**
     * This method initializes topPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTopPane() {
        if (topPane == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            topPane = new JPanel();
            topPane.setLayout(new BorderLayout());
            topPane.setPreferredSize(new java.awt.Dimension(30, 30));
            topPane.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return topPane;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(45);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.setPreferredSize(new java.awt.Dimension(45, 45));
            buttonPane.add(getViewReportButton(), null);
            buttonPane.add(getGenerateSummaryButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes mainPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPane() {
        if (mainPane == null) {
            mainPane = new JPanel();
            mainPane.setLayout(null);
            mainPane.add(getBreakdownBySiteCheckbox(), null);
            mainPane.add(getReportChoicePane(), null);
            mainPane.add(getThisClientFilterButton(), null);
            mainPane.add(getFilterPane(), null);
            mainPane.add(getXmlOutputCheckbox(), null);
        }
        return mainPane;
    }

    /**
     * This method initializes viewReportButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getViewReportButton() {
        if (viewReportButton == null) {
            viewReportButton = new JButton();
            viewReportButton.setText("View Report");
            viewReportButton.setToolTipText("View the selected Report");
            viewReportButton.setMnemonic(java.awt.event.KeyEvent.VK_V);
            viewReportButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (getBreakdownBySiteCheckbox().isSelected()){
                        generateSelectedReportBySite();
                    } else {
                        generateSelectedReport();
                    }
                }
            });
        }
        return viewReportButton;
    }

    public String getFileName(IReport selectedReport, String extension) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.SSS");
        // "yyMMdd HHmmss.SSS");
        String reportName = selectedReport.getReportTitle();
        
        while (reportName.indexOf(' ') > -1){
            reportName = reportName.replace(" ", "_");
        }
        return "report."+ reportName+ "." + simpleDateFormat.format(new Date()) + "." + extension;

    }

    private void viewFile(String filename, String title) {
        MultipleFileViewer multipleFileViewer = new MultipleFileViewer(log);
        multipleFileViewer.addFilePane(title, filename);
        multipleFileViewer.setTitle("PC^2 Report (Build " + new VersionInfo().getBuildNumber() + ")");
        FrameUtilities.centerFrameFullScreenHeight(multipleFileViewer);
        multipleFileViewer.setVisible(true);
    }
    
    protected void createHeader(PrintWriter printWriter, IReport report){
        /**
         * Create/write standardized header for all reports
         */

        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        ContestInformation contestInformation = getContest().getContestInformation();
        String contestTitle = "(Contest title not defined)";
        if (contestInformation != null) {
            contestTitle = contestInformation.getContestTitle();
        }
        printWriter.println("Contest Title: " + contestTitle);
        printWriter.print("On: " + Utilities.getL10nDateTime());
        
        GregorianCalendar resumeTime = null;
        if (getContest() != null) {
            resumeTime = getContest().getContestTime().getResumeTime();
        }
        if (resumeTime == null) {
            printWriter.print("  Contest date/time: never started");
        } else {
            printWriter.print("  Contest date/time: " + resumeTime.getTime());

        }
        
        printWriter.println();
        Profile profile = null;
        if (getContest() != null) {
            profile = getContest().getProfile();
        }
      
        if (profile != null) {
            printWriter.println("Profile: " + profile.getName() + " (" + profile.getDescription() + ")");
        } else {
            printWriter.println("Profile: none defined");
        }
        
        printWriter.println();
        printWriter.println();
        printWriter.println("** " + report.getReportTitle() + " Report");
        printWriter.println();
        if (filter != null) {
            String filterInfo = filter.toString();
            if (filter.isFilterOn() && (!filterInfo.equals(""))) {
                printWriter.println("Filter: " + filterInfo);
                printWriter.println();
            }
        }
 
    }
    
    public void createReportFile(IReport report, boolean suppressHeaderFooter, String filename, Filter inFilter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);
        filter = inFilter;

        try {

            if ( ! suppressHeaderFooter) {
                createHeader(printWriter, report);
            }

            try {
                if (report instanceof ExtractPlaybackLoadFilesReport){
                    ((ExtractPlaybackLoadFilesReport) report).setReportFilename(filename);
                    ((ExtractPlaybackLoadFilesReport) report).setReportDirectory(getReportDirectory());
                }
                report.writeReport(printWriter);
            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
            }

            if ( ! suppressHeaderFooter) {
                report.printFooter(printWriter);
            }

            printWriter.close();
            printWriter = null;

        } catch (Exception e) {
            log.log(Log.INFO, "Exception creating report", e);
            printWriter.println("Exception creating report " + e.getMessage());
        }
    }

    protected void generateSelectedReport() {

        try {

            FrameUtilities.waitCursor(this);
            
            showXMLCheckbox();
            
            IReport selectedReport = null;

            String selectedReportTitle = (String) getReportsComboBox().getSelectedItem();
            for (IReport report : listOfReports) {
                if (selectedReportTitle.equals(report.getReportTitle())) {
                    selectedReport = report;
                }
            }
            
            boolean writeXML = getXmlOutputCheckbox().isSelected();
            
            String extension = "txt";
            if ( writeXML ) {
                extension = "xml";
            }
            String filename = getFileName(selectedReport, extension);
            
            File reportDirectoryFile = new File(getReportDirectory());
            if (reportDirectoryFile.exists()) {
                if (reportDirectoryFile.isDirectory()) {
                    filename = reportDirectoryFile.getCanonicalPath() + File.separator + filename;
                }
            } else {
                if (reportDirectoryFile.mkdirs()) {
                    filename = reportDirectoryFile.getCanonicalPath() + File.separator + filename;
                 }
            }

            selectedReport.setContestAndController(getContest(), getController());
            
            // SOMEDAY insure that each report createReportFile sets the filter too
            /**
             * Using setFilter because createReportFile may not set the filter
             */
            selectedReport.setFilter(filter);
            if (writeXML){
                createXMLFile(selectedReport, filename, filter);
            } else {
                boolean suppressHeaderFooter = false;
                if (selectedReport instanceof IReportFile) {
                    IReportFile reportFile = (IReportFile) selectedReport;
                    suppressHeaderFooter = reportFile.suppressHeaderFooter();
                }
                createReportFile(selectedReport, suppressHeaderFooter, filename, filter);
            }
            
            viewFile(filename, selectedReport.getReportTitle());

        } catch (Exception e) {
            log.log(Log.WARNING, "Exception outputting a report ", e);
            showMessage("Unable to output report, check logs "+e.getMessage());
    } finally {
        FrameUtilities.regularCursor(this);
    }

    }
    
    public String notImplementedXML (IReport report) throws IOException{
        
        ContestXML contestXML = new ContestXML();
        contestXML.setShowPasswords(getContest().isAllowed(Type.VIEW_PASSWORDS));

        XMLMemento mementoRoot = XMLMemento.createWriteRoot(ContestXML.CONTEST_TAG);

        IMemento memento = mementoRoot.createChild("message");
        memento.putString("name", "Not implemented");
        memento.putString("reportName", report.getReportTitle());
        
        contestXML.addVersionInfo (mementoRoot, getContest());
        
        contestXML.addFileInfo (mementoRoot);
        
        return mementoRoot.saveToString();
       
    }
    
    private void createXMLFile(IReport report, String filename, Filter inFilter) {
        
        PrintWriter printWriter = null;

        filter = inFilter;

        try {

            printWriter = new PrintWriter(new FileOutputStream(filename, false), true);

            try {
                
                String xmlString = report.createReportXML(inFilter);
                printWriter.println(xmlString);

            } catch (SecurityException e) {
                if (e.getMessage().equals("Not implemented")) {
                    printWriter.println(notImplementedXML(report));
                } else {
                    printWriter.println("Exception in report: " + e.getMessage());
                    e.printStackTrace(printWriter);
                }
                
            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
            }

            printWriter.close();
            printWriter = null;

        } catch (Exception e) {
            log.log(Log.INFO, "Exception creating report", e);
            printWriter.println("Exception creating report " + e.getMessage());
        }
        
    }

    private void showXMLCheckbox() {
        getXmlOutputCheckbox().setVisible(true);
    }

    /**
     * Generate the selected report for each site defined in the contest.
     *
     */
    protected void generateSelectedReportBySite() {

        try {

            FrameUtilities.waitCursor(this);
            
            IReport selectedReport = null;

            String selectedReportTitle = (String) getReportsComboBox().getSelectedItem();
            for (IReport report : listOfReports) {
                if (selectedReportTitle.equals(report.getReportTitle())) {
                    selectedReport = report;
                }
            }
            
            boolean writeXML = getXmlOutputCheckbox().isSelected();
            
            String extension = "txt";
            if ( writeXML ) {
                extension = "xml";
            }
            
            String filename = getFileName(selectedReport, extension);
            
            File reportDirectoryFile = new File(getReportDirectory());
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
            
            printWriter.println();
            printWriter.println(new VersionInfo().getSystemName());
            printWriter.println("Date: " + Utilities.getL10nDateTime());
            printWriter.println(new VersionInfo().getSystemVersionInfo());
            printWriter.println();
            printWriter.println("Report "+selectedReport.getReportTitle() + " Report ");
            printWriter.println();

            selectedReport.setContestAndController(getContest(), getController());
            
            Site[] sites = getContest().getSites();
            Arrays.sort(sites, new SiteComparatorBySiteNumber());
            for (Site site : sites) {
                Filter reportFitler = new Filter();
                try {
                    reportFitler.addSite(site);
                    selectedReport.setFilter(reportFitler);
                    printWriter.println();
                    printWriter.println("Report   "+selectedReport.getReportTitle() + " Report ");
                    printWriter.println("For site "+site.getSiteNumber()+" "+site.getDisplayName());
                    
                    selectedReport.writeReport(printWriter);
                    
                } catch (Exception e) {
                    printWriter.println("Exception in report: " + e.getMessage());
                    e.printStackTrace(printWriter);
                }
            }
            
            printWriter.println();
            printWriter.println("end report");
            
            viewFile(filename, selectedReport.getReportTitle());

        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to output report, check logs");
        } finally {
            FrameUtilities.regularCursor(this);
        }

    }

    /**
     * show message to user
     * 
     * @param string
     */
    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
            }
        });

    }

    /**
     * This method initializes thisSiteCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getBreakdownBySiteCheckbox() {
        if (breakdownBySiteCheckbox == null) {
            breakdownBySiteCheckbox = new JCheckBox();
            breakdownBySiteCheckbox.setBounds(new Rectangle(21, 80, 175, 21));
            breakdownBySiteCheckbox.setMnemonic(KeyEvent.VK_S);
            breakdownBySiteCheckbox.setToolTipText("Break down by site");
            breakdownBySiteCheckbox.setText("Breakdown by site");
            breakdownBySiteCheckbox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    changeSiteFiltering();
                }
            });
        }
        return breakdownBySiteCheckbox;
    }

    protected void changeSiteFiltering() {
//        if (getThisClientFilterButton().isSelected()){
//            filter.setFilterOn();
//            filter.setSiteNumber(getContest().getSiteNumber());
//            filter.setThisSiteOnly(true);
//        } else {
//            filter.setThisSiteOnly(false);
//        }
//        
//        refreshFilterLabel();
    }

    /**
     * This method initializes reportChoicePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getReportChoicePane() {
        if (reportChoicePane == null) {
            reportChoicePane = new JPanel();
            reportChoicePane.setLayout(new BorderLayout());
            reportChoicePane.setBounds(new Rectangle(22, 9, 482, 53));
            reportChoicePane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Reports", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            reportChoicePane.add(getReportsComboBox(), java.awt.BorderLayout.CENTER);
        }
        return reportChoicePane;
    }

    /**
     * This method initializes reportsComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox<String> getReportsComboBox() {
        if (reportsComboBox == null) {
            reportsComboBox = new JComboBox<String>();
            reportsComboBox.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        if (getBreakdownBySiteCheckbox().isSelected()){
                            generateSelectedReportBySite();
                        } else {
                            generateSelectedReport();
                        }
                    }
                }
            });
        }
        return reportsComboBox;
    }

    /**
     * This method initializes thisClientFilterButton
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getThisClientFilterButton() {
        if (thisClientFilterButton == null) {
            thisClientFilterButton = new JCheckBox();
            thisClientFilterButton.setBounds(new java.awt.Rectangle(21,114,192,21));
            thisClientFilterButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
            thisClientFilterButton.setText("Filter for this client only");
            thisClientFilterButton.setVisible(false);
            thisClientFilterButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    changeThisClientFiltering();
                }
            });
        }
        return thisClientFilterButton;
    }

    protected void changeThisClientFiltering() {
        if (thisClientFilterButton.isSelected()){
            filter.clearAccountList();

        } else {
            filter.setFilterOn();
            filter.clearAccountList();
            filter.addAccount(getContest().getClientId());
        }
        
        refreshFilterLabel();
    }

    /**
     * This method initializes filterPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getFilterPane() {
        if (filterPane == null) {
            filterLabel = new JLabel();
            filterLabel.setText("");
            filterLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            filterPane = new JPanel();
            filterPane.setLayout(new BorderLayout());
            filterPane.setBounds(new Rectangle(223, 76, 279, 128));
            filterPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filter", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
                    null, null));
            filterPane.add(getFilterButtonPane(), java.awt.BorderLayout.SOUTH);
            filterPane.add(filterLabel, java.awt.BorderLayout.CENTER);
        }
        return filterPane;
    }

    /**
     * This method initializes filterButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getFilterButtonPane() {
        if (filterButtonPane == null) {
            filterButtonPane = new JPanel();
            filterButtonPane.add(getEditReportFilter(), null);
        }
        return filterButtonPane;
    }

    /**
     * This method initializes editReportFilter
     * 
     * @return javax.swing.JButton
     */
    private JButton getEditReportFilter() {
        if (editReportFilter == null) {
            editReportFilter = new JButton();
            editReportFilter.setText("Edit Filter");
            editReportFilter.setMnemonic(KeyEvent.VK_F);
            editReportFilter.setToolTipText("Edit Filter");
            editReportFilter.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showReportFilter();
                }
            });
        }
        return editReportFilter;
    }

    protected void showReportFilter() {

        // Added in reverse order (right to left)
        getEditFilterFrame().addList(ListNames.LANGUAGES);
        getEditFilterFrame().addList(ListNames.PROBLEMS);
        getEditFilterFrame().addList(ListNames.TEAM_ACCOUNTS);
        getEditFilterFrame().addList(ListNames.RUN_STATES);
        getEditFilterFrame().addList(ListNames.JUDGEMENTS);
        getEditFilterFrame().addList(ListNames.SITES);

        getEditFilterFrame().setFilter(filter);
        getEditFilterFrame().validate();
        
        getEditFilterFrame().setVisible(true);
    }

    public EditFilterFrame getEditFilterFrame() {
        if (editFilterFrame == null){
            Runnable callback = new Runnable() {
                public void run() {
                    refreshFilterLabel();
                };
            };
            editFilterFrame = new EditFilterFrame(filter, "Report Filter", callback);
        }
        return editFilterFrame;
    }

    private void refreshFilterLabel() {
        filterLabel.setText(filter.toString());
    }

    /**
     * This method initializes xmlOutputCheckbox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getXmlOutputCheckbox() {
        if (xmlOutputCheckbox == null) {
            xmlOutputCheckbox = new JCheckBox();
            xmlOutputCheckbox.setBounds(new Rectangle(21, 118, 152, 21));
            xmlOutputCheckbox.setMnemonic(KeyEvent.VK_X);
            xmlOutputCheckbox.setText("XML Output");
        }
        return xmlOutputCheckbox;
    }

    /**
     * This method initializes generateSummaryButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getGenerateSummaryButton() {
        if (generateSummaryButton == null) {
            generateSummaryButton = new JButton();
            generateSummaryButton.setText("Generate Summary");
            generateSummaryButton.setMnemonic(KeyEvent.VK_G);
            generateSummaryButton.setToolTipText("Generate Summary Reports");
            generateSummaryButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    generateSummaryReport();
                }
            });
        }
        return generateSummaryButton;
    }

    protected void generateSummaryReport() {
        try {
            ContestSummaryReports contestReports = new ContestSummaryReports();
            contestReports.setContestAndController(getContest(), getController());
            contestReports.generateReports();
            System.out.println("Reports Generated to " + Utilities.getCurrentDirectory() + File.separator + contestReports.getReportDirectory());
            JOptionPane.showMessageDialog(this, "Reports Generated to "+contestReports.getReportDirectory());
        } catch (Exception e) {
            logException("Unable to produce reports", e);
            JOptionPane.showMessageDialog(this, "Unable to produce reports " + e.getMessage());
        }
    }

    /**
     * Account Listener Implementation.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            // ignored
        }

        public void accountModified(AccountEvent accountEvent) {
            // check if is this account
            Account account = accountEvent.getAccount();
            /**
             * If this is the account then update the GUI display per the potential change in Permissions.
             */
            if (getContest().getClientId().equals(account.getClientId())) {
                // They modified us!!
                initializePermissions();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateGUIperPermissions();
                    }
                });

            }
        }

        public void accountsAdded(AccountEvent accountEvent) {
            // ignore
        }

        public void accountsModified(AccountEvent accountEvent) {
            Account[] accounts = accountEvent.getAccounts();
            for (Account account : accounts) {

                /**
                 * If this is the account then update the GUI display per the potential change in Permissions.
                 */
                if (getContest().getClientId().equals(account.getClientId())) {
                    // They modified us!!
                    initializePermissions();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            updateGUIperPermissions();
                        }
                    });
                }
            }
        }

        public void accountsRefreshAll(AccountEvent accountEvent) {

            initializePermissions();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateGUIperPermissions();
                }
            });
        }
    }
   
    
} // @jve:decl-index=0:visual-constraint="10,10"
