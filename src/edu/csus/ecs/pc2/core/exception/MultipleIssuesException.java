package edu.csus.ecs.pc2.core.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Exception with a list of messages/issues.
 * 
 * The {@link #getIssueList()} should provide a list of issues.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

public class MultipleIssuesException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -5110655532064685209L;

    private List<String> issueList = new ArrayList<>();

    public MultipleIssuesException(String message, List<String> messages) {
        super(message);
        issueList.addAll(messages);
    }

    public MultipleIssuesException(String message, String[] messages) {
        super(message);
        issueList.addAll(Arrays.asList(messages));
    }

    /**
     * Add a message to list of issues/messages
     * @param message
     */
    public void addIssueMessage(String message) {
        issueList.add(message);
    }

    /**
     * Get complete list of all issues/message.
     */
    public String[] getIssueList() {
        return (String[]) issueList.toArray(new String[issueList.size()]);
    }

}
