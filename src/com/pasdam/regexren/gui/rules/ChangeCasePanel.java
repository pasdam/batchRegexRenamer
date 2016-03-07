package com.pasdam.regexren.gui.rules;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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
public class ChangeCasePanel extends RuleContentPanel implements ActionListener, DocumentListener, ItemListener {

	private static final long serialVersionUID = 5273872928673106679L;

	/** Array of targets combobox's items */
	private final String[] targetValues = new String[2];
	
	/** Array of operations combobox's items */
	private final String[] operationValues = new String[4];

	// UI components
	private JLabel separatorLabel, targetLabel, operationLabel;
	private SteppedComboBox targetCmb, operationCmb;
	private JTextField separatorText;
	private JCheckBox regexCheckbox;
	
	/**	Rule factory related to this panel */
	private final ChangeCaseFactory ruleFactory;
	
	/** Create the panel */
	public ChangeCasePanel(ChangeCaseFactory ruleFactory) {
		super(ruleFactory);
		
		this.ruleFactory = ruleFactory;
		
		// set panel properties
		setBorder(UIManager.getBorder("Button.border"));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, ONE_ROW_PANEL_HEIGHT));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
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
		this.separatorText.getDocument().addDocumentListener(this);
		add(this.separatorText);

		// add space
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create and add the regex checkbox
		this.regexCheckbox = new JCheckBox();
		this.regexCheckbox.addItemListener(this);
		add(this.regexCheckbox);
		
		// set parameters from ruleFactory
		this.separatorText.setText(ruleFactory.getSentenceSeparator());
		this.regexCheckbox.setSelected(ruleFactory.isRegex());
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
		this.targetCmb.addActionListener(this);
		this.targetCmb.setSelectedIndex(this.ruleFactory.getTarget());
		add(targetCmb, 2);

		// remove previous operation combobox and create a new one
		if (operationCmb != null) {
			remove(operationCmb);
		}
		this.operationCmb = new SteppedComboBox(new DefaultComboBoxModel<String>(operationValues));
		this.operationCmb.setPreferredSize(new Dimension(this.targetCmb.getPreferredSize()));
		this.operationCmb.setMaximumSize(this.operationCmb.getPreferredSize());
		this.operationCmb.addActionListener(this);
		this.operationCmb.setSelectedIndex(this.ruleFactory.getOperation());
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
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == this.targetCmb) {
			this.ruleFactory.setTarget(this.targetCmb.getSelectedIndex());
			
		} else if (source == this.operationCmb) {
			this.ruleFactory.setOperation(this.operationCmb.getSelectedIndex());
			
			if (this.operationCmb.getSelectedIndex() == ChangeCaseFactory.OPERATION_CAPITALIZE_SENTENCES) {
				this.separatorLabel.setVisible(true);
				this.separatorText .setVisible(true);
				this.regexCheckbox .setVisible(true);
				
			} else {
				this.separatorLabel.setVisible(false);
				this.separatorText .setVisible(false);
				this.regexCheckbox .setVisible(false);
			}
		
		} else {
			return;
		}
		
		super.configurationChanged();
	}

	@Override
	public void changedUpdate(DocumentEvent event) {
		try {
			this.ruleFactory.setSentenceSeparator(this.separatorText.getText());
			super.configurationChanged();
			
		} catch (Exception exception) {
			if (LogManager.ENABLED) LogManager.error("ChangeCasePanel.changedUpdate> Invalid regex separator: " + this.separatorText.getText());
			// TODO: notify user, i.e. highlight input field 
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
			this.ruleFactory.setRegex(this.regexCheckbox.isSelected());
			super.configurationChanged();
			
		} catch (Exception exception) {
			if (LogManager.ENABLED) LogManager.error("ChangeCasePanel.itemStateChanged> Invalid regex separator: " + this.separatorText.getText());
			// TODO: notify user, i.e. highlight input field 
		}
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
}
