package edu.csus.ecs.pc2.core.execute;

import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import edu.csus.ecs.pc2.core.log.Log;

/**
 * Count up timer window.
 *
 *
 *
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
// TODO recreate in VE.
// TODO rename to ExecutionTimer
public class ExecuteTimer extends Thread implements
        java.awt.event.ActionListener {

    public static final String SVN_ID = "$Id$";

    private long maxTime = 120; // time in seconds when display turns Red

    private Timer timer; // a timer to generate 1-sec interrupts

    private JFrame ivjExecuteTimerFrame = null; // @jve:decl-index=0:visual-constraint="10,52"

    private JLabel ivjExecuteTimerLabel1 = null;

    private JPanel ivjJFrameContentPane = null;

    private JLabel ivjTimerCount = null;

    private GregorianCalendar startTime = now();

    private JButton ivjbtnTerminate = null;

    private Process proc;

    private IOCollector firstIO;

    private IOCollector secondIO;

    private boolean doAutoStop = false;

    private boolean runTimeLimitExceeded = false;

    private boolean otherContactStaff = false;

    private Log log = null;

    /**
     * Constructor
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    public ExecuteTimer() {
        super();
        initialize();
    }

    public ExecuteTimer(Log log, int timeLimit) {
        super();
        this.log = log;
        maxTime = timeLimit;
        initialize();
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
        // user code begin {1}

        // entered when the execution timer fires an event, which happens once
        // per
        // second

        long currentTime = getElapsedSecs();

        // compute the minutes and seconds elapsed (assumes execution will be <
        // 1
        // hour)
        long seconds = currentTime % 60;
        long minutes = currentTime / 60;

        // build a string containing the time in MM:SS form
        String newTime = "";
        if (minutes < 10) {
            newTime = newTime + "0";
        }
        newTime = newTime + minutes + ":";

        if (seconds < 10) {
            newTime = newTime + "0";
        }
        newTime = newTime + seconds;

        // check if time is past upper limit; if so, change to RED
        if (currentTime > maxTime) {
            getTimerCount().setForeground(java.awt.Color.red);

            if (doAutoStop) {
                log
                        .config("ExecuteTimer - halting run execute, over time limit ");
                setRunTimeLimitExceeded(true);
                stopIOCollectors();
            }

        }

        // update the on-screen display time
        getTimerCount().setText(newTime);

        // user code end
        if (e.getSource() == getbtnTerminate()) {
            connEtoC1(e);
        }
        // user code begin {2}

        // user code end
    }

    /**
     * This terminates the process that this timer is working
     */
    @SuppressWarnings("unused")
    public void btnTerminateActionPerformed(
            java.awt.event.ActionEvent actionEvent) {

        if (doAutoStop) {
            log.config("ExecuteTimer - User hit terminate while AJ'ing.");
            setOtherContactStaff(true);
        }

        stopIOCollectors();
    }

    /**
     * connEtoC1: (btnTerminate.action.actionPerformed(java.awt.event.ActionEvent) -->
     * ExecuteTimer.btnTerminate_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     *
     * @param arg1
     *            java.awt.event.ActionEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC1(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.btnTerminateActionPerformed(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * Return the btnTerminate property value.
     *
     * @return javax.swing.JButton
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getbtnTerminate() {
        if (ivjbtnTerminate == null) {
            try {
                ivjbtnTerminate = new javax.swing.JButton();
                ivjbtnTerminate.setName("btnTerminate");
                ivjbtnTerminate.setMnemonic('t');
                ivjbtnTerminate.setText("Terminate");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjbtnTerminate;
    }

    private long getElapsedSecs() {
        long milliDiff = now().getTime().getTime()
                - startTime.getTime().getTime();
        long secs = milliDiff / 1000;
        return secs;
    }

    /**
     * Return the ExecuteTimerFrame property value.
     *
     * @return javax.swing.JFrame
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JFrame getExecuteTimerFrame() {
        if (ivjExecuteTimerFrame == null) {
            try {
                ivjExecuteTimerFrame = new javax.swing.JFrame();
                ivjExecuteTimerFrame.setName("ExecuteTimerFrame");
                ivjExecuteTimerFrame
                        .setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
                ivjExecuteTimerFrame.setTitle("Execution Timer");
                ivjExecuteTimerFrame.setBounds(125, 30, 253, 143);
                ivjExecuteTimerFrame.setVisible(true);
                ivjExecuteTimerFrame.setCursor(new java.awt.Cursor(
                        java.awt.Cursor.DEFAULT_CURSOR));
                getExecuteTimerFrame().setContentPane(getJFrameContentPane());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjExecuteTimerFrame;
    }

    /**
     * Return the ExecuteTimerLabel1 property value.
     *
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getExecuteTimerLabel1() {
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
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjExecuteTimerLabel1;
    }

    /**
     * Return the JFrameContentPane property value.
     *
     * @return javax.swing.JPanel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getJFrameContentPane() {
        if (ivjJFrameContentPane == null) {
            try {
                ivjJFrameContentPane = new javax.swing.JPanel();
                ivjJFrameContentPane.setName("JFrameContentPane");
                ivjJFrameContentPane
                        .setLayout(getJFrameContentPaneBorderLayout());
                getJFrameContentPane().add(getExecuteTimerLabel1(), "North");
                getJFrameContentPane().add(getTimerCount(), "Center");
                getJFrameContentPane().add(getbtnTerminate(), "South");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
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
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private java.awt.BorderLayout getJFrameContentPaneBorderLayout() {
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

    /**
     * @return long
     */
    public long getMaxTime() {
        return maxTime;
    }

    /**
     * @return java.lang.Process
     */
    public java.lang.Process getProc() {
        return proc;
    }

    /**
     * Return the TimerCount property value.
     *
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getTimerCount() {
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
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjTimerCount;
    }

    public JFrame getTimerFrame() {

        return getExecuteTimerFrame();
    }

    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    /**
     * Initializes connections
     *
     * @exception java.lang.Exception
     *                The exception description.
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initConnections() throws java.lang.Exception {
        // user code begin {1}
        // user code end
        getbtnTerminate().addActionListener(this);
    }

    /**
     * Initialize the class.
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            // user code begin {1}
            timer = new javax.swing.Timer(1000, this);
            // user code end
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        // user code end
    }

    /**
     * @return boolean
     */
    public boolean isOtherContactStaff() {
        return otherContactStaff;
    }

    /**
     * @return boolean
     */
    public boolean isRunTimeLimitExceeded() {
        return runTimeLimitExceeded;
    }

    private GregorianCalendar now() {
        TimeZone tz = TimeZone.getTimeZone("GMT");
        return new GregorianCalendar(tz);
    }

    public void setDoAutoStop() {
        doAutoStop = true;
    }

    public void setDoAutoStop(boolean newDoAutoStop) {
        doAutoStop = newDoAutoStop;
    }

    public void setIOCollectors(IOCollector one, IOCollector two) {
        firstIO = one;
        secondIO = two;

    }

    public void setMaxTime(long newMaxTime) {
        maxTime = newMaxTime;
    }

    private void setOtherContactStaff(boolean newOtherContactStaff) {
        otherContactStaff = newOtherContactStaff;
    }

    public void setProc(java.lang.Process newProc) {
        proc = newProc;
    }

    private void setRunTimeLimitExceeded(boolean newRunTimeLimitExceeded) {
        runTimeLimitExceeded = newRunTimeLimitExceeded;
    }

    public void setStartTime() {
        getTimerCount().setText("00:00");
        startTime = now();

    }

    public void setTitle(String msg) {
        getExecuteTimerLabel1().setText(msg);
    }

    public void startTimer() {
        setStartTime();
        timer.start();
        getExecuteTimerFrame().setVisible(true);
    }

    /**
     * Kills timer, iocollectors, and process
     */
    public void stopIOCollectors() {

        // Stop the timer (duh!)

        stopTimer();

        // Stop IO Collectors

        firstIO.haltMe();
        secondIO.haltMe();

        // stop the process

        if (proc != null) {
            log.config("ExecuteTimer: attempting to destroy process");
            proc.destroy();
        }

    }

    /**
     * Kills timer, iocollectors, and process.
     */
    public void stopTimer() {
        timer.stop();
        getExecuteTimerFrame().setVisible(false);
    }

}
