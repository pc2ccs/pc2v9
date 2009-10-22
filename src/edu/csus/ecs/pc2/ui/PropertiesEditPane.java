package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.util.Properties;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PropertiesEditPane extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -8131372758010937976L;

    private JPanel buttonPane = null;

    private JButton updateButton = null;

    private JButton closeButton = null;

    private MCLB propertyListBox = null;

    private Properties originalProperties = new Properties(); // @jve:decl-index=0:

    /**
     * This method initializes
     * 
     */
    public PropertiesEditPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(374, 180));
        this.add(getButtonPane(), BorderLayout.SOUTH);
        this.add(getPropertyListBox(), BorderLayout.CENTER);

    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(35);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.setPreferredSize(new Dimension(35, 35));
            buttonPane.add(getUpdateButton(), null);
            buttonPane.add(getCloseButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes updateButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getUpdateButton() {
        if (updateButton == null) {
            updateButton = new JButton();
            updateButton.setText("Update");
            updateButton.setMnemonic(KeyEvent.VK_U);
        }
        return updateButton;
    }

    /**
     * This method initializes closeButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCloseButton() {
        if (closeButton == null) {
            closeButton = new JButton();
            closeButton.setText("Close");
            closeButton.setMnemonic(KeyEvent.VK_C);
        }
        return closeButton;
    }

    private MCLB getPropertyListBox() {
        if (propertyListBox == null) {
            propertyListBox = new MCLB();

            propertyListBox.setMultipleSelections(true);

            Object[] cols = { "Property", "Value" };

            propertyListBox.addColumns(cols);

            HeapSorter sorter = new HeapSorter();
            propertyListBox.setColumnSorter(1, sorter, 1);

            propertyListBox.autoSizeAllColumns();

            propertyListBox.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent e) {
                    enableButtons();
                }
            });
        }
        return propertyListBox;
    }

    protected void enableButtons() {
        boolean changed = propertiesChanged();
        updateButton.setEnabled(changed);
        if (changed){
            closeButton.setText("Cancel");
        }  else {
            closeButton.setText("Close");
        }
    }

    protected boolean propertiesChanged(){
        
        Properties props = getProperties();

        Set<Object> set = originalProperties.keySet();
        String[] keys = (String[]) set.toArray(new String[set.size()]);
        for (String key : keys) {
            System.out.println(key + ": " + originalProperties.get(key));
            System.out.println(key + "> " + props.get(key));
        }
        
        return ! originalProperties.equals(getProperties());
    }

    /**
     * Set properties and refresh list/component.
     * 
     * @param properties
     */
    public void setProperties(final Properties properties) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                originalProperties = properties;
                refreshProperties();
            }
        });

    }

    protected void refreshProperties() {

        propertyListBox.removeAllRows();

        Set<Object> set = originalProperties.keySet();

        String[] keys = (String[]) set.toArray(new String[set.size()]);

        for (String key : keys) {

            Object[] objects = new Object[2];
            objects[0] = key;
            objects[1] = createJTextField((String) originalProperties.get(key), false);
            propertyListBox.addRow(objects);
        }

        enableButtons();
    }

    public Properties getProperties() {

        Properties fieldProps = new Properties();

        for (int i = 0; i < propertyListBox.getRowCount(); i++) {
            Object[] objects = propertyListBox.getRow(i);
            JTextField field = (JTextField) objects[1];
            fieldProps.put(objects[0], field.getText());
        }

        return fieldProps;
    }

    private JTextField createJTextField(String text, boolean passwordField) {
        JTextField textField = new JTextField();
        textField.setText(text);

        // textField.setEditable(editFieldsEnabled);

        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                propertyListBox.autoSizeAllColumns();
                enableButtons();
            }
        });
        return textField;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
