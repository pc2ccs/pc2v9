package edu.csus.ecs.pc2.core.execute;

// TODO move to pc2.core package

import edu.csus.ecs.pc2.ccs.CCSConstants;
import edu.csus.ecs.pc2.core.AbstractElementObject;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Test case data.
 * 
 * Used with {@link SingleTestCase}.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class TestCaseData extends AbstractElementObject {

    /**
     * 
     */
    private static final long serialVersionUID = -5368603494697863097L;

    private int testCaseNumber;

    private Run run;

    private int validatorExitCode = -1;

    /**
     * Test case a failure
     */
    private boolean failure = true;
    
    /**
     * Message that assists in determining where validator/execution failed internally.
     */
    private String internalErrorMessage = null;
    
    public TestCaseData(Run run, int testCaseNumber) {
        super("TestCaseData");
        this.run = run;
        this.testCaseNumber = testCaseNumber;
    }

    public int getTestCaseNumber() {
        return testCaseNumber;
    }

    public Run getRun() {
        return run;
    }

    
    /**
     * Fail to pass validation.
     * 
     * @return
     */
    public boolean isFailure() {
        return failure;
    }
    
    public boolean hasinternalError(){
        return internalErrorMessage == null;
    }

    public void setValidatorExitCode(int validatorExitCode) {
        this.validatorExitCode = validatorExitCode;
        internalErrorMessage = null;
        failure = true;

        if (validatorExitCode == CCSConstants.VALIDATOR_JUDGED_SUCCESS_EXIT_CODE) {
            failure = false;
        }
    }
    
    public void setInternalErrorMessage(String internalErrorMessage) {
        this.internalErrorMessage = internalErrorMessage;
        validatorExitCode = -1;
        failure = true;
    }

    public int getValidatorExitCode() {
        return validatorExitCode;
    }

}
