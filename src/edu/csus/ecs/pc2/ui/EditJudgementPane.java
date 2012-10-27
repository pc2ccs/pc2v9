package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;

/**
 * Edit Judgement Pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditJudgementPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 6229906311932197623L;

    private JPanel buttonPane = null;

    private JButton addButton = null;

    private JButton updateButton = null;

    private JButton cancelButton = null;

    private JPanel centerPane = null;

    private JLabel jLabel2 = null;

    private JTextField displayNameTextField = null;

    private Judgement savedJudgement = null;  //  @jve:decl-index=0:

    private boolean populatingGUI = true;

    private JCheckBox deleteCheckBox = null;

    private JLabel judgementNameTitle = null;
    private JTextField acronymNameTextField;

    /**
     * This method initializes
     * 
     */
    public EditJudgementPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(422, 179));

        this.add(getCenterPane(), java.awt.BorderLayout.CENTER);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
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

    public String getPluginTitle() {
        return "Edit Judgement Pane";
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
                    addJudgement();
                }
            });
        }
        return addButton;
    }

    protected void addJudgement() {

        Judgement newJudgement = getJudgementFromFields();

        String name = newJudgement.getDisplayName().trim();
        
        if (name.length() < 1){
            JOptionPane.showMessageDialog(this,"Enter a judgement", "Missing judgement information", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        newJudgement.setDisplayName(name);

        getController().addNewJudgement(newJudgement);

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    private Judgement getJudgementFromFields() {
        Judgement judgement;
        if (savedJudgement == null){
            judgement = new Judgement(displayNameTextField.getText());
            judgement.setAcronym(acronymNameTextField.getText());
        } else {
            judgement = savedJudgement;
            judgement.setDisplayName(displayNameTextField.getText());
            judgement.setAcronym(acronymNameTextField.getText());
        }
        judgement.setActive(!getDeleteCheckBox().isSelected());
        return judgement;
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
                    updateJudgement();
                }
            });
        }
        return updateButton;
    }

    protected void updateJudgement() {

        Judgement newJudgement = getJudgementFromFields();

        String name = newJudgement.getDisplayName().trim();
        newJudgement.setDisplayName(name);

//        dumpJudgement (newJudgement);
        
        getController().updateJudgement(newJudgement);

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

        if (getParentFrame() != null) {
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

            int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Judgement modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                if (getAddButton().isEnabled()) {
                    addJudgement();
                } else {
                    updateJudgement();
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

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            judgementNameTitle = new JLabel();
            judgementNameTitle.setBounds(new Rectangle(14, 18, 99, 24));
            judgementNameTitle.setHorizontalAlignment(SwingConstants.RIGHT);
            judgementNameTitle.setText("Judgement");
            jLabel2 = new JLabel();
            jLabel2.setBounds(new java.awt.Rectangle(0, 0, 0, 0));
            jLabel2.setName("SourceExtLabel");
            jLabel2.setForeground(Color.black);
            jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
            jLabel2.setText("Source Extensions");
            jLabel2.setVisible(false);
            centerPane = new JPanel();
            centerPane.setLayout(null);
            centerPane.setName("advancedEdit");
            centerPane.add(jLabel2, jLabel2.getName());
            centerPane.add(getDisplayNameTextField(), getDisplayNameTextField().getName());
            centerPane.add(getDeleteCheckBox(), null);
            centerPane.add(judgementNameTitle, null);
            
            JLabel lblAcronym = new JLabel();
            lblAcronym.setText("Acronym");
            lblAcronym.setHorizontalAlignment(SwingConstants.RIGHT);
            lblAcronym.setBounds(new Rectangle(14, 18, 99, 24));
            lblAcronym.setBounds(24, 58, 99, 24);
            centerPane.add(lblAcronym);
            
            acronymNameTextField = new JTextField();
            acronymNameTextField.setToolTipText("Acronym for Judgement");
            acronymNameTextField.setName("acronymNameTextField");
            acronymNameTextField.setBounds(new Rectangle(126, 20, 263, 20));
            acronymNameTextField.setBounds(136, 60, 263, 20);
            acronymNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
            centerPane.add(acronymNameTextField);
        }
        return centerPane;
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getDisplayNameTextField() {
        if (displayNameTextField == null) {
            displayNameTextField = new JTextField();
            displayNameTextField.setBounds(new Rectangle(136, 20, 263, 20));
            displayNameTextField.setToolTipText("Name to display to users");
            displayNameTextField.setName("displayNameTextField");
            displayNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return displayNameTextField;
    }

    /**
     * Enable or disable Update button based on comparison of run to fields.
     * 
     */
    public void enableUpdateButton() {

        if (populatingGUI) {
            return;
        }
        
        boolean enableButton = false;

        if (savedJudgement != null) {
            
            Judgement judgement = new Judgement(displayNameTextField.getText());
            judgement.setDisplayName(displayNameTextField.getText());
            judgement.setActive(!getDeleteCheckBox().isSelected());
            enableButton = ! savedJudgement.isSameAs(judgement);
            
        } else {
            if (getAddButton().isVisible()) {
                enableButton = true;
            }
        }

        enableUpdateButtons(enableButton);
    }

    public Judgement getJudgement() {
        return savedJudgement;
    }

    public void setJudgement(final Judgement judgement) {

        this.savedJudgement = judgement;
        if (judgement == null){
            setDeleteCheckBoxEnabled(true);
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI(judgement);
                enableUpdateButtons(false);
            }
        });
    }

    public void setDeleteCheckBoxEnabled(boolean enabled) {
        getDeleteCheckBox().setEnabled(enabled);
    }

//    private void dumpJudgement(Judgement judgement) {
//        
//        System.out.println(judgement.isActive()
//                + " " + judgement.getElementId()
//                + " " + judgement 
//                + " " + judgement.getDisplayName());
//    }

    private void populateGUI(Judgement judgement2) {

        populatingGUI = true;

        if (judgement2 != null) {
            displayNameTextField.setText(judgement2.getDisplayName());
            getAddButton().setVisible(false);
            getUpdateButton().setVisible(true);
            getDeleteCheckBox().setSelected(! judgement2.isActive());

        } else {
            displayNameTextField.setText("");

            getAddButton().setVisible(true);
            getUpdateButton().setVisible(false);
            getDeleteCheckBox().setSelected(false);
        }

        populatingGUI = false;
    }

    protected void enableUpdateButtons(boolean changed) {
        if (changed) {
            cancelButton.setText("Cancel");
        } else {
            cancelButton.setText("Close");
        }
        // only enable the visible one, we are either editing or adding not both
        if (getUpdateButton().isVisible()) {
            getUpdateButton().setEnabled(changed);
        } else {
            getAddButton().setEnabled(changed);
        }
    }

    /**
     * This method initializes deleteCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getDeleteCheckBox() {
        if (deleteCheckBox == null) {
            deleteCheckBox = new JCheckBox();
            deleteCheckBox.setBounds(new Rectangle(136, 100, 208, 21));
            deleteCheckBox.setText("Hide Judgement");
            deleteCheckBox.setToolTipText("Hide this judgement from judges");
            deleteCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return deleteCheckBox;
    }
} // @jve:decl-index=0:visual-constraint="10,10"
