package emptyObjs;

import edu.csus.ecs.pc2.api.ILanguage;

public class EmptyLanguage implements ILanguage{

	@Override
	public String getCompilerCommandLine() {
		return "Does_Not_Exist";
	}

	@Override
	public String getExecutableMask() {
		return "Does_Not_Exist";
	}

	@Override
	public String getExecutionCommandLine() {
		return "Does_Not_Exist";
	}

	@Override
	public String getName() {
		return "Does_Not_Exist";
	}

	@Override
	public String getTitle() {
		return "Does_Not_Exist";
	}

	@Override
	public boolean isInterpreted() {
		return false;
	}

}
