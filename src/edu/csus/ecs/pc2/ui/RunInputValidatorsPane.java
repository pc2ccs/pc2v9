package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.inputValidation.InputValidationResult;
import edu.csus.ecs.pc2.core.model.inputValidation.ProblemInputValidationResults;

/**
 * A pane for running the input validators for currently defined problems and displaying the results.
 * 
 * @author $Author$ John
 * @version $Id$
 */
public class RunInputValidatorsPane extends JPanePlugin  {

    private static final long serialVersionUID = 1;

    private JButton closeButton;

    private JButton runSelectedButton;
    
    private JButton runAllButton;

    private JPanel msgPanel;

    private JPanel resultsPanel;

    private JScrollPane resultsScrollPane;

    private JTable resultsTable;

    private TableModel allProblemsInputValidationResultsTableModel = new AllProblemsInputValidationResultsTableModel();

    private JPanel buttonPanel;

    private boolean listenersAdded;


    public RunInputValidatorsPane() {
        
        initialize();
    }


    private void initialize() {

        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(775, 536));

        this.add(getMessagePanel(), java.awt.BorderLayout.NORTH);
        this.add(getResultsPanel(), java.awt.BorderLayout.CENTER);
        this.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);

    }
    
    
    
    private JPanel getMessagePanel() {
        if (msgPanel == null) {
            msgPanel = new JPanel();
            msgPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

            JLabel msgPanelLabel = new JLabel("\"\"");
            msgPanelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            msgPanel.add(msgPanelLabel);
        }
        return msgPanel;
    }
    
    private JPanel getResultsPanel() {
        if (resultsPanel == null) {
            resultsPanel = new JPanel();
            resultsPanel.setPreferredSize(new Dimension(700, 700));
            resultsPanel.setMinimumSize(new Dimension(700, 700));
            resultsPanel.setBorder(new TitledBorder(null, "Run Results", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            resultsPanel.add(getInputValidatorResultsScrollPane());
        }
        return resultsPanel;
    }
    
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.add(getRunSelectedButton());
            buttonPanel.add(getRunAllButton());
            buttonPanel.add(getCloseButton());
        }
        return buttonPanel;

    }
    
    private JScrollPane getInputValidatorResultsScrollPane() {
        if (resultsScrollPane == null) {
            resultsScrollPane = new JScrollPane();
            resultsScrollPane.setMinimumSize(new Dimension(450, 450));
            resultsScrollPane.setPreferredSize(new Dimension(700, 450));
            resultsScrollPane.setViewportView(getInputValidatorResultsTable());
        }
        return resultsScrollPane;
    }

    //copied from EditProblemPane; needs to be updated for this class
    public JTable getInputValidatorResultsTable() {
        if (resultsTable == null) {
            resultsTable = new JTable(allProblemsInputValidationResultsTableModel);
            resultsTable.setMinimumSize(new Dimension(450, 0));
            
            //set the desired options on the table
            resultsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            resultsTable.setFillsViewportHeight(true);
            resultsTable.setRowSelectionAllowed(false);
            resultsTable.getTableHeader().setReorderingAllowed(false);
            
            //the following statement is necessary (it's one way to force the Table to use the Table Model's rowcount, which it doesn't in all cases)
            //Another way might be to add a custom row sorter, but make sure the row sorter's model is updated every time the Table Model is updated...
            //@see http://stackoverflow.com/questions/23626951/jtable-row-count-vs-model-row-count
            resultsTable.setAutoCreateRowSorter(true);

            //code from MultipleDataSetPane:
            // insert a renderer that will center cell contents
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

            for (int i = 0; i < resultsTable.getColumnCount(); i++) {
                resultsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
            resultsTable.setDefaultRenderer(String.class, centerRenderer);
//
//            // also center column headers (which use a different CellRenderer)
            //(this code came from MultipleDataSetPane, but the JTable here already has centered headers...
//            ((DefaultTableCellRenderer) testDataSetsListBox.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

            // change the header font
            JTableHeader header = resultsTable.getTableHeader();
            header.setFont(new Font("Dialog", Font.BOLD, 12));
            
            // render Result column as Pass/Fail on Green/Red background
            resultsTable.getColumn("Overall Result").setCellRenderer(new PassFailCellRenderer());


        }
        return resultsTable;
    }

    private JButton getRunSelectedButton() {
        if (runSelectedButton == null) {
            runSelectedButton = new JButton("Run Selected");
            runSelectedButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(null, "This function isn't implemented yet...", "Not Implemented", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
        return runSelectedButton;
    }
    
    private JButton getRunAllButton() {
        if (runAllButton == null) {
            runAllButton = new JButton("Run All");
            runAllButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(null, "This function isn't implemented yet...", "Not Implemented", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
        return runAllButton;
    }
    
    private JButton getCloseButton() {
        if (closeButton == null) {
            closeButton = new JButton("Close");
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleCloseButton();
                }
            });
        }
        return closeButton;
    }
    
    private void handleCloseButton() {
        this.getParentFrame().setVisible(false);
    }
    

    /**
     * Populates the GUI fields with Problem InputValidationResults data from the underlying table model.
     */
    protected void populateGUI() {
        
        //get the currently-defined problems
        Problem [] probs = getContest().getProblems();
        
        //get the Input Validation Results for each problem (note that this could be empty for any given problem, or all problems)
        Vector<ProblemInputValidationResults> tableData = getInputValidationResultsTableData(probs);
               
        //put the table data into the table model
        ((AllProblemsInputValidationResultsTableModel)getInputValidatorResultsTable().getModel()).setResults(tableData);
        
        //fire table data changed to update the display
        ((AllProblemsInputValidationResultsTableModel)getInputValidatorResultsTable().getModel()).fireTableDataChanged();
        
    }

    
    private Vector<ProblemInputValidationResults> getInputValidationResultsTableData(Problem [] probs) {
        
        Vector<ProblemInputValidationResults> tableData = new Vector<ProblemInputValidationResults> ();
        
        for (int prob=0; prob<probs.length; prob++) {
            if (probs[prob].isProblemHasInputValidator()) {
                
                ProblemInputValidationResults result = new ProblemInputValidationResults(probs[prob]);
                
                // add each result for the current problem to the current row
                for (InputValidationResult res : probs[prob].getInputValidationResults()) {

                    result.addResult(res); // do we need to clone the InputValidationResult?
                }
                
                tableData.add(result);
            }
        }
        
        return tableData;
    }
    
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        addWindowListeners();
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI();
            }
        });

    }
  

    @Override
    public String getPluginTitle() {
        return "Run Input Validators Pane";
    }

    private void addWindowListeners() {

        if (listenersAdded) {
            // No need to add the listeners twice or more.
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (getParentFrame() != null) {
                    getParentFrame().addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            handleCloseButton();
                        }
                    });
                    listenersAdded = true;
                }
            }
        });
    }
    public class PassFailCellRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        public void setValue(Object value) {
            if (value instanceof Boolean) {
                boolean passed = (Boolean) value;
                if (passed) {
                    setBackground(Color.green);
                    setForeground(Color.black);
                    setText("Pass");
                } else {
                    setBackground(Color.red);
                    setForeground(Color.white);
                    setText("Fail");
                }
            } else {
                // illegal value
                setBackground(Color.yellow);
                setText("??");
                getController().getLog().log(Log.SEVERE, "EditProblem.PassFailCellRenderer: unknown pass/fail result: ", value);
            }
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(new EmptyBorder(0, 0, 0, 0));

        }

    }

}
