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

    private JLabel ivjTimerCount = null;

    private JButton ivjbtnTerminate = null;

    private IExecuteFrameNotify iNotifyClient = null;
    
    public ExecuteTimerFrame()
    {
        super();
        initialize();
    }
    
    /**
     * Return the ExecuteTimerFrame property value.
     *
     * @return javax.swing.JFrame
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
        if(ivjExecuteTimerLabel != null) {
            ivjExecuteTimerLabel.setText("Execution Time");
            ivjExecuteTimerLabel
                    .setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            ivjExecuteTimerLabel
                    .setFont(new java.awt.Font("dialog", 1, 18));
            ivjExecuteTimerLabel
                    .setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
            ivjExecuteTimerLabel
                    .setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            ivjExecuteTimerLabel.setCursor(new java.awt.Cursor(
                    java.awt.Cursor.DEFAULT_CURSOR));
        }
        if(ivjTimerCount != null) {
            ivjTimerCount.setForeground(java.awt.Color.black);
            ivjTimerCount.setFont(new java.awt.Font("monospaced", 1, 48));
            ivjTimerCount.setText(" ");
            ivjTimerCount
                    .setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            ivjTimerCount.setCursor(new java.awt.Cursor(
                    java.awt.Cursor.DEFAULT_CURSOR));
        }
        if(ivjbtnTerminate != null) {
            ivjbtnTerminate.setMnemonic('t');
            ivjbtnTerminate.setText("Terminate");           
        }
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
                ivjExecuteTimerLabel.setText("Execution Time");
                ivjExecuteTimerLabel
                        .setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                ivjExecuteTimerLabel
                        .setFont(new java.awt.Font("dialog", 1, 18));
                ivjExecuteTimerLabel
                        .setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
                ivjExecuteTimerLabel
                        .setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                ivjExecuteTimerLabel.setCursor(new java.awt.Cursor(
                        java.awt.Cursor.DEFAULT_CURSOR));
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
        if (ivjTimerCount == null) {
            try {
                ivjTimerCount = new javax.swing.JLabel();
                ivjTimerCount.setName("TimerCount");
                ivjTimerCount.setFont(new java.awt.Font("monospaced", 1, 48));
                ivjTimerCount.setText(" ");
                ivjTimerCount
                        .setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                ivjTimerCount.setCursor(new java.awt.Cursor(
                        java.awt.Cursor.DEFAULT_CURSOR));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTimerCount;
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
    public void setTerminateButtonNotify(IExecuteFrameNotify ntfy) {
        iNotifyClient = ntfy;   
    }

}
