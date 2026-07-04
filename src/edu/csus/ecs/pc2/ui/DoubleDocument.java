// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Accept Double input only.
 *
 * @see javax.swing.JTextField#setDocument(Document)
 * @author John Buck
 */
// $HeadURL$
public class DoubleDocument extends PlainDocument {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
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
                    Double.parseDouble(newValue);
                }
                super.insertString(offset, string, attributes);
            } catch (NumberFormatException exception) {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }
}
