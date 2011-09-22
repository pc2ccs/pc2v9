package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
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
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import java.awt.FlowLayout;

/**
 * Multiple File Viewer.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class MultipleFileViewer extends JFrame implements IFileViewer {

    private static final String NL = System.getProperty("line.separator");

    /**
     * 
     */
    private static final long serialVersionUID = -3837495960973474113L;

    public static final String SVN_ID = "$Id$";

    private JButton closeButton = null;

    @SuppressWarnings("unused")
    private IInternalContest contest;

    @SuppressWarnings("unused")
    private IInternalController controller;

    private IDiffPanel fileDiffPanel;

    private JLabel informationLabel = null;

    private JPanel ivjButtonFrame = null;

    private JPanel ivjCenterFrame = null;

    private JButton compareButton = null;

    private JTabbedPane ivjFileTabbedPane = null;

    private JPanel ivjJFrameContentPane = null;

    private String judgeOutputFileName = null;

    private Log log = null;

    private JPanel soutPane = null;

    private String teamOutputFileName = null;

    public MultipleFileViewer(Log log) {
        super();
        this.log = log;
        initialize();
    }

    public MultipleFileViewer(Log log, String title) {
        super(title);
        this.log = log;
        initialize();
        setTitle(title);
        getCompareButton().setVisible(false);
    }

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
     * Add file contents with title.
     * 
     * @param title
     * @param inFile
     * @return if the inFile contains data and is successfully loaded return true, otherwise return false
     */
    public boolean addFilePane(String title, SerializedFile inFile) {

        if (inFile == null) {
            return false;
        }

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

        JTextArea textArea = new JTextArea();
        textArea.setName("JTextPane" + numtabs);
        textArea.setBounds(0, 0, 11, 6);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setName("JScrollPane" + numtabs);
        scrollPane.setViewportView(textArea);

        JPanel jPanel = new JPanel();
        jPanel.setName("JPanel" + numtabs);
        jPanel.setLayout(new java.awt.BorderLayout());
        jPanel.add(scrollPane, "Center");

        ivjFileTabbedPane.insertTab(title, null, jPanel, null, 0);

        return loadFile(textArea, inFile);
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

        JTextArea textArea = new JTextArea();
        textArea.setName("JTextPane" + numtabs);
        textArea.setBounds(0, 0, 11, 6);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setName("JScrollPane" + numtabs);
        scrollPane.setViewportView(textArea);

        JPanel jPanel = new JPanel();
        jPanel.setName("JPanel" + numtabs);
        jPanel.setLayout(new java.awt.BorderLayout());
        jPanel.add(scrollPane, "Center");

        ivjFileTabbedPane.insertTab(title, null, jPanel, null, 0);

        return loadFile(textArea, filename);
    }

    /**
     * Add Text Pane to viewer panes.
     * 
     * @param title
     *            - title for tabbed pane
     * @param inMessage
     *            - message.
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

        JTextArea textArea = new JTextArea();
        textArea.setName("JTextPane" + numtabs);
        textArea.setBounds(0, 0, 11, 6);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setName("JScrollPane" + numtabs);
        scrollPane.setViewportView(textArea);

        JPanel jPanel = new JPanel();
        jPanel.setName("JPanel" + numtabs);
        jPanel.setLayout(new java.awt.BorderLayout());
        jPanel.add(scrollPane, "Center");

        ivjFileTabbedPane.insertTab(title, null, jPanel, null, 0);

        return loadText(textArea, inMessage);
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

    public void closeTheWindow() {
        if (fileDiffPanel != null) {
            fileDiffPanel.dispose();
            fileDiffPanel = null;
        }
        dispose();

    }

    public void compareFiles() {
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

    public void enableCompareButton(boolean value) {
        getCompareButton().setVisible(value);
    }

    private JPanel getButtonFrame() {
        if (ivjButtonFrame == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(35);
            ivjButtonFrame = new JPanel();
            ivjButtonFrame.setLayout(flowLayout);
            ivjButtonFrame.setName("ButtonFrame");
            ivjButtonFrame.setPreferredSize(new java.awt.Dimension(35, 35));
            getButtonFrame().add(getCompareButton(), getCompareButton().getName());
            getButtonFrame().add(getCloseButton(), getCloseButton().getName());
        }
        return ivjButtonFrame;
    }

    private JPanel getCenterFrame() {
        if (ivjCenterFrame == null) {
            ivjCenterFrame = new JPanel();
            ivjCenterFrame.setName("CenterFrame");
            ivjCenterFrame.setLayout(new java.awt.BorderLayout());
            getCenterFrame().add(getFileTabbedPane(), "Center");
        }
        return ivjCenterFrame;
    }

    private JButton getCloseButton() {
        if (closeButton == null) {
            closeButton = new JButton();
            closeButton.setName("CloseButton");
            closeButton.setMnemonic(KeyEvent.VK_C);
            closeButton.setText("Close");
            closeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    closeTheWindow();
                }
            });
        }
        return closeButton;
    }

    private JButton getCompareButton() {
        if (compareButton == null) {
            compareButton = new JButton();
            compareButton.setName("CompareButton");
            compareButton.setMnemonic('p');
            compareButton.setText("Compare");
            compareButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    compareFiles();
                }
            });
        }
        return compareButton;
    }

    private JTabbedPane getFileTabbedPane() {
        if (ivjFileTabbedPane == null) {
            ivjFileTabbedPane = new JTabbedPane();
            ivjFileTabbedPane.setName("FileTabbedPane");
        }
        return ivjFileTabbedPane;
    }

    private JPanel getJFrameContentPane() {
        if (ivjJFrameContentPane == null) {
            ivjJFrameContentPane = new JPanel();
            ivjJFrameContentPane.setName("JFrameContentPane");
            ivjJFrameContentPane.setLayout(new java.awt.BorderLayout());
            getJFrameContentPane().add(getCenterFrame(), "Center");
            ivjJFrameContentPane.add(getSouthPane(), BorderLayout.SOUTH);
        }
        return ivjJFrameContentPane;
    }

    public String getPluginTitle() {
        return "Multi File Viewer";
    }

    /**
     * This method initializes soutPane
     * 
     * @return JPanel
     */
    private JPanel getSouthPane() {
        if (soutPane == null) {
            informationLabel = new JLabel();
            informationLabel.setText("");
            informationLabel.setHorizontalAlignment(SwingConstants.CENTER);
            informationLabel.setPreferredSize(new java.awt.Dimension(10, 30));
            soutPane = new JPanel();
            soutPane.setLayout(new BorderLayout());
            soutPane.add(getButtonFrame(), java.awt.BorderLayout.SOUTH);
            soutPane.add(informationLabel, java.awt.BorderLayout.CENTER);
        }
        return soutPane;
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        setName("MultipleFileViewer");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setState(0);
        setSize(594, 546);
        setTitle("Information");
        setContentPane(getJFrameContentPane());

        FrameUtilities.centerFrame(this);
        getCompareButton().setVisible(false);
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

    public boolean loadFile(JTextArea jPane, String filename) {
        FileReader fileReader = null;
        try {
            jPane.setFont(new Font("Courier", Font.PLAIN, 12));

            String oldTitle = getTitle();
            setTitle("Loading " + filename + " ... ");

            File viewFile = new File(filename);
            if (!viewFile.exists()) {
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

        FrameUtilities.centerFrame(this);
    }

    public void setCompareFileNames(String incomingJudgeOutputFileName, String incomingTeamOutputFileName) {
        judgeOutputFileName = incomingJudgeOutputFileName;
        teamOutputFileName = incomingTeamOutputFileName;
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
    }

    /**
     * Set text for a usually not-visible label at bottom of frame.
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

    public void setSelectedIndex(int index) {
        ivjFileTabbedPane.setSelectedIndex(index);
    }

    public void showMessage(String inMessage) {

        setTitle("Message");

        addTextPane("Message", inMessage);
        this.setVisible(true);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
