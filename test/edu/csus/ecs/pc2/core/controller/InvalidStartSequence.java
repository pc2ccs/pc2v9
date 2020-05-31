// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.controller;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.SiteTest;
import edu.csus.ecs.pc2.core.model.ClientType.Type;

/**
 * Tests to insure InternalController.start must be called before InternalController.login
 *
 */
public class InvalidStartSequence extends TestCase {

    private IInternalContest modelOne;

    private InternalController controllerOne;

    public InvalidStartSequence() {
        super();
        // TODO Auto-generated constructor stub
    }

    public InvalidStartSequence(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    protected void setUp() throws Exception {
        super.setUp();

        modelOne = new InternalContest();
        initializeModel(modelOne);

        Site siteOne = SiteTest.createSite(1, "Site ONE", null, 0);
        modelOne.addSite(siteOne);

        // Start site 1
        controllerOne = new InternalController(modelOne);
        controllerOne.setContactingRemoteServer(false);
        controllerOne.setUsingMainUI(false);
    }
    public void testInvalidStartSequence() {
        boolean caughtException = false;
        try {
            controllerOne.login("site1", "site1");
        } catch (SecurityException e) {
            caughtException = true;
        } catch (Exception e) {
            System.out.println("Caught an unexpect exception");
            e.printStackTrace();
        }
        assertTrue("loginRequireStart", caughtException);
    }
    
    public void initializeModel(IInternalContest contest) {

        String[] languages = { "Java", "C", "APL" };
        String[] problems = { "Sumit", "Quadrangles", "Routing" };
        String[] judgements = { "No no", "No no no", "No - judges are confused" };

        for (String langName : languages) {
            Language language = new Language(langName);
            modelOne.addLanguage(language);
        }

        for (String probName : problems) {
            Problem problem = new Problem(probName);
            modelOne.addProblem(problem);
        }

        Judgement judgementYes = new Judgement("Yes");
        modelOne.addJudgement(judgementYes);

        for (String judgementName : judgements) {
            modelOne.addJudgement(new Judgement(judgementName));
        }

        contest.generateNewAccounts(ClientType.Type.TEAM.toString(), 10, true);
        contest.generateNewAccounts(ClientType.Type.JUDGE.toString(), 5, true);

        assertTrue("Insure generate of 10 teams", modelOne.getAccounts(Type.TEAM).size() == 10);
        assertTrue("Insure generate of 5 teams", modelOne.getAccounts(Type.JUDGE).size() == 5);
    }

}
