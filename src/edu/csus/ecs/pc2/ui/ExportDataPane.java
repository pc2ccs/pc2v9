package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.csus.ecs.pc2.exports.ccs.ResultsFile;
import edu.csus.ecs.pc2.exports.ccs.ScoreboardFile;

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
        
        // TODO CCS prompt for path to save this file to..
        
        String outfilename = "results.tsv";
        try {
            ResultsFile resultsFile = new ResultsFile();
            String [] lines = resultsFile.createTSVFileLines(getContest());
            writeLinesToFile (outfilename, lines);
            JOptionPane.showMessageDialog(this, "Saved to "+outfilename);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to save "+outfilename+" "+e.getMessage());
            e.printStackTrace();
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
        
        // TODO CCS prompt for path to save this file to..
        
        String outfilename = "scoreboard.tsv";
        try {
            ScoreboardFile scoreboardFile = new ScoreboardFile();
            String [] lines = scoreboardFile.createTSVFileLines(getContest());
            writeLinesToFile (outfilename, lines);
            JOptionPane.showMessageDialog(this, "Saved to "+outfilename);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to save "+outfilename+" "+e.getMessage());
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

    protected void saveUserDataTSVFile() {
        // TODO CCS save user data

    }

} // @jve:decl-index=0:visual-constraint="10,10"
