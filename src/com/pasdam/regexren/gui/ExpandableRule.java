package com.pasdam.regexren.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import com.pasdam.gui.swing.panel.ExpandableItem;
import com.pasdam.regexren.controller.LocaleManager;
import com.pasdam.regexren.controller.LocaleManager.Localizable;
import com.pasdam.regexren.gui.rules.RuleFactoryListener;

/**
 * Expandable panel that contains a {@link RuleContentPanel}
 * 
 * @author paco
 * @version 0.1
 */
public class ExpandableRule extends ExpandableItem implements RuleFactoryListener, Localizable, ItemListener {
	
	private static final long serialVersionUID = 8443447294086054937L;

	// Localizable string keys
	private static final String STRING_KEY_INVALID_PARAMETERS = "Error.RulesPanel.invalidParameters";

	// UI components
	public JLabel titleLabel;
	public JLabel validLabel;
	private JCheckBox selectCheckbox;
	private RuleMenu menu;
	
	/** The rule factory related to the contained panel */
	private RuleContentPanel<?> ruleContentPanel;
	
	/** Tooltip to show for invalid rules  */
	private String tooltipInvalid;
	
	/** Create the panel */
	public ExpandableRule(RuleContentPanel<?> ruleContentPanel) {
		this.ruleContentPanel = ruleContentPanel;
		
		// create title panel
		final Box titlePanel = Box.createHorizontalBox();
		
		// create and add checkbox
		this.selectCheckbox = new JCheckBox();
		this.selectCheckbox.setSelected(ruleContentPanel.getRuleFactory().isEnabled());
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
		this.menu = new RuleMenu(this.ruleContentPanel.getRuleFactory());
		setContextMenu(this.menu);
		
		// add rule's content
		setContent(ruleContentPanel);
		
		// set itself as content listener
		ruleContentPanel.getRuleFactory().addConfigurationListener(this);
	}
	
	@Override
	public void configurationChanged(boolean valid) {
		this.titleLabel.setText(this.ruleContentPanel.getDescription());

		if (valid) {
			this.validLabel.setIcon(new ImageIcon("images" + File.separator + "green_button.png"));
		
		} else {
			this.validLabel.setIcon(new ImageIcon("images" + File.separator + "red_button.png"));
			this.validLabel.setToolTipText(this.tooltipInvalid);
		}
	}

	@Override
	public void localeChanged(LocaleManager localeManager) {
		this.ruleContentPanel.localeChanged(localeManager);
		this.menu.localeChanged(localeManager);

		this.tooltipInvalid = localeManager.getString(STRING_KEY_INVALID_PARAMETERS);
		this.validLabel.setToolTipText(this.ruleContentPanel.getRuleFactory().isValid()
				? ""
				: this.tooltipInvalid);
		this.titleLabel.setText(this.ruleContentPanel.getDescription());
	}

	@Override
	public void itemStateChanged(ItemEvent event) {
		this.ruleContentPanel.getRuleFactory().setEnabled(this.selectCheckbox.isSelected());
	}
}
