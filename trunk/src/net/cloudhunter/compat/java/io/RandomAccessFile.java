package net.cloudhunter.compat.java.io;

import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.cloudhunter.bb.EBLogger;
import net.cloudhunter.bb.URLUTF8Encoder;
import net.rim.device.api.system.EventLogger;

public class RandomAccessFile {
	private FileConnection _conn = null;
	private String _path = null;
	private DataInputStream _stream = null;

	/** 開いている_streamの現在位置 */
	private long currentPos;
	
	public RandomAccessFile(File file, String mode) throws FileNotFoundException {
		try {
			_path = file.getPath();
			EBLogger.log("Opening RandomAccessFile:" + URLUTF8Encoder.encode(_path), EventLogger.DEBUG_INFO);
			_conn = (FileConnection)Connector.open(URLUTF8Encoder.encode(_path), Connector.READ);
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
    		 EBLogger.log("[S]RAF skip1:pos=" + pos + ",currentPos=" + currentPos);
    		 //TODO 差が大きいとパフォーマンスが著しく悪い
    		 _stream.skip(pos - currentPos);
    		 EBLogger.log("[E]RAF skip1");
    	 } else if (currentPos > pos) {
    		 if(_stream.markSupported() && pos < Integer.MAX_VALUE) {
    			 EBLogger.log("[S]RAF reset:pos=" + pos + ",currentPos=" + currentPos);
    			 _stream.reset();
    			 EBLogger.log("[E]RAF reset");
    		 } else {
    			 EBLogger.log("[S]RAF close&open:pos=" + pos + ",currentPos=" + currentPos);
    			 _stream.close();
    			 _stream = _conn.openDataInputStream();
    			 EBLogger.log("[E]RAF close&open");
    		 }
    		 EBLogger.log("[S]RAF skip2");
			 _stream.skip(pos);
			 EBLogger.log("[E]RAF skip2");
    	 }
		 currentPos = pos;
     }
     
     
	public void close() throws IOException {
		EBLogger.log("Closing RandomAccessFile:" + URLUTF8Encoder.encode(_path), EventLogger.DEBUG_INFO);
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
