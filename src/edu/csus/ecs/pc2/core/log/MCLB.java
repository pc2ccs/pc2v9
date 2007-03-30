package edu.csus.ecs.pc2.core.log;

import javax.swing.JPanel;

/**
 * A stub for now, An alternate for a multi-column list box.
 * 
 * @author pc2@ecs.csus.edu
 */
// TODO move this to a better package
// TODO implement this.
// $HeadURL$
// public class MCLB extends MultiColumnListbox {
public class MCLB extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -9110469835571727826L;

    /**
     * 
     */
    public static final String SVN_ID = "$Id$";

    // private void bulkUpdateMclb(Object[] rowKeys, Object[][] rowObjects, MCLB mclb) {
    // }

    private void autoSizeAllColumns(MCLB box) {
    }

    public void autoSizeAllColumns() {
        autoSizeAllColumns(this);
    }

    public void insertRow(Object[] newRow, int i) {
        // TODO Auto-generated method stub

    }

    public int getRowCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void removeRows(int numLines, int rowCount) {
        // TODO Auto-generated method stub

    }

    public void addColumns(Object[] cols) {
        // TODO Auto-generated method stub

    }

    /**
     * Set sorter for column in listbox.
     * 
     * @param columnNumber
     *            the column to apply the sort to.
     * @param sorter
     *            the sorter
     * @param sortRank
     *            which column will be sorted first, second, etc.
     */

    // TODO implement this.
    // public void setColumnSorter(int columnNumber, HeapSorter sorter, int sortRank) {
    // }
}
