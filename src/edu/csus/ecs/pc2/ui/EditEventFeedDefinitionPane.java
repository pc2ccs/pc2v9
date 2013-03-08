package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.EventFeedDefinition;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Add/Update an Event Feed definition pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditEventFeedDefinitionPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 5201693671680464978L;

    private JPanel centerPane = null;

    private JPanel buttonPanel = null;

    private JButton cancelButton = null;

    private JButton addButton = null;

    private JTextField nameTextField;

    private JTextField portTextField;
    
    private JCheckBox activeCheckBox = null;
    
    private JCheckBox frozenCheckBox = null;

    private boolean populatingGUI;

    private EventFeedDefinition existingEventDefinition = null;

    /**
     * This method initializes
     * 
     */
    public EditEventFeedDefinitionPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(399, 232));
        this.add(getCenterPane(), BorderLayout.CENTER);
        this.add(getButtonPanel(), BorderLayout.SOUTH);

    }

    @Override
    public String getPluginTitle() {
        return "Edit Event Feed Definition Plugin";
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            centerPane = new JPanel();
            centerPane.setLayout(null);

            JLabel lblNewLabel = new JLabel("Title");
            lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            lblNewLabel.setBounds(76, 26, 74, 14);
            centerPane.add(lblNewLabel);

            JLabel lblactuve = new JLabel("Port");
            lblactuve.setHorizontalAlignment(SwingConstants.RIGHT);
            lblactuve.setBounds(76, 102, 74, 14);
            centerPane.add(lblactuve);

            nameTextField = new JTextField();
            nameTextField.setBounds(167, 24, 196, 20);
            centerPane.add(nameTextField);
            nameTextField.setColumns(10);

            activeCheckBox = new JCheckBox("Active");
            activeCheckBox.setToolTipText("Is the event feed active?");
            activeCheckBox.setBounds(167, 60, 97, 23);
            centerPane.add(activeCheckBox);

            frozenCheckBox = new JCheckBox("Frozen");
            frozenCheckBox.setToolTipText("Stop sending judgements near end of contest");
            frozenCheckBox.setBounds(167, 137, 97, 23);
            centerPane.add(frozenCheckBox);

            portTextField = new JTextField();
            portTextField.setBounds(167, 100, 51, 20);
            centerPane.add(portTextField);
            portTextField.setColumns(10);
            portTextField.setDocument(new IntegerDocument());
        }
        return centerPane;
    }

    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(35);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(flowLayout);
            buttonPanel.setPreferredSize(new Dimension(35, 35));
            buttonPanel.add(getAddButton(), null);
            buttonPanel.add(getCancelButton(), null);
        }
        return buttonPanel;
    }

    /**
     * This method initializes cancelButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
            cancelButton.setMnemonic(KeyEvent.VK_C);
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleCancelButton();
                }
            });
        }
        return cancelButton;
    }
    
    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI(null);
            }
        });
    }

    protected void handleCancelButton() {

        if (getAddButton().isEnabled()) {

            int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Event Feed Definition modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                if (getAddButton().isEnabled()) {
                    addEventFeedDefinition();
                } else {
                    updateEventFeedDefinition();
                }
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
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

    private void updateEventFeedDefinition() {
        // TODO Auto-generated method stub

    }

    private void addEventFeedDefinition() {
        // TODO CCS add event def

    }

    /**
     * This method initializes addButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddButton() {
        if (addButton == null) {
            addButton = new JButton();
            addButton.setText("Add");
            addButton.setMnemonic(KeyEvent.VK_A);
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addDef();
                }
            });
        }
        return addButton;
    }

    protected void addDef() {
        // TODO Auto-generated method stub

    }
    
    public JCheckBox getActiveCheckBox() {
        return activeCheckBox;
    }
    
    public JCheckBox getFrozenCheckBox() {
        return frozenCheckBox;
    }
    
    void populateGUI(EventFeedDefinition eventDefinition){
        populatingGUI = true;

        try {
            if (eventDefinition == null) {
                getActiveCheckBox().setSelected(true);
                getFrozenCheckBox().setSelected(false);
                nameTextField.setText("");
                portTextField.setText("3700");
            } else {
                getActiveCheckBox().setSelected(eventDefinition.isActive());
                getFrozenCheckBox().setSelected(eventDefinition.isFrozen());
                nameTextField.setText(eventDefinition.getDisplayName());
                portTextField.setText(new Integer(eventDefinition.getPort()).toString());
            }

            setAddButtonState();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        populatingGUI = false;
    }

    public void setEventDefinition(EventFeedDefinition eventDefinition) {
        this.existingEventDefinition = eventDefinition;
        populateGUI(eventDefinition);
    }

    public void setAddButtonState() {
        if (existingEventDefinition == null) {
            getAddButton().setText("Add");
            getAddButton().setToolTipText("Add this definition");
            getAddButton().setMnemonic(KeyEvent.VK_A);
        } else {
            getAddButton().setText("Update");
            getAddButton().setToolTipText("Update this definition");
            getAddButton().setMnemonic(KeyEvent.VK_U);
        }
    }

    public void enableUpdateButton() {

        if (populatingGUI) {
            return;
        }

        boolean changed = false;
        
        EventFeedDefinition definition = getEventFeedDefinitionFromFields();
        
        if (existingEventDefinition != null){
            changed = existingEventDefinition.isSameAs(definition);
        }

        getAddButton().setEnabled(changed);

        if (changed) {
            getCancelButton().setText("Cancel");
            getAddButton().setMnemonic(KeyEvent.VK_C);
        } else {
            getCancelButton().setText("Close");
            getAddButton().setMnemonic(KeyEvent.VK_C);
        }
    }

    private EventFeedDefinition getEventFeedDefinitionFromFields() {
        
        String name = nameTextField.getText();
        EventFeedDefinition def = new EventFeedDefinition(name);
        def.setActive(getActiveCheckBox().isSelected());
        def.setFrozen(getFrozenCheckBox().isSelected());
        def.setPort(Integer.parseInt(portTextField.getText()));
        return def;
    }
} // @jve:decl-index=0:visual-constraint="10,10"
