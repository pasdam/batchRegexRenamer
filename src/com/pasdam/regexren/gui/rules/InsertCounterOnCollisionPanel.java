package com.pasdam.regexren.gui.rules;

import com.pasdam.regexren.controller.LocaleManager;

/**
 * Panel used to configure a "Insert counter on collision" rule
 * 
 * @author paco
 * @version 0.1
 */
public class InsertCounterOnCollisionPanel extends AbstractInsertCounterPanel<InsertCounterOnCollisionFactory> {

	private static final long serialVersionUID = -566951250722597010L;

	/**	Description pattern */
	private String description;

	/** Create the panel */
	public InsertCounterOnCollisionPanel(InsertCounterOnCollisionFactory ruleFactory) {
		super(ruleFactory);
	}
	
	@Override
	public void localeChanged(LocaleManager localeManager) {
		super.localeChanged(localeManager);
		
		this.description = localeManager.getString("Rule.insertCounterCollision.description");
	}

	@Override
	protected String getDescription() {
		return String.format(
				this.description,
				super.ruleFactory.getStartCount()
		);
	}
}
