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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import edu.csus.ecs.pc2.shadow.ShadowController;
import edu.csus.ecs.pc2.shadow.ShadowJudgementInfo;
import edu.csus.ecs.pc2.shadow.ShadowJudgementPair;

/**
 * A plug-in pane which displays Shadow comparison results.
 * 
 * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
 *
 */
public class ShadowComparePane extends JPanePlugin {

    private static final long serialVersionUID = 1L;
    
    private ShadowController shadowController = null ;
    
    //the current judgement information from the shadow controller
    private Map<String, ShadowJudgementInfo> currentJudgementMap = null;
    
    //the table displaying the current results
    private JTable resultsTable = null ;
    
    //a pane displaying a summary of the current judgement comparison status
    private ShadowCompareSummaryPane summaryPanel = null ;

    private String lastDirectory = ".";

    @Override
    public String getPluginTitle() {
        return "Shadow_Compare_Pane";
    }
    
    /**
     * This GUI class accepts a reference to a {@link ShadowController}, from which it obtains (by calling 
     * {@link ShadowController#getJudgementComparisonInfo()}) a
     * Mapping from Strings (which are submission IDs) to a {@link ShadowJudgementInfo} for that submission,
     * and displays the Shadow Judgement information in tabular form.
     * Each {@link ShadowJudgmentInfo} object contains a submissionID, TeamID, ProblemID, LanguageID, and a
     * {@link ShadowJudgementPair} containing the judgements from both the PC2 Shadow system and the Remote CCS,
     * and displays a table of those submissions and the corresponding judgements.
     * 
     * @param shadowController a ShadowController used to obtain a Mapping of submission IDs to ShadowJudgementInfo objects
     */
    public ShadowComparePane(ShadowController shadowController) {
        Dimension size = new Dimension(600,600);
        this.setPreferredSize(size);
        this.setMinimumSize(size);
        
        this.shadowController = shadowController ;
        
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JLabel header = new JLabel("Comparison of PC2 vs. Remote Judgements");
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(header);
        
        //get the framework for the table which will be used to display comparison results
        resultsTable = getResultsTable();
        
        //put the current comparison results into the table model
        resultsTable.setModel(getUpdatedResultsTableModel());
        
        //support sorting the table by clicking on the column headers
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(resultsTable.getModel());
        resultsTable.setRowSorter(sorter);
        resultsTable.setAutoCreateRowSorter(true); //necessary to allow updated model to display and sort correctly
                
        //put the results table in a scrollpane on the GUI
        JScrollPane scrollPane = new JScrollPane(resultsTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollPane);

        this.add(getSummaryPanel());
        
        this.add(getButtonPanel());
    }
        
    /**
     * Returns a JTable organized for containing a comparison, for each received submission, between the PC2 judgement
     * for the submission and the judgement assigned by the Remote CCS.
     * The returned JTable applies formatting to cell colors based on the status of the submission.
     * 
     * Note: this method does not actually fill in any table data; it is expected that external code will
     * invoke {@link #getUpdatedResultsTableModel()} to create and load the current comparison results into the table.
     * 
     * @return a JTable organized for containing judgement comparisons
     */
    private JTable getResultsTable() {

        JTable resultsTable = new JTable() {
            private static final long serialVersionUID = 1L;

//          String[] columnNames = { "Team", "Problem", "Language", "Submission ID", "PC2 Shadow", "Remote CCS", "Match?" };

            // override JTable's default renderer to set the background color based on the "Match?" value
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                // Color the row based on the "Match?" cell value
                c.setBackground(getBackground());
                int modelRow = convertRowIndexToModel(row);
                String matches = (String) getModel().getValueAt(modelRow, 6);
                if ("Y".equalsIgnoreCase(matches))
                    c.setBackground(new Color(153, 255, 153));
                if ("N".equalsIgnoreCase(matches))
                    c.setBackground(new Color(255, 153, 153));

                // override color with yellow if PC2 judgement is pending
                String pc2Judgement = (String) getModel().getValueAt(modelRow, 4);
                if (pc2Judgement != null && pc2Judgement.toLowerCase().contains("pending")) {
                    c.setBackground(new Color(255, 255, 153));
                }

                return c;
            }
            
            //we don't want any of the results cells to be editable
            public boolean isCellEditable(int nRow, int nCol) {
                return false;
            }
        };

        // set default "centering" renderers for strings and integers in the table
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        resultsTable.setDefaultRenderer(String.class, centerRenderer);
        resultsTable.setDefaultRenderer(Integer.class, centerRenderer);
        
        resultsTable.setRowSelectionAllowed(true);
        resultsTable.setColumnSelectionAllowed(false);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        return resultsTable;

    }
    
    /**
     * Returns a {@link TableModel} containing data for the current comparisons between the PC2 shadow and the Remote CCS.
     * 
     * @return
     */
    private TableModel getUpdatedResultsTableModel() {
        
        //get the current judgement information from the shadow controller
        currentJudgementMap = shadowController.getJudgementComparisonInfo();

        //define the columns for the table
        String[] columnNames = { "Team", "Problem", "Language", "Submission ID", "PC2 Shadow", "Remote CCS", "Match?" };
        
        //an array to hold the table data
        Object[][] data = new Object[currentJudgementMap.size()][7];
        
        //fill in each data row with info from the shadow controller's judgement map
        int row = 0;
        for (String key : currentJudgementMap.keySet()) {
            ShadowJudgementInfo curJudgementInfo = currentJudgementMap.get(key);
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
        
        //construct a TableModel from the data, also providing an overridden getColumnClass() method
        TableModel tableModel = new DefaultTableModel(data, columnNames){
            static final long serialVersionUID = 1;
            
//          String[] columnNames = { "Team", "Problem", "Language", "Submission ID", "PC2 Shadow", "Remote CCS", "Match?" };
            Class<?>[] types = { Integer.class, String.class, String.class, Integer.class, String.class, String.class, String.class };
            
            //return the appropriate class for the column so that correct cell renderer will be used
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return this.types[columnIndex];
            }
            
        };
        
        return tableModel ;
    }
    
    /**
     * Returns a JPanel containing a summary of the comparison information most recently obtained
     * from the {@link ShadowController}.  The global (field) variable "currentJudgementMap" is
     * used as the indicator of the most recently obtained comparison information; this variable
     * is set in {@link #getUpdatedResultsTableModel()}, which is called by this class's constructor
     * (and may also have been subsequently called again by the actionListener() for the "Refresh" button). 
     *  
     * @return a JPanel containing a submission comparison summary
     */
    private ShadowCompareSummaryPane getSummaryPanel() {
        
        if (summaryPanel==null) {
            summaryPanel = new ShadowCompareSummaryPane(currentJudgementMap);
        }
        
        return summaryPanel;
        
    }
    
    
    private JComponent getButtonPanel() {
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setMaximumSize(new Dimension(500,40));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                
                // refresh the results table
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {

                        //save info on the current sort column/order for the resultsTable
                        RowSorter<? extends TableModel> oldSorter = resultsTable.getRowSorter();
                        
                        //get a new model based on the current data
                        TableModel newTableModel = getUpdatedResultsTableModel();
                        
                        //create a new sorter based on the updated model
                        TableRowSorter<DefaultTableModel> newSorter = new TableRowSorter<DefaultTableModel>((DefaultTableModel) newTableModel);
                        if (oldSorter != null) {
                            newSorter.setSortKeys(oldSorter.getSortKeys());
                        }

                        //update the model and the row sorter in the table so the table remains sorted as before
                        resultsTable.setModel(newTableModel);
                        resultsTable.setRowSorter(newSorter);

                        //update the summary panel to correspond to the new table data
                        getSummaryPanel().updateSummary(currentJudgementMap);
                        
                    }
                });
            }
        });
        buttonPanel.add(refreshButton);
        
        Component horizontalStrut = Box.createHorizontalStrut(20);
        buttonPanel.add(horizontalStrut);

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
