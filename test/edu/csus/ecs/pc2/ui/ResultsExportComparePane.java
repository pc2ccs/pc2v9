// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.report.ExportFilesUtiltiites;
import edu.csus.ecs.pc2.core.report.ResultsCompareReport;
import edu.csus.ecs.pc2.exports.ccs.ResultsFile;

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

    private JLabel resultsPassFailLabel;

    private JPanel southPane = new JPanel();

    public ResultsExportComparePane() {
        setBorder(new TitledBorder(null, "Export and Compare Contest Results", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        setLayout(new BorderLayout(0, 0));

        JPanel centerPane = new JPanel();
        add(centerPane, BorderLayout.CENTER);
        JPanel primarySourcePanel = new JPanel();
        centerPane.add(primarySourcePanel);

        primaryCCSResultsDirectoryTextField = new JTextField();
        primaryCCSResultsDirectoryTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {

                    System.out.println("Save ccs results dir"); // TODO 760 debugging
                    updateUserSetting(ClientSettings.PRIMARY_CCS_RESULTS_DIR, pc2ResultsDirectoryTextField.getText());
                }
            }
        });
        primaryCCSResultsDirectoryTextField.setColumns(40);
        
        JLabel prmaryCCSLabel = new JLabel("Primary CCS Results Directory");
        primarySourcePanel.add(prmaryCCSLabel);
        primarySourcePanel.add(primaryCCSResultsDirectoryTextField);

        JButton selectPrimaryCCSResultsDirectoryButton = new JButton("...");
        primarySourcePanel.add(selectPrimaryCCSResultsDirectoryButton);
        selectPrimaryCCSResultsDirectoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                selectAndUpdatePrimaryCCSResultsDirectory(primaryCCSResultsDirectoryTextField);
            }
        });
        selectPrimaryCCSResultsDirectoryButton.setToolTipText("Select primary contest results directory");

        JPanel pc2SourcePanel = new JPanel();
        centerPane.add(pc2SourcePanel);

        exportDirectoryLabel = new JLabel("pc2 Results Directory");
        exportDirectoryLabel.setToolTipText("");
        pc2SourcePanel.add(exportDirectoryLabel);

        pc2ResultsDirectoryTextField = new JTextField();
        pc2ResultsDirectoryTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {

                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    System.out.println("Save pc2 results dir"); // TODO 760 debugging
                    updateUserSetting(ClientSettings.PC2_RESULTS_DIR, pc2ResultsDirectoryTextField.getText());
                }

            }
        });
        pc2ResultsDirectoryTextField.setToolTipText("export pc2 results files directory ");
        pc2ResultsDirectoryTextField.setColumns(40);
        pc2SourcePanel.add(pc2ResultsDirectoryTextField);

        JButton selectExportDirectoryButton = new JButton("...");
        pc2SourcePanel.add(selectExportDirectoryButton);
        selectExportDirectoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectUpdateExportDirectory(pc2ResultsDirectoryTextField);
            }
        });
        selectExportDirectoryButton.setToolTipText("Select export Directory");

        JPanel buttonPane = new JPanel();
        buttonPane.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        FlowLayout flowLayout = (FlowLayout) buttonPane.getLayout();
        flowLayout.setHgap(55);
        centerPane.add(buttonPane);
        
        JPanel resultsPane = new JPanel();
        centerPane.add(resultsPane);
        
        JLabel resultsWereLabel = new JLabel("Summary");
        resultsPane.add(resultsWereLabel);
        
        resultsPassFailLabel = new JLabel("-");
        resultsPassFailLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        resultsPane.add(resultsPassFailLabel);
        FlowLayout flowLayout_1 = (FlowLayout) southPane.getLayout();
        flowLayout_1.setHgap(45);
        
        southPane.setSize(30,30);
        add(southPane, BorderLayout.SOUTH);

        JButton exportResultsButton = new JButton("Export Results");
        southPane.add(exportResultsButton);
        exportResultsButton.setToolTipText("Export Results files to pc2 results directory");

        JButton compartResultsButton = new JButton("Compare Result");
        southPane.add(compartResultsButton);
        compartResultsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                compareFiles();

            }
        });
        compartResultsButton.setToolTipText("Compare pc2 results files to primary CCS results");
        exportResultsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportpc2ResultsFiles();

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
    
    private void updateUserSetting(String key, String value) {

        try {
            updateClientSettings(key, value);
        } catch (Exception e) {
            e.printStackTrace(); // TODO 760 error handle this better
        }

    }
    

    protected void selectAndUpdatePrimaryCCSResultsDirectory(JTextField textField) {

        String initDirectory = textField.getText();

        try {
            File newFile = FileUtilities.selectDirectory(initDirectory, textField, exportDirectoryLabel, "Select primary CCS results directory");
            updateUserSetting(ClientSettings.PRIMARY_CCS_RESULTS_DIR, newFile.getCanonicalFile().toString());
            textField.setText(newFile.getAbsolutePath());
            textField.setToolTipText(newFile.getAbsolutePath());

        } catch (Exception e) {
            getLog().log(Level.WARNING, "Exception trying to select or update primary CCS dir" + e.getMessage(), e);
            showMessage(this, "Problem with primary results dir", "Problem selecting or updating primary CCS results dir " + e.getMessage());
        }

    }

    protected void selectUpdateExportDirectory(JTextField textField) {

        String initDirectory = textField.getText();

        try {
            File newFile = FileUtilities.selectDirectory(initDirectory, textField, exportDirectoryLabel, "Select pc2 results directory");
            updateUserSetting(ClientSettings.PC2_RESULTS_DIR, newFile.getCanonicalFile().toString());

        } catch (Exception e) {
            getLog().log(Level.WARNING, "Exception trying to select or pc2 results dir" + e.getMessage(), e);
            showMessage(this, "Problem with primary results dir", "Problem selecting or updating pc2 results dir " + e.getMessage());
        }

    }

    /**
     * Create/Export pc2 results files
     * 
     * @return true if files created, else false.
     */
    protected boolean exportpc2ResultsFiles() {
        
        String pc2ResultsDirectory = pc2ResultsDirectoryTextField.getText();
        
        File pc2ResultsDir = new File(pc2ResultsDirectory); 
        
        if (! pc2ResultsDir.isDirectory()) {
            
            int result = FrameUtilities.yesNoCancelDialog(this, "pc2 results directory does not exist, create it?", "Create results directory");
            if (result == JOptionPane.YES_OPTION) {
                
                ExecuteUtilities.ensureDirectory(pc2ResultsDirectory);
                
                pc2ResultsDir = new File(pc2ResultsDirectory);
                if (! pc2ResultsDir.isDirectory()) {
                    
                    showMessage(this, "Cannot create directory", "Cannot create results dir: "+pc2ResultsDirectory);
                    return false;
                }
            }  else {
                return false;
            }
        }
        
        String targetResulsTSFFile = pc2ResultsDirectory + File.separator + ResultsFile.RESULTS_FILENAME;
        
        File resultsFile = new File(targetResulsTSFFile);
        
        if (resultsFile.exists()) {
            int result = FrameUtilities.yesNoCancelDialog(this, "pc2 results files exist, overwrite pc2 results files?", "Overwrite pc2 results files");
            if (result != JOptionPane.YES_OPTION) {
                return false;
            }
        }
        
        try {
             String[] filesCreated = ExportFilesUtiltiites.writeResultsFiles(getContest(), pc2ResultsDirectory);
             
             List<String> messageLines = new ArrayList<String>();
             messageLines.add(    "Created results files");
             for (String name : filesCreated) {
                messageLines.add(name);
            }
             
             String message = String.join(Constants.NL, messageLines);
             showMessage(this, "Files created", message);
             
             return true;
             
        } catch (Exception e) {
            // TODO REFACTOR present error message and stacktrace to user with to be written standard error dialog
            getController().getLog().log(Level.WARNING,"Unable to create export files to "+pc2ResultsDirectory+" "+e.getMessage(), e);
            showErrorMessage("Unable to create export files to "+pc2ResultsDirectory+" "+e.getMessage(), true);
        }
        
        return false;
    }

    protected void compareFiles() {

        String pc2ResultsDirectory = pc2ResultsDirectoryTextField.getText();

        String primaryCCSDirectory = primaryCCSResultsDirectoryTextField.getText();

        if (showErrorMessage("Enter a primary CCS results directory", StringUtilities.isEmpty(primaryCCSDirectory))) {
            return;
        }

        if (showErrorMessage("Primary CCS directory does not exist, pick an existing results directory", !directoryExists(primaryCCSDirectory))) {
            return;
        }

        if (showErrorMessage("Enter a pc2 results directory", StringUtilities.isEmpty(pc2ResultsDirectory))) {
            return;
        }
        
        if (showErrorMessage("Missing pc2 directory - use Export Results to create results files", !directoryExists(pc2ResultsDirectory))) {
            return;
        }

        try {

            String cdpResultsDirectory = null;
            ResultsCompareReport report = new ResultsCompareReport(getContest(), getController(), pc2ResultsDirectory, primaryCCSDirectory);
            Utilities.viewReport(report, report.getPluginTitle(), getContest(), getController(), true);

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
     * Update user settings (sent to server)
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
