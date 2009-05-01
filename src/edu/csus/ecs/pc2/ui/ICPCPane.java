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
import edu.csus.ecs.pc2.core.model.GroupEvent;
import edu.csus.ecs.pc2.core.model.IGroupListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;

import java.awt.FlowLayout;
import java.io.File;
import java.util.HashMap;
import java.util.Vector;
import java.awt.event.KeyEvent;
import java.awt.Dimension;

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

    private JButton importAccountsButton = null;

    private ICPCAccountFrame icpcAccountFrame = null;

    private ICPCImportData importData;

    private String lastDir;

    private Log log;

    private JButton importSitesButton = null;

    /**
     * 
     * 
     * @author pc2@ecs.csus.edu
     */
    public class GroupListenerImplementation implements IGroupListener {

        public void groupAdded(GroupEvent event) {
            getImportAccountsButton().setEnabled(getContest().getGroups() != null);
        }

        public void groupChanged(GroupEvent event) {
            getImportAccountsButton().setEnabled(getContest().getGroups() != null);
        }

        public void groupRemoved(GroupEvent event) {
            // TODO Auto-generated method stub
        }

    }


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
        this.add(getImportSitesButton(), null);
        this.add(getImportAccountsButton(), null);
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
     * This method initializes importAccountsButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getImportAccountsButton() {
        if (importAccountsButton == null) {
            importAccountsButton = new JButton();
            importAccountsButton.setText("Import Accounts");
            importAccountsButton.setPreferredSize(new java.awt.Dimension(150, 26));
            importAccountsButton.setMnemonic(KeyEvent.VK_A);
            importAccountsButton.setEnabled(false);
            importAccountsButton.setToolTipText("Import PC^2 ICPC contest initialization data (PC2_Team.tab)");
            importAccountsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // load the accounts
                    loadPC2Team();
                }
            });
        }
        return importAccountsButton;
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

    protected void loadPC2Site() {
        try {
            JFileChooser chooser = new JFileChooser(lastDir);
            chooser.setDialogTitle("Select PC2_Site.tab");
            chooser.setFileFilter(new TabFileFilter());
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File newFile = chooser.getSelectedFile().getCanonicalFile();
                boolean newFileProblem = true;
                if (newFile.exists()) {
                    if (newFile.isFile()) {
                        if (newFile.canRead()) {
                            // TODO move this off the swing thread, maybe into its own class
                            ICPCImportData importSiteData = LoadICPCData.loadSites(lastDir, getContest().getSites());
                            newFileProblem = false;
                            Group[] importedGroups = importSiteData.getGroups();
                            Group[] modelGroups = getContest().getGroups();
                            // XXX this is a funky location, but we do not want to add a 3rd icpc load for it
                            String contestTitle = importSiteData.getContestTitle();
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
                        } // canRead
                    } // isFile
                } // exists
                if (newFileProblem) {
                    log.warning("Problem reading PC2_Contest.tab " + newFile.getCanonicalPath() + "");
                    JOptionPane.showMessageDialog(getParentFrame(), "Could not open file " + newFile, "Warning", JOptionPane.WARNING_MESSAGE);
                }
            } // APPROVE_ACTION
        } catch(Exception e) {
            log.log(Log.WARNING, "loadPC2Site exception ", e);
        }
    }

    protected void loadPC2Team() {
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

                            Account[] accounts;
                            Vector<Account> accountVector = getContest().getAccounts(ClientType.Type.TEAM);
                            accounts = accountVector.toArray(new Account[accountVector.size()]);
                            importData = LoadICPCData.loadAccounts(lastDir, getContest().getGroups(), accounts);
                            newFileProblem = false;
                            changeDisplayFormat();
                        }
                    }
                }
                if (newFileProblem) {
                    log.warning("Problem reading _PC2_Team.tab " + newFile.getCanonicalPath() + "");
                    JOptionPane.showMessageDialog(getParentFrame(), "Could not open file " + newFile, "Warning", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Please 'Import Accounts' icpc account data first.", "Error", JOptionPane.ERROR_MESSAGE);
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
        getContest().addGroupListener(new GroupListenerImplementation());

        // initializePermissions();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // wait for groups before enabling accounts
                getImportAccountsButton().setEnabled(getContest().getGroups() != null);
                // updateGUIperPermissions();
            }
        });
    }

    /**
     * This method initializes importSitesButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getImportSitesButton() {
        if (importSitesButton == null) {
            importSitesButton = new JButton();
            importSitesButton.setPreferredSize(new Dimension(150, 26));
            importSitesButton.setMnemonic(KeyEvent.VK_S);
            importSitesButton.setText("Import Sites");
            importSitesButton.setActionCommand("Import Sites");
            importSitesButton.setToolTipText("Import PC^2 ICPC contest initialization data (PC2_Site.tab)");
            importSitesButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    loadPC2Site();
                }
            });
        }
        return importSitesButton;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
