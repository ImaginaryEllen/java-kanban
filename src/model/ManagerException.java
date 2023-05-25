package model;

import java.io.IOException;

public class ManagerException extends RuntimeException {

	public ManagerException(String message) {
		super(message);
	}

	public ManagerException(String message, IOException e) {
		super(message, e);
	}

	public ManagerException(String message, InterruptedException e) {
		super(message, e);
	}
}
