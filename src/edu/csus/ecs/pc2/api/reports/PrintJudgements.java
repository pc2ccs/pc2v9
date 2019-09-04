// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api.reports;

import edu.csus.ecs.pc2.api.IJudgement;

/**
 * Judgments.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintJudgements extends APIAbstractTest {

    @Override
    public void printTest() {
        IJudgement[] judgements = getContest().getJudgements();
        println("There are " + judgements.length + " judgements.");
        for (IJudgement judgement : judgements) {
            println("judgement name = " + judgement.getName());
        }
        println();
    }

    @Override
    public String getTitle() {
        return "getJudgements";
    }
}
