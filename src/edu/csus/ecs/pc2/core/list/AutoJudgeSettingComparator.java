package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.AutoJudgeSetting;

/**
 * AutoJudgeSetting Comparator, Order by the ClientId.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class AutoJudgeSettingComparator implements Comparator<AutoJudgeSetting>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5639854309384046534L;

    private ClientIdComparator comparator = new ClientIdComparator();

    /**
     * 
     */
    public int compare(AutoJudgeSetting auto1, AutoJudgeSetting auto2) {
        return comparator.compare(auto1.getClientId(), auto2.getClientId());
    }

}
