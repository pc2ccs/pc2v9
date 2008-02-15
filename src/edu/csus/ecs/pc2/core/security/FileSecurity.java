package edu.csus.ecs.pc2.core.security;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public class FileSecurity {

    private PBEParameterSpec algorithm;

    private Cipher dcipher;

    private Cipher ecipher;

    private String contestPassword;

    private SecretKey contestSecretKey;

    private static final String CONTEST_KEY_FILENAME = "contest.key";

    private static final String RECOVERY_KEY_FILENAME = "recovery.key";

    private static final String PC2_KEY_FILENAME = "pc2.key";

    /**
     * 
     * 
     */
    public FileSecurity() {
        super();

        int iteration = 128;
        byte[] salt = { (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c, (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99 };

        algorithm = new PBEParameterSpec(salt, iteration);
    }

    /**
     * verifyPassword will attempt to open two files contest.key and recovery.key using the supplied password to decrypt the files.
     * <P>
     * On failure will throw one of the following FileSecurityException
     * <P>
     * NO_KEY_FILES_FOUND - could not find either file<br>
     * 
     * WRONG_PASSWORD - could not decrpt either file ????
     * <P>
     * On Success will set contestSecretKey, contestPassword & InitCipher and return true
P     * 
     * @param folderName
     * @param password
     * @return
     * @throws FileSecurityException
     */
    public boolean verifyPassword(String folderName, String password) throws FileSecurityException {
        return true;
    }

    /**
     * returns the contestSecretKey.
     * <P>
     * 
     * @return contestSecretKey
     */
    public SecretKey getSecretKey() {
        return contestSecretKey;

    }

    /**
     * writes out theKey into the file
     * 
     * @CONTEST_KEY_FILENAME using PBE based on password in the specified.
     * 
     * Second severs will use this to create a "new configuration"
     * 
     */
    public void saveSecretKey(String folderName, SecretKey theKey, String password) throws FileSecurityException {

    }

    /**
     * writes out contestSecretKey into folderName/fileName using PublicKey ecryption based on the PublicKey theKey passed in.
     * 
     * this method will be used to create the PC2_KEY_FILENAME & User key based files (at a later time)
     */
    public void saveSecretKey(String folderName, PublicKey theKey, String filename) throws FileSecurityException {

    }

    /**
     * writes out contestSecretKey into the file
     * 
     * @CONTEST_KEY_FILENAME using PBE based on password in the specified
     * 
     * this method will be used by the first server to create its first profile.
     * @param folderName
     * @param password
     * @throws FileSecurityException
     */
    public void saveSecretKey(String folderName, String password) throws FileSecurityException {

    }

    /**
     * will return the current contestPassword
     * 
     * @return
     */
    public String getPassword() {
        return contestPassword;
    }

    /**
     * will write out the object passed in as a SealedObject to disk using the fully qualified fileName
     * 
     * @param fileName
     * @param objectToWrite
     * @throws FileSecurityException
     * @param objectToWrite
     * @throws FileSecurityException
     */
    public void writeFile(String fileName, Serializable objectToWrite) throws FileSecurityException {

    }

    /**
     * will attempt to read a file from disk and return a decrypted object.
     * 
     * @param fileName
     * @return
     * @throws FileSecurityException
     */
    public Serializable readFile(String fileName) throws FileSecurityException {
        return null;

    }

    /**
     * private method used to generate an internal secretKey used to encrypt data written and decrypt data read from disk.
     * 
     * @param pwd
     * @return
     * @throws Exception
     */
    private SecretKey makeSecretKey(char[] pwd) throws Exception {
        SecretKey key = null;
        PBEKeySpec pbeKeySpec;
        SecretKeyFactory keyBuilder;

        try {
            pbeKeySpec = new PBEKeySpec(pwd);

            // create key
            keyBuilder = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            key = keyBuilder.generateSecret(pbeKeySpec);

        } catch (NoSuchAlgorithmException e) {
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return key;
    }

    /**
     * method used to initialize ciphers, used after a key has been generated or set.
     * 
     * @throws Exception
     */
    private void cipherInit() throws Exception {
        try {
            // Prepare the encrypter
            ecipher = Cipher.getInstance("PBEWithMD5AndDES");
            ecipher.init(Cipher.ENCRYPT_MODE, contestSecretKey, algorithm);
        } catch (NoSuchPaddingException e) {
            throw new Exception(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(e.getMessage());
        } catch (InvalidKeyException e) {
            throw new Exception(e.getMessage());
        }

        try {
            // Prepare the decrypter
            dcipher = Cipher.getInstance("PBEWithMD5AndDES");
            dcipher.init(Cipher.DECRYPT_MODE, contestSecretKey, algorithm);
        } catch (NoSuchPaddingException e) {
            throw new Exception(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(e.getMessage());
        } catch (InvalidKeyException e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Encrypts any Serializable Object passed in and returns a SealedObject
     * 
     * @param objToEncrypt
     * @param inSecretKey
     * @return
     * @throws Exception
     */
    private SealedObject encryptObject(Serializable objToEncrypt, SecretKey inSecretKey) throws Exception {

        SealedObject encryptedObject = null;

        try {

            // Seal (encrypt) the object
            encryptedObject = new SealedObject(objToEncrypt, ecipher);

        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } catch (IllegalBlockSizeException e) {
            throw new Exception(e.getMessage());
        }
        return encryptedObject;
    }

    /**
     * Decrypts any SealedObject passed in and returns a Serializable
     * 
     * @param encryptedObject
     * @param inSecretKey
     * @return
     * @throws Exception
     */
    private Serializable decryptObject(SealedObject encryptedObject, SecretKey inSecretKey) throws Exception {
        Serializable decryptedObject = null;

        try {

            // Unseal (decrypt) the class
            decryptedObject = (Serializable) encryptedObject.getObject(dcipher);

        } catch (IOException e) {
            throw new Exception(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new Exception(e.getMessage());
        } catch (IllegalBlockSizeException e) {
            throw new Exception(e.getMessage());
        } catch (BadPaddingException e) {
            throw new Exception(e.getMessage());
        }

        return decryptedObject;
    }

}