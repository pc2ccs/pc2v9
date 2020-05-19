package models;

public class ClarificationModel {

	public String recipient, problem, question, answer;
	public String id;
	public long time;
	public boolean isAnswered;

	public ClarificationModel(String recipient, String problem, String question, String answer, long time, boolean isAnswered) {
		this.recipient = recipient;
		this.problem = problem;
		this.question = question;
		this.answer = answer;
		this.time = time;
		this.isAnswered = isAnswered;
		
	}
	
	public ClarificationModel(String recipient, String problem, String question, String answer, String id, long time,
			boolean isAnswered) {
		super();
		this.recipient = recipient;
		this.problem = problem;
		this.question = question;
		this.answer = answer;
		this.id = id;
		this.time = time;
		this.isAnswered = isAnswered;
	}
	
	public ClarificationModel() {
		
	}
}
