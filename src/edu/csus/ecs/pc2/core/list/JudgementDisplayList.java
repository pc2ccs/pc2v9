package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.Judgement;

/**
 * Maintain a list of {@link edu.csus.ecs.pc2.core.model.Judgement}s that are displayed.
 * 
 * Contains a list of Judgement classes, in order, to display for the users.
 *
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class JudgementDisplayList extends ElementDisplayList {

    /**
     *
     */
    private static final long serialVersionUID = -289689878049427570L;

    public static final String SVN_ID = "$Id$";

    public void addElement(Judgement judgement) {
        super.addElement(judgement);
    }

    public void insertElementAt(Judgement judgement, int idx) {
        super.insertElementAt(judgement, idx);
    }

    public void update(Judgement judgement) {
        for (int i = 0; i < size(); i++) {
            Judgement listJudgement = (Judgement) elementAt(i);

            if (listJudgement.getElementId().equals(judgement.getElementId())) {
                setElementAt(judgement, i);
            }
        }
    }

    /**
     * Get a sorted list of Judgements.
     * 
     * @return the array of Judgements
     */
    public Judgement[] getList() {
        if (size() == 0) {
            return new Judgement[0];
        } else {
            return (Judgement[]) this.toArray(new Judgement[this.size()]);
        }
    }

}
