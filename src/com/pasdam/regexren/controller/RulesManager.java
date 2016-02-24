package com.pasdam.regexren.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.pasdam.regexren.gui.rules.AbstractRuleFactory;
import com.pasdam.regexren.gui.rules.ChangeCaseFactory;
import com.pasdam.regexren.model.RuleType;

/**
 * Manager that keep a list of all renaming rules to apply
 * 
 * @author paco
 * @version 0.1
 */
public class RulesManager extends ErrorListenerManager {
	
	/**	Parameters separator used to create script files */
	private static final String PARAMETERS_SEPARATOR = ",";
	
	/** Pattern used to split lines of script files */
	private static final Pattern PATTERN_PARAMETERS_SEPARATOR = Pattern.compile(Pattern.quote(PARAMETERS_SEPARATOR));
	
	/** List of all rules created */
	private List<AbstractRuleFactory> rulesList = new ArrayList<AbstractRuleFactory>();
	
	/** List of listeners to notify when rules list changes */
	private final List<RulesListener> rulesListeners = new ArrayList<RulesListener>(); 
	
	/**
	 * Returns the list of all created rules
	 * 
	 * @return the list of all created rules
	 */
	public List<AbstractRuleFactory> getRulesList() {
		return rulesList;
	}
	
	/**
	 * Returns the number of rules created
	 * 
	 * @return the number of rules created
	 */
	public int getRulesCount() {
		return this.rulesList.size();
	}
	
	/**
	 * Add a rule to the list
	 * 
	 * @param rule
	 *            rule to add
	 */
	public void addRule(AbstractRuleFactory rule) {
		if (rule != null) {
			if (LogManager.ENABLED) LogManager.trace("RulesManager.addRule> Adding rule (index=" + this.rulesList.size() + ")");
			
			this.rulesList.add(rule);
			
			// notify listeners
			for (RulesListener rulesListener : rulesListeners) {
				rulesListener.ruleAdded(this.rulesList.size()-1, rule);
			}
		}
	}
	
	/** Remove rules that are selected (enabled) */
	public void removeSelected() {
		for (int i = this.rulesList.size()-1; i >= 0; i--) {
			if (this.rulesList.get(i).isEnabled()) {
				this.rulesList.remove(i);
				
				// notify listeners
				for (RulesListener rulesListener : rulesListeners) {
					rulesListener.ruleRemoved(i);;
				}
			}
		}
	}
	
	/**
	 * Removes the element at the specified position in this list and returns
	 * it.
	 * 
	 * @param index
	 *            position of the element to remove
	 * @return the removed element
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range (index < 0 || index >= size())
	 */
	public AbstractRuleFactory remove(int index) throws IndexOutOfBoundsException {
		return this.rulesList.remove(index);
	}
	
	/**
	 * Add the specified rules list's listener
	 * 
	 * @param listener
	 *            listener to add
	 */
	public void addRulesListener(RulesListener listener) {
		if (listener != null) {
			this.rulesListeners.add(listener);
		}
	}
	
	/**
	 * Open the specified script file
	 * 
	 * @param file
	 *            script file to open
	 */
	public void loadScriptFile(File file) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			List<AbstractRuleFactory> fileRules = new ArrayList<AbstractRuleFactory>();
			
			// parse each line
			String line;
			AbstractRuleFactory currentRule;
			boolean errorOccurred = false;
			while ((line = reader.readLine()) != null && !line.equals("")) {
				currentRule = parseRule(line);

				if (currentRule != null) {
					fileRules.add(currentRule);
				
				} else {
					errorOccurred = true;
					break;
				}
			}
			reader.close();
			
			if (!errorOccurred) {
				// commit changes
				this.rulesList = fileRules;
				// notify listeners
				for (RulesListener rulesListener : rulesListeners) {
					rulesListener.rulesChanged(this.rulesList);
				}
				
				// save last script
				ApplicationManager.getInstance().getPreferenceManager().setPreviousScriptFile(file);
			
			} else {
				if (LogManager.ENABLED) LogManager.error("RulesManager.openScriptFile: wrong rule [" + line + "]");
				
				notifyError(ApplicationManager.getInstance().getLocaleManager().getString("Error.RulesManager.addRule"));
			}
			
		} catch (Exception e) {
			if (LogManager.ENABLED) LogManager.error("RulesManager.openScriptFile: " + e.getMessage());
			
			notifyError(ApplicationManager.getInstance().getLocaleManager().getString("Error.RulesManager.loadScriptFile"));
		}
	}

	/**
	 * Parse the specified csv string and add the related rule to the list
	 * 
	 * @param rule
	 *            csv string containing the rule parameters
	 */
	private AbstractRuleFactory parseRule(String rule){
		try {
			// split csv line
			String[] parameters = PATTERN_PARAMETERS_SEPARATOR.split(rule);
			
			// parse rule type id
			RuleType ruleType = RuleType.parseId(Integer.parseInt(parameters[0]));
			
			// create correct rule
			AbstractRuleFactory ruleFactory;
			switch (ruleType) {
				case INSERT_TEXT_AT_POSITION:
					// TODO ruleFactory = new InsertTextAtPositionFactory();
					// break;
	
				case INSERT_TEXT_BEFORE_AFTER_PATTERN:
					// TODO ruleFactory = new InsertTextBeforeAfterFactory();
					// break;
					
				case INSERT_COUNTER_AT_POSITION:
					// TODO ruleFactory = new InsertCounterAtPositionFactory();
					// break;
					
				case INSERT_COUNTER_BEFORE_AFTER_PATTERN:
					// TODO ruleFactory = new InsertCounterBeforeAfterFactory();
					// break;
					
				case REPLACE:
					// TODO ruleFactory = new ReplaceFactory();
					// break;
				
				case REMOVE:
					// TODO ruleFactory = new ReplaceFactory();
					// break;
					return null;
	
				case CHANGE_CASE:
					ruleFactory = new ChangeCaseFactory();
					break;
					
				default:
					if (LogManager.ENABLED) LogManager.warning("RulesManager.parseRule: unknown rule type");
					return null;
			}

			// remove id from parameters
			parameters = Arrays.copyOfRange(parameters, 1, parameters.length);
			
			// configure rule
			ruleFactory.parseParameters(new String[][] {
				{parameters[0]},										// enable parameter
				Arrays.copyOfRange(parameters, 1, parameters.length)	// rule's specific parameters
			});
			
			return ruleFactory;
			
		} catch (Exception e) {
			if (LogManager.ENABLED) LogManager.error("RulesManager.parseRule: " + e.getMessage());
			return null;
		}
	}
	
	/** Clear the rules list */
	public void clear() {
		this.rulesList.clear();
		
		// notify listeners
		for (RulesListener rulesListener : rulesListeners) {
			rulesListener.rulesChanged(this.rulesList);
		}
	}
	
	/**
	 * Save the rules list to the specified file
	 * 
	 * @param file
	 *            script file to save
	 */
	public void saveToFile(File file) {
		if (this.rulesList.size() > 0) {
			BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(file));
				
				String[][] parameters;
				StringBuilder builder = new StringBuilder();
				for (AbstractRuleFactory rule: this.rulesList) {
					parameters = rule.getParameters();
					
					// build csv line
					builder.append(rule.getType().getId() + PARAMETERS_SEPARATOR + parameters[0][0]); // append id and enabled
					for (String parameter: parameters[1]) {
						builder.append(PARAMETERS_SEPARATOR + (parameter != null ? parameter : ""));
					}
					if (LogManager.ENABLED) LogManager.trace("RulesManager.save: saving rule [" + builder.toString() + "]");

					builder.append("\n");
					
					// write line to file
					writer.write(builder.toString());
					
					// clear buffer
					builder.setLength(0);
				}
				writer.flush();
				writer.close();
				
				// save last script
				ApplicationManager.getInstance().getPreferenceManager().setPreviousScriptFile(file);
				
			} catch (IOException e) {
				if (LogManager.ENABLED) LogManager.error("RulesManager.save: " + e.getMessage());
				
				notifyError(ApplicationManager.getInstance().getLocaleManager().getString("Error.RulesManager.addRule"));
			}
		}
	}
	
	/** Interface implemented by all components that need to be notified of rules list changes */
	public static interface RulesListener {
		
		/**
		 * Indicates that a rule was added to the list
		 * 
		 * @param index
		 *            index of the added rule
		 * @param addedRule
		 *            rule added
		 */
		public void ruleAdded(int index, AbstractRuleFactory addedRule);
		
		/**
		 * Indicates that a rule was removed from the list
		 * 
		 * @param index
		 *            index of the removed rule
		 */
		public void ruleRemoved(int index);
		
		/**
		 * Indicates that the rules list has changed
		 * 
		 * @param rulesList
		 *            the list of all created rules
		 */
		public void rulesChanged(List<AbstractRuleFactory> rulesList);
		
		/**
		 * Indicates that a rule has changed position in the list
		 * 
		 * @param oldPosition
		 *            old position in the list
		 * @param newPosition
		 *            new position
		 * @param rule
		 *            rule moved
		 */
		public void ruleMoved(int oldPosition, int newPosition, AbstractRuleFactory rule);
	}
}
