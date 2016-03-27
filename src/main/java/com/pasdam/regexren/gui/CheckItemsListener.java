package com.pasdam.regexren.gui;

/**
 * Interface implemented by components that support check actions on contained
 * elements
 * 
 * @author paco
 * @version 0.1
 */
interface CheckItemsListener {
	
	/**
	 * Indicates the target of the check action
	 */
	public enum Target {
		/** Indicates all elements of the list */
		ALL,
		/** Indicates only selected elements of the list */
		SELECTION,
		/** Indicates only unchecked elements of the list */
		INVERTED_SELECTION
	}

	/**
	 * Check elements specified
	 * 
	 * @param type
	 *            type of the elements to check/unchecked
	 *            (all/selected/unchecked).
	 * @param check
	 *            if true the targets will be checked, otherwise they will be
	 *            unchecked
	 */
	public void checkElements(Target type, boolean check);
}
