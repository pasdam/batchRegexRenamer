package com.pasdam.regexren.gui.rules;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import com.pasdam.gui.swing.widgets.WideComboBox;
import com.pasdam.regexren.controller.LocaleManager;
import com.pasdam.regexren.controller.LogManager;
import com.pasdam.regexren.gui.RuleContentPanel;

/**
 * Panel used to configure a "Insert text at before/after pattern" rule
 * 
 * @author paco
 * @version 0.1
 */
public class InsertTextBeforeAfterPanel extends RuleContentPanel<InsertTextBeforeAfterFactory> {
	
	private static final long serialVersionUID = 6210082886810893192L;

	// UI components
	private JCheckBox matchCaseCheckbox;
	private JCheckBox regexCheckbox;
	private JLabel textToInsertLabel;
	private JTextField textToInsertField;
	private JTextField textToSearchField;
	private WideComboBox beforeAfterCombobox;

	/** Array of "From" combobox values */
	private final String[] beforeAfterValues = new String[6];

	/** Handler of internal events */
	private final InternalEventHandler eventHandler;
	
	/**	Description pattern */
	private String description;

	/** Create the panel */
	public InsertTextBeforeAfterPanel(InsertTextBeforeAfterFactory ruleFactory) {
		super(ruleFactory, TWO_ROW_PANEL_HEIGHT, BoxLayout.Y_AXIS);
		
		// create first row
		Box row1Panel = Box.createHorizontalBox();
		row1Panel.setBorder(UIManager.getBorder("ComboBox.editorBorder"));
		
		// create and add text to insert label to the first row
		this.textToInsertLabel = new JLabel();
		row1Panel.add(this.textToInsertLabel);
		
		// create spacer and add it to first row
		row1Panel.add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create and add text to insert field to the first row
		this.textToInsertField = new JTextField();
		row1Panel.add(this.textToInsertField);

		// add first row to the panel
		add(row1Panel);
		
		// create second row and add it to the panel
		Box row2Panel = Box.createHorizontalBox();
		row2Panel.setBorder(UIManager.getBorder("ComboBox.editorBorder"));
		
		// create beforeAfter combobox and add it to the second row
		this.beforeAfterCombobox = new WideComboBox();
		this.beforeAfterCombobox.setMaximumSize(new Dimension(WIDGET_TEXT_MIN_WIDTH, WIDGET_HEIGHT));
		row2Panel.add(this.beforeAfterCombobox);
		
		// create spacer and add it to the second row
		row2Panel.add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));

		// create text to search field and add it to the second row
		this.textToSearchField = new JTextField();
		row2Panel.add(this.textToSearchField);
		
		// create spacer and add it to the second row
		row2Panel.add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create regex checkbox and add it to the second row
		this.regexCheckbox = new JCheckBox();
		row2Panel.add(this.regexCheckbox);
		
		// create spacer and add it to the second row
		row2Panel.add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create match case checkbox and add it to the second row
		this.matchCaseCheckbox = new JCheckBox();
		row2Panel.add(this.matchCaseCheckbox);

		// add second row to the panel
		add(row2Panel);
		
		// read initial values from rule factory
		this.matchCaseCheckbox.setSelected(ruleFactory.isMatchCase());
		this.regexCheckbox.setSelected(ruleFactory.isRegex());
		this.textToInsertField.setText(ruleFactory.getTextToInsert());
		this.textToSearchField.setText(ruleFactory.getTextToSearch());
		
		// create and set event handler
		this.eventHandler = new InternalEventHandler();
		this.beforeAfterCombobox.addActionListener(this.eventHandler);
		this.matchCaseCheckbox.addItemListener(this.eventHandler);
		this.regexCheckbox.addItemListener(this.eventHandler);
		this.textToInsertField.getDocument().addDocumentListener(this.eventHandler);
		this.textToSearchField.getDocument().addDocumentListener(this.eventHandler);
	}

	@Override
	public void localeChanged(LocaleManager localeManager) {
		this.description = localeManager.getString("Rule.insertTextPattern.description");
		
		this.matchCaseCheckbox.setText(localeManager.getString("Rule.matchCase"));
		this.matchCaseCheckbox.setToolTipText(localeManager.getString("Rule.matchCase.tooltip"));
		this.regexCheckbox.setText(localeManager.getString("Rule.regex"));
		this.regexCheckbox.setToolTipText(localeManager.getString("Rule.regex.tooltip"));
		this.textToInsertLabel.setText(localeManager.getString("Rule.text"));
		
		// update combobox
		this.beforeAfterValues[InsertTextBeforeAfterFactory.BEFORE_ALL]   = localeManager.getString("Rule.beforeAfter.beforeAll");
		this.beforeAfterValues[InsertTextBeforeAfterFactory.BEFORE_FIRST] = localeManager.getString("Rule.beforeAfter.beforeFirst");
		this.beforeAfterValues[InsertTextBeforeAfterFactory.BEFORE_LAST]  = localeManager.getString("Rule.beforeAfter.beforeLast");
		this.beforeAfterValues[InsertTextBeforeAfterFactory.AFTER_ALL]    = localeManager.getString("Rule.beforeAfter.afterAll");
		this.beforeAfterValues[InsertTextBeforeAfterFactory.AFTER_FIRST]  = localeManager.getString("Rule.beforeAfter.afterFirst");
		this.beforeAfterValues[InsertTextBeforeAfterFactory.AFTER_LAST]   = localeManager.getString("Rule.beforeAfter.afterLast");
		this.beforeAfterCombobox.setModel(new DefaultComboBoxModel<String>(this.beforeAfterValues));
		this.beforeAfterCombobox.setSelectedIndex(super.ruleFactory.getBeforeAfterType());
	}
	
	@Override
	protected String getDescription() {
		String textToInsert = super.ruleFactory.getTextToInsert();
		String textToSearch = super.ruleFactory.getTextToSearch();
		return String.format(
				this.description,
				textToInsert != null ? textToInsert : "",
				this.beforeAfterCombobox.getSelectedItem(),
				textToSearch != null ? textToSearch : ""
		);
	}
	
	/** Class that handles internal events */
	private class InternalEventHandler implements ActionListener, ItemListener, DocumentListener {

		@Override
		public void changedUpdate(DocumentEvent event) {
			Document document = event.getDocument();
			try {
				if (document == InsertTextBeforeAfterPanel.this.textToInsertField.getDocument()) {
					InsertTextBeforeAfterPanel.this.getRuleFactory().setTextToInsert(
							InsertTextBeforeAfterPanel.this.textToInsertField.getText());
					
				} else if (document == InsertTextBeforeAfterPanel.this.textToSearchField.getDocument()) {
					InsertTextBeforeAfterPanel.this.getRuleFactory().setTextToSearch(
							InsertTextBeforeAfterPanel.this.textToSearchField.getText());
						
				}
				
				// reset border
				InsertTextBeforeAfterPanel.this.textToSearchField.setBorder(UIManager.getBorder("TextField.border"));
				InsertTextBeforeAfterPanel.this.textToInsertField.setBorder(UIManager.getBorder("TextField.border"));
			
			} catch (InvalidParametersException exception) {
				// reset border
				InsertTextBeforeAfterPanel.this.textToSearchField.setBorder(UIManager.getBorder("TextField.border"));
				InsertTextBeforeAfterPanel.this.textToInsertField.setBorder(UIManager.getBorder("TextField.border"));
				// highlight invalid
				for (Integer id : exception.getInvalidParameter()) {
					switch (id) {
						case InsertTextBeforeAfterFactory.PARAMETER_TEXT_TO_SEARCH:
							InsertTextBeforeAfterPanel.this.textToSearchField.setBorder(BorderFactory.createLineBorder(Color.RED));
							break;
						
						case InsertTextBeforeAfterFactory.PARAMETER_TEXT_TO_INSERT:
							InsertTextBeforeAfterPanel.this.textToInsertField.setBorder(BorderFactory.createLineBorder(Color.RED));
							break;
	
						default:
							break;
					}
				}
				if (LogManager.ENABLED) LogManager.error("InsertTextBeforeAfterPanel.InternalEventHandler.changedUpdate> One or more parameters are invalid");
			}
		}

		@Override
		public void insertUpdate(DocumentEvent event) {
			changedUpdate(event);
		}

		@Override
		public void removeUpdate(DocumentEvent event) {
			changedUpdate(event);
		}
		
		@Override
		public void itemStateChanged(ItemEvent event) {
			Object source = event.getSource();
			if (source == InsertTextBeforeAfterPanel.this.regexCheckbox) {
				try {
					InsertTextBeforeAfterPanel.this.getRuleFactory().setRegex(
							InsertTextBeforeAfterPanel.this.regexCheckbox.isSelected());

					// reset border
					InsertTextBeforeAfterPanel.this.textToSearchField.setBorder(UIManager.getBorder("TextField.border"));
					InsertTextBeforeAfterPanel.this.textToInsertField.setBorder(UIManager.getBorder("TextField.border"));
				
				} catch (InvalidParametersException exception) {
					// reset border
					InsertTextBeforeAfterPanel.this.textToSearchField.setBorder(UIManager.getBorder("TextField.border"));
					InsertTextBeforeAfterPanel.this.textToInsertField.setBorder(UIManager.getBorder("TextField.border"));
					// highlight invalid
					for (Integer id : exception.getInvalidParameter()) {
						switch (id) {
							case InsertTextBeforeAfterFactory.PARAMETER_TEXT_TO_SEARCH:
								InsertTextBeforeAfterPanel.this.textToSearchField.setBorder(BorderFactory.createLineBorder(Color.RED));
								break;
							
							case InsertTextBeforeAfterFactory.PARAMETER_TEXT_TO_INSERT:
								InsertTextBeforeAfterPanel.this.textToInsertField.setBorder(BorderFactory.createLineBorder(Color.RED));
								break;
		
							default:
								break;
						}
					}
					if (LogManager.ENABLED) LogManager.error("InsertTextBeforeAfterPanel.InternalEventHandler.itemStateChanged> One or more parameters are invalid");
				}
				
			} else if (source == InsertTextBeforeAfterPanel.this.matchCaseCheckbox) {
				InsertTextBeforeAfterPanel.this.getRuleFactory().setMatchCase(
						InsertTextBeforeAfterPanel.this.matchCaseCheckbox.isSelected());
			}
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			InsertTextBeforeAfterPanel.this.getRuleFactory().setBeforeAfterType(
					InsertTextBeforeAfterPanel.this.beforeAfterCombobox.getSelectedIndex());
		}
	}
}
