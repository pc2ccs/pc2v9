package test.edu.csus.ecs.pc2.core;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;

/**
 * ClientId JUnit test class.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
//$HeadURL$
public class ClientIdTest extends TestCase {

    /**
     * svn id.
     */
    public static final String SVN_ID = "$Id$";

    public ClientIdTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'edu.csus.ecs.pc2.core.ClientId.hashCode()'
     */
    public void testHashCode() {
        ClientId client1 = new ClientId(1, ClientType.Type.TEAM, 20);
        ClientId client2 = new ClientId(1, ClientType.Type.TEAM, 20);
        assertTrue("hashCode mismatch", client1.hashCode() == client2
                .hashCode());
    }

    /*
     * Test method for 'edu.csus.ecs.pc2.core.ClientId.toString()'
     */
    public void testToString() {
        ClientId client1 = new ClientId(1, ClientType.Type.TEAM, 20);
        ClientId client2 = new ClientId(1, ClientType.Type.TEAM, 20);
        assertTrue("toString mismatch", client1.toString().equals(
                client2.toString()));
    }

    /*
     * Test method for 'edu.csus.ecs.pc2.core.ClientId.equals(Object)'
     */
    public void testEqualsObject() {
        ClientId client1 = new ClientId(1, ClientType.Type.TEAM, 20);
        ClientId client2 = new ClientId(1, ClientType.Type.TEAM, 20);
        assertTrue("equals mismatch", client1.equals(client2));
    }

    /*
     * Test method for 'edu.csus.ecs.pc2.core.ClientId.getTripletKey()'
     */
    public void testGetTripletKey() {
        ClientId client1 = new ClientId(1, ClientType.Type.TEAM, 20);
        ClientId client2 = new ClientId(1, ClientType.Type.TEAM, 20);
        assertTrue("tripletKey mismatch", client1.getTripletKey().equals(
                client2.getTripletKey()));
    }

}
