package edu.csus.ecs.pc2.convert;

import java.util.Comparator;

/**
 * Compare EventFeedRun by run id numerically.
 *
 * @author $Author$ Douglas A. Lane <laned@ecs.csus.edu>
 * @version $Id$
 */
public class ComparatorById implements Comparator<EventFeedRun> {

    @Override
    public int compare(EventFeedRun run1, EventFeedRun run2) {
        int id1 = Integer.parseInt(run1.getId());
        int id2 = Integer.parseInt(run2.getId());
        return id1 - id2;
    }
}
