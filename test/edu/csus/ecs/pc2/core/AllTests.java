package edu.csus.ecs.pc2.core;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test all JUnits in core package.
 * 
 * @author pc2@ecs.csus.edu
 */

//$HeadURL$
public final class AllTests {

    private AllTests() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.csus.ecs.pc2.core");
        //$JUnit-BEGIN$
        suite.addTestSuite(PacketHandlerTest.class);
        suite.addTestSuite(ContestLoaderTest.class);
        suite.addTestSuite(ClientIdTest.class);
        suite.addTestSuite(BalloonSettingsTest.class);
        suite.addTestSuite(UtilitiesTest.class);
        suite.addTestSuite(IniFileTest.class);
        suite.addTestSuite(NoteListTest.class);
        suite.addTestSuite(NotificationXMLTest.class);
        suite.addTestSuite(ElementIdTest.class);
        suite.addTestSuite(IniTest.class);
        suite.addTestSuite(ParseArgumentsTest.class);
        suite.addTestSuite(TestAccountNameComparator.class);
        //$JUnit-END$
        return suite;
    }

}
