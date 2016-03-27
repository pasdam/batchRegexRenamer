package com.pasdam.regexren.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

import com.pasdam.regexren.controller.ApplicationManager;
import com.pasdam.regexren.controller.FilterManager;
import com.pasdam.regexren.controller.LocaleManager;
import com.pasdam.regexren.controller.LocaleManager.Localizable;

/**
 * Menu that shows all available filters
 * 
 * @author paco
 * @version 0.1
 */
class FilterMenu extends JPopupMenu implements Localizable, ItemListener {

	private static final long serialVersionUID = 6704274830948974630L;
	
	/** ID of the "Show hidden" menu item */
	private static final int FILTER_SHOW_HIDDEN = 9;
	
	// GUI elements
	private final JCheckBoxMenuItem showHiddenItem;
	private final JRadioButtonMenuItem allItem;
	private final JRadioButtonMenuItem archiveItem;
	private final JRadioButtonMenuItem audioItem;
	private final JRadioButtonMenuItem documentItem;
	private final JRadioButtonMenuItem fileItem;
	private final JRadioButtonMenuItem folderItem;
	private final JRadioButtonMenuItem imageItem;
	private final JRadioButtonMenuItem mediaArchiveItem;
	private final JRadioButtonMenuItem videoItem;
	
	/** Creates GUI elements */
	public FilterMenu() {
		// Create default item

		// "show hidden" entry
		this.showHiddenItem = new JCheckBoxMenuItem();
		this.showHiddenItem.setName("" + FILTER_SHOW_HIDDEN);
		this.showHiddenItem.addItemListener(this);
		add(this.showHiddenItem);
		
		// Items separator
		add(new JSeparator(JSeparator.HORIZONTAL));

		// Create filters group
		ButtonGroup filtersGroup = new ButtonGroup();
		
		// Empty filter
		this.allItem = new JRadioButtonMenuItem();
		this.allItem.setName("" + FilterManager.FILTER_ALL);
		this.allItem.addItemListener(this);
		add(this.allItem);
		filtersGroup.add(this.allItem);

		// Folders filter
		this.folderItem = new JRadioButtonMenuItem();
		this.folderItem.setName("" + FilterManager.FILTER_FOLDERS);
		this.folderItem.addItemListener(this);
		add(this.folderItem);
		filtersGroup.add(this.folderItem);
		
		// Files filter
		this.fileItem = new JRadioButtonMenuItem();
		this.fileItem.setName("" + FilterManager.FILTER_FILES);
		this.fileItem.addItemListener(this);
		add(this.fileItem);
		filtersGroup.add(this.fileItem);
		
		// Items separator
		add(new JSeparator(JSeparator.HORIZONTAL));
		
		// Archives filter
		this.archiveItem = new JRadioButtonMenuItem();
		this.archiveItem.setName("" + FilterManager.FILTER_ARCHIVES);
		this.archiveItem.addItemListener(this);
		add(this.archiveItem);
		filtersGroup.add(this.archiveItem);
		
		// Audio filter
		this.audioItem = new JRadioButtonMenuItem();
		this.audioItem.setName("" + FilterManager.FILTER_AUDIO);
		this.audioItem.addItemListener(this);
		add(this.audioItem);
		filtersGroup.add(this.audioItem);
		
		// Documents filter
		this.documentItem = new JRadioButtonMenuItem();
		this.documentItem.setName("" + FilterManager.FILTER_DOCUMENTS);
		this.documentItem.addItemListener(this);
		add(this.documentItem);
		filtersGroup.add(this.documentItem);
		
		// Images filter
		this.imageItem = new JRadioButtonMenuItem();
		this.imageItem.setName("" + FilterManager.FILTER_IMAGES);
		this.imageItem.addItemListener(this);
		add(this.imageItem);
		filtersGroup.add(this.imageItem);
		
		// Media archives filter
		this.mediaArchiveItem = new JRadioButtonMenuItem();
		this.mediaArchiveItem.setName("" + FilterManager.FILTER_MEDIA_ARCHIVES);
		this.mediaArchiveItem.addItemListener(this);
		add(this.mediaArchiveItem);
		filtersGroup.add(this.mediaArchiveItem);
		
		// Videos filter
		this.videoItem = new JRadioButtonMenuItem();
		this.videoItem.setName("" + FilterManager.FILTER_VIDEOS);
		this.videoItem.addItemListener(this);
		add(this.videoItem);
		filtersGroup.add(this.videoItem);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// Applies selected filter
		int componentId = Integer.valueOf(((JComponent) e.getSource()).getName().trim()).intValue();
		if (componentId != FILTER_SHOW_HIDDEN) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				ApplicationManager.getInstance().getPreferenceManager().setPreviousFilter(componentId, this.showHiddenItem.isSelected());
			}
		} else {
			ApplicationManager.getInstance().getPreferenceManager().setPreviousFilter(-1, this.showHiddenItem.isSelected());
		}
	}
	
	/**
	 * Select previous filter or the default one in the menu
	 * 
	 * @param filterID
	 *            ID of the filter to select
	 * @param showHidden
	 *            indicates if the filter should accept hidden files/folders
	 */
	public void selectFilter(int filterID, boolean showHidden) {
		this.showHiddenItem.setSelected(showHidden);
		switch (filterID) {
		case FilterManager.FILTER_ALL:
			this.allItem.setSelected(true);
			break;
		case FilterManager.FILTER_FOLDERS:
			this.folderItem.setSelected(true);
			break;
		case FilterManager.FILTER_FILES:
			this.fileItem.setSelected(true);
			break;
		case FilterManager.FILTER_ARCHIVES:
			this.archiveItem.setSelected(true);
			break;
		case FilterManager.FILTER_AUDIO:
			this.audioItem.setSelected(true);
			break;
		case FilterManager.FILTER_DOCUMENTS:
			this.documentItem.setSelected(true);
			break;
		case FilterManager.FILTER_IMAGES:
			this.imageItem.setSelected(true);
			break;
		case FilterManager.FILTER_MEDIA_ARCHIVES:
			this.mediaArchiveItem.setSelected(true);
			break;
		case FilterManager.FILTER_VIDEOS:
			this.videoItem.setSelected(true);
			break;
		default:
			this.fileItem.setSelected(true);
			return;
		}
	}

	@Override
	public void localeChanged(LocaleManager localeManager) {
		this.archiveItem 	 .setText(localeManager.getString("FilterMenu.archives.label"));
		this.audioItem		 .setText(localeManager.getString("FilterMenu.audio.label"));
		this.documentItem	 .setText(localeManager.getString("FilterMenu.documents.label"));
		this.fileItem		 .setText(localeManager.getString("FilterMenu.files.label"));
		this.allItem		 .setText(localeManager.getString("FilterMenu.filesFolders.label"));
		this.folderItem		 .setText(localeManager.getString("FilterMenu.folders.label"));
		this.imageItem		 .setText(localeManager.getString("FilterMenu.images.label"));
		this.mediaArchiveItem.setText(localeManager.getString("FilterMenu.mediaArchives.label"));
		this.showHiddenItem	 .setText(localeManager.getString("FilterMenu.showHidden.label"));
		this.videoItem		 .setText(localeManager.getString("FilterMenu.video.label"));
	}
}