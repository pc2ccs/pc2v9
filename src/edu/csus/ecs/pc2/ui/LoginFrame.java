// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Rectangle;
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
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

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
    
    private final String PC2_LOGO_FILENAME = "PC2Logo.png";
    private final String ICPC_BANNER_FILENAME = "ICPCWebMast_small.png";
    private final String CSUS_LOGO_FILENAME = "csus_logo.png";
    private final String USER_PROVIDED_LOGO_FILENAME = "logo";
    private final String USER_PROVIDED_BANNER_FILENAME = "banner";
    
    private final int INITIAL_FRAME_WIDTH = 800 ;

    private IInternalContest contest;

    private IInternalController controller;

    private JPanel centerPane = null;

    private JPasswordField passwordTextField = null;

    private JTextField loginTextField = null;

    private JLabel nameTitleLabel = null;

    private JLabel versionTitleLabel = null;

    private JLabel passwordTitleLabel = null;

    private JButton loginButton = null;

    private JButton exitButton = null;

    private JLabel messageLabel = null;

    private LogWindow logWindow = null;

    private JPanel mainPanel;

    private JPanel westPanel;

    private JLabel logoCSUS = null;

    private JPanel bottomPanel = null;

    private JPanel northPanel = null;

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
     * This method initializes this LoginFrame to contain a main panel containing a title display, image icons,
     * and a set of account name/password login text fields.
     * 
     */
    private void initialize() {

        this.setSize(new java.awt.Dimension(INITIAL_FRAME_WIDTH,500));
        this.setPreferredSize(new java.awt.Dimension(INITIAL_FRAME_WIDTH,500));
        this.setMinimumSize(new java.awt.Dimension(INITIAL_FRAME_WIDTH,500));
        this.setBackground(new java.awt.Color(253, 255, 255));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("PC^2 Login");
        ImageIcon imgIcon = new ImageIcon("images/" + PC2_LOGO_FILENAME);
        this.setIconImage(imgIcon.getImage());
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

    /**
     * Returns a JPanel containing a north panel with title text and logos, a center panel with textfields for entering login information
     * along with control buttons, a west panel containing an appropriate contest logo, and a south panel containing an approprate banner image.
     * 
     * @return a JPanel populated with appropriate login frame components
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());

            mainPanel.add(getNorthPanel(), java.awt.BorderLayout.NORTH);
            mainPanel.add(getMainLoginInfoPanel(), java.awt.BorderLayout.CENTER);
            mainPanel.add(getWestPanel(), java.awt.BorderLayout.WEST);
            mainPanel.add(getBottomBannerPanel(), java.awt.BorderLayout.SOUTH);
        }

        return mainPanel;
    }

    private JPanel getWestPanel() {
        if (westPanel == null) {
            
            westPanel = new JPanel();
            westPanel.setBackground(java.awt.Color.white);
            
            westPanel.add(getMainLogoPanel(), null);            

//            westPanel.setMinimumSize(new java.awt.Dimension(130, 132));
//            westPanel.setPreferredSize(new java.awt.Dimension(140, 132));
        }

        return westPanel;
    }

    /**
     * This method returns a JPanel containing the main components used for logging in to PC2: textfields for name and password, Login and Exit buttons,
     * and message area for displaying approprate status messages (initially empty).
     * 
     * @return a JPanel with login components
     */
    private JPanel getMainLoginInfoPanel() {
        if (centerPane == null) {
            
            //message label layout constraints
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.insets = new java.awt.Insets(3,66,2,54);
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.gridy = 5;
            gridBagConstraints7.ipadx = 360;
            gridBagConstraints7.ipady = 26;
            gridBagConstraints7.gridwidth = 2;
            
            //exit button layout constraints
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.anchor = GridBagConstraints.WEST;
            gridBagConstraints6.insets = new java.awt.Insets(7,52,3,130);
            gridBagConstraints6.gridy = 4;
            gridBagConstraints6.ipadx = 40;
            gridBagConstraints6.gridx = 1;
            
            //login button layout constraints
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.insets = new java.awt.Insets(7,32,3,76);
            gridBagConstraints5.gridy = 4;
            gridBagConstraints5.ipadx = 30;
            gridBagConstraints5.gridx = 0;
            
            //login-name title label layout constraints
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.insets = new java.awt.Insets(23,20,1,51);
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.ipadx = 39;

            //password title label layout constraints
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.insets = new java.awt.Insets(12,20,1,60);
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.gridy = 2;
            gridBagConstraints4.ipadx = 39;
                        
            //version title label layout constraints
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.insets = new java.awt.Insets(2,99,11,75);
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.gridy = 6;
            gridBagConstraints3.ipadx = 158;
            gridBagConstraints3.ipady = 7;
            gridBagConstraints3.gridwidth = 2;
            
            //login textfield layout constraints
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.anchor = GridBagConstraints.WEST;
            gridBagConstraints1.fill = java.awt.GridBagConstraints.NONE;
            gridBagConstraints1.gridwidth = 2;
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 1;
            gridBagConstraints1.ipadx = 362;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new java.awt.Insets(1,32,12,82);
            
            //password textfield layout constraints
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.fill = java.awt.GridBagConstraints.NONE;
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
            
            //TODO: enable this border to see password title boundary
//            passwordTitleLabel.setBorder(new LineBorder(Color.green));
            
            versionTitleLabel = new JLabel();
            versionTitleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            versionTitleLabel.setText("Version XX. XX YYYY vv 22");
            
            nameTitleLabel = new JLabel();
            nameTitleLabel.setText("Team Login Name");
            nameTitleLabel.setPreferredSize(new java.awt.Dimension(65, 16));
            
            //TODO: enable this border to see login-name title boundary
//            nameTitleLabel.setBorder(new LineBorder(Color.green));
            
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
            
            //TODO: enable this statement to see the border around the main "login/password" screen panel
//            centerPane.setBorder(new LineBorder(Color.red));
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
            loginButton.registerKeyboardAction(loginButton.getActionForKeyStroke(
                    KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                    KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                    JComponent.WHEN_FOCUSED);
            loginButton.registerKeyboardAction(loginButton.getActionForKeyStroke(
                    KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                    KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                    JComponent.WHEN_FOCUSED);
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
            exitButton.registerKeyboardAction(exitButton.getActionForKeyStroke(
                    KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                    KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                    JComponent.WHEN_FOCUSED);
            exitButton.registerKeyboardAction(exitButton.getActionForKeyStroke(
                    KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                    KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                    JComponent.WHEN_FOCUSED);

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
     * This method returns a JLabel containing the CSUS logo
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getLogoCSUS() {
        if (logoCSUS == null) {
            logoCSUS = new JLabel();

            ImageIcon image = loadImageIconFromFile("images/" + CSUS_LOGO_FILENAME);
            logoCSUS.setIcon(image);
            logoCSUS.setBounds(new java.awt.Rectangle(-11, 48, 137, 127));
        }
        return logoCSUS;
    }

    /**
     * This method returns an ImageIcon containing the PC2 logo.
     * 
     * @return a PC2 Logo ImageIcon
     */
    private ImageIcon getPC2LogoImageIcon() {

        ImageIcon imageIcon = loadImageIconFromFile("images/" + PC2_LOGO_FILENAME);

        return imageIcon;
    }
    
    /*
     * Given a inFileName attempts to find file in jar, otherwise falls back to file system.
     * 
     * Will return null if file is not found in either location.
     */
    private ImageIcon loadImageIconFromFile(String inFileName) {
        File imgFile = new File(inFileName);
        ImageIcon icon = null;
        // attempt to locate image file in jar
        URL iconURL = getClass().getResource("/"+inFileName);
        if (iconURL == null) {
            //we didn't find the image file in the jar; look for it in the file system
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
            //we found a URL to the image; verify that it has the correct checksum
            if (verifyImage(inFileName, iconURL)) {
                //checksums match; return an ImageIcon for the image
                icon = new ImageIcon(iconURL);
            } else {
                StaticLog.warning(inFileName+"("+iconURL.toString()+") checksum failed");
            }
        }
        return icon;
    }

    /**
     * This method verifies that the file whose filename and corresponding URL are provided are legitimate --
     * that is, that the files have the expected SHA checksum values. It first reads the file from the 
     * specified URL, then uses the {@link MessageDigest} class to compute an SHA checksum for that file.
     * It then uses the given String filename and uses that to select the "expected" checksum for the file,
     * returning true if the checksums match, false otherwise.
     * 
     * @param inFileName the name of the file to be verified
     * @param url a URL pointing to an ImageIcon for the file
     * 
     * @return true if the SHA checksum for the image at the URL matches the expected checksum; false if not
     */
    private boolean verifyImage(String inFileName, URL url) {
        // these are the real (correct) checksums for the specified files, generated on a variety of platforms
        
        //CSUS Logo (images/csus_logo.png) checksums:
        
        //generated under Win8.1 w/ java 1.8.0_201; verified the same on Win10 w/ java 1.8.0_201:
        byte[] csusChecksum = { -78, -82, -33, 125, 3, 20, 3, -51, 53, -82, -66, -19, -96, 82, 39, -92, 16, 52, 17, 127};

        // generated under Windows10 running java version "1.8.0_144" and ubuntu running "1.8.0_131":
        byte[] csusChecksum2 = { 98, 105, -19, -31, -71, -121, 109, -34, 64, 83, -78, -31, 49, -57, 57, 8, 35, -79, 13, -49};
        
        // these are the ibm jre checksums
        byte[] csusChecksum3 = {-46, -84, -66, 55, 82, -78, 124, 88, 68, -83, -128, -110, -19, -26, 92, -3, 76, -26, 21, 30};
        
        
        //ICPC banner (images/ICPCWebMast_small.png) checksums:

        // old icpc_logo.png checksums
//      byte[] icpcChecksum = {-116, -88, -24, 46, 99, 102, -94, -64, -28, -61, 51, 4, -52, -116, -23, 92, 51, -78, -90, -107};
//      byte[] icpcChecksum2 = { 70, -55, 53, -41, 127, 102, 30, 95, -55, -13, 11, -11, -31, -103, -107, -31, 119, 25, -98, 14};

        byte[] icpcChecksum3 = {41, 72, 104, 75, 73, 55, 55, 93, 32, 35, -6, -12, -96, -23, -3, -17, -119, 26, 81, -2};
        
        // this is the eclipse checksum
        byte[] icpcChecksum4 = {47, -56, 88, -115, 40, 20, 98, -6, 99, 49, -17, 37, 74, -77, 0, -74, 55, -100, 9, -118};
      
        // new 20180924 generated on win10 java9; verified the same on Win8.1 w/ java 1.8.0_201
        byte[] icpcChecksum = {119, 107, 9, -52, 56, 121, 125, -115, -2, -40, 53, 86, 113, 4, 87, 42, 83, 118, 117, -2};
        
        // mac java8
        byte[] icpcChecksum2 = {-20, -110, 63, 117, -52, 4, -125, 31, 47, 92, 13, 97, 91, -28, -55, -28, 65, -106, 106, -24};
        
        
        //PC2 Logo (images/PC2Logo.png) checksums:
        
        //generated on Win10 w/ java 1.8.0_201; verified the same on Win8.1 w/ java 1.8.0_201:
        byte[] pc2Checksum = {-58, -108, 63, 33, 72, -127, -38, 75, 78, 104, -102, 119, -128, 96, 11, -86, 100, -74, -109, 9};

        
        //an array to hold the checksum which is chosen from the above:
        byte[] verifyChecksum = { };
        
        try {
            //compute the checksum for the ImageIcon whose URL was passed to us
            int matchedBytes = 0;
            InputStream is = url.openStream();
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.reset();
            byte[] b = new byte[1024];
            while(is.read(b) > 0) {
                md.update(b);
            }
            byte[] digested = md.digest();  //"digested" now holds the image checksum
            
            //find the appropriate "verifyChecksum" for the current image file
            if (inFileName.equals("images/" + CSUS_LOGO_FILENAME)) {
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
            } else if (inFileName.equalsIgnoreCase("images/" + PC2_LOGO_FILENAME)) {
                switch (digested[0]) {
                    case -58:
                        verifyChecksum = pc2Checksum ;
                        break;
                    //TODO: add cases here for pc2checksums computed on other platforms
                    default:
                        verifyChecksum = pc2Checksum;
                        break;
                }
            } else if (inFileName.equals("images/" + ICPC_BANNER_FILENAME)){
                switch (digested[0]) {
                    case -20:
                        verifyChecksum = icpcChecksum2;
                        break;
                    case 41:
                        verifyChecksum = icpcChecksum3;
                        break;
                    case 47:
                        verifyChecksum = icpcChecksum4;
                        break;
                    default:
                        verifyChecksum = icpcChecksum;
                        break;
                } 
            } else {
                //if we get here, the file we were given doesn't match any of the expected/known files we want to check; 
                // default to the CSUS checksum, which should cause the checksum verification (below) to fail
                verifyChecksum = csusChecksum;
            }

            //if in debug mode, print out the calculated checksum values for the specified image
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
            
            //count the number of byte in the calculated checksum which match the expected checksum
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
     * This method creates a JPanel containing a banner image -- either the default ICPC banner or (if provided) a user-specified banner.
     * 
     * @return a JPanel containing a banner image
     */
    private JPanel getBottomBannerPanel() {
        if (bottomPanel == null) {
            
            bottomPanel = new JPanel();
            bottomPanel.setBackground(java.awt.Color.white);

           // the image to be placed on the panel
           ImageIcon bannerImage;
           boolean useICPCBanner = true;

           // check if user has provided a banner file
           File bannerFilePNG = new File("images/" + USER_PROVIDED_BANNER_FILENAME + ".png");
           File bannerFileJPG = new File("images/" + USER_PROVIDED_BANNER_FILENAME + ".jpg");
           if (bannerFilePNG.exists() || bannerFileJPG.exists()) {
               // yes, there is a user-provided banner file
               if (bannerFilePNG.exists()) {
                   bannerImage = new ImageIcon("images/" + USER_PROVIDED_BANNER_FILENAME + ".png");
               } else {
                   bannerImage = new ImageIcon("images/" + USER_PROVIDED_BANNER_FILENAME + ".jpg");
               }
               useICPCBanner = false;
           } else {
               // no user-provided banner file; use the ICPC banner
               bannerImage = loadImageIconFromFile("images/" + ICPC_BANNER_FILENAME);

           }

           // if not using ICPC banner, scale the bannerImage to insure it fits
           if (!useICPCBanner) {
               int width = bannerImage.getIconWidth();
               int height = bannerImage.getIconHeight();
               
               //make sure the user banner fits across the frame width
               if (width > INITIAL_FRAME_WIDTH-20) {
                   width = INITIAL_FRAME_WIDTH-20;
               }
               
               //get the ICPC Banner, to use its height for max banner height if user banner exceeds that
               ImageIcon icpcBanner = loadImageIconFromFile("images/" + ICPC_BANNER_FILENAME);
               if (height > icpcBanner.getIconHeight()) {
                   height = icpcBanner.getIconHeight();
               }
               
               //scale the user's banner to match the available dimensions
               bannerImage = new ImageIcon(bannerImage.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));

           }

           JLabel bannerLabel = new JLabel(bannerImage);
           
           
           // TODO: delete border
//           logoLabel.setBorder(new LineBorder(Color.black));
           
           // put some space around the banner
           Border empty = new EmptyBorder(2, 5, 2, 5);
           
           // TODO: enable this border (and change the following statement to "setBorder(compound)") to see boundary around bottom panel
//           Border orangeline = BorderFactory.createLineBorder(Color.ORANGE);
//           Border compound = BorderFactory.createCompoundBorder(orangeline, empty);

           bottomPanel.setBorder(empty);
//           bottomPanel.setBorder(compound);

           bottomPanel.add(bannerLabel, null);
        }
        return bottomPanel;
    }

    /**
     * This method initializes northPanel, the panel at the top of the Login screen which contains the system title, 
     * the PC2 Logo, and (if the user provided their own "logo" file) the 
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getNorthPanel() {
        if (northPanel == null) {
            
            northPanel = new JPanel();
            northPanel.setLayout(new BorderLayout());
            northPanel.setBackground(java.awt.Color.white);
            
            //put space at the top, below the title bar
            JLabel spacerLabel = new JLabel();
            spacerLabel.setText(" ");
            northPanel.add(spacerLabel, java.awt.BorderLayout.NORTH);
            
            //TODO: enable this statement to see border around top spacer area
//            spacerLabel.setBorder (new LineBorder(Color.CYAN));

            //add main title to center of North Panel
            northPanel.add(getTitlePanel(),BorderLayout.CENTER);
            
            //add CSUS logo to west side of north panel (will be empty if the CSUS logo belongs on the main west panel)
            northPanel.add(getNorthWestLogoPanel(),BorderLayout.WEST);
            
            //add PC2 logo to east side of north panel
            northPanel.add(getNorthEastLogoPanel(),BorderLayout.EAST);
            
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
    
    /**
     * Returns a JPanel containing the PC2 Login Frame title sequence.
     * 
     * @return a JPanel with JLabels containing text
     */
    private JPanel getTitlePanel() {
        
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.white);
        titlePanel.setPreferredSize(new Dimension(300,120));
 
        //TODO: enable this statement to see the border around the top title panel
//        titlePanel.setBorder(new LineBorder(Color.MAGENTA));
        
        JLabel mainTitleTopLabel = new JLabel();
        mainTitleTopLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        mainTitleTopLabel.setText("California State University, Sacramento's");
        mainTitleTopLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(mainTitleTopLabel);

        //TODO: enable this statement to see the border around the top label in the title
//        mainTitleTopLabel.setBorder(new LineBorder(Color.RED));
        
        JLabel mainTitleBottomLabel = new JLabel();
        mainTitleBottomLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainTitleBottomLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        mainTitleBottomLabel.setText("<html>PC<sup>2</sup> <u>P</u>rogramming <u>C</u>ontest <u>C</u>ontrol System</html>");
        mainTitleBottomLabel.setBackground(java.awt.Color.white);
        mainTitleBottomLabel.setFont(new java.awt.Font("Dialog", Font.BOLD, 26));
        titlePanel.add(mainTitleBottomLabel);        
        
        //TODO: enable this statement to see the border around the bottom label in the title
//        mainTitleBottomLabel.setBorder(new LineBorder(Color.GREEN));
        
        return titlePanel;
    }
    
    /**
     * Returns a JPanel containing the CSUS Logo if the user has provided a separate "logo.png" or "logo.jpg" file, or an empty
     * panel if no user-provided logo file is found.   The effect is that IF the user provided a logo, that user-provided logo will be displayed
     * in the main logo area with the CSUS Logo displayed in smaller form in the NorthWest, whereas if the user did NOT provide a logo file
     * then we'll leave the NorthWest panel blank and instead display the CSUS logo in the main logo display area (as it appeared in earlier
     * versions of PC2).
     * 
     * @return a JPanel containing the CSUS logo if the user has provided a separate "main logo" file; otherwise returns an empty JPanel
     */
    private JPanel getNorthWestLogoPanel() {
        
        JPanel northWestPanel = new JPanel();
        northWestPanel.setBackground(Color.white);
        
        //TODO: enable this statement, together with ones below, to see the border around the top left logo panel
//        Border orangeline = BorderFactory.createLineBorder(Color.ORANGE);
        
        
        //add CSUS logo to north west panel, but only if user has provided a main logo file (otherwise the CSUS Logo goes on the main west panel)
        File mainLogoFilePNG = new File ("images/logo.png");
        File mainLogoFileJPG = new File ("images/logo.jpg");
        if (mainLogoFilePNG.exists() || mainLogoFileJPG.exists()) {
            //yes, there is a logo file for the main west panel; put the CSUS logo in the north west panel
            JLabel csusLogoLabel = getLogoCSUS();
            csusLogoLabel.setBounds(new Rectangle(0, 40, 80, 70));
            ImageIcon originalImage = new ImageIcon("images/csus_logo.png");
            ImageIcon scaledImage = new ImageIcon(
                    originalImage.getImage().getScaledInstance(
                            ((int) (originalImage.getIconWidth() * 0.6)), 
                            ((int) (originalImage.getIconHeight() * 0.6)), 
                            Image.SCALE_SMOOTH));
            JLabel csusLabel = new JLabel(scaledImage);
            
            //put some space around the logo
            Border empty = new EmptyBorder(10, 5, 10, 5);
            
            //TODO: enable this statement (together with one above and one below) to see the top left logo panel border
//            Border compound = BorderFactory.createCompoundBorder(orangeline, empty);
            
            northWestPanel.setBorder(empty);
//            northWestPanel.setBorder(compound);


            //TODO: enable this statement to see the border around the CSUS label in the top left
//            csusLabel.setBorder(new LineBorder(Color.black));

            northWestPanel.add(csusLabel);
        } else {
            //we're leaving the logo empty (it'll go on the main panel); add some spacing
            Border empty = new EmptyBorder(10, 20, 10, 20);
//            Border compound = BorderFactory.createCompoundBorder(orangeline, empty);
            northWestPanel.setBorder(empty);
//          northWestPanel.setBorder(compound);
        }
        
        return northWestPanel;
    }
    
    
    /**
     * Returns a JPanel containing the PC2 Logo.
     * 
     * @return a JPanel containing the PC2 logo
     */
    private JPanel getNorthEastLogoPanel() {

        JPanel northEastPanel = new JPanel();
        northEastPanel.setBackground(Color.white);

        // TODO: enable this statement (and ones below) to see the border around the northeast (PC2Logo) panel
//        Border blueline = BorderFactory.createLineBorder(Color.BLUE);

        // put some space around the logo
        Border empty = new EmptyBorder(7, 5, 10, 5);
//        Border compound = BorderFactory.createCompoundBorder(blueline, empty);
        northEastPanel.setBorder(empty);
//        northEastPanel.setBorder(compound);

        // add the PC2 logo to the north east panel
        ImageIcon pc2ImageIcon = getPC2LogoImageIcon();
        ImageIcon scaledImage = new ImageIcon(
                pc2ImageIcon.getImage().getScaledInstance(((int) (pc2ImageIcon.getIconWidth() * 0.1)), ((int) (pc2ImageIcon.getIconHeight() * 0.1)), Image.SCALE_SMOOTH));
        JLabel pc2Label = new JLabel(scaledImage);

        // TODO: enable this statement to see the border around the PC2 logo
//        pc2Label.setBorder(new LineBorder(Color.BLUE));
        
        northEastPanel.add(pc2Label);

        return northEastPanel;

    }

    /**
     * This method returns a JPanel containing the primary contest sponsor logo. If the user has provided a "logo.png" or "logo.jpg" file, the image in that file is displayed on the JPanel; otherwise,
     * the CSUS logo is displayed.
     * 
     * @return a JPanel containing the main contest logo
     */
    private JPanel getMainLogoPanel() {

        JPanel mainLogoPanel = new JPanel();
        mainLogoPanel.setBackground(Color.white);

        // TODO: enable this statement (and ones below) to see the border around the main logo display panel
//        Border orangeline = BorderFactory.createLineBorder(Color.ORANGE);

        // the image to be placed on the panel
        ImageIcon logoImage;
        boolean useCSUSLogo = true;

        // check if user has provided a main logo file
        File mainLogoFilePNG = new File("images/" + USER_PROVIDED_LOGO_FILENAME + ".png");
        File mainLogoFileJPG = new File("images/" + USER_PROVIDED_LOGO_FILENAME + ".jpg");
        if (mainLogoFilePNG.exists() || mainLogoFileJPG.exists()) {
            // yes, there is a user-provided logo file
            if (mainLogoFilePNG.exists()) {
                logoImage = new ImageIcon("images/" + USER_PROVIDED_LOGO_FILENAME + ".png");
            } else {
                logoImage = new ImageIcon("images/" + USER_PROVIDED_LOGO_FILENAME + ".jpg");
            }
            useCSUSLogo = false;
        } else {
            // no user-provided file; use the CSUS logo
            logoImage = loadImageIconFromFile("images/" + CSUS_LOGO_FILENAME);

        }

        // if not using CSUS logo, scale the logoImage to insure it fits; use the CSUS logo as the nominal correct size
        if (!useCSUSLogo) {
            // get the CSUS logo
            ImageIcon csusLogo = loadImageIconFromFile("images/" + CSUS_LOGO_FILENAME);
            int csusWidth = csusLogo.getIconWidth();
            int csusHeight = csusLogo.getIconHeight();
            
            //scale the user's logo to match the CSUS logo dimensions
            int size = Math.max(csusWidth, csusHeight);
            logoImage = new ImageIcon(logoImage.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));

        }

        JLabel logoLabel = new JLabel(logoImage);
        
        // TODO: enable this statement (and ones below) to see the border around the image in the logo display panel
//        logoLabel.setBorder(new LineBorder(Color.black));
        
        // put some space around the logo
        Border empty = new EmptyBorder(30, 5, 10, 5);
//        Border compound = BorderFactory.createCompoundBorder(orangeline, empty);
        mainLogoPanel.setBorder(empty);
//        mainLogoPanel.setBorder(compund);

        mainLogoPanel.add(logoLabel);
        
        return mainLogoPanel;
    }
        
} // @jve:decl-index=0:visual-constraint="10,10"
