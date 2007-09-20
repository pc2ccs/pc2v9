package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.BalloonSettings;

/**
 * Maintain a list of BalloonSettings.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class BalloonSettingsList extends ElementList {

    /**
     * 
     */
    private static final long serialVersionUID = 4812013610166409491L;

    /**
     * 
     * @param balloonSettings
     *            {@link BalloonSettings} to be added.
     */
    public void add(BalloonSettings balloonSettings) {
        super.add(balloonSettings);
    }

    /**
     * Return list of BalloonSettings.
     * 
     * @return list of {@link BalloonSettings}.
     */
    @SuppressWarnings("unchecked")
    public BalloonSettings[] getList() {
        BalloonSettings[] theList = new BalloonSettings[size()];

        if (theList.length == 0) {
            return theList;
        }

        return (BalloonSettings[]) values().toArray(new BalloonSettings[size()]);
    }
}
