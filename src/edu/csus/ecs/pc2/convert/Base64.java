package edu.csus.ecs.pc2.convert;

public class Base64 {
	private static final char[] BASE64ARRAY = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
			'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', '+', '/' };

	public static String encode(String string) {
		byte[] bytes = string.getBytes();	
		return encode (bytes);
	}
	
	public static String encode (byte [] bytes) {
	    
		String encodedString = "";

		int i = 0;
		int pad = 0;
		while (i < bytes.length) {
			byte b1 = bytes[i++];
			byte b2;
			byte b3;
			if (i >= bytes.length) {
				b2 = 0;
				b3 = 0;
				pad = 2;
			} else {
				b2 = bytes[i++];
				if (i >= bytes.length) {
					b3 = 0;
					pad = 1;
				} else
					b3 = bytes[i++];
			}
			

			//the following section has updates which are necessary because the original version of this class was designed
			// only to encode objects of type String, wherein every byte has a positive value.
			//Expanding the code to support Base64 encoding of arbitrary byte arrays requires dealing with Java's sign-extension
			// when using the >> (right-bit-shift) operator
//			byte c1 = (byte) (b1 >> 2);                //original code; fails to deal with sign extension
			byte c1 = (byte) ((b1 & 0x00FC) >> 2);     //&0xFC masks off upper sign bits of the 16-bit byte storage 
			
//			byte c2 = (byte) (((b1 & 0x3) << 4) | (b2 >> 4));
			byte c2 = (byte) (((b1 & 0x3) << 4) | ((b2 & 0x00FF) >> 4));
			
//			byte c3 = (byte) (((b2 & 0xf) << 2) | (b3 >> 6));
			byte c3 = (byte) (((b2 & 0xf) << 2) | ((b3 & 0x00FF) >> 6));
			
			byte c4 = (byte) (b3 & 0x3f);
			
			encodedString += BASE64ARRAY[c1];
			encodedString += BASE64ARRAY[c2];
			switch (pad) {
				case 0:
					encodedString += BASE64ARRAY[c3];
					encodedString += BASE64ARRAY[c4];
					break;
				case 1:
					encodedString += BASE64ARRAY[c3];
					encodedString += "=";
					break;
				case 2:
					encodedString += "==";
					break;
			}
		}
		return encodedString;
	}
}