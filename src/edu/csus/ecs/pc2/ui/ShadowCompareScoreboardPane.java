// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
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

import edu.csus.ecs.pc2.core.standings.json.TeamScoreRow;
import edu.csus.ecs.pc2.shadow.ShadowController;
import edu.csus.ecs.pc2.shadow.ShadowScoreboardRowComparison;

/**
 * A plug-in pane which displays comparison results between the PC2 Shadow system scoreboard and 
 * a remote CCS scoreboard.
 * 
 * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
 *
 */
public class ShadowCompareScoreboardPane extends JPanePlugin {

    private static final long serialVersionUID = 1L;
    
    private ShadowController shadowController = null ;
    
    private JTable pc2ScoreboardTable;

    private JTable remoteScoreboardTable;

    private ShadowCompareScoreboardSummaryPane shadowCompareScoreboardSummaryPane;

    @Override
    public String getPluginTitle() {
        return "Shadow_Compare_Scoreboard_Pane";
    }
    
    /**
     * Constructs a ShadowCompareScoreboardPane which accepts a reference to a {@link ShadowController}, 
     * from which it obtains scoreboard comparison information (by calling {@link ShadowController#getScoreboardComparisonInfo()})
     * and uses that comparison information to create a GUI display comparing the PC2 Shadow and Remote CCS scoreboards.
     * 
     * @param shadowController a ShadowController used to obtain scoreboard comparison information.
     */
    public ShadowCompareScoreboardPane(ShadowController shadowController) {
        Dimension size = new Dimension(800,600);
        this.setPreferredSize(size);
        this.setMinimumSize(size);
        
        this.shadowController = shadowController ;
        
        if (shadowController==null) {
            showMessage(this, "Missing Shadow Controller", "ShadowCompareScoreboardPane constructor called with null Shadow Controller; cannot continue."); 
        }
        
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JLabel header = new JLabel("Comparison of PC2 vs. Remote Scoreboards");
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(header);
        
        //get scoreboard comparison info from the shadow controller
        ShadowScoreboardRowComparison [] comparedResults = shadowController.getScoreboardComparisonInfo();

        //display equalness status in GUI
        this.add(getShadowCompareScoreboardSummaryPane());
        
        //get the tables which will be used to display comparison results
        pc2ScoreboardTable = getScoreboardTable();
        remoteScoreboardTable = getScoreboardTable();
        
        //put the current comparison results into the table models
        pc2ScoreboardTable.setModel(getUpdatedResultsTableModel(1,comparedResults));
        remoteScoreboardTable.setModel(getUpdatedResultsTableModel(2,comparedResults));
       
        //support sorting the table by clicking on the column headers
        TableRowSorter<TableModel> sorter1 = new TableRowSorter<TableModel>(pc2ScoreboardTable.getModel());
        pc2ScoreboardTable.setRowSorter(sorter1);
        pc2ScoreboardTable.setAutoCreateRowSorter(true); //necessary to allow updated model to display and sort correctly
        TableRowSorter<TableModel> sorter2 = new TableRowSorter<TableModel>(remoteScoreboardTable.getModel());
        remoteScoreboardTable.setRowSorter(sorter2);
        remoteScoreboardTable.setAutoCreateRowSorter(true); //necessary to allow updated model to display and sort correctly
               
        //put the results table in a scrollpane on the GUI
        JScrollPane pc2ScrollPane = new JScrollPane(pc2ScoreboardTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(pc2ScrollPane);
        JScrollPane remoteScrollPane = new JScrollPane(pc2ScoreboardTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(remoteScrollPane);

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
     * @param comparedResults 
     * @param i 
     * 
     * @return a JTable organized for containing judgement comparisons
     */
    private JTable getScoreboardTable() {

        JTable resultsTable = new JTable() {
            private static final long serialVersionUID = 1L;

//          String[] columnNames = { "Rank", "Team Id", "Num Solved", "Total Time" };

            //TODO: update this to match Scoreboard row renderings...
            
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
     * @param comparedResults 
     * @param i 
     * 
     * @return
     */
    private TableModel getUpdatedResultsTableModel(int scoreboardNum, ShadowScoreboardRowComparison[] comparedResults) {
        
        final int RANK_COLUMN = 0;
        final int TEAM_ID_COLUMN = 1;
        final int NUM_SOLVED_COLUMN = 2; 
        final int TOTAL_TIME_COLUMN = 3;
        
        //get the current scoreboard information from the compared results based on the scoreboard number (1=pc2, 2=remote)

        //define the columns for the table
        String[] columnNames = { "Rank", "Team Id", "Num Solved", "Total Time" };
       
        //an array to hold the table data
        Object[][] data = new Object[comparedResults.length][4];
        
        //fill in each data row with info for the specified scoreboard from the compared results
        for (int row=0; row<comparedResults.length; row++) {
            ShadowScoreboardRowComparison curSB = comparedResults[row];
            TeamScoreRow curRow ;
            if (scoreboardNum==1) {
                curRow = curSB.getSb1Row();
            } else {
                curRow = curSB.getSb2Row();
            }
            if (curRow!=null) {
                data[row][RANK_COLUMN] = curRow.getRank();
                data[row][TEAM_ID_COLUMN] = curRow.getTeam_id();
                data[row][NUM_SOLVED_COLUMN] = curRow.getScore().getNum_solved();
                data[row][TOTAL_TIME_COLUMN] = curRow.getScore().getTotal_time();
            } else {
                data[row][RANK_COLUMN] = "?";
                data[row][TEAM_ID_COLUMN] = "?";
                data[row][NUM_SOLVED_COLUMN] = "?";
                data[row][TOTAL_TIME_COLUMN] = "?";               
            }
        }
        
        //construct a TableModel from the data, also providing an overridden getColumnClass() method
        TableModel tableModel = new DefaultTableModel(data, columnNames){
            static final long serialVersionUID = 1;
            
//            String[] columnNames = { "Rank", "Team Id", "Num Solved", "Total Time" };
            Class<?>[] types = { Integer.class, Integer.class, Integer.class, Integer.class };
            
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
    public ShadowCompareScoreboardSummaryPane getShadowCompareScoreboardSummaryPane() {
        
        if (shadowCompareScoreboardSummaryPane==null) {
            shadowCompareScoreboardSummaryPane = new ShadowCompareScoreboardSummaryPane();
        }
        
        return shadowCompareScoreboardSummaryPane;
        
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

                        //save info on the current sort column/order for the resultsTables
                        RowSorter<? extends TableModel> oldPC2Sorter = pc2ScoreboardTable.getRowSorter();
                        RowSorter<? extends TableModel> oldRemoteCCSSorter = remoteScoreboardTable.getRowSorter();
                        
                        //get the current comparison info
                        ShadowScoreboardRowComparison[] currentComparison = shadowController.getScoreboardComparisonInfo();
                       
                        //get a new model based on the current data
                        TableModel newPC2TableModel = getUpdatedResultsTableModel(1,currentComparison);
                        TableModel newRemoteTableModel = getUpdatedResultsTableModel(2,currentComparison);
                                                
                        //create new sorters based on the updated models
                        TableRowSorter<DefaultTableModel> newPC2Sorter = new TableRowSorter<DefaultTableModel>((DefaultTableModel) newPC2TableModel);
                        if (oldPC2Sorter != null) {
                            newPC2Sorter.setSortKeys(oldPC2Sorter.getSortKeys());
                        }
                        TableRowSorter<DefaultTableModel> newRemoteCCSSorter = new TableRowSorter<DefaultTableModel>((DefaultTableModel) newRemoteTableModel);
                        if (oldRemoteCCSSorter != null) {
                            newRemoteCCSSorter.setSortKeys(oldRemoteCCSSorter.getSortKeys());
                        }

                        //update the models and the row sorters in the tables so the tables remain sorted as before
                        pc2ScoreboardTable.setModel(newPC2TableModel);
                        pc2ScoreboardTable.setRowSorter(newPC2Sorter);
                        remoteScoreboardTable.setModel(newRemoteTableModel);
                        remoteScoreboardTable.setRowSorter(newRemoteCCSSorter);

                        //update the summary panel to correspond to the new table data
                        shadowCompareScoreboardSummaryPane.updateSummary(currentComparison);
                        
                    }
                });
            }
        });
        buttonPanel.add(refreshButton);
        
        Component horizontalStrut = Box.createHorizontalStrut(20);
        buttonPanel.add(horizontalStrut);

        return buttonPanel ;
    }
    
}
