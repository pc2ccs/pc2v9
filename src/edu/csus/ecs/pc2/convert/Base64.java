package edu.csus.ecs.pc2.convert;

public class Base64 {
	private final static char base64Array[] = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
			'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', '+', '/' };

	public static String encode(String string) {
		byte bytes[] = string.getBytes();	
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
			byte c1 = (byte) (b1 >> 2);
			byte c2 = (byte) (((b1 & 0x3) << 4) | (b2 >> 4));
			byte c3 = (byte) (((b2 & 0xf) << 2) | (b3 >> 6));
			byte c4 = (byte) (b3 & 0x3f);
			encodedString += base64Array[c1];
			encodedString += base64Array[c2];
			switch (pad) {
				case 0:
					encodedString += base64Array[c3];
					encodedString += base64Array[c4];
					break;
				case 1:
					encodedString += base64Array[c3];
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