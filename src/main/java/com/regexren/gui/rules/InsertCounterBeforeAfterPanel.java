package com.pasdam.regexren.gui.rules;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.pasdam.gui.swing.widgets.WideComboBox;
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
	private JCheckBox matchCaseCheckbox;
	private JCheckBox regexCheckbox;
	private JTextField textToSearchField;
	private WideComboBox beforeAfterCombobox;
	
	/** Array of "Before/After" combobox values */
	private final String[] beforeAfterValues = new String[2];
	
	/**	Description pattern */
	private String description;

	/** Create the panel */
	public InsertCounterBeforeAfterPanel(InsertCounterBeforeAfterFactory ruleFactory) {
		super(ruleFactory);
		
		// create beforeAfter combobox and add it to the panel
		this.beforeAfterCombobox = new WideComboBox();
		this.beforeAfterCombobox.setMaximumSize(new Dimension(WIDGET_TEXT_MIN_WIDTH, WIDGET_HEIGHT));
		add(this.beforeAfterCombobox);
		
		// create spacer and add it to the panel
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));

		// create text to search field and add it to the panel
		this.textToSearchField = new JTextField();
		this.textToSearchField.setColumns(10);
		add(this.textToSearchField);
		
		// create spacer and add it to the panel
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));

		// create regex checkbox and add it to the panel
		this.regexCheckbox = new JCheckBox();
		add(this.regexCheckbox);
		
		// create spacer and add it to the panel
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create match case checkbox and add it to the panel
		this.matchCaseCheckbox = new JCheckBox();
		add(this.matchCaseCheckbox);
		
		// read initial values from rule factory
		this.matchCaseCheckbox.setSelected(ruleFactory.isMatchCase());
		this.regexCheckbox.setSelected(ruleFactory.isRegex());
		this.textToSearchField.setText(ruleFactory.getTextToSearch());

		// create and set event handler
		InternalEventHandler eventHandler = new InternalEventHandler();
		this.beforeAfterCombobox.addActionListener(eventHandler);
		this.matchCaseCheckbox.addItemListener(eventHandler);
		this.regexCheckbox.addItemListener(eventHandler);
		this.textToSearchField.getDocument().addDocumentListener(eventHandler);
	}
	
	@Override
	public void localeChanged(LocaleManager localeManager) {
		super.localeChanged(localeManager);
		
		this.description = localeManager.getString("Rule.insertCounterPattern.description");
		
		this.matchCaseCheckbox.setText(localeManager.getString("Rule.matchCase"));
		this.matchCaseCheckbox.setToolTipText(localeManager.getString("Rule.matchCase.tooltip"));
		this.regexCheckbox.setText(localeManager.getString("Rule.regex"));
		this.regexCheckbox.setToolTipText(localeManager.getString("Rule.regex.tooltip"));

		// update comboboxes
		this.beforeAfterValues[BEFORE] = localeManager.getString("Rule.before");
		this.beforeAfterValues[AFTER]  = localeManager.getString("Rule.after");
		this.beforeAfterCombobox.setModel(new DefaultComboBoxModel<String>(this.beforeAfterValues));
		this.beforeAfterCombobox.setSelectedIndex(super.getRuleFactory().isBeforePattern() ? BEFORE : AFTER);
		this.beforeAfterCombobox.setToolTipText(this.beforeAfterCombobox.getSelectedItem().toString());
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
				InsertCounterBeforeAfterPanel.super.ruleFactory.setTextToSearch(
						InsertCounterBeforeAfterPanel.this.textToSearchField.getText());
				
				// reset border
				InsertCounterBeforeAfterPanel.this.textToSearchField.setBorder(UIManager.getBorder("TextField.border"));
				
			} catch (Exception exception) {
				InsertCounterBeforeAfterPanel.this.textToSearchField.setBorder(BorderFactory.createLineBorder(Color.RED));
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
				
					// reset border
					InsertCounterBeforeAfterPanel.this.textToSearchField.setBorder(UIManager.getBorder("TextField.border"));

				} catch (Exception exception) {
					InsertCounterBeforeAfterPanel.this.textToSearchField.setBorder(BorderFactory.createLineBorder(Color.RED));
					if (LogManager.ENABLED) LogManager.error("ChangeCasePanel.InsertCounterBeforeAfterPanel> Invalid pattern: " + InsertCounterBeforeAfterPanel.this.textToSearchField.getText());
				}
				
			} else if (source == InsertCounterBeforeAfterPanel.this.matchCaseCheckbox) {
				InsertCounterBeforeAfterPanel.super.ruleFactory.setMatchCase(
						InsertCounterBeforeAfterPanel.this.matchCaseCheckbox.isSelected());
			}
		}
	}
}
