package com.pasdam.regexren.gui.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.pasdam.regexren.model.FileModelItem;
import com.pasdam.regexren.model.RuleType;

/**
 * <p>
 * {@link AbstractRuleFactory} used to create rule to move a text
 * </p>
 * 
 * @author paco
 * @version 0.1
 */
public class MoveTextBeforeAfterFactory extends AbstractRuleFactory {
	
	/** Creates a {@link MoveTextBeforeAfterFactory} */
	public MoveTextBeforeAfterFactory() {
		super(RuleType.MOVE);
		setValid(false);
	}

	/** Used to move the first occurrence of input text before the first occurrence of the specified pattern*/
	public static final int POSITION_BEFORE    = 0;
	/** Used to move the first occurrence of input text after the first occurrence of the specified pattern*/
	public static final int POSITION_AFTER = POSITION_BEFORE + 1;
	/** Used to move the input text at begin */
	public static final int POSITION_BEGIN = POSITION_AFTER  + 1;
	/** Used to move the input text at end */
	public static final int POSITION_END   = POSITION_BEGIN  + 1;
	
	/** Index of the "textToMove" parameter */
	private static final int PARAMETER_TEXT_TO_MOVE   = 0;
	/** Index of the "textToMove" parameter */
	private static final int PARAMETER_POSITION       = PARAMETER_TEXT_TO_MOVE   + 1;
	/** Index of the "textToSearch" parameter */
	private static final int PARAMETER_TEXT_TO_SEARCH = PARAMETER_POSITION       + 1;
	/** Index of the "matchCase" parameter */
	private static final int PARAMETER_MATCH_CASE     = PARAMETER_TEXT_TO_SEARCH + 1;
	/** Index of the "regex" parameter */
	private static final int PARAMETER_REGEX          = PARAMETER_MATCH_CASE     + 1;
	/** Indicates how many parameters this component has */
	private static final int PARAMETERS_COUNT         = PARAMETER_REGEX          + 1;

	/** Text to move */
	private String textToMove;
	
	/** Position where move the text */
	private int position;
	
	/** Pattern near which insert the text */
	private String textToSearch;
	
	/** Indicates whether textToSearch and textToMove are regular expression or literal patterns */
	private boolean regex;
	
	/** Indicates whether the pattern is case sensitive or not */
	private boolean matchCase;
	
	/**
	 * Returns the text to move
	 * 
	 * @return the text to move
	 */
	public String getTextToMove() {
		return this.textToMove;
	}

	/**
	 * Sets the text to move
	 * 
	 * @param textToMove
	 *            the text to move
	 * @throws NullPointerException
	 *             if the pattern is null or empty
	 * @throws PatternSyntaxException
	 *             if regex is true and the pattern isn't a valid regular
	 *             expression
	 * @throws IllegalArgumentException
	 *             if text to move is null or empty
	 */
	public void setTextToMove(String textToMove) throws NullPointerException, PatternSyntaxException, IllegalArgumentException {
		this.textToMove = textToMove;
		checkConfiguration();
	}

	/**
	 * Returns the position where move the text
	 * 
	 * @return the position where move the text
	 */
	public int getPosition() {
		return this.position;
	}

	/**
	 * Sets the position where move the text
	 * 
	 * @param position
	 *            the position where move the text
	 */
	public void setPosition(int position) {
		this.position = position > 0 ? position : 0;
		super.configurationChanged();
	}

	/**
	 * Returns the pattern near which insert the text
	 * 
	 * @return the pattern near which insert the text
	 */
	public String getTextToSearch() {
		return this.textToSearch;
	}

	/**
	 * Sets the pattern near which insert the text
	 * 
	 * @param textToSearch
	 *            the pattern near which insert the text
	 * @throws NullPointerException
	 *             if the pattern is null or empty
	 * @throws PatternSyntaxException
	 *             if regex is true and the pattern isn't a valid regular
	 *             expression
	 * @throws IllegalArgumentException
	 *             if text to move is null or empty
	 */
	public void setTextToSearch(String textToSearch) throws NullPointerException, PatternSyntaxException, IllegalArgumentException {
		this.textToSearch = textToSearch;
		checkConfiguration();
	}

	/**
	 * Returns true if textToSearch is a regular expression, false if it is a
	 * literal pattern
	 * 
	 * @return true if textToSearch is a regular expression, false if it is a
	 *         literal pattern
	 */
	public boolean isRegex() {
		return this.regex;
	}

	/**
	 * Sets whether textToSearch is a regular expression or a literal pattern
	 * 
	 * @param regex
	 *            true if textToSearch is a regular expression, false if it is a
	 *            literal pattern
	 * @throws NullPointerException
	 *             if the pattern is null or empty
	 * @throws PatternSyntaxException
	 *             if regex is true and the pattern isn't a valid regular
	 *             expression
	 * @throws IllegalArgumentException
	 *             if text to move is null or empty
	 */
	public void setRegex(boolean regex) throws NullPointerException, PatternSyntaxException, IllegalArgumentException {
		this.regex = regex;
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
	 *            true if the pattern is case sensitive, false otherwise
	 */
	public void setMatchCase(boolean matchCase) {
		this.matchCase = matchCase;
		super.configurationChanged();
	}
	
	/**
	 * Checks the rule's configuration
	 * 
	 * @throws NullPointerException
	 *             if the pattern is null or empty
	 * @throws PatternSyntaxException
	 *             if regex is true and the pattern isn't a valid regular
	 *             expression
	 * @throws IllegalArgumentException
	 *             if text to move is null or empty
	 */
	private void checkConfiguration() throws NullPointerException, PatternSyntaxException, IllegalArgumentException {
		super.setValid(false);
		if (this.textToMove == null || this.textToMove.isEmpty()) {
			throw new IllegalArgumentException("Invalid parameter, textToMove cannot be null or empty");

		} else if (this.textToSearch == null || this.textToSearch.isEmpty()) {
			throw new NullPointerException("Invalid parameter, textToSearch cannot be null or empty");

		} else if (this.regex) {
			Pattern.compile(this.textToSearch);
		}
		super.setValid(true);
	}

	@Override
	protected Rule createConfiguredRule() {
		return getRule(this.textToMove, this.position, this.textToSearch, this.regex, this.matchCase);
	}

	@Override
	protected void parseRuleSpecificParameters(String[] parameters) throws IllegalArgumentException {
		if (parameters.length == PARAMETERS_COUNT) {
			this.textToMove   = parameters[PARAMETER_TEXT_TO_MOVE];
			this.position     = Integer.parseInt(parameters[PARAMETER_POSITION]);
			this.textToSearch = parameters[PARAMETER_TEXT_TO_SEARCH];
			this.matchCase    = intToBool(Integer.parseInt(parameters[PARAMETER_MATCH_CASE]));
			this.regex        = intToBool(Integer.parseInt(parameters[PARAMETER_REGEX]));

		} else {
			throw new IllegalArgumentException("Invalid parameter's array length: " + parameters.length);
		}
	}

	@Override
	protected String[] getRuleSpecificParameters() {
		String[] parameters = new String[PARAMETERS_COUNT];
		parameters[PARAMETER_TEXT_TO_MOVE]   = this.textToMove;
		parameters[PARAMETER_POSITION]       = "" + this.position;
		parameters[PARAMETER_TEXT_TO_SEARCH] = this.textToSearch;
		parameters[PARAMETER_MATCH_CASE]     = "" + boolToInt(this.matchCase);
		parameters[PARAMETER_REGEX]          = "" + boolToInt(this.regex);
		return parameters;
	}

	/**
	 * This method based on the parameters values return the right rule
	 * 
	 * @param textToMove
	 *            - text to move
	 * @param position
	 *            - this sets where to insert text; use
	 *            {@link MoveTextBeforeAfterFactory} static fields to set this
	 * @param textToSearch
	 *            - text/regex near which insert text
	 * @param regex
	 *            - this flag indicates if textToSearch and textToMove are
	 *            regular expression or literal patterns
	 * @param matchCase
	 *            - if true textToSearch is case sensitive; used only if regex
	 *            is false
	 * @param occurrence
	 *            - which occurrence of textToSearch; use
	 *            MoveTextBeforeAfterPanel constraints to set this
	 * @return the right rule, based on input parameters
	 * @throws IllegalArgumentException
	 *             if <i>textToSearch</i> or <i>textToMove</i> are null or
	 *             empty, or if <i>position</i> value is invalid
	 */
	public static Rule getRule(String textToMove, int position, String textToSearch, boolean regex, boolean matchCase) throws IllegalArgumentException {
		if (textToMove != null && !textToMove.isEmpty()) {
			if (textToSearch != null && !textToSearch.isEmpty()) {
				switch (position) {
					case POSITION_BEFORE:
						return new MoveBefore(textToMove, textToSearch, regex, matchCase);
					
					case POSITION_AFTER:
						return new MoveAfter(textToMove, textToSearch, regex, matchCase);
					
					case POSITION_BEGIN:
						return new MoveAtBeginning(textToMove, regex, matchCase);
					
					case POSITION_END:
						return new MoveAtEnding(textToMove, regex, matchCase);
					
					default:
						throw new IllegalArgumentException("Invalid position value: " + position + ". Use the class static fields to set this parameter.");
				}

			} else {
				throw new IllegalArgumentException("Invalid textToSearch parameter, it can't be null or empty.");
			}
		} else {
			throw new IllegalArgumentException("Invalid textToMove parameter, it can't be null or empty");
		}
	}
	
	/** Abstract base class for "Move text..." rules
	 */
	private abstract static class AbstractMoveRule implements Rule {
		
		/** Pattern to move */
		protected final Pattern patternToMove;
		
		/**
		 * Creates a {@link AbstractMoveRule} with the specified parameters
		 * 
		 * @param textToMove
		 *            - text/regex to move
		 * @param matchCase
		 *            - if true textToSearch is case sensitive
		 * @param regex
		 *            - this flag indicates if textToSearch and textToMove are regular
		 *            expression or literal patterns
		 */
		public AbstractMoveRule(String textToMove, boolean regex, boolean matchCase) {
			this.patternToMove = matchCase 
					? Pattern.compile(regex ? textToMove : Pattern.quote(textToMove))
					: Pattern.compile(regex ? textToMove : Pattern.quote(textToMove), Pattern.CASE_INSENSITIVE);
		}
		
		@Override
		public void reset() {}
	}
	
	/** Abstract base class for "Move text..." rules */
	private abstract static class AbstractMoveBeforeAfterRule extends AbstractMoveRule {
		
		/** Pattern to find */
		protected final Pattern patternToSearch;
		
		/**
		 * Creates a {@link AbstractMoveRule} with the specified parameters
		 * 
		 * @param textToMove
		 *            - text/regex to move
		 * @param textToSearch
		 *            - text/regex near which insert text
		 * @param matchCase
		 *            - if true textToSearch is case sensitive
		 * @param regex
		 *            - this flag indicates if textToSearch and textToMove are regular
		 *            expression or literal patterns
		 */
		public AbstractMoveBeforeAfterRule(String textToMove, String textToSearch, boolean regex, boolean matchCase) {
			super(textToMove, regex, matchCase);
			this.patternToSearch = matchCase 
					? Pattern.compile(regex ? textToSearch : Pattern.quote(textToSearch))
					: Pattern.compile(regex ? textToSearch : Pattern.quote(textToSearch), Pattern.CASE_INSENSITIVE);
		}
	}
	
	/**	Rule that move text before a specific pattern */
	private static class MoveBefore extends AbstractMoveBeforeAfterRule {
		
		/** @see AbstractMoveBeforeAfterRule#AbstractMoveBeforeAfterRule(String, String, boolean, boolean) */
		public MoveBefore(String textToMove, String textToSearch, boolean regex, boolean matchCase) {
			super(textToMove, textToSearch, regex, matchCase);
		}
		
		@Override
		public FileModelItem apply(FileModelItem fileModelItem) {
			Matcher matcherToMove = super.patternToMove.matcher(fileModelItem.getName());
			Matcher matcherToSearch = super.patternToSearch.matcher(matcherToMove.replaceFirst(""));
			if (matcherToSearch.find() && matcherToMove.find()) {
				fileModelItem.setName(fileModelItem.getName().substring(0, matcherToSearch.start())
									+ matcherToMove.group()
									+ fileModelItem.getName().substring(matcherToSearch.start()));
			}
			return fileModelItem;
		}
	}
	
	/**	Rule that move text after a specific pattern */
	private static class MoveAfter extends AbstractMoveBeforeAfterRule {
		
		/** @see AbstractMoveBeforeAfterRule#AbstractMoveBeforeAfterRule(String, String, boolean, boolean) */
		public MoveAfter(String textToMove, String textToSearch, boolean regex, boolean matchCase) {
			super(textToMove, textToSearch, regex, matchCase);
		}
		
		@Override
		public FileModelItem apply(FileModelItem fileModelItem) {
			Matcher matcherToMove = super.patternToMove.matcher(fileModelItem.getName());
			final String name = matcherToMove.replaceFirst("");
			Matcher matcherToSearch = super.patternToSearch.matcher(name);
			if (matcherToSearch.find() && matcherToMove.find()) {
				fileModelItem.setName(fileModelItem.getName().substring(0, matcherToSearch.end())
									+ matcherToMove.group()
									+ fileModelItem.getName().substring(matcherToSearch.end()));
			}
			return fileModelItem;
		}
	}

	/**	Role that move the first occurrence of the input pattern at begin of the name */
	private static class MoveAtBeginning extends AbstractMoveRule {
		
		/** @see AbstractMoveRule#AbstractMoveRule(String, boolean, boolean) */
		public MoveAtBeginning(String textToMove, boolean regex, boolean matchCase) {
			super(textToMove, regex, matchCase);
		}
		
		@Override
		public FileModelItem apply(FileModelItem fileModelItem) {
			Matcher matcherToMove = super.patternToMove.matcher(fileModelItem.getName());
			if (matcherToMove.find()) {
				String group              = matcherToMove.group();
				String textWithoutPattern = matcherToMove.replaceFirst("");
				fileModelItem.setName(group + textWithoutPattern);
			}
			return fileModelItem;
		}
	}
	
	/**	Role that move the first occurrence of the input pattern at end of the name */
	private static class MoveAtEnding extends AbstractMoveRule {
		
		/** @see AbstractMoveRule#AbstractMoveRule(String, boolean, boolean) */
		public MoveAtEnding(String textToMove, boolean regex, boolean matchCase) {
			super(textToMove, regex, matchCase);
		}
		
		@Override
		public FileModelItem apply(FileModelItem fileModelItem) {
			Matcher matcherToMove = super.patternToMove.matcher(fileModelItem.getName());
			if (matcherToMove.find()) {
				String group              = matcherToMove.group();
				String textWithoutPattern = matcherToMove.replaceFirst("");
				fileModelItem.setName(textWithoutPattern + group);
			}
			return fileModelItem;
		}
	}
}