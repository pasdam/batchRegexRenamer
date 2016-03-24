package com.pasdam.regexren.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileFilter;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.pasdam.regexren.controller.ApplicationManager;
import com.pasdam.regexren.controller.LocaleManager;
import com.pasdam.regexren.controller.LocaleManager.Localizable;
import com.pasdam.regexren.controller.LogManager;

/**
 * Toolbar for current folder panel
 * 
 * @author paco
 * @version 1.0
 */
public class CurrentFolderToolbar extends JToolBar implements Localizable {

	private static final long serialVersionUID = 4958325718329266773L;

	// UI components
	private final CheckMenu checkMenu;
	private final FilterMenu filtersMenu;
	private final JButton checkButton;
	private final JButton filterButton;
	private final JButton parentFolderButton;
	private final JTextField folderPathText;
	
	/** If true folder changes will not be propagated to the text view */
	private boolean ignoreFolderChange = false;
	
	/** Current opened folder */
	private File currentFolder;

	/** Create the panel. */
	public CurrentFolderToolbar() {
		// set layout properties
		setFloatable(false);
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		
		// create event handler
		InternalEventHandler handler = new InternalEventHandler();
		
		// create and add "Check elements" button
		this.checkMenu = new CheckMenu();
		this.checkButton = new PopupMenuButton(this.checkMenu);
		this.checkButton.setIcon(new ImageIcon("images" + File.separator + "check.png"));
		add(this.checkButton);
		
		// create and add separator
		add(new JSeparator(JSeparator.VERTICAL));
		
		this.parentFolderButton = new JButton();
		this.parentFolderButton.setIcon(new ImageIcon(CurrentFolderToolbar.class.getResource("/com/sun/java/swing/plaf/windows/icons/UpFolder.gif")));
		this.parentFolderButton.addMouseListener(handler);
		add(this.parentFolderButton);
		
		// create and add "Current folder path" text field
		this.folderPathText = new JTextField();
		this.folderPathText.setColumns(10);
		this.folderPathText.getDocument().addDocumentListener(handler);
		add(this.folderPathText);
		
		// create and add filters button
		this.filtersMenu = new FilterMenu();
		this.filterButton = new PopupMenuButton(this.filtersMenu);
		this.filterButton.setIcon(new ImageIcon("images" + File.separator + "filter.png"));
		add(this.filterButton);
	}
	
	@Override
	public void localeChanged(LocaleManager localeManager) {
		if (LogManager.ENABLED) LogManager.trace("CurrentFolderPanel.localeChanged> reloading texts.");
		this.checkButton		.setToolTipText(localeManager.getString("CheckButton.toolTipText"));
		this.filterButton		.setToolTipText(localeManager.getString("FilterButton.toolTipText"));
		this.parentFolderButton	.setToolTipText(localeManager.getString("CurrentFolderPanel.parentFolder.toolTipText"));
		
		this.checkMenu.localeChanged(localeManager);
		this.filtersMenu.localeChanged(localeManager);
	}
	
	/**
	 * Sets listener to notify when check setting change.
	 * 
	 * @param listener
	 *            listener to notify when check setting change
	 */
	public void setCheckItemsListener(CheckItemsListener listener) {
		this.checkMenu.setCheckItemsListener(listener);
	}

	/**
	 * Sets current folder
	 * 
	 * @param folder
	 *            folder to set
	 */
	public void setCurrentFolder(File folder) {
		this.currentFolder = folder;
		if (!this.ignoreFolderChange) {
			this.folderPathText.setText(folder.getAbsolutePath());
		}
	}

	/**
	 * Sets filter property
	 * 
	 * @param filterType
	 *            type of the filter to select in the menu
	 * @param showHidden
	 *            indicates if the element "Show hidden" should be selected
	 * @param filter
	 *            filter to apply
	 */
	public void setFilter(int filterType, boolean showHidden, FileFilter filter) {
		this.filtersMenu.selectFilter(filterType, showHidden);
	}
	
	/** Class that handles all event fired by or direct to internal components */
	private class InternalEventHandler implements DocumentListener, MouseListener {

		/** "Parent folder" button mouse handler method */
		@Override
		public void mouseClicked(MouseEvent e) {
			ApplicationManager.getInstance().getPreferenceManager().setPreviousFolder(CurrentFolderToolbar.this.currentFolder.getParentFile(), false);
		}

		/** Text field handler method */
		@Override
	    public void insertUpdate(DocumentEvent e) {
			File file = new File(CurrentFolderToolbar.this.folderPathText.getText());
			if (file.isDirectory() && file.exists()) {
				CurrentFolderToolbar.this.ignoreFolderChange = true;
				ApplicationManager.getInstance().getPreferenceManager().setPreviousFolder(file, false);
				CurrentFolderToolbar.this.ignoreFolderChange = false;
				
				// reset border
				CurrentFolderToolbar.this.folderPathText.setBorder(UIManager.getBorder("TextField.border"));
				
			} else {
				CurrentFolderToolbar.this.folderPathText.setBorder(BorderFactory.createLineBorder(Color.RED));
			}
	    }

		/** Text field handler method */
		@Override
	    public void removeUpdate(DocumentEvent e) {
	        insertUpdate(e);
	    }
		
		/** Event ignored */
		@Override
		public void mouseEntered(MouseEvent e) {}
		
		/** Event ignored */
		@Override
		public void mouseExited(MouseEvent e) {}
		
		/** Event ignored */
		@Override
		public void mousePressed(MouseEvent e) {}
		
		/** Event ignored */
		@Override
		public void mouseReleased(MouseEvent e) {}
	    
	    /** Event ignored */
	    @Override
	    public void changedUpdate(DocumentEvent e) {} // Plain text components do not fire these events
	}
}