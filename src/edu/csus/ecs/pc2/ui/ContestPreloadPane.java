package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.ContestImporter;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.imports.ccs.ContestYAMLLoader;

/**
 * Contest load pre-configured contests pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestPreloadPane extends JPanePlugin {

    private static final long serialVersionUID = -8095817498502202588L;

    private JPanel buttonPanel = null;

    private MCLB contestsListbox = null;

    private JButton loadButton = null;
    
    private static final String NL = System.getProperty("line.separator");

    /**
     * This method initializes
     * 
     */
    public ContestPreloadPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(502, 207));
        this.add(getButtonPanel(), BorderLayout.SOUTH);
        this.add(getContestsListbox(), BorderLayout.CENTER);

    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        populateGUI();
    }

    private void populateGUI() {

        loadContestListBox();

    }
    

    /**
     * This method initializes contestsListbox
     * 
     * @return edu.csus.ecs.pc2.ui.MCLB
     */
    private MCLB getContestsListbox() {
        if (contestsListbox == null) {
            contestsListbox = new MCLB();

            String[] cols = { "Description", "Directory" };

            contestsListbox.addColumns(cols);
        }
        return contestsListbox;
    }

    private String[] buildContestRow(String contestFilename, String name) {
        
//        String[] cols = { "Description", "Directory" };
        String[] obj = new String[contestsListbox.getColumnCount()];
        
        ContestYAMLLoader loader = new ContestYAMLLoader();
        try {
            String description = loader.getContestTitle(contestFilename);

            obj[0] = description;
            obj[1] = name;

            return obj;
            
        } catch (Exception e) {
            return null;
        }
    }


    private void loadContestListBox() {

        String dirname = new VersionInfo().locateHome()+ File.separator + "samps" + File.separator + "contests";

        File dir = new File(dirname);

        if (dir.isDirectory()) {

            String[] filenames = dir.list();
            Arrays.sort(filenames);

            for (String name : filenames) {
                String contestFilename = dirname + File.separator + name + File.separator + "contest.yaml";
                if (new File(contestFilename).isFile()) {

                    String[] cols = buildContestRow(contestFilename, name);
                    updateContestRow(cols, contestFilename);
                }

            }

        } else {

            getLog().info("Could not load preconfigured contests from " + dirname);
            System.err.println("debug 22 -Could not load preconfigured contests from " + dirname);
        }

    }
    
    private void updateContestRow(String[] cols, String contestFilename) {

        if (cols == null || cols.length == 0) {
            getLog().info("No contest definition in file: " + contestFilename);
            return;
        }
        
        int row = contestsListbox.getIndexByKey(contestFilename);
        if (row == -1) {
            Object[] objects = cols;
            contestsListbox.addRow(objects, contestFilename);
        } else {
            Object[] objects = cols;
            contestsListbox.replaceRow(objects, row);
        }
        contestsListbox.autoSizeAllColumns();
    }

    private Log getLog() {
        return getController().getLog();
    }

    @Override
    public String getPluginTitle() {
        return "Contest Preconfigured Loader Pane";
    }

    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());
            buttonPanel.setPreferredSize(new Dimension(35, 35));
            buttonPanel.add(getLoadButton(), null);
        }
        return buttonPanel;
    }


    /**
     * This method initializes loadButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getLoadButton() {
        if (loadButton == null) {
            loadButton = new JButton();
            loadButton.setText("Load");
            loadButton.setMnemonic(KeyEvent.VK_L);
            loadButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    loadSelectedContest();
                }
            });
        }
        return loadButton;
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

    protected void loadSelectedContest() {

        if (getContestsListbox().getRowCount() < 1) {
            showMessage("No contests defined");
            return;
        }

        int selectedIndex = getContestsListbox().getSelectedIndex();

        if (selectedIndex == -1) {
            showMessage("Select a contest to load");
            return;
        }
        
        String filename = (String) getContestsListbox().getKeys()[selectedIndex];
        
        File file = new File(filename);
        String directoryName = file.getParent();
        
        try {
            
            ContestYAMLLoader loader = new ContestYAMLLoader();
            
            IInternalContest newContest = loader.fromYaml(null, directoryName);

            String contestSummary = getSummary(newContest);

            int result = FrameUtilities.yesNoCancelDialog(this, "Import" + NL + contestSummary, "Import Contest Settings");

            if (result != JOptionPane.YES_OPTION) {
                showMessage("No import done");
                return;
            }

            new ContestImporter().sendContestSettingsToServer(getController(), getContest(), newContest);
            
            showMessage("All contest settings sent to server" + NL + contestSummary);

        } catch (Exception e) {

            logException("Unable to load contest YAML from " + filename, e);
            e.printStackTrace();
            showMessage("Problem loading file(s) check log" + e.getMessage());
        }

    }

    private void showMessage(String string) {
        JOptionPane.showMessageDialog(this, string, "Contest Load", JOptionPane.INFORMATION_MESSAGE);

    }

} // @jve:decl-index=0:visual-constraint="10,10"
