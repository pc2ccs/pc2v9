package models;

public class LoginRequestModel {
	
	public String teamName;
	public String password;
	
	public LoginRequestModel() {
		
	}
	
	public LoginRequestModel(String teamName, String password) {
		this.teamName = teamName;
		this.password = password;
	}
	
}
