package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.export.ExportYAML;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.report.SubmissionsTSVReport;
import edu.csus.ecs.pc2.exports.ccs.Groupdata;
import edu.csus.ecs.pc2.exports.ccs.ResolverEventFeedXML;
import edu.csus.ecs.pc2.exports.ccs.ResultsFile;
import edu.csus.ecs.pc2.exports.ccs.ScoreboardFile;
import edu.csus.ecs.pc2.exports.ccs.Teamdata;
import edu.csus.ecs.pc2.exports.ccs.Userdata;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;

/**
 * Export Data Pane.
 * 
 * Export various tsv and xml files.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ExportDataPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 2608244878052255094L;

    private JPanel buttonPane = null;

    private JButton creatResultsButton = null;

    private JButton saveScoreboardButton = null;

    private JButton saveUserDataButton = null;

    private JButton saveTeamsButton = null;

    private JButton saveGroupButton = null;

    private JPanel centerPane = null;

    private JButton exportYamlButton = null;

    private String lastDirectory = ".";

    private JButton exportRunTSVButton = null;
    private JButton viewEventFeed;

    /**
     * This method initializes
     * 
     */
    public ExportDataPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(548, 291));
        this.add(getButtonPane(), BorderLayout.SOUTH);

        this.add(getCenterPane(), BorderLayout.CENTER);
    }

    @Override
    public String getPluginTitle() {
        return "Export Data Pane";
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(10);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.setPreferredSize(new Dimension(35, 35));
            buttonPane.add(getCreatResultsButton(), null);
            buttonPane.add(getSaveScoreboardButton(), null);
            buttonPane.add(getSaveUserDataButton(), null);
            buttonPane.add(getSaveTeamsButton(), null);
            buttonPane.add(getSaveGroupButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes creatResultsButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCreatResultsButton() {
        if (creatResultsButton == null) {
            creatResultsButton = new JButton();
            creatResultsButton.setText("Save Results");
            creatResultsButton.setToolTipText("Save results TSV file");
            creatResultsButton.setMnemonic(KeyEvent.VK_R);
            creatResultsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    saveResultsTSVFile();
                }
            });
        }
        return creatResultsButton;
    }

    protected void saveResultsTSVFile() {

        String outfilename = "results.tsv";
        
        try {
            ResultsFile resultsFile = new ResultsFile();
            String[] lines = resultsFile.createTSVFileLines(getContest());
            writeLinesToFile(outfilename, lines);
            viewFile(outfilename, outfilename);
        } catch (Exception e) {
            FrameUtilities.showMessage(this, "Can not save file", "Unable to save " + outfilename + " " + e.getMessage());
            getLog().log(Level.WARNING, e.toString(),e);
        }
    }

    /**
     * This method initializes saveScoreboardButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSaveScoreboardButton() {
        if (saveScoreboardButton == null) {
            saveScoreboardButton = new JButton();
            saveScoreboardButton.setText("Save Scoreboard");
            saveScoreboardButton.setToolTipText("Save Scoreboard TSV file");
            saveScoreboardButton.setMnemonic(KeyEvent.VK_S);
            saveScoreboardButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    saveScoreboardTSVFile();

                }
            });
        }
        return saveScoreboardButton;
    }

    protected void saveScoreboardTSVFile() {

        String outfilename = "scoreboard.tsv";
        try {
            ScoreboardFile scoreboardFile = new ScoreboardFile();
            String[] lines = scoreboardFile.createTSVFileLines(getContest());
            writeLinesToFile(outfilename, lines);
            viewFile(outfilename, outfilename);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to save " + outfilename + " " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void writeLinesToFile(String filename, String[] lines) throws FileNotFoundException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);
        for (String line : lines) {
            printWriter.println(line);
        }
        printWriter.close();

    }

    /**
     * This method initializes saveUserDataButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSaveUserDataButton() {
        if (saveUserDataButton == null) {
            saveUserDataButton = new JButton();
            saveUserDataButton.setText("Save UserData");
            saveUserDataButton.setMnemonic(KeyEvent.VK_U);
            saveUserDataButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    saveUserDataTSVFile();
                }
            });
        }
        return saveUserDataButton;
    }

    protected void saveGroupdataTSVFile() {

        String outfilename = "groups.tsv";
        try {
            Groupdata groupData = new Groupdata();
            String[] lines = groupData.getGroupData(getContest());
            writeLinesToFile(outfilename, lines);
            viewFile(outfilename, outfilename);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to save " + outfilename + " " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void saveTeamdataTSVFile() {

        String outfilename = "teams.tsv";
        try {
            Teamdata teamdata = new Teamdata();
            String[] lines = teamdata.getTeamData(getContest());
            writeLinesToFile(outfilename, lines);
            viewFile(outfilename, outfilename);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to save " + outfilename + " " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void saveUserDataTSVFile() {

        String outfilename = "userdata.tsv";
        try {
            Userdata userdata = new Userdata();
            String[] lines = userdata.getUserData(getContest());
            writeLinesToFile(outfilename, lines);
            viewFile(outfilename, outfilename);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to save " + outfilename + " " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This method initializes saveTeamsButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSaveTeamsButton() {
        if (saveTeamsButton == null) {
            saveTeamsButton = new JButton();
            saveTeamsButton.setText("Save Teams");
            saveTeamsButton.setToolTipText("Save Teams TSV file");
            saveTeamsButton.setMnemonic(KeyEvent.VK_T);
            saveTeamsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    saveTeamdataTSVFile();
                }
            });
        }
        return saveTeamsButton;
    }

    private void viewFile(String filename, String title) {
        MultipleFileViewer multipleFileViewer = new MultipleFileViewer(getController().getLog());
        multipleFileViewer.addFilePane(title, filename);
        multipleFileViewer.setTitle("PC^2 View File (Build " + new VersionInfo().getBuildNumber() + ")");
        FrameUtilities.centerFrameFullScreenHeight(multipleFileViewer);
        multipleFileViewer.setVisible(true);
    }

    /**
     * This method initializes saveGroupButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSaveGroupButton() {
        if (saveGroupButton == null) {
            saveGroupButton = new JButton();
            saveGroupButton.setText("Save Groups");
            saveGroupButton.setToolTipText("Save Groups TSV file");
            saveGroupButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    saveGroupdataTSVFile();
                }
            });
        }
        return saveGroupButton;
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            FlowLayout flowLayout1 = new FlowLayout();
            flowLayout1.setHgap(25);
            centerPane = new JPanel();
            centerPane.setLayout(flowLayout1);
            centerPane.add(getExportYamlButton(), null);
            centerPane.add(getExportRunTSVButton(), null);
            centerPane.add(getViewEventFeed());
        }
        return centerPane;
    }

    /**
     * This method initializes exportYamlButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getExportYamlButton() {
        if (exportYamlButton == null) {
            exportYamlButton = new JButton();
            exportYamlButton.setText("Export contest.yaml");
            exportYamlButton.setMnemonic(KeyEvent.VK_X);
            exportYamlButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    exportContestYaml();
                }
            });
        }
        return exportYamlButton;
    }
    
    public File selectDirectoryDialog(Component parent, String startDirectory) {
        
        JFileChooser chooser = new JFileChooser(startDirectory);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int action = chooser.showSaveDialog(parent);

        switch (action) {
            case JFileChooser.APPROVE_OPTION:
                File file = chooser.getSelectedFile();
                lastDirectory = chooser.getCurrentDirectory().toString();
                return file;
            case JFileChooser.CANCEL_OPTION:
            case JFileChooser.ERROR_OPTION:
            default:
                break;
        }
        return null;

    }
    
    public File saveAsFileDialog (Component parent, String startDirectory, String defaultFileName) {

        File inFile = new File(startDirectory + File.separator + defaultFileName);
        JFileChooser chooser = new JFileChooser(startDirectory);
        
        chooser.setSelectedFile(inFile);
        
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        FileFilter filterText = new FileNameExtensionFilter( "TSV file (*.tsv)", "tsv");
//        FileFilter filterText = new FileNameExtensionFilter( "Text document (*.txt)", "txt");
        chooser.addChoosableFileFilter(filterText);
        
        chooser.setAcceptAllFileFilterUsed(false);
        // bug 759 java7 requires us to select it, otherwise the default choice would be empty
        chooser.setFileFilter(filterText);
       
        int action = chooser.showSaveDialog(parent);

        switch (action) {
            case JFileChooser.APPROVE_OPTION:
                File file = chooser.getSelectedFile();
                lastDirectory = chooser.getCurrentDirectory().toString();
                return file;
            case JFileChooser.CANCEL_OPTION:
            case JFileChooser.ERROR_OPTION:
            default:
                break;
        }
        return null;

    }

    protected void exportContestYaml() {

        File dir = selectDirectoryDialog(this, lastDirectory);
        if (dir != null) {
            ExportYAML exportYAML = new ExportYAML();
            try {
                exportYAML.exportFiles(dir.getAbsolutePath(), getContest());
                String outfilename = dir.getCanonicalPath() + File.separator + IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;
                viewFile(outfilename, outfilename);
            } catch (IOException e) {
                FrameUtilities.showMessage(this, "Error exporting contest.yaml", "Error exporting YAML " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * This method initializes exportRunTSVButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getExportRunTSVButton() {
        if (exportRunTSVButton == null) {
            exportRunTSVButton = new JButton();
            exportRunTSVButton.setText("Export submissions.tsv");
            exportRunTSVButton.setMnemonic(KeyEvent.VK_R);
            exportRunTSVButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    exportRunTSVFile();
                }
            });
        }
        return exportRunTSVButton;
    }

    protected void exportRunTSVFile() {
        
        File file = saveAsFileDialog(this, lastDirectory, "submissions.tsv");
        
        if (file != null) {
            SubmissionsTSVReport runsTSVReport = new SubmissionsTSVReport();
            Utilities.viewReport(runsTSVReport, "submissions.tsv file", getContest(), getController(), ! runsTSVReport.suppressHeaderFooter());
        }
    }
    
    /**
     * Parses input time.
     * 
     * @param freezeTime
     * @return number of minutes.
     */
    private long parseFreezeTime(String freezeTime) {
        
        if (freezeTime == null){
            return ContestInformation.DEFAULT_FREEZE_MINUTES;
        } else {
            
            long seconds = Utilities.convertStringToSeconds(freezeTime);
            if (seconds == -1){
                return ContestInformation.DEFAULT_FREEZE_MINUTES;
            }
            return seconds / 60; 
        }
    }
    
    protected void showSnapshotOfEventViewer() {
        showSnapshotOfEventViewer(false);
    }
    
    protected void showSnapshotOfEventViewer(boolean showFilteredFeed) {

        ResolverEventFeedXML eventFeedXML = new ResolverEventFeedXML();

        String eventFeed = null;
        if (! showFilteredFeed) {
            eventFeed = eventFeedXML.toXML(getContest());
        } else {
            ContestInformation info = getContest().getContestInformation();
            String freezeTime = info.getFreezeTime();
            long numberMinutesFreezeTime = parseFreezeTime(freezeTime);
            eventFeed = eventFeedXML.toXMLFreeze(getContest(), numberMinutesFreezeTime);

        }
        String[] lines = { eventFeed };
            
        MultipleFileViewer multipleFileViewer = new MultipleFileViewer(getController().getLog());
        multipleFileViewer.addTextintoPane("Event Feed", lines);
        multipleFileViewer.setTitle("PC^2 Event Feed at " + new Date());
        FrameUtilities.centerFrameFullScreenHeight(multipleFileViewer);
        multipleFileViewer.setVisible(true);
        
    }

    private JButton getViewEventFeed() {
        if (viewEventFeed == null) {
            viewEventFeed = new JButton("Show Event Feed");
            viewEventFeed.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showSnapshotOfEventViewer();
                }
            });
        }
        return viewEventFeed;
    }
} // @jve:decl-index=0:visual-constraint="10,10"
