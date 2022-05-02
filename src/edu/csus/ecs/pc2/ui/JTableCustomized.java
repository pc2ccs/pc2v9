// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.ibm.webrunner.j2mclb.MultiColumnListbox;
import com.ibm.webrunner.j2mclb.util.HeapSorter;

import edu.csus.ecs.pc2.core.model.ElementId;

/**
 * Wrapper for JTable to allow customizations that can be used
 * by things like JPanePlugin clases.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class JTableCustomized extends JTable {

    /**
     * 
     */
    private static final long serialVersionUID = -354276413985309860L;

    public static final String SVN_ID = "$Id$";

    public JTableCustomized() {
        super();
    }

    public JTableCustomized(DefaultTableModel tModel) {
        super(tModel);
    }

    /**
     * Looks up the unique ID for the item at the supplied table row.
     * Have to map the row to the underlying tablemodel data first.
     * The ElementID is stored in the last (invisible) column, in most cases.
     * 
     * @param nRow - selected row
     */
    public ElementId getElementIdFromTableRow(int nRow) {
        int modelIndex = convertRowIndexToModel(nRow);
        TableModel tm = getModel();
        ElementId elementId = (ElementId) tm.getValueAt(modelIndex,  tm.getColumnCount()-1);
        return(elementId);
    }
    
}
