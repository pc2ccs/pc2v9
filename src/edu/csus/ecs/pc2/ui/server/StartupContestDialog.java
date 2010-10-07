package edu.csus.ecs.pc2.ui.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLEditorKit;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.ProfileComparatorByName;
import edu.csus.ecs.pc2.core.security.FileSecurity;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.profile.ProfileLoadException;
import edu.csus.ecs.pc2.profile.ProfileManager;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import javax.swing.JScrollPane;

/**
 * Contest Password and Profile login screen.
 * 
 * To return control and information on Login, the Runnable will be invoked.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class StartupContestDialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -8322972123417494060L;

    private JPanel centerPane = null;

    private JPasswordField confirmPasswordTextField = null;

    private JPasswordField contestPasswordTextField = null;

    private JLabel nameTitleLabel = null;

    private JLabel versionTitleLabel = null;

    private JLabel passwordTitleLabel = null;

    private JButton loginButton = null;

    private JButton exitButton = null;

    private JLabel messageLabel = null;

    private JPanel mainPanel;

    private JPanel northPanel = null;

    private boolean bAlreadyLoggingIn = false;

    private JComboBox profilesComboBox = null;

    private JLabel profileTitleLabel = null;

    private int siteNumber = 1;

    /**
     * Show the confirmation text field ?.
     */
    private boolean showConfirmPassword = true;

    private JTextPane topTextPane = null;

    private JScrollPane descriptionScrollpane = null;

    private final String firstLoginText = "<html><center><font size=\"+1\"><b>The Contest Password</b><br>  In order to insure contest security, "
            + " all contest data is   protected by a master password. Before anyone can restart a   contest or access sensitive data they will be asked to enter "
            + "  this contest master password.  This screen is the place where you set (and confirm) the value of the contest master password.   "
            + "(Note that the contest master password is independent of the   passwords needed to login to any specific contest   account -- "
            + "Server, Admin, Team, Judge, etc.)</font></center></html>";  //  @jve:decl-index=0:

    private final String subsequentLoginText = "<html><center><font size=\"+1\">This server has been started previously.  </font></center><font size=\"+1\">" + "<br>" //
            + "To restart the server you must: <br>" + //
            "(1) choose a \"contest profile\" to be used (choose \"default\" if you're not sure or if no other profile(s) have been created),<br>" + //
            " and<br>" +  "(2) enter the Contest Master Password which was specified when the server was first started<br>" + //
            "</font></html>";  //  @jve:decl-index=0:

    /**
     * This method initializes
     * 
     */
    public StartupContestDialog() {
        super();
        initialize();
        overRideLookAndFeel();
        setModal(true);
        FrameUtilities.centerFrame(this);
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(532, 545));
        this.setPreferredSize(new Dimension(500, 430));
        this.setMinimumSize(new java.awt.Dimension(500, 430));
        this.setBackground(new java.awt.Color(253, 255, 255));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("PC^2 Profile Selection ");
        this.setContentPane(getMainPanel());

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                promptBeforeExit();
            }
        });

        VersionInfo versionInfo = new VersionInfo();
        versionTitleLabel.setText("PC^2 version " + versionInfo.getVersionNumber() + " " + versionInfo.getBuildNumber());

        populateProfileComboBox();
    }

    /**
     * Shows description as toString().
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    protected class ProfileWrapper {
        private Profile profile;

        public ProfileWrapper(Profile profile) {
            super();
            this.profile = profile;
        }

        public Profile getProfile() {
            return profile;
        }

        public void setProfile(Profile profile) {
            this.profile = profile;
        }

        @Override
        public String toString() {
            return profile.getName() + " (" + profile.getDescription() + ")";
        }
    }

    private void populateProfileComboBox() {

        getProfilesComboBox().removeAllItems();

        ProfileManager manager = new ProfileManager();

        int comboIndex = 0;

        if (manager.hasDefaultProfile()) {

            Profile[] profiles = new Profile[0];
            try {
                profiles = manager.load();
                Profile currentProfile = manager.getDefaultProfile();

                if (!new File(currentProfile.getProfilePath()).isDirectory()) {
                    System.err.println("No such directory: " + currentProfile.getProfilePath());
                }

                Arrays.sort(profiles, new ProfileComparatorByName());

                int idx = 0;
                for (Profile profile : profiles) {

                    getProfilesComboBox().addItem(new ProfileWrapper(profile));
                    if (profile.getProfilePath().equals(currentProfile.getProfilePath())) {
                        comboIndex = idx;
                    }
                    idx++;
                }

                /**
                 * Only show confirm if there has not been a contest key file created.
                 */
                String baseDirectoryName = getBaseProfileDirectoryName(currentProfile, "db." + getSiteNumber());
                String contestKeyFilename = baseDirectoryName + File.separator + FileSecurity.getContestKeyFileName();
                showConfirmPassword = !new File(contestKeyFilename).exists();

            } catch (IOException e) {
                fatalError("Unable to load profile list ", e);
            } catch (ProfileLoadException e) {
                fatalError("Unable to load profile list ", e);
            }

        } else {
            showConfirmPassword = true;
            Profile profile = ProfileManager.createNewProfile();
            getProfilesComboBox().addItem(new ProfileWrapper(profile));

        }

        getProfilesComboBox().setSelectedIndex(comboIndex);

        profileTitleLabel.setVisible(! showConfirmPassword);
        getProfilesComboBox().setVisible(! showConfirmPassword);

        getConfirmPasswordTextField().setVisible(showConfirmPassword);
        passwordTitleLabel.setVisible(showConfirmPassword);
        
        if (! showConfirmPassword){
            topTextPane.setText(subsequentLoginText);
        }

    }

    private void fatalError(String string, Exception e) {

        // FIXME log this exception
        setStatusMessage(string + ", check logs");
        e.printStackTrace(System.err);

        System.exit(4);
    }

    private void overRideLookAndFeel() {
        // TODO eventually move this method to on location
        String value = IniFile.getValue("client.plaf");
        if (value != null && value.equalsIgnoreCase("java")) {
            FrameUtilities.setJavaLookAndFeel();
        }
        if (value != null && value.equalsIgnoreCase("native")) {
            FrameUtilities.setNativeLookAndFeel();
        }
    }

    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());

            mainPanel.add(getPasswordTitleLabel(), java.awt.BorderLayout.CENTER);
            mainPanel.add(getNorthPanel(), java.awt.BorderLayout.NORTH);
        }

        return mainPanel;
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPasswordTitleLabel() {
        if (centerPane == null) {
            profileTitleLabel = new JLabel();
            profileTitleLabel.setBounds(new Rectangle(53, 22, 105, 22));
            profileTitleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            profileTitleLabel.setText("Profile");
            messageLabel = new JLabel();
            messageLabel.setForeground(Color.red);
            messageLabel.setText("");
            messageLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            messageLabel.setBounds(new Rectangle(50, 222, 393, 26));
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            passwordTitleLabel = new JLabel();
            passwordTitleLabel.setText("Confirm Contest Password");
            passwordTitleLabel.setBounds(new Rectangle(48, 128, 239, 16));
            versionTitleLabel = new JLabel();
            versionTitleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            versionTitleLabel.setBounds(new Rectangle(116, 252, 306, 23));
            versionTitleLabel.setText("Version XX. XX YYYY vv 22");
            nameTitleLabel = new JLabel();
            nameTitleLabel.setText("Contest Password");
            nameTitleLabel.setBounds(new Rectangle(48, 68, 124, 15));
            nameTitleLabel.setPreferredSize(new java.awt.Dimension(45, 16));
            centerPane = new JPanel();
            centerPane.setLayout(null);
            centerPane.setBackground(java.awt.Color.white);
            centerPane.add(getConfirmPasswordTextField(), null);
            centerPane.add(getContestPasswordTextField(), null);
            centerPane.add(nameTitleLabel, null);
            centerPane.add(versionTitleLabel, null);
            centerPane.add(passwordTitleLabel, null);
            centerPane.add(getLoginButton(), null);
            centerPane.add(getExitButton(), null);
            centerPane.add(messageLabel, null);
            centerPane.add(getProfilesComboBox(), null);
            centerPane.add(profileTitleLabel, null);
        }
        return centerPane;
    }

    /**
     * This method initializes jPasswordField
     * 
     * @return javax.swing.JPasswordField
     */
    private JPasswordField getConfirmPasswordTextField() {
        if (confirmPasswordTextField == null) {
            confirmPasswordTextField = new JPasswordField();

            confirmPasswordTextField.setBounds(new Rectangle(48, 146, 368, 20));
            confirmPasswordTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        attemptToLogin();
                    }
                }
            });
        }
        return confirmPasswordTextField;
    }

    protected void attemptToLogin() {

        setStatusMessage("");

        if (getContestPassword() == null || getContestPassword().length() < 1) {
            setStatusMessage("Enter a contest password");
            return;
        }

        if (showConfirmPassword) {
            if (getConfirmPassword() == null || getConfirmPassword().length() < 1) {
                setStatusMessage("Enter a confirmation password");
                return;
            }

            if (!getContestPassword().equals(getConfirmPassword())) {
                setStatusMessage("Contest and Confirmation passwords do not match");
                return;
            }
        }

        if (bAlreadyLoggingIn) {
            return;
        }

        bAlreadyLoggingIn = true;

        try {

            if (!showConfirmPassword) {
                confirmContestPassword(getProfile(), getContestPassword());
                dispose();
            } else {
                dispose();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Incorrect contest password try again", JOptionPane.ERROR_MESSAGE);
        } finally {
            bAlreadyLoggingIn = false;
        }
    }

    private String getBaseProfileDirectoryName(Profile profile, String dirname) {

        if (profile != null) {
            return profile.getProfilePath() + File.separator + dirname;
        } else {
            return dirname;
        }
    }

    private void confirmContestPassword(Profile selectedProfile, String contestPassword) throws Exception {

        String baseDirectoryName = getBaseProfileDirectoryName(selectedProfile, "db." + getSiteNumber());

        if (!new File(baseDirectoryName).isDirectory()) {
            throw new Exception("Missing profile db directory " + baseDirectoryName);
        }

        try {
            FileSecurity fileSecurity = new FileSecurity(baseDirectoryName);
            fileSecurity.verifyPassword(contestPassword.toCharArray());
            fileSecurity = null;
        } catch (FileSecurityException fileSecurityException) {
            if (fileSecurityException.getMessage().equals(FileSecurity.FAILED_TO_DECRYPT)) {

                throw new Exception("Incorrect contest password, try again");

            } else if (!fileSecurityException.getMessage().equals(FileSecurity.KEY_FILE_NOT_FOUND)) {

                fileSecurityException.printStackTrace();
                throw new Exception("Trouble determining contest password (" + fileSecurityException.getMessage() + ")");
            }
        } catch (Exception e) {
            e.printStackTrace(); // debug 22
            throw new Exception("Bad Trouble dude " + e.getLocalizedMessage());
        }
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JPasswordField getContestPasswordTextField() {
        if (contestPasswordTextField == null) {
            contestPasswordTextField = new JPasswordField();

            contestPasswordTextField.setBounds(new Rectangle(48, 84, 366, 20));
            contestPasswordTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (showConfirmPassword) {
                            confirmPasswordTextField.requestFocus();
                        } else {
                            attemptToLogin();
                        }
                    }
                }
            });
        }
        return contestPasswordTextField;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getLoginButton() {
        if (loginButton == null) {
            loginButton = new JButton();
            loginButton.setMnemonic(KeyEvent.VK_C);
            loginButton.setBounds(new Rectangle(50, 180, 95, 26));
            loginButton.setText("Continue");
            loginButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    messageLabel.setText("Logging in");
                    attemptToLogin();
                }
            });
        }
        return loginButton;
    }

    /**
     * This method initializes jButton1
     * 
     * @return javax.swing.JButton
     */
    private JButton getExitButton() {
        if (exitButton == null) {
            exitButton = new JButton();
            exitButton.setMnemonic(KeyEvent.VK_X);
            exitButton.setBounds(new Rectangle(336, 181, 95, 26));
            exitButton.setText("Exit");
            exitButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    promptBeforeExit();
                }
            });
        }
        return exitButton;
    }

    protected void promptBeforeExit() {

        setStatusMessage("");
        int result = FrameUtilities.yesNoCancelDialog(null, "Are you sure you want to exit?", "Exit PC^2");

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * This method initializes northPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getNorthPanel() {
        if (northPanel == null) {
            northPanel = new JPanel();
            northPanel.setLayout(new BorderLayout());
            northPanel.setBackground(java.awt.Color.white);
            northPanel.add(getDescriptionScrollpane(), BorderLayout.NORTH);
        }
        return northPanel;
    }

    /**
     * This method initializes profilesComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getProfilesComboBox() {
        if (profilesComboBox == null) {
            profilesComboBox = new JComboBox();
            profilesComboBox.setBounds(new Rectangle(175, 21, 259, 25));
        }
        return profilesComboBox;
    }

    /**
     * Display a message for the user.
     * 
     * @param messageString
     *            text to show to user
     */
    public void setStatusMessage(final String messageString) {

        Runnable messageRunnable = new Runnable() {
            public void run() {
                messageLabel.setText(messageString);

            }
        };
        SwingUtilities.invokeLater(messageRunnable);
        FrameUtilities.regularCursor(this);
    }

    private String passwordTextFieldValue(JPasswordField field) {
        char[] fieldValue = field.getPassword();
        if (fieldValue.length == 0) {
            return null;
        } else {
            return new String(fieldValue);
        }
    }

    /**
     * Fetch Contest Password.
     * 
     * @return the login name
     */
    public String getContestPassword() {
        return passwordTextFieldValue(contestPasswordTextField);
    }

    /**
     * Fetch confirmation for contest password.
     * 
     * @return confirmation password
     */
    private String getConfirmPassword() {
        return passwordTextFieldValue(confirmPasswordTextField);
    }

    public void disableLoginButton() {
        getLoginButton().setEnabled(false);
    }

    public Profile getProfile() {
        ProfileWrapper wrapper = (ProfileWrapper) getProfilesComboBox().getSelectedItem();
        if (wrapper != null) {
            return wrapper.getProfile();
        }
        return null;
    }

    public int getSiteNumber() {
        return siteNumber;
    }

    public void setSiteNumber(int siteNumber) {
        this.siteNumber = siteNumber;
    }

    /**
     * This method initializes topTextPane
     * 
     * @return javax.swing.JTextPane
     */
    private JTextPane getTopTextPane() {
        if (topTextPane == null) {
            topTextPane = new JTextPane();
            topTextPane.setPreferredSize(new Dimension(6, 210));
            topTextPane.setEditorKit(new HTMLEditorKit());
            topTextPane.setText(firstLoginText);
            topTextPane.setEditable(false);
        }
        return topTextPane;
    }

    /**
     * This method initializes descriptionScrollpane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getDescriptionScrollpane() {
        if (descriptionScrollpane == null) {
            descriptionScrollpane = new JScrollPane();
            descriptionScrollpane.setViewportView(getTopTextPane());
        }
        return descriptionScrollpane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
