package com.pasdam.regexren.gui.rules;

/**
 * Interface implemented by those classes that need to be notified of
 * {@link AbstractRuleFactory}'s changes
 * 
 * @author paco
 * @version 0.1
 */
public interface RuleFactoryListener {
	
	/**
	 * Indicates that the validation value of the rule is changed
	 * 
	 * @param valid
	 *            if true indicates that the rule's current configuration is
	 *            valid, false indicates that is invalid
	 */
	public void isValidChanged(boolean valid);
}
