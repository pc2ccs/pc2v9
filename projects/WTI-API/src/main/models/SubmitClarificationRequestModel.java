package models;

public class SubmitClarificationRequestModel {
	private String probName;
	private String message;
	
	public SubmitClarificationRequestModel(String probName, String message) {
		this.probName = probName;
		this.message = message;
	}
	
	public SubmitClarificationRequestModel() {
		
	}
	
	public String getProbName() {
		return probName;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setProbName(String probName) {
		this.probName = probName;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
