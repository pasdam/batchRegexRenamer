package com.pasdam.regexren.controller;

import java.io.File;
import java.io.FileFilter;
import java.util.Locale;
import java.util.prefs.Preferences;

import com.pasdam.regexren.controller.FilterManager.FiltersListener;
import com.pasdam.utils.PropertyChangeListener;
import com.pasdam.utils.PropertyListenersManager;

/**
 * Preferences manager. It allows to store and loads preferences. Moreover it
 * allows to get notified when a preference changes.
 * 
 * @author paco
 * @version 0.1
 */
public class PreferenceManager {
	
	/** Name of the default script file, saved on application exit */
	public static final String LAST_SCRIPT_FILE = "lastScript.brr";
	
	// Preference keys
	private static final String PREFS                          = "prefs";
	private static final String PREFS_LOCALE_COUNTRY           = "locale.country";
	private static final String PREFS_LOCALE_LANGUAGE          = "locale.language";
	private static final String PREFS_PREVIOUS_FILTER          = "previousFilter";
	private static final String PREFS_PREVIOUS_FOLDER          = "previousFolder";
	private static final String PREFS_PREVIOUS_SCRIPT_FILE     = "previousScriptFile";
	private static final String PREFS_REMEMBER_PREVIOUS_FILTER = "rememberPreviousFilter";
	private static final String PREFS_REMEMBER_PREVIOUS_FOLDER = "rememberPreviousFolder";
	private static final String PREFS_REMEMBER_PREVIOUS_SCRIPT = "rememberPreviousScript";
	private static final String PREFS_SHOW_HIDDEN              = "showHidden";
	
	// Local preferences
	private File previousFolder;
	
	// Notifiers
	private final PropertyListenersManager<File> currentFolderNotifier = new PropertyListenersManager<File>();
	private final FilterManager filterManager = new FilterManager();

	/** Preference data collection */
	private Preferences prefs = Preferences.userRoot().node(PREFS);
	
	/** This method load preferences */
	public void loadPreferences() {
		// load and notify previous folder
		this.previousFolder         = new File(this.prefs.get(PREFS_PREVIOUS_FOLDER, ""));
		this.currentFolderNotifier.notifyListeners(this.previousFolder);
		
		// load and set locale
		String localeLanguage = this.prefs.get(PREFS_LOCALE_LANGUAGE, null);
		if (localeLanguage != null) {
			String localeCountry = this.prefs.get(PREFS_LOCALE_COUNTRY, null);
			
			if (localeCountry == null) {
				ApplicationManager.getInstance().getLocaleManager().setLocale(new Locale(localeLanguage));
			
			} else {
				ApplicationManager.getInstance().getLocaleManager().setLocale(new Locale(localeLanguage, localeCountry));
			}
		
		} else {
			ApplicationManager.getInstance().getLocaleManager().setLocale(Locale.getDefault());
		}

		// load and notify file filtering settings
		boolean showHidden = prefs.getBoolean(PREFS_SHOW_HIDDEN, false);
		int previousFilter = prefs.getInt(PREFS_PREVIOUS_FILTER, FilterManager.FILTER_FILES);
		this.filterManager.setFilter(previousFilter, showHidden);
		
		if (getRememberPreviousScript()) {
			// load last script
			ApplicationManager.getInstance().getRulesManager().loadScriptFile(getPreviousScriptFile());
		}
	}

	/**
	 * Returns the "Remember previous folder" setting
	 * 
	 * @return the "Remember previous folder" setting
	 */
	public boolean getRememberPreviousFolder() {
		return this.prefs.getBoolean(PREFS_REMEMBER_PREVIOUS_FOLDER, true);
	}

	/**
	 * Sets the "Remember previous folder" setting
	 * 
	 * @param rememberPreviousFolder
	 *            if true loads the previous selected folder at startup
	 */
	public void setRememberPreviousFolder(boolean rememberPreviousFolder) {
		prefs.putBoolean(PREFS_REMEMBER_PREVIOUS_FOLDER, rememberPreviousFolder);
		if (!rememberPreviousFolder) {
			// remove previous folder info
			prefs.remove(PREFS_PREVIOUS_FOLDER);
		}
	}

	/**
	 * Returns the previous selected folder, if null no folder was selected or
	 * "Remember previous folder" setting is false
	 * 
	 * @return the previous selected folder, null if no folder was selected or
	 *         "Remember previous folder" setting is false
	 */
	public File getPreviousFolder() {
		return previousFolder;
	}

	/**
	 * Sets the path of the last folder opened
	 * 
	 * @param previousFolder
	 *            the path of the last folder opened
	 */
	public void setPreviousFolder(File previousFolder) {
		if (!previousFolder.equals(this.previousFolder)) {
			// store locally
			this.previousFolder = previousFolder;
			if (getRememberPreviousFolder()) {
				// store in the preference data
				prefs.put(PREFS_PREVIOUS_FOLDER, previousFolder.getAbsolutePath());
			}
			// notify changes
			this.currentFolderNotifier.notifyListeners(this.previousFolder);
		}
	}
	
	/**
	 * Adds a listener for changes of last folder path
	 * 
	 * @param listener
	 *            listener to notify for changes of last folder path
	 */
	public void addCurrentFolderListener(PropertyChangeListener<File> listener) {
		this.currentFolderNotifier.addListener(listener);
	}

	/**
	 * Set the locale of the application
	 * 
	 * @param locale
	 *            the locale to set
	 */
	public void setLocale(Locale locale) {
		assert locale != null : "Locale cannot be null";
		// store to the preference bundle
		prefs.put(PREFS_LOCALE_LANGUAGE, locale.getLanguage());
		prefs.put(PREFS_LOCALE_COUNTRY, locale.getCountry());
		// update LocaleManager
		ApplicationManager.getInstance().getLocaleManager().setLocale(locale);
	}

	/**
	 * Returns the "Remember previous filter" setting
	 * 
	 * @return the "Remember previous filter" setting
	 */
	public boolean getRememberPreviousFilter() {
		return prefs.getBoolean(PREFS_REMEMBER_PREVIOUS_FILTER, true);
	}

	/**
	 * Set the "Remember previous filter setting"
	 * 
	 * @param rememberPreviousFilter
	 *            the rememberPreviousFilter to set
	 */
	public void setRememberPreviousFilter(boolean rememberPreviousFilter) {
		prefs.putBoolean(PREFS_REMEMBER_PREVIOUS_FILTER, rememberPreviousFilter);
		if (!rememberPreviousFilter) {
			// remove previous filter info
			prefs.remove(PREFS_PREVIOUS_FILTER);
		}
	}
	
	/**
	 * Returns the previous selected filter type, if null no filter was selected
	 * or "Remember previous filter" setting is false
	 * 
	 * @return the previous selected filter type, if null no filter was selected
	 *         or "Remember previous filter" setting is false
	 */
	public int getPreviousFilterType() {
		return this.filterManager.getFilterType();
	}

	/**
	 * Returns the previous selected {@link FileFilter}, if null no filter was selected
	 * or "Remember previous filter" setting is false
	 * 
	 * @return the previous selected {@link FileFilter}, if null no filter was selected
	 *         or "Remember previous filter" setting is false
	 */
	public FileFilter getPreviousFilter() {
		return this.filterManager.getFileFilter();
	}

	/**
	 * Set the ID of the last filter used
	 * 
	 * @param filter
	 *            the ID of the filter to save; values different from fields of
	 *            {@link FilterManager} will be ignored
	 * @param showHidden
	 *            indicates whether the filter should accept hidden files
	 */
	public void setPreviousFilter(int filter, boolean showHidden) {
		if (filter != getPreviousFilterType() || showHidden != showHidden()) {
			if (filter < FilterManager.FILTER_ALL || filter > FilterManager.FILTER_VIDEOS) {
				// if filter type is not valid, use previous one
				filter = getPreviousFilterType();
			}
			
			if (getRememberPreviousFilter()) {
				// store to the preference bundle
				prefs.putInt(PREFS_PREVIOUS_FILTER, filter);
				prefs.putBoolean(PREFS_SHOW_HIDDEN, showHidden);
			}
			this.filterManager.setFilter(filter, showHidden);;
		}
	}
	
	/**
	 * Adds a listener for changes of last used filter
	 * 
	 * @param listener
	 *            listener to notify for changes of last filter used
	 */
	public void addFilterListener(FiltersListener listener) {
		this.filterManager.addFilterListener(listener);
	}
	
	/**
	 * Returns true if hidden files/folders should be listed
	 * 
	 * @return true if hidden files/folders should be listed
	 */
	public boolean showHidden() {
		return this.filterManager.showHidden();
	}

	/**
	 * Returns true if the last script should be remembered, false otherwise
	 * 
	 * @return true if the last script should be remembered, false otherwise
	 */
	public boolean getRememberPreviousScript() {
		return prefs.getBoolean(PREFS_REMEMBER_PREVIOUS_SCRIPT, true);
	}

	/**
	 * Sets the remember last script preference
	 * 
	 * @param rememberPreviousScript
	 *            true if the last script should be remembered, false otherwise
	 */
	public void setRememberPreviousScript(boolean rememberPreviousScript) {
		prefs.putBoolean(PREFS_REMEMBER_PREVIOUS_SCRIPT, rememberPreviousScript);
		if (!rememberPreviousScript) {
			setPreviousScriptFile(null);
		}
	}

	/**
	 * It returns the last used script file
	 * 
	 * @return the last used script file, or null if the option
	 *         "Remember last script" is false
	 */
	public File getPreviousScriptFile() {
		String filePath = prefs.get(PREFS_PREVIOUS_SCRIPT_FILE, null);
		if (filePath != null) {
			return new File(filePath);
		} else {
			return null;
		}
	}

	/**
	 * Sets the last used script file, if the option "Remember last script" is
	 * true
	 * 
	 * @param file
	 *            script file used
	 */
	public void setPreviousScriptFile(File file) {
		if (getRememberPreviousScript()) {
			prefs.put(PREFS_PREVIOUS_SCRIPT_FILE, file.getAbsolutePath());
		}
	}
}
