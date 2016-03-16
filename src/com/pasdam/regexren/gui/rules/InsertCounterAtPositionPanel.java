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

import com.pasdam.gui.swing.widgets.SteppedComboBox;
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
	private SteppedComboBox fromCombobox;
	private SteppedComboBox targetCombobox;
	
	/** Array of "From" combobox values */
	private final String[] fromValues = new String[2];
	
	/** Array of "Target" combobox values */
	private final String[] targetValues = new String[2];
	
	/** Handler of internal events */
	private final InternalEventHandler eventHandler;
	
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
		
		// create spacer and add to the panel
		add(Box.createHorizontalStrut(FIXED_SPACE_LONG));
		
		// create target label and add to the panel
		this.targetLabel = new JLabel();
		add(targetLabel);
		
		// create spacer and add to the panel
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create and add to the panel
		add(Box.createHorizontalGlue());
		
		// read initial values from factory
		this.positionSpinner.setValue(super.ruleFactory.getPosition() + 1);
		
		// create and set event handler
		this.eventHandler = new InternalEventHandler();
		this.positionSpinner.addChangeListener(this.eventHandler);
	}
	
	/**	Updates the target and operations comboboxes */
	private void updateCombos() {
		if (this.fromCombobox != null) {
			remove(this.fromCombobox);
		}
		this.fromCombobox = new SteppedComboBox(new DefaultComboBoxModel<String>(this.fromValues));
		this.fromCombobox.setPreferredSize(new Dimension(WIDGET_TEXT_MIN_WIDTH, WIDGET_HEIGHT));
		this.fromCombobox.setMaximumSize(new Dimension(this.fromCombobox.getPreferredSize()));
		this.fromCombobox.setSelectedIndex(super.ruleFactory.isFromBegin() ? FROM_BEGIN : FROM_END);
		this.fromCombobox.addActionListener(this.eventHandler);
		add(this.fromCombobox, 14);
		
		if (this.targetCombobox != null) {
			remove(this.targetCombobox);
		}
		this.targetCombobox = new SteppedComboBox(new DefaultComboBoxModel<String>(this.targetValues));
		this.targetCombobox.setPreferredSize(new Dimension(this.fromCombobox.getPreferredSize()));
		this.targetCombobox.setMaximumSize(new Dimension(this.targetCombobox.getPreferredSize()));
		this.targetCombobox.setSelectedIndex(super.ruleFactory.getTarget());
		this.targetCombobox.addActionListener(this.eventHandler);
		add(this.targetCombobox, 18);
	}
	
	@Override
	public void localeChanged(LocaleManager localeManager) {
		super.localeChanged(localeManager);
		
		this.description                                               = localeManager.getString("Rule.insertCounterPosition.description");
		this.fromValues[FROM_BEGIN]                                    = localeManager.getString("Rule.begin");
		this.fromValues[FROM_END]                                      = localeManager.getString("Rule.end");
		this.targetValues[InsertCounterAtPositionFactory.OF_NAME]      = localeManager.getString("Rule.name");
		this.targetValues[InsertCounterAtPositionFactory.OF_EXTENSION] = localeManager.getString("Rule.extension");	
		
		this.positionLabel    .setText(localeManager.getString("Rule.position"));
		this.fromLabel        .setText(localeManager.getString("Rule.from"));
		this.targetLabel      .setText(localeManager.getString("Rule.of"));
		
		updateCombos();
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
