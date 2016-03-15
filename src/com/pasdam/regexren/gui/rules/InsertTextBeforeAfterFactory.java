package com.pasdam.regexren.gui.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.pasdam.regexren.model.FileModelItem;
import com.pasdam.regexren.model.RuleType;

/**
 * <p>
 * {@link AbstractRuleFactory} used to create rule to insert a text before/after
 * a specific pattern
 * </p>
 * <p>
 * To set the operation type, use one of the folloting fields:
 * </p>
 * <ul>
 * <li>{@link #BEFORE_ALL}</li>
 * <li>{@link #BEFORE_FIRST}</li>
 * <li>{@link #BEFORE_LAST}</li>
 * <li>{@link #AFTER_ALL}</li>
 * <li>{@link #AFTER_FIRST}</li>
 * <li>{@link #AFTER_LAST}</li>
 * </ul>
 * 
 * @author paco
 * @version 0.1
 */
public class InsertTextBeforeAfterFactory extends AbstractRuleFactory {

	/** Used to insert before all occurrence of input text */
	public static final int BEFORE_ALL   = 0;
	/** Used to insert before only the first occurrence of input text */
	public static final int BEFORE_FIRST = BEFORE_ALL   + 1;
	/** Used to insert before only the last occurrence of input text */
	public static final int BEFORE_LAST  = BEFORE_FIRST + 1;
	/** Used to insert after all occurrence of input text */
	public static final int AFTER_ALL    = BEFORE_LAST  + 1;
	/** Used to insert after only the first occurrence of input text */
	public static final int AFTER_FIRST  = AFTER_ALL    + 1;
	/** Used to insert after only the last occurrence of input text */
	public static final int AFTER_LAST   = AFTER_FIRST  + 1;

	/** Index of the "text to insert" parameter */
	public static final int PARAMETER_TEXT_TO_INSERT = 0;
	/** Index of the "beforePattern" parameter */
	public static final int PARAMETER_BEFORE_PATTERN = PARAMETER_TEXT_TO_INSERT + 1;
	/** Index of the "textToSearch" parameter */
	public static final int PARAMETER_TEXT_TO_SEARCH = PARAMETER_BEFORE_PATTERN + 1;
	/** Index of the "matchCase" parameter */
	public static final int PARAMETER_MATCH_CASE     = PARAMETER_TEXT_TO_SEARCH + 1;
	/** Index of the "regex" parameter */
	public static final int PARAMETER_REGEX          = PARAMETER_MATCH_CASE     + 1;
	/** Indicates how many parameters this component has */
	public static final int PARAMETERS_COUNT         = PARAMETER_REGEX          + 1;

	/** Text to insert */
	private String textToInsert;

	/**
	 * Indicates whether the pattern should be inserted before the searched
	 * pattern or after
	 */
	private int beforeAfterType;

	/** Pattern to find */
	private String textToSearch;

	/** Indicates whether the pattern should match case or not */
	private boolean matchCase;

	/** Indicates if the pattern is a regular expression or a literal one */
	private boolean regex;

	/** Creates a {@link InsertTextAtPositionFactory} */
	public InsertTextBeforeAfterFactory() {
		super(RuleType.INSERT_TEXT_BEFORE_AFTER_PATTERN);
		super.setValid(false);
	}

	/**
	 * Returns the text to insert
	 * 
	 * @return the text to insert
	 */
	public String getTextToInsert() {
		return this.textToInsert;
	}

	/**
	 * Sets the text to insert
	 * 
	 * @param textToInsert
	 *            text to insert
	 * @throws InvalidParametersException
	 *             if one ore more parameters are invalid
	 */
	public void setTextToInsert(String textToInsert) throws InvalidParametersException {
		this.textToInsert = textToInsert;
		checkConfiguration();
	}

	/**
	 * Returns the operation type
	 * 
	 * @return the operation type
	 */
	public int getBeforeAfterType() {
		return this.beforeAfterType;
	}

	/**
	 * Sets whether the pattern should be inserted before the searched pattern
	 * or after
	 * 
	 * @param type
	 *            operation type
	 */
	public void setBeforeAfterType(int type) {
		this.beforeAfterType = type;
		super.configurationChanged();
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
	 * @throws InvalidParametersException
	 *             if one ore more parameters are invalid
	 */
	public void setTextToSearch(String textToSearch) throws InvalidParametersException {
		this.textToSearch = textToSearch;
		checkConfiguration();
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
		this.matchCase = matchCase;
		super.configurationChanged();
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
	 * @throws InvalidParametersException
	 *             if one ore more parameters are invalid
	 */
	public void setRegex(boolean regex) throws InvalidParametersException {
		this.regex = regex;
		checkConfiguration();
	}

	/**
	 * Checks the rule's configuration
	 * 
	 * @throws InvalidParametersException
	 *             if one ore more parameters are invalid
	 */
	protected void checkConfiguration() throws InvalidParametersException {
		List<Integer> invalidParameters = new ArrayList<Integer>(PARAMETERS_COUNT);

		if (this.textToInsert == null || this.textToInsert.isEmpty()) {
			invalidParameters.add(PARAMETER_TEXT_TO_INSERT);

		}
		
		if (this.textToSearch == null || this.textToSearch.isEmpty()) {
			invalidParameters.add(PARAMETER_TEXT_TO_SEARCH);

		} else if (this.regex) {
			try {
				Pattern.compile(this.textToSearch);
			} catch (PatternSyntaxException exception) {
				invalidParameters.add(PARAMETER_TEXT_TO_SEARCH);
			}
		}
		
		if (invalidParameters.size() > 0) {
			super.setValid(false);
			throw new InvalidParametersException(invalidParameters, "One or more paramters are invalid");
			
		} else {
			super.setValid(true);
		}
	}

	@Override
	protected Rule createConfiguredRule() {
		return getRule(this.textToInsert, this.beforeAfterType, this.textToSearch, this.regex, this.matchCase);
	}

	@Override
	protected void parseRuleSpecificParameters(String[] parameters) throws IllegalArgumentException {
		if (parameters.length == PARAMETERS_COUNT) {
			this.textToInsert = parameters[PARAMETER_TEXT_TO_INSERT];
			this.beforeAfterType = Integer.parseInt(parameters[PARAMETER_BEFORE_PATTERN]);
			this.textToSearch = parameters[PARAMETER_TEXT_TO_SEARCH];
			this.matchCase = intToBool(Integer.parseInt(parameters[PARAMETER_MATCH_CASE]));
			this.regex = intToBool(Integer.parseInt(parameters[PARAMETER_REGEX]));

		} else {
			throw new IllegalArgumentException("Invalid parameter's array length: " + parameters.length);
		}
	}

	@Override
	protected String[] getRuleSpecificParameters() {
		String[] parameters = new String[PARAMETERS_COUNT];
		parameters[PARAMETER_TEXT_TO_INSERT] = this.textToInsert;
		parameters[PARAMETER_BEFORE_PATTERN] = "" + this.beforeAfterType;
		parameters[PARAMETER_TEXT_TO_SEARCH] = this.textToSearch;
		parameters[PARAMETER_MATCH_CASE] = "" + boolToInt(this.matchCase);
		parameters[PARAMETER_REGEX] = "" + boolToInt(this.regex);
		return parameters;
	}

	/**
	 * This method based on the parameters values return the configured rule
	 * 
	 * @param textToInsert
	 *            - text to insert
	 * @param beforeAfter
	 *            - this sets where to insert text; use class fields to set this
	 * @param textToSearch
	 *            - text/regex near which insert text
	 * @param regex
	 *            - this flag indicates that textToSearch is a regular
	 *            expression
	 * @param matchCase
	 *            - if true textToSearch is case sensitive
	 * @return the configured rule, based on input parameters
	 * @throws IllegalArgumentException
	 *             if <i>beforeAfter</i> is invalid, or one of
	 *             <i>textToInsert</i> or <i>textToSearch</i> is null or empty
	 */
	public static Rule getRule(String textToInsert, int beforeAfter, String textToSearch, boolean regex,
			boolean matchCase) throws IllegalArgumentException {
		if (textToInsert != null && !textToInsert.isEmpty()) {
			if (textToSearch != null && !textToSearch.isEmpty()) {
				switch (beforeAfter) {
				case BEFORE_ALL:
					return new InsertBeforeAll(textToInsert, textToSearch, matchCase, regex);

				case BEFORE_FIRST:
					return new InsertBeforeFirst(textToInsert, textToSearch, matchCase, regex);

				case BEFORE_LAST:
					return new InsertBeforeLast(textToInsert, textToSearch, matchCase, regex);

				case AFTER_ALL:
					return new InsertAfterAll(textToInsert, textToSearch, matchCase, regex);

				case AFTER_FIRST:
					return new InsertAfterFirst(textToInsert, textToSearch, matchCase, regex);

				case AFTER_LAST:
					return new InsertAfterLast(textToInsert, textToSearch, matchCase, regex);

				default:
					throw new IllegalArgumentException("Invalid before/after value: " + beforeAfter
							+ ". Use the class fiels to set this parameter.");
				}

			} else {
				throw new IllegalArgumentException("Invalid textToSearch parameter, it can't be null or empty.");
			}
		} else {
			throw new IllegalArgumentException("Invalid textToInsert parameter, it can't be null or empty");
		}
	}

	/** Abstract base class for "Insert text before/after..." rules */
	private abstract static class AbstractInsertRule implements Rule {

		/** Text to insert */
		protected final String textToInsert;

		/** Pattern to find */
		protected final Pattern pattern;

		/**
		 * Creates a {@link AbstractInsertRule} with the specified parameters
		 * 
		 * @param textToInsert
		 *            - text to insert
		 * @param textToSearch
		 *            - text/regex near which insert text
		 * @param matchCase
		 *            - if true textToSearch is case sensitive
		 * @param regex
		 *            - this flag indicates that textToSearch is a regular
		 *            expression
		 */
		public AbstractInsertRule(String textToInsert, String textToSearch, boolean matchCase, boolean regex) {
			this.textToInsert = textToInsert;
			this.pattern = matchCase 
					? Pattern.compile(regex ? textToSearch : Pattern.quote(textToSearch))
					: Pattern.compile(regex ? textToSearch : Pattern.quote(textToSearch), Pattern.CASE_INSENSITIVE);
		}

		@Override
		public void reset() {}
	}

	/** Rule to insert text before all occurrences of the specified pattern */
	private static class InsertBeforeAll extends AbstractInsertRule {

		/**
		 * @see AbstractInsertRule#AbstractInsertRule(String, String, boolean,
		 *      boolean)
		 */
		public InsertBeforeAll(String textToInsert, String textToSearch, boolean matchCase, boolean regex) {
			super(textToInsert, textToSearch, matchCase, regex);
		}

		@Override
		public FileModelItem apply(FileModelItem fileModelItem) {
			Matcher matcher = super.pattern.matcher(fileModelItem.getName());
			StringBuilder builder = new StringBuilder(super.textToInsert.length() + fileModelItem.getName().length());
			int previousEndIndex = 0;
			while (matcher.find()) {
				builder.append(fileModelItem.getName().substring(previousEndIndex, matcher.start()));
				builder.append(super.textToInsert);
				previousEndIndex = matcher.start();
			}
			if (builder.length() > 0) {
				fileModelItem.setName(builder.toString() + fileModelItem.getName().substring(previousEndIndex));
			}
			return fileModelItem;
		}
	}

	/** Rule to insert text before first occurrence of the specified pattern */
	private static class InsertBeforeFirst extends AbstractInsertRule {

		/** @see AbstractInsertRule#AbstractInsertRule(String, String, boolean, boolean) */
		public InsertBeforeFirst(String textToInsert, String textToSearch, boolean matchCase, boolean regex) {
			super(textToInsert, textToSearch, matchCase, regex);
		}

		@Override
		public FileModelItem apply(FileModelItem fileModelItem) {
			Matcher matcher = super.pattern.matcher(fileModelItem.getName());
			if (matcher.find()) {
				fileModelItem.setName(fileModelItem.getName().substring(0, matcher.start()) + super.textToInsert
						+ fileModelItem.getName().substring(matcher.start()));
			}
			return fileModelItem;
		}
	}

	/** Rule to insert text before last occurrence of the specified pattern */
	private static class InsertBeforeLast extends AbstractInsertRule {

		/**
		 * @see AbstractInsertRule#AbstractInsertRule(String, String, boolean,
		 *      boolean)
		 */
		public InsertBeforeLast(String textToInsert, String textToSearch, boolean matchCase, boolean regex) {
			super(textToInsert, textToSearch, matchCase, regex);
		}

		@Override
		public FileModelItem apply(FileModelItem fileModelItem) {
			Matcher matcher = super.pattern.matcher(fileModelItem.getName());
			int index = -1;
			while (matcher.find()) {
				index = matcher.start();
			}
			if (index > -1) {
				fileModelItem.setName(fileModelItem.getName().substring(0, index) + super.textToInsert
						+ fileModelItem.getName().substring(index));
			}
			return fileModelItem;
		}
	}

	/** Rule to insert text after all occurrences of the specified pattern */
	private static class InsertAfterAll extends AbstractInsertRule {

		/**
		 * @see AbstractInsertRule#AbstractInsertRule(String, String, boolean,
		 *      boolean)
		 */
		public InsertAfterAll(String textToInsert, String textToSearch, boolean matchCase, boolean regex) {
			super(textToInsert, textToSearch, matchCase, regex);
		}

		@Override
		public FileModelItem apply(FileModelItem fileModelItem) {
			Matcher matcher = super.pattern.matcher(fileModelItem.getName());
			StringBuilder builder = new StringBuilder(super.textToInsert.length() + fileModelItem.getName().length());
			int previousEndIndex = 0;
			while (matcher.find()) {
				builder.append(fileModelItem.getName().substring(previousEndIndex, matcher.end()));
				builder.append(super.textToInsert);
				previousEndIndex = matcher.end();
			}
			if (builder.length() > 0) {
				fileModelItem.setName(builder.toString() + fileModelItem.getName().substring(previousEndIndex));
			}
			return fileModelItem;
		}
	}

	/** Rule to insert text after first occurrence of the specified pattern */
	private static class InsertAfterFirst extends AbstractInsertRule {

		/**
		 * @see AbstractInsertRule#AbstractInsertRule(String, String, boolean,
		 *      boolean)
		 */
		public InsertAfterFirst(String textToInsert, String textToSearch, boolean matchCase, boolean regex) {
			super(textToInsert, textToSearch, matchCase, regex);
		}

		@Override
		public FileModelItem apply(FileModelItem fileModelItem) {
			Matcher matcher = super.pattern.matcher(fileModelItem.getName());
			if (matcher.find()) {
				fileModelItem.setName(fileModelItem.getName().substring(0, matcher.end()) + super.textToInsert
						+ fileModelItem.getName().substring(matcher.end()));
			}
			return fileModelItem;
		}
	}

	/** Rule to insert text after last occurrence of the specified pattern */
	private static class InsertAfterLast extends AbstractInsertRule {

		/**
		 * @see AbstractInsertRule#AbstractInsertRule(String, String, boolean,
		 *      boolean)
		 */
		public InsertAfterLast(String textToInsert, String textToSearch, boolean matchCase, boolean regex) {
			super(textToInsert, textToSearch, matchCase, regex);
		}

		@Override
		public FileModelItem apply(FileModelItem fileModelItem) {
			Matcher matcher = super.pattern.matcher(fileModelItem.getName());
			int previousEndIndex = -1;
			while (matcher.find()) {
				previousEndIndex = matcher.end();
			}
			if (previousEndIndex > -1) {
				fileModelItem.setName(fileModelItem.getName().substring(0, previousEndIndex) + super.textToInsert
						+ fileModelItem.getName().substring(previousEndIndex));
			}
			return fileModelItem;
		}
	}
}