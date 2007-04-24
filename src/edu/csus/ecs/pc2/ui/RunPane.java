package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Add/Edit Run Pane
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class RunPane extends JPanePlugin {

    private JPanel messagePane = null;

    private JPanel buttonPane = null;

    private JButton addButton = null;

    private JButton updateButton = null;

    private JButton cancelButton = null;

    private JLabel messageLabel = null;

    private Run run = null;

    private JTabbedPane mainTabbedPane = null;

    private JPanel generalPane = null;

    private Log log = null;

    /**
     * This method initializes
     * 
     */
    public RunPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(536, 413));

        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getMainTabbedPane(), java.awt.BorderLayout.EAST);
        this.add(getGeneralPane(), java.awt.BorderLayout.CENTER);
    }

    public void setModelAndController(IModel inModel, IController inController) {
        super.setModelAndController(inModel, inController);
        log = getController().getLog();
    }

    public String getPluginTitle() {
        return "Edit Run Pane";
    }

    /**
     * This method initializes messagePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePane() {
        if (messagePane == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePane = new JPanel();
            messagePane.setLayout(new BorderLayout());
            messagePane.setPreferredSize(new java.awt.Dimension(25, 25));
            messagePane.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePane;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(15);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.add(getAddButton(), null);
            buttonPane.add(getUpdateButton(), null);
            buttonPane.add(getCancelButton(), null);
        }
        return buttonPane;
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
            addButton.setEnabled(false);
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addRun();
                }
            });
        }
        return addButton;
    }

    protected void addRun() {

        Run newRun = getRunFromFields();

        // TODO update run
        // getController().addNewRun(newRun);

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);
        
        if ( getParentFrame() != null){
            getParentFrame().setVisible(false);
        }
    }

    private Run getRunFromFields() {
        
        return run;
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
            updateButton.setEnabled(false);
            updateButton.setMnemonic(java.awt.event.KeyEvent.VK_U);
            updateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateRun();
                }
            });
        }
        return updateButton;
    }

    protected void updateRun() {

        Run newRun = getRunFromFields();

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);
        
        // TODO update run
        // getController().updateRun(newRun);
        
        if ( getParentFrame() != null){
            getParentFrame().setVisible(false);
        }
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
            cancelButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleCancelButton();
                }
            });
        }
        return cancelButton;
    }

    protected void handleCancelButton() {

        if (getAddButton().isEnabled() || getUpdateButton().isEnabled()) {

            // Something changed, are they sure ?

            int result = FrameUtilities.yesNoCancelDialog("Run modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                if (getAddButton().isEnabled()) {
                    addRun();
                } else {
                    updateRun();
                }
                if ( getParentFrame() != null){
                    getParentFrame().setVisible(false);
                }
            }
        } else {
            if ( getParentFrame() != null){
                getParentFrame().setVisible(false);
            }
        }
    }

    public Run getRun() {
        return run;
    }

    public void setRun(final Run run) {

        this.run = run;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI(run);
                enableUpdateButtons(false);
            }
        });
    }

    private void populateGUI(Run run2) {

        if (run2 != null) {

            getAddButton().setVisible(false);
            getUpdateButton().setVisible(true);
            

        } else {

            
            getAddButton().setVisible(true);
            getUpdateButton().setVisible(false);
            
        }
    }

    protected void enableUpdateButtons(boolean editedText) {
        if (editedText) {
            cancelButton.setText("Cancel");
        } else {
            cancelButton.setText("Close");
        }
        addButton.setEnabled(editedText);
        updateButton.setEnabled(editedText);
    }

    /**
     * This method initializes mainTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getMainTabbedPane() {
        if (mainTabbedPane == null) {
            mainTabbedPane = new JTabbedPane();
        }
        return mainTabbedPane;
    }

    /**
     * This method initializes generalPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGeneralPane() {
        if (generalPane == null) {
            generalPane = new JPanel();
            generalPane.setLayout(null);
        }
        return generalPane;
    }

    public void showMessage(final String message){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(message);
            }
        });
    }

} // @jve:decl-index=0:visual-constraint="10,10"
