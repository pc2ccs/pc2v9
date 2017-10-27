package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * JPane to edit a SerializedFile.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class JSerializFilePicker extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 2669306324313513106L;

    private SerializedFile serializedFile = null;  //  @jve:decl-index=0:

    private SerializedFile newSerializedFile = null;  //  @jve:decl-index=0:

    private JButton pickFileButton = null;

    private String lastDirectory = null;

    private JLabel fileNameLabel = null;

    /**
     * This method initializes
     * 
     */
    public JSerializFilePicker() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        fileNameLabel = new JLabel();
        fileNameLabel.setText("");
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setHgap(5);
        borderLayout.setVgap(2);
        this.setLayout(borderLayout);
        this.setSize(new Dimension(420, 35));
        this.setPreferredSize(new Dimension(420, 35));
        this.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
        this.add(getPickFileButton(), BorderLayout.EAST);
        this.add(fileNameLabel, BorderLayout.CENTER);
    }

    /**
     * This method initializes pickFileButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getPickFileButton() {
        if (pickFileButton == null) {
            pickFileButton = new JButton();
            pickFileButton.setName("Select");
            pickFileButton.setText("Select");
            pickFileButton.setPreferredSize(new Dimension(90, 90));
            pickFileButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    selectAndLoadAFile();
                }
            });
        }
        return pickFileButton;
    }

    protected void selectAndLoadAFile() {

        SerializedFile theFile = newSerializedFile;

        if (theFile == null) {
            theFile = serializedFile;
        }
        
        System.out.println("debug selectAndLoadAFile "+isModified()+" " +serializedFile+" "+newSerializedFile);

        SerializedFile file = selectFile(theFile);
        if (file != null) {
            newSerializedFile = file;
            updateFileLabel(newSerializedFile);
        }

    }

    private void updateFileLabel(SerializedFile sFile) {
        fileNameLabel.setText(sFile.getName());
        fileNameLabel.setToolTipText(sFile.getAbsolutePath());
        
        System.out.println("debug updateFileLabel "+isModified()+" " +serializedFile+" "+newSerializedFile);

    }

    /**
     * select file, if file picked updates label.
     * 
     * @param label
     * @return True is a file was select and label updated
     * @throws Exception
     */
    private SerializedFile selectFile(SerializedFile file) {
        String startDir;
        if (file == null) {
            startDir = lastDirectory;
        } else {
            startDir = getParentDirectory(file.getAbsolutePath());
        }
        JFileChooser chooser = new JFileChooser(startDir);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            lastDirectory = chooser.getCurrentDirectory().toString();
            return new SerializedFile(chooser.getSelectedFile().getAbsolutePath());
        }
        chooser = null;
        return null;
    }

    private String getParentDirectory(String path) {

        if (!"".equals(path)) {
            File file = new File(path);
            return file.getParent();
        }

        return "";

    }

    public boolean isModified() {

        if (serializedFile == null) {
            return newSerializedFile != null;
        } else if (newSerializedFile == null) {
            return false;
        } else {
            return !serializedFile.equals(newSerializedFile);
        }
    }

    public SerializedFile getSerializedFile() {
        
        System.out.println("debug getSerializedFile "+isModified()+" " +serializedFile+" "+newSerializedFile);

        
        if (isModified()) {
            return newSerializedFile;
        } else {
            return serializedFile;
        }
    }

    public void setSerializedFile(SerializedFile serializedFile) {
        this.serializedFile = serializedFile;
        newSerializedFile = null;
        updateFileLabel(serializedFile);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
