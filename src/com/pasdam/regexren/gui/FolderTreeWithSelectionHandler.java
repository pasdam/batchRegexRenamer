/**
 * 
 */
package com.pasdam.regexren.gui;

import java.io.File;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.pasdam.gui.swing.folderTree.FolderTree;
import com.pasdam.regexren.controller.ApplicationManager;

/**
 * This class extends {@link FolderTree} in order to manage internally
 * preference changes
 * 
 * @author paco
 * @version 0.1
 */
class FolderTreeWithSelectionHandler extends FolderTree implements TreeSelectionListener {

	private static final long serialVersionUID = 7280891736231114189L;
	
	/** Creates the panel */
	public FolderTreeWithSelectionHandler() {
		addTreeSelectionListener(this);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		// Returns the last path element of the selection.
		// This method is useful only when the selection model allows a single
		// selection.
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
		if (node != null) {
			ApplicationManager.getInstance().getPreferenceManager().setPreviousFolder((File) node.getUserObject(), false);
		}
	}
}
