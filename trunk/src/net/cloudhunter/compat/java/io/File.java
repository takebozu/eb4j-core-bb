package net.cloudhunter.compat.java.io;

import java.io.IOException;
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
		_pathname = pathname;
	}
	
	public File(File parent, String child) {
		_pathname = parent.getPath() + PATH_SEPARATOR + child;
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
}
