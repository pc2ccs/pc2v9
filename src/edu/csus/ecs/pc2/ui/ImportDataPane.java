package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import edu.csus.ecs.pc2.api.exceptions.LoadContestDataException;
import edu.csus.ecs.pc2.core.ContestImporter;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestComparison;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.report.ProblemsReport;
import edu.csus.ecs.pc2.imports.ccs.ContestYAMLLoader;
import edu.csus.ecs.pc2.ui.FrameUtilities.HorizontalPosition;
import edu.csus.ecs.pc2.ui.FrameUtilities.VerticalPosition;

/**
 * Import YAML and other data into contest.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ImportDataPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 8507451908248919433L;

    private JButton importButton = null;

    private String lastDirectory = null;

    private ContestYAMLLoader loader = new ContestYAMLLoader();  //  @jve:decl-index=0:

    private static final String NL = System.getProperty("line.separator");

    private JPanel buttonPane = null;

    private JButton importPasswordsButton = null;

    private JPanel centerPane = null;

    private ReportFrame reportFrame = null;

    /**
     * This method initializes
     * 
     */
    public ImportDataPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(494, 242));
        this.add(getButtonPane(), BorderLayout.SOUTH);
        this.add(getCenterPane(), BorderLayout.CENTER);
    }

    @Override
    public String getPluginTitle() {
        return "Import Data Pane";
    }

    /**
     * This method initializes importButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getImportButton() {
        if (importButton == null) {
            importButton = new JButton();
            importButton.setText("Import contest.yaml");
            importButton.setToolTipText("Import Contest and Problem YAML");
            importButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    selectAndImportContestConfiguration();
                }
            });
        }
        return importButton;
    }

    protected void selectAndImportContestConfiguration() {

        String filename = null;

        try {
            filename = selectYamlFileName(lastDirectory);

        } catch (IOException e) {
            logException("Problem selecting filename", e);
            showMessage("Problem selecting filename " + e.getMessage());
        }

        if (filename != null) {
            if (filename.endsWith("contest.yaml")) {

                try {
                    checkAndLoadYAML(filename);
                } catch (Exception e) {
                    logException("Error loading contest.yaml", e);
                    showMessage("Error loading contest.yaml "+e.getMessage());
                }
                
            } else {
                showMessage("Please select a contest.yaml file");
            }

        }
    }

    protected void showReportFrame(IInternalContest inContest) {
        if (reportFrame == null) {
            reportFrame  = new ReportFrame();
            reportFrame.setContestAndController(inContest, getController());
        }
        FrameUtilities.setFramePosition(reportFrame, HorizontalPosition.RIGHT, VerticalPosition.CENTER);
        reportFrame.setVisible(true);
    }
    
    private void checkAndLoadYAML(String filename) {

        getController().getLog().info("Loading contest.yaml from " + filename);

        String directoryName = new File(filename).getParent();
        
        IInternalContest newContest = null;
        String contestSummary = "";
        
        int result = JOptionPane.NO_OPTION;

        try {
            // TODO CCS figure out how to determine whether to load data file contents.
            boolean loadDataFileContents = true;

            String value = IniFile.getValue("server.externalfiles");
            if (value != null && value.equalsIgnoreCase("yes")) {
                loadDataFileContents = false;
            }

            newContest = loader.fromYaml(null, directoryName, loadDataFileContents);

//            contestSummary = new ContestComparison().getContestLoadSummary(newContest);
            contestSummary = new ContestComparison().comparisonList(getContest(), newContest);

            if (Utilities.isDebugMode()) {
                
                Utilities.viewReport(new ProblemsReport(), "Title: " + newContest.getTitle(), newContest, getController());

                // FrameUtilities.showMessage(null, "New Comparison information", new ContestComparison().comparisonList(getContest(), newContest));
//                showReportFrame(newContest);
            }

            result = FrameUtilities.yesNoCancelDialog(this, "Import" + NL + contestSummary, "Import Contest Settings");

        } catch (Exception e) {
            logException("Unable to load contest YAML from " + filename, e);
            showMessage("Problem loading file(s), check log.  " + e.getMessage());
        }
        
        if (result != JOptionPane.YES_OPTION) {
            getLog().info("No YAML file selected to load");
            return;
        }

        if (newContest != null) {
            ContestImporter contestImporter = new ContestImporter();
            try {
                contestImporter.sendContestSettingsToServer(getController(), getContest(), newContest);
            } catch (LoadContestDataException e) {
                logException("LoadContestDataException for " + filename, e);
                logNoteList(contestImporter.getNoteList());
                showMessage("Problem loading contest data file(s) - " + e.getMessage());
            }
        }
        
        showMessage("All contest settings sent to server" + NL + contestSummary);
        
    }


    private void showMessage(String string) {
        JOptionPane.showMessageDialog(this, string, "Message", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public File selectYAMLFileDialog(Component parent, String startDirectory) {

        JFileChooser chooser = new JFileChooser(startDirectory);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
//        FileFilter filterXML = new FileNameExtensionFilter( "XML document (*.xml)", "xml");
//        chooser.addChoosableFileFilter(filterXML);
        
        FileFilter filterYAML = new FileNameExtensionFilter( "YAML document (*.yaml)", "yaml");
        chooser.addChoosableFileFilter(filterYAML);
        
        chooser.setAcceptAllFileFilterUsed(false);
        // bug 759 java7 requires us to select it, otherwise the default choice would be empty
        chooser.setFileFilter(filterYAML);
        
        int action = chooser.showOpenDialog(parent);

        switch (action) {
            case JFileChooser.APPROVE_OPTION:
                File file = chooser.getSelectedFile();
                lastDirectory = chooser.getCurrentDirectory().toString();
                return file;
            case JFileChooser.CANCEL_OPTION:
            case JFileChooser.ERROR_OPTION:
            default:
                break;
        }
        return null;

    }
    
    public File selectTextFileDialog (Component parent, String startDirectory) {

        JFileChooser chooser = new JFileChooser(startDirectory);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        FileFilter filterText = new FileNameExtensionFilter( "Text document (*.txt)", "txt");
        chooser.addChoosableFileFilter(filterText);
        
        chooser.setAcceptAllFileFilterUsed(false);
        // bug 759 java7 requires us to select it, otherwise the default choice would be empty
        chooser.setFileFilter(filterText);
        
        int action = chooser.showOpenDialog(parent);

        switch (action) {
            case JFileChooser.APPROVE_OPTION:
                File file = chooser.getSelectedFile();
                lastDirectory = chooser.getCurrentDirectory().toString();
                return file;
            case JFileChooser.CANCEL_OPTION:
            case JFileChooser.ERROR_OPTION:
            default:
                break;
        }
        return null;

    }

    private String selectTextFileName(String dirname) throws IOException {

        String chosenFile = null;
        File file = selectTextFileDialog(this, lastDirectory);
        if (file != null) {
            chosenFile = file.getCanonicalFile().toString();
            return chosenFile;
        } else {
            return null;
        }
    }
    
    private String selectYamlFileName(String dirname) throws IOException {

        String chosenFile = null;
        File file = selectYAMLFileDialog(this, lastDirectory);
        if (file != null) {
            chosenFile = file.getCanonicalFile().toString();
            return chosenFile;
        } else {
            return null;
        }
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(35);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.add(getImportButton(), null);
            buttonPane.add(getImportPasswordsButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes importPasswordsButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getImportPasswordsButton() {
        if (importPasswordsButton == null) {
            importPasswordsButton = new JButton();
         importPasswordsButton.setText("Import Passwords");
            importPasswordsButton.setToolTipText("Import passwords.txt");
            importPasswordsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    selectAndImportPasswordsFile();
                }
            });
        }
        return importPasswordsButton;
    }

    protected void selectAndImportPasswordsFile() {

        String filename = null;

        try {
            filename = selectTextFileName(lastDirectory);

        } catch (IOException e) {
            logException("Problem selecting filename", e);
            showMessage("Problem selecting filename " + e.getMessage());
        }

        if (filename != null) {
            if (filename.endsWith("passwords.txt")) {

                try {
                    checkAndLoadPasswordsFile(filename);
                } catch (FileNotFoundException fnfe) {
                    logException("File not found loading passwords.txt ", fnfe);
                    showMessage("File not found " + fnfe.getMessage());
                } catch (Exception e) {
                    logException("Error loading passwords.txt", e);
                    showMessage("Error loading passwords.txt " + e.getMessage());
                }
                
            } else {
                showMessage("Please select a passwords.txt file");
            }

        }
        
    }

    private void checkAndLoadPasswordsFile(String filename) throws Exception {

        String[] lines;
        try {
            lines = Utilities.loadFile(filename);
        } catch (IOException e) {
            throw new FileNotFoundException(filename);
        }

        if (lines.length < 1) {
            throw new FileNotFoundException(filename);
        } else {

            int numberOfPasswords = lines.length;
            Vector<Account> accounts = getContest().getAccounts(Type.TEAM, getContest().getSiteNumber());
            int numberOfTeams = accounts.size();

            if (numberOfPasswords > numberOfTeams) {
                throw new Exception("Too few accounts, expecting " + numberOfPasswords + " accounts, found " + numberOfTeams);
            } else {

                int result = FrameUtilities.yesNoCancelDialog(this, "Update " + numberOfPasswords + " teams passwords?", "Confirm update");

                if (result == JOptionPane.YES_OPTION) {

                    Account[] teams = (Account[]) accounts.toArray(new Account[accounts.size()]);
                    Arrays.sort(teams, new AccountComparator());
                    ArrayList<Account> accountList = new ArrayList<Account>();

                    for (int i = 0; i < lines.length; i++) {
                        teams[i].setPassword(lines[i]);
                        accountList.add(teams[i]);
                    }

                    Account[] changedAccounts = (Account[]) accountList.toArray(new Account[accountList.size()]);

                    getController().updateAccounts(changedAccounts);
                }

            }
        }
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            centerPane = new JPanel();
            centerPane.setLayout(new GridBagLayout());
        }
        return centerPane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
