package edu.csus.ecs.pc2.core.report;

import java.util.Vector;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * An AccountTSVReport with only teams and judges accounts.
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
class AccountsTSVReportTeamAndJudges extends AccountsTSVReport {

    /**
     * 
     */
    private static final long serialVersionUID = 921156175618527895L;

    private IInternalContest contest = null;

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        contest = inContest;

        /**
         * Set filter for only teams and judges to be output.
         */
        Filter reportFilter = new Filter();

        Account[] judgeAccounts = getAccounts(Type.JUDGE);
        reportFilter.addAccounts(judgeAccounts);

        Account[] teamAccounts = getAccounts(Type.TEAM);
        reportFilter.addAccounts(teamAccounts);

        super.setFilter(reportFilter);
    }

    private Account[] getAccounts(Type type) {
        Vector<Account> vector = contest.getAccounts(type);
        return (Account[]) vector.toArray(new Account[vector.size()]);
    }
}
