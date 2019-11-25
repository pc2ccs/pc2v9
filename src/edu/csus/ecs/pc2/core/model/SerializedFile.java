package edu.csus.ecs.pc2.core.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;

/**
 * A file that can be stored to disk or transported.
 * 
 * simplified SerializedFile.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */
// $HeadURL$
public class SerializedFile implements Serializable, IJsonizable {

    static final long serialVersionUID = -254619749606639287L;

//    private static File file;
    private File file;      //jlc 4/14/19  Makes no sense to have a static field for this

    /**
     * The file contents.
     */
    @JsonProperty
    private byte[] buffer;

    @JsonProperty
    private String name;

    @JsonProperty
    private String absolutePath;

    @JsonProperty
    private String sha1sum;

    @JsonProperty
    private int fileType = Constants.FILETYPE_BINARY;

    @JsonProperty
    private int newLineCount = 0;

    /**
     * Is this a file that is not stored in the buffer, stored on disk.
     */
    @JsonProperty
    private boolean externalFile = false;

    @JsonProperty
    private char [] errorMessage;

    @JsonProperty
    private Exception exception;

    /**
     * Construct a SerializedFile with default values.
     * The default values for name, buffer (data contents), and file are null;
     * the default value for fileType is Constants.FILETYPE_BINARY.
     */
    public SerializedFile() {
        name = null;
        buffer = null;
        file = null;
        fileType = Constants.FILETYPE_BINARY;
    }
    
    /**
     * Construct a SerializedFile with the specified fileName.
     * An invocation of this constructor has the same effect as invoking {@link #SerializedFile(String, boolean)}
     * with the specified fileName and a boolean parameter false.
     * 
     * @param fileName the name to be associated with the SerializedFile
     */
    public SerializedFile(String fileName) {
        this(fileName, false);
    }

    /**
     * Construct a SerializedFile with the specified fileName.
     * 
     * If externalFile true then will calculate SHA and
     * {@link #getBuffer()} returns null.
     *
     * @param fileName the name to be assigned to the SerializedFile
     * @param externalFile if true then no buffer/file loaded 
     */
    public SerializedFile(String fileName, boolean externalFile) {
        
        this.externalFile = externalFile;
        file = new File(fileName);
        setName(file.getName());

        if (!file.exists()) {
            // SOMEDAY throw a file not found exception
            addMessage(fileName + " not found in SerializedFile", new FileNotFoundException(fileName));
        } else {
            try {
                buffer = file2buffer(fileName);
                absolutePath = file.getAbsolutePath();
                generateSHA1();
                generateFileType(buffer);
                
                if (externalFile){
                    /**
                     * Use the buffer to calculate SHA then null
                     * out the buffer.
                     */
                    buffer = null;
                }

            } catch (Exception e) {
                addMessage("Exception in SerializeFile for file " + fileName, e);
            }
        }
    }

    /**
     * Constructs a SerializedFile containing the specified filename and the data bytes 
     * from the specified file, which must exist on disk.
     * The SerializedFile will contain the absolute path to the specified file as the filename.
     * 
     * If the specified file cannot be found on disk an error message is inserted into the SerializedFile object;
     * if an exception is thrown while constructing SerializedFile, both an error message and a corresponding
     * exception are stored in the SerializedFile object.  In either of these cases, no additional action is taken; 
     * it is the caller's responsibility to check for errors/exceptions when constructing a SerializedFile.
     * 
     * @param fileName
     *            disk file to be read/loaded into the SerializedFile object.
     * @param limit -
     *            maximum file size in bytes.
     *            
     * @see #getAbsolutePath()
     * @see #getErrorMessage()
     * @see #getException()
     */
    public SerializedFile(String fileName, int limit) {
        file = new File(fileName);
        setName(file.getName());

        if (!file.exists()) {
            // SOMEDAY throw a file not found exception
            addMessage(fileName + " not found",null);
        } else {
            try {
                buffer = file2buffer(fileName, limit);
                absolutePath = file.getAbsolutePath();
                generateSHA1();
                generateFileType(buffer);

            } catch (Exception e) {
                addMessage("Exception in SerializeFile for file " + fileName, e);
            }
        }
    }

    /**
     * Constructs a SerializedFile containing the specified file name and data bytes.
     * The specified file name will be assigned as the "absolute file name" for the file.
     * 
     * If an exception is thrown while constructing SerializedFile, an error message and a corresponding
     * exception are stored in the SerializedFile object; however, no additional action is taken -- 
     * it is the caller's responsibility to check for errors/exceptions when constructing a SerializedFile.
     * 
     * @param fileName the file name to be associated with the new SerializedFile
     * @param data an array of bytes containing the data to be stored in the SerializedFile
     * 
     * @see #getAbsolutePath()
     * @see #getErrorMessage()
     * @see #getException()
     */
    public SerializedFile(String fileName, byte[] data) {

        try {
            buffer = data;
            absolutePath = fileName;
            generateSHA1();
            generateFileType(buffer);

        } catch (Exception e) {
            addMessage("Exception in SerializeFile for file " + fileName, e);
        }
    }

    /**
     * Constructs a SerializedFile containing the file name and data bytes from the specified {@link IFile}.
     * The file name from the {@link IFile} object will be assigned as the "absolute file name" for the SerializedFile.
     * 
     * If an exception is thrown while constructing the SerializedFile, an error message and a corresponding
     * exception are stored in the SerializedFile object; however, no additional action is taken -- 
     * it is the caller's responsibility to check for errors/exceptions when constructing a SerializedFile.
     * 
     * @param file the IFile object containing a description of the SerializedFile to be constructed
     * 
     * @see #getAbsolutePath()
     * @see #getErrorMessage()
     * @see #getException()
     */
    public SerializedFile(IFile file) {

        try {
            buffer = file.getByteData();

            // Assign name to basename for input file
            name = Utilities.basename(file.getFileName(), '/');
            name = Utilities.basename(name, '\\');
            
            absolutePath = file.getAbsolutePath();
            generateSHA1();
            generateFileType(buffer);

        } catch (Exception e) {
            addMessage("Exception in SerializeFile for file " + file, e);
        }
    }

    /**
     * Constructs a SerializedFile containing the information specified in the parameters.
     * 
     * This constructor was created to support JSON serialization (which is the reason it is private).
     * 
     * @param fileName the base name of the file
     * @param absolutePath the full path to the file
     * @param buffer a char [] containing the data in the file
     * @param errorMessage a char [] containing any error message associated with creating the file
     * @param exception any Exception associated with creating the file
     * @param externalfile a boolean indicating whether the file is external (stored on disk)
     * @param fileType a flag indicating the file type -- DOS, Mac, Unix, or binary
     */
   @JsonCreator
   private SerializedFile(
           @JsonProperty("fileName") String fileName, 
           @JsonProperty("absolutePath") String absolutePath, 
           @JsonProperty("buffer") byte [] buffer, 
           @JsonProperty("errorMessage") char [] errorMessage, 
           @JsonProperty("exception") Exception exception, 
           @JsonProperty("externalfile") boolean externalfile, 
           @JsonProperty("fileType") int fileType) {
       
       this.name = fileName;
       this.absolutePath = absolutePath;
       this.buffer = buffer;
       this.errorMessage = errorMessage;
       this.exception = exception;
       this.externalFile = externalfile;
       this.fileType = fileType;
   }
   
   @Override
    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        }
        
        if (!(obj instanceof SerializedFile)) {
            return false;
        }

        SerializedFile otherFile = (SerializedFile) obj;
        
        if (!StringUtilities.stringSame(sha1sum, otherFile.sha1sum)) {
            return false;
        }
        if (!StringUtilities.stringSame(absolutePath, otherFile.absolutePath)) {
            return false;
        }
        if (externalFile != otherFile.externalFile) {
            return false;
        }
        if (!StringUtilities.stringSame(getErrorMessage(), otherFile.getErrorMessage())) {
            return false;
        }
        
//    clone.setFile(getFile());
        
        if (!StringUtilities.stringSame(name, otherFile.name)) {
            return false;
        }

        // XXX this breaks the testEqual junit test
        if (!java.util.Arrays.equals(buffer, otherFile.buffer)) {
            return false;
        }
        // XXX but doing this breaks EditProblemPane
//        if (buffer == null) {
//            if (otherFile.buffer != null) {
//                return(false);
//            }
//        } else  if (!buffer.equals(otherFile.buffer)) {
//            return false;
//        }
        
        if (fileType != otherFile.fileType) {
            return false;
        }
        if (newLineCount != otherFile.newLineCount) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return sha1sum.hashCode();
    }

    /**
     * Write bytes to file.
     * 
     * @see #getErrorMessage()
     * @see #getException()
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
            addMessage("Exception in SerializeFile for file " + fileName, e);

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

        clone.externalFile = externalFile;
        if (errorMessage != null) {
            clone.errorMessage = errorMessage.clone();
        }

        clone.exception = exception;

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

        if (buffer == null || buffer.length == 0) {
            buffer = new byte[0];
        }

        FileOutputStream outputStream = null;
        outputStream = new FileOutputStream(fileName);
        outputStream.write(buffer, 0, buffer.length);
        outputStream.close();
    }

    /**
     * Read file and output buffer of bytes.
     * 
     * @see #getErrorMessage()
     * @see #getException()
     * @param fileName
     *            file to be read.
     * @return bytes from the input file or null if error.
     */
    public byte[] file2buffer(String fileName) {
        InputStream inputStream = null;
        byte[] b;
        int len = 0;

        try {
            inputStream = new FileInputStream(fileName);
            len = inputStream.available();
            b = new byte[len];
            inputStream.read(b);
            inputStream.close();
        } catch (Exception e) {
            addMessage("Exception reading "+fileName, e);
            b = null;
        }

        return b;
    }

    /**
     * Read file bytes, read no more than limit bytes.

     * @see #getErrorMessage()
     * @see #getException()
     * @param fileName
     *            file to be read.
     * @param limit
     *            maximum number in bytes that will be read.
     * @return bytes from file.
     */
    public byte[] file2buffer(String fileName, int limit) {

        InputStream inputStream = null;
        byte[] b;
        int len = 0;

        try {
            inputStream = new FileInputStream(fileName);
            len = inputStream.available();

            if (len > limit) {
                len = limit;
            }

            b = new byte[len];

            inputStream.read(b);
            inputStream.close();
        } catch (Exception e) {
            addMessage("Exception reading "+fileName, e);
            b = null;
        }

        return b;
    }

    /**
     * Add an error message and/or exception.
     * 
     * @param message
     * @param ex
     */
    private void addMessage(String message, Exception ex) {
        // SOMEDAY throw exception rather than store it
        errorMessage = message.toCharArray();
        exception = ex;
    }

    /**
     * Generates a unique checksum for this file.
     * 
     * @see #getErrorMessage()
     * @see #getException()
     * @return null if sum not calculated
     */
    private void generateSHA1() {
        sha1sum = null;
        try {
            sha1sum = generateSHA1(buffer); 
        } catch (Exception ex99) {
            addMessage("Exception calculating SHA1", ex99);
            sha1sum = null;
        }
    }

    public static String generateSHA1(File theFile) throws NoSuchAlgorithmException, IOException {

        String fileName = theFile.getAbsolutePath();

        InputStream inputStream = null;
        byte[] buf;
        int len = 0;

        inputStream = new FileInputStream(fileName);
        len = inputStream.available();
        buf = new byte[len];
        inputStream.read(buf);
        inputStream.close();

        return generateSHA1(buf);
    }

    
    /**
     * Calculate SHA checksum.
     * @param buf
     * @return SHA checksum, if buf returns null 
     * @throws NoSuchAlgorithmException
     */
    public static String generateSHA1(byte[] buf) throws NoSuchAlgorithmException {
        String out = null;

        if (buf == null) {
            return null;
        }

        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.reset();
        md.update(buf);
        byte[] digested = md.digest();

        out = "";
        for (int i = 0; i < digested.length; i++) {
            out = out + new Integer(digested[i]).toString();  //bad code: byte values above 127 are treated as negative values
//            The above line is incorrect; it fails to treat type "byte" as signed.  The correction is to change it to the following:
//            (see Bug 1216:  https://pc2.ecs.csus.edu/bugzilla/show_bug.cgi?id=1216 for further discussion of this issue)
//            out = out + String.format("%02x", digested[i]&0xff) ;   //mask avoids sign-extension of negative values in byte
        }

        return out;
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
     * @return bytes from file (already loaded file), zero byte array if not loaded. 
     */
    public byte[] getBuffer() {
        if (buffer != null){
            return buffer;
        } else {
            return new byte[0];
        }
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
//        SerializedFile.file = file;
        this.file = file;           //jlc 4/14/19  changed because "static" was removed from field declaration
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
     * This method examines the bytes in the specified buffer and attempts to ascertain what type
     * of file the data represents.
     * The presence of CR/LF line terminators implies a DOS file; CR line terminators imply an (old-style)
     * MacOS file; LF terminators imply a Unix file (which includes Mac OSX files).
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

        fileType = convertFileToType;
        buffer = newbuffer;

        return true;
    }
    
    public boolean isExternalFile() {
        return externalFile;
    }
    
    /**
     * Get last error message.
     * 
     * If a method return null, a message may be set here.
     * 
     */
    public String getErrorMessage() {
        if (errorMessage == null) {
            return null;
        }
        
        return new String(errorMessage);
    }
    
    /**
     * Exception saved from a method.
     * 
     * If a method return null, an exception may be set here.
     * 
     */
    public Exception getException() {
        return exception;
    }
    
    @Override
    public String toString() {
        return name + " " + absolutePath + " ext=" + externalFile + " SHA=" + sha1sum;
    }
    /**
     * Returns a JSON string representation of this SerializedFile object.
     * 
     * @return a String containing a JSON representation of this object, or null if an error occurs during creation of the JSON string
     */
    public String toJSON() {
        
        ObjectMapper om = JSONObjectMapper.getObjectMapper();

        String jsonString ;
        try {
            jsonString = om.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
        return jsonString ;
     }
    
    /**
     * Returns a SerializedFile object constructed from the specified JSON String.
     *  
     * @param json a JSON string describing a SerializedFile object
     * @return a SerializedFile object constructed from JSON, or null if an exception occurred while mapping the JSON to an object
     */
    public SerializedFile fromJSON(String json) {

        ObjectMapper om = JSONObjectMapper.getObjectMapper();
        try {
            Object temp = om.readValue(json, SerializedFile.class);
            if (temp instanceof SerializedFile) {
                return (SerializedFile) temp;
            } else {
                // TODO REFACTOR when using a more modern Jackson jar use  JsonParseException
                throw new RuntimeException("JSON input does not properly map to SerializedFile") ;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
