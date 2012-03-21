package edu.csus.ecs.pc2.core.model;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * All JUnit tests for core.model package.
 * 
 * @author pc2@ecs.csus.edu
 * $version $Id$
 */

// $HeadURL$
public final class AllTests {

    private AllTests() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.csus.ecs.pc2.core.model");
        //$JUnit-BEGIN$
        suite.addTestSuite(InternalContestTest.class);
        suite.addTestSuite(LanguageAutoFillTest.class);
        suite.addTestSuite(AccountTest.class);
        suite.addTestSuite(SiteTest.class);
        suite.addTestSuite(ConfigurationIOTest.class);
        suite.addTestSuite(TimeFormatTest.class);
        suite.addTestSuite(JudgementNotificationTest.class);
        suite.addTestSuite(ContestInformationTest.class);
        suite.addTestSuite(ClarificationTest.class);
        suite.addTestSuite(PluralizeTest.class);
        suite.addTestSuite(ProblemTest.class);
        suite.addTestSuite(ContestTest.class);
        suite.addTestSuite(ProfileTest.class);
        suite.addTestSuite(AccountListTest.class);
        suite.addTestSuite(ProblemDataFilesTest.class);
        suite.addTestSuite(NotificationSettingTest.class);
        suite.addTestSuite(FilterTest.class);
        suite.addTestSuite(SerializedFileTest.class);
        suite.addTestSuite(RunUtilitiesTest.class);
        suite.addTestSuite(FilterFormatterTest.class);
        suite.addTestSuite(BalloonSettingsTest.class);
        suite.addTestSuite(ClarificationAnswerTest.class);
        suite.addTestSuite(DisplayTeamNameTest.class);
        //$JUnit-END$
        return suite;
    }

}
