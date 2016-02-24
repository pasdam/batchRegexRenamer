package com.pasdam.regexren.controller;

import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import com.pasdam.utils.file.fileFilters.FileFilterFactory;
import com.pasdam.utils.file.fileFilters.FileTypeFilter;

/**
 * Class that manages set and notification of the current {@link FileFilter} used
 * 
 * @author paco
 * @version 0.1
 */
public class FilterManager {
	
	/** Indicate a file filter that accept all files and folder */
	public static final int FILTER_ALL            = 0;
	
	/** Indicate a file filter that accept only folder */
	public static final int FILTER_FOLDERS        = FILTER_ALL            + 1;
	
	/** Indicate a file filter that accept only regular files */
	public static final int FILTER_FILES          = FILTER_FOLDERS        + 1;
	
	/** Indicate a file filter that accept only archive files */
	public static final int FILTER_ARCHIVES       = FILTER_FILES          + 1;
	
	/** Indicate a file filter that accept only audio files */
	public static final int FILTER_AUDIO          = FILTER_ARCHIVES       + 1;
	
	/** Indicate a file filter that accept only document files */
	public static final int FILTER_DOCUMENTS      = FILTER_AUDIO          + 1;
	
	/** Indicate a file filter that accept only images files */
	public static final int FILTER_IMAGES         = FILTER_DOCUMENTS      + 1;
	
	/** Indicate a file filter that accept only media archive files */
	public static final int FILTER_MEDIA_ARCHIVES = FILTER_IMAGES         + 1;
	
	/** Indicate a file filter that accept only video files*/
	public static final int FILTER_VIDEOS         = FILTER_MEDIA_ARCHIVES + 1;
	
	/** List of listener to notify when filter settings changed */
	private List<FiltersListener> filtersListeners = new ArrayList<FiltersListener>();
	
	/** Indicates the current filter type */
	private int filterType = -1;
	
	/** Indicates whether the filter should accept hidden files or not */
	private boolean showHidden = false;
	
	/** Current file filter */
	private FileFilter fileFilter;
	
	/**
	 * Adds a listener for changes of last used filter
	 * 
	 * @param listener
	 *            listener to notify for changes of last filter used
	 */
	public void addFilterListener(FiltersListener listener) {
		this.filtersListeners.add(0, listener);
	}
	
	/**
	 * Set a filter with the specified parameters
	 * 
	 * @param filterType
	 *            type of the filter to create
	 * @param showHidden
	 *            indicates if the filter should accept hidden files/folders
	 */
	public void setFilter(int filterType, boolean showHidden) {
		if (this.filterType != filterType ||  this.showHidden != showHidden) {
			// Create the correct filter and notify it
			this.fileFilter = null;
			switch (filterType) {
			case FILTER_ALL:
				this.fileFilter = FileFilterFactory.getFilter(true, true, showHidden);
				break;

			case FILTER_FOLDERS:
				this.fileFilter = FileFilterFactory.getFilter(true, false, showHidden);
				break;

			case FILTER_FILES:
				this.fileFilter = FileFilterFactory.getFilter(false, true, showHidden);
				break;

			case FILTER_ARCHIVES:
				this.fileFilter = new FileTypeFilter(FileTypeFilter.EXT_ARCHIVE, showHidden);
				break;

			case FILTER_AUDIO:
				this.fileFilter = new FileTypeFilter(FileTypeFilter.EXT_AUDIO, showHidden);
				break;

			case FILTER_DOCUMENTS:
				this.fileFilter = new FileTypeFilter(FileTypeFilter.EXT_DOCUMENT, showHidden);
				break;

			case FILTER_IMAGES:
				this.fileFilter = new FileTypeFilter(FileTypeFilter.EXT_IMAGES, showHidden);
				break;

			case FILTER_MEDIA_ARCHIVES:
				this.fileFilter = new FileTypeFilter(FileTypeFilter.EXT_MEDIA_ARCHIVE, showHidden);
				break;

			case FILTER_VIDEOS:
				this.fileFilter = new FileTypeFilter(FileTypeFilter.EXT_VIDEO, showHidden);
				break;

			default:
				// use previous filter type
				filterType = this.filterType;
				break;
			}
			
			// update values
			this.filterType = filterType;
			this.showHidden = showHidden;
			
			// notify filter changed
			FiltersListener listener;
			for (int i = this.filtersListeners.size()-1; i >= 0; i--) {
				listener = this.filtersListeners.get(i);
				if (listener != null) {
					listener.filterChanged(filterType, showHidden, this.fileFilter);
					
				} else {
					this.filtersListeners.remove(i);
				}
			}
		}
	}
	
	/**
	 * Returns the type of the current selected filter
	 * 
	 * @return the type of the current selected filter
	 */
	public int getFilterType() {
		return this.filterType;
	}
	
	/**
	 * Returns true if the current filter accept hidden files, false otherwise
	 * 
	 * @return true if the current filter accept hidden files, false otherwise
	 */
	public boolean showHidden() {
		return this.showHidden;
	}
	
	/**
	 * Returns the current file filter
	 * 
	 * @return the current file filter
	 */
	public FileFilter getFileFilter() {
		return this.fileFilter;
	}
	
	/**
	 * Interface that listener that want to be notified when the selected filter
	 * changed must implement
	 */
	public interface FiltersListener {
		
		/**
		 * Notify that the filter is changed
		 * 
		 * @param filter
		 *            id of the new filter to use
		 * @param showHidden
		 *            indicates whether the filter should accept hidden files
		 * @param filter
		 *            {@link FileFilter} to use, or null if no filter should be
		 *            applied
		 */
		public void filterChanged(int filterType, boolean showHidden, FileFilter filter);
	}
}
