package com.pasdam.regexren.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
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
public class FilesTable extends JPanel implements CheckItemsListener,
												  FilesListListener,
												  Localizable {
	
	private static final long serialVersionUID = 8039532837481259761L;
	
	/** Margin of the statistics label used to activate the floatable behavior */
	private static final int STATISTICS_LABEL_MARGIN = 20;
	
	// UI components
	private final Box filesNumberBox;
	private final FilesTableMenu contextMenu;
	private final JLabel filesNumberLabel;
	private final JScrollPane scrollPane;
	private final JTable table;
	private final FilesTableModel model;
	
	/** Initialize the element */
	public FilesTable() {
		setLayout(new GridLayout(1, 1));
		
		JLayeredPane layeredPane = new JLayeredPane();
		add(layeredPane);
		
		// create table and set model
		this.table = new JTable();
		this.model = new FilesTableModel();
		this.table.setModel(this.model);
		this.scrollPane = new JScrollPane(this.table);
		layeredPane.add(this.scrollPane, new Integer(1), -1);
		
		// create and add files number label and panel
		this.filesNumberLabel = new JLabel();
		this.filesNumberLabel.setOpaque(true);
		this.filesNumberLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		this.filesNumberLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		this.filesNumberBox = Box.createHorizontalBox();
		this.filesNumberBox.add(this.filesNumberLabel);
		layeredPane.add(this.filesNumberBox, new Integer(2), -1);

		// create and add context menu
		this.contextMenu = new FilesTableMenu();
		this.table.add(this.contextMenu);
		
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
		InternalEventHandler eventHandler = new InternalEventHandler();
		this.table.addMouseListener(eventHandler);
		this.filesNumberLabel.addMouseListener(eventHandler);
		this.filesNumberBox.addMouseListener(eventHandler);
		addComponentListener(eventHandler);
		
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
    	this.filesNumberLabel.setToolTipText(localeManager.getString("RulesPanel.filesNumber.toolTipText"));
    	this.contextMenu.localeChanged(localeManager);
	}

	@Override
	public void checkElements(Target type, boolean check) {
		int[] selectedIndices;
		switch (type) {
			case ALL:
				for (int i = 0; i < this.model.getRowCount(); i++) {
					this.model.setValueAt(new Boolean(check), i, 0);
				}
				break;
				
			case SELECTION:
				selectedIndices = this.table.getSelectedRows();
				for (int index: selectedIndices) {
					this.model.setValueAt(new Boolean(check), index, 0);
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
	
	/** Updates the label with files count */
	private void updateFileStatistics() {
		this.filesNumberLabel.setText(this.model.getCheckedCount() + "/" + this.model.getRowCount());
		updateStatisticsBoxPosition(false);
	}
	
	/**
	 * Update the position of the statistics box
	 * 
	 * @param invert
	 *            if true invert the position of the label (if it is at left, it
	 *            will be moved to right, and viceversa; if false the label will
	 *            remain at the same position
	 */
	private void updateStatisticsBoxPosition(boolean invert) {
		Dimension labelSize = this.filesNumberLabel.getPreferredSize();
		Dimension boxSize = this.filesNumberBox.getSize();
		
		// set the size of the container accordingly to the label and a margin
		// to enable the floatable behavior also out of the label box
		this.filesNumberBox.setSize(new Dimension(labelSize.width + STATISTICS_LABEL_MARGIN, labelSize.height + STATISTICS_LABEL_MARGIN));
		
		// evaluate the position of the container
		boolean left = invert ? this.filesNumberBox.getX() > 0 : this.filesNumberBox.getX() == 0;

		// update positions
		JScrollBar verticalScrollBar = this.scrollPane.getVerticalScrollBar();
		int scrollBarWidth = verticalScrollBar.isVisible() ? verticalScrollBar.getWidth() : 0;
		this.filesNumberLabel.setLocation(left ? 0 : STATISTICS_LABEL_MARGIN, STATISTICS_LABEL_MARGIN);
		this.filesNumberBox.setLocation(left ? 0 : getWidth() - boxSize.width - scrollBarWidth, getHeight() - boxSize.height);
	}
	
	/** Custom table model */
	private class FilesTableModel extends DefaultTableModel implements TableModelListener {
		
		private static final long serialVersionUID = 4016175757089191008L;

		/** Indicates how many files are checked */
		private int checkedCount = 0;
		
		/** Creates a model with tree untitled columns and 0 rows */
		public FilesTableModel() {
			super(new Object[][] {null, null, null}, 0);
			
			addTableModelListener(this);
		}

		/** Column types */
		@SuppressWarnings("rawtypes")
		private final Class[] columnTypes = new Class[] {
			Boolean.class,	// 0) check column
			String.class,	// 1) current name
			String.class	// 2) new name
		};
		
		/** Map of duplicated file names indexes */
		private final Map<Integer, Object> duplicateNameIndexes = new HashMap<Integer, Object>();
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int columnIndex) {
			return this.columnTypes[columnIndex];
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
			this.checkedCount = 0;
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
					this.duplicateNameIndexes.put(names.get(newName), null);
					this.duplicateNameIndexes.put(i, null);
				} else {
					names.put(newName, i);
				}
				
				// add single row
				addRow(new Object[] { 
						fileData.isChecked(), 
						fileData.getFile(),
						newName
				});
				
				if (fileData.isChecked()) {
					this.checkedCount++;
				}
			}
		}
		
		@Override
		public void setValueAt(Object aValue, int row, int column) {
			if (!FilesTable.this.model.getValueAt(row, column).equals(aValue)) {
				super.setValueAt(aValue, row, column);
			}
		}
		
		@Override
		public void tableChanged(TableModelEvent e) {
			int row = e.getFirstRow();
			int column = e.getColumn();
			if (row >= 0 && column == 0) {
				Boolean checked = (Boolean) FilesTable.this.model.getValueAt(row, column);
				
				// update model
				ApplicationManager.getInstance().getFilesListManager().setChecked(row, checked);
				
				// update checked count
				if (checked) {
					this.checkedCount++;
				} else {
					this.checkedCount--;
				}
				
				FilesTable.this.updateFileStatistics();
			}
		}
		
		/**
		 * Returns true if the filename at the specified row is duplicated
		 * 
		 * @param row
		 *            index of the filename to check
		 * @return true if the filename at the specified row is duplicated
		 */
		public boolean isDuplicated(int row) {
			return this.duplicateNameIndexes.containsKey(row);
		}
		
		/**
		 * Returns the number of checked files
		 * 
		 * @return the number of checked files
		 */
		public int getCheckedCount() {
			return this.checkedCount;
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
					
				if (isSelected) {
					label.setBackground(SystemColor.controlShadow);
				} else {
					label.setBackground((row % 2) == 0 ? SystemColor.controlLtHighlight : SystemColor.control);
				}
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
				// update statistics label
				FilesTable.this.updateFileStatistics();
			
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
	private class InternalEventHandler implements ComponentListener, MouseListener {

		@Override
		public void mouseClicked(MouseEvent event) {
			if (event.getSource() == FilesTable.this.table && event.getClickCount() == 2) {
				int row = FilesTable.this.table.rowAtPoint(event.getPoint());
				File file = ApplicationManager.getInstance().getFilesListManager().getCurrentFile(row);
				if (file.isDirectory()) {
					ApplicationManager.getInstance().getPreferenceManager().setPreviousFolder(file, false);
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent event) {
			if (event.getSource() != FilesTable.this.table) {
				FilesTable.this.updateStatisticsBoxPosition(true);
			}
		}
		
		/** Event ignored */
		@Override
		public void mouseExited(MouseEvent event) {}
		
		/** Event ignored */
		@Override
		public void mousePressed(MouseEvent event) {
			mouseReleased(event);
		}
		
		/** Event ignored */
		@Override
		public void mouseReleased(MouseEvent event) {
			if (event.getSource() == FilesTable.this.table && event.isPopupTrigger()) {
				// show context menu
				if (FilesTable.this.contextMenu != null) {
					int row = FilesTable.this.table.rowAtPoint(event.getPoint());
					File file = ApplicationManager.getInstance().getFilesListManager().getCurrentFile(row);
					FilesTable.this.contextMenu.show(file, FilesTable.this, event.getX(), event.getY());
				}
			}
		}

		/** Event ignored */
		@Override
		public void componentHidden(ComponentEvent event) {}

		/** Event ignored */
		@Override
		public void componentMoved(ComponentEvent event) {}

		@Override
		public void componentResized(ComponentEvent event) {
			// update components size
			Dimension componentSize = event.getComponent().getSize();
			FilesTable.this.scrollPane.setSize(componentSize);
			FilesTable.this.table.setSize(componentSize);
			FilesTable.this.updateStatisticsBoxPosition(false);
		}

		/** Event ignored */
		@Override
		public void componentShown(ComponentEvent event) {}
	}
}
