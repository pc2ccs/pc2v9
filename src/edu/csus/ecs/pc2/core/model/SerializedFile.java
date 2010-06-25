package edu.csus.ecs.pc2.core.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.MessageDigest;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.log.StaticLog;

/**
 * A file that can be stored to disk or transported.
 * 
 * simplified SerializedFile.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */
// $HeadURL$
// TODO complete all method comments.
public class SerializedFile implements Serializable {

    public static final String SVN_ID = "$Id$";

    static final long serialVersionUID = -254619749606639287L;

    private static File file;

    private byte[] buffer;

    private String name;

    private String absolutePath;

    private String sha1sum;

    private int fileType = Constants.FILETYPE_BINARY;

    private int newLineCount = 0;

    // private Constants.FileTypes fileType = Constants.FileTypes.BINARY;

    // private int newLineCount = 0;

    public SerializedFile() {
        name = null;
        buffer = null;
        file = null;
        fileType = Constants.FILETYPE_BINARY;
    }

    /**
     * 
     * @param fileName
     *            file to be read/loaded.
     */
    public SerializedFile(String fileName) {
        file = new File(fileName);
        setName(file.getName());

        if (!file.exists()) {
            info("SerializedFile:" + fileName + " does not exist");
        } else {
            try {
                buffer = file2buffer(fileName);
                absolutePath = file.getAbsolutePath();
                generateSHA1(buffer);
                generateFileType(buffer);

            } catch (Exception e) {
                StaticLog.log("Exception in SerializeFile for file " + fileName, e);
            }
        }
    }

    private void info(String string) {
        // TODO Auto-generated method stub

    }

    /**
     * 
     * @param fileName
     *            file to be read/loaded.
     * @param limit -
     *            maximum file size in bytes.
     */
    public SerializedFile(String fileName, int limit) {
        file = new File(fileName);
        setName(file.getName());

        if (!file.exists()) {
            info("SerializedFile:" + fileName + " does not exist");
        } else {
            try {
                buffer = file2buffer(fileName, limit);
                absolutePath = file.getAbsolutePath();
                generateSHA1(buffer);
                generateFileType(buffer);

            } catch (Exception e) {
                StaticLog.log("Exception in SerializeFile for file " + fileName, e);
            }
        }
    }

    /**
     * Write bytes to file.
     * 
     * @param b
     *            bytes to write.
     * @param fileName
     *            name of file to write to.
     */
    public void buffer2file(byte[] b, String fileName) {

        // String methodName = "buffer2file(byte[],String)";
        // t.trace(methodName,10);

        try {
            FileOutputStream outputStream = null;
            outputStream = new FileOutputStream(fileName);
            outputStream.write(b, 0, b.length);
            outputStream.close();
        } catch (Exception e) {
            StaticLog.log("Exception in buffer2file for file " + fileName, e);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        SerializedFile clone = new SerializedFile();
        clone.setFile(getFile());
        clone.buffer = cloneBuffer(buffer);
        clone.setName(cloneString(name));
        clone.absolutePath = cloneString(absolutePath);
        clone.setSHA1sum(cloneString(getSHA1sum()));
        clone.fileType = getFileType();
        clone.newLineCount = newLineCount;
        return clone;
    }

    private String cloneString(String s) {
        String result = null;
        if (s != null) {
            result = new String(s);
        }
        return result;
    }

    private byte[] cloneBuffer(byte[] bufferToCopy) {
        byte[] clone;
        if (bufferToCopy != null) {
            clone = new byte[bufferToCopy.length];
            for (int i = 0; i < bufferToCopy.length; i++) {
                clone[i]=bufferToCopy[i];
            }
        } else {
            clone = null;
        }
        return clone;
    }

    /**
     * Create file on disk.
     * 
     * 
     * 
     * @param fileName
     *            java.lang.String
     * @throws IOException
     */
    public void writeFile(String fileName) throws IOException {

        if (buffer != null && buffer.length > 0) {
            FileOutputStream outputStream = null;
            outputStream = new FileOutputStream(fileName);
            outputStream.write(buffer, 0, buffer.length);
            outputStream.close();
        } else {
            throw new IOException("Unable to write file, buffer is null or buffer.length is zero");
        }
    }

    /**
     * Read file and output buffer of bytes.
     * 
     * @param fileName
     *            file to be read.
     * @return bytes from the input file.
     */
    public byte[] file2buffer(String fileName) {
        InputStream inputStream = null;
        byte[] b;
        int len = 0;

        try {
            inputStream = new FileInputStream(fileName);
            len = inputStream.available();
        } catch (Exception e) {
            StaticLog.log("Exception in file2buffer for file " + fileName, e);
        }

        b = new byte[len];

        try {
            inputStream.read(b);
            inputStream.close();
        } catch (Exception e) {
            StaticLog.log("Exception in file2buffer for file " + fileName, e);
        }

        return b;
    }

    /**
     * Read file bytes, read no more than limit bytes.
     * 
     * @param fileName
     *            file to be read.
     * @param limit
     *            maximum number in bytes that will be read.
     * @return bytes from file.
     */
    public byte[] file2buffer(String fileName, int limit) {

        // String methodName = "file2buffer(String)";
        // t.trace(methodName,10);

        InputStream inputStream = null;
        byte[] b;
        int len = 0;

        try {
            inputStream = new FileInputStream(fileName);
            len = inputStream.available();
        } catch (Exception e) {
            StaticLog.log("Exception in file2buffer", e);
        }

        if (len > limit) {
            len = limit;
        }

        b = new byte[len];

        try {
            inputStream.read(b);
            inputStream.close();
        } catch (Exception e) {
            StaticLog.log("Exception in generateSHA1", e);
        }

        return b;
    }

    /**
     * Generates a unique checksum for this file.
     */
    public void generateSHA1(byte[] buf) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.reset();
            md.update(buf);
            byte[] digested = md.digest();

            String out = "";
            for (int i = 0; i < digested.length; i++) {
                out = out + new Integer(digested[i]).toString();
            }
            sha1sum = out;

        } catch (Exception ex99) {
            StaticLog.log("Exception in generateSHA1", ex99);
        }

    }

    /**
     * File absolutePath for this file.
     * 
     * @return java.lang.String absolute path for this file.
     */
    public java.lang.String getAbsolutePath() {
        return absolutePath;
    }

    /**
     * Get bytes from previously loaded file.
     * 
     * @return bytes from file (already loaded file)
     */
    public byte[] getBuffer() {
        return buffer;
    }

    public File getFile() {
        return file;
    }

    /**
     * A unique checksum for this file (SHA1).
     * 
     * @return java.lang.String
     */
    public java.lang.String getSHA1sum() {
        return sha1sum;
    }

    /**
     * Get short name of file.
     * 
     * @see File#getName()
     */
    public String getName() {
        return name;
    }

    public void setFile(File file) {
        SerializedFile.file = file;
    }

    /**
     * set SHA1 for this file.
     * 
     * @param newSHA1sum
     *            SHA1 string.
     */
    void setSHA1sum(java.lang.String newSHA1sum) {
        sha1sum = newSHA1sum;
    }

    private void setName(String name) {
        this.name = name;
    }

    /**
     * Insert the method's description here. Creation date: (11/16/2003 9:14:19 PM)
     */
    public void generateFileType(byte [] buf) {

        int dosFlag = 0;
        int unixFlag = 0;
        int macFlag = 0;
        byte lastChar = 0;

        /*
         * DOS FILE 0x0D 0x0A UNIX FILE 0xA MAC FILE 0xD
         */

        for (int i = 0; i < buf.length; i++) {

            if (buf[i] == 0x0A) {
                if (lastChar == 0x0D) {
                    dosFlag++;
                    macFlag--;
                } else {
                    unixFlag++;
                }
            } else if (buf[i] == 0x0D) {
                macFlag++;
            }

            if (buf[i] > 127) {
                fileType = Constants.FILETYPE_BINARY;
                newLineCount = 0;
                return;
            }

            lastChar = buf[i];
        }

        if ((dosFlag != 0) && (unixFlag == 0) && (macFlag == 0)) {
            fileType = Constants.FILETYPE_DOS;
            newLineCount = dosFlag;
        } else if ((dosFlag == 0) && (unixFlag != 0) && (macFlag == 0)) {
            fileType = Constants.FILETYPE_UNIX;
            newLineCount = unixFlag;
        } else if ((dosFlag == 0) && (unixFlag == 0) && (macFlag != 0)) {
            fileType = Constants.FILETYPE_MAC;
            newLineCount = macFlag;
        } else if ((dosFlag == 0) && (unixFlag == 0) && (macFlag == 0)) {
            fileType = Constants.FILETYPE_ASCII_GENERIC;
            newLineCount = 0;
        } else {
            fileType = Constants.FILETYPE_ASCII_OTHER;
            newLineCount = 0;

        }

        return;
    }

    /**
     * 
     */
    public int getFileType() {
        return fileType;
    }

    /**
     * Insert the method's description here. Creation date: (11/16/2003 9:14:19 PM)
     */
    public boolean convertFile(int convertFileToType) {
        byte [] newbuffer;
        int counter;

        if ((fileType == Constants.FILETYPE_BINARY) || (fileType == Constants.FILETYPE_ASCII_GENERIC) || (fileType == Constants.FILETYPE_ASCII_OTHER)) {

            /* we are not converting these types for now */
            return false;
        }

        if (fileType == convertFileToType) {
            /* duh easy conversion */
            return true;
        }

        if (convertFileToType == Constants.FILETYPE_DOS) {
            newbuffer = new byte[buffer.length + newLineCount];
        } else if (fileType == Constants.FILETYPE_DOS) {
            newbuffer = new byte[buffer.length - newLineCount];
        } else {
            newbuffer = new byte[buffer.length];
        }

        /*
         * if we convert the file we are saving the old MD5 checksum to avoid refersing the file everytime the problem is edited. The class will contain the MD5 check of the original file loaded from
         * disk
         */

        /*
         * DOS FILE 0x0D 0x0A UNIX FILE 0xA MAC FILE 0xD
         */
        counter = 0;
        for (int i = 0; i < buffer.length; i++) {
            /*
             * dos -> unix dos -> mac
             * 
             * unix -> mac unix -> dos
             * 
             * mac -> dos mac -> unix
             */
            if ((buffer[i] == 0x0D) || (buffer[i] == 0x0A)) {
                if (fileType == Constants.FILETYPE_DOS) {
                    if (convertFileToType == Constants.FILETYPE_UNIX) {
                        newbuffer[counter++] = 0x0A;
                        i++;
                    } else {
                        newbuffer[counter++] = 0x0D;
                        i++;
                    }
                } else if (fileType == Constants.FILETYPE_UNIX) {
                    if (convertFileToType == Constants.FILETYPE_MAC) {
                        newbuffer[counter++] = 0x0D;
                    } else {
                        newbuffer[counter++] = 0x0D;
                        newbuffer[counter++] = 0x0A;
                    }
                } else if (fileType == Constants.FILETYPE_MAC) {
                    if (convertFileToType == Constants.FILETYPE_UNIX) {
                        newbuffer[counter++] = 0x0A;
                    } else {
                        newbuffer[counter++] = 0x0D;
                        newbuffer[counter++] = 0x0A;
                    }
                }
            } else {
                newbuffer[counter++] = buffer[i];
            }
        }

        String s = "Converted file from ";

        if (fileType == Constants.FILETYPE_BINARY) {
            s = s + Constants.FILETYPE_BINARY_TEXT;
        } else if (fileType == Constants.FILETYPE_DOS) {
            s = s + Constants.FILETYPE_DOS_TEXT;
        } else if (fileType == Constants.FILETYPE_MAC) {
            s = s + Constants.FILETYPE_MAC_TEXT;
        } else if (fileType == Constants.FILETYPE_UNIX) {
            s = s + Constants.FILETYPE_UNIX_TEXT;
        } else if (fileType == Constants.FILETYPE_ASCII_GENERIC) {
            s = s + Constants.FILETYPE_ASCII_GENERIC_TEXT;
        } else if (fileType == Constants.FILETYPE_ASCII_OTHER) {
            s = s + Constants.FILETYPE_ASCII_OTHER_TEXT;
        }

        s = s + " to ";

        if (convertFileToType == Constants.FILETYPE_BINARY) {
            s = s + Constants.FILETYPE_BINARY_TEXT;
        } else if (convertFileToType == Constants.FILETYPE_DOS) {
            s = s + Constants.FILETYPE_DOS_TEXT;
        } else if (convertFileToType == Constants.FILETYPE_MAC) {
            s = s + Constants.FILETYPE_MAC_TEXT;
        } else if (convertFileToType == Constants.FILETYPE_UNIX) {
            s = s + Constants.FILETYPE_UNIX_TEXT;
        } else if (convertFileToType == Constants.FILETYPE_ASCII_GENERIC) {
            s = s + Constants.FILETYPE_ASCII_GENERIC_TEXT;
        } else if (convertFileToType == Constants.FILETYPE_ASCII_OTHER) {
            s = s + Constants.FILETYPE_ASCII_OTHER_TEXT;
        }

        StaticLog.info(s);

        fileType = convertFileToType;
        buffer = newbuffer;

        return true;
    }
}
