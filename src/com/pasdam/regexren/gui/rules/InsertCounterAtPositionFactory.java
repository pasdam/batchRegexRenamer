package com.pasdam.regexren.gui.rules;

import java.util.Arrays;

import com.pasdam.regexren.model.FileModelItem;
import com.pasdam.regexren.model.RuleType;

/**
 * {@link AbstractRuleFactory} used to create rule to insert a counter at a specific position
 * 
 * @author paco
 * @version 0.1
 */
public class InsertCounterAtPositionFactory extends AbstractInsertCounterFactory {

	/** Used to specify that the filter must modify only file name */
	public static final int OF_NAME      = 0;
	/** Used to specify that the filter must modify only file extension */
	public static final int OF_EXTENSION = OF_NAME + 1;
	
	/** Index of the "fromBegin" parameter */
	private static final int PARAMETER_FROM_BEGIN  = AbstractInsertCounterFactory.PARAMETERS_COUNT;
	/** Index of the "startCount" parameter */
	private static final int PARAMETER_POSITION    = PARAMETER_FROM_BEGIN + 1; 
	/** Index of the "target" parameter */
	private static final int PARAMETER_TARGET      = PARAMETER_POSITION   + 1;
	/** Indicates how many parameters this component has */
	private static final int PARAMETERS_COUNT      = PARAMETER_TARGET     + 1;

	/** Indicates whether the position starts from the beginning (true), or the end (false) */
	private boolean fromBegin = true;
	
	/** Indicates the position at which insert the counter */
	private int position;
	
	/** Target of the operation (name/extension) */
	private int target;
	
	/** Creates a {@link InsertCounterAtPositionFactory} */
	public InsertCounterAtPositionFactory() {
		super(RuleType.INSERT_COUNTER_AT_POSITION);
		super.setValid(true);
	}

	/**
	 * Returns true if the position starts from the beginning, false if it
	 * starts from end
	 * 
	 * @return true if the position starts from the beginning, false if it
	 *         starts from end
	 */
	public boolean isFromBegin() {
		return this.fromBegin;
	}

	/**
	 * Sets whether the position starts from the beginning (true), or the end
	 * (false)
	 * 
	 * @param fromBegin
	 *            true if the position starts from beginning, false if it starts
	 *            from end
	 */
	public void setFromBegin(boolean fromBegin) {
		if (this.fromBegin != fromBegin) {
			this.fromBegin = fromBegin;
			super.configurationChanged();
		}
	}

	/**
	 * Returns the position at which insert the counter
	 * 
	 * @return the position at which insert the counter
	 */
	public int getPosition() {
		return this.position;
	}

	/**
	 * Sets the position at which insert the counter
	 * 
	 * @param position
	 *            index at which insert the counter
	 */
	public void setPosition(int position) {
		if (this.position != position) {
			this.position = position;
			super.configurationChanged();
		}
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
	 * @throws IllegalArgumentException
	 *             if <i>target</i> is not a supported type ({@link #OF_NAME},
	 *             {@link #OF_EXTENSION})
	 */
	public void setTarget(int target) throws IllegalArgumentException {
		switch (target) {
			case OF_NAME:
			case OF_EXTENSION:
				if (this.target != target) {
					this.target = target;
					super.configurationChanged();
					break;
				}

			default:
				throw new IllegalArgumentException("Invalid target: " + target);
		}
	}

	@Override
	protected Rule createConfiguredRule() {
		return getRule(super.startCount, super.padding, this.position, this.fromBegin, this.target);
	}

	@Override
	protected void parseRuleSpecificParameters(String[] parameters) throws IllegalArgumentException {
		if (parameters.length == InsertCounterAtPositionFactory.PARAMETERS_COUNT) {
			super.parseRuleSpecificParameters(parameters);
			this.fromBegin  = intToBool(Integer.parseInt(parameters[PARAMETER_FROM_BEGIN]));
			this.position   = Integer.parseInt(parameters[PARAMETER_POSITION]);
			this.target     = Integer.parseInt(parameters[PARAMETER_TARGET]);

		} else {
			throw new IllegalArgumentException("Invalid parameter's array length: " + parameters.length);
		}
	}
	
	@Override
	protected void checkConfiguration() throws RuntimeException {}

	@Override
	protected String[] getRuleSpecificParameters() {
		String[] superParameters = super.getRuleSpecificParameters();
		String[] parameters = Arrays.copyOf(superParameters, InsertCounterAtPositionFactory.PARAMETERS_COUNT);
		parameters[PARAMETER_FROM_BEGIN]  = ""+boolToInt(this.fromBegin);
		parameters[PARAMETER_POSITION]    = ""+this.position;
		parameters[PARAMETER_TARGET]      = ""+this.target;
		return parameters;
	}
	
	/**
	 * This method based on the parameters values return the right rule
	 * 
	 * @param startCount
	 *            - counter starting value
	 * @param padding
	 *            - length of the string inserted, insert 1 for no padding
	 * @param position
	 *            - position at which insert text
	 * @param fromBeginOrEnd
	 *            - indicates whether position start from begin or from end
	 * @param target
	 *            - indicates the filename part to modify (name/extension)
	 * @return the right rule, based on input parameters
	 * @throws IllegalArgumentException
	 *             if parameters beforeAfter or fromBeginOrEnd are invalid
	 */
	public static Rule getRule(int startCount, int padding, int position, boolean fromBegin, int target) throws IllegalArgumentException {
		switch (target) {
			case OF_NAME:
				return fromBegin
						? new InsertFromNameBeginning(startCount, padding, position)
						: new InsertFromNameEnd(startCount, padding, position);
						
			case OF_EXTENSION:
				return fromBegin
						? new InsertFromExtensionBeginning(startCount, padding, position)
						: new InsertFromExtensionEnd(startCount, padding, position);
			
			default:
				throw new IllegalArgumentException("Invalid target value: " + target + ". Use the InsertAtPositionPanel constraint to set this parameter.");
		}
	}
	
	/** Abstract insert counter rule, with utility methods */
	private static abstract class AbstractCounterPositionRule extends AbstractCounterRule {
		
		/** Indicates the position at which insert the counter */
		private final int position;
		
		/**
		 * Creates a rule with the specific parameters
		 * 
		 * @param countStart
		 *            starting value of the counter
		 * @param padding
		 *            adding digits
		 * @param position
		 *            counter position
		 */
		public AbstractCounterPositionRule(int countStart, int padding, int position) {
			super(countStart, padding);
			this.position 	= position >= 0 ? position : 0;
		}
		
		/**
		 * Insert the current value of the counter's value at the specific
		 * position from begin
		 * 
		 * @param value
		 *            string in which insert the counter
		 * @return the string with counter's value
		 */
		protected String insertCounterFromBeginning(String value) {
			if (this.position >= value.length()) {
				return value + getCount();
				
			} else {
				return value.substring(0, this.position)
						+ getCount()
						+ value.substring(this.position);
			}
		}
		
		/**
		 * Insert the current value of the counter's value at the specific
		 * position from end
		 * 
		 * @param value
		 *            string in which insert the counter
		 * @return the string with counter's value
		 */
		protected String insertCounterFromEnd(String value) {
			if (this.position >= value.length()) {
				return getCount() + value;

			} else {
				int invertedPosition = value.length() - this.position;
				return value.substring(0, invertedPosition)
					 + getCount()
				 	 + value.substring(invertedPosition);
			}
		}
	}

	/** Rule to insert counter at specific position from name's begin */
	private static class InsertFromNameBeginning extends AbstractCounterPositionRule {

		/** @see AbstractCounterPositionRule#AbstractCounterRule(int, int, int) */
		public InsertFromNameBeginning(int startCount, int padding, int position) {
			super(startCount, padding, position);
		}

		@Override
		public FileModelItem apply(FileModelItem file) {
			file.setName(insertCounterFromBeginning(file.getName()));
			return file;
		}
	}
	
	/** Rule to insert counter at specific position from name's end */
	private static class InsertFromNameEnd extends AbstractCounterPositionRule {
		
		/** @see AbstractCounterPositionRule#AbstractCounterRule(int, int, int) */
		public InsertFromNameEnd(int startCount, int padding, int position) {
			super(startCount, padding, position);
		}
		
		@Override
		public FileModelItem apply(FileModelItem file) {
			file.setName(insertCounterFromEnd(file.getName()));
			return file;
		}
	}
	
	/** Rule to insert counter at specific position from extension's begin */
	private static class InsertFromExtensionBeginning extends AbstractCounterPositionRule {
		
		/** @see AbstractCounterPositionRule#AbstractCounterRule(int, int, int) */
		public InsertFromExtensionBeginning(int startCount, int padding, int position) {
			super(startCount, padding, position);
		}
		
		@Override
		public FileModelItem apply(FileModelItem file) {
			file.setExtension(insertCounterFromBeginning(file.getExtension()));
			return file;
		}
	}
	
	/** Rule to insert counter at specific position from extension's end */
	private static class InsertFromExtensionEnd extends AbstractCounterPositionRule {

		/** @see AbstractCounterPositionRule#AbstractCounterRule(int, int, int) */
		public InsertFromExtensionEnd(int startCount, int padding, int position) {
			super(startCount, padding, position);
		}
		
		@Override
		public FileModelItem apply(FileModelItem file) {
			file.setExtension(insertCounterFromEnd(file.getExtension()));
			return file;
		}
	}
	
	
}