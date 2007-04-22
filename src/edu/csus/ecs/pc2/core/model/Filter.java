package edu.csus.ecs.pc2.core.model;

/**
 * A filter for runs, clients, etc.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class Filter {

    // TODO code filter

    private boolean thisSiteOnly;

    private int siteNumber = 0;

    private boolean isThisSite(ISubmission submission) {
        return siteNumber == submission.getSiteNumber() || siteNumber == 0;
    }

    private boolean isThisSite(ClientId clientId) {
        return siteNumber == clientId.getSiteNumber() || siteNumber == 0;
    }

    public boolean matches(Run run) {
        return isThisSite(run);
    }

    public boolean matches(Clarification clarification) {
        return isThisSite(clarification);
    }

    public boolean matches(ClientId clientId) {
        return isThisSite(clientId);
    }

    public boolean isThisSiteOnly() {
        return thisSiteOnly;
    }

    public void setThisSiteOnly(boolean thisSiteOnly) {
        this.thisSiteOnly = thisSiteOnly;
    }

    public int isSiteNumber() {
        return siteNumber;
    }

    public void setSiteNumber(int siteNumber) {
        this.siteNumber = siteNumber;
    }

}
