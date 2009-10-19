package net.cloudhunter.compat.java.io;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.cloudhunter.compat.java.util.ArrayList;
import net.cloudhunter.compat.java.util.List;


public class File {
	private String _pathname = null;
//	private File _parent = null;
//	private String _child = null;
	
	public static final char PATH_SEPARATOR = '/';
	
	public File(String pathname) {
		_pathname = urlEncode(pathname);
	}
	
	public File(File parent, String child) {
		_pathname = parent.getPath() + PATH_SEPARATOR + urlEncode(child);
	}
	
	/**
	 * FileConnectionを取得する。
	 * @return
	 * @throws IOException
	 */
	private FileConnection getFileConnection() throws IOException {
		return (FileConnection)Connector.open(_pathname);
	}
	
	public boolean canRead() {
		boolean result = false;
		FileConnection conn = null;
		try {
			conn = getFileConnection();
			result = conn.canRead();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null) {
					conn.close();
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public String[] list() {
		List results = new ArrayList();
		FileConnection conn = null;
		try {
			conn = getFileConnection();
			Enumeration list = conn.list();
			while(list.hasMoreElements()) {
				String file = (String)list.nextElement();
				if (file.endsWith("/")) {
					//ディレクトリの場合は互換性のため最後の/を取る
					results.add(file.substring(0, file.length() - 1));
				} else {
					results.add(file);
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null) {
					conn.close();
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		return (String[])results.toArray(new String[results.size()]);
	}
	
	public File[] listFiles() {
		String[] list = list();
		File[] files = new File[list.length];
		for(int i=0; i<list.length; i++) {
			files[i] = new File(this, list[i]);
		}
		return files;
	}
	
	public boolean isDirectory() {
		boolean result = false;
		FileConnection conn = null;
		try {
			conn = getFileConnection();
			result = conn.isDirectory();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null) {
					conn.close();
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public String getName() {
		return _pathname.substring(_pathname.lastIndexOf(PATH_SEPARATOR));
	}
	
	public String getPath() {
		return _pathname;
	}
	
	/**
	 * encode URL
	 * @param value url
	 * @return encoded url
	 */
	private static String urlEncode(String value) {
		StringBuffer buf = new StringBuffer();
		byte[] utf;
		try {
			utf = value.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			utf = value.getBytes(); //基本的にはここには来ない
		}
		for (int i = 0; i < utf.length; ++i) {
			char b = (char) utf[i];
//			if (b == ' ') {
//				buf.append('+');
//			} else
			if (isRFC3986Unreserved(b)) {
				buf.append(b);
			} else {
				buf.append('%');
				buf.append(Integer.toHexString(b & 0xff).toUpperCase()); // u.c. per RFC 3986
			}
		}
		return buf.toString();
	}

	/**
	 * エンコードすべき文字かどうかを判定
	 * @param b
	 * @return エンコードすべき文字の場合true
	 */
	private static boolean isRFC3986Unreserved(char b) {
		return (b >= 'A' && b <= 'Z')
			|| (b >= 'a' && b <= 'z')
			|| Character.isDigit(b)
			|| ".-~_/:%".indexOf(b) >= 0;
	}
}
