package models;

import java.util.List;

public class RunModel {

	public String teamID, language, problem, judgement;
	public List<File> result;
	public long time;
	public boolean isTestRun, isPreliminary, isFinal;
	public String id;

	public RunModel(String teamID, String language, String problem, String judgement, List<File> result, long time, 
			boolean isTestRun, boolean isPreliminary, boolean isFinal, String id) {
		this.teamID = teamID;
		this.language = language;
		this.problem = problem;
		this.isTestRun = isTestRun;
		this.isPreliminary = isPreliminary;
		this.isFinal = isFinal;
		this.time = time;
		this.judgement = judgement;
		this.result = result;
		this.id = id;

	}

	public RunModel() {

	}



}

