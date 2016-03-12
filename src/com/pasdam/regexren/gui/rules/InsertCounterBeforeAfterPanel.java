package com.pasdam.regexren.gui.rules;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.regex.PatternSyntaxException;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.pasdam.gui.swing.widgets.SteppedComboBox;
import com.pasdam.regexren.controller.LocaleManager;
import com.pasdam.regexren.controller.LogManager;

/**
 * Panel used to configure a "Insert counter before/after pattern" rule
 * 
 * @author paco
 * @version 0.1
 */
public class InsertCounterBeforeAfterPanel extends AbstractInsertCounterPanel<InsertCounterBeforeAfterFactory> {
	
	private static final long serialVersionUID = -3678469494389072338L;

	/** Used to specify to insert before input text */
	public static final int BEFORE = 0;
	/** Used to specify to insert after input text */
	public static final int AFTER  = BEFORE + 1;
	
	// GUI elements
	private JCheckBox matchCaseChackbox;
	private JCheckBox regexCheckbox;
	private JTextField textToSearchField;
	private SteppedComboBox beforeAfterCombobox;
	
	/** Array of "Before/After" combobox values */
	private final String[] beforeAfter = new String[2];
	
	/** Handler of internal events */
	private final InternalEventHandler eventHandler;
	
	/**	Description pattern */
	private String description;

	/**
	 * Create the panel.
	 */
	public InsertCounterBeforeAfterPanel(InsertCounterBeforeAfterFactory ruleFactory) {
		super(ruleFactory);
		
		// create event handler
		this.eventHandler = new InternalEventHandler();

		this.textToSearchField = new JTextField();
		this.textToSearchField.getDocument().addDocumentListener(this.eventHandler);
		this.textToSearchField.setColumns(10);
		add(this.textToSearchField);
		
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));

		this.regexCheckbox = new JCheckBox();
		this.regexCheckbox.addItemListener(this.eventHandler);
		add(this.regexCheckbox);
		
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		this.matchCaseChackbox = new JCheckBox();
		this.matchCaseChackbox.addItemListener(this.eventHandler);
		add(this.matchCaseChackbox);
	}
	
	private void updateCombos() {
		if (this.beforeAfterCombobox != null) {
			remove(this.beforeAfterCombobox);
		}
		this.beforeAfterCombobox = new SteppedComboBox(new DefaultComboBoxModel<String>(beforeAfter));
		this.beforeAfterCombobox.setPreferredSize(new Dimension(WIDGET_TEXT_MIN_WIDTH, WIDGET_HEIGHT));
		this.beforeAfterCombobox.setMaximumSize(new Dimension(this.beforeAfterCombobox.getPreferredSize()));
		this.beforeAfterCombobox.setSelectedIndex(super.getRuleFactory().isBeforePattern() ? BEFORE : AFTER);
		this.beforeAfterCombobox.addActionListener(this.eventHandler);
		add(this.beforeAfterCombobox, 8);
	}
	
	@Override
	public void localeChanged(LocaleManager localeManager) {
		super.localeChanged(localeManager);
		
		this.regexCheckbox.setText(localeManager.getString("Rule.regex"));
		this.regexCheckbox.setToolTipText(localeManager.getString("Rule.regex.tooltip"));
		this.matchCaseChackbox.setText(localeManager.getString("Rule.matchCase"));
		this.matchCaseChackbox.setToolTipText(localeManager.getString("Rule.matchCase.tooltip"));

		this.description    = localeManager.getString("Rule.insertCounterPattern.description");
		this.beforeAfter[0] = localeManager.getString("Rule.before");
		this.beforeAfter[1] = localeManager.getString("Rule.after");
		updateCombos();
	}

	@Override
	protected String getDescription() {
		return String.format(
				this.description,
				super.ruleFactory.getStartCount(),
				this.beforeAfterCombobox.getSelectedItem(),
				this.textToSearchField.getText()
		);
	}
	
	/** Class that handles internal events */
	private class InternalEventHandler implements ActionListener, DocumentListener, ItemListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			InsertCounterBeforeAfterPanel.super.ruleFactory.setBeforePattern(
					InsertCounterBeforeAfterPanel.this.beforeAfterCombobox.getSelectedIndex() == InsertCounterBeforeAfterPanel.BEFORE);
		}
		
		@Override
		public void changedUpdate(DocumentEvent e) {
			try {
				InsertCounterBeforeAfterPanel.super.ruleFactory.setTextToSearch(InsertCounterBeforeAfterPanel.this.textToSearchField.getText());
				
			} catch (PatternSyntaxException exception) {
				// TODO: notify user, i.e. highlight input field 
				if (LogManager.ENABLED) LogManager.error("InsertCounterBeforeAfterPanel.changedUpdate> Invalid pattern: " + InsertCounterBeforeAfterPanel.this.textToSearchField.getText());
			} catch (NullPointerException exception) {
				// TODO: notify user, i.e. highlight input field 
				if (LogManager.ENABLED) LogManager.error("InsertCounterBeforeAfterPanel.changedUpdate> Invalid pattern: " + InsertCounterBeforeAfterPanel.this.textToSearchField.getText());
			}
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			changedUpdate(null);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			changedUpdate(null);
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getSource();
			if (source == InsertCounterBeforeAfterPanel.this.regexCheckbox) {
				try {
					InsertCounterBeforeAfterPanel.super.ruleFactory.setRegex(
							InsertCounterBeforeAfterPanel.this.regexCheckbox.isSelected());

				} catch (PatternSyntaxException exception) {
					// TODO: notify user, i.e. highlight input field 
					if (LogManager.ENABLED) LogManager.error("ChangeCasePanel.InsertCounterBeforeAfterPanel> Invalid pattern: " + InsertCounterBeforeAfterPanel.this.textToSearchField.getText());
				} catch (NullPointerException exception) {
					// TODO: notify user, i.e. highlight input field 
					if (LogManager.ENABLED) LogManager.error("ChangeCasePanel.InsertCounterBeforeAfterPanel> Invalid pattern: " + InsertCounterBeforeAfterPanel.this.textToSearchField.getText());
				}
				
			} else if (source == InsertCounterBeforeAfterPanel.this.matchCaseChackbox) {
				InsertCounterBeforeAfterPanel.super.ruleFactory.setMatchCase(
						InsertCounterBeforeAfterPanel.this.matchCaseChackbox.isSelected());
			}
		}
	}
}