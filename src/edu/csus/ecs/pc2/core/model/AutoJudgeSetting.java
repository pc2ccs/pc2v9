package edu.csus.ecs.pc2.core.model;

/**
 * Settings for an individual auto judge
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AutoJudgeSetting implements IElementObject {

    /**
     * 
     */
    private static final long serialVersionUID = 3940441995410704659L;

    private boolean active;

    private Filter problemFilter = new Filter();

    private ClientId clientId;

    private ElementId elementId;

    private String displayName;

    public AutoJudgeSetting(String displayName) {
        super();
        this.displayName = displayName;
        elementId = new ElementId(displayName);
        setSiteNumber(1);
    }

    public ElementId getElementId() {
        return elementId;
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }

    public int getSiteNumber() {
        return elementId.getSiteNumber();
    }

    public void setSiteNumber(int siteNumber) {
        elementId.setSiteNumber(siteNumber);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Filter getProblemFilter() {
        return problemFilter;
    }

    public void setProblemFilter(Filter problemFilter) {
        this.problemFilter = problemFilter;
    }

    public ClientId getClientId() {
        setSiteNumber(1);

        return clientId;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
        setSiteNumber(clientId.getSiteNumber());
    }

    public String getDisplayName() {
        return displayName;
    }
}
