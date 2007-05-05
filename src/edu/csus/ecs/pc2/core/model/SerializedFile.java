package edu.csus.ecs.pc2.core.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.MessageDigest;

import edu.csus.ecs.pc2.core.log.StaticLog;

/**
 * A file that can be stored to disk or transported.
 *
 *  simplified SerializedFile.
 *
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

//    private PC2Constants.FileTypes fileType = PC2Constants.FileTypes.BINARY;

//    private int newLineCount = 0;

    public SerializedFile() {
        name = null;
        buffer = null;
        file = null;
//        fileType = PC2Constants.FileTypes.BINARY;
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
//                generateFileType(buffer);

            } catch (Exception e) {
                StaticLog.log("Exception in SerializeFile for file "+fileName, e);
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
//                generateFileType(buffer);

            } catch (Exception e) {
                StaticLog.log("Exception in SerializeFile for file "+fileName, e);
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
            StaticLog.log("Exception in buffer2file for file "+fileName, e);
        }
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
            StaticLog.log("Exception in file2buffer for file "+fileName, e);
        }

        b = new byte[len];

        try {
            inputStream.read(b);
            inputStream.close();
        } catch (Exception e) {
            StaticLog.log("Exception in file2buffer for file "+fileName, e);
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
            StaticLog.log("Exception in generateMD5", e);
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
     * A unique checksum for this file (MD5).
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
     * set MD5 for this file.
     *
     * @param newMd5sum
     *            md5 string.
     */
    void setSHA1sum(java.lang.String newMd5sum) {
        sha1sum = newMd5sum;
    }

    private void setName(String name) {
        this.name = name;
    }
}
