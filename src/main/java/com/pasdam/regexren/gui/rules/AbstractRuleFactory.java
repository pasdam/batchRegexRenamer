package com.pasdam.regexren.gui.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pasdam.regexren.controller.LogManager;
import com.pasdam.regexren.model.RuleType;

/**
 * 
 * <p>
 * Derived class must call {@link #configurationChanged()} whenever their
 * content changes
 * </p>
 * <p>
 * Derived classes can call {@link #setValid(boolean)} to notify if the
 * configuration is valid or not. Such method implicitly call
 * {@link #configurationChanged()}.
 * </p>
 * 
 * @author paco
 * @version 0.1
 */
public abstract class AbstractRuleFactory {

	/** Rule's type */
	private final RuleType type;
	
	/** Configured rule */
	private Rule rule;
	
	/** Indicates whether the rule is enabled or not */
	private boolean enabled = false;

	/** Indicates whether the rule's configuration is valid */
	private boolean valid = false;
	
	/** Indicates whether the rule's configuration is changed until last rule build */
	private boolean changed = true;

	/** Listener to notify on rule configuration changes */
	private final List<RuleFactoryListener> listeners = new ArrayList<RuleFactoryListener>();
	
	/**
	 * Create a factory class for the specified rule's type
	 * 
	 * @param type
	 *            type of the rule of the factory
	 */
	public AbstractRuleFactory(RuleType type) {
		this.type = type;
	}

	/**
	 * Sets the listener to notify rule's configuration changes
	 * 
	 * @param listener
	 *            the listener to notify rule's configuration changes
	 */
	public void addConfigurationListener(RuleFactoryListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * Returns the rule type
	 * 
	 * @return the rule type
	 */
	public RuleType getType() {
		return type;
	}
	
	/**
	 * Returns the configured rule
	 * 
	 * @return the configured rule, or null if the configuration is invalid
	 */
	public Rule getRule() {
		if (this.valid) {
			if (this.changed || this.rule == null) {
				this.rule = createConfiguredRule();
				this.changed = false;
			}
			return this.rule;
		}
		return null;
	}
	
	/** Method that derived class must call to notify confiuration's changes */
	protected void configurationChanged() {
		this.changed = true;
		for (RuleFactoryListener listener : this.listeners) {
			listener.configurationChanged(this.valid);
		}
	}
	
	/**
	 * Returns true if the rule is enabled, false otherwise
	 * 
	 * @return true if the rule is enabled, false otherwise
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Sets whether the rule is enabled or not
	 * 
	 * @param enabled
	 *            true if the rule is enabled, false otherwise
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		configurationChanged();
	}

	/**
	 * Returns true if the rule's configuration is valid, false otherwise
	 * 
	 * @return true if the rule's configuration is valid, false otherwise
	 */
	public boolean isValid() {
		return valid;
	}
	
	/**
	 * Sets whether the rule's configuration is valid or not
	 * 
	 * @param valid
	 *            true if the rule's configuration is valid, false otherwise
	 */
	protected void setValid(boolean valid) {
		this.valid = valid;
		configurationChanged();
	}

	/**
	 * Returns the configured rule
	 * 
	 * @return the configured rule
	 */
	protected abstract Rule createConfiguredRule();
	
	/**
	 * Parses string parameters and configure the factory object
	 * 
	 * @param parameters
	 *            array of parameters to parse
	 * @throws IllegalArgumentException
	 *             if parameters are invalid
	 */
	protected abstract void parseRuleSpecificParameters(String[] parameters) throws IllegalArgumentException;
	
	/**
	 * Returns a string array with all the configuration's parameter
	 * 
	 * @return a string array with all the configuration's parameter
	 */
	protected abstract String[] getRuleSpecificParameters();
	
	/**
	 * Returns an array of array of parameters
	 * 
	 * @return an array of array of parameters (the first array has only one
	 *         element that indicates whether the rule is enabled or not, the
	 *         second array contains rule's specific parameters)
	 */
	public String[][] getParameters() {
		String[][] parameters = {
				{""+boolToInt(enabled)},	// enabled parameter
				getRuleSpecificParameters()	// rule's specific parameters
		};
		
		if (LogManager.ENABLED) LogManager.trace("AbstractRuleFactory.getParameters: " + Arrays.toString(parameters[1]));
		
		return parameters;
	}
	
	/**
	 * Checks the configuration of the rule factory, throwing a
	 * {@link RuntimeException} if some parameter's value is invalid
	 * 
	 * @throws RuntimeException
	 *             if some parameters are invalid
	 */
	protected abstract void checkConfiguration() throws RuntimeException;

	/**
	 * Parses the array of array of parameter and configure the factory object
	 * 
	 * @param parameters
	 *            array of array of parameters (the first array must have only
	 *            one element that indicates whether the rule is enabled or not,
	 *            the second array must contain rule's specific parameters)
	 * @throws IllegalArgumentException
	 *             if the parameters are invalid
	 */
	public void parseParameters(String[][] parameters) throws IllegalArgumentException {
		this.enabled = intToBool(Integer.parseInt(parameters[0][0]));
		
		parseRuleSpecificParameters(parameters[1]);
		
		try {
			checkConfiguration();
			
		} catch (RuntimeException exception) {
			if (LogManager.ENABLED) LogManager.info("AbstractRuleFactory.parseParameters: " + exception.getMessage());
		}
	}
	
	/**
	 * Utility function used to convert boolean parameters as int
	 * 
	 * @param value
	 *            value to convert
	 * @return 1 if value is true, 0 otherwise
	 */
	protected static int boolToInt(boolean value) {
		return value ? 1 : 0;
	}
	
	/**
	 * Utility function used to parse boolean parameters
	 * 
	 * @param value
	 *            value to parse
	 * @return true if value is 1, false otherwise
	 */
	protected static boolean intToBool(int value) {
		return value == 1;
	}
}
