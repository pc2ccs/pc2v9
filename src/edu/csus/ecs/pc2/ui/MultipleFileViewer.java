package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * Multiple File Viewer.
 * 
 * 
 * @author pc2@ecs.csus.edu
 */

// TODO - recreate using VE.
// $HeadURL$
public class MultipleFileViewer extends JFrame implements IFileViewer {

    /**
     * 
     */
    private static final long serialVersionUID = -3837495960973474113L;

    public static final String SVN_ID = "$Id$";

    private JPanel ivjButtonFrame = null;

    private JPanel ivjCenterFrame = null;

    private JButton ivjCloseButton = null;

    private JTabbedPane ivjFileTabbedPane = null;

    private JPanel ivjJFrameContentPane = null;

    /*
     * Compare button specific
     */

    private String judgeOutputFileName = null;

    private String teamOutputFileName = null;

    private JButton ivjCompareButton = null;

    private Log log = null;

    private IvjEventHandler ivjEventHandler = new IvjEventHandler();

    private static final String NL = System.getProperty("line.separator");

    /**
     * Event handler
     * 
     * @author pc2@ecs.csus.edu
     */
    class IvjEventHandler implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (e.getSource() == MultipleFileViewer.this.getCloseButton()) {
                connEtoC1();
            }
            if (e.getSource() == MultipleFileViewer.this.getCompareButton()) {
                connEtoC2(e);
            }
        }
    }

    /**
     * MultipleFileViewer constructor comment.
     */
    public MultipleFileViewer(Log log) {
        super();
        this.log = log;
        initialize();
    }

    /**
     * MultipleFileViewer constructor comment.
     * 
     * @param title
     *            java.lang.String
     */
    public MultipleFileViewer(Log log, String title) {
        super(title);
        this.log = log;
        initialize();
        setTitle(title);
        getCompareButton().setVisible(false);
    }

    public void closeButtonActionEvents() {
        if (fileDiffPanel != null) {
            fileDiffPanel.dispose();
            fileDiffPanel = null;
        }
        dispose();

    }

    /**
     * connEtoC1: (CloseButton.action. --> MultipleFileViewer.closeButtonActionEvents()V)
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC1() {
        try {
            // user code begin {1}
            // user code end
            this.closeButtonActionEvents();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * Return the ButtonFrame property value.
     * 
     * @return javax.swing.JPanel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getButtonFrame() {
        if (ivjButtonFrame == null) {
            try {
                ivjButtonFrame = new javax.swing.JPanel();
                ivjButtonFrame.setName("ButtonFrame");
                ivjButtonFrame.setPreferredSize(new java.awt.Dimension(35, 35));
                ivjButtonFrame.setLayout(new java.awt.FlowLayout());
                getButtonFrame().add(getCompareButton(), getCompareButton().getName());
                getButtonFrame().add(getCloseButton(), getCloseButton().getName());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjButtonFrame;
    }

    /**
     * Return the CenterFrame property value.
     * 
     * @return javax.swing.JPanel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getCenterFrame() {
        if (ivjCenterFrame == null) {
            try {
                ivjCenterFrame = new javax.swing.JPanel();
                ivjCenterFrame.setName("CenterFrame");
                ivjCenterFrame.setLayout(new java.awt.BorderLayout());
                getCenterFrame().add(getFileTabbedPane(), "Center");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjCenterFrame;
    }

    /**
     * Return the CloseButton property value.
     * 
     * @return javax.swing.JButton
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getCloseButton() {
        if (ivjCloseButton == null) {
            try {
                ivjCloseButton = new javax.swing.JButton();
                ivjCloseButton.setName("CloseButton");
                ivjCloseButton.setMnemonic('c');
                ivjCloseButton.setText("Close");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjCloseButton;
    }

    /**
     * Return the FileTabbedPane property value.
     * 
     * @return javax.swing.JTabbedPane
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JTabbedPane getFileTabbedPane() {
        if (ivjFileTabbedPane == null) {
            try {
                ivjFileTabbedPane = new javax.swing.JTabbedPane();
                ivjFileTabbedPane.setName("FileTabbedPane");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjFileTabbedPane;
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
                ivjJFrameContentPane.setLayout(new java.awt.BorderLayout());
                getJFrameContentPane().add(getCenterFrame(), "Center");
                // user code begin {1}
                ivjJFrameContentPane.add(getSoutPane(), java.awt.BorderLayout.SOUTH);
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
        getCloseButton().addActionListener(ivjEventHandler);
        getCompareButton().addActionListener(ivjEventHandler);
    }

    /**
     * Initialize the class.
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("MultipleFileViewer");
            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setState(0);
            setSize(538, 263);
            setTitle("Information");
            setContentPane(getJFrameContentPane());
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        // Center Dialog

        centerFrame();
        getCompareButton().setVisible(false);

        // user code end
    }

    public boolean loadFile(JTextArea jPane, String filename) {
        FileReader fileReader = null;
        try {
            jPane.setFont(new Font("Courier", Font.PLAIN, 12));

            String oldTitle = getTitle();
            setTitle("Loading " + filename + " ... ");

            File viewFile = new File(filename);
            if (!viewFile.exists()) {
                // System.out.println("Cannot find file "+ filename );
                return false;

            }

            fileReader = new FileReader(viewFile);
            BufferedReader in = new BufferedReader(fileReader);
            String line = in.readLine();
            while (line != null) {
                jPane.append(line);
                jPane.append("\n");
                line = in.readLine();
            }
            in.close();

            jPane.setCaretPosition(0);
            setTitle(oldTitle);
            return true;
        } catch (Exception e) {
            System.out.println("MultipleFileViewer class: exception " + e);
        }
        return false;
    }

    /**
     * MultipleFileViewer constructor comment.
     * 
     * @param paneTitle
     *            java.lang.String
     */
    public MultipleFileViewer(Log log, String windowTitle, String paneTitle, String messageData, boolean isFile) {

        super(windowTitle);
        this.log = log;
        initialize();
        setTitle(windowTitle);

        if (isFile) {
            addFilePane(paneTitle, messageData);
        } else {
            addTextPane(paneTitle, messageData);
        }
        getCompareButton().setVisible(false);

    }

    /**
     * Add a file with title... title.
     * 
     * @param title
     *            title for Tabbed Pane
     * @param filename
     *            name of file to view/load.
     * @return true if file loaded.
     */
    public boolean addFilePane(String title, String filename) {

        if (title == null) {
            title = filename;
        }
        if (title.length() < 1) {
            title = filename;
        }

        int numtabs = getFileTabbedPane().getTabCount() + 1;

        JTextArea jp = new javax.swing.JTextArea();
        jp.setName("JTextPane" + numtabs);
        jp.setBounds(0, 0, 11, 6);

        JScrollPane jsp = new javax.swing.JScrollPane();
        jsp.setName("JScrollPane" + numtabs);
        jsp.setViewportView(jp);

        JPanel jPanel = new javax.swing.JPanel();
        jPanel.setName("JPanel" + numtabs);
        jPanel.setLayout(new java.awt.BorderLayout());
        jPanel.add(jsp, "Center");

        ivjFileTabbedPane.insertTab(title, null, jPanel, null, 0);

        return loadFile(jp, filename);
    }

    /**
     * 
     * @param title
     * @param inFile
     * @return if the inFile contains data and is successfully loaded return true, otherwise return false
     */
    public boolean addFilePane(String title, SerializedFile inFile) {

        if (inFile.getBuffer().length < 1) {
            return false;
        }

        String filename = inFile.getName();

        if (title == null) {
            title = filename;
        }
        if (title.length() < 1) {
            title = filename;
        }

        int numtabs = getFileTabbedPane().getTabCount() + 1;

        JTextArea jp = new javax.swing.JTextArea();
        jp.setName("JTextPane" + numtabs);
        jp.setBounds(0, 0, 11, 6);

        JScrollPane jsp = new javax.swing.JScrollPane();
        jsp.setName("JScrollPane" + numtabs);
        jsp.setViewportView(jp);

        JPanel jPanel = new javax.swing.JPanel();
        jPanel.setName("JPanel" + numtabs);
        jPanel.setLayout(new java.awt.BorderLayout());
        jPanel.add(jsp, "Center");

        ivjFileTabbedPane.insertTab(title, null, jPanel, null, 0);

        return loadFile(jp, inFile);
    }

    /**
     * Add Text Pane to viewer panes.
     * 
     * @param title -
     *            title for tabbed pane
     * @param inMessage -
     *            message.
     * @return if the inMessage contains data and is successfully loaded return true, otherwise return false
     */
    public boolean addTextPane(String title, String inMessage) {

        if (inMessage.length() < 1) {
            return false;
        }

        if (title == null) {
            title = "Message";
        }
        if (title.length() < 1) {
            title = "Message";
        }

        int numtabs = getFileTabbedPane().getTabCount() + 1;

        JTextArea jp = new javax.swing.JTextArea();
        jp.setName("JTextPane" + numtabs);
        jp.setBounds(0, 0, 11, 6);

        JScrollPane jsp = new javax.swing.JScrollPane();
        jsp.setName("JScrollPane" + numtabs);
        jsp.setViewportView(jp);

        JPanel jPanel = new javax.swing.JPanel();
        jPanel.setName("JPanel" + numtabs);
        jPanel.setLayout(new java.awt.BorderLayout());
        jPanel.add(jsp, "Center");

        ivjFileTabbedPane.insertTab(title, null, jPanel, null, 0);

        return loadText(jp, inMessage);
    }

    public void enableCompareButton(boolean value) {
        getCompareButton().setVisible(value);
    }

    /**
     * Return the CompareButton property value.
     * 
     * @return javax.swing.JButton
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JButton getCompareButton() {
        if (ivjCompareButton == null) {
            try {
                ivjCompareButton = new javax.swing.JButton();
                ivjCompareButton.setName("CompareButton");
                ivjCompareButton.setMnemonic('p');
                ivjCompareButton.setText("Compare");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjCompareButton;
    }

    public boolean loadFile(JTextArea jPane, SerializedFile inFile) {
        try {

            String filename = inFile.getName();

            jPane.setFont(new Font("Courier", Font.PLAIN, 12));

            String s = new String(inFile.getBuffer());

            String oldTitle = getTitle();
            setTitle("Loading " + filename + " ... ");

            jPane.append(checkNPCharacters(s));
            jPane.setCaretPosition(0);

            setTitle(oldTitle);
            return true;
        } catch (Exception e) {
            System.out.println("MultipleFileViewer class: exception " + e);
        }
        return false;
    }

    public boolean loadText(JTextArea jPane, String inMessage) {

        try {
            jPane.setFont(new Font("Courier", Font.PLAIN, 12));

            jPane.append(inMessage);
            jPane.setCaretPosition(0);

            return true;
        } catch (Exception e) {
            System.out.println("MultipleFileViewer class: exception " + e);
        }
        return false;
    }

    public void showMessage(String inMessage) {

        setTitle("Message");

        addTextPane("Message", inMessage);
        this.setVisible(true);
    }

    private IDiffPanel fileDiffPanel;

    private JPanel soutPane = null;

    private JLabel informationLabel = null;

    @SuppressWarnings("unused")
    private IController controller;

    @SuppressWarnings("unused")
    private IContest contest;

    /**
     * Center Frame
     */
    private void centerFrame() {

        Dimension screenDim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenDim.width / 2 - getSize().width / 2, screenDim.height / 2 - getSize().height / 2);
    }

    public String checkNPCharacters(String s) {

        for (int i = 0; i < s.length(); i++) {

            char c = s.charAt(i);
            int x = new Character(c).hashCode();

            if (!(Character.isWhitespace(c) || ((x >= 32) && (x <= 126)))) {
                return ("***** NOTE: This output contains non-printable characters *****" + NL + s);
            }
        }
        return s;
    }

    /**
     * Comment
     */
    @SuppressWarnings("unused")
    public void compareButtonActionPerformed(java.awt.event.ActionEvent actionEvent) {
        if (judgeOutputFileName == null && teamOutputFileName == null) {
            log.config("compareButtonActionPerformed:  judge and team files are null");
            return;
        }

        if (judgeOutputFileName == null) {
            log.config("compareButtonActionPerformed:  judge file is null");
        }

        if (teamOutputFileName == null) {
            log.config("compareButtonActionPerformed:  team file is null");
        }

        try {

            if (fileDiffPanel == null) {
                fileDiffPanel = new FileDiffPanel(log);
                fileDiffPanel.showFiles(teamOutputFileName, "Team's Output", judgeOutputFileName, "Judge's Answer");
            } else {
                fileDiffPanel.show();
            }
        } catch (Throwable exception) {
            log.log(Log.CONFIG, "compareButton_ActionEvents:  Error in creating fileDiffViewer", exception);
        }
    }

    /**
     * connEtoC2: (CompareButton.action.actionPerformed(java.awt.event.ActionEvent) -->
     * MultipleFileViewer.compareButtonActionPerformed(Ljava.awt.event.ActionEvent;)V)
     * 
     * @param arg1
     *            java.awt.event.ActionEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC2(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.compareButtonActionPerformed(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * Center and position Frame
     */
    public void resizeToParentFrame(JFrame frame) {

        setSize(frame.getSize());
        setLocation(frame.getLocation());
    }

    /**
     * Size Frame to One Third Size of Screen
     */
    public void resizeToThirdScreen() {

        Dimension screenDim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

        Dimension frameDim = getSize();

        int thirdHeight = screenDim.height / 2;
        int thirdWidth = screenDim.width / 2;

        frameDim.height = Math.max(frameDim.height, thirdHeight);
        frameDim.width = Math.max(frameDim.height, thirdWidth);

        setSize(frameDim);

        centerFrame();
    }

    public void setCompareFileNames(String incomingJudgeOutputFileName, String incomingTeamOutputFileName) {
        judgeOutputFileName = incomingJudgeOutputFileName;
        teamOutputFileName = incomingTeamOutputFileName;
    }

    /**
     * This method initializes soutPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSoutPane() {
        if (soutPane == null) {
            informationLabel = new JLabel();
            informationLabel.setText("");
            informationLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            informationLabel.setPreferredSize(new java.awt.Dimension(10, 30));
            soutPane = new JPanel();
            soutPane.setLayout(new BorderLayout());
            soutPane.add(getButtonFrame(), java.awt.BorderLayout.SOUTH);
            soutPane.add(informationLabel, java.awt.BorderLayout.CENTER);
        }
        return soutPane;
    }

    /**
     * Set text for a ususally not-visible label at bottom of frame.
     * 
     * @param text
     */
    public void setInformationLabelText(String text) {
        if (text.equals("")) {
            informationLabel.setPreferredSize(new java.awt.Dimension(0, 0));
        } else {
            informationLabel.setPreferredSize(new java.awt.Dimension(10, 30));
        }
        informationLabel.setText(text);
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
    }

    public String getPluginTitle() {
        return "Multi File Viewer";
    }

}
