package edu.csus.ecs.pc2.core.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.MessageDigest;

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

    private String md5sum;

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
                generateMD5(buffer);
//                generateFileType(buffer);

            } catch (Exception e) {
                log("Exception in SerializeFile", e);
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
                generateMD5(buffer);
//                generateFileType(buffer);

            } catch (Exception e) {
                log("Exception in SerializeFile", e);
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
            log("Exception in buffer2file", e);
        }
    }

    /**
     * Create file on disk.
     *
     *
     *
     * @param fileName
     *            java.lang.String
     */
    public void writeFile(String fileName) {

        try {
            if (buffer != null && buffer.length > 0) {
                FileOutputStream outputStream = null;
                outputStream = new FileOutputStream(fileName);
                outputStream.write(buffer, 0, buffer.length);
                outputStream.close();
            } else {
                System.out.println("buffer is null or buffer.length == 0");
            }

        } catch (FileNotFoundException e1) {
            // TODO LOG log this exception
            e1.printStackTrace();
        } catch (IOException e) {
            // TODO LOG log this exception
            e.printStackTrace();
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
            // TODO log
            e.printStackTrace();
        }

        b = new byte[len];

        try {
            inputStream.read(b);
            inputStream.close();
        } catch (Exception e) {
            // TODO log
            e.printStackTrace();

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
            log("Exception in file2buffer", e);
        }

        if (len > limit) {
            len = limit;
        }

        b = new byte[len];

        try {
            inputStream.read(b);
            inputStream.close();
        } catch (Exception e) {
            log("Exception in file2buffer", e);
        }

        return b;
    }


    /**
     * Generates a unique checksum for this file.
     */
    public void generateMD5(byte[] buf) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.reset();
            md.update(buf);
            byte[] digested = md.digest();

            String out = "";
            for (int i = 0; i < digested.length; i++) {
                out = out + new Integer(digested[i]).toString();
            }
            md5sum = out;

        } catch (Exception ex99) {
            log("Exception in generateMD5", ex99);
        }

    }

    private void log(String string, Exception ex99) {
        // TODO Auto-generated method stub
        
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
    public java.lang.String getMd5sum() {
        return md5sum;
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
    void setMd5sum(java.lang.String newMd5sum) {
        md5sum = newMd5sum;
    }

    private void setName(String name) {
        this.name = name;
    }
}
