package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.JudgementNotification;
import edu.csus.ecs.pc2.core.model.NotificationSetting;

/**
 * edit Notification Settings pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class NotificationSettingPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -2435063240993379856L;

    private JPanel buttonPane = null;

    private JPanel mainPane = null;

    private JButton addButton = null;

    private JButton updateButton = null;

    private JButton cancelButton = null;

    @SuppressWarnings("unused")
    private boolean populatingGUI = true;

    private NotificationSetting notificationSetting;

    private JPanel preliminaryPane = null;

    private JPanel finalPane = null;

    private JCheckBox yesPrelimCheckBox = null;

    private JCheckBox noPrelimCheckBox = null;

    private JLabel yesPrelimCuttoffLabel = null;

    private JTextField yesPrelimCuttoffMinutesTextField = null;

    private JTextField noPrelimCuttoffMinutesTextField = null;

    private JLabel yesPrelimCutoffLabel = null;

    private JLabel prelimYesMinLabel = null;

    private JLabel prelimNoMinLabel = null;

    private JCheckBox yesFinalCheckBox = null;

    private JCheckBox noFinalCheckBox = null;

    private JLabel yesFinalCutoffLabel = null;

    private JLabel noFinalCutoffLabel = null;

    private JLabel finalYesMinLabel = null;

    private JLabel finalNoMinLabel = null;

    private JTextField yesFinalCuttoffMinutesTextField = null;

    private JTextField noFinalCuttoffMinutesTextField = null;

    private ClientId clientId = null;

    /**
     * This method initializes
     * 
     */
    public NotificationSettingPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(526,225));
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getMainPane(), java.awt.BorderLayout.CENTER);

    }

    @Override
    public String getPluginTitle() {
        return "Edit Notification Setting";
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
            buttonPane.add(getAddButton(), null);
            buttonPane.add(getUpdateButton(), null);
            buttonPane.add(getCancelButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes mainPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPane() {
        if (mainPane == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(2);
            gridLayout.setColumns(1);
            mainPane = new JPanel();
            mainPane.setLayout(gridLayout);
            mainPane.add(getPreliminaryPane(), null);
            mainPane.add(getFinalPane(), null);
        }
        return mainPane;
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
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addNotificationSetting();
                }
            });
        }
        return addButton;
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
            updateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateNotificationSetting();
                }
            });
        }
        return updateButton;
    }
    
    /**
     * Add a new Notification Settings.
     * 
     */
    protected void addNotificationSetting() {

        NotificationSetting newNotificationSetting = getNotificationSettingsFromFields();

//        dumpNotification(System.out, "addNotificationSetting", newNotificationSetting);

        ClientSettings clientSettings = getContest().getClientSettings(clientId);

        if (clientSettings == null) {
            clientSettings = new ClientSettings(clientId);
            clientSettings.setNotificationSetting(newNotificationSetting);
            getController().addNewClientSettings(clientSettings);
        } else {
            clientSettings.setNotificationSetting(newNotificationSetting);
            getController().updateClientSettings(clientSettings);
        }

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    /**
     * 
     *
     */
    protected void updateNotificationSetting() {

       NotificationSetting newNotificationSetting = getNotificationSettingsFromFields();
       
//        dumpNotification(System.out, "updateNotificationSetting", notificationSetting);
        
        ClientSettings clientSettings = getContest().getClientSettings(clientId);
        clientSettings.setNotificationSetting(newNotificationSetting);
        
        getController().updateClientSettings(clientSettings);
        
        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);
        
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
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleCancelButton();
                }
            });
        }
        return cancelButton;
    }

    public void setNotificationSetting(final NotificationSetting notificationSetting) {

        this.notificationSetting = notificationSetting;
        this.clientId = null;
        if (notificationSetting != null){
            this.clientId = notificationSetting.getClientId();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getAddButton().setVisible(false);
                getUpdateButton().setVisible(true);
                getAddButton().setEnabled(false);
                getUpdateButton().setEnabled(false);
                
                populateGUI(notificationSetting);
                enableUpdateButtons(false);
            }
        });
    }

    private void populateGUI(NotificationSetting notificationSetting2) {

        populatingGUI = true;
        
        if (getParentFrame() != null){
            getParentFrame().setTitle("Edit Notification Setting for " + NotificationsPane.getClientTitle(clientId));
        }
        
        if (notificationSetting2 != null) {

            JudgementNotification judgementNotification = notificationSetting2.getPreliminaryNotificationYes();
            getYesPrelimCheckBox().setSelected(judgementNotification.isNotificationSent());
            getYesPrelimCuttoffMinutesTextField().setText("" + judgementNotification.getCuttoffMinutes());

            judgementNotification = notificationSetting2.getPreliminaryNotificationNo();
            getNoPrelimCheckBox().setSelected(judgementNotification.isNotificationSent());
            getNoPrelimCuttoffMinutesTextField().setText("" + judgementNotification.getCuttoffMinutes());

            judgementNotification = notificationSetting2.getFinalNotificationYes();
            getYesFinalCheckBox().setSelected(judgementNotification.isNotificationSent());
            getYesFinalCuttoffMinutesTextField().setText("" + judgementNotification.getCuttoffMinutes());

            judgementNotification = notificationSetting2.getFinalNotificationNo();
            getNoFinalCheckBox().setSelected(judgementNotification.isNotificationSent());
            getNoFinalCuttoffMinutesTextField().setText("" + judgementNotification.getCuttoffMinutes());

            getAddButton().setVisible(false);
            getUpdateButton().setVisible(true);

        } else {

            JudgementNotification judgementNotification = new JudgementNotification(false, 0);

            getYesPrelimCheckBox().setSelected(judgementNotification.isNotificationSent());
            getYesPrelimCuttoffMinutesTextField().setText("" + judgementNotification.getCuttoffMinutes());

            getNoPrelimCheckBox().setSelected(judgementNotification.isNotificationSent());
            getNoPrelimCuttoffMinutesTextField().setText("" + judgementNotification.getCuttoffMinutes());

            getYesFinalCheckBox().setSelected(judgementNotification.isNotificationSent());
            getYesFinalCuttoffMinutesTextField().setText("" + judgementNotification.getCuttoffMinutes());

            getNoFinalCheckBox().setSelected(judgementNotification.isNotificationSent());
            getNoFinalCuttoffMinutesTextField().setText("" + judgementNotification.getCuttoffMinutes());

            getAddButton().setVisible(true);
            getUpdateButton().setVisible(false);
        }

        populatingGUI = false;
    }

    protected void enableUpdateButtons(boolean editedText) {
        if (editedText) {
            cancelButton.setText("Cancel");
        } else {
            cancelButton.setText("Close");
        }
        // only enable the visible one, we are either editing or adding not both
        if (getUpdateButton().isVisible()) {
            getUpdateButton().setEnabled(editedText);
        } else {
            getAddButton().setEnabled(editedText);
        }
    }

    /**
     * This method initializes preliminaryPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPreliminaryPane() {
        if (preliminaryPane == null) {
            prelimNoMinLabel = new JLabel();
            prelimNoMinLabel.setBounds(new java.awt.Rectangle(297,52,213,22));
            prelimNoMinLabel.setText("minutes before end of contest");
            prelimYesMinLabel = new JLabel();
            prelimYesMinLabel.setBounds(new java.awt.Rectangle(297,21,213,22));
            prelimYesMinLabel.setText("minutes before end of contest");
            yesPrelimCutoffLabel = new JLabel();
            yesPrelimCutoffLabel.setBounds(new java.awt.Rectangle(139,52,96,22));
            yesPrelimCutoffLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            yesPrelimCutoffLabel.setText("Cutoff Time");
            yesPrelimCuttoffLabel = new JLabel();
            yesPrelimCuttoffLabel.setBounds(new java.awt.Rectangle(139,21,96,22));
            yesPrelimCuttoffLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            yesPrelimCuttoffLabel.setText("Cutoff Time");
            preliminaryPane = new JPanel();
            preliminaryPane.setLayout(null);
            preliminaryPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Preliminary Judgements", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            preliminaryPane.add(getYesPrelimCheckBox(), null);
            preliminaryPane.add(getNoPrelimCheckBox(), null);
            preliminaryPane.add(yesPrelimCuttoffLabel, null);
            preliminaryPane.add(getYesPrelimCuttoffMinutesTextField(), null);
            preliminaryPane.add(getNoPrelimCuttoffMinutesTextField(), null);
            preliminaryPane.add(yesPrelimCutoffLabel, null);
            preliminaryPane.add(prelimYesMinLabel, null);
            preliminaryPane.add(prelimNoMinLabel, null);
        }
        return preliminaryPane;
    }

    /**
     * This method initializes finalPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getFinalPane() {
        if (finalPane == null) {
            finalNoMinLabel = new JLabel();
            finalNoMinLabel.setText("minutes before end of contest");
            finalNoMinLabel.setSize(new java.awt.Dimension(213, 22));
            finalNoMinLabel.setLocation(new java.awt.Point(297,52));
            finalYesMinLabel = new JLabel();
            finalYesMinLabel.setText("minutes before end of contest");
            finalYesMinLabel.setSize(new java.awt.Dimension(213, 22));
            finalYesMinLabel.setLocation(new java.awt.Point(297,22));
            noFinalCutoffLabel = new JLabel();
            noFinalCutoffLabel.setText("Cutoff Time");
            noFinalCutoffLabel.setSize(new java.awt.Dimension(96, 22));
            noFinalCutoffLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            noFinalCutoffLabel.setLocation(new java.awt.Point(139,52));
            yesFinalCutoffLabel = new JLabel();
            yesFinalCutoffLabel.setText("Cutoff Time");
            yesFinalCutoffLabel.setSize(new java.awt.Dimension(96, 22));
            yesFinalCutoffLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            yesFinalCutoffLabel.setLocation(new java.awt.Point(139,22));
            finalPane = new JPanel();
            finalPane.setLayout(null);
            finalPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Final Judgements", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            finalPane.add(getYesFinalCheckBox(), null);
            finalPane.add(getNoFinalCheckBox(), null);
            finalPane.add(yesFinalCutoffLabel, null);
            finalPane.add(noFinalCutoffLabel, null);
            finalPane.add(finalYesMinLabel, null);
            finalPane.add(finalNoMinLabel, null);
            finalPane.add(getYesFinalCuttoffMinutesTextField(), null);
            finalPane.add(getNoFinalCuttoffMinutesTextField(), null);
        }
        return finalPane;
    }

    /**
     * This method initializes yesPrelimJButton
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getYesPrelimCheckBox() {
        if (yesPrelimCheckBox == null) {
            yesPrelimCheckBox = new JCheckBox();
            yesPrelimCheckBox.setBounds(new java.awt.Rectangle(17,21,117,22));
            yesPrelimCheckBox.setText("Send Yes");
            yesPrelimCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return yesPrelimCheckBox;
    }

    /**
     * This method initializes noPrelimJButton
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getNoPrelimCheckBox() {
        if (noPrelimCheckBox == null) {
            noPrelimCheckBox = new JCheckBox();
            noPrelimCheckBox.setText("Send No");
            noPrelimCheckBox.setSize(new java.awt.Dimension(117,22));
            noPrelimCheckBox.setLocation(new java.awt.Point(17,52));
            noPrelimCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return noPrelimCheckBox;
    }

    /**
     * This method initializes yesPrelimCuttoffMinutesTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getYesPrelimCuttoffMinutesTextField() {
        if (yesPrelimCuttoffMinutesTextField == null) {
            yesPrelimCuttoffMinutesTextField = new JTextField();
            yesPrelimCuttoffMinutesTextField.setBounds(new java.awt.Rectangle(246,22,45,21));
            yesPrelimCuttoffMinutesTextField.setDocument(new IntegerDocument());
            yesPrelimCuttoffMinutesTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return yesPrelimCuttoffMinutesTextField;
    }

    /**
     * This method initializes noPrelimCuttoffMinutesTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getNoPrelimCuttoffMinutesTextField() {
        if (noPrelimCuttoffMinutesTextField == null) {
            noPrelimCuttoffMinutesTextField = new JTextField();
            noPrelimCuttoffMinutesTextField.setBounds(new java.awt.Rectangle(246,52,45,23));
            noPrelimCuttoffMinutesTextField.setDocument(new IntegerDocument());
            noPrelimCuttoffMinutesTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });


        }
        return noPrelimCuttoffMinutesTextField;
    }

    /**
     * This method initializes yesFinalCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getYesFinalCheckBox() {
        if (yesFinalCheckBox == null) {
            yesFinalCheckBox = new JCheckBox();
            yesFinalCheckBox.setText("Send Yes");
            yesFinalCheckBox.setLocation(new java.awt.Point(17, 22));
            yesFinalCheckBox.setSize(new java.awt.Dimension(117,22));
            yesFinalCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return yesFinalCheckBox;
    }

    /**
     * This method initializes jCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getNoFinalCheckBox() {
        if (noFinalCheckBox == null) {
            noFinalCheckBox = new JCheckBox();
            noFinalCheckBox.setText("Send No");
            noFinalCheckBox.setLocation(new java.awt.Point(17, 52));
            noFinalCheckBox.setSize(new java.awt.Dimension(117,22));
            noFinalCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return noFinalCheckBox;
    }

    /**
     * This method initializes textfield1
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getYesFinalCuttoffMinutesTextField() {
        if (yesFinalCuttoffMinutesTextField == null) {
            yesFinalCuttoffMinutesTextField = new JTextField();
            yesFinalCuttoffMinutesTextField.setLocation(new java.awt.Point(246,23));
            yesFinalCuttoffMinutesTextField.setSize(new java.awt.Dimension(45, 21));
            yesFinalCuttoffMinutesTextField.setDocument(new IntegerDocument());
            yesFinalCuttoffMinutesTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });

        }
        return yesFinalCuttoffMinutesTextField;
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getNoFinalCuttoffMinutesTextField() {
        if (noFinalCuttoffMinutesTextField == null) {
            noFinalCuttoffMinutesTextField = new JTextField();
            noFinalCuttoffMinutesTextField.setLocation(new java.awt.Point(246,53));
            noFinalCuttoffMinutesTextField.setPreferredSize(new java.awt.Dimension(4, 20));
            noFinalCuttoffMinutesTextField.setSize(new java.awt.Dimension(45, 21));
            noFinalCuttoffMinutesTextField.setDocument(new IntegerDocument());
            noFinalCuttoffMinutesTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });


        }
        return noFinalCuttoffMinutesTextField;
    }

    protected void handleCancelButton() {

        if (getAddButton().isEnabled() || getUpdateButton().isEnabled()) {

            // Something changed, are they sure ?

            int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Notification Settings modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                if (getAddButton().isEnabled()) {
                    addNotificationSetting();
                } else {
                    updateNotificationSetting();
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

    private NotificationSetting getNotificationSettingsFromFields() {

        NotificationSetting checkNotificationSetting =new NotificationSetting(clientId);

        JudgementNotification judgementNotification = null;

        judgementNotification = new JudgementNotification(getYesPrelimCheckBox().isSelected(), getIntegerValue(getYesPrelimCuttoffMinutesTextField().getText()));
        checkNotificationSetting.setPreliminaryNotificationYes(judgementNotification);

        judgementNotification = new JudgementNotification(getNoPrelimCheckBox().isSelected(), getIntegerValue(getNoPrelimCuttoffMinutesTextField().getText()));
        checkNotificationSetting.setPreliminaryNotificationNo(judgementNotification);

        judgementNotification = new JudgementNotification(getYesFinalCheckBox().isSelected(), getIntegerValue(getYesFinalCuttoffMinutesTextField().getText()));
        checkNotificationSetting.setFinalNotificationYes(judgementNotification);

        judgementNotification = new JudgementNotification(getNoFinalCheckBox().isSelected(), getIntegerValue(getNoFinalCuttoffMinutesTextField().getText()));
        checkNotificationSetting.setFinalNotificationNo(judgementNotification);

        return checkNotificationSetting;
    }

    @SuppressWarnings("unused")
    private void dumpNotification(PrintStream out, String message, NotificationSetting notificationSetting2) {

        System.out.println();
        System.out.println(message);
        JudgementNotification judgementNotification = null;

        judgementNotification = notificationSetting2.getPreliminaryNotificationYes();
        System.out.println(" Prelim Yes send " + judgementNotification.isNotificationSent() + " cuttoff at " + judgementNotification.getCuttoffMinutes());

        judgementNotification = notificationSetting2.getPreliminaryNotificationNo();
        System.out.println(" Prelim No  send " + judgementNotification.isNotificationSent() + " cuttoff at " + judgementNotification.getCuttoffMinutes());

        judgementNotification = notificationSetting2.getFinalNotificationYes();
        System.out.println(" Final  Yes send " + judgementNotification.isNotificationSent() + " cuttoff at " + judgementNotification.getCuttoffMinutes());

        judgementNotification = notificationSetting2.getFinalNotificationNo();
        System.out.println(" Final  No  send " + judgementNotification.isNotificationSent() + " cuttoff at " + judgementNotification.getCuttoffMinutes());
    }

    private int getIntegerValue(String text) {
        try {
            return Integer.parseInt(text);
        } catch (Exception e) {
            return 0;
        }
    }

    public ClientId getClientId() {
        return clientId;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
        notificationSetting = null;
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getAddButton().setVisible(true);
                getUpdateButton().setVisible(false);
                getAddButton().setEnabled(false);
                getUpdateButton().setEnabled(false);
                populateGUI(null);
                enableUpdateButtons(false);
            }
        });
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

        if (notificationSetting != null) {

            NotificationSetting checkNotificationSetting = getNotificationSettingsFromFields();
            enableButton = ! checkNotificationSetting.isSameAs(notificationSetting);
            
//            dumpNotification(System.out, "before", notificationSetting);
//            dumpNotification(System.out, "after ", checkNotificationSetting);
            
            System.out.println("debug enableUpdateButton "+enableButton);

        } else {
            enableButton = true;
        }

        enableUpdateButtons(enableButton);
    }


} // @jve:decl-index=0:visual-constraint="10,10"
