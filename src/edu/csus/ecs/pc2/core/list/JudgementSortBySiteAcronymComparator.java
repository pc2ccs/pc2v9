package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.Judgement;

/**
 * Sort judgemens by site, acronym, judgement text.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class JudgementSortBySiteAcronymComparator implements  Comparator<Judgement>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1424848798314592182L;

    @Override
    public int compare(Judgement jOne, Judgement j2) {

        if (jOne.getSiteNumber() != j2.getSiteNumber()) {
            return jOne.getSiteNumber() - j2.getSiteNumber();
        } else if (jOne.getAcronym().equals(j2.getAcronym())) {
            return jOne.getDisplayName().compareTo(j2.getDisplayName());
        } else {
            return jOne.getAcronym().compareTo(j2.getAcronym());
        }

    }

}
