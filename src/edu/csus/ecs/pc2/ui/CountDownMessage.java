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

import edu.csus.ecs.pc2.core.ICountDownMessage;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.RomanNumeral;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * GUI Countdown timer with option to halt program.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class CountDownMessage extends JDialog implements ActionListener, ICountDownMessage {

    /**
     *
     */
    private static final long serialVersionUID = -1066929023214360936L;

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
    }

    public void start(String prefixForCount, int seconds) {

        System.out.println("debug 22 START ");
        setRemainingSeconds(seconds);
        endMilliSeconds = new Date().getTime() + inputRemainingSeconds * 1000;

        setPrefixToTime(prefixForCount);
        String remainSring = prefixToTime + new RomanNumeral(seconds).toString() + " seconds";
        countdownTimerLabel.setText(remainSring);
        setEnabled(true); // SOMEDAY remove this redundant method call and test.
        setVisible(true);
        System.out.println("debug 22 timer started ");
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

        FrameUtilities.centerFrameTop(this);
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
            countdownTimerLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 18));
            countdownTimerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
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
        System.out.println("debug 22 actionOnClose");
        if (exitOnClose) {
            System.exit(4);
        } else {
            timer.stop();
            dispose();
        }
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
        String message = new String();

        if (remainingSeconds >= 1) {
            message = prefixToTime + new RomanNumeral(remainingSeconds).toString() + " seconds";
        } else {
            message = prefixToTime + "0 seconds";
        }

        countdownTimerLabel.setText(message);
        System.out.println("debug 22 remaining = "+message);

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

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        // unused
    }

    public String getPluginTitle() {
        return "Countdown timer";
    }

} // @jve:decl-index=0:visual-constraint="10,10"
