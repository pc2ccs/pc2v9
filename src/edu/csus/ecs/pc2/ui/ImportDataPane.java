package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import edu.csus.ecs.pc2.core.ContestImporter;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
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
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(494, 242));
        this.add(getImportButton(), gridBagConstraints);
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
            filename = selectFileName(lastDirectory);

        } catch (IOException e) {
            logException("Problem selecting filename", e);
            showMessage("Problem selecting filename " + e.getMessage());
        }

        if (filename == null) {
            showMessage("No file selected");
        } else {

            if (filename.endsWith("contest.yaml")) {

                checkAndLoadYAML(filename);

            } else {
                showMessage("Please select a context.yaml file");
            }

        }
    }

    private void checkAndLoadYAML(String filename) {

        getController().getLog().info("Loading contest.yaml from " + filename);

        String directoryName = new File(filename).getParent();

        try {
            IInternalContest newContest = loader.fromYaml(null, directoryName);

            String contestSummary = getSummary(newContest);

            int result = FrameUtilities.yesNoCancelDialog(this, "Import" + NL + contestSummary, "Import Contest Settings");

            if (result != JOptionPane.YES_OPTION) {
                showMessage("No import done");
                return;
            }

            new ContestImporter().sendContestSettingsToServer(getController(), newContest);
            
            showMessage("All contest settings sent to server" + NL + contestSummary);

        } catch (Exception e) {

            logException("Unable to load contest YAML from " + filename, e);
            e.printStackTrace();
            showMessage("Problem loading file(s) check log" + e.getMessage());
        }
    }

  
    private String getSummary(IInternalContest newContest) throws Exception {

        Language[] languages = newContest.getLanguages();
        Problem[] problems = newContest.getProblems();
        Account[] accounts = newContest.getAccounts();
        Site [] sites = newContest.getSites();

        StringBuffer sb = new StringBuffer();

        if (sites.length > 0) {
            sb.append(sites.length);
            sb.append(" sites");
            sb.append(NL);
        }

        if (problems.length > 0) {
            sb.append(problems.length);
            sb.append(" problems");
            sb.append(NL);
        }

        if (languages.length > 0) {
            sb.append(languages.length);
            sb.append(" languages");
            sb.append(NL);
        }

        if (accounts.length > 0) {
            sb.append(accounts.length);
            sb.append(" accounts");
            sb.append(NL);
        }

        return sb.toString();

    }

    private void showMessage(String string) {
        JOptionPane.showMessageDialog(this, string, "Message", JOptionPane.INFORMATION_MESSAGE);
    }

    private String selectFileName(String dirname) throws IOException {

        String chosenFile = null;

        JFileChooser chooser = new JFileChooser(dirname);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            lastDirectory = chooser.getCurrentDirectory().toString();
            chosenFile = chooser.getSelectedFile().getCanonicalFile().toString();
            return chosenFile;
        } else {
            return null;
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
