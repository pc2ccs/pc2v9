package edu.csus.ecs.pc2.ui.cellRenderer;

import java.awt.Color;

import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import edu.csus.ecs.pc2.core.model.Problem.InputValidationStatus;

/**
 * A {@link TableCellRenderer} that renders cells with a visual indication of an {@link InputValidationStatus} value.
 * 
 *  Input Validation Status "PASSED" is rendered in a green background; status "FAILED" is rendered with a red background; 
 *  status "NOT_TESTED" is rendered with a yellow background, and status "ERROR" is rendered with a magenta background.
 * <P>
 * The {@link #setValue(Object)} method (which is what clients use to put a value in
 * the cell being rendered) check the type of the Object it receives, and renders the cell
 * as follows:
 * <P>
 * If the Object is an InputValidationStatus, the cell is rendered as described above.
 * Otherwise, the cell is rendered with a yellow background containing question marks.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
public class InputValidationStatusCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    public void setValue(Object value) {

        if (value instanceof InputValidationStatus) {
            
            InputValidationStatus status = (InputValidationStatus) value;
            
            switch (status) {
                case PASSED:
                    setBackground(new Color(0x00, 0xC0, 0x00)); //green, with some shading
                    setForeground(Color.black);
                    setText(status.toString());
                    break;
                    
                case FAILED:
                    setBackground(Color.red);
                    setForeground(Color.white);
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
                    //this should never happen
                    setBackground(Color.ORANGE);
                    setForeground(Color.black);
                    setText("????");                      
                    
                    System.err.println ("This message should never appear (undefined InputValidationStatus type in Cell Renderer); "
                            + "please notify the PC2 Development team (pc2@ecs.csus.edu)");
            }
            
        } else {
            //we received an Object that wasn't an InputValidationStatus
            setBackground(Color.yellow);
            setForeground(Color.black);
            setText("????");   
            
            System.err.println ("Warning: InputValidationStatusCellRenderer asked to render unsupported type '" + value.getClass() + "'");
        }
        
        setHorizontalAlignment(SwingConstants.CENTER);
        setBorder(new EmptyBorder(0, 0, 0, 0));

    }
}

