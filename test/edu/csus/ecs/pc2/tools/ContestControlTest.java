// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.tools;


import java.util.Date;


import junit.framework.TestCase;

/**
 * Unit tests
 */
public class ContestControlTest extends TestCase {
	
	public void testcreateDateInFuture() throws Exception {
		
		String timeString = "55";
		Date d = new ContestControl().createDateInFuture(timeString);
		Date now = new Date();
		assertTrue(now.before(d));

		
		timeString = "55min";
		d = new ContestControl().createDateInFuture(timeString);
		assertTrue(now.before(d));

		
	}

}
