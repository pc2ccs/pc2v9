// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.FileUtilities;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.imports.clics.FileComparison;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.report.FileComparisonUtilities;
import edu.csus.ecs.pc2.core.report.ResultsCompareReport;
import edu.csus.ecs.pc2.core.report.ResultsExportReport;
import edu.csus.ecs.pc2.exports.ccs.ResultsFile;

/**
 * Results epxort and compare pane. 
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class ResultsExportComparePane extends JPanePlugin {

    private static final long serialVersionUID = -2726716271169661000L;

    private JTextField exportDirectoryTextField;

    private JTextField primaryCCSResultsDirectoryTextField;

    private JLabel exportDirectoryLabel;

    private JLabel primaryCCSresultsDirectoryLabel;

    public ResultsExportComparePane() {
        setBorder(new TitledBorder(null, "Export and Compare Contest Results", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        setLayout(new BorderLayout(0, 0));

        JPanel centerPane = new JPanel();
        add(centerPane, BorderLayout.CENTER);

        exportDirectoryLabel = new JLabel("Export Directory");
        centerPane.add(exportDirectoryLabel);

        exportDirectoryTextField = new JTextField();
        centerPane.add(exportDirectoryTextField);
        exportDirectoryTextField.setColumns(40);

        JButton selectExportDirectoryButton = new JButton("...");
        selectExportDirectoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectUpdateExportDirectory(exportDirectoryTextField);
            }
        });
        selectExportDirectoryButton.setToolTipText("Select export Directory");
        centerPane.add(selectExportDirectoryButton);

        primaryCCSresultsDirectoryLabel = new JLabel("Result compare to directory");
        centerPane.add(primaryCCSresultsDirectoryLabel);

        primaryCCSResultsDirectoryTextField = new JTextField();
        primaryCCSResultsDirectoryTextField.setColumns(40);

        JButton selectPrimaryCCSResultsDirectoryButton = new JButton("...");
        selectPrimaryCCSResultsDirectoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                selectAndUpdatePrimaryCCSResultsDirectory(primaryCCSResultsDirectoryTextField);
            }
        });
        selectPrimaryCCSResultsDirectoryButton.setToolTipText("Select results directory to compare with");
        centerPane.add(selectPrimaryCCSResultsDirectoryButton);

        JButton exportResultsButton = new JButton("Export Results");
        exportResultsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportResults();

            }
        });
        centerPane.add(exportResultsButton);

        JButton compartResultsButton = new JButton("Compare Result");
        compartResultsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                exportAndCompare();

            }
        });
        compartResultsButton.setToolTipText("Create Report to compare results");
        centerPane.add(compartResultsButton);
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI();
            }
        });
    }

    protected void populateGUI() {

        ClientSettings clientSet = getClientSettings(getContest());
        String exportDir = clientSet.getProperty(ClientSettings.PC2_RESULTS_DIR);
        exportDirectoryTextField.setText(exportDir);
        exportDirectoryLabel.setToolTipText(exportDir);

        String primaryResultsDir = clientSet.getProperty(ClientSettings.PRIMARY_CCS_RESULTS_DIR);
        primaryCCSResultsDirectoryTextField.setText(primaryResultsDir);

        // primaryCCSResultsDirectoryTextField
    }

    protected void selectAndUpdatePrimaryCCSResultsDirectory(JTextField textField) {

        String initDirectory = textField.getText();

        try {
            File newFile = FileUtilities.selectDirectory(initDirectory, textField, exportDirectoryLabel, "Select primary CCS results directory");
            updateClientSettings(ClientSettings.PRIMARY_CCS_RESULTS_DIR, newFile.getCanonicalFile().toString());

        } catch (Exception e) {
            getLog().log(Level.WARNING, "Exception trying to select or update primary CCS dir" + e.getMessage(), e);
            showMessage(this, "Problem with primary results dir", "Problem selecting or updating primary CCS results dir " + e.getMessage());
        }

    }

    protected void selectUpdateExportDirectory(JTextField textField) {

        String initDirectory = textField.getText();

        try {
            File newFile = FileUtilities.selectDirectory(initDirectory, textField, exportDirectoryLabel, "Select pc2 results directory");
            updateClientSettings(ClientSettings.PC2_RESULTS_DIR, newFile.getCanonicalFile().toString());

        } catch (Exception e) {
            getLog().log(Level.WARNING, "Exception trying to select or pc2 results dir" + e.getMessage(), e);
            showMessage(this, "Problem with primary results dir", "Problem selecting or updating pc2 results dir " + e.getMessage());
        }

    }

    protected void exportResults() {
        ResultsExportReport report = new ResultsExportReport();
        Utilities.viewReport(report, "Results Export Fies Report", getContest(), getController(), true);

    }

    protected void exportAndCompare() {

        try {
            // Export pc2 results
            String filename = Utilities. createReport(new ResultsExportReport()    , getContest(), getController(), true);
            
            // compare primary and pc2 rsults
            ResultsCompareReport report = new ResultsCompareReport();
            Utilities.viewReport(report, "Results Coparison", getContest(), getController(), true);
            
            String sourceDir = "" ; // TODO 760 assign source dir
            String targetDir = ""; // TODO 760 assign traget dir

            String compareMessage = "Comparison Summary:   FAILED - no such directory (cdp directory not set) " + targetDir;

            FileComparison resultsCompare = FileComparisonUtilities.createTSVFileComparison(ResultsFile.RESULTS_FILENAME, sourceDir, targetDir);
            FileComparison awardsFileCompare = FileComparisonUtilities.createJSONFileComparison(Constants.AWARDS_JSON_FILENAME, sourceDir, targetDir);
            FileComparison scoreboardJsonCompare = FileComparisonUtilities.createJSONFileComparison(Constants.SCOREBOARD_JSON_FILENAME, sourceDir, targetDir);

            // TODO 760 Update GUi with comparison information
            
        } catch (Exception e) {
            getLog().log(Level.WARNING, "Exception trying export and compare results" + e.getMessage(), e);
            showMessage(this, "Problem comparing results", "Error writing or comparing results " + e.getMessage());
        }
    }

    @Override
    public String getPluginTitle() {
        return "Results Export and Compare";
    }

    private ClientSettings getClientSettings(IInternalContest inContest) {
        ClientSettings clientSettings = inContest.getClientSettings(inContest.getClientId());
        if (clientSettings == null) {
            clientSettings = new ClientSettings(inContest.getClientId());
        }
        return clientSettings;
    }

    /**
     * Update settings (sent to server)
     * 
     * @param newClientSettings
     * @param name
     * @param value
     */
    private void updateClientSettings(String name, String value) {
        ClientSettings clientSettings = getClientSettings(getContest());
        clientSettings.put(name, value);
        getController().updateClientSettings(clientSettings);
    }

}
