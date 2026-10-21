// Copyright (C) 1989-2026 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.imports.ccs;

import java.io.File;
import java.util.Map;

import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformation.ShowAllTeamsOnScoreboard;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests for the system.pc2.yaml division-groups overlay.
 */
public class DivisionScoreboardYamlLoaderTest extends AbstractTestCase {

    private DivisionScoreboardYamlLoader loader = new DivisionScoreboardYamlLoader();

    private String getTestDataDirectory() {
        return getRootInputTestDataDirectory() + File.separator + "DivisionScoreboardYamlLoaderTest";
    }

    public void testApplyDivisionScoreboardOverlay() throws Exception {
        InternalContest contest = createContestWithGroups();

        File overlayFile = new File(getTestDataDirectory(), "system.pc2.yaml");
        Map<String, Object> content = new ContestSnakeYAMLLoader().loadYaml(overlayFile.getAbsolutePath());
        loader.applyOverlay(contest, content, overlayFile.getAbsolutePath());

        Group d1 = findGroupByName(contest, "D1");
        assertTrue("D1 on scoreboard", d1.isDisplayOnScoreboard());
        assertTrue("D1 division-wide", d1.isDivisionWideScoreboard());

        Group california = findGroupByName(contest, "California");
        assertFalse("California not on scoreboard", california.isDisplayOnScoreboard());

        Group europe = findGroupByName(contest, "Europe");
        assertFalse("Europe not on scoreboard", europe.isDisplayOnScoreboard());
        assertFalse("Europe not division-wide", europe.isDivisionWideScoreboard());
    }

    public void testMissingDivisionGroupsKeyIsNoOp() {
        InternalContest contest = createContestWithGroups();

        loader.applyOverlay(contest, new java.util.HashMap<String, Object>(), "system.pc2.yaml");

        Group d1 = findGroupByName(contest, "D1");
        assertTrue("Default display on scoreboard unchanged", d1.isDisplayOnScoreboard());
        assertFalse("Default division-wide unchanged", d1.isDivisionWideScoreboard());
    }

    public void testUnknownGroupDoesNotFailLoad() throws Exception {
        InternalContest contest = createContestWithGroups();

        File overlayFile = new File(getTestDataDirectory(), "unknown-group.yaml");
        Map<String, Object> content = new ContestSnakeYAMLLoader().loadYaml(overlayFile.getAbsolutePath());
        loader.applyOverlay(contest, content, overlayFile.getAbsolutePath());

        Group d1 = findGroupByName(contest, "D1");
        assertTrue("Existing groups unchanged", d1.isDisplayOnScoreboard());
    }

    public void testShowAllTeamsOnScoreboardEnumParsing() {
        assertEquals(ShowAllTeamsOnScoreboard.AUTO, ShowAllTeamsOnScoreboard.fromYamlValue("auto"));
        assertEquals(ShowAllTeamsOnScoreboard.TRUE, ShowAllTeamsOnScoreboard.fromYamlValue("TRUE"));
        assertEquals(ShowAllTeamsOnScoreboard.FALSE, ShowAllTeamsOnScoreboard.fromYamlValue("false"));
    }

    private InternalContest createContestWithGroups() {
        InternalContest contest = new InternalContest();

        Group d1 = new Group("D1");
        d1.setGroupId(901);
        contest.addGroup(d1);

        Group d2 = new Group("D2");
        d2.setGroupId(902);
        contest.addGroup(d2);

        Group california = new Group("California");
        california.setGroupId(100);
        contest.addGroup(california);

        Group europe = new Group("Europe");
        europe.setGroupId(201);
        contest.addGroup(europe);

        return contest;
    }

    private Group findGroupByName(InternalContest contest, String name) {
        for (Group group : contest.getGroups()) {
            if (name.equals(group.getDisplayName())) {
                return group;
            }
        }
        fail("Group not found: " + name);
        return null;
    }
}
