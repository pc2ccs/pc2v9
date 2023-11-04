// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.FileUtilities;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.execute.ExecuteUtilities;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.report.ExportFilesUtiltiites;
import edu.csus.ecs.pc2.core.report.ResultsCompareReport;
import edu.csus.ecs.pc2.exports.ccs.ResultsFile;

/**
 * Results export and compare pane.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class ResultsComparePane extends JPanePlugin {

    private static final String SHOW_COMPARISON_BUTTON_TITLE = "Show Comparison";

    private static final String MISSING_TARGET_TITLE = "Missing from Parimary";

    private static final String MISSING_SOURCE_TITLE = "MIssing from PC2";

    private static final long serialVersionUID = -2726716271169661000L;

    private JTextField pc2ResultsDirectoryTextField;

    private JTextField primaryCCSResultsDirectoryTextField;

    private JLabel exportDirectoryLabel;

    private JPanel southPane = new JPanel();

    private JCheckBox showDetailsCheckbox = null;
    
    private JTextArea textArea = new JTextArea();
    
    public ResultsComparePane() {
        setBorder(new TitledBorder(null, "Export and Compare Contest Results", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        setLayout(new BorderLayout(0, 0));

        JPanel centerPane = new JPanel();
        add(centerPane, BorderLayout.CENTER);
        JPanel primarySourcePanel = new JPanel();
        centerPane.add(primarySourcePanel);

        primaryCCSResultsDirectoryTextField = new JTextField();
        primaryCCSResultsDirectoryTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    updateUserSetting(ClientSettings.PRIMARY_CCS_RESULTS_DIR, pc2ResultsDirectoryTextField.getText());
                }
            }
        });
        primaryCCSResultsDirectoryTextField.setColumns(40);
        
        JLabel prmaryCCSLabel = new JLabel("Primary CCS Results Directory");
        primarySourcePanel.add(prmaryCCSLabel);
        primarySourcePanel.add(primaryCCSResultsDirectoryTextField);

        JButton selectPrimaryCCSResultsDirectoryButton = new JButton("Select...");
        primarySourcePanel.add(selectPrimaryCCSResultsDirectoryButton);
        selectPrimaryCCSResultsDirectoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                selectAndUpdatePrimaryCCSResultsDirectory(primaryCCSResultsDirectoryTextField);
            }
        });
        selectPrimaryCCSResultsDirectoryButton.setToolTipText("Select primary contest results directory");

        JPanel pc2SourcePanel = new JPanel();
        centerPane.add(pc2SourcePanel);

        exportDirectoryLabel = new JLabel("PC2 Results Directory");
        exportDirectoryLabel.setToolTipText("");
        pc2SourcePanel.add(exportDirectoryLabel);

        pc2ResultsDirectoryTextField = new JTextField();
        pc2ResultsDirectoryTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    updateUserSetting(ClientSettings.PC2_RESULTS_DIR, pc2ResultsDirectoryTextField.getText());
                }

            }
        });
        pc2ResultsDirectoryTextField.setToolTipText("export pc2 results files directory ");
        pc2ResultsDirectoryTextField.setColumns(40);
        pc2SourcePanel.add(pc2ResultsDirectoryTextField);

        JButton selectExportDirectoryButton = new JButton("Select...");
        pc2SourcePanel.add(selectExportDirectoryButton);
        selectExportDirectoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectUpdateExportDirectory(pc2ResultsDirectoryTextField);
            }
        });
        selectExportDirectoryButton.setToolTipText("Select export Directory");
        
        JPanel summaryResultsPane = new JPanel();
        summaryResultsPane.setPreferredSize(new Dimension(800, 300));
        summaryResultsPane.setMinimumSize(new Dimension(300, 300));
        summaryResultsPane.setBorder(new TitledBorder(null, "Compare Results Summary", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        centerPane.add(summaryResultsPane);
        summaryResultsPane.setLayout(new BorderLayout(0, 0));
        
        JPanel resCenterPane = new JPanel();
        summaryResultsPane.add(resCenterPane, BorderLayout.CENTER);
        resCenterPane.setLayout(new BorderLayout(0, 0));
        textArea.setFont(new Font("Courier New", Font.PLAIN, 13));
        
        
//        resCenterPane.add(textArea);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        resCenterPane.add(scrollPane, BorderLayout.CENTER);
        
        JPanel resButtonPane = new JPanel();
        FlowLayout flowLayout = (FlowLayout) resButtonPane.getLayout();
        flowLayout.setHgap(45);
        summaryResultsPane.add(resButtonPane, BorderLayout.SOUTH);
        
        JButton viewReportButton = new JButton("View Report");
        viewReportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewCompareResultsFiles();
            }
        });
        resButtonPane.add(viewReportButton);
        
        
        showDetailsCheckbox = new JCheckBox("Include details in comparison");
        resButtonPane.add(showDetailsCheckbox);
        showDetailsCheckbox.setToolTipText("Show full detils in comparison");
        FlowLayout flowLayout_1 = (FlowLayout) southPane.getLayout();
        flowLayout_1.setHgap(45);
        
        southPane.setSize(30,30);
        add(southPane, BorderLayout.SOUTH);

        JButton exportResultsButton = new JButton("Export Results");
        southPane.add(exportResultsButton);
        exportResultsButton.setToolTipText("Export Results files to pc2 results directory");

        JButton compartResultsButton = new JButton(SHOW_COMPARISON_BUTTON_TITLE);
        southPane.add(compartResultsButton);
        compartResultsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateSummary();
            }
        });
        compartResultsButton.setToolTipText("Compare pc2 results files to primary CCS results");
        exportResultsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportpc2ResultsFiles();

            }
        });
 
    }

    protected void updateSummary() {
        
        clearResults();
        
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
            
            ResultsCompareReport report = new ResultsCompareReport(getContest(), getController(), primaryCCSDirectory, pc2ResultsDirectory, showDetailsCheckbox.isSelected(), MISSING_SOURCE_TITLE, MISSING_TARGET_TITLE);
            String[] lines = report.createReport(new Filter());
            for (String string : lines) {
                addTextAreaLine(string);
            }

        } catch (Exception e) {
            getLog().log(Level.WARNING, "Exception trying export and compare results" + e.getMessage(), e);
            showMessage(this, "Problem comparing results", "Error writing or comparing results " + e.getMessage());
        }
        
    }

    protected void clearResults() {
        textArea.setText(null);
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
        
        textArea.removeAll();
        
        addTextAreaLine("Click "+SHOW_COMPARISON_BUTTON_TITLE+" to show comparison");
    }
    
    private void addTextAreaLine(String string) {
        textArea.append(string + Constants.NL);
    }

    /**
     * Update user settings, send to server.
     *  
     * @param key
     * @param value
     */
    private void updateUserSetting(String key, String value) {

        try {
            updateClientSettings(key, value);
        } catch (Exception e) {
            Utilities.printStackTrace(System.out, e);
            StaticLog.getLog().log(Level.WARNING, "Problem updating settings ", e);
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
            
            int result = FrameUtilities.yesNoCancelDialog(this, "pc2 results directory does not exist, create dir"+pc2ResultsDirectory+"?" , "Create results directory");
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

    protected void viewCompareResultsFiles() {

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
            
            ResultsCompareReport report = new ResultsCompareReport(getContest(), getController(), primaryCCSDirectory, pc2ResultsDirectory, showDetailsCheckbox.isSelected(), MISSING_SOURCE_TITLE, MISSING_TARGET_TITLE);
            Utilities.viewReport(report, report.getPluginTitle(), getContest(), getController(), true);
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
