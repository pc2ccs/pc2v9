package edu.csus.ecs.pc2.ui.cellRenderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import edu.csus.ecs.pc2.ui.MCLB;

/**
 * This class provides support for rendering String values centered in {@link MCLB} cells.
 * The {@link MCLB} class does not directly support CellRenderers (unlike JTables, where a {@link TableCellRenderer} 
 * can be assigned to an entire column).  
 * <P>
 * To use this class, construct an instance specifying a String value, and then assign that
 * object directly to the cell (specific row and column) of the {@link MCLB}.
 * <p>
 * Note that this class extends {@link DefaultTableCellRenderer}, which in turn extends {@link JLabel}.  It is this
 * property which makes it possible to use this to render {@link MCLB} cells -- essentially, the class provides
 * for constructing a {@link JLabel} rendered with its String text centered.
 *    
 * @author John
 *
 */
public class MCLBCenteredStringCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    public MCLBCenteredStringCellRenderer(String value) {
        super();
        
        System.err.println ("In MCLBCenteredStringCellRenderer(); value = " + value);

        setText(value);
        setOpaque(true);

        setHorizontalAlignment(SwingConstants.CENTER);
        setBorder(new EmptyBorder(0, 0, 0, 0));
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
//        if (table.isRowSelected(row)) {
//            return super.getTableCellRendererComponent(table, value, true, hasFocus, row, column);
//        } else {
//            return super.getTableCellRendererComponent(table, value, false, hasFocus, row, column);
//        }
        
        Component renderer = super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
        
        if (isSelected)
        {
            renderer.setBackground(table.getSelectionBackground());
            renderer.setForeground(table.getSelectionForeground());
            
            System.err.println ("Value=" + value + "; isSelected=" + isSelected + "; hasFocus=" + hasFocus + "; row=" + row + "; col=" + column);
            
        }
        else
        {
            renderer.setBackground(table.getBackground());
            renderer.setForeground(table.getForeground());

            System.err.println ("Value=" + value + "; isSelected=" + isSelected + "; hasFocus=" + hasFocus + "; row=" + row + "; col=" + column);

        }
        
        return renderer;

    }

}
