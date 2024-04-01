// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.util;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.SampleContest;

/**
 * Unit test.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class JSONToolTest extends AbstractTestCase {

    /**
     * Test judgement not starting with "No - " github issue #950
     *
     * @throws Exception
     */
    public void testIssue950failed() throws Exception {
        SampleContest sampleContest = new SampleContest();
        IInternalContest contest = sampleContest.createContest(2, 2, 12, 12, true);
        String outputTestDirectory = getOutputDataDirectory();
        IInternalController controller = sampleContest.createController(contest, outputTestDirectory, true, false); // creates StaticLog instance

        Judgement judgementJE = new Judgement("JE", "JE");
        contest.addJudgement(judgementJE);
        JSONTool json_tool = new JSONTool(contest, controller);
        json_tool.convertToJSON(judgementJE);
    }
}
