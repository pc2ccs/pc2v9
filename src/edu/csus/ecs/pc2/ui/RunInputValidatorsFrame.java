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
//import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * A frame for running the input validators for currently defined problems and displaying the results.
 * 
 * @author $Author$ John
 * @version $Id$
 */
public class RunInputValidatorsFrame extends JFrame implements UIPlugin  {

    private static final long serialVersionUID = 1;

    private IInternalContest contest;

    private IInternalController controller;

    private JButton closeButton;

    private JButton runAllButton;

    
    public RunInputValidatorsFrame() {
        
        initialize();
    }


    private void initialize() {

      this.setSize(new java.awt.Dimension(549, 312));
      this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
      this.setTitle("Run Problem Input Validators");

      this.addWindowListener(new java.awt.event.WindowAdapter() {
          public void windowClosing(java.awt.event.WindowEvent e) {
              handleCancelButton();
          }
      });
      FrameUtilities.centerFrame(this);

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
      
      
      JPanel judgesCDPPathPanel = new JPanel();
      judgesCDPPathPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
      textPanel.add(judgesCDPPathPanel);
      
      JLabel lblJudgesDataPath = new JLabel("Full path to data files on Judge machines:  ");
      judgesCDPPathPanel.add(lblJudgesDataPath);
      
      
      JPanel buttonPanel = new JPanel();
      mainPanel.add(buttonPanel, BorderLayout.SOUTH);
      
      JButton runSelectedButton = new JButton("Run Selected");
      runSelectedButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              System.out.println ("Not implemented yet...");
//            JOptionPane.showMessageDialog(this, "This function isn't implemented yet...", "Not Implemented", JOptionPane.INFORMATION_MESSAGE);
          }
      });
      buttonPanel.add(runSelectedButton);
      
      buttonPanel.add(getRunAllButton());
  
      buttonPanel.add(getCloseButton()); 
    }
    
    private JButton getRunAllButton() {
        if (runAllButton == null) {
            runAllButton = new JButton("Run All");
            runAllButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println ("Not implemented yet...");
//                    JOptionPane.showMessageDialog(this, "This function isn't implemented yet...", "Not Implemented", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
        return runAllButton;
    }
    
    private JButton getCloseButton() {
        if (closeButton == null) {
            closeButton = new JButton("Close");
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleCancelButton();
                }
            });
        }
        return closeButton;
    }
    
    private void handleCancelButton() {
        this.setVisible(false);
    }
    



    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        contest = inContest;
        controller = inController;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateUI();

            }
        });
    }

    /**
     * Populate UI fields.
     */
    protected void populateUI() {
        
        
    }

    public IInternalContest getContest() {
        return contest;
    }
    
    public IInternalController getController() {
        return controller;
    }

    

    
   

    @Override
    public String getPluginTitle() {
        return "Run Input Validators Frame";
    }
}
