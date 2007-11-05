package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.Comparator;
import com.ibm.webrunner.j2mclb.util.HeapSorter;
import com.ibm.webrunner.j2mclb.util.NumericStringComparator;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.AccountNameComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientSettingsEvent;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IClientSettingsListener;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * A grid of auto judging settings.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class AutoJudgesPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 36368747620026978L;

    private MCLB autoJudgeListBox = null;

    private JPanel buttonsPane = null;

    private JPanel statusPanel = null;

    private JLabel messageLabel = null;

    private JButton editButton = null;

    private EditAutoJudgeSettingFrame editAutoJudgeSettingFrame = new EditAutoJudgeSettingFrame();

    private Log log;

    /**
     * This method initializes
     * 
     */
    public AutoJudgesPane() {
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
        this.add(getAutoJudgeListBox(), java.awt.BorderLayout.CENTER);

    }

    @Override
    public String getPluginTitle() {
        return "Auto Judge List Panel";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);

        log = getController().getLog();

        editAutoJudgeSettingFrame.setContestAndController(inContest, inController);

        getContest().addClientSettingsListener(new ClientSettingsListenerImplementation());
        
        getContest().addAccountListener(new AccountListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateGUIperPermissions();
                reloadAutoJudgeList();
            }
        });

        showMessage("");
    }

    protected void updateGUIperPermissions() {
        // TODO code updateGUIperPermissions

    }

    protected void reloadAutoJudgeList() {

        getAutoJudgeListBox().removeAllRows();

        for (Account account : getContest().getAccounts(ClientType.Type.JUDGE)) {
            
            ClientSettings clientSettings = getContest().getClientSettings(account.getClientId());
            if (clientSettings != null) {
                updateAutoJudgeRow(clientSettings);

            } else {
                updateAutoJudgeRow(account.getClientId());
            }
        }
    }
    
    /**
     * MCLB account name comparator.
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    protected class AccountNameComparatorMCLB implements Comparator {

        /**
         * 
         */
        private static final long serialVersionUID = 109218643807182860L;
        private AccountNameComparator accountNameComparator = new AccountNameComparator();

        public int compare(Object arg0, Object arg1) {
            return accountNameComparator.compare((String) arg0, (String) arg1);
        }
    }

    /**
     * This method initializes AutoJudgeListBox
     * 
     * @return edu.csus.ecs.pc2.ui.MCLB
     */
    private MCLB getAutoJudgeListBox() {
        if (autoJudgeListBox == null) {
            autoJudgeListBox = new MCLB();

            autoJudgeListBox.add(getStatusPanel(), java.awt.BorderLayout.NORTH);
            Object[] cols = { "Site", "Judge", "On", "Problems" };
            autoJudgeListBox.addColumns(cols);
            
            // Sorters
            HeapSorter sorter = new HeapSorter();
            HeapSorter numericStringSorter = new HeapSorter();
            numericStringSorter.setComparator(new NumericStringComparator());
            HeapSorter accountNameSorter = new HeapSorter();
            accountNameSorter.setComparator(new AccountNameComparatorMCLB());

            // Site
            autoJudgeListBox.setColumnSorter(0, numericStringSorter, 1);

            // Judge name
            autoJudgeListBox.setColumnSorter(1, accountNameSorter, 2);

            // On (Auto Judge enabled)
            autoJudgeListBox.setColumnSorter(2, sorter, 3);

            // Problem List
            autoJudgeListBox.setColumnSorter(3, sorter, 4);

            cols = null;
        }
        return autoJudgeListBox;
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
            buttonsPane.add(getEditButton(), null);
        }
        return buttonsPane;
    }

    protected void addAutoJudgeSetting() {

    }

    /**
     * This method initializes statusPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getStatusPanel() {
        if (statusPanel == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            statusPanel = new JPanel();
            statusPanel.setLayout(new BorderLayout());
            statusPanel.setPreferredSize(new java.awt.Dimension(20, 20));
            statusPanel.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return statusPanel;
    }

    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
                messageLabel.setToolTipText(string);
            }
        });

    }

    private void updateAutoJudgeRow(final ClientSettings clientSettings) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildAutoJudgeRow(clientSettings);
                int rowNumber = autoJudgeListBox.getIndexByKey(clientSettings.getClientId());
                if (rowNumber == -1) {
                    autoJudgeListBox.addRow(objects, clientSettings.getClientId());
                } else {
                    autoJudgeListBox.replaceRow(objects, rowNumber);
                }
                autoJudgeListBox.autoSizeAllColumns();
            }
        });
    }
    
    private void updateAutoJudgeRow(ClientId clientId2) {
        ClientSettings clientSettings = new ClientSettings(clientId2);
        updateAutoJudgeRow(clientSettings);
    }

    private Object[] buildAutoJudgeRow(ClientSettings clientSettings) {

//        Object[] cols = { "Site", "Judge", "On", "Problems" };


        try {
            int cols = autoJudgeListBox.getColumnCount();
            Object[] s = new String[cols];

            s[0] = new Integer(clientSettings.getClientId().getSiteNumber()).toString();
            s[1] = clientSettings.getClientId().getName();
            s[2] = Utilities.yesNoString(clientSettings.isAutoJudging());
            s[3] = getProblemlist(clientSettings.getAutoJudgeFilter());
            
            return s;
        } catch (Exception exception) {
            StaticLog.getLog().log(Log.INFO, "Exception in buildRunRow()", exception);
            exception.printStackTrace(System.err); // TODO remove line
        }
        return null;
    }

    /**
     * Return a list of comma delimited problem names.
     * 
     * <P>
     * returns "none selected" if no problems in filter <br>
     * returns "none active selected" if problems in the filter are all deactivated <br>
     * 
     * @param filter
     * @return
     */
    private String getProblemlist(Filter filter) {
        ElementId[] elementIds = filter.getProblemIdList();

        if (elementIds.length == 0) {
            return "(none selected)";
        }

        StringBuffer stringBuffer = new StringBuffer();
        for (Problem problem : getContest().getProblems()) {
            for (ElementId elementId : elementIds) {
                if (problem.getElementId().equals(elementId)) {
                    stringBuffer.append(problem.getDisplayName());
                    stringBuffer.append(", ");
                }
            }
        }

        if (stringBuffer.length() > 3) {
            // stringBuffer.length() - 2 used to strip off trailing ", "
            return new String(stringBuffer).substring(0, stringBuffer.length() - 2);
        } else {
            return "(none active selected)";
        }
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
            editButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    editSelectedAutoJudgeRow();
                }
            });
        }
        return editButton;
    }

    protected void editSelectedAutoJudgeRow() {

        int selectedIndex = autoJudgeListBox.getSelectedIndex();
        if (selectedIndex == -1) {
            showMessage("Select a Judge to edit");
            return;
        }

        try {
            ClientId clientId = (ClientId) autoJudgeListBox.getKeys()[selectedIndex];
            ClientSettings clientSettings = getContest().getClientSettings(clientId);
            
            if (clientSettings == null) {
                clientSettings = new ClientSettings(clientId);
            }

            editAutoJudgeSettingFrame.setClientSetting(clientId, clientSettings);
            editAutoJudgeSettingFrame.setVisible(true);
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to edit client settings, check log");
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    public class ClientSettingsListenerImplementation implements IClientSettingsListener {

        public void clientSettingsAdded(final ClientSettingsEvent event) {
            clientSettingsChanged(event);
        }

        protected boolean isJudge(ClientSettings clientSettings) {
            return clientSettings.getClientId().getClientType() == ClientType.Type.JUDGE;
        }

        public void clientSettingsChanged(final ClientSettingsEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ClientSettings clientSettings = event.getClientSettings();
                    if (isJudge(clientSettings)) {
                        updateAutoJudgeRow(clientSettings);
                    }
                }
            });
        }

        public void clientSettingsRemoved(ClientSettingsEvent event) {
            clientSettingsChanged(event);
        }
    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public class AccountListenerImplementation implements IAccountListener {

        protected boolean isJudge(ClientId clientId) {
            return clientId.getClientType() == ClientType.Type.JUDGE;
        }

        public void accountAdded(final AccountEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ClientId clientId = event.getAccount().getClientId();
                    if (isJudge(clientId)) {
                        updateAutoJudgeRow(clientId);
                    }
                }
            });
        }

        public void accountModified(final AccountEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ClientId clientId = event.getAccount().getClientId();
                    if (isJudge(clientId)) {
                        updateAutoJudgeRow(clientId);
                    }
                }
            });
        }

        public void accountsAdded(final AccountEvent accountEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    for(Account account : accountEvent.getAccounts()) {
                        ClientId clientId = account.getClientId();
                        if (isJudge(clientId)) {
                            updateAutoJudgeRow(clientId);
                        }
                    }
                }
            });
        }

        public void accountsModified(final AccountEvent accountEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    for(Account account : accountEvent.getAccounts()) {
                        ClientId clientId = account.getClientId();
                        if (isJudge(clientId)) {
                            updateAutoJudgeRow(clientId);
                        }
                    }
                }
            });
        }
        
    }

} // @jve:decl-index=0:visual-constraint="10,10"

