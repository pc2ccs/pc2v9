package edu.csus.ecs.pc2.shadow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;

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

    public PacedFileInputStream(File file) throws FileNotFoundException {
        this(file, 2);
    }

    public PacedFileInputStream(File file, int sleepSeconds) throws FileNotFoundException {
        this.sleepSeconds = sleepSeconds;
        this.inputFile = file;
        if (file == null || !file.isFile()) {
            throw new InvalidParameterException("No such file " + file);
        }
        inputReader = new FileReader(inputFile);
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
            return 0;
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
                Thread.sleep(sleepSeconds * 1000);
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
