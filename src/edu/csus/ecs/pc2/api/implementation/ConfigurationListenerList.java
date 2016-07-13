package edu.csus.ecs.pc2.api.implementation;

import java.util.Vector;

import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IContestClock;
import edu.csus.ecs.pc2.api.IGroup;
import edu.csus.ecs.pc2.api.IJudgement;
import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.ISite;
import edu.csus.ecs.pc2.api.listener.ContestEvent;
import edu.csus.ecs.pc2.api.listener.IConfigurationUpdateListener;
import edu.csus.ecs.pc2.api.listener.ContestEvent.EventType;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.GroupEvent;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IGroupListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IJudgementListener;
import edu.csus.ecs.pc2.core.model.ILanguageListener;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.ISiteListener;
import edu.csus.ecs.pc2.core.model.JudgementEvent;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageEvent;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.model.SiteEvent;

/**
 * API Configuration Listener list.
 * 
 * This class maintains the ConfigurationListener(s).  Class
 * registers for listeners within the pc2 core code.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ConfigurationListenerList {

    private IInternalContest contest = null;

    private Vector<IConfigurationUpdateListener> listenerList = new Vector<IConfigurationUpdateListener>();
    
    public void addContestUpdateConfigurationListener(IConfigurationUpdateListener contestUpdateConfigurationListener) {
        listenerList.addElement(contestUpdateConfigurationListener);
    }

    public void removeContestUpdateConfigurationListener(IConfigurationUpdateListener contestUpdateConfigurationListener) {
        listenerList.remove(contestUpdateConfigurationListener);
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    class ProblemListener implements IProblemListener {

        public void problemAdded(ProblemEvent event) {
            fireProblemListener(event);
        }

        public void problemChanged(ProblemEvent event) {
            fireProblemListener(event);
        }

        public void problemRemoved(ProblemEvent event) {
            fireProblemListener(event);
        }

        public void problemRefreshAll(ProblemEvent event) {
            // FIXME API code
            
        }
    }

    private void fireProblemListener(ProblemEvent problemEvent) {
        for (int i = 0; i < listenerList.size(); i++) {

            IProblem problem = new ProblemImplementation(problemEvent.getProblem().getElementId(), contest);
            ContestEvent contestEvent = new ContestEvent(EventType.PROBLEM, problem);

            switch (problemEvent.getAction()) {
                case ADDED:
                    listenerList.elementAt(i).configurationItemAdded(contestEvent);
                    break;
                case DELETED:
                    listenerList.elementAt(i).configurationItemRemoved(contestEvent);
                    break;
                case REFRESH_ALL: // FIXME API code
                case CHANGED:
                default:
                    listenerList.elementAt(i).configurationItemUpdated(contestEvent);
                    break;
            }
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    class LanguageListener implements ILanguageListener {

        public void languageAdded(LanguageEvent event) {
            fireLanguageListener(event);
        }

        public void languageChanged(LanguageEvent event) {
            fireLanguageListener(event);
        }

        public void languageRemoved(LanguageEvent event) {
            fireLanguageListener(event);
        }

        public void languageRefreshAll(LanguageEvent event) {
            // FIXME API code
        }

        @Override
        public void languagesAdded(LanguageEvent event) {
            fireLanguageListener(event);
        }

        @Override
        public void languagesChanged(LanguageEvent event) {
            fireLanguageListener(event);
        }
    }

    private void fireLanguageListener(LanguageEvent languageEvent) {
        for (int i = 0; i < listenerList.size(); i++) {

            ILanguage language = new LanguageImplementation(languageEvent.getLanguage().getElementId(), contest);
            ContestEvent contestEvent = new ContestEvent(EventType.LANGUAGE, language);

            switch (languageEvent.getAction()) {
                case ADDED:
                    listenerList.elementAt(i).configurationItemAdded(contestEvent);
                    break;
                case DELETED:
                    listenerList.elementAt(i).configurationItemRemoved(contestEvent);
                    break;
                case ADDED_LANGUAGES:
                    for (Language language2 : languageEvent.getLanguages()) {
                        language = new LanguageImplementation(language2.getElementId(), contest);
                        contestEvent = new ContestEvent(EventType.LANGUAGE, language);
                        listenerList.elementAt(i).configurationItemAdded(contestEvent);
                    }
                    break;
                case CHANGED_LANGUAGES:
                    for (Language language2 : languageEvent.getLanguages()) {
                        language = new LanguageImplementation(language2.getElementId(), contest);
                        contestEvent = new ContestEvent(EventType.LANGUAGE, language);
                        listenerList.elementAt(i).configurationItemUpdated(contestEvent);
                    }
                    break;
                case REFRESH_ALL: // FIXME API code
                case CHANGED:
                default:
                    listenerList.elementAt(i).configurationItemUpdated(contestEvent);
                    break;
            }
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    class GroupListener implements IGroupListener {

        public void groupAdded(GroupEvent event) {
            fireGroupListener(event);
        }

        public void groupChanged(GroupEvent event) {
            fireGroupListener(event);
        }

        public void groupRemoved(GroupEvent event) {
            fireGroupListener(event);
        }

        public void groupRefreshAll(GroupEvent groupEvent) {
            // FIXME API code
        }

        @Override
        public void groupsAdded(GroupEvent event) {
            fireGroupListener(event);
        }

        @Override
        public void groupsChanged(GroupEvent event) {
            fireGroupListener(event);
        }
    }

    private void fireGroupListener(GroupEvent groupEvent) {
        for (int i = 0; i < listenerList.size(); i++) {

            IGroup group = new GroupImplementation(groupEvent.getGroup().getElementId(), contest);
            ContestEvent contestEvent = new ContestEvent(EventType.GROUP, group);

            switch (groupEvent.getAction()) {
                case ADDED:
                    listenerList.elementAt(i).configurationItemAdded(contestEvent);
                    break;
                case DELETED:
                    listenerList.elementAt(i).configurationItemRemoved(contestEvent);
                    break;
                case ADDED_GROUPS:
                    for (Group group2 : groupEvent.getGroups()) {
                        group = new GroupImplementation(group2.getElementId(), contest);
                        contestEvent = new ContestEvent(EventType.GROUP, group);
                        listenerList.elementAt(i).configurationItemAdded(contestEvent);
                    }
                    break;
                case CHANGED_GROUPS:
                    for (Group group2 : groupEvent.getGroups()) {
                        group = new GroupImplementation(group2.getElementId(), contest);
                        contestEvent = new ContestEvent(EventType.GROUP, group);
                        listenerList.elementAt(i).configurationItemUpdated(contestEvent);
                    }
                    break;
                case CHANGED:
                default:
                    listenerList.elementAt(i).configurationItemUpdated(contestEvent);
                    break;
            }
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    class ContestTimeListener implements IContestTimeListener {

        public void contestStarted(ContestTimeEvent event) {
            fireContestTimeListener(event);
        }

        public void contestStopped(ContestTimeEvent event) {
            fireContestTimeListener(event);
        }

        public void contestTimeAdded(ContestTimeEvent event) {
            fireContestTimeListener(event);
        }

        public void contestTimeChanged(ContestTimeEvent event) {
            fireContestTimeListener(event);
        }

        public void contestTimeRemoved(ContestTimeEvent event) {
            fireContestTimeListener(event);
        }

        public void refreshAll(ContestTimeEvent event) {
            // FIXME API handle refresh all
        }
        
        /** This method exists to support differentiation between manual and automatic starts,
         * in the event this is desired in the future.
         * Currently it just delegates the handling to the contestStarted() method.
         */
        @Override
        public void contestAutoStarted(ContestTimeEvent event) {
            contestStarted(event);
        }

    }

    private void fireContestTimeListener(ContestTimeEvent contestTimeEvent) {
        for (int i = 0; i < listenerList.size(); i++) {

            IContestClock contestTime = new ContestTimeImplementation(contestTimeEvent.getContestTime());
            ContestEvent contestEvent = new ContestEvent(EventType.CONTEST_CLOCK, contestTime);

            switch (contestTimeEvent.getAction()) {
                case ADDED:
                    listenerList.elementAt(i).configurationItemAdded(contestEvent);
                    break;
                case DELETED:
                    listenerList.elementAt(i).configurationItemRemoved(contestEvent);
                    break;
                case CHANGED:
                default:
                    listenerList.elementAt(i).configurationItemUpdated(contestEvent);
                    break;
            }
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    class JudgementListener implements IJudgementListener {

        public void judgementAdded(JudgementEvent event) {
            fireJudgementListener(event);
        }

        public void judgementChanged(JudgementEvent event) {
            fireJudgementListener(event);
        }

        public void judgementRemoved(JudgementEvent event) {
            fireJudgementListener(event);
        }

        public void judgementRefreshAll(JudgementEvent judgementEvent) {
            // FIXME API code
        }
    }

    private void fireJudgementListener(JudgementEvent judgementEvent) {
        for (int i = 0; i < listenerList.size(); i++) {

            IJudgement judgement = new JudgementImplementation(judgementEvent.getJudgement());
            ContestEvent contestEvent = new ContestEvent(EventType.JUDGEMENT, judgement);

            switch (judgementEvent.getAction()) {
                case ADDED:
                    listenerList.elementAt(i).configurationItemAdded(contestEvent);
                    break;
                case DELETED:
                    listenerList.elementAt(i).configurationItemRemoved(contestEvent);
                    break;
                case CHANGED:
                default:
                    listenerList.elementAt(i).configurationItemUpdated(contestEvent);
                    break;
            }
        }
    }

    /**
     * Account Listener.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    class AccountListener implements IAccountListener {

        public void accountAdded(AccountEvent event) {
            fireAccountListener(event);
        }

        public void accountModified(AccountEvent event) {
            fireAccountListener(event);
        }

        public void accountsAdded(AccountEvent event) {
            fireAccountListener(event);
        }

        public void accountsModified(AccountEvent event) {
            fireAccountListener(event);
        }

        public void accountsRefreshAll(AccountEvent event) {
            // FIXME API refresh all
        }
    }

    private void fireAccountListener(AccountEvent accountEvent) {
        for (int i = 0; i < listenerList.size(); i++) {

            IClient client;
            ContestEvent contestEvent;

            switch (accountEvent.getAction()) {
                case ADDED:
                    client = new ClientImplementation(accountEvent.getAccount().getClientId(), contest);
                    contestEvent = new ContestEvent(EventType.CLIENT, client);
                    listenerList.elementAt(i).configurationItemAdded(contestEvent);
                    break;

                case DELETED:
                    client = new ClientImplementation(accountEvent.getAccount().getClientId(), contest);
                    contestEvent = new ContestEvent(EventType.CLIENT, client);
                    listenerList.elementAt(i).configurationItemRemoved(contestEvent);
                    break;

                case ADDED_ACCOUNTS:
                    for (Account account : accountEvent.getAccounts()) {
                        client = new ClientImplementation(account.getClientId(), contest);
                        contestEvent = new ContestEvent(EventType.CLIENT, client);
                        listenerList.elementAt(i).configurationItemAdded(contestEvent);
                    }
                    break;

                case CHANGED_ACCOUNTS:
                    for (Account account : accountEvent.getAccounts()) {
                        client = new ClientImplementation(account.getClientId(), contest);
                        contestEvent = new ContestEvent(EventType.CLIENT, client);
                        listenerList.elementAt(i).configurationItemUpdated(contestEvent);
                    }
                    break;

                case CHANGED:
                default:
                    client = new ClientImplementation(accountEvent.getAccount().getClientId(), contest);
                    contestEvent = new ContestEvent(EventType.CLIENT, client);
                    listenerList.elementAt(i).configurationItemUpdated(contestEvent);
                    break;
            }
        }
    }


    /**
     * Contest Information Listener for API Configuration Listener.
     *  
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    class ContestInformationListener implements IContestInformationListener {

        public void contestInformationAdded(ContestInformationEvent event) {
            fireContestInformationListener(event);
        }

        public void contestInformationChanged(ContestInformationEvent event) {
            fireContestInformationListener(event);
        }

        public void contestInformationRemoved(ContestInformationEvent event) {
            fireContestInformationListener(event);
        }

        public void contestInformationRefreshAll(ContestInformationEvent contestInformationEvent) {
            // FIXME API code

        }
        public void finalizeDataChanged(ContestInformationEvent contestInformationEvent) {
            // Not used
        }
    }

      private void fireContestInformationListener(ContestInformationEvent contestInformationEvent) {
          for (int i = 0; i < listenerList.size(); i++) {
              
              ContestInformation contestInformation = contestInformationEvent.getContestInformation();
              String title = contestInformation.getContestTitle();
              
              if (title == null){
                  continue;
              }
              
              ContestEvent contestEvent = new ContestEvent(EventType.CONTEST_TITLE, title);

              switch (contestInformationEvent.getAction()) {
                  case ADDED:
                      listenerList.elementAt(i).configurationItemAdded(contestEvent);
                      break;
                  case DELETED:
                      listenerList.elementAt(i).configurationItemRemoved(contestEvent);
                      break;
                  case CHANGED:
                  default:
                      listenerList.elementAt(i).configurationItemUpdated(contestEvent);
                      break;
              }
          }
      }

      private void fireSiteListener(SiteEvent event)  {
          for (int i = 0; i < listenerList.size(); i++) {

              ISite site = new SiteImplementation(event.getSite());
              ContestEvent contestEvent = new ContestEvent(EventType.SITE, site);
              
              switch (event.getAction()){
                  case ADDED:
                      listenerList.elementAt(i).configurationItemAdded(contestEvent);
                      break;
                  case DELETED:
                      listenerList.elementAt(i).configurationItemRemoved(contestEvent);
                      break;
                  case CHANGED:
                      listenerList.elementAt(i).configurationItemUpdated(contestEvent);
                      break;
                      
                  case LOGIN:
                      break; // ignored for now
                  case LOGOFF:
                      break; // ignored for now
                     default:
                         break; // ignored for now
              }
          }
      }
      
      /**
       * 
       * @author pc2@ecs.csus.edu
       * @version $Id$
       */
      class SiteListener implements ISiteListener {
          
          public void siteProfileStatusChanged(SiteEvent event) {
              // TODO this UI does not use a change in profile status 
          }

        public void siteAdded(SiteEvent event) {
            fireSiteListener(event);
        }

        public void siteRemoved(SiteEvent event) {
            fireSiteListener(event);
        }

        public void siteChanged(SiteEvent event) {
            fireSiteListener(event);
        }

        public void siteLoggedOn(SiteEvent event) {
            fireSiteListener(event);
        }

        public void siteLoggedOff(SiteEvent event) {
            // TODO Auto-generated method stub
            
        }

        public void sitesRefreshAll(SiteEvent siteEvent) {
            // FIXME API code
            
        }
      }
      
    public void setContest(IInternalContest contest) {
        this.contest = contest;
        
        // Add to listener from pc2 listeners
        contest.addProblemListener(new ProblemListener());
        contest.addLanguageListener(new LanguageListener());
        contest.addAccountListener(new AccountListener());
        contest.addGroupListener(new GroupListener());
        contest.addContestTimeListener(new ContestTimeListener());
        contest.addContestInformationListener(new ContestInformationListener());
        contest.addJudgementListener(new JudgementListener());
        contest.addSiteListener(new SiteListener());
        
//        contest.addBalloonSettingsListener
//        contest.addChangePasswordListener
//        contest.addClarificationListener
//        contest.addClientSettingsListener
//        contest.addConnectionListener
//        contest.addLoginListener
//        contest.addRunListener
//        contest.addSecurityMessageListener
    }
}
