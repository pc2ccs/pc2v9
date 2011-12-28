package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.PlaybackInfo;

/**
 * Maintain a list of {@link PlaybackInfo}s.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class PlaybackInfoList extends ElementList {

    /**
     * 
     */
    private static final long serialVersionUID = 2756113155870274201L;

    public void add(PlaybackInfo playbackInfo) {
        super.add(playbackInfo);
    }

    public PlaybackInfo[] getList() {
        PlaybackInfo[] theList = new PlaybackInfo[size()];

        if (theList.length == 0) {
            return theList;
        }

        theList = (PlaybackInfo[]) values().toArray(new PlaybackInfo[size()]);
        return theList;
    }
}
