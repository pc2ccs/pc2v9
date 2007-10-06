package edu.csus.ecs.pc2.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Vector;

/**
 * Various common routines.
 *
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public final class Utilities {

    public static final String SVN_ID = "$Id$";

    /**
     * Constructor is private as this is a utility class which
     * should not be extended or invoked.
     */
    private Utilities() {
        super();
    }

    /**
     * Insure directory exists, if does not exist create it.
     *
     * @param dirName
     *            directory to create.
     * @return whether directory exists.
     */
    public static boolean insureDir(String dirName) {
        File dir = null;

        // insure that the ./newExecute directory is there
        dir = new File(dirName);
        if (!dir.exists() && !dir.mkdir()) {
            // TODO show user that couldn't create this directory
            System.out.println("insureDir Directory " + dir.getName()
                    + " could not be created.");
        }

        return dir.isDirectory();
    }

    /**
     * Read serialized object from file.
     *
     * @param filename
     * @return the object
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Object readObjectFromFile(String filename)
            throws IOException, ClassNotFoundException {
        Object object = new Object();

        FileInputStream in = new FileInputStream(filename);
        ObjectInputStream s = new ObjectInputStream(in);
        object = s.readObject();
        in.close();
        s.close();
        return object;

    }

    /**
     *
     * @param filename
     * @param serializable
     * @return true, otherwise throws an exception
     * @throws IOException
     */
    public static boolean writeObjectToFile(String filename,
            Serializable serializable) throws IOException {
        FileOutputStream f = new FileOutputStream(filename);
        ObjectOutputStream s = new ObjectOutputStream(f);
        s.writeObject(serializable);
        s.flush();
        s.close();
        s = null;
        return true;

    }

    /**
     *
     * @param filename
     *            String name of file
     * @return boolean true if file exists.
     */
    public static boolean isFileThere(String filename) {
        File file = new File(filename);
        return file.isFile();
    }

    /**
     * Returns lines from file.
     * 
     * @param filename
     *            String file to load
     * @return lines from file
     * @throws IOException
     */
    public static String[] loadFile(String filename) throws IOException {
        Vector<String> lines = new Vector<String>();

        if (filename == null) {
            throw new IllegalArgumentException("filename is null");
        }

        if (!new File(filename).exists()) {
            return new String[0];
        }

        FileReader fileReader = new FileReader(filename);
        BufferedReader in = new BufferedReader(fileReader);
        String line = in.readLine();
        while (line != null) {
            lines.addElement(line);
            line = in.readLine();
        }
        in.close();
        fileReader.close();
        in = null;
        fileReader = null;

        if (lines.size() == 0) {
            return new String[0];
        }

        String[] out = new String[lines.size()];

        for (int i = 0; i < lines.size(); i++) {
            out[i] = lines.elementAt(i);
        }

        return out;

    }

    /**
     * Get Current Working Directory.
     * @return current working directory.
     */
    public static String getCurrentDirectory() {
        File curdir = new File(".");

        try {
            return curdir.getCanonicalPath();
        } catch (Exception e) {
            // ignore exception
            return ".";
        }
    }

    /**
     * Returns Yes if true, No if false.
     * @param b
     * @return
     */
    public static String yesNoString(boolean b) {
        if (b) {
            return "Yes";
        } else {
            return "No";
        }
    }

    /**
     * Load INI file.
     * 
     * This will read a text file and strip out blank/empty lines
     * and lines that start with a hash mark.
     * <P>
     * This will also trim the input lines.
     * 
     * @param filename file to be read
     * @return String [] null if can't read/find file, else lines infile
     */
    public static String[] loadINIFile(String filename) {

        Vector<String> v = new Vector<String>();

        try {
            RandomAccessFile file = new RandomAccessFile(filename, "r");
            String line;

            while ((line = file.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0) {
                    if (line.charAt(0) != '#') {
                        v.addElement(line);
                    }
                }
            }

            file.close();
            file = null;
        } catch (Exception e) {
            return null;
        }
        
        return (String[]) v.toArray(new String[v.size()]);
    }

}
