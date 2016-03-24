package edu.csus.ecs.pc2.core.transport;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.csus.ecs.pc2.core.exception.RunUnavailableException;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationAnswer;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IEventFeedRunnable;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.services.eventFeed.EventFeeder;

/**
 * Unit Test.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EventFeederTest extends AbstractTestCase {

    /**
     * Testing using EventFeedXML or EventFeedXML2013?.
     */
    private boolean testing2013EventFeed = true;
    
    protected static final String CONTEST_END_TAG = "</contest>";

    private SampleContest sampleContest = new SampleContest();

    public void testFeed() throws Exception {

        SampleContest sample = new SampleContest();

        IInternalContest contest = sample.createStandardContest();

        addSampleRuns(contest);
        
        sample.assignSampleGroups(contest,"USA", "Sacramento");
        
        int numberClars = 9;
        addSampleClarifications(contest, numberClars);
        
        assertEquals("Expecting clarifications to exist ", numberClars, contest.getClarifications().length);

        IEventFeedRunnable runn = new IEventFeedRunnable() {

            public void send(String xmlString) {
                if (xmlString.indexOf(CONTEST_END_TAG) == -1) {
                    xmlString += CONTEST_END_TAG;
                }
                testEventFeed(xmlString);
            }
        };

        EventFeeder eventFeeder = new EventFeeder(contest, runn);
        eventFeeder.run();
    }

    private void addSampleClarifications(IInternalContest contest, int count) throws IOException, ClassNotFoundException, FileSecurityException {

        boolean answered = false;
        ClientId admin = contest.getAccounts(Type.ADMINISTRATOR).firstElement().getClientId();

        for (int i = 0; i < count; i++) {
            String question = "This is question " + (i + 1) + " in a series of " + count + " questions";
            Problem problemId = getRandomProblem(contest);
            ClientId submitter = getRandomTeam(contest).getClientId();
            Clarification clarification = new Clarification(submitter, problemId, question);
            if (answered) {
                ClarificationAnswer clarAnswer = new ClarificationAnswer("I got nothing, good luck and Godspeed", admin, false, contest.getContestTime());
                clarification.addAnswer(clarAnswer);
            }
            contest.addClarification(clarification);
            answered = ! answered; // answer every other clar.
        }

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
        
        if (testing2013EventFeed){
            
            tagName = "<run>";
            assertCount("Expecting tag " + tagName, 18, tagName, xmlString);
            
            tagName = "<team>";
            assertCount("Expecting tag " + tagName, 147, tagName, xmlString);
            
            tagName = "<region>";
            assertCount("Expecting tag " + tagName, 121, tagName, xmlString);
            
            tagName = "<language>";
            assertCount("Expecting tag " + tagName, 24, tagName, xmlString);
            
            tagName = "<clarification>";
            assertCount("Expecting tag " + tagName, 9, tagName, xmlString);
            
        } else {
            
            // assertCount("Expecting tag "+tagName, 4, tagName, tagName+tagName+tagName+tagName);
            // assertEquals("Expecting xml length ",26021, xmlString.length());
            assertCount("Expecting tag " + tagName, 18, tagName, xmlString);
            

        }

    }

    public void assertValidXML(String xmlString) throws SAXException, IOException, ParserConfigurationException {

        assertFalse("Expecting XML, not null", xmlString == null);
        assertFalse("Expecting XML, not empty string ", xmlString.trim().length() == 0);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        documentBuilder.parse(new InputSource(new StringReader(xmlString)));
    }
    
    public void testJudgementSequence() throws Exception {
        
        IInternalContest contest = sampleContest.createStandardContest();

        addSampleRuns(contest);

        IEventFeedRunnable stubRunnable = new IEventFeedRunnable() {

            public void send(String xmlString) {
                xmlString.trim(); // used trim to fool the code style 
            }
        };

        EventFeeder eventFeeder = new EventFeeder(contest, stubRunnable);    
        
        int sequence = 1;
        Judgement[] judgements = contest.getJudgements();
        for (Judgement judgement : judgements) {
            int value = eventFeeder.getJudgementNumber(contest, judgement);
            assertEquals("Expecting same sequence/id for judgement", sequence, value);
            sequence ++;
        }
        
    }

}
