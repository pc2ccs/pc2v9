package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.BalloonSettings;

/**
 * BalloonSettings Comparator sort by site number.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class BalloonSettingsComparatorbySite implements Comparator<BalloonSettings>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -419557289338217178L;

    public static final String SVN_ID = "$Id$";

    public int compare(BalloonSettings balloonSettings, BalloonSettings balloonSettings2) {
        return balloonSettings.getSiteNumber() - balloonSettings2.getSiteNumber();
    }

}
