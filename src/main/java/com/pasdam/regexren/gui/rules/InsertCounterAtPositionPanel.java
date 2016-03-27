package com.pasdam.regexren.gui.rules;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pasdam.gui.swing.widgets.WideComboBox;
import com.pasdam.regexren.controller.LocaleManager;

/**
 * Panel used to configure a "Insert counter at position" rule
 * 
 * @author paco
 * @version 0.1
 */
public class InsertCounterAtPositionPanel extends AbstractInsertCounterPanel<InsertCounterAtPositionFactory> {
	
	private static final long serialVersionUID = -6847543507848152241L;

	/**	Indicates the index of the combobox for the value "Begin" */
	private static final int FROM_BEGIN = 0;

	/**	Indicates the index of the combobox for the value "End" */
	private static final int FROM_END = FROM_BEGIN + 1;

	// UI components
	private JLabel fromLabel;
	private JLabel positionLabel;
	private JLabel targetLabel;
	private JSpinner positionSpinner;
	private WideComboBox fromCombobox;
	private WideComboBox targetCombobox;
	
	/** Array of "From" combobox values */
	private final String[] fromValues = new String[2];
	
	/** Array of "Target" combobox values */
	private final String[] targetValues = new String[2];
	
	/**	Description pattern */
	private String description;

	/** Create the panel */
	public InsertCounterAtPositionPanel(InsertCounterAtPositionFactory ruleFactory) {
		super(ruleFactory);
		
		// create position label and add to the panel
		this.positionLabel = new JLabel();
		add(positionLabel);
		
		// create spacer and add to the panel
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create position spinner and add to the panel
		this.positionSpinner = new JSpinner();
		this.positionSpinner.setPreferredSize(new Dimension(new Dimension(WIDGET_SPINNER_MIN_WIDTH, WIDGET_HEIGHT)));
		this.positionSpinner.setMaximumSize(new Dimension(this.positionSpinner.getPreferredSize()));
		this.positionSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		add(positionSpinner);
		
		// create spacer and add to the panel
		add(Box.createHorizontalStrut(FIXED_SPACE_LONG));
		
		// create from label and add to the panel
		this.fromLabel = new JLabel();
		add(fromLabel);
		
		// create spacer and add to the panel
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create and add from combobox to the panel
		this.fromCombobox = new WideComboBox();
		this.fromCombobox.setMaximumSize(new Dimension(WIDGET_TEXT_MIN_WIDTH, WIDGET_HEIGHT));
		add(this.fromCombobox);
		
		// create spacer and add to the panel
		add(Box.createHorizontalStrut(FIXED_SPACE_LONG));
		
		// create target label and add to the panel
		this.targetLabel = new JLabel();
		add(targetLabel);
		
		// create spacer and add to the panel
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create and add from combobox to the panel
		this.targetCombobox = new WideComboBox();
		this.targetCombobox.setMaximumSize(new Dimension(WIDGET_TEXT_MIN_WIDTH, WIDGET_HEIGHT));
		add(this.targetCombobox);
		
		// create and add to the panel
		add(Box.createHorizontalGlue());
		
		// read initial values from factory
		this.positionSpinner.setValue(super.ruleFactory.getPosition() + 1);
		
		// create and set event handler
		InternalEventHandler eventHandler = new InternalEventHandler();
		this.fromCombobox.addActionListener(eventHandler);
		this.positionSpinner.addChangeListener(eventHandler);
		this.targetCombobox.addActionListener(eventHandler);
	}
	
	@Override
	public void localeChanged(LocaleManager localeManager) {
		super.localeChanged(localeManager);
		
		this.description = localeManager.getString("Rule.insertCounterPosition.description");
		
		this.fromLabel    .setText(localeManager.getString("Rule.from"));
		this.positionLabel.setText(localeManager.getString("Rule.position"));
		this.targetLabel  .setText(localeManager.getString("Rule.of"));
		
		// update comboboxes
		this.fromValues[FROM_BEGIN]                                    = localeManager.getString("Rule.begin");
		this.fromValues[FROM_END]                                      = localeManager.getString("Rule.end");
		this.targetValues[InsertCounterAtPositionFactory.OF_NAME]      = localeManager.getString("Rule.name");
		this.targetValues[InsertCounterAtPositionFactory.OF_EXTENSION] = localeManager.getString("Rule.extension");	
		this.fromCombobox.setModel(new DefaultComboBoxModel<String>(this.fromValues));
		this.fromCombobox.setSelectedIndex(super.ruleFactory.isFromBegin() ? FROM_BEGIN : FROM_END);
		this.fromCombobox.setToolTipText(this.fromCombobox.getSelectedItem().toString());
		this.targetCombobox.setModel(new DefaultComboBoxModel<String>(this.targetValues));
		this.targetCombobox.setSelectedIndex(super.ruleFactory.getTarget());
		this.targetCombobox.setToolTipText(this.targetCombobox.getSelectedItem().toString());
	}

	@Override
	protected String getDescription() {
		return String.format(
				this.description,
				super.ruleFactory.getStartCount(),
				super.ruleFactory.getPosition() + 1,
				this.fromCombobox.getSelectedItem(),
				this.targetCombobox.getSelectedItem()
		);
	}
	
	/** Class that handles internal events */
	private class InternalEventHandler implements ActionListener, ChangeListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == InsertCounterAtPositionPanel.this.fromCombobox) {
				InsertCounterAtPositionPanel.super.ruleFactory.setFromBegin(
						InsertCounterAtPositionPanel.this.fromCombobox.getSelectedIndex() == FROM_BEGIN);
				
			} else if (source == InsertCounterAtPositionPanel.this.targetCombobox) {
				InsertCounterAtPositionPanel.super.ruleFactory.setTarget(
						InsertCounterAtPositionPanel.this.targetCombobox.getSelectedIndex());
			}
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {
			InsertCounterAtPositionPanel.super.ruleFactory.setPosition(
					((Integer)InsertCounterAtPositionPanel.this.positionSpinner.getValue()) - 1);
		}
	}
}
