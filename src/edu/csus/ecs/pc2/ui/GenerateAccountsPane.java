package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ISiteListener;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.SiteEvent;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import java.awt.FlowLayout;

/**
 * Generate accounts pane.
 * 
 * Can generate panes on any site, for any client type. Can use a client start number to start the numbering of the accounts.
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

    private JComboBox otherClientsComboBox = null;

    private JTextField otherClientCountTextBox = null;

    private JButton cancelButton = null;

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
        this.setSize(new java.awt.Dimension(441, 277));
        this.add(getCenterPanel(), java.awt.BorderLayout.CENTER);
        this.add(getGenerateButtonPanel(), java.awt.BorderLayout.SOUTH);

    }

    private void addWindowCloserListener() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (getParentFrame() != null) {
                    getParentFrame().addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            handleCancelButton();
                        }
                    });
                } 
            }
        });
    }

    private void updateSiteComboBox() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int selectedIndex = getSiteSelectionComboBox().getSelectedIndex();

                getSiteSelectionComboBox().removeAllItems();

                Site[] sites = getContest().getSites();
                Arrays.sort(sites, new SiteComparatorBySiteNumber());
                for (int i = 0; i < sites.length; i++) {
                    Site newSite;
                    if (sites[i].getSiteNumber() == getContest().getSiteNumber()) {
                        newSite = new Site(sites[i].getDisplayName() + " (Site "+sites[i].getSiteNumber()+", This Site)", getContest().getSiteNumber());
                        if (selectedIndex == -1) {  // default to local site
                            selectedIndex = i;
                        }
                    } else {
                        newSite = new Site(sites[i].getDisplayName() + " (Site "+sites[i].getSiteNumber()+")", sites[i].getSiteNumber());
                    }
                    getSiteSelectionComboBox().addItem(newSite);
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

                int index = getSiteSelectionComboBox().getSelectedIndex();
                if (index > -1) {
                    Site site = (Site) getSiteSelectionComboBox().getSelectedItem();
                    theSiteNumber = site.getSiteNumber();
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
            siteLabel.setBounds(new java.awt.Rectangle(79, 8, 48, 20));
            siteLabel.setText("Site");
            genStartNumberLabel = new JLabel();
            genStartNumberLabel.setBounds(new java.awt.Rectangle(81, 198, 166, 19));
            genStartNumberLabel.setText("Start Account Number at");
            genScoreboardLabel = new JLabel();
            genScoreboardLabel.setBounds(new java.awt.Rectangle(81, 129, 166, 19));
            genScoreboardLabel.setText("Scoreboards");
            genTeamLabels = new JLabel();
            genTeamLabels.setBounds(new java.awt.Rectangle(81, 99, 166, 19));
            genTeamLabels.setText("Teams");
            genJudgeLabel = new JLabel();
            genJudgeLabel.setBounds(new java.awt.Rectangle(81, 69, 166, 19));
            genJudgeLabel.setText("Judges");
            genAdminLabel = new JLabel();
            genAdminLabel.setBounds(new java.awt.Rectangle(81, 39, 166, 19));
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
            centerPanel.add(getOtherClientsComboBox(), null);
            centerPanel.add(getOtherClientCountTextBox(), null);
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
            adminCountTextField.setBounds(new java.awt.Rectangle(343, 35, 39, 22));
            adminCountTextField.setDocument(new IntegerDocument());
            adminCountTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
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
            boardCountTextField.setBounds(new java.awt.Rectangle(343, 127, 39, 22));
            boardCountTextField.setDocument(new IntegerDocument());
            boardCountTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
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
            teamCountTextField.setBounds(new java.awt.Rectangle(343, 97, 39, 22));
            teamCountTextField.setDocument(new IntegerDocument());
            teamCountTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
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
            judgeCountTextField.setBounds(new java.awt.Rectangle(343, 67, 39, 22));
            judgeCountTextField.setDocument(new IntegerDocument());
            judgeCountTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
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
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(40);
            generateButtonPanel = new JPanel();
            generateButtonPanel.setLayout(flowLayout);
            generateButtonPanel.setPreferredSize(new java.awt.Dimension(35, 35));
            generateButtonPanel.add(getGenerateButton(), null);
            generateButtonPanel.add(getCancelButton(), null);
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
            if (startNumber < 1) {
                startNumber = 1;
            }

            int thisSiteNumber = getContest().getSiteNumber();
            int theSiteNumber = thisSiteNumber;

            int index = getSiteSelectionComboBox().getSelectedIndex();
            if (index > -1) {
                Site site = (Site) getSiteSelectionComboBox().getSelectedItem();
                theSiteNumber = site.getSiteNumber();
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
                getController().generateNewAccounts(ClientType.Type.TEAM.toString(), theSiteNumber, count, startNumber, true);
                teamCountTextField.setText("");
            }

            count = getIntegerValue(boardCountTextField.getText());
            if (count > 0) {
                getController().generateNewAccounts(ClientType.Type.SCOREBOARD.toString(), theSiteNumber, count, startNumber, true);
                boardCountTextField.setText("");
            }
            
            count = getIntegerValue(otherClientCountTextBox.getText());
            if (count > 0) {
                Type type = (Type) otherClientsComboBox.getSelectedItem();
                getController().generateNewAccounts(type.toString(), theSiteNumber, count, startNumber, true);
                otherClientCountTextBox.setText("");
            }

            getStartNumberTextField().setText("");
            enableUpdateButtons(false);
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
            siteSelectionComboBox.setBounds(new java.awt.Rectangle(151, 9, 229, 19));
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
            startNumberTextField.setBounds(new java.awt.Rectangle(343, 193, 39, 22));
            startNumberTextField.setDocument(new IntegerDocument());
        }
        return startNumberTextField;
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        updateGenerateTitles();
        updateSiteComboBox();

        getContest().addAccountListener(new AccountListenerImplementation());
        getContest().addSiteListener(new SiteListenerImplementation());
        addWindowCloserListener();
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

        public void accountsAdded(AccountEvent accountEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateGenerateTitles();
                }
            });
        }

        public void accountsModified(AccountEvent accountEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateGenerateTitles();
                }
            });
        }

    }

    /**
     * Site Listener.
     * 
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

    /**
     * This method initializes otherClientsComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getOtherClientsComboBox() {
        if (otherClientsComboBox == null) {
            otherClientsComboBox = new JComboBox();
            otherClientsComboBox.setBounds(new java.awt.Rectangle(81, 162, 167, 20));
            otherClientsComboBox.setVisible(false);
            otherClientsComboBox.addItem(Type.EXECUTOR);
            otherClientsComboBox.addItem(Type.SPECTATOR);
        }
        return otherClientsComboBox;
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getOtherClientCountTextBox() {
        if (otherClientCountTextBox == null) {
            otherClientCountTextBox = new JTextField();
            otherClientCountTextBox.setBounds(new java.awt.Rectangle(343, 160, 39, 22));
            otherClientCountTextBox.setDocument(new IntegerDocument());
            otherClientCountTextBox.setVisible(false);
        }
        return otherClientCountTextBox;
    }

    /**
     * Enable or disable Cancel/Close button based on the count text fields.
     * 
     */
    public void enableUpdateButton() {
        boolean noData = false;
        // in 2 lines to keep the line size down
        noData = isTextFieldEmpty(getAdminCountTextField()) && isTextFieldEmpty(getJudgeCountTextField());
        noData = noData && (isTextFieldEmpty(getTeamCountTextField()) && isTextFieldEmpty(getBoardCountTextField()));
        enableUpdateButtons(!noData);
    }

    protected void enableUpdateButtons(boolean editedText) {
        if (editedText){
            getCancelButton().setText("Cancel");
        }else{
            getCancelButton().setText("Close");
        }
        getGenerateButton().setEnabled(editedText);
    }
    
    private boolean isTextFieldEmpty(JTextField textField) {
        return textField.getText().trim().equals("");
    }
    
    protected void handleCancelButton() {
        if (isTextFieldEmpty(getAdminCountTextField()) && isTextFieldEmpty(getJudgeCountTextField()) && isTextFieldEmpty(getTeamCountTextField()) && isTextFieldEmpty(getBoardCountTextField())){
            // all the counte textFields are empty, eg no changes
            if ( getParentFrame() != null){
                getParentFrame().setVisible(false);
            }
        } else {
            // Something changed, are they sure ?
            
            int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Counts modified, generate accounts?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                generateAccounts();
                if ( getParentFrame() != null){
                    getParentFrame().setVisible(false);
                }
            } else if (result == JOptionPane.NO_OPTION) {
                clearCounts();
                if ( getParentFrame() != null){
                    getParentFrame().setVisible(false);
                }
            }
        }

    }
    
    private void clearCounts() {
        getAdminCountTextField().setText("");
        getJudgeCountTextField().setText("");
        getTeamCountTextField().setText("");
        getBoardCountTextField().setText("");
        getStartNumberTextField().setText("");
        enableUpdateButtons(false);
    }

    /**
     * This method initializes cancelButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
            cancelButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
            cancelButton.setPreferredSize(new java.awt.Dimension(150,26));
            cancelButton.setVisible(false);
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleCancelButton();
                }
            });
        }
        return cancelButton;
    }

    /* (non-Javadoc)
     * @see edu.csus.ecs.pc2.ui.JPanePlugin#setParentFrame(javax.swing.JFrame)
     */
    @Override
    public void setParentFrame(JFrame parentFrame) {
        super.setParentFrame(parentFrame);
        getCancelButton().setVisible(true);
        enableUpdateButton();
    }

} // @jve:decl-index=0:visual-constraint="10,10"

