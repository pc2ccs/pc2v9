// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
    private JButton helpButton;

    private Component rigidArea;
    private Component rigidArea_1;
    private Component rigidArea_2;
    private Component rigidArea_3;
    private Component rigidArea_4;

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
        this.setPreferredSize(new Dimension(1120, 600));
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
            
            //show "busy"
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
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
            getPC2ScoreboardTable().setModel(getUpdatedResultsTableModel(ScoreboardType.PC2, comparedResults));
            getRemoteScoreboardTable().setModel(getUpdatedResultsTableModel(ScoreboardType.REMOTE, comparedResults));
            
            //set variable column widths in tables
            //TODO: use an enum to define column numbers instead of hard-coded ints
            getPC2ScoreboardTable().getColumnModel().getColumn(0).setPreferredWidth(5);
            getPC2ScoreboardTable().getColumnModel().getColumn(1).setPreferredWidth(100);
            getPC2ScoreboardTable().getColumnModel().getColumn(2).setPreferredWidth(5);
            getPC2ScoreboardTable().getColumnModel().getColumn(3).setPreferredWidth(10);
            getRemoteScoreboardTable().getColumnModel().getColumn(0).setPreferredWidth(5);
            getRemoteScoreboardTable().getColumnModel().getColumn(1).setPreferredWidth(100);
            getRemoteScoreboardTable().getColumnModel().getColumn(2).setPreferredWidth(5);
            getRemoteScoreboardTable().getColumnModel().getColumn(3).setPreferredWidth(10);
           

            //remove the "match" and "mismatchedFields" columns from the views (although not from the model; 
            // they need to remain in the model for access by the JTable CellRenderer)
            //Note that MISMATCHEDFIELDS (the rightmost column) is removed FIRST, *then* MATCH is removed.
            // If this is done in the opposite order, removing MATCH causes column numbers above it to shift down,
            // which results in an ArrayIndexOutOfBoundsException because of the attempt to remove column "5" (MISMATCHED.ordinal())
            // when there are now only columns 0-4 present.
            TableColumnModel tcm = getPC2ScoreboardTable().getColumnModel();
            tcm.removeColumn(tcm.getColumn(ScoreboardColumnId.MISMATCHEDFIELDS.ordinal()));
            tcm.removeColumn(tcm.getColumn(ScoreboardColumnId.MATCH.ordinal()));

            tcm = getRemoteScoreboardTable().getColumnModel();
            tcm.removeColumn(tcm.getColumn(ScoreboardColumnId.MISMATCHEDFIELDS.ordinal()));
            tcm.removeColumn(tcm.getColumn(ScoreboardColumnId.MATCH.ordinal()));


            //show "done"
            this.setCursor(Cursor.getDefaultCursor());

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
            
//          //debugging help:
//            scoreboardsPanel.setBorder(BorderFactory.createLineBorder(Color.green));
        }

        return scoreboardsPanel;
    }
    
    private JPanel getScoreboardPanelHeader() {

        if (scoreboardPanelHeader==null) {
            scoreboardPanelHeader = new JPanel();
            scoreboardPanelHeader.setPreferredSize(new Dimension(600, 25));
            scoreboardPanelHeader.setMaximumSize(new Dimension(700, 20));
            scoreboardPanelHeader.setMinimumSize(new Dimension(100, 20));
            JLabel pc2Header = new JLabel("PC2 Scoreboard");
            JLabel remoteHeader = new JLabel("Remote CCS Scoreboard");
            scoreboardPanelHeader.add(pc2Header);
            scoreboardPanelHeader.add(getRigidArea());
            scoreboardPanelHeader.add(remoteHeader);
            
//          //debugging help:
//            scoreboardPanelHeader.setBorder(BorderFactory.createLineBorder(Color.blue));
        }
        return scoreboardPanelHeader;
    }
        
    
    private JPanel getScoreboardScrollPanesPanel() {
        
        if (scoreboardScrollPanesPanel==null) {
            scoreboardScrollPanesPanel = new JPanel();
            scoreboardScrollPanesPanel.setLayout(new BoxLayout(scoreboardScrollPanesPanel, BoxLayout.LINE_AXIS));
            scoreboardScrollPanesPanel.add(getRigidArea_3());
            scoreboardScrollPanesPanel.add(getPC2ScoreboardScrollPane());
            scoreboardScrollPanesPanel.add(getRigidArea_1());
            scoreboardScrollPanesPanel.add(getRemoteCCSScrollPane());
            scoreboardScrollPanesPanel.add(getRigidArea_4());
            
//            //debugging help:
//            scoreboardScrollPanesPanel.setBorder(BorderFactory.createLineBorder(Color.red));
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
            remoteCCSScrollPane.setMaximumSize(new Dimension(32767, 32767));
            
            remoteCCSScrollPane.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.black, 3),
                    remoteCCSScrollPane.getBorder()));
            
            //set so that a single scrollbar scrolls both scoreboards together
            remoteCCSScrollPane.setVerticalScrollBar(((JScrollPane) getPC2ScoreboardScrollPane()).getVerticalScrollBar());
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
            pc2ScrollPane.setMaximumSize(new Dimension(32767, 32767));
            
            pc2ScrollPane.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.black, 3),
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
            
//          //debugging help:
//            statusPanel.setBorder(BorderFactory.createLineBorder(Color.yellow));
       }
        return statusPanel;
    }

    /**
     * Returns a JTable organized for containing Scoreboard representations, either for a PC2 scoreboard or 
     * for a Remote CSS scoreboard.
     * The returned JTable applies color formatting to individual row cells based on the status of the row comparisons
     * (that is, whether the row matched its corresponding row in the other scoreboard type and, if the row does not match,
     * based on the list of mismatched fields in the row).
     * 
     * Note: this method does not actually fill in any table data; it is expected that external code will
     * invoke {@link #getUpdatedResultsTableModel()} to create and load the current comparison results into the table.
     * 
     * @return a JTable organized for containing scoreboard comparisons.
     */
    private JTable getScoreboardTable() {

        JTable resultsTable = new JTable() {
            private static final long serialVersionUID = 1L;

//            String[] columnNames = { "Rank", "Team (Id)", "Solved", "Time", "Match", "Mismatched Fields" };
            
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

                    // Color the cell based on the "Mismatched Fields" cell value, which indicates whether this row matches its
                    //  corresponding row in the other scoreboard table
                    boolean matches = (boolean) getModel().getValueAt(modelRow, ScoreboardColumnId.MATCH.ordinal());
                    if (matches) {
                        c.setBackground(new Color(153, 255, 153)); // light green for all cells in this row
                    } else {
                        //the row doesn't match in (at least) one field; determine whether the particular cell currently being rendered matches.
                        //Start by getting the list of mismatched cells
                        @SuppressWarnings("unchecked")
                        ArrayList<Integer> mismatchedCellList = (ArrayList<Integer>) getModel().getValueAt(modelRow, ScoreboardColumnId.MISMATCHEDFIELDS.ordinal());
                        //see if the current cell (column) is in the mismatched list
                        boolean isMismatched = false;
                        for (Integer fieldNum : mismatchedCellList) {
                            if (fieldNum.equals(column)) {
                                isMismatched = true;
                                break;
                            }
                        }
                        if (isMismatched) {
                            c.setBackground(new Color(255, 153, 153)); //light red
                        } else {
                            c.setBackground(new Color(153, 255, 153)); // light green
                        }
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
                String team = getModel().getValueAt(modelRow, ScoreboardColumnId.TEAM.ordinal()).toString();
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
//        resultsTable.setDefaultRenderer(String.class, centerRenderer);  //let team names (strings) be left-justified
        resultsTable.setDefaultRenderer(Integer.class, centerRenderer);
        
        resultsTable.setRowSelectionAllowed(true);
        resultsTable.setColumnSelectionAllowed(false);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        
        return resultsTable;

    }
    
    public enum ScoreboardType {PC2, REMOTE} ;
    
    private enum ScoreboardColumnId {RANK, TEAM, NUM_SOLVED, TOTAL_TIME, MATCH, MISMATCHEDFIELDS} ;
    
    /**
     * Returns a {@link TableModel} containing data for the current comparisons between the PC2 shadow and the Remote CCS.
     * 
     * @param scoreboardType the scoreboard (PC2 or Remote) for which data should be extracted from the 
     *                  specified {@link ShadowScoreboardRowComparison} array and used to populate the returned TableModel.
     * @param comparedResults an array of ShadowScoreboardRowComparisons containing comparisons of corresponding rows of the
     *          pc2 and remote scoreboards.
     * 
     * @return a {@link TableModel} populated with table information for the specified scoreboard.
     */
    private TableModel getUpdatedResultsTableModel(ScoreboardType scoreboardType, ShadowScoreboardRowComparison[] comparedResults) {

        //define the columns for the table
        //TODO: use Enum name/values instead of hard-coded strings for columnNames
        String[] columnNames = { "Rank", "Team (Id)", "Solved", "Time", "Match", "Mismatched Fields" };
       
        //an array to hold the table data
        Object[][] data = new Object[comparedResults.length][ScoreboardColumnId.values().length];
        
        //fill in each data row with info for the specified scoreboard from the compared results
        for (int row=0; row<comparedResults.length; row++) {
            ShadowScoreboardRowComparison curSB = comparedResults[row];
            TeamScoreRow curRow ;
            //get the scoreboard row for the specified type of scoreboard (PC2 or Remote)
            if (scoreboardType==ScoreboardType.PC2) {
                curRow = curSB.getSb1Row();
            } else {
                curRow = curSB.getSb2Row();
            }
            if (curRow!=null) {
                //fill the columns of the current row with the data from the specified scoreboard row
                data[row][ScoreboardColumnId.RANK.ordinal()] = curRow.getRank();
                
                String teamName = curRow.getTeamName();
                int teamId = curRow.getTeam_id();
                data[row][ScoreboardColumnId.TEAM.ordinal()] = " " + teamName + "  (" + teamId + ")";
                
                data[row][ScoreboardColumnId.NUM_SOLVED.ordinal()] = curRow.getScore().getNum_solved();
                data[row][ScoreboardColumnId.TOTAL_TIME.ordinal()] = curRow.getScore().getTotal_time();
                data[row][ScoreboardColumnId.MATCH.ordinal()] = curSB.isMatch();
                data[row][ScoreboardColumnId.MISMATCHEDFIELDS.ordinal()] = curSB.getMismatchedFieldList();
            } else {
                //oops, we don't have this row in the specified scoreboard; fill the table data with placeholders
                data[row][ScoreboardColumnId.RANK.ordinal()] = "?";
                data[row][ScoreboardColumnId.TEAM.ordinal()] = "?";
                data[row][ScoreboardColumnId.NUM_SOLVED.ordinal()] = "?";
                data[row][ScoreboardColumnId.TOTAL_TIME.ordinal()] = "?";               
                data[row][ScoreboardColumnId.MATCH.ordinal()] = false;
                data[row][ScoreboardColumnId.MISMATCHEDFIELDS.ordinal()] = null;
            }
        }
        
        //construct a TableModel from the data, also providing an overridden getColumnClass() method
        TableModel tableModel = new DefaultTableModel(data, columnNames){
            static final long serialVersionUID = 1;
            
//          String[] columnNames = { "Rank", "Team (Id)", "Solved", "Time", "Match", "Mismatched Fields" };
            Class<?>[] types = { Integer.class, String.class, Integer.class, Integer.class, Boolean.class, ArrayList.class };
            
            //return the appropriate class for the column so that the correct cell renderer will be used
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
        buttonPanel.add(getRigidArea_2());
        buttonPanel.add(getHelpButton());

        return buttonPanel ;
    }
    
    private Component getRigidArea() {
        if (rigidArea == null) {
        	rigidArea = Box.createRigidArea(new Dimension(400, 20));
        }
        return rigidArea;
    }
    private Component getRigidArea_1() {
        if (rigidArea_1 == null) {
        	rigidArea_1 = Box.createRigidArea(new Dimension(30, 20));
        }
        return rigidArea_1;
    }
    private Component getRigidArea_2() {
        if (rigidArea_2 == null) {
        	rigidArea_2 = Box.createRigidArea(new Dimension(20, 20));
        }
        rigidArea_2.setPreferredSize(new Dimension(20, 40));
        
        return rigidArea_2;
    }
    private Component getRigidArea_3() {
        if (rigidArea_3 == null) {
            rigidArea_3 = Box.createRigidArea(new Dimension(20, 20));
        }
        return rigidArea_3;
    }
    private Component getRigidArea_4() {
        if (rigidArea_4 == null) {
            rigidArea_4 = Box.createRigidArea(new Dimension(20, 20));
        }
        return rigidArea_4;
    }
   
    private String infoMessage = "The Shadow Scoreboard Comparison screen displays the current PC2 Shadow and Remote Primary CCS scoreboards, side-by-side."
            
            + "\n\nThe comparison between scoreboards starts by considering each RANK (1, 2, 3...)."
            + "\nFor each rank, all scoreboard rows holding that rank are displayed in each scoreboard."
            + "\nIf there is a rank present in one scoreboard but not in the other scoreboard (for example, due to ties in earlier ranks),"
            + "\nan empty row containing question marks is displayed in the scoreboard which does not have an entry for that rank."
            
            + "\n\nFor each rank which is present in both scoreboards, if the data for that rank matches in all corresponding fields of the two scoreboards"
            + "\nthen the row is displayed in green; if any data values do not match then the row is displayed in red."
            
            + "\n\nThe effect of the above is that early in a contest when the PC2 Shadow system is still catching up to the Remote CCS, there will typically"
            + "\nbe many rows where one scoreboard has data that the other does not (because of ties within ranks due to the submissions which that system"
            + "\nhas/has not judged), and there will be many rows where there is a single rank in both scoreboards but the data values will not match"
            + "\n(the rows will be red) because the PC2 Shadow system has not judged precisely the same number of submissions as the remote CCS."
            
            + "\n\nAt the end of the contest the Scoreboard Comparison screen will show the true comparison of scoreboards for the PC2 Shadow and the Remote CCS."
            + "\n(If the scoreboards do not match at the end of the contest, the most likely reason is because the PC2 Shadow system has given a different"
            + "\njudgement than the Remote CCS to one or more submissions; see the PC2 Shadow \"Compare Runs\" grid to find and resolve such differences.)"
            ;
        
    private JButton getHelpButton() {
        if (helpButton == null) {
        	helpButton = new JButton("Help?");
        	helpButton.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent arg0) {
        	        JOptionPane.showMessageDialog(null, infoMessage, "Scoreboard Information", JOptionPane.INFORMATION_MESSAGE);
        	    }
        	});
        	helpButton.setToolTipText("Displays explanatory information about the content of the scoreboard comparison display");
        }
        return helpButton;
    }

}
