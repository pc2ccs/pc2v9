package edu.csus.ecs.pc2.ui;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IContest;

/**
 * A file diff (comparison) panel.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// TODO recreate in VE.
// $HeadURL$
public class FileDiffPanel implements IDiffPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 5826285702175183581L;

    public static final String SVN_ID = "$Id$";

    private JFrame ivjJFrame1 = null;

    private JPanel ivjJFrameContentPane = null;

    private JSplitPane ivjJSplitPane1 = null;

    private JScrollPane ivjJScrollPane1 = null;

    private JScrollPane ivjJScrollPane2 = null;

    private JButton ivjBtnClose = null;

    private IvjEventHandler ivjEventHandler = new IvjEventHandler();

    private Log log = null;

    /**
     * Event handler
     * 
     * @author pc2@ecs.csus.edu
     */
    class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, java.awt.event.MouseMotionListener {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (e.getSource() == FileDiffPanel.this.getBtnClose()) {
                connEtoC1(e);
            }
        }

        public void mouseClicked(java.awt.event.MouseEvent e) {
            // Unused
        }

        public void mouseDragged(java.awt.event.MouseEvent e) {
            if (e.getSource() == FileDiffPanel.this.getJList1()) {
                connEtoC4(e);
            }
            if (e.getSource() == FileDiffPanel.this.getJList2()) {
                connEtoC5(e);
            }
        }

        public void mouseEntered(java.awt.event.MouseEvent e) {
        }

        public void mouseExited(java.awt.event.MouseEvent e) {
        }

        public void mouseMoved(java.awt.event.MouseEvent e) {
        }

        public void mousePressed(java.awt.event.MouseEvent e) {
            if (e.getSource() == FileDiffPanel.this.getJList1()) {
                connEtoC3(e);
            }
            if (e.getSource() == FileDiffPanel.this.getJList2()) {
                connEtoC6(e);
            }
        }

        public void mouseReleased(java.awt.event.MouseEvent e) {
        }
    }

    /**
     * FileDiffPanel constructor comment.
     */
    public FileDiffPanel(Log log) {
        super();
        this.log = log;
        initialize();
    }

    /**
     * Comment
     */
    public void btnCloseActionPerformed(java.awt.event.ActionEvent actionEvent) {
        dispose();
    }

    /**
     * connEtoC1: (BtnClose.action.actionPerformed(java.awt.event.ActionEvent) -->
     * FileDiffPanel.btnClose_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     * 
     * @param arg1
     *            java.awt.event.ActionEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC1(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.btnCloseActionPerformed(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * Return the BtnClose property value.
     * 
     * @return JButton
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private JButton getBtnClose() {
        if (ivjBtnClose == null) {
            try {
                ivjBtnClose = new JButton();
                ivjBtnClose.setName("BtnClose");
                ivjBtnClose.setText("Close");
                ivjBtnClose.setBounds(344, 484, 85, 25);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjBtnClose;
    }

    /**
     * Return the JFrame1 property value.
     * 
     * @return JFrame
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private JFrame getJFrame1() {
        if (ivjJFrame1 == null) {
            try {
                ivjJFrame1 = new JFrame();
                ivjJFrame1.setName("JFrame1");
                ivjJFrame1.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                ivjJFrame1.setResizable(false);
                ivjJFrame1.setBounds(21, 30, 815, 560);
                ivjJFrame1.setTitle("File Viewer");
                getJFrame1().setContentPane(getJFrameContentPane());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJFrame1;
    }

    /**
     * Return the JFrameContentPane property value.
     * 
     * @return JPanel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private JPanel getJFrameContentPane() {
        if (ivjJFrameContentPane == null) {
            try {
                ivjJFrameContentPane = new JPanel();
                ivjJFrameContentPane.setName("JFrameContentPane");
                ivjJFrameContentPane.setToolTipText("Close Viewer");
                ivjJFrameContentPane.setLayout(null);
                getJFrameContentPane().add(getJSplitPane1(), getJSplitPane1().getName());
                getJFrameContentPane().add(getBtnClose(), getBtnClose().getName());
                getJFrameContentPane().add(getJLabel1(), getJLabel1().getName());
                getJFrameContentPane().add(getJLabel2(), getJLabel2().getName());
                getJFrameContentPane().add(getJLabel3(), getJLabel3().getName());
                getJFrameContentPane().add(getJCheckBox1(), getJCheckBox1().getName());
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
     * Return the JScrollPane1 property value.
     * 
     * @return JScrollPane
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private JScrollPane getJScrollPane1() {
        if (ivjJScrollPane1 == null) {
            try {
                ivjJScrollPane1 = new JScrollPane();
                ivjJScrollPane1.setName("JScrollPane1");
                ivjJScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                getJScrollPane1().setViewportView(getJList1());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJScrollPane1;
    }

    /**
     * Return the JScrollPane2 property value.
     * 
     * @return JScrollPane
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private JScrollPane getJScrollPane2() {
        if (ivjJScrollPane2 == null) {
            try {
                ivjJScrollPane2 = new JScrollPane();
                ivjJScrollPane2.setName("JScrollPane2");
                ivjJScrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                getJScrollPane2().setViewportView(getJList2());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJScrollPane2;
    }

    /**
     * Return the JSplitPane1 property value.
     * 
     * @return JSplitPane
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private JSplitPane getJSplitPane1() {
        if (ivjJSplitPane1 == null) {
            try {
                ivjJSplitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
                ivjJSplitPane1.setName("JSplitPane1");
                ivjJSplitPane1.setDividerSize(2);
                ivjJSplitPane1.setLastDividerLocation(0);
                ivjJSplitPane1.setDividerLocation(390);
                ivjJSplitPane1.setPreferredSize(new java.awt.Dimension(200, 36));
                ivjJSplitPane1.setBounds(12, 37, 790, 438);
                ivjJSplitPane1.setMinimumSize(new java.awt.Dimension(200, 200));
                ivjJSplitPane1.setContinuousLayout(true);
                getJSplitPane1().add(getJScrollPane1(), "left");
                getJSplitPane1().add(getJScrollPane2(), "right");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJSplitPane1;
    }

    /**
     * Called whenever the part throws an exception.
     * 
     * @param exception
     *            java.lang.Throwable
     */

    private void handleException(java.lang.Throwable exception) {
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
        getBtnClose().addActionListener(ivjEventHandler);
        getJList1().addMouseListener(ivjEventHandler);
        getJList1().addMouseMotionListener(ivjEventHandler);
        getJList2().addMouseMotionListener(ivjEventHandler);
        getJList2().addMouseListener(ivjEventHandler);
    }

    /**
     * Initialize the class.
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            // user code begin {1}
            this.getJSplitPane1().setDividerLocation(390);
            // user code end
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        // user code end
    }

    private JLabel ivjJLabel1 = null;

    private JList ivjJList1 = null;

    private JList ivjJList2 = null;

    public BufferedReader getFileHandle(String fileName) throws FileNotFoundException {
        BufferedReader br = null;
        try {
            FileReader fr = new FileReader(fileName);
            br = new BufferedReader(fr);
        } catch (FileNotFoundException fe) {
            // log.config("FileNotFound: "+ fileName,fe);
            throw fe;
        }

        return br;
    }

    /**
     * Return the JLabel1 property value.
     * 
     * @return JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private JLabel getJLabel1() {
        if (ivjJLabel1 == null) {
            try {
                ivjJLabel1 = new JLabel();
                ivjJLabel1.setName("JLabel1");
                ivjJLabel1.setFont(new java.awt.Font("Arial", 1, 14));
                ivjJLabel1.setText("Unable to retrieve file information!!!");
                ivjJLabel1.setBounds(194, 5, 396, 27);
                ivjJLabel1.setVisible(false);
                ivjJLabel1.setForeground(java.awt.Color.red);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabel1;
    }

    /**
     * Return the JList1 property value.
     * 
     * @return JList
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    protected JList getJList1() {
        if (ivjJList1 == null) {
            try {
                ivjJList1 = new JList();
                ivjJList1.setName("JList1");
                ivjJList1.setBounds(0, 0, 160, 120);
                // user code begin {1}
                ivjJList1.setFont(new Font("Courier", Font.PLAIN, 12));
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJList1;
    }

    /**
     * Return the JList2 property value.
     * 
     * @return JList
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private JList getJList2() {
        if (ivjJList2 == null) {
            try {
                ivjJList2 = new JList();
                ivjJList2.setName("JList2");
                ivjJList2.setBounds(0, 0, 160, 120);
                // user code begin {1}
                ivjJList2.setFont(new Font("Courier", Font.PLAIN, 12));
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJList2;
    }

    private JCheckBox ivjJCheckBox1 = null;

    private JLabel ivjJLabel2 = null;

    private JLabel ivjJLabel3 = null;

    @SuppressWarnings("unused")
    private IContest contest;

    @SuppressWarnings("unused")
    private IController controller;

    /**
     * connEtoC3: (JList1.mouse.mousePressed(java.awt.event.MouseEvent) -->
     * FileDiffPanel.jList1_MousePressed(Ljava.awt.event.MouseEvent;)V)
     * 
     * @param arg1
     *            java.awt.event.MouseEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC3(java.awt.event.MouseEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.jList1MousePressed(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC4: (JList1.mouseMotion.mouseDragged(java.awt.event.MouseEvent) -->
     * FileDiffPanel.jList1_MouseDragged(Ljava.awt.event.MouseEvent;)V)
     * 
     * @param arg1
     *            java.awt.event.MouseEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC4(java.awt.event.MouseEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.jList1MouseDragged(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC5: (JList2.mouseMotion.mouseDragged(java.awt.event.MouseEvent) -->
     * FileDiffPanel.jList2_MouseDragged(Ljava.awt.event.MouseEvent;)V)
     * 
     * @param arg1
     *            java.awt.event.MouseEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC5(java.awt.event.MouseEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.jList2MouseDragged(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC6: (JList2.mouse.mousePressed(java.awt.event.MouseEvent) -->
     * FileDiffPanel.jList2_MousePressed(Ljava.awt.event.MouseEvent;)V)
     * 
     * @param arg1
     *            java.awt.event.MouseEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC6(java.awt.event.MouseEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.jList2MousePressed(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * Comment
     */
    public void dispose() {
        getJFrame1().dispose();
    }

    /**
     * Return the JCheckBox1 property value.
     * 
     * @return JCheckBox
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private JCheckBox getJCheckBox1() {
        if (ivjJCheckBox1 == null) {
            try {
                ivjJCheckBox1 = new JCheckBox();
                ivjJCheckBox1.setName("JCheckBox1");
                ivjJCheckBox1.setText("Disable Locked Scrolling");
                ivjJCheckBox1.setBounds(141, 484, 170, 22);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJCheckBox1;
    }

    /**
     * Return the JLabel2 property value.
     * 
     * @return JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private JLabel getJLabel2() {
        if (ivjJLabel2 == null) {
            try {
                ivjJLabel2 = new JLabel();
                ivjJLabel2.setName("JLabel2");
                ivjJLabel2.setText("JLabel2");
                ivjJLabel2.setBounds(13, 16, 167, 14);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabel2;
    }

    /**
     * Return the JLabel3 property value.
     * 
     * @return JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private JLabel getJLabel3() {
        if (ivjJLabel3 == null) {
            try {
                ivjJLabel3 = new JLabel();
                ivjJLabel3.setName("JLabel3");
                ivjJLabel3.setText("JLabel3");
                ivjJLabel3.setBounds(619, 17, 180, 14);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabel3;
    }

    /**
     * Comment
     */
    public void jList1MouseDragged(java.awt.event.MouseEvent mouseEvent) {
        updateListUI(1);
        return;
    }

    /**
     * Comment
     */
    public void jList1MousePressed(java.awt.event.MouseEvent mouseEvent) {
        updateListUI(1);
        return;
    }

    /**
     * Comment
     */
    public void jList2MouseDragged(java.awt.event.MouseEvent mouseEvent) {
        updateListUI(2);
        return;
    }

    /**
     * Comment
     */
    public void jList2MousePressed(java.awt.event.MouseEvent mouseEvent) {
        updateListUI(2);
        return;
    }

    /**
     * Comment
     */
    public void jMenuItem2ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        getJFrame1().dispose();
        return;
    }

    /**
     * show the pane (bring forward on screen)
     */
    public void show() {
        getJFrame1().setVisible(true);
    }

    public void showFiles(String firstFileName, String firstPaneLabel, String secondFileName, String secondPaneLabel) {
        log.config("FileDiffPane::ShowFiles(" + firstFileName + "," + secondFileName + ") Begin");
        BufferedReader firstFileHandle = null;
        BufferedReader secondFileHandle = null;
        if (firstFileName != null) {
            try {
                firstFileHandle = getFileHandle(firstFileName);
                String lineRead = firstFileHandle.readLine();
                Vector<String> v = new Vector<String>();
                while (lineRead != null) {
                    v.addElement(lineRead);
                    lineRead = new String();
                    lineRead = firstFileHandle.readLine();
                }
                getJList1().setListData(v);
            } catch (Exception e) {
                log.config("FileDiffPane::ShowFiles() - error Processing " + firstFileName + "," + e.getMessage());
            } finally {
                try {
                    if (firstFileHandle != null) {
                        firstFileHandle.close();
                    }
                } catch (Exception e) {
                    log.config("FileDiffPane::ShowFiles() - error closing fileHandle " + firstFileName + "," + e.getMessage());
                }
            }
        }

        if (secondFileName != null) {
            try {
                secondFileHandle = getFileHandle(secondFileName);
                String lineRead = secondFileHandle.readLine();
                Vector<String> v = new Vector<String>();
                while (lineRead != null) {
                    v.addElement(lineRead);
                    lineRead = new String();
                    lineRead = secondFileHandle.readLine();
                }
                getJList2().setListData(v);
            } catch (Exception e) {
                log.config("FileDiffPane::ShowFiles() - error Processing " + secondFileName + "," + e.getMessage());
            } finally {
                try {
                    if (secondFileHandle != null) {
                        secondFileHandle.close();
                    }
                } catch (Exception e) {
                    log.config("FileDiffPane::ShowFiles() - error closing fileHandle " + secondFileName + "," + e.getMessage());
                }
            }
        }

        getJLabel1().setVisible(false);

        getJLabel2().setText(firstPaneLabel);
        getJLabel3().setText(secondPaneLabel);

        getJLabel2().setToolTipText(firstPaneLabel);
        getJLabel3().setToolTipText(secondPaneLabel);

        getJLabel2().setVisible(true);
        getJLabel3().setVisible(true);
        getJFrame1().setVisible(true);
    }

    public void updateListUI(int iListId) {

        if (getJCheckBox1().isSelected()) {
            return;
        }
        switch (iListId) {
        case 1:
            getJList2().setBounds(getJList1().getBounds());
            getJList2().setSelectedIndex(getJList1().getSelectedIndex());
            getJList2().ensureIndexIsVisible(getJList1().getSelectedIndex());
            break;
        case 2:
            getJList1().setBounds(getJList2().getBounds());
            getJList1().setSelectedIndex(getJList2().getSelectedIndex());
            getJList1().ensureIndexIsVisible(getJList2().getSelectedIndex());
            break;
        default:
            log.log(Log.DEBUG, iListId + " did not match a known case.");
        }

    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        
    }

    public String getPluginTitle() {
        return "File Diff Viewer";
    }
}
