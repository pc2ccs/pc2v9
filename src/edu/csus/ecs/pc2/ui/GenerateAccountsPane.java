package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.Site;

/**
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

    private void updateGenerateTitles() {

        // Update the Number of accounts

        int number = getModel().getAccounts(ClientType.Type.SCOREBOARD).size();
        genScoreboardLabel.setText("Scoreboards (" + number + ")");

        number = getModel().getAccounts(ClientType.Type.TEAM).size();
        genTeamLabels.setText("Teams (" + number + ")");

        number = getModel().getAccounts(ClientType.Type.JUDGE).size();
        genJudgeLabel.setText("Judges (" + number + ")");

        number = getModel().getAccounts(ClientType.Type.ADMINISTRATOR).size();
        genAdminLabel.setText("Administrators (" + number + ")");
        
        if ( getSiteSelectionComboBox().getItemCount() < 1){
            Site newSite = new Site("This Site", getModel().getSiteNumber());
            getSiteSelectionComboBox().addItem(newSite);
            for (Site site : getModel().getSites()){
                getSiteSelectionComboBox().addItem(site);
            }
        }
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
            int count = getIntegerValue(adminCountTextField.getText());
            int startNumber = getIntegerValue(startNumberTextField.getText());
            if (count > 0) {
                getController().generateNewAccounts(ClientType.Type.ADMINISTRATOR.toString(), count, startNumber, true);
            }

            count = getIntegerValue(judgeCountTextField.getText());
            if (count > 0) {
                getController().generateNewAccounts(ClientType.Type.JUDGE.toString(), startNumber, count, true);
            }

            count = getIntegerValue(teamCountTextField.getText());
            if (count > 0) {
                getController().generateNewAccounts(ClientType.Type.TEAM.toString(), startNumber, count, true);
            }

            count = getIntegerValue(boardCountTextField.getText());
            if (count > 0) {
                getController().generateNewAccounts(ClientType.Type.SCOREBOARD.toString(), startNumber, count, true);
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
    
    public void setModelAndController(IModel inModel, IController inController) {
        super.setModelAndController(inModel, inController);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateGenerateTitles();
            }
        });
    }


} // @jve:decl-index=0:visual-constraint="10,10"
