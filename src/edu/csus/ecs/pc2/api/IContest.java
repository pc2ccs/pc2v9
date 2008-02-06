package edu.csus.ecs.pc2.api;

import edu.csus.ecs.pc2.api.listener.IConfigurationUpdateListener;
import edu.csus.ecs.pc2.api.listener.IRunEventListener;

/**
 * Contest data/information.
 * 
 * <pre>
 * import edu.csus.ecs.pc2.api.IContest;
 * import edu.csus.ecs.pc2.api.IRun;
 * import edu.csus.ecs.pc2.api.ServerConnection;
 * import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
 * 
 * public class RunsSample {
 * 
 * private void loginAndShowRuns(String login, String password) throws LoginFailureException {
 *  
 *          ServerConnection serverConnection = new ServerConnection();
 *          IContest contest = serverConnection.login(login, password);
 *  
 *          for (IRun run : contest.getRuns()) {
 *  
 *              System.out.println(&quot;Run &quot; + run.getNumber() + &quot; from site &quot; + run.getSiteNumber());
 *              System.out.println(&quot;    submitted at &quot; + run.getSubmissionTime() + &quot; by &quot; + run.getSubmitterTeam().getTitle());
 *              System.out.println(&quot;    For problem &quot; + run.getProblem().getTitle());
 *              System.out.println(&quot;    Written in &quot; + run.getLanguage().getTitle());
 *  
 *              if (run.isJudged()) {
 *                  System.out.println(&quot;    Judgement: &quot; + run.getJudgementTitle());
 *              } else {
 *                  System.out.println(&quot;    Judgement: not judged yet &quot;);
 *              }
 *  
 *              System.out.println();
 *          }
 *  
 *      }    public static void main(String[] args) {
 *         if (args.length != 2) {
 *             System.out.println(&quot;API Sample, usage: APIExample loginName password&quot;);
 *         } else {
 *             System.out.println(&quot;login: &quot; + args[0] + &quot; password: &quot; + args[1]);
 *             try {
 *                 new RunsSample().loginAndShowRuns(args[0], args[1]);
 *             } catch (LoginFailureException e) {
 *                 e.printStackTrace();
 *             }
 *         }
 *     }
 * }
 * </pre>
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IContest {

    /**
     * Gets all the team ClientIds in the contest.
     * 
     * <P>
     * Code snippet to print all team names.
     * 
     * <pre>
     * 
     * for (ITeam team : contest.getTeams()) {
     *     System.out.println(team.getShortName() + &quot; Site &quot; + team.getSiteNumber() + &quot; title: &quot; + team.getTitle() + &quot; group: &quot; + team.getGroup());
     * }
     * </pre>
     * 
     * @return array of ClientId not sorted.
     */
    ITeam[] getTeams();

    /**
     * Get the name for the specified site.
     * 
     * @param siteNumber
     * @return title for this site. @
     */
    String getSiteName(int siteNumber);

    /**
     * Get the contest title.
     * 
     * @return the title of the contest.
     * 
     */
    String getContestTitle();

    /**
     * Get the current server's site name.
     * 
     * Gets the name for the server this client is logged into.
     * 
     * @return the current server's site name.
     * 
     */
    String getSiteName();

    /**
     * Get a list of languages.
     * 
     * Returns a list of languages.
     * <P>
     * Code snippet to print all language names.
     * 
     * <pre>
     * for (ILanguage language : contest.getLanguages()) {
     *     System.out.println(language.getTitle());
     * }
     * </pre>
     * 
     * @return list of language ids in proper order
     * 
     */
    ILanguage[] getLanguages();

    /**
     * Get a list of problems.
     * 
     * Returns a list of problems.
     * <P>
     * Code snippet to print all language titles/names.
     * 
     * <pre>
     * for (IProblem problem : contest.getProblems()) {
     *     System.out.println(problem.getTitle());
     * }
     * </pre>
     * 
     * @return list of problem ids in proper order
     * 
     */
    IProblem[] getProblems();

    /**
     * Get a list of judgements.
     * 
     * Code snippet to print all judgement titles/names.
     * 
     * <pre>
     * for (IJudgement judgement : contest.getJudgements()) {
     *     System.out.println(judgement.getTitle());
     * }
     * 
     * </pre>
     * 
     * @return list of judgements in proper order.
     * 
     */
    IJudgement[] getJudgements();

    /**
     * Get a list of runs.
     * 
     * <P>
     * Code snippet to print all run info in contest.
     * 
     * <pre>
     * for (IRun run : contest.getRuns()) {
     * 
     *     System.out.println(&quot;Run &quot; + run.getNumber() + &quot; from site &quot; + run.getSiteNumber());
     *     System.out.println(&quot;    submitted at &quot; + run.getSubmissionTime() + &quot; minutes by &quot; + run.getSubmitterTeam().getTitle());
     *     System.out.println(&quot;    For problem &quot; + run.getProblem().getTitle());
     *     System.out.println(&quot;    Written in &quot; + run.getLanguage().getTitle());
     * 
     *     if (run.isJudged()) {
     *         System.out.println(&quot;    Judgement: &quot; + run.getJudgementTitle());
     *     } else {
     *         System.out.println(&quot;    Judgement: not judged yet &quot;);
     *     }
     * 
     *     System.out.println();
     * }
     * 
     * </pre>
     * 
     * @return list of Runs, unordered.
     */
    IRun[] getRuns();

    /**
     * Add run event listener.
     * 
     * @param runEventListener
     *            listener for Run events
     */
    void addRunListener(IRunEventListener runEventListener);

    /**
     * Remove run event listener.
     * 
     * @param runEventListener
     *            listener for Run events
     */
    void removeRunListener(IRunEventListener runEventListener);

    /**
     * Add Contest Update listener.
     * 
     * @param contestUpdateConfigurationListener
     *            listener for Configuration Update events
     */
    void addContestUpdateConfigurationListener(IConfigurationUpdateListener contestUpdateConfigurationListener);

    /**
     * Remove Contest Update listener.
     * 
     * @param contestUpdateConfigurationListener
     *            listener for Configuration Update events
     */
    void removeContestUpdateConfigurationListener(IConfigurationUpdateListener contestUpdateConfigurationListener);

    /**
     * Get the contest clock info.
     * 
     * <pre>
     * System.out.println(&quot;Contest Time Updated, running &quot; 
     *    + contestTime.isContestClockRunning() + &quot;, remaining &quot; + contestTime.getRemainingSecs());
     * </pre>
     * 
     * @return contest time information
     */
    IContestClock getContestTime();

    /**
     * Get list of defined groups/regions.
     * 
     * @return list of groups.
     */
    IGroup[] getGroups();

    /**
     * Get the current logged in client.
     */
    IClient getMyClient();

    /**
     * Is Contest Clock started/running ?.
     * 
     * @return return true if clock started, false if stopped.
     */
    boolean isContestClockRunning();
}
