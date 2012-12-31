package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ILoginListener;
import edu.csus.ecs.pc2.core.model.LoginEvent;

/**
 * Login frame for all clients.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public class LoginFrame extends JFrame implements ILoginUI {

    /**
     * 
     */
    private static final long serialVersionUID = -6389607881992853161L;

    private static final String AUTO_REGISTRATION_LOGIN = "auto";

    private IInternalContest contest;

    private IInternalController controller;

    private JPanel centerPane = null;

    private JPasswordField passwordTextField = null;

    private JTextField loginTextField = null;

    private JLabel nameTitleLabel = null;

    private JLabel versionTitleLabel = null;

    private JLabel mainTitleTopLabel = null;

    private JLabel passwordTitleLabel = null;

    private JButton loginButton = null;

    private JButton exitButton = null;

    private JLabel messageLabel = null;

    private JLabel mainTitleBottomLabel = null;

    private LogWindow logWindow = null;

    private JPanel mainPanel;

    private JPanel westPanel;

    private JLabel logoCSUS = null;

    private JPanel bottomPanel = null;

    private JLabel logoICPC = null;

    private JPanel northPanel = null;

    private JLabel spacerLabel = null;

    private Boolean bAlreadyLoggingIn = false;
    
    private AutoRegistrationFrame autoRegistrationFrame = null;

    /**
     * This method initializes
     * 
     */
    public LoginFrame() {
        super();
        initialize();
        overRideLookAndFeel();
        FrameUtilities.centerFrame(this);
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(628,430));
        this.setPreferredSize(new java.awt.Dimension(628,430));
        this.setMinimumSize(new java.awt.Dimension(628,430));
        this.setBackground(new java.awt.Color(253, 255, 255));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("PC^2 Login");
        this.setContentPane(getMainPanel());

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                promptBeforeExit();
            }
        });

        VersionInfo versionInfo = new VersionInfo();
        versionTitleLabel.setText("PC^2 version " + versionInfo.getVersionNumber() + " " + versionInfo.getBuildNumber());
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
            mainPanel.add(getWestPanel(), java.awt.BorderLayout.WEST);
            mainPanel.add(getBottomPanel(), java.awt.BorderLayout.SOUTH);
            mainPanel.add(getNorthPanel(), java.awt.BorderLayout.NORTH);
        }

        return mainPanel;
    }

    private JPanel getWestPanel() {
        if (westPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setVgap(30);
            flowLayout.setHgap(5);
            westPanel = new JPanel();
            westPanel.setLayout(flowLayout);
            westPanel.setMinimumSize(new java.awt.Dimension(130, 132));
            westPanel.setPreferredSize(new java.awt.Dimension(140, 132));
            westPanel.setBackground(java.awt.Color.white);
            westPanel.add(getLogoCSUS(), null);
        }

        return westPanel;
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPasswordTitleLabel() {
        if (centerPane == null) {
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.insets = new java.awt.Insets(3,66,2,54);
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.gridy = 5;
            gridBagConstraints7.ipadx = 360;
            gridBagConstraints7.ipady = 26;
            gridBagConstraints7.gridwidth = 2;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.insets = new java.awt.Insets(7,52,3,130);
            gridBagConstraints6.gridy = 4;
            gridBagConstraints6.ipadx = 40;
            gridBagConstraints6.gridx = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.insets = new java.awt.Insets(7,32,3,76);
            gridBagConstraints5.gridy = 4;
            gridBagConstraints5.ipadx = 30;
            gridBagConstraints5.gridx = 0;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.insets = new java.awt.Insets(12,32,1,51);
            gridBagConstraints4.gridy = 2;
            gridBagConstraints4.ipadx = 62;
            gridBagConstraints4.gridx = 0;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.insets = new java.awt.Insets(2,99,11,75);
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.gridy = 6;
            gridBagConstraints3.ipadx = 158;
            gridBagConstraints3.ipady = 7;
            gridBagConstraints3.gridwidth = 2;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.insets = new java.awt.Insets(23,32,0,87);
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.ipadx = 39;
            gridBagConstraints2.ipady = -1;
            gridBagConstraints2.gridx = 0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridwidth = 2;
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 1;
            gridBagConstraints1.ipadx = 362;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new java.awt.Insets(1,32,12,82);
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.ipadx = 364;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(1,32,7,80);
            messageLabel = new JLabel();
            messageLabel.setForeground(Color.red);
            messageLabel.setText("");
            messageLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            passwordTitleLabel = new JLabel();
            passwordTitleLabel.setText("Password");
            versionTitleLabel = new JLabel();
            versionTitleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            versionTitleLabel.setText("Version XX. XX YYYY vv 22");
            nameTitleLabel = new JLabel();
            nameTitleLabel.setText("Name");
            nameTitleLabel.setPreferredSize(new java.awt.Dimension(45, 16));
            centerPane = new JPanel();
            centerPane.setLayout(new GridBagLayout());
            centerPane.setBackground(java.awt.Color.white);
            centerPane.add(getPasswordTextField(), gridBagConstraints);
            centerPane.add(getLoginTextField(), gridBagConstraints1);
            centerPane.add(nameTitleLabel, gridBagConstraints2);
            centerPane.add(versionTitleLabel, gridBagConstraints3);
            centerPane.add(passwordTitleLabel, gridBagConstraints4);
            centerPane.add(getLoginButton(), gridBagConstraints5);
            centerPane.add(getExitButton(), gridBagConstraints6);
            centerPane.add(messageLabel, gridBagConstraints7);
        }
        return centerPane;
    }

    /**
     * This method initializes jPasswordField
     * 
     * @return javax.swing.JPasswordField
     */
    private JPasswordField getPasswordTextField() {
        if (passwordTextField == null) {
            passwordTextField = new JPasswordField();

            passwordTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        if (getLoginButton().isEnabled()) {
                            attemptToLogin();
                        } // else server probably was not reachable
                    }
                }
            });
        }
        return passwordTextField;
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getLoginTextField() {
        if (loginTextField == null) {
            loginTextField = new JTextField();

            loginTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        passwordTextField.requestFocus();
                        checkToShowAutoRegForm();
                    }
                }
            });
        }
        return loginTextField;
    }

    protected boolean checkToShowAutoRegForm() {
        if (AUTO_REGISTRATION_LOGIN.equalsIgnoreCase(loginTextField.getText())) {
            getAutoRegistrationFrame().setVisible(true);
            setVisible(false);
            return true;
        }
        return false;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getLoginButton() {
        if (loginButton == null) {
            loginButton = new JButton();
            loginButton.setMnemonic(KeyEvent.VK_L);
            loginButton.setText("Login");
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
     * User hit ok, attempt to login.
     */
    protected void attemptToLogin() {

        setStatusMessage("");
        
        if (checkToShowAutoRegForm()) {
            return;
        }
        
        if (getLoginName() == null || getLoginName().length() < 1) {
            setStatusMessage("Please enter a login");
        } else {

            if (getLoginName().toLowerCase().startsWith("log")) {
                logWindow.setVisible(true);
                return;
            }
            
            synchronized (bAlreadyLoggingIn) {
                if (bAlreadyLoggingIn) {
                    return;
                }
                bAlreadyLoggingIn = true;
            }
            
            try {
                setStatusMessage("Logging in...");
                FrameUtilities.waitCursor(this);
                controller.login(getLoginName(), getPassword());
            } catch (Exception e) {
                // TODO: log handle exception
                setStatusMessage(e.getMessage());
                StaticLog.info("Login not successful: " + e.getMessage());
                System.err.println("Login not successful: " + e.getMessage());
                bAlreadyLoggingIn = false;
            }
        }
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
     * This method initializes logoCSUS
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getLogoCSUS() {
        if (logoCSUS == null) {
            logoCSUS = new JLabel();

            ImageIcon image = loadImageIconFromFile("images/csus_logo.png");
            logoCSUS.setIcon(image);
            logoCSUS.setBounds(new java.awt.Rectangle(-11, 48, 137, 127));
        }
        return logoCSUS;
    }

    /*
     * Given a inFileName attempts to find file in jar, otherwise falls back to file system.
     * 
     * Will return null if file is not found in either location.
     */
    private ImageIcon loadImageIconFromFile(String inFileName) {
        File imgFile = new File(inFileName);
        ImageIcon icon = null;
        // attempt to locate in jar
        URL iconURL = getClass().getResource("/"+inFileName);
        if (iconURL == null) {
            if (imgFile.exists()) {
                try {
                    iconURL = imgFile.toURI().toURL();
                } catch (MalformedURLException e) {
                    iconURL = null;
                    StaticLog.log("LoginFrame.loadImageIconFromFile("+inFileName+")", e);
                }
            }
        }
        if (iconURL != null) {
            if (verifyImage(inFileName, iconURL)) {
                icon = new ImageIcon(iconURL);
            } else {
                StaticLog.warning(inFileName+"("+iconURL.toString()+") checksum failed");
            }
        }
        return icon;
    }

    private boolean verifyImage(String inFileName, URL url) {
        // these are the real checksums
        byte[] csusChecksum = { -78, -82, -33, 125, 3, 20, 3, -51, 53, -82, -66, -19, -96, 82, 39, -92, 16, 52, 17, 127};
        byte[] icpcChecksum = { -9, -91, 66, 44, 57, 117, 47, 58, 103, -17, 31, 53, 10, 6, 100, 68, 0, 127, -103, -58};
        // these are the checkums from java jvm under microsoft
        byte[] csusChecksum2 = { 98, 105, -19, -31, -71, -121, 109, -34, 64, 83, -78, -31, 49, -57, 57, 8, 35, -79, 13, -49};
        byte[] icpcChecksum2 = { 70, -55, 53, -41, 127, 102, 30, 95, -55, -13, 11, -11, -31, -103, -107, -31, 119, 25, -98, 14};
        // these are the ibm jre checksums
        byte[] csusChecksum3 = {-46, -84, -66, 55, 82, -78, 124, 88, 68, -83, -128, -110, -19, -26, 92, -3, 76, -26, 21, 30};
        byte[] icpcChecksum3 = {41, 72, 104, 75, 73, 55, 55, 93, 32, 35, -6, -12, -96, -23, -3, -17, -119, 26, 81, -2};
        
        // this is the jdk 1.7 checksum for icpc_banner
        byte[] icpcChecksum4 = {35, 40, -57, -30, -7, 121, 59, 25, 3, 26, -82, -75, -65, 123, 54, -57, 39, 36, -3, 107};

        byte[] verifyChecksum;
        
        try {
            int matchedBytes = 0;
            InputStream is = url.openStream();
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.reset();
            byte[] b = new byte[1024];
            while(is.read(b) > 0) {
                md.update(b);
            }
            byte[] digested = md.digest();
            if (inFileName.equals("images/csus_logo.png")) {
                switch (digested[0]) {
                    case 98:
                        verifyChecksum = csusChecksum2;
                        break;
                    case -46:
                        verifyChecksum = csusChecksum3;
                        break;
                    default:
                        verifyChecksum = csusChecksum;
                        break;
                } 
            } else {
                switch (digested[0]) {
                    case 70:
                        verifyChecksum = icpcChecksum2;
                        break;
                    case 41:
                        verifyChecksum = icpcChecksum3;
                        break;
                    case 35:
                        verifyChecksum = icpcChecksum4;
                        break;
                    default:
                        verifyChecksum = icpcChecksum;
                        break;
                } 
            }

            if (edu.csus.ecs.pc2.core.Utilities.isDebugMode()) {
                System.out.println ();
                System.out.println (inFileName);
                System.out.print ("byte[] ChecksumX = {");
                 
                for (int i = 0; i < digested.length; i++) {
                    System.out.print(digested[i]);
                    if (i < digested.length -1) {
                        System.out.print(", ");
                    }
                }
                System.out.println("};");
            }
            
            for (int i = 0; i < digested.length; i++) {
                if (digested[i] == verifyChecksum[i]) {
                    matchedBytes++;
                } else {
                    break;
                }
            }
            return(matchedBytes == verifyChecksum.length);
        } catch (IOException e) {
            StaticLog.log("verifyImage("+inFileName+")", e);
        } catch (NoSuchAlgorithmException e) {
            StaticLog.log("verifyImage("+inFileName+")", e);
        }
        
        return false;
    }

    /**
     * This method initializes bottomPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getBottomPanel() {
        if (bottomPanel == null) {
            logoICPC = new JLabel();

            ImageIcon image = loadImageIconFromFile("images/icpc_banner.png");
            logoICPC.setIcon(image);
            bottomPanel = new JPanel();
            bottomPanel.setBackground(java.awt.Color.white);
            bottomPanel.add(logoICPC, null);
        }
        return bottomPanel;
    }

    /**
     * This method initializes northPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getNorthPanel() {
        if (northPanel == null) {
            spacerLabel = new JLabel();
            spacerLabel.setText(" ");
            northPanel = new JPanel();
            northPanel.setLayout(new BorderLayout());
            northPanel.setBackground(java.awt.Color.white);
            mainTitleBottomLabel = new JLabel();
            mainTitleBottomLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mainTitleBottomLabel.setHorizontalTextPosition(SwingConstants.CENTER);
            mainTitleBottomLabel.setText("Programming Contest Control System");
            mainTitleBottomLabel.setBackground(java.awt.Color.white);
            mainTitleBottomLabel.setFont(new java.awt.Font("Dialog", Font.BOLD, 26));
            mainTitleTopLabel = new JLabel();
            mainTitleTopLabel.setFont(new Font("Dialog", Font.BOLD, 22));
            mainTitleTopLabel.setText("California State University, Sacramento");
            mainTitleTopLabel.setHorizontalAlignment(SwingConstants.CENTER);
            northPanel.add(mainTitleTopLabel, java.awt.BorderLayout.CENTER);
            northPanel.add(mainTitleBottomLabel, java.awt.BorderLayout.SOUTH);
            northPanel.add(spacerLabel, java.awt.BorderLayout.NORTH);

        }
        return northPanel;
    }

    public static void main(String[] args) {
        new LoginFrame().setVisible(true);
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

    /**
     * Fetch Login name for client.
     * 
     * @return the login name
     */
    private String getLoginName() {
        return loginTextField.getText();
    }

    /**
     * fetch password for client.
     * 
     * @return the password
     */
    private String getPassword() {
        return new String(passwordTextField.getPassword());
    }

    /**
     * A login listener
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    public class LoginListenerImplementation implements ILoginListener {

        public void loginAdded(LoginEvent event) {
            // TODO log this.
            // System.err.println("Login " + event.getAction() + " " + event.getClientId());
        }

        public void loginRemoved(LoginEvent event) {
            // TODO log this.
            // System.err.println("Login " + event.getAction() + " " + event.getClientId());
        }

        public void loginDenied(LoginEvent event) {
            setStatusMessage(event.getMessage());
            bAlreadyLoggingIn = false;
        }
        
        public void loginRefreshAll(LoginEvent event) {
            // TODO Auto-generated method stub
            
        }
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        // initialize logWindow so it can add itself as a listener and
        // start populating the mclb
        logWindow = new LogWindow();
        logWindow.setContestAndController(contest, controller);

        contest.addLoginListener(new LoginListenerImplementation());

        getAutoRegistrationFrame().setContestAndController(inContest, inController);
        
        setVisible(true);
    }

    public String getPluginTitle() {
        return "Login";
    }

    public void disableLoginButton() {
        getLoginButton().setEnabled(false);
    }
    
    public AutoRegistrationFrame getAutoRegistrationFrame() {
        if (autoRegistrationFrame == null) {
            autoRegistrationFrame = new AutoRegistrationFrame();
            autoRegistrationFrame.setParentFrame(this);
            autoRegistrationFrame.setController(controller);
        }
        return autoRegistrationFrame;
    }
    
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        getLoginTextField().setText("");
        getLoginTextField().requestFocus();
    }

    public void regularCursor() {
        FrameUtilities.regularCursor(this);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
