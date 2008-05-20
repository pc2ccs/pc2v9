package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import junit.framework.TestCase;

/**
 * Test for Edit Filter Frame 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditFilterFrameTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public static void main(String[] args) {
        EditFilterFrame editFilterFrame = new EditFilterFrame();
        editFilterFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editFilterFrame.setVisible(true);
    }

}
