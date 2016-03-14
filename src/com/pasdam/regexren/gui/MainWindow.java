package com.pasdam.regexren.gui;

import java.awt.EventQueue;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileFilter;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.pasdam.regexren.controller.ApplicationManager;
import com.pasdam.regexren.controller.ErrorListener;
import com.pasdam.regexren.controller.FilterManager.FiltersListener;
import com.pasdam.regexren.controller.LocaleManager;
import com.pasdam.regexren.controller.LogManager;
import com.pasdam.regexren.controller.PreferenceManager;
import com.pasdam.utils.PropertyChangeListener;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {

	// UI components
	private FolderTreeWithSelectionHandler folderTree;
	private CurrentFolderToolbar currentFolderToolbar;
	private RulesPanel rulesPanel;
	private RulesToolBar toolBar;
	private FilesTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// TODO: add support for system look and feel
		// try {
		// 	// set the system skin
		// 	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		// } catch (Exception e) {
		// 	if (LogManager.ENABLED)
		// 		LogManager.warning("Main> unable to set system look and feel. " + e.getMessage());
		// }

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				if (LogManager.ENABLED) LogManager.trace("Main> starting main window.");

				// create main window
				MainWindow window = new MainWindow();
				window.createWindow();
				
				// initialize application
				ApplicationManager.getInstance().init();
				
				// make window visible
				window.setVisible(true);
			}
		});
	}

	private void createWindow() {
		if (LogManager.ENABLED) LogManager.trace("MainWindow> creating main window.");

		// create main frame
		setTitle("Batch RegEx Renamer");
		setIconImage(new ImageIcon("images" + File.separator + "rename.png").getImage());
		setBounds(100, 100, 613, 404);

		// create vertical splitter
		JSplitPane topBottomSplitterSplitter = new JSplitPane();
		topBottomSplitterSplitter.setResizeWeight(0.5);
		topBottomSplitterSplitter.setDividerLocation(120); 
		topBottomSplitterSplitter.setOrientation(JSplitPane.VERTICAL_SPLIT);
		getContentPane().add(topBottomSplitterSplitter);

		// create horizontal splitter (top of the topDownSplitter)
		JSplitPane leftRightSplitter = new JSplitPane();
		leftRightSplitter.setDividerSize(7);
		leftRightSplitter.setOneTouchExpandable(true);
		leftRightSplitter.setResizeWeight(0.3);
		topBottomSplitterSplitter.setLeftComponent(leftRightSplitter);
		
		// create and add folder tree
		this.folderTree = new FolderTreeWithSelectionHandler();
		leftRightSplitter.setLeftComponent(new JScrollPane(this.folderTree));
		
		// create left panel
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

		// create and add current folder toolbar
		this.currentFolderToolbar = new CurrentFolderToolbar();
		leftPanel.add(this.currentFolderToolbar);
		
		// create and add the table containing files/folders list
		this.table = new FilesTable();
		this.currentFolderToolbar.setCheckItemsListener(this.table);
		leftPanel.add(new JScrollPane(this.table));
		
		// add left panel
		leftRightSplitter.setRightComponent(leftPanel);

		// create bottom panel
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		
		this.toolBar = new RulesToolBar();
		bottomPanel.add(this.toolBar);
		
		this.rulesPanel = new RulesPanel();
		bottomPanel.add(new JScrollPane(this.rulesPanel));
		
		topBottomSplitterSplitter.setRightComponent(bottomPanel);

		// set preference listeners and load initial preferences
		InternalEventsHandler internalEventsHandler = new InternalEventsHandler();
		PreferenceManager preferenceManager = ApplicationManager.getInstance().getPreferenceManager();
		preferenceManager.addCurrentFolderListener(internalEventsHandler);
		preferenceManager.addFilterListener(internalEventsHandler);

		// adding listener
		addWindowListener(internalEventsHandler);

		// set the close operation
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// set locale listeners and initialize them
		LocaleManager localeManager = ApplicationManager.getInstance().getLocaleManager();
		localeManager.addLocalizableComponent(this.currentFolderToolbar);
		localeManager.addLocalizableComponent(this.rulesPanel);
		localeManager.addLocalizableComponent(this.toolBar);
		localeManager.addLocalizableComponent(this.table);
		
		// set errors handler
		ApplicationManager.getInstance().getRulesManager().setErrorListener(internalEventsHandler);

		if (LogManager.ENABLED) LogManager.trace("MainWindow> main window created.");
	}
	
	private class InternalEventsHandler implements PropertyChangeListener<File>, 
												   FiltersListener,
												   WindowListener,
												   ErrorListener {

		@Override
		public void propertyChanged(File value) {
			MainWindow.this.currentFolderToolbar.setCurrentFolder(value);
			MainWindow.this.folderTree.setCurrentFolder(value);
		}

		@Override
		public void filterChanged(int filterType, boolean showHidden, FileFilter filter) {
			MainWindow.this.currentFolderToolbar.setFilter(filterType, showHidden, filter);
			MainWindow.this.folderTree.setShowHidden(showHidden);
		}
		
		@Override
		public void errorOccurred(String messageKey) {
			LocaleManager localeManager = ApplicationManager.getInstance().getLocaleManager();
			JOptionPane.showMessageDialog(MainWindow.this,
					localeManager.getString(messageKey),
				    localeManager.getString("Alert.error.title"),
				    JOptionPane.ERROR_MESSAGE);
		}

		@Override
		public void windowClosing(WindowEvent e) {
			if (LogManager.ENABLED) LogManager.trace("MainWindow.windowClosing>");
			ApplicationManager.getInstance().terminate();
		}
	
		/** Event ignored */
		@Override
		public void windowOpened(WindowEvent e) {}
		
		/** Event ignored */
		@Override
		public void windowIconified(WindowEvent e) {}
		
		/** Event ignored */
		@Override
		public void windowDeiconified(WindowEvent e) {}
		
		/** Event ignored */
		@Override
		public void windowDeactivated(WindowEvent e) {}
		
		/** Event ignored */
		@Override
		public void windowClosed(WindowEvent e) {}
		
		/** Event ignored */
		@Override
		public void windowActivated(WindowEvent e) {}
	}
}
