package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.Judgement;

/**
 * List of judgements.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class JudgementList extends ElementList {

    /**
     *
     */
    private static final long serialVersionUID = -4214802174057330733L;

    public static final String SVN_ID = "$Id$";

    /**
     *
     * @param judgement
     *            {@link Judgement} to be added.
     */
    public void add(Judgement judgement) {
        super.add(judgement);

    }

    /**
     * Return list of Judgements.
     *
     * @return list of {@link Judgement}.
     */
    @SuppressWarnings("unchecked")
    public Judgement[] getList() {
        Judgement[] theList = new Judgement[size()];

        if (theList.length == 0) {
            return theList;
        }
        return (Judgement[]) values().toArray(new Judgement[size()]);
    }

}
