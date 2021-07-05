// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import edu.csus.ecs.pc2.clics.CLICSJudgementType;
import edu.csus.ecs.pc2.clics.CLICSJudgementType.CLICS_JUDGEMENT_ACRONYM;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.shadow.ShadowController;
import edu.csus.ecs.pc2.shadow.ShadowJudgementInfo;
import edu.csus.ecs.pc2.shadow.ShadowJudgementPair;

/**
 * A plug-in pane which displays Shadow comparison results.
 * 
 * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
 *
 */
public class ShadowCompareRunsPane extends JPanePlugin {

    private static final long serialVersionUID = 1L;
    
    private ShadowController shadowController = null ;
    
    //the current judgement information from the shadow controller
    private Map<String, ShadowJudgementInfo> currentJudgementMap = null;
    
    //the table displaying the current results
    private JTable resultsTable = null ;
    
    //a pane displaying a summary of the current judgement comparison status
    private ShadowCompareSummaryPane summaryPanel = null ;

    private String lastDirectory = ".";

    private Log log;

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
    public ShadowCompareRunsPane(ShadowController shadowController) {
        Dimension size = new Dimension(600,600);
        this.setPreferredSize(size);
        this.setMinimumSize(size);
        
        this.shadowController = shadowController ;
        
        this.log = shadowController.getLog();
        
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
            
            private Border outside = new MatteBorder(1, 0, 1, 0, Color.RED);
            private Border inside = new EmptyBorder(0, 1, 0, 1);
            private Border highlight = new CompoundBorder(outside, inside);


//          String[] columnNames = { "Team", "Problem", "Language", "Submission ID", "PC2 Shadow", "Remote CCS", "Match?", "Overridden?" };

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

                // override color with yellow if either PC2 or Remote judgement is pending
                String pc2Judgement = (String) getModel().getValueAt(modelRow, 4);
                String remoteJudgement = (String) getModel().getValueAt(modelRow, 5);
                if ( (pc2Judgement != null && pc2Judgement.toLowerCase().contains("pending")) || 
                     (remoteJudgement != null && remoteJudgement.toLowerCase().contains("pending"))) {
                    c.setBackground(new Color(255, 255, 153));
                }
                
                // update font to bold & italic if row is selected
                if (isRowSelected(row)) {
                    c.setFont(new Font("Arial Bold", Font.ITALIC, 14));
                    ((JComponent)c).setBorder(highlight);
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
        resultsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

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
        String[] columnNames = { "Team", "Problem", "Language", "Submission ID", "PC2 Shadow", "Remote CCS", "Match?", "Overridden?" };
        
        //an array to hold the table data
        Object[][] data = new Object[currentJudgementMap.size()][8];
        
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
                if (!((String)data[row][4]).toLowerCase().contains("pending") &&
                    !((String)data[row][5]).toLowerCase().contains("pending") ) {
                    
                    data[row][6] = ((String) data[row][4]).equalsIgnoreCase((String) data[row][5]) ? "Y" : "N";
                }
            }
            
            data[row][7] = "N";

            row++;
        }
        
        //construct a TableModel from the data, also providing an overridden getColumnClass() method
        TableModel tableModel = new DefaultTableModel(data, columnNames){
            static final long serialVersionUID = 1;
            
//          String[] columnNames = { "Team", "Problem", "Language", "Submission ID", "PC2 Shadow", "Remote CCS", "Match?", "Overridden?" };
            Class<?>[] types = { Integer.class, String.class, String.class, Integer.class, String.class, String.class, String.class, String.class };
            
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
                        refreshResultsTable();
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
        
        Component horizontalStrut2 = Box.createHorizontalStrut(20);
        buttonPanel.add(horizontalStrut2);
        
        JButton resolveButton = new JButton("Resolve Selected");
        resolveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                
                // resolve currently selected runs and refresh the results table
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        boolean changed = resolveSelectedRuns();
                        if (changed) {
                            refreshResultsTable();
                        }
                    }
                });
            }
        });
        resolveButton.setToolTipText("Updates PC2 so that the PC2 judgement in all selected table rows matches the Remote CCS judgement");
        buttonPanel.add(resolveButton);
        
        
       
        return buttonPanel ;
    }
    
    private void refreshResultsTable() {
        
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
    
    /**
     * "Resolves" all currently selected runs by updating the run judgement in the PC2 Shadow to match
     * the judgement assigned by the remote Primary CCS.  Displays a warning dialog and requires confirmation
     * prior to actually updating the runs in PC2; displays an error dialog if there are no runs currently selected.
     * 
     * @return true if one or more runs were "resolved" (updated in PC2); false if no runs were changed.
     */
    private boolean resolveSelectedRuns() {
        
        //make sure there are some runs selected
        if (runsAreSelected()) {
            
        } else {
            //display error dialog
            JOptionPane.showMessageDialog(this, "There are no runs selected for resolving.", "No runs selected; nothing to resolve", JOptionPane.ERROR_MESSAGE);
            return false ;
        }
        
        //there are runs selected; determine how many
        int selectedRunCount = getCountOfSelectedRuns();
        String pluralizer = "";
        if (selectedRunCount>1) {
            pluralizer = "s";
        }
        
        //warn the user about the consequences of continuing
        String warningMsg = "";
        warningMsg += "You are about to change the judgement for " + selectedRunCount + " run" + pluralizer + " in PC2 from the current PC2 " + "\n";
        warningMsg += "judgement value to the value reported by the remote Primary CCS.\n\n";
        warningMsg += "Are you sure you want to do this?\n";
        warningMsg += "(Hit Yes to change the judgement on all selected runs; hit No or Cancel to abort without changing any judgements.)";
        
        int response = JOptionPane.showConfirmDialog(this, warningMsg, "Confirm intent to change PC2 Judgement(s)", JOptionPane.YES_NO_CANCEL_OPTION);
        
        if (response==JOptionPane.YES_OPTION) {
            
            if (isAllowedEditRun()) {
                
                //update the selected runs
                boolean result = updateSelectedRuns();
                if (result) {
                    log.log(Log.INFO, "Updated judgements in selected runs");
                } else {
                    log.log(Log.WARNING, "Update of selected run judgements failed");
                }
                return result;
                
            } else {
                
                //current Feeder client account does not have EditRun permission - display error msg
                ClientId clientId = getContest().getClientId();
                String client = clientId.getName();
                String msg = "";
                msg += "Your login account (" + client + ") does not have permission to edit (update) runs.\n\n" ;
                msg += "You will need to have an Administrator update your account permissions in order to be able to update runs.";
                
                JOptionPane.showMessageDialog(this, msg, "Missing Edit Permission", JOptionPane.WARNING_MESSAGE);
                log.log (Log.WARNING, "Client attempted to update run without having Edit Run permission");
                return false;
            }
        } else {
            log.log(Log.INFO, "Update of selected runs cancelled by user");
            return false;
        }
    }
    
    /**
     * Returns an indication of whether the current client has permission to Edit Runs.
     * @return true if the current client can edit runs; false if not.
     */
    private boolean isAllowedEditRun() {
        
        ClientId clientId = getContest().getClientId();
        if (getContest().getAccount(clientId).isAllowed(Permission.Type.EDIT_RUN)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns an indication of whether or not there are any runs currently selected in the Runs table.
     * 
     * @return true if the results table is not null and there is at least one run (row) in the table 
     *              which is selected; false otherwise.
     */
    private boolean runsAreSelected() {
        
        if (resultsTable==null) {
            return false;
        }
        
        int selectedCount = resultsTable.getSelectedRowCount();
        if (selectedCount > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns a count of the number of rows in the runs table which are currently selected.
     * 
     * @return the count of selected runs (rows) in the runs table.
     */
    private int getCountOfSelectedRuns() {
        if (resultsTable==null) {
            return 0;
        } else {
            return resultsTable.getSelectedRowCount();
        }
    }

    /**
     * Processes each currently-selected row in the runs table by updating the PC2 judgement for that
     * row to match the judgements specified by the remote (Primary) CCS, unless either PC2 or the
     * remote CCS shows the judgement as "Pending".
     * 
     * @return true if all the currently-selected runs were successfully updated; false if not.
     * 
     * 
     */
    private boolean updateSelectedRuns() {

        //debug:
        System.out.println ("Updating the following " + getCountOfSelectedRuns() + " runs in PC2:");
        
        //get JTable (view) row indices
        int [] viewRowIndices = resultsTable.getSelectedRows();
        
        //convert view indices to model indices
        int [] modelRowIndices = new int[viewRowIndices.length];
        for (int i=0; i<viewRowIndices.length; i++) {
            modelRowIndices[i] = resultsTable.convertRowIndexToModel(viewRowIndices[i]);
        }
        
        boolean result = true;
        for (int modelRow : modelRowIndices) {
            
            //get the run id
            //TODO: use an Enum for columnIds instead of hard-coded integers which can change if the table is rearranged
            Integer submissionId = (Integer) resultsTable.getModel().getValueAt(modelRow, 3); // 3=SubmissionID

            //debug:
            System.out.println ("   Submision ID: " + submissionId);
            
            //make sure the run is not "Pending" in either system
            String pc2Judgement = (String) resultsTable.getModel().getValueAt(modelRow, 4);      // 4 = PC2 Judgment
            String remoteJudgement = (String) resultsTable.getModel().getValueAt(modelRow, 5);   // 5=remote CCS judgement
            
            if (pc2Judgement.toLowerCase().contains("pending") || remoteJudgement.toLowerCase().contains("pending")) {
                
                JOptionPane.showMessageDialog(this, "Cannot update submission " + submissionId + " because it still has judgements Pending", 
                                                    "Submission judgement Pending", JOptionPane.WARNING_MESSAGE);
                log.log (Log.INFO, "Attempted to update pending submission: " + submissionId);
                result = false;
                
            } else {
            
                //verify the remote judgement is a valid value
                boolean isClicsAcronym = CLICSJudgementType.isCLICSAcronym(remoteJudgement);
                if (isClicsAcronym) {
                    
                    //get the CLICS acronym for the specified judgement 
                    CLICS_JUDGEMENT_ACRONYM judgementAcronym = CLICSJudgementType.getCLICSAcronymFromElementName(remoteJudgement);

                    // run is not pending in either system and the remote judgement is a valid CLICS judgement; 
                    // attempt to update PC2 to match remote CCS judgement
                    result &= updateRun(submissionId, judgementAcronym);
                    
                } else {
                    
                    //there is no known judgement matching the judgement string
                    JOptionPane.showMessageDialog(this, "Cannot update submission " + submissionId + ": invalid remote judgement: " + remoteJudgement, 
                                                        "Invalid judgement acronym", JOptionPane.WARNING_MESSAGE);
                    log.log (Log.INFO, "Attempted to update pending submission: " + submissionId);
                    result = false;
                }
            }
        }
        
        //return true iff every selected run was successfully updated
        return result;
        
    }

    /**
     * Invokes the PC2 server to update the status of the specified run to match the specified judgement.
     * 
     * @param submissionId the Id of the run to be updated.
     * 
     * @return true if the run was successfully updated; false if the run could not be updated for some reason 
     *              (such as not being able to find the specified run).
     */
    protected boolean updateRun(Integer submissionId, CLICS_JUDGEMENT_ACRONYM newJudgement) {

        log.log(Log.INFO, "Updating run " + submissionId + " to '" + newJudgement.name() + "'");
        
        //get all the runs
        Run [] allRuns = getController().getContest().getRuns();
        
        //search for the desired run by Id
        boolean found = false ;
        Run targetRun = null;
        for (Run run : allRuns) {
            if (run.getNumber()==submissionId) {
                targetRun = run;
                found = true;
                break;
            }
        }
        
        //if we didn't find the run, return failure
        if (!found) {
            
            JOptionPane.showMessageDialog(this, "Failed to find submission " + submissionId, "Submission not found", JOptionPane.WARNING_MESSAGE);
            log.log (Log.WARNING, "Failed to find run to be updated: " + submissionId);
            return false;
        }
        
        //if we get here we've found the run to be updated;
        //try to find a PC2 Judgement that matches the remote CCS's CLICS judgement
        Judgement [] judgementsArray = getContest().getJudgements();
        for (Judgement judgement : judgementsArray) {
            
            if (newJudgement.name().contentEquals(judgement.getAcronym())) {
                
                //we found a matching PC2 judgement; check if the new judgement is a "yes"
                boolean solved = CLICSJudgementType.isYesAcronym(newJudgement);
                
                //build a new JudgementRecord for PC2 containing the desired judgement values
                JudgementRecord judgementRecord = new JudgementRecord(judgement.getElementId(), getContest().getClientId(), solved, false);

                //duplicate the existing RunResultFiles, with null executionData (since we haven't actually re-executed the run)
                RunResultFiles runResultFiles = new RunResultFiles(targetRun, targetRun.getProblemId(), judgementRecord, null);
                
                log.log(Log.INFO, "Sending new JudgementRecord to PC2 server: "
                        + " judgementId=" + judgementRecord.getJudgementId()
                        + " elementId=" + judgementRecord.getElementId()
                        + " isSolved=" + judgementRecord.isSolved());
                
                //update the run in PC2
                getController().updateRun(targetRun, judgementRecord, runResultFiles);
                
                return true;
                
            }
        }
        
        //we failed to find a matching judgement
        JOptionPane.showMessageDialog(this, "Failed to find PC2 Judgement corresponding to Remote CCS Judgement for submission " + submissionId, "No such judgement", JOptionPane.WARNING_MESSAGE);
        log.log (Log.WARNING, "Failed to find PC2 Judgement corresponding to Remote CCS Judgement for submission " + submissionId);
        return false;
        
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
                        log.log(Log.SEVERE, "Exception saving file: " + e.getMessage(), e);
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
