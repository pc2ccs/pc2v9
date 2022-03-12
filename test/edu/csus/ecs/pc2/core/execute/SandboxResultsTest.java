// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.execute;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * 
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class SandboxResultsTest extends AbstractTestCase {
    
    public void testLoadResults() throws Exception {
        
        ensureOutputDirectory();
        String storageDirectory = getOutputDataDirectory();
        
//        startExplorer(storageDirectory);
        
        Properties props = new Properties();
        
        props.put(SandboxResults.COMMENT_KEY,"This is too much");
        props.put(SandboxResults.JUDGEMENT_ACRONYM_KEY,"IO");
        props.put(SandboxResults.RUNID_KEY,"45");
        
        String propFilename = storageDirectory + File.separator + SandboxResults.SANDBOX_RESULTS_FILENAME;

        FileOutputStream fileOutputStream = new FileOutputStream(propFilename, false);
        props.store(fileOutputStream, "PC^2 Sandbox Output properties");
        
        assertFileExists(propFilename, "Sandbox output file");
        
        SandboxResults sandboxResults = new SandboxResults(new File(propFilename));
        
        int count = sandboxResults.getProperties().size();
        assertEquals("Expected number of properties in "+propFilename, 3, count);
        
        String keyName = SandboxResults.JUDGEMENT_ACRONYM_KEY;
        String expected = "IO";
        String actual = sandboxResults.getProperty(keyName);
        assertPropertiesValueEquals("Property value does not match", props, keyName, expected, actual);

        keyName = SandboxResults.RUNID_KEY;
        expected = "45";
        actual = sandboxResults.getProperty(keyName);
        assertPropertiesValueEquals("Property value does not match", props, keyName, expected, actual);

        keyName = SandboxResults.COMMENT_KEY;
        expected = "This is too much";
        actual = sandboxResults.getProperty(keyName);
        assertPropertiesValueEquals("Property value does not match", props, keyName, expected, actual);

    }

}
