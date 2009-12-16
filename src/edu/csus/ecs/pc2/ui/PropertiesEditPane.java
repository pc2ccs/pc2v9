package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;

/**
 * Properties Edit Pane.
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

    private JFrame parentFrame = null;

    private IPropertyUpdater propertyUpdater = null;

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
            updateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateProperties(getProperties());
                }
            });
        }
        return updateButton;
    }

    protected void updateProperties(Properties properties) {
        propertyUpdater.updateProperties(properties);
        propertyListBox.removeAllRows();
        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
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
            closeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleCancelButton();
                }
            });
        }
        return closeButton;
    }

    /**
     * 
     */
    protected void handleCancelButton() {

        if (propertiesChanged()) {

            // Something changed, are they sure ?

            int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Properties modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                updateProperties(getProperties());
            } else if (result == JOptionPane.NO_OPTION) {
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            }

        } else {
            if (getParentFrame() != null) {
                getParentFrame().setVisible(false);
            }
        }
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
        if (changed) {
            closeButton.setText("Cancel");
        } else {
            closeButton.setText("Close");
        }
    }

    /**
     * Have the properties been edited (changed).
     * 
     * @return true if there have been changes.
     */
    public boolean propertiesChanged() {
        return !originalProperties.equals(getProperties());
    }

    /**
     * Set properties and refresh list/component.
     * 
     * @param properties
     */
    public void setProperties(final Properties properties) {
        
        if (properties == null){
            throw new InvalidParameterException("Properties are null");
        }

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

        Arrays.sort(keys);
        for (String key : keys) {

            Object[] objects = new Object[2];
            objects[0] = key;
            objects[1] = createJTextField((String) originalProperties.get(key), false);
            propertyListBox.addRow(objects);
        }
        
        propertyListBox.autoSizeAllColumns();

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

    public JFrame getParentFrame() {
        return parentFrame;
    }

    public void setParentFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        addWindowCloserListener();
    }

    private void addWindowCloserListener() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (getParentFrame() != null) {
                    getParentFrame().addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            handleCancelButton();
                        }
                    });
                }
            }
        });
    }

    public IPropertyUpdater getPropertyUpdater() {
        return propertyUpdater;
    }

    public void setPropertyUpdater(IPropertyUpdater propertyUpdater) {
        this.propertyUpdater = propertyUpdater;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
