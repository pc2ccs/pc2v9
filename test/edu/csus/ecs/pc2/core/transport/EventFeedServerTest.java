package edu.csus.ecs.pc2.core.transport;

import java.io.IOException;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;

/**
 * Test for Event Feed Server.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EventFeedServerTest extends TestCase {

    public static void main(String[] args) throws IOException {
        int port = 5555;

        EventFeedServer sock = new EventFeedServer();

        SampleContest sample = new SampleContest();

        IInternalContest contest = sample.createContest(1, 1, 12, 6, true);
        sock.startSocketListener(port, contest);
    }

}
