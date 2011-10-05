package edu.csus.ecs.pc2.ui;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.imports.ICPCImportData;
import edu.csus.ecs.pc2.core.imports.LoadICPCData;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.GroupEvent;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IGroupListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;
import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.event.KeyEvent;
import java.awt.FlowLayout;
import java.io.File;
import java.util.HashMap;
import java.util.Vector;

/**
 * ICPC CMS Import Pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ICPCLoadPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 4668437987656812366L;

    private Log log;

    private PermissionList permissionList = new PermissionList();

    private JButton importAccountsButton = null;

    private JButton changeDisplayFormatButton = null;

    private JButton importSitesButton = null;

    private JButton importTSVButton = null;

    private String lastDir;

    private ICPCImportData importData;

    private ICPCAccountFrame icpcAccountFrame = null;

    /**
     * This method initializes
     * 
     */
    public ICPCLoadPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setHgap(10);
        this.setLayout(flowLayout);
        this.setSize(new Dimension(580, 349));
        this.add(getImportAccountsButton(), null);
        this.add(getChangeDisplayFormatButton(), null);
        this.add(getImportSitesButton(), null);
        this.add(getImportTSVButton(), null);
    }

    @Override
    public String getPluginTitle() {
        return "ICPC Import Data Pane";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        log = getController().getLog();
        getContest().addAccountListener(new AccountListenerImplementation());
        getContest().addGroupListener(new GroupListenerImplementation());

        initializePermissions();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // wait for groups before enabling accounts
                getImportAccountsButton().setEnabled(getContest().getGroups() != null);
                updateGUIperPermissions();
            }
        });
    }

    private boolean isAllowed(Permission.Type type) {
        return permissionList.isAllowed(type);
    }

    private void initializePermissions() {
        permissionList.clearAndLoadPermissions(getPermissionList());
    }

    private void updateGUIperPermissions() {
        changeDisplayFormatButton.setEnabled(isAllowed(Permission.Type.EDIT_ACCOUNT));
        importSitesButton.setEnabled(isAllowed(Permission.Type.EDIT_ACCOUNT));
        importAccountsButton.setEnabled(isAllowed(Permission.Type.EDIT_ACCOUNT));
        importTSVButton.setEnabled(isAllowed(Permission.Type.EDIT_ACCOUNT));
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
            importAccountsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // load the accounts
                    loadPC2Team();
                }
            });
        }
        return importAccountsButton;
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
            changeDisplayFormatButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    changeDisplayFormat();
                }
            });
        }
        return changeDisplayFormatButton;
    }

    protected void changeDisplayFormat() {
        boolean gotData = false;
        try {
            Vector<Account> teams = getContest().getAccounts(ClientType.Type.TEAM);
            for (Account account : teams) {
                if (account != null) {
                    if (account.getLongSchoolName() != null && !isEmpty(account.getLongSchoolName())) {
                        gotData = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.throwing("ICPCPane", "changeDisplayFormat", e);
        }
        if (importData == null && !gotData) {
            JOptionPane.showMessageDialog(this, "Please 'Import Accounts' icpc account data first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (importData != null && importData.getAccounts() != null) {
            getICPCAccountFrame().setICPCAccounts(importData.getAccounts());
        }
        getICPCAccountFrame().setContestAndController(getContest(), getController());
        getICPCAccountFrame().setVisible(true);
    }

    private boolean isEmpty(String longSchoolName) {
        return longSchoolName == null || longSchoolName.trim().length() == 0;
    }

    /**
     * This method initializes importSitesButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getImportSitesButton() {
        if (importSitesButton == null) {
            importSitesButton = new JButton();
            importSitesButton.setText("Import Sites ");
            importSitesButton.setMnemonic(KeyEvent.VK_S);
            importSitesButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    loadPC2Site();
                }
            });
        }
        return importSitesButton;
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
                            // update lastDir otherwise it is null
                            lastDir = chooser.getCurrentDirectory().toString();

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
        } catch (Exception e) {
            log.log(Log.WARNING, "loadPC2Site exception ", e);
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

    /**
     * This method initializes importTSVButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getImportTSVButton() {
        if (importTSVButton == null) {
            importTSVButton = new JButton();
            importTSVButton.setText("Import TSV ");
            importTSVButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    loadTSVFiles();
                }
            });
        }
        return importTSVButton;
    }

    /**
     * Load CCS TSV CMS files.
     * 
     */
    protected void loadTSVFiles() {
        // TODO code Load TSV files
        
    }

    /**
     * Account Listener Implementation.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id: ICPCPane.java 2275 2010-11-30 03:39:24Z laned $
     */
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            // ignored
        }

        public void accountModified(AccountEvent accountEvent) {
            // check if is this account
            Account account = accountEvent.getAccount();
            /**
             * If this is the account then update the GUI display per the potential change in Permissions.
             */
            if (getContest().getClientId().equals(account.getClientId())) {
                // They modified us!!
                initializePermissions();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateGUIperPermissions();
                    }
                });

            }
        }

        public void accountsAdded(AccountEvent accountEvent) {
            // ignore
        }

        public void accountsModified(AccountEvent accountEvent) {
            Account[] accounts = accountEvent.getAccounts();
            for (Account account : accounts) {

                /**
                 * If this is the account then update the GUI display per the potential change in Permissions.
                 */
                if (getContest().getClientId().equals(account.getClientId())) {
                    // They modified us!!
                    initializePermissions();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            updateGUIperPermissions();
                        }
                    });
                }
            }
        }

        public void accountsRefreshAll(AccountEvent accountEvent) {

            initializePermissions();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateGUIperPermissions();
                }
            });
        }
    }

    /**
     * Group Listener for ICPC Pane.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id: ICPCPane.java 2275 2010-11-30 03:39:24Z laned $
     */

    // $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/ui/ICPCPane.java $
    public class GroupListenerImplementation implements IGroupListener {

        public void groupAdded(GroupEvent event) {
            getImportAccountsButton().setEnabled(getContest().getGroups() != null);
        }

        public void groupChanged(GroupEvent event) {
            getImportAccountsButton().setEnabled(getContest().getGroups() != null);
        }

        public void groupRemoved(GroupEvent event) {
            // ignore
        }

        public void groupRefreshAll(GroupEvent groupEvent) {
            getImportAccountsButton().setEnabled(getContest().getGroups() != null);
        }
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

} // @jve:decl-index=0:visual-constraint="10,10"
