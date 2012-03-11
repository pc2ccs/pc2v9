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
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Category;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.PlaybackInfo;
import edu.csus.ecs.pc2.core.model.Pluralize;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.imports.ccs.ContestYAMLLoader;

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

    private ContestYAMLLoader loader = new ContestYAMLLoader();

    private static final String NL = System.getProperty("line.separator");

    private JPanel buttonPane = null;

    private JButton importPasswordsButton = null;

    private JPanel centerPane = null;

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

        if (filename == null) {
            showMessage("No file selected");
        } else {

            if (filename.endsWith("contest.yaml")) {

                try {
                    checkAndLoadYAML(filename);
                } catch (Exception e) {
                    logException("Error loading contest.xml", e);
                    showMessage("Error loading contest.xml "+e.getMessage());
                }
                
            } else {
                showMessage("Please select a context.yaml file");
            }

        }
    }

    private void checkAndLoadYAML(String filename) {

        getController().getLog().info("Loading contest.yaml from " + filename);

        String directoryName = new File(filename).getParent();
        
        IInternalContest newContest = null;
        String contestSummary = "";
        
        int result = JOptionPane.NO_OPTION;

        try {
             newContest = loader.fromYaml(null, directoryName);
             
             contestSummary = getContestLoadSummary(newContest, getController());
             
             result = FrameUtilities.yesNoCancelDialog(this, "Import" + NL + contestSummary, "Import Contest Settings");
   
        } catch (Exception e) {
            logException("Unable to load contest YAML from " + filename, e);
            e.printStackTrace();
            showMessage("Problem loading file(s), check log.  " + e.getMessage());
        }
        
        if (result != JOptionPane.YES_OPTION) {
            showMessage("No import done");
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

    private static void addSummaryEntry(StringBuffer buf, int count, String prefix, String entryName) {
        if (count > 0) {
            buf.append(count);
            String pluralized = Pluralize.pluralize(entryName, count);
            if (prefix.length() > 0) {
                buf.append(' ');
                buf.append(prefix);
            }
            buf.append(' ');
            buf.append(pluralized);
            buf.append(NL);
        }
    }

    private static void addSummaryEntry(StringBuffer sb, int count, String entryName) {
        addSummaryEntry(sb, count, "", entryName);
    }


    public static String getContestLoadSummary(IInternalContest newContest, IInternalController inController) throws Exception {

        // TODO debug 22 replace getContestLoadSummary with ContestSummaryFrame
//        ContestSummaryFrame frame = new ContestSummaryFrame();
//        frame.setContestAndController(newContest, inController);
//        frame.setVisible(true);
       
        Language[] languages = newContest.getLanguages();
        Problem[] problems = newContest.getProblems();
        Site[] sites = newContest.getSites();
        Category[] categories = newContest.getCategories();
        ClientSettings [] settings = newContest.getClientSettingsList();
        
        PlaybackInfo info = getPlaybackInfo(newContest);

        StringBuffer sb = new StringBuffer();

        addSummaryEntry(sb, sites.length, "site");

        addSummaryEntry(sb, problems.length, "problem");
        
        if (problems.length > 0) {
            int ansCount = 0;
            int datCount = 0;
            
            for (Problem problem : problems) {
                ProblemDataFiles pdfiles = newContest.getProblemDataFile(problem);
                if (pdfiles != null) {
                    ansCount += pdfiles.getJudgesAnswerFiles().length;
                    datCount += pdfiles.getJudgesDataFiles().length;
//                } else { // nothing to do here, move on
                }
            }
            addSummaryEntry(sb, datCount, "input data files");
            addSummaryEntry(sb, ansCount, "answer data files");
        }

        addSummaryEntry(sb, languages.length, "language");

        for (Type type : Type.values()) {
            Vector<Account> accounts = newContest.getAccounts(type);
            String accountTypeName = type.toString().toLowerCase();
            addSummaryEntry(sb, accounts.size(), accountTypeName, " account");
        }

        addSummaryEntry(sb, categories.length, "clar", "category");

        addSummaryEntry(sb, settings.length, "AJ setting");
        
        if (info != null) {
            sb.append("1 replay defined, auto started? "+info.isStarted());
            sb.append(NL);
            sb.append("   file: "+info.getFilename());
            sb.append(NL);
            sb.append(info.toString());
            sb.append(NL);
        }

        return sb.toString();
    }
    
    private static PlaybackInfo getPlaybackInfo(IInternalContest newContest) {
        
        PlaybackInfo [] infos = newContest.getPlaybackInfos();
        if (infos.length > 0) {
            return infos[0];
        } else {
            return null;
        }
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

        if (filename == null) {
            showMessage("No file selected");
        } else {

            if (filename.endsWith("passwords.txt")) {

                try {
                    checkAndLoadPasswordsFile(filename);
                } catch (FileNotFoundException fnfe) {
                    fnfe.printStackTrace(); // TODO remove this, debug 22
                    logException("File not found loading passwords.txt ", fnfe);
                    showMessage("File not found " + fnfe.getMessage());
                } catch (Exception e) {
                    e.printStackTrace(); // TODO remove this, debug 22
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
