package com.pasdam.regexren.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.pasdam.gui.swing.widgets.WideComboBox;
import com.pasdam.regexren.controller.ApplicationManager;
import com.pasdam.regexren.controller.LocaleManager;
import com.pasdam.regexren.controller.LogManager;
import com.pasdam.regexren.controller.PreferenceManager;

/**
 * Dialog with application settings
 * 
 * @author paco
 * @version 0.1
 */
public class SettingDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = -1246396265114536942L;

	// supported languages
	private static final int LOCALE_ENGLISH = 0;
	private static final int LOCALE_ITALIAN = LOCALE_ENGLISH + 1;
	private static final int LOCALE_COUNT   = LOCALE_ITALIAN + 1;

	// UI components
	private JButton cancelButton;
	private JButton okButton;
	private JCheckBox rememberFilterCheckbox;
	private JCheckBox rememberFolderCheckbox;
	private JCheckBox rememberScriptCheckbox;
	private WideComboBox localeCombobox;

	/** Create the dialog */
	public SettingDialog() {
		// set layout properties
		setBounds(100, 100, 415, 300);
		getContentPane().setLayout(new BorderLayout());
		setModal(true);
		
		// create and add content panel
		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new GridLayout(3, 1, 0, 0));
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.add(contentPanel);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		// create "Remember" panel
		JPanel rememberPanel = new JPanel();
		
		// create title border and add to the remember panel
		TitledBorder rememberTitledBorder = new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null);
		rememberPanel.setBorder(rememberTitledBorder);
		
		// create remember folder checkbox and add it to the panel
		this.rememberFolderCheckbox = new JCheckBox();
		rememberPanel.add(this.rememberFolderCheckbox);
		
		// create remember filter checkbox and add it to the panel
		this.rememberFilterCheckbox = new JCheckBox();
		rememberPanel.add(this.rememberFilterCheckbox);
		
		// create remember script checkbox and add it to the panel
		this.rememberScriptCheckbox = new JCheckBox();
		rememberPanel.add(this.rememberScriptCheckbox);

		// add remember panel to the content
		contentPanel.add(rememberPanel);

		JPanel panel = new JPanel();
		TitledBorder languageTitledBorder = new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null);
		panel.setBorder(languageTitledBorder);
		contentPanel.add(panel);
		
		// create locale combobox and add it to the panel
		this.localeCombobox = new WideComboBox();
		this.localeCombobox.setModel(new DefaultComboBoxModel<String>(getLocaleValues()));
		panel.add(this.localeCombobox);
		
		// create nuyyon panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		// create ok button and add it to the panel
		this.okButton = new JButton();
		this.okButton.addActionListener(this);
		buttonPanel.add(this.okButton);
		getRootPane().setDefaultButton(this.okButton);
		
		// create cancel button and add it to the panel
		this.cancelButton = new JButton();
		this.cancelButton.addActionListener(this);
		buttonPanel.add(this.cancelButton);

		// set localized strings
		LocaleManager localeManager = ApplicationManager.getInstance().getLocaleManager();
		setTitle(localeManager.getString("Settings.settings"));
		rememberTitledBorder.setTitle(localeManager.getString("Settings.rememberOnOpen"));
		languageTitledBorder.setTitle(localeManager.getString("Settings.language"));
		this.rememberFolderCheckbox.setText(localeManager.getString("Settings.rememberOnOpen.folder"));
		this.rememberFilterCheckbox.setText(localeManager.getString("Settings.rememberOnOpen.filter"));
		this.rememberScriptCheckbox.setText(localeManager.getString("Settings.rememberOnOpen.script"));
		this.okButton.setText(localeManager.getString("Settings.ok"));
		this.cancelButton.setText(localeManager.getString("Settings.cancel"));
		
		// load preferences
		PreferenceManager prefs = ApplicationManager.getInstance().getPreferenceManager();
		this.rememberFilterCheckbox.setSelected(prefs.getRememberPreviousFilter());
		this.rememberFolderCheckbox.setSelected(prefs.getRememberPreviousFolder());
		this.rememberScriptCheckbox.setSelected(prefs.getRememberPreviousScript());
		this.localeCombobox.setSelectedIndex(getLocaleId(localeManager.getLocale()));
	}
	
	/**
	 * Returns the locale combobox values
	 * 
	 * @return the locale combobox values
	 */
	private static String[] getLocaleValues() {
		String[] values = new String[LOCALE_COUNT];
		values[LOCALE_ENGLISH] = Locale.ENGLISH.getDisplayName();
		values[LOCALE_ITALIAN] = Locale.ITALIAN.getDisplayName();
		return values;
	}
	
	/**
	 * Get the locale associated with the specified id
	 * 
	 * @param id
	 *            id of the locale to get
	 * @return the locale associated with the specified id
	 */
	private static Locale getLocale(int id) {
		switch (id) {
			case LOCALE_ENGLISH:
				return Locale.ENGLISH;

			case LOCALE_ITALIAN:
				return Locale.ITALIAN;
	
			default:
				if (LogManager.ENABLED) LogManager.warning("SettingDialog.getLocale> Invalid selected item: " + id);
				return null;
		}
	}
	
	/**
	 * Returns the id of the specified locale, used to select the correct item
	 * of the combobox
	 * 
	 * @param locale
	 *            locale to select
	 * @return the id of the specified locale
	 */
	private static int getLocaleId(Locale locale) {
		if (locale.equals(Locale.ENGLISH)) {
			return LOCALE_ENGLISH;
		
		} else if (locale.equals(Locale.ITALIAN)) {
			return LOCALE_ITALIAN;
		}
		
		if (LogManager.ENABLED) LogManager.warning("SettingDialog.getLocaleId> Invalid locale: " + locale);
		
		return -1;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (this.okButton == source) {
			PreferenceManager preferenceManager = ApplicationManager.getInstance().getPreferenceManager();
			preferenceManager.setRememberPreviousFilter(this.rememberFilterCheckbox.isSelected());
			preferenceManager.setRememberPreviousFolder(this.rememberFolderCheckbox.isSelected());
			preferenceManager.setRememberPreviousScript(this.rememberScriptCheckbox.isSelected());
			preferenceManager.setLocale(getLocale(this.localeCombobox.getSelectedIndex()));
		}
		setVisible(false);
	}
}
