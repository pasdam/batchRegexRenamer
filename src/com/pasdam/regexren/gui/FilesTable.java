package com.pasdam.regexren.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.pasdam.regexren.controller.ApplicationManager;
import com.pasdam.regexren.controller.FilesListManager.FilesListListener;
import com.pasdam.regexren.controller.LocaleManager;
import com.pasdam.regexren.controller.LocaleManager.Localizable;
import com.pasdam.regexren.model.FileModelItem;

/**
 * GUI element that show a table from a list of files
 * 
 * @author paco
 * @version 0.1
 */
public class FilesTable extends JLayeredPane implements CheckItemsListener,
												  FilesListListener,
												  Localizable {
	
	private static final long serialVersionUID = 8039532837481259761L;

	private final JTable table;
	private final FilesTableModel model;
//	private final JLabel filesNumberLabel;
	
	/** Initialize the element */
	public FilesTable() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		// create table and set model
		this.table = new JTable();
		this.model = new FilesTableModel();
		this.table.setModel(this.model);
		add(this.table);
		
//		Box statusBar = Box.createHorizontalBox();
//		filesNumberLabel = new JLabel();
//		filesNumberLabel.setBorder(new EmptyBorder(0, 10, 0, 10));
//		filesNumberLabel.setText("TEST");
//		statusBar.add(filesNumberLabel);
//		statusBar.add(Box.createHorizontalGlue());
//		add(statusBar, JLayeredPane.PALETTE_LAYER);
		
		// set table properties
		this.table.setShowGrid(false);
		this.table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		// set cells renderer
		this.table.getColumnModel().getColumn(1).setCellRenderer(new ListItemRenderer());
		this.table.getColumnModel().getColumn(2).setCellRenderer(new ListItemRenderer());
		
		// set first cell properties
		TableColumn column = this.table.getColumnModel().getColumn(0);
		column.setHeaderValue("");
		column.setResizable(false);
		column.setPreferredWidth(20);
		column.setMinWidth(20);
		column.setMaxWidth(20);
		
		// set internal event handler
		this.table.addMouseListener(new InternalEventHandler());
		
		// register as files list's listener
		ApplicationManager.getInstance().getFilesListManager().addFilesListListener(this);
	}
	
	@Override
	public void filesListChanged(List<FileModelItem> list, boolean undoAvailable) {
		SwingUtilities.invokeLater(new Worker(true));
	}
    
	/** Force the refresh of the UI component */
    public void refresh() {
    	SwingUtilities.invokeLater(new Worker(false));
    }

	@Override
	public void localeChanged(LocaleManager localeManager) {
    	this.table.getColumnModel().getColumn(1).setHeaderValue(localeManager.getString("FilesTable.column1.title"));
    	this.table.getColumnModel().getColumn(2).setHeaderValue(localeManager.getString("FilesTable.column2.title"));
//    	this.filesNumberLabel.setToolTipText(localeManager.getString("RulesPanel.filesNumber.toolTipText"));
	}

	@Override
	public void checkElements(Target type, boolean check) {
		int[] selectedIndices;
		switch (type) {
			case ALL:
				for (int i = 0; i < model.getRowCount(); i++) {
					model.setValueAt(new Boolean(check), i, 0);
				}
				break;
				
			case SELECTION:
				selectedIndices = this.table.getSelectedRows();
				for (int index: selectedIndices) {
					model.setValueAt(new Boolean(check), index, 0);
				}
				break;
				
			case INVERTED_SELECTION:
				selectedIndices = this.table.getSelectedRows();
				this.table.selectAll();
				for (int index : selectedIndices) {
				    this.table.removeRowSelectionInterval(index, index);
				}
				break;
		}
	}
	
	/** Custom table model */
	private class FilesTableModel extends DefaultTableModel implements TableModelListener {
		
		private static final long serialVersionUID = 4016175757089191008L;
		
		/** Creates a model with tree untitled columns and 0 rows */
		public FilesTableModel() {
			super(new Object[][] {null, null, null}, 0);
			
			addTableModelListener(this);
		}
			
		@SuppressWarnings("rawtypes")
		private final Class[] columnTypes = new Class[] {
			Boolean.class,	// 0) check column
			String.class,	// 1) current name
			String.class	// 2) new name
		};
		private final Map<Integer, Object> duplicateNameIndexes = new HashMap<Integer, Object>();
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}
		
		@Override
		public boolean isCellEditable(int row, int column) {
			return column == 0; // only check column is editable
		}

		/** Clears the model, removing all elements */
		public void clear() {
			for (int i = getRowCount()-1; i >= 0; i--) {
				removeRow(i);
			}
		}
		
		/**
		 * Adds the specified file data objects to the model
		 * 
		 * @param list
		 *            list of file data to add to the model
		 */
		public void addFilesData(List<FileModelItem> list) {
			this.duplicateNameIndexes.clear();

			Map<String, Integer> names = new HashMap<String, Integer>();
			String newName;
			FileModelItem fileData;
			
			for (int i = 0; i < list.size(); i++) {
				// check for duplicates
				fileData = list.get(i);
				newName = fileData.getNewFullName();
				if (names.containsKey(newName)) {
					duplicateNameIndexes.put(names.get(newName), null);
					duplicateNameIndexes.put(i, null);
				} else {
					names.put(newName, i);
				}
				
				// add single row
				addRow(new Object[] { 
						fileData.isChecked(), 
						fileData.getFile(),
						newName
				});
			}
		}
		
		@Override
		public void tableChanged(TableModelEvent e) {
			int row = e.getFirstRow();
			int column = e.getColumn();
			if (row >= 0 && column == 0) {
				ApplicationManager.getInstance().getFilesListManager().setChecked(row, (Boolean) FilesTable.this.model.getValueAt(row, column));
			}
		}
		
		public boolean isDuplicated(int row) {
			return this.duplicateNameIndexes.containsKey(row);
		}
	}

	/** Cell Renderer used to color row and insert an icon near filename */
	private class ListItemRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 4570798395208803066L;

		@Override
    	public Component getTableCellRendererComponent(JTable table,
    			Object value, boolean isSelected, boolean hasFocus, int row,
    			int column) {
			JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
					column);
    		if (value != null) {
				if (column == 1) {
					File file = (File) value;
					label.setText(file.getName());
					label.setToolTipText(file.getName());
					label.setIcon(FileSystemView.getFileSystemView().getSystemIcon(file));
					if ((Boolean) table.getValueAt(row, 0)) {
						label.setForeground(Color.BLACK);
					} else {
						label.setForeground(Color.LIGHT_GRAY);
					}
					
				} else if (column == 2) {
					if ((Boolean) table.getValueAt(row, 0)) {
						String name = (String) value;
						label.setText(name);
						label.setToolTipText(name);
						if (((File) table.getValueAt(row, 1)).getName().equals(name)) {
							label.setForeground(Color.LIGHT_GRAY);
						} else if (FilesTable.this.model.isDuplicated(row)) {
							label.setForeground(Color.RED);
						} else {
							label.setForeground(Color.BLACK);
						}
					} else {
						label.setText("");
					}
				}
					
				label.setBackground((row % 2) == 0
						? SystemColor.controlLtHighlight
						: SystemColor.control);
			}
			return label;
    	}
    }
	
	/** Thread worker that refresh/reload current folder children */
	private class Worker implements Runnable {
		
		/** Indicates if the worker should reload the model, or update only the new name column */
		private final boolean reload;
		
		/**
		 * Creates a worker
		 * 
		 * @param reload
		 *            if true the worker will reload the model, if false it will
		 *            only update the new name column (the 3rd one)
		 */
		public Worker(boolean reload) {
			this.reload = reload;
		}
		
		@Override
		public void run() {
			List<FileModelItem> list = ApplicationManager.getInstance().getFilesListManager().getFilesList();
			if (this.reload) {
				// clear list
				FilesTable.this.model.clear();
				// add new elements to the model
				FilesTable.this.model.addFilesData(list);
			
			} else {
				FileModelItem file;
				for (int i = 0; i < list.size(); i++) {
					file = list.get(i);
					if (file.isChecked()) {
						// refresh model value
						FilesTable.this.model.setValueAt(file.getNewFullName(), i, 2);
					}
				}
			}
		}
	}
	
	/** Class that handles internal events */
	private class InternalEventHandler implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				int row = FilesTable.this.table.rowAtPoint(e.getPoint());
				File folder = ApplicationManager.getInstance().getFilesListManager().getCurrentFile(row);
				ApplicationManager.getInstance().getPreferenceManager().setPreviousFolder(folder);
			}
		}

		/** Event ignored */
		@Override
		public void mouseEntered(MouseEvent arg0) {}
		
		/** Event ignored */
		@Override
		public void mouseExited(MouseEvent arg0) {}
		
		/** Event ignored */
		@Override
		public void mousePressed(MouseEvent arg0) {}
		
		/** Event ignored */
		@Override
		public void mouseReleased(MouseEvent arg0) {}
	}
}
