package edu.csus.ecs.pc2.convert;

import junit.framework.TestCase;

public class EventFeedUtilitiesTest extends TestCase
{
	
	  public void testtoMS() throws Exception {
	        
	        String [] data = //
	            {
	                "0.0;0", //
	                "1.0;1000", //
	                "10.0;10000", //
	                "100.00;100000", //
	                "3434123423;3434123423000", //
	                "123.4;123400", //
	                "123.45;123450", //
	                "123.456;123456", //
	                "123.456789;123456", //
	                
	            };
	        
	        for (String datum : data) {
	            
	            String[] parts = datum.split(";");
	            
	            String input = parts[0];
	            String msString = parts[1];
	            long expected = Long.parseLong(msString);
	            
	            long actual = EventFeedUtilities.toMS(input);
	            
//	            System.out.println(input +" actual = " +actual);
	            
	            assertEquals("Expected from input: "+input,expected,actual);
	            
	            
	        }
	        
	    }

}
