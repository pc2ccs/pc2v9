// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.FileUtilities;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.execute.ExecuteUtilities;
import edu.csus.ecs.pc2.core.imports.clics.FileComparison;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.report.FileComparisonUtilities;
import edu.csus.ecs.pc2.core.report.ResultsCompareReport;
import edu.csus.ecs.pc2.core.report.ResultsExportReport;
import edu.csus.ecs.pc2.exports.ccs.ResultsFile;
import java.awt.FlowLayout;

/**
 * Results epxort and compare pane.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class ResultsExportComparePane extends JPanePlugin {

    private static final long serialVersionUID = -2726716271169661000L;

    private JTextField pc2ResultsDirectoryTextField;

    private JTextField primaryCCSResultsDirectoryTextField;

    private JLabel exportDirectoryLabel;

    private JLabel primaryCCSresultsDirectoryLabel;

    public ResultsExportComparePane() {
        setBorder(new TitledBorder(null, "Export and Compare Contest Results", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        setLayout(new BorderLayout(0, 0));

        JPanel centerPane = new JPanel();
        add(centerPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        centerPane.add(panel);

        exportDirectoryLabel = new JLabel("Export Directory");
        exportDirectoryLabel.setToolTipText("");
        panel.add(exportDirectoryLabel);

        pc2ResultsDirectoryTextField = new JTextField();
        pc2ResultsDirectoryTextField.setToolTipText("export pc2 results files directory ");
        panel.add(pc2ResultsDirectoryTextField);
        pc2ResultsDirectoryTextField.setColumns(40);

        JButton selectExportDirectoryButton = new JButton("...");
        panel.add(selectExportDirectoryButton);
        selectExportDirectoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectUpdateExportDirectory(pc2ResultsDirectoryTextField);
            }
        });
        selectExportDirectoryButton.setToolTipText("Select export Directory");

        primaryCCSResultsDirectoryTextField = new JTextField();
        primaryCCSResultsDirectoryTextField.setColumns(40);

        JPanel primaryDirPane = new JPanel();
        centerPane.add(primaryDirPane);

        primaryCCSresultsDirectoryLabel = new JLabel("Result compare to directory");
        primaryCCSresultsDirectoryLabel.setToolTipText("Pimary CCS results directory");
        primaryDirPane.add(primaryCCSresultsDirectoryLabel);

        JButton selectPrimaryCCSResultsDirectoryButton = new JButton("...");
        primaryDirPane.add(selectPrimaryCCSResultsDirectoryButton);
        selectPrimaryCCSResultsDirectoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                selectAndUpdatePrimaryCCSResultsDirectory(primaryCCSResultsDirectoryTextField);
            }
        });
        selectPrimaryCCSResultsDirectoryButton.setToolTipText("Select primary contest results directory");

        JPanel buttonPane = new JPanel();
        buttonPane.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        FlowLayout flowLayout = (FlowLayout) buttonPane.getLayout();
        flowLayout.setHgap(55);
        centerPane.add(buttonPane);

        JButton exportResultsButton = new JButton("Export Results");
        exportResultsButton.setToolTipText("Export Results files to pc2 results directory");
        buttonPane.add(exportResultsButton);

        JButton compartResultsButton = new JButton("Compare Result");
        buttonPane.add(compartResultsButton);
        compartResultsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                exportAndCompare();

            }
        });
        compartResultsButton.setToolTipText("Compare pc2 results files to primary CCS results");
        exportResultsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportResults();

            }
        });
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
        pc2ResultsDirectoryTextField.setText(exportDir);
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

        String pc2ResultsDirectory = pc2ResultsDirectoryTextField.getText();

        String primaryCCSDirectory = primaryCCSresultsDirectoryLabel.getText();

        if (showErrorMessage("Enter a pc2 results directory", StringUtilities.isEmpty(pc2ResultsDirectory))) {
            return;
        }

        if (showErrorMessage("Enter a primary CCS results directory", StringUtilities.isEmpty(primaryCCSDirectory))) {
            return;
        }

        if (showErrorMessage("Primary CCS directory does not exist, pick an existing results directory", !directoryExists(primaryCCSDirectory))) {
            return;
        }

        if (!directoryExists(pc2ResultsDirectory)) {

            int result = FrameUtilities.yesNoCancelDialog(this, "pc2 results directory does not exist, create it?", "Create results directory");
            if (result != JOptionPane.YES_OPTION) {
                return;
            }

            ExecuteUtilities.ensureDirectory(pc2ResultsDirectory);
        }
        
        // huh

        try {
            // Export pc2 results
            String filename = Utilities.createReport(new ResultsExportReport(), getContest(), getController(), true);

            // compare primary and pc2 rsults
            ResultsCompareReport report = new ResultsCompareReport();
            Utilities.viewReport(report, "Results Coparison", getContest(), getController(), true);

            String sourceDir = ""; // TODO 760 assign source dir
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

    private boolean directoryExists(String dirname) {
        // TODO REFACTOR move this to a utility class
        if (StringUtilities.isEmpty(dirname)){
            return false;
        }
        
        return new File(dirname).isDirectory();
    }

    /**
     * Show message if showMessage is true.
     * 
     * @param message
     *            describe what to do
     * @param showMessage
     *            should the mesage be shown
     * 
     * @return the input condition specified by showMessage
     */
    private boolean showErrorMessage(String message, boolean showMessage) {
        if (showMessage) {
            showMessage(this, "Correct error, then try again", message);
        }
        return showMessage;
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
