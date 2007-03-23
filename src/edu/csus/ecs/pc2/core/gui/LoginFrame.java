package edu.csus.ecs.pc2.core.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JPasswordField;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.KeyEvent;
import java.awt.Color;

/**
 * Login frame for all clients.
 * 
 * @author pc2@ecs.csus.edu
 */
// $HeadURL: http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu/csus/ecs/pc2/core/gui/LoginFrame.java $
public class LoginFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = -6389607881992853161L;

    private JPanel jPanel = null;

    private JPasswordField jPasswordField = null;

    private JTextField jTextField = null;

    private JLabel jLabel = null;

    private JLabel jLabel1 = null;

    private JLabel jLabel2 = null;

    private JLabel jLabel3 = null;

    private JLabel jLabel4 = null;

    private JButton jButton = null;

    private JButton jButton1 = null;

    private JLabel jLabel5 = null;

    /**
     * This method initializes
     * 
     */
    public LoginFrame() {
        super();
        initialize();
        FrameUtilities.centerFrame(this);
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(429, 365));
        this.setTitle("PC^2 Login");
        this.setContentPane(getJPanel());
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.insets = new Insets(4, 5, 2, 5);
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.gridy = 7;
            gridBagConstraints9.ipadx = 411;
            gridBagConstraints9.ipady = 26;
            gridBagConstraints9.gridwidth = 2;
            jLabel5 = new JLabel();
            jLabel5.setForeground(Color.red);
            jLabel5.setText("");
            jLabel5.setHorizontalAlignment(SwingConstants.CENTER);
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.insets = new Insets(14, 37, 3, 92);
            gridBagConstraints8.gridy = 6;
            gridBagConstraints8.ipadx = 40;
            gridBagConstraints8.gridx = 1;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.insets = new Insets(14, 87, 3, 37);
            gridBagConstraints7.gridy = 6;
            gridBagConstraints7.ipadx = 30;
            gridBagConstraints7.gridx = 0;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.insets = new Insets(0, 24, 5, 20);
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.gridy = 1;
            gridBagConstraints6.ipadx = 168;
            gridBagConstraints6.ipady = 9;
            gridBagConstraints6.gridwidth = 2;
            jLabel4 = new JLabel();
            jLabel4.setFont(new Font("Dialog", Font.BOLD, 18));
            jLabel4.setText("Contest Control System");
            jLabel4.setHorizontalAlignment(SwingConstants.CENTER);
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.insets = new Insets(7, 81, 6, 72);
            gridBagConstraints5.gridy = 4;
            gridBagConstraints5.gridx = 0;
            jLabel3 = new JLabel();
            jLabel3.setText("Password");
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.insets = new Insets(20, 22, 0, 23);
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.gridy = 0;
            gridBagConstraints4.ipadx = 202;
            gridBagConstraints4.ipady = 6;
            gridBagConstraints4.gridwidth = 2;
            jLabel2 = new JLabel();
            jLabel2.setFont(new Font("Dialog", Font.BOLD, 18));
            jLabel2.setHorizontalTextPosition(SwingConstants.CENTER);
            jLabel2.setText("CSUS Programming");
            jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.insets = new Insets(2, 5, 11, 11);
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.gridy = 8;
            gridBagConstraints3.ipadx = 265;
            gridBagConstraints3.gridwidth = 2;
            jLabel1 = new JLabel();
            jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
            jLabel1.setText("Version XX. XX YYYY vv 22");
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.insets = new Insets(6, 80, 5, 95);
            gridBagConstraints2.gridy = 2;
            gridBagConstraints2.gridx = 0;
            jLabel = new JLabel();
            jLabel.setText("Name");
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridwidth = 2;
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 3;
            gridBagConstraints1.ipadx = 244;
            gridBagConstraints1.weightx = 1.0;
            gridBagConstraints1.insets = new Insets(6, 80, 6, 93);
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 5;
            gridBagConstraints.ipadx = 246;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(7, 80, 13, 91);
            jPanel = new JPanel();
            jPanel.setLayout(new GridBagLayout());
            jPanel.add(getJPasswordField(), gridBagConstraints);
            jPanel.add(getJTextField(), gridBagConstraints1);
            jPanel.add(jLabel, gridBagConstraints2);
            jPanel.add(jLabel1, gridBagConstraints3);
            jPanel.add(jLabel2, gridBagConstraints4);
            jPanel.add(jLabel3, gridBagConstraints5);
            jPanel.add(jLabel4, gridBagConstraints6);
            jPanel.add(getJButton(), gridBagConstraints7);
            jPanel.add(getJButton1(), gridBagConstraints8);
            jPanel.add(jLabel5, gridBagConstraints9);
        }
        return jPanel;
    }

    /**
     * This method initializes jPasswordField
     * 
     * @return javax.swing.JPasswordField
     */
    private JPasswordField getJPasswordField() {
        if (jPasswordField == null) {
            jPasswordField = new JPasswordField();
        }
        return jPasswordField;
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJTextField() {
        if (jTextField == null) {
            jTextField = new JTextField();
        }
        return jTextField;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton() {
        if (jButton == null) {
            jButton = new JButton();
            jButton.setMnemonic(KeyEvent.VK_L);
            jButton.setText("Login");
            jButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
                }
            });
        }
        return jButton;
    }

    /**
     * This method initializes jButton1
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton1() {
        if (jButton1 == null) {
            jButton1 = new JButton();
            jButton1.setMnemonic(KeyEvent.VK_X);
            jButton1.setText("Exit");
            jButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.exit(0);
                }
            });
        }
        return jButton1;
    }
    
    public static void main(String[] args) {
        new LoginFrame().setVisible(true);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
