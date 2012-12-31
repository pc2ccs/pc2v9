package edu.csus.ecs.pc2.ui;

import junit.framework.TestCase;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class TextCountDownMessageTest extends TestCase {

    public void testSimple() {
        int remainSecs = 10;
        TextCountDownMessage countDownMessage = new TextCountDownMessage();
        countDownMessage.start("Shutdown test, ", remainSecs);
        assertEquals("Countdown seconds ", remainSecs, countDownMessage.getRemainingSeconds());
    }

    public static void main(String[] args) {
        new TextCountDownMessage().start("Shutdown test, ", 10);
    }
}
