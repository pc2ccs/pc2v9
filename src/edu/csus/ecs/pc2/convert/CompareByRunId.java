package edu.csus.ecs.pc2.convert;

import java.util.Comparator;

/**
 * Compare by run id numerically.
 * 
 * @author $Author$ Douglas A. Lane &lt;laned@ecs.csus.edu&gt;
 * @version $Id$
 */
public class CompareByRunId implements Comparator<EventFeedRun> {

    @Override
    public int compare(EventFeedRun run1, EventFeedRun run2) {
        int id1 = Integer.parseInt(run1.getId());
        int id2 = Integer.parseInt(run2.getId());
        return id1 - id2;
    }
}
