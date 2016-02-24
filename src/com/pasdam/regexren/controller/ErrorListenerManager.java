package com.pasdam.regexren.controller;

/**
 * Abstract manager that support error notification
 * 
 * @author paco
 * @version 0.1
 */
public abstract class ErrorListenerManager {

	/** Component to notify on error */
	private ErrorListener errorListener;
	
	/**
	 * Set the listener to notify in case of errors
	 * 
	 * @param errorListener
	 *            listener to add
	 */
	public void setErrorListener(ErrorListener errorListener) {
		this.errorListener = errorListener;
	}
	
	/**
	 * Notify the listener that an error occurred
	 * @param error error message
	 */
	protected void notifyError(String error) {
		if (this.errorListener != null) {
			this.errorListener.errorOccurred(error);
		}
	}
}
