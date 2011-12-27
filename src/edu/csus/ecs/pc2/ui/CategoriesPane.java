package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.Category;
import edu.csus.ecs.pc2.core.model.CategoryEvent;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.ICategoryListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * Show Categories, allow add and edit.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: CategorysPane.java 2343 2011-09-16 16:37:02Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/ui/CategorysPane.java $
public class CategoriesPane extends JPanePlugin {


    /**
     * 
     */
    private static final long serialVersionUID = 7037601796882987184L;

    private MCLB categoryListBox = null;

    private JPanel buttonsPane = null;

    private JButton addButton = null;

    private JPanel statusPanel = null;

    private JButton editButton = null;

    private EditCategoryFrame editCategoryFrame;

    private EditCategoryFrame getEditCategoryFrame() {
        if (editCategoryFrame == null) {
            editCategoryFrame = new EditCategoryFrame();
        }
        return editCategoryFrame;
    }

    /**
     * This method initializes
     * 
     */
    public CategoriesPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(584, 211));
        this.add(getButtonsPane(), java.awt.BorderLayout.SOUTH);
        this.add(getCategoryListBox(), java.awt.BorderLayout.CENTER);

//        editCategoryFrame = new EditCategoryFrame();
    }

    @Override
    public String getPluginTitle() {
        return "Categories Panel";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        getContest().addCategoryListener(new CategoryListenerImplementation());

        getEditCategoryFrame().setContestAndController(inContest, inController);

        initializePermissions();
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateGUIperPermissions();
                reloadCategoriesList();
            }
        });
    }
    
    private void updateGUIperPermissions() {
        addButton.setVisible(isAllowed(Permission.Type.ADD_CATEGORY));
        editButton.setVisible(isAllowed(Permission.Type.EDIT_CATEGORY));
    }

    /**
     * Number of clars that match this Category.
     * 
     * @param category
     * @return count of clars that match.
     */
    private int numberOfClars(Category category) {

        int count = 0;
        ElementId elementId = category.getElementId();

        for (Clarification clar : getContest().getClarifications()) {
            if (clar.isDeleted()) {
                continue;
            }
            if (clar.getProblemId().equals(elementId)) {
                    count++;
            }
        }
        return count;
    }

    protected void reloadCategoriesList() {

        getCategoryListBox().removeAllRows();

        for (Category category : getContest().getCategories()) {
            updateCategoryRow(category);
        }
        
        getCategoryListBox().autoSizeAllColumns();
    }

    /**
     * This method initializes CategoryListBox
     * 
     * @return edu.csus.ecs.pc2.ui.MCLB
     */
    private MCLB getCategoryListBox() {
        if (categoryListBox == null) {
            categoryListBox = new MCLB();

            categoryListBox.add(getStatusPanel(), java.awt.BorderLayout.NORTH);
            Object[] cols = { "Category" };
            categoryListBox.addColumns(cols);
        }
        return categoryListBox;
    }

    /**
     * This method initializes buttonsPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonsPane() {
        if (buttonsPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(45);
            buttonsPane = new JPanel();
            buttonsPane.setLayout(flowLayout);
            buttonsPane.add(getAddButton(), null);
            buttonsPane.add(getEditButton(), null);
        }
        return buttonsPane;
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
                    addCategory();
                }
            });
        }
        return addButton;
    }

    protected void addCategory() {
        getEditCategoryFrame().setCategory(null);
        getEditCategoryFrame().setDeleteCheckBoxEnabled(true);
        getEditCategoryFrame().setVisible(true);
    }

    /**
     * This method initializes statusPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getStatusPanel() {
        if (statusPanel == null) {
            statusPanel = new JPanel();
            statusPanel.setLayout(new BorderLayout());
            statusPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        }
        return statusPanel;
    }

    private void showMessage(final String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void updateCategoryRow(final Category category) {
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildCategoryRow(category);
                int rowNumber = categoryListBox.getIndexByKey(category.getElementId());
                if (rowNumber == -1) {
                    categoryListBox.addRow(objects, category.getElementId());
                } else {
                    categoryListBox.replaceRow(objects, rowNumber);
                }
                categoryListBox.autoSizeAllColumns();
            }
        });
    }

    private Object[] buildCategoryRow(Category category) {

        // Object[] cols = { "Category" };

        try {
            int cols = categoryListBox.getColumnCount();
            Object[] s = new String[cols];

            s[0] = category.toString();
            if (!category.isActive()) {
                s[0] = "[HIDDEN] " + category.toString();
            }
            return s;
        } catch (Exception exception) {
            StaticLog.getLog().log(Log.INFO, "Exception in buildCategoryRow()", exception);
        }
        return null;

    }

    /**
     * This method initializes editButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getEditButton() {
        if (editButton == null) {
            editButton = new JButton();
            editButton.setText("Edit");
            editButton.setMnemonic(KeyEvent.VK_E);
            editButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    editSelectedCategory();
                }
            });
        }
        return editButton;
    }

    protected void editSelectedCategory() {

        int selectedIndex = categoryListBox.getSelectedIndex();
        if (selectedIndex == -1) {
            showMessage("Select a category to edit");
            return;
        }

        try {
            ElementId elementId = (ElementId) categoryListBox.getKeys()[selectedIndex];
            Category categoryToEdit = getContest().getCategory(elementId);
            
            int numberClars = numberOfClars(categoryToEdit);
            if (numberClars > 0) {
                JOptionPane.showMessageDialog(this, "There are " + numberClars + " clarifications which will be changed if this category is changed", 
                        "Clarifictions may be changed", JOptionPane.WARNING_MESSAGE);
            }

            getEditCategoryFrame().setCategory(categoryToEdit);
            getEditCategoryFrame().setVisible(true);
            
        } catch (Exception e) {
            getController().getLog().log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to edit category, check log");
        }
    }

    /**
     * Account Listener Implementation.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id: CategorysPane.java 2343 2011-09-16 16:37:02Z laned $
     */
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            // ignored
        }

        public void accountModified(AccountEvent accountEvent) {
            // check if is this account
            Account account = accountEvent.getAccount();
            /**
             * If this is the account then update the GUI display per the potential change in Permissions.
             */
            if (getContest().getClientId().equals(account.getClientId())) {
                // They modified us!!
                initializePermissions();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateGUIperPermissions();
                    }
                });

            }
        }

        public void accountsAdded(AccountEvent accountEvent) {
            // ignore
        }

        public void accountsModified(AccountEvent accountEvent) {
            Account[] accounts = accountEvent.getAccounts();
            for (Account account : accounts) {

                /**
                 * If this is the account then update the GUI display per the potential change in Permissions.
                 */
                if (getContest().getClientId().equals(account.getClientId())) {
                    // They modified us!!
                    initializePermissions();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            updateGUIperPermissions();
                        }
                    });
                }
            }
        }

        public void accountsRefreshAll(AccountEvent accountEvent) {

            initializePermissions();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateGUIperPermissions();
                }
            });
        }
    }
    
    /**
     * Category Listener Implementation.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public class CategoryListenerImplementation implements ICategoryListener {

        public void categoryAdded(CategoryEvent event) {
            reloadCategoriesList();
        }

        public void categoryChanged(CategoryEvent event) {
            reloadCategoriesList();
        }

        public void categoryRemoved(CategoryEvent event) {
            reloadCategoriesList();
        }

        public void categoryRefreshAll(CategoryEvent event) {
            reloadCategoriesList();
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
