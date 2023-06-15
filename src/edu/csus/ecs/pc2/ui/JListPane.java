// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * A generic list pane
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class JListPane extends JPanePlugin {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1383088589409036086L;

	@SuppressWarnings("rawtypes")
	JList theList = new JList();

	private JFrame parentFrame;

	private int[] holdSelectedIndexes = new int[0];

	private ISelectedListsSetter selectedListsSetter;

	public JListPane() {
		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane);

		scrollPane.setViewportView(theList);

		JPanel buttonPane = new JPanel();
		FlowLayout flowLayout = (FlowLayout) buttonPane.getLayout();
		flowLayout.setHgap(45);
		add(buttonPane, BorderLayout.SOUTH);

		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAndClose();
			}
		});
		buttonPane.add(saveButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeTheWindow();
			}
		});
		buttonPane.add(cancelButton);
	}

	/**
	 * Create a pane that has a list of objects.
	 * 
	 * 
	 * @param parentFrame          parent frame for JPanel, will close parent frame
	 * @param items                list of items to display, must be at least one
	 *                             item
	 * @param selectedItemsIndexes selected items indexces
	 * @param selectedListsSetter  must be not null, invoked if user saves
	 *                             selections/choices
	 */
	@SuppressWarnings("unchecked")
	public JListPane(JFrame parentFrame, Object [] items, int[] selectedItemsIndexes, ISelectedListsSetter selectedListsSetter) {
		this();
		
		if (selectedListsSetter == null) {
			throw new IllegalArgumentException("selectedListsSetter is null");
		}
		if (parentFrame == null) {
			throw new IllegalArgumentException("parentFrame is null");
		}

		if (items == null) {
			throw new IllegalArgumentException("items is null");
		}

		if (items.length == 0) {
			throw new IllegalArgumentException("No items in list");
		}

		this.parentFrame = parentFrame;
		theList.removeAll();

		theList.setListData(items);

		this.selectedListsSetter = selectedListsSetter;

		if (selectedItemsIndexes != null) {
			holdSelectedIndexes = selectedItemsIndexes;
			theList.setSelectedIndices(selectedItemsIndexes);
		}
	}

	@SuppressWarnings("unchecked")
	protected void closeTheWindow() {

		if (isChanged()) {

			int result = FrameUtilities.yesNoCancelDialog(parentFrame, "Selection changed, save selection?",
					"Save selection?");
			if (result == JOptionPane.YES_OPTION) {
				selectedListsSetter.setSelectedValuesList(theList.getSelectedValuesList(), theList.getSelectedIndices());
			}
		}

		parentFrame.setVisible(false);
		parentFrame.dispose();
	}

	private boolean isChanged() {
		int numSelected = theList.getModel().getSize();
		return numSelected != holdSelectedIndexes.length;
	}

	@SuppressWarnings("unchecked")
	protected void saveAndClose() {

		selectedListsSetter.setSelectedValuesList(theList.getSelectedValuesList(), theList.getSelectedIndices());
		
		parentFrame.setVisible(false);
		parentFrame.dispose();
	}

	@Override
	public String getPluginTitle() {
		return "generic JList picker";
	}

	public int[] getSelectedItemIndex() {
		return theList.getSelectedIndices();
	}

}
