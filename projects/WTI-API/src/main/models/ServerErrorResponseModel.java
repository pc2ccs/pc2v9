package models;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ServerErrorResponseModel {
	
	public Response.Status errorCode;
	public String message;
	
	public ServerErrorResponseModel(Status code, String message) {
		this.errorCode = code;
		this.message = message;
	}
	
	public ServerErrorResponseModel() {
	}
}
