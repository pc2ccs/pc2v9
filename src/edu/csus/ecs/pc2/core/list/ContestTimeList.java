package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IElementObject;

/**
 * Maintain a list of ContestTime.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ContestTimeList extends BaseElementList {

    /**
     * 
     */
    private static final long serialVersionUID = -1573135566498698327L;

    public static final String SVN_ID = "$Id$";

    /**
     * Add contest time into list.
     * @param contestTime
     */
    public void add(ContestTime contestTime) {
        if (get(contestTime) != null) {
            throw new IllegalArgumentException("Attemped to add Contest time for " + contestTime.getSiteNumber()
                    + " site already added");
        }
        super.add(contestTime);
    }

    @SuppressWarnings("unchecked")
    public ContestTime[] getList() {
        return (ContestTime[]) values().toArray(new ContestTime[size()]);
    }
    
    public ContestTime get (int siteNumber){
        for (ContestTime contestTime : getList()){
            if (contestTime.getSiteNumber() == siteNumber) {
                return contestTime;
            }
        }
        return null;
    }
    
    public ContestTime get (ElementId elementId){
        for (ContestTime contestTime : getList()){
            if (contestTime.getElementId().equals(elementId)){
                return contestTime;
            }
        }
        return null;
    }

    @Override
    public String getKey(IElementObject elementObject) {
        ContestTime contestTime = (ContestTime) elementObject;
        return new Long(contestTime.getSiteNumber()).toString();
    }

}
