package edu.csus.ecs.pc2.ui;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * A JList full of check boxes.
 * 
 * Use Ctrl-A to select and deselect all checkboxes. <br>
 * Use Ctrl-I to invert the selection for all checkboxes.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 * 
 * NOTE: addListListener should not be used, gets called before the list is updated.
 */
public class JCheckBoxJList extends JList {

    /**
     * 
     */
    private static final long serialVersionUID = 6660805620712546373L;

    /**
     * 
     */
    public JCheckBoxJList() {
        super();
        constructorCommon();
    }

    /**
     * @param dataModel
     */
    public JCheckBoxJList(ListModel dataModel) {
        super(dataModel);
        constructorCommon();
    }

    /**
     * @param listData
     */
    public JCheckBoxJList(Object[] listData) {
        super(listData);
        constructorCommon();
    }

    /**
     * @param listData
     */
    public JCheckBoxJList(Vector<?> listData) {
        super(listData);
        constructorCommon();
    }

    private void constructorCommon() {
        CheckBoxListCellRenderer renderer = new CheckBoxListCellRenderer();

        setCellRenderer(renderer);

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        CheckBoxListener lst = new CheckBoxListener(this);

        addMouseListener(lst);

        addKeyListener(lst);

    }

    /**
     * 
     */
    class CheckBoxListener implements MouseListener, KeyListener {

        private JCheckBoxJList jCheckBoxJList;

        public CheckBoxListener(JCheckBoxJList list) {
            jCheckBoxJList = list;
        }

        public void mouseClicked(MouseEvent e) {
            processAction();
        }

        public void mousePressed(MouseEvent e) {
            // ignore

        }

        public void mouseReleased(MouseEvent e) {
            // ignore

        }

        public void mouseEntered(MouseEvent e) {
            // ignore

        }

        public void mouseExited(MouseEvent e) {
            // ignore

        }

        public void keyPressed(KeyEvent e) {
            if (e.getKeyChar() == ' ') {
                processAction();
            } else if ((e.getKeyCode() == 65) && e.isControlDown()) {
                // Ctrl-A select or deselect
                selectDeselectAll();
            } else if ((e.getKeyCode() == 73) && e.isControlDown()) {
                // Ctrl-I invert selection
                invertSelection();
            }
        }

        /**
         * Select all or deselect all checkboxes.
         * 
         * Sets all checkboxes to the opposite (logical not) of the
         * first checkbox in the list. 
         */
        private void selectDeselectAll() {

            if (getModel().getSize() < 1) {
                // Nothing to select or deselect
                return;
            }

            JCheckBox checkBox = (JCheckBox) jCheckBoxJList.getModel().getElementAt(0);
            boolean opposite = !checkBox.isSelected();

            for (int i = 0; i < getModel().getSize(); i++) {
                checkBox = (JCheckBox) jCheckBoxJList.getModel().getElementAt(i);
                checkBox.setSelected(opposite);
            }
            repaint();
            jCheckBoxJList.firePropertyChange("change", false, true);
        }
        
        /**
         * Invert the selection for each element in checklist.
         *
         */
        private void invertSelection() {

            if (getModel().getSize() < 1) {
                // Nothing to invert
                return;
            }

            JCheckBox checkBox = null;
            for (int i = 0; i < getModel().getSize(); i++) {
                checkBox = (JCheckBox) jCheckBoxJList.getModel().getElementAt(i);
                checkBox.setSelected(! checkBox.isSelected());
            }
            repaint();
            jCheckBoxJList.firePropertyChange("change", false, true);
        }


        public void keyReleased(KeyEvent e) {
            // ignore
        }

        public void keyTyped(KeyEvent e) {
            // ignore

        }

        void processAction() {
            int index = getSelectedIndex();

            if (index < 0) {
                return;
            }
            
            JCheckBox checkBox = (JCheckBox) jCheckBoxJList.getModel().getElementAt(index);
            checkBox.setSelected(!checkBox.isSelected());

            repaint();
            jCheckBoxJList.firePropertyChange("change", false, true);
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.JList#setSelectedIndices(int[])
     */
    @Override
    public void setSelectedIndices(int[] indices) {
        for (int i = 0; i < indices.length; i++) {
            JCheckBox checkBox = (JCheckBox)getModel().getElementAt(indices[i]);
            checkBox.setSelected(true);
        }
//        super.setSelectedIndices(indices);
    }

    /* (non-Javadoc)
     * @see javax.swing.JList#getSelectedIndices()
     */
    @Override
    public int[] getSelectedIndices() {
        HashSet<Integer> selectedIndicesSet = getSelectedIndicesSet();
        int[] indices = new int[selectedIndicesSet.size()];
        Integer[] intArray = selectedIndicesSet.toArray(new Integer[selectedIndicesSet.size()]);
        Arrays.sort(intArray);
        for (int i = 0; i < intArray.length; i++) {
            indices[i] = intArray[i].intValue();
        }
        return indices;
    }

    /* (non-Javadoc)
     * @see javax.swing.JList#getSelectedValues()
     */
    @Override
    public Object[] getSelectedValues() {
        HashSet<Integer> selectedIndicesSet = getSelectedIndicesSet();
        Object[] values = new Object[selectedIndicesSet.size()];
        Integer[] intArray = selectedIndicesSet.toArray(new Integer[selectedIndicesSet.size()]);
        Arrays.sort(intArray);
        for (int i = 0; i < intArray.length; i++) {
            values[i] = getModel().getElementAt(intArray[i].intValue());
        }
        return values;
    }

    /**
     * @return Returns the selectedIndicesSet.
     */
    public HashSet<Integer> getSelectedIndicesSet() {
        HashSet<Integer> selectedIndicesSet = new HashSet<Integer>();
        for(int i = 0; i < getModel().getSize(); i++) {
            JCheckBox checkBox = (JCheckBox)getModel().getElementAt(i);
            if (checkBox.isSelected()) {
                selectedIndicesSet.add(Integer.valueOf(i));
            }
        }
        return selectedIndicesSet;
    }

}

/**
 * Special Renderer.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
class CheckBoxListCellRenderer extends JCheckBox implements ListCellRenderer {

    /**
     * 
     */
    private static final long serialVersionUID = -6394690201864252212L;

    protected static final Border NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JCheckBox data = (JCheckBox) value;

        setText(data.getText());

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        setSelected(data.isSelected());

        setFont(list.getFont());

        if (cellHasFocus) {
            setBorder(UIManager.getBorder("List.focusCellHighlightBorder"));
        } else {
            setBorder(NO_FOCUS_BORDER);
        }
        return this;
    }

}
