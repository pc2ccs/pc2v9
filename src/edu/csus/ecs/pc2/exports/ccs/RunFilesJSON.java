package edu.csus.ecs.pc2.exports.ccs;

import java.util.Base64;
import java.util.List;

import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * RunFiles information in CLICS 2014 Submission format.
 * 
 * @author pc2@ecs.csus.edu
 */

public class RunFilesJSON {

    /**
     * Returns a JSON string describing the specified {@link RunFilesJSON} object in the format defined by the 
     * CLICS 2014 Submission format.  Note that this format does not include all of the fields defined in a PC2
     * {@link RunFiles} object; this method returns JSON containing only the data specified by the CLICS format.
     * <P>
     * Example of a returned JSON string:
     * <pre>
     *    [{"filename":"a.java","content":"<base64_string>"},{"filename":"helper.java","content":"<base64_string>"}]
     * </pre>
     * 
     * @param runFiles - the RunFiles object whose JSON form is being requested
     * 
     * @return a JSON string describing the RunFiles object
     * 
     */
    public String createJSON(RunFiles runFiles) {

        if (runFiles == null) {
            return "[]";
        }

        StringBuffer buffer = new StringBuffer();

        //Template:  [{"filename":"a.java","content":"<base64_string>"},{"filename":"helper.java","content":"<base64_string>"}]
        
        SerializedFile mainFile = runFiles.getMainFile();
        
        //add the main file element to the buffer
        if (mainFile != null) {
            buffer.append('{');
            
            buffer.append(pair("filename", mainFile.getName()));
            buffer.append(",");     
            
            byte[] mainFileBuf = mainFile.getBuffer();
            String encoded = Base64.getEncoder().encodeToString(mainFileBuf);
            buffer.append(pair("content", encoded));
            
            buffer.append("}");
        }
        
        //add each of the other file elements (if any) to the buffer
        SerializedFile [] otherFiles = runFiles.getOtherFiles();
        if (otherFiles != null) {

            for (int i = 0; i < otherFiles.length; i++) {
                buffer.append(",");

                buffer.append('{');

                buffer.append(pair("filename", otherFiles[i].getName()));
                buffer.append(",");

                byte[] otherFileBuf = otherFiles[i].getBuffer();
                String encoded = Base64.getEncoder().encodeToString(otherFileBuf);
                buffer.append(pair("content", encoded));

                buffer.append("}");
            }
        }

        // return the collected standings as elements of a JSON array
        return "[" + buffer.toString() + "]";
    }

    public static String join(String delimit, List<String> list) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            buffer.append(list.get(i));
            if (i < list.size() - 1) {
                buffer.append(delimit);
            }
        }
        return buffer.toString();
    }

    /*
     * these should be a utility class
     */
    @SuppressWarnings("unused")
    private String pair(String name, long value) {
        return "\"" + name + "\":" + value;
    }

    private String pair(String name, String value) {
        return "\"" + name + "\":\"" + value + "\"";
    }
}
