package com.pasdam.regexren.gui.rules;

import com.pasdam.regexren.model.FileModelItem;

/**
 * All rules must implements this interface
 * 
 * @author paco
 * @version 0.1
 */
public interface Rule {

	/**
	 * This method apply the rule to the filename
	 * 
	 * @param file
	 *            - the {@link FileModelItem} containing file to rename
	 * @return the input file with updated values, useful when use more
	 *         rules in pipe
	 */
	public FileModelItem apply(FileModelItem file);
	
	/**	Reset the internal state of the rule */
	public void reset();
}
