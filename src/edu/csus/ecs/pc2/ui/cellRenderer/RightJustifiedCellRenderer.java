package edu.csus.ecs.pc2.ui.cellRenderer;

import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * A {@link TableCellRenderer} for displaying right-justified values but with some margin space.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
public class RightJustifiedCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    public void setValue(Object value) {
        setHorizontalAlignment(SwingConstants.RIGHT);
        setBorder(new EmptyBorder(0, 0, 0, 30));
        setText((String) value);
    }

}
