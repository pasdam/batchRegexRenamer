package com.pasdam.regexren.gui.rules;

import java.util.List;

/**
 * Exception thrown when one or more parameters of a rule's factory are invalid
 * 
 * @author paco
 * @version 0.1
 */
public class InvalidParametersException extends RuntimeException {

	private static final long serialVersionUID = -3162515907618032867L;

	/** List of invalid prameter's IDs */
	private final List<Integer> invalidParameterIds;
	
	/**
	 * Create an exception with the specified list of invalid parameters and
	 * message
	 * 
	 * @param invalidParameters
	 *            list of IDs of invalid parameters
	 * @param message
	 *            the detail message
	 */
	public InvalidParametersException(List<Integer> invalidParameters, String message) {
		super(message);
		this.invalidParameterIds = invalidParameters;
	}
	
	/**
	 * Returns the list of invalid parameters
	 * 
	 * @return the list of invalid parameters
	 */
	public List<Integer> getInvalidParameter() {
		return this.invalidParameterIds;
	}
}
