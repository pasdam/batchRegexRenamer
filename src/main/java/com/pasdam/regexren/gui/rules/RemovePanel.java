package com.pasdam.regexren.gui.rules;

import com.pasdam.regexren.controller.LocaleManager;

/**
 * Panel used to configure a "Replace..." rule
 * 
 * @author paco
 * @version 0.1
 */
public class RemovePanel extends AbstractReplacePanel {

	private static final long serialVersionUID = 2974732201758479386L;

	/**	Description pattern */
	private String description;

	/** Create the panel */
	public RemovePanel(ReplaceFactory ruleFactory) {
		super(ruleFactory);
	}

	@Override
	public void localeChanged(LocaleManager localeManager) {
		super.localeChanged(localeManager);
		super.textToReplaceLabel.setText(localeManager.getString("Rule.remove"));
		this.description = localeManager.getString("Rule.remove.description");
	}

	@Override
	protected String getDescription() {
		String textToReplace = super.ruleFactory.getTextToReplace();
		return String.format(
				this.description,
				textToReplace != null ? textToReplace : "",
				super.targetCombobox.getSelectedItem());
	}
}
