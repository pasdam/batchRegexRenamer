package com.pasdam.regexren.controller;

/**
 * Interface implemented by those components that need to be notified in case of error 
 * 
 * @author paco
 * @version 0.1
 */
public interface ErrorListener {

	/**
	 * Invoked to notify an error occurred
	 * @param message localized error message
	 */
	public void errorOccurred(String message);
}
