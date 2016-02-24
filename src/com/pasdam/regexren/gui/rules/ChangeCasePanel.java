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
 * @author Paco
 * @version 1.0
 */
public class ChangeCasePanel extends RuleContentPanel implements ActionListener, DocumentListener, ItemListener {

	private static final long serialVersionUID = 5273872928673106679L;

	private final String[] targetValues = new String[2];
	private final String[] operationValues = new String[4];

	// UI components
	private JLabel separatorLbl, targetLbl, operationLbl;
	private SteppedComboBox targetCmb, operationCmb;
	private JTextField separatorTxt;
	private JCheckBox regexChk;
	
	private final ChangeCaseFactory ruleFactory;
	
	/**
	 * Create the panel.
	 */
	public ChangeCasePanel(ChangeCaseFactory ruleFactory) {
		super(ruleFactory);
		
		this.ruleFactory = ruleFactory;
		
		// set panel properties
		setBorder(UIManager.getBorder("Button.border"));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, ONE_ROW_PANEL_HEIGHT));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
//		setLayout(new BorderLayout());
		
		// populate row
		this.targetLbl = new JLabel();
		this.targetLbl.setAlignmentX(CENTER_ALIGNMENT);
		
		add(targetLbl);
		
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));

		add(Box.createHorizontalStrut(FIXED_SPACE_LONG));
		
		this.operationLbl = new JLabel();
		
		add(operationLbl);
		
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		add(Box.createHorizontalStrut(FIXED_SPACE_LONG));
		
		this.separatorLbl = new JLabel();
		add(separatorLbl);

		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		this.separatorTxt = new JTextField();
		this.separatorTxt.setMaximumSize(new Dimension(10000, 100));
		this.separatorTxt.getDocument().addDocumentListener(this);
		add(separatorTxt);

		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		this.regexChk = new JCheckBox();
		this.regexChk.addItemListener(this);
		add(regexChk);
		
		setBorder(BorderFactory.createLineBorder(Color.black));
		
		// set parameters from ruleFactory
		this.separatorTxt.setText(ruleFactory.getSentenceSeparator());
		this.regexChk.setSelected(ruleFactory.isRegex());
	}
	
	private void updateCombos() {
		if (this.targetCmb != null) {
			remove(this.targetCmb);
		}
		
		this.targetCmb = new SteppedComboBox(new DefaultComboBoxModel<String>(targetValues));
		this.targetCmb.setPreferredSize(new Dimension(60, 20));
		this.targetCmb.setMinimumSize(new Dimension(60, 18));
		this.targetCmb.setMaximumSize(new Dimension(60, 32767));
		this.targetCmb.setSelectedIndex(this.ruleFactory.getTarget());
		this.targetCmb.addActionListener(this);
		add(targetCmb, 2);

		if (operationCmb != null) {
			remove(operationCmb);
		}
		this.operationCmb = new SteppedComboBox(new DefaultComboBoxModel<String>(operationValues));
		this.operationCmb.setPreferredSize(new Dimension(100, 20));
		this.operationCmb.setMinimumSize(new Dimension(100, 18));
		this.operationCmb.setMaximumSize(new Dimension(100, 32767));
		this.operationCmb.setSelectedIndex(this.ruleFactory.getOperation());
		this.operationCmb.addActionListener(this);
		// hide separator label/text
		actionPerformed(new ActionEvent(operationCmb, 0, null));
		add(operationCmb, 6);
	}
	
	@Override
	public void localeChanged(LocaleManager localeManager) {
		this.targetLbl	 .setText(localeManager.getString("Rule.target"));
		this.operationLbl.setText(localeManager.getString("Rule.operation"));
		this.separatorLbl.setText(localeManager.getString("Rule.sentencesSeparator"));
		this.regexChk	 .setText(localeManager.getString("Rule.regex"));
		this.regexChk	 .setToolTipText(localeManager.getString("Rule.regex.tooltip"));

		this.targetValues[ChangeCaseFactory.TARGET_NAME]                       = localeManager.getString("Rule.name");
		this.targetValues[ChangeCaseFactory.TARGET_EXTENSION]                  = localeManager.getString("Rule.extension");
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
		
		} else {
			return;
		}

		if (operationCmb.getSelectedIndex() == ChangeCaseFactory.OPERATION_CAPITALIZE_SENTENCES) {
			separatorLbl.setVisible(true);
			separatorTxt.setVisible(true);
			regexChk.setVisible(true);
		} else {
			separatorLbl.setVisible(false);
			separatorTxt.setVisible(false);
			regexChk.setVisible(false);
		}
		
		super.configurationChanged();
	}

	@Override
	public void changedUpdate(DocumentEvent event) {
		try {
			this.ruleFactory.setSentenceSeparator(this.separatorTxt.getText());
			super.configurationChanged();
			
		} catch (Exception exception) {
			if (LogManager.ENABLED) LogManager.error("RulesPanel.changedUpdate> Invalid regex separator: " + this.separatorTxt.getText());
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
			this.ruleFactory.setRegex(this.regexChk.isSelected());
			super.configurationChanged();
			
		} catch (Exception exception) {
			if (LogManager.ENABLED) LogManager.error("RulesPanel.itemStateChanged> Invalid regex separator: " + this.separatorTxt.getText());
			// TODO: notify user, i.e. highlight input field 
		}
	}

	@Override
	protected String getDescription() {
		if (this.operationCmb != null && this.targetCmb != null) {
			return this.operationCmb.getSelectedItem() + " [" + this.targetCmb.getSelectedItem()
					+ (this.separatorTxt.isVisible() ? "]: " + this.separatorTxt.getText() : "]");
		} else {
			return "";
		}
	}
}
