package net.cloudhunter.compat.java.io;

import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

public class RandomAccessFile {
	private FileConnection _conn = null;
	private String _path = null;
	private DataInputStream _stream = null;

	/** 開いている_streamの現在位置 */
	private long currentPos;
	
	public RandomAccessFile(File file, String mode) throws FileNotFoundException {
		try {
			_path = file.getPath();
			//System.out.println("Opening RandomAccessFile:" + _path);
			_conn = (FileConnection)Connector.open(_path, Connector.READ);
			if(!_conn.exists()) {
				throw new FileNotFoundException(_path);
			}
			_stream = _conn.openDataInputStream();
			currentPos = 0;
			if(_stream.markSupported()) {
				_stream.mark(Integer.MAX_VALUE);	// 2,147,483,647 (BlackBerry Bold)  
			}
		} catch(IOException e) {
			//TODO this works?
			throw new RuntimeException(e.getMessage());
		}
	}
     
     public long length() throws IOException {
    	 return _conn.fileSize();
     }
     
     public void seek(long pos) throws IOException {
    	 if(currentPos < pos) {
    		 _stream.skip(pos - currentPos);
    	 } else if (currentPos > pos) {
    		 if(_stream.markSupported() && pos < Integer.MAX_VALUE) {
    			 _stream.reset();
    		 } else {
    			 _stream.close();
    			 _stream = _conn.openDataInputStream();
    		 }
			 _stream.skip(pos);
    	 }
		 currentPos = pos;
     }
     
     
	public void close() throws IOException {
		//System.out.println("Closing RandomAccessFile:" + _path);
		currentPos = 0;
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
		int size = _stream.read(b, off, len);
		currentPos += size;
		return size;
	}
     
     public final void readFully(byte[] b, int off, int len) throws IOException {
    	 _stream.readFully(b, off, len);
    	 currentPos += len;
     }
}
