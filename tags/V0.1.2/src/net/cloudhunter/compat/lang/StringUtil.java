package net.cloudhunter.compat.lang;

public class StringUtil {
	/**
	 * Returns the character (Unicode code point) at the specified index. 
	 * The index refers to char values (Unicode code units) and ranges from 0 to length() - 1.
	 * If the char value specified at the given index is in the high-surrogate range, the following index is less than the length of this String, 
	 * and the char value at the following index is in the low-surrogate range, 
	 * then the supplementary code point corresponding to this surrogate pair is returned. 
	 * Otherwise, the char value at the given index is returned.
	 * 
	 * The set of characters from U+0000 to U+FFFF is sometimes referred to as the Basic Multilingual Plane (BMP). 
	 * Characters whose code points are greater than U+FFFF are called supplementary characters.
	 * 
	 * ＜補足＞
	 * サロゲートペアの文字コードについては、独自の変換規則で4バイトのデータへ変換する。
	 * その変換方法は、以下の手順に従います。
	 * 1. 文字コードから0x10000を引いて1番左の桁を"2"から"1"にする。これをXとする。
	 * 2. Xを0x400で割ってその商を0xD800に足す。これを「上位サロゲート」とする。
	 * 3. Xを0x400で割ってその剰余を0xDC00に足す。これを「下位サロゲート」とする。
	 * 4. 上位サロゲート、下位サロゲートの順番で出力する。
	 * 
	 * @param index the index to the char values 
	 * @return the code point value of the character at the index
	 */
	public static int codePointAt(String str, int index) {
		if (str.charAt(index) >= '\uD800' && str.charAt(index) <= '\uDBFF'
			&& str.charAt(index+1) >= '\uDC00' && str.charAt(index+1) < '\uDFFF' ) {
			int highSurrogate = str.charAt(index) - 0xD800;
			int lowSurrogate = str.charAt(index+1) - 0xDC00;
			return highSurrogate * 0x400 + lowSurrogate + 0x10000;
		}
		return str.charAt(index);
	}
}
