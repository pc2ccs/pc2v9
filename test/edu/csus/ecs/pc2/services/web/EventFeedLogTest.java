package edu.csus.ecs.pc2.services.web;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.services.core.EventFeedJSON;

/**
 * Unit test.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class EventFeedLogTest extends AbstractTestCase {

    public void testWriteRead() throws Exception {

        String outDir = getOutputDataDirectory(this.getName());
        ensureDirectory(outDir);
        //        startExplorer(outDir);
        EventFeedLog.setLogsDirectory(outDir);

        IInternalContest contest = new SampleContest().createStandardContest();

        EventFeedLog eFeedLog = new EventFeedLog(contest);

        assertEquals(0, eFeedLog.getLogLines().length);

        //        System.out.println("debug log file "+eFeedLog.getLogFileName());
        //        editFile ( eFeedLog.getLogFileName());

        EventFeedJSON efEventFeedJSON = new EventFeedJSON(contest);
        String events = efEventFeedJSON.createJSON(contest, null, null);

        eFeedLog.writeEvent(events);

        eFeedLog = new EventFeedLog(contest);
        assertEquals(142, eFeedLog.getLogLines().length);

    }

}
