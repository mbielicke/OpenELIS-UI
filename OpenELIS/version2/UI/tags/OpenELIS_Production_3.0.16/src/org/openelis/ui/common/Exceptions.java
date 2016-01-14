package org.openelis.ui.common;

import java.io.Serializable;
import java.util.ArrayList;

public class Exceptions implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	ArrayList<Exception> validateExceptions,userExceptions;

	public boolean hasExceptions() {
		return userExceptions != null || validateExceptions != null;
	}

	public void addException(Exception exception) {
		if (userExceptions == null)
			userExceptions = new ArrayList<Exception>();
		userExceptions.add(exception);
	}
	
	public void addValidateException(Exception exception) {
		if (validateExceptions == null)
			validateExceptions = new ArrayList<Exception>();
		validateExceptions.add(exception);
	}

	public ArrayList<Exception> getEndUserExceptions() {
		return userExceptions;
	}

	public ArrayList<Exception> getValidateExceptions() {
		return validateExceptions;
	}

	public void clearExceptions() {
		userExceptions = null;
		validateExceptions = null;
	}

	public void clearEndUserExceptions() {
		userExceptions = null;
	}

	public void clearValidateExceptions() {
		validateExceptions = null;
	}

}
