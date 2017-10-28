package edu.csus.ecs.pc2.core.model;

/**
 * Group or Region information for Account.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class Group implements IElementObject {
    /**
     * 
     */
    private static final long serialVersionUID = -9039235537777735642L;
    /**
     * Unique id, version and site number.
     * 
     */
    private ElementId elementId;
    
    private int groupId;
    private String displayName;
    private ElementId site;
    private boolean displayOnScoreboard = true;
    
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

    /**
     * External Id.
     * 
     * CMS assigned id for group/region.
     * 
     * @return id for region/group.
     */
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String groupTitle) {
        this.displayName = groupTitle;
    }

    /**
     * Returns what PC2 site is responsible for this group.
     * 
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
        return displayName;
    }

    /**
     * @param displayName
     */
    public Group(String displayName) {
        super();
        this.displayName = displayName;
        elementId = new ElementId("group");
        setSiteNumber(0);
    }
    
    @Override
    public int hashCode() {
        // use elementId to be consistent with equals()
        return elementId.hashCode();
    }

    /**
     * @return Returns the displayOnScoreboard.
     */
    public boolean isDisplayOnScoreboard() {
        return displayOnScoreboard;
    }

    /**
     * @param displayOnScoreboard The displayOnScoreboard to set.
     */
    public void setDisplayOnScoreboard(boolean displayOnScoreboard) {
        this.displayOnScoreboard = displayOnScoreboard;
    }

    /**
     * Compares string, handles if either string is null.
     * 
     * @param s1
     * @param s2
     * @return true if both null or equal, false otherwise
     */
    private boolean stringSame (String s1, String s2){
        // SOMEDAY  use  return StringUtilities.stringSame(s1,s2);
        if (s1 == null && s2 == null) {
            return true;
        }
        
        if (s1 == null && s2 != null){
            return false;
        }
        
        return s1.equals(s2);
    }

    public boolean isSameAs(Group newGroup) {
        try {
            if (newGroup == null){
                return false;
            }
            if (! stringSame(displayName, newGroup.getDisplayName())){
                return false;
            }
            if (getSiteNumber() != newGroup.getSiteNumber()){
                return false;
            }
            if (groupId != newGroup.getGroupId()) {
                return false;
            }
            if (isDisplayOnScoreboard() != newGroup.isDisplayOnScoreboard()) {
                return false;
            }
            if (site != null && newGroup.getSite() != null) {
                if (!newGroup.getSite().equals(site)) {
                    return false;
                }
            } else {
                // 1 or both are null
                if (!(newGroup.getSite() == null && site == null)) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            // SOMEDAY properly Log this as a debug log exception
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Update fields from another group.
     * 
     * clone certain settings.
     * 
     * @param newGroup
     */
    public void updateFrom(Group newGroup) {
        
        groupId = newGroup.getGroupId();
        displayName = newGroup.getDisplayName();
        site = newGroup.getSite();
        displayOnScoreboard = newGroup.displayOnScoreboard;
    }
}
