package edu.csus.ecs.pc2.ui;

import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import java.awt.Toolkit;

/**
 * Accept Integer input only.
 * 
 * @see javax.swing.JTextField#setDocument(Document)
 * @author pc2@ecs.csus.edu
 */
// $HeadURL$
public class IntegerDocument extends PlainDocument {
    public static final String SVN_ID = "$Id$";

    /**
     * 
     */
    private static final long serialVersionUID = -3661272696851557961L;

    public void insertString(int offset, String string, AttributeSet attributes) throws BadLocationException {

        if (string != null) {
            String newValue;
            int length = getLength();
            if (length == 0) {
                newValue = string;
            } else {
                String currentContent = getText(0, length);
                StringBuffer currentBuffer = new StringBuffer(currentContent);
                currentBuffer.insert(offset, string);
                newValue = currentBuffer.toString();
            }
            try {
                if (!newValue.equals("")) {
                    Integer.parseInt(newValue);
                }
                super.insertString(offset, string, attributes);
            } catch (NumberFormatException exception) {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }
}
