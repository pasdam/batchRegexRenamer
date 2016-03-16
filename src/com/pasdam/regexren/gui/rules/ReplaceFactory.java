package com.pasdam.regexren.gui.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.pasdam.regexren.controller.LogManager;
import com.pasdam.regexren.model.FileModelItem;
import com.pasdam.regexren.model.RuleType;

/**
 * {@link AbstractRuleFactory} used to create rule to replace/remove pattern from name/extension
 * 
 * @author paco
 * @version 0.1
 */
public class ReplaceFactory extends AbstractRuleFactory {
	
	/** Used to specify that the filter replace all occurrences in the name */
	public static final int TARGET_NAME_ALL        = 0;
	/** Used to specify that the filter replace first occurrence in the name */
	public static final int TARGET_NAME_FIRST      = TARGET_NAME_ALL + 1;
	/** Used to specify that the filter replace last occurrence in the name */
	public static final int TARGET_NAME_LAST       = TARGET_NAME_FIRST + 1;
	/** Used to specify that the filter replace all occurrences in the extension */
	public static final int TARGET_EXTENSION_ALL   = TARGET_NAME_LAST + 1;
	/** Used to specify that the filter replace first occurrence in the extension */
	public static final int TARGET_EXTENSION_FIRST = TARGET_EXTENSION_ALL + 1;
	/** Used to specify that the filter replace last occurrence in the extension */
	public static final int TARGET_EXTENSION_LAST  = TARGET_EXTENSION_FIRST + 1;
	
	/** Index of the "text_to_replace" parameter */
	private static final int PARAMETER_TEXT_TO_REPLACE = 0; 
	/** Index of the "text_to_insert" parameter */
	private static final int PARAMETER_TEXT_TO_INSERT  = PARAMETER_TEXT_TO_REPLACE + 1;
	/** Index of the "target" parameter */
	private static final int PARAMETER_TARGET          = PARAMETER_TEXT_TO_INSERT  + 1;
	/** Index of the "start_index" parameter */
	private static final int PARAMETER_START_INDEX     = PARAMETER_TARGET       	  + 1;
	/** Index of the "end_index" parameter */
	private static final int PARAMETER_END_INDEX       = PARAMETER_START_INDEX     + 1;
	/** Index of the "match_case" parameter */
	private static final int PARAMETER_MATCH_CASE      = PARAMETER_END_INDEX       + 1;
	/** Index of the "regex" parameter */
	private static final int PARAMETER_REGEX           = PARAMETER_MATCH_CASE      + 1;
	/** Indicates how many parameters this component has */
	private static final int PARAMETERS_COUNT          = PARAMETER_REGEX           + 1;
	
	/** Indicates whether the pattern should match case or not */
	private boolean matchCase;

	/** Indicates if the pattern is a regular expression or a literal one */
	private boolean regex;

	/** Indicates the end index of the replace operation */
	private int endIndex;

	/** Indicates the start index of the replace operation */
	private int startIndex;

	/**	Indicates the target of the operation (name/extension) */
	private int target;

	/** Indicates the text to insert */
	private String textToInsert;

	/** Pattern to find and replace */
	private String textToReplace;
	
	/**
	 * Creates a {@link ReplaceFactory}
	 * 
	 * @param removeOnly
	 *            if true indicates that the instance refers to a remove rule,
	 *            instead a relace one
	 */
	public ReplaceFactory(boolean removeOnly) {
		super(removeOnly ? RuleType.REMOVE : RuleType.REPLACE);
		
		setValid(false);
		
		// set default values
		this.matchCase  = this.regex = false;
		this.target     = TARGET_NAME_ALL;
		this.startIndex = 0;
		this.endIndex   = 100;
	}
	
	/** Creates a {@link ReplaceFactory} */
	public ReplaceFactory() {
		this(false);
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
	 * Sets if the pattern is case sensitive
	 * 
	 * @param matchCase
	 *            true to indicate that the pattern is case sensitive, false
	 *            otherwise
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
	 * Sets whether the pattern is a regular expression or a literal pattern
	 * 
	 * @param regex
	 *            true to indicate that the pattern is a literal expression,
	 *            false to indicate a literal pattern
	 * @throws PatternSyntaxException
	 *             if regex it true and the sentence separator is an invalid
	 *             regular expression
	 * @throws NullPointerException
	 *             if regex is true and the sentence separator is null
	 */
	public void setRegex(boolean regex) throws PatternSyntaxException, NullPointerException {
		this.regex = regex;
		super.configurationChanged();
		checkConfiguration();
	}

	/**
	 * Returns the end index of the replace operation
	 * 
	 * @return the end index of the replace operation
	 */
	public int getEndIndex() {
		return this.endIndex;
	}

	/**
	 * Sets the end index ot the replace operation
	 * 
	 * @param endIndex
	 *            index (exclusive) of the character at which terminate the
	 *            replace operation; a value less then or equal to startIndex or
	 *            greater then text length means no limit
	 */
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
		super.configurationChanged();
	}

	/**
	 * Returns the start index of the replace operation
	 * 
	 * @return the start index of the replace operation
	 */
	public int getStartIndex() {
		return this.startIndex;
	}

	/**
	 * Sets the start index of the replace operation
	 * 
	 * @param startIndex
	 *            index (inclusive) of the character from which start the
	 *            replace operation
	 */
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
		super.configurationChanged();
	}

	/**
	 * Returns the target of the renaming operation (name/extension)
	 * 
	 * @return the target of the renaming operation (name/extension)
	 */
	public int getTarget() {
		return this.target;
	}

	/**
	 * Sets the target of the operation ({@link #TARGET_NAME}, {@link #TARGET_EXTENSION})
	 * 
	 * @param target
	 *            the target of the operation
	 */
	public void setTarget(int target) {
		this.target = target;
		super.configurationChanged();
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
	 */
	public void setTextToInsert(String textToInsert) {
		this.textToInsert = textToInsert;
		super.configurationChanged();
	}

	/**
	 * Returns the text to find and replace
	 * 
	 * @return the text to find and replace
	 */
	public String getTextToReplace() {
		return this.textToReplace;
	}

	/**
	 * Sets the text to find and replace
	 * 
	 * @param textToReplace
	 *            text to replace
	 * @throws PatternSyntaxException
	 *             if regex it true and the sentence separator is an invalid
	 *             regular expression
	 * @throws NullPointerException
	 *             if regex is true and the sentence separator is null
	 */
	public void setTextToReplace(String textToReplace) throws PatternSyntaxException, NullPointerException {
		this.textToReplace = textToReplace;
		super.configurationChanged();
		checkConfiguration();
	}
	
	/**
	 * Checks the configuration of the rule
	 * 
	 * @throws PatternSyntaxException
	 *             if regex it true and the sentence separator is an invalid
	 *             regular expression
	 * @throws NullPointerException
	 *             if regex is true and the sentence separator is null
	 */
	@Override
	protected void checkConfiguration() throws PatternSyntaxException, NullPointerException {
		if (this.regex) {
			setValid(false);
			Pattern.compile(this.textToReplace);
		}
		setValid(true);
	}

	@Override
	protected Rule createConfiguredRule() {
		return getRule(
				this.textToReplace,
				this.regex,
				this.matchCase,
				this.textToInsert,
				this.startIndex,
				this.endIndex,
				this.target);
	}

	@Override
	protected void parseRuleSpecificParameters(String[] parameters) throws IllegalArgumentException {
		if (parameters.length == PARAMETERS_COUNT) {
			this.textToReplace = parameters[PARAMETER_TEXT_TO_REPLACE];
			this.textToInsert  = parameters[PARAMETER_TEXT_TO_INSERT];
			this.target        = Integer.parseInt(parameters[PARAMETER_TARGET]);
			this.startIndex    = Integer.parseInt(parameters[PARAMETER_START_INDEX]);
			this.endIndex      = Integer.parseInt(parameters[PARAMETER_END_INDEX]);
			this.matchCase     = intToBool(Integer.parseInt(parameters[PARAMETER_MATCH_CASE]));
			this.regex         = intToBool(Integer.parseInt(parameters[PARAMETER_REGEX]));
		} else {
			throw new IllegalArgumentException("Invalid parameter's array length: " + parameters.length);
		}
	}

	@Override
	protected String[] getRuleSpecificParameters() {
		String[] parameters = new String[PARAMETERS_COUNT];
		parameters[PARAMETER_TEXT_TO_REPLACE] = this.textToReplace;
		parameters[PARAMETER_TEXT_TO_INSERT]  = this.textToInsert;
		parameters[PARAMETER_TARGET]          = ""+this.target;
		parameters[PARAMETER_START_INDEX]     = ""+this.startIndex;
		parameters[PARAMETER_END_INDEX]       = ""+this.endIndex;
		parameters[PARAMETER_MATCH_CASE]      = ""+boolToInt(this.matchCase);
		parameters[PARAMETER_REGEX]           = ""+boolToInt(this.regex);
		return parameters;
	}

	/**
	 * Factory method that create the correct rule based on the parameters
	 * 
	 * @param textToReplace
	 *            - text to replace with new text (textToInsert)
	 * @param regex
	 *            - if true textToReplace is parsed as a regular expression
	 * @param matchCase
	 *            - if true textToReplace is case sensitive; used only if regex
	 *            is false
	 * @param textToInsert
	 *            - the new text to insert
	 * @param startIndex
	 *            - index from which start the replace operation
	 * @param endIndex
	 *            - index (exclusive) at which end the replace operation; a
	 *            value less then or equal to startIndex or greater then text
	 *            length means no limit
	 * @param target
	 *            - indicates the filename part to modify (name/extension) and
	 *            the occurrence to replace (all/first/last); use ReplacePanel
	 *            constraints to set this
	 * @return the right rule, based on input parameters
	 * @throws IllegalArgumentException
	 *             if <i>target</i> is invalid
	 */
	public static Rule getRule(String textToReplace, boolean regex, boolean matchCase, String textToInsert, int startIndex, int endIndex, int target) throws IllegalArgumentException {
		if (LogManager.ENABLED) LogManager.trace(String.format("ReplaceFactory.getRule(textToReplace=%s, regex=%b, matchCase=%b, textToInsert=%s, startIndex=%d, endIndex=%d, target=%d)", textToReplace, regex, matchCase, textToInsert, startIndex, endIndex, target));
		switch (target) {
			case TARGET_NAME_ALL:
				return new ReplaceNameAll(textToReplace, textToInsert, startIndex, endIndex, regex, matchCase);
			
			case TARGET_NAME_FIRST:
				return new ReplaceNameFirst(textToReplace, textToInsert, startIndex, endIndex, regex, matchCase);
			
			case TARGET_NAME_LAST:
				return new ReplaceNameLast(textToReplace, textToInsert, startIndex, endIndex, regex, matchCase);
			
			case TARGET_EXTENSION_ALL:
				return new ReplaceExtensionAll(textToReplace, textToInsert, startIndex, endIndex, regex, matchCase);
			
			case TARGET_EXTENSION_FIRST:
				return new ReplaceExtensionFirst(textToReplace, textToInsert, startIndex, endIndex, regex, matchCase);
			
			case TARGET_EXTENSION_LAST:
				return new ReplaceExtensionLast(textToReplace, textToInsert, startIndex, endIndex, regex, matchCase);
			
			default:
				throw new IllegalArgumentException("Invalid target parameter: " + target + ". Use ReplacePanel class constraints to set the value.");
		}
	}

	/** Abstract class with common fields used for replace rules */
	private static abstract class AbstractReplaceRule implements Rule {

		/** Text to insert */
		protected final String textToInsert;
		
		/** Index from where to start the replace process */
		protected final int startIndex;
		
		/** Index where ends the replace process */
		protected final int endIndex;

		/** Pattern to replace */
		protected final Pattern pattern;

		/**
		 * Creates an abstract replace rule with the specified parameters
		 * 
		 * @param textToReplace
		 *            text to replace
		 * @param textToInsert
		 *            text to insert
		 * @param startIndex
		 *            index from where to start the replace process
		 * @param endIndex
		 *            index where ends the replace process
		 * @param regex
		 *            if true indicates that textToReplace is a regular
		 *            expression, if false indicates that it is a literal
		 *            pattern
		 * @param matchCase
		 *            if true indicates that the pattern should be considered as
		 *            case sensitive
		 */
		public AbstractReplaceRule(String textToReplace, String textToInsert, int startIndex, int endIndex, boolean regex, boolean matchCase) {
			this.textToInsert = textToInsert != null ? textToInsert : "";
			this.startIndex   = startIndex > 0 ? startIndex : 0;
			this.endIndex     = endIndex >= this.startIndex ? endIndex : Integer.MAX_VALUE;
			
			if (matchCase) {
				this.pattern = Pattern.compile(regex ? textToReplace : Pattern.quote(textToReplace)); 
				
			} else {
				this.pattern = Pattern.compile(regex ? textToReplace : Pattern.quote(textToReplace), Pattern.CASE_INSENSITIVE); 
			}
		}

		@Override
		public void reset() {}
	}
	
	/** Abstract rule that has a method to replace all occurrences of a given pattern */
	private static abstract class AbstractReplaceAllRule extends AbstractReplaceRule {
		
		/** @see AbstractReplaceRule#AbstractReplaceRule(String, String, int, int, boolean, boolean) */
		public AbstractReplaceAllRule(String textToReplace, String textToInsert, int startIndex, int endIndex,
				boolean regex, boolean matchCase) {
			super(textToReplace, textToInsert, startIndex, endIndex, regex, matchCase);
		}
		
		/**
		 * Replace every occurrence of <i>pattern</i> in <i>originalValue</i>
		 * 
		 * @param pattern
		 *            pattern to replace
		 * @param originalValue
		 *            string in which search and replace the pattern
		 * @return the string with the replaced pattern
		 */
		protected String replaceAll(Pattern pattern, String originalValue) {
			StringBuilder builder = new StringBuilder(originalValue.length() + super.textToInsert.length());
			builder.append(originalValue.substring(0, super.startIndex));
			
			if (super.endIndex >= originalValue.length()) {
				builder.append(pattern.matcher(originalValue.substring(super.startIndex)).replaceAll(super.textToInsert));
				
			} else {
				builder.append(pattern.matcher(originalValue.substring(super.startIndex, super.endIndex)).replaceAll(super.textToInsert));
				builder.append(originalValue.substring(super.endIndex));
			}
			
			return builder.toString();
		}
	}
	
	/** Abstract rule that has a method to replace first occurrence of a given pattern */
	private static abstract class AbstractReplaceFirstRule extends AbstractReplaceRule {
		
		/** @see AbstractReplaceRule#AbstractReplaceRule(String, String, int, int, boolean, boolean) */
		public AbstractReplaceFirstRule(String textToReplace, String textToInsert, int startIndex, int endIndex,
				boolean regex, boolean matchCase) {
			super(textToReplace, textToInsert, startIndex, endIndex, regex, matchCase);
		}
		
		/**
		 * Replace the first occurrence of <i>pattern</i> in <i>originalValue</i>
		 * 
		 * @param pattern
		 *            pattern to replace
		 * @param originalValue
		 *            string in which search and replace the pattern
		 * @return the string with the replaced pattern
		 */
		protected String replaceFirst(Pattern pattern, String originalValue) {
			StringBuilder builder = new StringBuilder(originalValue.length() + super.textToInsert.length());
			builder.append(originalValue.substring(0, super.startIndex));
			
			if (super.endIndex >= originalValue.length()) {
				builder.append(super.pattern.matcher(originalValue.substring(super.startIndex)).replaceFirst(super.textToInsert));
				
			} else {
				builder.append(super.pattern.matcher(originalValue.substring(super.startIndex, super.endIndex)).replaceFirst(super.textToInsert));
				builder.append(originalValue.substring(super.endIndex));
			}
			
			return builder.toString();
		}
	}
	
	/** Abstract rule that has a method to replace last occurrence of a given pattern */
	private static abstract class AbstractReplaceLastRule extends AbstractReplaceRule {

		/** @see AbstractReplaceRule#AbstractReplaceRule(String, String, int, int, boolean, boolean) */
		public AbstractReplaceLastRule(String textToReplace, String textToInsert, int startIndex, int endIndex,
				boolean regex, boolean matchCase) {
			super(textToReplace, textToInsert, startIndex, endIndex, regex, matchCase);
		}
		
		/**
		 * Replace the last occurrence of <i>pattern</i> in <i>originalValue</i>
		 * 
		 * @param pattern
		 *            pattern to replace
		 * @param originalValue
		 *            string in which search and replace the pattern
		 * @return the string with the replaced pattern
		 */
		protected String replaceLast(Pattern pattern, String originalValue) {
			StringBuilder builder = new StringBuilder(originalValue.length() + super.textToInsert.length());
			
			Matcher matcher;
			if (super.endIndex > originalValue.length()) {
				matcher = super.pattern.matcher(originalValue.substring(super.startIndex));
			} else {
				matcher = super.pattern.matcher(originalValue.substring(super.startIndex, super.endIndex));
			}

			int lastOccurrenceStart = originalValue.length();
			int lastOccurrenceEnd   = lastOccurrenceStart;
			while (matcher.find()) {
				lastOccurrenceStart = matcher.start();
				lastOccurrenceEnd   = matcher.end();
			}

			if (lastOccurrenceStart < originalValue.length()) {
				builder.append(originalValue.substring(0, lastOccurrenceStart));
				builder.append(super.textToInsert);
				builder.append(originalValue.substring(lastOccurrenceEnd));
				return builder.toString();
			} // pattern not matched

			return originalValue;
		}
	}
	
	/** Rule that replace all occurrences of the pattern from name */
	private static class ReplaceNameAll extends AbstractReplaceAllRule {

		/** @see AbstractReplaceRule#AbstractReplaceRule(String, String, int, int, boolean, boolean) */
		public ReplaceNameAll(String textToReplace, String textToInsert, int startIndex, int endIndex, boolean regex, boolean matchCase) {
			super(textToReplace, textToInsert, startIndex, endIndex, regex, matchCase);
		}

		@Override
		public FileModelItem apply(FileModelItem fileModelItem) {
			String originalValue = fileModelItem.getName();
			if (super.startIndex < originalValue.length()) {
				fileModelItem.setName(replaceAll(super.pattern, originalValue));
			}
			return fileModelItem;
		}
	}
	
	/** Rule that replace the first occurrence of the pattern from name */
	private static class ReplaceNameFirst extends AbstractReplaceFirstRule {

		/** @see AbstractReplaceRule#AbstractReplaceRule(String, String, int, int, boolean, boolean) */
		public ReplaceNameFirst(String textToReplace, String textToInsert, int startIndex, int endIndex, boolean regex, boolean matchCase) {
			super(textToReplace, textToInsert, startIndex, endIndex, regex, matchCase);
		}

		@Override
		public FileModelItem apply(FileModelItem fileModelItem) {
			String originalValue = fileModelItem.getName();
			if (super.startIndex < originalValue.length()) {
				fileModelItem.setName(replaceFirst(super.pattern, originalValue));
			}
			return fileModelItem;
		}
	}
	
	/** Rule that replace the last occurrence of the pattern from name */
	private static class ReplaceNameLast extends AbstractReplaceLastRule {

		/** @see AbstractReplaceRule#AbstractReplaceRule(String, String, int, int, boolean, boolean) */
		public ReplaceNameLast(String textToReplace, String textToInsert, int startIndex, int endIndex, boolean regex, boolean matchCase) {
			super(textToReplace, textToInsert, startIndex, endIndex, regex, matchCase);
		}

		@Override
		public FileModelItem apply(FileModelItem fileModelItem) {
			String originalValue = fileModelItem.getName();
			if (super.startIndex < originalValue.length()) {
				fileModelItem.setName(replaceLast(super.pattern, originalValue));
			}
			return fileModelItem;
		}
	}
	
	/** Rule that replace all occurrences of the pattern from extension */
	private static class ReplaceExtensionAll extends AbstractReplaceAllRule {

		/** @see AbstractReplaceRule#AbstractReplaceRule(String, String, int, int, boolean, boolean) */
		public ReplaceExtensionAll(String textToReplace, String textToInsert, int startIndex, int endIndex, boolean regex, boolean matchCase) {
			super(textToReplace, textToInsert, startIndex, endIndex, regex, matchCase);
		}

		@Override
		public FileModelItem apply(FileModelItem fileModelItem) {
			String originalValue = fileModelItem.getExtension();
			if (super.startIndex < originalValue.length()) {
				fileModelItem.setExtension(replaceAll(super.pattern, originalValue));
			}
			return fileModelItem;
		}
	}
	
	/** Rule that replace the first occurrence of the pattern from extension */
	private static class ReplaceExtensionFirst extends AbstractReplaceFirstRule {

		/** @see AbstractReplaceRule#AbstractReplaceRule(String, String, int, int, boolean, boolean) */
		public ReplaceExtensionFirst(String textToReplace, String textToInsert, int startIndex, int endIndex, boolean regex, boolean matchCase) {
			super(textToReplace, textToInsert, startIndex, endIndex, regex, matchCase);
		}

		@Override
		public FileModelItem apply(FileModelItem fileModelItem) {
			String originalValue = fileModelItem.getExtension();
			if (super.startIndex < originalValue.length()) {
				fileModelItem.setExtension(replaceFirst(super.pattern, originalValue));
			}
			return fileModelItem;
		}
	}
	
	/** Rule that replace the last occurrence of the pattern from extension */
	private static class ReplaceExtensionLast extends AbstractReplaceLastRule {

		/** @see AbstractReplaceRule#AbstractReplaceRule(String, String, int, int, boolean, boolean) */
		public ReplaceExtensionLast(String textToReplace, String textToInsert, int startIndex, int endIndex, boolean regex, boolean matchCase) {
			super(textToReplace, textToInsert, startIndex, endIndex, regex, matchCase);
		}

		@Override
		public FileModelItem apply(FileModelItem fileModelItem) {
			String originalValue = fileModelItem.getName();
			if (super.startIndex < originalValue.length()) {
				fileModelItem.setExtension(replaceLast(super.pattern, originalValue));
			}
			return fileModelItem;
		}
	}
}