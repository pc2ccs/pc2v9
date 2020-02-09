package edu.csus.ecs.pc2.core.model;

import java.util.Base64;

import edu.csus.ecs.pc2.core.model.IFile;

/**
 * This class is a concrete implementation of a file from the point of view of the PC<sup>2</sup> API.
 * 
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 *
 */
public class IFileImpl implements IFile {
    
    private String name ;  //the name of the file
    
    private byte [] data;  //the raw data bytes of the file

    private String absolutePath;
    
    /**
     * Constructs a IFileImpl object with the specified name and containing data bytes defined
     * by decoding the specified base64 String.
     * 
     * @param name -- the name of the file
     * @param base64Data -- a Base64-encoded String containing the file data
     */
    public IFileImpl(String name, String base64Data) {
        this.name = name;
        this.absolutePath = "./" + name;

        //get a base64 decoder
        Base64.Decoder decoder = Base64.getDecoder();
        //use the decoder to both check the validity of, and to store, the byte data
        try {
            data = decoder.decode(base64Data);
        } catch (IllegalArgumentException e) {
            //probably should log this issue; not sure how...
            //TODO: probably should propagate this exception?
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBase64Data() {
        //get a base64 decoder
        Base64.Encoder encoder = Base64.getEncoder();
        //use the encoder to create a Base64 String
        String base64String = encoder.encodeToString(data);

        return base64String;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getByteData() {
        return data;
    }

    @Override
    public String getAbsolutePath() {
        return this.absolutePath;
    }
}
