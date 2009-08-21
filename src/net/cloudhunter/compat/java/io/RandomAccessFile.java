package net.cloudhunter.compat.java.io;

import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

public class RandomAccessFile {
	private FileConnection _conn = null;
	private String _path = null;
	private DataInputStream _stream = null;
	
	public RandomAccessFile(File file, String mode) throws FileNotFoundException {
		try {
			_path = file.getPath();
			System.out.println("Opening RandomAccessFile:" + _path);
			_conn = (FileConnection)Connector.open(_path, Connector.READ);
			if(!_conn.exists()) {
				throw new FileNotFoundException(_path);
			}
			_stream = _conn.openDataInputStream(); 
		} catch(IOException e) {
			//TODO this works?
			throw new RuntimeException(e.getMessage());
		}
	}
     
     
     public long length() throws IOException {
    	 return _conn.fileSize();
     }
     
     public void seek(long pos) throws IOException {
    	 _stream.close();
    	 _stream = _conn.openDataInputStream();
    	 _stream.skip(pos);
     }
     
     
	public void close() throws IOException {
		System.out.println("Closing RandomAccessFile:" + _path);
		if(_stream != null) {
			_stream.close();
			_stream = null;
		}
		if(_conn != null) {
			_conn.close();
			_conn = null;
		}
	}
	
	public int read(byte[] b, int off, int len) throws IOException {
		return _stream.read(b, off, len);
	}
     
     public final void readFully(byte[] b, int off, int len) throws IOException {
    	 _stream.readFully(b, off, len);
     }
}
