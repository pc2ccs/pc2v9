package edu.csus.ecs.pc2.ui.cellRenderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * A {@link TableCellRenderer} that renders its cell contents as a link which can be clicked on, for example to open
 * a file.
 * This class extends {@link DefaultTableCellRenderer}, which in turn extends {@link JLabel} --
 * meaning that this class supports the behavior of a JLabel, which is how it handles most of its
 * rendering attributes.
 * <p>
 * Note that this class only manages the <I>appearance</i> of the rendered cell.  If it is desired to 
 * have the cell actually "act like" a link -- that is, to invoke code when it is clicked on, then it
 * is the user's obligation to also add a MouseListener to the containing {@link JTable} to invoke the
 * desired operation, similar to:
 * <pre>
            myTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    JTable targetTable = (JTable) e.getSource();
                    int row = targetTable.getSelectedRow();
                    int column = targetTable.getSelectedColumn();
                    
                    if (column == targetTable.getColumn(columnOfInterest) {
                        invokeDesiredOperation(targetTable, row, column);
                    } 
                }
            });
 * </pre>
 * 
 * @author John@pc2.ecs.csus.edu
 *
 */
public class LinkCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    public void setValue(Object value) {
        setForeground(Color.BLUE);
        setText(((JLabel) value).getText());
        Font font = getFont();
        Map<TextAttribute, Object> map =
                new HashMap<TextAttribute, Object>(font.getAttributes());
        map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        setFont(font.deriveFont(map));
        setHorizontalAlignment(SwingConstants.CENTER);
    }

}
