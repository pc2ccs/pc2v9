// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.StringComparator;
/**
 * ScoringPropertiesPane is a inner pane for ContestInformationPane
 * Allows editing Scoring Properties for admin.
 * @author pc2@ecs.csus.edu
 *
 */
public class ScoringPropertiesPane extends JPanel {


    private static final long serialVersionUID = 147946474651407431L;
    private static final int PROPERTY_COL_WIDTH = 170;
    private static final int VALUE_COL_WIDTH = 500;
    private MCLB propertyListBox = null;

    private Properties originalProperties = new Properties(); // @jve:decl-index=0:

    private IPropertyUpdater propertyUpdater = null;

    private JButton updateButton;

    private JButton cancelButton;

    public ScoringPropertiesPane(JButton updateButtona,JButton cancelButtona) {
        super();
        updateButton = updateButtona;
        cancelButton = cancelButtona;

        initialize();
    }

    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(PROPERTY_COL_WIDTH + VALUE_COL_WIDTH + 8, 200));
        this.setMinimumSize(new Dimension(PROPERTY_COL_WIDTH + VALUE_COL_WIDTH + 8, 200));
        this.add(getPropertyListBox(), BorderLayout.CENTER);
    }

    protected void updateProperties(Properties properties) {
        propertyUpdater.updateProperties(properties);
        propertyListBox.removeAllRows();

    }

    private MCLB getPropertyListBox() {
        if (propertyListBox == null) {
            propertyListBox = new MCLB();

            propertyListBox.setMultipleSelections(true);

            Object[] cols = { "Property", "Value" };

            propertyListBox.addColumns(cols);

            HeapSorter sorter = new HeapSorter();
            sorter.setComparator(new StringComparator());
            propertyListBox.setColumnSorter(0, sorter, 1);

            propertyListBox.setColumnSize(0,PROPERTY_COL_WIDTH);
            propertyListBox.setColumnSize(1,VALUE_COL_WIDTH);
            propertyListBox.setResizable(false);

            propertyListBox.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {
                    enableButtons();
                }
            });
        }
        return propertyListBox;
    }

    public boolean propertiesChanged() {
        return !originalProperties.equals(getProperties());
    }

    public void setProperties(final Properties properties) {

        if (properties == null){
            throw new InvalidParameterException("Properties are null");
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                originalProperties = properties;
                refreshProperties();
            }
        });
    }

    protected void refreshProperties() {

        propertyListBox.removeAllRows();

        Set<Object> set = originalProperties.keySet();

        String[] keys = set.toArray(new String[set.size()]);

        Arrays.sort(keys);
        for (String key : keys) {

            Object[] objects = new Object[2];
            objects[0] = key;
            objects[1] = createJTextField((String) originalProperties.get(key), false);
            propertyListBox.addRow(objects);
        }

        propertyListBox.setColumnSize(0,PROPERTY_COL_WIDTH);
        propertyListBox.setColumnSize(1,VALUE_COL_WIDTH);

        enableButtons();
    }

    /**
     * Reads properties from user input.
     * @return
     */
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
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                enableButtons();
            }
        });
        return textField;
    }

    public IPropertyUpdater getPropertyUpdater() {
        return propertyUpdater;
    }

    public void setPropertyUpdater(IPropertyUpdater propertyUpdater) {
        this.propertyUpdater = propertyUpdater;
    }

    protected void enableButtons() {
        boolean changed = propertiesChanged();

        if (changed) {
            updateButton.setEnabled(true);
            cancelButton.setEnabled(true);
        }
        else {
            updateButton.setEnabled(false);
            cancelButton.setEnabled(false);
        }
    }
}
