package edu.csus.ecs.pc2.ui.cellRenderer;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * A {@link TableCellRenderer} that renders cells showing the result of executing a submission against a single "test case".
 * Test cases that passed (correctly solved the problem) are rendering with a green background containing the word "Pass";
 * test cases that failed to solve the problem (or during whose execution there was an issue of some kind) are rendered 
 * with a red background containing the word "Fail".  
 * Test cases for which there was no validator assigned to the problem are rendered with a yellow background containing the
 * label "No Validator"; test cases which were never executed (e.g. due to "stop on first failure" being set) are rendered
 * with a yellow background containing the label "Not Executed".
 * 
 * The {@link #setValue(Object)} method (which is what clients use to put a value in
 * the cell being rendered) checks the type of the Object it receives, and renders the cell
 * as follows:
 * <P>
 * If the Object is a Boolean, the cell is rendered Pass/Fail (green/red) based on the value of the Boolean (true==pass, false==fail).
 * <p>
 * If the received Object is a String containing either "pass", "fail", "no validator", or "not executed" then the cell is 
 * rendered based on the text of the string as described above. The check for the value of the String is not case-sensitive.
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
public class TestCaseResultCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 2L;

    public void setValue(Object value) {
        
        // default values
        setBackground(Color.yellow);
        setText("???");
        setForeground(Color.black);
        setFont(new Font(getFont().getName(),Font.ITALIC, 12));

        if (value instanceof Boolean) {
            boolean passed = (Boolean) value;
            if (passed) {
                setPass();
            } else {
                setFail();
            }
            
        } else if (value instanceof String) {
            String text = (String)value;
            updateCellText(text);
            
        } else if (value instanceof JLabel) {
            String text = ((JLabel)value).getText();
            updateCellText(text);
        }

        setHorizontalAlignment(SwingConstants.CENTER);
        setBorder(new EmptyBorder(0, 0, 0, 0));

    }
    
    private void updateCellText (String text) {
        if (text.equalsIgnoreCase("pass")) {
            setPass();
        } else if (text.equalsIgnoreCase("fail")) {
            setFail();
        } else if (text.equalsIgnoreCase("no validator")) {
            setNoValidator();
        } else if (text.equalsIgnoreCase("not executed")) {
            setNotExecuted();
        }
        
    }
    
    private void setPass() {
        setBackground(Color.green);
        setForeground(Color.black);
        setFont(new Font(getFont().getName(),Font.PLAIN, 12));
        setText("Pass");
    }
    
    private void setFail() {
        setBackground(Color.red);
        setForeground(Color.white);
        setFont(new Font(getFont().getName(),Font.ITALIC+Font.BOLD, 12));
        setText("Fail");  
    }

    private void setNoValidator() {
        setBackground(Color.yellow);
        setForeground(Color.black);
        setFont(new Font(getFont().getName(),Font.PLAIN, 12));
        setText("<No Validator>");  
    }
    
    private void setNotExecuted() {
        setBackground(Color.yellow);
        setForeground(Color.black);
        setFont(new Font(getFont().getName(),Font.ITALIC, 12));
        setText("(Not Executed)");  
    }

}

