package com.pasdam.regexren.gui.rules;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.pasdam.gui.swing.widgets.SteppedComboBox;
import com.pasdam.regexren.controller.LocaleManager;
import com.pasdam.regexren.controller.LogManager;
import com.pasdam.regexren.gui.RuleContentPanel;

/**
 * Panel used to configure a "Replace..." rule
 * 
 * @author paco
 * @version 0.1
 */
public class ReplacePanel extends RuleContentPanel {

	private static final long serialVersionUID = -7951879690934241967L;

	// UI components
	private Box row2Panel;
	private JCheckBox matchCaseCheckbox;
	private JCheckBox regexCheckbox;
	private JLabel endLabel;
	private JLabel targetLabel;
	private JLabel startLabel;
	private JLabel textToInsertLabel;
	private JLabel textToReplaceLabel;
	private JSpinner endSpinner;
	private JSpinner startSpinner;
	private JTextField textToInsertText;
	private JTextField textToReplaceText;
	private SteppedComboBox targetCombobox;
	
	/** Array of targets combobox's items */
	private final String[] targetValues  = new String[6];

	/**	Description pattern */
	private String description;
	
	/** Handler of internal events */
	private final InternalEventHandler eventHandler;

	/**	Rule factory related to this panel */
	private final ReplaceFactory ruleFactory;

	/** Create the panel */
	public ReplacePanel(ReplaceFactory ruleFactory) {
		super(ruleFactory);
		this.ruleFactory = ruleFactory;
		
		// create event handler
		this.eventHandler = new InternalEventHandler();

		// set panel properties
		setBorder(UIManager.getBorder("Button.border"));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, TWO_ROW_PANEL_HEIGHT));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		// create first row box
		Box row1Panel = Box.createHorizontalBox();
		row1Panel.setBorder(UIManager.getBorder("ComboBox.editorBorder"));
		
		// create and add the replace label to the first row
		this.textToReplaceLabel = new JLabel();
		row1Panel.add(this.textToReplaceLabel);
		row1Panel.add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create and add the replace text to the first row
		this.textToReplaceText = new JTextField();
		this.textToReplaceText.getDocument().addDocumentListener(this.eventHandler);
		row1Panel.add(this.textToReplaceText);
		
		// create and add space to the first row
		row1Panel.add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create and add regex checkbox to the first row
		this.regexCheckbox = new JCheckBox();
		this.regexCheckbox.addActionListener(this.eventHandler);
		row1Panel.add(this.regexCheckbox);
		
		// create and add space to the first row
		row1Panel.add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create and add match case checkbox to the first row
		this.matchCaseCheckbox = new JCheckBox();
		this.matchCaseCheckbox.addActionListener(this.eventHandler);
		row1Panel.add(this.matchCaseCheckbox);

		// add first row to the panel
		add(row1Panel);
		
		// create second row box
		this.row2Panel = Box.createHorizontalBox();
		this.row2Panel.setBorder(UIManager.getBorder("ComboBox.editorBorder"));
		
		// create and add text to insert label to the second row
		this.textToInsertLabel = new JLabel();
		this.row2Panel.add(this.textToInsertLabel);
		
		// create and add space to the second row
		this.row2Panel.add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create and add text to insert field to the second row
		this.textToInsertText = new JTextField();
		this.textToInsertText.setPreferredSize(new Dimension(WIDGET_TEXT_MIN_WIDTH, WIDGET_HEIGHT));
		this.textToInsertText.getDocument().addDocumentListener(this.eventHandler);
		this.row2Panel.add(this.textToInsertText);
		
		// create and add space to the second row
		this.row2Panel.add(Box.createHorizontalStrut(FIXED_SPACE_LONG));
		
		// create and add start label to the second row
		this.startLabel = new JLabel();
		this.row2Panel.add(this.startLabel);

		// create and add space to the second row
		this.row2Panel.add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create and add start spinner to the second row
		this.startSpinner = new JSpinner();
		this.startSpinner.setPreferredSize(new Dimension(WIDGET_SPINNER_MIN_WIDTH, WIDGET_HEIGHT));
		this.startSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		this.startSpinner.addChangeListener(this.eventHandler);
		this.row2Panel.add(this.startSpinner);

		// create and add space to the second row
		this.row2Panel.add(Box.createHorizontalStrut(FIXED_SPACE_LONG));
		
		// create and add end label to the second row
		this.endLabel = new JLabel();
		this.row2Panel.add(this.endLabel);
		
		// create and add space to the second row
		this.row2Panel.add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create and add end spinner to the second row
		this.endSpinner = new JSpinner();
		this.endSpinner.setPreferredSize(new Dimension(this.startSpinner.getPreferredSize()));
		this.endSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		this.endSpinner.addChangeListener(this.eventHandler);
		this.row2Panel.add(this.endSpinner);
		
		// create and add space to the second row
		this.row2Panel.add(Box.createHorizontalStrut(FIXED_SPACE_LONG));
		
		// create and add target label to the second row
		this.targetLabel = new JLabel();
		this.row2Panel.add(this.targetLabel);
		
		// create and add space to the second row
		this.row2Panel.add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create and add space to the second row
		this.row2Panel.add(Box.createHorizontalGlue());
		
		// add second row to the panel
		add(this.row2Panel);
		
		// read initial ruls's values
		this.endSpinner.setValue(this.ruleFactory.getEndIndex());
		this.matchCaseCheckbox.setSelected(this.ruleFactory.isMatchCase());
		this.regexCheckbox.setSelected(this.ruleFactory.isRegex());
		this.startSpinner.setValue(this.ruleFactory.getStartIndex());
		this.textToInsertText.setText(this.ruleFactory.getTextToInsert());
		this.textToReplaceText.setText(this.ruleFactory.getTextToReplace());
	}
	
	/**	Updates the target and operations comboboxes */
	private void updateCombos() {
		// remove previous target combobox and create a new one
		// TODO: update model instead of recreate the view
		if (this.targetCombobox != null) {
			this.row2Panel.remove(this.targetCombobox);
		}
		this.targetCombobox = new SteppedComboBox(new DefaultComboBoxModel<String>(this.targetValues));
		this.targetCombobox.setPreferredSize(new Dimension(WIDGET_TEXT_MIN_WIDTH, WIDGET_HEIGHT));
		this.targetCombobox.setSelectedIndex(this.ruleFactory.getTarget());
		this.targetCombobox.addActionListener(this.eventHandler);
		this.row2Panel.add(this.targetCombobox, this.row2Panel.getComponentCount()-1);
	}

	@Override
	public void localeChanged(LocaleManager localeManager) {
		this.textToReplaceLabel.setText(localeManager.getString("Rule.replace"));
		this.regexCheckbox.setText(localeManager.getString("Rule.regex"));
		this.regexCheckbox.setToolTipText(localeManager.getString("Rule.regex.tooltip"));
		this.matchCaseCheckbox.setToolTipText(localeManager.getString("Rule.matchCase.tooltip"));
		this.matchCaseCheckbox.setText(localeManager.getString("Rule.matchCase"));
		this.textToInsertLabel.setText(localeManager.getString("Rule.with"));
		this.startLabel.setText(localeManager.getString("Rule.from"));
		this.endLabel.setText(localeManager.getString("Rule.to"));
		this.targetValues[ReplaceFactory.TARGET_NAME_ALL] = localeManager.getString("Rule.nameAll");
		this.targetValues[ReplaceFactory.TARGET_NAME_FIRST] = localeManager.getString("Rule.nameFirst");
		this.targetValues[ReplaceFactory.TARGET_NAME_LAST] = localeManager.getString("Rule.nameLast");
		this.targetValues[ReplaceFactory.TARGET_EXTENSION_ALL] = localeManager.getString("Rule.extensionAll");
		this.targetValues[ReplaceFactory.TARGET_EXTENSION_FIRST] = localeManager.getString("Rule.extensionFirst");
		this.targetValues[ReplaceFactory.TARGET_EXTENSION_LAST] = localeManager.getString("Rule.extensionLast");
		this.targetLabel.setText(localeManager.getString("Rule.of"));
		this.description = localeManager.getString("Rule.replace.description");
		updateCombos();
	}

	@Override
	protected String getDescription() {
		String textToReplace = this.ruleFactory.getTextToReplace();
		String textToInsert = this.ruleFactory.getTextToInsert();
		return String.format(
				this.description,
				textToReplace != null ? textToReplace : "",
				textToInsert != null ? textToInsert : "",
				this.targetCombobox.getSelectedItem());
	}

	/** Class that handles internal events */
	private class InternalEventHandler implements ActionListener, ChangeListener, DocumentListener {
		
		@Override
		public void changedUpdate(DocumentEvent e) {
			try {
				ReplacePanel.this.ruleFactory.setTextToReplace(ReplacePanel.this.textToReplaceText.getText());
			} catch (Exception exception) {
				if (LogManager.ENABLED) LogManager.error("ReplacePanel.InternalEventHandler.changedUpdate> Invalid regex pattern: " + ReplacePanel.this.textToReplaceText.getText());
			}
			ReplacePanel.this.ruleFactory.setTextToInsert(ReplacePanel.this.textToInsertText.getText());
			
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
	
		@Override
		public void stateChanged(ChangeEvent e) {
			Object source = e.getSource();
			if (source == ReplacePanel.this.startSpinner) {
				ReplacePanel.this.ruleFactory.setStartIndex(((Integer)ReplacePanel.this.startSpinner.getValue()).intValue()-1);
				
			} else if (source == ReplacePanel.this.endSpinner) {
				ReplacePanel.this.ruleFactory.setEndIndex(((Integer)ReplacePanel.this.endSpinner.getValue()).intValue()-1);
			}

			ReplacePanel.super.configurationChanged();
		}
	
		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == ReplacePanel.this.regexCheckbox) {
				try {
					ReplacePanel.this.ruleFactory.setRegex(ReplacePanel.this.regexCheckbox.isSelected());
				} catch (Exception exception) {
					if (LogManager.ENABLED) LogManager.error("ReplacePanel.InternalEventHandler.actionPerformed> Invalid regex pattern: " + ReplacePanel.this.textToReplaceText.getText());
				}
				
			} else if (source == ReplacePanel.this.matchCaseCheckbox) {
				ReplacePanel.this.ruleFactory.setMatchCase(ReplacePanel.this.matchCaseCheckbox.isSelected());
				
			} else if (source == ReplacePanel.this.targetCombobox) {
				ReplacePanel.this.ruleFactory.setTarget(ReplacePanel.this.targetCombobox.getSelectedIndex());
			}

			ReplacePanel.super.configurationChanged();
		}
	}
}
