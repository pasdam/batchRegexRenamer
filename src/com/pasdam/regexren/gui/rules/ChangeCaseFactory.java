package com.pasdam.regexren.gui.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.pasdam.regexren.gui.Rule;
import com.pasdam.regexren.model.FileModelItem;
import com.pasdam.regexren.model.RuleType;

/**
 * @author Paco
 * @version 1.0
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
	
	private static final Pattern PATTERN_WORD = Pattern.compile("\\w+");
	
	/** Index of the "target" parameter */
	private static final int PARAMETER_TARGET    = 0;
	/** Index of the "operation" parameter */
	private static final int PARAMETER_OPERATION = PARAMETER_TARGET + 1;
	/** Index of the "separator" parameter */
	private static final int PARAMETER_SEPARATOR = PARAMETER_OPERATION + 1;
	/** Index of the "regex" parameter */
	private static final int PARAMETER_REGEX     = PARAMETER_SEPARATOR + 1;
	/** Indicates how many parameters this component has */
	private static final int PARAMETER_COUNT     = PARAMETER_REGEX + 1;
	
	private int target;
	private int operation;
	private String sentenceSeparator;
	private boolean regex;
	
	public ChangeCaseFactory() {
		super(RuleType.CHANGE_CASE);
	}
	
	public void setTarget(int target) throws IllegalArgumentException, NullPointerException {
		configurationChanged();
		switch (target) {
			case TARGET_NAME:
			case TARGET_EXTENSION:
				this.target = target;
				setValid(true);
				return;
	
			default:
				setValid(false);
				throw new IllegalArgumentException("Invalid target: " + target);
		}
	}
	
	public int getTarget() {
		return target;
	}
	
	public void setOperation(int operation) throws IllegalArgumentException, NullPointerException {
		configurationChanged();
		switch (target) {
			case OPERATION_TO_LOWERCASE:
			case OPERATION_TO_UPPERCASE:
			case OPERATION_CAPITALIZE_WORDS:
			case OPERATION_CAPITALIZE_SENTENCES:
				this.operation = operation;
				setValid(true);
				return;
	
			default:
				setValid(false);
				throw new IllegalArgumentException("Invalid operation: " + operation);
		}
	}
	
	public int getOperation() {
		return operation;
	}
	
	public void setSentenceSeparator(String sentenceSeparator) throws PatternSyntaxException, NullPointerException {
		this.sentenceSeparator = sentenceSeparator;
		checkState();
		configurationChanged();
	}
	
	public String getSentenceSeparator() {
		return sentenceSeparator;
	}
	
	public void setRegex(boolean regex) throws PatternSyntaxException, NullPointerException {
		this.regex = regex;
		checkState();
		configurationChanged();
	}
	
	public boolean isRegex() {
		return regex;
	}
	
	private void checkState() throws PatternSyntaxException, NullPointerException {
		if (this.regex) {
			setValid(false);
			Pattern.compile(this.sentenceSeparator);
		}
		setValid(true);
	}
	
	@Override
	public void parseRuleSpecificParameters(String[] parameters) throws IllegalArgumentException {
		if (parameters.length == PARAMETER_COUNT) {
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
		String[] parameters = new String[PARAMETER_COUNT];
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
						return regex
								? new CapitalizeSentencesNameRegex(sentenceSeparator)
								: new CapitalizeSentencesName(sentenceSeparator);

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

	private static class UpperCaseName implements Rule {
		
		@Override
		public FileModelItem apply(FileModelItem renamer) {
			renamer.setName(renamer.getName().toUpperCase());
			return renamer;
		}
	}
	
	private static class LowerCaseName implements Rule {
		
		@Override
		public FileModelItem apply(FileModelItem renamer) {
			renamer.setName(renamer.getName().toLowerCase());
			return renamer;
		}
	}
	
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
	}
	
	private static class CapitalizeSentencesNameRegex implements Rule {
		
		private final Pattern patternSentences;

		public CapitalizeSentencesNameRegex(String separator) {
			if (separator != null && !separator.equals("")) {
				// capitalize sentences, separated by "separator"
				patternSentences = Pattern.compile(separator);
			
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
	}
	
	private static class CapitalizeSentencesName implements Rule {
		
		private final String separator;

		public CapitalizeSentencesName(String separator) {
			if (separator != null && !separator.equals("")) {
				// capitalize sentences, separated by "separator"
				this.separator = separator;
			
			} else {
				this.separator = ".";
			}
		}
		
		@Override
		public FileModelItem apply(FileModelItem renamer) {
			String name = renamer.getName();
			String currentSentence;
			StringBuilder builder = new StringBuilder(name.length());
			Matcher wordsMatcher;
			int previousIndex = 0;
			int currentIndex = name.indexOf(separator, previousIndex);
				
			// iterate over phrases
			while ((currentIndex = name.indexOf(separator, previousIndex)) >= 0) {
				// get current phrase
				currentSentence = name.substring(previousIndex, currentIndex + separator.length());
				
				// iterate over words
				wordsMatcher = PATTERN_WORD.matcher(currentSentence);
				if (wordsMatcher.find()) {
					builder.append(currentSentence.substring(0, wordsMatcher.start()))
						   .append(Character.toUpperCase(currentSentence.charAt(wordsMatcher.start())))
						   .append(currentSentence.substring(wordsMatcher.start() + 1));
				} else {
					builder.append(currentSentence);
				}
				
				previousIndex = currentIndex + separator.length();
			}
			
			// evaluate last phrase
			currentSentence = name.substring(previousIndex);
			wordsMatcher = PATTERN_WORD.matcher(currentSentence);
			if (wordsMatcher.find()) {
				builder.append(
						currentSentence.substring(0, wordsMatcher.start()))
						.append(Character.toUpperCase(currentSentence
								.charAt(wordsMatcher.start())))
						.append(currentSentence.substring(wordsMatcher
								.start() + 1));
			} else {
				builder.append(currentSentence);
			}

			// update renamer
			renamer.setName(builder.toString());
			
			return renamer;
		}
	}
	
	private static class UpperCaseExtension implements Rule {
		
		@Override
		public FileModelItem apply(FileModelItem renamer) {
			String extension = renamer.getExtension();
			if (extension != null) {
				renamer.setExtension(extension.toUpperCase());
			}
			return renamer;
		}
	}
	
	private static class LowerCaseExtension implements Rule {
		
		@Override
		public FileModelItem apply(FileModelItem renamer) {
			String extension = renamer.getExtension();
			if (extension != null) {
				renamer.setExtension(extension.toLowerCase());
			}
			return renamer;
		}
	}
	
	private static class CapitalizeWordsExtension implements Rule {
		
		@Override
		public FileModelItem apply(FileModelItem renamer) {
			String extension = renamer.getExtension();
			if (extension != null) {
				renamer.setExtension(Character.toUpperCase(extension.charAt(0)) + extension.substring(1));
			}
			return renamer;
		}
	}
}