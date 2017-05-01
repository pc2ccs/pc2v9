package edu.csus.ecs.pc2.convert;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

public class Base64Test extends AbstractTestCase {
    
    /**
     * Tests whether the PC2v9 Base64 class correctly encodes a file.  Inputs are a .class file which 
     * is known to have caused a problem for the Base64 class in the past ("Problem9.class"), and the
     * known-correct Base64 encoding of that file.  The method invokes the PC2v9 Base64.encode()
     * method to encode the Problem9.class file, then compares it with the known-correct Base64 encoding.
     */
    public void testBase64Encode() {
        
        String dataDir = getDataDirectory() + File.separator;
        assertDirectoryExists(dataDir, "Missing data directory");
        
        String inputFileName = dataDir + "Problem9.class";
        assertFileExists(inputFileName, "input file '" + inputFileName + "'");
        
        String correctlyEncodedFileName = dataDir + "Problem9.CorrectlyBase64Encoded" ;
        assertFileExists(correctlyEncodedFileName, "Correct file '" + correctlyEncodedFileName + "'");
        
//        String tempDirName = "." + File.separator + "tempBase64Test" + File.separator;
//        String tempDirName = getOutputDataDirectory() + File.separator;
//        ensureDirectory(tempDirName);
        String outputFileName = getOutputTestFilename("computedBase64File");
        
        //the required files and directories exist; read input file as a byte array
        byte [] bytes = getFileBytes(inputFileName);
        assertNotNull(bytes);
        
        //encode the input file into Base64 using the class under test
        String encodedInputFile = Base64.encode(bytes);
        assertNotNull(encodedInputFile);
        
        //write the Base64-encoded text data out as a file
        String [] fileLines = new String [1];
        fileLines[0] = encodedInputFile;
        try {
            Utilities.writeLinesToFile(outputFileName, fileLines);
        } catch (FileNotFoundException e) {
            failTest("Exception writing encoded file '" + outputFileName + "': " + e.getMessage(), e);
        }
        
        File computedFile = new File(outputFileName);
        File correctFile = new File(correctlyEncodedFileName);
        try {
            //compare the two Base64 text files for equality
            assertFileContentsEquals(computedFile, correctFile);
        } catch (IOException e) {
            failTest("Exception comparing file '" + outputFileName + "' with file '" + correctlyEncodedFileName + "': "+ e.getMessage(), e);
        }
        
//        //cleanup: remove the temporary folder
//        String outputDir = getOutputDataDirectory();
//        clearDir(outputDir);
//        removeDir(outputDir);
        
    }
    
    /** Read the given binary file, and return its contents as a byte array.*/     
    private byte [] getFileBytes (String inputFileName) {
        
        File inFile = new File(inputFileName);
        assertTrue("Cannot read input file '" + inputFileName + "'", inFile.canRead());
    
      byte[] result = new byte[(int)inFile.length()];
      try {
        InputStream input = null;
        try {
          int totalBytesRead = 0;
          input = new BufferedInputStream(new FileInputStream(inFile));
          while(totalBytesRead < result.length){
            int bytesRemaining = result.length - totalBytesRead;
            //input.read() returns -1, 0, or more :
            int bytesRead = input.read(result, totalBytesRead, bytesRemaining); 
            if (bytesRead > 0){
              totalBytesRead = totalBytesRead + bytesRead;
            }
          }
          /*
           the above style is a bit tricky: it places bytes into the 'result' array; 
           'result' is an output parameter;
           the while loop usually has a single iteration only.
          */
        }
        finally {
          input.close();
        }
      }
      catch (FileNotFoundException ex) {
        assertTrue("File not found: " + ex.getMessage(),false);
      }
      catch (IOException ex) {
          assertTrue("IOException processing file '" + inputFileName + "': " + ex.getMessage(), false);
      }
      return result;
    }

}
