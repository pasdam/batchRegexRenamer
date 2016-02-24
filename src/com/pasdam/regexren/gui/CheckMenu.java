package com.pasdam.regexren.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import com.pasdam.regexren.controller.LocaleManager;
import com.pasdam.regexren.controller.LocaleManager.Localizable;
import com.pasdam.regexren.gui.CheckItemsListener.Target;

/**
 * Menu that shows all available filters
 * 
 * @author paco
 * @version 0.1
 */
class CheckMenu extends JPopupMenu implements ActionListener, Localizable {

	private static final long serialVersionUID = -7494295291367256330L;

	/** Menu element */
	public final JMenuItem itemCheckAll, itemCheckNone, itemCheckSelected, itemUncheckSelected, itemInvertSelection;

	// ID of the menu items
	private static final int ITEM_CHECK_ALL        = 0;
	private static final int ITEM_CHECK_NONE       = 1;
	private static final int ITEM_CHECK_SELECTED   = 2;
	private static final int ITEM_UNCHECK_SELECTED = 3;
	private static final int ITEM_INVERT           = 4;

	/** Listener to notify when the check settings change */
	private CheckItemsListener checkItemsListener;

	/** Creates GUI elements */
	public CheckMenu() {
		// Check all button
		this.itemCheckAll = new JMenuItem();
		this.itemCheckAll.addActionListener(this);
		this.itemCheckAll.setIcon(new ImageIcon("images" + File.separator + "check_all.gif"));
		this.itemCheckAll.setActionCommand("" + ITEM_CHECK_ALL);
		add(this.itemCheckAll);

		// Check none button
		this.itemCheckNone = new JMenuItem();
		this.itemCheckNone.addActionListener(this);
		this.itemCheckNone.setIcon(new ImageIcon("images" + File.separator + "check_none.gif"));
		this.itemCheckNone.setActionCommand("" + ITEM_CHECK_NONE);
		add(this.itemCheckNone);

		// Items separator
		add(new JSeparator(JSeparator.HORIZONTAL));

		// check selected button
		this.itemCheckSelected = new JMenuItem();
		this.itemCheckSelected.addActionListener(this);
		this.itemCheckSelected.setIcon(new ImageIcon("images" + File.separator + "check_all.gif"));
		this.itemCheckSelected.setActionCommand("" + ITEM_CHECK_SELECTED);
		add(this.itemCheckSelected);

		// uncheck selected button
		this.itemUncheckSelected = new JMenuItem();
		this.itemUncheckSelected.addActionListener(this);
		this.itemUncheckSelected.setIcon(new ImageIcon("images" + File.separator + "check_none.gif"));
		this.itemUncheckSelected.setActionCommand("" + ITEM_UNCHECK_SELECTED);
		add(this.itemUncheckSelected);

		// Items separator
		add(new JSeparator(JSeparator.HORIZONTAL));

		// invert selection button
		this.itemInvertSelection = new JMenuItem();
		this.itemInvertSelection.addActionListener(this);
		this.itemInvertSelection.setIcon(new ImageIcon("images" + File.separator + "check.png"));
		this.itemInvertSelection.setActionCommand("" + ITEM_INVERT);
		add(this.itemInvertSelection);
	}

	/**
	 * Sets listener to notify when check setting change.<br />
	 * Listener must ignore the <i>check</i> parameter of
	 * {@link CheckItemsListener#checkElements(Target, boolean)}, if target is
	 * {@link Target#INVERTED_SELECTION}
	 * 
	 * @param listener
	 *            listener to notify when check setting change
	 */
	public void setCheckItemsListener(CheckItemsListener listener) {
		this.checkItemsListener = listener;
	}

	@Override
	public void localeChanged(LocaleManager localeManager) {
		this.itemCheckAll 		.setText(localeManager.getString("CheckMenu.checkAll.label"));
		this.itemCheckNone		.setText(localeManager.getString("CheckMenu.checkNone.label"));
		this.itemCheckSelected 	.setText(localeManager.getString("CheckMenu.checkSelected.label"));
		this.itemUncheckSelected.setText(localeManager.getString("CheckMenu.uncheckSelected.label"));
		this.itemInvertSelection.setText(localeManager.getString("CheckMenu.invertSelection.label"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.checkItemsListener != null) {
			int componentID = Integer.valueOf(e.getActionCommand().trim()).intValue();
			switch (componentID) {
				case ITEM_CHECK_ALL:
					this.checkItemsListener.checkElements(Target.ALL, true);
					break;
	
				case ITEM_CHECK_NONE:
					this.checkItemsListener.checkElements(Target.ALL, false);
					break;
	
				case ITEM_CHECK_SELECTED:
					this.checkItemsListener.checkElements(Target.SELECTION, true);
					break;
	
				case ITEM_UNCHECK_SELECTED:
					this.checkItemsListener.checkElements(Target.SELECTION, false);
					break;
	
				case ITEM_INVERT:
					this.checkItemsListener.checkElements(Target.INVERTED_SELECTION, false);
					break;
			}
		}
	}
}