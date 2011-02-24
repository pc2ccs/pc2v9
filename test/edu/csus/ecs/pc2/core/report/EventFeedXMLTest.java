package edu.csus.ecs.pc2.core.report;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.XMLMemento;

/**
 * Test Event Feed XML.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EventFeedXMLTest extends TestCase {

    private IInternalContest contest = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SampleContest sample = new SampleContest();
        contest = sample.createContest(1, 1, 22, 12, true);
    }

    public void testContestElement() throws Exception {

        // TODO tag: contest>
    }

    /**
     * Test <info> tag.
     * 
     * @throws Exception
     */
    public void testInfoElement() throws Exception {

        EventFeedXML eventFeedXML = new EventFeedXML();

        String xml = toContestXML(eventFeedXML.createInfoElement(contest, null));
        System.out.println(xml);

        System.out.println();

        contest.startContest(1);
        xml = toContestXML(eventFeedXML.createInfoElement(contest, null));
        System.out.println(xml);
    }

    public void testLanguageElement() throws Exception {

        // TODO tag: language>

        EventFeedXML eventFeedXML = new EventFeedXML();
        int idx = 1;
        for (Language language : contest.getLanguages()) {
            String xml = toContestXML(eventFeedXML.createElement(contest, language, idx));
            System.out.println(xml);
            idx++;
        }
    }

    /**
     * Create Contest XML.
     * 
     * @param memento
     * @return
     */
    private String toContestXML(XMLMemento mementoRoot) {
        try {
            return mementoRoot.saveToString();
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
            return null;
        }
    }

    public void testRegionElement() throws Exception {

        // TODO tag: region
    }

    public void testJudgementElement() throws Exception {

        EventFeedXML eventFeedXML = new EventFeedXML();
        for (Judgement judgement : contest.getJudgements()) {
            String xml = toContestXML(eventFeedXML.createElement(contest, judgement));
            System.out.println(xml);
        }
    }

    public void testProblemElement() throws Exception {

        // TODO tag: problem id="1" state="enabled">

        EventFeedXML eventFeedXML = new EventFeedXML();
        int idx = 1;
        for (Problem problem : contest.getProblems()) {
            String xml = toContestXML(eventFeedXML.createElement(contest, problem, idx));
            System.out.println(xml);
            idx++;
        }
    }

    public void testTeamElement() throws Exception {
        // TODO tag: team id="1" external-id="23412">

    }

    public void testClarElement() throws Exception {

        // TODO tag: clar id="1" team-id="0" problem-id="1">

        EventFeedXML eventFeedXML = new EventFeedXML();
        Clarification[] clarifications = contest.getClarifications();
        Arrays.sort(clarifications, new ClarificationComparator());

        for (Clarification clarification : clarifications) {
            String xml = toContestXML(eventFeedXML.createElement(contest, clarification));
            System.out.println(xml);
        }
    }

    public void testRunElement() throws Exception {

        // TODO tag: run id="1410" team-id="74" problem-id="4">
        EventFeedXML eventFeedXML = new EventFeedXML();
        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());

        for (Run run : runs) {
            String xml = toContestXML(eventFeedXML.createElement(contest, run));
            System.out.println(xml);
        }

    }

    public void testTestcaseElement() throws Exception {

        // TODO tag: testcase run-id="1">
    }

    public void testFinalizedElement() throws Exception {

        // TODO tag: finalized>
        EventFeedXML eventFeedXML = new EventFeedXML();
        String xml = eventFeedXML.createFinalizeXML(contest);
        System.out.println(xml);
    }

    public void testStartupElement() throws Exception {
        EventFeedXML eventFeedXML = new EventFeedXML();
        String xml = eventFeedXML.createStartupXML(contest);
        System.out.println(xml);
    }
}
