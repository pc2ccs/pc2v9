// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 * This class defines a {@link JPanel} containing Shadow Mode Settings.
 * The shadow mode settings include the Remote CCS URL, Login account, and Password,
 * along with a checkbox used to enable/disable shadow mode.
 * 
 * Note that the active components (including {@link JTextField}s for the Remote CCS URL,
 * login, and password, along with the checkbox indicating whether shadow mode is to be enabled or not),
 * do not by default have any listeners attached (because this pane cannot know what an arbitrary 
 * encapsulating client might want for listener operations.
 * If a client wishes to have the active components of this pane invoke listeners, it is the client's 
 * responsibility to invoke the corresponding "getters" ({@link #getRemoteCCSURLTextfield()},
 * {@link #getRemoteCCSLoginTextfield()}, {@link #getRemoteCCSPasswdTextfield()}, and 
 * {@link #getShadowModeCheckbox()}) and call their <I>addKeyListener()</i> and/or <I>addActionListener()</i>
 * methods as appropriate.
 * 
 * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
 *
 */
public class ShadowSettingsPane extends JPanel {

    private static final long serialVersionUID = 1L;
    
    private JCheckBox shadowModeCheckbox;
    private JLabel labelPrimaryCCSURL;
    private JTextField textfieldPrimaryCCSURL;
    private JLabel labelPrimaryCCSLogin;
    private JTextField textfieldPrimaryCCSLogin;
    private JLabel labelPrimaryCCSPasswd;    
    private JTextField textfieldPrimaryCCSPasswd;

    private Border margin = new EmptyBorder(5,10,5,10); //top,left,bottom,right

    public ShadowSettingsPane() {

        this.setMinimumSize(new Dimension(850, 100));
        this.setMaximumSize(new Dimension(850, 100));
        this.setPreferredSize(new Dimension(850, 100));
        this.setAlignmentX(LEFT_ALIGNMENT);

        this.setLayout(new GridBagLayout());

        TitledBorder tb = BorderFactory.createTitledBorder("Shadow Mode");
        this.setBorder(new CompoundBorder(margin, tb));

        labelPrimaryCCSURL = new JLabel();
        labelPrimaryCCSURL.setHorizontalAlignment(SwingConstants.RIGHT);
        labelPrimaryCCSURL.setText("Primary CCS URL:  ");

        labelPrimaryCCSLogin = new JLabel();
        labelPrimaryCCSLogin.setHorizontalAlignment(SwingConstants.RIGHT);
        labelPrimaryCCSLogin.setText("Primary CCS Login (account):  ");

        labelPrimaryCCSPasswd = new JLabel();
        labelPrimaryCCSPasswd.setHorizontalAlignment(SwingConstants.RIGHT);
        labelPrimaryCCSPasswd.setText("Primary CCS Password:  ");

        // the content of the pane:

        this.add(getShadowModeCheckbox(), getShadowModeCheckboxConstraints());
        this.add(labelPrimaryCCSURL, getRemoteCCSURLLabelConstraints());
        this.add(getRemoteCCSURLTextfield(), getRemoteCCSURLTextfieldConstraints());
        this.add(labelPrimaryCCSLogin, getRemoteCCSLoginLabelConstraints());
        this.add(getRemoteCCSLoginTextfield(), getRemoteCCSLoginTextfieldConstraints());
        this.add(labelPrimaryCCSPasswd, getRemoteCCSPasswdLabelConstraints());
        this.add(getRemoteCCSPasswdTextfield(), getRemoteCCSPasswdTextfieldConstraints());
    }
    
    /**
     * Returns a reference to the JTextField holding the Primary (remote) CCS login (account name).
     * 
     * Note that invoking this method constructs the JTextField object if it does not 
     * already exist, but it does <I>not</i> add any listeners (e.g. KeyListeners)
     * to the text field.  It is up to the calling routine to add any desired listeners to the 
     * JTextField.
     * 
     * @return a JTextField holding the Primary (Remote) CCS login (account name)
     */
    public JTextField getRemoteCCSLoginTextfield() {
        if (textfieldPrimaryCCSLogin == null) {
            textfieldPrimaryCCSLogin = new JTextField();
            textfieldPrimaryCCSLogin.setColumns(20);
            textfieldPrimaryCCSLogin.setEditable(true);
            textfieldPrimaryCCSLogin.setToolTipText("The account used to login to the Primary CCS (when operating in 'Shadow Mode')");
            
            textfieldPrimaryCCSLogin.setMaximumSize(new Dimension(150,20));
            textfieldPrimaryCCSLogin.setMinimumSize(new Dimension(150,20));
            textfieldPrimaryCCSLogin.setPreferredSize(new Dimension(150,20));

        }
        return textfieldPrimaryCCSLogin ;
    }

    /**
     * Returns a reference to the JTextField holding the Primary (remote) CCS password.
     * 
     * Note that invoking this method constructs the JTextField object if it does not 
     * already exist, but it does <I>not</i> add any listeners (e.g. KeyListeners)
     * to the text field.  It is up to the calling routine to add any desired listeners to the 
     * JTextField.
     * 
     * @return a JTextField holding the Primary (Remote) CCS password
     */
    public JTextField getRemoteCCSPasswdTextfield() {
        if (textfieldPrimaryCCSPasswd == null) {
            
            textfieldPrimaryCCSPasswd = new JPasswordField();
            textfieldPrimaryCCSPasswd.setColumns(20);
            textfieldPrimaryCCSPasswd.setEditable(true);
            textfieldPrimaryCCSPasswd.setToolTipText("The Primary CCS account password");

            textfieldPrimaryCCSPasswd.setMaximumSize(new Dimension(150,20));
            textfieldPrimaryCCSPasswd.setMinimumSize(new Dimension(150,20));
            textfieldPrimaryCCSPasswd.setPreferredSize(new Dimension(150,20));

        }
        return textfieldPrimaryCCSPasswd ;
    }

    /**
     * Returns a reference to the JTextField holding the Primary (remote) CCS URL.
     * 
     * Note that invoking this method constructs the JTextField object if it does not 
     * already exist, but it does <I>not</i> add any listeners (e.g. KeyListeners)
     * to the text field.  It is up to the calling routine to add any desired listeners to the 
     * JTextField.
     * 
     * @return a JTextField holding the Primary (Remote) CCS URL
     */
    public JTextField getRemoteCCSURLTextfield() {
        if (textfieldPrimaryCCSURL == null) {
            textfieldPrimaryCCSURL = new JTextField();
            textfieldPrimaryCCSURL.setColumns(50);
            textfieldPrimaryCCSURL.setEditable(true);
            textfieldPrimaryCCSURL.setToolTipText("The URL to the Primary CCS (when operating in 'Shadow Mode')");
            
            textfieldPrimaryCCSURL.setMaximumSize(new Dimension(400,20));
            textfieldPrimaryCCSURL.setMinimumSize(new Dimension(400,20));
            textfieldPrimaryCCSURL.setPreferredSize(new Dimension(400,20));
            
//            textfieldPrimaryCCSURL.addKeyListener(keyListener);

        }
        return textfieldPrimaryCCSURL ;
    }
    

    private Object getRemoteCCSPasswdTextfieldConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        //row & col at upper left of component
            c.gridx = 2;
            c.gridy = 2;
        //number of cols and rows the component occupies
            c.gridwidth = 1;
            c.gridheight = 1;
        //external padding (in pixels) added around the top, left, bottom, and right of the component
        //  (the default is "no inset")
            c.insets = new Insets(1, 1, 1, 1);
        //where to anchor the component if it is smaller than the display area
            c.anchor = GridBagConstraints.LINE_START; // CENTER is the default
        //specify relative weights of components
            c.weightx = 0.8 ;
            c.weighty = 0.0 ;
        return c;
    }

    private Object getRemoteCCSPasswdLabelConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        //row & col at upper left of component
            c.gridx = 1;
            c.gridy = 2;
        //number of cols and rows the component occupies
            c.gridwidth = 1;
            c.gridheight = 1;
        //how to fill when the component is smaller than the available display area
        // (options are NONE (the default), HORIZONTAL, VERTICAL, BOTH)
            c.fill = GridBagConstraints.BOTH;
        //external padding (in pixels) added around the top, left, bottom, and right of the component
        //  (the default is "no inset")
            c.insets = new Insets(1, 1, 1, 1);
        return c;
    }

    private Object getRemoteCCSLoginTextfieldConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        //row & col at upper left of component
            c.gridx = 2;
            c.gridy = 1;
        //number of cols and rows the component occupies
            c.gridwidth = 1;
            c.gridheight = 1;
        //external padding (in pixels) added around the top, left, bottom, and right of the component
        //  (the default is "no inset")
            c.insets = new Insets(1, 1, 1, 1);
        //where to anchor the component if it is smaller than the display area
            c.anchor = GridBagConstraints.LINE_START; // CENTER is the default
        //specify relative weights of components
            c.weightx = 0.8 ;
            c.weighty = 0.0 ;
        return c;
    }

    private Object getRemoteCCSLoginLabelConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        //row & col at upper left of component
            c.gridx = 1;
            c.gridy = 1;
        //number of cols and rows the component occupies
            c.gridwidth = 1;
            c.gridheight = 1;
        //how to fill when the component is smaller than the available display area
        // (options are NONE (the default), HORIZONTAL, VERTICAL, BOTH)
            c.fill = GridBagConstraints.BOTH;
        //external padding (in pixels) added around the top, left, bottom, and right of the component
        //  (the default is "no inset")
            c.insets = new Insets(1, 1, 1, 1);
        return c;
    }

    private Object getRemoteCCSURLTextfieldConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        //row & col at upper left of component
            c.gridx = 2;
            c.gridy = 0;
        //number of cols and rows the component occupies
            c.gridwidth = 1;
            c.gridheight = 1;
        //external padding (in pixels) added around the top, left, bottom, and right of the component
        //  (the default is "no inset")
            c.insets = new Insets(1, 1, 1, 1);
        //where to anchor the component if it is smaller than the display area
            c.anchor = GridBagConstraints.LINE_START; // CENTER is the default
        //specify relative weights of components
            c.weightx = 0.8 ;
            c.weighty = 0.0 ;
        return c;
    }

    private GridBagConstraints getRemoteCCSURLLabelConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        //row & col at upper left of component
            c.gridx = 1;
            c.gridy = 0;
        //number of cols and rows the component occupies
            c.gridwidth = 1;
            c.gridheight = 1;
        //how to fill when the component is smaller than the available display area
        // (options are NONE (the default), HORIZONTAL, VERTICAL, BOTH)
            c.fill = GridBagConstraints.BOTH;
        //external padding (in pixels) added around the top, left, bottom, and right of the component
        //  (the default is "no inset")
            c.insets = new Insets(1, 1, 1, 1);
            //specify relative weights of components
            c.weightx = 0.1 ;
            c.weighty = 0.0 ;
        return c;
    }

    private GridBagConstraints getShadowModeCheckboxConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        //row & col at upper left of component
            c.gridx = 0;
            c.gridy = 0;
        //number of cols and rows the component occupies
            c.gridwidth = 1;
            c.gridheight = 1;
        //how to fill when the component is smaller than the available display area
        // (options are NONE (the default), HORIZONTAL, VERTICAL, BOTH)
            c.fill = GridBagConstraints.BOTH;
        //external padding (in pixels) added around the top, left, bottom, and right of the component
        //  (the default is "no inset")
            c.insets = new Insets(1, 1, 1, 1);
//        //where to anchor the component if it is smaller than the display area
//            c.anchor = GridBagConstraints.LINE_START; // CENTER is the default
        //specify relative weights of components
            c.weightx = 0.4 ;
            c.weighty = 0.0 ;
            return c;
    }

    public JCheckBox getShadowModeCheckbox() {
        if(shadowModeCheckbox == null) {
            
            shadowModeCheckbox = new JCheckBox("Enable Shadow Mode", false);
            shadowModeCheckbox.setToolTipText("Shadow Mode allows PC2 to fetch submissions from a remote Contest Control System "
                    + "(called the 'Primary CCS')");
            shadowModeCheckbox.setMnemonic(KeyEvent.VK_S);

        }
        return shadowModeCheckbox;
        
    }

}
