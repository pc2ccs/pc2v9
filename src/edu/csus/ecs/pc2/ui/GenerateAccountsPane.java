package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.ISiteListener;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.SiteEvent;

/**
 * Generate accounts pane.
 * 
 * Can generate panes on any site, for any client type.
 * Can use a client start number to start the numbering of
 * the accounts.  
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class GenerateAccountsPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 8377729793720023288L;

    private JPanel centerPanel = null;

    private JTextField adminCountTextField = null;

    private JTextField boardCountTextField = null;

    private JTextField teamCountTextField = null;

    private JTextField judgeCountTextField = null;

    private JLabel genAdminLabel = null;

    private JLabel genJudgeLabel = null;

    private JLabel genTeamLabels = null;

    private JLabel genScoreboardLabel = null;

    private JPanel generateButtonPanel = null;

    private JButton generateButton = null;

    private JComboBox siteSelectionComboBox = null;

    private JLabel genStartNumberLabel = null;

    private JTextField startNumberTextField = null;

    private JLabel siteLabel = null;

    /**
     * This method initializes
     * 
     */
    public GenerateAccountsPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(441,234));
        this.add(getCenterPanel(), java.awt.BorderLayout.CENTER);
        this.add(getGenerateButtonPanel(), java.awt.BorderLayout.SOUTH);

    }
    
    private void updateSiteComboBox() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int selectedIndex = getSiteSelectionComboBox().getSelectedIndex();

                getSiteSelectionComboBox().removeAllItems();
                
                Site newSite = new Site("This Site", getContest().getSiteNumber());
                getSiteSelectionComboBox().addItem(newSite);
                Site[] sites = getContest().getSites();
                Arrays.sort(sites, new SiteComparatorBySiteNumber());
                for (Site site : sites) {
                    getSiteSelectionComboBox().addItem(site);
                }

                if (selectedIndex != -1) {
                    getSiteSelectionComboBox().setSelectedIndex(selectedIndex);
                }

            }
        });
    }

    /**
     * Update titles for client types.
     * 
     * 
     */
    private void updateGenerateTitles() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int thisSiteNumber = getContest().getSiteNumber();
                int theSiteNumber = thisSiteNumber;

                if (getSiteSelectionComboBox().getSelectedIndex() > 0) {
                    theSiteNumber = getSiteSelectionComboBox().getSelectedIndex();
                }

                int number = getContest().getAccounts(ClientType.Type.SCOREBOARD, theSiteNumber).size();
                genScoreboardLabel.setText("Scoreboards (" + number + ")");

                number = getContest().getAccounts(ClientType.Type.TEAM, theSiteNumber).size();
                genTeamLabels.setText("Teams (" + number + ")");

                number = getContest().getAccounts(ClientType.Type.JUDGE, theSiteNumber).size();
                genJudgeLabel.setText("Judges (" + number + ")");

                number = getContest().getAccounts(ClientType.Type.ADMINISTRATOR, theSiteNumber).size();
                genAdminLabel.setText("Administrators (" + number + ")");

                generateButton.setText("Generate Accounts for Site " + theSiteNumber);
                
            }
        });
    }


    @Override
    public String getPluginTitle() {
        return "Generate Accounts Pane";
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPanel() {
        if (centerPanel == null) {
            siteLabel = new JLabel();
            siteLabel.setBounds(new java.awt.Rectangle(79,8,69,20));
            siteLabel.setText("Site");
            genStartNumberLabel = new JLabel();
            genStartNumberLabel.setBounds(new java.awt.Rectangle(81,159,166,19));
            genStartNumberLabel.setText("Start Account Number at");
            genScoreboardLabel = new JLabel();
            genScoreboardLabel.setBounds(new java.awt.Rectangle(81,129,166,19));
            genScoreboardLabel.setText("Scoreboards");
            genTeamLabels = new JLabel();
            genTeamLabels.setBounds(new java.awt.Rectangle(81,99,166,19));
            genTeamLabels.setText("Teams");
            genJudgeLabel = new JLabel();
            genJudgeLabel.setBounds(new java.awt.Rectangle(81,69,166,19));
            genJudgeLabel.setText("Judges");
            genAdminLabel = new JLabel();
            genAdminLabel.setBounds(new java.awt.Rectangle(81,39,166,19));
            genAdminLabel.setText("Administrators");
            centerPanel = new JPanel();
            centerPanel.setLayout(null);
            centerPanel.add(getAdminCountTextField(), null);
            centerPanel.add(getBoardCountTextField(), null);
            centerPanel.add(getTeamCountTextField(), null);
            centerPanel.add(getJudgeCountTextField(), null);
            centerPanel.add(genAdminLabel, null);
            centerPanel.add(genJudgeLabel, null);
            centerPanel.add(genTeamLabels, null);
            centerPanel.add(genScoreboardLabel, null);
            centerPanel.add(getSiteSelectionComboBox(), null);
            centerPanel.add(genStartNumberLabel, null);
            centerPanel.add(getStartNumberTextField(), null);
            centerPanel.add(siteLabel, null);
        }
        return centerPanel;
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getAdminCountTextField() {
        if (adminCountTextField == null) {
            adminCountTextField = new JTextField();
            adminCountTextField.setBounds(new java.awt.Rectangle(305,35,39,22));
            adminCountTextField.setDocument(new IntegerDocument());
        }
        return adminCountTextField;
    }

    /**
     * This method initializes jTextField1
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getBoardCountTextField() {
        if (boardCountTextField == null) {
            boardCountTextField = new JTextField();
            boardCountTextField.setBounds(new java.awt.Rectangle(305,127,39,22));
            boardCountTextField.setDocument(new IntegerDocument());
        }
        return boardCountTextField;
    }

    /**
     * This method initializes jTextField2
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTeamCountTextField() {
        if (teamCountTextField == null) {
            teamCountTextField = new JTextField();
            teamCountTextField.setBounds(new java.awt.Rectangle(305,97,39,22));
            teamCountTextField.setDocument(new IntegerDocument());
        }
        return teamCountTextField;
    }

    /**
     * This method initializes jTextField3
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJudgeCountTextField() {
        if (judgeCountTextField == null) {
            judgeCountTextField = new JTextField();
            judgeCountTextField.setBounds(new java.awt.Rectangle(305,67,39,22));
            judgeCountTextField.setDocument(new IntegerDocument());
        }
        return judgeCountTextField;
    }

    /**
     * This method initializes generateButtonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGenerateButtonPanel() {
        if (generateButtonPanel == null) {
            generateButtonPanel = new JPanel();
            generateButtonPanel.setPreferredSize(new java.awt.Dimension(35, 35));
            generateButtonPanel.add(getGenerateButton(), null);
        }
        return generateButtonPanel;
    }

    /**
     * This method initializes generateButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getGenerateButton() {
        if (generateButton == null) {
            generateButton = new JButton();
            generateButton.setText("Generate");
            generateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    generateAccounts();
                }
            });
        }
        return generateButton;
    }
    
    private int getIntegerValue(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    protected void generateAccounts() {

        try {
            int startNumber = getIntegerValue(startNumberTextField.getText());
            if (startNumber < 1){
                startNumber = 1;
            }
            
            int thisSiteNumber = getContest().getSiteNumber();
            int theSiteNumber = thisSiteNumber;

            if (getSiteSelectionComboBox().getSelectedIndex() > 0) { // 0 is this site
                theSiteNumber = getSiteSelectionComboBox().getSelectedIndex();
            }
            
            int count = getIntegerValue(adminCountTextField.getText());
            if (count > 0) {
                getController().generateNewAccounts(ClientType.Type.ADMINISTRATOR.toString(), theSiteNumber, count, startNumber, true);
                adminCountTextField.setText("");
            }

            count = getIntegerValue(judgeCountTextField.getText());
            if (count > 0) {
                getController().generateNewAccounts(ClientType.Type.JUDGE.toString(), theSiteNumber, count, startNumber, true);
                judgeCountTextField.setText("");
            }

            count = getIntegerValue(teamCountTextField.getText());
            if (count > 0) {
                getController().generateNewAccounts(ClientType.Type.TEAM.toString(), theSiteNumber, count, startNumber,true);
                teamCountTextField.setText("");
            }

            count = getIntegerValue(boardCountTextField.getText());
            if (count > 0) {
                getController().generateNewAccounts(ClientType.Type.SCOREBOARD.toString(), theSiteNumber, count, startNumber, true);
                boardCountTextField.setText("");
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * This method initializes siteSelectionComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getSiteSelectionComboBox() {
        if (siteSelectionComboBox == null) {
            siteSelectionComboBox = new JComboBox();
            siteSelectionComboBox.setBounds(new java.awt.Rectangle(169,9,175,19));
            siteSelectionComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateGenerateTitles();
                }
            });
        }
        return siteSelectionComboBox;
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getStartNumberTextField() {
        if (startNumberTextField == null) {
            startNumberTextField = new JTextField();
            startNumberTextField.setBounds(new java.awt.Rectangle(305,157,39,22));
            startNumberTextField.setDocument(new IntegerDocument());
        }
        return startNumberTextField;
    }
    
    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);

        updateGenerateTitles();
        updateSiteComboBox();
        
        getContest().addAccountListener(new AccountListenerImplementation());
        getContest().addSiteListener(new SiteListenerImplementation());
    }
    
    /**
     * Account Listener for GenerateAccountsPanel.
     *  
     * @author pc2@ecs.csus.edu
     */
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateGenerateTitles();
                }
            });
            
        }

        public void accountModified(AccountEvent accountEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateGenerateTitles();
                }
            });
            
        }
        
    }

    /**
     * Site Listener. 
     * @author pc2@ecs.csus.edu
     *
     */
    public class SiteListenerImplementation implements ISiteListener {

        public void siteAdded(SiteEvent event) {
            updateSiteComboBox();
        }

        public void siteRemoved(SiteEvent event) {
            updateSiteComboBox();
        }

        public void siteChanged(SiteEvent event) {
            updateSiteComboBox();
        }

        public void siteLoggedOn(SiteEvent event) {
            updateSiteComboBox();
        }

        public void siteLoggedOff(SiteEvent event) {
            updateSiteComboBox();
        }
        
    }

} // @jve:decl-index=0:visual-constraint="10,10"
