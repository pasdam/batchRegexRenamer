package com.pasdam.regexren.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import com.pasdam.regexren.controller.ApplicationManager;
import com.pasdam.regexren.controller.FilesListManager.FilesListListener;
import com.pasdam.regexren.controller.LocaleManager;
import com.pasdam.regexren.controller.LocaleManager.Localizable;
import com.pasdam.regexren.controller.LogManager;
import com.pasdam.regexren.controller.RulesManager;
import com.pasdam.regexren.controller.RulesManager.RulesListener;
import com.pasdam.regexren.gui.rules.AbstractRuleFactory;
import com.pasdam.regexren.model.FileModelItem;

/**
 * Rules panel's toolbar
 * 
 * @author paco
 * @version 0.1
 */
class RulesToolBar extends JToolBar implements Localizable,
											   MouseListener,
											   RulesListener,
											   FilesListListener {

	private static final long serialVersionUID = -3461213981982443383L;
	
	// Buttons IDs
	private static final int ID_DELETE    = 0;
	private static final int ID_MOVE_UP   = ID_DELETE    + 1;
	private static final int ID_MOVE_DOWN = ID_MOVE_UP   + 1;
	private static final int ID_SETTINGS  = ID_MOVE_DOWN + 1;
	private static final int ID_PREVIEW   = ID_SETTINGS  + 1;
	private static final int ID_APPLY     = ID_PREVIEW   + 1;
	private static final int ID_UNDO      = ID_APPLY     + 1;
	
	// UI components
	private final AddRuleMenu addRuleMenu;
	private final JButton addButton;
	private final JButton applyButton;
	private final JButton deleteButton;
	private final JButton moveDownButton;
	private final JButton moveUpButton;
	private final JButton previewButton;
	private final JButton scriptButton;
	private final JButton settingsButton;
	private final JButton undoButton;
	private final ScriptMenu scriptMenu;
	
	/**
	 * Indicates whether the files list has at least one element (true) or is
	 * empty (false)
	 */
	private boolean filesAvailable = false;
	
	/**
	 * Indicates whether the rules list has at least one element (true) or is
	 * empty (false)
	 */
	private boolean rulesAvailable = false;

	public RulesToolBar() {
		// set layout properties
		setFloatable(false);
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		
		// create and add script button
		this.scriptMenu = new ScriptMenu();
		this.scriptButton = new PopupMenuButton(this.scriptMenu);
		this.scriptButton.setIcon(new ImageIcon(ImageProvider.getInstance().getImage(this, "script.png")));
		add(this.scriptButton);
		
		// create and add separator
		add(Box.createHorizontalStrut(10));
		JSeparator separator = new JSeparator();
		separator.setMaximumSize(new Dimension(5, 50));
		separator.setOrientation(SwingConstants.VERTICAL);
		add(separator);
		add(Box.createHorizontalStrut(10));
		
		// add rule button
		this.addRuleMenu = new AddRuleMenu();
		this.addButton = new PopupMenuButton(this.addRuleMenu);
		this.addButton.setIcon(new ImageIcon(ImageProvider.getInstance().getImage(this, "add.png")));
		add(this.addButton);
		
		// add move up button
		moveUpButton = new JButton();
		moveUpButton.setIcon(new ImageIcon(ImageProvider.getInstance().getImage(this, "up.png")));
		moveUpButton.setName(""+ID_MOVE_UP);
		moveUpButton.addMouseListener(this);
		add(moveUpButton);

		// add move down button
		moveDownButton = new JButton();
		moveDownButton.setIcon(new ImageIcon(ImageProvider.getInstance().getImage(this, "down.png")));
		moveDownButton.setName(""+ID_MOVE_DOWN);
		moveDownButton.addMouseListener(this);
		add(moveDownButton);

		// add delete button
		this.deleteButton = new JButton();
		this.deleteButton.setIcon(new ImageIcon(ImageProvider.getInstance().getImage(this, "delete.png")));
		this.deleteButton.setName(""+ID_DELETE);
		this.deleteButton.addMouseListener(this);
		add(this.deleteButton);

		// add horizontal glue
		add(Box.createHorizontalGlue());
		
		// add settings button
		this.settingsButton = new JButton();
		this.settingsButton.setIcon(new ImageIcon(ImageProvider.getInstance().getImage(this, "settings.png")));
		this.settingsButton.setName(""+ID_SETTINGS);
		this.settingsButton.addMouseListener(this);
		add(this.settingsButton);
		
		// add separator
		add(Box.createHorizontalStrut(10));
		separator = new JSeparator();
		separator.setMaximumSize(new Dimension(5, 50));
		separator.setOrientation(SwingConstants.VERTICAL);
		add(separator);
		add(Box.createHorizontalStrut(10));

		// add preview button
		this.previewButton = new JButton();
		this.previewButton.setIcon(new ImageIcon(ImageProvider.getInstance().getImage(this, "preview.png")));
		this.previewButton.setName(""+ID_PREVIEW);
		this.previewButton.addMouseListener(this);
		add(this.previewButton);

		// add apply button
		this.applyButton = new JButton();
		this.applyButton.setIcon(new ImageIcon(ImageProvider.getInstance().getImage(this, "apply.png")));
		this.applyButton.setName(""+ID_APPLY);
		this.applyButton.addMouseListener(this);
		add(this.applyButton);

		// add undo button
		this.undoButton = new JButton();
		this.undoButton.setIcon(new ImageIcon(ImageProvider.getInstance().getImage(this, "undo.png")));
		this.undoButton.setName(""+ID_UNDO);
		this.undoButton.addMouseListener(this);
		add(this.undoButton);
		
		// register itself as files listener
		ApplicationManager.getInstance().getFilesListManager().addFilesListListener(this);
		
		// register itself as rules listener
		RulesManager rulesManager = ApplicationManager.getInstance().getRulesManager();
		rulesManager.addRulesListener(this);
		rulesChanged(rulesManager.getRulesList());
	}
	
	private void setRulesAvailable(boolean rulesInList) {
		this.rulesAvailable = rulesInList;
		boolean buttonsEnabled = this.rulesAvailable && this.filesAvailable;
		this.previewButton.setEnabled(buttonsEnabled);
		this.applyButton.setEnabled(buttonsEnabled);
	}

	@Override
	public void localeChanged(LocaleManager localeManager) {
		this.addButton 		 .setToolTipText(localeManager.getString("RulesPanel.add.toolTipText"));
    	this.applyButton	 .setToolTipText(localeManager.getString("RulesPanel.apply.toolTipText"));
    	this.deleteButton	 .setToolTipText(localeManager.getString("RulesPanel.delete.toolTipText"));
    	this.previewButton	 .setToolTipText(localeManager.getString("RulesPanel.preview.toolTipText"));
    	this.scriptButton	 .setText(localeManager.getString("RulesPanel.script.label"));
    	this.settingsButton	 .setToolTipText(localeManager.getString("Settings.settings"));
    	this.undoButton		 .setToolTipText(localeManager.getString("RulesPanel.undo.toolTipText"));
		
		this.addRuleMenu.localeChanged(localeManager);
		this.scriptMenu.localeChanged(localeManager);
	}
	
	@Override
	public void filesListChanged(List<FileModelItem> list, boolean undoAvailable) {
		if (LogManager.ENABLED) LogManager.trace("RulesPanel.filesListChanged> filesCount=" + list.size() + ", undoAvailable=" + undoAvailable);
		this.undoButton.setEnabled(undoAvailable);
		this.filesAvailable = list.size() > 0;
		boolean buttonsEnabled = this.rulesAvailable && this.filesAvailable;
		this.previewButton.setEnabled(buttonsEnabled);
		this.applyButton.setEnabled(buttonsEnabled);
	}

	@Override
	public void ruleAdded(int index, AbstractRuleFactory addedRule) {
		setRulesAvailable(true);
	}

	@Override
	public void ruleRemoved(int index) {
		setRulesAvailable(ApplicationManager.getInstance().getRulesManager().getRulesCount() > 0);
	}

	@Override
	public void rulesChanged(List<AbstractRuleFactory> rulesList) {
		setRulesAvailable(rulesList.size() > 0);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int componentID = new Integer(((Component) e.getSource()).getName());
		switch (componentID) {
			case ID_DELETE:
				ApplicationManager.getInstance().getRulesManager().removeSelected();
				break;

			case ID_MOVE_UP:
				ApplicationManager.getInstance().getRulesManager().moveUpSelected();
				break;
			
			case ID_MOVE_DOWN:
				ApplicationManager.getInstance().getRulesManager().moveDownSelected();
				break;
			
			case ID_SETTINGS:
				SettingDialog dialog = new SettingDialog();
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
				break;
			
			case ID_PREVIEW:
				ApplicationManager.getInstance().getFilesListManager().applyRules(false);
				break;
			
			case ID_APPLY:
				ApplicationManager.getInstance().getFilesListManager().applyRules(true);
				break;
			
			case ID_UNDO:
				ApplicationManager.getInstance().getFilesListManager().undoRename();
				break;
		}
	}

	/** Do nothing */
	@Override
	public void mouseEntered(MouseEvent e) {}
	/** Do nothing */
	@Override
	public void mouseExited(MouseEvent e) {}
	/** Do nothing */
	@Override
	public void mousePressed(MouseEvent e) {}
	/** Do nothing */
	@Override
	public void mouseReleased(MouseEvent e) {}
}
