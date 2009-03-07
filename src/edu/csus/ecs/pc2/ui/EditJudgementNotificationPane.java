package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import edu.csus.ecs.pc2.core.model.NotificationSetting;

/**
 * A single set of notification settings.
 * 
 *  For each problem pane there is one of these to edit the notifications.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditJudgementNotificationPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -6654853645826212444L;

    private JPanel topPane = null;

    private JPanel centerPanel = null;

    private NotificationPane preliminaryNotificationPane = null;

    private NotificationPane finalNotificationPane = null;
    
    private NotificationSetting notificationSetting = null;  //  @jve:decl-index=0:


    /**
     * This method initializes
     * 
     */
    public EditJudgementNotificationPane(NotificationSetting inNotificationSetting) {
        super();
        notificationSetting = inNotificationSetting;
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(441, 164));
        this.add(getTopPane(), BorderLayout.NORTH);
        this.add(getCenterPanel(), BorderLayout.CENTER);
        
        setNotificationSetting(notificationSetting);
    }

    @Override
    public String getPluginTitle() {
        return "Edit Judgement Notifications";
    }

    /**
     * This method initializes topPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTopPane() {
        if (topPane == null) {
            topPane = new JPanel();
            topPane.setLayout(new GridBagLayout());
        }
        return topPane;
    }

    /**
     * This method initializes centerPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPanel() {
        if (centerPanel == null) {
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 1;
            gridBagConstraints1.gridy = 1;
            centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(getCenterPanel(), BoxLayout.Y_AXIS));
            centerPanel.add(getPreliminaryNotificationPane(), null);
            centerPanel.add(getFinalNotificationPane(), null);
        }
        return centerPanel;
    }

    /**
     * This method initializes preliminaryNotificationPane
     * 
     * @return edu.csus.ecs.pc2.ui.NotificationPane
     */
    private NotificationPane getPreliminaryNotificationPane() {
        if (preliminaryNotificationPane == null) {
            preliminaryNotificationPane = new NotificationPane();
            preliminaryNotificationPane.setBorder(BorderFactory.createTitledBorder(null, "Preliminary Judgement", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog",
                    Font.BOLD, 12), new Color(51, 51, 51)));
        }
        return preliminaryNotificationPane;
    }

    /**
     * This method initializes finalNotificationPane
     * 
     * @return edu.csus.ecs.pc2.ui.NotificationPane
     */
    private NotificationPane getFinalNotificationPane() {
        if (finalNotificationPane == null) {
            finalNotificationPane = new NotificationPane();
            finalNotificationPane.setBorder(BorderFactory.createTitledBorder(null, "Final Judgements", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD,
                    12), new Color(51, 51, 51)));
        }
        return finalNotificationPane;
    }
    
    /**
     * Get notification settings from fields (as entered).
     * 
     * These settings are different than the input NotificationSettings to this class.
     * These settings are derived from the UI fields/model and can be compared
     * to the input to this class NotificationSettings (retrieved using {@link #getNotificationSetting()})
     * 
     * @return new NotificationSettings from UI fields
     */
    public NotificationSetting getNotificationSettingFromFields() {
        
        NotificationSetting newNotificationSetting = new NotificationSetting(notificationSetting.getElementId());

        newNotificationSetting.setPreliminaryNotificationYes(getPreliminaryNotificationPane().getYesJudgementNotificationFromFields());
        newNotificationSetting.setPreliminaryNotificationNo(getPreliminaryNotificationPane().getNoJudgementNotificationFromFields());
        
        newNotificationSetting.setFinalNotificationYes(getFinalNotificationPane().getYesJudgementNotificationFromFields());
        newNotificationSetting.setFinalNotificationNo(getFinalNotificationPane().getNoJudgementNotificationFromFields());
        
        return newNotificationSetting;
    }



    /**
     * Get input notification settings.
     * 
     * @see #getNotificationSettingFromFields()
     * 
     */
    public NotificationSetting getNotificationSetting() {
        return notificationSetting;
    }

    /**
     * Set the notification settings and populate the UI.
     * 
     * @param inNotificationSetting
     */
    public void setNotificationSetting(NotificationSetting inNotificationSetting) {
        this.notificationSetting = inNotificationSetting;
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI(notificationSetting);
            }
        });
    }

    protected void populateGUI(NotificationSetting notificationSetting2) {
        
        getPreliminaryNotificationPane().setJudgementNotifications(notificationSetting2.getPreliminaryNotificationYes(),
                notificationSetting2.getPreliminaryNotificationNo());
        
        getFinalNotificationPane().setJudgementNotifications(notificationSetting2.getFinalNotificationYes(),
                notificationSetting2.getFinalNotificationNo());
        
        // TODO Auto-generated method stub
        
    }

    
} // @jve:decl-index=0:visual-constraint="10,10"
