package edu.csus.ecs.pc2.ui;

import java.util.Properties;

import javax.swing.JFrame;

import junit.framework.TestCase;

/**
 * Properties Edit Frame test.
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

        PropertiesEditFrame frame = new PropertiesEditFrame();
        Properties properties =new Properties();
        properties.put("one", "value1");
        properties.put("two", "2");
        properties.put("zero", "cero");
        class Updater implements IPropertyUpdater{

            public void updateProperties(Properties properties) {
                System.out.println("Properies changed");
            }
        }
        
        frame.setProperties(properties, new Updater());
        
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
