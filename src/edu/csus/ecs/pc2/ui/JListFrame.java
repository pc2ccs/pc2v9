// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

/**
 * Generic List frame.
 * 
 * @see JListPane
 * 
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class JListFrame extends JFrame {
	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = -5073426131461967909L;

	private JListPane listPane;

	public JListFrame(String title, Object [] items, int[] selecteditems, ISelectedListsSetter selectedListsSetter) throws HeadlessException {
		super();
		setMinimumSize(new Dimension(400, 400));
		setName("JListFrame");
		setTitle(title);
		setPreferredSize(new Dimension(400, 400));
		
		listPane = new JListPane(this, items, selecteditems, selectedListsSetter);
		getContentPane().add(listPane, BorderLayout.CENTER);
		FrameUtilities.centerFrame(this);
	}
	
	/**
	 * Used for unit testing.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
	    
	    
		Object[] items2 = { "One", "Two", "Three", "Four" };

		JListFrame f = new JListFrame("hi", items2, new int[0], new ISelectedListsSetter() {

			@Override
			public void setSelectedValuesList(List<Object> selectedValuesList, int[] selectedIndices) {
				for (Object object : selectedValuesList) {
					System.out.println("debug 22 values " + object.toString());
				}
				System.out.println("debug 22 ind " + Arrays.toString(selectedIndices));
			}
		});
		f.setVisible(true);
	}



}
