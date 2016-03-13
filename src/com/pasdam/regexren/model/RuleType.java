package com.pasdam.regexren.model;

/**
 * Enumeration of all rule types
 * 
 * @author paco
 * @version 0.1
 */
public enum RuleType {

	/**	Rule that allows to change the case */
	CHANGE_CASE							(0),
	
	/**	Rule that allows to insert a counter at specific position */
	INSERT_COUNTER_AT_POSITION			(1),
	
	/**	Rule that allows to insert a counter before/after a specific phrase */
	INSERT_COUNTER_BEFORE_AFTER_PATTERN	(2),
	
	/**	Rule that allows to insert a counter on name collision */
	INSERT_COUNTER_ON_COLLISION			(3),
	
	/**	Rule that allows to insert a text at specific position */
	INSERT_TEXT_AT_POSITION				(4),
	
	/**	Rule that allows to insert a text before/after a specific phrase */
	INSERT_TEXT_BEFORE_AFTER_PATTERN	(5),
	
	/**	Rule that allows to remove specific text */
	REMOVE								(6),
	
	/**	Rule that allows to replace specific text*/
	REPLACE								(7),

	/**	Rule that allows to replace specific text*/
	MOVE								(8);

	/** ID of the rule, used to store it in a script file */
	private final int id;
	
	/**
	 * Private constructor used to specify an unique id for each enum value
	 * 
	 * @param id
	 *            ID to associate to the rule
	 */
	private RuleType(int id) {
		this.id = id;
	}
	
	/**
	 * Parse the input id and return the related rule type
	 * 
	 * @param id
	 *            rule id
	 * @return the {@link RuleType} identified by <i>id</i>
	 */
	public static RuleType parseId(int id) {
		switch (id) {
			case 0:
				return CHANGE_CASE;
			
			case 1:
				return INSERT_COUNTER_AT_POSITION;
				
			case 2:
				return INSERT_COUNTER_BEFORE_AFTER_PATTERN;
			
			case 3:
				return INSERT_COUNTER_ON_COLLISION;
			
			case 4:
				return INSERT_TEXT_AT_POSITION;
			
			case 5:
				return INSERT_TEXT_BEFORE_AFTER_PATTERN;
			
			case 6:
				return REMOVE;
				
			case 7:
				return REPLACE;
			
			case 8:
				return MOVE;

			default:
				return null;
		}
	}
	
	/**
	 * Returns the id of the rule type
	 * 
	 * @return the id of the rule type
	 */
	public int getId() {
		return this.id;
	}
}
