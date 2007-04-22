package edu.csus.ecs.pc2.ui.admin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.ui.AccountsPane;
import edu.csus.ecs.pc2.ui.ClarificationsPane;
import edu.csus.ecs.pc2.ui.ContestTimesPane;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.GenerateAccountsPane;
import edu.csus.ecs.pc2.ui.InfoPane;
import edu.csus.ecs.pc2.ui.JPanePlugin;
import edu.csus.ecs.pc2.ui.LanguagesPane;
import edu.csus.ecs.pc2.ui.LogWindow;
import edu.csus.ecs.pc2.ui.LoginsPane;
import edu.csus.ecs.pc2.ui.OptionsPanel;
import edu.csus.ecs.pc2.ui.ProblemsPane;
import edu.csus.ecs.pc2.ui.RunsPanel;
import edu.csus.ecs.pc2.ui.SitesPanel;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Administrator GUI.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class AdministratorView extends JFrame implements UIPlugin {

    private static final long serialVersionUID = 1L;

    private IModel model;

    private IController controller;

    private JPanel jPanel = null;

    private JTabbedPane mainTabbedPanel = null;

    private JPanel statusPanel = null;

    private JPanel topPanel = null;

    private JButton exitButton = null;

    private LogWindow logWindow = null;
    
    /**
     * This method initializes
     * 
     */
    public AdministratorView() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(562,387));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getJPanel());
        this.setTitle("PC^2 Administrator");
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                promptAndExit();
            }
        });

        FrameUtilities.centerFrame(this);


    }

    public void setModelAndController(IModel inModel, IController inController) {
        this.model = inModel;
        this.controller = inController;
        
        if (logWindow == null) {
            logWindow = new LogWindow();
        }
        logWindow.setModelAndController(model, controller);
        logWindow.setTitle("Log "+model.getClientId().toString());
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                RunsPanel runsPane = new RunsPanel();
                addUIPlugin(getMainTabbedPanel(), "Runs", runsPane);
                
                SitesPanel sitesPanel = new SitesPanel();
                addUIPlugin(getMainTabbedPanel(), "Sites", sitesPanel);
                
                LanguagesPane languagesPane = new LanguagesPane();
                addUIPlugin(getMainTabbedPanel(), "Languages", languagesPane);
                
                ProblemsPane problemsPane = new ProblemsPane();
                addUIPlugin(getMainTabbedPanel(), "Problems", problemsPane);
                
                ClarificationsPane clarificationsPane = new ClarificationsPane();
                addUIPlugin(getMainTabbedPanel(), "Clarifications", clarificationsPane);
                
                ContestTimesPane contestTimesPane = new ContestTimesPane();
                addUIPlugin(getMainTabbedPanel(), "Times", contestTimesPane);
                
                GenerateAccountsPane generateAccountsPane = new GenerateAccountsPane();
                addUIPlugin(getMainTabbedPanel(), "Generate", generateAccountsPane);
                
                LoginsPane loginsPane = new LoginsPane();
                addUIPlugin(getMainTabbedPanel(), "Logins", loginsPane);
                
                AccountsPane accountsPane = new AccountsPane();
                addUIPlugin(getMainTabbedPanel(), "Accounts", accountsPane);
                
                OptionsPanel optionsPanel = new OptionsPanel();
                addUIPlugin(getMainTabbedPanel(), "Options", optionsPanel);
                optionsPanel.setLogWindow(logWindow);
                
                InfoPane infoPane = new InfoPane();
                addUIPlugin(getMainTabbedPanel(), "Dev", infoPane);
                
                setTitle("PC^2 " + model.getTitle() + " Build " + new VersionInfo().getBuildNumber());
                setVisible(true);
            }
        });
    }
    
    public String getPluginTitle() {
        return "Admin GUI";
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new JPanel();
            jPanel.setLayout(new BorderLayout());
            jPanel.add(getMainTabbedPanel(), java.awt.BorderLayout.CENTER);
            jPanel.add(getTopPanel(), java.awt.BorderLayout.NORTH);
            jPanel.add(getStatusPanel(), java.awt.BorderLayout.SOUTH);
        }
        return jPanel;
    }

    /**
     * This method initializes mainTabbedPanel
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getMainTabbedPanel() {
        if (mainTabbedPanel == null) {
            mainTabbedPanel = new JTabbedPane();
        }
        return mainTabbedPanel;
    }

    /**
     * This method initializes statusPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getStatusPanel() {
        if (statusPanel == null) {
            statusPanel = new JPanel();
            statusPanel.setPreferredSize(new java.awt.Dimension(30, 30));
        }
        return statusPanel;
    }

    /**
     * This method initializes topPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTopPanel() {
        if (topPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment(java.awt.FlowLayout.RIGHT);
            topPanel = new JPanel();
            topPanel.setLayout(flowLayout);
            topPanel.setPreferredSize(new java.awt.Dimension(45, 45));
            topPanel.add(getExitButton(), null);
        }
        return topPanel;
    }

    /**
     * This method initializes exitButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getExitButton() {
        if (exitButton == null) {
            exitButton = new JButton();
            exitButton.setText("Exit");
            exitButton.setMnemonic(java.awt.event.KeyEvent.VK_X);
            exitButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    promptAndExit();
                }
            });
        }
        return exitButton;
    }

    protected void promptAndExit() {
        int result = FrameUtilities.yesNoCancelDialog("Are you sure you want to exit PC^2?", "Exit PC^2");

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    protected void addUIPlugin(JTabbedPane tabbedPane, String tabTitle, JPanePlugin plugin) {

        plugin.setModelAndController(model, controller);
        tabbedPane.add(plugin, tabTitle);

    }

    protected void showLog(boolean showLogWindow) {
        logWindow.setVisible(showLogWindow);
    }

    public static void main(String[] args) {
        AdministratorView administratorView = new AdministratorView();
        administratorView.setVisible(true);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
