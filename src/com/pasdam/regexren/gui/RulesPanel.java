package com.pasdam.regexren.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.pasdam.gui.swing.dragAndDropPanels.DragAndDropTransferHandler;
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
	
	/** Cursor used to indicate a droppable area */
	private static final Cursor CURSOR_DROPPABLE     = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	/** Cursor used to indicate a not droppable area */
	private static final Cursor CURSOR_NOT_DROPPABLE = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	
	// UI components
	private final JPanel content;

	/** Create the panel */
	public RulesPanel() {
		// create and set content
		this.content = new JPanel();
		this.content.setLayout(new BoxLayout(this.content, BoxLayout.Y_AXIS));
		this.content.setTransferHandler(new DragAndDropTransferHandler());
		this.content.setDropTarget(new DropTarget(this.content, new InternalEventHandler()));
		setViewportView(this.content);
		
		// register itself as rules listener
		RulesManager rulesManager = ApplicationManager.getInstance().getRulesManager();
		rulesManager.addRulesListener(this);
		// load rules
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
		ExpandableRule expandableRule = new ExpandableRule(ruleContentPanel);
		expandableRule.localeChanged(ApplicationManager.getInstance().getLocaleManager());
		this.content.add(expandableRule, index);
		
		// force re-layout
		validate();
		repaint();
		
		// scroll down to the last element
		getVerticalScrollBar().setValue(getVerticalScrollBar().getMaximum());
	}

	@Override
	public void ruleRemoved(int index) {
		if (LogManager.ENABLED) LogManager.trace("RulesPanel.ruleRemoved> Removing rule at index " + index);
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
	public void localeChanged(LocaleManager localeManager) {
		Component[] components = this.content.getComponents();
		for (Component component : components) {
			((ExpandableRule) component).localeChanged(localeManager);
		}
	}

	/**	Class that handles internal events */
	private class InternalEventHandler implements DropTargetListener {

		@Override
		public void dragExit(DropTargetEvent event) {
			RulesPanel.this.setCursor(CURSOR_NOT_DROPPABLE);
		}

		@Override
		public void dragOver(DropTargetDragEvent event) {
			if (!RulesPanel.this.getCursor().equals(CURSOR_DROPPABLE)) {
				RulesPanel.this.setCursor(CURSOR_DROPPABLE);
	        }
		}

		@Override
		public void drop(DropTargetDropEvent event) {
			// reset cursor
			RulesPanel.this.setCursor(Cursor.getDefaultCursor());
			
			try {
				// evaluate the index position where move the rule
				int to = -1;
				int y = event.getLocation().y;
				for (Component component : RulesPanel.this.content.getComponents()) {
					if (component.getY() > y) {
						break;
					} else {
						to++;
					}
				}
				
				AbstractRuleFactory droppedRule = ((AbstractRuleFactory) event.getTransferable().getTransferData(new DataFlavor(ExpandableRule.MIME_TYPE_RULE_FACTORY)));
				RulesManager rulesManager = ApplicationManager.getInstance().getRulesManager();
				int from = rulesManager.indexOf(droppedRule);

				if (to >= 0 && to != from) {
					rulesManager.move(from, to);
				}
				
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		
		/** Event ignored */
		@Override
		public void dragEnter(DropTargetDragEvent event) {}

		/** Event ignored */
		@Override
		public void dropActionChanged(DropTargetDragEvent event) {}
	}
}
