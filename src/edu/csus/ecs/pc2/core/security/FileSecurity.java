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

    private boolean readyToReadWrite = false;

    private Crypto fileCrypt = null;

    private String contestDirectory = "." + java.io.File.separator;

    /**
     * Initialize salt, algorithm and log.

     * @param inLog - Logging file
     * @param inContestDirectory - Directory in which to read/write encrypted files
     */
    public FileSecurity(Log inLog, String inContestDirectory) {
        super();

        log = inLog;
        int iteration = 128;
        byte[] salt = { (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c, (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99 };

        readyToReadWrite = false;
        algorithm = new PBEParameterSpec(salt, iteration);

        Utilities.insureDir(inContestDirectory);
        if (!inContestDirectory.endsWith(java.io.File.separator)) {
            inContestDirectory += java.io.File.separator;
        }
        contestDirectory = inContestDirectory;

        fileCrypt = new Crypto();
        SecretKey sk = fileCrypt.generateSecretKey(fileCrypt.getPublicKey(), fileCrypt.getPrivateKey());
        fileCrypt.setSecretKey(sk);

    }

    /**
     * Returns the current value of contestDirectory
     * 
     * @return contestDirectory;
     */
    public String getContestDirectory() {
        return contestDirectory;
    }

    /**
     * verifyPassword will attempt to open the contest.key file using the supplied password to decrypt the files.
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
     * @param password
     * @return
     * @throws FileSecurityException
     */
    public boolean verifyPassword(char[] password) throws FileSecurityException {

        SealedObject objectFromDisk;
        SecretKey secretKey = null;
        KeyPair tmpKeyPair = null;

        if (!Utilities.isFileThere(contestDirectory + CONTEST_KEY_FILENAME)) {
            throw new FileSecurityException("KEY_FILE_NOT_FOUND");
        }

        try {
            objectFromDisk = (SealedObject) Utilities.readObjectFromFile(contestDirectory + CONTEST_KEY_FILENAME);
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
            tmpKeyPair = (KeyPair) decryptObject(objectFromDisk, secretKey);
        } catch (Exception e) {
            log.log(Log.INFO, "verify password - failed to decrypt object", e);
            throw new FileSecurityException("FAILED_TO_DECRYPT");
        }

        fileCrypt.setMyKeyPair(tmpKeyPair);
        SecretKey sk = fileCrypt.generateSecretKey(fileCrypt.getPublicKey(), fileCrypt.getPrivateKey());
        fileCrypt.setSecretKey(sk);

        readyToReadWrite  = true;

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
     * writes out theKey into the file contest.key
     * 
     * using PBE based on password in the specified.
     * 
     * Second severs will use this to create a "new configuration"

     * @param theKey - contestSecretKey received from initial server
     * @param password - contestPassword received from initial server
     * @throws FileSecurityException
     */
    public void saveSecretKey(SecretKey theKey, char[] password) throws FileSecurityException {

        contestSecretKey = theKey;
        contestPassword = password;

        try {
            cipherInit();
        } catch (Exception e) {
            log.log(Log.INFO, "saveSecretKey - initialize ciphers", e);
            throw new FileSecurityException("FAILED_TO_INIT_CIPHERS");
        }

        contestKeyPair = fileCrypt.getKeyPair();

        writePC2RecoveryInfo();

        SealedObject sealedSecretKey = null;

        try {
            sealedSecretKey = encryptObject(contestKeyPair, contestSecretKey);
        } catch (Exception e) {
            log.log(Log.INFO, "saveSecretKey - failed to encrypt contestSecretKey", e);
            throw new FileSecurityException("FAILED TO ENCRYPT", e);
        }

        try {
            Utilities.writeObjectToFile(contestDirectory + CONTEST_KEY_FILENAME, sealedSecretKey);
        } catch (Exception e) {
            log.log(Log.INFO, "saveSecretKey - failed to write file to disk", e);
            throw new FileSecurityException("FAILED TO WRITE", e);
        }

        readyToReadWrite = true;
    }

    /**
     * writes out contest password into folderName/fileName using PublicKey ecryption based on the PublicKey theKey passed in.
     * 
     * this method will be used to create the User key based files (at a later time)

     * @param theKey - Key used to encrypt contestPassword out to disk
     * @param filename - filename to use
     * @throws FileSecurityException
     */
    public void saveSecretKey(PublicKey theKey, String filename) throws FileSecurityException {

        // TODO: encryptObject(contestPassword, pc2pgpkey);

        try {
            Utilities.writeObjectToFile(contestDirectory + filename, contestPassword);
        } catch (Exception e) {
            log.log(Log.INFO, "writePC2RecoveryFile - failed to write file to disk", e);
            throw new FileSecurityException("FAILED TO WRITE", e);
        }
    }

    /**
     * writes out contestSecretKey into the file contest.key
     * 
     * using PBE based on password in the specified
     * 
     * this method will be used by the first server to create its first profile.

     * @param password - password to encrypt the contestSecretKey with
     * @throws FileSecurityException
     */
    public void saveSecretKey(char[] password) throws FileSecurityException {

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
            Utilities.writeObjectToFile(contestDirectory + CONTEST_KEY_FILENAME, sealedSecretKey);
        } catch (Exception e) {
            log.log(Log.INFO, "saveSecretKey - failed to write file to disk", e);
            throw new FileSecurityException("FAILED TO WRITE", e);
        }

        writePC2RecoveryInfo();

        readyToReadWrite = true;
    }

    /**
     * Used to write out PC2_KEY file containing recovery information. Encrypted using the PC2 PGP key.
     * 
     * @throws FileSecurityException
     */

    private void writePC2RecoveryInfo() throws FileSecurityException {

        PC2RecoveryInfo pc2RecoveryInfo = new PC2RecoveryInfo();

        pc2RecoveryInfo.setSecretKey(contestSecretKey);
        pc2RecoveryInfo.setPassword(contestPassword);
        pc2RecoveryInfo.setKeyPair(contestKeyPair);
        pc2RecoveryInfo.setContestDirectory(contestDirectory);

        // TODO: encryptObject(pc2RecoveryInfo, pc2pgpkey);

        try {
            Utilities.writeObjectToFile(contestDirectory + PC2_KEY_FILENAME, pc2RecoveryInfo);
        } catch (Exception e) {
            log.log(Log.INFO, "writePC2RecoveryFile - failed to write file to disk", e);
            throw new FileSecurityException("FAILED TO WRITE", e);
        }
    }

//    /**
//     * Used to read PC2_KEY file containing recovery information. Encrypted using the PC2 PGP key.
//     * 
//     * @throws FileSecurityException
//     */
//    private void readPC2RecoveryInfo() throws FileSecurityException {
//
//        PC2RecoveryInfo pc2RecoveryInfo = null;
//
//        try {
//            pc2RecoveryInfo = (PC2RecoveryInfo) Utilities.readObjectFromFile(contestDirectory+ PC2_KEY_FILENAME);
//        } catch (Exception e) {
//            log.log(Log.INFO, "writePC2RecoveryFile - failed to write file to disk", e);
//            throw new FileSecurityException("FAILED TO WRITE", e);
//        }
//
//        // TODO: encryptObject(pc2RecoveryInfo, pc2pgpkey);
//
//        System.out.println("The password: " + new String(pc2RecoveryInfo.getPassword()));
//        System.out.println("The contestSecretKey: " + pc2RecoveryInfo.getSecretKey());
//        System.out.println("The contestKeyPair: " + pc2RecoveryInfo.getKeyPair());
//        System.out.println("The contestDirectory: " + pc2RecoveryInfo.getContestDirectory());
//
//    }

    /**
     * will return the current contestPassword
     * 
     * @return returns the contestPassword
     */
    public String getPassword() {
        return new String(contestPassword);
    }

    /**
     * Method used to write out an encrypted object to disk.
     * 
     * @param fileName - fileName to write out
     * @param objectToWrite - Serializable object to write to disk
     * @throws FileSecurityException
     */
    public void writeSealedFile(String fileName, Serializable objectToWrite) throws FileSecurityException {

        if (!readyToReadWrite) {
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
     * Method used to read and decrypt an object from disk.
     * 
     * @param fileName - File name to read off disk
     * 
     * @return the decrypted Serializable object read from disk
     * @throws FileSecurityException
     */
    public Serializable readSealedFile(String fileName) throws FileSecurityException {

        SealedObject sealedObjectFromDisk;
        Serializable objectToReturn = null;

        if (!readyToReadWrite) {
            throw new FileSecurityException("NOT_READY_TO_READ");
        }
        
        try {
            sealedObjectFromDisk = (SealedObject) Utilities.readObjectFromFile(fileName);
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

