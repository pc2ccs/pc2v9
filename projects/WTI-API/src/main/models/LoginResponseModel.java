package models;

public class LoginResponseModel {

	public String teamName;
	public String teamId;
	
	public LoginResponseModel() {
		
	}
	
	public LoginResponseModel(String teamName, String cookieValue) {
		this.teamName = teamName;
		this.teamId = cookieValue;
	}

}
