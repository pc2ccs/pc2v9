package edu.csus.ecs.pc2.core.security;

import java.io.Serializable;

import javax.crypto.SecretKey;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 *
 * Inner class used to save PC2 recovery file using PC2 PGP Key.
 * 
 */
class PC2RecoveryInfo implements Serializable {
    /**
     * Inner class used to save recovery information to disk.
     */
    private static final long serialVersionUID = 1664370850668297030L;
    private SecretKey secretKey;
    private char[] password;
    
    public SecretKey getSecretKey() {
        return secretKey;
    }
    
    public char[] getPassword() {
        return password;
    }
    
    public void setPassword(char[] inPassword) {
        password = inPassword;
    }
    
    public void setSecretKey(SecretKey inSecretKey) {
        secretKey = inSecretKey;
    }
}
