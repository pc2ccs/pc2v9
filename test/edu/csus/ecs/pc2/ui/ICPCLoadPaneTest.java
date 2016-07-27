package edu.csus.ecs.pc2.ui;

import junit.framework.TestCase;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ICPCLoadPaneTest extends TestCase {
    
    public void testNull() throws Exception {
        
    }
    
    public static void main(String[] args) {
        
        ICPCLoadPane plugin = new ICPCLoadPane();
        
        new TestingFrame(plugin);
        
    }

}
