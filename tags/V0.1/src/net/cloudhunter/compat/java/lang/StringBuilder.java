package net.cloudhunter.compat.java.lang;

public class StringBuilder {
	private StringBuffer _buffer = null;
	
	public StringBuilder() {
		_buffer = new StringBuffer();
	}
	
	public StringBuilder(int capacity) {
		_buffer = new StringBuffer(capacity);
	}
	
	public StringBuilder(String str) {
		_buffer = new StringBuffer(str);
	}
	
	public StringBuilder append(char c) {
		_buffer.append(c);
		return this;
	}
	
	public StringBuilder append(String str) {
		_buffer.append(str);
		return this;
	}
	
	public StringBuilder appendCodePoint(int codePoint) {
		if(codePoint <= 0xFFFF) {
			_buffer.append((char)codePoint);
		} else{
			int highSurrogate = (int)Math.floor((codePoint - 0x10000) / 0x400) + 0xD800;
			int lowSurrogate = (int)Math.floor((codePoint - 0x10000) % 0x400) + 0xDC00;
			_buffer.append((char)highSurrogate).append((char)lowSurrogate);
		}
		return this;
	}
	
	public int length() {
		return _buffer.length();
	}
	
	public char charAt(int index) {
		return _buffer.charAt(index);
	}
	
	public StringBuilder delete(int start, int end) {
		_buffer.delete(start, end);
		return this;
	}
	
	public StringBuilder deleteCharAt(int index) {
		_buffer.deleteCharAt(index);
		return this;
	}
	
	public StringBuilder insert(int offset, char c) {
		_buffer.insert(offset, c);
		return this;
	}
	
	public String toString() {
		return _buffer.toString();
	}
}
