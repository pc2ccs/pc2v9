package suites;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	controllers.Test_Teams_Login.class,
	controllers.Test_Teams_Submit_Runs.class
})
public class TestTeamControllerFeatures {


	public TestTeamControllerFeatures() {

	}

}
