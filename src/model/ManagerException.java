package model;

import java.io.IOException;

public class ManagerException extends RuntimeException {
	IOException e;

	public ManagerException(String message, IOException e) {
		super(message);
		this.e = e;
	}
}
