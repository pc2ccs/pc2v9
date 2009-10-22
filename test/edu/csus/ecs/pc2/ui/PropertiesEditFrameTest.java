package edu.csus.ecs.pc2.ui;

import java.util.Properties;

import javax.swing.JFrame;

import junit.framework.TestCase;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PropertiesEditFrameTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public static void main(String[] args) {

        System.err.println("main start");
        PropertiesEditFrame frame = new PropertiesEditFrame();
        Properties properties =new Properties();
        properties.put("one", "value1");
        properties.put("two", "2");
        properties.put("zero", "cero");
        frame.setProperties(properties);
        
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        System.err.println("main end");
    }
}
