package edu.csus.ecs.pc2.api.reports;

import edu.csus.ecs.pc2.api.IClarification;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PrintClarification extends APIAbstractTest {

    @Override
    public void printTest() {

        int clarificationNumber = getNumber();
        if (clarificationNumber < 1) {
            println("getRun() Select a run number");
        } else {
            int siteNum = getSiteNumber();
            if (siteNum == 0) {
                siteNum = getContest().getMyClient().getSiteNumber();
            }
            boolean foundClarification = false;

            for (IClarification clarification : getContest().getClarifications()) {
                if (clarification.getNumber() == clarificationNumber && clarification.getSiteNumber() == siteNum) {
                    foundClarification = true;
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
                    println();
                    break;
                }
            } // for IClarification

            if (!foundClarification) {
                println("No such clarification " + clarificationNumber + " eists at site " + siteNum);
            }
        }
    }

    @Override
    public String getTitle() {
        return "getClarification";
    }

}
