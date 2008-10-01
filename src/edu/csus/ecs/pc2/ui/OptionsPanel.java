package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.security.ISecurityMessageListener;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;
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
public class OptionsPanel extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -7331492559860531233L;

    private LogWindow logWindow;
    
    private LogWindow securityLogWindow = null;

    private ReportFrame reportFrame;

    private JPanel contentPane = null;

    private JButton showLogButton = null;

    private JButton showBiffWindow = null;

    private SubmissionBiffFrame submissionBiffFrame = new SubmissionBiffFrame();
    
    private PermissionList permissionList = new PermissionList();

    private JButton showSecurityAlertWindowButton = null;
    

    /**
     * This method initializes
     * 
     */
    public OptionsPanel() {
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
    
 
    
    private boolean isAllowed (Permission.Type type){
        return permissionList.isAllowed(type);
    }
    
    
    private void initializePermissions() {
        Account account = getContest().getAccount(getContest().getClientId());
        if (account != null){
            permissionList.clearAndLoadPermissions(account.getPermissionList());
        }
    }
    
    private void updateGUIperPermissions() {
        getShowBiffWindow().setVisible(isAllowed(Permission.Type.JUDGE_RUN));
        getShowSecurityAlertWindowButton().setVisible(isAllowedToViewSecurityWindow());
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        
        initializePermissions();
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                submissionBiffFrame.setContestAndController(getContest(), getController());
                FrameUtilities.setFramePosition(submissionBiffFrame, HorizontalPosition.RIGHT, VerticalPosition.TOP);
                submissionBiffFrame.setFontSize(56);
                
                getContest().addSecurityMessageListener(new SecurityMessageListenerImplementation());
                
                updateGUIperPermissions();
                
            }
        });
    }

    @Override
    public String getPluginTitle() {
        return "Options Pane";
    }

    public LogWindow getLogWindow() {
        return logWindow;
    }

    /**
     * Sets log window.
     *
     * @see #showLog(boolean)
     * @param logWindow
     */
    public void setLogWindow(LogWindow logWindow) {
        this.logWindow = logWindow;
    }

    /**
     * Displays or hides log window.
     * 
     * @param showLogWindow
     */
    protected void showLog(boolean showLogWindow) {
        try {
            logWindow.setVisible(showLogWindow);
        } catch (Exception e) {
            getLog().log(Log.WARNING, "Exception showing log window", e);
        }
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
     * @param showLogWindow
     */
    protected void showSecurityLog(boolean showLogWindow) {
        try {          
            securityLogWindow.setVisible(showLogWindow);
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
            contentPane.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() > 1 && e.isControlDown() && e.isShiftDown()) {
                        showReportFrame();
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
        submissionBiffFrame.setVisible(true);
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
            showSecurityAlertWindowButton.setMnemonic(java.awt.event.KeyEvent.VK_S);
            showSecurityAlertWindowButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showSecurityLog(true);
                }
            });
        }
        return showSecurityAlertWindowButton;
    }
    
    protected boolean isAllowedToViewSecurityWindow(){
        
        return isAllowed(Permission.Type.VIEW_SECURITY_ALERTS) || isServer();
    }

    private boolean isServer() {
        return getContest().getClientId().getClientType().equals(Type.SERVER); 
    }

    /**
     * Listen for security alerts. 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    protected class SecurityMessageListenerImplementation implements ISecurityMessageListener{
        /**
         * Show message window if alert appears.
         */
        public void newMessage(SecurityMessageEvent event) {
            if (isAllowedToViewSecurityWindow()){
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        showSecurityLog(true);
                    }
                });
            }
        }
    }
} // @jve:decl-index=0:visual-constraint="10,10"
