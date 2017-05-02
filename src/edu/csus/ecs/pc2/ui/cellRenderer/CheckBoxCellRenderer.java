package edu.csus.ecs.pc2.ui.cellRenderer;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

/**
 * A {@link TableCellRenderer} that renders boolean values as {@link JCheckBox}es.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
public class CheckBoxCellRenderer extends JCheckBox implements TableCellRenderer {

    private static final long serialVersionUID = 1L;

    public CheckBoxCellRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setSelected((Boolean)value);
        return this;
    }
}

