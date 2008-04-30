package edu.csus.ecs.pc2.core.security;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

/**
 * This based on code from Aviran Mordo http://aviran.mordos.com
 * http://www.aviransplace.com/2004/10/12/using-rsa-encryption-with-java/3/
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 *
 */
public final class KeyUtilities {

    private KeyUtilities() {
        super();
    }

    public static String decryptFile(String srcFileName, PrivateKey key) throws Exception {
        StringBuffer buffer = new StringBuffer();
        
        InputStream inputReader = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            //RSA encryption data size limitations are slightly less than the key modulus size,
            //depending on the actual padding scheme used (e.g. with 1024 bit (128 byte) RSA key,
            //the size limit is 117 bytes for PKCS#1 v 1.5 padding. (http://www.jensign.com/JavaScience/dotnet/RSAEncrypt/)
            byte[] buf = new byte[128];
            int bufl;
            // init the Cipher object for Encryption...
            cipher.init(Cipher.DECRYPT_MODE, key);

            // start FileIO
            inputReader = new FileInputStream(srcFileName);
            while ( (bufl = inputReader.read(buf)) != -1) {
                byte[] decryptedText = null;
                decryptedText = cipher.doFinal(copyBytes(buf,bufl));
                buffer.append(new String(decryptedText));
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (inputReader != null) {
                    inputReader.close();
                }
            } catch (Exception e) {
               e.printStackTrace();
            } // end of inner try, catch (Exception)...
        }
        return buffer.toString();
    }
    
    /**
     * Writes a clearText string RSA encrypted with the privided Public key.
     * 
     * @param clearText
     * @param destFileName
     * @param key
     * @throws Exception
     */
    public static void encryptString(String clearText, String destFileName, PublicKey key) throws Exception {
        OutputStream outputWriter = null;
        try {

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            byte[] buf = new byte[100];
            int bufl;
            // init the Cipher object for Encryption…
            cipher.init(Cipher.ENCRYPT_MODE, key);

            // start FileIO
            outputWriter = new FileOutputStream(destFileName);
            byte[] completeBuffer = clearText.getBytes("UTF8");
            int bufferIndex = 0;
            while(bufferIndex < completeBuffer.length) {
                if (bufferIndex + 100 > completeBuffer.length) {
                    bufl = completeBuffer.length-bufferIndex;
                } else {
                    bufl = 100;
                }
                for (int i = bufferIndex; i < bufferIndex+bufl; i++) {
                    buf[i-bufferIndex] = completeBuffer[i];
                }
                bufferIndex += 100;
                byte[] encText = null;
                encText = cipher.doFinal(copyBytes(buf, bufl));
                outputWriter.write(encText);
            }
            outputWriter.flush();
        } finally {
            try {
                if (outputWriter != null) {
                    outputWriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static byte[] copyBytes(byte[] arr, int length) {
      byte[] newArr = null;
      if (arr.length == length) {
        newArr = arr;
      } else {
        newArr = new byte[length];
        for (int i = 0; i < length; i++) {
          newArr[i] = (byte) arr[i];
        }
      }
      return newArr;
    }
    
 }
