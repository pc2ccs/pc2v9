package edu.csus.ecs.pc2.ui.cellRenderer;

import java.awt.Color;
import java.awt.Font;

import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import edu.csus.ecs.pc2.core.model.Problem.InputValidationStatus;

/**
 * This class provides support for rendering {@link InputValidationStatus} values in {@link MCLB} cells.
 * The {@link MCLB} class does not directly support CellRenderers (unlike JTables, where a {@link TableCellRenderer} 
 * can be assigned to an entire column).  
 * <P>
 * To use this class, construct an instance specifying an {@link InputValidationStatus} value, and then assign that
 * object directly to the cell (specific row and column) of the {@link MCLB}.
 * <p>
 * Note that this class extends {@link DefaultTableCellRenderer}, which in turn extends {@link JLabel}.  It is this
 * property which makes it possible to use this to render {@link MCLB} cells -- essentially, the class provides
 * for constructing a {@link JLabel} rendered according to the rules for rendering {@link InputValidationStatus} values.
 *    
 * @author John
 *
 */
public class MCLBInputValidationStatusCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    public MCLBInputValidationStatusCellRenderer(InputValidationStatus status) {
        super();

        switch (status) {
            case PASSED:
                setBackground(new Color(0x00, 0xC0, 0x00)); // green, with some shading
                setForeground(Color.black);
                setText(status.toString());
                break;

            case FAILED:
                setBackground(Color.red);
                setForeground(Color.white);
                setFont(new Font(Font.DIALOG, Font.BOLD | Font.ITALIC, getFont().getSize()+1));
                setText(status.toString());
                break;

            case NOT_TESTED:
                setBackground(Color.yellow);
                setForeground(Color.black);
                setText(status.toString());
                break;

            case ERROR:
                setBackground(Color.magenta);
                setForeground(Color.black);
                setText(status.toString());
                break;

            default:
                // this should never happen
                setBackground(Color.ORANGE);
                setForeground(Color.black);
                setText("????");

                System.err.println("This message should never appear (undefined InputValidationStatus type in Cell Renderer); "
                        + "please notify the PC2 Development team (pc2@ecs.csus.edu)");
        }

        setHorizontalAlignment(SwingConstants.CENTER);
        setBorder(new EmptyBorder(0, 0, 0, 0));
    }

}
