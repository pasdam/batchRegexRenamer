package com.pasdam.regexren.gui;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JMenuItem;

import com.pasdam.gui.swing.folderTree.FileItemPopupMenu;
import com.pasdam.regexren.controller.ApplicationManager;
import com.pasdam.regexren.controller.LocaleManager;
import com.pasdam.regexren.controller.LocaleManager.Localizable;

/**
 * Context menu for expandable rules
 * 
 * @author paco
 * @version 0.1
 */
class FilesTableMenu extends FileItemPopupMenu implements Localizable {
	
	private static final long serialVersionUID = 1146198431195195039L;

	// UI components
	private final JMenuItem openItem;
	
	/** Creates the menu */
	public FilesTableMenu() {
		// create items
		this.openItem = new JMenuItem();
		
		// add items to the menu
		add(this.openItem);
		
		// create and set listener
		this.openItem.addActionListener(new InternalEventHandler());
	}
	
	@Override
	public void localeChanged(LocaleManager localeManager) {
		this.openItem.setText(localeManager.getString("FilesTable.menu.openExternally"));
	}
	
	/** Class that handles internal events */
	private class InternalEventHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();
			if (source == FilesTableMenu.this.openItem) {
				try {
					Desktop.getDesktop().open(FilesTableMenu.super.folder);
					
				} catch (IOException exception) {
					exception.printStackTrace();
				}
				
			} else {
				ApplicationManager.getInstance().getPreferenceManager().setPreviousFolder(FilesTableMenu.super.folder, true);
			}
		}
	}
}
