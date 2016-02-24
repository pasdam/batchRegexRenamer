package com.pasdam.regexren.gui;

import com.pasdam.regexren.model.FileModelItem;

/**
 * All rules must implements this interface
 * @author Paco
 * @version 1.0
 */
public interface Rule {

	/**
	 * This methid apply the rule to the filename
	 * @param fileRenamer - the FileRenamer containing file to transform
	 * @return the input fileRenamer with updated values, usefull when use more rules in pipe
	 */
	public FileModelItem apply(FileModelItem file);
}
