package edu.csus.ecs.pc2.services.eventFeed;

import java.io.FileOutputStream;
import java.util.Properties;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests.
 * 
 * @author laned
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
        System.out.println("Wrote to "+filename);
        
        editFile(filename);
    }

    public static void main(String[] args) {
        
        
    }
}
