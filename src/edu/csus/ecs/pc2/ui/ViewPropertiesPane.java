package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.Permission.Type;

/**
 * View this clients properties.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ViewPropertiesPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 2554693130978453347L;

    private ListModel<Object> defaultListModel = new DefaultListModel<Object>();  //  @jve:decl-index=0:visual-constraint="455,14"

    private JLabel permissionCountLabel = null;

    private JCheckBoxJList permissionsJList = null;

    private Account account;  //  @jve:decl-index=0:

    private Permission permission = new Permission();

    private JScrollPane permissionsScrollPane = null;

    private JPanel buttonPane = null;

    private JButton closeButton = null;

    /**
     * This method initializes
     * 
     */
    public ViewPropertiesPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(396, 208));
        this.add(getPermissionsScrollPane(), BorderLayout.CENTER);
        this.add(getButtonPane(), BorderLayout.SOUTH);

    }

    @Override
    public String getPluginTitle() {
        return "View Properties Pane";
    }

    /**
     * This method initializes permissionsJList
     * 
     * @return javax.swing.JList
     */
    private JCheckBoxJList getPermissionsJList() {
        if (permissionsJList == null) {
            permissionsJList = new JCheckBoxJList();
            permissionsJList.setModel(defaultListModel);
            // ListSelectionListeners are called before JCheckBoxes get updated
            permissionsJList.addPropertyChangeListener("change", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    showPermissionCount(permissionsJList.getSelectedIndices().length + " permissions selected");
                }
            });
        }
        return permissionsJList;
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        account = inContest.getAccount(inContest.getClientId());
        populatePermissions(account);
        
        if (getParentFrame() != null){
            getParentFrame().setTitle("Permissions/Abilities for " + inContest.getClientId());
        }

    }

    private void populatePermissions(Account inAccount) {

        ((DefaultListModel<Object>) defaultListModel).removeAllElements();

        if (inAccount == null) {

            for (String name : getPermissionDescriptions()) {
                JCheckBox checkBox = new JCheckBox(name);
                ((DefaultListModel<Object>) defaultListModel).addElement(checkBox);
            }
            getPermissionsJList().setSelectedIndex(-1);

        } else {

            int count = 0;
            for (Type type : Permission.Type.values()) {
                if (account.isAllowed(type)) {
                    count++;
                }
            }

            if (count > 0) {
                int[] indexes = new int[count];
                count = 0;
                int idx = 0;
                for (Type type : Permission.Type.values()) {
                    JCheckBox checkBox = new JCheckBox(permission.getDescription(type));
                    ((DefaultListModel<Object>) defaultListModel).addElement(checkBox);
                    if (account.isAllowed(type)) {
                        indexes[count] = idx;
                        count++;
                    }
                    idx++;
                }
                getPermissionsJList().setSelectedIndices(indexes);
                getPermissionsJList().ensureIndexIsVisible(0);
            } else {
                for (Type type : Permission.Type.values()) {
                    JCheckBox checkBox = new JCheckBox(permission.getDescription(type));
                    ((DefaultListModel<Object>) defaultListModel).addElement(checkBox);
                }
            }
        }

        showPermissionCount(getPermissionsJList().getSelectedIndices().length + " permissions selected");
    }

    private String[] getPermissionDescriptions() {
        String[] permissionListNames = new String[Permission.Type.values().length];

        int i = 0;
        for (Type type : Permission.Type.values()) {
            permissionListNames[i] = permission.getDescription(type);
            i++;
        }

        Arrays.sort(permissionListNames);

        return permissionListNames;
    }

    public void showPermissionCount(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                permissionCountLabel.setText(message);
            }
        });
    }

    /**
     * This method initializes permissionsScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getPermissionsScrollPane() {
        if (permissionsScrollPane == null) {
            permissionsScrollPane = new JScrollPane();
            permissionsScrollPane.setViewportView(getPermissionsJList());
        }
        return permissionsScrollPane;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout());
            buttonPane.setPreferredSize(new Dimension(35, 35));
            buttonPane.add(getCloseButton(), null);
        }
        return buttonPane;
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
            closeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    closeWindow();
                }
            });
        }
        return closeButton;
    }

    protected void closeWindow() {
        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
