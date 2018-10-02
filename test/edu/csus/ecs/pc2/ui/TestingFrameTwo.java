package edu.csus.ecs.pc2.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 * Testing enable/disable Checkboxes.
 * 
 * b1174.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class TestingFrameTwo extends JPanePlugin {
    WrapperJCheckBox chckbxNewCheckBox = new WrapperJCheckBox("New check box");

    JCheckBoxJList list = new JCheckBoxJList();

    public TestingFrameTwo() {
        setLayout(new BorderLayout(0, 0));

        add(list);

        add(panel, BorderLayout.SOUTH);
        populate(chckbxNewCheckBox);
        panel.add(chckbxNewCheckBox);

        JButton btnToggle = new JButton("Toggle");
        panel.add(btnToggle);

        add(panel_2, BorderLayout.WEST);

        add(panel_3, BorderLayout.EAST);

        add(panel_1, BorderLayout.NORTH);
        btnToggle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                toggleEnabled(chckbxNewCheckBox);
            }
        });
    }

    private JCheckBoxJList createList() {

        String[] list = {
                "One",
                "Two",
                "Three",
                "Four",
        };

        return new JCheckBoxJList(list);
    }

    private void populate(WrapperJCheckBox wrapperJCheckBox) {

    }

    protected void toggleEnabled(JCheckBox checkBox) {

        checkBox.setEnabled(!checkBox.isEnabled());

    }

    /**
     * 
     */
    private static final long serialVersionUID = 1680709710892814751L;

    private final JPanel panel = new JPanel();

    private final JPanel panel_2 = new JPanel();

    private final JPanel panel_3 = new JPanel();

    private final JPanel panel_1 = new JPanel();

    @Override
    public String getPluginTitle() {
        // TODO Auto-generated method stub
        return null;
    }

}
