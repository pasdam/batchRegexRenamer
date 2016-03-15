package com.pasdam.regexren.gui.rules;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
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
 * Panel used to configure a "Insert text at position" rule
 * 
 * @author paco
 * @version 0.1
 */
public class InsertTextAtPositionPanel extends RuleContentPanel<InsertTextAtPositionFactory> {
	
	private static final long serialVersionUID = 2570655458263744568L;

	/** Used to specify that position starts from begin */
	public static final int FROM_BEGIN = 0;
	
	/** Used to specify that position starts from end */
	public static final int FROM_END = FROM_BEGIN + 1;
	
	// UI components
	private JTextField textToInsertField;
	private JLabel textToInsertLabel;
	private JLabel positionLabel;
	private JLabel fromLabel;
	private JLabel targetLabel;
	private JSpinner positionSpinner;
	private SteppedComboBox fromCombobox;
	private SteppedComboBox targetCombobox;
	private Box row2Panel;
	
	/** Array of "From" combobox values */
	private final String[] fromValues = new String[2];
	
	/** Array of "Target" combobox values */
	private final String[] targetValues   = new String[2];

	/** Handler of internal events */
	private final InternalEventHandler eventHandler;
	
	/**	Description pattern */
	private String description;

	/** Create the panel */
	public InsertTextAtPositionPanel(InsertTextAtPositionFactory ruleFactory) {
		super(ruleFactory, TWO_ROW_PANEL_HEIGHT, BoxLayout.Y_AXIS);
		
		// create first row
		Box row1Panel = Box.createHorizontalBox();
		row1Panel.setBorder(UIManager.getBorder("ComboBox.editorBorder"));
		
		// create text to insert label and add it to the panel		
		this.textToInsertLabel = new JLabel();
		row1Panel.add(this.textToInsertLabel);
		
		// create spacer and add to the panel
		row1Panel.add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create text to insert field and add it to the panel
		this.textToInsertField = new JTextField();
		row1Panel.add(this.textToInsertField);

		// add first row to the panel
		add(row1Panel);

		// create first row
		this.row2Panel = Box.createHorizontalBox();
		this.row2Panel.setBorder(UIManager.getBorder("ComboBox.editorBorder"));
		
		// create position label and add to the panel
		this.positionLabel = new JLabel();
		this.row2Panel.add(positionLabel);
		
		// create spacer and add to the panel
		this.row2Panel.add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create position spinner and add to the panel
		this.positionSpinner = new JSpinner();
		this.positionSpinner.setPreferredSize(new Dimension(new Dimension(WIDGET_SPINNER_MIN_WIDTH, WIDGET_HEIGHT)));
		this.positionSpinner.setMaximumSize(new Dimension(this.positionSpinner.getPreferredSize()));
		this.positionSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		this.row2Panel.add(positionSpinner);
		
		// create spacer and add to the panel
		this.row2Panel.add(Box.createHorizontalStrut(FIXED_SPACE_LONG));
		
		// create from label and add to the panel
		this.fromLabel = new JLabel();
		this.row2Panel.add(fromLabel);
		
		// create spacer and add to the panel
		this.row2Panel.add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create spacer and add to the panel
		this.row2Panel.add(Box.createHorizontalStrut(FIXED_SPACE_LONG));
		
		// create target label and add to the panel
		this.targetLabel = new JLabel();
		this.row2Panel.add(targetLabel);
		
		// create spacer and add to the panel
		this.row2Panel.add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create spacer and add to the panel
		this.row2Panel.add(Box.createHorizontalGlue());

		// add second row to the panel
		add(this.row2Panel);
		
		// read initial values from rule factory
		this.textToInsertField.setText(ruleFactory.getTextToInsert());
		this.positionSpinner.setValue(ruleFactory.getPosition() + 1);

		// create and set event handler
		this.eventHandler = new InternalEventHandler();
		this.positionSpinner.addChangeListener(this.eventHandler);
		this.textToInsertField.getDocument().addDocumentListener(this.eventHandler);
	}
	
	private void updateCombos() {
		if (this.fromCombobox != null) {
			remove(this.fromCombobox);
		}
		this.fromCombobox = new SteppedComboBox(new DefaultComboBoxModel<String>(this.fromValues));
		this.fromCombobox.setPreferredSize(new Dimension(WIDGET_TEXT_MIN_WIDTH, WIDGET_HEIGHT));
		this.fromCombobox.setMaximumSize(new Dimension(this.fromCombobox.getPreferredSize()));
		this.fromCombobox.setSelectedIndex(super.ruleFactory.isFromBegin() ? FROM_BEGIN : FROM_END);
		this.fromCombobox.addActionListener(this.eventHandler);
		this.row2Panel.add(this.fromCombobox, 6);
		
		if (this.targetCombobox != null) {
			remove(this.targetCombobox);
		}
		this.targetCombobox = new SteppedComboBox(new DefaultComboBoxModel<String>(this.targetValues));
		this.targetCombobox.setPreferredSize(new Dimension(this.fromCombobox.getPreferredSize()));
		this.targetCombobox.setMaximumSize(new Dimension(this.targetCombobox.getPreferredSize()));
		this.targetCombobox.setSelectedIndex(super.ruleFactory.getTarget());
		this.targetCombobox.addActionListener(this.eventHandler);
		this.row2Panel.add(this.targetCombobox, this.row2Panel.getComponentCount()-1);
	}
	
	@Override
	public void localeChanged(LocaleManager localeManager) {
		this.description            = localeManager.getString("Rule.insertTextPosition.description");
		this.fromValues[FROM_BEGIN] = localeManager.getString("Rule.begin");
		this.fromValues[FROM_END]   = localeManager.getString("Rule.end");
		this.targetValues[InsertTextAtPositionFactory.TARGET_NAME]      = localeManager.getString("Rule.name");
		this.targetValues[InsertTextAtPositionFactory.TARGET_EXTENSION] = localeManager.getString("Rule.extension");
		this.textToInsertLabel.setText(localeManager.getString("Rule.text"));
		this.positionLabel.setText(localeManager.getString("Rule.position"));
		this.fromLabel.setText(localeManager.getString("Rule.from"));
		this.targetLabel.setText(localeManager.getString("Rule.of"));
		updateCombos();
	}

	@Override
	protected String getDescription() {
		String textToInsert = super.ruleFactory.getTextToInsert();
		return String.format(
				this.description,
				textToInsert != null ? textToInsert : "",
				super.ruleFactory.getPosition() + 1,
				this.fromCombobox.getSelectedItem(),
				this.targetCombobox.getSelectedItem()
		);
	}

	/** Class that handles internal events */
	private class InternalEventHandler implements ActionListener, ChangeListener, DocumentListener {

		@Override
		public void changedUpdate(DocumentEvent arg0) {
			try {
				InsertTextAtPositionPanel.super.ruleFactory.setTextToInsert(
						InsertTextAtPositionPanel.this.textToInsertField.getText());

				// reset border
				InsertTextAtPositionPanel.this.textToInsertField.setBorder(UIManager.getBorder("TextField.border"));
				
			} catch (IllegalArgumentException e) {
				InsertTextAtPositionPanel.this.textToInsertField.setBorder(BorderFactory.createLineBorder(Color.RED));
				if (LogManager.ENABLED) LogManager.error("InsertTextAtPositionPanel.InternalEventHandler.changedUpdate> Text is null or empty");
			}
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			changedUpdate(null);
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			changedUpdate(null);
		}

		@Override
		public void stateChanged(ChangeEvent arg0) {
			InsertTextAtPositionPanel.super.ruleFactory.setPosition(
					((Integer)InsertTextAtPositionPanel.this.positionSpinner.getValue()) - 1);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();
			
			if (source == InsertTextAtPositionPanel.this.fromCombobox) {
				InsertTextAtPositionPanel.super.ruleFactory.setFromBegin(
						InsertTextAtPositionPanel.this.fromCombobox.getSelectedIndex() == FROM_BEGIN);
				
			} else if (source == InsertTextAtPositionPanel.this.targetCombobox) {
				InsertTextAtPositionPanel.super.ruleFactory.setTarget(
						InsertTextAtPositionPanel.this.targetCombobox.getSelectedIndex());
			}
		}
	}
}
