package com.pasdam.regexren.controller;

/**
 * Application manager, it allows access to all data managers
 * 
 * @author paco
 * @version 0.1
 */
public class ApplicationManager {
	
	/** Singleton instance */
	private static final ApplicationManager instance = new ApplicationManager();

	/** Preferences manager */
	private final PreferenceManager preferenceManager = new PreferenceManager();
	
	/** Manager of the current locale */
	private final LocaleManager localeManager = new LocaleManager();
	
	/** Manager of rules list */
	private final RulesManager rulesManager = new RulesManager();
	
	/** Manager of files list */
	private final FilesListManager filesListManager = new FilesListManager();
	
	/** Private constructor: it avoids direct instantiation */
	private ApplicationManager() {}
	
	/**
	 * Returns the singleton instance of the manager
	 * 
	 * @return the singleton instance of the manager
	 */
	public static ApplicationManager getInstance() {
		return instance;
	}
	
	/** Initialize the application managers */
	public void init() {
		this.preferenceManager.addCurrentFolderListener(this.filesListManager);
		this.preferenceManager.addFilterListener(this.filesListManager);
		this.preferenceManager.loadPreferences();
	}

	/**
	 * Returns the preference manager of the application
	 * 
	 * @return the preference manager of the application
	 */
	public PreferenceManager getPreferenceManager() {
		return preferenceManager;
	}
	
	/**
	 * Returns the locale manager
	 * 
	 * @return the locale manager
	 */
	public LocaleManager getLocaleManager() {
		return localeManager;
	}
	
	/**
	 * Returns the rules manager
	 * 
	 * @return the rules manager
	 */
	public RulesManager getRulesManager() {
		return rulesManager;
	}
	
	/**
	 * Returns the files list manager
	 * 
	 * @return the files list manager
	 */
	public FilesListManager getFilesListManager() {
		return filesListManager;
	}
}
