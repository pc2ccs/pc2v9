package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import edu.csus.ecs.pc2.core.LoadContest;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * UI for loading contest data from version 8 
 * 
 * @see LoadContest
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class LoadContestPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 264134440705823081L;

    private JPanel mainPane = null;

    private JButton addButton = null;

    private JPanel mainPanel = null;

    private JPanel contestInfoPane = null;

    private JScrollPane scrollPane = null;

    private JTextArea contestInfoTextArea = null;

    private JLabel filenameTitleLabel = null;

    private JButton loadFilename = null;

    private String lastDirectory = ".";

    private JLabel filenameLabel = null;

    private LoadContest loadContest = new LoadContest(); // @jve:decl-index=0:

    private static final String SEPARATOR = System.getProperty("line.separator");

    /**
     * This method initializes
     * 
     */
    public LoadContestPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(562, 241));
        this.add(getMainPanel(), BorderLayout.NORTH);
        this.add(getContestInfoPane(), BorderLayout.CENTER);

    }

    @Override
    public String getPluginTitle() {
        return "Load Plugin Pane";
    }

    /**
     * This method initializes mainPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPane() {
        if (mainPane == null) {
            filenameLabel = new JLabel();
            filenameLabel.setBounds(new Rectangle(109, 28, 346, 26));
            filenameLabel.setText("");
            filenameTitleLabel = new JLabel();
            filenameTitleLabel.setBounds(new Rectangle(11, 28, 85, 26));
            filenameTitleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            filenameTitleLabel.setText("File name");
            mainPane = new JPanel();
            mainPane.setLayout(null);
            mainPane.add(getAddButton(), null);
            mainPane.add(filenameTitleLabel, null);
            mainPane.add(getLoadFilename(), null);
            mainPane.add(filenameLabel, null);
        }
        return mainPane;
    }

    /**
     * This method initializes addButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddButton() {
        if (addButton == null) {
            addButton = new JButton();
            addButton.setBounds(new Rectangle(226, 71, 114, 30));
            addButton.setToolTipText("Add files contents into contest data");
            addButton.setEnabled(false);
            addButton.setText("Add");
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    loadContestFromFile(filenameLabel.getText(), getContest());
                }
            });
        }
        return addButton;
    }

    protected void loadContestFromFile(final String filename, IInternalContest existingContest) {
        
        FrameUtilities.waitCursor(this);

        getContestInfoTextArea().setText("Loading " + filename + "...");
        
        if (!(new File(filename).exists())) {
            FrameUtilities.regularCursor(this);
            getContestInfoTextArea().setText("File does not exist " + filename);
            JOptionPane.showMessageDialog(this, "File does not exist " + filename);
            return;
        }
        
        Date startTime = new Date();

        try {

            /**
             * Note in loadContest if contest is null will not load, will generate a new contest
             */

            IInternalContest contest = loadContest.loadContest(filename, null);

            String info = "runs " + contest.getRuns().length + "\n" + "teams " + contest.getAccounts(ClientType.Type.TEAM).size()
                    + "\n" + "probs " + contest.getProblems().length + "\n" + "langs " + contest.getLanguages().length + "\n"
                    + "judgements " + contest.getJudgements().length + "\n";

            if (existingContest != null) {
                info = "Will load " + SEPARATOR + info;
            }

            String summaryText = getSiteSummary(contest);

            info += summaryText;

            getContestInfoTextArea().setText(info);
            
//            for (Judgement judgement : contest.getJudgements()){
//                getContestInfoTextArea().append(judgement.toString()+SEPARATOR);
//            }

            if (existingContest != null) {
                contest = loadContest.loadContest(filename, existingContest);
                existingContest.storeConfiguration(getController().getLog());
                getContestInfoTextArea().append("Contest information added");
                getAddButton().setEnabled(false);
            }
            long seconds = new Date().getTime() - startTime.getTime();
            getContestInfoTextArea().append(SEPARATOR + "Load time "+(seconds/1000)+" seconds.");

        } catch (Exception e) {

            getContestInfoTextArea().setText(getStackTraceString(e));
        }
        FrameUtilities.regularCursor(this);

    }

    private String getSiteSummary(IInternalContest contest) {
        String summary = "";

        int maxSiteNumber = 0;
        for (Run run : contest.getRuns()) {
            if (run.getSiteNumber() > maxSiteNumber) {
                maxSiteNumber = run.getSiteNumber();
            }
        }
        
        if (maxSiteNumber > 0) {
            int[] siteRunCount = new int[maxSiteNumber];
            for (Run run : contest.getRuns()) {
                siteRunCount[run.getSiteNumber() - 1]++;
            }

            for (int siteNumber = 0; siteNumber < maxSiteNumber; siteNumber++) {
                summary += "Site " + (siteNumber + 1) + " " + siteRunCount[siteNumber] + " runs." + SEPARATOR;
            }
        } else {
            summary = "No sites exist" + SEPARATOR;
        }

        return summary;
    }

    String getStackTraceString(Throwable throwable) {

        String line = "Exception " + SEPARATOR + throwable.getClass().getName() + ": " + throwable.getMessage() + SEPARATOR;

        StackTraceElement[] elements = throwable.getStackTrace();
        for (StackTraceElement stackTraceElement : elements) {
            String sourceName = "(Unknown Source)";
            if (stackTraceElement.getFileName() != null) {
                sourceName = "(" + stackTraceElement.getFileName() + ":" + stackTraceElement.getLineNumber() + ")";
            }
            line = line + "|" + "    at " + stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + " "
                    + sourceName + SEPARATOR;
        }
        return line;
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
            mainPanel.setPreferredSize(new Dimension(120, 120));
            mainPanel.add(getMainPane(), BorderLayout.CENTER);
        }
        return mainPanel;
    }

    /**
     * This method initializes contestInfoPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getContestInfoPane() {
        if (contestInfoPane == null) {
            contestInfoPane = new JPanel();
            contestInfoPane.setLayout(new BorderLayout());
            contestInfoPane.setBorder(BorderFactory.createTitledBorder(null, "Contest Information",
                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12),
                    new Color(51, 51, 51)));
            contestInfoPane.add(getScrollPane(), BorderLayout.CENTER);
        }
        return contestInfoPane;
    }

    /**
     * This method initializes scrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView(getContestInfoTextArea());
        }
        return scrollPane;
    }

    /**
     * This method initializes contestInfoTextArea
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getContestInfoTextArea() {
        if (contestInfoTextArea == null) {
            contestInfoTextArea = new JTextArea();
        }
        return contestInfoTextArea;
    }

    /**
     * This method initializes loadFilename
     * 
     * @return javax.swing.JButton
     */
    private JButton getLoadFilename() {
        if (loadFilename == null) {
            loadFilename = new JButton();
            loadFilename.setBounds(new Rectangle(464, 26, 61, 30));
            loadFilename.setToolTipText("Load Contest Data File Name");
            loadFilename.setText("...");
            loadFilename.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    boolean pickedFile = selectFile(filenameLabel);
                    loadContestFromFile(filenameLabel.getText(), null);
                    getAddButton().setEnabled(pickedFile);
                }
            });
        }
        return loadFilename;
    }

    /**
     * select file, if file picked updates label.
     * 
     * @param label
     * @param button
     * @return True is a file was select and label updated
     * @throws Exception
     */
    private boolean selectFile(JLabel label) {
        boolean result = false;
        // toolTip should always have the full path
        String oldFile = label.getToolTipText();
        String startDir;
        if (oldFile != null && oldFile.equalsIgnoreCase("")) {
            startDir = lastDirectory;
        } else {
            startDir = oldFile;
        }
        JFileChooser chooser = new JFileChooser(startDir);
        try {
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                lastDirectory = chooser.getCurrentDirectory().toString();
                label.setText(chooser.getSelectedFile().getCanonicalFile().toString());
                result = true;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading file " + e.getMessage());
            result = false;
        }
        chooser = null;
        return result;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
