package com.pasdam.regexren.utils;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * {@link FileFilter} that accept only file with extension {@link BrrFileFilter#BRR_EXTENSION}
 * 
 * @author paco
 * @version 0.1
 */
public class BrrFileFilter extends FileFilter {
	
	/** Extension of BathcRegexRenamer script files */
	public static final String BRR_EXTENSION = "brr";

	@Override
	public boolean accept(File file) {
		return file.isDirectory() || file.getName().endsWith("." + BRR_EXTENSION);
	}

	@Override
	public String getDescription() {
		return "BatchRegexRenamer script (.brr)";
	}
}
