package com.pasdam.regexren.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import com.pasdam.gui.swing.panel.ExpandableItem;
import com.pasdam.regexren.controller.ApplicationManager;
import com.pasdam.regexren.controller.LocaleManager;
import com.pasdam.regexren.controller.LocaleManager.Localizable;
import com.pasdam.regexren.gui.RuleContentPanel.RuleContentListener;
import com.pasdam.regexren.gui.rules.AbstractRuleFactory;
import com.pasdam.regexren.gui.rules.RuleFactoryListener;

/**
 * @author Paco
 * @version 1.0
 */
@SuppressWarnings("serial")
public class ExpandableRule extends ExpandableItem implements RuleFactoryListener, Localizable, ItemListener, RuleContentListener {
	
	private static final String STRING_KEY_INVALID_PARAMETERS = "Error.RulesPanel.invalidParameters";

	// UI components
	public JLabel titleLabel;
	public JLabel validLabel;
	private JCheckBox selectCheckbox;
	// private RuleMenu menu;
	
	/** The rule factory related to the contained panel */
	private AbstractRuleFactory ruleFactory;
	
	/** Create the panel */
	public ExpandableRule(RuleContentPanel ruleContentPanel) {
		this.ruleFactory = ruleContentPanel.getRuleFactory();
		
		// create title panel
		final Box titlePanel = Box.createHorizontalBox();
		
		// create and add checkbox
		this.selectCheckbox = new JCheckBox();
		this.selectCheckbox.setSelected(ruleFactory.isEnabled());
		this.selectCheckbox.addItemListener(this);
		titlePanel.add(this.selectCheckbox);
		
		// create and add title label
		this.titleLabel = new JLabel();
		titlePanel.add(this.titleLabel);
		
		// add spacer
		titlePanel.add(Box.createHorizontalGlue());
		
		// add valid indicator
		this.validLabel = new JLabel();
		titlePanel.add(this.validLabel);

		// set title panel
		setTitleComponent(titlePanel);
		
		// add context menu
		// menu = new RuleMenu(this);
		// titlePanel.add(menu);
		
		// read initial rule's data
		isValidChanged(this.ruleFactory.isValid());

		// add rule's content
		setContent(ruleContentPanel);
		
		// set itself as content listener
		ruleContentPanel.setContentListener(this);
		this.ruleFactory.setListener(this);
	}

	@Override
	public void isValidChanged(boolean valid) {
		if (valid) {
			this.validLabel.setIcon(new ImageIcon("images" + File.separator + "green_button.png"));
		
		} else {
			this.validLabel.setIcon(new ImageIcon("images" + File.separator + "red_button.png"));
			this.validLabel.setToolTipText(ApplicationManager.getInstance().getLocaleManager().getString(STRING_KEY_INVALID_PARAMETERS));
		}
	}

	@Override
	public void descriptionChanged(String description) {
		this.titleLabel.setText(description);
	}

	@Override
	public void localeChanged(LocaleManager localeManager) {
		String toolTip = this.validLabel.getToolTipText();
		if (toolTip != null && toolTip.length() > 0) {
			this.validLabel.setToolTipText(localeManager.getString(STRING_KEY_INVALID_PARAMETERS));
		}
	}

	@Override
	public void itemStateChanged(ItemEvent event) {
		this.ruleFactory.setEnabled(this.selectCheckbox.isSelected());
	}
}
