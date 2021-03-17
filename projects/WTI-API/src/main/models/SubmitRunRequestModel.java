package models;

public class SubmitRunRequestModel {
	
	public String probName;
	public String language;
	public File mainFile;
	public File[] extraFiles;
	public File testFile;
	public File[] additionalTestFiles;
	public boolean isTest;
	public String osName = "No OS name set";

	public SubmitRunRequestModel(String probName, String language, File mainFile, File[] extraFiles, File testFile,
			boolean isTest) {
		super();
		this.probName = probName;
		this.language = language;
		this.mainFile = mainFile;
		this.extraFiles = extraFiles;
		this.testFile = testFile;
		this.isTest = isTest;
	}
	
	public SubmitRunRequestModel(String probName, String language, File mainFile, File[] extraFiles, File testFile,
			File[] additionalTestFiles, boolean isTest) {
		super();
		this.probName = probName;
		this.language = language;
		this.mainFile = mainFile;
		this.extraFiles = extraFiles;
		this.testFile = testFile;
		this.additionalTestFiles = additionalTestFiles;
		this.isTest = isTest;
	}
	
	public SubmitRunRequestModel() {
		
	}
	

	
	public File[] getAdditionalTestFiles() {
		return additionalTestFiles;
	}

	public void setAdditionalTestFiles(File[] additionalTestFiles) {
		this.additionalTestFiles = additionalTestFiles;
	}
	
	public String getProbName() {
		return probName;
	}

	public void setProbName(String probName) {
		this.probName = probName;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public File getMainFile() {
		return mainFile;
	}

	public void setMainFile(File mainFile) {
		this.mainFile = mainFile;
	}

	public File[] getExtraFiles() {
		return extraFiles;
	}

	public void setExtraFiles(File[] extraFiles) {
		this.extraFiles = extraFiles;
	}

	public File getTestFile() {
		return testFile;
	}

	public void setTestFile(File testFile) {
		this.testFile = testFile;
	}

	public boolean isTest() {
		return isTest;
	}

	public void setTest(boolean isTest) {
		this.isTest = isTest;
	}
	
	public String getOSName() {
		return osName;
	}

}


