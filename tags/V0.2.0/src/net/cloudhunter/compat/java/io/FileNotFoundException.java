package net.cloudhunter.compat.java.io;

import java.io.IOException;

public class FileNotFoundException extends IOException {
	public FileNotFoundException() {
		super();
	}
	public FileNotFoundException(String message) {
		super(message);
	}

}
