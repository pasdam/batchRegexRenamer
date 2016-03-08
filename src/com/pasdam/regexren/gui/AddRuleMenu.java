package com.pasdam.regexren.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import com.pasdam.regexren.controller.ApplicationManager;
import com.pasdam.regexren.controller.LocaleManager;
import com.pasdam.regexren.controller.LocaleManager.Localizable;
import com.pasdam.regexren.controller.LogManager;
import com.pasdam.regexren.gui.rules.AbstractRuleFactory;
import com.pasdam.regexren.gui.rules.ChangeCaseFactory;
import com.pasdam.regexren.gui.rules.ReplaceFactory;
import com.pasdam.regexren.model.RuleType;

/** Menu that shows all available rule types */
class AddRuleMenu extends JPopupMenu implements ActionListener, Localizable {
	
	private static final long serialVersionUID = 8219513433720040595L;

	// GUI elements
	private final JMenuItem insertTextAtPositionItem;
	private final JMenuItem insertTextBeforeAfterItem;
	private final JMenuItem insertCounterAtPositionItem;
	private final JMenuItem replaceItem;
	private final JMenuItem changeCaseItem;
	private final JMenuItem removeItem;
	private final JMenuItem insertCounterBeforeAfterItem;
	
	// Rule type values used as id of the menu item: at runtime the ordinal
	// value of each type doesn't change so it is safe to use it as ID
	private static final RuleType[] RULE_TYPES = RuleType.values();
	
	/** Creates GUI elements */
	public AddRuleMenu() {
		// Insert text at position
		this.insertTextAtPositionItem = new JMenuItem();
		this.insertTextAtPositionItem.setActionCommand("" + RuleType.INSERT_TEXT_AT_POSITION.ordinal());
		this.insertTextAtPositionItem.addActionListener(this);
		add(this.insertTextAtPositionItem);

		// Insert text befor/after
		this.insertTextBeforeAfterItem = new JMenuItem();
		this.insertTextBeforeAfterItem.setActionCommand("" + RuleType.INSERT_TEXT_BEFORE_AFTER_PATTERN.ordinal());
		this.insertTextBeforeAfterItem.addActionListener(this);
		add(this.insertTextBeforeAfterItem);

		// Insert counter at position
		this.insertCounterAtPositionItem = new JMenuItem();
		this.insertCounterAtPositionItem.setActionCommand("" + RuleType.INSERT_COUNTER_AT_POSITION.ordinal());
		this.insertCounterAtPositionItem.addActionListener(this);
		add(this.insertCounterAtPositionItem);

		// Insert counter before/after
		this.insertCounterBeforeAfterItem = new JMenuItem();
		this.insertCounterBeforeAfterItem.setActionCommand("" + RuleType.INSERT_COUNTER_BEFORE_AFTER_PATTERN.ordinal());
		this.insertCounterBeforeAfterItem.addActionListener(this);
		add(this.insertCounterBeforeAfterItem);
		
		// separator
		add(new JSeparator(JSeparator.HORIZONTAL));

		// Replace
		this.replaceItem = new JMenuItem();
		this.replaceItem.setActionCommand("" + RuleType.REPLACE.ordinal());
		this.replaceItem.addActionListener(this);
		add(this.replaceItem);
		
		// Remove
		this.removeItem = new JMenuItem();
		this.removeItem.setActionCommand("" + RuleType.REMOVE.ordinal());
		this.removeItem.addActionListener(this);
		add(this.removeItem);

		// separator
		add(new JSeparator(JSeparator.HORIZONTAL));
		
		// Change case
		this.changeCaseItem = new JMenuItem();
		this.changeCaseItem.setActionCommand("" + RuleType.CHANGE_CASE.ordinal());
		this.changeCaseItem.addActionListener(this);
		add(this.changeCaseItem);
	}

	@Override
	public void localeChanged(LocaleManager localeManager) {
    	this.changeCaseItem				  .setText(localeManager.getString("AddRuleMenu.changeCaseItem.label"));
    	this.insertCounterAtPositionItem  .setText(localeManager.getString("AddRuleMenu.insertCounterAtPositionItem.label"));
    	this.insertCounterBeforeAfterItem .setText(localeManager.getString("AddRuleMenu.insertCounterBeforeAfterItem.label"));
    	this.insertTextAtPositionItem	  .setText(localeManager.getString("AddRuleMenu.insertTextAtPositionItem.label"));
    	this.insertTextBeforeAfterItem	  .setText(localeManager.getString("AddRuleMenu.insertTextBeforeAfterItem.label"));
    	this.removeItem					  .setText(localeManager.getString("AddRuleMenu.removeItem.label"));
    	this.replaceItem				  .setText(localeManager.getString("AddRuleMenu.replaceItem.label"));
	}

	/** Applies selected filter*/
	@Override
	public void actionPerformed(ActionEvent e) {
		AbstractRuleFactory rule;
		int componentId = Integer.valueOf(e.getActionCommand().trim()).intValue();
		if (LogManager.ENABLED) LogManager.trace("AddRuleMenu.mouseClicked> Clicked menu item " + componentId);
		switch (RULE_TYPES[componentId]) {
//			case INSERT_TEXT_AT_POSITION:
//				rule = new InsertTextAtPositionFactory();
//				break;
//				
//			case INSERT_TEXT_BEFORE_AFTER:
//				rule = new InsertTextBeforeAfterFactory();
//				break;
//			
//			case INSERT_COUNTER_AT_POSITION:
//				rule = new InsertCounterAtPositionFactory();
//				break;
//			
//			case INSERT_COUNTER_BEFORE_AFTER:
//				rule = new InsertCounterBeforeAfterFactory();
//				break;
			
			case REPLACE:
				rule = new ReplaceFactory();
				break;

			case REMOVE:
				rule = new ReplaceFactory(true);
				break;
			
			case CHANGE_CASE:
				rule = new ChangeCaseFactory();
				break;
			
			default:
				return;
		}
		
		rule.setEnabled(true);
		
		ApplicationManager.getInstance().getRulesManager().addRule(rule);
	}
}