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

import com.pasdam.gui.swing.widgets.WideComboBox;
import com.pasdam.regexren.controller.LocaleManager;
import com.pasdam.regexren.controller.LogManager;
import com.pasdam.regexren.gui.RuleContentPanel;

/**
 * Panel used to configure a "Change case" rule
 * 
 * @author paco
 * @version 0.1
 */
public class ChangeCasePanel extends RuleContentPanel<ChangeCaseFactory> {

	private static final long serialVersionUID = 5273872928673106679L;

	/** Array of targets combobox's items */
	private final String[] targetValues = new String[2];
	
	/** Array of operations combobox's items */
	private final String[] operationValues = new String[4];

	// UI components
	private JCheckBox regexCheckbox;
	private JLabel operationLabel;
	private JLabel separatorLabel;
	private JLabel targetLabel;
	private JTextField separatorText;
	private WideComboBox operationCombobox;
	private WideComboBox targetCombobox;
	
	/** Handler of internal events */
	private final InternalEventHandler eventHandler;
	
	/** Create the panel */
	public ChangeCasePanel(ChangeCaseFactory ruleFactory) {
		super(ruleFactory, ONE_ROW_PANEL_HEIGHT, BoxLayout.X_AXIS);
		
		// create and add target label
		this.targetLabel = new JLabel();
		this.targetLabel.setAlignmentX(CENTER_ALIGNMENT);
		add(this.targetLabel);
		
		// add space
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));

		// create and add target combobox to the panel
		this.targetCombobox = new WideComboBox();
		this.targetCombobox.setMaximumSize(new Dimension(WIDGET_TEXT_MIN_WIDTH, WIDGET_HEIGHT));
		add(targetCombobox);
		
		// add space
		add(Box.createHorizontalStrut(FIXED_SPACE_LONG));
		
		// create and add the operation label
		this.operationLabel = new JLabel();
		add(this.operationLabel);
		
		// add space
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create and add operation combobox to the panel
		this.operationCombobox = new WideComboBox();
		this.operationCombobox.setMaximumSize(new Dimension(WIDGET_TEXT_MIN_WIDTH, WIDGET_HEIGHT));
		add(operationCombobox);
		
		// add space
		add(Box.createHorizontalStrut(FIXED_SPACE_LONG));
		
		// create and add the separator label
		this.separatorLabel = new JLabel();
		add(this.separatorLabel);

		// add space
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create and add the separator text
		this.separatorText = new JTextField();
		this.separatorText.setPreferredSize(new Dimension(WIDGET_TEXT_MIN_WIDTH, WIDGET_HEIGHT));
		add(this.separatorText);

		// add space
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create and add the regex checkbox
		this.regexCheckbox = new JCheckBox();
		add(this.regexCheckbox);
		
		// set parameters from ruleFactory
		this.regexCheckbox.setSelected(ruleFactory.isRegex());
		this.separatorText.setText(ruleFactory.getSentenceSeparator());

		// set values listener
		this.eventHandler = new InternalEventHandler();
		this.operationCombobox.addActionListener(this.eventHandler);
		this.regexCheckbox.addItemListener(this.eventHandler);
		this.separatorText.getDocument().addDocumentListener(this.eventHandler);
		this.targetCombobox.addActionListener(this.eventHandler);
	}
	
	@Override
	public void localeChanged(LocaleManager localeManager) {
		this.targetLabel   .setText(localeManager.getString("Rule.target"));
		this.operationLabel.setText(localeManager.getString("Rule.operation"));
		this.separatorLabel.setText(localeManager.getString("Rule.sentencesSeparator"));
		this.regexCheckbox .setText(localeManager.getString("Rule.regex"));
		this.regexCheckbox .setToolTipText(localeManager.getString("Rule.regex.tooltip"));

		this.targetValues   [ChangeCaseFactory.TARGET_NAME]                    = localeManager.getString("Rule.name");
		this.targetValues   [ChangeCaseFactory.TARGET_EXTENSION]               = localeManager.getString("Rule.extension");
		this.operationValues[ChangeCaseFactory.OPERATION_TO_LOWERCASE]         = localeManager.getString("Rule.toLowerCase");
		this.operationValues[ChangeCaseFactory.OPERATION_TO_UPPERCASE]         = localeManager.getString("Rule.toUpperCase");
		this.operationValues[ChangeCaseFactory.OPERATION_CAPITALIZE_WORDS]     = localeManager.getString("Rule.capitalizeWords");
		this.operationValues[ChangeCaseFactory.OPERATION_CAPITALIZE_SENTENCES] = localeManager.getString("Rule.capitalizeSentences");
		
		// update comboboxes
		this.targetCombobox.setModel(new DefaultComboBoxModel<String>(this.targetValues));
		this.targetCombobox.setSelectedIndex(super.ruleFactory.getTarget());
		this.operationCombobox.setModel(new DefaultComboBoxModel<String>(this.operationValues));
		this.operationCombobox.setSelectedIndex(super.ruleFactory.getOperation());
		updateComponentsVisibility();
	}

	@Override
	protected String getDescription() {
		if (this.operationCombobox != null && this.targetCombobox != null) {
			return this.operationCombobox.getSelectedItem() + " [" + this.targetCombobox.getSelectedItem()
					+ (this.separatorText.isVisible() ? "]: " + this.separatorText.getText() : "]");
		} else {
			return "";
		}
	}
	
	/** Update visibility of components when operation value changes */
	private void updateComponentsVisibility() {
		if (this.operationCombobox.getSelectedIndex() == ChangeCaseFactory.OPERATION_CAPITALIZE_SENTENCES) {
			this.separatorLabel.setVisible(true);
			this.separatorText .setVisible(true);
			this.regexCheckbox .setVisible(true);
			
		} else {
			this.separatorLabel.setVisible(false);
			this.separatorText .setVisible(false);
			this.regexCheckbox .setVisible(false);
		}
	}
	
	/** Class that handles internal events */
	private class InternalEventHandler implements ActionListener, DocumentListener, ItemListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == ChangeCasePanel.this.targetCombobox) {
				ChangeCasePanel.super.ruleFactory.setTarget(ChangeCasePanel.this.targetCombobox.getSelectedIndex());
				
			} else if (source == ChangeCasePanel.this.operationCombobox) {
				ChangeCasePanel.super.ruleFactory.setOperation(ChangeCasePanel.this.operationCombobox.getSelectedIndex());
				
				ChangeCasePanel.this.updateComponentsVisibility();
			
			} else {
				return;
			}
		}

		@Override
		public void changedUpdate(DocumentEvent event) {
			try {
				ChangeCasePanel.super.ruleFactory.setSentenceSeparator(ChangeCasePanel.this.separatorText.getText());
				
				// reset border
				ChangeCasePanel.this.separatorText.setBorder(UIManager.getBorder("TextField.border"));
				
			} catch (Exception exception) {
				ChangeCasePanel.this.separatorText.setBorder(BorderFactory.createLineBorder(Color.RED));
				if (LogManager.ENABLED) LogManager.error("ChangeCasePanel.changedUpdate> Invalid regex separator: " + ChangeCasePanel.this.separatorText.getText());
			}
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			changedUpdate(e);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			changedUpdate(e);
		}

		@Override
		public void itemStateChanged(ItemEvent arg0) {
			try {
				ChangeCasePanel.super.ruleFactory.setRegex(ChangeCasePanel.this.regexCheckbox.isSelected());
				
				// reset border
				ChangeCasePanel.this.separatorText.setBorder(UIManager.getBorder("TextField.border"));
				
			} catch (Exception exception) {
				ChangeCasePanel.this.separatorText.setBorder(BorderFactory.createLineBorder(Color.RED));
				if (LogManager.ENABLED) LogManager.error("ChangeCasePanel.itemStateChanged> Invalid regex separator: " + ChangeCasePanel.this.separatorText.getText());
			}
		}
	}
}
