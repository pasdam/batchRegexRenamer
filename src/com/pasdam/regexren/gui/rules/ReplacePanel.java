package com.pasdam.regexren.gui.rules;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.pasdam.regexren.controller.LocaleManager;

/**
 * Panel used to configure a "Replace..." rule
 * 
 * @author paco
 * @version 0.1
 */
public class ReplacePanel extends AbstractReplacePanel {

	private static final long serialVersionUID = -7951879690934241967L;

	// UI components
	private JLabel textToInsertLabel;
	private JTextField textToInsertText;
	
	/** Handler of internal events */
	private final InternalEventHandler eventHandler;
	
	/**	Description pattern */
	private String description;
	
	/** Create the panel */
	public ReplacePanel(ReplaceFactory ruleFactory) {
		super(ruleFactory);
		
		// create event handler
		this.eventHandler = new InternalEventHandler();
		
		// create and add text to insert label to the second row
		this.textToInsertLabel = new JLabel();
		super.row2Panel.add(this.textToInsertLabel, 0);
		
		// create and add space to the second row
		super.row2Panel.add(Box.createHorizontalStrut(FIXED_SPACE_SHORT), 1);
		
		// create and add text to insert field to the second row
		this.textToInsertText = new JTextField();
		this.textToInsertText.setPreferredSize(new Dimension(WIDGET_TEXT_MIN_WIDTH, WIDGET_HEIGHT));
		this.textToInsertText.getDocument().addDocumentListener(this.eventHandler);
		super.row2Panel.add(this.textToInsertText, 2);
		
		// create and add space to the second row
		this.row2Panel.add(Box.createHorizontalStrut(FIXED_SPACE_LONG), 3);
		
		// read initial ruls's values
		this.textToInsertText.setText(this.ruleFactory.getTextToInsert());
	}

	@Override
	public void localeChanged(LocaleManager localeManager) {
		super.localeChanged(localeManager);
		super.textToReplaceLabel.setText(localeManager.getString("Rule.replace"));
		this.textToInsertLabel.setText(localeManager.getString("Rule.with"));
		this.description = localeManager.getString("Rule.replace.description");
	}

	@Override
	protected String getDescription() {
		String textToReplace = super.ruleFactory.getTextToReplace();
		String textToInsert = super.ruleFactory.getTextToInsert();
		return String.format(
				this.description,
				textToReplace != null ? textToReplace : "",
				textToInsert != null ? textToInsert : "",
				this.targetCombobox.getSelectedItem());
	}

	/** Class that handles internal events */
	private class InternalEventHandler implements DocumentListener {
		
		@Override
		public void changedUpdate(DocumentEvent e) {
			ReplacePanel.super.ruleFactory.setTextToInsert(ReplacePanel.this.textToInsertText.getText());
			ReplacePanel.super.configurationChanged();
		}
	
		@Override
		public void insertUpdate(DocumentEvent e) {
			changedUpdate(e);
		}
	
		@Override
		public void removeUpdate(DocumentEvent e) {
			changedUpdate(e);
		}
	}
}
