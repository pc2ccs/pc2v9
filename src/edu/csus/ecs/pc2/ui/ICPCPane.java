/**
 * 
 */
package edu.csus.ecs.pc2.ui;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.imports.ICPCImportData;
import edu.csus.ecs.pc2.core.imports.LoadICPCData;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;

import java.awt.FlowLayout;
import java.io.File;
import java.util.HashMap;
import java.util.Vector;

/**
 * This Pane has 2 buttons, one for loading the ICPC tab files for PC^2 and a 2nd button
 * just to change the display name format.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public class ICPCPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 5514087353704960972L;

    private JButton changeDisplayFormatButton = null;

    private JButton importButton = null;

    private ICPCAccountFrame icpcAccountFrame = null;

    private ICPCImportData importData;

    private String lastDir;

    private Log log;

    /**
     * This method initializes
     * 
     */
    public ICPCPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setHgap(50);
        this.setLayout(flowLayout);
        this.setSize(new java.awt.Dimension(448, 207));
        this.add(getImportButton(), null);
        this.add(getChangeDisplayFormatButton(), null);

    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.csus.ecs.pc2.ui.JPanePlugin#getPluginTitle()
     */
    @Override
    public String getPluginTitle() {
        return "ICPC Data Pane";
    }

    /**
     * This method initializes importButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getImportButton() {
        if (importButton == null) {
            importButton = new JButton();
            importButton.setText("Import");
            importButton.setPreferredSize(new java.awt.Dimension(150, 26));
            importButton.setToolTipText("Import PC^2 ICPC contest initialization data");
            importButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // load the accounts
                    loadICPCFiles();
                }
            });
        }
        return importButton;
    }

    /**
     * This method initializes ICPCAccountFrame
     * 
     * @return edu.csus.ecs.pc2.ui.ICPCAccountFrame
     */
    private ICPCAccountFrame getICPCAccountFrame() {
        if (icpcAccountFrame == null) {
            icpcAccountFrame = new ICPCAccountFrame();
        }
        return icpcAccountFrame;
    }

    protected void loadICPCFiles() {
        try {
            JFileChooser chooser = new JFileChooser(lastDir);
            chooser.setDialogTitle("Select PC2_Team.tab");
            chooser.setFileFilter(new TabFileFilter());
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File newFile = chooser.getSelectedFile().getCanonicalFile();
                boolean newFileProblem = true;
                if (newFile.exists()) {
                    if (newFile.isFile()) {
                        if (newFile.canRead()) {
                            lastDir = chooser.getCurrentDirectory().toString();
                            // TODO move this off the swing thread, maybe into its own class
                            importData = LoadICPCData.loadSites(lastDir, getContest().getSites());
                            newFileProblem = false;
                            Group[] importedGroups = importData.getGroups();
                            Group[] modelGroups = getContest().getGroups();
                            // XXX this is a funky location, but we do not want to add a 3rd icpc load for it
                            String contestTitle = importData.getContestTitle();
                            if (contestTitle != null && contestTitle.trim().length() > 0) {
                                ContestInformation ci = getContest().getContestInformation();
                                ci.setContestTitle(contestTitle);
                                getController().updateContestInformation(ci);
                            }
                            if (importedGroups != null && importedGroups.length > 0) {
                                if (modelGroups == null || modelGroups.length == 0) {
                                    for (Group group : importedGroups) {
                                        getController().addNewGroup(group);
                                    }
                                } else {
                                    // there exists modelGroups, that we need to merge with
                                    // primary match should be based on external id
                                    // secondary match based on name
                                    HashMap<String, Group> groupMap = new HashMap<String, Group>();
                                    for (Group group : modelGroups) {
                                        groupMap.put(group.getDisplayName(), group);
                                        groupMap.put(Integer.toString(group.getGroupId()), group);
                                    }
                                    for (Group group : importedGroups) {
                                        if (groupMap.containsKey(Integer.toString(group.getGroupId()))) {
                                            mergeGroups(groupMap.get(Integer.toString(group.getGroupId())), group);
                                        } else {
                                            if (groupMap.containsKey(group.getDisplayName())) {
                                                mergeGroups(groupMap.get(group.getDisplayName()), group);
                                            } else {
                                                // new group
                                                getController().addNewGroup(group);
                                            }
                                        }
                                    }
                                }
                            } // XXX odd, but is it an error if we have no groups?

                            Account[] accounts;
                            Vector<Account> accountVector = getContest().getAccounts(ClientType.Type.TEAM);
                            accounts = accountVector.toArray(new Account[accountVector.size()]);
                            importData = LoadICPCData.loadAccounts(lastDir, getContest().getGroups(), accounts);
                            changeDisplayFormat();
                        }
                    }
                }
                if (newFileProblem) {
                    log.warning("Problem reading PC2_Contest.tab " + newFile.getCanonicalPath() + "");
                    JOptionPane.showMessageDialog(null, "Could not open file " + newFile, "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (Exception e) {
            log.log(Log.WARNING, "Exception ", e);
        }
    }

    /**
     * This method initializes changeDisplayFormatButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getChangeDisplayFormatButton() {
        if (changeDisplayFormatButton == null) {
            changeDisplayFormatButton = new JButton();
            changeDisplayFormatButton.setText("Change Display Format");
            changeDisplayFormatButton.setToolTipText("Change Name Display Choice");
            changeDisplayFormatButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    changeDisplayFormat();
                }
            });
        }
        return changeDisplayFormatButton;
    }

    protected void changeDisplayFormat() {
        if (importData == null) {
//            importData = new ICPCImportData(getContest().getAccounts(Type.TEAM), getContest().getGroups(), getContest().getContestInformation().getContestTitle());
            JOptionPane.showMessageDialog(this, "Please 'Import' icpc data first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (importData.getAccounts() != null) {
            getICPCAccountFrame().setICPCAccounts(importData.getAccounts());
            getICPCAccountFrame().setContestAndController(getContest(), getController());
            getICPCAccountFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "No accounts loaded.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * This method merges the data read from PC2_Site.tab into the model.
     * 
     * @param destGroup
     * @param srcGroup
     */
    private void mergeGroups(Group dstGroup, Group srcGroup) {
        // no-op if the groups are the same
        if (dstGroup.isSameAs(srcGroup)) {
            return;
        }
        dstGroup.setDisplayName(srcGroup.getDisplayName());
        dstGroup.setGroupId(srcGroup.getGroupId());
        if (srcGroup.getSite() != null) { // do not overwrite this
            dstGroup.setSite(srcGroup.getSite());
        }
        getController().updateGroup(dstGroup);
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        log = getController().getLog();
        // getContest().addAccountListener(new AccountListenerImplementation());

        // initializePermissions();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // updateGUIperPermissions();
            }
        });
    }

} // @jve:decl-index=0:visual-constraint="10,10"
