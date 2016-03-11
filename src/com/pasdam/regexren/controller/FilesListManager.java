package com.pasdam.regexren.controller;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pasdam.regexren.controller.FilterManager.FiltersListener;
import com.pasdam.regexren.gui.rules.AbstractRuleFactory;
import com.pasdam.regexren.model.FileModelItem;
import com.pasdam.utils.PropertyChangeListener;
import com.pasdam.utils.file.FileRenamer;
import com.pasdam.utils.file.fileFilters.FirstFolderComparator;

/**
 * Manager of the files list
 * 
 * @author paco
 * @version 0.1
 */
public class FilesListManager implements PropertyChangeListener<File>, FiltersListener {

	/** List of files data */
	private final ArrayList<FileModelItem> filesDataList = new ArrayList<FileModelItem>();

	/** List of file renamers */
	private final ArrayList<FileRenamer> fileRenamersList = new ArrayList<FileRenamer>();

	/** List of listener to notify when the files list change */
	private final List<FilesListListener> listeners = new ArrayList<FilesListListener>();

	/**
	 * Returns the list of files
	 * 
	 * @return the list of files
	 */
	public ArrayList<FileModelItem> getFilesList() {
		return filesDataList;
	}

	/**
	 * Add a listener to notify on files list changes
	 * 
	 * @param listener
	 *            listener to add
	 */
	public void addFilesListListener(FilesListListener listener) {
		if (listener != null) {
			this.listeners.add(listener);
		}
	}

	/** Clear the file list */
	private void clear() {
		this.filesDataList.clear();
		this.fileRenamersList.clear();
	}

	/** Apply the rules to each file in the list and rename it */
	public void applyRules(boolean rename) {
		if (this.fileRenamersList.size() > 0) {
			List<AbstractRuleFactory> rules = ApplicationManager.getInstance().getRulesManager().getRulesList();
			if (rules.size() > 0) {
				FileRenamer renamer;
				FileModelItem fileData;
				File newFile;

				// TODO: check for invalid rules
				// TODO: add support to check if a file with the same name already exists
				
				// reset rules state
				for (AbstractRuleFactory ruleFactory : rules) {
					if (ruleFactory.isEnabled()) {
						ruleFactory.getRule().reset();
					}
				}

				for (int i = 0; i < this.fileRenamersList.size(); i++) {
					renamer = this.fileRenamersList.get(i);
					fileData = this.filesDataList.get(i);
					fileData.reset();

					if (fileData.isChecked()) {
						// apply rules for each checked file
						for (AbstractRuleFactory ruleFactory : rules) {
							if (ruleFactory.isEnabled()) {
								ruleFactory.getRule().apply(fileData);
							}
						}

						// rename file
						if (rename) {
							newFile = new File(renamer.getCurrentFile().getParentFile(), fileData.getNewFullName());
							renamer.renameTo(newFile);
							this.filesDataList.set(i, new FileModelItem(newFile));
						}
					}
				}
				
				boolean undoAvailable = this.fileRenamersList.get(0).undoAvailable();

				// notify changes
				for (FilesListListener filesListListener : listeners) {
					filesListListener.filesListChanged(this.filesDataList, undoAvailable);
				}
			} 
		}
	}

	/** Undo the last rename operation of all files */
	public void undoRename() {
		FileRenamer fileRenamer;
		for (int i = 0; i < this.fileRenamersList.size(); i++) {
			fileRenamer = this.fileRenamersList.get(i);
			fileRenamer.undoRename();
			this.filesDataList.set(i, new FileModelItem(fileRenamer.getCurrentFile()));
		}
		
		// reset undoAvailable property
		boolean undoAvailable = false;
		if (this.fileRenamersList.size() > 0) {
			undoAvailable = this.fileRenamersList.get(0).undoAvailable();
		}
		
		// notify changes
		for (FilesListListener filesListListener : listeners) {
			filesListListener.filesListChanged(this.filesDataList, undoAvailable);
		}
	}

	/**
	 * Returns the file at the specified index
	 * 
	 * @param index
	 *            index of the file to get
	 * @return the file at the specified index
	 */
	public File getCurrentFile(int index) {
		if (index >= 0 && index < this.fileRenamersList.size()) {
			return this.fileRenamersList.get(index).getCurrentFile();
		} else {
			return null;
		}
	}

	/**
	 * Sets whether the file at position <i>index</i> should be renamed or not
	 * 
	 * @param index
	 *            index of the file
	 * @param checked
	 *            true if the file should be renamed, false otherwise
	 */
	public void setChecked(int index, boolean checked) {
		if (index >= 0 && index < this.filesDataList.size()) {
			this.filesDataList.get(index).setChecked(checked);
		}
	}

	/**
	 * Add specified files to the list
	 * 
	 * @param children
	 *            list of files to add
	 */
	private void add(File[] children) {
		// clear current list
		clear();
		
		if (children != null) {
			// sort
			Arrays.sort(children, new FirstFolderComparator());
			// add elements to the model
			for (File file : children) {
				this.filesDataList.add(new FileModelItem(file));
				this.fileRenamersList.add(new FileRenamer(file));
			}
		}
		
		// notify changes
		for (FilesListListener filesListListener : listeners) {
			filesListListener.filesListChanged(this.filesDataList, false);
		}
	}

	@Override
	public void filterChanged(int filterType, boolean showHidden, FileFilter filter) {
		// current filter is changed
		File[] children = ApplicationManager.getInstance().getPreferenceManager().getPreviousFolder().listFiles(filter);
		add(children);
	}

	/**
	 * Update the current folder
	 * 
	 * @see com.pasdam.utils.PropertyChangeListener#propertyChanged(java.lang.Object)
	 */
	@Override
	public void propertyChanged(File currentFolder) {
		// current folder is changed
		File[] children = currentFolder
				.listFiles(ApplicationManager.getInstance().getPreferenceManager().getPreviousFilter());
		add(children);
	}

	/**
	 * Interface to be implemented by listeners that need to be notified when
	 * the list of files change
	 *
	 * @author paco
	 * @version 0.1
	 */
	public static interface FilesListListener {

		/**
		 * Indicates that the list of files is changed
		 * 
		 * @param list
		 *            new list of files
		 * @param undoAvailable
		 *            true if undo rename is available, false otherwise
		 */
		public void filesListChanged(List<FileModelItem> list, boolean undoAvailable);
	}
}
