package edu.csus.ecs.pc2.core.model;

/**
 * This interface defines a polymorphic view of a file in the PC<sup>2</sup> API.
 * A file from the point of view of the API consists of two components: a Name and a 
 * collection of Data.
 * 
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 *
 */
public interface IFile {

    /**
     * Returns a String containing the name of this IFile object.
     * @return a String containing the file name
     */
    public String getFileName();
    
    /**
     * Returns a String containing the Base64-encoded data in this IFile object.
     * @return a Base64-encoded String
     */
    public String getBase64Data();
    
    /**
     * Returns a byte array containing the data in this IFile object.
     * @return a byte array
     */
    public byte [] getByteData();
    
    /**
     * Returns a String containing the absolute path for the name of this IFile object.
     * @return a String containing the absolute path to the filename
     */
    public String getAbsolutePath();
}
