/**
 * 
 */
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * This Frame supports editing the current Contest Data Package (CDP) path values stored in the system.
 * There are two CDP paths:  one for the Administrator and one for the Judge machines.  These might have
 * the same path value, or they might be different.  The actual path values (Strings) are stored in
 * an instance of ContestInformation, which requires having an IContest and an IInternalController to 
 * access; therefore, classes using this class should call this class's setContestAndController() method
 * after this class is instantiated.  Clients should then call this class's loadCurrentCDPPathsIntoGUI() method
 * to update the GUI for proper display.
 * 
 * @author John
 *
 */
public class EditCDPPathFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private IInternalContest contest;

    private IInternalController controller;
    private JTextField adminCDPPathTextField;
    private JTextField judgeCDPPathTextField;

    private JButton cancelButton;
    
    public EditCDPPathFrame() {
        
        initialize();
    }

    /**
     * Checks what type of client is logged in, and then checks for a valid CDP at the location specified
     * by the user in the GUI for the CDP Path for that type of client (Admin or Judge). 
     * If a "valid" CDP is found, saves both the Admin and the Judge CDP paths in the ContestInformation object.
     */
    protected void handleCDPSaveButton() {
        
        boolean validCDP ;
        if (isAdmin()) {
            validCDP = validateCDP(getAdminCDPPathTextField().getText());
            if (validCDP) {
                saveCDPPaths();
            } else {
                JOptionPane.showMessageDialog(this, "No valid CDP found at " + getAdminCDPPathTextField().getText(), "Invalid CDP", JOptionPane.OK_OPTION);
            }
            
        } else if (isJudge()) {
            // (This is really provided just for the eventuality that sometime this EditCDPPathFrame might get used by the Judge code...)
            validCDP = validateCDP(getJudgeCDPPathTextField().getText());
            if (validCDP) {
                saveCDPPaths();
            } else {
                JOptionPane.showMessageDialog(this, "No valid CDP found at " + getJudgeCDPPathTextField().getText(), "Invalid CDP", JOptionPane.OK_OPTION);
            }
        }
    }

    /**
     * Saves the currently-defined CDP Paths into the ContestInformation.
     */
    private void saveCDPPaths() {
        
        if (getContest() != null) {
            ContestInformation ci = getContest().getContestInformation();
        
            String curAdminPath = getAdminCDPPathTextField().getText();
            if (curAdminPath != null && curAdminPath.equals("<null>")) {
                curAdminPath = null;
            }
            ci.setAdminCDPBasePath(curAdminPath);
            
            String curJudgePath = getJudgeCDPPathTextField().getText();
            if (curJudgePath != null && curJudgePath.equals("<null>")) {
                curJudgePath = null;
            }
            ci.setJudgeCDPBasePath(curJudgePath);
            
            //once we've saved, change "Cancel" to "Close" since we don't support "Cancel" in the sense of "Undo Save"
            getCancelButton().setText("Close");
            
        } else {
            //getContest() returned null
            IInternalController contr = getController();
            if (contr != null) {
                getController().getLog().log(Log.SEVERE, "EditCDPPathFrame.saveCDPPaths(): getContest() returned null");
            } else {
                System.err.println ("EditCDPPathFrame.saveCDPPaths(): getContest() or getController() returned null");
            }
        }
        
    }

    /**
     * Returns true if the current client is logged in as an Admin; false otherwise.
     * @return whether or not the current client is an Admin
     */
    private boolean isAdmin() {        
        if (getContest().getClientId().getClientType() == ClientType.Type.ADMINISTRATOR) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Returns true if the current client is logged in as a Judge; false otherwise.
     * @return whether or not the current client is a Judge
     */
    private boolean isJudge() {
        if (getContest().getClientId().getClientType() == ClientType.Type.JUDGE) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Returns true if it finds what it considers a valid Contest Data Package structure at the specified path.
     * @param path - the path to the root folder of the purported CDP
     * @return true if it appears there is a valid CDP at the specified path root
     */
    private boolean validateCDP(String path) {
        //TODO: implement this method: verify that a reasonable CDP structure is found at the given path
        System.err.println("EditCDPPathFrame.validateCDP(): missing code; would have checked for a CDP at '" + path + "'; assuming valid and returning true" );
        return true ;
    }
    
    private void initialize() {

      this.setSize(new java.awt.Dimension(549, 312));
//      this.setContentPane(getAnswerClarificationPane());
      this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
      this.setTitle("Set Contest Data Package Path Root");

      this.addWindowListener(new java.awt.event.WindowAdapter() {
          public void windowClosing(java.awt.event.WindowEvent e) {
              handleCancelButton();
          }
      });
      FrameUtilities.centerFrame(this);

      //TODO: move all the following into a separate SetCDPPathPane class, use a singleton accessor with it
      JPanel mainPanel = new JPanel();
      getContentPane().add(mainPanel, BorderLayout.NORTH);
      mainPanel.setLayout(new BorderLayout(0, 0));
      
      Component verticalStrut = Box.createVerticalStrut(20);
      mainPanel.add(verticalStrut, BorderLayout.NORTH);
      
      JPanel textPanel = new JPanel();
      mainPanel.add(textPanel, BorderLayout.CENTER);
      textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
      
      JPanel adminCDPPathPanel = new JPanel();
      adminCDPPathPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
      textPanel.add(adminCDPPathPanel);
      
      JLabel lblAdminCDPPath = new JLabel("Full Path to Admin machine CDP Root:  ");
      adminCDPPathPanel.add(lblAdminCDPPath);
      
      adminCDPPathTextField = getAdminCDPPathTextField();
      adminCDPPathPanel.add(adminCDPPathTextField);
      
      JPanel judgesCDPPathPanel = new JPanel();
      judgesCDPPathPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
      textPanel.add(judgesCDPPathPanel);
      
      JLabel lblJudgesCDPPath = new JLabel("Full Path to Judge's machines CDP Root:  ");
      judgesCDPPathPanel.add(lblJudgesCDPPath);
      
      judgeCDPPathTextField = getJudgeCDPPathTextField();
      judgesCDPPathPanel.add(judgeCDPPathTextField);
      
      JPanel buttonPanel = new JPanel();
      mainPanel.add(buttonPanel, BorderLayout.SOUTH);
      
      JButton btnSaveCDPPathValues = new JButton("Save");
      btnSaveCDPPathValues.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              handleCDPSaveButton();
          }
      });
      buttonPanel.add(btnSaveCDPPathValues);
      buttonPanel.add(getCancelButton()); 
    }
    
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleCancelButton();
                }
            });
        }
        return cancelButton;
    }
    
    private JTextField getAdminCDPPathTextField() {
        if (adminCDPPathTextField == null) {
            adminCDPPathTextField = new JTextField();
            adminCDPPathTextField.setColumns(20);
        }
        return adminCDPPathTextField;
    }

    private JTextField getJudgeCDPPathTextField() {
        if (judgeCDPPathTextField == null) {
            judgeCDPPathTextField = new JTextField();
            judgeCDPPathTextField.setColumns(20);
        }
        return judgeCDPPathTextField;
    }

    protected void handleCancelButton() {
        this.dispose();
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        contest = inContest;
        controller = inController;
    }
    
    public IInternalContest getContest() {
        return contest;
    }
    
    public IInternalController getController() {
        return controller;
    }

    /** Checks the currently-defined CDP path properties and loads them into the appropriate textboxes.
     * 
     */
    public void loadCurrentCDPPathsIntoGUI() {
        
        if (getContest() != null) {
            ContestInformation ci = getContest().getContestInformation();
        
            String curAdminPath = ci.getAdminCDPBasePath();
            if (curAdminPath == null) {
                curAdminPath = "<null>";
            }
            getAdminCDPPathTextField().setText(curAdminPath);
            
            String curJudgePath = ci.getJudgeCDPBasePath();
            if (curJudgePath == null) {
                curJudgePath = "<null>";
            }
            getJudgeCDPPathTextField().setText(curJudgePath);
            
            
        } else {
            //getContest() returned null
            IInternalController contr = getController();
            if (contr != null) {
                getController().getLog().log(Log.SEVERE, "EditCDPPathFrame.loadCurrentCDPPathsIntoGUI(): getContest() returned null");
            } else {
                System.err.println ("EditCDPPathFrame.loadCurrentCDPPathsIntoGUI(): getContest() or getController() returned null");
            }
        }
    }
            


}
