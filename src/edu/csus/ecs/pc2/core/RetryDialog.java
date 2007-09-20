/**
 * 
 */
package edu.csus.ecs.pc2.core;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.ui.FrameUtilities;

/**
 * Retry Dialog.
 * 
 * Present user with dialog which shows Retry button or exit button.
 * <P>
 * The retry action invokes a Runnable.run method, the Runnable should
 * be set using {link #setRunnable}.
 * <P>
 * A typical use is:
 * <pre>
 * RetryDialog retryDialog = new RetryDialog();
 * retryDialog.setMessage("Something failed.");
 * retryDialog.setReconnectRunnable(new Runnable() {
 *     public void run() {
 *     // this method is called when Retry button hit.
 *     }
 * });
 * retryDialog.setVisible(true);
 * </pre>
 * <P>
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RetryDialog extends JDialog {
    
    /**
     * When retry button is selected, run this runnable.
     */
    private Runnable reconnectRunnable = null;

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private JPanel mainPanel = null;

    private JPanel buttonPanel = null;

    private JPanel messagePanel = null;

    private JLabel messageLabel = null;

    private JButton retryButton = null;

    private JButton exitButton = null;

    /**
     * This method initializes
     * 
     */
    public RetryDialog() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(496, 209));
        this.setContentPane(getMainPanel());
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("Disconnected");
        FrameUtilities.centerFrame(this);
    }

    /**
     * This method initializes mainPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
            mainPanel.add(getMessagePanel(), java.awt.BorderLayout.CENTER);
        }
        return mainPanel;
    }

    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(60);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(flowLayout);
            buttonPanel.setPreferredSize(new java.awt.Dimension(35, 35));
            buttonPanel.add(getRetryButton(), null);
            buttonPanel.add(getExitButton(), null);
        }
        return buttonPanel;
    }

    /**
     * This method initializes messagePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePanel() {
        if (messagePanel == null) {
            messageLabel = new JLabel();
            messageLabel.setText("Disconnected from server");
            messageLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePanel = new JPanel();
            messagePanel.setLayout(new BorderLayout());
            messagePanel.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePanel;
    }

    /**
     * This method initializes retryButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRetryButton() {
        if (retryButton == null) {
            retryButton = new JButton();
            retryButton.setText("Retry");
            retryButton.setMnemonic(java.awt.event.KeyEvent.VK_R);
            retryButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    setTitle("Retrying... ");
                    if (reconnectRunnable != null){
                        reconnectRunnable.run();
                    }
                }
            });
        }
        return retryButton;
    }

    /**
     * This method initializes exitButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getExitButton() {
        if (exitButton == null) {
            exitButton = new JButton();
            exitButton.setText("Exit / Abort");
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

    /**
     * Update message on dialog.
     * @param message
     */
    public void setMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(message);
            }
        });
    }

    public Runnable getReconnectRunnable() {
        return reconnectRunnable;
    }

    /**
     * 
     * @param reconnectRunnable
     */
    public void setReconnectRunnable(Runnable reconnectRunnable) {
        this.reconnectRunnable = reconnectRunnable;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
