package edu.csus.ecs.pc2.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.Insets;

public class InputValidatorSelectionPane extends JPanePlugin {

    private static final long serialVersionUID = 1L;
    
    private JPanel noInputValidatorPane;
    private JRadioButton noInputValidatorRadioButton;
    private JPanel vivaInputValidatorPane;
    private JRadioButton useVivaInputValidatorRadioButton;
    private JPanel vivaOptionsPane;
    private JLabel patternLabel;
    private JButton loadPatternButton;
    private JScrollPane patternTextScrollPane;
    private JTextArea textArea;
    private JPanel customInputValidatorPane;

    private JPanePlugin parentPane;

    private JRadioButton useCustomInputValidatorRadioButton;

    private DefineCustomInputValidatorPane customInputValidatorOptionsPane;

    private JPanel useVivaInputValidatorRadioButtonPanel;

    private JLabel lblWhatsThisViva;

    public InputValidatorSelectionPane() {
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.setBorder(new TitledBorder(null, "Select Input Validator", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        Component verticalStrut_0 = Box.createVerticalStrut(15);
        this.add(verticalStrut_0);
        this.add(getNoInputValidatorPane());
        Component verticalStrut = Box.createVerticalStrut(15);
        this.add(verticalStrut);
        this.add(getVivaInputValidatorPane());
        Component verticalStrut_1 = Box.createVerticalStrut(15);
        this.add(verticalStrut_1);
        this.add(getCustomInputValidatorPane());
        Component verticalStrut_2 = Box.createVerticalStrut(15);
        this.add(verticalStrut_2);
        
        ButtonGroup group = new ButtonGroup();
        group.add(getNoInputValidatorRadioButton());
        group.add(getUseVivaInputValidatorRadioButton());
        group.add(getUseCustomInputValidatorRadioButton());

    }
    
    private JPanel getNoInputValidatorPane() {
        if (noInputValidatorPane == null) {
            noInputValidatorPane = new JPanel();
            noInputValidatorPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            FlowLayout fl_noInputValidatorPane = (FlowLayout) noInputValidatorPane.getLayout();
            fl_noInputValidatorPane.setHgap(0);
            fl_noInputValidatorPane.setAlignment(FlowLayout.LEFT);
            noInputValidatorPane.add(getNoInputValidatorRadioButton());
        }
        return noInputValidatorPane;
    }
    private JRadioButton getNoInputValidatorRadioButton() {
        if (noInputValidatorRadioButton == null) {
            noInputValidatorRadioButton = new JRadioButton("Problem has no Input Validator");
            noInputValidatorRadioButton.setHorizontalAlignment(SwingConstants.LEFT);
            noInputValidatorRadioButton.setToolTipText("Choose this if the problem has no Input Validator");
            noInputValidatorRadioButton.setSelected(true);
            noInputValidatorRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (noInputValidatorRadioButton.isSelected()) {
                        if (parentPane!=null && parentPane instanceof InputValidatorPane) {
                            ((InputValidatorPane) parentPane).getRunInputValidatorButton().setEnabled(false);
                        }
                    }
                }
            });

        }
        return noInputValidatorRadioButton;
    }
    
    private JPanel getVivaInputValidatorPane() {
        if (vivaInputValidatorPane == null) {
            vivaInputValidatorPane = new JPanel();
            vivaInputValidatorPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            vivaInputValidatorPane.setLayout(new BoxLayout(vivaInputValidatorPane, BoxLayout.Y_AXIS));
            vivaInputValidatorPane.add(getUseVivaInputValidatorRadioButtonPanel());
            vivaInputValidatorPane.add(getVivaOptionsPane());
        }
        return vivaInputValidatorPane;
    }
                
    private JPanel getUseVivaInputValidatorRadioButtonPanel() {
        if (useVivaInputValidatorRadioButtonPanel == null) {
            useVivaInputValidatorRadioButtonPanel = new JPanel();
            FlowLayout flowLayout = (FlowLayout) useVivaInputValidatorRadioButtonPanel.getLayout();
            flowLayout.setHgap(2);
            flowLayout.setAlignment(FlowLayout.LEFT);
            useVivaInputValidatorRadioButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            useVivaInputValidatorRadioButtonPanel.add(getUseVivaInputValidatorRadioButton());
            useVivaInputValidatorRadioButtonPanel.add(getLblWhatsThisViva());
        }
        return useVivaInputValidatorRadioButtonPanel ;
    }
    
    public JRadioButton getUseVivaInputValidatorRadioButton() {
        if (useVivaInputValidatorRadioButton==null) {
            useVivaInputValidatorRadioButton = new JRadioButton("Use VIVA Input Validator");
            useVivaInputValidatorRadioButton.setMargin(new Insets(2, 0, 2, 2));
            useVivaInputValidatorRadioButton.setHorizontalAlignment(SwingConstants.LEFT);
            useVivaInputValidatorRadioButton.setToolTipText("Choose this to use the VIVA Input Validator");
            useVivaInputValidatorRadioButton.setSelected(false);
            
            useVivaInputValidatorRadioButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (useVivaInputValidatorRadioButton.isSelected()) {
                        if (parentPane!=null && parentPane instanceof InputValidatorPane) {
                            ((InputValidatorPane) parentPane).getRunInputValidatorButton().setEnabled(true);
                        }
                    }
                }
            });
        }
        return useVivaInputValidatorRadioButton ;
    }

    private JLabel getLblWhatsThisViva() {
        if (lblWhatsThisViva == null) {
            Icon questionIcon = UIManager.getIcon("OptionPane.questionIcon");
            if (questionIcon == null || !(questionIcon instanceof ImageIcon)) {
                // the current PLAF doesn't have an OptionPane.questionIcon that's an ImageIcon
                lblWhatsThisViva = new JLabel("<What's This?>");
                lblWhatsThisViva.setForeground(Color.blue);
            } else {
                Image image = ((ImageIcon) questionIcon).getImage();
                lblWhatsThisViva = new JLabel(new ImageIcon(getScaledImage(image, 20, 20)));
            }

            lblWhatsThisViva.setToolTipText("What's This? (click for additional information)");
            lblWhatsThisViva.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    JOptionPane.showMessageDialog(null, whatsThisVivaMessage, "VIVA Input Validator", JOptionPane.INFORMATION_MESSAGE, null);
                }
            });
            lblWhatsThisViva.setBorder(new EmptyBorder(0, 15, 0, 0));
        }
        return lblWhatsThisViva;
    }

    private String whatsThisVivaMessage = "Selecting this option allows you to use the \"VIVA\" Input Validator embedded within PC^2."

            + "\n\nVIVA is \"Vanb's Input Verification Assistant\", implemented by and provided courtesy of David \"vanb\" Van Brackle."

            + "\n\nVIVA supports a complex \"pattern recognition\" language, allowing the Contest Administrator to write a detailed specification"
            + "\nto which input test data files must conform."
            
            + "\n\nThe VIVA pattern language includes operations such as the ability to specify input data type requirements; range constraints "
            + "\nconsisting of expressions including logical, relational, and arithmetic operators; and data repetition patterns both within lines"
            + "\nand across different lines. The language also includes a variety of functions (such as \"length(string)\", \"distance(x1,y1,x2,y2)\","
            + "\n\"unique(x)\", and many others) which can be used to constrain input data."

            + "\n\nTo use VIVA, enter a valid VIVA \"pattern\" (or load one from a file), then click the \"Run Input Validator\" button"
            + "\nto verify that all data files currently loaded on the \"Test Data Files\" tab conform to the specified VIVA pattern."
            + "\n(The results of running VIVA against the Test Data Files will be displayed in the \"Input Validation Results\" pane, below)."

            + "\n\nFor more information on VIVA patterns, see the VIVA User's Guide under the PC^2 \"docs\" folder."
            + "\nFor additional information, or to download a copy of VIVA, see the VIVA website at http://viva.vanb.org/.";

    private JPanel getVivaOptionsPane() {
        if (vivaOptionsPane == null) {
            vivaOptionsPane = new JPanel();
            vivaOptionsPane.setAlignmentY(Component.TOP_ALIGNMENT);
            vivaOptionsPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            FlowLayout flowLayout = (FlowLayout) vivaOptionsPane.getLayout();
            flowLayout.setAlignment(FlowLayout.LEFT);
            vivaOptionsPane.add(Box.createHorizontalStrut(20));
            vivaOptionsPane.add(getPatternLabel());
            vivaOptionsPane.add(getVivaPatternTextScrollPane());
            vivaOptionsPane.add(getLoadPatternButton());
        }
        return vivaOptionsPane;
    }

    private JLabel getPatternLabel() {
        if (patternLabel == null) {
            patternLabel = new JLabel("Pattern:  ");
            patternLabel.setAlignmentY(Component.TOP_ALIGNMENT);
            patternLabel.setVerticalTextPosition(SwingConstants.TOP);
            patternLabel.setVerticalAlignment(SwingConstants.TOP);
        }
        return patternLabel;
    }
    private JButton getLoadPatternButton() {
        if (loadPatternButton == null) {
            loadPatternButton = new JButton("Load Pattern...");
        }
        return loadPatternButton;
    }
    private JScrollPane getVivaPatternTextScrollPane () {
        if (patternTextScrollPane==null) {
            patternTextScrollPane = new JScrollPane(getVivaPatternTextArea());
        }
        return patternTextScrollPane;
    }
    public JTextArea getVivaPatternTextArea() {
        if (textArea == null) {
            textArea = new JTextArea(5,50);
        }
        return textArea;
    }
    
    public JPanel getCustomInputValidatorPane() {
        if (customInputValidatorPane == null) {
            customInputValidatorPane = new JPanel();
            customInputValidatorPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            customInputValidatorPane.setLayout(new BoxLayout(customInputValidatorPane, BoxLayout.Y_AXIS));
            customInputValidatorPane.add(getUseCustomInputValidatorRadioButton());
            customInputValidatorPane.add(getCustomInputValidatorOptionsPane());
        }
        return customInputValidatorPane;
    }

    public JRadioButton getUseCustomInputValidatorRadioButton() {
        if (useCustomInputValidatorRadioButton == null) {
            useCustomInputValidatorRadioButton = new JRadioButton("Use Custom (User-supplied) Input Validator");
            useCustomInputValidatorRadioButton.setHorizontalAlignment(SwingConstants.LEFT);
            useCustomInputValidatorRadioButton.setToolTipText("Choose this to use a Custom Input Validator (i.e., a program that you supply).");
            useCustomInputValidatorRadioButton.setSelected(false);
            
            useCustomInputValidatorRadioButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (useCustomInputValidatorRadioButton.isSelected()) {
                        if (parentPane!=null && parentPane instanceof InputValidatorPane) {
                            ((InputValidatorPane) parentPane).getRunInputValidatorButton().setEnabled(true);
                        }
                    }
                }
            });
        }
        return useCustomInputValidatorRadioButton ;
    }

    public DefineCustomInputValidatorPane getCustomInputValidatorOptionsPane() {
        if (customInputValidatorOptionsPane==null) {
            customInputValidatorOptionsPane = new DefineCustomInputValidatorPane();
            customInputValidatorOptionsPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            customInputValidatorOptionsPane.setContestAndController(this.getContest(), this.getController());
            customInputValidatorOptionsPane.setParentPane(this);
        }
        return customInputValidatorOptionsPane;
    }


    @Override
    public String getPluginTitle() {
        return "Input Validator Selection Pane";
    }
    
    
    public void setParentPane(JPanePlugin parentPane) {
        this.parentPane = parentPane;
    }
    
    public JPanePlugin getParentPane() {
        return this.parentPane;
    }

    private Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

    //main() method for testing only
    public static void main (String [] args) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new InputValidatorSelectionPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
