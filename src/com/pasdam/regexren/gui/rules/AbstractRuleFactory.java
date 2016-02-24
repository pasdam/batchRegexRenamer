package com.pasdam.regexren.gui.rules;

import java.util.Arrays;

import com.pasdam.regexren.controller.LogManager;
import com.pasdam.regexren.gui.Rule;
import com.pasdam.regexren.model.RuleType;

/**
 * <p>
 * Derived classes must call {@link #setValid(boolean)} whenever their content
 * change, in order to notify if the configuration is valid or not.
 * </p>
 * 
 * @author paco
 *
 */
public abstract class AbstractRuleFactory {
	
	private Rule rule;
	
	private boolean enabled = false;
	private boolean valid = false;
	private boolean changed = true;
	
	private RuleFactoryListener listener;
	
	private final RuleType type;

	public AbstractRuleFactory(RuleType type) {
		this.type = type;
	}
	
	public RuleType getType() {
		return type;
	}

	public void setListener(RuleFactoryListener listener) {
		this.listener = listener;
	}
	
	public Rule getRule() {
		if (this.changed || (this.rule == null && valid)) {
			this.rule = createConfiguredRule();
			this.changed = false;
		}
		return this.rule;
	}
	
	protected void configurationChanged() {
		this.changed = true;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isValid() {
		return valid;
	}
	
	protected void setValid(boolean valid) {
		this.valid = valid;
		if (this.listener != null) {
			this.listener.isValidChanged(this.valid);
		}
	}
	
	protected static int boolToInt(boolean value) {
		return value ? 1 : 0;
	}
	
	protected static boolean intToBool(int value) {
		return value == 1;
	}
	
	protected abstract Rule createConfiguredRule();
	
	protected abstract void parseRuleSpecificParameters(String[] parameters) throws IllegalArgumentException;
	protected abstract String[] getRuleSpecificParameters();
	
	public String[][] getParameters() {
		String[][] parameters = {
				{""+boolToInt(enabled)},	// enabled parameter
				getRuleSpecificParameters()	// rule's specific parameters
		};
		
		if (LogManager.ENABLED) LogManager.trace("AbstractRuleFactory.getParameters: " + Arrays.toString(parameters[1]));
		
		return parameters;
	}

	public void parseParameters(String[][] parameters) throws IllegalArgumentException {
		this.enabled = intToBool(Integer.parseInt(parameters[0][0]));
		parseRuleSpecificParameters(parameters[1]);
	}
}
