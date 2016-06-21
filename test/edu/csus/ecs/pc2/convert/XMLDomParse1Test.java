package edu.csus.ecs.pc2.convert;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit Test.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class XMLDomParse1Test extends AbstractTestCase {

    public void testBasicParseandConvert() throws Exception
	{
		String ifn = SampleCDP.getEventFeedFilename();

		assertFileExists(ifn);

		XMLDomParse1 parse1 = new XMLDomParse1();
		Document document = parse1.create(ifn);

		assertNotNull(document);

		String path = "/contest/run/*";
		NodeList nodes = parse1.getNodes(document, path);

		Properties[] runPropertyList = parse1.create(nodes, EventFeedRun.ID_TAG_NAME);

		List<EventFeedRun> runs = EventFeedRun.toRuns(runPropertyList, true);

		Collections.sort(runs, new ComparatorById());

		// print runs
//        for (EventFeedRun eventFeedRun : runs) {
//            System.out.println(eventFeedRun.getId() + " " + eventFeedRun.getResult() + " " + //
//                    eventFeedRun.getTime() + " " + eventFeedRun.getTimestamp());
//        }
		
		assertEquals("Expecting runs ", 1494, runs.size());
		assertEquals("Expecting first runid ", "20", runs.get(0).getId());
        assertEquals("Expecting last  runid ", "1518", runs.get(runs.size()-1).getId());
        
        runs = EventFeedRun.toRuns(runPropertyList, false);
        assertEquals("Expecting runs ", 2988, runs.size());
        
	}

}
