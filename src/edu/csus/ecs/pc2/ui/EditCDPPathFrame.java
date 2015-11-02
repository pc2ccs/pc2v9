/**
 * 
 */
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * This Frame supports editing the current Contest Data Package (CDP) path values stored in the system.
 * There are two CPD paths:  one for the Administrator and one for the Judge machines.  These might have
 * the same path value, or they might be different.
 * 
 * @author John
 *
 */
public class EditCDPPathFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private IInternalContest contest;

    private IInternalController controller;
    private JTextField adminCDPPathTextField;
    private JTextField textField;
    
    public EditCDPPathFrame() {
        
        initialize();
    }

    protected void saveCDPPaths() {
        System.err.println("EditCPDPathFrame.saveCurrentCDPPath() invoked but not implemented!");
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
      
      adminCDPPathTextField = new JTextField();
      adminCDPPathPanel.add(adminCDPPathTextField);
      adminCDPPathTextField.setColumns(10);
      
      JPanel judgesCDPPathPanel = new JPanel();
      judgesCDPPathPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
      textPanel.add(judgesCDPPathPanel);
      
      JLabel lblJudgesCDPPath = new JLabel("Full Path to Judge's machines CDP Root:  ");
      judgesCDPPathPanel.add(lblJudgesCDPPath);
      
      textField = new JTextField();
      judgesCDPPathPanel.add(textField);
      textField.setColumns(10);
      
      JPanel buttonPanel = new JPanel();
      mainPanel.add(buttonPanel, BorderLayout.SOUTH);
      
      JButton btnSetProperties = new JButton("Set");
      btnSetProperties.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              saveCDPPaths();
          }
      });
      buttonPanel.add(btnSetProperties);
      
      JButton btnCancelButton = new JButton("Cancel");
      btnCancelButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              handleCancelButton();
          }
      });
      buttonPanel.add(btnCancelButton);
      
      loadCurrentCDPPath();
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
    public void loadCurrentCDPPath() {
        //TODO: implement me
        System.err.println("EditCPDPathFrame.loadCurrentCDPPath() invoked but not implemented!");
        
    }

}
