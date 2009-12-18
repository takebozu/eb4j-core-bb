package net.cloudhunter.compat.java.util.zip;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.rim.device.api.compress.ZLibInputStream;

public class Inflater {
	private InputStream istream = null;
	
	public void setInput(byte[] b, int off, int len) {
		istream = new ZLibInputStream(new ByteArrayInputStream(b, off, len));
	}
	
	public int inflate(byte[] b, int off, int len) throws DataFormatException {
		try {
			return istream.read(b, off, len);
		} catch(IOException e) {
			return 0;
		}
	}
	
	public void end() {
		try {
			if(istream != null) {
				istream.close();
			}
		} catch (IOException e) {
			//do nothing
		}
		istream = null;
	}
}
