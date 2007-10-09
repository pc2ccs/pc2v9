package edu.csus.ecs.pc2.ui.board;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.util.XSLTransformer;
import edu.csus.ecs.pc2.ui.BalloonHandler;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.JPanePlugin;
import edu.csus.ecs.pc2.ui.LogWindow;
import edu.csus.ecs.pc2.ui.OptionsPanel;
import edu.csus.ecs.pc2.ui.StandingsPane;
import edu.csus.ecs.pc2.ui.UIPlugin;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JButton;

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

    private IContest contest;

    private IController controller;

    private LogWindow logWindow = null;

    private JTabbedPane mainTabbedPane = null;

    private String xslDir;

    private String outputDir = "html";

    private Log log;

    private JPanel mainViewPane = null;

    private JPanel northLabel = null;

    private JLabel timeLabel = null;

    private JLabel messageLabel = null;

    private JPanel eastPane = null;

    private JButton exitButton = null;

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
     * 
     */
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
        this.setSize(new java.awt.Dimension(515, 319));
        this.setContentPane(getMainViewPane());
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("Scoreboard");

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                promptAndExit();
            }
        });

        FrameUtilities.centerFrame(this);
    }

    protected void promptAndExit() {
        int result = FrameUtilities.yesNoCancelDialog("Are you sure you want to exit PC^2?", "Exit PC^2");

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;

        contest.addContestTimeListener(new ContestTimeListenerImplementation());

        VersionInfo versionInfo = new VersionInfo();
        // TODO xslDir should be configurable this is just one possible default
        xslDir = versionInfo.locateHome() + File.separator + "data" + File.separator + "xsl";

        log = controller.getLog();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setTitle("PC^2 " + contest.getTitle() + " Build " + new VersionInfo().getBuildNumber());

                if (logWindow == null) {
                    logWindow = new LogWindow();
                }
                logWindow.setContestAndController(contest, controller);
                logWindow.setTitle("Log " + contest.getClientId().toString());
                
                setFrameTitle(contest.getContestTime().isContestRunning());

                StandingsPane standingsPane = new StandingsPane();
                addUIPlugin(getMainTabbedPane(), "Standings", standingsPane);
                standingsPane.addPropertyChangeListener("standings", new PropertyChangeListenerImplementation());
                BalloonHandler balloonHandler = new BalloonHandler();
                // TODO replace with addUIPlugin when/if it is graphical
                balloonHandler.setContestAndController(contest, controller);
                OptionsPanel optionsPanel = new OptionsPanel();
                addUIPlugin(getMainTabbedPane(), "Options", optionsPanel);
                optionsPanel.setLogWindow(logWindow);
                
                showMessage("");

                setVisible(true);
            }
        });
    }

    public String getPluginTitle() {
        return "Scoreboard View";
    }

    protected void addUIPlugin(JTabbedPane tabbedPane, String tabTitle, JPanePlugin plugin) {
        plugin.setContestAndController(contest, controller);
        tabbedPane.add(plugin, tabTitle);
    }

    private void generateOutput(String xmlString) {
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
            FileOutputStream outputXML = new FileOutputStream("results.xml");
            outputXML.write(xmlString.getBytes());
            outputXML.close();
        } catch (FileNotFoundException e1) {
            log.log(Log.WARNING, "Could not write to " + "results.xml", e1);
            e1.printStackTrace();
        } catch (IOException e) {
            log.log(Log.WARNING, "Problem writing to " + "results.xml", e);
            e.printStackTrace();
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
                    log.log(Log.WARNING, "Trouble generating output", e);
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO re-visit this log message
                    log.log(Log.WARNING, "Trouble generating output", e);
                    e.printStackTrace();
                }
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
            mainViewPane.add(getNorthLabel(), java.awt.BorderLayout.NORTH);
        }
        return mainViewPane;
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getNorthLabel() {
        if (northLabel == null) {
            messageLabel = new JLabel();
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            messageLabel.setText("JLabel");
            timeLabel = new JLabel();
            timeLabel.setText("STOPPED ");
            northLabel = new JPanel();
            northLabel.setLayout(new BorderLayout());
            northLabel.add(timeLabel, java.awt.BorderLayout.WEST);
            northLabel.add(messageLabel, java.awt.BorderLayout.CENTER);
            northLabel.add(getEastPane(), java.awt.BorderLayout.EAST);
        }
        return northLabel;
    }

    /**
     * This method initializes jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getEastPane() {
        if (eastPane == null) {
            eastPane = new JPanel();
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
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (contestStarted) {
                    setTitle("PC^2  " + contest.getTitle() + " [STARTED] Build " + new VersionInfo().getBuildNumber());
                    timeLabel.setText("");
                } else {
                    setTitle("PC^2 " + contest.getTitle() + " [STOPPED] Build " + new VersionInfo().getBuildNumber());
                    timeLabel.setText("STOPPED");
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
        
    }
    
    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
                messageLabel.setToolTipText(string);
            }
        });

    }
} // @jve:decl-index=0:visual-constraint="10,10"
