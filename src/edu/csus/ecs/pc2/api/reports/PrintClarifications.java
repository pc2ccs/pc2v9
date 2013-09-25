package edu.csus.ecs.pc2.api.reports;

import java.util.Arrays;

import edu.csus.ecs.pc2.api.IClarification;
import edu.csus.ecs.pc2.api.IClarificationComparator;

/**
 * Clarifications.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintClarifications extends APIAbstractTest {

    @Override
    public void printTest() {
        IClarification[] clarifications = getContest().getClarifications();
        println("There are " + clarifications.length + " clarifications ");

        Arrays.sort(clarifications, new IClarificationComparator());

        for (IClarification clarification : clarifications) {

            print("Clar " + clarification.getNumber() + " Site " + clarification.getSiteNumber());

            print(" @ " + clarification.getSubmissionTime() + " by " + clarification.getTeam().getLoginName());
            print(" problem: " + clarification.getProblem().getName());
            print(" " + APIPrintReports.trueFalseString(clarification.isAnswered(), "ANSWERED", "NOT ANSWERED"));
            print(" " + APIPrintReports.trueFalseString(clarification.isDeleted(), "DELETED", ""));
            print(" " + APIPrintReports.trueFalseString(clarification.isSendToAll(), "SEND TO ALL", "NOT SENT TO ALL"));
            println();
            println("  Question: " + clarification.getQuestion());
            if (clarification.isAnswered()) {
                println("    Answer: " + clarification.getAnswer());
            }
        }
        println();
    }

    @Override
    public String getTitle() {
        return "getClarifications";
    }
}
