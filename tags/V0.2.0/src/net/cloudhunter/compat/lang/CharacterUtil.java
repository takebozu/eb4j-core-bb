package net.cloudhunter.compat.lang;

public class CharacterUtil {
	/**
	 * Determines whether the specified character (Unicode code point) is in the supplementary character range. 
	 * 
	 * @param codePoint the character (Unicode code point) to be tested 
	 * @return true if the specified character is in the Unicode supplementary character range; false otherwise.
	 */
	public static boolean isSupplementaryCodePoint(int codePoint) {
		return codePoint >= 0x10000 && codePoint <= 0x10FFFF;
	}
	
	/**
	 * Determines the number of char values needed to represent the specified character (Unicode code point). 
	 * This method doesn't validate the specified character to be a valid Unicode code point. 
	 * The caller must validate the character value using isValidCodePoint  if necessary. 
	 * 
	 * @param codePoint the character (Unicode code point) to be tested. 
	 * @return 2 if the character is a valid supplementary character; 1 otherwise.
	 */
	public static int charCount(int codePoint) {
		if (codePoint >= 0x10000) {
			return 2;
		}
		return 1;
	}
	
	/**
	 * Determines if the specified character is white space according to Java. 
	 * 
	 * @param ch the character to be tested. 
	 * @return true if the character is a Java whitespace character; false otherwise.
	 */
	public static boolean isWhitespace(char ch) {
		return 
			(
				   ch == 12	//SPACE_SEPARATOR 
				|| ch == 13 //LINE_SEPARATOR
				|| ch == 14 //PARAGRAPH_SEPARATOR
			) && ch != '\u00A0' && ch != '\u2007' && ch != '\u202F' 
			|| ch == '\u0009'
			|| ch == '\n'	//LINE FEED
			|| ch == '\u000B'
			|| ch == '\u000C'			
			|| ch == '\r'	//CARRIAGE RETURN. 
			|| ch == '\u001C'
			|| ch == '\u001D'
			|| ch == '\u001E'
			|| ch == '\u001F';
	}
	
}
