// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
/**
 * ExecuteTimerFrame
 * This frame can be used to display status of executing a run.  It implements the IExecutableMonitor
 * interface used by the Executable class.
 */
package edu.csus.ecs.pc2.core.execute;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author John Buck
 *
 */
public class ExecuteTimerFrame extends JFrame implements IExecutableMonitor {
    private JLabel ivjExecuteTimerLabel = null;

    private JPanel ivjJFrameContentPane = null;

    private JLabel ivjTimerCountLabel = null;

    private JButton ivjbtnTerminate = null;

    private IExecutableNotify iNotifyClient = null;
    
    public ExecuteTimerFrame()
    {
        super();
        initialize();
    }
    
    /**
     * Initialize the GUI components
     * 
     */
    private void initialize() {
        try {
            setName("ExecuteTimerFrame");
            setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
            setTitle("Execution Timer");
            setBounds(125, 30, 350, 143);
            setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            setContentPane(getJFrameContentPane());
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private void handleException(Throwable exception) {
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    /**
     * Return the JFrameContentPane property value.
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJFrameContentPane() {
        if (ivjJFrameContentPane == null) {
            try {
                ivjJFrameContentPane = new javax.swing.JPanel();
                ivjJFrameContentPane.setName("JFrameContentPane");
                ivjJFrameContentPane
                        .setLayout(getJFrameContentPaneBorderLayout());
                getJFrameContentPane().add(getExecuteTimerLabel(), "North");
                getJFrameContentPane().add(getTimerCountLabel(), "Center");
                getJFrameContentPane().add(getbtnTerminate(), "South");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJFrameContentPane;
    }

    /**
     * Return the JFrameContentPaneBorderLayout property value.
     *
     * @return java.awt.BorderLayout
     */
    private BorderLayout getJFrameContentPaneBorderLayout() {
        java.awt.BorderLayout contentPaneBorderLayout = null;
        try {
            /* Create part */
            contentPaneBorderLayout = new java.awt.BorderLayout();
            contentPaneBorderLayout.setVgap(10);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

        return contentPaneBorderLayout;
    }

    public void resetFrame()
    {
        initializeExecuteTimerLabel();
        initializeTimerCountLabel();
    }
    
    public void setTimerFrameVisible(boolean bVis)
    {
        setVisible(bVis);
    }
    
    /**
     * Return the ExecuteTimerLabel1 property value.
     *
     * @return javax.swing.JLabel
     */
    private JLabel getExecuteTimerLabel() {
        if (ivjExecuteTimerLabel == null) {
            try {
                ivjExecuteTimerLabel = new javax.swing.JLabel();
                ivjExecuteTimerLabel.setName("ExecuteTimerLabel");
                initializeExecuteTimerLabel();
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjExecuteTimerLabel;
    }
    
    /**
     * Return the TimerCount property value.
     *
     * @return javax.swing.JLabel
     */
    private JLabel getTimerCountLabel() {
        if (ivjTimerCountLabel == null) {
            try {
                ivjTimerCountLabel = new javax.swing.JLabel();
                ivjTimerCountLabel.setName("TimerCount");
                initializeTimerCountLabel();
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTimerCountLabel;
    }

    /**
     * Return the btnTerminate property value.
     *
     * @return javax.swing.JButton
     */
    private JButton getbtnTerminate() {
        if (ivjbtnTerminate == null) {
            try {
                ivjbtnTerminate = new javax.swing.JButton();
                ivjbtnTerminate.setName("btnTerminate");
                ivjbtnTerminate.setMnemonic('t');
                ivjbtnTerminate.setText("Terminate");
                ivjbtnTerminate.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        if(iNotifyClient != null) {
                            iNotifyClient.executeFrameTerminated();
                        }
                    }
                });
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjbtnTerminate;
    }

    /**
     * Perform common initialization of the execute timer label
     */
    private void initializeExecuteTimerLabel()
    {
        if(ivjExecuteTimerLabel != null) {
            ivjExecuteTimerLabel.setText("Execution Time");
            ivjExecuteTimerLabel
                    .setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            ivjExecuteTimerLabel
                    .setFont(new java.awt.Font("monospaced", 1, 18));
            ivjExecuteTimerLabel
                    .setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
            ivjExecuteTimerLabel
                    .setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            ivjExecuteTimerLabel.setCursor(new java.awt.Cursor(
                    java.awt.Cursor.DEFAULT_CURSOR));
        }
    }
    
    /**
     * Perform common initialization of the timer counter
     */
    private void initializeTimerCountLabel()
    {
        if(ivjTimerCountLabel != null) {
            ivjTimerCountLabel.setForeground(java.awt.Color.black);
            ivjTimerCountLabel.setFont(new java.awt.Font("monospaced", 1, 48));
            ivjTimerCountLabel.setText(" ");
            ivjTimerCountLabel
                    .setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            ivjTimerCountLabel.setCursor(new java.awt.Cursor(
                    java.awt.Cursor.DEFAULT_CURSOR));
        }
    }
    
    @Override
    public void setTimerCountLabelColor(Color fg) {
        getTimerCountLabel().setForeground(fg);
        
    }

    @Override
    public void setTimerCountLabelText(String msg) {
        getTimerCountLabel().setText(msg);
        
    }

    @Override
    public void setExecuteTimerLabel(String msg) {
        getExecuteTimerLabel().setText(msg);
        
    }

    @Override
    public void setTerminateButtonNotify(IExecutableNotify ntfy) {
        iNotifyClient = ntfy;   
    }

}
