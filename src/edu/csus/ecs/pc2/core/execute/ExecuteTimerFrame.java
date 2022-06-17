/**
 * 
 */
package edu.csus.ecs.pc2.core.execute;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * @author John Buck
 *
 */
public class ExecuteTimerFrame extends JFrame {
    private JLabel ivjExecuteTimerLabel1 = null;

    private JPanel ivjJFrameContentPane = null;

    private JLabel ivjTimerCount = null;

    private JButton ivjbtnTerminate = null;

    private IExecuteFrameNotify iNotifyClient = null;
    
    public ExecuteTimerFrame()
    {
        super();
        initialize();
    }
    
    public void setNotify(IExecuteFrameNotify ifc) {
        iNotifyClient = ifc;
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
                getJFrameContentPane().add(getExecuteTimerLabel1(), "North");
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
        if(ivjExecuteTimerLabel1 != null) {
            ivjExecuteTimerLabel1.setText("Execution Time");
            ivjExecuteTimerLabel1
                    .setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            ivjExecuteTimerLabel1
                    .setFont(new java.awt.Font("dialog", 1, 18));
            ivjExecuteTimerLabel1
                    .setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
            ivjExecuteTimerLabel1
                    .setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            ivjExecuteTimerLabel1.setCursor(new java.awt.Cursor(
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
    
    /**
     * Return the ExecuteTimerLabel1 property value.
     *
     * @return javax.swing.JLabel
     */
    public JLabel getExecuteTimerLabel1() {
        if (ivjExecuteTimerLabel1 == null) {
            try {
                ivjExecuteTimerLabel1 = new javax.swing.JLabel();
                ivjExecuteTimerLabel1.setName("ExecuteTimerLabel1");
                ivjExecuteTimerLabel1.setText("Execution Time");
                ivjExecuteTimerLabel1
                        .setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                ivjExecuteTimerLabel1
                        .setFont(new java.awt.Font("dialog", 1, 18));
                ivjExecuteTimerLabel1
                        .setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
                ivjExecuteTimerLabel1
                        .setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                ivjExecuteTimerLabel1.setCursor(new java.awt.Cursor(
                        java.awt.Cursor.DEFAULT_CURSOR));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjExecuteTimerLabel1;
    }
    
    /**
     * Return the TimerCount property value.
     *
     * @return javax.swing.JLabel
     */
    public JLabel getTimerCountLabel() {
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

}
