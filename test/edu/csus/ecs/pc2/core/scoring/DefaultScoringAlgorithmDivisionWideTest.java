// Copyright (C) 1989-2026 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.scoring;

import java.io.StringReader;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformation.ShowAllTeamsOnScoreboard;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.standings.ContestStandings;
import edu.csus.ecs.pc2.core.standings.ScoreboardUtilities;
import edu.csus.ecs.pc2.core.standings.ScoringGroup;
import edu.csus.ecs.pc2.core.standings.StandingsHeader;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Tests for division-wide / showAllTeams attributes in DSA standings XML.
 */
public class DefaultScoringAlgorithmDivisionWideTest extends AbstractTestCase {

    private Log log = createLog(getName());

    public void testStandingsXmlEmitsDivisionWideAndShowAllTeams() throws Exception {
        IInternalContest contest = createPacNWStyleContest(true);

        DefaultScoringAlgorithm dsa = new DefaultScoringAlgorithm();
        String xml = dsa.getStandings(contest, new Properties(), log);
        assertNotNull(xml);
        assertTrue(xml.length() > 0);

        Document doc = parseXml(xml);
        Element header = (Element) doc.getElementsByTagName("standingsHeader").item(0);
        assertNotNull(header);
        assertEquals("false", header.getAttribute("showAllTeams"));

        NodeList groups = header.getElementsByTagName("group");
        assertEquals(3, groups.getLength());

        Element d1 = findGroupByTitle(groups, "D1");
        assertEquals("1", d1.getAttribute("id"));
        assertEquals("1200", d1.getAttribute("externalId"));
        assertEquals("true", d1.getAttribute("divisionWide"));
        assertTrue(d1.hasAttribute("teamCount"));

        Element californiaD1 = findGroupByTitle(groups, "California - D1");
        assertEquals("3", californiaD1.getAttribute("id"));
        assertEquals("false", californiaD1.getAttribute("divisionWide"));

        // Hidden California site group must not appear in groupList
        assertNull(findGroupByTitle(groups, "California"));
    }

    public void testShowAllTeamsTrueWhenNoDivisionWide() throws Exception {
        IInternalContest contest = createPacNWStyleContest(false);

        DefaultScoringAlgorithm dsa = new DefaultScoringAlgorithm();
        String xml = dsa.getStandings(contest, new Properties(), log);
        Element header = (Element) parseXml(xml).getElementsByTagName("standingsHeader").item(0);
        assertEquals("true", header.getAttribute("showAllTeams"));

        Element d1 = findGroupByTitle(header.getElementsByTagName("group"), "D1");
        assertEquals("false", d1.getAttribute("divisionWide"));
    }

    public void testShowAllTeamsOverrideTrue() throws Exception {
        IInternalContest contest = createPacNWStyleContest(true);
        ContestInformation info = contest.getContestInformation();
        info.setShowAllTeamsOnScoreboard(ShowAllTeamsOnScoreboard.TRUE);
        contest.updateContestInformation(info);

        DefaultScoringAlgorithm dsa = new DefaultScoringAlgorithm();
        String xml = dsa.getStandings(contest, new Properties(), log);
        Element header = (Element) parseXml(xml).getElementsByTagName("standingsHeader").item(0);
        assertEquals("true", header.getAttribute("showAllTeams"));
    }

    public void testPojoDeserializesNewAttributes() throws Exception {
        IInternalContest contest = createPacNWStyleContest(true);
        DefaultScoringAlgorithm dsa = new DefaultScoringAlgorithm();
        String xml = dsa.getStandings(contest, new Properties(), log);

        ContestStandings standings = ScoreboardUtilities.createContestStandings(xml);
        StandingsHeader header = standings.getStandingsHeader();
        assertNotNull(header);
        assertEquals("false", header.getShowAllTeams());

        ScoringGroup[] scoringGroups = header.getGrouplist().getGroups().toArray(new ScoringGroup[0]);
        assertNotNull(scoringGroups);
        assertTrue(scoringGroups.length >= 1);

        ScoringGroup first = scoringGroups[0];
        assertNotNull(first.getDivisionWide());
        assertNotNull(first.getTeamCount());
        assertNotNull(first.getId());
        assertNotNull(first.getExternalId());
    }

    private IInternalContest createPacNWStyleContest(boolean markDivisionWide) {
        IInternalContest contest = new SampleContest().createContest(1, 1, 2, 0, true);

        Group d1 = new Group("D1");
        d1.setGroupId(1200);
        d1.setDisplayOnScoreboard(true);
        d1.setDivisionWideScoreboard(markDivisionWide);
        contest.addGroup(d1);

        Group d2 = new Group("D2");
        d2.setGroupId(1201);
        d2.setDisplayOnScoreboard(true);
        d2.setDivisionWideScoreboard(markDivisionWide);
        contest.addGroup(d2);

        Group california = new Group("California");
        california.setGroupId(1202);
        california.setDisplayOnScoreboard(false);
        contest.addGroup(california);

        Group californiaD1 = new Group("California - D1");
        californiaD1.setGroupId(400);
        californiaD1.setDisplayOnScoreboard(true);
        contest.addGroup(californiaD1);

        return contest;
    }

    private Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xml)));
    }

    private Element findGroupByTitle(NodeList groups, String title) {
        for (int i = 0; i < groups.getLength(); i++) {
            Node node = groups.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (title.equals(element.getAttribute("title"))) {
                    return element;
                }
            }
        }
        return null;
    }
}
