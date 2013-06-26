package edu.csus.ecs.pc2.core.transport;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.csus.ecs.pc2.core.exception.RunUnavailableException;
import edu.csus.ecs.pc2.core.model.IEventFeedRunnable;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EventFeederTest extends AbstractTestCase {

    protected static final String CONTEST_END_TAG = "</contest>";

    private SampleContest sampleContest = new SampleContest();

    public void testFrozenFeed() throws Exception {

        SampleContest sample = new SampleContest();

        IInternalContest contest = sample.createStandardContest();

        addSampleRuns(contest);

        IEventFeedRunnable runn = new IEventFeedRunnable() {

            public void send(String xmlString) {
                if (xmlString.indexOf(CONTEST_END_TAG) == -1) {
                    xmlString += CONTEST_END_TAG;
                }
                testEventFeed(xmlString);
                testFrozenFeed(xmlString);
            }
        };

        EventFeeder eventFeeder = new EventFeeder(contest, runn);
        eventFeeder.run();
    }

    private void addSampleRuns(IInternalContest contest) throws IOException, ClassNotFoundException, FileSecurityException, RunUnavailableException {

        String[] runsData = { "1,1,A,1,No,Yes", // 20 (a No before first yes)
                "2,1,A,3,Yes,Yes", // 3 (first yes counts Minute points but never Run Penalty points)
                "3,1,A,5,No,Yes", // zero -- after Yes
                "4,1,A,7,Yes,Yes", // zero -- after Yes
                "5,1,A,9,No,Yes", // zero -- after Yes
                "6,1,B,11,No,Yes", // zero -- not solved
                "7,1,B,13,No,Yes", // zero -- not solved
                "8,2,A,30,Yes,Yes", // 30 (minute points; no Run points on first Yes)
                "9,2,B,35,No,Yes", // zero -- not solved
                "10,2,B,40,No,Yes", // zero -- not solved
                "11,2,B,45,No,Yes", // zero -- not solved
                "12,2,B,50,No,Yes", // zero -- not solved
                "13,2,B,55,No,Yes", // zero -- not solved

                "14,9,A,55,Yes,Yes", //
                "15,9,B,155,Yes,Yes", //
                "16,9,C,255,Yes,Yes", //
                "17,9,D,355,Yes,Yes", //
                "18,9,E,455,Yes,Yes", //

        };

        for (String runInfoLine : runsData) {
            sampleContest.addARun((InternalContest) contest, runInfoLine);
        }
    }

    protected void testFrozenFeed(String xmlString) {

        try {
            assertValidXML(xmlString);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    protected void testEventFeed(String xmlString) {

        try {
            assertValidXML(xmlString);
        } catch (Exception e) {
            System.out.println(xmlString);
            e.printStackTrace();
            fail(e.getMessage());
        }

        String tagName = "<run ";

        // assertCount("Expecting tag "+tagName, 4, tagName, tagName+tagName+tagName+tagName);
        // assertEquals("Expecting xml length ",26021, xmlString.length());
        assertCount("Expecting tag " + tagName, 18, tagName, xmlString);
    }

    public void assertValidXML(String xmlString) throws SAXException, IOException, ParserConfigurationException {

        assertFalse("Expecting XML, not null", xmlString == null);
        assertFalse("Expecting XML, not empty string ", xmlString.trim().length() == 0);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        documentBuilder.parse(new InputSource(new StringReader(xmlString)));
    }

}
