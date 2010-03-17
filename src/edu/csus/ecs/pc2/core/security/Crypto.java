package edu.csus.ecs.pc2.core.security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Encryption/Decryption Class for secure data transmission
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class Crypto {

    public static final String SVN_ID = "$Id$";

    /**
     * KeyPair is used to store the Private/Public Keypair for each instance.
     * 
     */
    private KeyPair keyPair = null;

    /**
     * SecretKey is generated using a Private and Public keys passed in. Typically, the SecretKey is generated from My Private Key
     * and another parties Public Key.
     * 
     * KeyPairs used to generate this SecretKey must be created using the same DHParameterSpec, otherwise the Keys are not
     * compatible.
     * 
     */
    private SecretKey secretKey = null;

    /**
     * Encryption type used to Generate KeyPairs and Ciphers.
     */
    private String encryptionType = "DES";

    private String agreementType = "DH";

    /**
     * Constructor with no parameters, will generate a KeyPair.
     * 
     */
    public Crypto() {
        super();
        setMyKeyPair(generateKeyPair());
    }

    /**
     * Constructor with the KeyPair passed in.
     * 
     * @param keyPair
     */
    public Crypto(KeyPair keyPair) {
        super();
        setMyKeyPair(keyPair);
    }

    /**
     * Returns the Public Key.
     * 
     * @return the public key
     */
    public PublicKey getPublicKey() {
        return getKeyPair().getPublic();
    }

    /**
     * Returns the Private Key.
     * 
     * @return the private key
     */
    public PrivateKey getPrivateKey() {
        return getKeyPair().getPrivate();
    }

    /**
     * Encrypts an object using the internal SecretKey.
     * 
     * @param objToEncrypt
     * @return the sealed object
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     */
    public SealedObject encrypt(Serializable objToEncrypt) throws CryptoException {
        return (encrypt(objToEncrypt, getSecretKey()));
    }

    /**
     * Encrypts an object using the passed in SecretKey.
     * 
     * @param objToEncrypt
     * @param inSecretKey
     * @return the sealed object
     * @throws IOException
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public SealedObject encrypt(Serializable objToEncrypt, SecretKey inSecretKey) throws CryptoException {

        SealedObject encryptedObject = null;

        try {
            // Prepare the encrypter
            Cipher ecipher = Cipher.getInstance(encryptionType);
            ecipher.init(Cipher.ENCRYPT_MODE, inSecretKey);

            // Seal (encrypt) the object
            encryptedObject = new SealedObject(objToEncrypt, ecipher);

        } catch (IOException e) {
            e.printStackTrace();
            throw new CryptoException(e.getMessage());
        } catch (IllegalBlockSizeException e) {
            throw new CryptoException(e.getMessage());
        } catch (NoSuchPaddingException e) {
            throw new CryptoException(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e.getMessage());
        } catch (InvalidKeyException e) {
            throw new CryptoException(e.getMessage());
        }

        return encryptedObject;
    }

    /**
     * Decrypts the passed in Sealed Object using the internal SecretKey.
     * 
     * @param encryptedObject
     * @return the unecrypted object
     * @throws CryptoException
     */
    public Serializable decrypt(SealedObject encryptedObject) throws CryptoException {
        return decrypt(encryptedObject, getSecretKey());
    }

    /**
     * Decrypts the passed in Sealed Object using the passed in SecretKey.
     * 
     * @param encryptedObject
     * @param inSecretKey
     * @return the unecrypted object
     * @throws CryptoException
     */
    public Serializable decrypt(SealedObject encryptedObject, SecretKey inSecretKey) throws CryptoException {
        Serializable decryptedObject = null;

        try {

            // Get the algorithm used to seal the object
            String algoName = encryptedObject.getAlgorithm();

            // Prepare the decrypter
            Cipher dcipher = Cipher.getInstance(algoName);
            dcipher.init(Cipher.DECRYPT_MODE, inSecretKey);

            // Unseal (decrypt) the class
            decryptedObject = (Serializable) encryptedObject.getObject(dcipher);

        } catch (IOException e) {
            throw new CryptoException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new CryptoException(e.getMessage());
        } catch (IllegalBlockSizeException e) {
            throw new CryptoException(e.getMessage());
        } catch (BadPaddingException e) {
            throw new CryptoException(e.getMessage());
        } catch (NoSuchPaddingException e) {
            throw new CryptoException(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e.getMessage());
        } catch (InvalidKeyException e) {
            throw new CryptoException(e.getMessage());
        }

        return decryptedObject;
    }

    /**
     * Returns the internal KeyPair
     * 
     * @return the key pair
     */
    public KeyPair getKeyPair() {
        return keyPair;
    }

    /**
     * Sets the internal KeyPair using the passed in parameter.
     * 
     * @param myKeyPair
     */
    public void setMyKeyPair(KeyPair myKeyPair) {
        this.keyPair = myKeyPair;
    }

    /**
     * Generates a SecretKey based on the Public/Private Keys passed in. The pair of keys must be generated using the same
     * DHParamSpec to successfully generate a SecretKey using a KeyAgreement.
     * 
     * @param publicKey
     * @param privateKey
     * @return the secret key
     */
    public SecretKey generateSecretKey(PublicKey publicKey, PrivateKey privateKey) {
        SecretKey outSecretKey = null;
        try {
            // Get the generated public and private keys
            // Prepare to generate the secret key with the private key and public key of the other party
            KeyAgreement ka = KeyAgreement.getInstance("DH");
            ka.init(privateKey);
            ka.doPhase(publicKey, true);

            // Specify the type of key to generate;
            // see e458 Listing All Available Symmetric Key Generators
            String algorithm = encryptionType;

            // Generate the secret key
            outSecretKey = ka.generateSecret(algorithm);

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return outSecretKey;
    }

    // The 1024 bit Diffie-Hellman modulus values used by SKIP
    private byte[] skip1024ModulusBytes = { (byte) 0xF4, (byte) 0x88, (byte) 0xFD, (byte) 0x58, (byte) 0x4E, (byte) 0x49,
            (byte) 0xDB, (byte) 0xCD, (byte) 0x20, (byte) 0xB4, (byte) 0x9D, (byte) 0xE4, (byte) 0x91, (byte) 0x07, (byte) 0x36,
            (byte) 0x6B, (byte) 0x33, (byte) 0x6C, (byte) 0x38, (byte) 0x0D, (byte) 0x45, (byte) 0x1D, (byte) 0x0F, (byte) 0x7C,
            (byte) 0x88, (byte) 0xB3, (byte) 0x1C, (byte) 0x7C, (byte) 0x5B, (byte) 0x2D, (byte) 0x8E, (byte) 0xF6, (byte) 0xF3,
            (byte) 0xC9, (byte) 0x23, (byte) 0xC0, (byte) 0x43, (byte) 0xF0, (byte) 0xA5, (byte) 0x5B, (byte) 0x18, (byte) 0x8D,
            (byte) 0x8E, (byte) 0xBB, (byte) 0x55, (byte) 0x8C, (byte) 0xB8, (byte) 0x5D, (byte) 0x38, (byte) 0xD3, (byte) 0x34,
            (byte) 0xFD, (byte) 0x7C, (byte) 0x17, (byte) 0x57, (byte) 0x43, (byte) 0xA3, (byte) 0x1D, (byte) 0x18, (byte) 0x6C,
            (byte) 0xDE, (byte) 0x33, (byte) 0x21, (byte) 0x2C, (byte) 0xB5, (byte) 0x2A, (byte) 0xFF, (byte) 0x3C, (byte) 0xE1,
            (byte) 0xB1, (byte) 0x29, (byte) 0x40, (byte) 0x18, (byte) 0x11, (byte) 0x8D, (byte) 0x7C, (byte) 0x84, (byte) 0xA7,
            (byte) 0x0A, (byte) 0x72, (byte) 0xD6, (byte) 0x86, (byte) 0xC4, (byte) 0x03, (byte) 0x19, (byte) 0xC8, (byte) 0x07,
            (byte) 0x29, (byte) 0x7A, (byte) 0xCA, (byte) 0x95, (byte) 0x0C, (byte) 0xD9, (byte) 0x96, (byte) 0x9F, (byte) 0xAB,
            (byte) 0xD0, (byte) 0x0A, (byte) 0x50, (byte) 0x9B, (byte) 0x02, (byte) 0x46, (byte) 0xD3, (byte) 0x08, (byte) 0x3D,
            (byte) 0x66, (byte) 0xA4, (byte) 0x5D, (byte) 0x41, (byte) 0x9F, (byte) 0x9C, (byte) 0x7C, (byte) 0xBD, (byte) 0x89,
            (byte) 0x4B, (byte) 0x22, (byte) 0x19, (byte) 0x26, (byte) 0xBA, (byte) 0xAB, (byte) 0xA2, (byte) 0x5E, (byte) 0xC3,
            (byte) 0x55, (byte) 0xE9, (byte) 0x2F, (byte) 0x78, (byte) 0xC7 };

    // The SKIP 1024 bit modulus
    private BigInteger skip1024Modulus = new BigInteger(1, skip1024ModulusBytes);

    // The base used with the SKIP 1024 bit modulus
    private BigInteger skip1024Base = BigInteger.valueOf(2);

    /**
     * This method is used to Generate a KeyPair using the DHParmSpec.
     * 
     * @return the key pair
     */
    public KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(agreementType);
            DHParameterSpec dhParamSpec = new DHParameterSpec(skip1024Modulus, skip1024Base);
            keyGen.initialize(dhParamSpec);
            keyPair = keyGen.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyPair;
    }

    /**
     * Returns the internal SecretKey.
     * 
     * @return the secret key
     */
    public SecretKey getSecretKey() {
        return secretKey;
    }

    /**
     * Sets the internal SecretKey
     * 
     * @param secretKey
     */
    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }
}
