// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.ContestTimeComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IProfileListener;
import edu.csus.ecs.pc2.core.model.ISiteListener;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.ProfileEvent;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.SiteEvent;
import edu.csus.ecs.pc2.core.report.IReport;
import edu.csus.ecs.pc2.core.report.ProfileCloneSettingsReport;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.QuickLoad;

/**
 * Profile administration pane.
 * 
 * Provides a front end to all profile functions, like rename, change, clone, etc.
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfilesPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 9075523788534575300L;

    private ProfileSaveFrame profileSaveFrame = null;

    private JLabel profileNameLabel = null;

    private JButton switchButton = null;

    private JButton setButton = null;

    private JTextField profileTextField = null;

    private JPanel centerPane = null;

    private JPanel buttonPane = null;

    private JButton newButton = null;

    private JButton exportButton = null;

    private JButton cloneButton = null;

    private JButton resetContestButton = null;

    private ResetContestFrame resetContestFrame = null;

    private JTextField profileDescriptionTextField = null;

    private JLabel profileDescriptionLabel = null;

    private JPanel topPanel = null;

    private MCLB profilesListBox = null;

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    private JButton reportButton = null;

    private SwitchProfileConfirmFrame switchFrame = null;

    private boolean usingExtraColumns = false;
    
    private boolean showHidden = false;

    private JCheckBox showHiddenProfilesCheckbox = null;

    private JButton loadButton = null;


    /**
     * This method initializes
     * 
     */
    public ProfilesPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        
        usingExtraColumns = Utilities.isDebugMode();
        
        profileNameLabel = new JLabel();
        profileNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        profileNameLabel.setText("Active Profile Name");
        profileNameLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() > 1 && e.isControlDown() && e.isShiftDown()) {
                    Profile profile = getContest().getProfile();
                    String message = "Contest Id " + profile.getContestId();
                    JOptionPane.showMessageDialog(null, message);
                }
            }
        });
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(843, 319));
        this.add(getCenterPane(), java.awt.BorderLayout.CENTER);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);

        this.add(getTopPanel(), BorderLayout.NORTH);
        FrameUtilities.centerFrame(this);

       
        
    }

    public String getPluginTitle() {
        return "Profiles Pane";
    }

    /**
     * This method initializes Set
     * 
     * @return javax.swing.JButton
     */
    private JButton getSwitchButton() {
        if (switchButton == null) {
            switchButton = new JButton();
            switchButton.setEnabled(true);
            switchButton.setMnemonic(java.awt.event.KeyEvent.VK_W);
            switchButton.setPreferredSize(new java.awt.Dimension(100, 26));
            switchButton.setSelected(false);
            switchButton.setText("Switch");
            switchButton.setToolTipText("Switch to the selected profile");
            switchButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    switchSelectedProfile();
                }
            });
        }
        return switchButton;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSetButton() {
        if (setButton == null) {
            setButton = new JButton();
            setButton.setEnabled(false);
            setButton.setMnemonic(java.awt.event.KeyEvent.VK_S);
            setButton.setText("Set");
            setButton.setToolTipText("Update the profile name and description");
            setButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    renameProfile();
                }
            });
        }
        return setButton;
    }

    /**
     * This method initializes profileTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getProfileTextField() {
        if (profileTextField == null) {
            profileTextField = new JTextField();
            profileTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableSetButton();
                }
            });
        }
        return profileTextField;
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            profileDescriptionLabel = new JLabel();
            profileDescriptionLabel.setText("Active Profile Description");
            profileDescriptionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            centerPane = new JPanel();
            centerPane.setLayout(new BorderLayout());
            centerPane.add(getProfilesListBox(), BorderLayout.CENTER);
        }
        return centerPane;
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
            buttonPane.setPreferredSize(new java.awt.Dimension(35, 35));
            buttonPane.add(getNewButton(), null);
            getNewButton().setEnabled(false);
            buttonPane.add(getCloneButton(), null);
            buttonPane.add(getSwitchButton(), null);
            buttonPane.add(getResetContestButton(), null);
            buttonPane.add(getExportButton(), null);
            buttonPane.add(getReportButton(), null);
            buttonPane.add(getLoadButton(), null);
            buttonPane.add(getShowHiddenProfilesCheckbox(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes newButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getNewButton() {
        if (newButton == null) {
            newButton = new JButton();
            newButton.setText("New");
            newButton.setMnemonic(KeyEvent.VK_N);
            newButton.setEnabled(true);
            newButton.setToolTipText("Create a new (blank) profile");
            newButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    newProfile();
                }
            });
        }
        return newButton;
    }

    /**
     * This method initializes jButton2
     * 
     * @return javax.swing.JButton
     */
    private JButton getExportButton() {
        if (exportButton == null) {
            exportButton = new JButton();
            exportButton.setText("Export");
            exportButton.setMnemonic(java.awt.event.KeyEvent.VK_X);
            exportButton.setEnabled(false);
            exportButton.setToolTipText("Export profile information");
            exportButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    exportProfile();
                }
            });
        }
        return exportButton;
    }

    /**
     * This method initializes cloneButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCloneButton() {
        if (cloneButton == null) {
            cloneButton = new JButton();
            cloneButton.setText("Clone");
            cloneButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
            cloneButton.setEnabled(true);
            cloneButton.setToolTipText("Create a copy of the active profile");
            cloneButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    cloneProfile();
                }
            });
        }
        return cloneButton;
    }



    private String getProfileName() {
        return getContest().getProfile().getName();
    }
    
    private String getProfileDescription(){
        return getContest().getProfile().getDescription();
    }

    protected void newProfile() {
        getProfileSaveFrame().setTitle("Create new profile");
        getProfileSaveFrame().setSaveButtonName(ProfileSavePane.CREATE_BUTTON_NAME);
        getProfileSaveFrame().setVisible(true);
    }

    protected void exportProfile() {
        getProfileSaveFrame().setTitle("Export settings " + getProfileName());
        getProfileSaveFrame().setSaveButtonName(ProfileSavePane.EXPORT_BUTTON_NAME);
        getProfileSaveFrame().setVisible(true);
    }
    
    public SwitchProfileConfirmFrame getSwitchFrame() {
        if (switchFrame == null){
            switchFrame = new SwitchProfileConfirmFrame();
        }
        return switchFrame;
    }

    protected void switchSelectedProfile() {
        int selectedIndex = getProfilesListBox().getSelectedIndex();
        if (selectedIndex == -1) {
            showMessage("No profile selected");

            return;  // ------------------------------------ RETURN
        }

        if (getContest().getContestTime().isContestRunning()){
            showMessage("Contest Clock/Time must be stopped");
            
            return;  // ------------------------------------ RETURN
        }

        String profilePath = (String) getProfilesListBox().getKeys()[selectedIndex];
        Profile selectedProfile = getProfile(profilePath);

        if (selectedProfile.equals(getContest().getProfile())) {

            showMessage("Profile '" + selectedProfile.getName() + "' is already the current profile");

            return; // ------------------------------------ RETURN

        } else {
            // if (selectedProfile.equals(getContest().getProfile())) {
            int result = FrameUtilities.yesNoCancelDialog(this, "Change to profile '" + selectedProfile.getName() + "'?", "Change profile");

            if (result != JOptionPane.YES_OPTION) {
                return; // ------------------------------------ RETURN
            }
        }
        
        getSwitchFrame().setProfile(selectedProfile);
        getSwitchFrame().setVisible(true);
    }

    private Profile getProfile(String profilePath) {

        for (Profile profile : getContest().getProfiles()) {
            if (profile.getProfilePath().equals(profilePath)) {
                return profile;
            }
        }

        return null;
    }

    protected void renameProfile() {

        if (getProfileTextField() == null || getProfileTextField().getText().trim().length() < 1) {
            showMessage("No profile name specified");
            return;
        }

        String newProfileName = getProfileTextField().getText().trim();

        String description = getProfileDescriptionTextField().getText().trim() + "";

        Profile profile = getContest().getProfile();
        profile.setName(newProfileName);
        profile.setDescription(description);
        getController().updateProfile(profile);
    }

    private void showMessage(String string) {
        JOptionPane.showMessageDialog(this, string);
    }

    public ProfileSaveFrame getProfileSaveFrame() {
        if (profileSaveFrame == null) {
            profileSaveFrame = new ProfileSaveFrame();
        }
        return profileSaveFrame;
    }

    /**
     * This method initializes resetContestButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getResetContestButton() {
        if (resetContestButton == null) {
            resetContestButton = new JButton();
            resetContestButton.setMnemonic(java.awt.event.KeyEvent.VK_S);
            resetContestButton.setText("Reset");
            resetContestButton.setToolTipText("Reset the active contest profile - clear runs, clars, reset contest time");
            resetContestButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    resetContest();
                }
            });
        }
        return resetContestButton;
    }

    protected void resetContest() {

        String siteContestClockRunning = "";

        ContestTime[] contestTimes = getContest().getContestTimes();
        Arrays.sort(contestTimes, new ContestTimeComparator());

        int numberSites = 0; // number of sites with contest clock running/started

        for (ContestTime contestTime : contestTimes) {
            if (contestTime.isContestRunning() && siteConnected(contestTime.getSiteNumber())) {
                Site site = getContest().getSite(contestTime.getSiteNumber());
                siteContestClockRunning = siteContestClockRunning + ", " + site.format(Site.LONG_NAME_PATTERN);
                numberSites++;
            }
        }

        if (numberSites != 0) {
            siteContestClockRunning = siteContestClockRunning.substring(2); // remove ", " off front of string to make
            String sitePhrase = "a site";
            if (numberSites > 1) {
                sitePhrase = numberSites + " sites";
            }
            JOptionPane.showMessageDialog(this, "Contest Clock not stopped at " + sitePhrase + " (" + siteContestClockRunning + ")\n All contest clocks must be stopped", "Unable to reset",
                    JOptionPane.ERROR_MESSAGE);
        } else {

            showResetDialog();

        }
    }

    private boolean siteConnected(int siteNumber) {
        ClientId serverId = new ClientId(siteNumber, Type.SERVER, 0);
        return getContest().isLocalLoggedIn(serverId) || isThisSite(siteNumber);
    }

    private boolean isThisSite(int siteNumber) {
        return getContest().getSiteNumber() == siteNumber;
    }

    private void showResetDialog() {

        if (resetContestFrame == null) {
            resetContestFrame = new ResetContestFrame();
            resetContestFrame.setContestAndController(getContest(), getController());
        }

        resetContestFrame.setVisible(true);
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        initializePermissions();
        
        getProfileSaveFrame().setContestAndController(inContest, inController);
        getSwitchFrame().setContestAndController(getContest(), getController());

        Profile profile = getContest().getProfile();
        updateProfileInformation(profile);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                refreshProfilesList();
                updateGUIperPermissions();
            }

        });

        inContest.addProfileListener(new ProfileListenerImplementation());
        
        inContest.addAccountListener(new AccountListenerImplementation());
        
        inContest.addSiteListener(new SiteListenerImplementation());
    }
    
    private void updateGUIperPermissions() {

        switchButton.setVisible(isAllowed(Permission.Type.SWITCH_PROFILE));
        setButton.setVisible(isAllowed(Permission.Type.CLONE_PROFILE));
        newButton.setVisible(isAllowed(Permission.Type.CLONE_PROFILE));
        exportButton.setVisible(isAllowed(Permission.Type.EXPORT_PROFILE));
        cloneButton.setVisible(isAllowed(Permission.Type.CLONE_PROFILE));
        resetContestButton.setVisible(isAllowed(Permission.Type.RESET_CONTEST));
        reportButton.setVisible(isAllowed(Permission.Type.CLONE_PROFILE));
        
        loadButton.setVisible(isAllowed(Permission.Type.CLONE_PROFILE) && Utilities.isDebugMode());
    }

    protected void refreshProfilesList() {

        try {
            Profile[] profiles = getContest().getProfiles();

            getProfilesListBox().removeAllRows();

            getSwitchButton().setEnabled(false);
            if (profiles.length > 0) {

                for (Profile profile : profiles) {
                    updateProfileRow(profile);
                }
                getProfilesListBox().autoSizeAllColumns();
                getSwitchButton().setEnabled(true);
            }
            
            if (getContest().getSites().length > 1) {
                // Bug 792
                disableAllButtons();
            }

            updateProfileInformation(getContest().getProfile());

        } catch (Exception e) {
            getController().getLog().log(Log.DEBUG, "Exception refreshing profile list", e);
        }
    }

    /**
     * Disable all buttons
     */
    private void disableAllButtons() {
        // Bug 792
        getNewButton().setEnabled(false);
        getSwitchButton().setEnabled(false);
        getSetButton().setEnabled(false);
        getCloneButton().setEnabled(false);
        getResetContestButton().setEnabled(false);
        getResetContestButton().setEnabled(false);
    }

    private Object[] buildProfileRow(Profile profile) {

//        Object[] defaultColumns = { "Name", "Description", "Create Date" };
//        Object[] fullColumns = { "Name", "Description", "Create Date", "Contest ID", "Path" };

        int numberColumns = profilesListBox.getColumnCount();
        Object[] c = new String[numberColumns];

        c[0] = profile.getName();
        if (profile.getProfilePath().equals(getContest().getProfile().getProfilePath())) {
            c[0] = "(Active) " + profile.getName();
        }
        c[1] = profile.getDescription();
        c[2] = formatter.format(profile.getCreateDate());
        
        if (usingExtraColumns){
            c[3] = profile.getContestId();
            c[4] = profile.getProfilePath();
        }

        return c;
    }

    private void updateProfileInformation(final Profile profile) {

        if (profile != null) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    getProfileTextField().setText(profile.getName());
                    profileNameLabel.setToolTipText("Contest Profile Name " + profile.getContestId());
                    getProfileDescriptionTextField().setText(profile.getDescription());
                    enableSetButton();
                }
            });
        }
    }

    /**
     * Profile Listener Implementation
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class ProfileListenerImplementation implements IProfileListener {

        public void profileAdded(ProfileEvent event) {
            updateProfileRow(event.getProfile());
        }

        public void profileChanged(ProfileEvent event) {
            updateProfileRow(event.getProfile());
            Profile profile = getContest().getProfile();
            updateProfileInformation(profile);
        }

        public void profileRemoved(ProfileEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    refreshProfilesList();
                }
            });
        }

        public void profileRefreshAll(ProfileEvent profileEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    refreshProfilesList();
                }
            });
        }
    }

    /**
     * This method initializes profileDescriptionTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getProfileDescriptionTextField() {
        if (profileDescriptionTextField == null) {
            profileDescriptionTextField = new JTextField();
            profileDescriptionTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableSetButton();
                }
            });
        }
        return profileDescriptionTextField;
    }

    protected void enableSetButton() {

        Profile profile = getContest().getProfile();

        String name = getProfileTextField().getText();
        String description = getProfileDescriptionTextField().getText();

        boolean enable = (!profile.getName().equals(name)) || (!profile.getDescription().equals(description));

        getSetButton().setEnabled(enable);
    }

    /**
     * This method initializes topPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTopPanel() {
        if (topPanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.insets = new Insets(1, 26, 40, 187);
            gridBagConstraints4.gridx = 2;
            gridBagConstraints4.gridy = 1;
            gridBagConstraints4.ipadx = 47;
            gridBagConstraints4.gridheight = 2;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.VERTICAL;
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.gridy = 2;
            gridBagConstraints3.ipadx = 244;
            gridBagConstraints3.ipady = 9;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new Insets(10, 9, 16, 25);
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.VERTICAL;
            gridBagConstraints2.gridheight = 2;
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.ipadx = 244;
            gridBagConstraints2.ipady = 10;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.insets = new Insets(25, 9, 10, 25);
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.insets = new Insets(13, 51, 19, 8);
            gridBagConstraints1.gridy = 2;
            gridBagConstraints1.ipadx = 46;
            gridBagConstraints1.ipady = 7;
            gridBagConstraints1.gridx = 0;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new Insets(29, 51, 1, 8);
            gridBagConstraints.gridy = 0;
            gridBagConstraints.ipadx = 78;
            gridBagConstraints.ipady = 7;
            gridBagConstraints.gridx = 0;
            topPanel = new JPanel();
            topPanel.setLayout(new GridBagLayout());
            topPanel.setPreferredSize(new Dimension(120, 120));
            topPanel.add(profileNameLabel, gridBagConstraints);
            topPanel.add(profileDescriptionLabel, gridBagConstraints1);
            topPanel.add(getProfileTextField(), gridBagConstraints2);
            topPanel.add(getProfileDescriptionTextField(), gridBagConstraints3);
            topPanel.add(getSetButton(), gridBagConstraints4);
        }
        return topPanel;
    }

    /**
     * This method initializes profilesListBox
     * 
     * @return edu.csus.ecs.pc2.ui.MCLB
     */
    private MCLB getProfilesListBox() {
        if (profilesListBox == null) {
            profilesListBox = new MCLB();

            Object[] defaultColumns = { "Name", "Description", "Create Date" };
            Object[] fullColumns = { "Name", "Description", "Create Date", "Contest ID", "Path" };

            Object[] cols = defaultColumns;
            if (usingExtraColumns) {
                cols = fullColumns;
            }

            profilesListBox.addColumns(cols);

            HeapSorter sorter = new HeapSorter();

            int idx = 0;

            profilesListBox.setColumnSorter(idx++, sorter, 1); // Name
            profilesListBox.setColumnSorter(idx++, sorter, 2); // Description
            profilesListBox.setColumnSorter(idx++, sorter, 3); // Create Date

            if (usingExtraColumns) {
                profilesListBox.setColumnSorter(idx++, sorter, 4); // Contest Id
                profilesListBox.setColumnSorter(idx++, sorter, 5); // Path
            }

        }
        return profilesListBox;
    }
    
    
    /**
     * Remove run from grid.
     * 
     * @param run
     */
    private void removeProfilesRow(final Profile profile) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                int rowNumber = profilesListBox.getIndexByKey(profile.getProfilePath());
                if (rowNumber != -1) {
                    profilesListBox.removeRow(rowNumber);
                }
            }
        });
    }

    public void updateProfileRow(final Profile profile) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (!profile.isActive() && !isShowHidden()) {
                    removeProfilesRow(profile);
                } else {

                    Object[] objects = buildProfileRow(profile);
                    int rowNumber = profilesListBox.getIndexByKey(profile.getProfilePath());
                    if (rowNumber == -1) {
                        profilesListBox.addRow(objects, profile.getProfilePath());
                    } else {
                        profilesListBox.replaceRow(objects, rowNumber);
                    }
                    profilesListBox.autoSizeAllColumns();
                    profilesListBox.sort();
                }
            }
        });
    }

    /**
     * This method initializes reportButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getReportButton() {
        if (reportButton == null) {
            reportButton = new JButton();
            reportButton.setText("Report");
            reportButton.setMnemonic(java.awt.event.KeyEvent.VK_R);
            reportButton.setToolTipText("Display Profiles Report");
            reportButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    createReport(new ProfileCloneSettingsReport());
                }
            });
        }
        return reportButton;
    }

    protected void createReport(IReport report) {
        report.setContestAndController(getContest(), getController());

        try {
            createAndViewReportFile(report, new Filter(), getController().getLog());
        } catch (IOException e) {
            StaticLog.log("Exception creating report", e);
            JOptionPane.showMessageDialog(this, "Exception in report " + e.getLocalizedMessage());
        }
    }
    
    /**
     * Account listener for permissions.
     *  
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            // ignore doesn't affect this pane
        }

        public void accountModified(AccountEvent event) {
            // check if is this account
            Account account = event.getAccount();
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
            // ignore, this does not affect this class

        }

        public void accountsModified(AccountEvent accountEvent) {
            for (Account account : accountEvent.getAccounts()) {
                /**
                 * If this is the account then update the GUI display per the potential change in Permissions.
                 */
                if (getContest().getClientId().equals(account.getClientId())) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            initializePermissions();
                            updateGUIperPermissions();
                        }
                    });
                }
            }
        }

        public void accountsRefreshAll(AccountEvent accountEvent) {
            accountsModified(accountEvent);
        }
    }
    
    public boolean isShowHidden() {
        return showHidden;
    }
    
    public void setShowHidden(boolean showHidden) {
        this.showHidden = showHidden;
    }

    /**
     * This method initializes showHiddenProfilesCheckbox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getShowHiddenProfilesCheckbox() {
        if (showHiddenProfilesCheckbox == null) {
            showHiddenProfilesCheckbox = new JCheckBox();
            showHiddenProfilesCheckbox.setText("Show Backups");
            showHiddenProfilesCheckbox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    toggleShowHiddenProfiles();
                }
            });
        }
        return showHiddenProfilesCheckbox;
    }

    protected void toggleShowHiddenProfiles() {

        showHidden = getShowHiddenProfilesCheckbox().isSelected();
        refreshProfilesList();
        
    }

    protected void cloneProfile() {
        getProfileSaveFrame().setTitle("Clone active profile: " + getProfileName() + " (" +getProfileDescription()+")");
        getProfileSaveFrame().setSaveButtonName(ProfileSavePane.CLONE_BUTTON_NAME);
        getProfileSaveFrame().setVisible(true);
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
                    quickLoad();
                }
            });
        }
        return loadButton;
    }

    protected boolean isAdministrator(ClientId clientId) {
        return clientId.getClientType().equals(ClientType.Type.ADMINISTRATOR);
    }
    
    /**
     * Add problems, languages and insure accounts.
     */
    protected void quickLoad() {
        
        if (! isAdministrator(getContest().getClientId())){

            JOptionPane.showMessageDialog(this,"You must be an Administrator to use this feature");
            return;
        }
        
        QuickLoad loader = new QuickLoad();
        loader.setContestAndController(getContest(), getController());
        loader = null;
        
        JOptionPane.showMessageDialog(this,"Contest quick loaded");
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    public class SiteListenerImplementation implements ISiteListener {

        @Override
        public void siteAdded(SiteEvent event) {
            refreshProfilesList();            
        }

        @Override
        public void siteRemoved(SiteEvent event) {
            refreshProfilesList();            
            }

        @Override
        public void siteChanged(SiteEvent event) {
            refreshProfilesList();            
        }

        @Override
        public void siteLoggedOn(SiteEvent event) {
            // ignored
            
        }

        @Override
        public void siteLoggedOff(SiteEvent event) {
            // ignored
            
        }

        @Override
        public void siteProfileStatusChanged(SiteEvent event) {
            // ignored
            
        }

        @Override
        public void sitesRefreshAll(SiteEvent siteEvent) {
            refreshProfilesList();            
        }

    }
    
    
 
} // @jve:decl-index=0:visual-constraint="25,9"
