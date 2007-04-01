package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import edu.csus.ecs.pc2.core.RomanNumeral;

/**
 * Countdown and halt timer.
 *
 * @author pc2@ecs.csus.edu
 *
 *
 */

// $HeadURL$
public class CountDownMessage extends JDialog implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = -1066929023214360936L;

    public static final String SVN_ID = "$Id$";

    private JPanel contentsPanel = null;

    private JPanel southPane = null;

    private JPanel centerPane = null;

    private JButton closeButton = null;

    private JLabel countdownTimerLabel = null;

    private boolean exitOnClose = false;

    private String prefixToTime = "";

    private int inputRemainingSeconds = 10;

    private Timer timer = new Timer(500, this);

    private long endMilliSeconds;

    public CountDownMessage() {
        super();
        initialize();
        startCountdown();
    }

    /**
     * Shows message and halts JVM (System.exit) after N seconds.
     *
     * @param secondsBeforeExit
     * @param prefixToTime
     */
    public CountDownMessage(String prefixToTime, int secondsBeforeExit) {
        super();
        initialize();
        setRemainingSeconds(secondsBeforeExit);
        String remainSring = prefixToTime
                + new RomanNumeral(secondsBeforeExit).toString() + " seconds";
        countdownTimerLabel.setText(remainSring);
        setEnabled(true);
        setPrefixToTime(prefixToTime);
        startCountdown();
    }

    private void startCountdown() {
        endMilliSeconds = new Date().getTime() + inputRemainingSeconds * 1000;
        timer.start();
    }

    /**
     * This method initializes this
     *
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(503, 135));
        this.setContentPane(getContentsPanel());
        this.setTitle("Countdown Message");
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                actionOnClose();
            }
        });

        centerFrameTop();
    }

    /**
     * This method initializes mainPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getContentsPanel() {
        if (contentsPanel == null) {
            contentsPanel = new JPanel();
            contentsPanel.setLayout(new BorderLayout());
            contentsPanel.add(getSouthPane(), java.awt.BorderLayout.SOUTH);
            contentsPanel.add(getCenterPane(), java.awt.BorderLayout.CENTER);
        }
        return contentsPanel;
    }

    /**
     * This method initializes southPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getSouthPane() {
        if (southPane == null) {
            southPane = new JPanel();
            southPane.add(getCloseButton(), null);
        }
        return southPane;
    }

    /**
     * This method initializes centerPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            countdownTimerLabel = new JLabel();
            countdownTimerLabel.setText("");
            countdownTimerLabel.setFont(new java.awt.Font("Dialog",
                    java.awt.Font.PLAIN, 18));
            countdownTimerLabel
                    .setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            centerPane = new JPanel();
            centerPane.setLayout(new BorderLayout());
            centerPane.add(countdownTimerLabel, java.awt.BorderLayout.CENTER);
        }
        return centerPane;
    }

    /**
     * This method initializes closeButton
     *
     * @return javax.swing.JButton
     */
    private JButton getCloseButton() {
        if (closeButton == null) {
            closeButton = new JButton();
            closeButton.setText("Close");
            closeButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
            closeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    actionOnClose();
                }
            });
        }
        return closeButton;
    }

    public void actionOnClose() {
        if (exitOnClose) {
            System.exit(4);
        } else {
            timer.stop();
            dispose();
        }
    }

    /**
     * Center this frame on the screen.
     */
    protected void centerFrame() {
        java.awt.Dimension screenDim = java.awt.Toolkit.getDefaultToolkit()
                .getScreenSize();
        setLocation(screenDim.width / 2 - getSize().width / 2, screenDim.height
                / 2 - getSize().height / 2);
    }

    /**
     * Center frame at top of screen.
     *
     */
    protected void centerFrameTop() {
        java.awt.Dimension screenDim = java.awt.Toolkit.getDefaultToolkit()
                .getScreenSize();
        setLocation(screenDim.width / 2 - getSize().width / 2, 20);
    }

    public boolean isExitOnClose() {
        return exitOnClose;
    }

    public void setExitOnClose(boolean exitOnClose) {
        this.exitOnClose = exitOnClose;
    }

    public int getRemainingSeconds() {
        return inputRemainingSeconds;
    }

    public void setRemainingSeconds(int remainingSeconds) {
        inputRemainingSeconds = remainingSeconds;
    }

    public void actionPerformed(ActionEvent arg0) {

        long remainingSeconds = (endMilliSeconds - new Date().getTime()) / 1000;
        String remainSring = new String();

        if (remainingSeconds >= 1) {
            remainSring = prefixToTime
                    + new RomanNumeral(remainingSeconds).toString()
                    + " seconds";
        } else {
            remainSring = prefixToTime + "0 seconds";
        }

        countdownTimerLabel.setText(remainSring);

        if (remainingSeconds < 1) {
            actionOnClose();
        }
    }

    public String getPrefixToTime() {
        return prefixToTime;
    }

    public void setPrefixToTime(String prefixToTime) {
        if (prefixToTime != null) {
            this.prefixToTime = prefixToTime;
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
