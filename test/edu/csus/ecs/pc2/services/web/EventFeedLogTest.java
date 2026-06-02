// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.services.web;

import edu.csus.ecs.pc2.clics.API202306.EventFeedJSON;
import edu.csus.ecs.pc2.clics.API202306.EventFeedLog;
import edu.csus.ecs.pc2.clics.API202306.EventFeedType;
import edu.csus.ecs.pc2.clics.API202306.JSONTool;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 *
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class EventFeedLogTest extends AbstractTestCase {

    private static final int STANDARD_CONTEST_NON_COLLECTION_LINES = 277;

    private static final int STANDARD_CONTEST_ALL_COLLECTION_LINES = 7;

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

        EventFeedJSON efEventFeedJSON = new EventFeedJSON(new JSONTool(contest, null));
        efEventFeedJSON.setUseCollections(false);
        String events = efEventFeedJSON.createJSON(contest, null, null);

        eFeedLog.writeEvent(events);

        eFeedLog = new EventFeedLog(contest);
        assertEquals(expectedLineCount(contest, false, efEventFeedJSON), eFeedLog.getLogLines().length);

    }

    public void testWriteReadCollections() throws Exception {

        String outDir = getOutputDataDirectory(this.getName());
        ensureDirectory(outDir);
        //        startExplorer(outDir);
        EventFeedLog.setLogsDirectory(outDir);

        IInternalContest contest = new SampleContest().createStandardContest();

        EventFeedLog eFeedLog = new EventFeedLog(contest);

        assertEquals(0, eFeedLog.getLogLines().length);

        //        System.out.println("debug log file "+eFeedLog.getLogFileName());
        //        editFile ( eFeedLog.getLogFileName());

        EventFeedJSON efEventFeedJSON = new EventFeedJSON(new JSONTool(contest, null));
        efEventFeedJSON.setUseCollections(true);
        String events = efEventFeedJSON.createJSON(contest, null, null);

        eFeedLog.writeEvent(events);

        eFeedLog = new EventFeedLog(contest);
        assertEquals(expectedLineCount(contest, true, efEventFeedJSON), eFeedLog.getLogLines().length);

    }

    /**
     * Expected log line count respects {@link EventFeedJSON} collection settings from pc2v9.ini
     * (e.g. {@code clics.disable-collections=accounts}).
     */
    private int expectedLineCount(IInternalContest contest, boolean useCollections, EventFeedJSON efJson) {
        if (!useCollections) {
            return STANDARD_CONTEST_NON_COLLECTION_LINES;
        }
        int expected = STANDARD_CONTEST_ALL_COLLECTION_LINES;
        if (!efJson.isUseNotificationCollection(EventFeedType.ACCOUNTS)) {
            expected += contest.getAccounts().length - 1;
        }
        return expected;
    }

}
