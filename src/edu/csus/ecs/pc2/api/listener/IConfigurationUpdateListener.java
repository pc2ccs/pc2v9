package edu.csus.ecs.pc2.api.listener;

import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IContestTime;
import edu.csus.ecs.pc2.api.IGroup;
import edu.csus.ecs.pc2.api.IJudgement;
import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;

/**
 * Set of methods that any Configuration Update Listener must implement.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public interface IConfigurationUpdateListener {

    /**
     * All contest data has been reset or changed.
     * 
     * This event happens when a reset is done on the server, any or all of the configuration information may have changed.
     * <P>
     */
    void reset();

    /**
     * client has been added.
     * 
     * @param client client info.
     */
    void clientAdded(IClient client);

    /**
     * client has been removed.
     * 
     * @param client client info.
     */
    void clientRemoved(IClient client);

    /**
     * client has been updated.
     * 
     * Could be triggered by a change in the client's title.
     * 
     * @param client client info.
     */
    void clientUpdated(IClient client);

    /**
     * The contest title has been updated.
     * 
     * @see edu.csus.ecs.pc2.api.IContest#getContestTitle()
     * @param title new title for contest.
     */
    void titleUpdated(String title);

    /**
     * Language has been added.
     * 
     * The title, compile command line, etc has been changed.
     * 
     * @param language language info.
     */
    void languageAdded(ILanguage language);

    /**
     * A language was removed.
     * 
     * @param language language info.
     */
    void languageRemoved(ILanguage language);

    /**
     * Language updated.
     * 
     * @param language language info.
     */
    void languageUpdated(ILanguage language);

    /**
     * Problem added.
     * 
     * @param problem problem info.
     */
    void problemAdded(IProblem problem);

    /**
     * Problem removed.
     * 
     * @param problem problem info.
     */
    void problemRemoved(IProblem problem);

    /**
     * Problem updated
     * 
     * @param problem problem info.
     */
    void problemUpdated(IProblem problem);

    /**
     * Judgement added.
     * 
     * @param judgement judgement info.
     */
    void judgementAdded(IJudgement judgement);

    /**
     * Judgement removed.
     * 
     * @param judgement judgement info.
     */
    void judgementRemoved(IJudgement judgement);

    /**
     * Judgement Updated.
     * 
     * @param judgement judgement info.
     */
    void judgementUpdated(IJudgement judgement);

    /**
     * group has been added.
     * 
     * @param group group info.
     */
    void groupAdded(IGroup group);

    /**
     * group has been removed.
     * 
     * @param group group info.
     */
    void groupRemoved(IGroup group);

    /**
     * group has been updated.
     * 
     * Could be triggered by a change in the group's title.
     * 
     * @param group group info.
     */
    void groupUpdated(IGroup group);

    /**
     * Contest Clock started.
     * @param contestTime 
     */
    void contestStarted(IContestTime contestTime);

    /**
     * Contest Clock ended.
     * @param contestTime 
     */
    void contestStopped(IContestTime contestTime);

    /**
     * Contest Clock has been updated.
     * @param contestTime
     */
    void contestTimeUpdated(IContestTime contestTime);

}
