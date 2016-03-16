package com.pasdam.regexren.gui;

import java.awt.Component;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.pasdam.regexren.controller.ApplicationManager;
import com.pasdam.regexren.controller.LocaleManager;
import com.pasdam.regexren.controller.LocaleManager.Localizable;
import com.pasdam.regexren.controller.LogManager;
import com.pasdam.regexren.controller.RulesManager;
import com.pasdam.regexren.controller.RulesManager.RulesListener;
import com.pasdam.regexren.gui.rules.AbstractRuleFactory;
import com.pasdam.regexren.gui.rules.ChangeCaseFactory;
import com.pasdam.regexren.gui.rules.ChangeCasePanel;
import com.pasdam.regexren.gui.rules.InsertCounterAtPositionFactory;
import com.pasdam.regexren.gui.rules.InsertCounterAtPositionPanel;
import com.pasdam.regexren.gui.rules.InsertCounterBeforeAfterFactory;
import com.pasdam.regexren.gui.rules.InsertCounterBeforeAfterPanel;
import com.pasdam.regexren.gui.rules.InsertCounterOnCollisionFactory;
import com.pasdam.regexren.gui.rules.InsertCounterOnCollisionPanel;
import com.pasdam.regexren.gui.rules.InsertTextAtPositionFactory;
import com.pasdam.regexren.gui.rules.InsertTextAtPositionPanel;
import com.pasdam.regexren.gui.rules.InsertTextBeforeAfterFactory;
import com.pasdam.regexren.gui.rules.InsertTextBeforeAfterPanel;
import com.pasdam.regexren.gui.rules.MoveTextBeforeAfterFactory;
import com.pasdam.regexren.gui.rules.MoveTextBeforeAfterPanel;
import com.pasdam.regexren.gui.rules.RemovePanel;
import com.pasdam.regexren.gui.rules.ReplaceFactory;
import com.pasdam.regexren.gui.rules.ReplacePanel;

public class RulesPanel extends JScrollPane implements RulesListener, Localizable {

	private static final long serialVersionUID = -6600541345376059836L;
	
	// UI components
	private final JPanel content;

	/** Create the panel */
	public RulesPanel() {
		// set content
		this.content = new JPanel();
		this.content.setLayout(new BoxLayout(this.content, BoxLayout.Y_AXIS));
		setViewportView(this.content);
		
		// register itself as rules listener
		RulesManager rulesManager = ApplicationManager.getInstance().getRulesManager();
		rulesManager.addRulesListener(this);
		rulesChanged(rulesManager.getRulesList());
	}

	/** Collapses all rules */
	private void collapseAll(){
		Component[] components = this.content.getComponents();
		for (Component component : components) {
			((ExpandableRule) component).collapse();
		}
	}

	@Override
	public void ruleAdded(int index, AbstractRuleFactory addedRule) {
		if (LogManager.ENABLED) LogManager.trace("RulesPanel.ruleAdded> Adding rule (at index " + index + "): " + addedRule.getType());
		RuleContentPanel<?> ruleContentPanel;
		
		switch (addedRule.getType()) {
			case INSERT_TEXT_AT_POSITION:
				ruleContentPanel = new InsertTextAtPositionPanel((InsertTextAtPositionFactory) addedRule);
				break;

			case INSERT_TEXT_BEFORE_AFTER_PATTERN:
				ruleContentPanel = new InsertTextBeforeAfterPanel((InsertTextBeforeAfterFactory) addedRule);
				break;

			case INSERT_COUNTER_AT_POSITION:
				ruleContentPanel = new InsertCounterAtPositionPanel((InsertCounterAtPositionFactory) addedRule);
				break;
				
			case INSERT_COUNTER_BEFORE_AFTER_PATTERN:
				ruleContentPanel = new InsertCounterBeforeAfterPanel((InsertCounterBeforeAfterFactory) addedRule);
				break;

			case INSERT_COUNTER_ON_COLLISION:
				ruleContentPanel = new InsertCounterOnCollisionPanel((InsertCounterOnCollisionFactory) addedRule);
				break;
		
			case MOVE:
				ruleContentPanel = new MoveTextBeforeAfterPanel((MoveTextBeforeAfterFactory) addedRule);
				break;

			case REPLACE:
				ruleContentPanel = new ReplacePanel((ReplaceFactory) addedRule);
				break;

			case REMOVE:
				ruleContentPanel = new RemovePanel((ReplaceFactory) addedRule);
				break;

			case CHANGE_CASE:
				ruleContentPanel = new ChangeCasePanel((ChangeCaseFactory) addedRule);
				break;
				
			default:
				return;
		}

		// add rule panel
		ruleContentPanel.localeChanged(ApplicationManager.getInstance().getLocaleManager());
		this.content.add(new ExpandableRule(ruleContentPanel));
		
		// force re-layout
		validate();
		repaint();
		
		// scroll down to the last element
		getVerticalScrollBar().setValue(getVerticalScrollBar().getMaximum());
	}

	@Override
	public void ruleRemoved(int index) {
		this.content.remove(index);

		// force re-layout
		validate();
		repaint();
	}

	@Override
	public void rulesChanged(List<AbstractRuleFactory> rulesList) {
		this.content.removeAll();
		
		if (rulesList.size() > 0) {
			for (int i = 0; i < rulesList.size(); i++) {
				ruleAdded(i, rulesList.get(i));
			}
			collapseAll();
		}

		// force re-layout: this is usefull when the list is cleared, since no
		// #ruleAdd is called, so no repaint is performed
		validate();
		repaint();
	}

	@Override
	public void ruleMoved(int oldPosition, int newPosition, AbstractRuleFactory rule) {
		// TODO Missing implementation
		if (LogManager.ENABLED) LogManager.warning("RulesPanel.ruleMoved> Missing implementation");
	}

	@Override
	public void localeChanged(LocaleManager localeManager) {
		Component[] components = this.content.getComponents();
		for (Component component : components) {
			((ExpandableRule) component).localeChanged(localeManager);
		}
	}
}
