package edu.csus.ecs.pc2.core;

import java.io.IOException;
import java.io.Serializable;

import edu.csus.ecs.pc2.core.security.FileSecurityException;

/**
 * A Storage class interface.
 * 
 * This interface provides methods to save and load objects
 * from disk.   
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IStorage {

    /**
     * Load Object from filename.
     * 
     * @param fileName
     * @return
     */
    Serializable load(String fileName) throws IOException, ClassNotFoundException, FileSecurityException;

    /**
     * Store object to disk.
     * 
     * @param fileName
     * @param serializable
     * @return true if file is saved
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FileSecurityException
     */
    boolean store(String fileName, Serializable serializable) throws IOException, ClassNotFoundException, FileSecurityException;

    /**
     * Get directory for files to be written to.
     * 
     * @return
     */
    String getDirectoryName();
}
