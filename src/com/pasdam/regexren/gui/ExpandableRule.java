package com.pasdam.regexren.gui;

import java.awt.Container;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import com.pasdam.gui.swing.dragAndDropPanels.DragAndDropMouseListener;
import com.pasdam.gui.swing.dragAndDropPanels.DragAndDropTransferHandler;
import com.pasdam.gui.swing.panel.ExpandableItem;
import com.pasdam.regexren.controller.LocaleManager;
import com.pasdam.regexren.controller.LocaleManager.Localizable;
import com.pasdam.regexren.controller.LogManager;
import com.pasdam.regexren.gui.rules.AbstractRuleFactory;
import com.pasdam.regexren.gui.rules.RuleFactoryListener;

/**
 * Expandable panel that contains a {@link RuleContentPanel}
 * 
 * @author paco
 * @version 0.1
 */
public class ExpandableRule extends ExpandableItem implements Localizable, Transferable {
	
	/**	MIME type that indicates an {@link AbstractRuleFactory} */
	public static final String MIME_TYPE_RULE_FACTORY = DataFlavor.javaJVMLocalObjectMimeType + ";class=com.pasdam.regexren.gui.rules.AbstractRuleFactory";
	
	private static final long serialVersionUID = 8443447294086054937L;
	
	// Localizable string keys
	private static final String STRING_KEY_INVALID_PARAMETERS = "Error.RulesPanel.invalidParameters";

	// UI components
	private final Box titlePanel;
	private final JCheckBox selectCheckbox;
	private final JLabel titleLabel;
	private final JLabel validLabel;
	private final RuleMenu menu;
	
	/** The rule factory related to the contained panel */
	private RuleContentPanel<?> ruleContentPanel;
	
	/** Tooltip to show for invalid rules  */
	private String tooltipInvalid;
	
	/** Create the panel */
	public ExpandableRule(RuleContentPanel<?> ruleContentPanel) {
		this.ruleContentPanel = ruleContentPanel;
		
		// create title panel
		this.titlePanel = Box.createHorizontalBox();
		
		// create and add checkbox
		this.selectCheckbox = new JCheckBox();
		this.selectCheckbox.setSelected(ruleContentPanel.getRuleFactory().isEnabled());
		this.titlePanel.add(this.selectCheckbox);
		
		// create and add title label
		this.titleLabel = new JLabel();
		this.titlePanel.add(this.titleLabel);
		
		// add spacer
		this.titlePanel.add(Box.createHorizontalGlue());
		
		// add valid indicator
		this.validLabel = new JLabel();
		this.titlePanel.add(this.validLabel);

		// set title panel
		setTitleComponent(this.titlePanel);
		
		// add context menu
		this.menu = new RuleMenu(this.ruleContentPanel.getRuleFactory());
		setContextMenu(this.menu);
		
		// add rule's content
		setContent(ruleContentPanel);
		
		// set events hanler
		InternalEventHandler eventHandler = new InternalEventHandler();
		this.selectCheckbox.addItemListener(eventHandler);
		ruleContentPanel.getRuleFactory().addConfigurationListener(eventHandler);
		
		// enable drag&drop behavior
		this.titlePanel.addMouseMotionListener(eventHandler);
		this.titlePanel.addMouseListener(eventHandler);
		this.titlePanel.setDropTarget(new DropTarget(this.titlePanel, eventHandler));
		setTransferHandler(new DragAndDropTransferHandler());
		
		updateValidIndicator(ruleContentPanel.getRuleFactory().isValid());
	}
	
	/**
	 * Updates the valid indicator
	 * 
	 * @param valid
	 *            true if the configuration is valid, false otherwise
	 */
	private void updateValidIndicator(boolean valid) {
		if (valid) {
			this.validLabel.setIcon(new ImageIcon("images" + File.separator + "green_button.png"));
			this.validLabel.setToolTipText("");
			
		} else {
			this.validLabel.setIcon(new ImageIcon("images" + File.separator + "red_button.png"));
			this.validLabel.setToolTipText(this.tooltipInvalid);
		}
	}
	
	@Override
	public void localeChanged(LocaleManager localeManager) {
		this.ruleContentPanel.localeChanged(localeManager);
		this.menu.localeChanged(localeManager);
		
		this.tooltipInvalid = localeManager.getString(STRING_KEY_INVALID_PARAMETERS);
		this.validLabel.setToolTipText(this.ruleContentPanel.getRuleFactory().isValid()
				? ""
				: this.tooltipInvalid);
		this.titleLabel.setText(this.ruleContentPanel.getDescription());
	}
	
	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		return this.ruleContentPanel.getRuleFactory();
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		try {
			DataFlavor[] flavors = { new DataFlavor(MIME_TYPE_RULE_FACTORY) };
			return flavors;
			
		} catch (Exception exception) {
			if (LogManager.ENABLED) LogManager.error("ExpandableRule.getTransferDataFlavors> Error while creating data flavor: " + exception.getMessage());
			return null;
		}
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return MIME_TYPE_RULE_FACTORY.equals(flavor.getMimeType());
	}
	
	/** Class that handle internal events */
	private class InternalEventHandler extends DragAndDropMouseListener
									   implements ItemListener,
									   			  DropTargetListener,
									   			  RuleFactoryListener {
		@Override
		public void configurationChanged(boolean valid) {
			ExpandableRule.this.titleLabel.setText(ExpandableRule.this.ruleContentPanel.getDescription());
			
			ExpandableRule.this.updateValidIndicator(valid);
		}
		
		@Override
		public void itemStateChanged(ItemEvent event) {
			ExpandableRule.this.ruleContentPanel.getRuleFactory().setEnabled(ExpandableRule.this.selectCheckbox.isSelected());
		}

		@Override
		public void mouseDragged(MouseEvent event) {
			// this override is necessary since the mouse listener is for the title
			// panel which is not a Transferable, so we need to manually set the
			// ExpandableRule
			ExpandableRule.this
	        	.getTransferHandler()
	        	.exportAsDrag(ExpandableRule.this, event, TransferHandler.COPY);
		}

		@Override
		public void dragExit(DropTargetEvent event) {
			// dispatch the event to the rules panel
			ExpandableRule.this.getParent().getDropTarget().dragExit(event);
		}

		@Override
		public void dragOver(DropTargetDragEvent event) {
			// get rules panel
			Container parent = ExpandableRule.this.getParent();
			// update the event's location, according to the rules panel coordinates system
			event.getLocation().setLocation(SwingUtilities.convertPoint(ExpandableRule.this.titlePanel, event.getLocation(), parent));
			// dispatch the event to the rules panel
			parent.getDropTarget().dragOver(event);
		}

		@Override
		public void drop(DropTargetDropEvent event) {
			// get rules panel
			Container parent = ExpandableRule.this.getParent();
			// update the event's location, according to the rules panel coordinates system
			event.getLocation().setLocation(SwingUtilities.convertPoint(ExpandableRule.this.titlePanel, event.getLocation(), parent));
			// dispatch the event to the rules panel
			parent.getDropTarget().drop(event);
		}
		
		@Override
		public void dragEnter(DropTargetDragEvent event) {
			// get rules panel
			Container parent = ExpandableRule.this.getParent();
			// update the event's location, according to the rules panel coordinates system
			event.getLocation().setLocation(SwingUtilities.convertPoint(ExpandableRule.this.titlePanel, event.getLocation(), parent));
			// dispatch the event to the rules panel
			parent.getDropTarget().dragEnter(event);
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent event) {
			// get rules panel
			Container parent = ExpandableRule.this.getParent();
			// update the event's location, according to the rules panel coordinates system
			event.getLocation().setLocation(SwingUtilities.convertPoint(ExpandableRule.this.titlePanel, event.getLocation(), parent));
			// dispatch the event to the rules panel
			parent.getDropTarget().dropActionChanged(event);
		}
	}
}
