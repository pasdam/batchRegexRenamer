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
 * Panel used to configure a "Move text..." rules
 * 
 * @author paco
 * @version 0.1
 */
public class MoveTextBeforeAfterPanel extends RuleContentPanel<MoveTextBeforeAfterFactory> {
	
	private static final long serialVersionUID = 1393856890092322820L;

	// UI components
	private JCheckBox matchCaseCheckbox;
	private JCheckBox regexCheckbox;
	private JLabel textToMoveLabel;
	private JTextField textToMoveField;
	private JTextField textToSearchField;
	private WideComboBox positionCombobox;

	/** Array of "Position" combobox values */
	private final String[] positionValues = new String[4];

	/** Handler of internal events */
	private final InternalEventHandler eventHandler;
	
	/**	Description pattern */
	private String description;

	public MoveTextBeforeAfterPanel(MoveTextBeforeAfterFactory ruleFactory) {
		// set rule's type
		super(ruleFactory, TWO_ROW_PANEL_HEIGHT, BoxLayout.Y_AXIS);
		
		// create first row and add it to the panel
		Box row1Panel = Box.createHorizontalBox();
		row1Panel.setBorder(UIManager.getBorder("ComboBox.editorBorder"));
		
		// create text to move label and add to the first row
		this.textToMoveLabel = new JLabel();
		row1Panel.add(this.textToMoveLabel);
		
		// create spacer and add it to first row
		row1Panel.add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create text to move field and add to the first row
		this.textToMoveField = new JTextField();
		row1Panel.add(this.textToMoveField);

		// add the first row to the panel
		add(row1Panel);
		
		// create second row and add it to the panel
		Box row2Panel = Box.createHorizontalBox();
		row2Panel.setBorder(UIManager.getBorder("ComboBox.editorBorder"));
		
		// create position combobox and add to the second row
		this.positionCombobox = new WideComboBox(new DefaultComboBoxModel<String>(this.positionValues));
		this.positionCombobox.setMaximumSize(new Dimension(WIDGET_TEXT_MIN_WIDTH, WIDGET_HEIGHT));
		row2Panel.add(this.positionCombobox, 0);
		
		// create spacer and add it to second row
		row2Panel.add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create text to search and add to the second row
		this.textToSearchField = new JTextField();
		row2Panel.add(this.textToSearchField);
		
		// create spacer and add it to second row
		row2Panel.add(Box.createHorizontalGlue());
		
		// create regex checkbox and add to the second row
		this.regexCheckbox = new JCheckBox();
		row2Panel.add(this.regexCheckbox);
		
		// create match case checkbox and add to the second row
		this.matchCaseCheckbox = new JCheckBox();
		row2Panel.add(this.matchCaseCheckbox);
		
		// add the second row to the panel
		add(row2Panel);
		
		// read initial values from rule factory
		this.matchCaseCheckbox.setSelected(ruleFactory.isMatchCase());
		this.regexCheckbox.setSelected(ruleFactory.isRegex());
		this.textToMoveField.setText(ruleFactory.getTextToMove());
		this.textToSearchField.setText(ruleFactory.getTextToSearch());
		
		// create and set event handler
		this.eventHandler = new InternalEventHandler();
		this.matchCaseCheckbox.addItemListener(this.eventHandler);
		this.positionCombobox.addActionListener(this.eventHandler);
		this.regexCheckbox.addItemListener(this.eventHandler);
		this.textToMoveField.getDocument().addDocumentListener(this.eventHandler);
		this.textToSearchField.getDocument().addDocumentListener(this.eventHandler);
	}

	@Override
	public void localeChanged(LocaleManager localeManager) {
		this.description = localeManager.getString("Rule.moveText.description");

		this.matchCaseCheckbox.setText(localeManager.getString("Rule.matchCase"));
		this.matchCaseCheckbox.setToolTipText(localeManager.getString("Rule.matchCase.tooltip"));
		this.regexCheckbox.setText(localeManager.getString("Rule.regex"));
		this.regexCheckbox.setToolTipText(localeManager.getString("Rule.regex.tooltip"));
		this.textToMoveLabel.setText(localeManager.getString("Rule.move"));
		
		this.positionValues[MoveTextBeforeAfterFactory.POSITION_BEFORE] = localeManager.getString("Rule.before");
		this.positionValues[MoveTextBeforeAfterFactory.POSITION_AFTER]  = localeManager.getString("Rule.after");
		this.positionValues[MoveTextBeforeAfterFactory.POSITION_BEGIN]  = localeManager.getString("Rule.begin");
		this.positionValues[MoveTextBeforeAfterFactory.POSITION_END]    = localeManager.getString("Rule.end");
		this.positionCombobox.setModel(new DefaultComboBoxModel<String>(this.positionValues));
		this.positionCombobox.setSelectedIndex(super.ruleFactory.getPosition());
	}

	@Override
	protected String getDescription() {
		String textToMove = super.ruleFactory.getTextToMove();
		return String.format(this.description, textToMove != null ? textToMove : "");
	}
	
	/** Class that handles internal events */
	private class InternalEventHandler implements ActionListener, DocumentListener, ItemListener {

		@Override
		public void changedUpdate(DocumentEvent event) {
			Document document = event.getDocument();
			try {
				if (document == MoveTextBeforeAfterPanel.this.textToMoveField.getDocument()) {
					MoveTextBeforeAfterPanel.this.getRuleFactory().setTextToMove(
							MoveTextBeforeAfterPanel.this.textToMoveField.getText());

				} else if (document == MoveTextBeforeAfterPanel.this.textToSearchField.getDocument()) {
					MoveTextBeforeAfterPanel.this.getRuleFactory().setTextToSearch(
							MoveTextBeforeAfterPanel.this.textToSearchField.getText());
				}

				// reset border
				MoveTextBeforeAfterPanel.this.textToMoveField.setBorder(UIManager.getBorder("TextField.border"));
				MoveTextBeforeAfterPanel.this.textToSearchField.setBorder(UIManager.getBorder("TextField.border"));
					
			} catch (InvalidParametersException exception) {
				// reset border
				MoveTextBeforeAfterPanel.this.textToMoveField.setBorder(UIManager.getBorder("TextField.border"));
				MoveTextBeforeAfterPanel.this.textToSearchField.setBorder(UIManager.getBorder("TextField.border"));
				// highlight invalid
				for (Integer id : exception.getInvalidParameter()) {
					switch (id) {
						case MoveTextBeforeAfterFactory.PARAMETER_TEXT_TO_MOVE:
							MoveTextBeforeAfterPanel.this.textToMoveField.setBorder(BorderFactory.createLineBorder(Color.RED));
							break;
						
						case MoveTextBeforeAfterFactory.PARAMETER_TEXT_TO_SEARCH:
							MoveTextBeforeAfterPanel.this.textToSearchField.setBorder(BorderFactory.createLineBorder(Color.RED));
							break;
	
						default:
							break;
					}
				}
				if (LogManager.ENABLED) LogManager.error("InsertTextBeforeAfterPanel.InternalEventHandler.changedUpdate> " + exception.getMessage());
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
			if (source == MoveTextBeforeAfterPanel.this.regexCheckbox) {
				// reset border
				MoveTextBeforeAfterPanel.this.textToMoveField.setBorder(UIManager.getBorder("TextField.border"));
				MoveTextBeforeAfterPanel.this.textToSearchField.setBorder(UIManager.getBorder("TextField.border"));

				try {
					MoveTextBeforeAfterPanel.this.getRuleFactory().setRegex(
							MoveTextBeforeAfterPanel.this.regexCheckbox.isSelected());
						
				} catch (InvalidParametersException exception) {
					// highlight invalid
					for (Integer id : exception.getInvalidParameter()) {
						switch (id) {
							case MoveTextBeforeAfterFactory.PARAMETER_TEXT_TO_MOVE:
								MoveTextBeforeAfterPanel.this.textToMoveField.setBorder(BorderFactory.createLineBorder(Color.RED));
								break;
							
							case MoveTextBeforeAfterFactory.PARAMETER_TEXT_TO_SEARCH:
								MoveTextBeforeAfterPanel.this.textToSearchField.setBorder(BorderFactory.createLineBorder(Color.RED));
								break;
		
							default:
								break;
						}
					}
					if (LogManager.ENABLED) LogManager.error("InsertTextBeforeAfterPanel.InternalEventHandler.stateChanged> " + exception.getMessage());
				}
				
			} else if (source == MoveTextBeforeAfterPanel.this.matchCaseCheckbox) {
				MoveTextBeforeAfterPanel.this.getRuleFactory().setMatchCase(
						MoveTextBeforeAfterPanel.this.matchCaseCheckbox.isSelected());
			}
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			// reset border
			MoveTextBeforeAfterPanel.this.textToMoveField.setBorder(UIManager.getBorder("TextField.border"));
			MoveTextBeforeAfterPanel.this.textToSearchField.setBorder(UIManager.getBorder("TextField.border"));

			try {
				int selectedIndex = MoveTextBeforeAfterPanel.this.positionCombobox.getSelectedIndex();
				
				MoveTextBeforeAfterPanel.this.textToSearchField.setVisible(selectedIndex == MoveTextBeforeAfterFactory.POSITION_AFTER
																		|| selectedIndex == MoveTextBeforeAfterFactory.POSITION_BEFORE);
				
				MoveTextBeforeAfterPanel.this.getRuleFactory().setPosition(selectedIndex);
				
			} catch (InvalidParametersException exception) {
				// highlight invalid
				for (Integer id : exception.getInvalidParameter()) {
					switch (id) {
						case MoveTextBeforeAfterFactory.PARAMETER_TEXT_TO_MOVE:
							MoveTextBeforeAfterPanel.this.textToMoveField.setBorder(BorderFactory.createLineBorder(Color.RED));
							break;
						
						case MoveTextBeforeAfterFactory.PARAMETER_TEXT_TO_SEARCH:
							MoveTextBeforeAfterPanel.this.textToSearchField.setBorder(BorderFactory.createLineBorder(Color.RED));
							break;
	
						default:
							break;
					}
				}
				if (LogManager.ENABLED) LogManager.error("InsertTextBeforeAfterPanel.InternalEventHandler.actionPerformed> " + exception.getMessage());
			}
		}
	}
}
