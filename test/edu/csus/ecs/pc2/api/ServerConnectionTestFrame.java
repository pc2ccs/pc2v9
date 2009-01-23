package edu.csus.ecs.pc2.api;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.api.listener.IConnectionEventListener;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.ui.FrameUtilities;

/**
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ServerConnectionTestFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1322896990568374819L;

    private JPanel centerPane = null;

    private JPanel mainPain = null;

    private JPanel buttonPanel = null;

    private JButton loginButton = null;

    private JButton logoffButton = null;

    private JTextField loginTextField = null;

    private JTextField passwordTextField = null;

    private JLabel loginLabel = null;

    private JLabel passwordLabel = null;

    private ServerConnection serverConnection = new ServerConnection();

    private ConnectionEventListenerImplementation connectionEventListenerImplementation = new ConnectionEventListenerImplementation();

    /**
     * This method initializes
     * 
     */
    public ServerConnectionTestFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(461, 199));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("Test Frame");
        this.setContentPane(getCenterPane());
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });

        setFrameTitle();
        FrameUtilities.centerFrame(this);
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            centerPane = new JPanel();
            centerPane.setLayout(new BorderLayout());
            centerPane.add(getMainPain(), java.awt.BorderLayout.CENTER);
            centerPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
        }
        return centerPane;
    }

    /**
     * This method initializes mainPain
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPain() {
        if (mainPain == null) {
            passwordLabel = new JLabel();
            passwordLabel.setBounds(new java.awt.Rectangle(54, 50, 79, 21));
            passwordLabel.setText("Password");
            loginLabel = new JLabel();
            loginLabel.setBounds(new java.awt.Rectangle(54, 16, 79, 21));
            loginLabel.setText("Login");
            mainPain = new JPanel();
            mainPain.setLayout(null);
            mainPain.add(getLoginTextField(), null);
            mainPain.add(getPasswordTextField(), null);
            mainPain.add(loginLabel, null);
            mainPain.add(passwordLabel, null);
        }
        return mainPain;
    }

    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(flowLayout);
            buttonPanel.add(getLoginButton(), null);
            buttonPanel.add(getLogoffButton(), null);
        }
        return buttonPanel;
    }

    /**
     * This method initializes loginButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getLoginButton() {
        if (loginButton == null) {
            loginButton = new JButton();
            loginButton.setText("Login");
            loginButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    doLogin();
                }
            });
        }
        return loginButton;
    }

    protected void doLogin() {

        try {
            ClientId clientId = InternalController.loginShortcutExpansion(0, getLoginTextField().getText());
            String login = clientId.getName();
            String password = getPasswordTextField().getText();
            if (password == null || password.trim().length() == 0) {
                password = login;
            }

            serverConnection.login(login, password);

            IContest contest = serverConnection.getContest();
            contest.addConnectionListener(connectionEventListenerImplementation);

            setFrameTitle();

        } catch (Exception e) {
            showMessage(e.getMessage());
            e.printStackTrace();
        }
    }

    private void setFrameTitle() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                if (getContest() == null) {
                    setTitle("SCT Frame - Not logged in");
                } else {
                    setTitle("SCT Frame - " + getContest().getMyClient().getLoginName() + " @ " + getContest().getSiteName());
                }
            }
        });
    }

    protected IContest getContest() {
        try {
            return serverConnection.getContest();
        } catch (Exception e) {
            return null;
        }
    }

    private void showMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(null, message);
            }
        });
    }

    /**
     * This method initializes logoffButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getLogoffButton() {
        if (logoffButton == null) {
            logoffButton = new JButton();
            logoffButton.setText("Logoff");
            logoffButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    doLogoff();
                }
            });
        }
        return logoffButton;
    }

    protected void doLogoff() {

        try {
            serverConnection.logoff();
            setFrameTitle();
        } catch (Exception e) {
            showMessage(e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * This method initializes loginTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getLoginTextField() {
        if (loginTextField == null) {
            loginTextField = new JTextField();
            loginTextField.setBounds(new java.awt.Rectangle(143, 20, 147, 22));
        }
        return loginTextField;
    }

    /**
     * This method initializes passwordTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getPasswordTextField() {
        if (passwordTextField == null) {
            passwordTextField = new JTextField();
            passwordTextField.setBounds(new java.awt.Rectangle(143, 56, 147, 22));
        }
        return passwordTextField;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new ServerConnectionTestFrame().setVisible(true);
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class ConnectionEventListenerImplementation implements IConnectionEventListener {

        public void connectionDropped() {
            setFrameTitle();
            showMessage("Connection Dropped - logged off server");
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
