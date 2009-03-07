package edu.csus.ecs.pc2.core.model;

/**
 * A single set of judgement notification settings.
 * 
 * Contains four notification settings:
 * <ol>
 * <li>Setting for Final Yes judgement
 * <li>Setting for Final No judgment
 * <li>Setting for Preliminary Yes judgement
 * <li>Setting for Preliminary No judgment
 * </ol>
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class NotificationSetting implements IElementObject {

    private JudgementNotification finalNotificationYes = new JudgementNotification();

    private JudgementNotification finalNotificationNo = new JudgementNotification();

    private JudgementNotification preliminaryNotificationYes = new JudgementNotification();

    private JudgementNotification preliminaryNotificationNo = new JudgementNotification();
    
    /**
     * 
     */
    private static final long serialVersionUID = 585748435205196728L;

    private ElementId elementId;

    public NotificationSetting(ElementId inElementId) {
        super();
        this.elementId = inElementId;
    }

    public ElementId getElementId() {
        return elementId;
    }

    public JudgementNotification getFinalNotificationNo() {
        return finalNotificationNo;
    }

    public JudgementNotification getFinalNotificationYes() {
        return finalNotificationYes;
    }

    public JudgementNotification getPreliminaryNotificationNo() {
        return preliminaryNotificationNo;
    }

    public JudgementNotification getPreliminaryNotificationYes() {
        return preliminaryNotificationYes;
    }

    public int getSiteNumber() {
        return elementId.getSiteNumber();
    }

    public void setFinalNotificationNo(JudgementNotification finalNotificationNo) {
        this.finalNotificationNo = finalNotificationNo;
    }

    public void setFinalNotificationYes(JudgementNotification finalNotificationYes) {
        this.finalNotificationYes = finalNotificationYes;
    }

    public void setPreliminaryNotificationNo(JudgementNotification preliminaryNotificationNo) {
        this.preliminaryNotificationNo = preliminaryNotificationNo;
    }

    public void setPreliminaryNotificationYes(JudgementNotification preliminaryNotificationYes) {
        this.preliminaryNotificationYes = preliminaryNotificationYes;
    }

    public void setSiteNumber(int siteNumber) {
        elementId.setSiteNumber(siteNumber);
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }
    
    public boolean isSameAs (NotificationSetting notificationSettingIn){
        return 
        notificationSettingIn.getFinalNotificationYes().isSameAs(getFinalNotificationYes())
        && notificationSettingIn.getFinalNotificationNo().isSameAs(getFinalNotificationNo()) 
        && notificationSettingIn.getPreliminaryNotificationYes().isSameAs(getPreliminaryNotificationYes()) 
        && notificationSettingIn.getPreliminaryNotificationNo().isSameAs(getPreliminaryNotificationNo());
    }
}
