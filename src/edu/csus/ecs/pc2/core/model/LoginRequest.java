package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

/**
 * A request to clientId to a contest server.
 * 
 * @author pc2@ecs.csus
 */

// $HeadURL$
public class LoginRequest implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -911355815843431017L;

    public static final String SVN_ID = "$Id: Controller.java 23 2007-03-23 05:47:13Z laned $";

    private ClientId clientId;
    private String password;
    private String clientTypeName;

    /**
     * Create a request
     * @param clientId - client number and type
     * @param password - password for clientId
     * @param clientTypeName - clienttype name, ie "team", "judge"
     */
    public LoginRequest(ClientId clientId, String password, String clientTypeName) {
        super();
        this.clientId = clientId;
        this.password = password;
        this.clientTypeName = clientTypeName;
    }
    
    public String getClientTypeName() {
        return clientTypeName;
    }

    public void setClientTypeName(String clientTypeName) {
        this.clientTypeName = clientTypeName;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ClientId getClientId() {
        return clientId;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }
}
