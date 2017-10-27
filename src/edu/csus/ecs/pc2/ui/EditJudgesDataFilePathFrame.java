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
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.MultipleIssuesException;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Edit Admin and Judge CDP paths.
 * 
 * This Frame supports editing the current Contest Data Package (CDP) path values stored in the system.
 * There are two CDP paths:  one for the Administrator and one for the Judge machines.  These might have
 * the same path value, or they might be different.  The actual path values (Strings) are stored in
 * an instance of ContestInformation, which requires having an IContest and an IInternalController to 
 * access; therefore, classes using this class should call this class's setContestAndController() method
 * after this class is instantiated.  Clients should then call this class's loadCurrentCDPPathsIntoGUI() method
 * to update the GUI for proper display.
 * 
 * @author $Author$ John
 * @version $Id$
 */
public class EditJudgesDataFilePathFrame extends JFrame implements UIPlugin  {

    /**
     * 
     */
    private static final long serialVersionUID = -8570718182014642771L;

    /**
     * Special String value for any field that is null,
     */
    private static final String NULL_STRING_VALUE = "<null>";

    private IInternalContest contest;

    private IInternalController controller;
    private JTextField adminCDPPathTextField;
    private JTextField judgeCDPPathTextField;

    private JButton cancelButton;
    private JButton updateButton = null;

    private Log log;
    
    public EditJudgesDataFilePathFrame() {
        
        initialize();
    }

    /**
     * saves the Judge data path in the ContestInformation object.
     */
    protected void handleUpdateDataPathButton() {
                
        if (getContest() != null) {
            ContestInformation ci = getContestInformation();
            
            if (ci == null){
                log.warning("Initializing ContestInformation in Contest, should have already been initialized");
                ci = new ContestInformation();
            }
                    
            String curJudgePath = getJudgeDataPathTextField().getText();
            if (curJudgePath != null && curJudgePath.equals(NULL_STRING_VALUE)) {
                curJudgePath = null;
            }
            ci.setJudgeCDPBasePath(curJudgePath);
            
            getController().updateContestInformation(ci);
            this.setVisible(false);
            
        } else {
            
            
            /**
             * This should never happen because setContestandController will initialize the contest and
             * controller.   If this does happen then the setContestandController must be called/used 
             * from the calling frame.
             */
            
            //getContest() returned null
            IInternalController contr = getController();
            if (contr != null) {
                getController().getLog().log(Log.SEVERE, "EditCDPPathFrame.handleUpdateDataPathButton(): getContest() returned null");
            } else {
                System.err.println ("EditCDPPathFrame.handleUpdateDataPathButton(): getContest() or getController() returned null");
            }
        }
    }

    private ContestInformation getContestInformation() {

        return getContest().getContestInformation();
    }

    // SOMEDAY - remove this unused code.
//    /**
//     * Returns true if the current client is logged in as an Admin; false otherwise.
//     * @return whether or not the current client is an Admin
//     */
//    private boolean isAdmin() {        
//        if (getContest().getClientId().getClientType() == ClientType.Type.ADMINISTRATOR) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//    
//    /**
//     * Returns true if the current client is logged in as a Judge; false otherwise.
//     * @return whether or not the current client is a Judge
//     */
//    private boolean isJudge() {
//        if (getContest().getClientId().getClientType() == ClientType.Type.JUDGE) {
//            return true;
//        } else {
//            return false;
//        }
//    }
    
    /**
     * Returns true if it finds what it considers a valid Contest Data Package structure at the specified path.
     * @param path - the path to the root folder of the purported CDP
     * @return true if it appears there is a valid CDP at the specified path root
     */
    @SuppressWarnings("unused")
    private boolean validateCDP(String path) {
        
        // TODO: implement this method: verify that a reasonable CDP structure is found at the given path
        System.err.println("TODO EditCDPPathFrame.validateCDP(): missing code; would have checked for a CDP at '" + path + "'; assuming valid and returning true");
        try {
            Utilities.validateCDP(getContest(), path);
        } catch (MultipleIssuesException e) {

            showMessage("Not valid CDP for path " + path, "Invalid CDP");

            String[] messages = e.getIssueList();
            System.err.println("Errors in CDP at path '" + path + "'");
            for (String message : messages) {
                System.err.println(message);
                log.warning(message);
            }
            
            // TODO TODAY Figure out how strict we should be after the regionals.
            int result = FrameUtilities.yesNoCancelDialog(this, "TEMPORARY DEBUGGING THING. CDP is invalid - save anyways?", "TEMPORARY DEBUGGING THING.");

            if (result == JOptionPane.YES_OPTION) {
                return true;
            }
            
            return false;
        }

        return true;
    }
    
    private void showMessage(String string, String title) {
        JOptionPane.showMessageDialog(null, string, title, JOptionPane.WARNING_MESSAGE);
    }

    private void initialize() {

      this.setSize(new java.awt.Dimension(549, 312));
//      this.setContentPane(getAnswerClarificationPane());
      this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
      this.setTitle("Set External Data File Path");

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
      adminCDPPathPanel.setEnabled(false);
      adminCDPPathPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
      textPanel.add(adminCDPPathPanel);
      
      JLabel lblAdminCDPPath = new JLabel("Full Path to Admin machine CDP Root:  ");
      lblAdminCDPPath.setEnabled(false);
      adminCDPPathPanel.add(lblAdminCDPPath);
      
      adminCDPPathTextField = getAdminCDPPathTextField();
      adminCDPPathTextField.addKeyListener(new java.awt.event.KeyAdapter() {
          public void keyReleased(java.awt.event.KeyEvent e) {
              enableUpdateButton();
          }
      });
      adminCDPPathPanel.add(adminCDPPathTextField);
      
      JPanel judgesCDPPathPanel = new JPanel();
      judgesCDPPathPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
      textPanel.add(judgesCDPPathPanel);
      
      JLabel lblJudgesDataPath = new JLabel("Full path to data files on Judge machines:  ");
      judgesCDPPathPanel.add(lblJudgesDataPath);
      
      judgeCDPPathTextField = getJudgeDataPathTextField();
      judgeCDPPathTextField.addKeyListener(new java.awt.event.KeyAdapter() {
          public void keyReleased(java.awt.event.KeyEvent e) {
              enableUpdateButton();
          }
      });
      judgesCDPPathPanel.add(judgeCDPPathTextField);
      
      JPanel buttonPanel = new JPanel();
      mainPanel.add(buttonPanel, BorderLayout.SOUTH);
  
      buttonPanel.add(getUpdateButton());
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
            adminCDPPathTextField.setEnabled(false);
            adminCDPPathTextField.setColumns(20);
        }
        return adminCDPPathTextField;
    }

    private JTextField getJudgeDataPathTextField() {
        if (judgeCDPPathTextField == null) {
            judgeCDPPathTextField = new JTextField();
            judgeCDPPathTextField.setColumns(20);
        }
        return judgeCDPPathTextField;
    }

    protected void handleCancelButton() {
       
        if (updateButton.isEnabled()){
            // something was changed, ask them if they want to save it?
            
            int result = FrameUtilities.yesNoCancelDialog(this, "Data path modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                handleUpdateDataPathButton();
                this.dispose();
            } else if (result == JOptionPane.NO_OPTION) {
                this.dispose();
            } // else cancel do nothing
            
        } else {
            // nothing updated hide me!!
            this.setVisible(false);
        }
            
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        contest = inContest;
        controller = inController;
        
        log =  inController.getLog();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateUI();
                enableUpdateButtons(false);
            }
        });
    }

    /**
     * Populate UI fields.
     */
    protected void populateUI() {
        
        ContestInformation info = getContestInformation();
        
        String judgeCDP = info.getJudgeCDPBasePath();
        
        if (judgeCDP == null){
            judgeCDP = NULL_STRING_VALUE;
        }
        
        String adminCDP = info.getAdminCDPBasePath();
        
        if (adminCDP == null){
            adminCDP = NULL_STRING_VALUE;
        }
        
         getAdminCDPPathTextField().setText(adminCDP);
         getJudgeDataPathTextField().setText(judgeCDP);
        
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
                curAdminPath = "";
            }
            getAdminCDPPathTextField().setText(curAdminPath);
            
            String curJudgePath = ci.getJudgeCDPBasePath();
            if (curJudgePath == null) {
                curJudgePath = "";
            }
            getJudgeDataPathTextField().setText(curJudgePath);
            
            
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
    
    /**
     * Enable or disable Update button based on comparison fields.
     * 
     */
    public void enableUpdateButton() {

        boolean enableButton = false;

        ContestInformation information = getContest().getContestInformation();

        if (information != null) {
            
            String fieldValue = getTextFieldValue(adminCDPPathTextField);
            if (!StringUtilities.stringSame(information.getAdminCDPBasePath(), fieldValue)) {
                enableButton = true;
            }

            String judgeFieldValue = getTextFieldValue(judgeCDPPathTextField);
            if (!StringUtilities.stringSame(information.getJudgeCDPBasePath(), judgeFieldValue)) {
                enableButton = true;
            }

        } else {
            enableButton = true;
        }
            
        enableUpdateButtons(enableButton);
    }
    
    /**
     * Returns JTextField value, iv falue is {@value #NULL_STRING_VALUE} then returns null.
     * @param textField
     * @return null if value is {@value #NULL_STRING_VALUE}, else return text field value.
     */
    private String getTextFieldValue(JTextField textField) {
        
        String fieldValue = textField.getText();
        
        if (fieldValue.equalsIgnoreCase(NULL_STRING_VALUE)) {
            fieldValue = null; 
        }
        
        return fieldValue;
    }

    public JButton getUpdateButton() {

        if (updateButton == null) {
            updateButton = new JButton("Update");
            updateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleUpdateDataPathButton();
                }
            });
        }
        return updateButton;
    }
    
    protected void enableUpdateButtons(boolean editedText) {
        getUpdateButton().setEnabled(editedText);
        
        if (editedText) {
            cancelButton.setText("Cancel");
        } else {
            cancelButton.setText("Close");
        }
    }

    @Override
    public String getPluginTitle() {
        return "Edit CDP Frame";
    }
}
