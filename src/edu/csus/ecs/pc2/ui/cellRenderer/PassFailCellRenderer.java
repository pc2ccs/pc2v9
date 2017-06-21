package edu.csus.ecs.pc2.ui.cellRenderer;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * A {@link TableCellRenderer} that renders cells with a 
 * green background containing the word "Pass" or 
 * with a red background containing the word "Fail".  
 * 
 * The {@link #setValue(Object)} method (which is what clients use to put a value in
 * the cell being rendered) check the type of the Object it receives, and renders the cell
 * as follows:
 * <P>
 * If the Object is a Boolean, the cell is rendered Pass/Fail (green/red) based on the value of the Boolean (true==pass).
 * <p>
 * If the received Object is a String containing either "pass" or "fail" then the cell is rendered based on the text of the string:
 * "pass" renders as a green/pass while "fail" renders as a red/fail.  The check for the value of the String is not case-sensitive.
 * <P>
 * If the received Object is a JLabel, the String text in the JLabel is fetched and then the cell is rendered in the same way
 * as for Strings, above. 
 *
 * If the value passed to the {@link #setValue(Object)} method is anything not covered of the above-listed steps then 
 * the cell is rendered with a yellow background containing question marks.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
public class PassFailCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    public void setValue(Object value) {
        
        // default values
        setBackground(Color.yellow);
        setText("<No Validator>");

        if (value instanceof Boolean) {
            boolean passed = (Boolean) value;
            if (passed) {
                setPass();
            } else {
                setFail();
            }
            
        } else if (value instanceof String) {
            String text = (String)value;
            if (text.equalsIgnoreCase("Pass")) {
                setPass();
            } else if (text.equalsIgnoreCase("Fail")) {
                setFail();
            }
            
        } else if (value instanceof JLabel) {
            String text = ((JLabel)value).getText();
            if (text.equalsIgnoreCase("Pass")) {
                setPass();
            } else if (text.equalsIgnoreCase("Fail")) {
                setFail();
            }
        }
        
        setHorizontalAlignment(SwingConstants.CENTER);
        setBorder(new EmptyBorder(0, 0, 0, 0));

    }
    
    private void setPass() {
        setBackground(Color.green);
        setForeground(Color.black);
        setText("Pass");
    }
    
    private void setFail() {
        setBackground(Color.red);
        setForeground(Color.white);
        setText("Fail");  
    }

}

