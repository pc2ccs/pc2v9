/**
 * 
 */
package edu.csus.ecs.pc2.core.model;

/**
 * @author PC2
 *
 */
public class Group implements IElementObject {
    /**
     * 
     */
    private static final long serialVersionUID = -9039235537777735642L;
    public static final String SVN_ID = "$Id$";
    /**
     * Unique id, version and site number.
     * 
     */
    private ElementId elementId;
    
    private int groupId;
    private String groupTitle;
    private ElementId site;
    
    public ElementId getElementId() {
        return elementId;
    }

    public int getSiteNumber() {
        return elementId.getSiteNumber();
    }

    public void setSiteNumber(int siteNumber) {
        elementId.setSiteNumber(siteNumber);
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    /**
     * @return Returns the site.
     */
    public ElementId getSite() {
        return site;
    }

    /**
     * Should be set to the site responsible for this group.
     * 
     * @param site The site to set.
     */
    public void setSite(ElementId site) {
        this.site = site;
    }

    public String toString() {
        return groupTitle;
    }

    /**
     * @param groupTitle
     */
    public Group(String groupTitle) {
        super();
        // TODO Auto-generated constructor stub
        this.groupTitle = groupTitle;
    }
}
