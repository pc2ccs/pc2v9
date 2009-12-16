package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.ContestTimeComparator;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IProfileListener;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.ProfileComparatorByName;
import edu.csus.ecs.pc2.core.model.ProfileEvent;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import java.awt.Rectangle;
import java.awt.Point;
import javax.swing.SwingConstants;

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

    private JLabel profileLabel = null;

    private JComboBox profileComboBox = null;

    private JButton switchButton = null;

    private JButton setButton = null;

    private JTextField profileTextField = null;

    private JPanel centerPane = null;

    private JPanel buttonPane = null;

    private JButton newButton = null;

    private JButton exportButton = null;

    private JLabel notificationOfNonImplementationLabel = null;

    private JButton cloneButton = null;

    private JButton resetContestButton = null;

    private ResetContestFrame resetContestFrame = null;

    private JTextField profileDescriptionTextField = null;

    private JLabel profileDescriptionLabel = null;

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
        profileLabel = new JLabel();
        profileLabel.setBounds(new Rectangle(14, 121, 134, 23));
        profileLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        profileLabel.setText("Profiles");
        profileNameLabel = new JLabel();
        profileNameLabel.setBounds(new Rectangle(14, 28, 134, 23));
        profileNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        profileNameLabel.setText("Profile Name");
        profileNameLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() > 1 && e.isControlDown() && e.isShiftDown()) {
                    Profile profile = getContest().getProfile();
                    String message = "Contest Id "+profile.getContestId();
                    JOptionPane.showMessageDialog(null, message);
                }
            }
        });
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(729, 319));
        this.add(getCenterPane(), java.awt.BorderLayout.CENTER);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);

        FrameUtilities.centerFrame(this);

    }

    public String getPluginTitle() {
        return "Profiles Pane";
    }

    /**
     * This method initializes profileComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getProfileComboBox() {
        if (profileComboBox == null) {
            profileComboBox = new JComboBox();
            profileComboBox.setBounds(new Rectangle(165, 118, 303, 28));
        }
        return profileComboBox;
    }

    /**
     * This method initializes Set
     * 
     * @return javax.swing.JButton
     */
    private JButton getSwitchButton() {
        if (switchButton == null) {
            switchButton = new JButton();
            switchButton.setEnabled(false);
            switchButton.setMnemonic(java.awt.event.KeyEvent.VK_W);
            switchButton.setPreferredSize(new java.awt.Dimension(100, 26));
            switchButton.setLocation(new Point(492, 120));
            switchButton.setSize(new java.awt.Dimension(100, 28));
            switchButton.setText("Switch");
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
            setButton.setLocation(new Point(494, 28));
            setButton.setSize(new java.awt.Dimension(100, 26));
            setButton.setText("Set");
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
            profileTextField.setBounds(new Rectangle(165, 24, 303, 30));
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
            profileDescriptionLabel.setBounds(new Rectangle(14, 77, 134, 23));
            profileDescriptionLabel.setText("Description");
            profileDescriptionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            notificationOfNonImplementationLabel = new JLabel();
            notificationOfNonImplementationLabel.setBounds(new Rectangle(0, 163, 733, 82));
            notificationOfNonImplementationLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            notificationOfNonImplementationLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
            notificationOfNonImplementationLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            notificationOfNonImplementationLabel.setText("");
            centerPane = new JPanel();
            centerPane.setLayout(null);
            centerPane.add(profileNameLabel, null);
            centerPane.add(profileLabel, null);
            centerPane.add(getProfileComboBox(), null);
            centerPane.add(getSwitchButton(), null);
            centerPane.add(getSetButton(), null);
            centerPane.add(getProfileTextField(), null);

            centerPane.add(notificationOfNonImplementationLabel, null);
            centerPane.add(getProfileDescriptionTextField(), null);
            centerPane.add(profileDescriptionLabel, null);
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
            buttonPane.add(getResetContestButton(), null);
            buttonPane.add(getCloneButton(), null);
            buttonPane.add(getExportButton(), null);
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
            newButton.setMnemonic(java.awt.event.KeyEvent.VK_N);
            newButton.setEnabled(true);
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
            cloneButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    cloneProfile();
                }
            });
        }
        return cloneButton;
    }

    protected void cloneProfile() {
        getProfileSaveFrame().setTitle("Clone settings for " + getProfileName());
        getProfileSaveFrame().populateGUI(getContest().getProfile());
        getProfileSaveFrame().setSaveButtonName(ProfileSavePane.CLONE_BUTTON_NAME);
        getProfileSaveFrame().setVisible(true);
    }

    private String getProfileName() {
        return getContest().getProfile().getName();
    }

    protected void newProfile() {
        getProfileSaveFrame().setTitle("New Profile ");
        getProfileSaveFrame().populateGUI();
        getProfileSaveFrame().setSaveButtonName(ProfileSavePane.NEW_BUTTON_NAME);
        getProfileSaveFrame().setVisible(true);
    }

    protected void exportProfile() {
        getProfileSaveFrame().setTitle("Export settings " + getProfileName());
        getProfileSaveFrame().populateGUI(getContest().getProfile());
        getProfileSaveFrame().setSaveButtonName(ProfileSavePane.EXPORT_BUTTON_NAME);
        getProfileSaveFrame().setVisible(true);
    }

    protected void switchSelectedProfile() {
        // TODO code switchSelectedProfile

        if (getProfileComboBox().getSelectedIndex() < 0) {
            showMessage("No profile selected");
            return;
        }

        ProfileWrapper wrapper = (ProfileWrapper) getProfileComboBox().getSelectedItem();

        if (wrapper.getProfile().equals(getContest().getProfile())) {
            showMessage("Currently using profile '" + wrapper + "' (no need to switch)");
            return;
        }

        showMessage("Switch Profile to: " + wrapper);
    }

    protected void renameProfile() {
        // TODO code renameProfile

        if (getProfileTextField() == null || getProfileTextField().getText().trim().length() < 1) {
            showMessage("No profile name specified");
            return;
        }

        String newProfileName = getProfileTextField().getText().trim();
        
        String description = getProfileDescriptionTextField().getText().trim()+"";
        
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
            resetContestButton.setText("Reset");
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

        getProfileSaveFrame().setContestAndController(inContest, inController);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Profile profile = getContest().getProfile();
                updateProfileInformation(profile);
                refreshProfilesList();
            }

        });

        inContest.addProfileListener(new ProfileListenerImplementation());
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     *
     */
    class ProfileWrapper {
        private Profile profile;

        public Profile getProfile() {
            return profile;
        }

        public ProfileWrapper(Profile profile) {
            this.profile = profile;
        }

        @Override
        public String toString() {
            return profile.getName();
        }
    }

    protected void refreshProfilesList() {

        Profile[] profiles = getContest().getProfiles();
        
        getProfileComboBox().removeAllItems();

        getSwitchButton().setEnabled(false);
        if (profiles.length > 0) {
            Arrays.sort(profiles, new ProfileComparatorByName());
            for (Profile profile : profiles) {
                getProfileComboBox().addItem(new ProfileWrapper(profile));
            }
            getSwitchButton().setEnabled(true);
        }
        
        updateProfileInformation(getContest().getProfile());
        
        enableSetButton();

    }

    private void updateProfileInformation(Profile profile) {

        if (profile != null) {
            getProfileTextField().setText(profile.getName());
            profileNameLabel.setToolTipText("Contest Profile Name " + profile.getContestId());
            getProfileDescriptionTextField().setText(profile.getDescription());
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
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    refreshProfilesList();
                }
            });
        }

        public void profileChanged(ProfileEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Profile profile = getContest().getProfile();
                    updateProfileInformation(profile);
                }
            });
        }

        public void profileRemoved(ProfileEvent event) {
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
            profileDescriptionTextField.setBounds(new Rectangle(165, 74, 303, 29));
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

} // @jve:decl-index=0:visual-constraint="25,9"
