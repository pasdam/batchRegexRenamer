package com.pasdam.regexren.gui.rules;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pasdam.regexren.controller.LocaleManager;
import com.pasdam.regexren.gui.RuleContentPanel;

/**
 * Abstract panel used to configure a "Insert counter..." rules
 * 
 * @author paco
 * @version 0.1
 */
abstract class AbstractInsertCounterPanel<T extends AbstractInsertCounterFactory> extends RuleContentPanel<T> {
	
	private static final long serialVersionUID = -6847543507848152241L;

	// UI components	
	private JLabel counterStartLabel;
	private JLabel paddingLabel;
	private JSpinner counterStartSpinner;
	private JSpinner paddingSpinner;
	
	/** Handler of internal events */
	private final InternalEventHandler eventHandler;

	/** Create the panel */
	public AbstractInsertCounterPanel(T ruleFactory) {
		super(ruleFactory, ONE_ROW_PANEL_HEIGHT, BoxLayout.X_AXIS);
		
		// create event handler
		this.eventHandler = new InternalEventHandler();
		
		// create start label and add to the panel
		this.counterStartLabel = new JLabel();
		add(counterStartLabel);
		
		// create spacer and add to the panel
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create start pinner and add to the panel
		this.counterStartSpinner = new JSpinner();
		this.counterStartSpinner.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		this.counterStartSpinner.setPreferredSize(new Dimension(WIDGET_SPINNER_MIN_WIDTH, WIDGET_HEIGHT));
		this.counterStartSpinner.setMaximumSize(new Dimension(this.counterStartSpinner.getPreferredSize()));     
		this.counterStartSpinner.addChangeListener(this.eventHandler);
		add(counterStartSpinner);

		// create spacer and add to the panel
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create padding label and add to the panel
		this.paddingLabel = new JLabel();
		add(paddingLabel);
		
		// create spacer and add to the panel
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// create padding spaced and add to the panel
		this.paddingSpinner = new JSpinner();
		this.paddingSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		this.paddingSpinner.setPreferredSize(new Dimension(this.counterStartSpinner.getPreferredSize()));
		this.paddingSpinner.setMaximumSize(new Dimension(this.paddingSpinner.getPreferredSize()));
		this.paddingSpinner.addChangeListener(this.eventHandler);
		add(paddingSpinner);
		
		// create spacer and add to the panel
		add(Box.createHorizontalStrut(FIXED_SPACE_SHORT));
		
		// read initial values from factory
		this.counterStartSpinner.setValue(super.ruleFactory.getStartCount());
		this.paddingSpinner.setValue(super.ruleFactory.getPadding());
	}
	
	@Override
	public void localeChanged(LocaleManager localeManager) {
		this.counterStartLabel.setText(localeManager.getString("Rule.counterStart"));
		this.paddingLabel     .setText(localeManager.getString("Rule.padding"));
		this.paddingSpinner   .setToolTipText(localeManager.getString("Rule.padding.tooltip"));
	}
	
	/** Class that handles internal events */
	private class InternalEventHandler implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			Object source = e.getSource();
			if (source == AbstractInsertCounterPanel.this.counterStartSpinner) {
				AbstractInsertCounterPanel.super.ruleFactory.setStartCount(
						((Integer)AbstractInsertCounterPanel.this.counterStartSpinner.getValue()));
			
			} else if (source == AbstractInsertCounterPanel.this.paddingSpinner) {
				AbstractInsertCounterPanel.super.ruleFactory.setPadding(
						((Integer)AbstractInsertCounterPanel.this.paddingSpinner.getValue()));
			}
		}
	}
}
