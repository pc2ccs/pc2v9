package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.JudgementNotification;
import edu.csus.ecs.pc2.core.model.NotificationSetting;
import edu.csus.ecs.pc2.core.model.ClientType.Type;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class NotificationsPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -5837850150714301616L;

    private JPanel languageButtonPane = null;

    private MCLB notificationListBox = null;

    private JButton addButton = null;

    private JButton editButton = null;

    private JPanel messagePane = null;

    private JLabel messageLabel = null;

    private EditNotificationSettingFrame editNotificationSettingFrame = null;

    private Log log;

    /**
     * This method initializes
     * 
     */
    public NotificationsPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(564, 229));
        this.add(getNotificationListBox(), java.awt.BorderLayout.CENTER);
        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getNotificationButtonPane(), java.awt.BorderLayout.SOUTH);

        editNotificationSettingFrame = new EditNotificationSettingFrame();

    }

    @Override
    public String getPluginTitle() {
        return "Notifications Pane";
    }

    /**
     * This method initializes languageButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getNotificationButtonPane() {
        if (languageButtonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            languageButtonPane = new JPanel();
            languageButtonPane.setLayout(flowLayout);
            languageButtonPane.setPreferredSize(new java.awt.Dimension(35, 35));
            languageButtonPane.add(getAddButton(), null);
            languageButtonPane.add(getEditButton(), null);
        }
        return languageButtonPane;
    }

    /**
     * This method initializes notificationListBox
     * 
     * @return edu.csus.ecs.pc2.core.log.MCLB
     */
    private MCLB getNotificationListBox() {
        if (notificationListBox == null) {
            notificationListBox = new MCLB();

            Object[] cols = { "Client", "Final", "Preliminary" };
            notificationListBox.addColumns(cols);

            /**
             * No sorting at this time, the only way to know what order the languages are is to NOT sort them. Later we can add a sorter per NotificationDisplayList somehow.
             */

            // // Sorters
            // HeapSorter sorter = new HeapSorter();
            // // HeapSorter numericStringSorter = new HeapSorter();
            // // numericStringSorter.setComparator(new NumericStringComparator());
            //
            // // Display Name
            // notificationListBox.setColumnSorter(0, sorter, 1);
            // // Compiler Command Line
            // notificationListBox.setColumnSorter(1, sorter, 2);
            // // Exe Name
            // notificationListBox.setColumnSorter(2, sorter, 3);
            // // Execute Command Line
            // notificationListBox.setColumnSorter(3, sorter, 4);
            notificationListBox.autoSizeAllColumns();

        }
        return notificationListBox;
    }

    public void updateNotificationRow(final ClientId clientId) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildNotificationRow(clientId);
                int rowNumber = notificationListBox.getIndexByKey(clientId);
                if (rowNumber == -1) {
                    notificationListBox.addRow(objects, clientId);
                } else {
                    notificationListBox.replaceRow(objects, rowNumber);
                }
                notificationListBox.autoSizeAllColumns();
                // notificationListBox.sort();
            }
        });
    }

    protected Object[] buildNotificationRow(ClientId clientId) {

        buildNotificationRow(clientId);

        // Object[] cols = { "Client", "Final", "Preliminary"};

        int numberColumns = notificationListBox.getColumnCount();
        Object[] c = new String[numberColumns];

        c[0] = clientId.getName();

        // TODO populate notification settings from clientSettings
        NotificationSetting notificationSetting = null;

        c[1] = "";
        c[2] = "";

        if (notificationSetting != null) {
            c[1] = getNotificationSettingsString(notificationSetting.getFinalNotificationYes(), notificationSetting.getFinalNotificationNo());
            c[2] = getNotificationSettingsString(notificationSetting.getPreliminaryNotificationYes(), notificationSetting.getPreliminaryNotificationNo());
        }

        return c;
    }

    protected String getNotificationSettingsString(JudgementNotification notificationYes, JudgementNotification notificationNo) {

        String s = "";

        if (notificationYes.isNotificationSent()) {
            s = "Cutoff Yes at " + notificationYes.getCuttoffMinutes();
        }
        if (notificationNo.isNotificationSent()) {
            s = "Cutoff No at " + notificationNo.getCuttoffMinutes();
        }

        return s;
    }

    private Account[] getAccountList(Type type) {
        Vector<Account> vector = getContest().getAccounts(type);
        return (Account[]) vector.toArray(new Account[vector.size()]);
    }

    private void reloadListBox() {
        notificationListBox.removeAllRows();

        // Get All Scoreboard clients
        for (Account account : getAccountList(Type.SCOREBOARD)) {
            addNotificationRow(account.getClientId());
        }

        // Get All API clients
        for (Account account : getAccountList(Type.SPECTATOR)) {
            addNotificationRow(account.getClientId());
        }

        // Set generic team ( TEAM 0 site 0 )
        ClientId clientId = new ClientId(0, Type.TEAM, 0);
        addNotificationRow(clientId);
    }

    private void addNotificationRow(ClientId clientId) {

        Object[] objects = buildNotificationRow(clientId);
        notificationListBox.addRow(objects, clientId.getTripletKey());
        notificationListBox.autoSizeAllColumns();
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        editNotificationSettingFrame.setContestAndController(inContest, inController);

        // TODO add ClientSetting listener
        // getContest().addNotificationListener(new NotificationListenerImplementation());

        log = getController().getLog();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                reloadListBox();
            }
        });
    }

    // TODO add listener for client Settings changes
    // /**
    // *
    // *
    // * @author pc2@ecs.csus.edu
    // */
    // public class NotificationListenerImplementation implements INotificationListener {
    //
    // public void languageAdded(NotificationEvent event) {
    // updateNotificationRow(event.getNotification());
    // }
    //
    // public void languageChanged(NotificationEvent event) {
    // updateNotificationRow(event.getNotification());
    // }
    //
    // public void languageRemoved(NotificationEvent event) {
    // // TODO Auto-generated method stub
    // }
    //
    // }

    /**
     * This method initializes addButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddButton() {
        if (addButton == null) {
            addButton = new JButton();
            addButton.setText("Add");
            addButton.setToolTipText("Add a new Notification definition");
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addNewNotification();
                }
            });
        }
        return addButton;
    }

    protected void addNewNotification() {
        // TODO remove addNewNotification, there is no add
        // editNotificationSettingFrame.setNotification(null);
        editNotificationSettingFrame.setVisible(true);
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
            editButton.setToolTipText("Edit existing Notification definition");
            editButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    editSelectedNotification();
                }
            });
        }
        return editButton;
    }

    protected void editSelectedNotification() {

        int selectedIndex = notificationListBox.getSelectedIndex();
        if (selectedIndex == -1) {
            showMessage("Select a notification to edit");
            return;
        }

        try {
            ClientId clientId = (ClientId) notificationListBox.getKeys()[selectedIndex];
            NotificationSetting notificationToEdit = getNotificationSettings (clientId);

            editNotificationSettingFrame.setNotificationSetting (notificationToEdit);
            editNotificationSettingFrame.setVisible(true);
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
            showMessage("Unable to edit notification, check log");
        }
    }

    private NotificationSetting getNotificationSettings(ClientId clientId) {
        
        // TODO get Notification from clientsettings
        return null;
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
     * show message to user
     * 
     * @param string
     */
    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
            }
        });

    }
} // @jve:decl-index=0:visual-constraint="10,10"
