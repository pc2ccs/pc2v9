package edu.csus.ecs.pc2.ui;

import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.MultiColumnListbox;
import com.ibm.webrunner.j2mclb.util.HeapSorter;

/**
 * A multi-column list box.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// TODO move this to a better package
// $HeadURL$
public class MCLB extends MultiColumnListbox {

    /**
     * 
     */
    private static final long serialVersionUID = -2265655808758386762L;

    public static final String SVN_ID = "$Id$";

    
    // TODO what is this used for ?  dal
    @SuppressWarnings("unused")
    private void bulkUpdateMclb(Object[] rowKeys, Object[][] rowObjects, MCLB mclb) {
        final Object[] fRowKeys = rowKeys;
        final Object[][] fRowObjects = rowObjects;
        final MCLB fMclb = mclb;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int rowIndex = fMclb.getSelectedIndex();
                Object selectedObject = null;
                if (rowIndex != -1) {
                    selectedObject = fMclb.getRowKey(rowIndex);
                }

                fMclb.removeAllRows();
                fMclb.addRows(fRowObjects, fRowKeys);
                autoSizeAllColumns(fMclb);

                if (selectedObject != null) {
                    fMclb.selectRowByKey(selectedObject);
                }

            }
        });
    }

    private void autoSizeAllColumns(MCLB box) {
        final MCLB theBox = box;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for (int i = 0; i < theBox.getColumnCount(); i++) {
                    theBox.autoSizeColumn(i);
                    if (theBox.getColumnInfo(i).getSorter() != null) {
                        theBox.getColumnInfo(i).setWidth(theBox.getColumnInfo(i).getWidth() + 20);
                    }
                }
                theBox.sort();
            }
        });
    }

    public void autoSizeAllColumns() {
        autoSizeAllColumns(this);
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

    public void setColumnSorter(int columnNumber, HeapSorter sorter, int sortRank) {
        getColumnInfo(columnNumber).setSorter(sorter);
        getColumnInfo(columnNumber).getSorter().setSortOrder(sortRank);
    }

}
