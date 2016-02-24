package com.pasdam.regexren.gui;

import javax.swing.JPanel;

import com.pasdam.regexren.controller.LocaleManager.Localizable;
import com.pasdam.regexren.gui.rules.AbstractRuleFactory;

/**
 * <p>
 * All rule panels must extends this class.
 * </p>
 * <p>
 * Derived class must call {@link #configurationChanged()} whenever their
 * content changes
 * </p>
 * 
 * @author paco
 * @version 0.1
 */
public abstract class RuleContentPanel extends JPanel implements Localizable {

	private static final long serialVersionUID = -5837835553536001443L;

	// public static final int ROW_HEIGHT = 20;
	
	/** Height of panel with only one row */
	public static final int ONE_ROW_PANEL_HEIGHT = 35;

	/** Height of panel with two rows */
	public static final int TWO_ROW_PANEL_HEIGHT = 55;

	/** Size of a short space */
	public static final int FIXED_SPACE_SHORT = 10;

	/** Size of a long space */
	public static final int FIXED_SPACE_LONG = 20;
	
	/** Listener to notify content changes */
	private RuleContentListener contentListener;
	
	/** Rule factory related to the content */
	private final AbstractRuleFactory ruleFactory;
	
	/** Constructor that initialize the type of the rule */
	public RuleContentPanel(AbstractRuleFactory ruleFactory) {
		this.ruleFactory = ruleFactory;
	}
	
	/**
	 * Returns the rule factory associated with this view
	 * 
	 * @return the rule factory associated with this view
	 */
	public AbstractRuleFactory getRuleFactory() {
		return ruleFactory;
	}
	
	/**
	 * Sets the content listener to notify on content changes
	 * 
	 * @param contentListener
	 *            listener of content changes
	 */
	public void setContentListener(RuleContentListener contentListener) {
		this.contentListener = contentListener;
		configurationChanged();
	}
	
	/** This method must be called by derived classes whenever their content is changed */
	protected void configurationChanged() {
		if (this.contentListener != null) {
			this.contentListener.descriptionChanged(getDescription());
		}
	}
	
	/**
	 * Returns a brief description of the rule, to be shown as title
	 * 
	 * @return a brief description of the rule, to be shown as title
	 */
	protected abstract String getDescription();
	
	/**
	 * Interface implemented by those classes that need to be notified of
	 * content changes
	 * 
	 * @author paco
	 * @version 0.1
	 */
	public static interface RuleContentListener {
		
		/**
		 * Indicates that the description of the rule is changed
		 * 
		 * @param description
		 *            new description
		 */
		public void descriptionChanged(String description);
	}
}
