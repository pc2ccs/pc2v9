package edu.csus.ecs.pc2.core.model;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.JPanePlugin;
import edu.csus.ecs.pc2.ui.MultipleFileViewer;

/**
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class ReportPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -1206899817603554760L;

    private JPanel jPanel = null;

    private JPanel buttonPane = null;

    private JPanel mainPane = null;

    private JButton viewReportButton = null;

    private JCheckBox thisSiteCheckBox = null;

    private JPanel reportChoicePane = null;

    private JComboBox reportsComboBox = null;

    private JLabel messageLabel = null;

    private IReport[] listOfReports;

    private Log log;

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
        this.setSize(new java.awt.Dimension(505, 251));
        this.add(getJPanel(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getMainPane(), java.awt.BorderLayout.CENTER);

        // populate list of reports
        listOfReports = new IReport[6];
        listOfReports[0] = new RunsReport();
        listOfReports[1] = new ClarificationsReport();
        listOfReports[2] = new ProblemsReport();
        listOfReports[3] = new InternalDumpReport();
        listOfReports[4] = new AccountPermissionReport();
        listOfReports[5] = new StandingsReport();
    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);

        this.log = getController().getLog();
        refreshGUI();
    }

    protected void refreshGUI() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                refreshReportComboBox();
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
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jPanel = new JPanel();
            jPanel.setLayout(new BorderLayout());
            jPanel.setPreferredSize(new java.awt.Dimension(30, 30));
            jPanel.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return jPanel;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            buttonPane = new JPanel();
            buttonPane.setPreferredSize(new java.awt.Dimension(45, 45));
            buttonPane.add(getViewReportButton(), null);
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
            mainPane.add(getThisSiteCheckBox(), null);
            mainPane.add(getReportChoicePane(), null);
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
            viewReportButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    generateSelectedReport();
                }
            });
        }
        return viewReportButton;
    }

    private String getFileName() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.SSS");
        // "yyMMdd HHmmss.SSS");
        return "report." + simpleDateFormat.format(new Date()) + ".txt";

    }
    
    private void viewFile (String filename){
        MultipleFileViewer multipleFileViewer = new MultipleFileViewer(log);
        multipleFileViewer.addFilePane("Internal Dump", filename);
        FrameUtilities.centerFrameFullScreenHeight(multipleFileViewer);
        multipleFileViewer.setVisible(true);
    }

    protected void generateSelectedReport() {

        // TODO code populate filter.
        Filter filter = new Filter();
        filter.setSiteNumber(getContest().getSiteNumber());
        filter.setThisSiteOnly(getThisSiteCheckBox().isSelected());

        try {
            
            String filename = getFileName();

            IReport selectedReport = null;
            
            String selectedReportTitle = (String) getReportsComboBox().getSelectedItem();
            for (IReport report : listOfReports) {
                if (selectedReportTitle.equals(report.getReportTitle())) {
                    selectedReport = report;
                }
            }
            
            selectedReport.setContestAndController(getContest(), getController());
            selectedReport.createReportFile(filename, filter);
            viewFile (filename);

        } catch (Exception e) {
            // TODO: log handle exception
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to output report, check logs");
        }

    }

    /**
     * show message to user
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
    private JCheckBox getThisSiteCheckBox() {
        if (thisSiteCheckBox == null) {
            thisSiteCheckBox = new JCheckBox();
            thisSiteCheckBox.setBounds(new java.awt.Rectangle(30, 87, 165, 21));
            thisSiteCheckBox.setText("Filter for this site only");
        }
        return thisSiteCheckBox;
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
            reportChoicePane.setBounds(new java.awt.Rectangle(31, 16, 445, 53));
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
    private JComboBox getReportsComboBox() {
        if (reportsComboBox == null) {
            reportsComboBox = new JComboBox();
        }
        return reportsComboBox;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
