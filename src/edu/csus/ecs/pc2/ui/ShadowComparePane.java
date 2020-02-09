// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import edu.csus.ecs.pc2.shadow.ShadowJudgementInfo;
import edu.csus.ecs.pc2.shadow.ShadowJudgementPair;
import javax.swing.Box;

/**
 * A plug-in pane which displays Shadow comparison results.
 * 
 * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
 *
 */
public class ShadowComparePane extends JPanePlugin {

    private static final long serialVersionUID = 1L;
    
    private Map<String, ShadowJudgementInfo> currentJudgementMap ;
    
    private String lastDirectory = ".";


    @Override
    public String getPluginTitle() {
        return "Shadow_Compare_Pane";
    }
    
    /**
     * This GUI class accepts a Mapping from Strings (which are submission IDs) to a {@link ShadowJudgementInfo} for that submission,
     * and displays the Shadow Judgement information in tabular form.
     * Each {@link ShadowJudgmentInfo} object contains a submissionID, TeamID, ProblemID, LanguageID, and a
     * {@link ShadowJudgementPair} containing the judgements from both the PC2 Shadow system and the Remote CCS,
     * and displays a table of those submissions and the corresponding judgements.
     * 
     * The received map is originally constructed (and passed to here) by {@link ShadowController#getJudgementComparisonInfo()).
     * 
     * @param map a Mapping of submission IDs to ShadowJudgementInfo objects
     */
    public ShadowComparePane(Map<String,ShadowJudgementInfo> map) {
        Dimension size = new Dimension(600,600);
        this.setPreferredSize(size);
        this.setMinimumSize(size);
        
        currentJudgementMap = map;
        
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JLabel header = new JLabel("Comparison of PC2 vs. Remote Judgements");
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(header);
        
        if (map != null && !map.keySet().isEmpty()) {
            
            String[] columnNames = { "Team", "Problem", "Language", "Submission ID", "PC2 Shadow", "Remote CCS", "Match?" };
            Object[][] data = new Object[map.size()][7];
            int row = 0;
            for (String key : map.keySet()) {
                ShadowJudgementInfo curJudgementInfo = map.get(key);
                data[row][0] = curJudgementInfo.getTeamID();
                data[row][1] = curJudgementInfo.getProblemID();
                data[row][2] = curJudgementInfo.getLanguageID();
                data[row][3] = new Integer(key);
                ShadowJudgementPair curPair = curJudgementInfo.getShadowJudgementPair();
                
                if (curPair!=null) {
                    data[row][4] = curPair.getPc2Judgement();
                    data[row][5] = curPair.getRemoteCCSJudgement();
                }
                data[row][6] = "---";
                if (data[row][4]!=null && data[row][5]!=null) {
                    if (!((String)data[row][4]).toLowerCase().contains("pending")) {
                        data[row][6] = ((String) data[row][4]).equalsIgnoreCase((String) data[row][5]) ? "Y" : "N";
                    }
                }

                row++;
            }
            
            JTable results = new JTable() {
                    private static final long serialVersionUID = 1L;

//                    String[] columnNames = { "Team", "Problem", "Language", "Submission ID", "PC2 Shadow", "Remote CCS", "Match?" };

                    //override JTable's default renderer to set the background color based on the "Match?" value
                    public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
                    {
                        Component c = super.prepareRenderer(renderer, row, column);

                        //  Color row based on a cell value

                            c.setBackground(getBackground());
                            int modelRow = convertRowIndexToModel(row);
                            String matches = (String)getModel().getValueAt(modelRow, 6);
                            if ("Y".equalsIgnoreCase(matches)) c.setBackground(new Color(153,255,153));
                            if ("N".equalsIgnoreCase(matches)) c.setBackground(new Color(255,153,153));
                            
                            //override color with yellow if PC2 judgement is pending
                            String pc2Judgement = (String)getModel().getValueAt(modelRow, 4);
                            if (pc2Judgement!=null && pc2Judgement.toLowerCase().contains("pending")) {
                                c.setBackground(new Color(255,255,153));
                            }

                        return c;
                    }

            };

            
            results.setModel(new DefaultTableModel(data, columnNames){
                static final long serialVersionUID = 1;
                
//              String[] columnNames = { "Team", "Problem", "Language", "Submission ID", "PC2 Shadow", "Remote CCS", "Match?" };

                Class[] types = { Integer.class, String.class, String.class, Integer.class, String.class, String.class, String.class };

                @Override
                public Class getColumnClass(int columnIndex) {
                    return this.types[columnIndex];
                }
                
            });
            
            TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(results.getModel());
            results.setRowSorter(sorter);
            
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
            results.setDefaultRenderer(String.class, centerRenderer);
            results.setDefaultRenderer(Integer.class, centerRenderer);

            this.add(new JScrollPane(results, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));

        } else {
            JLabel label = new JLabel("Comparison map is null or empty");
            this.add(label);
        }
        
        this.add(getSummaryPanel());
        
        this.add(getButtonPanel());
        
    }
    
    private JComponent getSummaryPanel() {
        
        JPanel summaryPanel = new JPanel();
        summaryPanel.setMaximumSize(new Dimension(500,40));

        int submissionCount = currentJudgementMap.keySet().size();
        
        JLabel subCountLabel = new JLabel();
        subCountLabel.setText("Total Submissions = " + new Integer(submissionCount).toString());
        summaryPanel.add(subCountLabel);
        
//        JLabel shadowYesCountLabel = new JLabel();
//        int shadowYesCount = 0 ;
//        for (String key : currentJudgementMap.keySet()) {
//            ShadowJudgementPair pair = currentJudgementMap.get(key);
//            String pc2Judgment = pair.getPc2Judgement();
//            if (pc2Judgment!=null && (pc2Judgment.equalsIgnoreCase("AC")||pc2Judgment.equalsIgnoreCase("Yes")) ){
//                shadowYesCount++;
//            }
//        }
//        
//        Component horizontalGlue = Box.createHorizontalGlue();
//        summaryPanel.add(horizontalGlue);
//        shadowYesCountLabel.setText("Shadow AC = " + new Integer(shadowYesCount).toString());
//        summaryPanel.add(shadowYesCountLabel);
//        
//        JLabel remoteYesCountLabel = new JLabel();
//        int remoteYesCount = 0 ;
//        for (String key : currentJudgementMap.keySet()) {
//            ShadowJudgementPair pair = currentJudgementMap.get(key);
//            String remoteJudgment = pair.getRemoteCCSJudgement();
//            if (remoteJudgment!=null && (remoteJudgment.equalsIgnoreCase("AC")||remoteJudgment.equalsIgnoreCase("Yes")) ){
//                remoteYesCount++;
//            }
//        }
//        
//        Component horizontalGlue_1 = Box.createHorizontalGlue();
//        summaryPanel.add(horizontalGlue_1);
//        remoteYesCountLabel.setText("Remote AC = " + new Integer(remoteYesCount).toString());
//        summaryPanel.add(remoteYesCountLabel);
        
        JLabel matchCounts = new JLabel();
        int match = 0;
        int noMatch = 0;
        for (String submissionID : currentJudgementMap.keySet()) {

            String pc2Judgement = currentJudgementMap.get(submissionID).getShadowJudgementPair().getPc2Judgement();
            String remoteJudgement = currentJudgementMap.get(submissionID).getShadowJudgementPair().getRemoteCCSJudgement();
            if (pc2Judgement!=null && remoteJudgement!=null && (pc2Judgement.equalsIgnoreCase(remoteJudgement))) {
                match++ ;
            } else {
                noMatch++ ;                    
            }
        }
        
        Component horizontalGlue_2 = Box.createHorizontalGlue();
        summaryPanel.add(horizontalGlue_2);
        matchCounts.setText("Matches: " + match + "   Non-matches: " + noMatch);
        summaryPanel.add(matchCounts);
        
       return summaryPanel ;
    }
    
    
    private JComponent getButtonPanel() {
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setMaximumSize(new Dimension(500,40));
        JButton saveButton = new JButton("Save As .csv");
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                saveCSVFile();
            }
        });
                
        buttonPanel.add(saveButton);
        
        return buttonPanel ;
    }
    
    /**
     * Saves the current judgement comparisons in a CSV (comma-separate-values) file.
     */
    private void saveCSVFile() {
        
        JFileChooser chooser = new JFileChooser(lastDirectory);
        int action = chooser.showSaveDialog(null);
        
        if (action == JFileChooser.APPROVE_OPTION) {
            
            File saveFile = chooser.getSelectedFile();
            lastDirectory = chooser.getCurrentDirectory().toString();

            if (saveFile != null) {
                
                //see if we're about to overwrite an existing file
                if (saveFile.isFile()){
                    //yes; get confirmation ok
                    int result = FrameUtilities.yesNoCancelDialog(null, "Overwrite "+saveFile.getName()+" ?", "Overwrite File?");
                    if (result != JOptionPane.YES_OPTION) {
                        return;
                    }
                } else {
                    try {
                        saveFile.createNewFile();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                BufferedWriter bw = null;
                try {
                    
                    bw = new BufferedWriter(new FileWriter(saveFile));
                    
                    //write CSV header to file
//                  String[] columnNames = { "Team", "Problem", "Language", "Submission ID", "PC2 Shadow", "Remote CCS", "Match?" };

                    String data = "TEAM,PROBLEM,LANGUAGE,SUBMISSION_ID,PC2_SHADOW,REMOTE_CCS,MATCH?" ;
                    bw.write(data);
                    bw.newLine();
                    
                    //write CSV for each judgement to file
                    for (String submissionID : currentJudgementMap.keySet()) {
                        data = getCSVString(currentJudgementMap.get(submissionID));
                        bw.write(data);
                        bw.newLine();
                    }
                    System.out.println ("Wrote Shadow Comparison data to file '" + saveFile.getName() + "'");

               } catch (IOException ioe) {
                   ioe.printStackTrace();
               }
               finally { 
                  try{
                     if(bw!=null) {
                         bw.close();
                     }
                  }catch(Exception ex){
                      System.err.println("Error in closing the BufferedWriter"+ex);
                  }
              }
            }
        };
    }
        
    /**
     * Returns a comma-separate-value string containing the values found in the specified {@link ShadowJudgementInfo}.
     * 
     * @param info a ShadowJudgementInfo
     * 
     * @return a comma-separated-values string for the info
     */
    private String getCSVString(ShadowJudgementInfo info) {
        
        String teamID = info.getTeamID();
        String probID = info.getProblemID();
        String langID = info.getLanguageID();
        String submissionID = info.getSubmissionID() ;
        ShadowJudgementPair judgementPair = info.getShadowJudgementPair();
        String pc2Result = "";
        String remoteResult = "";
        if (judgementPair!=null) {
            pc2Result = judgementPair.getPc2Judgement() ;
            remoteResult = judgementPair.getRemoteCCSJudgement();
        }
        
        //TODO:  need to escape any commas in either the pc2Result or the remoteResult
        String retStr = "" ;
        retStr += teamID + "," ;
        retStr += probID + "," ;
        retStr += langID + "," ;
        retStr += submissionID + "," ;
        retStr += pc2Result + "," ;
        retStr += remoteResult + ",";
        
        String match ;
        if (pc2Result!=null && remoteResult!=null) {
            match = pc2Result.trim().equalsIgnoreCase(remoteResult) ? "Y" : "N";
        } else {
            match = "---";
        }
        retStr += match ;

        return retStr;
    }
    
}
