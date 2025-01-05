// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.services.web;

import edu.csus.ecs.pc2.clics.API202306.EventFeedJSON;
import edu.csus.ecs.pc2.clics.API202306.EventFeedLog;
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
        assertEquals(143, eFeedLog.getLogLines().length);

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
        assertEquals(6, eFeedLog.getLogLines().length);

    }

}
