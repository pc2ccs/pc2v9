package edu.csus.ecs.pc2.ui.board;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.xml.transform.TransformerConfigurationException;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.util.XSLTransformer;
import edu.csus.ecs.pc2.exports.ccs.ResultsFile;
import edu.csus.ecs.pc2.exports.ccs.ScoreboardFile;
import edu.csus.ecs.pc2.exports.ccs.StandingsJSON2016;
import edu.csus.ecs.pc2.ui.BalloonColorListPane;
import edu.csus.ecs.pc2.ui.BalloonPane;
import edu.csus.ecs.pc2.ui.ContestClockDisplay;
import edu.csus.ecs.pc2.ui.ContestClockDisplay.DisplayTimes;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.JPanePlugin;
import edu.csus.ecs.pc2.ui.OptionsPane;
import edu.csus.ecs.pc2.ui.PacketMonitorPane;
import edu.csus.ecs.pc2.ui.PluginLoadPane;
import edu.csus.ecs.pc2.ui.StandingsPane;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * This class is the default scoreboard view (frame).
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ScoreboardView extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -8071477348056424178L;

    private IInternalContest contest;

    private IInternalController controller;

    private JTabbedPane mainTabbedPane = null;

    private String xslDir;

    private String outputDir = "html";

    private Log log;

    private JPanel mainViewPane = null;

    private JPanel northPane = null;

    private JLabel clockLabel = null;

    private JLabel messageLabel = null;

    private JPanel eastPane = null;

    private JButton exitButton = null;

    private String currentXMLString = "";

    private JButton refreshButton = null;
    
    private ContestClockDisplay contestClockDisplay = null;

    private JPanel clockPanel = null;

    /**
     * This method initializes
     * 
     */
    public ScoreboardView() {
        super();
        initialize();
    }


    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    public class PropertyChangeListenerImplementation implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equalsIgnoreCase("standings")) {
                if (evt.getNewValue() != null && !evt.getNewValue().equals(evt.getOldValue())) {
                    // standings have changed
                    // TODO take this off the awt thread
                    generateOutput((String) evt.getNewValue());
                }
            }
        }
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(800, 400));
        this.setContentPane(getMainViewPane());
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("Scoreboard");

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                promptAndExit();
            }
        });
        
        overRideLookAndFeel();
        FrameUtilities.centerFrame(this);
    }
    
    private void overRideLookAndFeel(){
        // TODO eventually move this method to on location 
        String value = IniFile.getValue("client.plaf");
        if (value != null && value.equalsIgnoreCase("java")){
            FrameUtilities.setJavaLookAndFeel();
        }
        if (value != null && value.equalsIgnoreCase("native")){
            FrameUtilities.setNativeLookAndFeel();
        }
    }

    protected void promptAndExit() {
        int result = FrameUtilities.yesNoCancelDialog(null, "Are you sure you want to exit PC^2?", "Exit PC^2");

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;

        contest.addContestTimeListener(new ContestTimeListenerImplementation());

        // TODO xslDir should be configurable this is just one possible default
        xslDir = "data" + File.separator + "xsl";
        File xslDirFile = new File(xslDir);
        if (!(xslDirFile.canRead() && xslDirFile.isDirectory())) {
            VersionInfo versionInfo = new VersionInfo();
            xslDir = versionInfo.locateHome() + File.separator + xslDir;
        }

        log = controller.getLog();
        log.info("Using XSL from directory "+xslDir);
        
        contestClockDisplay = new ContestClockDisplay(controller.getLog(), contest.getContestTime(), contest.getSiteNumber(), true, null);
        contestClockDisplay.addLabeltoUpdateList(clockLabel, DisplayTimes.REMAINING_TIME, contest.getSiteNumber());
        controller.register(contestClockDisplay);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setTitle("PC^2 " + contest.getTitle() + " Build " + new VersionInfo().getBuildNumber());

                controller.startLogWindow(contest);
                
                setFrameTitle(contest.getContestTime().isContestRunning());

                StandingsPane standingsPane = new StandingsPane();
                addUIPlugin(getMainTabbedPane(), "Standings", standingsPane);
                standingsPane.addPropertyChangeListener("standings", new PropertyChangeListenerImplementation());
                BalloonColorListPane colorsPane = new BalloonColorListPane();
                addUIPlugin(getMainTabbedPane(), "Colors", colorsPane);
                BalloonPane balloonHandler = new BalloonPane();
                // TODO replace with addUIPlugin when/if it is graphical
//                balloonHandler.setContestAndController(contest, controller);
                addUIPlugin(getMainTabbedPane(), "Balloon Test", balloonHandler);
                OptionsPane optionsPanel = new OptionsPane();
                addUIPlugin(getMainTabbedPane(), "Options", optionsPanel);
                
                if (Utilities.isDebugMode()) {
                    
                    try {
                        PacketMonitorPane pane = new PacketMonitorPane();
                        addUIPlugin(getMainTabbedPane(), "Packets", pane);
                    } catch (Exception e) {
                        logException(e);
                    }
                
                    try {
                        PluginLoadPane pane = new PluginLoadPane();
                        pane.setParentTabbedPane(getMainTabbedPane());
                        addUIPlugin(getMainTabbedPane(), "Plugin Load", pane);
                    } catch (Exception e) {
                        if (StaticLog.getLog() != null) {
                            StaticLog.getLog().log(Log.WARNING, "Exception", e);
                            e.printStackTrace(System.err);
                        } else {
                            e.printStackTrace(System.err);
                        }
                    }
                }

                showMessage("");

                setVisible(true);
            }
        });
    }

    public String getPluginTitle() {
        return "Scoreboard View";
    }

    protected void addUIPlugin(JTabbedPane tabbedPane, String tabTitle, JPanePlugin plugin) {
        controller.register(plugin);
        plugin.setParentFrame(this);
        plugin.setContestAndController(contest, controller);
        tabbedPane.add(plugin, tabTitle);
    }

    private void generateOutput(String xmlString) {
        // save it so we can refresh the html after updating the xsl
        currentXMLString = xmlString;
        File inputDir = new File(xslDir);
        if (!inputDir.isDirectory()) {
            log.warning("xslDir is not a directory");
            return;
        }
        File outputDirFile = new File(outputDir);
        if (!outputDirFile.exists() && !outputDirFile.mkdirs()) {
            log.warning("Could not create " + outputDirFile.getAbsolutePath() + ", defaulting to current directory");
            outputDir = ".";
            outputDirFile = new File(outputDir);
        }
        if (!outputDirFile.isDirectory()) {
            log.warning(outputDir + " is not a directory.");
            return;
        } else {
            log.fine("Sending output to " + outputDirFile.getAbsolutePath());
        }
        try {
            File output = File.createTempFile("__t", ".tmp", new File("."));
            FileOutputStream outputXML = new FileOutputStream(output);
            outputXML.write(xmlString.getBytes());
            outputXML.close();
            if (output.length() > 0) {
                File outputFile = new File("results.xml");
                // behaviour of renameTo is platform specific, try the possibly atomic 1st
                if (!output.renameTo(outputFile)) {
                    // otherwise fallback to the delete then rename
                    outputFile.delete();
                    if (!output.renameTo(outputFile)) {
                        log.warning("Could not create " + outputFile.getCanonicalPath());
                    }
                }
            } else {
                // 0 length file
                log.warning("New results.xml is empty, not updating");
                output.delete();
            }
            output = null;
        } catch (FileNotFoundException e1) {
            log.log(Log.WARNING, "Could not write to " + "results.xml", e1);
        } catch (IOException e) {
            log.log(Log.WARNING, "Problem writing to " + "results.xml", e);
        }
        // TODO consider changing this to use a filenameFilter
        String[] inputFiles = inputDir.list();
        XSLTransformer transformer = new XSLTransformer();
        for (int i = 0; i < inputFiles.length; i++) {
            String xslFilename = inputFiles[i];
            if (xslFilename.endsWith(".xsl")) {
                String outputFilename = xslFilename.substring(0, xslFilename.length() - 4) + ".html";
                try {
                    File output = File.createTempFile("__t", ".htm", outputDirFile);
                    FileOutputStream outputStream = new FileOutputStream(output);
                    transformer.transform(xslDir + File.separator + xslFilename, new ByteArrayInputStream(xmlString.getBytes()), outputStream);
                    outputStream.close();
                    if (output.length() > 0) {
                        File outputFile = new File(outputDir + File.separator + outputFilename);
                        if (xslFilename.equals("pc2export.xsl")) {
                            // change that, we want the pc2export written as a .dat in the cwd
                            outputFile = new File("pc2export.dat");
                        }
                        // dump json and tsv and csv files in the html directory
                        if (xslFilename.endsWith(".json.xsl") || xslFilename.endsWith(".tsv.xsl") || xslFilename.endsWith(".csv.xsl") || xslFilename.endsWith(".php.xsl")) {
                            outputFile = new File(outputDir + File.separator + xslFilename.substring(0, xslFilename.length() - 4));
                        }
                        // behaviour of renameTo is platform specific, try the possibly atomic 1st
                        if (!output.renameTo(outputFile)) {
                            // otherwise fallback to the delete then rename
                            outputFile.delete();
                            if (!output.renameTo(outputFile)) {
                                log.warning("Could not create " + outputFile.getCanonicalPath());
                            } else {
                                log.finest("rename2 to " + outputFile.getCanonicalPath() + " succeeded.");
                            }
                        } else {
                            log.finest("rename to " + outputFile.getCanonicalPath() + " succeeded.");
                        }
                    } else {
                        // 0 length file
                        log.warning("output from tranformation " + xslFilename + " was empty");
                        output.delete();
                    }
                    output = null;
                } catch (IOException e) {
                    // TODO re-visit this log message
                    log.log(Log.WARNING, "Trouble transforming "+xslFilename, e);
                } catch (TransformerConfigurationException e) {
                    // unfortunately this prints the details to stdout (or maybe stderr)
                    log.log(Log.WARNING, "Trouble transforming "+xslFilename, e);
                } catch (Exception e) {
                    log.log(Log.WARNING, "Trouble transforming "+xslFilename, e);
                }
            }
        }
        FinalizeData finalizeData = contest.getFinalizeData();
        if (finalizeData != null && finalizeData.isCertified()) {
            File outputResultsDirFile = new File("results");
            if (!outputResultsDirFile.exists() && !outputResultsDirFile.mkdirs()) {
                log.warning("Could not create " + outputResultsDirFile.getAbsolutePath() + ", defaulting to current directory");
                outputDir = ".";
                outputResultsDirFile = new File(outputDir);
            }
            if (!outputResultsDirFile.isDirectory()) {
                log.warning(outputDir + " is not a directory.");
                return;
            } else {
                log.fine("Sending results output to " + outputResultsDirFile.getAbsolutePath());
            }
            try {
                ResultsFile resultsFile = new ResultsFile();
                String[] createTSVFileLines = resultsFile.createTSVFileLines(contest);
                FileWriter outputFile = new FileWriter(outputResultsDirFile + "results.tsv");
                for (int i = 0; i < createTSVFileLines.length; i++) {
                    outputFile.write(createTSVFileLines[i]);
                }
                outputFile.close();
            } catch (IllegalContestState | IOException e) {
                log.log(Log.WARNING, "Trouble creating results.tsv", e);
            }
            try {
                ScoreboardFile scoreboardFile = new ScoreboardFile();
                String[] createTSVFileLines = scoreboardFile.createTSVFileLines(contest);
                FileWriter outputFile = new FileWriter(outputResultsDirFile + "scoreboard.tsv");
                for (int i = 0; i < createTSVFileLines.length; i++) {
                    outputFile.write(createTSVFileLines[i]);
                }
                outputFile.close();
            } catch (IllegalContestState | IOException e) {
                log.log(Log.WARNING, "Trouble creating scoreboard.tsv", e);
            }
            StandingsJSON2016 standingsJson = new StandingsJSON2016();
            try {
                String createJSON = standingsJson.createJSON(contest, controller);
                FileWriter outputFile = new FileWriter(outputResultsDirFile + "scoreboard.json");
                outputFile.write(createJSON);
                outputFile.close();
            } catch (IllegalContestState | IOException e) {
                log.log(Log.WARNING, "Trouble creating scoreboard.json", e);
            }
            
        }
    }

    /**
     * This method initializes mainTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getMainTabbedPane() {
        if (mainTabbedPane == null) {
            mainTabbedPane = new JTabbedPane();
        }
        return mainTabbedPane;
    }

    /**
     * This method initializes mainViewPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainViewPane() {
        if (mainViewPane == null) {
            mainViewPane = new JPanel();
            mainViewPane.setLayout(new BorderLayout());
            mainViewPane.add(getMainTabbedPane(), java.awt.BorderLayout.CENTER);
            mainViewPane.add(getNorthPane(), java.awt.BorderLayout.NORTH);
        }
        return mainViewPane;
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getNorthPane() {
        if (northPane == null) {
            messageLabel = new JLabel();
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            messageLabel.setText("JLabel");
            clockLabel = new JLabel();
            clockLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 16));
            clockLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            clockLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            clockLabel.setText("STOPPED ");
            northPane = new JPanel();
            northPane.setLayout(new BorderLayout());
            northPane.add(messageLabel, java.awt.BorderLayout.CENTER);
            northPane.add(getEastPane(), java.awt.BorderLayout.EAST);
            northPane.add(getClockPanel(), BorderLayout.WEST);
        }
        return northPane;
    }

    /**
     * This method initializes jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getEastPane() {
        if (eastPane == null) {
            eastPane = new JPanel();
            eastPane.add(getRefreshButton(), null);
            eastPane.add(getExitButton(), null);
        }
        return eastPane;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getExitButton() {
        if (exitButton == null) {
            exitButton = new JButton();
            exitButton.setText("Exit");
            exitButton.setToolTipText("Click here to Shutdown PC^2");
            exitButton.setMnemonic(java.awt.event.KeyEvent.VK_X);
            exitButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    promptAndExit();
                }
            });
        }
        return exitButton;
    }

    private void setFrameTitle(final boolean contestStarted) {
        final JFrame thisFrame = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                FrameUtilities.setFrameTitle(thisFrame, contest.getTitle(), contestStarted, new VersionInfo());
                if (contestStarted) {
                    contestClockDisplay.fireClockStateChange(contest.getContestTime());
                } else {
                    clockLabel.setText("STOPPED");
                }

                if (contestClockDisplay.getClientFrame() == null) {
                    contestClockDisplay.setClientFrame(thisFrame);
                }
            }
        });

        FrameUtilities.regularCursor(this);
    }

    
    protected boolean isThisSite (int siteNumber){
        return contest.getSiteNumber() == siteNumber;
    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    class ContestTimeListenerImplementation implements IContestTimeListener {

        public void contestTimeAdded(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        public void contestTimeRemoved(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        public void contestTimeChanged(ContestTimeEvent event) {
            ContestTime contestTime = event.getContestTime();
            if (isThisSite(contestTime.getSiteNumber())){
                setFrameTitle (contestTime.isContestRunning());
            }
        }

        public void contestStarted(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        public void contestStopped(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        public void refreshAll(ContestTimeEvent event) {
            contestTimeChanged(event);
        }
        
        /** This method exists to support differentiation between manual and automatic starts,
         * in the event this is desired in the future.
         * Currently it just delegates the handling to the contestStarted() method.
         */
        @Override
        public void contestAutoStarted(ContestTimeEvent event) {
            contestStarted(event);
        }

    }
    
    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
                messageLabel.setToolTipText(string);
            }
        });

    }

    /**
     * This method initializes refreshButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRefreshButton() {
        if (refreshButton == null) {
            refreshButton = new JButton();
            refreshButton.setPreferredSize(new java.awt.Dimension(100, 26));
            refreshButton.setToolTipText("Re-generate the HTML");
            refreshButton.setMnemonic(java.awt.event.KeyEvent.VK_R);
            refreshButton.setText("Refresh");
            refreshButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (currentXMLString.length() > 0) {
                        new Thread(new Runnable() {
                            public void run() {
                                generateOutput(currentXMLString);
                            }
                        }).start();
                    } else{
                        JOptionPane.showMessageDialog(getParent(), "XML currently unavailable", "Please wait", JOptionPane.WARNING_MESSAGE);
                    }
                }
            });
        }
        return refreshButton;
    }
    
    private void logException(Exception e) {

        if (StaticLog.getLog() != null) {
            StaticLog.getLog().log(Log.WARNING, "Exception", e);
            e.printStackTrace(System.err);
        } else {
            e.printStackTrace(System.err);
        }
    }

    /**
     * This method initializes clockPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getClockPanel() {
        if (clockPanel == null) {
            clockPanel = new JPanel();
            clockPanel.setPreferredSize(new java.awt.Dimension(85,34));
            clockPanel.setLayout(new BorderLayout());
            clockPanel.add(clockLabel, BorderLayout.CENTER);
        }
        return clockPanel;
    }
    
} // @jve:decl-index=0:visual-constraint="10,10"
