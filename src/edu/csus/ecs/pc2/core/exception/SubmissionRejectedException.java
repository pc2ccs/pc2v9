/**
 * 
 */
package edu.csus.ecs.pc2.core.exception;

import edu.csus.ecs.pc2.core.IThrottleStrategy;

/**
 * This exception is intended to be thrown when a team makes a submission ("submits a Run" in PC2 terminology)
 * but the system refuses to accept the run, for example due to the team having exceeded the submission threshhold
 * defined by the current {@link IThrottleStrategy}.
 * 
 * @author John Clevenger
 *
 */
public class SubmissionRejectedException extends Exception {

    private static final long serialVersionUID = 5282881959866727134L;

    /**
     * Constructs a SubmissionRejectedException which contains no information about the reason for the Exception.
     */
    public SubmissionRejectedException() {
    }

    /**
     * Constructs a SubmissionRejectedException containing text message.
     * 
     * @param message A text message associated with the Exception, typically the reason for the Exception.
     */
    public SubmissionRejectedException(String message) {
        super(message);
    }

    /**
     * Constructs a SubmissionRejectedException containing a {@link Throwable} which caused this exception.
     * 
     * @param cause A {@link Throwable} which caused the Exception.
     */
    public SubmissionRejectedException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a SubmissionRejectedException containing a text message and a {@link Throwable} which caused the exception.
     * 
     * @param message A text message associated with the Exception, typically the reason for the Exception.
     * @param cause A {@link Throwable} which caused the Exception.
     */
    public SubmissionRejectedException(String message, Throwable cause) {
        super(message, cause);
    }

}
