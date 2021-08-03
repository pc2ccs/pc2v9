// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * A UI that provides a button to display the PC2 {@link Permission}s associated with the currently logged-in account.
 * 
 * 
 * @author John Clevenger, PC^2 Development Team (pc2@ecs.csus.edu).
 */
public class CheckPermissionsPane extends JPanePlugin {

    private static final long serialVersionUID = 1L;

    public CheckPermissionsPane() {
        super();
        this.setLayout(new BorderLayout(0, 0));

        JPanel centerPane = new JPanel();
        add(centerPane, BorderLayout.CENTER);
        centerPane.setLayout(new FlowLayout());

        JButton displayPermissionsButton = new JButton("Display Permissions");
        displayPermissionsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                displayPermissions();

            }

        });

        centerPane.add(displayPermissionsButton);

    }

    private void displayPermissions() {

        //find out who I am
        ClientId clientId = getContest().getClientId();
        
        //get my account
        Account myAccount = getContest().getAccount(clientId);
        
        //get the list of permissions granted to my account
        Permission.Type[] myPermissions = myAccount.getPermissionList().getList();
        
        //construct a String listing each permission
        String msg = "Permissions (" + myPermissions.length + "): \n";
        
        if (myPermissions.length==0) {
            msg += "  <none>\n";
        } else {

            ArrayList<String> perms = new ArrayList<String>();
            
            for (Permission.Type permission : myPermissions) {
                perms.add(permission.toString());
            }
            perms.sort(null);
            
            for (String perm : perms) {
                msg += "  " + perm + "\n";
            }
        }
        
        //display the permision list
        JOptionPane.showMessageDialog(this, msg, "Current Permissions", JOptionPane.INFORMATION_MESSAGE);
        
    }

    @Override
    public String getPluginTitle() {
        return "CheckPermissions Pane";
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
    }

}
