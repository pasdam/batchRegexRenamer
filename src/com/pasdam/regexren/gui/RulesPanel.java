package com.pasdam.regexren.gui;

import java.awt.Component;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.pasdam.regexren.controller.ApplicationManager;
import com.pasdam.regexren.controller.LogManager;
import com.pasdam.regexren.controller.RulesManager;
import com.pasdam.regexren.controller.RulesManager.RulesListener;
import com.pasdam.regexren.gui.rules.AbstractRuleFactory;
import com.pasdam.regexren.gui.rules.ChangeCaseFactory;
import com.pasdam.regexren.gui.rules.ChangeCasePanel;
import com.pasdam.regexren.gui.rules.InsertCounterAtPositionFactory;
import com.pasdam.regexren.gui.rules.InsertCounterAtPositionPanel;
import com.pasdam.regexren.gui.rules.RemovePanel;
import com.pasdam.regexren.gui.rules.ReplaceFactory;
import com.pasdam.regexren.gui.rules.ReplacePanel;

public class RulesPanel extends JScrollPane implements RulesListener {

	private static final long serialVersionUID = -6600541345376059836L;
	
	// TODO: add localizable support for rules panels

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
			((ExpandableRule) component).collapse();;
		}
	}

	@Override
	public void ruleAdded(int index, AbstractRuleFactory addedRule) {
		if (LogManager.ENABLED) LogManager.trace("RulesPanel.ruleAdded> Adding rule (at index " + index + "): " + addedRule.getType());
		RuleContentPanel ruleContentPanel;
		
		switch (addedRule.getType()) {
	//		case INSERT_TEXT_AT_POSITION:
	//			ruleContentPanel = new InsertTextAtPositionPanel(() addedRule);
	//			break;

	//		case INSERT_TEXT_BEFORE_AFTER:
	//			ruleContentPanel = new InsertTextBeforeAfterPanel(() addedRule);
	//			break;

			case INSERT_COUNTER_AT_POSITION:
				ruleContentPanel = new InsertCounterAtPositionPanel((InsertCounterAtPositionFactory) addedRule);
				break;

	//		case INSERT_COUNTER_BEFORE_AFTER:
	//			ruleContentPanel = new InsertCounterBeforeAfterPanel(() addedRule);
	//			break;
		
	//		case MOVE:
	//			ruleContentPanel = new MoveTextBeforeAfterPanel(() addedRule);
	//			break;

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
		
		revalidate();
		repaint();
	}

	@Override
	public void rulesChanged(List<AbstractRuleFactory> rulesList) {
		this.content.removeAll();
		for (int i = 0; i < rulesList.size(); i++) {
			ruleAdded(i, rulesList.get(i));
		}
		
		collapseAll();
	}

	@Override
	public void ruleMoved(int oldPosition, int newPosition, AbstractRuleFactory rule) {
		// TODO Missing implementation
		if (LogManager.ENABLED) LogManager.warning("RulesPanel.ruleMoved> Missing implementation");
	}
}
