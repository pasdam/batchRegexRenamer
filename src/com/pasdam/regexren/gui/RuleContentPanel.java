package com.pasdam.regexren.gui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.pasdam.regexren.controller.LocaleManager.Localizable;
import com.pasdam.regexren.gui.rules.AbstractRuleFactory;

/**
 * <p>
 * All rule panels must extends this class.
 * </p>
 * 
 * @author paco
 * @version 0.1
 */
public abstract class RuleContentPanel<T extends AbstractRuleFactory> extends JPanel implements Localizable {

	private static final long serialVersionUID = -5837835553536001443L;

	/** Height of panel with only one row */
	public static final int ONE_ROW_PANEL_HEIGHT = 35;

	/** Height of panel with two rows */
	public static final int TWO_ROW_PANEL_HEIGHT = 55;

	/** Size of a short space */
	public static final int FIXED_SPACE_SHORT = 10;

	/** Size of a long space */
	public static final int FIXED_SPACE_LONG = 20;
	
	/** Height of panel's widgets */
	public static final int WIDGET_HEIGHT = 20;
	
	/**	Minimum width of text fields */
	public static final int WIDGET_TEXT_MIN_WIDTH = 100;
	
	/**	Minimum width of spinners */
	public static final int WIDGET_SPINNER_MIN_WIDTH = 50;
	
	/** Rule factory related to the content */
	protected final T ruleFactory;
	
	/** 
	 * Constructor that initialize the type of the rule
	 * 
	 * @param ruleFactory rule factory associated with the panel
	 * @param panelHeight
	 * @param layoutAxis
	 */
	public RuleContentPanel(T ruleFactory, int panelHeight, int layoutAxis) {
		this.ruleFactory = ruleFactory;
		
		// set panel style
		setBorder(UIManager.getBorder("Button.border"));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, panelHeight));
		setLayout(new BoxLayout(this, layoutAxis));
	}
	
	/**
	 * Returns the rule factory associated with this view
	 * 
	 * @return the rule factory associated with this view
	 */
	public T getRuleFactory() {
		return ruleFactory;
	}
	
	/**
	 * Returns a brief description of the rule, to be shown as title
	 * 
	 * @return a brief description of the rule, to be shown as title
	 */
	protected abstract String getDescription();
}
