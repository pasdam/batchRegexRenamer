package com.pasdam.regexren.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.pasdam.regexren.controller.ApplicationManager;
import com.pasdam.regexren.controller.LocaleManager;
import com.pasdam.regexren.controller.LocaleManager.Localizable;
import com.pasdam.regexren.controller.RulesManager;
import com.pasdam.regexren.gui.rules.AbstractRuleFactory;

/**
 * Context menu for expandable rules
 * 
 * @author paco
 * @version 0.1
 */
public class RuleMenu extends JPopupMenu implements Localizable {
	
	private static final long serialVersionUID = -8735384067484992307L;

	// UI components
	private final JMenuItem moveUpItem;
	private final JMenuItem moveDownItem;
	private final JMenuItem deleteItem;
	
	/**	Rule factory related to the menu */
	private AbstractRuleFactory ruleFactory;
	
	/** Creates the menu */
	public RuleMenu(AbstractRuleFactory ruleFactory) {
		this.ruleFactory = ruleFactory;
		
		// create items
		this.moveUpItem = new JMenuItem();
		this.moveDownItem = new JMenuItem();
		this.deleteItem = new JMenuItem();
		
		// add items to the menu
		add(this.moveUpItem);
		add(this.moveDownItem);
		add(this.deleteItem);
		
		// create and set listener
		InternalEventHandler eventHandler = new InternalEventHandler();
		this.moveUpItem.addActionListener(eventHandler);
		this.moveDownItem.addActionListener(eventHandler);
		this.deleteItem.addActionListener(eventHandler);
	}
	
	@Override
	public void localeChanged(LocaleManager localeManager) {
		this.moveUpItem.setText(localeManager.getString("RulesPanel.menu.moveUp"));
    	this.moveDownItem.setText(localeManager.getString("RulesPanel.menu.moveDown"));
    	this.deleteItem.setText(localeManager.getString("RulesPanel.menu.delete"));
	}
	
	/** Class that handles internal events */
	private class InternalEventHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			RulesManager rulesManager = ApplicationManager.getInstance().getRulesManager();
			if (source == RuleMenu.this.moveUpItem) {
				int index = rulesManager.indexOf(RuleMenu.this.ruleFactory);
				if (index > 0) { // if it is not the first
					rulesManager.move(index, index-1);
				}
				
			} else if (source == RuleMenu.this.moveDownItem) {
				int index = rulesManager.indexOf(RuleMenu.this.ruleFactory);
				if (index < rulesManager.getRulesCount()-1) { // if it is not the last
					rulesManager.move(index, index+1);
				}
				
			} else if (source == RuleMenu.this.deleteItem) {
				rulesManager.remove(RuleMenu.this.ruleFactory);
			}
		}
	}
}
