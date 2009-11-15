package edu.csus.ecs.pc2.core.security;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.csus.ecs.pc2.core.IStorage;

/**
 * Write objects to disk (unencrypted).
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class FileStorage implements IStorage {

    private String directoryName = ".";

    public FileStorage() {

    }

    public FileStorage(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public Serializable load(String fileName) throws IOException, ClassNotFoundException, FileSecurityException {
        return (Serializable) readObjectFromFile(fileName);
    }

    public boolean store(String fileName, Serializable serializable) throws IOException, ClassNotFoundException, FileSecurityException {
        return writeObjectToFile(fileName, serializable);
    }

    /**
     * 
     * @param filename
     * @param serializable
     * @return true, otherwise throws an exception
     * @throws IOException
     */
    public boolean writeObjectToFile(String filename, Serializable serializable) throws IOException {
        FileOutputStream f = new FileOutputStream(filename);
        ObjectOutputStream s = new ObjectOutputStream(f);
        s.writeObject(serializable);
        s.flush();
        s.close();
        s = null;
        return true;

    }

    /**
     * Read serialized object from file.
     * 
     * @param filename
     * @return the object
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private Object readObjectFromFile(String filename) throws IOException, ClassNotFoundException {
        FileInputStream in = new FileInputStream(filename);
        ObjectInputStream s = new ObjectInputStream(in);
        Object object = s.readObject();
        in.close();
        s.close();
        return object;
    }

}
