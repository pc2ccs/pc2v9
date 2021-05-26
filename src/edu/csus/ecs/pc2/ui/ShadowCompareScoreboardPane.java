// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

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

    private ShadowCompareScoreboardSummaryPane statusPanel;

    private JPanel scoreboardsPanel;

    private JPanel scoreboardPanelHeader;

    private JPanel scoreboardScrollPanesPanel;

    private JScrollPane remoteCCSScrollPane;

    private JScrollPane pc2ScrollPane;
    private Component rigidArea;
    private Component rigidArea_1;

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
        Dimension size = new Dimension(1050,600);
        this.setPreferredSize(size);
        this.setMinimumSize(size);
        
        this.shadowController = shadowController ;
        
        if (shadowController==null) {
            showMessage(this, "Missing Shadow Controller", "ShadowCompareScoreboardPane constructor called with null Shadow Controller; cannot continue."); 
        }
        
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        
        this.add(getStatusPanel());
        this.add(getScoreboardsPanel());
        this.add(getButtonPanel());
        
        populateGUI();
        
    }
        
    private void populateGUI() {
        if (shadowController!=null) {
            
            // get scoreboard comparison info from the shadow controller
            ShadowScoreboardRowComparison[] comparedResults = shadowController.getScoreboardComparisonInfo();
            
//            //debug
//            System.out.println ("Comparison results from Shadow Controller (" + comparedResults.length + " rows):");
//            int rowNum = 1;
//            for (ShadowScoreboardRowComparison row : comparedResults) {
//                System.out.println ("Row " + rowNum++ + ": " + row);
//            }
            
            //update the summary status line
            getStatusPanel().updateSummary(comparedResults);
            
            // put the current comparison results into the table models
            getPC2ScoreboardTable().setModel(getUpdatedResultsTableModel(ScoreboardId.PC2, comparedResults));
            getRemoteScoreboardTable().setModel(getUpdatedResultsTableModel(ScoreboardId.REMOTE, comparedResults));
            
            //remove the "match" column from the views (although not from the model; it needs to remain in the 
            // model for access by the JTable CellRenderer)
            TableColumnModel tcm = getPC2ScoreboardTable().getColumnModel();
            tcm.removeColumn(tcm.getColumn(ScoreboardColumnId.MATCH.ordinal()));
            tcm = getRemoteScoreboardTable().getColumnModel();
            tcm.removeColumn(tcm.getColumn(ScoreboardColumnId.MATCH.ordinal()));

        } else {
            showMessage(this, "Missing Shadow Controller", "ShadowCompareScoreboardPane populateGUI() called with null Shadow Controller; cannot populate GUI."); 
        }
        
    }
    

    private JTable getPC2ScoreboardTable() {
        if (pc2ScoreboardTable==null) {
            pc2ScoreboardTable = getScoreboardTable();
        }
        return pc2ScoreboardTable;
    }
    
    private JTable getRemoteScoreboardTable() {
        if (remoteScoreboardTable==null) {
            remoteScoreboardTable = getScoreboardTable();
        }
        return remoteScoreboardTable;
    }


    private JPanel getScoreboardsPanel( ) {
        
        if (scoreboardsPanel == null) {

            scoreboardsPanel = new JPanel();
            scoreboardsPanel.setLayout(new BoxLayout(scoreboardsPanel, BoxLayout.PAGE_AXIS));
            
            scoreboardsPanel.add(getScoreboardPanelHeader());
            scoreboardsPanel.add(getScoreboardScrollPanesPanel());
        }

        return scoreboardsPanel;
    }
    
    private JPanel getScoreboardPanelHeader() {

        if (scoreboardPanelHeader==null) {
            scoreboardPanelHeader = new JPanel();
            scoreboardPanelHeader.setMaximumSize(new Dimension(600, 30));
            scoreboardPanelHeader.setMinimumSize(new Dimension(100, 20));
            JLabel pc2Header = new JLabel("PC2 Scoreboard");
            JLabel remoteHeader = new JLabel("Remote CCS Scoreboard");
            scoreboardPanelHeader.add(pc2Header);
            scoreboardPanelHeader.add(getRigidArea());
            scoreboardPanelHeader.add(remoteHeader);
        }
        return scoreboardPanelHeader;
    }
        
    
    private JPanel getScoreboardScrollPanesPanel() {
        
        if (scoreboardScrollPanesPanel==null) {
            scoreboardScrollPanesPanel = new JPanel();
            scoreboardScrollPanesPanel.setLayout(new BoxLayout(scoreboardScrollPanesPanel, BoxLayout.LINE_AXIS));
            scoreboardScrollPanesPanel.add(getPC2ScoreboardScrollPane());
            scoreboardScrollPanesPanel.add(getRigidArea_1());
            scoreboardScrollPanesPanel.add(getRemoteCCSScrollPane());
        }
        
        return scoreboardScrollPanesPanel;
    }
    
    
    private JScrollPane getRemoteCCSScrollPane() {
        
        if (remoteCCSScrollPane == null) {

            // get the table which will be used to display scoreboard results
            remoteScoreboardTable = getScoreboardTable();

            // put the results table into a scrollpane 
            remoteCCSScrollPane = new JScrollPane(remoteScoreboardTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            remoteCCSScrollPane.setPreferredSize(new Dimension(500, 419));
            remoteCCSScrollPane.setMinimumSize(new Dimension(500, 23));
            remoteCCSScrollPane.setMaximumSize(new Dimension(500, 32767));
            
            //debugging help:
            remoteCCSScrollPane.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.red),
                    remoteCCSScrollPane.getBorder()));
        }
        return remoteCCSScrollPane;
    }

    private Component getPC2ScoreboardScrollPane() {
        
        if (pc2ScrollPane==null) {
            
            // get the table which will be used to display comparison results
            pc2ScoreboardTable = getScoreboardTable();
                        
            // put the results table in a scrollpane
            pc2ScrollPane = new JScrollPane(pc2ScoreboardTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            pc2ScrollPane.setPreferredSize(new Dimension(500, 419));
            pc2ScrollPane.setMinimumSize(new Dimension(500, 23));
            pc2ScrollPane.setMaximumSize(new Dimension(500, 32767));
            
            //debugging help:
            pc2ScrollPane.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.green),
                    pc2ScrollPane.getBorder()));
        }
        
        return pc2ScrollPane;
    }

    /**
     * Returns a {@link ShadowCompareScoreboardSummaryPane} containing a summary of the comparison information most 
     * recently obtained from the {@link ShadowController}.  
     *  
     * @return a ShadowCompareScoreboardSummaryPane containing a comparison summary (scoreboards do/do not match)
     */
    private ShadowCompareScoreboardSummaryPane getStatusPanel() {
        if (statusPanel==null) {
            statusPanel = new ShadowCompareScoreboardSummaryPane();
       }
        return statusPanel;
    }

    /**
     * Returns a JTable organized for containing Scoreboard representations, either for a PC2 scoreboard or 
     * for a Remote CSS scoreboard.
     * The returned JTable applies formatting to row cell colors based on the status of the row comparisons
     * (that is, whether the row matched its corresponding row in the other scoreboard type).
     * 
     * Note: this method does not actually fill in any table data; it is expected that external code will
     * invoke {@link #getUpdatedResultsTableModel()} to create and load the current comparison results into the table.
     * 
     * @return a JTable organized for containing scoreboard comparisons.
     */
    private JTable getScoreboardTable() {

        JTable resultsTable = new JTable() {
            private static final long serialVersionUID = 1L;

//          String[] columnNames = { "Rank", "Team Id", "Num Solved", "Total Time", "Match" };
            
            // override JTable's default renderer to set the background color based on the "Match?" value
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                //default to normal background
                c.setBackground(getBackground());
                
                //map the specified row index number to the corresponding model row (index numbers can change due
                // to sorting; model row numbers never changed).
                int modelRow = convertRowIndexToModel(row);
                
                //check if this is a row which had no corresponding row in the other scoreboard table (such rows 
                // have "?" in all data fields in this table)
                if (hasCorrespondingRow(modelRow)) {

                    // Color the row based on the "Match?" cell value, which indicates whether this row matches its
                    //  corresponding row in the other scoreboard table
                    boolean matches = (boolean) getModel().getValueAt(modelRow, ScoreboardColumnId.MATCH.ordinal());
                    if (matches) {
                        c.setBackground(new Color(153, 255, 153)); // light green for all cells in this row
                    } else {
                        c.setBackground(new Color(255, 153, 153)); // light red for all celss in this row
                    }
                }
                
                return c;
            }
            
            /**
             * Returns an indication of whether or not the specified row in the model has a non-null corresponding row
             * in the other scoreboard table.  Rows in a scoreboard table which have no corresponding row in the other
             * scoreboard table have all their data values set to "?" by method getUpdatedResultsTableModel().
             * 
             * @param modelRow the row in this table's model to be checked.
             * 
             * @return true if the specified model row has a non-null corresponding row in the other scoreboard table; 
             *          false if not.
             */
            private boolean hasCorrespondingRow(int modelRow) {
                //get the data values out of the specified model row
                String rank = getModel().getValueAt(modelRow, ScoreboardColumnId.RANK.ordinal()).toString();
                String team = getModel().getValueAt(modelRow, ScoreboardColumnId.TEAM_ID.ordinal()).toString();
                String numSolved = getModel().getValueAt(modelRow, ScoreboardColumnId.NUM_SOLVED.ordinal()).toString();
                String time = getModel().getValueAt(modelRow, ScoreboardColumnId.TOTAL_TIME.ordinal()).toString();
                
                //assume there IS a corresponding row in the other scoreboard table
                boolean hasCorrespondingRow = true;
                
                //check whether the specified row has all data values set to the string "?" -- if so, there's no corresponding
                // row in the other table.  (See {@link ShadowCompareScoreboardPane#getUpdatedResultsTableModel()} for further details.)
                if (rank.contentEquals("?") && team.contentEquals("?") && numSolved.contentEquals("?") && time.contentEquals("?")) {
                    hasCorrespondingRow = false;
                }
               
                return hasCorrespondingRow;
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
    
    private enum ScoreboardId {PC2, REMOTE} ;
    
    private enum ScoreboardColumnId {RANK, TEAM_ID, NUM_SOLVED, TOTAL_TIME, MATCH} ;
    
    /**
     * Returns a {@link TableModel} containing data for the current comparisons between the PC2 shadow and the Remote CCS.
     * 
     * @param scoreboardId the scoreboard (PC2 or Remote) for which data should be extracted from the 
     *                  specified {@link ShadowScoreboardRowComparison} array and used to populate the returned TableModel.
     * @param comparedResults an array of ShadowScoreboardRowComparisons containing comparisons of corresponding rows of the
     *          pc2 and remote scoreboards.
     * 
     * @return a {@link TableModel} populated with table information for the specified scoreboard.
     */
    private TableModel getUpdatedResultsTableModel(ScoreboardId scoreboardId, ShadowScoreboardRowComparison[] comparedResults) {

        //define the columns for the table
        //TODO: use Enum name/values instead of hard-coded strings for columnNames
        String[] columnNames = { "Rank", "Team Id", "Num Solved", "Total Time", "Match" };
       
        //an array to hold the table data
        Object[][] data = new Object[comparedResults.length][ScoreboardColumnId.values().length];
        
        //fill in each data row with info for the specified scoreboard from the compared results
        for (int row=0; row<comparedResults.length; row++) {
            ShadowScoreboardRowComparison curSB = comparedResults[row];
            TeamScoreRow curRow ;
            if (scoreboardId==ScoreboardId.PC2) {
                curRow = curSB.getSb1Row();
            } else {
                curRow = curSB.getSb2Row();
            }
            if (curRow!=null) {
                data[row][ScoreboardColumnId.RANK.ordinal()] = curRow.getRank();
                data[row][ScoreboardColumnId.TEAM_ID.ordinal()] = curRow.getTeam_id();
                data[row][ScoreboardColumnId.NUM_SOLVED.ordinal()] = curRow.getScore().getNum_solved();
                data[row][ScoreboardColumnId.TOTAL_TIME.ordinal()] = curRow.getScore().getTotal_time();
                data[row][ScoreboardColumnId.MATCH.ordinal()] = curSB.isMatch();
            } else {
                data[row][ScoreboardColumnId.RANK.ordinal()] = "?";
                data[row][ScoreboardColumnId.TEAM_ID.ordinal()] = "?";
                data[row][ScoreboardColumnId.NUM_SOLVED.ordinal()] = "?";
                data[row][ScoreboardColumnId.TOTAL_TIME.ordinal()] = "?";               
                data[row][ScoreboardColumnId.MATCH.ordinal()] = false;
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
                        populateGUI();
                    }
                });
            }
        });
        buttonPanel.add(refreshButton);

        return buttonPanel ;
    }
    
    private Component getRigidArea() {
        if (rigidArea == null) {
        	rigidArea = Box.createRigidArea(new Dimension(300, 20));
        }
        return rigidArea;
    }
    private Component getRigidArea_1() {
        if (rigidArea_1 == null) {
        	rigidArea_1 = Box.createRigidArea(new Dimension(20, 20));
        }
        return rigidArea_1;
    }
}
