package com.pasdam.regexren.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.pasdam.regexren.controller.ApplicationManager;
import com.pasdam.regexren.controller.LocaleManager;
import com.pasdam.regexren.controller.RulesManager;
import com.pasdam.regexren.controller.LocaleManager.Localizable;
import com.pasdam.regexren.controller.RulesManager.RulesListener;
import com.pasdam.regexren.gui.rules.AbstractRuleFactory;
import com.pasdam.regexren.utils.BrrFileFilter;

/** Menu that shows all available rule types */
class ScriptMenu extends JPopupMenu implements ActionListener, Localizable, RulesListener {
	
	private static final long serialVersionUID = 8219513433720040595L;

	// GUI elements
	private final JMenuItem clearItem;
	private final JMenuItem openItem;
	private final JMenuItem saveItem;

	// Menu items IDs
	private static final int ID_SCRIPT_OPEN  = 0;
	private static final int ID_SCRIPT_SAVE  = 1;
	private static final int ID_SCRIPT_CLEAR = 2;
	
	/** Creates GUI elements */
	public ScriptMenu() {
		// Open menu item
		this.openItem = new JMenuItem();
		this.openItem.setIcon(new ImageIcon("images" + File.separator + "open.gif"));
		this.openItem.setActionCommand(""+ID_SCRIPT_OPEN);
		this.openItem.addActionListener(this);
		add(openItem);
		
		// Save menu item
		this.saveItem = new JMenuItem();
		this.saveItem.setIcon(new ImageIcon("images" + File.separator + "save.png"));
		this.saveItem.setActionCommand(""+ID_SCRIPT_SAVE);
		this.saveItem.addActionListener(this);
		add(saveItem);
		
		// Clear menu item
		this.clearItem = new JMenuItem();
		this.clearItem.setIcon(new ImageIcon("images" + File.separator + "clear.png"));
		this.clearItem.setActionCommand(""+ID_SCRIPT_CLEAR);
		this.clearItem.addActionListener(this);
		add(clearItem);
		
		// register itself as rules listener
		RulesManager rulesManager = ApplicationManager.getInstance().getRulesManager();
		rulesManager.addRulesListener(this);
		rulesChanged(rulesManager.getRulesList());
	}

	@Override
	public void localeChanged(LocaleManager localeManager) {
    	this.clearItem.setText(localeManager.getString("ScriptMenu.clear.label"));
    	this.openItem .setText(localeManager.getString("ScriptMenu.open.label"));
    	this.saveItem .setText(localeManager.getString("ScriptMenu.save.label"));
	}

	/** Applies selected filter*/
	@Override
	public void actionPerformed(ActionEvent e) {
		int componentID = Integer.valueOf(e.getActionCommand().trim()).intValue();
		switch (componentID) {
			case ID_SCRIPT_OPEN:
				{
					final JFileChooser fileChooser = createFileChooser();
					
					if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
						File file = fileChooser.getSelectedFile();
						ApplicationManager.getInstance().getRulesManager().loadScriptFile(file);
					}
				}
				return;
			
			case ID_SCRIPT_SAVE:
				if (ApplicationManager.getInstance().getRulesManager().getRulesCount() > 0) {
					
					final JFileChooser fileChooser = createFileChooser();

					if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
						File file = fileChooser.getSelectedFile();
						
						if (!fileChooser.getFileFilter().accept(file)) {
							// fix file name
							file = new File(file.getAbsolutePath() + "." + BrrFileFilter.BRR_EXTENSION);
						}
						
						// TODO: check if file already exists, and ask to overwrite
						
						ApplicationManager.getInstance().getRulesManager().saveToFile(file);
					}
				}
				return;
			
			case ID_SCRIPT_CLEAR:
				ApplicationManager.getInstance().getRulesManager().clear();
				return;
		}
	}
	
	private static JFileChooser createFileChooser() {
		final JFileChooser fileChooser = new JFileChooser();
		
		File folder = ApplicationManager.getInstance().getPreferenceManager().getPreviousScriptFile();
		if (folder != null) {
			// get last script file's parent folder
			fileChooser.setCurrentDirectory(folder.getParentFile());
		} else {
			// set current folder
			fileChooser.setCurrentDirectory(new File(""));
		}
		
		fileChooser.setFileFilter(new BrrFileFilter());
		
		return fileChooser;
	}

	@Override
	public void ruleAdded(int index, AbstractRuleFactory addedRule) {
		this.clearItem.setEnabled(true);
		this.saveItem.setEnabled(true);
	}

	@Override
	public void ruleRemoved(int index) {
		if (ApplicationManager.getInstance().getRulesManager().getRulesCount() == 0) {
			this.clearItem.setEnabled(false);
			this.saveItem.setEnabled(false);
		}
	}

	@Override
	public void rulesChanged(List<AbstractRuleFactory> rulesList) {
		if (rulesList.size() > 0) {
			this.clearItem.setEnabled(true);
			this.saveItem.setEnabled(true);
			
		} else {
			this.clearItem.setEnabled(false);
			this.saveItem.setEnabled(false);
		}
	}
}