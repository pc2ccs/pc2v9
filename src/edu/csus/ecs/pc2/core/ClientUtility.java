// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * A set of client utilities.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public final class ClientUtility {

    
    private ClientUtility() {
        // this constructor added to address checkstyle rule.
    }

    /**
     * Client.
     * 
     * Used to assign and provide contest and controller.
     * 
     * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
     */
    private static class ClientPlugin extends Plugin {

        private static final long serialVersionUID = 283327914268686602L;

        @Override
        public String getPluginTitle() {
            return "Temporary Login Plugin";
        }

        @Override
        public void dispose() {
            // nothing to displose of, kept for subclasses
            
        }
        
    }

    /**
     * A non-GUI login to a server.
     * 
     * @param login
     * @param password
     * @return a class with contest and controller accessors.
     * @throws LoginFailureException
     */
    public static Plugin logInToContest(String login, String password) throws LoginFailureException {

        IInternalContest internalContest = new InternalContest();
        InternalController controller = new InternalController(internalContest);

        controller.setUsingGUI(false);
        controller.setUsingMainUI(false);
        controller.setClientAutoShutdown(false);

        try {
            controller.start(new String[0]);
            internalContest = controller.clientLogin(internalContest, login, password);

            Plugin plugin = new ClientPlugin();
            plugin.setContestAndController(internalContest, controller);
            
            return plugin;

        } catch (Exception e) {
            throw new LoginFailureException(e.getMessage());
        }
    }
    
    /**
     * a login to a potential UI login.
     * 
     * @param login
     * @param password
     * @param plugin a instantiated UIPlugin
     * @return a class with contest and controller accessors.
     * @throws LoginFailureException
     */
    public static UIPlugin logInToContest(String login, String password, UIPlugin plugin) throws LoginFailureException {
        
        if (plugin == null){
            throw new IllegalArgumentException("plugin cannot be null");
        }

        IInternalContest internalContest = new InternalContest();
        InternalController controller = new InternalController(internalContest);

        controller.setUsingGUI(false);
        controller.setUsingMainUI(false);
        controller.setClientAutoShutdown(false);

        try {
            controller.start(new String[0]);
            internalContest = controller.clientLogin(internalContest, login, password);
            plugin.setContestAndController(internalContest, controller);
            
            return plugin;

        } catch (Exception e) {
            throw new LoginFailureException(e.getMessage());
        }
    }
    
    
    /**
     * Get all sites' teams.
     */
    public static List<Account> getTeamAccounts(IInternalContest inContest) {
        return getAccounts(inContest, ClientType.Type.TEAM);
    }
    
    /**
     * Get all sites' teams.
     */
    public static List<Account> getAccounts(IInternalContest inContest, ClientType.Type type) {
        Vector<Account> accountVector = inContest.getAccounts(type);
        Account[] accounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);
        Arrays.sort(accounts, new AccountComparator());
        return Arrays.asList(accounts);
    }
}
