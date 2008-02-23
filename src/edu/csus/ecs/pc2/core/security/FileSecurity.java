package edu.csus.ecs.pc2.core.security;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.KeyPair;
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

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;

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

    private char[] contestPassword;

    private SecretKey contestSecretKey;

    private KeyPair contestKeyPair;
    
    private Log log;
    
    private static final String CONTEST_KEY_FILENAME = "contest.key";

    private static final String PC2_KEY_FILENAME = "pc2.key";
    
    private boolean readyToWrite = false;

    private Crypto fileCrypt = new Crypto();
    /**
     * Initialize salt, algorithm and log.
     * 
     */
    public FileSecurity(Log inLog) {
        super();

        log = inLog;
        int iteration = 128;
        byte[] salt = { (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c, (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99 };
        
        readyToWrite = false;
        algorithm = new PBEParameterSpec(salt, iteration);
        
    }

    /**
     * verifyPassword will attempt to open two files contest.key using the supplied password to decrypt the files.
     * <P>
     * On failure will throw one of the following FileSecurityException
     * <P>
     * KEY_FILE_NOT_FOUND - could not find either file<br>
     * <P>
     * FAILED_TO_READ_FILE - the file could not be read<br>
     * <P>
     * FAILED_TO_CREATE_KEY - failed to generate key from password
     * <P>
     * FAILED_TO_DECRYPT - could not decrpt either file ????
     * <P>
     * FAILED_TO_INIT_CIPHERS - could not init ciphers with key
     * <P>
     * On Success will set contestSecretKey, contestPassword & InitCipher and return true
     * 
     * @param folderName
     * @param password
     * @return
     * @throws FileSecurityException 
     */
    public boolean verifyPassword(String folderName, char[] password) throws FileSecurityException {

        SealedObject objectFromDisk;
        SecretKey secretKey = null;
        KeyPair tmpKeyPair = null;
        
        Utilities.insureDir(folderName);
        if (!folderName.endsWith(java.io.File.separator)) {
            folderName += java.io.File.separator;
        }
        
        if (!Utilities.isFileThere(folderName + CONTEST_KEY_FILENAME)) {
            throw new FileSecurityException("KEY_FILE_NOT_FOUND");
        }
        
        try {
            objectFromDisk = (SealedObject) Utilities.readObjectFromFile(folderName + CONTEST_KEY_FILENAME);
        } catch (Exception e) {
            log.log(Log.INFO, "verify password - failed to read file from disk", e);
            throw new FileSecurityException("FAILED_TO_READ_FILE");
        }
        
        try {
            secretKey = makeSecretKey(password);
        } catch (Exception e) {
            log.log(Log.INFO, "verify password - failed to create key from password", e);
            throw new FileSecurityException("FAILED_TO_CREATE_KEY");
        }
        
        contestPassword = password;
        contestSecretKey = secretKey;
        
        try {
            cipherInit();
        } catch (Exception e) {
            log.log(Log.INFO, "verify password - initialize ciphers", e);
            throw new FileSecurityException("FAILED_TO_INIT_CIPHERS");
        }
        
        try {
            tmpKeyPair = (KeyPair)decryptObject(objectFromDisk, secretKey);
        } catch (Exception e) {
            log.log(Log.INFO, "verify password - failed to decrypt object", e);
            throw new FileSecurityException("FAILED_TO_DECRYPT");
        }
        
        fileCrypt.setMyKeyPair(tmpKeyPair);
        
        readyToWrite = true;
        
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
    public void saveSecretKey(String folderName, SecretKey theKey, char[] password) throws FileSecurityException {

        Utilities.insureDir(folderName);
        if (!folderName.endsWith(java.io.File.separator)) {
            folderName += java.io.File.separator;
        }
        
        contestSecretKey = theKey;
        contestPassword = password;
        
        try {
            cipherInit();
        } catch (Exception e) {
            log.log(Log.INFO, "saveSecretKey - initialize ciphers", e);
            throw new FileSecurityException("FAILED_TO_INIT_CIPHERS");
        }
        
        contestKeyPair = fileCrypt.getKeyPair();

        writePC2RecoveryInfo(folderName, password);
        
        SealedObject sealedSecretKey = null;
        
        try { 
            sealedSecretKey = encryptObject(contestKeyPair, contestSecretKey);
        } catch (Exception e) {
            log.log(Log.INFO, "saveSecretKey - failed to encrypt contestSecretKey", e);
            throw new FileSecurityException("FAILED TO ENCRYPT", e);
        }
        
        try {
            Utilities.writeObjectToFile(folderName + CONTEST_KEY_FILENAME, sealedSecretKey );
        } catch (Exception e) {
            log.log(Log.INFO, "saveSecretKey - failed to write file to disk", e);
            throw new FileSecurityException("FAILED TO WRITE", e);
        }
        
    }

    /**
     * writes out contest password into folderName/fileName using PublicKey ecryption based on the PublicKey theKey passed in.
     * 
     * this method will be used to create the User key based files (at a later time)
     */
    public void saveSecretKey(String folderName, PublicKey theKey, String filename) throws FileSecurityException {

        Utilities.insureDir(folderName);
        if (!folderName.endsWith(java.io.File.separator)) {
            folderName += java.io.File.separator;
        }
        
        // TODO: encryptObject(contestPassword, pc2pgpkey);
        
        try {
            Utilities.writeObjectToFile(folderName + PC2_KEY_FILENAME, contestPassword);
        } catch (Exception e) {
            log.log(Log.INFO, "writePC2RecoveryFile - failed to write file to disk", e);
            throw new FileSecurityException("FAILED TO WRITE", e);
        }
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
    public void saveSecretKey(String folderName, char[] password) throws FileSecurityException {
        
        Utilities.insureDir(folderName);
        if (!folderName.endsWith(java.io.File.separator)) {
            folderName += java.io.File.separator;
        }
        
        SealedObject sealedSecretKey = null;
        SecretKey secretKey = null;

        contestPassword = password;
        try {
            contestSecretKey = makeSecretKey(password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            cipherInit();
        } catch (Exception e) {
            log.log(Log.INFO, "saveSecretKey - initialize ciphers", e);
            throw new FileSecurityException("FAILED_TO_INIT_CIPHERS");
        }

        contestKeyPair = fileCrypt.getKeyPair();
        
        try {
            sealedSecretKey = encryptObject(contestKeyPair, secretKey);
        } catch (Exception e) {
            log.log(Log.INFO, "saveSecretKey - failed to encrypt contestSecretKey", e);
            throw new FileSecurityException("FAILED TO ENCRYPT", e);
        }
        
        try {
            Utilities.writeObjectToFile(folderName + CONTEST_KEY_FILENAME, sealedSecretKey );
        } catch (Exception e) {
            log.log(Log.INFO, "saveSecretKey - failed to write file to disk", e);
            throw new FileSecurityException("FAILED TO WRITE", e);
        }
        
        writePC2RecoveryInfo(folderName, password);
    }

    /**
     * Used to write out PC2_KEY file containing recovery information. Encrypted using the
     * PC2 PGP key.
     * 
     * @param folderName
     * @param password
     * @throws FileSecurityException 
     */
    
    private void writePC2RecoveryInfo(String folderName, char[] password) throws FileSecurityException {
        
        Utilities.insureDir(folderName);
        if (!folderName.endsWith(java.io.File.separator)) {
            folderName += java.io.File.separator;
        }
        
        PC2RecoveryInfo pc2RecoveryInfo = new PC2RecoveryInfo();
        
        pc2RecoveryInfo.setSecretKey(contestSecretKey);
        pc2RecoveryInfo.setPassword(contestPassword);
        pc2RecoveryInfo.setKeyPair(contestKeyPair);
        
        // TODO: encryptObject(pc2RecoveryInfo, pc2pgpkey);
        
        try {
            Utilities.writeObjectToFile(folderName + PC2_KEY_FILENAME, pc2RecoveryInfo);
        } catch (Exception e) {
            log.log(Log.INFO, "writePC2RecoveryFile - failed to write file to disk", e);
            throw new FileSecurityException("FAILED TO WRITE", e);
        }
    }
    
    /**
     * will return the current contestPassword
     * 
     * @return
     */
    public String getPassword() {
        return new String(contestPassword);
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
    private void writeFile(String fileName, Serializable objectToWrite) throws FileSecurityException {

        if (!readyToWrite) {
            throw new FileSecurityException("NOT_READY_TO_WRITE");
        }
        
        SealedObject sealedObjectToWrite;
        
        try {
            sealedObjectToWrite = encryptObject(objectToWrite, contestSecretKey);
        } catch (Exception e) {
            log.log(Log.INFO, "writeFile - failed to encrypt object", e);
            throw new FileSecurityException("FAILED TO ENCRYPT", e);
        }
        
        try {
            Utilities.writeObjectToFile(fileName, sealedObjectToWrite);
        } catch (Exception e) {
            log.log(Log.INFO, "writeFile - failed to write file to disk", e);
            throw new FileSecurityException("FAILED TO WRITE", e);
        }

    }

    /**
     * 
     * @param fileName
     * @param objectToWrite
     * @throws FileSecurityException
     */
    public void writeSealedFile(String fileName, Serializable objectToWrite) throws FileSecurityException {

        if (!readyToWrite) {
            throw new FileSecurityException("NOT_READY_TO_WRITE");
        }
        
        SealedObject sealedObjectToWrite;
        
        try {
            sealedObjectToWrite = fileCrypt.encrypt(objectToWrite);
        } catch (Exception e) {
            log.log(Log.INFO, "writeFile - failed to encrypt object", e);
            throw new FileSecurityException("FAILED TO ENCRYPT", e);
        }
        
        try {
            Utilities.writeObjectToFile(fileName, sealedObjectToWrite);
        } catch (Exception e) {
            log.log(Log.INFO, "writeFile - failed to write file to disk", e);
            throw new FileSecurityException("FAILED TO WRITE", e);
        }


    
    }

    /**
     * 
     * @param fileName
     * @return
     * @throws FileSecurityException
     */
    public Serializable readSealedFile(String fileName) throws FileSecurityException {

        SealedObject sealedObjectFromDisk;
        Serializable objectToReturn = null;
        
        try {
            sealedObjectFromDisk = (SealedObject)Utilities.readObjectFromFile(fileName);
        } catch (Exception e) {
            log.log(Log.INFO, "readFile - failed to read file from disk", e);
            throw new FileSecurityException("FAILED TO READ", e);
        }

        
        try {
            objectToReturn = fileCrypt.decrypt(sealedObjectFromDisk);
        } catch (Exception e) {
            log.log(Log.INFO, "readFile - failed to decrypt object", e);
            throw new FileSecurityException("FAILED TO DECRYPT", e);
        }
        
        return objectToReturn;
    }

    
    /**
     * will attempt to read a file from disk and return a decrypted object.
     * 
     * @param fileName
     * @return
     * @throws FileSecurityException
     */
    private Serializable readFile(String fileName) throws FileSecurityException {

        SealedObject sealedObjectFromDisk;
        Serializable objectToReturn = null;
        
        try {
            sealedObjectFromDisk = (SealedObject)Utilities.readObjectFromFile(fileName);
        } catch (Exception e) {
            log.log(Log.INFO, "readFile - failed to read file from disk", e);
            throw new FileSecurityException("FAILED TO READ", e);
        }

        
        try {
            objectToReturn = decryptObject(sealedObjectFromDisk, contestSecretKey);
        } catch (Exception e) {
            log.log(Log.INFO, "readFile - failed to decrypt object", e);
            throw new FileSecurityException("FAILED TO DECRYPT", e);
        }
        
        return objectToReturn;

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