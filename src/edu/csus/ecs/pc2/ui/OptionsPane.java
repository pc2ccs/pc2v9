package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IChangePasswordListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.PasswordChangeEvent;
import edu.csus.ecs.pc2.core.security.ISecurityMessageListener;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.SecurityMessageEvent;
import edu.csus.ecs.pc2.ui.FrameUtilities.HorizontalPosition;
import edu.csus.ecs.pc2.ui.FrameUtilities.VerticalPosition;

/**
 * Options Pane, Show Log checkbox.
 * 
 * You must invoke {@link #setLogWindow(LogWindow)} for Show Log checkbox to enable.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class OptionsPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -7331492559860531233L;

    private LogWindow securityLogWindow = null;

    private ReportFrame reportFrame;

    private PacketMonitorFrame packetMonitorFrame = null;

    private JPanel contentPane = null;

    private JButton showLogButton = null;

    private JButton showBiffWindow = null;

    private SubmissionBiffFrame submissionBiffFrame = new SubmissionBiffFrame();
    
    private JButton showSecurityAlertWindowButton = null;

    private JButton changePasswordButton = null;
    
    private ChangePasswordFrame changePasswordFrame = null;
    
    /**
     * This method initializes
     * 
     */
    public OptionsPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(453, 259));
        this.add(getContentPane(), java.awt.BorderLayout.CENTER);

    }

    protected void showReportFrame() {
        if (reportFrame == null) {
            reportFrame = new ReportFrame();
            reportFrame.setContestAndController(getContest(), getController());
        }
        FrameUtilities.setFramePosition(reportFrame, HorizontalPosition.RIGHT, VerticalPosition.CENTER);
        reportFrame.setVisible(true);
    }

    void showPacketMonitorFrame() {

        if (packetMonitorFrame == null) {
            packetMonitorFrame = new PacketMonitorFrame();
            packetMonitorFrame.setContestAndController(getContest(), getController());

        }

        FrameUtilities.setFramePosition(packetMonitorFrame, HorizontalPosition.CENTER, VerticalPosition.CENTER);
        packetMonitorFrame.setVisible(true);
    }

    private void updateGUIperPermissions() {
        
        if (getController().isUsingGUI()){
            getShowBiffWindow().setVisible(isAllowed(Permission.Type.JUDGE_RUN));
            getShowSecurityAlertWindowButton().setVisible(isAllowedToViewSecurityWindow());
            getChangePasswordButton().setVisible(isAllowed(Permission.Type.CHANGE_PASSWORD));
        }
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        
        getContest().addAccountListener(new AccountListenerImplementation());
        getContest().addChangePasswordListener(new ChangePasswordListenerImplementation());
        
        initializePermissions();
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                submissionBiffFrame.setShowNoRunsTitle(true);
                submissionBiffFrame.setFontSize(56);
                submissionBiffFrame.setContestAndController(getContest(), getController());
                
                if (getController().isUsingGUI()){
                    FrameUtilities.setFramePosition(submissionBiffFrame, HorizontalPosition.RIGHT, VerticalPosition.TOP);
                }
                
                getContest().addSecurityMessageListener(new SecurityMessageListenerImplementation());
                
                updateGUIperPermissions();
                
            }
        });
    }

    @Override
    public String getPluginTitle() {
        return "Options Pane";
    }

    /**
     * Displays or hides log window.
     * 
     * @param showLogWindow
     */
    protected void showLog(boolean showLogWindow) {

        getController().showLogWindow(showLogWindow);

    }

    private Logger getLog() {
        return getController().getLog();
    }

    /**
     * Sets security log window.
     * 
     * @see #showSecurityLog(boolean)
     * @param securityLogWindow
     */
    public void setSecurityLogWindow(LogWindow securityLogWindow) {
        this.securityLogWindow = securityLogWindow;
    }

    /**
     * Displays or hides security log window.
     * 
     * @param showLogWindow
     */
    protected void showSecurityLog(boolean showLogWindow) {
        try {
            if (getController().isUsingGUI()){
                securityLogWindow.setVisible(showLogWindow);
            }
        } catch (Exception e) {
            getLog().log(Log.WARNING, "Exception showing security log window", e);
        }
    }

    /**
     * Initialize contentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getContentPane() {
        if (contentPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(20);
            contentPane = new JPanel();
            contentPane.setLayout(flowLayout);
            contentPane.add(getShowLogButton(), null);
            contentPane.add(getShowSecurityAlertWindowButton(), null);
            contentPane.add(getShowBiffWindow(), null);
            contentPane.add(getChangePasswordButton(), null);
            contentPane.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() > 1 && e.isControlDown() && e.isShiftDown()) {
                        showReportFrame();
                    } else if (e.getClickCount() > 1 && e.isControlDown()) {
                        showPacketMonitorFrame();
                    }
                }
            });
        }
        return contentPane;
    }

    /**
     * This method initializes showLogButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getShowLogButton() {
        if (showLogButton == null) {
            showLogButton = new JButton();
            showLogButton.setText("Show Log");
            showLogButton.setText("Show PC^2 Log");
            showLogButton.setMnemonic(java.awt.event.KeyEvent.VK_L);
            showLogButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showLog(true);
                }
            });
        }
        return showLogButton;
    }

    /**
     * This method initializes showBiffWindow
     * 
     * @return javax.swing.JButton
     */
    private JButton getShowBiffWindow() {
        if (showBiffWindow == null) {
            showBiffWindow = new JButton();
            showBiffWindow.setText("Show Unjudged Submissions Count");
            showBiffWindow.setToolTipText("Show Unjudged Submissions Count");
            showBiffWindow.setMnemonic(java.awt.event.KeyEvent.VK_U);
            showBiffWindow.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showBiffWindow();
                }
            });
        }
        return showBiffWindow;
    }

    protected void showBiffWindow() {
        if (getController().isUsingGUI()){
            submissionBiffFrame.setVisible(true);
        }
    }

    /**
     * This method initializes showSecurityAlertWindowButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getShowSecurityAlertWindowButton() {
        if (showSecurityAlertWindowButton == null) {
            showSecurityAlertWindowButton = new JButton();
            showSecurityAlertWindowButton.setText("Show Security Alert Log");
            showSecurityAlertWindowButton.setToolTipText("Show Security Alert Log");
            showSecurityAlertWindowButton.setMnemonic(java.awt.event.KeyEvent.VK_S);
            showSecurityAlertWindowButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showSecurityLog(true);
                }
            });
        }
        return showSecurityAlertWindowButton;
    }

    protected boolean isAllowedToViewSecurityWindow() {

        return isAllowed(Permission.Type.VIEW_SECURITY_ALERTS) || isServer();
    }

    private boolean isServer() {
        return getContest().getClientId().getClientType().equals(Type.SERVER);
    }

    /**
     * Listen for security alerts.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    protected class SecurityMessageListenerImplementation implements ISecurityMessageListener {
        /**
         * Show message window if alert appears.
         */
        public void newMessage(SecurityMessageEvent event) {
            if (isAllowedToViewSecurityWindow()) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        showSecurityLog(true);
                    }
                });
            }
        }
    }

    /**
     * This method initializes changePasswordButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getChangePasswordButton() {
        if (changePasswordButton == null) {
            changePasswordButton = new JButton();
            changePasswordButton.setText("Change Password");
            changePasswordButton.setToolTipText("Click here to change your password");
            changePasswordButton.setMnemonic(java.awt.event.KeyEvent.VK_P);
            changePasswordButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showChangePassword();
                }
            });
        }
        return changePasswordButton;
    }

    protected void showChangePassword() {
        if (isAllowed(Permission.Type.CHANGE_PASSWORD)) {
            if (changePasswordFrame == null) {
                changePasswordFrame = new ChangePasswordFrame();
                changePasswordFrame.setContestAndController(getContest(), getController());
            }
            changePasswordFrame.setVisible(true);
        }
    }

    protected void showPasswordMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(null, message);
            }
        });
    }

    /**
     * Change Password Listener Implementation.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public class ChangePasswordListenerImplementation implements IChangePasswordListener {
        public void passwordChanged(PasswordChangeEvent event) {
            showPasswordMessage(event.getMessage());
        }

        public void passwordNotChanged(PasswordChangeEvent event) {
            showPasswordMessage(event.getMessage());
        }
    }

    /**
     * Account Listener for OptionsPanel.
     *  
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
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
                updateThisClient();
            }
        }

        private void updateThisClient() {
            initializePermissions();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateGUIperPermissions();
                }
            });
        }

        public void accountsAdded(AccountEvent accountEvent) {
            // ignore, this does not affect me

        }

        public void accountsModified(AccountEvent accountEvent) {
            for (Account account : accountEvent.getAccounts()) {
                /**
                 * If this is the account then update the GUI display per the potential change in Permissions.
                 */
                if (getContest().getClientId().equals(account.getClientId())) {
                    updateThisClient();
                }
            }
        }

        public void accountsRefreshAll(AccountEvent accountEvent) {
            accountsModified(accountEvent);
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
