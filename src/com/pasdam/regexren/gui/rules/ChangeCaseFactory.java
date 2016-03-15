package com.pasdam.regexren.gui.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.pasdam.regexren.model.FileModelItem;
import com.pasdam.regexren.model.RuleType;

/**
 * {@link AbstractRuleFactory} used to create rules to change case of the name/extension
 * 
 * @author paco
 * @version 0.1
 */
public class ChangeCaseFactory extends AbstractRuleFactory {
	
	/** Used to specify that the filter must modify only file name */
	public static final int TARGET_NAME = 0;
	/** Used to specify that the filter must modify only file extension */
	public static final int TARGET_EXTENSION = 1;
	
	/** Operation constraint: change to lowercase */
	public static final int OPERATION_TO_LOWERCASE = 0;
	/** Operation constraint: change to uppercase */
	public static final int OPERATION_TO_UPPERCASE = 1;
	/** Operation constraint: capitalize words */
	public static final int OPERATION_CAPITALIZE_WORDS = 2;
	/** Operation constraint: capitalize sentences */
	public static final int OPERATION_CAPITALIZE_SENTENCES = 3;
	
	/** Index of the "target" parameter */
	private static final int PARAMETER_TARGET    = 0;
	/** Index of the "operation" parameter */
	private static final int PARAMETER_OPERATION = PARAMETER_TARGET + 1;
	/** Index of the "separator" parameter */
	private static final int PARAMETER_SEPARATOR = PARAMETER_OPERATION + 1;
	/** Index of the "regex" parameter */
	private static final int PARAMETER_REGEX     = PARAMETER_SEPARATOR + 1;
	/** Indicates how many parameters this component has */
	private static final int PARAMETERS_COUNT    = PARAMETER_REGEX + 1;
	
	/**	Pattern used to extract words */
	private static final Pattern PATTERN_WORD = Pattern.compile("\\w+");
	
	/**	Indicates the target of the operation (name/extension) */
	private int target;
	
	/**	Indicates the operation type */
	private int operation;
	
	/**	Indicates the pattern used to separate the phrases */
	private String sentenceSeparator = "";
	
	/**	Indicates if the sentence separator is a regular expression or not */
	private boolean regex;
	
	/**	Creates a {@link ChangeCaseFactory} */
	public ChangeCaseFactory() {
		super(RuleType.CHANGE_CASE);
		
		// set default values
		this.target    = TARGET_NAME;
		this.operation = OPERATION_TO_LOWERCASE;
		this.regex     = false;
	}
	
	/**
	 * Sets the target of the operation ({@link #TARGET_NAME}, {@link #TARGET_EXTENSION})
	 * 
	 * @param target
	 *            the target of the operation
	 * @throws IllegalArgumentException
	 *             if the specified target is not supported
	 */
	public void setTarget(int target) throws IllegalArgumentException, NullPointerException {
		switch (target) {
			case TARGET_NAME:
			case TARGET_EXTENSION:
				this.target = target;
				checkConfiguration();
				return;
	
			default:
				setValid(false);
				throw new IllegalArgumentException("Invalid target: " + target);
		}
	}
	
	/**
	 * Returns the target of the renaming operation (name/extension)
	 * 
	 * @return the target of the renaming operation (name/extension)
	 */
	public int getTarget() {
		return target;
	}
	
	/**
	 * Sets the operation type
	 * 
	 * @param operation
	 *            operation type
	 * @throws IllegalArgumentException
	 *             if the specified operation is not a supported type (
	 *             {@link #OPERATION_TO_LOWERCASE},
	 *             {@link #OPERATION_TO_UPPERCASE},
	 *             {@link #OPERATION_CAPITALIZE_WORDS} or
	 *             {@link #OPERATION_CAPITALIZE_SENTENCES})
	 * @throws PatternSyntaxException
	 *             if regex it true and the sentence separator is an invalid
	 *             regular expression
	 * @throws NullPointerException
	 *             if regex is true and the sentence separator is null
	 */
	public void setOperation(int operation) throws IllegalArgumentException, PatternSyntaxException, NullPointerException {
		switch (target) {
			case OPERATION_TO_LOWERCASE:
			case OPERATION_TO_UPPERCASE:
			case OPERATION_CAPITALIZE_WORDS:
			case OPERATION_CAPITALIZE_SENTENCES:
				this.operation = operation;
				checkConfiguration();
				return;
	
			default:
				setValid(false);
				throw new IllegalArgumentException("Invalid operation: " + operation);
		}
	}
	
	/**
	 * Returns the operation to perform
	 * 
	 * @return the operation to perform
	 */
	public int getOperation() {
		return operation;
	}
	
	/**
	 * Sets the pattern used to split sentences
	 * 
	 * @param sentenceSeparator
	 *            pattern used to split sentences
	 * @throws PatternSyntaxException
	 *             if regex it true and the sentence separator is an invalid
	 *             regular expression
	 * @throws NullPointerException
	 *             if regex is true and the sentence separator is null
	 */
	public void setSentenceSeparator(String sentenceSeparator) throws PatternSyntaxException, NullPointerException {
		this.sentenceSeparator = sentenceSeparator;
		super.configurationChanged();
		checkConfiguration();
	}
	
	/**
	 * Returns the pattern used to separate sentences
	 * 
	 * @return the pattern used to separate sentences
	 */
	public String getSentenceSeparator() {
		return sentenceSeparator;
	}
	
	/**
	 * Sets whether the sentence separator is a regular expression or not
	 * 
	 * @param regex
	 *            true if the sentence separator is a regular expression, false
	 *            otherwise
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
	 * Returns true if the sentence separator is a regular expression
	 * 
	 * @return true if the sentence separator is a regular expression
	 */
	public boolean isRegex() {
		return regex;
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
	protected void checkConfiguration() throws PatternSyntaxException, NullPointerException {
		if (this.regex) {
			setValid(false);
			Pattern.compile(this.sentenceSeparator);
		}
		setValid(true);
	}
	
	@Override
	public void parseRuleSpecificParameters(String[] parameters) throws IllegalArgumentException {
		if (parameters.length == PARAMETERS_COUNT) {
			this.target            = Integer.parseInt(parameters[PARAMETER_TARGET]);
			this.operation         = Integer.parseInt(parameters[PARAMETER_OPERATION]);
			this.sentenceSeparator = parameters[PARAMETER_SEPARATOR];
			this.regex             = intToBool(Integer.parseInt(parameters[PARAMETER_REGEX]));
		} else {
			throw new IllegalArgumentException("Invalid parameter's array length: " + parameters.length);
		}
	}

	@Override
	public String[] getRuleSpecificParameters() {
		String[] parameters = new String[PARAMETERS_COUNT];
		parameters[PARAMETER_TARGET]    = ""+this.target;
		parameters[PARAMETER_OPERATION] = ""+this.operation;
		parameters[PARAMETER_SEPARATOR] = this.sentenceSeparator;
		parameters[PARAMETER_REGEX]     = ""+boolToInt(this.regex);
		return parameters;
	}
	
	@Override
	protected Rule createConfiguredRule() {
		return getRule(this.target, this.operation, this.sentenceSeparator, this.regex);
	}
	
	/**
	 * Factory method that creates the correct rule based on the parameters values
	 * 
	 * @param target
	 *            - target of the operation (name/extension), use
	 *            ChangeCasePanel constraint to set this.
	 * @param operation
	 *            - kind of operation, use ChangeCasePanel constraint to set
	 *            this.
	 * @param sentenceSeparator
	 *            - separator used to split sentences, used only for the
	 *            operation "Capitalize sentences"; default ".".
	 * @param regex
	 *            - if true <i>sentenceSeparator</i> will be interpreted as a
	 *            regular expression; it will be ignored if <i>target</i> is
	 *            {@link ChangeCaseFactory#TARGET_EXTENSION}
	 * @return the correct rule based on input parameters
	 * @throws IllegalArgumentException
	 *             if target or operation parameter is unknown
	 */
	public static Rule getRule(int target, int operation, String sentenceSeparator, boolean regex) throws IllegalArgumentException {
		switch (target) {
			case TARGET_NAME:
				switch (operation) {
					case OPERATION_TO_LOWERCASE:
						return new LowerCaseName();
						
					case OPERATION_TO_UPPERCASE:
						return new UpperCaseName();
					
					case OPERATION_CAPITALIZE_WORDS:
						return new CapitalizeWordsName();
					
					case OPERATION_CAPITALIZE_SENTENCES:
						return new CapitalizeSentencesName(sentenceSeparator, regex);

					default:
						throw new IllegalArgumentException("Invalid operation: " + operation + ". Use ChangeCaseFactory constraints to set this.");
				}
				
			case TARGET_EXTENSION:
				switch (operation) {
					case OPERATION_TO_LOWERCASE:
						return new LowerCaseExtension();
					
					case OPERATION_TO_UPPERCASE:
						return new UpperCaseExtension();
					
					case OPERATION_CAPITALIZE_WORDS:
						return new CapitalizeWordsExtension();
					
					case OPERATION_CAPITALIZE_SENTENCES:
						return new CapitalizeWordsExtension();
					
					default:
						throw new IllegalArgumentException("Invalid operation: " + operation + ". Use ChangeCaseFactory constraints to set this.");
				}
				
			default:
				throw new IllegalArgumentException("Invalid target: " + target + ". Use ChangeCaseFactory constraints to set this.");
		}
	}

	/** Rule to change the case of the name to uppercase*/
	private static class UpperCaseName implements Rule {
		
		@Override
		public FileModelItem apply(FileModelItem renamer) {
			renamer.setName(renamer.getName().toUpperCase());
			return renamer;
		}

		@Override
		public void reset() {}
	}
	
	/** Rule to change the case of the name to lowercase */
	private static class LowerCaseName implements Rule {
		
		@Override
		public FileModelItem apply(FileModelItem renamer) {
			renamer.setName(renamer.getName().toLowerCase());
			return renamer;
		}

		@Override
		public void reset() {}
	}
	
	/** Rule to capitalize all words of the name*/
	private static class CapitalizeWordsName implements Rule {
		
		@Override
		public FileModelItem apply(FileModelItem renamer) {
			String name = renamer.getName();
			Matcher matcher = PATTERN_WORD.matcher(name);
			StringBuilder builder = new StringBuilder(name.length());
			int previousIndex = 0;
			String currentGroup;

			// loop over words 
			while (matcher.find()) {
				builder.append(name.substring(previousIndex, matcher.start()));
				currentGroup = matcher.group();
				builder.append(Character.toUpperCase(currentGroup.charAt(0))).append(currentGroup.substring(1));
				previousIndex = matcher.end();
			}
			if (builder.length() > 0) {
				name = builder.toString();
			}
			
			// update renamer
			renamer.setName(name);
			
			return renamer;
		}

		@Override
		public void reset() {}
	}
	
	/** Rule to capitalize the sentences (delimited by a regex/simple pattern) of the name*/
	private static class CapitalizeSentencesName implements Rule {
		
		/** Pattern used to separate sentences */
		private final Pattern patternSentences;
		
		/**
		 * Create a rule with the specified regex separator pattern
		 * 
		 * @param separator
		 *            regex pattern
		 * @param regex
		 *            if true indicates that the separator is a regular
		 *            expression pattern, otherwise <i>separator</i> will be
		 *            interpreted as a literal pattern
		 */
		public CapitalizeSentencesName(String separator, boolean regex) {
			if (separator != null && !separator.equals("")) {
				// capitalize sentences, separated by "separator"
				patternSentences = regex
						? Pattern.compile(separator)
						: Pattern.compile(Pattern.quote(separator));
				
			} else {
				patternSentences = Pattern.compile("\\.+");
			}
		}
		
		@Override
		public FileModelItem apply(FileModelItem renamer) {
			String name = renamer.getName();
			String currentWord;
			StringBuilder builder = new StringBuilder(name.length());
			Matcher sentencesMatcher = patternSentences.matcher(name);
			Matcher wordsMatcher;
			int previousIndex = 0;
			
			// iterate over phrases
			while (sentencesMatcher.find()) {
				wordsMatcher = PATTERN_WORD.matcher(name.substring(previousIndex, sentencesMatcher.start()));
				
				if (wordsMatcher.find()) {
					// change case of the first word
					currentWord = wordsMatcher.group();
					builder.append(name.substring(previousIndex, wordsMatcher.start()+previousIndex))
					.append(Character.toUpperCase(currentWord.charAt(0)))
					.append(name.substring(wordsMatcher.start()+previousIndex+1, sentencesMatcher.end()));
				} else {
					// append phrase as is
					builder.append(name.substring(previousIndex, sentencesMatcher.end()));
				}
				
				previousIndex = sentencesMatcher.end();
			}
			
			// modify last phrase
			wordsMatcher = PATTERN_WORD.matcher(name.substring(previousIndex));
			if (wordsMatcher.find()) {
				currentWord = wordsMatcher.group();
				builder.append(name.substring(previousIndex, wordsMatcher.start()+previousIndex))
				.append(Character.toUpperCase(currentWord.charAt(0)))
				.append(name.substring(wordsMatcher.start()+previousIndex+1));
				name = builder.toString();
			}
			
			// update renamer
			renamer.setName(name);
			
			return renamer;
		}

		@Override
		public void reset() {}
	}
	
	/** Rule to change the case of the extension to uppercase */
	private static class UpperCaseExtension implements Rule {
		
		@Override
		public FileModelItem apply(FileModelItem renamer) {
			String extension = renamer.getExtension();
			if (extension != null) {
				renamer.setExtension(extension.toUpperCase());
			}
			return renamer;
		}

		@Override
		public void reset() {}
	}
	
	/** Rule to change the case of the extension to lowercase */
	private static class LowerCaseExtension implements Rule {
		
		@Override
		public FileModelItem apply(FileModelItem renamer) {
			String extension = renamer.getExtension();
			if (extension != null) {
				renamer.setExtension(extension.toLowerCase());
			}
			return renamer;
		}

		@Override
		public void reset() {}
	}
	
	/** Rule to capitalize the extension */
	private static class CapitalizeWordsExtension implements Rule {
		
		@Override
		public FileModelItem apply(FileModelItem renamer) {
			String extension = renamer.getExtension();
			if (extension != null) {
				renamer.setExtension(Character.toUpperCase(extension.charAt(0)) + extension.substring(1));
			}
			return renamer;
		}

		@Override
		public void reset() {}
	}
}