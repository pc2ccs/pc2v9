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


    public void testNull() throws Exception {
        
    }

    public static void main(String[] args) {

        PropertiesEditFrame frame = new PropertiesEditFrame();
        Properties properties =new Properties();
        properties.put("one", "value1");
        properties.put("two", "2");
        properties.put("zero", "cero");
        /**
         * 
         * @author pc2@ecs.csus.edu
         *
         */
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
