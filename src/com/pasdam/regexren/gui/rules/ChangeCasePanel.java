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

import com.pasdam.gui.swing.widgets.SteppedComboBox;
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
	private SteppedComboBox operationCmb;
	private SteppedComboBox targetCmb;
	
	/** Handler of internal events */
	private final InternalEventHandler eventHandler;
	
	/** Create the panel */
	public ChangeCasePanel(ChangeCaseFactory ruleFactory) {
		super(ruleFactory, ONE_ROW_PANEL_HEIGHT, BoxLayout.X_AXIS);
		
		// create and add target label
		this.targetLabel = new JLabel();
		this.targetLabel.setAlignmentX(CENTER_ALIGNMENT);
		add(this.targetLabel);
		
		// add spaces (between them will be added the targets combobox)
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		add(Box.createHorizontalStrut(FIXED_SPACE_LONG));
		
		// create and add the operation label
		this.operationLabel = new JLabel();
		add(this.operationLabel);
		
		// add spaces (between them will be added the operations combobox)
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
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
		this.separatorText.setText(ruleFactory.getSentenceSeparator());
		this.regexCheckbox.setSelected(ruleFactory.isRegex());

		// set values listener
		this.eventHandler = new InternalEventHandler();
		this.regexCheckbox.addItemListener(this.eventHandler);
		this.separatorText.getDocument().addDocumentListener(this.eventHandler);
	}
	
	/**	Updates the target and operations comboboxes */
	private void updateCombos() {
		// remove previous target combobox and create a new one
		// TODO: update model instead of recreate the view
		if (this.targetCmb != null) {
			remove(this.targetCmb);
		}
		this.targetCmb = new SteppedComboBox(new DefaultComboBoxModel<String>(this.targetValues));
		this.targetCmb.setPreferredSize(new Dimension(WIDGET_TEXT_MIN_WIDTH, WIDGET_HEIGHT));
		this.targetCmb.setMaximumSize(this.targetCmb.getPreferredSize());
		this.targetCmb.addActionListener(this.eventHandler);
		this.targetCmb.setSelectedIndex(super.ruleFactory.getTarget());
		add(targetCmb, 2);

		// remove previous operation combobox and create a new one
		if (operationCmb != null) {
			remove(operationCmb);
		}
		this.operationCmb = new SteppedComboBox(new DefaultComboBoxModel<String>(operationValues));
		this.operationCmb.setPreferredSize(new Dimension(this.targetCmb.getPreferredSize()));
		this.operationCmb.setMaximumSize(this.operationCmb.getPreferredSize());
		this.operationCmb.addActionListener(this.eventHandler);
		this.operationCmb.setSelectedIndex(super.ruleFactory.getOperation());
		add(operationCmb, 6);
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
		
		updateCombos();
	}

	@Override
	protected String getDescription() {
		if (this.operationCmb != null && this.targetCmb != null) {
			return this.operationCmb.getSelectedItem() + " [" + this.targetCmb.getSelectedItem()
					+ (this.separatorText.isVisible() ? "]: " + this.separatorText.getText() : "]");
		} else {
			return "";
		}
	}
	
	/** Class that handles internal events */
	private class InternalEventHandler implements ActionListener, DocumentListener, ItemListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == ChangeCasePanel.this.targetCmb) {
				ChangeCasePanel.super.ruleFactory.setTarget(ChangeCasePanel.this.targetCmb.getSelectedIndex());
				
			} else if (source == ChangeCasePanel.this.operationCmb) {
				ChangeCasePanel.super.ruleFactory.setOperation(ChangeCasePanel.this.operationCmb.getSelectedIndex());
				
				if (ChangeCasePanel.this.operationCmb.getSelectedIndex() == ChangeCaseFactory.OPERATION_CAPITALIZE_SENTENCES) {
					ChangeCasePanel.this.separatorLabel.setVisible(true);
					ChangeCasePanel.this.separatorText .setVisible(true);
					ChangeCasePanel.this.regexCheckbox .setVisible(true);
					
				} else {
					ChangeCasePanel.this.separatorLabel.setVisible(false);
					ChangeCasePanel.this.separatorText .setVisible(false);
					ChangeCasePanel.this.regexCheckbox .setVisible(false);
				}
			
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
