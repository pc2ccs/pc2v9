package edu.csus.ecs.pc2.services.eventFeed;

import java.io.FileOutputStream;
import java.util.Properties;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests.
 * 
 * @author Douglas A. Lane, PC^2 Team, <pc2@ecs.csus.edu>
 */
public class WebServerTest extends AbstractTestCase {
    
    /**
     * Create sample web server properties file.
     * 
     * @throws Exception
     */
    public void testCreateSample() throws Exception {
        
        Properties properties = WebServer.createSampleProperties();
        String filename = "/tmp/"+EventFeederModule.WEB_SERVICES_PROPERTIES_FILENAME + ".samp";
        FileOutputStream fileOutputStream = new FileOutputStream(filename,false);
        properties.store(fileOutputStream, "Sample PC^2 Web Server properties ");
//        System.out.println("Wrote to "+filename);
//        editFile(filename);
    }
}
