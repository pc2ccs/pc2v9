// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.Utilities;
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
    
    private final String USER_PROVIDED_LOGO_FILENAME = "logo";
    private final String USER_PROVIDED_BANNER_FILENAME = "banner";
    
    private final int DEFAULT_FRAME_WIDTH = 800 ;

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

        this.setSize(new java.awt.Dimension(DEFAULT_FRAME_WIDTH,500));
        this.setPreferredSize(new java.awt.Dimension(DEFAULT_FRAME_WIDTH,500));
        this.setMinimumSize(new java.awt.Dimension(DEFAULT_FRAME_WIDTH,500));
        this.setBackground(new java.awt.Color(253, 255, 255));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("PC^2 Login");
        this.setIconImages(getImagesForPC2LogoIcons());
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
     * This method returns a list of Images which can be used as icon images for the JFrame.
     * Providing a list of icon images allows the underlying OS to select the most appropriate
     * size image for the purpose.  (For example, Windows uses a different size icon image in the title bar
     * from what it uses in the System Tray when the frame is minimized.)
     * 
     * @return a List of IconImages
     */
    private List<? extends Image> getImagesForPC2LogoIcons() {
        
        //the sizes of Icon Images we're going to generate/return (NxN)
        final int[] sizes = { 16, 20, 30, 32, 40, 64};

        ArrayList<Image> icons = new ArrayList<Image>();
        
        ImageIcon pc2logo = getPC2LogoImageIcon();
        
        if (pc2logo!=null) {
            for (int i=0; i< sizes.length; i++) {
                //generate the next scaled version of the pc2logo
                icons.add(pc2logo.getImage().getScaledInstance(sizes[i], sizes[i], Image.SCALE_SMOOTH));  
            }
        }
        
        return icons;
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
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.insets = new Insets(23, 33, 1, 51);
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.ipadx = 39;

            //password title label layout constraints
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.insets = new Insets(12, 33, 1, 60);
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
            nameTitleLabel.setText("Login Name");
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
     * This method returns an ImageIcon containing the PC2 logo.
     * The first time the method is called it will attempt to load the image icon
     * from either the pc2.jar or from the file system by calling {@link #loadAndVerifyImageFile(String)},
     * and will return the result of that attempt (which could be null if the file being loaded either
     * doesn't exist or fails to satisfy the checksum test).  
     * On subsequent calls it returns whatever was returned
     * from the first call (which could therefore be null as well).
     * 
     * @return a PC2 Logo ImageIcon, or null if the image icon could not be properly loaded
     */
    private ImageIcon pc2LogoImageIcon = null;
    private boolean pc2LogoLoadAttempted = false;
    private ImageIcon getPC2LogoImageIcon() {

        if (pc2LogoImageIcon==null && !pc2LogoLoadAttempted) {
            pc2LogoImageIcon = FrameUtilities.loadAndVerifyImageFile("images/" + FrameUtilities.PC2_LOGO_FILENAME);
            pc2LogoLoadAttempted = true;
        }

        return pc2LogoImageIcon;
    }
    

    /**
     * This method returns a JPanel containing a banner image from either a user-specified
     * banner file (if found), or else from the default ICPC banner file (if found and if valid).
     * The method first checks for the existence of either a "banner.png" or a "banner.jpg" file
     * in the ./images folder; if present then it uses that file (favoring the PNG file if
     * both are present).  If neither "banner.png" nor "banner.jpg" is found in "./images",
     * the method delegates to {@link FrameUtilities#loadAndVerifyImageFile(String)} to 
     * load the default ICPC banner image file.
     * 
     * @return a JPanel containing a banner image, or an empty JPanel if no banner image file could be found
     */
    private JPanel getBottomBannerPanel() {
        if (bottomPanel == null) {

            bottomPanel = new JPanel();
            bottomPanel.setBackground(java.awt.Color.white);

            // load the ICPC banner (we're going to need it, either to display it or to use it for scaling the user-provided banner)
            ImageIcon icpcBannerImage;
            icpcBannerImage = FrameUtilities.loadAndVerifyImageFile("images/" + FrameUtilities.ICPC_BANNER_FILENAME);
            boolean useICPCBanner = true;

            // check if user has provided a banner file
            ImageIcon userBannerImage=null;
            File bannerFilePNG = new File("images/" + USER_PROVIDED_BANNER_FILENAME + ".png");
            File bannerFileJPG = new File("images/" + USER_PROVIDED_BANNER_FILENAME + ".jpg");
            if (bannerFilePNG.exists() || bannerFileJPG.exists()) {
                // yes, there is a user-provided banner file
                if (bannerFilePNG.exists()) {
                    userBannerImage = new ImageIcon("images/" + USER_PROVIDED_BANNER_FILENAME + ".png");
                } else {
                    userBannerImage = new ImageIcon("images/" + USER_PROVIDED_BANNER_FILENAME + ".jpg");
                }
                useICPCBanner = false;
            }

            // if not using ICPC banner, scale the user's banner Image to insure it fits
            if (!useICPCBanner) {
                
                int width = userBannerImage.getIconWidth();
                int height = userBannerImage.getIconHeight();

                // make sure the user banner fits across the frame width
                if (width > DEFAULT_FRAME_WIDTH - 20) {
                    width = DEFAULT_FRAME_WIDTH - 20;
                }

                if (height > icpcBannerImage.getIconHeight()) {
                    height = icpcBannerImage.getIconHeight();
                }
                // scale the user's banner to match the available dimensions
                userBannerImage = new ImageIcon(userBannerImage.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));

            }

            //choose which image to use
            ImageIcon bannerImage = useICPCBanner ? icpcBannerImage : userBannerImage ;
            JLabel bannerLabel = new JLabel(bannerImage);

            // TODO: delete border
//            logoLabel.setBorder(new LineBorder(Color.black));

            // put some space around the banner
            Border empty = new EmptyBorder(2, 5, 2, 5);

            // TODO: enable this border (and change the following statement to "setBorder(compound)") to see boundary around bottom panel
//            Border orangeline = BorderFactory.createLineBorder(Color.ORANGE);
//            Border compound = BorderFactory.createCompoundBorder(orangeline, empty);

            bottomPanel.setBorder(empty);
//            bottomPanel.setBorder(compound);

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
        //make sure we have a place to log to if we're running this frame stand-alone
        Utilities.insureDir("./log");
        Log log = new Log("./log", "LoginFrame.log");
        StaticLog.setLog(log);

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
            
            ImageIcon csusLogoImage = FrameUtilities.loadAndVerifyImageFile("images/" + FrameUtilities.CSUS_LOGO_FILENAME);
            ImageIcon scaledImage = null;
            if (csusLogoImage!=null) {
                scaledImage = new ImageIcon(
                    csusLogoImage.getImage().getScaledInstance(
                            (int) (csusLogoImage.getIconWidth() * 0.6), (int) (csusLogoImage.getIconHeight() * 0.6), Image.SCALE_SMOOTH));
            }
            JLabel csusLogoLabel = new JLabel(scaledImage);
            
            //put some space around the logo in the panel
            Border empty = new EmptyBorder(10, 5, 10, 5);
            
            //TODO: enable this statement (together with one above and one below) to see the top left logo panel border
//            Border compound = BorderFactory.createCompoundBorder(orangeline, empty);
            
            northWestPanel.setBorder(empty);
//            northWestPanel.setBorder(compound);


            //TODO: enable this statement to see the border around the CSUS label in the top left
//            csusLabel.setBorder(new LineBorder(Color.black));

            northWestPanel.add(csusLogoLabel);
            
        } else {
            
            //we're leaving the northwest panel empty (the CSUS logo will go on the main panel); add some spacing
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
        ImageIcon scaledImage = new ImageIcon(pc2ImageIcon.getImage().getScaledInstance(66, 80, Image.SCALE_SMOOTH));
        JLabel pc2Label = new JLabel(scaledImage);

        // TODO: enable this statement to see the border around the PC2 logo
//        pc2Label.setBorder(new LineBorder(Color.BLUE));
        
        northEastPanel.add(pc2Label);

        return northEastPanel;

    }

    /**
     * This method returns a JPanel containing the logo of the primary contest sponsor (University, Club, etc). 
     * If the user has provided a "logo.png" or "logo.jpg" file, the image in that file is displayed on the JPanel; 
     * otherwise, the CSUS logo is displayed.
     * 
     * @return a JPanel containing the main contest logo
     */
    private JPanel getMainLogoPanel() {

        JPanel mainLogoPanel = new JPanel();
        mainLogoPanel.setBackground(Color.white);

        // TODO: enable this statement (and ones below) to see the border around the main logo display panel
//        Border orangeline = BorderFactory.createLineBorder(Color.ORANGE);

        //load the CSUS logo (we're going to need it either because it goes on the panel or because it is used for scaling the user image)
        ImageIcon csusLogoImage;
        csusLogoImage = FrameUtilities.loadAndVerifyImageFile("images/" + FrameUtilities.CSUS_LOGO_FILENAME);
        boolean useCSUSLogo = true;

        // check if user has provided a main logo file
        ImageIcon userLogoImage = null;
        File mainLogoFilePNG = new File("images/" + USER_PROVIDED_LOGO_FILENAME + ".png");
        File mainLogoFileJPG = new File("images/" + USER_PROVIDED_LOGO_FILENAME + ".jpg");
        if (mainLogoFilePNG.exists() || mainLogoFileJPG.exists()) {
            // yes, there is a user-provided logo file
            if (mainLogoFilePNG.exists()) {
                userLogoImage = new ImageIcon("images/" + USER_PROVIDED_LOGO_FILENAME + ".png");
            } else {
                userLogoImage = new ImageIcon("images/" + USER_PROVIDED_LOGO_FILENAME + ".jpg");
            }
            useCSUSLogo = false;
        }

        // if not using CSUS logo, scale the userImage to insure it fits, using the CSUS logo as the nominal correct size
        if (!useCSUSLogo) {
            int csusWidth = csusLogoImage.getIconWidth();
            int csusHeight = csusLogoImage.getIconHeight();
            int size = Math.max(csusWidth, csusHeight);
            userLogoImage = new ImageIcon(userLogoImage.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
        }

        //choose which image to use
        ImageIcon logoImage = useCSUSLogo ? csusLogoImage : userLogoImage ;
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
