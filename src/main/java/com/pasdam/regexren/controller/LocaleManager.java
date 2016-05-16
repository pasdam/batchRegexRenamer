package com.pasdam.regexren.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Class that manages the locale of the application
 * 
 * @author paco
 * @version 0.1
 */
public class LocaleManager {
	
	/** Resource bundle base name */
	private static final String BUNDLE_BASE_NAME = "com.pasdam.regexren.gui.lang.messages";
	
	/** Resource bundle containing the string resources */
	private ResourceBundle localeBundle;
	
	/** List of localizable components */
	private List<Localizable> localizableComponents = new ArrayList<Localizable>();
	
	/** Current locale */
	private Locale locale;
	
	/**
	 * Sets the interface language to currentLocale
	 * 
	 * @param locale
	 *            - language to load
	 */
	public void setLocale(Locale locale){
		if (this.locale != locale  || (this.locale != null && !this.locale.equals(locale))) {
			if (LogManager.ENABLED) LogManager.trace("LocaleManager.setLocale> Setting locale: " + locale);
			this.locale = locale;
			Locale.setDefault(this.locale);
			this.localeBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, this.locale);
			// notify changes
			for (Localizable localizable : localizableComponents) {
				localizable.localeChanged(this);
			} 
		}
    }
	
	/**
	 * Returns the current locale
	 * 
	 * @return the current locale
	 */
	public Locale getLocale() {
		return this.locale;
	}
	
	/**
	 * Add a component that needs to be notified when the locale changes
	 * 
	 * @param localizable
	 *            localizable component to add
	 */
	public void addLocalizableComponent(Localizable localizable) {
		if (localizable != null) {
			this.localizableComponents.add(localizable);
		}
	}
	
	/**
	 * Get the string with the specified key
	 * 
	 * @param key
	 *            of the string to retrieve
	 * @return the string with the specified key
	 */
	public String getString(String key) {
		return this.localeBundle != null
				? this.localeBundle.getString(key)
				: "";
	}
	
	/**
	 * Interface implemented by localizable components
	 * 
	 * @author paco
	 * @version 0.1
	 */
	public static interface Localizable {
		
		/**
		 * Indicates that the application's locale is changed
		 * 
		 * @param localeManager
		 *            manager to use to get localized strings
		 */
		public void localeChanged(LocaleManager localeManager);
	}
}
