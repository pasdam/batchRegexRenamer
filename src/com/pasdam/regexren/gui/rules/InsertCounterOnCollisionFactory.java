package com.pasdam.regexren.gui.rules;

import java.util.HashMap;
import java.util.Map;

import com.pasdam.regexren.model.FileModelItem;
import com.pasdam.regexren.model.RuleType;

/**
 * {@link AbstractRuleFactory} used to create rule to insert a counter at a specific position
 * 
 * @author paco
 * @version 0.1
 */
public class InsertCounterOnCollisionFactory extends AbstractInsertCounterFactory {
	
	/** Creates a {@link InsertCounterOnCollisionFactory} */
	public InsertCounterOnCollisionFactory() {
		super(RuleType.INSERT_COUNTER_ON_COLLISION);
		super.setValid(true);
	}

	@Override
	protected Rule createConfiguredRule() {
		return getRule(super.startCount, super.padding);
	}
	
	@Override
	protected void checkConfiguration() throws RuntimeException {}
	
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
	 */
	public static Rule getRule(int startCount, int padding) {
		return new InsertCounterOnCollisionRule(startCount, padding);
	}
	
	/** Rule that insert a counter in case of name collision */
	private static class InsertCounterOnCollisionRule extends AbstractCounterRule {
		
		/**	Map of names, used to check collision */
		private final Map<String, Object> names = new HashMap<String, Object>();
		
		/** @see AbstractCounterRule#AbstractCounterRule(int, int) */
		public InsertCounterOnCollisionRule(int countStart, int padding) {
			super(countStart, padding);
		}

		@Override
		public FileModelItem apply(FileModelItem file) {
			String name = file.getNewFullName();
			if (this.names.containsKey(name)) {
				// append counter
				file.setName(file.getName() + " " + getCount());
				
			} else {
				// store name
				this.names.put(name, null);
			}
			
			return file;
		}
		
		@Override
		public void reset() {
			super.reset();
			this.names.clear();
		}
	}
}