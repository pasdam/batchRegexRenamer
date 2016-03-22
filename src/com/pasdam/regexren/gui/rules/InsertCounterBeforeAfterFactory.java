package com.pasdam.regexren.gui.rules;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.pasdam.regexren.model.FileModelItem;
import com.pasdam.regexren.model.RuleType;

/**
 * {@link AbstractRuleFactory} used to create rule to insert a counter before/after a specific pattern
 * 
 * @author paco
 * @version 0.1
 */
public class InsertCounterBeforeAfterFactory extends AbstractInsertCounterFactory {
	
	/** Index of the "beforePattern" parameter */
	private static final int PARAMETER_BEFORE_PATTERN = AbstractInsertCounterFactory.PARAMETERS_COUNT;
	/** Index of the "textToSearch" parameter */
	private static final int PARAMETER_TEXT_TO_SEARCH = PARAMETER_BEFORE_PATTERN + 1;
	/** Index of the "matchCase" parameter */
	private static final int PARAMETER_MATCH_CASE     = PARAMETER_TEXT_TO_SEARCH + 1;
	/** Index of the "regex" parameter */
	private static final int PARAMETER_REGEX          = PARAMETER_MATCH_CASE     + 1;
	/** Indicates how many parameters this component has */
	private static final int PARAMETERS_COUNT         = PARAMETER_REGEX          + 1;

	/** Indicates whether the pattern should be inserted before the searched pattern or after */
	private boolean beforePattern = true;
	
	/** Pattern to find */
	private String textToSearch;

	/** Indicates whether the pattern should match case or not */
	private boolean matchCase;

	/** Indicates if the pattern is a regular expression or a literal one */
	private boolean regex;
	
	/** Creates a {@link InsertCounterBeforeAfterFactory} */
	public InsertCounterBeforeAfterFactory() {
		super(RuleType.INSERT_COUNTER_BEFORE_AFTER_PATTERN);
	}

	/**
	 * Returns true if the counter should be inserted before the specified
	 * pattern, false otherwise
	 * 
	 * @return true if the counter should be inserted before the specified
	 *         pattern, false otherwise
	 */
	public boolean isBeforePattern() {
		return this.beforePattern;
	}

	/**
	 * Sets whether the pattern should be inserted before the searched pattern
	 * or after
	 * 
	 * @param beforePattern
	 *            if true the counter value is inserted before the specified
	 *            pattern, if false it is inserted after
	 */
	public void setBeforePattern(boolean beforePattern) {
		if (this.beforePattern != beforePattern) {
			this.beforePattern = beforePattern;
			super.configurationChanged();
		}
	}

	/**
	 * Returns the pattern to search
	 * 
	 * @return the pattern to search
	 */
	public String getTextToSearch() {
		return this.textToSearch;
	}

	/**
	 * Sets the pattern to search
	 * 
	 * @param textToSearch
	 *            the pattern to search
	 * @throws NullPointerException
	 *             if the pattern is null or empty
	 * @throws PatternSyntaxException
	 *             if regex is true and the pattern isn't a valid regular
	 *             expression
	 */
	public void setTextToSearch(String textToSearch) throws NullPointerException, PatternSyntaxException {
		if (this.textToSearch != textToSearch  || (this.textToSearch != null && !this.textToSearch.equals(textToSearch))) {
			this.textToSearch = textToSearch;
			checkConfiguration();
		}
	}

	/**
	 * Returns true if the pattern is case sensitive, false otherwise
	 * 
	 * @return true if the pattern is case sensitive, false otherwise
	 */
	public boolean isMatchCase() {
		return this.matchCase;
	}

	/**
	 * Sets whether the pattern is case sensitive or not
	 * 
	 * @param matchCase
	 *            if true the pattern is case sensitive
	 */
	public void setMatchCase(boolean matchCase) {
		if (this.matchCase != matchCase) {
			this.matchCase = matchCase;
			super.configurationChanged();
		}
	}

	/**
	 * Returns true if the pattern is a regular expression, false if it is a
	 * literal pattern
	 * 
	 * @return true if the pattern is a regular expression, false if it is a
	 *         literal pattern
	 */
	public boolean isRegex() {
		return this.regex;
	}

	/**
	 * Sets whether the specified pattern is a regular expression or a literal
	 * pattern
	 * 
	 * @param regex
	 *            if true the pattern will be used as a regular expression, as a
	 *            literal pattern if false
	 * @throws NullPointerException
	 *             if the pattern is null or empty
	 * @throws PatternSyntaxException
	 *             if regex is true and the pattern isn't a valid regular
	 *             expression
	 */
	public void setRegex(boolean regex) throws NullPointerException, PatternSyntaxException {
		if (this.regex != regex) {
			this.regex = regex;
			checkConfiguration();
		}
	}
	
	/**
	 * Checks the rule's configuration
	 * 
	 * @throws NullPointerException
	 *             if the pattern is null or empty
	 * @throws PatternSyntaxException
	 *             if regex is true and the pattern isn't a valid regular
	 *             expression
	 */
	@Override
	protected void checkConfiguration() throws NullPointerException, PatternSyntaxException {
		if (this.textToSearch == null || this.textToSearch.isEmpty()) {
			super.setValid(false);
			return;
			
		} else if (this.regex) {
			Pattern.compile(this.textToSearch);
		}
		super.setValid(true);
	}

	@Override
	protected Rule createConfiguredRule() {
		return getRule(
				super.startCount,
				super.padding,
				this.beforePattern,
				this.textToSearch,
				this.regex,
				this.matchCase);
	}

	@Override
	protected void parseRuleSpecificParameters(String[] parameters) throws IllegalArgumentException {
		if (parameters.length == PARAMETERS_COUNT) {
			super.parseRuleSpecificParameters(parameters);
			
			this.beforePattern = intToBool(Integer.parseInt(parameters[PARAMETER_BEFORE_PATTERN]));
			this.textToSearch  = parameters[PARAMETER_TEXT_TO_SEARCH];
			this.matchCase     = intToBool(Integer.parseInt(parameters[PARAMETER_MATCH_CASE]));
			this.regex         = intToBool(Integer.parseInt(parameters[PARAMETER_REGEX]));

		} else {
			throw new IllegalArgumentException("Invalid parameter's array length: " + parameters.length);
		}
	}

	@Override
	protected String[] getRuleSpecificParameters() {
		String[] superParameters = super.getRuleSpecificParameters();
		String[] parameters = Arrays.copyOf(superParameters, PARAMETERS_COUNT);
		parameters[PARAMETER_BEFORE_PATTERN] = ""+boolToInt(this.beforePattern);
		parameters[PARAMETER_TEXT_TO_SEARCH] = this.textToSearch;
		parameters[PARAMETER_MATCH_CASE]     = ""+boolToInt(this.matchCase);
		parameters[PARAMETER_REGEX]          = ""+boolToInt(this.regex);
		return parameters;
	}
	
	/**
	 * This method based on the parameters values return the right rule
	 * 
	 * @param countStart
	 *            - counter starting value
	 * @param padding
	 *            - length of the string inserted, insert 0 for no padding
	 * @param beforePattern
	 *            - indicates where to insert counter's value, true to insert it
	 *            before the pattern, false to insert after
	 * @param textToSearch
	 *            - text/regex near which insert text
	 * @param regex
	 *            - this flag indicates that textToSearch is a regular
	 *            expression
	 * @return the right rule, based on input parameters
	 */
	public static Rule getRule(int countStart, int padding, boolean beforePattern, String textToSearch, boolean regex, boolean matchCase) {
		return beforePattern
				? new InsertCounterBeforeRule(countStart, padding, textToSearch, matchCase, regex)
				: new InsertCounterAfterRule(countStart, padding, textToSearch, matchCase, regex);
	}
	
	/**	Abstract insert counter rule, that configure the pattern to search */
	private abstract static class AbstractCounterPatternRule extends AbstractCounterRule {
		
		/**	Pattern to search */
		protected final Pattern pattern;
		
		/**
		 * Creates a rule and configure the pattern to search
		 * 
		 * @param countStart
		 *            - counter starting value
		 * @param padding
		 *            - length of the string inserted, insert 0 for no padding
		 * @param textToSearch
		 *            - text/regex near which insert text
		 * @param regex
		 *            - this flag indicates that textToSearch is a regular
		 *            expression
		 */
		public AbstractCounterPatternRule(int countStart, int padding, String textToSearch, boolean matchCase, boolean regex) {
			super(countStart, padding);
			if (matchCase) {
				this.pattern = Pattern.compile(regex ? textToSearch : Pattern.quote(textToSearch));
				
			} else {
				this.pattern = Pattern.compile(regex ? textToSearch : Pattern.quote(textToSearch), Pattern.CASE_INSENSITIVE);
			}
		}
	}
	
	/**	Rule that insert the counter's value before the specified pattern */
	private static class InsertCounterBeforeRule extends AbstractCounterPatternRule {
		
		/** @see AbstractCounterPatternRule#AbstractCounterPatternRule(int, int, String, boolean, boolean) */
		public InsertCounterBeforeRule(int countStart, int padding, String textToSearch, boolean matchCase, boolean regex) {
			super(countStart, padding, textToSearch, matchCase, regex);
		}
		
		@Override
		public FileModelItem apply(FileModelItem file) {
			Matcher matcher = super.pattern.matcher(file.getName());
			if (matcher.find()) {
				file.setName(file.getName().substring(0, matcher.start())
						+ getCount()
						+ file.getName().substring(matcher.start()));
			}
			return file;
		}
	}
	
	/**	Rule that insert the counter's value after the specified pattern */
	private static class InsertCounterAfterRule extends AbstractCounterPatternRule {
		
		/** @see AbstractCounterPatternRule#AbstractCounterPatternRule(int, int, String, boolean, boolean) */
		public InsertCounterAfterRule(int countStart, int padding, String textToSearch, boolean matchCase, boolean regex) {
			super(countStart, padding, textToSearch, matchCase, regex);
		}
		
		@Override
		public FileModelItem apply(FileModelItem file) {
			Matcher matcher = super.pattern.matcher(file.getName());
			if (matcher.find()) {
				file.setName(file.getName().substring(0, matcher.end())
						+ getCount()
						+ file.getName().substring(matcher.end()));
			}
			return file;
		}
	}
}