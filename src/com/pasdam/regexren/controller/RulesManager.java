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
import com.pasdam.regexren.gui.rules.InsertCounterAtPositionFactory;
import com.pasdam.regexren.gui.rules.InsertCounterBeforeAfterFactory;
import com.pasdam.regexren.gui.rules.InsertCounterOnCollisionFactory;
import com.pasdam.regexren.gui.rules.InsertTextAtPositionFactory;
import com.pasdam.regexren.gui.rules.InsertTextBeforeAfterFactory;
import com.pasdam.regexren.gui.rules.MoveTextBeforeAfterFactory;
import com.pasdam.regexren.gui.rules.ReplaceFactory;
import com.pasdam.regexren.gui.rules.RuleFactoryListener;
import com.pasdam.regexren.model.RuleType;

/**
 * Manager that keep a list of all renaming rules to apply
 * 
 * @author paco
 * @version 0.1
 */
public class RulesManager extends ErrorListenerManager implements RuleFactoryListener {
	
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
	 * Returns the index of the first occurrence of the specified element in
	 * this list, or -1 if this list does not contain the element
	 * 
	 * @param ruleFactory
	 *            element to search for
	 * @return the index of the first occurrence of the specified element in
	 *         this list, or -1 if this list does not contain the element
	 */
	public int indexOf(AbstractRuleFactory ruleFactory) {
		return this.rulesList.indexOf(ruleFactory);
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
			for (RulesListener rulesListener : this.rulesListeners) {
				rulesListener.ruleAdded(this.rulesList.size()-1, rule);
			}
			
			rule.addConfigurationListener(this);
			
			configurationChanged(true);
		}
	}
	
	/** Remove rules that are selected (enabled) */
	public void removeSelected() {
		boolean removed = false;
		
		for (int i = this.rulesList.size()-1; i >= 0; i--) {
			if (this.rulesList.get(i).isEnabled()) {
				this.rulesList.remove(i);
				
				removed = true;
				
				// notify listeners
				for (RulesListener rulesListener : this.rulesListeners) {
					rulesListener.ruleRemoved(i);
				}
			}
		}
		
		if (removed) {
			configurationChanged(true);
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
		AbstractRuleFactory rule = this.rulesList.remove(index);
		
		configurationChanged(true);
		
		// notify listeners
		for (RulesListener rulesListener : this.rulesListeners) {
			rulesListener.ruleRemoved(index);
		}
		
		return rule;
	}
	
	/**
	 * Removes the element at the specified position in this list and returns
	 * it.
	 * 
	 * @param rule
	 *            rule factory to remove
	 * @return the removed element
	 */
	public AbstractRuleFactory remove(AbstractRuleFactory rule) {
		int index = this.rulesList.indexOf(rule);
		if (index >= 0) {
			return remove(index);
		}
		return null;
	}
	
	/**
	 * Move the rule at position <i>from</i> to position <i>to</i>
	 * 
	 * @param from
	 *            index of the rule to move
	 * @param to
	 *            new position of the rule
	 * @throws IndexOutOfBoundsException
	 *             if parameters are not valid
	 */
	public void move(int from, int to) throws IndexOutOfBoundsException {
		if (LogManager.ENABLED) LogManager.trace("RulesManager.move> Move rule from " + from + " to " + to);

		// move rule
		AbstractRuleFactory ruleFactory = this.rulesList.remove(from);
		this.rulesList.add(to, ruleFactory);
		
		configurationChanged(true);
		
		// notify listeners
		for (RulesListener rulesListener : this.rulesListeners) {
			rulesListener.ruleRemoved(from);
			rulesListener.ruleAdded(to, ruleFactory);
		}
	}
	
	/** Move the selected rules up of one position */
	public void moveUpSelected() {
		int size = this.rulesList.size();
		if (size >= 2) {
			// move the second rule
			if (this.rulesList.get(1).isEnabled() && !this.rulesList.get(0).isEnabled()) {
				move(1, 0);
			}
			
			// move rules from the 3rd position
			for (int i = 2; i < size; i++) {
				if (this.rulesList.get(i).isEnabled()) {
					move(i, i-1);
				}
			}
		}
	}
	
	/** Move the selected rules down of one position */
	public void moveDownSelected() {
		int size = this.rulesList.size();
		if (size >= 2) {
			// move the second-last rule
			if (this.rulesList.get(size-2).isEnabled() && !this.rulesList.get(size-1).isEnabled()) {
				move(size-2, size-1);
			}

			// move other rules
			for (int i = size - 3; i >= 0; i--) {
				if (this.rulesList.get(i).isEnabled()) {
					move(i, i + 1);
				}
			}
		}
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
	
	@Override
	public void configurationChanged(boolean valid) {
		if (valid) {
			if (LogManager.ENABLED) LogManager.trace("RulesManager.configurationChanged> Rule configuration changed: updating files list");
			ApplicationManager.getInstance().getFilesListManager().applyRules(false);
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
					
					currentRule.addConfigurationListener(this);
				
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
				for (RulesListener rulesListener : this.rulesListeners) {
					rulesListener.rulesChanged(this.rulesList);
				}
				
				configurationChanged(true);
				
				// save last script
				ApplicationManager.getInstance().getPreferenceManager().setPreviousScriptFile(file);
			
			} else {
				if (LogManager.ENABLED) LogManager.error("RulesManager.openScriptFile: wrong rule [" + line + "]");
				notifyError("Error.RulesManager.addRule");
			}
			
		} catch (Exception e) {
			if (LogManager.ENABLED) LogManager.error("RulesManager.openScriptFile: " + e.getMessage());
			notifyError("Error.RulesManager.loadScript");
			e.printStackTrace();
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
					ruleFactory = new InsertTextAtPositionFactory();
					break;
	
				case INSERT_TEXT_BEFORE_AFTER_PATTERN:
					ruleFactory = new InsertTextBeforeAfterFactory();
					break;
					
				case INSERT_COUNTER_AT_POSITION:
					ruleFactory = new InsertCounterAtPositionFactory();
					break;
					
				case INSERT_COUNTER_BEFORE_AFTER_PATTERN:
					ruleFactory = new InsertCounterBeforeAfterFactory();
					break;
					
				case INSERT_COUNTER_ON_COLLISION:
					ruleFactory = new InsertCounterOnCollisionFactory();
					break;
					
				case REPLACE:
					ruleFactory = new ReplaceFactory();
					break;
					
				case REMOVE:
					ruleFactory = new ReplaceFactory();
					break;
				
				case MOVE:
					ruleFactory = new MoveTextBeforeAfterFactory();
					break;
	
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
			if (LogManager.ENABLED) LogManager.error("RulesManager.parseRule: " + e.getMessage() + ". Invalid rule: " + rule);
			return null;
		}
	}
	
	/** Clear the rules list */
	public void clear() {
		this.rulesList.clear();
		
		configurationChanged(true);
		
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
	public boolean saveToFile(File file) {
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
			
			return true;
			
		} catch (IOException e) {
			if (LogManager.ENABLED) LogManager.error("RulesManager.save: " + e.getMessage());
			notifyError("Error.RulesManager.saveScript");
		}
		
		return false;
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
	}
}
