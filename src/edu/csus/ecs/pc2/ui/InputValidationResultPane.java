package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import edu.csus.ecs.pc2.core.model.inputValidation.InputValidationResultsTableModel;
import edu.csus.ecs.pc2.ui.cellRenderer.PassFailCellRenderer;
import java.awt.FlowLayout;

/**
 * This class defines a JPanel for displaying the results of running an Input Validator on a set of Input Data files
 * (Judge's input data).
 * 
 * @author John
 *
 */
public class InputValidationResultPane extends JPanePlugin {

    private static final long serialVersionUID = 1L;
    
    private JPanel inputValidationResultSummaryPanel;

    private JLabel inputValidationResultsSummaryLabel;

    private JLabel inputValidationResultSummaryTextLabel;

    private Component verticalStrut_1;

    private Component verticalStrut_2;

    private JPanel inputValidationResultDetailsPanel;

    private JScrollPane resultsScrollPane;

    private JTable resultsTable;
    
    private InputValidationResultsTableModel inputValidationResultsTableModel = new InputValidationResultsTableModel();

    private JPanePlugin parentPane;
    
    
    public InputValidationResultPane() {

        this.setBorder(new TitledBorder(null, "Input Validation Results", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(getVerticalStrut_1());
        this.add(getInputValidationResultSummaryPanel());
        this.add(getVerticalStrut_2());
        this.add(getInputValidationResultDetailsPanel());

    }

    
    private JPanel getInputValidationResultSummaryPanel() {
        if (inputValidationResultSummaryPanel == null) {
            inputValidationResultSummaryPanel = new JPanel();
            inputValidationResultSummaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            FlowLayout flowLayout = (FlowLayout) inputValidationResultSummaryPanel.getLayout();
            flowLayout.setHgap(10);
            flowLayout.setAlignment(FlowLayout.LEFT);
            inputValidationResultSummaryPanel.add(getInputValidationResultsSummaryLabel());
            inputValidationResultSummaryPanel.add(getInputValidationResultSummaryTextLabel());
        }
        return inputValidationResultSummaryPanel;
    }
    
    private JLabel getInputValidationResultsSummaryLabel() {
        if (inputValidationResultsSummaryLabel == null) {
            inputValidationResultsSummaryLabel = new JLabel("Most Recent Status: ");
        }
        return inputValidationResultsSummaryLabel;
    }
    
    public JLabel getInputValidationResultSummaryTextLabel() {
        if (inputValidationResultSummaryTextLabel == null) {
            inputValidationResultSummaryTextLabel = new JLabel("<No Input Validation test run yet>");
            inputValidationResultSummaryTextLabel.setForeground(Color.black);
        }
        return inputValidationResultSummaryTextLabel;
    }

    private JPanel getInputValidationResultDetailsPanel() {
        if (inputValidationResultDetailsPanel == null) {
            inputValidationResultDetailsPanel = new JPanel();
            inputValidationResultDetailsPanel.setLayout(new BorderLayout(0, 0));
            inputValidationResultDetailsPanel.add(getInputValidatorResultsScrollPane(), BorderLayout.CENTER);           
        }
        return inputValidationResultDetailsPanel;
    }
    
    private JScrollPane getInputValidatorResultsScrollPane() {
        if (resultsScrollPane == null) {
            resultsScrollPane = new JScrollPane();
            resultsScrollPane.setViewportView(getInputValidatorResultsTable());
        }
        return resultsScrollPane;
    }
    
    public JTable getInputValidatorResultsTable() {
        if (resultsTable == null) {
            resultsTable = new JTable(inputValidationResultsTableModel);
            
            //set the desired options on the table
            resultsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            resultsTable.setFillsViewportHeight(true);
            resultsTable.setRowSelectionAllowed(false);
            resultsTable.getTableHeader().setReorderingAllowed(false);

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
            resultsTable.getColumn("Result").setCellRenderer(new PassFailCellRenderer());


        }
        return resultsTable;
    }

    private Component getVerticalStrut_1() {
        if (verticalStrut_1 == null) {
            verticalStrut_1 = Box.createVerticalStrut(20);
        }
        return verticalStrut_1;
    }

    private Component getVerticalStrut_2() {
        if (verticalStrut_2 == null) {
            verticalStrut_2 = Box.createVerticalStrut(20);
        }
        return verticalStrut_2;
    }

    @Override
    public String getPluginTitle() {
        return "Input Validation Result Pane";
    }

    public void setParentPane(JPanePlugin parentPane) {
       this.parentPane = parentPane;
        
    }
    
    public JPanePlugin getParentPane() {
        return this.parentPane;
    }

}
