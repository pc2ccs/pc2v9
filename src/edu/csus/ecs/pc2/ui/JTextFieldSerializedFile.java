package edu.csus.ecs.pc2.ui;

import javax.swing.JTextField;

import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class JTextFieldSerializedFile extends JTextField {

    /**
     * 
     */
    private static final long serialVersionUID = -6110794226133531770L;
    
    private SerializedFile serializedFile = null;
    
    private String filename = "&UNLIKEY&TO&MATCH";
    
    public JTextFieldSerializedFile() {
        super();
    }

    public JTextFieldSerializedFile(SerializedFile file) {
        super();
        serializedFile = file;
        filename = file.getName();
        setText(filename);
    }
    
    public SerializedFile getOriginalSerializedFile() {
        return serializedFile;
    }
    
    public boolean isChanged (){
        return filename.equals(getText());
    }

}
