package com.pasdam.regexren.gui;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPopupMenu;

/**
 * {@link JButton} with a popup menu that is shown on click; the menu position
 * is under the button
 * 
 * @author paco
 * @version 0.1
 */
class PopupMenuButton extends JButton {

	private static final long serialVersionUID = 2279657569485225227L;
	
	public PopupMenuButton(JPopupMenu menu) {
		// setting mouse listner
		addMouseListener(new ShowPopupMenuMouseAdapter(menu));
	}

	/**
	 * {@link MouseListener} that shows the popup menu under the clicked
	 * {@link Component}
	 * 
	 * @author paco
	 * @version 0.1
	 */
	private class ShowPopupMenuMouseAdapter implements MouseListener {
		
		/** Menu to open on click */
		private final JPopupMenu menu;
		
		/**
		 * Creates a {@link MouseListener} that open the specified menu
		 * 
		 * @param menu
		 *            menu to open
		 */
		public ShowPopupMenuMouseAdapter(JPopupMenu menu) {
			this.menu = menu;
		}

		/**
		 * Shows the popup menu, under the clicked {@link Component}
		 * 
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			Component button = e.getComponent();
			this.menu.show(button, 0, button.getHeight());
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
	}
}
