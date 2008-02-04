package edu.csus.ecs.pc2.api.implementation;

import java.util.Vector;

import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IContestTime;
import edu.csus.ecs.pc2.api.IGroup;
import edu.csus.ecs.pc2.api.IJudgement;
import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.listener.ContestEvent;
import edu.csus.ecs.pc2.api.listener.IConfigurationUpdateListener;
import edu.csus.ecs.pc2.api.listener.ContestEvent.EventType;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.GroupEvent;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IGroupListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IJudgementListener;
import edu.csus.ecs.pc2.core.model.ILanguageListener;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.JudgementEvent;
import edu.csus.ecs.pc2.core.model.LanguageEvent;
import edu.csus.ecs.pc2.core.model.ProblemEvent;

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
    }

    private void fireProblemListener(ProblemEvent problemEvent) {
        for (int i = 0; i < listenerList.size(); i++) {

            IProblem problem = new ProblemImplementation(problemEvent.getProblem().getElementId(), contest);
            ContestEvent contestEvent = new ContestEvent(EventType.PROBLEM, problem);

            switch (problemEvent.getAction()) {
                case ADDED:
                    listenerList.elementAt(i).elementAdded(contestEvent);
                    break;
                case DELETED:
                    listenerList.elementAt(i).elementRemoved(contestEvent);
                    break;
                case CHANGED:
                default:
                    listenerList.elementAt(i).elementUpdated(contestEvent);
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
    }

    private void fireLanguageListener(LanguageEvent languageEvent) {
        for (int i = 0; i < listenerList.size(); i++) {

            ILanguage language = new LanguageImplementation(languageEvent.getLanguage().getElementId(), contest);
            ContestEvent contestEvent = new ContestEvent(EventType.LANGUAGE, language);

            switch (languageEvent.getAction()) {
                case ADDED:
                    listenerList.elementAt(i).elementAdded(contestEvent);
                    break;
                case DELETED:
                    listenerList.elementAt(i).elementRemoved(contestEvent);
                    break;
                case CHANGED:
                default:
                    listenerList.elementAt(i).elementUpdated(contestEvent);
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
    }

    private void fireGroupListener(GroupEvent groupEvent) {
        for (int i = 0; i < listenerList.size(); i++) {

            IGroup group = new GroupImplementation(groupEvent.getGroup().getElementId(), contest);
            ContestEvent contestEvent = new ContestEvent(EventType.GROUP, group);

            switch (groupEvent.getAction()) {
                case ADDED:
                    listenerList.elementAt(i).elementAdded(contestEvent);
                    break;
                case DELETED:
                    listenerList.elementAt(i).elementRemoved(contestEvent);
                    break;
                case CHANGED:
                default:
                    listenerList.elementAt(i).elementUpdated(contestEvent);
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

    }

    private void fireContestTimeListener(ContestTimeEvent contestTimeEvent) {
        for (int i = 0; i < listenerList.size(); i++) {

            IContestTime contestTime = new ContestTimeImplementation(contestTimeEvent.getContestTime());
            ContestEvent contestEvent = new ContestEvent(EventType.CONTEST_CLOCK, contestTime);

            switch (contestTimeEvent.getAction()) {
                case ADDED:
                    listenerList.elementAt(i).elementAdded(contestEvent);
                    break;
                case DELETED:
                    listenerList.elementAt(i).elementRemoved(contestEvent);
                    break;
                case CHANGED:
                default:
                    listenerList.elementAt(i).elementUpdated(contestEvent);
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
    }

    private void fireJudgementListener(JudgementEvent judgementEvent) {
        for (int i = 0; i < listenerList.size(); i++) {

            IJudgement judgement = new JudgementImplementation(judgementEvent.getJudgement());
            ContestEvent contestEvent = new ContestEvent(EventType.LANGUAGE, judgement);

            switch (judgementEvent.getAction()) {
                case ADDED:
                    listenerList.elementAt(i).elementAdded(contestEvent);
                    break;
                case DELETED:
                    listenerList.elementAt(i).elementRemoved(contestEvent);
                    break;
                case CHANGED:
                default:
                    listenerList.elementAt(i).elementUpdated(contestEvent);
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
    }

    private void fireAccountListener(AccountEvent accountEvent) {
        for (int i = 0; i < listenerList.size(); i++) {

            IClient client;
            ContestEvent contestEvent;

            switch (accountEvent.getAction()) {
                case ADDED:
                    client = new ClientImplementation(accountEvent.getAccount().getClientId(), contest);
                    contestEvent = new ContestEvent(EventType.LOGIN_ACCOUNT, client);
                    listenerList.elementAt(i).elementAdded(contestEvent);
                    break;

                case DELETED:
                    client = new ClientImplementation(accountEvent.getAccount().getClientId(), contest);
                    contestEvent = new ContestEvent(EventType.LOGIN_ACCOUNT, client);
                    listenerList.elementAt(i).elementRemoved(contestEvent);
                    break;

                case ADDED_ACCOUNTS:
                    for (Account account : accountEvent.getAccounts()) {
                        client = new ClientImplementation(account.getClientId(), contest);
                        contestEvent = new ContestEvent(EventType.LOGIN_ACCOUNT, client);
                        listenerList.elementAt(i).elementAdded(contestEvent);
                    }
                    break;

                case CHANGED_ACCOUNTS:
                    for (Account account : accountEvent.getAccounts()) {
                        client = new ClientImplementation(account.getClientId(), contest);
                        contestEvent = new ContestEvent(EventType.LOGIN_ACCOUNT, client);
                        listenerList.elementAt(i).elementUpdated(contestEvent);
                    }
                    break;

                case CHANGED:
                default:
                    client = new ClientImplementation(accountEvent.getAccount().getClientId(), contest);
                    contestEvent = new ContestEvent(EventType.LOGIN_ACCOUNT, client);
                    listenerList.elementAt(i).elementUpdated(contestEvent);
                    break;
            }
        }
    }


    /**
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
                      listenerList.elementAt(i).elementAdded(contestEvent);
                      break;
                  case DELETED:
                      listenerList.elementAt(i).elementRemoved(contestEvent);
                      break;
                  case CHANGED:
                  default:
                      listenerList.elementAt(i).elementUpdated(contestEvent);
                      break;
              }
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
    }

}
