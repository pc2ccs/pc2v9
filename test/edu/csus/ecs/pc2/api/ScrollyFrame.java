package edu.csus.ecs.pc2.api;

import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.FrameUtilities.HorizontalPosition;
import edu.csus.ecs.pc2.ui.FrameUtilities.VerticalPosition;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;

/**
 * Frame with JList.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ScrollyFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private JScrollPane scrollPane = null;

    private JList listOfStuff = null;
    
    private DefaultListModel defaultListModel = new DefaultListModel();

    private JPanel mainPanel = null;

    private JPanel buttonPane = null;

    private JButton clearButton = null;


    /**
     * This method initializes
     * 
     */
    public ScrollyFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(481,291));
        this.setContentPane(getMainPanel());
        this.setTitle("List");
        setHeightScreenLength (120);
        FrameUtilities.setFramePosition(this, HorizontalPosition.RIGHT, VerticalPosition.CENTER);

    }

    /**
     * 
     * @param pixelBuffer number of pixels offset from top and bottom
     */
    private void setHeightScreenLength(int pixelBuffer) {
        Dimension screenDim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameDim = getSize();
        frameDim.height = screenDim.height - (pixelBuffer/2);
        setSize(frameDim);
    }

    /**
     * This method initializes scrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView(getListOfStuff());
        }
        return scrollPane;
    }

    /**
     * This method initializes listOfStuff
     * 
     * @return javax.swing.JList
     */
    private JList getListOfStuff() {
        if (listOfStuff == null) {
            listOfStuff = new JList(defaultListModel);
            listOfStuff.setFont(new java.awt.Font("Courier New", java.awt.Font.BOLD, 12));
        }
        return listOfStuff;
    }
    
    public void addLine (final String line){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                defaultListModel.addElement(line);
//                huh;
            }
        });
    }
    
    public void removeAll(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                defaultListModel.removeAllElements();
            }
        });
    }

    /**
     * This method initializes mainPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(getScrollPane(), java.awt.BorderLayout.CENTER);
            mainPanel.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        }
        return mainPanel;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            buttonPane = new JPanel();
            buttonPane.setPreferredSize(new java.awt.Dimension(35,35));
            buttonPane.add(getClearButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes clearButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getClearButton() {
        if (clearButton == null) {
            clearButton = new JButton();
            clearButton.setText("Clear");
            clearButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
            clearButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    removeAll();
                }
            });
        }
        return clearButton;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        ScrollyFrame scrollyFrame = new ScrollyFrame();
        scrollyFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        scrollyFrame.setVisible(true);
        for (int i = 0; i < 34; i++) {
            scrollyFrame.addLine("Line "+i);
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
