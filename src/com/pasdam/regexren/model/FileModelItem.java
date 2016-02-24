package com.pasdam.regexren.model;

import java.io.File;

import com.pasdam.utils.file.FileInfo;

/**
 * This class represent a single item in the files model 
 * 
 * @author paco
 * @version 0.1
 */
public class FileModelItem {
	
	/** Indicates whether the item is checked or not, i.e. if it should be renamed */
	private boolean checked = true;
	
	/**
	 * It stores the current file's name, as result of the renaming rules, used
	 * in the rename operations
	 */
	private String name;

	/**
	 * It stores the current file's extension, as result of the renaming rules,
	 * used in the rename operations
	 */
	private String extension;
	
	/** It store the current file */
	private final File file;

	/**
	 * Create an item for the specified file
	 * 
	 * @param file
	 *            file to store
	 * @throws NullPointerException
	 *             if the parameter is null
	 */
	public FileModelItem(File file) throws NullPointerException {
		this.file = file;
		reset();
	}

	/** Restore initial file's info */
	public void reset() {
		// parse file's info
		FileInfo info = FileInfo.parseFileInfo(file);
		this.name = info.getName();
		this.extension = info.getExtension();
	}
	
	/**
	 * Sets whether the file should be renamed or not
	 * 
	 * @param checked
	 *            true if the file should be renamed, false otherwise
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	/**
	 * Returns true if the file should be renamed, false otherwise
	 * 
	 * @return true if the file should be renamed, false otherwise
	 */
	public boolean isChecked() {
		return this.checked;
	}
	
	/**
	 * Set the new name of the file
	 * 
	 * @param name
	 *            new name of the file to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the new name of the file, after the apply of the renaming rules
	 * 
	 * @return the new name of the file, after the apply of the renaming rules
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Sets the new extension of the file, after the apply of the renaming rules
	 * 
	 * @param extension
	 *            new extension of the file to set
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	/**
	 * Returns the new extension of the file, after the apply of the renaming
	 * rules
	 * 
	 * @return the new extension of the file, after the apply of the renaming
	 *         rules
	 */
	public String getExtension() {
		return this.extension;
	}
	
	/**
	 * Returns the full filename, after the apply of the renaming rules
	 * 
	 * @return the full filename, after the apply of the renaming rules
	 */
	public String getNewFullName () {
		if (this.extension != null && this.extension.length() > 0) {
			return this.name + "." + this.extension;
		} else {
			return this.name;
		}
	}
	
	/**
	 * Returns the current file object
	 * 
	 * @return the current file object
	 */
	public File getFile() {
		return this.file;
	}
}