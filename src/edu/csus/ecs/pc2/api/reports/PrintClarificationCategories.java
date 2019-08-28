// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api.reports;

import edu.csus.ecs.pc2.api.IProblem;

/**
 * ClarificationCategories.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintClarificationCategories extends APIAbstractTest {

    @Override
    public void printTest() {
        println("There are " + getContest().getClarificationCategories().length + " clarification category");
        for (IProblem problem : getContest().getClarificationCategories()) {
            print("Clarification Category name = " + problem.getName());
            println();
        }
        println();
    }

    @Override
    public String getTitle() {
        return "getClarificationCategories";
    }
}
