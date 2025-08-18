// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
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

    public enum SubmissionRejectionReason {
        UNKNOWN,
        THROTTLE_EXCEEDED,
        SOURCE_TOO_BIG
    }

    private SubmissionRejectionReason rejectReason = SubmissionRejectionReason.UNKNOWN;

    /**
     * Constructs an empty SubmissionRejectedException which contains no textual information about the reason for the Exception.
     */
    public SubmissionRejectedException() {
    }

    /**
     * Constructs a THROTTLE_EXCEEDED SubmissionRejectedException containing the supplied text message.
     * This is for backward compatibility.
     * TODO: eventually change any code that calls this to specify the reason Enum explicitly.
     *
     * @param message A text message associated with the Exception using the reason THROTTLE_EXCEEDED.
     */
    public SubmissionRejectedException(String message) {
        this(message, SubmissionRejectionReason.THROTTLE_EXCEEDED);
    }

    /**
     * Constructs a SubmissionRejectedException containing text message and reason.
     *
     * @param message A text message associated with the Exception
     * @param reason Why it was rejected
     */
    public SubmissionRejectedException(String message, SubmissionRejectionReason reason) {
        super(message);
        rejectReason = reason;
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

    /**
     * Return the enum of why the submission was rejected
     *
     * @return SubmissionRejectionReason
     */
    public SubmissionRejectionReason getRejectionReason() {
        return(rejectReason);
    }

}
