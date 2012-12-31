package edu.csus.ecs.pc2.ui;

import junit.framework.TestCase;

/**
 * Unit test.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class CountDownMessageTest extends TestCase {
    
    public static void main(String[] args) {
        CountDownMessage countDownMessage = new CountDownMessage();
        countDownMessage.setExitOnClose(true);
        countDownMessage.start("Shutdown test, ", 10);
    }

}
