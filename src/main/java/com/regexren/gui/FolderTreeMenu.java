package com.pasdam.regexren.gui;

import java.awt.Component;
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
 * Context menu for the folder tree
 * 
 * @author paco
 * @version 0.1
 */
class FolderTreeMenu extends FileItemPopupMenu implements Localizable {
	
	private static final long serialVersionUID = -1665415082043828308L;

	// UI components
	private final JMenuItem openFolderItem;
	private final JMenuItem refreshFolderItem;
	
	/** Creates the menu */
	public FolderTreeMenu() {
		// create items
		this.openFolderItem = new JMenuItem();
		this.refreshFolderItem = new JMenuItem();
		
		// add items to the menu
		add(this.openFolderItem);
		add(this.refreshFolderItem);
		
		// create and set listener
		InternalEventHandler eventHandler = new InternalEventHandler();
		this.openFolderItem.addActionListener(eventHandler);
		this.refreshFolderItem.addActionListener(eventHandler);
	}
	
	@Override
	public void localeChanged(LocaleManager localeManager) {
		this.openFolderItem.setText(localeManager.getString("FolderTree.menu.openFolder"));
		this.refreshFolderItem.setText(localeManager.getString("FolderTree.menu.refresh"));
	}
	
	@Override
	public void show(Component arg0, int arg1, int arg2) {
		this.refreshFolderItem.setEnabled(super.folder.equals(ApplicationManager.getInstance().getPreferenceManager().getPreviousFolder()));
		super.show(arg0, arg1, arg2);
	}
	
	/** Class that handles internal events */
	private class InternalEventHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();
			if (source == FolderTreeMenu.this.openFolderItem) {
				try {
					// open the folder with system file manager
					Desktop.getDesktop().open(FolderTreeMenu.super.folder);
					
				} catch (IOException exception) {
					exception.printStackTrace();
				}
				
			} else if (source == FolderTreeMenu.this.refreshFolderItem) {
				ApplicationManager.getInstance().getPreferenceManager().setPreviousFolder(FolderTreeMenu.super.folder, true);
			}
		}
	}
}
