package com.pasdam.regexren.gui.rules;

import com.pasdam.regexren.model.FileModelItem;
import com.pasdam.regexren.model.RuleType;

/**
 * {@link AbstractRuleFactory} used to create rule to insert a text at a specific position
 * 
 * @author paco
 * @version 0.1
 */
public class InsertTextAtPositionFactory extends AbstractRuleFactory {

	/** Used to specify that the filter must modify only file name */
	public static final int TARGET_NAME = 0;
	/** Used to specify that the filter must modify only file extension */
	public static final int TARGET_EXTENSION = TARGET_NAME + 1;

	/** Index of the "text to insert" parameter */
	private static final int PARAMETER_TEXT_TO_INSERT = 0;
	/** Index of the "fromBegin" parameter */
	private static final int PARAMETER_FROM_BEGIN     = PARAMETER_TEXT_TO_INSERT + 1;
	/** Index of the "startCount" parameter */
	private static final int PARAMETER_POSITION       = PARAMETER_FROM_BEGIN     + 1;
	/** Index of the "target" parameter */
	private static final int PARAMETER_TARGET         = PARAMETER_POSITION       + 1;
	/** Indicates how many parameters this component has */
	private static final int PARAMETERS_COUNT         = PARAMETER_TARGET         + 1;

	/**	Text to insert */
	private String textToInsert;
	
	/**	Position at which insert text */
	private int position;
	
	/**	Indicates whether position starts from begin or from end */
	private boolean fromBegin = true;
	
	/**	Indicates the target of the operation (name/extension) */
	private int target;
	
	/** Creates a {@link InsertTextAtPositionFactory} */
	public InsertTextAtPositionFactory() {
		super(RuleType.INSERT_TEXT_AT_POSITION);
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
	 * @throws IllegalArgumentException
	 *             if <i>textToInsert</i> is null or empty
	 */
	public void setTextToInsert(String textToInsert) throws IllegalArgumentException {
		this.textToInsert = textToInsert;
		
		boolean valid = this.textToInsert != null && this.textToInsert.length() > 0;
		super.setValid(valid);
		if (!valid) {
			throw new IllegalArgumentException("Invalid parameter, textToInsert cannot be null or empty");
		}
	}

	/**
	 * Returns position at which insert text
	 * 
	 * @return position at which insert text
	 */
	public int getPosition() {
		return this.position;
	}

	/**
	 * Sets the position at which insert text
	 * 
	 * @param position
	 *            the position at which insert text
	 */
	public void setPosition(int position) {
		this.position = position;
		super.configurationChanged();
	}

	/**
	 * Returns true if the position starts from begin, false if it starts from
	 * end
	 * 
	 * @return true if the position starts from begin, false if it starts from
	 *         end
	 */
	public boolean isFromBegin() {
		return this.fromBegin;
	}

	/**
	 * Sets whether position start from begin or from end
	 * 
	 * @param fromBegin
	 *            if true the position starts from begin, false if it starts
	 *            from end
	 */
	public void setFromBegin(boolean fromBegin) {
		this.fromBegin = fromBegin;
		super.configurationChanged();
	}

	/**
	 * Returns the target of the operation (name/extension)
	 * 
	 * @return the target of the operation (name/extension)
	 */
	public int getTarget() {
		return this.target;
	}

	/**
	 * Sets the target of the operation (name/extension)
	 * 
	 * @param target
	 *            target of the operation (name/extension)
	 */
	public void setTarget(int target) {
		this.target = target;
		super.configurationChanged();
	}

	@Override
	protected Rule createConfiguredRule() {
		return getRule(this.textToInsert, this.position, this.fromBegin, this.target);
	}

	@Override
	protected void parseRuleSpecificParameters(String[] parameters) throws IllegalArgumentException {
		if (parameters.length == PARAMETERS_COUNT) {
			this.textToInsert = parameters[PARAMETER_TEXT_TO_INSERT];
			this.position     = Integer.parseInt(parameters[PARAMETER_POSITION]);
			this.fromBegin    = intToBool(Integer.parseInt(parameters[PARAMETER_FROM_BEGIN]));
			this.target       = Integer.parseInt(parameters[PARAMETER_TARGET]);

		} else {
			throw new IllegalArgumentException("Invalid parameter's array length: " + parameters.length);
		}
	}

	@Override
	protected String[] getRuleSpecificParameters() {
		String[] parameters = new String[PARAMETERS_COUNT];
		parameters[PARAMETER_TEXT_TO_INSERT] = this.textToInsert;
		parameters[PARAMETER_FROM_BEGIN]     = ""+boolToInt(this.fromBegin);
		parameters[PARAMETER_POSITION]       = ""+this.position;
		parameters[PARAMETER_TARGET]         = ""+this.target;
		return parameters;
	}

	/**
	 * This method based on the parameters values return the right rule
	 * 
	 * @param textToInsert
	 *            - text to insert
	 * @param position
	 *            - position at which insert text
	 * @param fromBegin
	 *            - if true indicates that the position starts from begin, if
	 *            false it starts from end
	 * @param target
	 *            - indicates the filename part to modify (name/extension)
	 * @return the right rule, based on input parameters
	 * @throws IllegalArgumentException
	 *             if target is invalid
	 */
	public static Rule getRule(String textToInsert, int position, boolean fromBegin, int target) throws IllegalArgumentException {
		switch (target) {
			case TARGET_NAME:
				return fromBegin
						? new InsertFromNameBeginning(textToInsert, position)
						: new InsertFromNameEnd(textToInsert, position);
			
			case TARGET_EXTENSION:
				return fromBegin
						? new InsertFromExtensionBeginning(textToInsert, position)
						: new InsertFromExtensionEnd(textToInsert, position);
			
			default:
				throw new IllegalArgumentException("Invalid target value: " + target);
		}
	}

	/**	Abstract rule with utility methods */
	private static abstract class AbstractInsertTextRule implements Rule {

		/**	Text to insert */
		protected String textToInsert;

		/**	Position at which insert the text */
		protected final int position;
		
		/**
		 * Creates an AbstractInsertTextRule
		 * 
		 * @param textToInsert
		 *            text to insert
		 * @param position
		 *            position at which insert the text
		 */
		public AbstractInsertTextRule(String textToInsert, int position) {
			this.textToInsert = textToInsert;
			this.position = position > 0 ? position : 0;
		}

		@Override
		public void reset() {}
		
		/**
		 * Inserts the specified text at specified position, starting from begin
		 * of the original text
		 * 
		 * @param textToInsert
		 *            text to insert
		 * @param originalText
		 *            original text in which insert the string
		 * @param position
		 *            position at which insert the text
		 * @return a new string with the inserted text
		 */
		protected static String insertFromBegin(String textToInsert, String originalText, int position) {
			if (position >= originalText.length()) {
				return originalText + textToInsert;
				
			} else {
				return originalText.substring(0, position) + textToInsert + originalText.substring(position);
			}
		}
		
		/**
		 * Inserts the specified text at specified position, starting from end
		 * of the original text
		 * 
		 * @param textToInsert
		 *            text to insert
		 * @param originalText
		 *            original text in which insert the string
		 * @param position
		 *            position at which insert the text
		 * @return a new string with the inserted text
		 */
		protected static String insertFromEnd(String textToInsert, String originalText, int position) {
			if (position >= originalText.length()) {
				return textToInsert + originalText;

			} else {
				int invertedPosition = originalText.length() - position;
				return originalText.substring(0, invertedPosition) + textToInsert + originalText.substring(invertedPosition);
			}
		}
	}
	
	/**	Rule that insert the text, at the specified position, starting from begin of the name */
	private static class InsertFromNameBeginning extends AbstractInsertTextRule {
		
		/** @see AbstractInsertTextRule#AbstractInsertTextRule(String, int) */
		public InsertFromNameBeginning(String textToInsert, int position) {
			super(textToInsert, position);
		}

		@Override
		public FileModelItem apply(FileModelItem fileModelItem) {
			fileModelItem.setName(
					AbstractInsertTextRule.insertFromBegin(
							super.textToInsert, 
							fileModelItem.getName(), 
							super.position));;
			return fileModelItem;
		}
	}
	
	/** Rule that insert the text, at the specified position, starting from end of the name */
	private static class InsertFromNameEnd extends AbstractInsertTextRule {
		
		/** @see AbstractInsertTextRule#AbstractInsertTextRule(String, int) */
		public InsertFromNameEnd(String textToInsert, int position) {
			super(textToInsert, position);
		}
		
		@Override
		public FileModelItem apply(FileModelItem fileModelItem) {
			fileModelItem.setName(
					AbstractInsertTextRule.insertFromEnd(
							super.textToInsert, 
							fileModelItem.getName(), 
							super.position));;
			return fileModelItem;
		}
	}
	
	/** Rule that insert the text, at the specified position, starting from begin of the extension */
	private static class InsertFromExtensionBeginning extends AbstractInsertTextRule {
		
		/** @see AbstractInsertTextRule#AbstractInsertTextRule(String, int) */
		public InsertFromExtensionBeginning(String textToInsert, int position) {
			super(textToInsert, position);
		}
		
		@Override
		public FileModelItem apply(FileModelItem fileModelItem) {
			fileModelItem.setExtension(
					AbstractInsertTextRule.insertFromBegin(
							super.textToInsert, 
							fileModelItem.getExtension(), 
							super.position));;
			return fileModelItem;
		}
	}
	
	/** Rule that insert the text, at the specified position, starting from end of the extension */
	private static class InsertFromExtensionEnd extends AbstractInsertTextRule {

		/** @see AbstractInsertTextRule#AbstractInsertTextRule(String, int) */
		public InsertFromExtensionEnd(String textToInsert, int position) {
			super(textToInsert, position);
		}
		
		@Override
		public FileModelItem apply(FileModelItem fileModelItem) {
			fileModelItem.setExtension(
					AbstractInsertTextRule.insertFromEnd(
							super.textToInsert, 
							fileModelItem.getExtension(), 
							super.position));;
			return fileModelItem;
		}
	}
}