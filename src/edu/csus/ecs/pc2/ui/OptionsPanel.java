package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;
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

    private ReportFrame reportFrame;

    private JPanel contentPane = null;

    private JButton showLogButton = null;

    private JButton showBiffWindow = null;

    private SubmissionBiffFrame submissionBiffFrame = new SubmissionBiffFrame();
    
    private PermissionList permissionList = new PermissionList();
    

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
        
        showBiffWindow.setVisible(isAllowed(Permission.Type.JUDGE_RUN));
        
    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);
        
        initializePermissions();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                submissionBiffFrame.setContestAndController(getContest(), getController());
                FrameUtilities.setFramePosition(submissionBiffFrame, HorizontalPosition.RIGHT, VerticalPosition.TOP);
                submissionBiffFrame.setFontSize(56);
                
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
     * Sets log window, enables Show Log checkbox.
     * 
     * @param logWindow
     */
    public void setLogWindow(LogWindow logWindow) {
        this.logWindow = logWindow;
    }

    protected void showLog(boolean showLogWindow) {
        logWindow.setVisible(showLogWindow);
    }

    /**
     * This method initializes contentPane
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

} // @jve:decl-index=0:visual-constraint="10,10"
