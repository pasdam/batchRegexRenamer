package com.pasdam.regexren.gui.rules;

import com.pasdam.regexren.model.RuleType;

/**
 * {@link AbstractRuleFactory} used to create rule to insert a counter
 * 
 * @author paco
 * @version 0.1
 */
abstract class AbstractInsertCounterFactory extends AbstractRuleFactory {
	
	/** Index of the "padding" parameter */
	protected static final int PARAMETER_START_COUNT = 0;
	/** Index of the "position" parameter */
	protected static final int PARAMETER_PADDING     = PARAMETER_START_COUNT + 1;
	/** Indicates how many parameters this component has */
	protected static final int PARAMETERS_COUNT      = PARAMETER_PADDING     + 1;

	/** Indicates how many padding digits, if any, to use */
	protected int padding = 1;
	
	/** Counter's starting value */
	protected int startCount;
	
	/** Creates a {@link AbstractInsertCounterFactory} */
	public AbstractInsertCounterFactory(RuleType ruleType) {
		super(ruleType);
		
		setValid(false);
	}

	/**
	 * Returns how many padding digits are used
	 * 
	 * @return how many padding digits are used
	 */
	public int getPadding() {
		return this.padding;
	}

	/**
	 * Sets the padding to use
	 * 
	 * @param padding
	 *            indicates the number of padding digits to use
	 */
	public void setPadding(int padding) {
		int newValue = padding > 1 ? padding : 1;
		if (this.padding != newValue) {
			this.padding = newValue;
			super.configurationChanged();
		}
	}

	/**
	 * Returns the counter's start value
	 * 
	 * @return the counter's start value
	 */
	public int getStartCount() {
		return this.startCount;
	}

	/**
	 * Sets the counter's start value
	 * 
	 * @param startCount
	 *            starting value of the counter
	 */
	public void setStartCount(int startCount) {
		if (this.startCount != startCount) {
			this.startCount = startCount;
			super.configurationChanged();
		}
	}

	@Override
	protected void parseRuleSpecificParameters(String[] parameters) throws IllegalArgumentException {
		if (parameters.length >= PARAMETERS_COUNT) {
			this.padding    = Integer.parseInt(parameters[PARAMETER_PADDING]);
			this.startCount = Integer.parseInt(parameters[PARAMETER_START_COUNT]);

		} else {
			throw new IllegalArgumentException("Invalid parameter's array length: " + parameters.length);
		}
	}

	@Override
	protected String[] getRuleSpecificParameters() {
		String[] parameters = new String[PARAMETERS_COUNT];
		parameters[PARAMETER_PADDING]     = ""+this.padding;
		parameters[PARAMETER_START_COUNT] = ""+this.startCount;
		return parameters;
	}
	
	/** Abstract insert counter rule, with utility methods */
	protected static abstract class AbstractCounterRule implements Rule {
		
		/** Inficates the counter's starting value */
		private final int countStart;
		
		/** Inficates the current counter's value */
		private int countValue;

		/** Inficates the counter value format */
		private final String valueFormat;
		
		/**
		 * Creates a rule with the specific parameters
		 * 
		 * @param countStart
		 *            starting value of the counter
		 * @param padding
		 *            adding digits
		 */
		public AbstractCounterRule(int countStart, int padding) {
			this.countStart  = countStart;
			this.countValue  = this.countStart;
			this.valueFormat = "%0" + (padding > 1 ? padding : 1) + "d";
		}

		/**
		 * Returns the current formatted value of the counter
		 * 
		 * @return the current formatted value of the counter
		 */
		protected String getCount(){
			return String.format(this.valueFormat, this.countValue++);
		}

		@Override
		public void reset() {
			this.countValue  = this.countStart;
		}
	}
}