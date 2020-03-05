package edu.csus.ecs.pc2.shadow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;

import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.Utilities;

/**
 * A file reader that pauses between text lines.
 * 
 * Reads a file and returns the file byte by byte, if
 * a ASCII 10 is encountered will sleep for N seconds.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class PacedFileInputStream extends InputStream {

    private final static int UNSET_INDEX = -1;

    int bufferIndex = UNSET_INDEX;

    char[] buffer = new char[65000];

    private File inputFile;

    private int sleepSeconds = 2;
    
    int pauseCount = 0;

    /**
     * Characters read into buffer.
     */
    private int maxCharsInBuffer;

    private FileReader inputReader = null;
    
//    [shadow]
//            overrideeffilename=FILENAME
//            overrideefsleep=SECONDS  
    
    /**
     * A key used to override any input event feed filename.
     */
    private static final String OVERRIDE_EVENT_FEED_FILENAME_KEY = "shadow.overrideeffilename";
    
    /**
     * A key used to override any pacing sleep seconds 
     */
    private static final String OVERRIDE_EVENT_FEED_SLEEP_SECONDS_KEY = "shadow.overrideefsleep";

    public PacedFileInputStream(File file) throws FileNotFoundException {
        this(file, 2);
    }

    public PacedFileInputStream(File file, int sleepSeconds) throws FileNotFoundException {
        this.sleepSeconds = sleepSeconds;
        this.inputFile = file;
        
        if (IniFile.isFilePresent()) {
            new IniFile();
        }
        
        /**
         * Override values
         */

        if (containsINIKey(OVERRIDE_EVENT_FEED_FILENAME_KEY)) {
            String filename = getINIValue(OVERRIDE_EVENT_FEED_FILENAME_KEY);

            inputFile = new File(filename);
            if (!inputFile.isFile()) {
                throw new FileNotFoundException("Cannot override filename " + OVERRIDE_EVENT_FEED_FILENAME_KEY + "=" + filename);
            }
        }

        if (containsINIKey(OVERRIDE_EVENT_FEED_SLEEP_SECONDS_KEY)) {
            String sleepString = getINIValue(OVERRIDE_EVENT_FEED_SLEEP_SECONDS_KEY);
            int newSecs = Utilities.nullSafeToInt(sleepString, -1);
            if (newSecs > 0) {
                sleepSeconds = newSecs;
            } else {
                throw new InvalidParameterException("Invalid override seconds " + OVERRIDE_EVENT_FEED_SLEEP_SECONDS_KEY + "=" + sleepString);
            }
        }
        
//        System.out.println("Secs = "+sleepSeconds+" file="+inputFile.getName());
        
        if (inputFile == null || !inputFile.isFile()) {
            throw new InvalidParameterException("No such file " + file);
        }
        inputReader = new FileReader(inputFile);
    }
    
    /**
     * Does .ini file contain key ?
     * @param key
     * @return true if key found, else false.
     */
    private static boolean containsINIKey(String key) {
        if (IniFile.isFilePresent()) {
            return IniFile.containsKey(key);
        } else {
            return false;
        }
    }
    
    /**
     * Get value from .ini file if it exists.
     * 
     * @param key
     * @return
     */
    private static String getINIValue(String key) {
        if (IniFile.isFilePresent()) {
            return IniFile.getValue(key);
        } else {
            return "";
        }
    }
    

    protected int nextByte(FileReader reader) throws IOException {

        if (bufferIndex == UNSET_INDEX) {
            maxCharsInBuffer = reader.read(buffer);
            bufferIndex = 0;
            return buffer[bufferIndex];
        }

        if (maxCharsInBuffer == -1)
        {
            // at EOF
            return -1;
        }
        else
        {
            bufferIndex++;

            if (bufferIndex >= maxCharsInBuffer) {
                bufferIndex = UNSET_INDEX;
                return nextByte(reader);
            }

            return buffer[bufferIndex];
        }
    }

    @Override
    public int read() throws IOException {
        int byteRead = nextByte(inputReader);

        if (byteRead == 10) {
            pauseCount++;
            // if a end of line, ASCII 10 sleep for a second
            try {
//                Thread.sleep(sleepSeconds * 1000);
                Thread.sleep(sleepSeconds * 20);
            } catch (InterruptedException e) {
                // very unlikely, but possible.
                e.printStackTrace();
            }
        }

        return byteRead;
    }
    
    public int getPauseCount() {
        return pauseCount;
    }
}
