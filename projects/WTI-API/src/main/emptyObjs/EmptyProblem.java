package emptyObjs;

import edu.csus.ecs.pc2.api.IProblem;

public class EmptyProblem implements IProblem {

	@Override
	public byte[] getJudgesAnswerFileContents() {
		return new byte[0];
	}

	@Override
	public String getJudgesAnswerFileName() {
		return "Does_Not_Exist";
	}

	@Override
	public byte[] getJudgesDataFileContents() {
		return new byte[0];
	}

	@Override
	public String getJudgesDataFileName() {
		return "Does_Not_Exist";
	}

	@Override
	public String getName() {
		return "Does_Not_Exist";
	}

	@Override
	public String getShortName() {
		return "Does_Not_Exist";
	}

	@Override
	public String getValidatorCommandLine() {
		return "Does_Not_Exist";
	}

	@Override
	public byte[] getValidatorFileContents() {
		return new byte[0];
	}

	@Override
	public String getValidatorFileName() {
		return "Does_Not_Exist";
	}

	@Override
	public boolean hasAnswerFile() {
		return false;
	}

	@Override
	public boolean hasDataFile() {
		return false;
	}

	@Override
	public boolean hasExternalValidator() {
		return false;
	}

	@Override
	public boolean isDeleted() {
		return false;
	}

	@Override
	public boolean readsInputFromFile() {
		return false;
	}

	@Override
	public boolean readsInputFromStdIn() {
		return false;
	}

}
