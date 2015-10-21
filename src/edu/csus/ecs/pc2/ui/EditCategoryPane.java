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
import edu.csus.ecs.pc2.core.model.Category;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Edit Category Pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: EditJudgementPane.java 2343 2011-09-16 16:37:02Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/ui/EditJudgementPane.java $
public class EditCategoryPane extends JPanePlugin {

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

    private JTextField jTextField3 = null;

    private Category category = null;  //  @jve:decl-index=0:

    private boolean populatingGUI = true;

    private JCheckBox deleteCheckBox = null;

    private JLabel categoryNameTitle = null;

    /**
     * This method initializes
     * 
     */
    public EditCategoryPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(422, 144));

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
        return "Edit Category Pane";
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
                    addCategory();
                }
            });
        }
        return addButton;
    }

    protected void addCategory() {

        Category newCategory = getCategoryFromFields();

        String name = newCategory.getDisplayName().trim();
        
        if (name.length() < 1){
            JOptionPane.showMessageDialog(this,"Enter a category", "Missing category information", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        newCategory.setDisplayName(name);

        getController().addNewCategory(newCategory);

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    private Category getCategoryFromFields() {
        Category category2;
        if (category == null){
            category2 = new Category(displayNameTextField.getText());
        } else {
            category2 = category;
            category2.setDisplayName(displayNameTextField.getText());
        }
        category2.setActive(!getDeleteCheckBox().isSelected());
        return category2;
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
                    updateCategory();
                }
            });
        }
        return updateButton;
    }

    protected void updateCategory() {

        Category newCategory = getCategoryFromFields();

        String name = newCategory.getDisplayName().trim();
        if (name.length() < 1){
            JOptionPane.showMessageDialog(this,"Enter a category", "Missing category information", JOptionPane.ERROR_MESSAGE);
            return;
        }
        newCategory.setDisplayName(name);

        getController().updateCategory(newCategory);

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

            int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Category modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                if (getAddButton().isEnabled()) {
                    addCategory();
                } else {
                    updateCategory();
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
            categoryNameTitle = new JLabel();
            categoryNameTitle.setBounds(new Rectangle(14, 18, 99, 24));
            categoryNameTitle.setHorizontalAlignment(SwingConstants.RIGHT);
            categoryNameTitle.setText("Category");
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
            centerPane.add(getJTextField3(), getJTextField3().getName());
            centerPane.add(getDeleteCheckBox(), null);
            centerPane.add(categoryNameTitle, null);
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
            displayNameTextField.setBounds(new Rectangle(126, 20, 263, 20));
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
     * This method initializes jTextField3
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJTextField3() {
        if (jTextField3 == null) {
            jTextField3 = new JTextField();
            jTextField3.setBounds(new java.awt.Rectangle(0, 0, 0, 0));
            jTextField3.setName("sourceExtTextField");
            jTextField3.setToolTipText("Form: *.cpp;*.c");
            jTextField3.setVisible(false);
        }
        return jTextField3;
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

        if (category != null) {
            
            Category category2 = new Category(displayNameTextField.getText());
            category2.setDisplayName(displayNameTextField.getText());
            category2.setActive(!getDeleteCheckBox().isSelected());
            enableButton = ! category2.isSameAs(category);
            
        } else {
            if (getAddButton().isVisible()) {
                enableButton = true;
            }
        }

        enableUpdateButtons(enableButton);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(final Category category) {

        this.category = category;
        if (category == null){
            setDeleteCheckBoxEnabled(true);
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI(category);
                enableUpdateButtons(false);
            }
        });
    }

    public void setDeleteCheckBoxEnabled(boolean enabled) {
        getDeleteCheckBox().setEnabled(enabled);
    }

    private void populateGUI(Category category2) {

        populatingGUI = true;

        if (category2 != null) {
            displayNameTextField.setText(category2.getDisplayName());
            getAddButton().setVisible(false);
            getUpdateButton().setVisible(true);
            getDeleteCheckBox().setSelected(! category2.isActive());

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
            deleteCheckBox.setBounds(new Rectangle(124, 61, 208, 21));
            deleteCheckBox.setText("Hide Category");
            deleteCheckBox.setActionCommand("Hide Category");
            deleteCheckBox.setToolTipText("Hide this Category");
            deleteCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return deleteCheckBox;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
