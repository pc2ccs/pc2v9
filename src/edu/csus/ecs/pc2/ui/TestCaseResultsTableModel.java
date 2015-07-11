package edu.csus.ecs.pc2.ui;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * This class defines a table model for tables holding Test Case Results.
 * 
 * @author john
 *
 */
public class TestCaseResultsTableModel extends DefaultTableModel {
    private static final long serialVersionUID = 1L;
    
    
    /**
     * Constructs a Table Model for Test Case Results. The data and columnName values
     * are stored in Vectors in the parent class, initialized by a call to
     * super(data,columnnames) in this constructor.
     * @param data - the data to go in the model
     * @param columnNames - Strings defining the table column headers
     */
    public TestCaseResultsTableModel(Object[][] data, Object[] columnNames) {
        super(data,columnNames);
    }
    
    /**
     * Returns the Class of objects contained in the specified table column.
     * The Class is determined by the type of object in the first row of
     * the specified column.
     */
    @Override
    public Class getColumnClass(int col) {
        //the data for the model is stored in the parent class vector "dataVector"
        return ((Vector)(dataVector.elementAt(0))).elementAt(col).getClass();
    }

    @Override
    public int getRowCount() {
        return dataVector.size();
    }

    @Override
    public int getColumnCount() {
        return columnIdentifiers.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return ((Vector)(dataVector.elementAt(rowIndex))).elementAt(columnIndex);
    }

    /**
     * Returns whether the specified cell is editable.
     * The only editable cells in a Test Case model are those in the "row selected" 
     * column; all other cells in all other columns are not editable.
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        boolean retVal = ( columnIndex == MultiTestSetOutputViewerPane.COLUMN.SELECT_CHKBOX.ordinal() );
        return retVal;
    }

}
