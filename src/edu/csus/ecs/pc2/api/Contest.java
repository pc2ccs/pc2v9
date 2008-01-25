package edu.csus.ecs.pc2.api;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;

/**
 * Contest data/information.
 * 
 * Contains access to contest data and suppport for events that listen for changes in contest data.
 * <P>
 * 
 * 
 * 
 * @see Controller
 * @see edu.csus.ecs.pc2.api.ConfigurationUpdateEvent
 * @see edu.csus.ecs.pc2.api.RunUpdateEvent
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class Contest implements IContest {

    public boolean isLoggedIn() {
        // TODO Auto-generated method stub
        return false;
    }

    public String getTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    public ClientId getClientId() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getSiteTitle(int siteNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getJudgementTitle(ElementId elementId) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getProblemTitle(ElementId elementId) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLanguageTitle(ElementId elementId) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getContestTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getSiteTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    public ElementId[] getLanguageIds() {
        // TODO Auto-generated method stub
        return null;
    }

    public ElementId[] getProblemIds() {
        // TODO Auto-generated method stub
        return null;
    }

    public ElementId[] getJudgementIds() {
        // TODO Auto-generated method stub
        return null;
    }

    public void addRunListener(IRunEventListener runEventListener) {
        // TODO Auto-generated method stub

    }

    public void removeRunListener(IRunEventListener runEventListener) {
        // TODO Auto-generated method stub

    }

    public void addContestUpdateConfigurationListener(IConfigurationUpdateListener contestUpdateConfigurationListener) {
        // TODO Auto-generated method stub

    }

    public void removeContestUpdateConfigurationListener(IConfigurationUpdateListener contestUpdateConfigurationListener) {
        // TODO Auto-generated method stub

    }

    public ElementId[] getRunIds() {
        // TODO Auto-generated method stub
        return null;
    }

    public Run getRun(ElementId elementId) {
        // TODO Auto-generated method stub
        return null;
    }

}
