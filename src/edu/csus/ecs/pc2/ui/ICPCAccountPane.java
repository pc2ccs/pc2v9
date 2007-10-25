/**
 * 
 */
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;


import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.imports.ICPCAccount;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IContest;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import java.awt.FlowLayout;

/**
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ICPCAccountPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -94109299290020550L;

    private DisplayNameFormatterPane displayNameFormatterPane = null;

    private MCLB accountListBox = null;

    private JPanel buttonPanel = null;

    private JButton updateButton = null;

    private JButton cancelButton = null;

    private ICPCAccount[] icpcAccounts;
    
    private String displayChoice = "SHORTSCHOOLNAME";
    
    private Vector<Account> updatedAccountVector = new Vector<Account>();  
    /**
     * @author pc2@ecs.csus.edu
     *
     */
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            changeDisplayName(displayChoice);
        }

        public void accountModified(AccountEvent event) {
            changeDisplayName(displayChoice);
        }
        
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    public class PropertyChangeListenerImplementation implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getNewValue() != null && !evt.getNewValue().equals(evt.getOldValue())) {
                // display choice has changed
                displayChoice=(String) evt.getNewValue();
                changeDisplayName(displayChoice);
                // TODO now reload the grid?
            }
        }
    }

    /**
     * 
     */
    public ICPCAccountPane() {
        super();
        initialize();

    }

    private String getSiteTitle(String string) {
        return "Site " + string;
    }

    private String getTeamDisplayName(Account account) {
        if (account != null) {
            return account.getDisplayName();
        }

        return account.getDisplayName();
    }

    private Object[] buildAccountRow(Account account, String newDisplayName) {
        // Object[] cols = {"Site", "Type", "Account Id", "Current Display Name", "New Display Name"};
        try {
            int cols = accountListBox.getColumnCount();
            Object[] s = new String[cols];

            ClientId clientId = account.getClientId();
            s[0] = getSiteTitle("" + account.getSiteNumber());
            s[1] = clientId.getClientType().toString().toLowerCase();
            s[2] = "" + clientId.getClientNumber();

            s[3] = getTeamDisplayName(account);
            s[4] = newDisplayName;
            return s;
        } catch (Exception exception) {
            getController().getLog().log(Log.INFO, "Exception in buildAccountRow()", exception);
        }
        return null;
    }
    
    protected void changeDisplayName(String string) {
        if (string != null && string.length() > 0) {
            updatedAccountVector.clear();
            getAccountListBox().removeAllRows();
            String newDisplayName;
            // TODO must be a cleaner way of doing this
            if (string.equalsIgnoreCase(DisplayNameFormatterPane.DisplayNameChoice.TEAMNAME.toString())) {
                for (ICPCAccount account : icpcAccounts) {
                    if (account.getClientId() != null) {
                        Account account2 = getContest().getAccount(account.getClientId());
                        if (account2 != null) {
                            newDisplayName=account.getExternalName();
                            updateDisplayName(account2, account, newDisplayName);
                        }
                    }
                }
            } else {
                if (string.equalsIgnoreCase(DisplayNameFormatterPane.DisplayNameChoice.SCHOOLNAME.toString())) {
                    for (ICPCAccount account : icpcAccounts) {
                        if (account.getClientId() != null) {
                            Account account2 = getContest().getAccount(account.getClientId());
                            if (account2 != null) {
                                newDisplayName=account.getLongSchoolName();
                                updateDisplayName(account2, account, newDisplayName);
                            }
                        }
                    }
                } else {
                    if (string.equalsIgnoreCase(DisplayNameFormatterPane.DisplayNameChoice.SHORTSCHOOLNAME.toString())) {
                        for (ICPCAccount account : icpcAccounts) {
                            if (account.getClientId() != null) {
                                Account account2 = getContest().getAccount(account.getClientId());
                                if (account2 != null) {
                                    newDisplayName=account.getShortSchoolName();
                                    updateDisplayName(account2, account, newDisplayName);
                                }
                            }
                        }
                    } else {
                        if (string.equalsIgnoreCase(DisplayNameFormatterPane.DisplayNameChoice.TEAMANDSHORTSCHOOLNAME.toString())) {
                            for (ICPCAccount account : icpcAccounts) {
                                if (account.getClientId() != null) {
                                    Account account2 = getContest().getAccount(account.getClientId());
                                    if (account2 != null) {
                                        newDisplayName=account.getExternalName()+" ("+account.getShortSchoolName()+")";
                                        updateDisplayName(account2, account, newDisplayName);
                                    }
                                }
                            }
                        } else {
                            getController().getLog().info("Unknown display name choice " + string);
                        }
                        
                    }
                    
                }
            }
            if (updatedAccountVector.size() > 0) {
                cancelButton.setText("Cancel");
            } else {
                cancelButton.setText("Close");
            }
            getUpdateButton().setEnabled(updatedAccountVector.size() > 0);

            getAccountListBox().autoSizeAllColumns();
        }
    }

    /**
     * This method will update the Account if the newDisplayName does not
     * equal the account2.getDisplayName().  If an update is required
     * the Account will be added to the updateAccountVector.
     * 
     * @param account2
     * @param newDisplayName
     */
    private void updateDisplayName(Account account2, ICPCAccount icpcAccount, String newDisplayName) {
        if (!account2.getDisplayName().equals(newDisplayName)) {
            // TODO would be nice if Account had a deep clone, actually we just need
            // a deep clone of the string fields
            Account account = new Account(account2.getClientId(), account2.getPassword(), account2.getClientId().getSiteNumber());
            account.clearListAndLoadPermissions(account2.getPermissionList());
            account.setGroupId(account2.getGroupId());
            account.setAliasName(new String(account2.getAliasName()));
            account.setDisplayName(new String(account2.getDisplayName()));
            account.setExternalId(new String(account2.getExternalId()));
            account.setExternalName(new String(account2.getExternalName()));
            account.setLongSchoolName(icpcAccount.getLongSchoolName());
            account.setShortSchoolName(icpcAccount.getShortSchoolName());
            if (account.isSameAs(account2)) {
                account.setDisplayName(newDisplayName);
                updatedAccountVector.add(account);
                getAccountListBox().addRow(buildAccountRow(account2, newDisplayName));
            } else {
                getController().getLog().info("clone failed isSameAs test "+account+" vs "+account2);
            }
        }
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(600, 1500));
        this.setPreferredSize(new java.awt.Dimension(600, 1500));
        this.add(getDisplayNameFormatterPane(), java.awt.BorderLayout.NORTH);

        this.add(getAccountListBox(), java.awt.BorderLayout.CENTER);
    }

    @Override
    public String getPluginTitle() {
        return "ICPC Account Pane";
    }

    /**
     * This method initializes displayNameFormatterPane
     * 
     * @return edu.csus.ecs.pc2.ui.DisplayNameFormatterPane
     */
    private DisplayNameFormatterPane getDisplayNameFormatterPane() {
        if (displayNameFormatterPane == null) {
            displayNameFormatterPane = new DisplayNameFormatterPane();
            displayNameFormatterPane.addPropertyChangeListener(DisplayNameFormatterPane.CHANGE_PROPERTY, new PropertyChangeListenerImplementation());
            displayChoice = displayNameFormatterPane.getCurrentSelection().toString();
        }
        return displayNameFormatterPane;
    }

    /**
     * This method initializes accountListbox
     * 
     * @return com.ibm.webrunner.j2mclb.MultiColumnListbox
     */
    private MCLB getAccountListBox() {
        if (accountListBox == null) {
            accountListBox = new MCLB();
            accountListBox.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
            Object[] cols = {"Site", "Type", "Account Id", "Current Display Name", "New Display Name"};
            accountListBox.addColumns(cols);
        }
        return accountListBox;
    }

    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(flowLayout);
            buttonPanel.add(getUpdateButton(), null);
            buttonPanel.add(getCancelButton(), null);
        }
        return buttonPanel;
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
            // let changeDisplayName() changes to enabled as needed
            updateButton.setEnabled(false);
            updateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleUpdateButton();
                }
            });
        }
        return updateButton;
    }

    protected void handleUpdateButton() {
        if (updatedAccountVector.size() > 0) {
            Account[] accounts = updatedAccountVector.toArray(new Account[updatedAccountVector.size()]);
            if (accounts != null) {
                getController().updateAccounts(accounts);
            }
        }
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
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleCancelButton();
                }
            });
        }
        return cancelButton;
    }

    private void addWindowCloserListener() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (getParentFrame() != null) {
                    getParentFrame().addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            System.out.println("call handleCancel");
                            handleCancelButton();
                        }
                    });
                }
            }
        });
    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);
        addWindowCloserListener();
        getContest().addAccountListener(new AccountListenerImplementation());
        changeDisplayName(displayChoice);
    }

    protected void handleCancelButton() {
        if (updatedAccountVector.size() > 0) {
            // Something changed, are they sure ?
    
            int result = FrameUtilities.yesNoCancelDialog("Accounts modified, save changes?", "Confirm Choice");
            
            if (result == JOptionPane.YES_OPTION) {
                // pretend that hit update in that case
                handleUpdateButton();
            } else if (result == JOptionPane.NO_OPTION) {
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            } // cancel is a no-op
        } else {
            if (getParentFrame() != null) {
                getParentFrame().setVisible(false);
            }
        }
    }

    /**
     * @param icpcAccounts The icpcAccounts to set.
     */
    public void setIcpcAccounts(ICPCAccount[] icpcAccounts) {
        this.icpcAccounts = icpcAccounts;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
