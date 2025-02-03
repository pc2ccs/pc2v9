package models;

import java.util.ArrayList;

/**
 * This class encapsulates the WTI view of a PC2 clarification (which includes "announcement clarifications").
 *
 */
public class ClarificationModel {

	public String recipient, problem, question, answer;
	public ArrayList<String> teamRecipients, groupRecipients;
	public String id;
	public long time;
	public boolean isAnswered;
	
	public ClarificationModel(String recipient, ArrayList<String> teamRecipients, ArrayList<String> groupRecipients, 
								String problem, String question, String answer, String id, long time, boolean isAnswered) {
		super();
		this.recipient = recipient;
		this.teamRecipients = teamRecipients;
		this.groupRecipients = groupRecipients;
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
